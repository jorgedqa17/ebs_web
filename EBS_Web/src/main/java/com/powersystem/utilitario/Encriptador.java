/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.utilitario;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Jorge GBSYS
 */
public class Encriptador {

    /**
     * Método que encripta con SHA256 una cadena específica
     *
     * @param cadena
     * @return - String con la cadena firmada
     * @throws NoSuchAlgorithmException
     */
    public static String encriptarCadena(String cadena) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // Las claves existentes se calcularon con el charset por defecto de Windows (windows-1252);
        // se fija explícito para que el hash no cambie según el sistema operativo.
        md.update(cadena.getBytes(Charset.forName("windows-1252")));

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        //convert the byte to hex format method 2
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    // ------------------------------------------------------------------------------------------------
    // Contraseñas: PBKDF2-HMAC-SHA256 con sal propia por usuario. Las claves antiguas (SHA-256 sin sal, 64 hex) se
    // siguen aceptando y se reemplazan por el formato nuevo en el primer ingreso correcto de cada usuario.
    // Formato guardado: pbkdf2_sha256$<iteraciones>$<sal base64>$<hash base64>
    // ------------------------------------------------------------------------------------------------
    private static final String PREFIJO_PBKDF2 = "pbkdf2_sha256$";
    private static final int ITERACIONES = 120000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    /**
     * Interruptor para volver al formato antiguo sin recompilar: -Debs.password.pbkdf2=false. Mientras esté en false no se
     * escriben claves nuevas en PBKDF2 (las que ya estén en PBKDF2 se siguen verificando bien).
     */
    public static boolean usarFormatoNuevo() {
        return !"false".equalsIgnoreCase(System.getProperty("ebs.password.pbkdf2", "true"));
    }

    /** Valor a guardar en la columna password para la clave dada, según el interruptor. */
    public static String claveParaGuardar(String clave) throws GeneralSecurityException {
        return usarFormatoNuevo() ? hashSeguro(clave) : encriptarCadena(clave);
    }

    public static String hashSeguro(String clave) throws GeneralSecurityException {
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] hash = pbkdf2(clave, sal, ITERACIONES);
        return PREFIJO_PBKDF2 + ITERACIONES + "$" + Base64.getEncoder().encodeToString(sal) + "$" + Base64.getEncoder().encodeToString(hash);
    }

    /** true si lo guardado todavía es el SHA-256 antiguo (hay que reemplazarlo al ingresar). */
    public static boolean esFormatoAntiguo(String almacenado) {
        return almacenado != null && !almacenado.trim().startsWith(PREFIJO_PBKDF2);
    }

    /** Compara la clave escrita contra lo guardado, sea PBKDF2 o el SHA-256 antiguo. Comparación en tiempo constante. */
    public static boolean verificar(String clave, String almacenado) throws GeneralSecurityException {
        if (clave == null || almacenado == null) {
            return false;
        }
        String guardado = almacenado.trim();
        if (!guardado.startsWith(PREFIJO_PBKDF2)) {
            return MessageDigest.isEqual(encriptarCadena(clave).getBytes(StandardCharsets.US_ASCII), guardado.getBytes(StandardCharsets.US_ASCII));
        }
        String[] partes = guardado.substring(PREFIJO_PBKDF2.length()).split("\\$");
        if (partes.length != 3) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            if (iteraciones < 1000 || iteraciones > 5000000 || sal.length < 8 || esperado.length < 16) {
                return false;
            }
            return MessageDigest.isEqual(esperado, pbkdf2(clave, sal, iteraciones, esperado.length * 8));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] pbkdf2(String clave, byte[] sal, int iteraciones) throws GeneralSecurityException {
        return pbkdf2(clave, sal, iteraciones, BITS_HASH);
    }

    private static byte[] pbkdf2(String clave, byte[] sal, int iteraciones, int bits) throws GeneralSecurityException {
        PBEKeySpec especificacion = new PBEKeySpec(clave.toCharArray(), sal, iteraciones, bits);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(especificacion).getEncoded();
        } finally {
            especificacion.clearPassword();
        }
    }

}
