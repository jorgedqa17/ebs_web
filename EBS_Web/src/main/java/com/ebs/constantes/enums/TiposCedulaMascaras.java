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
public enum TiposCedulaMascaras {

    CEDULA_FISICA(Long.parseLong("1"), "999999999", 9),
    CEDULA_JURIDICA(Long.parseLong("2"), "9999999999", 10),
    DIMEX(Long.parseLong("3"), "999999999999", 12),
    NITE(Long.parseLong("4"), "9999999999", 10);

    private final Long idTipoCedula;
    private final String mascara;
    private final Integer tamannoTipoCedula;

    private TiposCedulaMascaras(Long idTipoCedula, String mascara, Integer tamannoTipoCedula) {
        this.mascara = mascara;
        this.idTipoCedula = idTipoCedula;
        this.tamannoTipoCedula = tamannoTipoCedula;
    }

    public String getMascara() {
        return mascara;
    }

    public Long getIdTipoCedula() {
        return idTipoCedula;
    }

    public Integer getTamannoTipoCedula() {
        return tamannoTipoCedula;
    }

}
