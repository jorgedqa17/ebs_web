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
public enum Indicadores {
    EXENTO_SI(1),
    EXENTO_NO(0),
    EXONERADO_SI(1),
    EXONERADO_NO(0),
    ES_FACTURA_CON_RECEPTOR(0),
    ES_FACTURA_SIN_RECEPTOR(1),
    ES_NOTA_CREDITO_INTERNA(1),
    NO_ES_NOTA_CREDITO_INTERNA(1),
    OPCION_PARA_REEMPLAZAR_FACTURA_RECHAZADA(4),
    HACIENDA_PROD(1),
    HACIENDA_STAG(0);

    private final Integer indicador;

    private Indicadores(Integer indicador) {
        this.indicador = indicador;
    }

    public Integer getIndicador() {
        return indicador;
    }

}
