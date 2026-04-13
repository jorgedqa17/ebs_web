package com.powersystem.utilitario;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;

/**
 * Utilitarios para JSF
 *
 * @author Consorcio Siansa-Indra.<br>
 * Modificado por: Lsanchez 21/08/2014 Año:2014
 */
public class JSFUtil {

    public static final String NOMBRE_MAP_REQUEST = "pw_param_request";

    /**
     * Obtiene un componente
     *
     * @param id String
     * @return UIComponent
     */
    public static UIComponent obtenerComponente(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        return root.findComponent(id);
        //return findComponent(root, id);
    }

    /**
     * Con respecto a un componente padre y un id busca el componente
     * correspondiente al Id
     *
     * @param parent componente padre, generalmente es el componente raiz
     * @param id id del componente solamente es el valor del id que se coloca en
     * la vista
     * @return the ui component
     */
    public static UIComponent obtenerComponente(UIComponent parent, String id) {

        if (id.equals(parent.getId())) {
            return parent;
        }
        Iterator<UIComponent> kids = parent.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            UIComponent found = obtenerComponente(kid, id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Get parameter value from request scope.
     *
     * @param name the name of the parameter
     * @return the parameter value
     */
    public static String obtenerParametroRequest(String name) {
        Object obj = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap().get(name);

        return obj != null
                ? ((!obj.equals("null"))
                ? UTF8.decodeUTF8((String) obj)
                : null)
                : null;
    }

    /**
     * Método que obtiene el listado de todos los parámetros enviados por
     * request en el contexto de JSF.
     *
     * @return Mapa con los parámetros.
     */
    public static Map<String, String> obtenerParametrosRequest() {
        return FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
    }

    /**
     * Método encargado de colocar parametros para ser obtenidos por el request.
     * Es una tipo de simulación ya que los parámetros colocados aqui, son
     * obtenidos por el controlador de navegación, para ser colocados en la URL
     * y enviados por Request.
     *
     * @param name Nombre del parámetro
     * @param value Valor del parámetro
     *
     */
    public static void colocarParametroRequest(String name, String value) {
        Map<String, String> parametrosColocados = (HashMap<String, String>) obtenerDeSesion(NOMBRE_MAP_REQUEST);
        if (parametrosColocados == null) {
            parametrosColocados = new HashMap<>();
        }
        parametrosColocados.put(name, value);
        guardarEnSesion(NOMBRE_MAP_REQUEST, parametrosColocados);
    }

    /**
     * Remueve una variable de la session
     *
     * @param llave Llave del objeto a eliminar.
     */
    public static void removerDeSesion(String llave) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove(llave);
    }

    /**
     * Metodo para setear/guardar variables en session.
     *
     * @param llave Identificador/llave del objeto a guardar
     * @param objeto Objeto a guardar
     */
    public static void guardarEnSesion(String llave, Object objeto) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map<String, Object> sesionState = ctx.getExternalContext().getSessionMap();
        sesionState.put(llave, objeto);
    }

