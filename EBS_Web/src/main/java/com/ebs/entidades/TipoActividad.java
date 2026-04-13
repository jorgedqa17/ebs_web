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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
@EqualsAndHashCode(of = {"id_actividad"}, callSuper = false)
@Entity
@Table(name = "tipo_actividad", schema = "searmedica")
public class TipoActividad implements Serializable {

    @Id
    @Column(name = "id_actividad")
    private Long id_actividad;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "descripcion_actividad")
    private String descripcion_actividad;

    @Column(name = "codigo_hacienda")
    private String codigo_hacienda;
}
