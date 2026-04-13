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
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"id_tipo_cedula"}, callSuper = false)
@Entity
@Table(name = "tipo_cedula", schema = "searmedica")
public class TipoIdentificacion implements Serializable {

    @Id
    @Column(name = "id_tipo_cedula")
    private Long id_tipo_cedula;

    @Column(name = "codigo_hacienda")
    private String codigo_hacienda;

    @Column(name = "descripcion")
    private String descripcion;
}
