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
public enum TipoFacturaEnum {

    FACTURA(Long.parseLong("1")),
    PEDIDO(Long.parseLong("2")),
    COTIZACION(Long.parseLong("3")),
    TIQUETE_ELECTRONICO(Long.parseLong("4"));

    private final Long idTipoFactura;

    private TipoFacturaEnum(Long idTipoFactura) {
        this.idTipoFactura = idTipoFactura;
    }

    public Long getIdTipoFactura() {
        return idTipoFactura;
    }

}
