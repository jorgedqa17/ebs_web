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
 * @author jdquesad
 */
@Data
@EqualsAndHashCode(of = {"id_parametro"}, callSuper = false)
@Entity
@Table(name = "parametros", schema = "searmedica")
public class Parametro implements Serializable {

    @Id
    @Column(name = "id_parametro")
    private Long id_parametro;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "valor")
    private String valor;
}
