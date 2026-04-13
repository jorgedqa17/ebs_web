/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;

public class Impuesto {

    private String codigoImpuesto;
    private Integer porcentajeImpuesto;
    private BigDecimal total_impuestos;

   
    public String getCodigoImpuesto() {
        return codigoImpuesto;
    }

    public void setCodigoImpuesto(String codigoImpuesto) {
        this.codigoImpuesto = codigoImpuesto;
    }

    public Integer getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public void setPorcentajeImpuesto(Integer porcentajeImpuesto) {
        this.porcentajeImpuesto = porcentajeImpuesto;
    }


    public BigDecimal getTotal_impuestos() {
        return total_impuestos;
    }

    public void setTotal_impuestos(BigDecimal total_impuestos) {
        this.total_impuestos = total_impuestos;
    }

}
