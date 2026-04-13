/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author
 */
@Data
@EqualsAndHashCode(of = {"idInventarioIngreso"}, callSuper = false)
@Entity
@Table(name = "inventario_ingreso", schema = "searmedica")
@SequenceGenerator(name = "seq_invent_ingreso", sequenceName = "searmedica.seq_invent_ingreso", allocationSize = 1)
public class InventarioIngreso implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invent_ingreso")
    @Column(name = "id_inventario_ingreso")
    private Long idInventarioIngreso;
    
    @Column(name = "id_bodega_origen")
    private Long idBodegaOrigen;
    
    @Column(name = "login")
    private String login;
    
    @Column(name = "fecharegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    
    @Column(name = "id_inventario_solicitud")
    private Long idInventarioSolicitud;
    
    @Column(name = "id_inventario_ingreso_tipo")
    private Long idInventarioIngresoTipo;

    public InventarioIngreso() {
    }


    public InventarioIngreso(Date fechaRegistro, Long idInventarioIngresoTipo) {
        this.fechaRegistro = fechaRegistro;
        this.idInventarioIngresoTipo = idInventarioIngresoTipo;
    }

    public InventarioIngreso(Long idBodegaOrigen, String login, Date fechaRegistro, Long idInventarioSolicitud, Long idInventarioIngresoTipo) {
        this.idBodegaOrigen = idBodegaOrigen;
        this.login = login;
        this.fechaRegistro = fechaRegistro;
        this.idInventarioSolicitud = idInventarioSolicitud;
        this.idInventarioIngresoTipo = idInventarioIngresoTipo;
    }

    
}
