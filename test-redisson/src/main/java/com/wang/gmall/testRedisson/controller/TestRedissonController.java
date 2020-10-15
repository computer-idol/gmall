package com.wang.gmall.testRedisson.controller;

import com.wang.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
public class TestRedissonController {

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private RedissonClient redissonClient;

  @RequestMapping(value="testRedisson",produces = "application/json;charset=UTF-8")
  public String testRedisson(){
    //使用分布式锁解决缓存击穿
    RLock lock = redissonClient.getLock("lock");
    Jedis jedis = redisUtil.getJedis();
    //加锁
    lock.lock();
    String s="";
    try {
      s = jedis.get("k");
      if (StringUtils.isBlank(s)) {
        s = "1";
      }
      System.out.println(s);
      jedis.set("k", (Integer.parseInt(s) + 1) + "");
      jedis.close();
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      lock.unlock();
    }
    return s;
  }

}
