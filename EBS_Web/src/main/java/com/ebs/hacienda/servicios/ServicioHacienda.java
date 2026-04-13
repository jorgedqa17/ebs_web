/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.hacienda.servicios;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.FacturaElectronica;
import com.ebs.modelos.RespuestaHacienda;
import com.powersystem.util.ServicioBase;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Jorge GBSYS
 */
@Slf4j
@Stateless
public class ServicioHacienda extends ServicioBase {

    /**
     * Método que envía la factura a hacienda
     *
     * @param factura
     * @return String con la respuesta de hacienda
     */
    public RespuestaHacienda enviarHacienda(FacturaElectronica factura) {

        RespuestaHacienda respuestaHacienda = null;

        try {

            HttpClient httpClient = new DefaultHttpClient();
//            SSLSocketFactory sf = null;
//            SSLContext sslContext = null;
//
//            sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, null, null);
//
//            sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            Scheme scheme = new Scheme("https", 443, sf);
//            httpClient.getConnectionManager().getSchemeRegistry().register(scheme);

            //HttpPost httpPost = new HttpPost("https://35.185.53.104:9999/ServiciosRestFul/services/hacienda");
            HttpPost httpPost = new HttpPost("http://localhost:8080/ServiciosRestFul/services/hacienda");
            httpPost.setHeader("Content-type", "application/json");

            Gson gson = new Gson();
            System.out.println(gson.toJson(factura));
            StringEntity stringEntity = new StringEntity(gson.toJson(factura));
            httpPost.getRequestLine();
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httpPost);

            Integer codigoRespuesta = response.getStatusLine().getStatusCode();
            System.out.println("Códido de Respuesta:" + codigoRespuesta);

            String documentXML = EntityUtils.toString(response.getEntity());

            respuestaHacienda = new RespuestaHacienda();
//            respuestaHacienda.setDocumento_xml_firmado(documentXML);
//            if (codigoRespuesta == 200 || codigoRespuesta == 202) {
//
//                respuestaHacienda.setClave("");
//                respuestaHacienda.setDetalle("Codigo de Respuesta:" + codigoRespuesta);
//                respuestaHacienda.setDetalleRespuesta("");
//
//                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//                respuestaHacienda.setFecha(df.format(Calendar.getInstance().getTime()));
//
//            } else {
//                respuestaHacienda = new RespuestaHacienda();
//                respuestaHacienda.setClave("Error Servicio");
//                respuestaHacienda.setDetalle("Error, Código de Respuesta:" + codigoRespuesta);
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.envio.hacienda",
                    "mensaje.error.envio.hacienda");
        }
        return respuestaHacienda;
    }

    public RespuestaHacienda obtenerEstadoDocumento(String clave) {
        RespuestaHacienda respuestaHacienda = null;
        String respuesta = "";
        Gson gson = new Gson();
        HttpResponse response = null;
        try {

            HttpClient httpClient = new DefaultHttpClient();
//            SSLSocketFactory sf = null;
//            SSLContext sslContext = null;
//
//            sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, null, null);
//
//            sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            Scheme scheme = new Scheme("https", 443, sf);
//            httpClient.getConnectionManager().getSchemeRegistry().register(scheme);

            HttpGet request = new HttpGet("http://localhost:8080/ServiciosRestFul/services/hacienda/?clave=" + clave);
            //HttpGet request = new HttpGet("https://35.185.53.104:9999/ServiciosRestFul/services/hacienda/?clave=" + clave);
            HttpParams params = new BasicHttpParams();
            params.setParameter("clave", clave);
            request.setParams(params);
            response = httpClient.execute(request);
            respuesta = EntityUtils.toString(response.getEntity());
            respuestaHacienda = new RespuestaHacienda();
//            if (response.getStatusLine().getStatusCode() == 417) {
//                respuestaHacienda.setClave(clave);
//                respuestaHacienda.setDetalle(respuesta);
//                respuestaHacienda.setInd_estado("Error Servicio");
//            } else {
//                if (!respuesta.equals("")) {
//                    try {
//                        respuestaHacienda = gson.fromJson(respuesta, RespuestaHacienda.class);
//                    } catch (IllegalStateException | JsonSyntaxException ex) {
//                        respuestaHacienda.setClave(clave);
//                        respuestaHacienda.setDetalle(ex.getMessage());
//                         respuestaHacienda.setDetalleRespuesta(ex.getMessage());
//                        respuestaHacienda.setInd_estado("error_servicio");
//                    }
//
//                } else {
//                    respuestaHacienda.setClave(clave);
//                    respuestaHacienda.setDetalle("El mensaje del servicio llegó vacio o nulo");
//                    respuestaHacienda.setInd_estado("error_servicio");
//                }
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.envio.hacienda",
                    "mensaje.error.envio.hacienda");
        }
        return respuestaHacienda;
    }
}
