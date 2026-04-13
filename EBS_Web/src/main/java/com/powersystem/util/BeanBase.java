package com.powersystem.util;

import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

/**
 * Clase Abstracta encargada de dar el comportamiento básico que se desea tener
 * en los ManagedBean utilizados en la aplicación.
 * <br/><i> Todo Bean debe extender de ella.</i>
 *
 * @author Consorcio Siansa-Indra. Diseñado y Desarrollado por: Lsanchez
 * 21/08/2014 Año: 2014
 */
public abstract class BeanBase implements Serializable {

    /**
     * Inicializa las variables del bean, al acceder al constructor
     */
    public abstract void inicializar();

    /**
     * Inicializas las variables y atributos del bean, después del llamado del
     * constructor. Es utilizado para las inicializaciones que utilicen datos de
     * objetos injectados.
     */
    public abstract void inicializarPost();

    /**
     * Muestra en la pantalla el mensaje genérico de guardar.
     *
     * @param key llave de la descripción del elemento a ser guardado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mensajeGuardar(String key) {
        MensajeUtil.agregarMensajeInfo(
                EtiquetasUtil.mensajeGuardar(key));
    }

    /**
     * Muestra en la pantalla el mensaje genérico de modificar.
     *
     * @param key llave de la descripción del elemento a ser modificado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mensajeModificar(String key) {
        MensajeUtil.agregarMensajeInfo(
                EtiquetasUtil.mensajeModificar(key));
    }

    /**
     * Muestra en la pantalla el mensaje genérico de asignar.
     *
     * @param key llave de la descripción del elemento a ser asignado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mensajeAsignar(String key) {
        MensajeUtil.agregarMensajeInfo(
                EtiquetasUtil.mensajeAsignar(key));
    }

    /**
     * Muestra en la pantalla el mensaje genérico de excluir.
     *
     * @param key llave de la descripción del elemento a ser excluido.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mensajeExcluir(String key) {
        MensajeUtil.agregarMensajeInfo(
                EtiquetasUtil.mensajeExcluir(key));
    }

    /**
     * Muestra en la pantalla el mensaje genérico de ordenar.
     *
     * @param key llave de la descripción del elemento a ser ordenado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mensajeOrdenar(String key) {
        MensajeUtil.agregarMensajeInfo(
                EtiquetasUtil.mensajeOrdenar(key));
    }

    /**
     * Muestra en la pantalla el mensaje genérico de eliminar.
     *
     * @param key llave de la descripción del elemento a ser eliminado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mensajeEliminar(String key) {
        MensajeUtil.agregarMensajeInfo(
                EtiquetasUtil.mensajeEliminar(key));
    }

    /**
     * Muestra en la pantalla el mensaje genérico cuando la consulta no trae
     * resultados.
     *
     * @param key llave de la descripción del elemento de no hay resultados.
     * @param parametros lista de parámetros del mensaje
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 29/03/2017 12:55
     */
    protected void mensajeNoResultados(String key, Object... parametros) {
        MensajeUtil.agregarMensajeAdvertencia(
                EtiquetasUtil.mensajeNoResultados(key, parametros));
    }

    /**
     * Limpia los filtros de una o mas tablas.
     *
     * @param tablas Lista de tablas a limpiar los filtros
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 09/06/2017 08:14
     */
    protected void limpiarFiltro(String... tablas) {
        List<String> componentes = new ArrayList<>();
        for (String nombreTabla : tablas) {
            DataTable tabla = (DataTable) FacesContext
                    .getCurrentInstance()
                    .getViewRoot()
                    .findComponent(nombreTabla);
            if (tabla != null && !tabla.getFilters().isEmpty()) {
                tabla.reset();
                tabla.setSortBy(null);
                componentes.add(nombreTabla);
            }
        }
        if (!componentes.isEmpty()) {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.update(componentes);
        }
    }

    /**
     * Muestra en la pantalla un mensaje de confirmación genérico de guardar.
     *
     * @param key llave de la descripción del elemento a ser guardado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mostrarMensajeConfirmacionGuardar(String key) {
        MensajeUtil.mostrarMensajeConfirmacionInformacion(EtiquetasUtil.mensajeGuardar(key));
    }

    /**
     * Muestra en la pantalla un mensaje de confirmación genérico de guardar.
     *
     * @param keys llave de la descripción del elemento a ser guardado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mostrarMensajeConfirmacionRequerido(ArrayList<String> keys) {
        String mensaje = "";
        for (String msj : keys) {
            mensaje += EtiquetasUtil.mensajeRequerido(msj) + "\\n";
        }
        MensajeUtil.mostrarMensajeConfirmacionAdvertencia(mensaje);
    }

    /**
     * Muestra en la pantalla un mensaje de confirmación genérico de eliminar.
     *
     * @param key llave de la descripción del elemento a ser eliminado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mostrarMensajeConfirmacionEliminar(String key) {
        MensajeUtil.mostrarMensajeConfirmacionInformacion(EtiquetasUtil.mensajeEliminar(key));
    }

    /**
     * Muestra en la pantalla un mensaje de confirmación genérico de modificar.
     *
     * @param key llave de la descripción del elemento a ser modificado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mostrarMensajeConfirmacionModificar(String key) {
        MensajeUtil.mostrarMensajeConfirmacionInformacion(EtiquetasUtil.mensajeModificar(key));
    }

    /**
     * Muestra en la pantalla un mensaje de confirmación genérico de asignar.
     *
     * @param key llave de la descripción del elemento a ser asignado.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mostrarMensajeConfirmacionAsignar(String key) {
        MensajeUtil.mostrarMensajeConfirmacionInformacion(EtiquetasUtil.mensajeAsignar(key));
    }

    /**
     * Muestra en la pantalla un mensaje de confirmación genérico de excluir.
     *
     * @param key llave de la descripción del elemento a ser excluído.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Katherine Ledezma R.
     * @version 1.0.0
     * @date 20/03/2017 15:55
     */
    protected void mostrarMensajeConfirmacionExcluir(String key) {
        MensajeUtil.mostrarMensajeConfirmacionInformacion(EtiquetasUtil.mensajeExcluir(key));
    }
}
