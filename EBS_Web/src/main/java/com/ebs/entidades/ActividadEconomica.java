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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author jorge
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "actividad_economica", schema = "searmedica")
public class ActividadEconomica implements Serializable {

    @Id
    @Column(name = "id_codigo_actividad")
    private Long idCodigoActividad;

    @Column(name = "codigo_ciiu3")
    private String codigoActividadCIIU3;

    @Column(name = "dsc_ciiu3")
    private String dscCIIU3;

    @Column(name = "codigo_ciiu4")
    private String codigoActividadCIIU4;

    @Column(name = "dsc_ciiu4")
    private String dscCIIU4;

    public Long getIdCodigoActividad() {
        return idCodigoActividad;
    }

    public void setIdCodigoActividad(Long idCodigoActividad) {
        this.idCodigoActividad = idCodigoActividad;
    }
}
