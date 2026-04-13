/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import javax.persistence.Transient;
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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"id_pago"}, callSuper = false)
@Entity
@Table(name = "pago", schema = "searmedica")
@SequenceGenerator(name = "seq_pago", sequenceName = "searmedica.seq_pago", allocationSize = 1)
public class Pago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pago")
    @Column(name = "id_pago")
    private Long id_pago;

    @Column(name = "id_pago_referencia")
    private Long id_pago_referencia;

    @Column(name = "numero_consecutivo")
    private String numero_consecutivo;

    @Column(name = "identificacion_proveedor")
    private String identificacion_proveedor;

    @Column(name = "clave_comprobante_pago")
    private String clave_comprobante_pago;

    @Column(name = "fecha_emision")
    private String fecha_emision;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "comprobante_enviado")
    private String comprobante_enviado;

    @Column(name = "correo_electronico")
    private String correo_electronico;

    @Column(name = "id_estado")
    private Integer id_estado;

    @Column(name = "fecha_pago")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_pago;

    @Column(name = "respuesta_hacienda")
    private String respuesta_hacienda;

    @Column(name = "monto_total_comprobante")
    private BigDecimal monto_total_comprobante;

    @Column(name = "monto_impuestos")
    private BigDecimal monto_impuestos;

    @Column(name = "tipo_mensaje_respuesta")
    private String tipo_mensaje_respuesta;

    @Column(name = "documento_xml_firmado")
    private String documento_xml_firmado;

    @Column(name = "detalle_respuesta_hacienda")
    private String detalle_respuesta_hacienda;

    @Column(name = "tipo_identificacion_proveedor")
    private String tipo_identificacion_proveedor;

    @Column(name = "mensaje_detalle")
    private String mensaje_detalle;

    @Column(name = "url_consulta_estado")
    private String url_consulta_estado;

    @Column(name = "codigo_actividad")
    private String codigo_actividad;

    @Column(name = "monto_total_impuesto_acreditar")
    private BigDecimal monto_total_impuesto_acreditar;

    @Column(name = "monto_total_gasto_aplicable")
    private BigDecimal monto_total_gasto_aplicable;

    @Column(name = "id_condicion_impuesto")
    private Long id_condicion_impuesto;

    @Column(name = "nombre_empresa")
    private String nombre_empresa;

    @Transient
    private String descripcionEstadoPago;

    @Transient
    private String descripcionRespuesta;

    @Transient
    private String xmlAceptado;

    @Transient
    private List<PagoDetalle> listaPagoDetalle;

    @Transient
    private List<PagoTotal> listaTotales;

}
