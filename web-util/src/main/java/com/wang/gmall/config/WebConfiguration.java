package com.wang.gmall.config;

import com.wang.gmall.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Autowired
  AuthInterceptor authInteceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    //不拦截静态资源
    registry.addInterceptor(authInteceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/login","/css/**","/js/**","/img/**","/**/.html","/error");
  }
}
