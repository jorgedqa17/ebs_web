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
@EqualsAndHashCode(of = {"idEstado"}, callSuper = false)
@Entity
@Table(name = "estados", schema = "searmedica")
public class Estados implements Serializable {

    @Id
    @Column(name = "id_estado")
    private Long idEstado;

    @Column(name = "id_clase")
    private Long idClase;

    @Column(name = "descripcion")
    private String descripcion;
}
