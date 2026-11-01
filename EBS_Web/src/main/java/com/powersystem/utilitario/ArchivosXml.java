package com.powersystem.utilitario;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Lectura y escritura de los XML de los comprobantes en la carpeta de documentos (Ruta padre).
 *
 * Misma estructura que el resto del sistema: {@code <ruta padre>/yy/mm/dd/<consecutivo><sufijo>}, donde
 * el sufijo es {@code _firmado.xml}, {@code _respuesta.xml} o {@code _recibido.xml}. Son XML en texto plano,
 * se pueden abrir directamente desde la carpeta.
 */
public final class ArchivosXml {

    public static final String SUFIJO_FIRMADO = "_firmado.xml";
    public static final String SUFIJO_RESPUESTA = "_respuesta.xml";
    public static final String SUFIJO_RECIBIDO = "_recibido.xml";

    /** Solo dígitos, letras, guion y punto: el nombre nunca puede salir de la carpeta del día. */
    private static final Pattern NOMBRE_SEGURO = Pattern.compile("^[0-9A-Za-z_.\\-]{1,120}$");

    private ArchivosXml() {
    }

    /** Carpeta del día: {@code <ruta padre>/yy/mm/dd}. */
    public static File carpeta(String rutaPadre, Date fecha) {
        Date f = fecha == null ? new Date() : fecha;
        return new File(rutaPadre + File.separator + new SimpleDateFormat("yy").format(f)
                + File.separator + new SimpleDateFormat("MM").format(f)
                + File.separator + new SimpleDateFormat("dd").format(f));
    }

    /**
     * Guarda el contenido de forma atómica (archivo temporal y renombrado en la misma carpeta).
     * Si ya existe un archivo idéntico no lo vuelve a escribir.
     */
    public static File guardar(String rutaPadre, Date fecha, String nombre, byte[] contenido) throws IOException {
        validarNombre(nombre);
        if (contenido == null || contenido.length == 0) {
            throw new IOException("No hay contenido para guardar en " + nombre);
        }
        File dir = carpeta(rutaPadre, fecha);
        if (!dir.isDirectory() && !dir.mkdirs() && !dir.isDirectory()) {
            throw new IOException("No se pudo crear la carpeta " + dir);
        }
        File destino = new File(dir, nombre);
        if (destino.isFile() && Arrays.equals(Files.readAllBytes(destino.toPath()), contenido)) {
            return destino;
        }
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
        return destino;
    }

    /**
     * Busca {@code <consecutivo><sufijo>} en la carpeta de cada fecha indicada y en el día anterior y el
     * siguiente (por si el proceso cruzó la medianoche). Devuelve null si no existe.
     */
    public static File buscar(String rutaPadre, String consecutivo, String sufijo, Date... fechas) {
        if (rutaPadre == null || consecutivo == null || consecutivo.isEmpty() || !NOMBRE_SEGURO.matcher(consecutivo).matches()) {
            return null;
        }
        Set<Date> candidatas = new LinkedHashSet<>();
        if (fechas != null) {
            for (Date f : fechas) {
                if (f != null) {
                    candidatas.add(f);
                }
            }
        }
        Set<String> probadas = new LinkedHashSet<>();
        for (int desplazamiento : new int[]{0, -1, 1}) {
            for (Date f : candidatas) {
                Calendar c = Calendar.getInstance();
                c.setTime(f);
                c.add(Calendar.DAY_OF_MONTH, desplazamiento);
                File archivo = new File(carpeta(rutaPadre, c.getTime()), consecutivo + sufijo);
                if (probadas.add(archivo.getPath()) && archivo.isFile()) {
                    return archivo;
                }
            }
        }
        return null;
    }

    public static byte[] leer(File archivo) throws IOException {
        return archivo == null ? null : Files.readAllBytes(archivo.toPath());
    }

    public static String leerTexto(File archivo) throws IOException {
        return archivo == null ? null : new String(Files.readAllBytes(archivo.toPath()), StandardCharsets.UTF_8);
    }

    private static void validarNombre(String nombre) throws IOException {
        if (nombre == null || !NOMBRE_SEGURO.matcher(nombre).matches()) {
            throw new IOException("Nombre de archivo no permitido: " + nombre);
        }
    }
}
