package com.wang.gmall.manage.service;

import com.wang.gmall.bean.*;
import com.wang.gmall.manage.mapper.*;
import com.wang.gmall.service.ProductService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Component
public class ProductServiceImpl implements ProductService {

  @Autowired
  private ProductInfoMapper productInfoMapper;

  @Autowired
  private ProductImageMapper productImageMapper;

  @Autowired
  private ProductSaleAttrMapper productSaleAttrMapper;

  @Autowired
  private ProductSaleAttrValueMapper productSaleAttrValueMapper;

  @Override
  public List<PmsProductInfo> spuList(Integer catalog3Id) {
    Example example = new Example(PmsProductInfo.class);
    example.createCriteria().andEqualTo("catalog3Id",catalog3Id);
    return productInfoMapper.selectByExample(example);
  }

  @Override
  public String savaSpuInfo(PmsProductInfo pmsProductInfo) {
    productInfoMapper.insertSelective(pmsProductInfo);
    int productId = pmsProductInfo.getId();
    List<PmsProductImage> imageList= pmsProductInfo.getSpuImageList();
    List<PmsProductSaleAttr> attrList = pmsProductInfo.getSpuSaleAttrList();
    imageList.forEach(item->{
        item.setProductId(productId);
        productImageMapper.insertSelective(item);
    });

    attrList.forEach(saleAttr->{
      saleAttr.setProductId(productId);
      productSaleAttrMapper.insertSelective(saleAttr);
      List<PmsProductSaleAttrValue> saleAttrValues = saleAttr.getSpuSaleAttrValueList();
      saleAttrValues.forEach(saleAttrValue->{
        saleAttrValue.setProductId(productId);
        saleAttrValue.setSaleAttrId(saleAttr.getId());
        productSaleAttrValueMapper.insertSelective(saleAttrValue);
      });
    });
    return "success";
  }

  @Override
  public List<PmsProductSaleAttr> spuSaleAttrList(Integer spuId) {
      PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
      pmsProductSaleAttr.setProductId(spuId);
      List<PmsProductSaleAttr> pmsProductSaleAttrs = productSaleAttrMapper.select(pmsProductSaleAttr);
      pmsProductSaleAttrs.forEach(pmsProductSaleAttr2->{
        PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
        pmsProductSaleAttrValue.setProductId(spuId);
        pmsProductSaleAttrValue.setSaleAttrId(pmsProductSaleAttr2.getSaleAttrId());
        List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = productSaleAttrValueMapper.select(pmsProductSaleAttrValue);
        pmsProductSaleAttr2.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
      });
      return  pmsProductSaleAttrs;
  }

  @Override
  public List<PmsProductImage> spuImageList(Integer spuId) {
    PmsProductImage pmsProductImage = new PmsProductImage();
    pmsProductImage.setProductId(spuId);
    return productImageMapper.select(pmsProductImage);
  }

  @Override
  public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(Integer productId,Integer skuId) {
//    PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
//    pmsProductSaleAttr.setProductId(productId);
//    List<PmsProductSaleAttr> productSaleAttrs= productSaleAttrMapper.select(pmsProductSaleAttr);
//
//    productSaleAttrs.forEach(productSaleAttr->{
//      PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
//      pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());
//      pmsProductSaleAttrValue.setProductId(productId);
//      List<PmsProductSaleAttrValue> productSaleAttrValues = productSaleAttrValueMapper.select(pmsProductSaleAttrValue);
//      productSaleAttr.setSpuSaleAttrValueList(productSaleAttrValues);
//    });
//    return productSaleAttrs;
    return productSaleAttrMapper.selectSaleAttrListCheckBySku(productId,skuId);
  }
}
