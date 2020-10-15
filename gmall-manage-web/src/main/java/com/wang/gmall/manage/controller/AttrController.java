package com.wang.gmall.manage.controller;

import com.wang.gmall.bean.PmsBaseAttrInfo;
import com.wang.gmall.bean.PmsBaseAttrValue;
import com.wang.gmall.bean.PmsBaseSaleAttr;
import com.wang.gmall.service.AttrService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class AttrController {

  @Reference
  private AttrService attrService;

  /**
   *
   * 获取平台属性列表
   * @param catalog3Id
   * @return
   */
  @RequestMapping( value = "attrInfoList", produces = "application/json;charset=UTF-8" )
  public List<PmsBaseAttrInfo> attrInfoList(Integer catalog3Id) {

    return attrService.attrInfoList(catalog3Id);
  }

  /**
   * 保存平台属性
   * @param pmsBaseAttrInfo
   * @return
   */
  @RequestMapping("saveAttrInfo")
  @ResponseBody
  public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
    return attrService.savaAttrInfo(pmsBaseAttrInfo);
  }

  /**
   * 修改平台属性
   * @param attrId
   * @return
   */
  @RequestMapping( value = "getAttrValueList", produces = "application/json;charset=UTF-8" )
  public List<PmsBaseAttrValue> attrValueList(@RequestParam("attrId") Integer attrId) {

    return attrService.attrValueList(attrId);
  }

  @RequestMapping(value="baseSaleAttrList",produces = "application/json;charset=UTF-8" )
  public List<PmsBaseSaleAttr> getBaseAttrList(){
    return attrService.baseSaleAttrList();
  }

}
