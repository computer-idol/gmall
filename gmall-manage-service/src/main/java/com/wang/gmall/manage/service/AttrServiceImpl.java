package com.wang.gmall.manage.service;

import com.wang.gmall.bean.PmsBaseAttrInfo;
import com.wang.gmall.bean.PmsBaseAttrValue;
import com.wang.gmall.bean.PmsBaseSaleAttr;
import com.wang.gmall.bean.PmsSkuAttrValue;
import com.wang.gmall.manage.mapper.AttrInfoMapper;
import com.wang.gmall.manage.mapper.AttrValueMapper;
import com.wang.gmall.manage.mapper.BaseSaleAttrMapper;
import com.wang.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;

@Service
@Component
public class AttrServiceImpl implements AttrService {

  @Autowired
  AttrInfoMapper attrInfoMapper;

  @Autowired
  AttrValueMapper attrValueMapper;

  @Autowired
  BaseSaleAttrMapper baseSaleAttrMapper;

  @Override
  public List<PmsBaseAttrInfo> attrInfoList(Integer catalog3Id) {
    PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
    pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
    List<PmsBaseAttrInfo> baseAttrInfos = attrInfoMapper.select(pmsBaseAttrInfo);
    baseAttrInfos.forEach(pmsBaseAttrInfo1 -> {
      PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
      pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo1.getId());
      List<PmsBaseAttrValue> pmsBaseAttrValues = attrValueMapper.select(pmsBaseAttrValue);
      pmsBaseAttrInfo1.setAttrValueList(pmsBaseAttrValues);
    });
    return baseAttrInfos;
  }

  @Override
  public String savaAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
    Integer id = pmsBaseAttrInfo.getId();
    if(id==null){
      attrInfoMapper.insertSelective(pmsBaseAttrInfo);
      attrValueList.forEach(attrValue->{
        attrValue.setAttrId(pmsBaseAttrInfo.getId());
        attrValueMapper.insertSelective(attrValue);
      });
    }
    else{
      attrInfoMapper.updateByPrimaryKeySelective(pmsBaseAttrInfo);
      Example example = new Example(PmsBaseAttrValue.class);
      example.createCriteria().andEqualTo("attrId",pmsBaseAttrInfo.getId());
      attrValueMapper.deleteByExample(example);
      attrValueList.forEach(attrValue->{
        attrValueMapper.insertSelective(attrValue);
      });
    }

    return "success";
  }

  @Override
  public List<PmsBaseAttrValue> attrValueList(Integer attrId) {
    Example example = new Example(PmsBaseAttrValue.class);
    example.createCriteria().andEqualTo("attrId",attrId);
    return attrValueMapper.selectByExample(example);
  }

  @Override
  public List<PmsBaseSaleAttr> baseSaleAttrList() {
    return baseSaleAttrMapper.selectAll();
  }

  @Override
  public List<PmsBaseAttrInfo> getAttrValueListByValueIds(Set<Integer> valueIdSet) {
    String valueIds = StringUtils.join(valueIdSet,",");
    return attrValueMapper.getAttrValueListByValueIds(valueIds);
  }

}
