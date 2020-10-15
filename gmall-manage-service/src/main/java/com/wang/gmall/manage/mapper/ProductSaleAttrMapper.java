package com.wang.gmall.manage.mapper;

import com.wang.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {

  List<PmsProductSaleAttr> selectSaleAttrListCheckBySku(@Param("productId")Integer productId, @Param("skuId") Integer skuId);

}
