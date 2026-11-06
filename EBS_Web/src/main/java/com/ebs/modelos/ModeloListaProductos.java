/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Producto;
import com.ebs.entidades.ProductoPrecio;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class ModeloListaProductos {

    @Getter
    @Setter
    private Producto producto;

    @Getter
    @Setter
    private String descripcionUnidadMedida;

    @Getter
    @Setter
    private String descripcionTipoProducto;

    @Getter
    @Setter
    private String descripcionExoneracion;

    @Getter
    @Setter
    private String descripcionImpuesto;

    private String estado;

    @Getter
    @Setter
    private List<ModeloTipoPrecio> listaPrecios;

    // Datos de la página del catálogo (los llena CatalogoLazyModel; no se usan en otras pantallas).
    @Getter
    @Setter
    private BigDecimal precioGeneral;

    @Getter
    @Setter
    private String monedaGeneral;

    @Getter
    @Setter
    private BigDecimal precioPorMayor;

    @Getter
    @Setter
    private String monedaPorMayor;

    @Getter
    @Setter
    private int existenciaTotal;

    /** Nombre de archivo de la primera imagen del producto, o null si no tiene. */
    @Getter
    @Setter
    private String primeraImagen;

    public boolean isDisponible() {
        return existenciaTotal > 0;
    }

    public String getEstado() {
        String estado = "";
        if (producto.getActivo().equals(1)) {
            estado = "Activo";
        } else {
            estado = "Inactivo";
        }
        return estado;
    }

}
