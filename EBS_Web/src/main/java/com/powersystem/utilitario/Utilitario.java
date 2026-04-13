/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.utilitario;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ebs.constantes.enums.Cadenas;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.constantes.enums.RespuestaMensaje;
import com.ebs.constantes.enums.Roles;
import com.ebs.constantes.enums.TiposCedulaMascaras;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.Parametro;
import com.ebs.entidades.Usuario;
import com.ebs.modelos.CorreoElectronicoPersonaHacienda;
import com.ebs.modelos.Emisor;
import com.ebs.modelos.ExoneracionUsuario;
import com.ebs.modelos.InfoExoneracion;
import com.ebs.modelos.MensajesHaciendaReceptor;
import com.powersystem.seguridad.modelos.RolesUsuario;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.ebs.modelos.PersonaHacienda;
import com.ebs.modelos.RespuestaHacienda;
import com.ebs.modelos.Token;
import com.google.gson.Gson;
import com.ebs.parametros.servicios.ServicioParametro;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Matcher;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 *
 * @author Jorge GBSYS
 */
public class Utilitario {

    private static DateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static NumberFormat doubleDigit = new DecimalFormat("00");

    @Inject
    private ServletContext context;

    private static final String[] UNIDADES = {"", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "};
    private static final String[] DECENAS = {"diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ",
        "diecisiete ", "dieciocho ", "diecinueve", "veinte ", "treinta ", "cuarenta ",
        "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa "};
    private static final String[] CENTENAS = {"", "ciento ", "doscientos ", "trecientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
        "setecientos ", "ochocientos ", "novecientos "};

    @Inject
    private ServicioParametro servicioParametro;

    public static String obtenerNombreCarpeta(String rutaPadre,
            String clave) {
        String fecha = "";
        String anno = "";
        String mes = "";
        String dia = "";

        if (clave != null) {
            fecha = clave.substring(3, 9);
            anno = fecha.substring(fecha.length() - 2);
            mes = fecha.substring(2, 4);
            dia = fecha.substring(0, 2);
        } else {
            DateFormat formatter = new SimpleDateFormat("ddMMyy");
            String hoy = formatter.format(new Date());
            anno = hoy.substring(hoy.length() - 2);
            mes = hoy.substring(2, 4);
            dia = hoy.substring(0, 2);
        }
        return rutaPadre + File.separator + anno + File.separator + mes + File.separator + dia;
    }

    public static byte[] obtenerArchivoXML(String rutaPadre, String clave, String numeroConsecutivo) throws IOException {
        byte[] retorno = null;
        String ruta = obtenerNombreCarpeta(rutaPadre, clave);
        File archivoRetorno = new File(ruta + File.separator + numeroConsecutivo + "_firmado.xml");
        if (archivoRetorno.exists()) {
            retorno = FileUtils.readFileToByteArray(archivoRetorno);
        }
        return retorno;
    }

    public static String convertirAJson(Object objeto) {
        if (objeto == null) {
            return "";
        }
        Gson transformacion = new Gson();
        return transformacion.toJson(objeto);
    }

    public static <T> List<T> convertirALista(String exoneraciones, Class<T> clazz) {
        if (exoneraciones == null || exoneraciones.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();

        // Crear un Type para List<T>
        Type type = TypeToken.getParameterized(List.class, clazz).getType();

        // Deserializar el JSON a una nueva lista del mismo tipo
        return gson.fromJson(exoneraciones, type);
    }

    public static String obtenerIpComputadora() {
        String resultado = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            resultado = address.getHostAddress();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return resultado;
    }

    public static Long obtenerIdBodegaUsuario() {
        return (Long) JSFUtil.obtenerDeSesion("idBodega");
    }

    public ExoneracionUsuario obtenerExoneracionDetalle(Parametro endPointExoneracionHacienda, String exoneracion) {
        HttpResponse response = null;
        String body = "";
        ExoneracionUsuario resultado = null;
        Gson transformacion = new Gson();
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(endPointExoneracionHacienda.getValor() + exoneracion);

            response = httpClient.execute(request);
            body = EntityUtils.toString(response.getEntity());
            resultado = transformacion.fromJson(body, ExoneracionUsuario.class);
            if (response.getStatusLine().getStatusCode() == 404) {
                resultado = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultado = null;
        }

        return resultado;
    }

    public InfoExoneracion obtenerExoneracion(Parametro endPointExoneracionHacienda, String exoneracion) {
        HttpResponse response = null;
        String body = "";
        InfoExoneracion resultado = null;
        Gson transformacion = new Gson();
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(endPointExoneracionHacienda.getValor() + exoneracion);

            response = httpClient.execute(request);
            body = EntityUtils.toString(response.getEntity());
            resultado = transformacion.fromJson(body, InfoExoneracion.class);
            if (response.getStatusLine().getStatusCode() == 404) {
                resultado = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultado = null;
        }

        return resultado;
    }

    public boolean esExoneracionValida(Parametro endPointExoneracionHacienda, String exoneracion) throws ParseException {

        HttpResponse response = null;
        String body = "";
        InfoExoneracion resultado = null;
        Gson transformacion = new Gson();
        boolean result = false;
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(endPointExoneracionHacienda.getValor() + exoneracion);

            response = httpClient.execute(request);
            body = EntityUtils.toString(response.getEntity());
            resultado = transformacion.fromJson(body, InfoExoneracion.class);
            if (response.getStatusLine().getStatusCode() == 404) {
                result = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result = false;
        }
        if (resultado != null) {
            if (resultado.getFechaVencimiento() != null) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date fechaVencimiento = format.parse(obtenerValorRegex(resultado.getFechaVencimiento(), "(.*)T.*", 1));

                Date hoy = new Date();

                if (fechaVencimiento.before(hoy)) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = false;
            }

        } else {
            result = false;
        }

        return result;
    }

    public String obtenerValorRegex(String cadena, String regex, int posicion) {
        String resultado = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher m;
        m = pattern.matcher(cadena);
        if (m.find()) {
            resultado = m.group(posicion);
        }
        return resultado;
    }

    public static String obtenerNombreMaquina() {
        String resultado = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            resultado = address.getHostName();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return resultado;
    }

    public static List<MensajesHaciendaReceptor> obtenerListaMensajesHaciendaReceptor() {
        List<MensajesHaciendaReceptor> resultado = new ArrayList<>();

        MensajesHaciendaReceptor mensaje = new MensajesHaciendaReceptor();

        mensaje.setCodigo(RespuestaMensaje.ACEPTADO.getCodigoRespuesta());
        mensaje.setNombre(RespuestaMensaje.ACEPTADO.getNombre());
        resultado.add(mensaje);

        mensaje = new MensajesHaciendaReceptor();
        mensaje.setCodigo(RespuestaMensaje.PARCIALMENTE_ACEPTADO.getCodigoRespuesta());
        mensaje.setNombre(RespuestaMensaje.PARCIALMENTE_ACEPTADO.getNombre());
        resultado.add(mensaje);

        mensaje = new MensajesHaciendaReceptor();
        mensaje.setCodigo(RespuestaMensaje.RECHAZADO.getCodigoRespuesta());
        mensaje.setNombre(RespuestaMensaje.RECHAZADO.getNombre());
        resultado.add(mensaje);

        return resultado;
    }

    public static byte[] convertirBase64ABytes(String base) {
        byte[] respuesta = null;
        Base64.Decoder decoder = Base64.getDecoder();
        respuesta = decoder.decode(base);
        return respuesta;
    }

    /**
     * Convierte una fecha date en el formato que solicita
     *
     * @param d - Date
     * @return String con la fecha según el formato solicitado
     */
    public static String dateToRFC3339(Date d) {
        StringBuilder result = new StringBuilder(rfc3339.format(d));
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.setTimeZone(TimeZone.getDefault());
        int offset_millis = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
        int offset_hours = Math.abs(offset_millis / (1000 * 60 * 60));
        int offset_minutes = Math.abs((offset_millis / (1000 * 60)) % 60);

        if (offset_millis == 0) {
            result.append("Z");
        } else {
            result
                    .append((offset_millis > 0) ? "+" : "-")
                    .append(doubleDigit.format(offset_hours))
                    .append(":")
                    .append(doubleDigit.format(offset_minutes));
        }
        return result.toString();
    }

    public static Date convertirStringToDate(String fecha) throws ParseException {
        //2019-08-21T20:34:31-06:00
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((fecha.substring(0, 19)).replace("T", " "));
    }

    public static String dateToRFC3339Text(String fecha) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(fecha);

        StringBuilder result = new StringBuilder(rfc3339.format(d));
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.setTimeZone(TimeZone.getDefault());
        int offset_millis = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
        int offset_hours = Math.abs(offset_millis / (1000 * 60 * 60));
        int offset_minutes = Math.abs((offset_millis / (1000 * 60)) % 60);

        if (offset_millis == 0) {
            result.append("Z");
        } else {
            result
                    .append((offset_millis > 0) ? "+" : "-")
                    .append(doubleDigit.format(offset_hours))
                    .append(":")
                    .append(doubleDigit.format(offset_minutes));
        }
        return result.toString();
    }

