/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Jorge Quesada Arias
 */
@Entity
@Table(name = "persona_correo", schema = "searmedica")
public class PersonaCorreo implements Serializable {

    @Id
    @Column(name = "numero_cedula")
    private String numero_cedula;
    @Id
    @Column(name = "id_tipo_cedula")
    private Long id_tipo_cedula;
    @Id
    @Column(name = "correo_electronico")
    private String correo_electronico;
    @Id
    @Column(name = "alias_cliente")
    private String alias_cliente;

    public PersonaCorreo(String numero_cedula, Long id_tipo_cedula, String correo_electronico, String alias_cliente) {
        this.numero_cedula = numero_cedula;
        this.id_tipo_cedula = id_tipo_cedula;
        this.correo_electronico = correo_electronico;
        this.alias_cliente = alias_cliente;
    }

    public PersonaCorreo() {
    }

    public String getNumero_cedula() {
        return numero_cedula;
    }

    public void setNumero_cedula(String numero_cedula) {
        this.numero_cedula = numero_cedula;
    }

    public Long getId_tipo_cedula() {
        return id_tipo_cedula;
    }

    public void setId_tipo_cedula(Long id_tipo_cedula) {
        this.id_tipo_cedula = id_tipo_cedula;
    }

    public String getCorreo_electronico() {
        return correo_electronico;
    }

    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    public String getAlias_cliente() {
        return alias_cliente;
    }

    public void setAlias_cliente(String alias_cliente) {
        this.alias_cliente = alias_cliente;
    }

}
