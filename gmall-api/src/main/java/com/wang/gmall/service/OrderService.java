package com.wang.gmall.service;

import com.wang.gmall.bean.OmsOrder;

public interface OrderService {

  String genTradeCode(String memberId);

  String checkTradeCode(String memberId,String tradeCode);


  void saveOrder(OmsOrder omsOrder);

  OmsOrder getOrderByOutTradeNo(String outTradeNo);

  void updateOrderStatus(String outTradeNo);

}