    public static boolean tieneUnRol(Long idRol) {
        boolean resultado = false;
        for (RolesUsuario rolesUsuario : obtenerRolesUsuario()) {
            if (rolesUsuario.getIdRol().equals(idRol)) {
                resultado = true;
            }
        }
        return resultado;
    }

    /**
     * Método que obtiene un tipo de identificacion
     *
     * @param idTipoIdentificacion - Long
     * @return String con la máscara correspondiente a cada tipo de cédula
     */
    public static String obtenerMascaraPorTipoIdentificacion(Long idTipoIdentificacion) {
        String resultado = "";
        if (idTipoIdentificacion.equals(TiposCedulaMascaras.CEDULA_FISICA.getIdTipoCedula())) {
            resultado = TiposCedulaMascaras.CEDULA_FISICA.getMascara();
        }
        if (idTipoIdentificacion.equals(TiposCedulaMascaras.CEDULA_JURIDICA.getIdTipoCedula())) {
            resultado = TiposCedulaMascaras.CEDULA_JURIDICA.getMascara();
        }
        if (idTipoIdentificacion.equals(TiposCedulaMascaras.DIMEX.getIdTipoCedula())) {
            resultado = TiposCedulaMascaras.DIMEX.getMascara();
        }
        if (idTipoIdentificacion.equals(TiposCedulaMascaras.NITE.getIdTipoCedula())) {
            resultado = TiposCedulaMascaras.NITE.getMascara();
        }
        return resultado;
    }

