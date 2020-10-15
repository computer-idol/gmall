package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class PmsSkuAttrValue implements Serializable {

    @Id
    @Column
    Integer id;

    @Column
    Integer attrId;

    @Column
    Integer valueId;

    @Column
    Integer skuId;

}
