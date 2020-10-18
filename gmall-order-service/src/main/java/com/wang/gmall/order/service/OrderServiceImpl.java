package com.wang.gmall.order.service;

import com.wang.gmall.bean.OmsOrder;
import com.wang.gmall.bean.OmsOrderItem;
import com.wang.gmall.order.mapper.OrderItemMapper;
import com.wang.gmall.order.mapper.OrderMapper;
import com.wang.gmall.service.CartService;
import com.wang.gmall.service.OrderService;
import com.wang.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderMapper orderMapper;

  @Autowired
  private OrderItemMapper orderItemMapper;

  @Autowired
  private CartService cartService;

  @Autowired
  RedisUtil redisUtil;

  @Override
  public String generateTradeCode(Integer memberId) {
    String key = "user:"+memberId+":tradeCode";
    String tradeCode = UUID.randomUUID().toString();
    Jedis jedis = null;
    try {
      jedis = redisUtil.getJedis();
      if(jedis!=null) {
        jedis.setex(key, 60 * 15, tradeCode);
      }
    }finally {
      if(jedis!=null) {
        jedis.close();
      }
    }

    return tradeCode;
  }

  @Override
  public String checkTradeCode(Integer memberId, String tradeCode) {

    String key = "user:"+memberId+":tradeCode";
    Jedis jedis = null;
    try {
      jedis = redisUtil.getJedis();
      if(jedis!=null){
        String tradeCodeFromCache = jedis.get(key);
        //使用lua脚本删除防止重复删除
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long eval = (Long) jedis.eval(script, Collections.singletonList(key), Collections.singletonList(tradeCode));
        /**
         * 之前的代码,会有并发问题，重复删除
         if(StringUtils.isNotBlank(tradeCodeFromCache)&&tradeCode.equals(tradeCodeFromCache)){
                jedis.del(key);
                return "success";
            }else{
                return "fail";
            }*/
        if(eval!=null&&eval!=0){
          return "success";
        }else{
          return "fail";
        }
      }
    }finally {
      if(jedis!=null) {
        jedis.close();
      }
    }
    return "fail";
  }

  @Override
  public void saveOrder(OmsOrder omsOrder) {
     orderMapper.insertSelective(omsOrder);
     List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
     for(OmsOrderItem omsOrderItem:omsOrderItems){
       omsOrderItem.setOrderId(omsOrder.getId());
       orderItemMapper.insertSelective(omsOrderItem);
       //删除购物车上的商品
       cartService.delCart(omsOrderItem.getProductSkuId(),omsOrder.getMemberId());
     }
  }

  @Override
  public OmsOrder getOrderByOutTradeNo(String outTradeNo) {
    return null;
  }

  @Override
  public void updateOrderStatus(String outTradeNo) {

  }

}
