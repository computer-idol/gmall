package com.wang.gmall.service;

import com.wang.gmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {

  OmsCartItem ifCartExistByMemberId(Integer memberId, Integer skuId);

  void addCart(OmsCartItem omsCartItem);

  void updateCart(OmsCartItem omsCartItemFromDb);

  void flushCartDb(Integer memberId);

  List<OmsCartItem> cartList(Integer memberId);

  void checkCart(OmsCartItem omsCartItem);

  void delCart(Integer productSkuId, Integer memberId);
}
