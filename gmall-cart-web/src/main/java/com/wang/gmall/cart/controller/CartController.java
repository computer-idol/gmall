package com.wang.gmall.cart.controller;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.annotations.LoginRequire;
import com.wang.gmall.bean.OmsCartItem;
import com.wang.gmall.bean.PmsSkuInfo;
import com.wang.gmall.service.CartService;
import com.wang.gmall.service.SkuService;
import com.wang.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

  @Reference
  CartService cartService;

  @Reference
  SkuService skuService;

  @RequestMapping(value = "/cartList")
  @LoginRequire(mustLoginSuccess = false) //不一定要登录成功
  public String getCartList(HttpServletRequest request,ModelMap modelMap){
    List<OmsCartItem> omsCartItems = new ArrayList<>();
    Integer memberId = 1;
    if(memberId!=null){//查缓存
      omsCartItems = cartService.cartList(memberId);
    }
    else{//查cookie
      String cookieValue = CookieUtil.getCookieValue(request,"cartListCookie",true);
      if(StringUtils.isNotBlank(cookieValue)){
        omsCartItems = JSON.parseArray(cookieValue,OmsCartItem.class);
      }
    }

    for(OmsCartItem omsCartItem:omsCartItems){

      omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
    }
    modelMap.put("cartList",omsCartItems);
    return "cartList";
  }

  @RequestMapping(value="toTrade")
  @LoginRequire(mustLoginSuccess = true) //必须要登录成功
  public String tradeCart(HttpServletRequest request,HttpServletResponse response,ModelMap modelMap){
    Integer memberId = (Integer)request.getAttribute("memberId");
    String username = (String)request.getAttribute("username");
    return "One_JDshop";
  }

  @RequestMapping(value="checkCart")
  @LoginRequire(mustLoginSuccess = false) //不一定要登录成功
  public String checkCart(Integer isChecked,Integer skuId,HttpServletRequest request,HttpServletResponse response,ModelMap modelMap){
    Integer memberId = (Integer)request.getAttribute("memberId");
    String username = (String)request.getAttribute("username");
    List<OmsCartItem> omsCartItems = new ArrayList<>();
    if(memberId!=null) {
      //更新
      OmsCartItem omsCartItem = new OmsCartItem();
      omsCartItem.setIsChecked(isChecked);
      omsCartItem.setMemberId(memberId);
      omsCartItem.setProductSkuId(skuId);
      cartService.checkCart(omsCartItem);
      omsCartItems = cartService.cartList(memberId);
    }
    else{
      String cookieValue = CookieUtil.getCookieValue(request,"cartListCookie",true);
      if(StringUtils.isNotBlank(cookieValue)){
        omsCartItems = JSON.parseArray(cookieValue,OmsCartItem.class);
        for(OmsCartItem omsCartItem1:omsCartItems){
          if(omsCartItem1.getProductSkuId().equals(skuId)){
            omsCartItem1.setIsChecked(isChecked);
          }
        }
        CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(omsCartItems),60*60*72,true);
      }
    }
    modelMap.put("cartList",omsCartItems);
    return "cartListinner";
  }

  @RequestMapping(value = "/addToCart")
  @LoginRequire(mustLoginSuccess = false) //不一定要登录成功
  public String addToCart(Integer skuId, Integer quantity, HttpServletRequest request, HttpServletResponse response){

    List<OmsCartItem> omsCartItems = new ArrayList<>();

    //查询skuInfo
    PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);

    //封装成购物车
    OmsCartItem omsCartItem = new OmsCartItem();
    omsCartItem.setCreateDate(new Date());
    omsCartItem.setDeleteStatus(0);
    omsCartItem.setModifyDate(new Date());
    omsCartItem.setPrice(pmsSkuInfo.getPrice());
    omsCartItem.setProductAttr("");
    omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
    omsCartItem.setProductBrand("");
    omsCartItem.setProductId(pmsSkuInfo.getProductId());
    omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
    omsCartItem.setProductSkuId(pmsSkuInfo.getId());
    omsCartItem.setProductName(pmsSkuInfo.getSkuName());
    omsCartItem.setProductSn("");
    omsCartItem.setProductSkuCode("");
    omsCartItem.setQuantity(new BigDecimal(quantity));
    omsCartItem.setIsChecked(1);
    //判断用户是否登录
    Integer memberId = (Integer)request.getAttribute("memberId");
    String username = (String)request.getAttribute("username");
    //根据用户登录情况是写cookie还是db
    if(memberId==null){
      String cookieValue = CookieUtil.getCookieValue(request,"cartListCookie",true);
      if(StringUtils.isNotBlank(cookieValue)){
        omsCartItems = JSON.parseArray(cookieValue,OmsCartItem.class);
        //判断添加的数据是否存在，如果存在，需要更新，修改数量
        Integer index = ifExist(omsCartItems,omsCartItem);
        if(index!=-1){
           OmsCartItem omsCartItem1 = omsCartItems.get(index);
           omsCartItem1.setQuantity(omsCartItem1.getQuantity().add(omsCartItem.getQuantity()));
           omsCartItems.set(index,omsCartItem1);
        }
        else{
          omsCartItems.add(omsCartItem);
        }
      }
      else{
        omsCartItems.add(omsCartItem);
      }

      CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(omsCartItems),60*60*72,true);

    }else{ //用户已经登录

      OmsCartItem omsCartItemFromDb = cartService.ifCartExistByMemberId(memberId,skuId);
      if(omsCartItemFromDb==null){//未添加过
         omsCartItem.setMemberId(memberId);
         cartService.addCart(omsCartItem);
      }else{//已添加过
          omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
          cartService.updateCart(omsCartItemFromDb);
      }

      //同步缓存
      cartService.flushCartDb(memberId);
    }

    return "redirect:/success.html";

  }

  private Integer ifExist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
    for(OmsCartItem omsCartItem1:omsCartItems){
      if(omsCartItem1.getProductId().equals(omsCartItem.getProductId())){
        return omsCartItems.indexOf(omsCartItem1);
      }
    }
    return -1;
  }

}
