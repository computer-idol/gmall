package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lenovo
 * 用于elasticsearch
 */
@Data
public class PmsSearchSkuInfo implements Serializable {

  @Id
  private Integer id;

  private String skuName;

  private String skuDesc;

  private Integer catalog3Id;

  private BigDecimal price;

  private String  skuDefaultImg;

  private double hotScore;

  private List<PmsSkuAttrValue> skuAttrValueList;

  private Integer productId;

}
