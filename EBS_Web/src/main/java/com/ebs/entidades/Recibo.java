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
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
@EqualsAndHashCode(of = {"id_recibo"}, callSuper = false)
@Entity
@Table(name = "recibo", schema = "searmedica")
@SequenceGenerator(name = "seq_recibo", sequenceName = "searmedica.seq_recibo", allocationSize = 1)
public class Recibo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_recibo")
    @Column(name = "id_recibo")
    private Long id_recibo;

    @Column(name = "id_cliente")
    private Long id_cliente;

    @Column(name = "id_factura")
    private Long id_factura;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(name = "monto_pago")
    private BigDecimal monto_pago;

    @Column(name = "monto_total_factura")
    private BigDecimal monto_total_factura;

    @Column(name = "monto_restante")
    private BigDecimal monto_restante;

    @Column(name = "login")
    private String login;

    @Column(name = "concepto_recibo")
    private String concepto_recibo;

    @Column(name = "id_estado")
    private Long id_estado;

    @Column(name = "id_medio_pago")
    private Long id_medio_pago;

    @Column(name = "numero_referencia")
    private String numero_referencia;

    @Column(name = "envio_correo_electronico")
    private Integer envio_correo_electronico;

    @Column(name = "es_recibo_manual")
    private Integer es_recibo_manual;

    @Column(name = "numero_recibo_manual")
    private String numero_recibo_manual;

    @Column(name = "ind_tomar_cierre")
    private Integer ind_tomar_cierre;

    @Transient
    private String estado;
    @Transient
    private boolean esUnReciboManual;
}
