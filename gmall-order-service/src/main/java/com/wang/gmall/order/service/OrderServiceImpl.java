package com.wang.gmall.order.service;

import com.wang.gmall.bean.OmsOrder;
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

  @Override
  public String genTradeCode(String memberId) {
    return null;
  }

  @Override
  public String checkTradeCode(String memberId, String tradeCode) {
    return null;
  }

  @Override
  public void saveOrder(OmsOrder omsOrder) {

  }

  @Override
  public OmsOrder getOrderByOutTradeNo(String outTradeNo) {
    return null;
  }

  @Override
  public void updateOrderStatus(String outTradeNo) {

  }

}
