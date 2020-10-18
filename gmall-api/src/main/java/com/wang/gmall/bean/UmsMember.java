package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
public class UmsMember implements Serializable {

    @Id
    private Integer id;
    private Integer         memberLevelId;
    private String username;
    private String         password;
    private String nickname;
    private String         phone;
    private int status;
    private Date createTime;
    private String icon;
    private Integer         gender;
    private Date birthday;
    private String        city;
    private String job;
    private String         personalizedSignature;
    private Integer sourceType;
    private String sourceUid;//微博的id
    private Integer         integration;
    private Integer growth;
    private Integer         luckeyCount;
    private Integer historyIntegration;
    private String accessToken;

}
