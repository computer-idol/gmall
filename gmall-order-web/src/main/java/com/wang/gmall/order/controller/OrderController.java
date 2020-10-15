package com.wang.gmall.order.controller;


import com.wang.gmall.service.OrderService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

  @Reference
  OrderService orderService;


}
