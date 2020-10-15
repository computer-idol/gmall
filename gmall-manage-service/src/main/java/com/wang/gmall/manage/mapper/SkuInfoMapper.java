package com.wang.gmall.manage.mapper;

import com.wang.gmall.bean.PmsSkuInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuInfoMapper extends Mapper<PmsSkuInfo> {
  List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(Integer productId);
}
