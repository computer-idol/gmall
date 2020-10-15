package com.wang.gmall.payment;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
@MapperScan("com.wang.gmall.payment.mapper")
public class PaymentApplication {
}
