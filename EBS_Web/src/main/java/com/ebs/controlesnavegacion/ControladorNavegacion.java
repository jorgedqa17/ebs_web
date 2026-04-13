package com.ebs.controlesnavegacion;

//import cr.go.rnp.seguridad.entidad.BitacoraNavegacion;
//import cr.go.rnp.seguridad.modelo.InformacionUsuario;
//import cr.go.rnp.seguridad.servicio.ServicioBitacoraNavegacion;
//import cr.go.rnp.util.modelo.seguridad.NavegacionRegreso;
//import cr.go.rnp.util.utilitario.CDIUtil;
//import cr.go.rnp.util.utilitario.EJBUtils;
//import cr.go.rnp.util.utilitario.JSFUtil;
//import cr.go.rnp.util.utilitario.UTF8;
//import cr.go.rnp.util.utilitario.sesion.SesionUtil;
import com.powersystem.seguridad.bean.InformacionUsuario;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

/**
 * Clase encargada de servir de contralodor de navegación, cada vez, que se
 * navegue en el sistema esta clase se encargada de administrar esa navegación.
 * Es utilizada, para el bitacoreo de navegación, así como también para la
 * redirección de pantallas y el paso de parámetros.
 *
 * @author Consorcio Siansa-Indra. yarias 09/08/2015
 */
public class ControladorNavegacion extends ConfigurableNavigationHandler {

    private NavigationHandler padre;

    /**
     * Constructor
     *
     * @param p
     */
    public ControladorNavegacion(NavigationHandler p) {
        this.padre = p;
    }

    /**
     * Método encargado de la navegación. Es este método se obtiene y coloca los
     * parámetros de una pantalla a otra en el URL, además es aquí en donde se
     * lleva el bitacoreo de navegación.
     *
     *
     * @param context Contexto de JSF
     * @param fromAction Metodo/action que ejecuto la navegación
     * @param outcome Regla de Navegación.
     */
    @Override
    public void handleNavigation(FacesContext context, String fromAction, String outcome) {

        this.bitacoreoNavegacion(outcome);

        //Adjunta a la URL de navegacion, los distintos parametros enviados a la
        //otra pagina.
        String navegacion = outcome;
        navegacion = this.manejoParametros(navegacion, outcome);
        try {
            //Navego normalmente   
            this.padre.handleNavigation(context, fromAction, navegacion);
        } catch (Exception ex) {
        }
    }

