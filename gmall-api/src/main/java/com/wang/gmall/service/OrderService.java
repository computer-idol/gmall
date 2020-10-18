package com.wang.gmall.service;

import com.wang.gmall.bean.OmsOrder;

public interface OrderService {

  /**
   * 生成交易码
   * @param memberId 用户id
   * @return
   */
  String generateTradeCode(Integer memberId);

  /**
   * 检验交易码
   * @param memberId 用户id
   * @param tradeCode 交易码
   * @return
   */
  String checkTradeCode(Integer memberId,String tradeCode);

  /**
   * 保存订单
   * @param omsOrder
   */
  void saveOrder(OmsOrder omsOrder);

  /**
   * 通过订单号获得订单
   * @param outTradeNo
   * @return
   */
  OmsOrder getOrderByOutTradeNo(String outTradeNo);

  /**
   * 更新订单状态
   * @param outTradeNo
   */
  void updateOrderStatus(String outTradeNo);

}
