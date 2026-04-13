/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"id_detalle"}, callSuper = false)
@Entity
@Table(name = "cierre_detalle", schema = "searmedica")
public class DetalleCierre implements Serializable {

    @Column(name = "id_cierre")
    private Long id_cierre;

    @Id
    @Column(name = "id_detalle")
    private Integer id_detalle;

    @Column(name = "motivo_salida")
    private String motivo_salida;

    @Column(name = "monto")
    private BigDecimal monto;

}
