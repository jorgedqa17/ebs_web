/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Cliente;
import com.ebs.entidades.Persona;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class PersonaModelo {

    @Getter
    @Setter
    private Cliente cliente;
    @Getter
    @Setter
    private Persona persona;

    public String getNombreCompleto() {
        String resultado = "";

        return resultado;
    }

}
