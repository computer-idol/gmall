package com.wang.gmall.manage.controller;

import com.wang.gmall.bean.PmsSkuInfo;
import com.wang.gmall.service.SkuService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SkuController {

    @Reference
    private SkuService skuService;

    /**
     * 保存信息
     * @param pmsSkuInfo
     * @return
     */
    @RequestMapping(value="saveSkuInfo",produces = "application/json;charset=UTF-8")
    public String  savaSku(@RequestBody PmsSkuInfo pmsSkuInfo){
        System.out.println(pmsSkuInfo);
        return skuService.saveSkuInfo(pmsSkuInfo);
    }
}
