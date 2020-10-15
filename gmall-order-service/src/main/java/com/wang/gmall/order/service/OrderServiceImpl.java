package com.wang.gmall.order.service;

import com.wang.gmall.order.mapper.OrderItemMapper;
import com.wang.gmall.order.mapper.OrderMapper;
import com.wang.gmall.service.OrderService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderMapper orderMapper;

  @Autowired
  private OrderItemMapper orderItemMapper;
}
