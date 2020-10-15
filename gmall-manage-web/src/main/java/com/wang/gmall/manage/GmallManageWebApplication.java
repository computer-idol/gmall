package com.wang.gmall.manage;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class GmallManageWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(GmallManageWebApplication.class, args);
  }
}


