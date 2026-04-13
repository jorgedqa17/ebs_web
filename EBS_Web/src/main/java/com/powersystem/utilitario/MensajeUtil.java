package com.powersystem.utilitario;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 * Clase Utilitaria, que contiene los métodos para realizar el despliegue de
 * errores en la aplicación. Contiene procedimientos para cada uno de los
 * posibles distintos tipos de mensajes a desplegar ERROR, INFO, WARN, FATAL,
 * además de los distintos lugar en donde mostrar dichos mensajes, Globalmente o
 * el mensaje va a un componente en específico.
 *
 *
 * @author Adam M. Gamboa González
 * @since
 *
 * @author Consorcio Siansa-Indra.<br>
 * @Modificado por: Lsanchez 21/08/2014<br>
 * @Año:2014<br>
 *
 */
public class MensajeUtil {

    /**
     * Enumerado con los diferentes Niveles de mensaje utilizados en la
     * aplicación.
     */
    public enum TipoMensaje {

        ADVERTENCIA(FacesMessage.SEVERITY_WARN, "Advertencia"),
        ERROR(FacesMessage.SEVERITY_ERROR, "Error"),
        FATAL(FacesMessage.SEVERITY_FATAL, "Error Fatal"),
        INFORMACION(FacesMessage.SEVERITY_INFO, "Información");
        public FacesMessage.Severity severidad;
        public String nombre;

        TipoMensaje(FacesMessage.Severity severity, String nombre) {
            this.severidad = severity;
            this.nombre = nombre;
        }
    }

    /**
     * Agrega un mensaje de tipo ERROR, a la cola de faces, para ser mostrado
     * por alguno de los componentes &lt; p:messages/> en la pantalla.
     *
     * @param msg Mensaje de error a desplegar.
     */
    public static void agregarMensajeError(String msg) {
        agregarMensaje(null, msg, TipoMensaje.ERROR);
    }

    /**
     * Agrega un mensaje de tipo INFORMACION, a la cola de faces, para ser
     * mostrado por alguno de los componentes &lt;p:messages/> en la pantalla.
     *
     * @param msg Mensaje de informativo a desplegar.
     */
    public static void agregarMensajeInfo(String msg) {
        agregarMensaje(null, msg, TipoMensaje.INFORMACION);
    }

    /**
     * Agrega un mensaje de tipo Advertencia, a la cola de faces, para ser
     * mostrado por alguno de los componentes &lt;p:messages/> en la pantalla.
     *
     * @param msg Mensaje de advertencia a desplegar.
     */
    public static void agregarMensajeAdvertencia(String msg) {
        agregarMensaje(null, msg, TipoMensaje.ADVERTENCIA);
    }

    /**
     * Agrega un mensaje de tipo FATAL, a la cola de faces, para ser mostrado
     * por alguno de los componentes &lt;p :messages /> en la pantalla.
     *
     * @param msg Mensaje de error fatal a desplegar.
     */
    public static void agregarMensajeFatal(String msg) {
        agregarMensaje(null, msg, TipoMensaje.FATAL);
    }

    /**
     * Agrega un mensaje de error por componente
     *
     * @param componente Id del componente a mostrar mensaje
     * @param msg Mensaje a mostrar
     */
    public static void agregarMensajeErrorComponente(String componente, String msg) {
        agregarMensaje(componente, msg, TipoMensaje.ERROR);
        UIInput uii = (UIInput) JSFUtil.obtenerComponente(componente);
        if (uii != null) {
            uii.setValid(false);
        }
    }

    /**
     * Activa el color de un componentes especifico
     *
     * @param componente Componente a colocar Focus
     */
    public static void agregarFocusComponente(String componente) {
        UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
        if (uui != null) {
            uui.setValid(false);
        }
    }

    /**
     * Agrega un mensaje de error por componente
     *
     * @param componentes String[]
     * @param msg String
     */
    public static void agregarMensajeErrorComponente(String componentes[], String msg) {
        agregarMensaje(null, msg, TipoMensaje.ERROR);
        if (componentes != null) {
            for (String componente : componentes) {
                if (componente != null) {
                    UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
                    if (uui != null) {
                        uui.setValid(false);
                    }
                }
            }
        }
    }

    /**
     * Agrega de un mensaje de información
     *
     * @param componente String Id del componente a mostrar mensaje
     * @param msg String Mensaje a mostrar
     */
    public static void agregarMensajeInfoComponente(String componente, String msg) {
        agregarMensaje(componente, msg, TipoMensaje.INFORMACION);
        UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
        if (uui != null) {
            uui.setValid(true);
        }
    }

