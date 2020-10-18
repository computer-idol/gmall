package com.wang.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.bean.OmsCartItem;
import com.wang.gmall.cart.mapper.OrderCartMapper;
import com.wang.gmall.service.CartService;
import com.wang.gmall.util.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

  @Autowired
  OrderCartMapper orderCartMapper;

  @Autowired
  RedisUtil redisUtil;

  @Override
  public OmsCartItem ifCartExistByMemberId(Integer memberId, Integer skuId) {
    OmsCartItem omsCartItem = new OmsCartItem();
    omsCartItem.setMemberId(memberId);
    omsCartItem.setProductSkuId(skuId);
    return orderCartMapper.selectOne(omsCartItem);
  }

  @Override
  public void addCart(OmsCartItem omsCartItem) {
    if(omsCartItem.getMemberId()!=null){
      orderCartMapper.insertSelective(omsCartItem);
    }
  }

  @Override
  public void updateCart(OmsCartItem omsCartItemFromDb) {
    orderCartMapper.updateByPrimaryKeySelective(omsCartItemFromDb);
  }

  //同步缓存
  @Override
  public void flushCartDb(Integer memberId) {
     OmsCartItem omsCartItem = new OmsCartItem();
     omsCartItem.setMemberId(memberId);
     List<OmsCartItem> omsCartItemList = orderCartMapper.select(omsCartItem);
     String key = "user:"+memberId+":cart";
     Map<String,String> map= new HashMap<>();
     for(OmsCartItem omsCartItem1 :omsCartItemList){
       omsCartItem1.setTotalPrice(omsCartItem1.getPrice().multiply(omsCartItem1.getQuantity()));
       map.put(omsCartItem1.getProductSkuId()+"", JSON.toJSONString(omsCartItem1));
     }
     Jedis jedis = redisUtil.getJedis();
     jedis.del(key);
     jedis.hmset(key,map);
     jedis.close();
  }

  @Override
  public List<OmsCartItem> cartList(Integer memberId) {
    List<OmsCartItem> omsCartItems = new ArrayList<>();
    String key = "user:"+memberId+":cart";
    Jedis jedis = redisUtil.getJedis();
    List<String> vals= jedis.hvals(key);
    for(String val:vals){
      OmsCartItem omsCartItem = JSON.parseObject(val,OmsCartItem.class);
      omsCartItems.add(omsCartItem);
    }
    jedis.close();
    return omsCartItems;
  }

  @Override
  public void checkCart(OmsCartItem omsCartItem) {
    Example example = new Example(OmsCartItem.class);
    example.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());
    orderCartMapper.updateByExampleSelective(omsCartItem,example);
    //缓存同步
    flushCartDb(omsCartItem.getMemberId());

  }

  @Override
  public void delCart(Integer productSkuId, Integer memberId) {
    OmsCartItem omsCartItem = new OmsCartItem();
    omsCartItem.setMemberId(memberId);
    omsCartItem.setProductSkuId(productSkuId);
    orderCartMapper.delete(omsCartItem);
  }
}
