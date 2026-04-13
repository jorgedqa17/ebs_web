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
public enum SituacionComprobante {

    SITUACION_NORMAL("1"),
    SITUACION_CONTINGENCIA("2"),
    SITUACION_SIN_INTERNET("3");

    private final String situacion;

    private SituacionComprobante(String situacion) {
        this.situacion = situacion;
    }

    public String getSituacion() {
        return situacion;
    }

}
