package com.wang.gmall.user.service.impl;

import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.bean.UmsMemberReceiveAddress;
import com.wang.gmall.user.mapper.UserAddressMapper;
import com.wang.gmall.user.mapper.UserMapper;
import com.wang.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private UserAddressMapper userAddressMapper;

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
     return userAddressMapper.selectByExample(example);
  }

  @Override
  public UmsMember login(UmsMember umsMember) {
    return null;
  }

  @Override
  public void addToken(String token, Integer id) {
  }

  @Override
  public UmsMember addOauth(UmsMember umsMember) {
    return null;
  }
}