    /**
     * Método que agrega ceros al incio de la cadena
     *
     * @param valorv - String cadena
     * @param cantidadPosiciones - Integer, cantidad de caracteres que debería
     * de tener el string
     * @return String
     */
    public static String agregarCeros(String valor, Integer cantidadPosiciones) {
        String resultado = valor;
        if (valor.length() < cantidadPosiciones) {
            Integer cantidadCerosAgregar = cantidadPosiciones - valor.length();
            for (int i = 0; i < cantidadCerosAgregar; i++) {
                resultado = Cadenas.CERO.getCadena() + resultado;
            }
        }
        return resultado;
    }

    /**
     * Método que obtiene el emisor actual
     *
     * @return Emisor
     */
    public static Emisor obtenerEmisor() {
        return (Emisor) JSFUtil.obtenerDeSesion("personaEmisor");
    }

    /**
     * Método que obtiene el usuario
     *
     * @return Usuario
     */
    public static Usuario obtenerUsuario() {
        return (Usuario) JSFUtil.obtenerDeSesion("Usuario");
    }

    public static Bodega obtenerBodega() {
        return (Bodega) JSFUtil.obtenerDeSesion("bodega");
    }

    /**
     * Método que obtiene la lista de roles de la persona logueada
     *
     * @return List<RolesUsuario>
     */
    public static List<RolesUsuario> obtenerRolesUsuario() {
        return (List<RolesUsuario>) JSFUtil.obtenerDeSesion("rolesUsuario");
    }

    public static boolean validarRolUsuario(Roles rol) {
        boolean resultado = false;
        for (RolesUsuario rolesUsuario : Utilitario.obtenerRolesUsuario()) {
            if (rolesUsuario.getIdRol().equals(rol.getIdRol())) {
                resultado = true;
            }
        }
        return resultado;
    }

    public static boolean esAdministrador() {
        return (boolean) JSFUtil.obtenerDeSesion("administrador");
    }

    public static void cerrarConexion(Connection conection) throws SQLException {
        if (!conection.isClosed()) {
            conection.close();
        }
    }

