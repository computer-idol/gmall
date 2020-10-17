package com.wang.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.passport.util.Oauth2Util;
import com.wang.gmall.service.UserService;
import com.wang.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

  @Reference
  UserService userService;

  @RequestMapping(value = "index.html")
  public String index(@RequestParam(value = "ReturnUrl",required = false) String ReturnUrl, ModelMap modelMap){
    if(StringUtils.isNotBlank(ReturnUrl)){
      modelMap.put("ReturnUrl",ReturnUrl);
    }
    return "index";
  }

  @RequestMapping(value="login",produces = "application/json; charset=utf-8")
  @ResponseBody
  public String login(@RequestBody UmsMember umsMember, HttpServletRequest request){
    String token = "";
    UmsMember umsMember1 = userService.login(umsMember);
    if(umsMember1!=null){
      //登录成功
      //使用jwt制作token
      Integer userId = umsMember1.getId();
      String username = umsMember1.getUsername();
      //需要加密，先不写
      token = getJwtToken(request,userId,username);
      //redis存入token
      userService.addToken(token,umsMember1.getId());
    }
    else{
      token = "fail";
    }
    return token;
  }

  //微博登录的回调，获取授权码
  @RequestMapping(value="vlogin",produces = "application/json; charset=utf-8")
  public String vlogin(@RequestParam("code")String code,HttpServletRequest request){
     Map<String,String> access = Oauth2Util.getAccess(code);
     Map<String,String> userMap = Oauth2Util.getUserMessage(access);
     String accessToken = access.get("access_token");
     String uid = access.get("uid");
     String name = userMap.get("screen_name");
     String location = userMap.get("location");
     //用户信息存到数据库,类型设成微博用户
     UmsMember umsMember = new UmsMember();
     umsMember.setAccessToken(accessToken);
     umsMember.setSourceUid(uid);
     umsMember.setSourceType(2);
     umsMember.setUsername(name);
     umsMember.setCity(location);

     //方法里已经判断了是否原来存在
     umsMember = userService.addOauth(umsMember);
     //生成jwtToken，重定向到首页，携带token
     Integer userId = umsMember.getId();
     String username = umsMember.getUsername();
     //需要加密，先不写
     String token = getJwtToken(request,userId,username);
     //redis存入token
     userService.addToken(token,userId);
     return "redirect:http://list.mall.com/index.html?token="+token;
  }

  @RequestMapping(value="verify")
  @ResponseBody
  public String verify(String token,String ip){

    Map<String,Object> map = new HashMap<>();
    Map<String,Object> decode = JwtUtil.decode(token,"centos7wang",ip);

    //成功
    if(decode!=null) {
      map.put("status", "success");
      map.put("memberId", decode.get("memberId"));
      map.put("username", decode.get("username"));
    }else{
      map.put("status","fail");
    }
    return JSON.toJSONString(map);
  }

  public String getIp(HttpServletRequest request){
    //获取ip
    String ip = request.getHeader("x-forwarded-for");//通过nginx转发的ip
    if(StringUtils.isBlank(ip)) {
      ip = request.getRemoteAddr();
      if (StringUtils.isBlank(ip)) {
        ip = "127.0.0.1";
      }
    }
    return ip;
  }

  public String getJwtToken(HttpServletRequest request,Integer userId,String username){
    Map<String,Object> userMap = new HashMap<>();
    userMap.put("memberId",userId);
    userMap.put("username",username);

    //获取ip
    String ip = getIp(request);

    //需要加密，先不写
    return JwtUtil.encode("centos7wang",userMap, ip);
  }

}
