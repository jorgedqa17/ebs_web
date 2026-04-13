/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.InventarioIngresoDetalle;
import com.ebs.entidades.Producto;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author
 */
@Data
public class ModeloIngresoDetalle {
    
    
    private String nombreProducto;
    
    private Long cantidad;
    
    private String fecha;
    
    private String lote;    
    
    private Producto producto;
    
    private InventarioIngresoDetalle inventarioIngresoDetalle;
    
    private String nombreProveedor;

    public ModeloIngresoDetalle(Producto producto, InventarioIngresoDetalle inventarioIngresoDetalle) {
        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        this.producto = producto;
        this.inventarioIngresoDetalle = inventarioIngresoDetalle;
        this.nombreProducto = producto.getDescripcion();
        this.cantidad = inventarioIngresoDetalle.getCantidad();
        Date fechaTemporal = inventarioIngresoDetalle.getFechaVencimiento();
        this.fecha = (fechaTemporal == null ? "" : formato.format(fechaTemporal));
        this.lote = inventarioIngresoDetalle.getNumeroLote();
        this.nombreProveedor = inventarioIngresoDetalle.getNombreProveedor();
    }

    
    
    
    
    
}
