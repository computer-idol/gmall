package com.wang.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.util.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;

public class TestOauth2 {

  public static void main(String[] args) {
//    String s1 = HttpClientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=1876835754&response_type=code&redirect_uri=http://passport.mall.com/vlogin");
//    System.out.println(s1);

    String code = "d08fae7594b4363edda0b611a8e797e2";
    String access_url="https://api.weibo.com/oauth2/access_token";
    Map<String,String> map = new HashMap<>();
    map.put("client_id","1876835754");
    map.put("client_secret","2736077c4a99ef71934f8fa1a03b9799");
    map.put("grant_type","authorization_code");
    map.put("redirect_uri","http://passport.mall.com/vlogin");
    map.put("code",code);
//    String s2 = HttpClientUtil.doPost(access_url,map);
//    System.out.println(s2);
    //Map<String,String> map2 = JSON.parseObject(s2,Map.class);
    //拿到的access_token
    //{"access_token":"2.00pmpXaG7iABDC9f6d3fad37xjpuHD","remind_in":"157679999","expires_in":157679999,"uid":"6036808983","isRealName":"true"}
    String access_token = "2.00pmpXaG7iABDC9f6d3fad37xjpuHD";  //mp2.get("access_token");
    String uid = "6036808983";
    //请求用户数据
    String s3 = HttpClientUtil.doGet("https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid);
    Map<String,String> map3 = JSON.parseObject(s3,Map.class);
    System.out.println(map3);
  }
}
