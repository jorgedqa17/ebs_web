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
public enum EstadoPago {

    PENDIENTE_DE_ENVIO(Long.parseLong("20")),
    ENVIADO(Long.parseLong("21")),
    PROCESANDO_HACIENDA(Long.parseLong("22")),
    APROBADO_POR_HACIENDA(Long.parseLong("23")),
    RECHAZADO_HACIENDA(Long.parseLong("24")),
    ERROR_HACIENDA(Long.parseLong("25")),
    ERROR_DE_SERVICIO(Long.parseLong("26")),;
    private final Long idEstado;

    private EstadoPago(Long idEstado) {
        this.idEstado = idEstado;
    }

    public Long getIdEstado() {
        return idEstado;
    }

}
