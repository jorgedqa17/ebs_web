/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Producto;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import lombok.Data;

/**
 *
 * @author
 */
@Data
public class NuevoProducto {

    private Long idProductoActual;

    private String nombreProducto;

    private long cantidad;

    private Date fechaVencimiento;

    private String numeroLote;

    private DateFormat formato;

    private Long numeroFila;

    private String nombreProveedor;

    private BigDecimal precio;

    private BigDecimal precioGeneral;
    private BigDecimal impuesto;
    private ModeloTipoPrecio tipoPrecio;
    private ModeloTipoPrecio tipoPrecioAlPorMayor;

    public NuevoProducto() {
        this.formato = new SimpleDateFormat("dd/MM/yyyy");
    }

    public NuevoProducto(Long numeroFila, String seleccion, BigDecimal precio, String nombreProveedor) {
        this.numeroFila = numeroFila;
        String[] valores = seleccion.split("#");
        this.formato = new SimpleDateFormat("dd/MM/yyyy");
        this.idProductoActual = Long.parseLong(valores[0]);
        this.nombreProducto = valores[1];
        this.precio = precio;
        this.nombreProveedor = nombreProveedor;
    }

    public NuevoProducto(Long numeroFila, String seleccion, BigDecimal precio, String nombreProveedor, ModeloTipoPrecio tipoPrecio) {
        this.numeroFila = numeroFila;
        String[] valores = seleccion.split("#");
        this.formato = new SimpleDateFormat("dd/MM/yyyy");
        this.idProductoActual = Long.parseLong(valores[0]);
        this.nombreProducto = valores[1];
        this.precio = precio;
        this.nombreProveedor = nombreProveedor;
        this.tipoPrecio = tipoPrecio;
        
    }

    public NuevoProducto(Long numeroFila, String seleccion, BigDecimal precio, String nombreProveedor, ModeloTipoPrecio tipoPrecio, ModeloTipoPrecio tipoPrecioAlPorMayor) {
        this.numeroFila = numeroFila;
        String[] valores = seleccion.split("#");
        this.formato = new SimpleDateFormat("dd/MM/yyyy");
        this.idProductoActual = Long.parseLong(valores[0]);
        this.nombreProducto = valores[1];
        this.precio = precio;
        this.nombreProveedor = nombreProveedor;
        this.tipoPrecio = tipoPrecio;
        this.tipoPrecioAlPorMayor = tipoPrecioAlPorMayor;
    }

    public NuevoProducto(Long numeroFila, String seleccion) {
        this.numeroFila = numeroFila;
        String[] valores = seleccion.split("#");
        this.formato = new SimpleDateFormat("dd/MM/yyyy");
        this.idProductoActual = Long.parseLong(valores[0]);
        this.nombreProducto = valores[1];
        this.precio = new BigDecimal("0.0");
    }

    public NuevoProducto(Long numeroFila, Long idProducto, String nombreProducto) {
        this.numeroFila = numeroFila;
        this.formato = new SimpleDateFormat("dd/MM/yyyy");
        this.idProductoActual = idProducto;
        this.nombreProducto = nombreProducto;
        this.precio = new BigDecimal("0.0");
    }

    public String getFechaTexto() {
        if (fechaVencimiento == null) {
            return "";
        } else {
            return formato.format(fechaVencimiento);
        }
    }

    public void setFechaTexto(String fecha) {

    }

}
