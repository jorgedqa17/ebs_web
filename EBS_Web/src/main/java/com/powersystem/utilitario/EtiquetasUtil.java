package com.powersystem.utilitario;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 * Clase encargada de obtener las etiquetas del recurso de mensajes.
 *
 * @author Adam M. Gamboa González
 * @since 05 de Marzo de 2011
 *
 *
 * @author Consorcio Siansa-Indra. Modificado por: Lsanchez 21/08/2014 Año:2014
 */
public class EtiquetasUtil {

    /**
     * Definicion de la ruta del archivo de propiedades, donde están definidas
     * las etiquetas.
     */
    private static final String ARCHIVO_ETIQUETA = "com.powersystem.base.etiquetas";
    /**
     * Definición de la ruta del archivo de propiedades, donde están definidos
     * los mensajes.
     */
    private static final String ARCHIVO_MENSAJE = "com.powersystem.base.mensajes";
    /**
     * Definición de la ruta del archivo de propiedades, donde están definidos
     * los mensajes de las exepciones
     */
    private static final String ARCHIVO_EXCEPTION = "com.powersystem.base.mensajesExcepcion";

    /**
     * Mensaje de éxito cuando se guarda
     */
    private static final String MENSAJE_GUARDAR = "mensaje.info.guardar";
    /**
     * Mensaje de éxito cuando se modifica
     */
    private static final String MENSAJE_MODIFICAR = "mensaje.info.modificar";
    /**
     * Mensaje de éxito cuando se elimina
     */
    private static final String MENSAJE_ELIMINAR = "mensaje.info.eliminar";
    /**
     * Mensaje genérico para cuando no hay resultados en la consulta
     */
    private static final String MENSAJE_NO_RESULTADOS = "mensaje.alerta.no_resultados";
    /**
     * Mensaje genérico para cuando se asigna
     */
    private static final String MENSAJE_ASIGNAR = "mensaje.info.asignar";
    /**
     * Mensaje genérico para cuando se excluye
     */
    private static final String MENSAJE_EXCLUIR = "mensaje.info.excluir";
    
    /**
     * Mensaje genérico cuando es requerido
     */
    private static final String MENSAJE_REQUERIDO = "mensaje.error.campo.requerido2";

    /**
     * Definición de variables para el manejo de las propiedades por archivo
     */
    private static ResourceBundle recursoEtiquetas = null;
    private static ResourceBundle recursoMensaje = null;
    private static ResourceBundle recursoMensajeExcepcion = null;
    /**
     * Mensaje de éxito cuando se ordena
     */
    private static final String MENSAJE_ORDENAR = "mensaje.info.ordenar";

