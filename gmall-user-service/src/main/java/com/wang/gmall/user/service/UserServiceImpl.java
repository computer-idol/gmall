package com.wang.gmall.user.service;

import com.alibaba.fastjson.JSON;
import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.bean.UmsMemberReceiveAddress;
import com.wang.gmall.service.UserService;
import com.wang.gmall.user.mapper.UserAddressMapper;
import com.wang.gmall.user.mapper.UserMapper;
import com.wang.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.dubbo.config.annotation.Service;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private UserAddressMapper userAddressMapper;

  @Autowired
  RedisUtil redisUtil;

  @Override
  public List<UmsMember> getUsers() {
    return userMapper.selectAll();
  }

  @Override
  public UmsMember getUser(int userId) {
    UmsMember umsMember = new UmsMember();
    umsMember.setId(userId);
    return userMapper.selectByPrimaryKey(umsMember);
  }

  @Override
  public List<UmsMemberReceiveAddress> getUserAddressList(int memberId) {
    Example example = new Example(UmsMemberReceiveAddress.class);
    example.createCriteria().andEqualTo("memberId",memberId);
    List<UmsMemberReceiveAddress> addressList = userAddressMapper.selectByExample(example);
    addressList.forEach(address->{
      address.setAddress(address.getProvince()+address.getCity()+address.getRegion()+address.getDetailAddress());
    });
    return addressList;
  }

  @Override
  public UmsMember login(UmsMember umsMember) {
     Jedis jedis = null;
     try {
       jedis = redisUtil.getJedis();
       if (jedis != null) {
         String userInfo = jedis.get("user:" + umsMember.getUsername() + umsMember.getPassword() + ":password");
         if (StringUtils.isNotBlank(userInfo)) {
           return JSON.parseObject(userInfo, UmsMember.class);
         } else {
           //缓存中没有去查数据库
           UmsMember umsMemberFromDb = loginFromDb(umsMember);
           if(umsMemberFromDb!=null){
              jedis.setex("user:" + umsMember.getUsername() + umsMember.getPassword() + ":password",60*60*24,JSON.toJSONString(umsMemberFromDb));
           }
           return umsMemberFromDb;
         }
       } else {//redis失效
          return loginFromDb(umsMember);
       }
     }catch (Exception e){
       System.out.println(e.getMessage());
     } finally {
       if(jedis!=null) {
         jedis.close();
       }
     }
    return null;
  }

  @Override
  public void addToken(String token, Integer id) {
    Jedis jedis = null;
    try{
      jedis = redisUtil.getJedis();
      if(jedis!=null){
        jedis.setex("user:"+id+":token",60*60*2,token);
      }
    }finally {
      if (jedis != null) {
        jedis.close();
      }
    }
  }

  @Override
  public UmsMember addOauth(UmsMember umsMember) {
     //先判断有无存在
     UmsMember umsMember1 = new UmsMember();
     umsMember1.setSourceUid(umsMember.getSourceUid());
     umsMember1 = userMapper.selectOne(umsMember1);
     if(umsMember1==null){
       userMapper.insertSelective(umsMember);
       return umsMember;
     }else{
       return umsMember1;
     }
  }

  private UmsMember loginFromDb(UmsMember umsMember) {
     return userMapper.selectOne(umsMember);
  }
}
