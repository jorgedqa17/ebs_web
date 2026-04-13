package com.ebs.exception;

import javax.ejb.ApplicationException;

/**
 * Clase encargada de contener la excepcion a utilizar durante la aplicacion,
 * para indicar errores sucedidos en la capa de servicio. Esta excepcion
 * contiene informacion mas detallada acerca de la situacion sucedida.
 *
 * @author Adam M. Gamboa González
 * @since 16 de Enero de 2012
 *
 * @author Consorcio Siansa-Indra. Modificado por: Lsanchez 21/08/2014 Año:2014
 *
 * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
 * @version 1.0.0
 * @date 28/02/2017 16:44
 */
@ApplicationException(rollback = true)
public class ExcepcionServicio extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Atributo de la excepcion, para almacenar algun mensaje en especifico para
     * el programador. Se puede colocar informacion detallada como la clase y
     * metodo en que se lanzo la excepcion.
     */
    private String mensajeProgramador;
    /**
     * Clase de donde proviene el error.
     */
    private Class clase;
    /**
     * Bandera para indicar que tipo de mensaje mostrar.
     */
    private final boolean debug = false;
    /**
     * Bandera para indicar si es necesario enviar el mensaje programador.
     */
    private boolean envioMensajeProgramador = false;

    /**
     * constructor.
     */
    private ExcepcionServicio() {
    }

    /**
     * Maneja la excepciones para los servicios.
     *
     * @param mensajeDetalle Mensaje que se debe presentar al usuario
     * @param clase Clase de la cual proviene el error
     */
    public ExcepcionServicio(String mensajeDetalle, Class clase) {
        super(mensajeDetalle);
        this.envioMensajeProgramador = false;
        this.clase = clase;
    }

    /**
     * Maneja la excepciones para los servicios.
     *
     * @param mensajeDetalle Mensaje que se debe presentar al usuario
     * @param mensajeProgramador Mensaje a mayor detalle, para ser utilizado por
     * el programador
     * @param clase Clase de la cual proviene el error
     */
    public ExcepcionServicio(
            String mensajeDetalle, String mensajeProgramador, Class clase) {
        super(mensajeDetalle);
        this.mensajeProgramador = mensajeProgramador;
        this.envioMensajeProgramador = true;
        this.clase = clase;
    }

    /**
     * Maneja la excepciones para los servicios
     *
     * @param mensajeDetalle Mensaje que se debe presentar al usuario
     * @param causa Throwable de la excepcion
     * @param clase Clase de la cual proviene el error
     */
    public ExcepcionServicio(
            String mensajeDetalle, Throwable causa, Class clase) {
        super(mensajeDetalle, causa);
        this.envioMensajeProgramador = false;
        this.clase = clase;
    }

    /**
     * Maneja la excepciones para los servicios
     *
     * @param mensajeDetalle Mensaje que se debe presentar al usuario
     * @param mensajeProgramador Mensaje a mayor detalle, para ser utilizado por
     * el programador
     * @param causa Throwable de la excepcion
     * @param clase Clase de la cual proviene el error
     */
    public ExcepcionServicio(
            String mensajeDetalle, String mensajeProgramador,
            Throwable causa, Class clase) {
        super(mensajeDetalle, causa);
        this.mensajeProgramador = mensajeProgramador;
        this.envioMensajeProgramador = true;
        this.clase = clase;
    }

    //<<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
    //<<>><<>><<>><<>><<>><<>>   GETTER y SETTER  <<>><<>><<>><<>><<>><<>><<>>
    //<<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
    public String getMensajeProgramador() {
        return mensajeProgramador;
    }

    public String getMensajeDetalle() {
        return super.getMessage();
    }

    public Throwable getCausa() {
        return super.getCause();
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    public boolean isEnvioMensajeProgramador() {
        return envioMensajeProgramador;
    }

    @Override
    public String toString() {
        if (this.debug) {
            return this.mensajeProgramador;
        }
        return this.getMensajeDetalle();
    }
}
