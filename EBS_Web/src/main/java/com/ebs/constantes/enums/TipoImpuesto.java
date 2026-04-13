/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 *
 * @author Jorge Quesada Arias
 */
public enum TipoImpuesto {

    VALOR_AGREGADO(1L, "13");

    private final Long idImpuesto;
    private final String valor;

    private TipoImpuesto(Long idImpuesto, String valor) {
        this.idImpuesto = idImpuesto;
        this.valor = valor;
    }

    public Long getIdImpuesto() {
        return idImpuesto;
    }

    public String getValor() {
        return valor;
    }

}
