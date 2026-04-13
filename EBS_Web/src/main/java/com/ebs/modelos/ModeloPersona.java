/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Cliente;
import com.ebs.entidades.Persona;
import com.ebs.entidades.PersonaCorreo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class ModeloPersona {
    
    @Getter
    @Setter
    private String numero_cedula;
    @Getter
    @Setter
    private Long id_tipo_cedula;
    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private String primer_apellido;
    @Getter
    @Setter
    private String segundo_apellido;
    @Getter
    @Setter
    private String correo_electronico;
    @Getter
    @Setter
    private String direccion;
    @Getter
    @Setter
    private String telefono_1;
    @Getter
    @Setter
    private String telefono_2;
    @Getter
    @Setter
    private String codigo_pais;
    @Getter
    @Setter
    private Long id_provincia;
    @Getter
    @Setter
    private Long id_canton;
    @Getter
    @Setter
    private Long id_distrito;
    @Getter
    @Setter
    private Long id_barrio;
    @Getter
    @Setter
    private String nombreCompleto;
    @Getter
    @Setter
    private Cliente cliente;
    @Getter
    @Setter
    private List<PersonaCorreo> listaCorreosPersona;
    @Getter
    @Setter
    private Long codigoActividad;
    
    public static ModeloPersona convertirPersonaAModeloPersona(Persona persona) {
        ModeloPersona modelo = new ModeloPersona();
        modelo.setNumero_cedula(persona.getPersonaPK().getNumero_cedula());
        modelo.setId_tipo_cedula(persona.getPersonaPK().getId_tipo_cedula());
        modelo.setNombre(persona.getNombre().trim());
        modelo.setPrimer_apellido(persona.getPrimer_apellido() == null ? "" : persona.getPrimer_apellido().trim());
        modelo.setSegundo_apellido(persona.getSegundo_apellido() == null ? "" : persona.getSegundo_apellido().trim());
        modelo.setCorreo_electronico(persona.getCorreo_electronico() == null ? "" : persona.getCorreo_electronico().trim());
        modelo.setDireccion(persona.getDireccion() == null ? "" : persona.getDireccion().trim());
        modelo.setTelefono_1(persona.getTelefono_1() == null ? "" : persona.getTelefono_1().trim());
        modelo.setTelefono_2(persona.getTelefono_2() == null ? "" : persona.getTelefono_2().trim());
        modelo.setCodigo_pais(persona.getCodigo_pais() == null ? "" : persona.getCodigo_pais().trim());
        modelo.setId_provincia(persona.getId_provincia() == null ? null : persona.getId_provincia());
        modelo.setId_canton(persona.getId_canton() == null ? null : persona.getId_canton());
        modelo.setId_distrito(persona.getId_distrito() == null ? null : persona.getId_distrito());
        modelo.setId_barrio(persona.getId_barrio() == null ? null : persona.getId_barrio());
        modelo.setNombreCompleto(modelo.getNombre() + " " + modelo.getPrimer_apellido() + " " + modelo.getSegundo_apellido());
        modelo.setCodigoActividad(persona.getIdCodigoActividad());
        modelo.setListaCorreosPersona(persona.getListaCorreosPersona());
        return modelo;
    }
    
    public static ModeloPersona convertirPersonaAModeloPersona(PersonaModelo personaModelo) {
        Persona persona = personaModelo.getPersona();
        
        ModeloPersona modelo = new ModeloPersona();
        modelo.setCliente(personaModelo.getCliente());
        modelo.setNumero_cedula(persona.getPersonaPK().getNumero_cedula());
        modelo.setId_tipo_cedula(persona.getPersonaPK().getId_tipo_cedula());
        modelo.setNombre(persona.getNombre().trim());
        modelo.setPrimer_apellido(persona.getPrimer_apellido() == null ? "" : persona.getPrimer_apellido().trim());
        modelo.setSegundo_apellido(persona.getSegundo_apellido() == null ? "" : persona.getSegundo_apellido().trim());
        modelo.setCorreo_electronico(persona.getCorreo_electronico() == null ? "" : persona.getCorreo_electronico().trim());
        modelo.setDireccion(persona.getDireccion() == null ? "" : persona.getDireccion().trim());
        modelo.setTelefono_1(persona.getTelefono_1() == null ? "" : persona.getTelefono_1().trim());
        modelo.setTelefono_2(persona.getTelefono_2() == null ? "" : persona.getTelefono_2().trim());
        modelo.setCodigo_pais(persona.getCodigo_pais() == null ? "" : persona.getCodigo_pais().trim());
        modelo.setId_provincia(persona.getId_provincia() == null ? null : persona.getId_provincia());
        modelo.setId_canton(persona.getId_canton() == null ? null : persona.getId_canton());
        modelo.setId_distrito(persona.getId_distrito() == null ? null : persona.getId_distrito());
        modelo.setId_barrio(persona.getId_barrio() == null ? null : persona.getId_barrio());
        modelo.setNombreCompleto(modelo.getNombre() + " " + modelo.getPrimer_apellido() + " " + modelo.getSegundo_apellido());
        return modelo;
    }
}
