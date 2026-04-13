/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.util;


import com.powersystem.utilitario.EJBUtils;
import java.io.Serializable;

/**
 * Clase abstracta de las cuales hereda las entidades y objetos modelo de la
 * aplicacion. Proporciona algunas propiedades o atributos basicos para dichos
 * objetos.
 *
 * @author Adam M. Gamboa González
 * @since 16 de Enero de 2012
 */
/**
 * @author Consorcio Siansa-Indra. Modificado por: Lsanchez 21/08/2014 Año: 2014
 */
public abstract class ObjetoBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Manejo de variables
     */
    protected boolean seleccionado;

    /**
     * Retorna un EJB, para ser accedado, en caso que alguno de los getter y
     * setter deberia realizar algún lógica específica en uno de ellos. De esta
     * manera no se coloca lógica en el Objeto.
     *
     * @param <T> Genérico de clase
     * @param clase Clase del servicio.
     * @return Servicio encontrado. Si no se encuentra ninguno, retorna <code>
     * null</code>
     */
    protected <T> T obtenerServicio(Class<T> clase) {
        return EJBUtils.obtenerEJB(clase);
    }
    //<<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
    //<<>><<>><<>><<>><<>><<>><<>>   GETS && SETS  <>><<>><<>><<>><<>><<>><<>>
    //<<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
