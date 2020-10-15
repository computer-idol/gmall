package com.wang.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.annotations.LoginRequire;
import com.wang.gmall.util.CookieUtil;
import com.wang.gmall.util.HttpClientUtil;
import com.wang.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

  /**
   * @param request  request
   * @param response response
   * @param handler  handler
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
      //判断拦截方法是否有LoginRequire注解
      if(handler instanceof ResourceHttpRequestHandler){
        return true;
      }
      HandlerMethod hm = (HandlerMethod)handler;
      LoginRequire loginRequire = hm.getMethodAnnotation(LoginRequire.class);
      if(loginRequire==null){
        return true;
      }

      //获取token
      String token = "";
      String oldToken = CookieUtil.getCookieValue(request,"oldToken",true);
      String newToken = request.getParameter("token");
      if(StringUtils.isNotBlank(oldToken)){
        token = oldToken;
      }
      if(StringUtils.isNotBlank(newToken)){
        token = newToken;
      }
      String ip = request.getHeader("x-forwarded-for");//通过nginx转发的ip
      if(StringUtils.isBlank(ip)) {
        ip = request.getRemoteAddr();
        if (StringUtils.isBlank(ip)) {
          ip = "127.0.0.1";
        }
      }
      Map<String,Object> successMap = new HashMap<>();
      String success = "fail";
      //向认证中心请求认证
      if(StringUtils.isNotBlank(token)) {

        String successJson = HttpClientUtil.doGet("http://passport.mall.com/verify?token=" + token+"&ip="+ip);
        System.out.println(successJson);
        successMap = JSON.parseObject(successJson,Map.class);
        success = (String)successMap.get("status");
      }
        //需要登录拦截
      if(loginRequire.mustLoginSuccess()){//必须要登录成功才能继续
        if(!"success".equals(success)){
           //重定向会登录
          StringBuffer returnUrlBuf = request.getRequestURL();
          response.sendRedirect("http://passport.mall.com/index.html?ReturnUrl="+returnUrlBuf);
          return false;
        }
        request.setAttribute("memberId",successMap.get("memberId"));
        request.setAttribute("username",successMap.get("username"));
        //验证通过,覆盖cookie中的token
        if(StringUtils.isNotBlank(token)) {
          CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
        }
      }else {
        //即使登录失败也能使用，如购物车,但登录与未登录使用方式不同
        if("success".equals(success)){
          request.setAttribute("memberId",successMap.get("memberId"));
          request.setAttribute("username",successMap.get("username"));
          //验证通过,覆盖cookie中的token
          if(StringUtils.isNotBlank(token)) {
            CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
          }
        }
      }

      return true;
  }

  /**
   * @param request  request
   * @param response response
   * @param handler  handler
   * @param ex       ex
   */
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

  }

}
