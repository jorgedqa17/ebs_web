/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

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
 * @author jdquesad
 */
@Data
@EqualsAndHashCode(of = {"id_nota_debito"}, callSuper = false)
@Entity
@Table(name = "factura_nota_debito", schema = "searmedica")
@SequenceGenerator(name = "seq_factura_nota_debito", sequenceName = "searmedica.seq_factura_nota_debito", allocationSize = 1)
public class FacturaDebito {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_factura_nota_debito")
    @Column(name = "id_nota_debito")
    private Long id_nota_debito;

    @Column(name = "id_factura")
    private Long id_factura;

    @Column(name = "id_estado")
    private Long id_estado;

    @Column(name = "id_motivo_nota_debito")
    private Long id_motivo_nota_debito;

    @Column(name = "motivo_nota_debito")
    private String motivo_nota_debito;

    @Column(name = "numero_consecutivo")
    private String numero_consecutivo;

    @Column(name = "clave")
    private String clave;

    @Column(name = "fecha_emision")
    private String fecha_emision;

    @Column(name = "login")
    private String login;

    @Column(name = "id_bodega")
    private Long id_bodega;

    @Column(name = "ip")
    private String ip;

    @Column(name = "nombre_maquina")
    private String nombre_maquina;

    @Column(name = "envio_correo_electronico")
    private Integer envio_correo_electronico;

    @Column(name = "fecha_nota_debito")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_nota_debito;

    @Column(name = "id_tipo_doc_referencia")
    private Long id_tipo_doc_referencia;

    @Column(name = "id_anulacion")
    private Long id_anulacion;

    @Column(name = "total_descuento")
    private BigDecimal total_descuento;

    @Column(name = "total_impuesto")
    private BigDecimal total_impuesto;

    @Column(name = "total_venta_neta")
    private BigDecimal total_venta_neta;

    @Column(name = "total_servicios_grabados")
    private BigDecimal total_servicios_grabados;

    @Column(name = "total_servicios_exentos")
    private BigDecimal total_servicios_exentos;

    @Column(name = "total_mercancias_gravadas")
    private BigDecimal total_mercancias_gravadas;

    @Column(name = "total_mercancias_exentas")
    private BigDecimal total_mercancias_exentas;

    @Column(name = "total_gravado")
    private BigDecimal total_gravado;

    @Column(name = "total_exento")
    private BigDecimal total_exento;

    @Column(name = "total_nota_debito")
    private BigDecimal total_nota_debito;

    @Column(name = "total_venta")
    private BigDecimal total_venta;

    @Column(name = "total_mercancias_exonerados")
    private BigDecimal total_mercancias_exonerados;

    @Column(name = "total_exonerado")
    private BigDecimal total_exonerado;

    @Column(name = "total_servicios_exonerados")
    private BigDecimal total_servicios_exonerados;

    @Column(name = "nota_debito_interna")
    private Integer nota_debito_interna;

    @Transient
    private Estados estadoFactura;

}
