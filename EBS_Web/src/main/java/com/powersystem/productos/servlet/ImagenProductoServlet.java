package com.powersystem.productos.servlet;

import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.parametros.servicios.ServicioParametro;
import com.powersystem.utilitario.ImagenesProducto;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Sirve los bytes de una imagen de producto directamente con la API de Servlet, sin pasar por el
 * manejador de contenido dinámico de PrimeFaces 6.2 (p:graphicImage con StreamedContent), que en esta
 * versión lanza NullPointerException al transmitir el stream (bug de la librería, no de este código:
 * reproducible incluso con un DefaultStreamedContent válido). Requiere sesión iniciada, igual que el
 * resto de la aplicación.
 */
@WebServlet("/imagenProducto")
public class ImagenProductoServlet extends HttpServlet {

    @Inject
    private ServicioParametro servicioParametro;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("Usuario") == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String archivo = request.getParameter("archivo");
        Long idProducto;
        try {
            idProducto = Long.valueOf(request.getParameter("producto"));
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String rutaBase = servicioParametro.obtenerValorParametro(ParametrosEnum.RUTA_IMAGENES_PRODUCTOS.getIdParametro()).getValor();
        byte[] datos = ImagenesProducto.leer(rutaBase, idProducto, archivo);
        if (datos == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(archivo.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg");
        response.setContentLength(datos.length);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.getOutputStream().write(datos);
        response.getOutputStream().flush();
    }
}
