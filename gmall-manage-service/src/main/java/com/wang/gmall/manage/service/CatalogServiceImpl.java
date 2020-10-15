package com.wang.gmall.manage.service;

import com.wang.gmall.bean.PmsBaseCatalog1;
import com.wang.gmall.bean.PmsBaseCatalog2;
import com.wang.gmall.bean.PmsBaseCatalog3;
import com.wang.gmall.manage.mapper.Catalog1Mapper;
import com.wang.gmall.manage.mapper.Catalog2Mapper;
import com.wang.gmall.manage.mapper.Catalog3Mapper;
import com.wang.gmall.service.CatalogService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Component
public class CatalogServiceImpl implements CatalogService {

  @Autowired
  Catalog1Mapper catalog1Mapper;

  @Autowired
  Catalog2Mapper catalog2Mapper;

  @Autowired
  Catalog3Mapper catalog3Mapper;

  @Override
  public List<PmsBaseCatalog1> getCatalog1() {
    return catalog1Mapper.selectAll();
  }

  @Override
  public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id) {
    Example example = new Example(PmsBaseCatalog2.class);
    example.createCriteria().andEqualTo("catalog1Id",catalog1Id);
    return catalog2Mapper.selectByExample(example);
  }

  @Override
  public List<PmsBaseCatalog3> getCatalog3(Integer catalog2Id) {
    Example example = new Example(PmsBaseCatalog3.class);
    example.createCriteria().andEqualTo("catalog2Id",catalog2Id);
    return catalog3Mapper.selectByExample(example);
  }

}
