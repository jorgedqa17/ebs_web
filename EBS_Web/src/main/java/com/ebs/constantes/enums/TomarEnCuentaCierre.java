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
public enum TomarEnCuentaCierre {

    TOMAR_EN_CUENTA(1),
    NO_TOMAR_EN_CUENTA(0);
    private final Integer indicador;

    private TomarEnCuentaCierre(Integer indicador) {
        this.indicador = indicador;
    }

    public Integer getIndicador() {
        return indicador;
    }

}