    /**
     * Agrega de un mensaje de advertencia
     *
     * @param componente String Id del mensaje a mostrar
     * @param msg String Mensaje a mostrar
     */
    public static void agregarMensajeAdvertenciaComponente(String componente, String msg) {
        agregarMensaje(componente, msg, TipoMensaje.ADVERTENCIA);
        UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
        if (uui != null) {
            uui.setValid(false);
        }
    }

    /**
     * Agrega de un mensaje de advertencia a un componente
     *
     * @param componentes String[] Listados de id, de los componentes a mostrar
     * mensaje
     * @param msg String Mensaje a mostrar
     */
    public static void agregarMensajeAdvertenciaComponente(String componentes[], String msg) {
        agregarMensaje(null, msg, TipoMensaje.ADVERTENCIA);
        if (componentes != null) {
            for (String componente : componentes) {
                UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
                if (uui != null) {
                    uui.setValid(false);
                }
            }
        }
    }

    /**
     * Agrega de un mensaje fatal a un componente
     *
     * @param componente String Id del componente a mostrar mensaje
     * @param msg String Mensaje a mostrar
     */
    public static void agregarMensajeFatalComponente(String componente, String msg) {
        agregarMensaje(componente, msg, TipoMensaje.FATAL);
        UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
        if (uui != null) {
            uui.setValid(true);
        }
    }

    /**
     * Agrega de un mensaje fatal a un componente
     *
     * @param componentes String[] Listado de componentes a agregar mensaje
     * @param msg String Mensaje a mostrar
     */
    public static void agregarMensajeFatalComponente(String componentes[], String msg) {
        agregarMensaje(null, msg, TipoMensaje.FATAL);
        if (componentes != null) {
            for (String componente : componentes) {
                UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
                if (uui != null) {
                    uui.setValid(true);
                }
            }
        }
    }

    /**
     * Utiliza el FacesContex para desplegar un mensaje en un objeto de la UI
     *
     * @param id identificador del objeto
     * @param mensaje mensaje a mostrar
     * @param severity severidad del mensaje
     */
    private static void agregarMensaje(String id, String mensaje, TipoMensaje tipo) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage fm = crearFacesMessage(tipo, mensaje);
        context.addMessage(id, fm);
    }

    /**
     * /**
     * Método encargado de crear un FacesMessage que será desplegado en la
     * aplicación.
     *
     * @param tipo Tipo de Mensaje
     * @param mensaje Texto del mensaje
     * @return FacesMessage Creado
     */
    public static FacesMessage crearFacesMessage(TipoMensaje tipo, String mensaje) {
        return new FacesMessage(tipo.severidad, tipo.nombre, mensaje);
    }

    /**
     * Metodo encargado de marcar en rojo el campo dado.
     *
     * @param componente Componente a marcar en rojo.
     */
    public static void marcarEnRojo(String componente) {
        UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
        if (uui != null) {
            uui.setValid(false);
        }
    }

    /**
     * Metodo que se encarga de marcar en rojo los campos dados en el arreglo.
     *
     * @param componentes Listado de componentes a marcar en Rojo
     */
    public static void marcarEnRojo(String componentes[]) {
        for (String componente : componentes) {
            UIInput uui = (UIInput) JSFUtil.obtenerComponente(componente);
            if (uui != null) {
                uui.setValid(false);
            }
        }
    }

    public static void mostrarMensajeConfirmacionInformacion(String mensaje) {

        FacesMessage message = new FacesMessage(
                TipoMensaje.INFORMACION.severidad, TipoMensaje.INFORMACION.nombre, mensaje);
        RequestContext.getCurrentInstance().showMessageInDialog(message);
        /**try {
            RequestContext.getCurrentInstance().wait(1);
        } catch (InterruptedException e) {

        }
        RequestContext.getCurrentInstance().closeDialog(message);**/
    }

    public static void mostrarMensajeConfirmacionAdvertencia(String mensaje) {
        FacesMessage message = new FacesMessage(
                TipoMensaje.ADVERTENCIA.severidad, TipoMensaje.ADVERTENCIA.nombre, mensaje);
        RequestContext.getCurrentInstance().showMessageInDialog(message);        
       
    }

    public static void mostrarMensajeConfirmacionError(String mensaje) {
        FacesMessage message = new FacesMessage(
                TipoMensaje.ERROR.severidad, TipoMensaje.ERROR.nombre, mensaje);
        RequestContext.getCurrentInstance().showMessageInDialog(message);
    }

    
}
