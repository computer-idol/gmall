package com.wang.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PmsSearchSkuParam implements Serializable {

  private Integer catalog3Id;

  private String keyword;

  //private List<PmsSkuAttrValue> pmsSkuAttrValues;

  private Integer[] valueId;
}
