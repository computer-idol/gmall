package com.wang.gmall.order.controller;


import com.wang.gmall.annotations.LoginRequire;
import com.wang.gmall.bean.OmsCartItem;
import com.wang.gmall.bean.OmsOrderItem;
import com.wang.gmall.bean.UmsMemberReceiveAddress;
import com.wang.gmall.service.CartService;
import com.wang.gmall.service.OrderService;
import com.wang.gmall.service.UserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

  @Reference
  OrderService orderService;

  @Reference
  CartService cartService;

  @Reference
  UserService userService;

  @RequestMapping(value="submitOrder")
  @LoginRequire(mustLoginSuccess = true) //必须要登录成功
  public String submitOrder(int receiveAddressId,BigDecimal totalAmount,HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
      return null;
  }

  @RequestMapping(value="toTrade")
  @LoginRequire(mustLoginSuccess = true) //必须要登录成功
  public String tradeCart(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
    Integer memberId = (Integer)request.getAttribute("memberId");
    String username = (String)request.getAttribute("username");

    //购物车列表
    List<OmsCartItem> cartList = cartService.cartList(memberId);

    //地址列表
    List<UmsMemberReceiveAddress> addressList = userService.getUserAddressList(memberId);

    List<OmsOrderItem> orderItemList = new ArrayList<>();
    for(OmsCartItem omsCartItem:cartList){
      if(omsCartItem.getIsChecked()==1) {//被选中的商品才能提交
        OmsOrderItem omsOrderItem = new OmsOrderItem();
        omsOrderItem.setProductName(omsCartItem.getProductName());
        omsOrderItem.setProductPic(omsCartItem.getProductPic());
        omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
        omsOrderItem.setProductPrice(omsCartItem.getPrice());
        orderItemList.add(omsOrderItem);
      }
    }
    BigDecimal totalAmount = getTotalAmount(orderItemList);
    modelMap.put("orderItemList",orderItemList);
    modelMap.put("userAddressList",addressList);
    modelMap.put("totalAmount",totalAmount);
    modelMap.put("nickName",username);
    return "trade";
  }

  public BigDecimal getTotalAmount(List<OmsOrderItem> omsOrderItems){
    BigDecimal bigDecimal = new BigDecimal(0);
    for(OmsOrderItem omsOrderItem:omsOrderItems){
      BigDecimal totalPrice = omsOrderItem.getProductPrice().multiply(omsOrderItem.getProductQuantity());
      bigDecimal = bigDecimal.add(totalPrice);
    }
    return bigDecimal;
  }

}
