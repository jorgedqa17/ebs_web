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
public enum TipoCondicionVenta {
    CONTADO(Long.parseLong("1"), "01"),
    CREDITO(Long.parseLong("2"), "02"),
    CONSIGNACION(Long.parseLong("3"), "03"),
    APARTADO(Long.parseLong("4"), "04"),
    ARRENDAMIENTO_CON_OPCION_DE_COMPRA(Long.parseLong("5"), "05"),
    ARRENDAMIENTO_EN_FUNCION_FINANCIERA(Long.parseLong("6"), "06"),
    OTROS(Long.parseLong("7"), "07"),;
    private final Long tipoCondicionVenta;
    private final String codigoHacienda;

    private TipoCondicionVenta(Long tipoCondicionVenta, String codigoHacienda) {
        this.tipoCondicionVenta = tipoCondicionVenta;
        this.codigoHacienda = codigoHacienda;
    }

    public Long getTipoCondicionVenta() {
        return tipoCondicionVenta;
    }

    public String getCodigoHacienda() {
        return codigoHacienda;
    }

}