    /**
     * Método para obtener variables de sessión por JSF. Si un objeto no existe
     * en sessión, entonces retorna null.
     *
     * @param llave Llave de objeto en sesión
     * @return Objeto obtenido.
     */
    public static Object obtenerDeSesion(String llave) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            Map<String, Object> sesionState = ctx.getExternalContext().getSessionMap();
            return sesionState.get(llave);
        }
        return null;
    }

    /**
     * Método encargado de obtener del contexto del contenedor, el id de la
     * sesión.
     *
     * @return Id de la sesión del navegador
     */
    public static String obtenerIDSesion() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpSession httpSession = (HttpSession) ctx.getExternalContext().getSession(true);
        String idSesion = null;

        if (httpSession != null) {
            idSesion = httpSession.getId();
        }

        return idSesion;
    }

    
    
    /**
     * Crea una expresion EL
     *
     * @param expresionEL String
     * @return MethodExpression
     */
    public static MethodExpression crearExpresionEL(String expresionEL) {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        ELContext elCtx = facesCtx.getELContext();
        ExpressionFactory expFact = facesCtx.getApplication().getExpressionFactory();

        MethodExpression me = expFact.createMethodExpression(elCtx, expresionEL, String.class, new Class[0]);
        return me;
    }

    public static String obtenerIpComputadora() {
        String resultado = "";
        try {
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            resultado = req.getRemoteAddr();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultado;
    }

    public static String obtenerNombreMaquina() {
        String resultado = "";
        try {
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            resultado = req.getRemoteHost();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultado;
    }

    /**
     * Se encargada de redireccionar por medio de código a cualquier página
     * dentro del proyecto, que se especifique en el parámetro url.
     *
     * @param url Direccion de la página a donde se desea redireccionar,
     * (empieza desde la raíz del contexto)
     *
     * @throws IOException
     */
    public static void redireccionarPagina(String url) throws IOException {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + url);
    }

    /**
     * Método encargado de habilitar un componente para su edición o trabajo en
     * la pantalla segun el valor indicado como parámetro.
     *
     * @param idComponente Id del componente a habilitar
     * @param habilitado Si habilita o Deshabilita
     */
    public static void habilitarComponente(String idComponente, boolean habilitado) {

        idComponente = "form_spj:" + idComponente;
        UIComponent uiComponente = JSFUtil.obtenerComponente(idComponente);
        if (uiComponente != null) {
            try {
                String setter = "setDisabled";
                Class clase = uiComponente.getClass();
                Method setMetodo = clase.getMethod(setter, new Class[]{boolean.class});
                setMetodo.invoke(uiComponente, !habilitado);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Método encargado de habilitar un componente para su edición o trabajo en
     * la pantalla segun el valor indicado como parámetro.
     *
     * @param idComponente Id del componente a habilitar
     * @param habilitado Si habilita o Deshabilita
     */
    public static void habilitarComponentePantallaGrande(String idComponente, boolean habilitado) {

        idComponente = "form_spj:id_tab:" + idComponente;
        UIComponent uiComponente = JSFUtil.obtenerComponente(idComponente);
        if (uiComponente != null) {
            try {
                String setter = "setDisabled";
                Class clase = uiComponente.getClass();
                Method setMetodo = clase.getMethod(setter, new Class[]{boolean.class});
                setMetodo.invoke(uiComponente, !habilitado);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Obtiene el componente y encuentra si esta habilitado o no.
     *
     * @param idComponente Identificador del componente
     * @return Si esta habilitado o no.
     */
    public static boolean isHabilitado(String idComponente) {
//        idComponente = "form_spj:" + idComponente;
//        UIComponent uiComponente = JSFUtil.obtenerComponente(idComponente);
//        if (uiComponente != null) {
//            try {
//                String getter = (uiComponente instanceof InputTextExt || uiComponente instanceof InputTextareaExt)
//                        ? "isReadonly" : "isDisabled";
//                Class clase = uiComponente.getClass();
//                Method getMetodo = clase.getMethod(getter);
//                return !((Boolean) getMetodo.invoke(uiComponente));
//            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            }
//        }
        return true;
    }

    /**
     * Obtiene el componente y encuentra si esta habilitado o no.
     *
     * @param idComponente Identificador del componente
     * @return Si esta habilitado o no.
     */
    public static boolean isHabilitadoPantallaInscripcion(String idComponente) {
//        idComponente = "form_spj:id_tab:" + idComponente;
//        UIComponent uiComponente = JSFUtil.obtenerComponente(idComponente);
//        if (uiComponente != null) {
//            try {
//                String getter = (uiComponente instanceof InputTextExt || uiComponente instanceof InputTextareaExt)
//                        ? "isReadonly" : "isDisabled";
//                Class clase = uiComponente.getClass();
//                Method getMetodo = clase.getMethod(getter);
//                return !((Boolean) getMetodo.invoke(uiComponente));
//            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            }
//        }
        return true;
    }

    /**
     * Setea el componente como no requerido.
     *
     * @param idComponente Identificador del componente
     * @param opcional Si es opcional o no.
     */
    public static void setOpcionalComponente(String idComponente, boolean opcional) {
        idComponente = "form_spj:" + idComponente;
        UIComponent uiComponente = JSFUtil.obtenerComponente(idComponente);
        if (uiComponente != null) {
            try {
                String setter = "setRequired";
                Class clase = uiComponente.getClass();
                Method setMetodo = clase.getMethod(setter, new Class[]{boolean.class});
                setMetodo.invoke(uiComponente, !opcional);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            }
        }
    }

    /**
     * Obtiene si el componente es opcional o no. Si esta deshabilitado se
     * coloca directamente que es opcional (ya que su edición no es válida).
     *
     * @param idComponente Identificador del componente
     * @return Si es opcional o no. <code>true</code> es * opcional,
     * <code>false</code> es requerido.
     */
    public static boolean isOpcional(String idComponente) {
        if (!isHabilitado(idComponente)) { //Si esta deshabilitado, entonces no es requerido.
            return true;
        } else {
            idComponente = "form_spj:" + idComponente;
            UIComponent uiComponente = JSFUtil.obtenerComponente(idComponente);
            if (uiComponente != null) {
                try {
                    String getter = "isRequired";
                    Class clase = uiComponente.getClass();
                    Method getMetodo = clase.getMethod(getter);
                    return !((Boolean) getMetodo.invoke(uiComponente));
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                }
            }
        }
        return true;
    }

    /**
     * Indica si el componente es válido o no. Para saber si es válido o no,
     * pregunta si es esta habilitado o no, en caso de estar deshabilitado
     * indica que si es valido. En caso de estar habilitado, pregunta si es
     * opcional o no. Si es opcional, no hay problema con su valor, por lo que,
     * retorna true. Si no es opcional, es necesario revisar su contenido, para
     * saber si se ha digitado algo o no. En caso de no tener nada digitado, se
     * retorna <code>false</code> para indicar que no es valido. En caso de
     * tener contenido el resultado es <code>true</code>.
     *
     * @param idComponente Identificador del componente
     * @return Si es válido o no.
     */
    public static boolean isValidoComponente(String idComponente) {
        boolean habilitado = isHabilitado(idComponente);
        if (!habilitado) {
            return true; //Si esta deshabilitado, no fue editable, por lo que 
            //debe de indicarse que es valido lo que este ahí.
        } else {
            boolean opcional = isOpcional(idComponente);
            if (opcional) {
                return true; //Es opcional, puede traer lo que quiera
            } else {//Si no es opcional, debe revisarse el contenido si trae algo.
                UIComponent uiComponente = JSFUtil.obtenerComponente("form_spj:" + idComponente);
                Object valor = null;
                if (uiComponente != null) {
                    try {
                        String getter = "getValue";
                        Class clase = uiComponente.getClass();
                        Method getMetodo = clase.getMethod(getter);
                        valor = getMetodo.invoke(uiComponente);
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        return true; // Si el componente no tiene value, ni modo, retornemos true.
                    }
                }
                //Se obtuvo value
                if (valor == null) { //Esta habilitado, no es opcional y tiene null, 
                    return false; //No es valido
                } else {//Si no es nulo
                    if (valor instanceof String) {//Si es String ""  o " "; 
                        String valorString = (String) valor;
                        if (valorString.trim().equals("")) {
                            return false;//no es valido
                        }
                    }
                    //Si esta habilitado, no es opcional y tiene datos, retorno true.
                    return true; //Es valido.
                }
            }
        }
    }

    public static Date obtenerFechaActual() {
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        return cal.getTime();
    }

    public static String obtenerConsecutivoSolicitud() {
        DateFormat formato = new SimpleDateFormat("yyyy-MMddHHmmssSSS");
        return formato.format(obtenerFechaActual());
    }
    
    public static String obtenerIdentificador() {
        DateFormat formato = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        return formato.format(obtenerFechaActual());
    }

    public static void limpiarFiltros(String nombre) {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('"+nombre+"').clearFilters()");
    }
    
    public static void cerrarModal(String nombreModal){
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('"+nombreModal+"').hide()");
    }
    public static void abrirModal(String nombreModal){
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('"+nombreModal+"').show()");
    }
    public static String obtenerParametrosPagina(String param){
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        Map<String,String> params = facesCtx.getExternalContext().getRequestParameterMap();
        return params.get(param);
    }
    
    /*public static void refrescarComponente(String nombreComponente){
        PrimeFaces.current().ajax().update(nombreComponente);
        
    }*/
    
}
