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
@EqualsAndHashCode(of = {"detallePK"}, callSuper = false)
@Entity
@Table(name = "detalle_factura", schema = "searmedica")
public class DetalleFactura implements Serializable {

    @EmbeddedId
    protected DetalleFacturaPK detallePK;

    @Column(name = "cantidad")
    private Double cantidad;

    @Column(name = "precio_neto")
    private BigDecimal precio_neto;

    @Column(name = "id_moneda")
    private Long id_moneda;

    @Column(name = "id_tipo_precio")
    private Long id_tipo_precio;

    @Column(name = "tipo_cambio")
    private BigDecimal tipo_cambio;

    @Column(name = "descuento")
    private Integer descuento;

    @Column(name = "total_descuento")
    private BigDecimal total_descuento;

    @Column(name = "total_impuestos")
    private BigDecimal total_impuestos;

    @Column(name = "sub_total")
    private BigDecimal sub_total;

    @Column(name = "total_exonerado")
    private BigDecimal total_exonerado;

    @Column(name = "total_impuesto_neto")
    private BigDecimal total_impuesto_neto;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "total_linea")
    private BigDecimal total_linea;

    @Column(name = "es_para_nota_debito")
    private Integer es_para_nota_debito;

    @Column(name = "es_para_nota_credito")
    private Integer es_para_nota_credito;

    @Column(name = "id_estado")
    private Long id_estado;

    @Column(name = "monto_total")
    private BigDecimal monto_total;

    @Column(name = "id_tipo_tarifa")
    private Long id_tipo_tarifa;

    @Column(name = "es_linea_exonerada")
    private Integer es_linea_exonerada;

    @Column(name = "id_exoneracion")
    private Long id_exoneracion;

    @Column(name = "numero_documento")
    private String numero_documento;

    @Column(name = "nombre_institucion")
    private String nombre_institucion;

    @Column(name = "fecha_emision")
    private String fecha_emision;

    @Column(name = "porcentaje_exonerado")
    private Integer porcentaje_exonerado;

    @Column(name = "id_impuesto")
    private Long id_impuesto;

    @Column(name = "es_linea_exenta")
    private Integer es_linea_exenta;
}
