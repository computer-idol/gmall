package com.wang.gmall.service;

import com.wang.gmall.bean.PmsProductImage;
import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface ProductService {
  List<PmsProductInfo> spuList(Integer catalog3Id);

  String savaSpuInfo(PmsProductInfo pmsProductInfo);

  List<PmsProductSaleAttr> spuSaleAttrList(Integer spuId);

  List<PmsProductImage> spuImageList(Integer spuId);

  List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(Integer productId,Integer skuInfo);
}
