package com.wang.gmall.manage.controller;


import com.wang.gmall.bean.PmsProductImage;
import com.wang.gmall.bean.PmsProductInfo;
import com.wang.gmall.bean.PmsProductSaleAttr;
import com.wang.gmall.service.ProductService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
public class SpuController {

  @Reference
  ProductService productService;

  /**
   * 获取spu销售属性
   * @param spuId
   * @return
   */
  @RequestMapping( value = "spuSaleAttrList", produces = "application/json;charset=UTF-8" )
  public List<PmsProductSaleAttr> spuSaleAttrList(Integer spuId) {

    return productService.spuSaleAttrList(spuId);
  }

  /**
   * 获取spu销售属性
   * @param spuId
   * @return
   */
  @RequestMapping( value = "spuImageList", produces = "application/json;charset=UTF-8" )
  public List<PmsProductImage> spuImageList(Integer spuId) {

    return productService.spuImageList(spuId);
  }

  /**
   * 获取spu属性
   * @param catalog3Id
   * @return
   */
  @RequestMapping( value = "spuList", produces = "application/json;charset=UTF-8" )
  public List<PmsProductInfo> spuList(Integer catalog3Id) {

    return productService.spuList(catalog3Id);
  }

  /**
   * 保存信息
   * @param pmsProductInfo
   * @return
   */
  @RequestMapping(value="saveSpuInfo",produces = "application/json;charset=UTF-8")
  public String  savaSpu(@RequestBody PmsProductInfo pmsProductInfo){
    return productService.savaSpuInfo(pmsProductInfo);
  }

  /**
   * 上传图片
   */
  @RequestMapping(value="fileUpload",produces = "application/json;charset=UTF-8")
  public String uploadFile(@RequestParam("file") MultipartFile multipartFile){
    return "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3716669321,3674639441&fm=26&gp=0.jpg";
  }

}
