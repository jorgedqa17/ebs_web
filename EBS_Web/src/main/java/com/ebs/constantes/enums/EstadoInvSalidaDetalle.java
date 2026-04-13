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
 * @author
 */
public enum EstadoInvSalidaDetalle {
 
    PENDIENTE(1l, "Pendiente"),
    APLICADO(2l,"Aplicado"),
    RECHAZADO(3l,"Rechazado");

    @Getter
    @Setter
    private String descripcion;
    
    @Getter
    @Setter
    private Long id;

    private EstadoInvSalidaDetalle(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
    
    public static EstadoInvSalidaDetalle obtenerEstado(Long idEstado){
        EstadoInvSalidaDetalle resultado = null;
        for(EstadoInvSalidaDetalle soli : EstadoInvSalidaDetalle.values()){
            if(soli.getId().longValue() == idEstado.longValue()){
                resultado = soli;
                break;
            }
        }
        
        return resultado;
    }
}
