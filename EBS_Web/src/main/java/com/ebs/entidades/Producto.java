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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"id_producto"}, callSuper = false)
@Entity
@Table(name = "producto", schema = "searmedica")
@SequenceGenerator(name = "seq_producto", sequenceName = "searmedica.seq_producto", allocationSize = 1)
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_producto")
    @Column(name = "id_producto")
    private Long id_producto;

    @Column(name = "id_unidad_medida")
    private Long id_unidad_medida;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "ind_caduce")
    private Integer ind_caduce;

    @Column(name = "ind_exonerado")
    private Integer ind_exonerado;

    @Column(name = "ind_exento")
    private Integer ind_exento;

    @Column(name = "codigo_barras")
    private String codigo_barras;

    @Column(name = "id_tipo_producto")
    private Long id_tipo_producto;

    @Column(name = "id_impuesto")
    private Long id_impuesto;

    @Column(name = "activo")
    private Integer activo;

    @Column(name = "codigo_cabys")
    private String codigo_cabys;
    /*
    @Column(name = "precio_costo ")
    private BigDecimal precio_costo;
     */
    @Column(name = "cantidad_minima ")
    private Integer cantidad_minima;

    @Column(name = "id_tipo_tarifa_impuesto")
    private Long id_tipo_tarifa_impuesto;

    @Transient
    private Persona personaAsociada;

}
