package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class PmsBaseCatalog3 implements Serializable {

    @Id
    private Integer id;
    private String name;
    private Integer catalog2Id;

}