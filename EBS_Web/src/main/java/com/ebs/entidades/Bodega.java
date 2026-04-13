/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"bodegaPK"}, callSuper = false)
@Entity
@Table(name = "bodega", schema = "searmedica")
public class Bodega implements Serializable {

    @EmbeddedId
    protected BodegaPK bodegaPK;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "ubicacion")
    private String ubicacion;

    public Bodega() {
    }

    public Bodega(BodegaPK bodegaPK) {
        this.bodegaPK = bodegaPK;
    }

    public static Bodega retornarBodega(String nombreBodega) {
        Bodega bodegaRetorno = new Bodega();
        BodegaPK bodegaRetornoPK = new BodegaPK();
        bodegaRetornoPK.setId_bodega(0L);
        bodegaRetornoPK.setId_organizacion(0L);
        bodegaRetorno.setBodegaPK(bodegaRetornoPK);
        bodegaRetorno.setDescripcion(nombreBodega);
        bodegaRetorno.setUbicacion("SIN UBICACION");

        return bodegaRetorno;
    }
}
