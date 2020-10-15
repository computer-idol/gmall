package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
public class PmsProductSaleAttr implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id ;

    @Column
    Integer productId;

    @Column
    Integer saleAttrId;

    @Column
    String saleAttrName;


    @Transient
    List<PmsProductSaleAttrValue> spuSaleAttrValueList;

}
