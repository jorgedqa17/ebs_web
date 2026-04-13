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
public enum EstadosLineasFactura {
    NORMAL(30L),
    PENDIENTE_ENVIO(31L),
    ENVIADA(32L),
    FINALIZADA(33L);

    private final Long estadoLineaFactura;

    private EstadosLineasFactura(Long estadoLineaFactura) {
        this.estadoLineaFactura = estadoLineaFactura;
    }

    public Long getEstadoLineaFactura() {
        return estadoLineaFactura;
    }

}
