/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Persona;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class ModeloPersonaTSE {

    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private String primerApellido;
    @Getter
    @Setter
    private String segundoApellido;

    public Persona obtenerPersona(Persona persona) {

        persona.setNombre(this.nombre);
        persona.setPrimer_apellido(primerApellido);
        persona.setSegundo_apellido(segundoApellido);
        return persona;
    }
}
