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

    /**
     * La carpeta base casi nunca cambia y cada imagen del catálogo la pide: se recuerda 60 s en lugar de
     * consultar la BD por cada foto (un bloque de 20 productos son 20 consultas iguales).
     */
    private static final long VIGENCIA_RUTA_MS = 60_000L;
    private volatile String rutaBaseCache;
    private volatile long rutaBaseVence;

    private String rutaBase() {
        String ruta = rutaBaseCache;
        if (ruta == null || System.currentTimeMillis() > rutaBaseVence) {
            ruta = servicioParametro.obtenerValorParametro(ParametrosEnum.RUTA_IMAGENES_PRODUCTOS.getIdParametro()).getValor();
            rutaBaseCache = ruta;
            rutaBaseVence = System.currentTimeMillis() + VIGENCIA_RUTA_MS;
        }
        return ruta;
    }

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
        String rutaBase = rutaBase();
        boolean miniatura = "1".equals(request.getParameter("mini"));
        byte[] datos = miniatura
                ? ImagenesProducto.leerMiniatura(rutaBase, idProducto, archivo)
                : ImagenesProducto.leer(rutaBase, idProducto, archivo);
        if (datos == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // La miniatura siempre es JPEG, salvo que no se haya podido generar y se sirva el original png.
        boolean png = archivo.toLowerCase().endsWith(".png") && (!miniatura || esPng(datos));
        response.setContentType(png ? "image/png" : "image/jpeg");
        response.setContentLength(datos.length);
        // Cada archivo tiene nombre único (timestamp_uuid), así que su contenido no cambia: el navegador del
        // teléfono la guarda 30 días y no vuelve a pedirla (private: nunca en cachés compartidas).
        response.setHeader("Cache-Control", "private, max-age=2592000, immutable");
        response.getOutputStream().write(datos);
        response.getOutputStream().flush();
    }

    /** Firma de archivo PNG (0x89 'P' 'N' 'G'). */
    private static boolean esPng(byte[] datos) {
        return datos.length > 4 && (datos[0] & 0xFF) == 0x89 && datos[1] == 'P' && datos[2] == 'N' && datos[3] == 'G';
    }
}
