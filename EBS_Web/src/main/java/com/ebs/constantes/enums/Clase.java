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
public enum Clase {
    FACTURA(1L),
    RECIBO(4L);
    private final Long idClase;

    private Clase(Long idClase) {
        this.idClase = idClase;
    }

    public Long getIdClase() {
        return idClase;
    }

}
