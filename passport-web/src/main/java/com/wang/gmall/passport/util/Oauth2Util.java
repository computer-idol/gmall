package com.wang.gmall.passport.util;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.util.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;

public class Oauth2Util {

  //获取用户信息
  public static Map<String,String> getUserMessage(Map<String,String> access){
    String accessToken = access.get("access_token");
    String uid = access.get("uid");
    String s3 = HttpClientUtil.doGet("https://api.weibo.com/2/users/show.json?access_token="+accessToken+"&uid="+uid);
    return JSON.parseObject(s3,Map.class);
  }

  //获取access_token和uid
  public static Map<String,String> getAccess(String code){
    String accessUrl="https://api.weibo.com/oauth2/access_token";
    Map<String,String> map = new HashMap<>();
    map.put("client_id","1876835754");
    map.put("client_secret","2736077c4a99ef71934f8fa1a03b9799");
    map.put("grant_type","authorization_code");
    map.put("redirect_uri","http://passport.mall.com/vlogin");
    map.put("code",code);
    String s2 = HttpClientUtil.doPost(accessUrl,map);
    System.out.println(s2);
    return JSON.parseObject(s2,Map.class);
  }

}
