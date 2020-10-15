package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @param
 * @return
 */
@Data
public class PmsBaseCatalog2 implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String name;
    @Column
    private Integer catalog1Id;

    @Transient
    private List<PmsBaseCatalog3> catalog3List;


}
