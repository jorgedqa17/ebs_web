///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.powersystem.jobs;
//
//import com.ebs.constantes.enums.EstadoPago;
//import com.ebs.constantes.enums.Reportes;
//import com.ebs.constantes.enums.TiposMimeTypes;
//import com.ebs.entidades.Pago;
//import com.ebs.exception.ExcepcionManager;
//import com.ebs.hacienda.servicios.ServicioHacienda;
//import com.ebs.modelos.Emisor;
//import com.ebs.modelos.RespuestaHacienda;
//import com.powersystem.pago.servicios.ServicioPago;
//import com.powersystem.personas.servicios.ServicioPersona;
//import com.powersystem.servicio.reporte.ServicioReporte;
//import com.powersystem.utilitario.CorreoElectronico;
//import com.powersystem.utilitario.Utilitario;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import javax.annotation.PostConstruct;
//import javax.ejb.Schedule;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//import net.sf.jasperreports.engine.JRException;
//
///**
// *
// * @author hp i5 7300u
// */
////@Stateless
//public class TimerJobEstadoPago {
//
//    @Inject
//    private ServicioPago servicioPago;
//    @Inject
//    private ServicioHacienda servicioHacienda;
//    private Emisor personaEmisor;
//    @Inject
//    private ServicioPersona servicioPersona;
//    @Inject
//    private ServicioReporte servicioReporte;
//
//    @PostConstruct
//    public void inicializar() {
//        //personaEmisor = servicioPersona.obtenerPersonaEmisorJob();
//        //personaEmisor = servicioPersona.obtenerPersonaEmisorJobPruebas();
//    }
//
//    //@Schedule(second = "*/35", minute = "*", hour = "*")
//    public void obtenerEstado() {
//        try {
//            List<Pago> listaPago = new ArrayList<>();//servicioPago.obtenerListaPagosEnviados();//
//
//            for (Pago pago : listaPago) {
//                RespuestaHacienda respuestaHacienda = null;
//                respuestaHacienda = servicioHacienda.obtenerEstadoDocumento(pago.getClave_comprobante_pago() + "-" + pago.getNumero_consecutivo());
//
//                if (respuestaHacienda != null) {
//                    pago.setRespuesta_hacienda(respuestaHacienda.getDetalle());
//                    pago.setDetalle_respuesta_hacienda(respuestaHacienda.getDetalleRespuesta());
//
//                    if (respuestaHacienda.getInd_estado() != null) {
//                        if (respuestaHacienda.getInd_estado().equals("rechazado")) {
//                            pago.setId_estado(EstadoPago.RECHAZADO_HACIENDA.getIdEstado().intValue());
//                            //enviarCorreosElectronicos(pago);
//                        } else if (respuestaHacienda.getInd_estado().equals("aceptado")) {
//                            pago.setId_estado(EstadoPago.APROBADO_POR_HACIENDA.getIdEstado().intValue());
//                            //enviarCorreosElectronicos(pago);
//                        } else if (respuestaHacienda.getInd_estado().equals("error")) {
//                            pago.setId_estado(EstadoPago.ERROR_HACIENDA.getIdEstado().intValue());
//
//                        } else if (respuestaHacienda.getInd_estado().equals("recibido")) {
//                            pago.setId_estado(EstadoPago.ENVIADO.getIdEstado().intValue());
//
//                        } else if (respuestaHacienda.getInd_estado().equals("procesando")) {
//                            pago.setId_estado(EstadoPago.PROCESANDO_HACIENDA.getIdEstado().intValue());
//
//                        } else if (respuestaHacienda.getInd_estado().equals("Error Servicio")) {
//                            pago.setId_estado(EstadoPago.ERROR_DE_SERVICIO.getIdEstado().intValue());
//
//                        } else if (respuestaHacienda.getInd_estado().equals("error_servicio")) {
//                            pago.setId_estado(EstadoPago.ERROR_DE_SERVICIO.getIdEstado().intValue());
//                        }
//                        servicioPago.actualizarPago(pago);
//                        //enviarCorreosElectronicos(pago);
//
//                    }
//
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void enviarCorreosElectronicos(Pago pago) {
//        try {
//            CorreoElectronico correo = new CorreoElectronico();
//            List<String> listaCorreos = new ArrayList<>();
//
//            listaCorreos = new ArrayList<>();
//            listaCorreos.add(pago.getCorreo_electronico());
//            correo.sendEmailConfirmacion("Confirmación de Comprobante Electrónico, Respuesta Hacienda - ",
//                    "Sear Médica le adjunta la respuesta de Hacienda en relación a la confirmación del Comprobante Electrónico " + pago.getClave_comprobante_pago(),
//                    null,
//                    (Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS.getNombreReporte() + "-" + pago.getNumero_consecutivo() + "." + TiposMimeTypes.PDF.getExtension()),
//                    listaCorreos,
//                    (pago.getDocumento_xml_firmado() == null ? null : Utilitario.convertirBase64ABytes(pago.getDocumento_xml_firmado())),
//                    pago.getDetalle_respuesta_hacienda().getBytes(),
//                    pago.getNumero_consecutivo());
//
//        } catch (Exception ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        }
//    }
//
//    public byte[] generarReporteInfoConfirmacionComprobante(Pago pago) {
//        byte[] reporte = null;
//        try {
//            Map parametros = new HashMap<>();
//            parametros.put("idPago", pago.getId_pago());
//            reporte = servicioReporte.generarReporte(Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS, TiposMimeTypes.PDF, parametros, false);
//
//        } catch (JRException ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        }
//        return reporte;
//    }
//}
