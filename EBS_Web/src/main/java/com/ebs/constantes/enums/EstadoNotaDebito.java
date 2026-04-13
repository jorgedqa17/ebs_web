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
public enum EstadoNotaDebito {

    PENDIENTE_ENVIO(38L),
    ENVIADA(39L),
    APROBADA_HACIENDA(40L),
    RECHAZADA_HACIENDA(41L),
    ERROR_HACIENDA(42L),
    PROCESANDO_HACIENDA(43L),
    ANULADA(45L),
    NOTA_DEBITO_INTERNA(48L);

    private final Long estadoNotaCredito;

    private EstadoNotaDebito(Long estadoNotaCredito) {
        this.estadoNotaCredito = estadoNotaCredito;
    }

    public Long getEstadoNotaCredito() {
        return estadoNotaCredito;
    }

}
