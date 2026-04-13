/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author sergio
 */
@Data
@EqualsAndHashCode(of = {"idInventarioIngresoDetalle"}, callSuper = false)
@Entity
@Table(name = "inventario_ingreso_detalle", schema = "searmedica")
@SequenceGenerator(name = "seq_invent_ingreso_detalle", sequenceName = "searmedica.seq_invent_ingreso_detalle", allocationSize = 1)
public class InventarioIngresoDetalle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invent_ingreso_detalle")
    @Column(name = "id_inventario_ingreso_detalle")
    private Long idInventarioIngresoDetalle;

    @Column(name = "id_inventario_ingreso")
    private Long idInventarioIngreso;

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "cantidad")
    private Long cantidad;

    @Column(name = "fechavencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "numerolote")
    private String numeroLote;

    @Column(name = "id_inventario")
    private Long idInventario;

    @Column(name = "fecharegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "proveedor")
    private String nombreProveedor;

    @Transient
    private BigDecimal precio;

    public InventarioIngresoDetalle() {
    }

    public InventarioIngresoDetalle(Long idInventarioIngreso, Long idProducto, Long cantidad, Long idInventario) {
        this.idInventarioIngreso = idInventarioIngreso;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.idInventario = idInventario;
    }

    public InventarioIngresoDetalle(Long idInventarioIngreso, Long idProducto, Long cantidad, Date fechaVencimiento, String numeroLote, Long idInventario, Date fechaRegistro) {
        this.idInventarioIngreso = idInventarioIngreso;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
        this.numeroLote = numeroLote;
        this.idInventario = idInventario;
        this.fechaRegistro = fechaRegistro;
        this.nombreProveedor = null;
    }

    public InventarioIngresoDetalle(Long idInventarioIngreso, Long idProducto, Long cantidad,
            Date fechaVencimiento, String numeroLote, Long idInventario, Date fechaRegistro, String nombreProveedor, BigDecimal precio) {
        this.idInventarioIngreso = idInventarioIngreso;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
        this.numeroLote = numeroLote;
        this.idInventario = idInventario;
        this.fechaRegistro = fechaRegistro;
        this.nombreProveedor = nombreProveedor;
        this.precio = precio;
    }

}
