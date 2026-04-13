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
@EqualsAndHashCode(of = {"idInventarioSalidaDetalle"}, callSuper = false)
@Entity
@Table(name = "inventario_salida_detalle", schema = "searmedica")
@SequenceGenerator(name = "seq_invent_salida_detalle", sequenceName = "searmedica.seq_invent_salida_detalle", allocationSize = 1)
public class InventarioSalidaDetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invent_salida_detalle")
    @Column(name = "id_inventario_salida_detalle")
    private Long idInventarioSalidaDetalle;
    
    @Column(name = "id_bodega_destino")
    private Long idBodegaDestino;
    
    @Column(name = "id_producto")
    private Long idProducto;
    
    @Column(name = "id_factura")
    private Long idFactura;
    
    @Column(name = "numero_linea")
    private Long numeroLinea;
    
    @Column(name = "cantidad")
    private Long cantidad;
    
    @Column(name = "id_inventario_salida")
    private Long idInventarioSalida;
    
    @Column(name = "id_inventario")
    private Long idInventario;
    
    @Column(name = "fechavencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    
    @Column(name = "numerolote")
    private String numeroLote;
    
    @Column(name = "id_inventario_solicitud_detalle")
    private Long idInventarioSolicitudDetalle;
    
    @Column(name = "id_estado")
    private Long idEstado;
    
    @Column(name = "fecharegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    public InventarioSalidaDetalle() {
    }


    public InventarioSalidaDetalle(Long idBodegaDestino, Long idProducto, Long cantidad, Long idInventarioSalida, Long idInventario, Long idInventarioSolicitudDetalle, Long idEstado,Date fechaRegistro,String numeroLote, Date fechaVencimiento) {
        this.idBodegaDestino = idBodegaDestino;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.idInventarioSalida = idInventarioSalida;
        this.idInventario = idInventario;
        this.idInventarioSolicitudDetalle = idInventarioSolicitudDetalle;
        this.idEstado = idEstado;
        this.fechaRegistro = fechaRegistro;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
    }

    public InventarioSalidaDetalle(long idBodegaDestino, long idProducto, long idFactura, long numeroLinea, long cantidad, long idInventarioSalida, long idInventario, Long idInventarioSolicitudDetalle,Long idEstado,Date fechaRegistro,String numeroLote, Date fechaVencimiento) {
        this.idBodegaDestino = idBodegaDestino;
        this.idProducto = idProducto;
        this.idFactura = idFactura;
        this.numeroLinea = numeroLinea;
        this.cantidad = cantidad;
        this.idInventarioSalida = idInventarioSalida;
        this.idInventario = idInventario;
        this.idInventarioSolicitudDetalle = idInventarioSolicitudDetalle;
        this.idEstado = idEstado;
        this.fechaRegistro = fechaRegistro;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
    }

    
}
