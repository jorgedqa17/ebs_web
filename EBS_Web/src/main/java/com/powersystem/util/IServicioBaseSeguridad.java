/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.util;

import com.ebs.exception.BeanException;
import java.io.Serializable;

/**
 * Clase interface creada para que sea extendida por cada una de las interfaces
 * de los servicios locales
 *
 * @author Consorcio Siansa-Indra. Diseñado y Desarrollado por: Lsanchez
 * 21/08/2014 Año: 2014
 */
public interface IServicioBaseSeguridad extends Serializable {

    /**
     * Crea un nuevo registro en la base de datos utilizando la entidad
     * solicitada
     *
     * @param g Object a manipular
     * @throws RuntimeException Manejo y control de errores
     */
    void guardar(Object g);

    /**
     * Actualiza un registro ya existente en la base de datos utilizando la
     * entidad solicitada
     *
     * @param g Object a manipular
     * @throws RuntimeException Manejo y control de errores
     */
    void actualizar(Object g);

    /**
     * Elimina un registro ya existente en la base de datos utilizando la
     * entidad solicitada
     *
     * @param g Object a manipular
     * @throws RuntimeException Manejo y control de errores
     */
    void eliminar(Object g) throws BeanException;
}
