package com.wang.gmall.service;

import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @author Lenovo
 */
public interface UserService {

  List<UmsMember> getUsers();

  UmsMember getUser(int userId);

  List<UmsMemberReceiveAddress> getUserAddressList(int memberId);


  UmsMember login(UmsMember umsMember);

  void addToken(String token, Integer id);

  UmsMember addOauth(UmsMember umsMember);
}
