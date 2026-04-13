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
public enum EstadoAnulacion {

    PENDIENTE_DE_ENVIO_HACIENDA(9L),
    ANULADA(19L),
    ENVIADA_HACIENDA(10L),
    APROBADA_HACIENDA(11L),
    RECHAZADA_HACIENDA(12L),
    ERROR_HACIENDA(13L),
    PROCESANDO_HACIENDA(14L),
    NOTA_CREDITO_INTERNA(47L);

    private final Long estadoAnulacion;

    private EstadoAnulacion(Long estadoAnulacion) {
        this.estadoAnulacion = estadoAnulacion;
    }

    public Long getEstadoAnulacion() {
        return estadoAnulacion;
    }

}
