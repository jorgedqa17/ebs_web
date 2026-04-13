package com.powersystem.utilitario;

import com.ebs.exception.ExcepcionManager;
import com.powersystem.seguridad.bean.InformacionUsuario;
import java.lang.reflect.Method;
import java.util.*;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase Utilitaria creada, para el manejo de las sesiones de la aplicación.
 *
 * @author Adam M. Gamboa González, Steven Barba
 * @since 17 de Febrero de 2012
 *
 *
 * @author Consorcio Siansa-Indra.<br>
 * @Modificado por: Lsanchez 21/08/2014<br>
 * @version 2.0 <br>
 * @Año:2014<br>
 * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
 * @version 1.0.2
 * @date 11/07/2017 14:17
 */
@Slf4j
public class SesionUtil {

    public static final String ATRIBUTO_SESION_INFO_USUARIO = "INFO_USUARIO";
    private static final String REDIRECT = "/xhtml/logout.xhtml";

    public enum VariablesSesion {

        INFO_USUARIO,
        HORA_INICIO,
        HORA_FINAL;
    }

    /**
     * Obtiene la clase que implementa HttpSession.
     *
     * @param httpSession Objeto HttpSession actual
     * @return Clase que implementa HttpSession
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.2
     * @date 11/07/2017 14:17
     */
    private static Class getSessionData(HttpSession httpSession) {
        return httpSession.getClass();
    }

    /**
     * Obtiene el contexto de la clase que implementa HttpSession.
     *
     * @param httpSession Objeto HttpSession
     * @return Clase que implementa el contexto de HttpSession
     * @throws Exception Si ocurre algún problema con Reflection obteniendo el
     * contexto
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.2
     * @date 11/07/2017 14:17
     */
    private static Class getSessionContext(HttpSession httpSession) throws Exception {
        Method metodo = httpSession.getClass().getMethod("getContext");
        Object resultado = metodo.invoke(httpSession);
        return resultado.getClass();
    }

    /**
     * Remueve una variable de la sesión.
     *
     * @param llave Llave del objeto a eliminar.
     */
    public static void removerDeSesion(VariablesSesion llave) {
        JSFUtil.removerDeSesion(llave.toString());
    }

    /**
     * Método para setear/guardar variables en sesión.
     *
     * @param llave Identificador/llave del objeto a guardar
     * @param objeto Objeto a guardar
     */
    public static void guardarEnSesion(VariablesSesion llave, Object objeto) {
        JSFUtil.guardarEnSesion(llave.toString(), objeto);
    }

    /**
     * Método para obtener variables de sesión por JSF. Si un objeto no existe
     * en sesión, entonces retorna null.
     *
     * @param llave Llave de objeto en sesión
     * @return Objeto obtenido.
     */
    public static Object obtenerDeSesion(VariablesSesion llave) {
        return JSFUtil.obtenerDeSesion(llave.toString());
    }

    //**************************************************************************
    //****************************HTTP SESSION**********************************
    //**************************************************************************
    /**
     * Método encargado de verificar que el acceso a la aplicación sea exitoso,
     * verificado que no se repitan sesión de un mismo usuario en diferentes
     * computadoras o dispositivos
     *
     * @param facesContext Recibe el contexto de la aplicación actual.
     * @param current Recibe las credenciales del usuario que esta haciendo la
     * identificación en la aplicación
     * @return Retorna {@code EstadosLogin.EXITOSO} si no se encontraron errores
     * y {@code EstadosLogin.WARN_SESSION_ACTIVA} si el usuario ya se encuentra
     * identificado en otra computadora o dispositivo
     * @throws Exception Lanza la {@code Exception} si ocurre un error no
     * controlado
     */
//    public static EstadoSesion accesoSolicitud(FacesContext facesContext, InformacionUsuario current) throws Exception {
//        List<SessionWrapper> listaUsuarioSession = obtenerSesionesAbiertas(facesContext);
//        for (SessionWrapper usuario : listaUsuarioSession) {
//            if (usuario.getUsuario().equals(current.getUsuario()) && usuario.isAutentificado()) {
////                SesionUtil.recobrarSesionRemota(FacesContext.getCurrentInstance(), current.getUsuario());
////                TODO HERMAN esto se comentó para el WAR de QA en GBSYS, para registro no se debe usar
////                return EstadoSesion.WARN_SESSION_ACTIVA;//Ya existe una sesión activa de este usuario
//                return EstadoSesion.EXITOSA;//Ya existe una sesión activa de este usuario
//            }
//        }
//        return EstadoSesion.EXITOSA;//Exitosa
//    }
    /**
     * Método que se encarga de invalidar la sesión del usuario actual
     *
     * @param facesContext
     * @throws Exception Lanza una {@code Exception} si no logra obtener la
     * sesión.
     */
    public static void salirAplicacion(FacesContext facesContext) throws Exception {
        HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(false);
        httpSession.invalidate();
    }

