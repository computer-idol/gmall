package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmsCartItem implements Serializable{

    @Id
    private Integer id;
    private Integer productId;
    private Integer productSkuId;
    private Integer memberId;
    private BigDecimal quantity;
    private BigDecimal price;
    private String sp1;
    private String sp2;
    private String sp3;
    private String productPic;
    private String productName;
    private String productSubTitle;
    private String productSkuCode;
    private String memberNickname;
    private Date createDate;
    private Date modifyDate;
    private Integer deleteStatus;
    private Integer productCategoryId;
    private String productBrand;
    private String productSn;
    private String productAttr;
    private Integer isChecked;

    @Transient
    private BigDecimal totalPrice;
}
