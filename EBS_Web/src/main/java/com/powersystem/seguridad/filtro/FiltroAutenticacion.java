package com.powersystem.seguridad.filtro;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Exige una sesión iniciada para cualquier página o recurso de la aplicación. Antes solo la pantalla de login pedía
 * credenciales: las demás páginas respondían 200 sin sesión.
 *
 * "Sesión iniciada" significa que BeanLogin guardó el atributo "Usuario" (el mismo criterio de ImagenProductoServlet).
 * Sin sesión solo se permiten las páginas del flujo de ingreso, los recursos estáticos del tema y las páginas de error;
 * lo demás redirige a login.xhtml (o, en una petición AJAX de JSF, responde un partial-response con redirect para que
 * PrimeFaces cambie de página en vez de pintar la pantalla de login dentro de un fragmento).
 */
public class FiltroAutenticacion implements Filter {

    private static final String ATRIBUTO_SESION = "Usuario";

    /** Páginas visibles sin sesión (ruta dentro de la aplicación, ya normalizada a .xhtml). */
    private static final Set<String> PAGINAS_PUBLICAS = new HashSet<>(Arrays.asList(
            "/login.xhtml",
            "/xhtml/paginas/usuario/cambiarcontrasenna.xhtml",
            "/xhtml/paginas/sesion/expirosesion.xhtml",
            "/xhtml/paginas/error/error_500.xhtml",
            "/404.xhtml",
            "/error.xhtml"));

    /** Prefijos de recursos estáticos que usan las páginas públicas (CSS, JS, fuentes e imágenes del tema). */
    private static final String[] PREFIJOS_PUBLICOS = {
        "/javax.faces.resource/",
        "/resources/"
    };

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (esPublica(request) || haySesion(request)) {
            chain.doFilter(req, res);
            return;
        }

        String destino = request.getContextPath() + "/login.xhtml";
        if ("partial/ajax".equals(request.getHeader("Faces-Request"))) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/xml;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.getWriter().write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\""
                    + destino + "\"/></partial-response>");
            return;
        }
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.sendRedirect(destino);
    }

    private boolean haySesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion != null && sesion.getAttribute(ATRIBUTO_SESION) != null;
    }

    private boolean esPublica(HttpServletRequest request) {
        // getServletPath no incluye ";jsessionid=..." ni la parte de la consulta
        String ruta = request.getServletPath();
        if (request.getPathInfo() != null) {
            ruta += request.getPathInfo();
        }
        if (ruta == null || ruta.isEmpty() || "/".equals(ruta)) {
            return true; // la raíz muestra login.xhtml (welcome-file)
        }
        for (String prefijo : PREFIJOS_PUBLICOS) {
            if (ruta.startsWith(prefijo)) {
                return true;
            }
        }
        // la aplicación mapea *.xhtml y *.jsf a FacesServlet: ambas variantes son la misma página
        if (ruta.endsWith(".jsf")) {
            ruta = ruta.substring(0, ruta.length() - 4) + ".xhtml";
        }
        return PAGINAS_PUBLICAS.contains(ruta);
    }

    @Override
    public void destroy() {
    }
}
