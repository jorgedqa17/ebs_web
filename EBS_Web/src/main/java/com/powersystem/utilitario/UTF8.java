package com.powersystem.utilitario;

import java.io.UnsupportedEncodingException;

/**
 * Almacena código HTML que va a ser usado constantemente por el sistema.
 *
 * @author Consorcio Siansa-Indra.<br>
 * @Modificado por: Lsanchez 21/08/2014<br>
 * @Año:2014<br>
 *
 */
public class UTF8 {

    /**
     * No queremos que se pueda instanciar la clase
     *
     * @since /2009
     */
    private UTF8() {
    }

    /**
     * Convierte las tildes y Ñ a codificación UTF8 para los String UTF16. Todos
     * los caracteres de los String son codificados en UTF16, por lo que para su
     * despliegue, se requiere convertirlo a UTF8.
     *
     * @param original String
     * @return String con caracteres UTF8
     * @see java.lang.String
     */
    public static String aUTF8(String original) {
        original = original.replaceAll("á", "\u00E1");
        original = original.replaceAll("é", "\u00E9");
        original = original.replaceAll("í", "\u00ED");
        original = original.replaceAll("ó", "\u00F3");
        original = original.replaceAll("ú", "\u00FA");

        original = original.replaceAll("Á", "\u00C1");
        original = original.replaceAll("É", "\u00C9");
        original = original.replaceAll("Í", "\u00CD");
        original = original.replaceAll("Ó", "\u00D3");
        original = original.replaceAll("Ú", "\u00DA");

        original = original.replaceAll("ñ", "\u00D1");
        original = original.replaceAll("Ñ", "\u00F1");
        return original;
    } //aUTF8

    /**
     * Método encargado de codificar el string recibido como parámetro en
     * formato UTF-8.
     *
     * @param utf8String String a formatear
     * @return String formateado
     */
    public static String decodeUTF8(String utf8String) {
        String decode = "";
        try {
            decode = java.net.URLDecoder.decode(utf8String, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return decode;
    }

    /**
     * Método encargado de tomar un String en formato UTF8, y decodificarlo a
     * caracteres normales.
     *
     * @param string String codificado
     * @return String normal.
     */
    public static String encodeUTF8(String string) {
        String resultado = "";
        try {
            resultado = java.net.URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return resultado;
    }
}   //fin de la clase CodigoHTML
