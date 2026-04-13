/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


public class Ubicacion {

    private String id_provincia;

    private String id_canton;

    private String id_distrito;

    private String id_barrio;

    private String otrasSennas;


    public String getId_provincia() {
        return id_provincia;
    }

    public void setId_provincia(String id_provincia) {
        this.id_provincia = id_provincia;
    }


    public String getId_canton() {
        return id_canton;
    }

    public void setId_canton(String id_canton) {
        this.id_canton = id_canton;
    }


    public String getId_distrito() {
        return id_distrito;
    }

    public void setId_distrito(String id_distrito) {
        this.id_distrito = id_distrito;
    }


    public String getId_barrio() {
        return id_barrio;
    }

    public void setId_barrio(String id_barrio) {
        this.id_barrio = id_barrio;
    }

    public String getOtrasSennas() {
        return otrasSennas;
    }

    public void setOtrasSennas(String otrasSennas) {
        this.otrasSennas = otrasSennas;
    }

}
