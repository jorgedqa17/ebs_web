/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;

/**
 *
 * @author Jorge GBSYS
 */
public class MensajeReceptor {

    private String Clave;

    private String NumeroCedulaEmisor;

    private String tipoIdentificacionEmisor;

    private String FechaEmisionDoc;

    private String Mensaje;

    private String DetalleMensaje;

    private BigDecimal MontoTotalImpuesto;

    private BigDecimal TotalFactura;

    private String NumeroCedulaReceptor;

    private String tipoIdentificacionReceptor;

    private String NumeroConsecutivoReceptor;

    public String getClave() {
        return Clave;
    }

    public void setClave(String Clave) {
        this.Clave = Clave;
    }

    public String getNumeroCedulaEmisor() {
        return NumeroCedulaEmisor;
    }

    public void setNumeroCedulaEmisor(String NumeroCedulaEmisor) {
        this.NumeroCedulaEmisor = NumeroCedulaEmisor;
    }

    public String getFechaEmisionDoc() {
        return FechaEmisionDoc;
    }

    public void setFechaEmisionDoc(String FechaEmisionDoc) {
        this.FechaEmisionDoc = FechaEmisionDoc;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String Mensaje) {
        this.Mensaje = Mensaje;
    }

    public String getDetalleMensaje() {
        return DetalleMensaje;
    }

    public void setDetalleMensaje(String DetalleMensaje) {
        this.DetalleMensaje = DetalleMensaje;
    }

    public String getNumeroCedulaReceptor() {
        return NumeroCedulaReceptor;
    }

    public void setNumeroCedulaReceptor(String NumeroCedulaReceptor) {
        this.NumeroCedulaReceptor = NumeroCedulaReceptor;
    }

    public String getNumeroConsecutivoReceptor() {
        return NumeroConsecutivoReceptor;
    }

    public void setNumeroConsecutivoReceptor(String NumeroConsecutivoReceptor) {
        this.NumeroConsecutivoReceptor = NumeroConsecutivoReceptor;
    }

    public BigDecimal getMontoTotalImpuesto() {
        return MontoTotalImpuesto;
    }

    public void setMontoTotalImpuesto(BigDecimal MontoTotalImpuesto) {
        this.MontoTotalImpuesto = MontoTotalImpuesto;
    }

    public BigDecimal getTotalFactura() {
        return TotalFactura;
    }

    public void setTotalFactura(BigDecimal TotalFactura) {
        this.TotalFactura = TotalFactura;
    }

    public String getTipoIdentificacionEmisor() {
        return tipoIdentificacionEmisor;
    }

    public void setTipoIdentificacionEmisor(String tipoIdentificacionEmisor) {
        this.tipoIdentificacionEmisor = tipoIdentificacionEmisor;
    }

    public String getTipoIdentificacionReceptor() {
        return tipoIdentificacionReceptor;
    }

    public void setTipoIdentificacionReceptor(String tipoIdentificacionReceptor) {
        this.tipoIdentificacionReceptor = tipoIdentificacionReceptor;
    }

}
