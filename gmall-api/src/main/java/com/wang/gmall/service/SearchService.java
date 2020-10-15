package com.wang.gmall.service;

import com.wang.gmall.bean.PmsSearchSkuInfo;
import com.wang.gmall.bean.PmsSearchSkuParam;

import java.util.List;

public interface SearchService {

  List<PmsSearchSkuInfo> list(PmsSearchSkuParam searchSkuParam)  throws Exception;

}
