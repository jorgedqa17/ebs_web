/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 *
 * @author
 */
public enum IngresoTipo {

    INGRESO_BODEGA(1l,"Ingreso a Bodega"),
    TRASLADO_BODEGA(2l,"Traslado a Bodega"),
    REINTEGRO_BODEGA(3L,"Reintegro a Bodega");
    
    private final Long idTipo;
    private final String nombre;

    private IngresoTipo(Long idTipo, String nombre) {
        this.idTipo = idTipo;
        this.nombre = nombre;
    }

    public Long getIdTipo() {
        return idTipo;
    }

    public String getNombre() {
        return nombre;
    }
    
    
    
}
