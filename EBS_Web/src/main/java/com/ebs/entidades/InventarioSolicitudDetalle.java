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
@EqualsAndHashCode(of = {"idInventarioSolicitudDetalle"}, callSuper = false)
@Table(name = "inventario_solicitud_detalle", schema = "searmedica")
@SequenceGenerator(name = "seq_invent_solicitud_detalle", sequenceName = "searmedica.seq_invent_solicitud_detalle", allocationSize = 1)
public class InventarioSolicitudDetalle implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invent_solicitud_detalle")
    @Column(name = "id_inventario_solicitud_detalle")
    private Long idInventarioSolicitudDetalle;
    
    @Column(name = "id_inventario_solicitud")
    private Long idInventarioSolicitud;
    
    @Column(name = "id_bodega_destino")
    private Long idBodegaDestino;
    
    @Column(name = "id_producto")
    private Long idProducto;
    
    @Column(name = "cantidad")
    private Long cantidad;
    
    @Column(name = "estado")
    private Long estado;   
    
    
    public InventarioSolicitudDetalle() {
    }

    
    public InventarioSolicitudDetalle(Long idInventarioSolicitud, Long idBodegaDestino, Long idProducto, Long cantidad, Long estado) {
        this.idInventarioSolicitud = idInventarioSolicitud;
        this.idBodegaDestino = idBodegaDestino;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.estado = estado;
    }
    
    
    
}
