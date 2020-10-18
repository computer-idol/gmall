package com.wang.gmall.manage.service;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.bean.*;
import com.wang.gmall.manage.mapper.SkuAttrValueMapper;
import com.wang.gmall.manage.mapper.SkuImageMapper;
import com.wang.gmall.manage.mapper.SkuInfoMapper;
import com.wang.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.wang.gmall.service.SkuService;
import com.wang.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

  @Autowired
  private SkuAttrValueMapper skuAttrValueMapper;

  @Autowired
  private SkuImageMapper skuImageMapper;

  @Autowired
  private SkuInfoMapper skuInfoMapper;

  @Autowired
  private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

  @Autowired
  private RedisUtil redisUtil;

  @Override
  public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
    skuInfoMapper.insertSelective(pmsSkuInfo);
    Integer skuInfoId = pmsSkuInfo.getId();

    //插入平台属性管理
    List<PmsSkuAttrValue> skuAttrValues = pmsSkuInfo.getSkuAttrValueList();
    skuAttrValues.forEach(skuAttrValue->{
      skuAttrValue.setSkuId(skuInfoId);
      skuAttrValueMapper.insertSelective(skuAttrValue);
    });

    //插入销售属性列表
    List<PmsSkuSaleAttrValue> skuSaleAttrValues = pmsSkuInfo.getSkuSaleAttrValueList();
    skuSaleAttrValues.forEach(pmsSkuSaleAttrValue -> {
      pmsSkuSaleAttrValue.setSkuId(skuInfoId);
      skuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
    });

    //插入图像
    List<PmsSkuImage> pmsSkuImages = pmsSkuInfo.getSkuImageList();
    pmsSkuImages.forEach(pmsSkuImage -> {
      pmsSkuImage.setSkuId(skuInfoId);
      skuImageMapper.insertSelective(pmsSkuImage);
    });

    return "success";

  }

  @Override
  public PmsSkuInfo getSkuFromDb(Integer skuId) {
    PmsSkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
    if(skuInfo==null){
      return null;
    }
    PmsSkuImage skuImage = new PmsSkuImage();
    skuImage.setSkuId(skuId);
    List<PmsSkuImage> skuImages = skuImageMapper.select(skuImage);
    skuInfo.setSkuImageList(skuImages);

    PmsSkuSaleAttrValue skuSaleAttrValue=new PmsSkuSaleAttrValue();
    skuSaleAttrValue.setSkuId(skuId);
    List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.select(skuSaleAttrValue);
    skuInfo.setSkuSaleAttrValueList(skuSaleAttrValueList);
    return skuInfo;
  }


  @Override
  public PmsSkuInfo getSkuById(Integer skuId) {

    PmsSkuInfo skuInfo = new PmsSkuInfo();

    //链接缓存
    Jedis jedis = redisUtil.getJedis();

    //查询缓存
    String skuKey = "sku:"+skuId+":info";
    String skuJson = jedis.get(skuKey);
    if(StringUtils.isNotBlank(skuJson)) {
      skuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
    }
    else{  //如果缓存没有，就查mysql

      //设置分布式锁
      //使用redis自带的分布式锁
      String skuLock = "sku:"+skuId+":lock";
      String token = UUID.randomUUID().toString();
      SetParams setParams = SetParams.setParams().nx().px(10000);
      String result = jedis.set(skuLock,token,setParams);
      if(StringUtils.isNotBlank(result)&& "OK".equals(result)){
        //10秒过期时间内有权访问数据库
        skuInfo = getSkuFromDb(skuId);
        //mysql查询后存入redis
        if(skuInfo!=null) {
          jedis.set(skuKey, JSON.toJSONString(skuInfo));
        }
        else {
          //防止缓存穿透，防止访问者通过使用不存在的key来绕过redis
          jedis.setex(skuKey, 60 * 3, JSON.toJSONString(""));
        }

        //访问完成后，将分布式锁释放,让其他人可以访问
        String lockToken = jedis.get(skuLock); //保证要删自己的锁
        if(StringUtils.isNotBlank(lockToken)&&lockToken.equals(token)) {
          //可以用lua脚本在查到key的同时删掉key，防止高并发下意外的发生
          jedis.del(skuLock);
        }
      }
      else{
        //设置失败,自旋(该线程睡眠几秒后，重新尝试访问)
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return getSkuById(skuId);
      }
    }
    //关闭jedis
    jedis.close();

    return skuInfo;
  }

  @Override
  public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Integer productId) {
    return skuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
  }

  @Override
  public List<PmsSkuInfo> getAllSku(Integer catalog3Id) {
    PmsSkuInfo skuInfo = new PmsSkuInfo();
    skuInfo.setCatalog3Id(catalog3Id);
    List<PmsSkuInfo> skuInfos = skuInfoMapper.select(skuInfo);
    skuInfos.forEach(item->{
      PmsSkuAttrValue skuAttrValue = new PmsSkuAttrValue();
      skuAttrValue.setSkuId(item.getId());
      List<PmsSkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
      item.setSkuAttrValueList(skuAttrValueList);
    });
    return skuInfos;
  }

  @Override
  public boolean checkPrice(Integer productSkuId, BigDecimal productPrice) {
    boolean b = false;
    PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
    pmsSkuInfo.setId(productSkuId);
    PmsSkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(pmsSkuInfo);
    if(skuInfo!=null){
      if(skuInfo.getPrice().compareTo(productPrice)==0){
        b = true;
      }
    }
    return b;
  }

}