    /**
     * Método implementado por requirimiento de la clase padre, lo que hace es
     * llamar al método de padre.
     *
     * @param context Contexto de Java Server Faces
     * @param fromAction Metodo/action que ejecuto la navegación
     * @param outcome Regla de Navegación.
     * @return Caso de Navegación
     */
    @Override
    public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
        if (padre instanceof ConfigurableNavigationHandler) {
            return ((ConfigurableNavigationHandler) padre).getNavigationCase(context, fromAction, outcome);
        } else {
            return null;
        }
    }

    /**
     * Método implementado por requirimiento de la clase padre, lo que hace es
     * llamar al método de padre.
     *
     * @param context Contexto de Java Server Faces
     * @param fromAction Metodo/action que ejecuto la navegación
     * @param outcome Regla de Navegación.
     * @return Caso de Navegación
     */
    @Override
    public Map<String, Set<NavigationCase>> getNavigationCases() {
        if (padre instanceof ConfigurableNavigationHandler) {
            return ((ConfigurableNavigationHandler) padre).getNavigationCases();
        } else {
            return null;
        }
    }

    /**
     * Método encargado del bitacoreo de navegación que se aplica en el sistema,
     * cada vez que un usuario con indicador de Trazable activado navega a una
     * pantalla del sistema distinta.
     *
     * @param outcome Regla de navegación.
     */
    private void bitacoreoNavegacion(String outcome) {
        //Esta seccion es para registrar el bitacoreo de navegacion.
        if (outcome != null && !outcome.isEmpty()) {
            try {
                String[] navegacion_aux = outcome.split("\\?");
                //    InformacionUsuario info = (InformacionUsuario) SesionUtil.obtenerDeSesion(SesionUtil.VariablesSesion.INFO_USUARIO);

//                if ((info != null) && (info.isTrazable())) {
//                    ServicioBitacoraNavegacion sbn = EJBUtils.obtenerEJB(ServicioBitacoraNavegacion.class);
//                    BitacoraNavegacion bn = sbn.crearObjetoBitacora(navegacion_aux[0], info, BitacoraNavegacion.ModoEjecucion.NAVEGACION);
//                    sbn.guardarBitacoraNavegacion(bn);
//                }
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Ajusta la navegación del sistema, indicado que sea redireccionada, así
     * como setea los parámetros en el URL a la siguiente pantalla.
     *
     * @param navegacion Regla de navegación
     * @param outcome Regla de navegación
     * @return Nueva regla de navegación con parámetros incluidos.
     */
    private String manejoParametros(String navegacion, String outcome) {
//        if (navegacion != null && !navegacion.trim().equals("")) {
//
//            String[] temp = outcome.split("\\?");
//            if (temp != null && temp.length > 1) {
//                String pagina = temp[0];
//                String parametros = temp[1];
//                navegacion = pagina + "?faces-redirect=true&" + parametros;
//            } else {
//                navegacion += "?faces-redirect=true";
//            }
//
//            //Parametros Request Indicados por medio de un f:param 
//            Map<String, String> parametros = JSFUtil.obtenerParametrosRequest();
//            for (Entry<String, String> parametro : parametros.entrySet()) {
//                String nombre_param = parametro.getKey();
//                String valor_param = parametro.getValue();
//                valor_param = valor_param != null ? UTF8.encodeUTF8(valor_param) : null;
//
//                if (nombre_param.startsWith("rtn_")) {
//                    MenuControlador mc = (MenuControlador) CDIUtil.getBeanByName("menuControlador");
//                    NavegacionRegreso nr = null;
//                    if (mc.getElementos_anteriores() != null && !mc.getElementos_anteriores().isEmpty()) {
//                        nr = mc.getElementos_anteriores().peek();
//                    }
//                    if (nr != null) {
//                        String nuevo_nombre_param = nombre_param.substring(4);
//                        nr.agregarParametro(nuevo_nombre_param, valor_param);
//                    }
//                } else if (!nombre_param.startsWith("form_spj") && !nombre_param.startsWith("javax")
//                        && !nombre_param.startsWith("form_migajas")) {
//                    navegacion += "&" + nombre_param + "=" + valor_param;
//                }
//            }
//
//            //Parametros indicados por medio de Codigo Java, con el utilitario
//            Map<String, String> parametros_colocados = (HashMap<String, String>) JSFUtil.obtenerDeSesion(JSFUtil.NOMBRE_MAP_REQUEST);
//            if (parametros_colocados != null) {
//                for (Entry<String, String> parametro : parametros_colocados.entrySet()) {
//                    String nombre_param = parametro.getKey();
//                    String valor_param = parametro.getValue();
//                    valor_param = valor_param != null ? UTF8.encodeUTF8(valor_param) : null;
//                    if (nombre_param.startsWith("rtn_")) {
//                        MenuControlador mc = (MenuControlador) CDIUtil.getBeanByName("menuControlador");
//                        NavegacionRegreso nr = null;
//                        if (mc.getElementos_anteriores() != null && !mc.getElementos_anteriores().isEmpty()) {
//                            nr = mc.getElementos_anteriores().peek();
//                        }
//
//                        if (nr != null) {
//                            String nuevo_nombre_param = nombre_param.substring(4);
//                            nr.agregarParametro(nuevo_nombre_param, valor_param);
//                        }
//                    } else {
//                        navegacion += "&" + nombre_param + "=" + valor_param;
//                    }
//                }
//                JSFUtil.removerDeSesion(JSFUtil.NOMBRE_MAP_REQUEST);
//            }
//
//            /**
//             * if (navegacion.contains("/xhtml/paginas/index") &&
//             * JSFUtil.obtenerParametroRequest("fromLogin") == null) {
//             * MenuControlador mc = (MenuControlador)
//             * CDIUtil.getBeanByName("menuControlador");
//             * mc.getModel().topMenu(); }*
//             */
//        }
//
//        return navegacion;
        return "";

    }
}
