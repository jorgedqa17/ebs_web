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
public enum TipoProducto {

    MERCANCIA(Long.parseLong("1"), "Mercancia"),
    SERVICIO(Long.parseLong("2"), "Servicio");
    private final Long idTipoProducto;
    private final String descripcion;

    private TipoProducto(Long idTipoProducto, String descripcion) {
        this.idTipoProducto = idTipoProducto;
        this.descripcion = descripcion;
    }

    public Long getIdTipoProducto() {
        return idTipoProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
