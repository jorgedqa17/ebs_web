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
public enum TiposPerfiles {

    ADMINISTRADOR(Long.parseLong("2"), "Administrador"),
    FACTURADOR(Long.parseLong("1"), "Facturador");

    private final Long idPerfil;
    private final String nombrePerfil;

    private TiposPerfiles(Long idPerfil, String nombrePerfil) {
        this.idPerfil = idPerfil;
        this.nombrePerfil = nombrePerfil;
    }

    public Long getIdPerfil() {
        return idPerfil;
    }

    public String getNombrePerfil() {
        return nombrePerfil;
    }

}
