/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author jdquesad
 */
@Data
@EqualsAndHashCode(of = {"id_producto"}, callSuper = false)
@Entity
@Table(name = "trazabilidad_producto", schema = "searmedica")
public class TrazabilidadProducto implements Serializable {

    @Id
    @Column(name = "id_trazabilidad")
    private Long id_trazabilidad;

    @Column(name = "id_producto")
    private Long id_producto;

    @Column(name = "cantidad")
    private Long cantidad;

    @Column(name = "gestion")
    private String gestion;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "numero_consecutivo")
    private String numero_consecutivo;

    @Column(name = "login")
    private String login;

    @Column(name = "id_bodega_origen")
    private Long id_bodega_origen;

    @Column(name = "id_bodega_destino")
    private Long id_bodega_destino;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
}
