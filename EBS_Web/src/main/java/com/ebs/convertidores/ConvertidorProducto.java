/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.convertidores;

import com.ebs.entidades.Producto;
import com.ebs.modelos.ModeloProducto;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author
 */
@FacesConverter(value = "convertidorProducto")
public class ConvertidorProducto implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String valor) {
        return valor;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o == null) {
            return "";
        } else {
            Producto producto = (Producto) o;
            return producto.getId_producto().toString() + "#" + producto.getDescripcion().trim();
        }
    }

}
