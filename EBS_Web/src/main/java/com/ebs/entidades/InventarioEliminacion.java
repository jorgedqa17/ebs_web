/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.util.Date;
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
 * @author Jorge Quesada Arias
 */
@Data
@EqualsAndHashCode(of = {"id_eliminacion"}, callSuper = false)
@Entity
@Table(name = "inventario_eliminacion", schema = "searmedica")
@SequenceGenerator(name = "seq_inventario_eliminacion", sequenceName = "searmedica.seq_inventario_eliminacion", allocationSize = 1)
public class InventarioEliminacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inventario_eliminacion")
    @Column(name = "id_eliminacion")
    private Long idEliminacion;
    
    @Column(name = "id_inventario")
    private Long idInventario;
    
    @Column(name = "login")
    private String login;
    
    @Column(name = "fecha_registro")
    private Date fechaRegistro;
    
    @Column(name = "motivo")
    private String Motivo;
    
    @Column(name = "cantidad")
    private Long cantidad;

    public InventarioEliminacion() {
    }

    public InventarioEliminacion(Long idInventario, String login, Date fechaRegistro, String Motivo, Long cantidad) {
        this.idInventario = idInventario;
        this.login = login;
        this.fechaRegistro = fechaRegistro;
        this.Motivo = Motivo;
        this.cantidad = cantidad;
    }
    
    
}
