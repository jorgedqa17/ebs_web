/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

public class ResumenFactura {

    private BigDecimal total_descuentos;

    private BigDecimal total_impuestos;

    private BigDecimal total_venta_neta;

    private BigDecimal total_venta;

    private BigDecimal total_servicios_grabados;

    private BigDecimal total_servicios_exentos;

    private BigDecimal total_mercancias_gravadas;

    private BigDecimal total_mercancias_exentas;

    private BigDecimal total_gravado;

    private BigDecimal total_exento;

    private BigDecimal total_comprobante;

    private BigDecimal total_servicios_exonerados;

    private BigDecimal total_mercancias_exonerados;

    private BigDecimal total_exonerado;

    public BigDecimal getTotal_descuentos() {
        return total_descuentos;
    }

    public void setTotal_descuentos(BigDecimal total_descuentos) {
        this.total_descuentos = total_descuentos;
    }

    public BigDecimal getTotal_impuestos() {
        return total_impuestos;
    }

    public void setTotal_impuestos(BigDecimal total_impuestos) {
        this.total_impuestos = total_impuestos;
    }

    public BigDecimal getTotal_venta_neta() {
        return total_venta_neta;
    }

    public void setTotal_venta_neta(BigDecimal total_venta_neta) {
        this.total_venta_neta = total_venta_neta;
    }

    public BigDecimal getTotal_venta() {
        return total_venta;
    }

    public void setTotal_venta(BigDecimal total_venta) {
        this.total_venta = total_venta;
    }

    public BigDecimal getTotal_servicios_grabados() {
        return total_servicios_grabados;
    }

    public void setTotal_servicios_grabados(BigDecimal total_servicios_grabados) {
        this.total_servicios_grabados = total_servicios_grabados;
    }

    public BigDecimal getTotal_servicios_exentos() {
        return total_servicios_exentos;
    }

    public void setTotal_servicios_exentos(BigDecimal total_servicios_exentos) {
        this.total_servicios_exentos = total_servicios_exentos;
    }

    public BigDecimal getTotal_mercancias_gravadas() {
        return total_mercancias_gravadas;
    }

    public void setTotal_mercancias_gravadas(BigDecimal total_mercancias_gravadas) {
        this.total_mercancias_gravadas = total_mercancias_gravadas;
    }

    public BigDecimal getTotal_mercancias_exentas() {
        return total_mercancias_exentas;
    }

    public void setTotal_mercancias_exentas(BigDecimal total_mercancias_exentas) {
        this.total_mercancias_exentas = total_mercancias_exentas;
    }

    public BigDecimal getTotal_gravado() {
        return total_gravado;
    }

    public void setTotal_gravado(BigDecimal total_gravado) {
        this.total_gravado = total_gravado;
    }

    public BigDecimal getTotal_exento() {
        return total_exento;
    }

    public void setTotal_exento(BigDecimal total_exento) {
        this.total_exento = total_exento;
    }

    public BigDecimal getTotal_comprobante() {
        return total_comprobante;
    }

    public void setTotal_comprobante(BigDecimal total_comprobante) {
        this.total_comprobante = total_comprobante;
    }

    public BigDecimal getTotal_servicios_exonerados() {
        return total_servicios_exonerados;
    }

    public void setTotal_servicios_exonerados(BigDecimal total_servicios_exonerados) {
        this.total_servicios_exonerados = total_servicios_exonerados;
    }

    public BigDecimal getTotal_mercancias_exonerados() {
        return total_mercancias_exonerados;
    }

    public void setTotal_mercancias_exonerados(BigDecimal total_mercancias_exonerados) {
        this.total_mercancias_exonerados = total_mercancias_exonerados;
    }

    public BigDecimal getTotal_exonerado() {
        return total_exonerado;
    }

    public void setTotal_exonerado(BigDecimal total_exonerado) {
        this.total_exonerado = total_exonerado;
    }

}
