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
public enum CondicionImpuestoEnum {

    PROPORCIONALIDAD(5L);

    private final Long idCondicionImpuesto;

    private CondicionImpuestoEnum(Long idCondicionImpuesto) {
        this.idCondicionImpuesto = idCondicionImpuesto;
    }

    public Long getIdCondicionImpuesto() {
        return idCondicionImpuesto;
    }

}