    /**
     * Método que se encarga de invalidar la sesión del usuario actual y de
     * redireccionar al usuario
     *
     * @param facesContext
     * @throws Exception
     */
    public static void salirAplicacionDirecto(FacesContext facesContext) throws Exception {
        facesContext.getExternalContext().invalidateSession();
        facesContext.getExternalContext().redirect(facesContext.getExternalContext().getRequestContextPath() + REDIRECT);
    }

    /**
     * Método que se encarga de invalidar la sesión del usuario actual y de
     * redireccionar al usuario
     *
     * @param facesContext
     * @param informacionUsuario
     * @throws Exception
     */
    public static void salirAplicacionForce(FacesContext facesContext, InformacionUsuario informacionUsuario) throws Exception {
        //   killSessionRFLCT(FacesContext.getCurrentInstance(), informacionUsuario);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
    }

    /**
     * Método que se encarga de invalidar la sesión del usuario actual y de
     * redireccionar al usuario
     *
     * @param facesContext
     * @throws Exception
     */
    public static void limpiarSesion(FacesContext facesContext, InformacionUsuario informacionUsuario) throws Exception {
        //  killSessionRFLCT(FacesContext.getCurrentInstance(), informacionUsuario);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
    }

    /**
     * Método que se encarga de invalidar la sesión del usuario actual y de
     * redireccionar al usuario
     *
     * @param facesContext
     * @throws Exception
     */
    public static void redireccionarLogout(FacesContext facesContext) throws Exception {
        facesContext.getExternalContext().redirect(facesContext.getExternalContext().getRequestContextPath() + REDIRECT);
    }

