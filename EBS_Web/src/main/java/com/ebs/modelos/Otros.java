/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Jorge GBSYS
 */
@XmlRootElement(name = "Otros")
@XmlType(propOrder = {"OtroTexto"})
public class Otros {

    private String OtroTexto;

    public String getOtroTexto() {
        return OtroTexto;
    }

    public void setOtroTexto(String OtroTexto) {
        this.OtroTexto = OtroTexto;
    }

}
