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
    FACTURA_ENDOSADA("08"),
    NOTA_DE_CREDITO_FINANCIERA("09"),
    NOTA_DE_DEBITO_FINANCIERA("10"),
    PROVEEDOR_NO_DOMICILIADO("11"),
    NOTA_DE_CREDITO_FINANCIERA_POR_EXONERACION_POSTERIOR("12"),
    ANULA_DOCUMENTO_DE_REFERENCIA_POR_ERROR_MATERIAL("13"),
    CORRIGE_MONTO_POR_ERROR_MATERIAL("14"),
    SUSTITUYE_COMPROBANTE_ELECTRONICO_POR_ERROR_MATERIAL("15"),
    SUSTITUYE_COMPROBANTE_ELECTRONICO_RECHAZADO("16"),
    //Uso exclusivo del Recibo Electrónico de Pago
    PAGO_A_COMPROBANTE_ELECTRONICO("17"),
    OTROS("99");

    private final String codigo;

    private TipoDocumentoReferenciaEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

}
