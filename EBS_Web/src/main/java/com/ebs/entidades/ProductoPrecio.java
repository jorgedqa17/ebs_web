/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"productoPrecioPK"}, callSuper = false)
@Entity
@Table(name = "producto_precios", schema = "searmedica")
public class ProductoPrecio implements Serializable {

    @EmbeddedId
    private ProductoPrecioPK productoPrecioPK;

    @Column(name = "precio")
    private BigDecimal precio;
    
    @Column(name = "id_moneda")
    private Long id_moneda;
}
