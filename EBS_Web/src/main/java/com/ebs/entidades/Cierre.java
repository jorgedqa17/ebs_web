/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"id_cierre"}, callSuper = false)
@Entity
@Table(name = "cierre", schema = "searmedica")
@SequenceGenerator(name = "seq_cierre", sequenceName = "searmedica.seq_cierre", allocationSize = 1)
public class Cierre implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cierre")
    @Column(name = "id_cierre")
    private Long id_cierre;

    @Column(name = "login")
    private String login;

    @Column(name = "fecha_inicio")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha_inicio;

    @Column(name = "fecha_fin")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha_fin;

    @Column(name = "id_tipo_cierre")
    private Long id_tipo_cierre;

    @Column(name = "fecha_generacion_cierre")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fecha_generacion_cierre;

    @Column(name = "monto_entradas")
    private BigDecimal monto_entradas;

    @Column(name = "monto_saludas")
    private BigDecimal monto_saludas;

    @Column(name = "monto_anulaciones")
    private BigDecimal monto_anulaciones;

    @Column(name = "monto_total")
    private BigDecimal monto_total;

    @Column(name = "monto_facturas_credito")
    private BigDecimal monto_facturas_credito;

    @Column(name = "monto_facturas_tarjeta")
    private BigDecimal monto_facturas_tarjeta;

    @Column(name = "monto_efectivo")
    private BigDecimal monto_efectivo;

    @Column(name = "monto_cheque")
    private BigDecimal monto_cheque;

    @Column(name = "monto_transferencia")
    private BigDecimal monto_transferencia;

    @Column(name = "monto_recaudado_terceros")
    private BigDecimal monto_recaudado_terceros;

    @Column(name = "fecha_generacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fecha_generacion;

}
