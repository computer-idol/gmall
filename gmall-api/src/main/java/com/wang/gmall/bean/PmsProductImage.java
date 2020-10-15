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
public class PmsProductImage implements Serializable {

    @Column
    @Id
    private Integer id;
    @Column
    private Integer productId;
    @Column
    private String imgName;
    @Column
    private String imgUrl;
}
