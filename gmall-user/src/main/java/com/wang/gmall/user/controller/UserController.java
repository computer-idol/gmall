package com.wang.gmall.user.controller;

import com.wang.gmall.bean.UmsMember;
import com.wang.gmall.bean.UmsMemberReceiveAddress;
import com.wang.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping(value = "/{id}",produces = "application/json;charset=UTF-8")
  public UmsMember getUser(@PathVariable("id") int id){
    return userService.getUser(id);
  }

  @GetMapping(value = "/list",produces = "application/json;charset=UTF-8")
  public List<UmsMember> getUserList(){
    return userService.getUsers();
  }

  @GetMapping(value="/addresses/{id}",produces = "application/json;charset=UTF-8")
  public List<UmsMemberReceiveAddress> getList(@PathVariable("id") int id){
    return userService.getUserAddressList(id);
  }
}
