/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author
 */
@Data
@EqualsAndHashCode(of = {"idInventarioIngresoTipo"}, callSuper = false)
@Entity
@Table(name = "inventario_ingreso_tipo", schema = "searmedica")
@SequenceGenerator(name = "seq_invent_tipo", sequenceName = "searmedica.seq_invent_tipo", allocationSize = 1)
public class InventarioIngresoTipo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invent_tipo")
    @Column(name = "id_inventario_ingreso_tipo")
    private Long idInventarioIngresoTipo;
        
    @Column(name = "descripcion")
    private String descripcion;

    public InventarioIngresoTipo() {
    }

    public InventarioIngresoTipo(String descripcion) {
        this.descripcion = descripcion;
    }
   

}
