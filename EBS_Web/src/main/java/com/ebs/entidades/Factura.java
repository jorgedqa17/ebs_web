/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"id_factura"}, callSuper = false)
@Entity
@Table(name = "factura", schema = "searmedica")
@SequenceGenerator(name = "seq_factura", sequenceName = "searmedica.seq_factura", allocationSize = 1)
public class Factura implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_factura")
    @Column(name = "id_factura")
    private Long id_factura;

    @Column(name = "id_cliente")
    private Long id_cliente;

    @Column(name = "id_cond_venta")
    private Long id_cond_venta;

    @Column(name = "id_medio_pago")
    private Long id_medio_pago;

    @Column(name = "id_anulacion")
    private Long id_anulacion;

    @Column(name = "fecha_factura")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_factura;

    @Column(name = "fecha_factura_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_factura_actualizacion;

    @Column(name = "total_descuentos")
    private BigDecimal total_descuentos;

    @Column(name = "total_impuestos")
    private BigDecimal total_impuestos;

    @Column(name = "total_venta_neta")
    private BigDecimal total_venta_neta;

    @Column(name = "total_venta")
    private BigDecimal total_venta;

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

    @Column(name = "total_comprobante")
    private BigDecimal total_comprobante;

    @Column(name = "clave")
    private String clave;

    @Column(name = "numero_consecutivo")
    private String numero_consecutivo;

    @Column(name = "agente")
    private String agente;

    @Column(name = "descuento_aplicado")
    private Integer descuento_aplicado;

    @Column(name = "estado_factura")
    private Integer estado_factura;

    @Column(name = "fecha_emision")
    private String fecha_emision;

    @Column(name = "login")
    private String login;

    @Column(name = "plazo_credito")
    private String plazo_credito;

    @Column(name = "correo_electronico")
    private String correo_electronico;

    @Column(name = "correo_electronico_cliente")
    private String correo_electronico_cliente;

    @Column(name = "envio_correo_electronico")
    private Integer envio_correo_electronico;

    @Column(name = "envio_respuesta_hacienda")
    private Integer envio_respuesta_hacienda;

    @Column(name = "nombre_cliente_fantasia")
    private String nombre_cliente_fantasia;

    @Column(name = "id_tipo_factura")
    private Long id_tipo_factura;

    @Column(name = "ip")
    private String ip;

    @Column(name = "nombre_estacion")
    private String nombre_estacion;

    @Column(name = "ip_actualizacion")
    private String ip_actualizacion;

    @Column(name = "nombre_estacion_actualizacion")
    private String nombre_estacion_actualizacion;

    @Column(name = "id_bodega")
    private Long id_bodega;

    @Column(name = "id_bodega_actualiza")
    private Long id_bodega_actualiza;

    @Column(name = "codigo_situacion_comprobante")
    private String codigo_situacion_comprobante;

    @Column(name = "codigo_referencia")
    private String codigo_referencia;

    @Column(name = "codigo_documento_referencia")
    private String codigo_documento_referencia;

    @Column(name = "fecha_documento_referencia")
    private String fecha_documento_referencia;

    @Column(name = "razon_documento_referencia")
    private String razon_documento_referencia;

    @Column(name = "numero_factura_documento_referencia")
    private String numero_factura_documento_referencia;

    @Column(name = "factura_cancelada")
    private Integer factura_cancelada;

    @Column(name = "fecha_cancelacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_cancelacion;

    @Column(name = "usuario_cancela")
    private String usuario_cancela;

    @Column(name = "cant_dias_vigencia")
    private Integer cant_dias_vigencia;

    @Column(name = "monto_restante")
    private BigDecimal monto_restante;

    @Column(name = "total_servicios_exonerados")
    private BigDecimal total_servicios_exonerados;

    @Column(name = "total_mercancias_exonerados")
    private BigDecimal total_mercancias_exonerados;

    @Column(name = "total_exonerado")
    private BigDecimal total_exonerado;

    @Column(name = "es_factura_sin_receptor")
    private Integer es_factura_sin_receptor;

    @Column(name = "id_factura_referencia")
    private Long id_factura_referencia;

    @Transient
    private String fechaVencimiento;

    @Transient
    private String descripcionEstado;
    @Transient
    private Estados estadoFactura;

}
