/*
 * To change this license header; choose License Headers in Project Properties.
 * To change this template file; choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

/**
 *
 * @author Jorge GBSYS
 */
public class FacturaElectronica {

    private String Clave;

    private String CodigoActividad;

    private String numeroConsecutivo;

    private String fechaEmision;

    private Emisor personaEmisor;

    private Receptor personaReceptor;

    private String condicionVenta;

    private String PlazoCredito;

    private String medioPago;

    private DetalleServicio detalleServicio;

    private ResumenFactura resumen;

    private InformacionReferenciaFactura referencia;

    private Normativa normativa;

    private String tipoDocumento;

    private Otros otros;

    private MensajeReceptor mensajeReceptor;

    public Emisor getPersonaEmisor() {
        return personaEmisor;
    }

    public void setPersonaEmisor(Emisor personaEmisor) {
        this.personaEmisor = personaEmisor;
    }

    public Receptor getPersonaReceptor() {
        return personaReceptor;
    }

    public void setPersonaReceptor(Receptor personaReceptor) {
        this.personaReceptor = personaReceptor;
    }

    public String getCondicionVenta() {
        return condicionVenta;
    }

    public void setCondicionVenta(String condicionVenta) {
        this.condicionVenta = condicionVenta;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public DetalleServicio getDetalleServicio() {
        return detalleServicio;
    }

    public void setDetalleServicio(DetalleServicio detalleServicio) {
        this.detalleServicio = detalleServicio;
    }

    public ResumenFactura getResumen() {
        return resumen;
    }

    public void setResumen(ResumenFactura resumen) {
        this.resumen = resumen;
    }

    public Normativa getNormativa() {
        return normativa;
    }

    public void setNormativa(Normativa normativa) {
        this.normativa = normativa;
    }

    public String getClave() {
        return Clave;
    }

    public void setClave(String Clave) {
        this.Clave = Clave;
    }

    public String getNumeroConsecutivo() {
        return numeroConsecutivo;
    }

    public void setNumeroConsecutivo(String numeroConsecutivo) {
        this.numeroConsecutivo = numeroConsecutivo;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Otros getOtros() {
        return otros;
    }

    public void setOtros(Otros otros) {
        this.otros = otros;
    }

    public InformacionReferenciaFactura getReferencia() {
        return referencia;
    }

    public void setReferencia(InformacionReferenciaFactura referencia) {
        this.referencia = referencia;
    }

    public String getPlazoCredito() {
        return PlazoCredito;
    }

    public void setPlazoCredito(String PlazoCredito) {
        this.PlazoCredito = PlazoCredito;
    }

    public MensajeReceptor getMensajeReceptor() {
        return mensajeReceptor;
    }

    public void setMensajeReceptor(MensajeReceptor mensajeReceptor) {
        this.mensajeReceptor = mensajeReceptor;
    }

    public String getCodigoActividad() {
        return CodigoActividad;
    }

    public void setCodigoActividad(String CodigoActividad) {
        this.CodigoActividad = CodigoActividad;
    }

}
