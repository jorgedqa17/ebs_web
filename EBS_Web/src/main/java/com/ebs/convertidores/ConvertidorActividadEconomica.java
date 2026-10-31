package com.ebs.convertidores;

import com.ebs.entidades.ActividadEconomica;
import com.powersystem.personas.servicios.ServicioPersona;
import javax.enterprise.inject.spi.CDI;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Convierte entre el texto enviado por el navegador (id de la actividad) y la actividad económica. Se usa
 * en los autocompletar que reemplazan al desplegable de 750 actividades.
 */
@FacesConverter(value = "ConvertidorActividadEconomica")
public class ConvertidorActividadEconomica implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return CDI.current().select(ServicioPersona.class).get().obtenerActividadPorId(Long.parseLong(valor.trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof ActividadEconomica && ((ActividadEconomica) o).getIdCodigoActividad() != null) {
            return ((ActividadEconomica) o).getIdCodigoActividad().toString();
        }
        return "";
    }
}
