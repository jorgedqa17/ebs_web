/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 *
 * @author Jorge GBSYS
 */
public enum EstadoRecibo {

    PAGADO(Long.parseLong("28")),
    ANULADO(Long.parseLong("29"));

    private final Long estadoRecibo;

    private EstadoRecibo(Long estadoRecibo) {
        this.estadoRecibo = estadoRecibo;
    }

    public Long getEstadoRecibo() {
        return estadoRecibo;
    }

}
