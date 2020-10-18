package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

@Data
public class UmsMemberReceiveAddress implements Serializable {

    @Id
    private Integer id;
    private Integer memberId;
    private String  name;
    private String  phoneNumber;
    private Integer defaultStatus;
    private String postCode;
    private String province;
    private String city;
    private String region;
    private String detailAddress;

    @Transient
    private String address; //完整的地址

}
