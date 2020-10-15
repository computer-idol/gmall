package com.wang.gmall.manage.controller;

import com.wang.gmall.bean.PmsBaseCatalog1;
import com.wang.gmall.bean.PmsBaseCatalog2;
import com.wang.gmall.bean.PmsBaseCatalog3;
import com.wang.gmall.service.CatalogService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class CatalogController {

  @Reference
  CatalogService catalogService;

  @RequestMapping(value = "getCatalog1",produces = "application/json;charset=UTF-8")
  public List<PmsBaseCatalog1> getCatalog1List(){
    return catalogService.getCatalog1();
  }

  @RequestMapping(value = "getCatalog2",produces = "application/json;charset=UTF-8")
  public List<PmsBaseCatalog2> getCatalog2(@RequestParam("catalog1Id") Integer catalog1Id){

    return catalogService.getCatalog2(catalog1Id);
  }


  @RequestMapping(value = "getCatalog3",produces = "application/json;charset=UTF-8")
  public List<PmsBaseCatalog3> getCatalog3(@RequestParam("catalog2Id") Integer catalog2Id){

    return catalogService.getCatalog3(catalog2Id);
  }
}
