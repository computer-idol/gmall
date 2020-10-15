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
public class PmsSkuImage implements Serializable {

    @Id
    @Column
    Integer id;
    @Column
    Integer skuId;
    @Column
    String imgName;
    @Column
    String imgUrl;
    @Column
    String productImgId;
    @Column
    String isDefault;

}
