/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 *
 * @author jdquesad
 */
public enum CancelacionFactura {
    CANCELADA(1),
    PENDIENTE_PAGO(0);

    private final Integer facturaCancelada;

    private CancelacionFactura(Integer facturaCancelada) {
        this.facturaCancelada = facturaCancelada;
    }

    public Integer getFacturaCancelada() {
        return facturaCancelada;
    }

}
