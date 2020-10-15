package com.wang.gmall.list.util;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class JedisClientUtil {

  public static JestClient getJestClient(){
    JestClientFactory factory = new JestClientFactory();
    factory.setHttpClientConfig(new HttpClientConfig
            .Builder("http://47.112.214.8:9200")
            .multiThreaded(true)
            .build());
    return  factory.getObject();
  }
}
