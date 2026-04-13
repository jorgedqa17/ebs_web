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
public enum Moneda {
    COLON(Long.parseLong("1")),
    DOLAR(Long.parseLong("2"));

    private final Long idMoneda;

    private Moneda(Long idMoneda) {
        this.idMoneda = idMoneda;
    }

    public Long getIdMoneda() {
        return idMoneda;
    }

}
