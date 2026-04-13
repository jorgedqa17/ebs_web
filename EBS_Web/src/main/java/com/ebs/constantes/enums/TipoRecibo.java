/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 *
 * @author Jorge Quesada Arias
 */
public enum TipoRecibo {
    MANUAL(1),
    AUTOMATICO(0);

    private final Integer tipoRecibo;

    private TipoRecibo(Integer tipoRecibo) {
        this.tipoRecibo = tipoRecibo;
    }

    public Integer getTipoRecibo() {
        return tipoRecibo;
    }

}
