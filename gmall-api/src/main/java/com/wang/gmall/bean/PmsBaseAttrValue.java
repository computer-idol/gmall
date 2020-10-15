package com.wang.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @param
 * @return
 */
@Data
public class PmsBaseAttrValue implements Serializable {
    @Id
    @Column
    private Integer id;
    @Column
    private String valueName;
    @Column
    private Integer attrId;
    @Column
    private String isEnabled;

    @Transient
    private String urlParam;
}
