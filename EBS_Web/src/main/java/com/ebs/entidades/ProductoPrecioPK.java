/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductoPrecioPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_producto")
    private Long id_producto;

    @Column(name = "id_tipo")
    private Long id_tipo;

}
