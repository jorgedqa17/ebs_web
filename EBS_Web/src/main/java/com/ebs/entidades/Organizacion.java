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
@EqualsAndHashCode(of = {"id_organizacion"}, callSuper = false)
@Entity
@Table(name = "organizacion", schema = "searmedica")
public class Organizacion implements Serializable {

    @Id
    @Column(name = "id_organizacion")
    private Long id_organizacion;

    @Column(name = "nombre")
    private String nombre;

}
