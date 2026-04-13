/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge Quesada
 */
public enum TipoResultado {

    ACEPTADO(1l, "Aceptado"),
    CANT_INSUFICIENTE(2l, "Cantidad Insuficiente"),
    PRODUCTO_INACTIVO(3l, "Producto Inactivo");

    @Getter
    @Setter
    private String descripcion;

    @Getter
    @Setter
    private Long id;

    private TipoResultado(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
}
