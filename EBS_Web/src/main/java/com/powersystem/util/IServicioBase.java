/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.util;

import com.ebs.exception.BeanException;
import java.io.Serializable;
import java.util.List;

/**
 * Clase interface creada para que sea extendida por cada una de las interfaces
 * de los servicios locales
 *
 * @author Consorcio Siansa-Indra. Diseñado y Desarrollado por: Lsanchez
 * 21/08/2014 Año: 2014
 */
public interface IServicioBase extends Serializable {

    /**
     * Método encargado de ejecutar un guardar sobre la base de datos en una
     * tabla especifica con los valores que trae el objeto que se le pasa como
     * parámetro.
     *
     * @param g Objeto a manipular
     */
    void guardar(Object g);
    
    /**
     * Permite almacenar una lista de objectos
     * @param lista Lista de objetos
     */
    void guardar(List<Object> lista); 

    /**
     * Método encargado de ejecutar un actualizar sobre la base de datos en una
     * tabla especifica con los valores que trae el objeto que se le pasa como
     * parámetro.
     *
     * @param g Objeto a manipular
     */
    void actualizar(Object g);

    /**
     * Método encargado de ejecutar un eliminar sobre la base de datos en una
     * tabla especifica con los valores que trae el objeto que se le pasa como
     * parámetro.
     *
     * @param g Objeto a manipular
     */
    public void eliminar(Object g) throws BeanException;

}
