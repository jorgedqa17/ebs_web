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

    //Nota 9, Anexos y Estructuras 4.4 (bitácora 22/04/2026)
    ANULA_DOCUMENTO_DE_REFERENCIA("01"),
    CORRIGE_MONTO("02"),
    REFERENCIA_A_OTRO_DOCUMENTO("04"),
    SUSTITUYE_COMPROBANTE_PROVISIONAL_POR_CONTINGENCIA("05"),
    DEVOLUCION_DE_MERCANCIA("06"),
    SUSTITUYE_COMPROBANTE_ELECTRONICO("07"),
    ANULA_DOCUMENTO_DE_REFERENCIA_POR_ERROR_MATERIAL("13"),
    CORRIGE_MONTO_POR_ERROR_MATERIAL("14"),
    SUSTITUYE_COMPROBANTE_ELECTRONICO_POR_ERROR_MATERIAL("15"),
    SUSTITUYE_COMPROBANTE_ELECTRONICO_RECHAZADO("16"),
    OTROS("99");

    private final String codigo;

    private TipoDocumentoReferenciaEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

}
