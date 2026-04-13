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
@Entity
@EqualsAndHashCode(of = {"idInventarioSolicitud"}, callSuper = false)
@Table(name = "inventario_solicitud", schema = "searmedica")
@SequenceGenerator(name = "seq_invent_solicitud", sequenceName = "searmedica.seq_invent_solicitud", allocationSize = 1)
public class InventarioSolicitud implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invent_solicitud")
    @Column(name = "id_inventario_solicitud")
    private Long idInventarioSolicitud;
    
    @Column(name = "id_bodega_origen")
    private Long idBodegaOrigen;
    
    @Column(name = "estado")
    private Long estado;
    
    @Column(name = "fecharegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    
    @Column(name = "login")    
    private String login;
    
    @Column(name = "consecutivo")
    private String consecutivo;

    public InventarioSolicitud() {
    }    
    

    public InventarioSolicitud(Long idBodegaOrigen, Long estado, Date fechaRegistro, String login,String consecutivo) {
        this.idBodegaOrigen = idBodegaOrigen;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.login = login;
        this.consecutivo = consecutivo;
    }
    
    
    
}
