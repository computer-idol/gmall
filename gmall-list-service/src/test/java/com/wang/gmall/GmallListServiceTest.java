package com.wang.gmall;

import com.wang.gmall.bean.PmsSearchSkuInfo;
import com.wang.gmall.bean.PmsSkuInfo;
import com.wang.gmall.service.SkuService;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@EnableDubbo
@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceTest {

  @Reference
  private SkuService skuService;

  public JestClient getJestClient(){
    JestClientFactory factory = new JestClientFactory();
    factory.setHttpClientConfig(new HttpClientConfig
            .Builder("http://47.112.214.8:9200")
            .multiThreaded(true)
            .build());
    return  factory.getObject();
  }

  @Test
  public void contextLoads() throws Exception{

    JestClient jestClient = getJestClient();

    //查询mysql
    List<PmsSkuInfo> skuInfoList = new ArrayList<>();
    skuInfoList = skuService.getAllSku(61);

    //转化为es数据结构
    List<PmsSearchSkuInfo> skuSearchInfoList = new ArrayList<>();
    for(PmsSkuInfo skuInfo:skuInfoList){
      PmsSearchSkuInfo searchSkuInfo = new PmsSearchSkuInfo();
      BeanUtils.copyProperties(searchSkuInfo,skuInfo);
      skuSearchInfoList.add(searchSkuInfo);
    }
    //导入es
    for(PmsSearchSkuInfo searchSkuInfo2:skuSearchInfoList) {
      Index put = new Index.Builder(searchSkuInfo2).index("gmall").type("skuinfo").id(String.valueOf(searchSkuInfo2.getId())).build();
      jestClient.execute(put);
    }
  }
}
