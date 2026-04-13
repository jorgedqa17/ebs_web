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
public enum TipoDocumentoReferenciaEnum {

    ANULA_DOCUMENTO_DE_REFERENCIA("01"),
    CORRIGE_TEXTO_DOCUMENTO_DE_REFERENCIA("02"),
    CORRIGE_MONTO("03"),
    REFERENCIA_A_OTRO_DOCUMENTO("04"),
    SUSTITUYE_COMPROBANTE_PROVISIONAL_POR_CONTINGENCIA("05"),
    OTROS("99");

    private final String codigo;

    private TipoDocumentoReferenciaEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

}
