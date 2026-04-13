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
public enum Roles {

    GENERACION_CIERRE(Long.parseLong("5")),
    FACTURADOR_CREDITO(Long.parseLong("7")),
    PUEDE_CREAR_RECIBOS_AUTOMATICOS(Long.parseLong("14"));
    private final Long idRol;

    private Roles(Long idRol) {
        this.idRol = idRol;
    }

    public Long getIdRol() {
        return idRol;
    }

}
