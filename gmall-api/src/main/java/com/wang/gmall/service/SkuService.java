package com.wang.gmall.service;

import com.wang.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {

  String saveSkuInfo(PmsSkuInfo pmsSkuInfo);

  PmsSkuInfo getSkuFromDb(Integer skuId);

  PmsSkuInfo getSkuById(Integer skuId);

  List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Integer productId);

  List<PmsSkuInfo> getAllSku(Integer catalog3Id);

  boolean checkPrice(Integer productSkuId, BigDecimal productPrice);
}
