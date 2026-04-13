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
@EqualsAndHashCode(of = {"idInventarioSalida"}, callSuper = false)
@Entity
@Table(name = "inventario_salida", schema = "searmedica")
@SequenceGenerator(name = "seq_invent_salida", sequenceName = "searmedica.seq_invent_salida", allocationSize = 1)
public class InventarioSalida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invent_salida")
    @Column(name = "id_inventario_salida")
    private Long idInventarioSalida;
    
    @Column(name = "id_bodega_origen")
    private Long idBodegaOrigen;
    
    @Column(name = "fecharegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    
    @Column(name = "login")
    private String login;
    
    @Column(name = "id_inventario_solicitud")
    private Long idInventarioSolicitud;

    public InventarioSalida() {
    }

    
    public InventarioSalida(Long idBodegaOrigen, Date fechaRegistro, String login,Long idInventarioSolicitud) {
        this.idBodegaOrigen = idBodegaOrigen;
        this.fechaRegistro = fechaRegistro;
        this.login = login;
        this.idInventarioSolicitud = idInventarioSolicitud;
    }

    
}
