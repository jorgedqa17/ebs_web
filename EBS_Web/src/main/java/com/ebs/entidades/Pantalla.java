/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
@Entity
@Table(name = "pantalla", schema = "searmedica")
public class Pantalla implements Serializable {

    @Getter
    @Setter
    @Id
    private Long id_pantalla;
    @Getter
    @Setter
    private String descripcion;
    @Getter
    @Setter
    private Integer id_padre_pantalla;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private String icono;
    @Getter
    @Setter
    private Integer activo;

    public Pantalla() {
    }

    public Pantalla(Long id_pantalla, String descripcion, Integer id_padre_pantalla) {
        this.id_pantalla = id_pantalla;
        this.descripcion = descripcion;
        this.id_padre_pantalla = id_padre_pantalla;
    }

}
