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
public enum EstadoFactura {
    PENDIENTE_PAGO(1),
    PAGADA_PENDIENTE_ENVIO_HACIENDA(2),
    APROBADA_HACIENDA(3),
    RECHAZADA_HACIENDA(4),
    ERROR_HACIENDA(5),
    ENVIADA_HACIENDA(6),
    PROCESANDO_HACIENDA(7),
    ANULADA(8),
    ERROR_SERVICIO(16),
    PEDIDO(18),
    EN_DESARROLLO(17),
    VENCIDA(27);

    private final Integer estadoFactura;

    private EstadoFactura(Integer estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public Integer getEstadoFactura() {
        return estadoFactura;
    }

}
