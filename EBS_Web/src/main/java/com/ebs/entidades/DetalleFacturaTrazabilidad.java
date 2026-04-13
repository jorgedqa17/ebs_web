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

/**
 *
 * @author Jorge GBSYS
 */
@Data
@Entity
@Table(name = "detalle_factura_trazabilidad", schema = "searmedica")
public class DetalleFacturaTrazabilidad implements Serializable {

    @Id
    @Column(name = "id_producto")
    private Long id_producto;
    @Id
    @Column(name = "id_factura_trazabilidad")
    private Long id_factura_trazabilidad;
    @Id
    @Column(name = "numero_linea")
    private Integer numero_linea;

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

    /**
     *
     * @param facturaTrazabilidad
     * @param detalle
     * @return DetalleFacturaTrazabilidad
     */
    public static DetalleFacturaTrazabilidad convertirDetalleFacturaADetalleTrazabilidad(FacturaTrazabilidad facturaTrazabilidad, DetalleFactura detalle) {
        DetalleFacturaTrazabilidad resultado = new DetalleFacturaTrazabilidad();
        resultado.setId_producto(detalle.getDetallePK().getId_producto());
        resultado.setId_factura_trazabilidad(facturaTrazabilidad.getId_factura_trazabilidad());
        resultado.setNumero_linea(detalle.getDetallePK().getNumero_linea());
        resultado.setCantidad(detalle.getCantidad());
        resultado.setPrecio_neto(detalle.getPrecio_neto());
        resultado.setId_moneda(detalle.getId_moneda());
        resultado.setId_tipo_precio(detalle.getId_tipo_precio());
        resultado.setTipo_cambio(detalle.getTipo_cambio());
        resultado.setDescuento(detalle.getDescuento());
        resultado.setTotal_descuento(detalle.getTotal_descuento());
        resultado.setTotal_impuestos(detalle.getTotal_impuestos());
        resultado.setSub_total(detalle.getSub_total());
        resultado.setTotal_exonerado(detalle.getTotal_exonerado());
        resultado.setTotal_impuesto_neto(detalle.getTotal_impuesto_neto());
        resultado.setDescripcion(detalle.getDescripcion());
        resultado.setTotal_linea(detalle.getTotal_linea());
        resultado.setEs_para_nota_debito(detalle.getEs_para_nota_debito());
        resultado.setEs_para_nota_credito(detalle.getEs_para_nota_credito());
        resultado.setId_estado(detalle.getId_estado());
        resultado.setMonto_total(detalle.getMonto_total());
        resultado.setId_tipo_tarifa(detalle.getId_tipo_tarifa());
        resultado.setEs_linea_exonerada(detalle.getEs_linea_exonerada());
        resultado.setId_exoneracion(detalle.getId_exoneracion());
        resultado.setNumero_documento(detalle.getNumero_documento());
        resultado.setNombre_institucion(detalle.getNombre_institucion());
        resultado.setFecha_emision(detalle.getFecha_emision());
        resultado.setPorcentaje_exonerado(detalle.getPorcentaje_exonerado());
        resultado.setId_impuesto(detalle.getId_impuesto());
        resultado.setEs_linea_exenta(detalle.getEs_linea_exenta());

        return resultado;

    }
}