    /**
     * Obtiene una propiedad identificada con la llave enviada como parametros,
     * para obtener un valor del archivo de etiquetas.
     *
     * @param key Identificador de la etiqueta
     * @return Valor de la etiqueta
     */
    public static String obtenerEtiqueta(String key) {
        try {
            if (recursoEtiquetas == null) {
                recursoEtiquetas = ResourceBundle.getBundle(ARCHIVO_ETIQUETA,
                        FacesContext.getCurrentInstance().getViewRoot().getLocale());
            }

            return recursoEtiquetas.getString(key);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Retorna el mensaje internacionalizado para el key enviado, aplicando los
     * parametros que
     *
     * @param key java.lang.String llave del valor
     * @param argumentos java.lang.Object [] argumentos para aplicar al mensaje
     * @return java.lang.String mensaje internacionalizado
     */
    public static String obtenerEtiqueta(String key, Object... argumentos) {
        return MessageFormat.format(obtenerEtiqueta(key), argumentos);
    }

    /**
     * Obtiene una propiedad identificada con la llave enviada como parametros,
     * para obtener un valor del archivo de mensajes.
     *
     * @param key Identificador del mensaje
     * @return Valor del mensaje
     */
    public static String obtenerMensaje(String key) {
        try {
            if (recursoMensaje == null) {
                recursoMensaje = ResourceBundle.getBundle(ARCHIVO_MENSAJE,
                        FacesContext.getCurrentInstance().getViewRoot().getLocale());
            }

            return recursoMensaje.getString(key);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Obtiene una propiedad identificada con la llave enviada como parametros,
     * para obtener un valor del archivo de mensajes de exepciones.
     *
     * @param key Identificador del mensaje
     * @return Valor del mensaje
     */
    public static String obtenerMensajeExepcion(String key) {
        try {
            if (recursoMensajeExcepcion == null) {
                recursoMensajeExcepcion = ResourceBundle.getBundle(ARCHIVO_EXCEPTION,
                        FacesContext.getCurrentInstance().getViewRoot().getLocale());
            }

            return recursoMensajeExcepcion.getString(key);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Retorna el mensaje internacionalizado para el key enviado, aplicando los
     * parametros que
     *
     * @param key java.lang.String llave del valor
     * @param argumentos java.lang.Object [] argumentos para aplicar al mensaje
     * @return java.lang.String mensaje internacionalizado
     */
    public static String obtenerMensaje(String key, Object... argumentos) {
        return MessageFormat.format(obtenerMensaje(key), argumentos);
    }
    
    
     /**
     * Retorna el mensaje genérico de guardar.
     *
     * @param key llave de la descripción del elemento a ser guardado.
     * @return Retorna el mensaje de guardar genérico.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    public static String mensajeRequerido(String key) {
        String elemento = obtenerEtiqueta(key);
        return obtenerMensaje(MENSAJE_REQUERIDO, elemento);
    }

    /**
     * Retorna el mensaje genérico de guardar.
     *
     * @param key llave de la descripción del elemento a ser guardado.
     * @return Retorna el mensaje de guardar genérico.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    public static String mensajeGuardar(String key) {
        String elemento = obtenerMensaje(key);
        return obtenerMensaje(MENSAJE_GUARDAR, elemento);
    }

    /**
     * Retorna el mensaje genérico de modificar.
     *
     * @param key llave de la descripción del elemento a ser modificado.
     * @return Retorna el mensaje de modificar genérico.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    public static String mensajeModificar(String key) {
        String elemento = obtenerMensaje(key);
        return obtenerMensaje(MENSAJE_MODIFICAR, elemento);
    }

    /**
     * Retorna el mensaje genérico de ordenar.
     *
     * @param key llave de la descripción del elemento a ser ordenado.
     * @return Retorna el mensaje de ordenar genérico.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    public static String mensajeOrdenar(String key) {
        String elemento = obtenerMensaje(key);
        return obtenerMensaje(MENSAJE_ORDENAR, elemento);
    }

    /**
     * Retorna el mensaje genérico de asignar.
     *
     * @param key llave de la descripción del elemento a ser asignado.
     * @return Retorna el mensaje de asignar.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    public static String mensajeAsignar(String key) {
        String elemento = obtenerMensaje(key);
        return obtenerMensaje(MENSAJE_ASIGNAR, elemento);
    }

    /**
     * Retorna el mensaje genérico de excluir.
     *
     * @param key llave de la descripción del elemento a ser excluido.
     * @return Retorna el mensaje de excluir genérico.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    public static String mensajeExcluir(String key) {
        String elemento = obtenerMensaje(key);
        return obtenerMensaje(MENSAJE_EXCLUIR, elemento);
    }

    /**
     * Retorna el mensaje genérico de eliminar.
     *
     * @param key llave de la descripción del elemento a ser eliminado.
     * @return Retorna el mensaje de eliminar genérico.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    public static String mensajeEliminar(String key) {
        String elemento = obtenerMensaje(key);
        return obtenerMensaje(MENSAJE_ELIMINAR, elemento);
    }

    /**
     * Retorna el mensaje genérico cuando la consulta no trae resultados.
     *
     * @param key llave de la descripción del elemento de no hay resultados.
     * @param parametros lista de parámetros del mensaje
     * @return Retorna el mensaje de no hay resultados genérico.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 29/03/2017 12:55
     */
    public static String mensajeNoResultados(String key, Object... parametros) {
        String elemento = obtenerMensaje(key, parametros);
        return obtenerMensaje(MENSAJE_NO_RESULTADOS, elemento);
    }
}
