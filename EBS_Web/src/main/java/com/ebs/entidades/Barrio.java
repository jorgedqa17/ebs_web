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
@EqualsAndHashCode(of = {"id_barrio"}, callSuper = false)
@Entity
@Table(name = "barrio", schema = "searmedica")
public class Barrio implements Serializable {

    @Id
    @Column(name = "id_barrio")
    private Long id_barrio;
    
    @Id
    @Column(name = "id_distrito")
    private Long id_distrito;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "codigo_hacienda")
    private String codigo_hacienda;
}
