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
public class PmsBaseSaleAttr implements Serializable {

    @Id
    @Column
    Integer id ;

    @Column
    String name;

}
