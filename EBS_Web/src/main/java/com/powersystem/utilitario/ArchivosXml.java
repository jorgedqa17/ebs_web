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

    public static final int FACTURA = 1;
    public static final int NOTA_CREDITO = 2;
    public static final int NOTA_DEBITO = 3;

    /** Fecha de la clave (ddMMyy en las posiciones 3 a 9, como arma la carpeta el servicio REST); null si no se puede leer. */
    public static Date fechaDeClave(String clave) {
        if (clave == null || clave.length() < 9) {
            return null;
        }
        try {
            return new SimpleDateFormat("ddMMyy").parse(clave.substring(3, 9));
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    /** Estado "enviada a Hacienda": la fila del historial que llevaba el XML firmado. */
    private static boolean filaConXmlFirmado(int tipo, Long estado) {
        return estado != null && ((tipo == FACTURA && estado == 6L) || (tipo == NOTA_CREDITO && estado == 10L) || (tipo == NOTA_DEBITO && estado == 39L));
    }

    /** Estados "aprobada" y "rechazada": las filas del historial que llevaban la respuesta de Hacienda. */
    private static boolean filaConRespuesta(int tipo, Long estado) {
        return estado != null && ((tipo == FACTURA && (estado == 3L || estado == 4L))
                || (tipo == NOTA_CREDITO && (estado == 11L || estado == 12L))
                || (tipo == NOTA_DEBITO && (estado == 40L || estado == 41L)));
    }

    /**
     * Completa una fila del historial con el XML guardado como archivo cuando la BD ya no lo trae, con el mismo formato
     * de siempre (XML firmado en base64 y respuesta de Hacienda en texto). Si la BD aun tiene el dato, lo conserva.
     *
     * @return {xml firmado en base64, respuesta de Hacienda}
     */
    public static String[] desdeArchivo(int tipo, Long estado, String rutaPadre, String clave, String consecutivo,
            String firmadoBd, String respuestaBd) {
        String firmado = firmadoBd == null ? "" : firmadoBd;
        String respuesta = respuestaBd == null ? "" : respuestaBd;
        if (consecutivo != null && rutaPadre != null) {
            try {
                if (firmado.trim().isEmpty() && filaConXmlFirmado(tipo, estado)) {
                    File f = buscar(rutaPadre, consecutivo, SUFIJO_FIRMADO, fechaDeClave(clave));
                    if (f != null) {
                        firmado = java.util.Base64.getEncoder().encodeToString(leer(f));
                    }
                }
                if (respuesta.trim().isEmpty() && filaConRespuesta(tipo, estado)) {
                    File f = buscar(rutaPadre, consecutivo, SUFIJO_RESPUESTA, fechaDeClave(clave));
                    if (f != null) {
                        respuesta = leerTexto(f);
                    }
                }
            } catch (IOException e) {
                // si no se puede leer el archivo, queda lo que traiga la BD
            }
        }
        return new String[]{firmado, respuesta};
    }

    private static void validarNombre(String nombre) throws IOException {
        if (nombre == null || !NOMBRE_SEGURO.matcher(nombre).matches()) {
            throw new IOException("Nombre de archivo no permitido: " + nombre);
        }
    }
}
