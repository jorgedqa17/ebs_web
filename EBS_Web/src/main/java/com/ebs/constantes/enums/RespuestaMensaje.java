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
public enum RespuestaMensaje {

    ACEPTADO(Long.parseLong("1"), "Aceptado"),
    PARCIALMENTE_ACEPTADO(Long.parseLong("2"), "Aceptado Parcial"),
    RECHAZADO(Long.parseLong("3"), "Rechazado");

    private final Long codigoRespuesta;
    private final String nombre;

    private RespuestaMensaje(Long codigoRespuesta, String nombre) {
        this.codigoRespuesta = codigoRespuesta;
        this.nombre = nombre;
    }

    public Long getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public String getNombre() {
        return nombre;
    }

}
