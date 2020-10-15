package com.wang.gmall.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class GmallOrderWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(GmallOrderWebApplication.class, args);
  }
}
