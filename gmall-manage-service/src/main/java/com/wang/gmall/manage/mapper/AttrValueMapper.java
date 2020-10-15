package com.wang.gmall.manage.mapper;

import com.wang.gmall.bean.PmsBaseAttrInfo;
import com.wang.gmall.bean.PmsBaseAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttrValueMapper extends Mapper<PmsBaseAttrValue> {

  List<PmsBaseAttrInfo> getAttrValueListByValueIds(String valueIds);
}
