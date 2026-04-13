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
public enum Generico {
    SI("1"),
    NO("0");
    private final String valor;

    private Generico(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

}
