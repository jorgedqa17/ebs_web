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
public enum LineaDetalleEstado {
    PARA_NOTA_CREDITO(1),
    PARA_NOTA_DEBITO(1),
    PARA_NOTA_CREDITO_QUITAR(0),
    PARA_NOTA_DEBITO_QUITAR(0);

    private final Integer estadoLineaDetalle;

    private LineaDetalleEstado(Integer estadoLineaDetalle) {
        this.estadoLineaDetalle = estadoLineaDetalle;
    }

    public Integer getEstadoLineaDetalle() {
        return estadoLineaDetalle;
    }

}
