/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class InformacionReferencia {

    @Getter
    @Setter
    private String TipoDoc;
    @Getter
    @Setter
    private String Numero;
    @Getter
    @Setter
    private String FechaEmision;
    @Getter
    @Setter
    private Date Fecha;
    @Getter
    @Setter
    private String Codigo;
    @Getter
    @Setter
    private String Razon;

    public static InformacionReferencia inicializar() {
        InformacionReferencia informacionReferencia = new InformacionReferencia();
        informacionReferencia.setTipoDoc("1");
        informacionReferencia.setNumero("");
        informacionReferencia.setFechaEmision("");
        informacionReferencia.setFecha(new Date());
        informacionReferencia.setCodigo("05");
        informacionReferencia.setRazon("");
        return informacionReferencia;

    }
}
