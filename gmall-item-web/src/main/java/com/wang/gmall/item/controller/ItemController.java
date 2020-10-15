package com.wang.gmall.item.controller;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.bean.PmsProductSaleAttr;
import com.wang.gmall.bean.PmsSkuInfo;
import com.wang.gmall.bean.PmsSkuSaleAttrValue;
import com.wang.gmall.service.ProductService;
import com.wang.gmall.service.SkuService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

  @Reference
  SkuService skuService;

  @Reference
  ProductService productService;

  @RequestMapping("/{skuId}.html")
  public String getSkuInfo(@PathVariable("skuId") Integer skuId, ModelMap map){


    PmsSkuInfo skuInfo = skuService.getSkuById(skuId);

    //sku
    map.put("skuInfo",skuInfo);

    if(skuInfo!=null) {

      //销售属性列表
      List<PmsProductSaleAttr> productSaleAttrList = productService.spuSaleAttrListCheckBySku(skuInfo.getProductId(),skuInfo.getId());
      map.put("spuSaleAttrListCheckBySku", productSaleAttrList);

      //查询相关sku的hash集合
      Map<String,Integer> skuHashList = new HashMap<String,Integer>();
      List<PmsSkuInfo> skuInfos = skuService.getSkuSaleAttrValueListBySpu(skuInfo.getProductId());
      System.out.println(skuInfos);
      for(PmsSkuInfo pmsSkuInfo : skuInfos){
          String k = "";
          Integer v = pmsSkuInfo.getId();

          List<PmsSkuSaleAttrValue> skuSaleAttrValues = pmsSkuInfo.getSkuSaleAttrValueList();
          for(PmsSkuSaleAttrValue pmsProductSaleAttr : skuSaleAttrValues){
             k += pmsProductSaleAttr.getSaleAttrValueId()+"|";
          }
          skuHashList.put(k,v);
      };
      String skuHashStr = JSON.toJSONString(skuHashList);
      System.out.println(skuHashStr);
      map.put("skuHashMap",skuHashStr);
    }

    return "item";
  }

}
