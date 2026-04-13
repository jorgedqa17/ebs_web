/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.jobs;

import com.google.gson.Gson;
import com.ebs.constantes.enums.EstadoPago;
import com.ebs.constantes.enums.RespuestaMensaje;
import com.ebs.constantes.enums.TipoDocumento;
import com.ebs.entidades.Pago;
import com.ebs.hacienda.servicios.ServicioHacienda;
import com.ebs.modelos.Emisor;
import com.ebs.modelos.FacturaElectronica;
import com.ebs.modelos.MensajeReceptor;
import com.ebs.modelos.RespuestaHacienda;
import com.esb.pago.servicios.ServicioPago;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.utilitario.Utilitario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Jorge GBSYS
 */
//@Stateless
public class TimerJobPagos {

    @Inject
    private ServicioPago servicioPago;
    @Inject
    private ServicioHacienda servicioHacienda;
    private Emisor personaEmisor;
    @Inject
    private ServicioPersona servicioPersona;

    @PostConstruct
    public void inicializar() {
        personaEmisor = servicioPersona.obtenerPersonaEmisorJob();
        //personaEmisor = servicioPersona.obtenerPersonaEmisorJobPruebas();
    }

    //@Schedule(minute = "*/1", hour = "*")
    public void obtenerPagos() {
        try {
            List<Pago> listaPagosPorProcesar = new ArrayList<>();// servicioPago.obtenerListaPagosPorProcesar();
            FacturaElectronica factura = null;
            MensajeReceptor mensajeReceptor = null;

            for (Pago pago : listaPagosPorProcesar) {
                factura = new FacturaElectronica();

                factura.setTipoDocumento(pago.getTipo_mensaje_respuesta());

                mensajeReceptor = new MensajeReceptor();

                mensajeReceptor.setNumeroCedulaEmisor(pago.getIdentificacion_proveedor());
                mensajeReceptor.setTipoIdentificacionEmisor(pago.getTipo_identificacion_proveedor());
                mensajeReceptor.setNumeroCedulaReceptor(personaEmisor.getIdentificacion().getNumeroCedula());
                mensajeReceptor.setTipoIdentificacionReceptor(personaEmisor.getIdentificacion().getTipo());
                mensajeReceptor.setClave(pago.getClave_comprobante_pago());
                mensajeReceptor.setMensaje(pago.getMensaje());
                mensajeReceptor.setDetalleMensaje(pago.getMensaje_detalle());
                mensajeReceptor.setMontoTotalImpuesto(pago.getMonto_impuestos());
                mensajeReceptor.setTotalFactura(pago.getMonto_total_comprobante());
                mensajeReceptor.setNumeroConsecutivoReceptor(pago.getNumero_consecutivo());

                String fecha = Utilitario.dateToRFC3339(new Date());
                mensajeReceptor.setFechaEmisionDoc(fecha);
                pago.setFecha_emision(fecha);
                servicioPago.actualizarPago(pago);

                factura.setMensajeReceptor(mensajeReceptor);

                RespuestaHacienda respuestaHacienda = servicioHacienda.enviarHacienda(factura);
//                if (respuestaHacienda != null) {
//
//                    pago.setRespuesta_hacienda(respuestaHacienda.getDetalle());
//                    pago.setDetalle_respuesta_hacienda(respuestaHacienda.getDetalleRespuesta());
//                    pago.setDocumento_xml_firmado(respuestaHacienda.getDocumento_xml_firmado());
//                    pago.setId_estado(EstadoPago.ENVIADO.getIdEstado().intValue());
//                    servicioPago.actualizarPago(pago);
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
