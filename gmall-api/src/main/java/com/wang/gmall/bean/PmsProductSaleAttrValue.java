package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

@Data
public class PmsProductSaleAttrValue implements Serializable {
    @Id
    @Column
    Integer id ;

    @Column
    Integer productId;

    @Column
    Integer saleAttrId;

    @Column
    String saleAttrValueName;

    @Transient
    String isChecked;

}
