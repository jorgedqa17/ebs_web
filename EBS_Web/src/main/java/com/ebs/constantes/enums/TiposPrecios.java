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
public enum TiposPrecios {

    GENERAL(1L),
    POR_MAYOR(2L),
    PRECIO_ESPECIAL(9L),
    PRECIO_BASE(10L);

    private final Long tipoPrecio;

    private TiposPrecios(Long tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }

    public Long getTipoPrecio() {
        return tipoPrecio;
    }

}
