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
public enum FacturaEnvioCorreos {

    NO_ENVIO_CORREO(0),
    ENVIO_CORREO(1);
    private final Integer envioCorreo;

    private FacturaEnvioCorreos(Integer envioCorreo) {
        this.envioCorreo = envioCorreo;
    }

    public Integer getEnvioCorreo() {
        return envioCorreo;
    }

}
