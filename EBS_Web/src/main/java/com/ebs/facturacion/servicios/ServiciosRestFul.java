/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.servicios;

import com.ebs.modelos.FacturaImpresion;
import com.ebs.modelos.ObjetoFactura;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.primefaces.context.RequestContext;

/**
 *
 * @author jdquesad
 */
@Path("/documentos")
public class ServiciosRestFul {

    @Inject
    private ServicioFactura servicioFactura;

    @GET
    @Path("/lista/facturas")
    public Response obtenerListaColegios() throws Exception {

        return Response.ok(servicioFactura.obtenerFacturasEnvioCorreoElectronicoPDF()).build();
    }

    public static void enviarFactura(List<FacturaImpresion> factura) throws UnsupportedEncodingException, IOException {
        Gson gson = new Gson();
        ObjetoFactura objetoFactura = new ObjetoFactura();
        objetoFactura.setFactura(factura);
        String asciiEncodedString = new String(gson.toJson(objetoFactura).getBytes(), StandardCharsets.UTF_8);

        RequestContext.getCurrentInstance()
                .execute("callPrinter('" + asciiEncodedString + "');");

        //StringEntity stringEntity = new StringEntity(gson.toJson(objetoFactura), "UTF-8");
        //stringEntity.getContent().
        //String result = IOUtils.toString(null, StandardCharsets.UTF_8);
//        try {
//
//            HttpClient httpClient = new DefaultHttpClient();
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
//
//            HttpPost httpPost = new HttpPost("http://localhost:5000/printer/print");
//            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
//
//            ObjetoFactura objetoFactura = new ObjetoFactura();
//            objetoFactura.setFactura(factura);
//            Gson gson = new Gson();
//
//            //System.out.println(gson.toJson(objetoFactura));
//
//            StringEntity stringEntity = new StringEntity(gson.toJson(objetoFactura), "UTF-8");
//            httpPost.getRequestLine();
//            httpPost.setEntity(stringEntity);
//            HttpResponse response = httpClient.execute(httpPost);
//
//            Integer codigoRespuesta = response.getStatusLine().getStatusCode();
//            //System.out.println("Códido de Respuesta:" + codigoRespuesta);
//
//            String documentXML = EntityUtils.toString(response.getEntity());
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

}
