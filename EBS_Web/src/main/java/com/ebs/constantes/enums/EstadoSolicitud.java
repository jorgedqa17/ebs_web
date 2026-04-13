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
public enum EstadoSolicitud {

    PENDIENTE(1l, "Solicitud Pendiente"),
    RECHAZADO(2l,"Solicitud Rechazada"),
    RECIBIDO_PARCIAL(3l,"Solicitud Recibida Parcial"),
    RECIBIDO(4l,"Solicitud Recibida"),
    ANULADA(5l,"Solicitud Anulada");

    @Getter
    @Setter
    private String descripcion;
    
    @Getter
    @Setter
    private Long id;

    private EstadoSolicitud(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
    
    public static EstadoSolicitud obtenerEstado(Long idEstado){
        EstadoSolicitud resultado = null;
        for(EstadoSolicitud soli : EstadoSolicitud.values()){
            if(soli.getId().longValue() == idEstado.longValue()){
                resultado = soli;
                break;
            }
        }
        
        return resultado;
    }
}
