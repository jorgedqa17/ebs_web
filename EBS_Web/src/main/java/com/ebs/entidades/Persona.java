/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import com.ebs.constantes.enums.Indicadores;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge GBSYS
 */
@EqualsAndHashCode(of = {"personaPK"}, callSuper = false)
@Entity
@Table(name = "persona", schema = "searmedica")
public class Persona implements Serializable {

    @EmbeddedId
    protected PersonaPK personaPK;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "primer_apellido")
    private String primer_apellido;

    @Column(name = "segundo_apellido")
    private String segundo_apellido;

    @Column(name = "correo_electronico")
    private String correo_electronico;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono_1")
    private String telefono_1;

    @Column(name = "telefono_2")
    private String telefono_2;

    @Column(name = "codigo_pais")
    private String codigo_pais;

    @Column(name = "id_provincia")
    private Long id_provincia;

    @Column(name = "id_canton")
    private Long id_canton;

    @Column(name = "id_distrito")
    private Long id_distrito;

    @Column(name = "id_barrio")
    private Long id_barrio;

    @Column(name = "es_persona_hacienda")
    private Integer es_persona_hacienda;

    @Column(name = "nombre_fantasia")
    private String nombre_fantasia;

    @Column(name = "es_activo")
    private Integer es_activo;

    @Column(name = "es_exonerado")
    private Integer es_exonerado;

    @Column(name = "es_exento")
    private Integer es_exento;
    @Column(name = "id_codigo_actividad")
    private Long idCodigoActividad;

    @Column(name = "exoneraciones")
    private String exoneraciones;

    @Transient
    private boolean esExonerado;

    @Transient
    private boolean esExento;

    @Transient
    private List<PersonaCorreo> listaCorreosPersona;

    public List<PersonaCorreo> getListaCorreosPersona() {
        return listaCorreosPersona;
    }

    public void setListaCorreosPersona(List<PersonaCorreo> listaCorreosPersona) {
        this.listaCorreosPersona = listaCorreosPersona;
    }

    public String getNombreCompleto() {
        if (this.segundo_apellido == null) {
            segundo_apellido = "";
        }
        if (this.primer_apellido == null) {
            primer_apellido = "";
        }
        return nombre + " " + primer_apellido + " " + segundo_apellido;
    }

    public Persona() {
        this.listaCorreosPersona = new ArrayList<>();
    }

    public Persona(PersonaPK personaPK, String nombre,
            String primer_apellido, String segundo_apellido,
            String correo_electronico, String direccion,
            String telefono_1, String telefono_2, String codigo_pais,
            Long id_provincia, Long id_canton, Long id_distrito,
            Long id_barrio, String nombre_fantasia,
            Integer es_activo,
            Integer esExonerado,
            Integer esExento) {
        this.personaPK = personaPK;
        this.nombre = nombre;
        this.primer_apellido = primer_apellido;
        this.segundo_apellido = segundo_apellido;
        this.correo_electronico = correo_electronico;
        this.direccion = direccion;
        this.telefono_1 = telefono_1;
        this.telefono_2 = telefono_2;
        this.codigo_pais = codigo_pais;
        this.id_provincia = id_provincia;
        this.id_canton = id_canton;
        this.id_distrito = id_distrito;
        this.id_barrio = id_barrio;
        this.nombre_fantasia = nombre_fantasia;
        this.es_activo = es_activo;
        this.es_exonerado = esExonerado;
        this.es_exento = esExento;

        this.esExento = esExento == null ? false : (esExento.equals(Indicadores.EXENTO_SI.getIndicador()));
        this.esExonerado = esExonerado == null ? false : (esExonerado.equals(Indicadores.EXONERADO_SI.getIndicador()));
        this.listaCorreosPersona = new ArrayList<>();
    }

    public PersonaPK getPersonaPK() {
        return personaPK;
    }

    public void setPersonaPK(PersonaPK personaPK) {
        this.personaPK = personaPK;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimer_apellido() {
        if (this.primer_apellido == null) {
            primer_apellido = "";
        }
        return primer_apellido;
    }

    public void setPrimer_apellido(String primer_apellido) {

        this.primer_apellido = primer_apellido;
    }

    public String getSegundo_apellido() {
        if (this.segundo_apellido == null) {
            segundo_apellido = "";
        }
        return segundo_apellido;
    }

    public void setSegundo_apellido(String segundo_apellido) {
        this.segundo_apellido = segundo_apellido;
    }

    public String getCorreo_electronico() {
        return correo_electronico;
    }

    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono_1() {
        return telefono_1;
    }

    public void setTelefono_1(String telefono_1) {
        this.telefono_1 = telefono_1;
    }

    public String getTelefono_2() {
        return telefono_2;
    }

    public void setTelefono_2(String telefono_2) {
        this.telefono_2 = telefono_2;
    }

    public String getCodigo_pais() {
        return codigo_pais;
    }

    public void setCodigo_pais(String codigo_pais) {
        this.codigo_pais = codigo_pais;
    }

    public Long getId_provincia() {
        return id_provincia;
    }

    public void setId_provincia(Long id_provincia) {
        this.id_provincia = id_provincia;
    }

    public Long getId_canton() {
        return id_canton;
    }

    public void setId_canton(Long id_canton) {
        this.id_canton = id_canton;
    }

    public Long getId_distrito() {
        return id_distrito;
    }

    public void setId_distrito(Long id_distrito) {
        this.id_distrito = id_distrito;
    }

    public Long getId_barrio() {
        return id_barrio;
    }

    public void setId_barrio(Long id_barrio) {
        this.id_barrio = id_barrio;
    }

    public Integer getEs_persona_hacienda() {
        return es_persona_hacienda;
    }

    public void setEs_persona_hacienda(Integer es_persona_hacienda) {
        this.es_persona_hacienda = es_persona_hacienda;
    }

    public String getNombre_fantasia() {
        return nombre_fantasia;
    }

    public void setNombre_fantasia(String nombre_fantasia) {
        this.nombre_fantasia = nombre_fantasia;
    }

    public Integer getEs_activo() {
        return es_activo;
    }

    public void setEs_activo(Integer es_activo) {
        this.es_activo = es_activo;
    }

    public Integer getEs_exonerado() {
        return es_exonerado;
    }

    public void setEs_exonerado(Integer es_exonerado) {
        this.es_exonerado = es_exonerado;
    }

    public Integer getEs_exento() {
        return es_exento;
    }

    public void setEs_exento(Integer es_exento) {
        this.es_exento = es_exento;
    }

    public boolean isEsExonerado() {
        return esExonerado;
    }

    public void setEsExonerado(boolean esExonerado) {
        this.esExonerado = esExonerado;
    }

    public boolean isEsExento() {
        return esExento;
    }

    public void setEsExento(boolean esExento) {
        this.esExento = esExento;
    }

    public Long getIdCodigoActividad() {
        return idCodigoActividad;
    }

    public void setIdCodigoActividad(Long idCodigoActividad) {
        this.idCodigoActividad = idCodigoActividad;
    }

    public String getExoneraciones() {
        return exoneraciones;
    }

    public void setExoneraciones(String exoneraciones) {
        this.exoneraciones = exoneraciones;
    }

}
