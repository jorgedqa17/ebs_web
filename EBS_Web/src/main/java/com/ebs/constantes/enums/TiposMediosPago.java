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
public enum TiposMediosPago {
    EFECTIVO(Long.parseLong("1")),
    TARJETA(Long.parseLong("2")),
    CHEQUE(Long.parseLong("3")),
    TRANSFERENCIA_DEPÓSITO_BANCARIO(Long.parseLong("4")),
    RECAUDADO_POR_TERCEROS(Long.parseLong("5")),
    OTROS(Long.parseLong("6"));

    private final Long idMedioPago;

    private TiposMediosPago(Long idMedioPago) {
        this.idMedioPago = idMedioPago;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

}
