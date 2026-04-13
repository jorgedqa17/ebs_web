/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;

/**
 *
 * @author jdquesad
 */
public class Exoneracion {

    private String TipoDocumento;
    private String NumeroDocumento;
    private String NombreInstitucion;
    private String FechaEmision;
    private BigDecimal MontoImpuesto;
    private Integer PorcentajeCompra;

    public String getTipoDocumento() {
        return TipoDocumento;
    }

    public void setTipoDocumento(String TipoDocumento) {
        this.TipoDocumento = TipoDocumento;
    }

    public String getNumeroDocumento() {
        return NumeroDocumento;
    }

    public void setNumeroDocumento(String NumeroDocumento) {
        this.NumeroDocumento = NumeroDocumento;
    }

    public String getNombreInstitucion() {
        return NombreInstitucion;
    }

    public void setNombreInstitucion(String NombreInstitucion) {
        this.NombreInstitucion = NombreInstitucion;
    }

    public String getFechaEmision() {
        return FechaEmision;
    }

    public void setFechaEmision(String FechaEmision) {
        this.FechaEmision = FechaEmision;
    }

    public BigDecimal getMontoImpuesto() {
        return MontoImpuesto;
    }

    public void setMontoImpuesto(BigDecimal MontoImpuesto) {
        this.MontoImpuesto = MontoImpuesto;
    }

    public Integer getPorcentajeCompra() {
        return PorcentajeCompra;
    }

    public void setPorcentajeCompra(Integer PorcentajeCompra) {
        this.PorcentajeCompra = PorcentajeCompra;
    }

}
