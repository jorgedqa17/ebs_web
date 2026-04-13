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
public enum TiposCierre {
    DIARIO(Long.parseLong("1")),
    SEMANAL(Long.parseLong("2"));
    private final Long idTipoCierre;

    private TiposCierre(Long idTipoCierre) {
        this.idTipoCierre = idTipoCierre;
    }

    public Long getIdTipoCierre() {
        return idTipoCierre;
    }

}
