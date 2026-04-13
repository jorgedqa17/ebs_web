/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 * @author Jorge API Technologies
 */
public enum Cadenas {

    LINK("https://tribunet.hacienda.go.cr/docs/esquemas/2017/v4.2/facturaElectronica"),
    POLITICA("Politica de Factura Digital"),
    TIPO_CERTIFICADO("pkcs12"),
    /*Campos para clave numérica*/
    CODIGO_PAIS("506"),
    CASA_MATRIZ("001"),
    TERMINAL_CENTRAL("00001"),
    CERO("0");
    private final String cadena;

    private Cadenas(String cadena) {
        this.cadena = cadena;
    }

    public String getCadena() {
        return cadena;
    }

}
