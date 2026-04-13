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
public enum TipoUnidadMedida {
    ALQUILER_COMERCIAL(59L),
    ALQUILER_HABITACIONAL(16L);
    private final Long unidadMedida;

    private TipoUnidadMedida(Long unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Long getUnidadMedida() {
        return unidadMedida;
    }

}
