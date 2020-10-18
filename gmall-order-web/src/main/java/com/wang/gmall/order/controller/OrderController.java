package com.wang.gmall.order.controller;


import com.wang.gmall.annotations.LoginRequire;
import com.wang.gmall.bean.OmsCartItem;
import com.wang.gmall.bean.OmsOrder;
import com.wang.gmall.bean.OmsOrderItem;
import com.wang.gmall.bean.UmsMemberReceiveAddress;
import com.wang.gmall.service.CartService;
import com.wang.gmall.service.OrderService;
import com.wang.gmall.service.SkuService;
import com.wang.gmall.service.UserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

  @Reference
  OrderService orderService;

  @Reference
  CartService cartService;

  @Reference
  UserService userService;

  @Reference
  SkuService skuService;

  @RequestMapping(value="submitOrder")
  @LoginRequire(mustLoginSuccess = true) //必须要登录成功
  public String submitOrder(String tradeCode,int receiveAddressId,BigDecimal totalAmount,HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
    Integer memberId = (Integer)request.getAttribute("memberId");
    String username = (String)request.getAttribute("username");

    //检验交易码，不相等无法提交，防止同一订单重复提交
    String result = orderService.checkTradeCode(memberId,tradeCode);
    if("success".equals(result)) {
      //根据memberId获得(商品)购物车列表

      String outTradeNo = "gmall";
      outTradeNo += username + System.currentTimeMillis();//将毫秒时间戳拼接到外部订单号

      OmsOrder omsOrder = new OmsOrder();
      omsOrder.setMemberId(memberId);
      omsOrder.setOrderSn(outTradeNo); //设置外部订单号
      omsOrder.setPayAmount(totalAmount);
      omsOrder.setOrderType(1);
      omsOrder.setAutoConfirmDay(7); //过期时间
      omsOrder.setCreateTime(new Date());
      omsOrder.setMemberUsername(username);
      omsOrder.setPayAmount(totalAmount);

      //订单地址
      UmsMemberReceiveAddress userAddress = userService.getUserAddresById(receiveAddressId);
      omsOrder.setReceiverName(username);
      omsOrder.setReceiverCity(userAddress.getCity());
      omsOrder.setReceiverDetailAddress(userAddress.getDetailAddress());
      omsOrder.setReceiverProvince(userAddress.getProvince());
      omsOrder.setReceiverRegion(userAddress.getRegion());
      omsOrder.setReceiverPhone(userAddress.getPhoneNumber());
      omsOrder.setReceiverPostCode(userAddress.getPostCode());

      //收货日期
      //当前日期加一天
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DATE, 1);
      Date date = calendar.getTime();
      omsOrder.setReceiveTime(date);

      omsOrder.setSourceType(0);
      omsOrder.setStatus(0);

      List<OmsOrderItem> omsOrderItems = new ArrayList<>();
      List<OmsCartItem> omsCartItems = cartService.cartList(memberId);

      for(OmsCartItem omsCartItem:omsCartItems){
        if(omsCartItem.getIsChecked().equals(1)){
          OmsOrderItem omsOrderItem = new OmsOrderItem();
          //验价,可能在提交时价格有变动
          boolean b = skuService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
          if(b==false){//验价失败，回滚，重新选择
             return "tradeFail";
          }

          //验证库存

          omsOrderItem.setProductPrice(omsCartItem.getPrice());
          omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
          omsOrderItem.setProductPic(omsCartItem.getProductPic());
          omsOrderItem.setProductName(omsCartItem.getProductName());
          omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
          omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
          omsOrderItem.setProductSkuCode("11111111111");
          omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
          omsOrderItem.setProductId(omsCartItem.getProductId());
          omsOrderItem.setProductSn("仓库对应的商品编号");//在仓库中对应的skuId
          omsOrderItem.setOrderSn(outTradeNo);//外部订单号

          omsOrderItems.add(omsOrderItem);
        }
      }

      //写入订单
      omsOrder.setOmsOrderItems(omsOrderItems);
      orderService.saveOrder(omsOrder);

      //重定向到支付系统
    }else{
      return "fail";
    }

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

    //生成交易码，用于校验
    String tradeCode = orderService.generateTradeCode(memberId);
    modelMap.put("tradeCode",tradeCode);
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
