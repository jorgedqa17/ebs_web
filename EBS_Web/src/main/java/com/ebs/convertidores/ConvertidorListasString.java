/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.convertidores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;

/**
 *
 * @author Jorge GBSYS
 */
@FacesConverter(value = "ConvertidorListasString")
public class ConvertidorListasString implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return string;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        String resultado = "";
        if (o instanceof String) {
            resultado = o.toString();
        } else if (o instanceof Long) {
            resultado = o.toString();
        } else if (o instanceof Integer) {
            resultado = o.toString();
        }
        return resultado;
    }

}
