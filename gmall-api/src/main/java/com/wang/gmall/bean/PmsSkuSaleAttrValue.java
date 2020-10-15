package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @param
 * @return
 */

@Data
public class PmsSkuSaleAttrValue implements Serializable {

    @Id
    @Column
    Integer id;

    @Column
    Integer skuId;

    @Column
    Integer saleAttrId;

    @Column
    Integer saleAttrValueId;

    @Column
    String saleAttrName;

    @Column
    String saleAttrValueName;

}
