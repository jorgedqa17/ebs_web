/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Jorge Quesada Arias
 */
public class ArchivoNombre {

    private InputStream inputStream;
    private String nombre;
    private byte[] archivo;

    public ArchivoNombre(InputStream inputStream, String nombre) {
        this.inputStream = inputStream;
        this.nombre = nombre;
     
    }

  

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