    /**
     * Obtiene las sesiones abiertas en el servidor
     *
     * @param facesContext Recibe como parámetro el contexto de la aplicación
     * @return Retorna una lista de usuarios que se encuentran identificados en
     * la aplicación.
     */
//    public static List<SessionWrapper> obtenerSesionesAbiertas(FacesContext facesContext) {
//        Long lastRequestTime;
//        SessionWrapper sessionWrapper;
//        List<SessionWrapper> listaSesionesAbiertas = new ArrayList<>();
//        try {
//            HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(true);
//            Class memorySessionData = getSessionData(httpSession);
//            Class memorySessionContext = getSessionContext(httpSession);
//
//            Method getOpenSessions = memorySessionContext.getMethod("getOpenSessions");
//            Hashtable<String, Object> sessionAbiertas = (Hashtable<String, Object>) getOpenSessions.invoke(httpSession.getSessionContext());
//
//            Iterator it = sessionAbiertas.entrySet().iterator();
//
//            while (it.hasNext()) {
//                Map.Entry ent = (Map.Entry) it.next();
//                if (memorySessionData.isInstance(ent.getValue())) {
//                    Method getLastAccessedTime = memorySessionData.getMethod("getLastAccessedTime");
//                    lastRequestTime = (new Date().getTime() - (Long) getLastAccessedTime.invoke(ent.getValue())) / 1000;
//                    Method getAttribute = memorySessionData.getMethod("getAttribute", String.class);
//                    if (getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO) instanceof InformacionUsuario) {
//                        sessionWrapper = (SessionWrapper) getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO);
//                        if (sessionWrapper != null) {
//                            log.info("***** Usuario: {} *****", sessionWrapper.getUsuario());
//                            if (sessionWrapper.getUsuario() == null) {
//                                sessionWrapper.setUsuario("<anonymous>");
//                            }
//                            log.info("USUARIO QUE SE CONECTA {}", sessionWrapper.getUsuario());
//                            sessionWrapper.setTiempoUltimaSolicitud(lastRequestTime);
//                            listaSesionesAbiertas.add(sessionWrapper);
//                        }
//                    }
//                }
//            }
//            return listaSesionesAbiertas;
//        } catch (Exception ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        }
//        return listaSesionesAbiertas;
//    }
    /**
     * Obtiene las sesiones abiertas en el servidor para un mismo usuario.
     *
     * @param facesContext Recibe como parámetro el contexto de la aplicación
     * @return Retorna una lista de usuarios que se encuentran identificados en
     * la aplicación.
     */
    public static List<SessionWrapper> obtenerSesionesAbiertas(FacesContext facesContext, String usuario) {
        SessionWrapper sessionWrapper;

        List<SessionWrapper> listaSesionesAbiertas = new ArrayList<>();
        try {
            HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(true);
            Class memorySessionData = getSessionData(httpSession);
            Class memorySessionContext = getSessionContext(httpSession);

            Method getOpenSessions = memorySessionContext.getMethod("getOpenSessions");
            Hashtable<String, Object> sessionAbiertas = (Hashtable<String, Object>) getOpenSessions.invoke(httpSession.getSessionContext());

            Iterator it = sessionAbiertas.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry ent = (Map.Entry) it.next();
                if (memorySessionData.isInstance(ent.getValue())) {
                    Method getAttribute = memorySessionData.getMethod("getAttribute", String.class);
                    if (getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO) instanceof InformacionUsuario) {
                        sessionWrapper = (SessionWrapper) getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO);
                        if (sessionWrapper != null) {
                            log.info("***** Usuario: {} *****", sessionWrapper.getUsuario());
                            if (sessionWrapper.getUsuario() != null && sessionWrapper.getUsuario().equals(usuario)) {
                                listaSesionesAbiertas.add(sessionWrapper);
                            }
                        }
                    }
                }
            }
            return listaSesionesAbiertas;
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return listaSesionesAbiertas;
    }

    /**
     * Se encarga de matar la sesión abierta del usuario.
     *
     * @param facesContext FacesContext de la aplicación
     * @param identidad Usuario al que hay que sacar de sesión
     */
    /*public static void killSessionRFLCT(FacesContext facesContext, InformacionUsuario informacionUsuario) {
        try {
            HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(true);
            Class memorySessionData = getSessionData(httpSession);
            Class memorySessionContext = getSessionContext(httpSession);

            SessionWrapper sessionWrapper;

            List<Object> killSessionsList = new ArrayList<>();

            Method getOpenSessions = memorySessionContext.getMethod("getOpenSessions");
            Hashtable<String, Object> sessionAbiertas = (Hashtable<String, Object>) getOpenSessions.invoke(httpSession.getSessionContext());

            Iterator it = sessionAbiertas.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry ent = (Map.Entry) it.next();

                if (memorySessionData.isInstance(ent.getValue())) {
                    Method getAttribute = memorySessionData.getMethod("getAttribute", String.class);
                    if (getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO) instanceof InformacionUsuario) {
                        
                        sessionWrapper = (SessionWrapper) getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO);
                        
                        if (sessionWrapper != null) {
                            
                            if (sessionWrapper.getUsuario().toUpperCase(Locale.getDefault()).equals(informacionUsuario.().toUpperCase(Locale.getDefault()))
                                    && sessionWrapper.getIp().equals(informacionUsuario.getIp())
                                    && sessionWrapper.getBrowser().toUpperCase(Locale.getDefault()).equals(informacionUsuario.getBrowser().toUpperCase(Locale.getDefault()))) {
                                
                                killSessionsList.add(ent.getValue());
                                
                                break;
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < killSessionsList.size(); i++) {
                Object sessionOpen = killSessionsList.get(i);
                Method invalidate = memorySessionData.getMethod("invalidate");
                limpiarSession((HttpSession) sessionOpen);
                invalidate.invoke(sessionOpen);
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }*/
    /**
     * Se encarga de matar la sesión abierta del usuario, a traves el usuario y
     * la ip de las terminal.
     *
     * @param facesContext FacesContext de la aplicación
     * @param usuario Usuario al que hay que sacar de session
     */
