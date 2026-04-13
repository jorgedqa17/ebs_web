/*
 * To change this license header; choose License Headers in Project Properties.
 * To change this template file; choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;

public class LineaDetalle {

    private Integer NumeroLinea;

    private Integer Cantidad;

    private String UnidadMedida;

    private String Detalle;

    private BigDecimal PrecioUnitario;

    private BigDecimal MontoTotal;

    private Descuento descuento;

    private BigDecimal SubTotal;

    private Impuesto Impuesto;

    //private Exoneracion exoneracion;

    private BigDecimal impuestoNeto;

    // private String tipo_exoneracion;
    private BigDecimal MontoTotalLinea;

    private String Codigo;

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String Codigo) {
        this.Codigo = Codigo;
    }

    public Integer getNumeroLinea() {
        return NumeroLinea;
    }

    public void setNumeroLinea(Integer NumeroLinea) {
        this.NumeroLinea = NumeroLinea;
    }

    public Integer getCantidad() {
        return Cantidad;
    }

    public void setCantidad(Integer Cantidad) {
        this.Cantidad = Cantidad;
    }

    public String getUnidadMedida() {
        return UnidadMedida;
    }

    public void setUnidadMedida(String UnidadMedida) {
        this.UnidadMedida = UnidadMedida;
    }

    public String getDetalle() {
        return Detalle;
    }

    public void setDetalle(String Detalle) {
        this.Detalle = Detalle;
    }

    public BigDecimal getPrecioUnitario() {
        return PrecioUnitario;
    }

    public void setPrecioUnitario(BigDecimal PrecioUnitario) {
        this.PrecioUnitario = PrecioUnitario;
    }

    public BigDecimal getMontoTotal() {
        return MontoTotal;
    }

    public void setMontoTotal(BigDecimal MontoTotal) {
        this.MontoTotal = MontoTotal;
    }

    public Descuento getDescuento() {
        return descuento;
    }

    public void setDescuento(Descuento descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(BigDecimal SubTotal) {
        this.SubTotal = SubTotal;
    }

    public Impuesto getImpuesto() {
        return Impuesto;
    }

    public void setImpuesto(Impuesto Impuesto) {
        this.Impuesto = Impuesto;
    }

    public BigDecimal getMontoTotalLinea() {
        return MontoTotalLinea;
    }

    public void setMontoTotalLinea(BigDecimal MontoTotalLinea) {
        this.MontoTotalLinea = MontoTotalLinea;
    }

//    public Exoneracion getExoneracion() {
//        return exoneracion;
//    }
//
//    public void setExoneracion(Exoneracion exoneracion) {
//        this.exoneracion = exoneracion;
//    }

    public BigDecimal getImpuestoNeto() {
        return impuestoNeto;
    }

    public void setImpuestoNeto(BigDecimal impuestoNeto) {
        this.impuestoNeto = impuestoNeto;
    }

}
