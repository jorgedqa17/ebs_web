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
public enum TipoCliente {
    
    DETALLE(Long.parseLong("2")),
    POR_MAYOR(Long.parseLong("1"));
    private final Long idTipoCliente;
    
    private TipoCliente(Long idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }
    
    public Long getIdTipoCliente() {
        return idTipoCliente;
    }
    
}