//    public static void killSessionRFLCT(FacesContext facesContext, String usuario, String ipTerminal) {
//        try {
//            HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(true);
//            Class memorySessionData = getSessionData(httpSession);
//            Class memorySessionContext = getSessionContext(httpSession);
//
//            SessionWrapper sessionWrapper;
//
//            List<Object> killSessionsList = new ArrayList<>();
//
//            Method getOpenSessions = memorySessionContext.getMethod("getOpenSessions");
//            Hashtable<String, Object> sessionAbiertas = (Hashtable<String, Object>) getOpenSessions.invoke(httpSession.getSessionContext());
//
//            Iterator it = sessionAbiertas.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry ent = (Map.Entry) it.next();
//
//                if (memorySessionData.isInstance(ent.getValue())) {
//                    Method getAttribute = memorySessionData.getMethod("getAttribute", String.class);
//                    if (getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO) instanceof InformacionUsuario) {
//                        sessionWrapper = (SessionWrapper) getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO);
//                        if (sessionWrapper != null) {
//                            if (sessionWrapper.getUsuario().toUpperCase(Locale.getDefault()).equals(usuario.toUpperCase(Locale.getDefault())) && sessionWrapper.getIp().equals(ipTerminal)) {
//                                killSessionsList.add(ent.getValue());
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            for (int i = 0; i < killSessionsList.size(); i++) {
//                Object sessionOpen = killSessionsList.get(i);
//                Method invalidate = memorySessionData.getMethod("invalidate");
//                limpiarSession((HttpSession) sessionOpen);
//                invalidate.invoke(sessionOpen);
//            }
//        } catch (Exception ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        }
//    }
    /**
     * Se encarga de matar la sesión abierta del usuario, a traves el usuario y
     * la ip de las terminal.
     *
     * @param facesContext FacesContext de la aplicación
     * @param usuario Usuario al que hay que sacar de session
     */
//    public static void killSessionRFLCT(FacesContext facesContext, String usuario, String ipTerminal, String browser) {
//        try {
//            String userAgent;
//            HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(true);
//            Class memorySessionData = getSessionData(httpSession);
//            Class memorySessionContext = getSessionContext(httpSession);
//
//            SessionWrapper sessionWrapper;
//
//            List<Object> killSessionsList = new ArrayList<>();
//
//            Method getOpenSessions = memorySessionContext.getMethod("getOpenSessions");
//            Hashtable<String, Object> sessionAbiertas = (Hashtable<String, Object>) getOpenSessions.invoke(httpSession.getSessionContext());
//
//            Iterator it = sessionAbiertas.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry ent = (Map.Entry) it.next();
//
//                if (memorySessionData.isInstance(ent.getValue())) {
//                    Method getAttribute = memorySessionData.getMethod("getAttribute", String.class);
//                    if (getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO) instanceof InformacionUsuario) {
//                        sessionWrapper = (SessionWrapper) getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO);
//                        if (sessionWrapper != null) {
//                            if (sessionWrapper.getUsuario().toUpperCase(Locale.getDefault()).equals(usuario.toUpperCase(Locale.getDefault())) && sessionWrapper.getIp().equals(ipTerminal) && sessionWrapper.getBrowser().equals(browser)) {
//                                killSessionsList.add(ent.getValue());
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            for (int i = 0; i < killSessionsList.size(); i++) {
//                Object sessionOpen = killSessionsList.get(i);
//                Method invalidate = memorySessionData.getMethod("invalidate");
//                limpiarSession((HttpSession) sessionOpen);
//                invalidate.invoke(sessionOpen);
//            }
//        } catch (Exception ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        }
//    }
//    public static void recobrarSesionRemota(FacesContext facesContext, String usuario) {
//        try {
//            HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(true);
//            Class memorySessionData = getSessionData(httpSession);
//            Class memorySessionContext = getSessionContext(httpSession);
//
//            SessionWrapper sessionWrapper;
//
//            HttpSession sesionRemota = null;
//
//            Method getOpenSessions = memorySessionContext.getMethod("getOpenSessions");
//            Hashtable<String, Object> sessionAbiertas = (Hashtable<String, Object>) getOpenSessions.invoke(httpSession.getSessionContext());
//
//            Iterator it = sessionAbiertas.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry ent = (Map.Entry) it.next();
//
//                if (memorySessionData.isInstance(ent.getValue())) {
//                    Method getAttribute = memorySessionData.getMethod("getAttribute", String.class);
//                    if (getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO) instanceof InformacionUsuario) {
//                        sessionWrapper = (SessionWrapper) getAttribute.invoke(ent.getValue(), ATRIBUTO_SESION_INFO_USUARIO);
//                        if (sessionWrapper != null) {
//                            if (sessionWrapper.getUsuario().toUpperCase(Locale.getDefault()).equals(usuario.toUpperCase(Locale.getDefault()))) {
//                                sesionRemota = (HttpSession) ent.getValue();
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            if (sesionRemota != null) {
//                for (Enumeration<String> e = sesionRemota.getAttributeNames(); e.hasMoreElements();) {
//                    String elemento = e.nextElement();
//                    if (elemento.contains(ATRIBUTO_SESION_INFO_USUARIO)) {
//                        httpSession.putValue(ATRIBUTO_SESION_INFO_USUARIO, sesionRemota.getAttribute(ATRIBUTO_SESION_INFO_USUARIO));
//                    }
//                    if (elemento.contains(ATRIBUTO_SESION_IDENTIDAD_CLASS)) {
//                        httpSession.putValue(ATRIBUTO_SESION_IDENTIDAD, sesionRemota.getAttribute(elemento));
//                    }
//                    if (elemento.contains(ATRIBUTO_SESION_MENU_CLASS)) {
//                        httpSession.putValue(ATRIBUTO_SESION_MENU, sesionRemota.getAttribute(elemento));
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        }
//    }
    /**
     * Se encarga de verificar que el usuario tenga una sesión valida para
     * navegar en la aplicación, de no tenerla lo redirecciona a la pagina de
     * indicada.
     *
     * @param facesContext Es el contexto de la aplicación cliente
     * @return Retorna true si la sesión es correcta
     */
//    public static EstadoSesion getEstadoMiSesion(FacesContext facesContext) {
//        try {
//            SessionWrapper sessionWrapper;
//            HttpSession httpSession = (HttpSession) facesContext.getExternalContext().getSession(false);
//            if (httpSession != null) {
//                if (httpSession.getAttribute(ATRIBUTO_SESION_INFO_USUARIO) != null) {
//                    sessionWrapper = (SessionWrapper) httpSession.getAttribute(ATRIBUTO_SESION_INFO_USUARIO);
//                    if (sessionWrapper != null) {
//                        if (!sessionWrapper.isAutentificado()) {
//                            return EstadoSesion.ERROR_SESSION_INVALIDA;
//                        }
//                    } else {
//                        return EstadoSesion.ERROR_SESSION_INVALIDA;
//                    }
//                } else {
//                    return EstadoSesion.ERROR_SESSION_INVALIDA;
//                }
//            } else {
//                return EstadoSesion.ERROR_SESSION_INVALIDA;
//            }
//        } catch (IllegalArgumentException | SecurityException ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//            return EstadoSesion.ERROR_SESSION;
//        }
//        return EstadoSesion.EXITOSA;
//    }
    private static void limpiarSession(HttpSession httpSession) {
        String elemento;
        List<String> listaElementos = new ArrayList<>();
        for (Enumeration<String> e = httpSession.getAttributeNames(); e.hasMoreElements();) {
            elemento = e.nextElement();
            //if (elemento.contains(ATRIBUTO_SESION_INFO_USUARIO)) {
            listaElementos.add(elemento);
            //}
//            if (elemento.contains(ATRIBUTO_SESION_IDENTIDAD_CLASS)) {
//                listaElementos.add(elemento);
//            }
//            if (elemento.contains(ATRIBUTO_SESION_MENU_CLASS)) {
//                listaElementos.add(elemento);
//            }
        }
        for (String elementoEliminar : listaElementos) {
            httpSession.removeAttribute(elementoEliminar);
        }
    }

    public static String obtenerBrowser(FacesContext facesContext) {
        String browser;
        String userAgent;
        userAgent = ((HttpServletRequest) facesContext.getExternalContext().getRequest()).getHeader("User-Agent");
        if (userAgent.contains("Chrome")) {
            browser = "Chrome";
        } else if (userAgent.contains("Firefox")) {
            browser = "Firefox";
        } else if (userAgent.contains("MSIE")) {
            browser = "Internet Explorer";
        } else if (userAgent.contains("Opera")) {
            browser = "Opera";
        } else if (userAgent.contains("Safari")) {
            browser = "Safari";
        } else {
            browser = "Desconocido";
        }
        return browser;
    }
}
