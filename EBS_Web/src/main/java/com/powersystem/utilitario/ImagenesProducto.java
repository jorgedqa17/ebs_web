package com.powersystem.utilitario;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import org.json.JSONArray;

/**
 * Lectura y escritura de las imágenes de productos en la carpeta base (parámetro RUTA_IMAGENES_PRODUCTOS).
 *
 * Cada producto tiene su propia subcarpeta, por id: {@code &lt;rutaBase&gt;/&lt;id_producto&gt;/&lt;archivo&gt;}
 * (p. ej. producto 1 → {@code .../1/1730592000000_a1b2c3d4.jpg}). En la BD (columna producto.ruta_imagen,
 * varchar) se guarda un arreglo JSON solo con los nombres de archivo (nunca la ruta completa), en el
 * orden en que se subieron — la subcarpeta se arma siempre a partir del id_producto, nunca se graba.
 */
public final class ImagenesProducto {

    public static final long TAMANO_MAXIMO = 2L * 1024 * 1024; // 2 MB
    private static final Pattern EXTENSION_PERMITIDA = Pattern.compile("^(jpg|jpeg|png)$");
    /** Solo dígitos, letras, guion y punto: el nombre nunca puede salir de la carpeta del producto. */
    private static final Pattern NOMBRE_SEGURO = Pattern.compile("^[0-9A-Za-z_.\\-]{1,120}$");

    private ImagenesProducto() {
    }

    /** Extensión en minúsculas sin el punto, o null si no es una extensión de imagen permitida. */
    public static String extensionPermitida(String nombreOriginal) {
        if (nombreOriginal == null) {
            return null;
        }
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto < 0 || punto == nombreOriginal.length() - 1) {
            return null;
        }
        String ext = nombreOriginal.substring(punto + 1).toLowerCase(Locale.ROOT);
        return EXTENSION_PERMITIDA.matcher(ext).matches() ? ext : null;
    }

    /** Carpeta del producto (se crea si no existe): {@code <rutaBase>/<id_producto>}. */
    private static File carpetaProducto(String rutaBase, Long idProducto) {
        return new File(new File(rutaBase), String.valueOf(idProducto));
    }

    /**
     * Guarda una imagen nueva del producto, en su propia subcarpeta, de forma atómica (archivo temporal
     * y renombrado en la misma carpeta), con un nombre único para no pisar las demás imágenes.
     *
     * @return el nombre de archivo guardado (p. ej. "1730592000000_a1b2c3d4.jpg"), para agregar al JSON
     *         de producto.ruta_imagen con {@link #agregarNombre}.
     */
    public static String guardar(String rutaBase, Long idProducto, String extension, byte[] contenido) throws IOException {
        if (contenido == null || contenido.length == 0) {
            throw new IOException("No hay contenido para guardar");
        }
        if (contenido.length > TAMANO_MAXIMO) {
            throw new IOException("La imagen supera el tamaño máximo permitido (2 MB)");
        }
        if (!EXTENSION_PERMITIDA.matcher(extension).matches()) {
            throw new IOException("Extensión no permitida: " + extension);
        }
        // currentTimeMillis no basta: dos archivos del mismo lote de subida (multiple="true") pueden
        // procesarse en el mismo milisegundo y pisarse entre sí. Se agrega un sufijo aleatorio corto.
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        String nombre = System.currentTimeMillis() + "_" + sufijo + "." + extension;
        validarNombre(nombre);
        File dir = carpetaProducto(rutaBase, idProducto);
        if (!dir.isDirectory() && !dir.mkdirs() && !dir.isDirectory()) {
            throw new IOException("No se pudo crear la carpeta " + dir);
        }
        File destino = new File(dir, nombre);
        File temporal = new File(dir, nombre + "." + System.nanoTime() + ".tmp");
        try {
            Files.write(temporal.toPath(), contenido);
            try {
                Files.move(temporal.toPath(), destino.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicoNoSoportado) {
                Files.move(temporal.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal.toPath());
        }
        return nombre;
    }

    /** Borra el archivo físico de una imagen, o no hace nada si el nombre no es válido o no existe. */
    public static void eliminar(String rutaBase, Long idProducto, String nombreArchivo) {
        if (rutaBase == null || idProducto == null || nombreArchivo == null || !NOMBRE_SEGURO.matcher(nombreArchivo).matches()) {
            return;
        }
        new File(carpetaProducto(rutaBase, idProducto), nombreArchivo).delete();
    }

    /** Lee los bytes de la imagen, o null si no existe o el nombre no es válido. */
    public static byte[] leer(String rutaBase, Long idProducto, String nombreArchivo) {
        if (rutaBase == null || idProducto == null || nombreArchivo == null || nombreArchivo.isEmpty()
                || !NOMBRE_SEGURO.matcher(nombreArchivo).matches()) {
            return null;
        }
        File archivo = new File(carpetaProducto(rutaBase, idProducto), nombreArchivo);
        if (!archivo.isFile()) {
            return null;
        }
        try {
            return Files.readAllBytes(archivo.toPath());
        } catch (IOException e) {
            return null;
        }
    }

    /** Parsea el JSON guardado en producto.ruta_imagen. Devuelve una lista vacía si es null/vacío/inválido. */
    public static List<String> listarNombres(String json) {
        List<String> lista = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) {
            return lista;
        }
        try {
            JSONArray arreglo = new JSONArray(json);
            for (int i = 0; i < arreglo.length(); i++) {
                lista.add(arreglo.getString(i));
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return lista;
    }

    /** Agrega un nombre de archivo al final del arreglo JSON y devuelve el JSON resultante. */
    public static String agregarNombre(String json, String nuevoNombre) {
        List<String> lista = listarNombres(json);
        lista.add(nuevoNombre);
        return serializar(lista);
    }

    /** Quita un nombre de archivo del arreglo JSON y devuelve el JSON resultante. */
    public static String quitarNombre(String json, String nombreABorrar) {
        List<String> lista = listarNombres(json);
        lista.remove(nombreABorrar);
        return serializar(lista);
    }

    private static String serializar(List<String> lista) {
        JSONArray arreglo = new JSONArray();
        for (String nombre : lista) {
            arreglo.put(nombre);
        }
        return arreglo.toString();
    }

    private static void validarNombre(String nombre) throws IOException {
        if (!NOMBRE_SEGURO.matcher(nombre).matches()) {
            throw new IOException("Nombre de archivo inválido: " + nombre);
        }
    }
}
