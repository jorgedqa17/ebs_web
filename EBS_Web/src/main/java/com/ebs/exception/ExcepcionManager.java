package com.ebs.exception;


import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;

import javax.ejb.EJBException;
import javax.enterprise.inject.InjectionException;
import javax.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import org.slf4j.Logger;

/**
 * Clase utilitaria para manejar las excepciones. Se encarga de mostrar los
 * mensajes de dicha excepcion.
 *
 * @author Adam M. Gamboa González & wPorras
 * @since 05 de Marzo 2011
 * @author Consorcio Siansa-Indra. Modificado por: Lsanchez 21/08/2014 Año:2014
 * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
 * @version 1.0.0
 * @date 06/03/2017 10:01
 */
@Slf4j
public class ExcepcionManager extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String MSG_BD_CONEXION
            = "No se logró establecer conexión con la base de datos.";
    private static final String MSG_NULL_POINTER = "Valores nulos";
    private static final String MSG_WEB_SERVICE
            = "No se pudo conectar al servicio web";
    private static final String MSG_TIME_OUT
            = "Ocurrió un timeout en la transacción";
    private static final String MSG_INYECCION
            = "No se pudieron crear las dependencias";
    private static final String MSG_REPORTE
            = "Error al generar el reporte";

    /**
     * Método encargado de manejar la Exception. Dependiendo del tipo de
     * Exception muestra y registra un distinto tipo de mensaje. Es decir, para
     * Exception de la lógica de aplicación son tomadas como errores normales,
     * cualquier otro tipo de Exception es tomada como un error Fatal.
     *
     * @param ex Exception a manejar
     */
    public static void manejarExcepcion(Exception ex) {
        if (ex instanceof EJBException) {
            Throwable trb = ((EJBException) ex).getCause();
            if (trb instanceof BeanException) {
                String mensaje = String.valueOf(((BeanException) ((EJBException) ex).getCausedByException()).getCode());
                MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensajeExepcion(mensaje));
            } else if (trb instanceof BeanException) {
                String mensaje = String.valueOf(((BeanException) ((EJBException) ex).getCausedByException()).getCode());
                MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensajeExepcion(mensaje));
            } else if (trb instanceof ExcepcionServicio) {
                String mensajeDetalle = String.valueOf(((ExcepcionServicio) ((EJBException) ex).getCausedByException()).getMensajeDetalle());
                MensajeUtil.agregarMensajeError(mensajeDetalle);
            } else if (trb instanceof JRException) {
                String mensaje = String.valueOf(((JRException) trb).getMessage());
                MensajeUtil.agregarMensajeError(mensaje);
                log.error(MSG_REPORTE, trb);
            } else if (trb instanceof JRRuntimeException) {
                String mensaje = String.valueOf(((JRRuntimeException) trb).getMessage());
                MensajeUtil.agregarMensajeError(mensaje);
                log.error(MSG_REPORTE, trb);
            } else if (trb instanceof InjectionException) {
                MensajeUtil.agregarMensajeError(MSG_INYECCION);
                log.error(MSG_INYECCION, trb);
            } else if (trb instanceof NullPointerException) {
                MensajeUtil.agregarMensajeError(MSG_NULL_POINTER);
                log.error(MSG_NULL_POINTER, trb);
            } else if (trb.getClass().getCanonicalName().contains("TimedOutException")) {
                MensajeUtil.agregarMensajeError(MSG_TIME_OUT);
                log.error(MSG_TIME_OUT, trb);
            } else if (trb.getClass().getCanonicalName().contains("LifecycleCallbackException")) {
                MensajeUtil.agregarMensajeError(MSG_WEB_SERVICE);
                log.error(MSG_WEB_SERVICE, trb);
            } else {
                String mensaje = ex.getMessage() != null ? ex.getMessage() : "";
                MensajeUtil.agregarMensajeError(mensaje);
            }
        } else if (ex instanceof ExcepcionServicio) {
            String mensajeDetalle = String.valueOf(((ExcepcionServicio) ex).getMensajeDetalle());
            MensajeUtil.agregarMensajeError(mensajeDetalle);
        } else if (ex instanceof ComponentException) {
            ComponentException cex = (ComponentException) ex;
            String mensajeDetalle;
            //Exception de TablaControl
            if (cex.getException() instanceof ExcepcionServicio) {
                mensajeDetalle = ((ExcepcionServicio) cex.getException()).getMensajeDetalle();
            } else {
                mensajeDetalle = cex.getException().getMessage();
            }
            MensajeUtil.agregarMensajeError(mensajeDetalle);
        } else if (ex instanceof NullPointerException) {
            MensajeUtil.agregarMensajeError(MSG_NULL_POINTER);
            log.error(MSG_NULL_POINTER, ex);
        } else if (ex instanceof JRException) {
            String mensaje = String.valueOf(((JRException) ex).getMessage());
            MensajeUtil.agregarMensajeError(mensaje);
            log.error(MSG_REPORTE, ex);
        } else if (ex instanceof JRRuntimeException) {
            String mensaje = String.valueOf(((JRRuntimeException) ex).getMessage());
            MensajeUtil.agregarMensajeError(mensaje);
            log.error(MSG_REPORTE, ex);
        } else if (ex instanceof BeanException) {
            String mensaje = String.valueOf(((BeanException) ex).getCode());
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensajeExepcion(mensaje));
        } else if (ex instanceof BeanException) {
            String mensaje = String.valueOf(((BeanException) ex).getCode());
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensajeExepcion(mensaje));
        } else if (ex instanceof EJBException && ex.getCause() instanceof ExcepcionServicio) {
            ExcepcionServicio esex = (ExcepcionServicio) ex.getCause();
            if (esex.getCausa() != null && esex.getCausa() instanceof PersistenceException) {
                PersistenceException persistenceException = (PersistenceException) esex.getCausa();
                //validacion conexión con la base de datos
                if (persistenceException.getMessage() != null && persistenceException.getMessage().indexOf("Cannot open connection") > 0) {
                    MensajeUtil.agregarMensajeError(MSG_BD_CONEXION);
                } else {
                    MensajeUtil.agregarMensajeError(esex.getMessage() != null ? esex.getMessage() : "");
                }
            } else {
                MensajeUtil.agregarMensajeError(esex.getMessage() != null ? esex.getMessage() : "");
            }
        } //Un error inesperado, o que no fue capturado por la logica del negocio.
        else {
            String mensaje = ex.getMessage() != null ? ex.getMessage() : "";
            log.error("Ocurrió un error inesperado.", ex);
            MensajeUtil.agregarMensajeError(mensaje);
        }
    }//FIN DEL METODO

    /**
     * Método que simplifica el código repetitivo al lanzar una Exeption desde
     * un servicios
     *
     * @param clase Clase donde esta siendo llamado
     * @param log Logger donde se va imprimir el mensaje
     * @param causa Exception que se encapsula en ExceptionServicio
     * @param mensajeUsuario llave del mensaje que se muestra al usuario final
     * @param mensajeDesarrollador llave del mensaje que se muestra al
     * desarrollador
     * @param paramentros parámetros que se muestran en el mensaje al
     * desarrollador
     * @return Exception encapsulada en ExceptionServicio
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 06/03/2017 10:01
     */
    public static ExcepcionServicio lanzarExcepcionServicio(Class clase,
            Logger log, Throwable causa, String mensajeUsuario,
            String mensajeDesarrollador, Object... paramentros) {

        String nombreClase = clase.getName();
        Object[] parametrosClase = new Object[paramentros.length + 1];
        parametrosClase[0] = nombreClase;
        for (int i = 1; i <= paramentros.length; i++) {
            parametrosClase[i] = paramentros[i - 1];
        }

        String usuario = EtiquetasUtil.obtenerMensaje(mensajeUsuario);
        String desarrollador = EtiquetasUtil.obtenerMensaje(
                mensajeDesarrollador, parametrosClase);

        log.error(desarrollador, causa);
        return new ExcepcionServicio(usuario, desarrollador, causa, clase);
    }
    
    
       /**
     * Método encargado de manejar la Exception. Dependiendo del tipo de
     * Exception muestra y registra un distinto tipo de mensaje. Es decir, para
     * Exception de la lógica de aplicación son tomadas como errores normales,
     * cualquier otro tipo de Exception es tomada como un error Fatal.
     *
     * @param ex Exception a manejar
     */
    public static void manejarExcepcionCalificacion(Exception ex) {
        if (ex instanceof EJBException) {
            Throwable trb = ((EJBException) ex).getCause();
            if (trb instanceof BeanException) {
                String mensaje = String.valueOf(((BeanException) ((EJBException) ex).getCausedByException()).getCode());
                MensajeUtil.mostrarMensajeConfirmacionError(EtiquetasUtil.obtenerMensajeExepcion(mensaje));
            } else if (trb instanceof BeanException) {
                String mensaje = String.valueOf(((BeanException) ((EJBException) ex).getCausedByException()).getCode());
                MensajeUtil.mostrarMensajeConfirmacionError(EtiquetasUtil.obtenerMensajeExepcion(mensaje));
            } else if (trb instanceof ExcepcionServicio) {
                String mensajeDetalle = String.valueOf(((ExcepcionServicio) ((EJBException) ex).getCausedByException()).getMensajeDetalle());
                MensajeUtil.mostrarMensajeConfirmacionError(mensajeDetalle);
            } else if (trb instanceof JRException) {
                String mensaje = String.valueOf(((JRException) ex).getMessage());
                MensajeUtil.mostrarMensajeConfirmacionError(mensaje);
                log.error("Error al generar el reporte", trb);
            } else if (trb instanceof JRRuntimeException) {
                String mensaje = String.valueOf(((JRRuntimeException) ex).getMessage());
                MensajeUtil.mostrarMensajeConfirmacionError(mensaje);
                log.error("Error al generar el reporte", trb);
            } else if (trb instanceof InjectionException) {
                MensajeUtil.mostrarMensajeConfirmacionError(MSG_INYECCION);
                log.error(MSG_INYECCION, trb);
            } else if (trb instanceof NullPointerException) {
                MensajeUtil.mostrarMensajeConfirmacionError(MSG_NULL_POINTER);
                log.error(MSG_NULL_POINTER, trb);
            } else if (trb.getClass().getCanonicalName().contains("TimedOutException")) {
                MensajeUtil.mostrarMensajeConfirmacionError(MSG_TIME_OUT);
                log.error(MSG_TIME_OUT, trb);
            } else if (trb.getClass().getCanonicalName().contains("LifecycleCallbackException")) {
                MensajeUtil.mostrarMensajeConfirmacionError(MSG_WEB_SERVICE);
                log.error(MSG_WEB_SERVICE, trb);
            } else {
                String mensaje = ex.getMessage() != null ? ex.getMessage() : "";
                MensajeUtil.mostrarMensajeConfirmacionError(mensaje);
            }
        } else if (ex instanceof ExcepcionServicio) {
            String mensajeDetalle = String.valueOf(((ExcepcionServicio) ex).getMensajeDetalle());
            MensajeUtil.mostrarMensajeConfirmacionError(mensajeDetalle);
        } else if (ex instanceof ComponentException) {
            ComponentException cex = (ComponentException) ex;
            String mensajeDetalle;
            //Exception de TablaControl
            if (cex.getException() instanceof ExcepcionServicio) {
                mensajeDetalle = ((ExcepcionServicio) cex.getException()).getMensajeDetalle();
            } else {
                mensajeDetalle = cex.getException().getMessage();
            }
            MensajeUtil.mostrarMensajeConfirmacionError(mensajeDetalle);
        } else if (ex instanceof NullPointerException) {
            MensajeUtil.mostrarMensajeConfirmacionError(MSG_NULL_POINTER);
            log.error(MSG_NULL_POINTER, ex);
        } else if (ex instanceof JRException) {
            String mensaje = String.valueOf(((JRException) ex).getMessage());
            MensajeUtil.mostrarMensajeConfirmacionError(mensaje);
            log.error("Error al generar el reporte", ex);
        } else if (ex instanceof JRRuntimeException) {
            String mensaje = String.valueOf(((JRRuntimeException) ex).getMessage());
            MensajeUtil.mostrarMensajeConfirmacionError(mensaje);
            log.error("Error al generar el reporte", ex);
        } else if (ex instanceof BeanException) {
            String mensaje = String.valueOf(((BeanException) ex).getCode());
            MensajeUtil.mostrarMensajeConfirmacionError(EtiquetasUtil.obtenerMensajeExepcion(mensaje));
        } else if (ex instanceof BeanException) {
            String mensaje = String.valueOf(((BeanException) ex).getCode());
            MensajeUtil.mostrarMensajeConfirmacionError(EtiquetasUtil.obtenerMensajeExepcion(mensaje));
        } else if (ex instanceof EJBException && ex.getCause() instanceof ExcepcionServicio) {
            ExcepcionServicio esex = (ExcepcionServicio) ex.getCause();
            if (esex.getCausa() != null && esex.getCausa() instanceof PersistenceException) {
                PersistenceException persistenceException = (PersistenceException) esex.getCausa();
                //validacion conexión con la base de datos
                if (persistenceException.getMessage() != null && persistenceException.getMessage().indexOf("Cannot open connection") > 0) {
                    MensajeUtil.mostrarMensajeConfirmacionError(MSG_BD_CONEXION);
                } else {
                    MensajeUtil.mostrarMensajeConfirmacionError(esex.getMessage() != null ? esex.getMessage() : "");
                }
            } else {
                MensajeUtil.mostrarMensajeConfirmacionError(esex.getMessage() != null ? esex.getMessage() : "");
            }
        } //Un error inesperado, o que no fue capturado por la logica del negocio.
        else {
            String mensaje = ex.getMessage() != null ? ex.getMessage() : "";
            log.error("Ocurrió un error inesperado.", ex);
            MensajeUtil.mostrarMensajeConfirmacionError(mensaje);
        }
    }//FIN DEL METODO

}
