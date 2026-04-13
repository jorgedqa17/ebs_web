/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Producto;
import com.ebs.entidades.ProductoPrecio;
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
