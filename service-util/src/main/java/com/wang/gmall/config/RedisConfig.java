package com.wang.gmall.config;

import com.wang.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lenovo
 * 配置类，将redis连接池置于spring容器中
 */
@Configuration
public class RedisConfig {

  //读取配置文件中的redis的ip地址
  @Value("${spring.redis.host:disabled}")
  private String host;

  @Value("${spring.redis.port:6379}")
  private int port;

  @Value("${spring.redis.database:0}")
  private int database;

  @Bean
  public RedisUtil getRedisUtil(){
    if("disabled".equals(host)){
      return null;
    }
    RedisUtil redisUtil=new RedisUtil();
    redisUtil.initPool(host,port,database);
    return redisUtil;
  }

}

