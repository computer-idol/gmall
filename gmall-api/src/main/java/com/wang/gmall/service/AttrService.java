package com.wang.gmall.service;

import com.wang.gmall.bean.PmsBaseAttrInfo;
import com.wang.gmall.bean.PmsBaseAttrValue;
import com.wang.gmall.bean.PmsBaseSaleAttr;
import com.wang.gmall.bean.PmsSkuAttrValue;

import java.util.List;
import java.util.Set;

public interface AttrService {
  List<PmsBaseAttrInfo> attrInfoList(Integer catalog3Id);

  String savaAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

  List<PmsBaseAttrValue> attrValueList(Integer attrId);

  List<PmsBaseSaleAttr> baseSaleAttrList();

  List<PmsBaseAttrInfo> getAttrValueListByValueIds(Set<Integer> valueIdSet);
}
