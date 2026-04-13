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
public enum EstadoSolicitudDetalle {
    
    PENDIENTE_ENVIO(1l, "Solicitudes Pendientes de Envío"),
    SOLICITUD_ENVIADA(2l,"Preparar Solicitudes"),
    PENDIENTE_DESPACHO(3l,"Solicitudes para Despachar"),
    SOLICITUD_DESPACHADA(4l,"Solicitudes Pendiente de Recibo"),
    SOLICITUD_RECHAZADA(5l,"Solicitudes Rechazadas"),
    SOLICITUD_RECIBIDA(6l,"Solicitudes Recibidas"),
    SOLICITUD_DEVUELTA(7l,"Solicitudes Devueltas"),
    SOLICITUD_ANULADA(8l,"Solicitudes Anuladas");

    @Getter
    @Setter
    private String descripcion;
    
    @Getter
    @Setter
    private Long id;

    private EstadoSolicitudDetalle(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
    
    public static EstadoSolicitudDetalle obtenerEstado(Long idEstado){
        EstadoSolicitudDetalle resultado = null;
        for(EstadoSolicitudDetalle soli : EstadoSolicitudDetalle.values()){
            if(soli.getId().longValue() == idEstado.longValue()){
                resultado = soli;
                break;
            }
        }
        
        return resultado;
    }
}
