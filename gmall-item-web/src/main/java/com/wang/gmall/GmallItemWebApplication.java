package com.wang.gmall;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class GmallItemWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(GmallItemWebApplication.class, args);
  }

}
