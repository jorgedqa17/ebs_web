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
public enum TipoDocumento {
    FACTURA_ELECTRONICA("01"),
    NOTA_DE_DEBITO_ELECTRONICA("02"),
    NOTA_DE_CREDITO_ELECTRONICA("03"),
    TIQUETE_ELECTRONICO("04"),
    CONFIRMACION_DE_ACEPTACION_DEL_COMPROBANTE_ELECTRONICO("05"),
    CONFIRMACION_DE_ACEPTACION_PARCIAL_DEL_COMPROBANTE_ELECTRONICO("06"),
    CONFIRMACION_DE_RECHAZO_DEL_COMPROBANTE_ELECTRONICO("07"),
    RECIBO_ELECTRONICO_DE_PAGO("10"),;
    private final String tipoDocumento;

    private TipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

}
