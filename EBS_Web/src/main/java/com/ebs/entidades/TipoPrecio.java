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
@EqualsAndHashCode(of = {"id_tipo"}, callSuper = false)
@Entity
@Table(name = "tipos_precios", schema = "searmedica")
public class TipoPrecio implements Serializable {

    @Id
    @Column(name = "id_tipo")
    private Long id_tipo;

    @Column(name = "descripcion")
    private String descripcion;
}
