package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @param
 * @return
 */

@Data
public class PaymentInfo {

    @Column
    @Id
    private Integer  id;

    @Column
    private String outTradeNo;

    @Column
    private Integer orderId;

    @Column
    private String alipayTradeNo;

    @Column
    private BigDecimal totalAmount;

    @Column
    private String Subject;

    @Column
    private String paymentStatus;

    @Column
    private Date createTime;

    @Column
    private Date callbackTime;

    @Column
    private String callbackContent;

}