    /**
     * Método que genera un QR Code para el reporte de factura
     *
     * @param texto
     * @return byte[]
     * @throws WriterException
     * @throws IOException
     */
    public static byte[] generarQRCode(String texto) throws WriterException, IOException {
        byte[] arreglo = null;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, 350, 350);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", out);
        arreglo = out.toByteArray();
        return arreglo;
    }

    public static Connection obtenerConexion() {
        Connection conection = null;
        try {
            Context context = new InitialContext();
            DataSource dt = (DataSource) context.lookup("jdbc/sear");
            conection = dt.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conection;
    }

    public static String Convertir(String numero, boolean mayusculas) {
        String literal = "";
        String parte_decimal;
        //si el numero utiliza (.) en lugar de (,) -> se reemplaza
        numero = numero.replace(".", ",");
        //si el numero no tiene parte decimal, se le agrega ,00
        if (numero.indexOf(",") == -1) {
            numero = numero + ",00";
        }
        //se valida formato de entrada -> 0,00 y 999 999 999,00
        if (Pattern.matches("\\d{1,9},\\d{1,3}", numero)) {
            //se divide el numero 0000000,00 -> entero y decimal
            String Num[] = numero.split(",");
            //de da formato al numero decimal
            parte_decimal = Num[1] + "/100 ";
            //se convierte el numero a literal
            if (Integer.parseInt(Num[0]) == 0) {//si el valor es cero
                literal = "cero ";
            } else if (Integer.parseInt(Num[0]) > 999999) {//si es millon
                literal = getMillones(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 999) {//si es miles
                literal = getMiles(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 99) {//si es centena
                literal = getCentenas(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 9) {//si es decena
                literal = getDecenas(Num[0]);
            } else {//sino unidades -> 9
                literal = getUnidades(Num[0]);
            }
            //devuelve el resultado en mayusculas o minusculas
            if (mayusculas) {
                return (literal + parte_decimal).toUpperCase();
            } else {
                return (literal + parte_decimal);
            }
        } else {//error, no se puede convertir
            return literal = null;
        }
    }

    /* funciones para convertir los numeros a literales */
    private static String getUnidades(String numero) {// 1 - 9
        //si tuviera algun 0 antes se lo quita -> 09 = 9 o 009=9
        String num = numero.substring(numero.length() - 1);
        return UNIDADES[Integer.parseInt(num)];
    }

    private static String getDecenas(String num) {// 99                        
        int n = Integer.parseInt(num);
        if (n < 10) {//para casos como -> 01 - 09
            return getUnidades(num);
        } else if (n > 19) {//para 20...99
            String u = getUnidades(num);
            if (u.equals("")) { //para 20,30,40,50,60,70,80,90
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8];
            } else {
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8] + "y " + u;
            }
        } else {//numeros entre 11 y 19
            return DECENAS[n - 10];
        }
    }

    private static String getCentenas(String num) {// 999 o 099
        if (Integer.parseInt(num) > 99) {//es centena
            if (Integer.parseInt(num) == 100) {//caso especial
                return " cien ";
            } else {
                return CENTENAS[Integer.parseInt(num.substring(0, 1))] + getDecenas(num.substring(1));
            }
        } else {//por Ej. 099 
            //se quita el 0 antes de convertir a decenas
            return getDecenas(Integer.parseInt(num) + "");
        }
    }

    private static String getMiles(String numero) {// 999 999
        //obtiene las centenas
        String c = numero.substring(numero.length() - 3);
        //obtiene los miles
        String m = numero.substring(0, numero.length() - 3);
        String n = "";
        //se comprueba que miles tenga valor entero
        if (Integer.parseInt(m) > 0) {
            n = getCentenas(m);
            return n + "mil " + getCentenas(c);
        } else {
            return "" + getCentenas(c);
        }

    }

    private static String getMillones(String numero) { //000 000 000        
        //se obtiene los miles
        String miles = numero.substring(numero.length() - 6);
        //se obtiene los millones
        String millon = numero.substring(0, numero.length() - 6);
        String n = "";
        if (millon.length() > 1) {
            n = getCentenas(millon) + "millones ";
        } else {
            n = getUnidades(millon) + "millon ";
        }
        return n + getMiles(miles);
    }

    public CorreoElectronicoPersonaHacienda obtenerCorreoElectronicoPersonaHacienda(String numeroCedula, String endPoint) {
        CorreoElectronicoPersonaHacienda resultado = null;
        HttpResponse response = null;
        String body = "";
        Gson transformacion = new Gson();
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(endPoint.replace("{identificacion}", numeroCedula));
            request.addHeader("access-user", "113940124");
            request.addHeader("access-token", "J3IyIyn5ztbZ7xqux5iJ");
            response = httpClient.execute(request);
            body = EntityUtils.toString(response.getEntity());
            resultado = transformacion.fromJson(body, CorreoElectronicoPersonaHacienda.class);

            if (resultado != null && (resultado.getEstado() != null && (resultado.getEstado().equals("404")))) {
                resultado = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultado;

    }

    public PersonaHacienda obtenerPersonaHacienda(String numeroCedula, String endPoint) {
        PersonaHacienda resultado = null;
        HttpResponse response = null;
        String body = "";
        Gson transformacion = new Gson();
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(String.format("%s%s", endPoint, numeroCedula));

            response = httpClient.execute(request);
            body = EntityUtils.toString(response.getEntity());
            resultado = transformacion.fromJson(body, PersonaHacienda.class);

            if (resultado != null && (resultado.getCode() != null && (resultado.getCode().equals("404")))) {
                resultado = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultado;

    }

    public Token obtenerTokenHacienda(String endPoint, String usuario, String contrasenna, boolean esDesarrollo) throws UnsupportedEncodingException, IOException, ParseException {

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost post = new HttpPost(endPoint);

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("username", usuario));
        params.add(new BasicNameValuePair("password", contrasenna));
        params.add(new BasicNameValuePair("client_id", esDesarrollo ? "api-prod" : "api-stag"));
        params.add(new BasicNameValuePair("client_secret", ""));
        params.add(new BasicNameValuePair("grant_type", "password"));
        //params.add(new BasicNameValuePair("scope", ""));

        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = httpclient.execute(post);

        String body = EntityUtils.toString(response.getEntity());

        Gson gson = new Gson();

        Token tokenResultado = gson.fromJson(body, Token.class);

        return tokenResultado;
    }

    public void cerrarSesion(String token, String endPoint, boolean esDesarrollo) throws IOException {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(endPoint);

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("client_id", esDesarrollo ? "api-stag" : "prod"));
        params.add(new BasicNameValuePair("refresh_token", token));
        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = httpclient.execute(post);

        for (Header allHeader : response.getAllHeaders()) {
            System.out.println(allHeader.getValue());
        }
        System.out.println(response.getStatusLine().getStatusCode());
    }

    /**
     * Método que obtiene el estado del documento de Hacienda
     *
     * @param token - String
     * @param clave - String
     * @return RespuestaHacienda
     * @throws IOException
     * @throws ParseException
     */
    public RespuestaHacienda obtenerEstadoDocumentoConfirmacion(Token token,
            String clave, String endPointEstadoDocumentos, boolean esDesarrollo,
            String endPointCerrarToken) throws IOException, ParseException, org.json.simple.parser.ParseException {

        RespuestaHacienda respuesta = null;
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(endPointEstadoDocumentos + clave);
        System.out.println("Consultando factura: " + clave);

        request.addHeader("Authorization", "bearer " + token.getAccess_token());
        HttpParams params = new BasicHttpParams();
        params.setParameter("clave", clave);
        request.setParams(params);
        System.out.println("---------------GET-------------------");
        HttpResponse response = client.execute(request);
        String body = EntityUtils.toString(response.getEntity());
        System.out.println(body);

        respuesta = new RespuestaHacienda();

        if (body.contains("Unauthorized")) {
            respuesta.setMensaje("Unauthorized");
            respuesta.setInd_estado("Unauthorized");
        } else if (body.equals("")) {

            for (Header header : response.getAllHeaders()) {
                if (header.getName().equals("X-Error-Cause")) {
                    System.out.println(header.getValue());
                    if (this.executeRegex(header.getValue(), "(El comprobante\\s+\\[\\d+\\]\\s+no ha sido recibido)")) {
                        respuesta.setMensaje("No Recibido");
                        respuesta.setInd_estado("No Recibido");
                        break;
                    }
                }
            }
        } else {
            JSONParser parser = new JSONParser();
            JSONObject jsonPadre = (JSONObject) parser.parse(body);

            if (jsonPadre.get("ind-estado") != null) {
                respuesta.setInd_estado((String) jsonPadre.get("ind-estado"));
            }
        }
        System.out.println("---------------GET-------------------");
        return respuesta;
    }

    private boolean executeRegex(String line, String regex) {
        boolean result = false;
        Pattern pattern = Pattern.compile(regex);
        Matcher m;
        m = pattern.matcher(line);
        while (m.find()) {
            result = true;
        }
        return result;
    }

    public static Long returnZeroLongIfNullOrEmpty(Object objeto) {
        try {
            if (objeto == null) {
                return 0L;
            } else if (StringUtils.isEmpty(objeto.toString())) {
                return 0L;
            }
            return Long.parseLong(objeto.toString());
        } catch (Exception e) {
            return 0l;
        }
    }

    public static BigDecimal returnZeroBigDecimalIfNullOrEmpty(Object objeto) {
        if (objeto == null) {
            return new BigDecimal("0.0");
        } else if (StringUtils.isEmpty(objeto.toString())) {
            return new BigDecimal("0.0");
        }
        return new BigDecimal(objeto.toString());
    }

    public static Integer returnZeroIntegerIfNullOrEmpty(Object objeto) {
        if (objeto == null) {
            return 0;
        } else if (StringUtils.isEmpty(objeto.toString())) {
            return 0;
        }
        return Integer.parseInt(objeto.toString());
    }

    public static String returnEmptyIfObjectNullOrEmpty(Object objeto) {
        if (objeto == null) {
            return "";
        } else if (StringUtils.isEmpty(objeto.toString())) {
            return "";
        }
        return objeto.toString();
    }
}
