///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.powersystem.jobs;
//
//import com.ebs.constantes.enums.EstadoAnulacion;
//import com.ebs.entidades.AnulacionFactura;
//import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
//import com.ebs.facturacion.servicios.ServicioFactura;
//import com.ebs.hacienda.servicios.ServicioHacienda;
//import com.ebs.modelos.Emisor;
//import com.ebs.modelos.RespuestaHacienda;
//import com.powersystem.personas.servicios.ServicioPersona;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.logging.Logger;
//import javax.annotation.PostConstruct;
//import javax.ejb.Schedule;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//
///**
// *
// * @author Jorge GBSYS
// */
////@Stateless
//public class TimerJobAnulacionHaciendaEstado {
//
//    private final Logger log = Logger
//            .getLogger(TimerJobAnulacionHacienda.class.getName());
//    @Inject
//    private ServicioFactura servicioFactura;
//    @Inject
//    private ServicioPersona servicioPersona;
//    @Inject
//    private ServicioHacienda servicioHacienda;
//    private Emisor personaEmisor;
//    private boolean estaProcesoEnvio = false;
//
//    @PostConstruct
//
//    public void inicializar() {
//        personaEmisor = servicioPersona.obtenerPersonaEmisorJob();
//    }
//
//    //@Schedule(second = "*/30", minute = "*", hour = "*")
//    public void obtenerEstadoDocumentoAnulacion() {
//
//        try {
//            List<AnulacionFactura> listaFacturas = new ArrayList<>();//servicioFactura.obtenerFacturasAnuladasHacienda();// 
//
//            for (AnulacionFactura anulacionFactura : listaFacturas) {
//                try {
//                    RespuestaHacienda respuestaHacienda = servicioHacienda.obtenerEstadoDocumento(anulacionFactura.getClave());
//                    if (respuestaHacienda != null) {
//                        FacturaAnulacionHistoricoHacienda historico = new FacturaAnulacionHistoricoHacienda();
//                        historico.setId_anulacion(anulacionFactura.getId_anulacion());
//                        historico.setFecha(new Date());
//                        historico.setLogin("Proceso Automático");
//                        historico.setRespuesta(respuestaHacienda.getDetalle());
//                        historico.setDetallerespuesta(respuestaHacienda.getDetalleRespuesta());
//
//                        if (respuestaHacienda.getInd_estado() != null) {
//
//                            if (respuestaHacienda.getInd_estado().equals("rechazado")) {
//                                servicioFactura.actualizarAnulacion(anulacionFactura.getId_anulacion(), EstadoAnulacion.RECHAZADA_HACIENDA.getEstadoAnulacion());
//                                historico.setEstado_factura(EstadoAnulacion.RECHAZADA_HACIENDA.getEstadoAnulacion());
//
//                            } else if (respuestaHacienda.getInd_estado().equals("aceptado")) {
//                                historico.setEstado_factura(EstadoAnulacion.APROBADA_HACIENDA.getEstadoAnulacion());
//                                servicioFactura.actualizarAnulacion(anulacionFactura.getId_anulacion(), EstadoAnulacion.APROBADA_HACIENDA.getEstadoAnulacion());
//
//                            } else if (respuestaHacienda.getInd_estado().equals("error")) {
//                                historico.setEstado_factura(EstadoAnulacion.ERROR_HACIENDA.getEstadoAnulacion());
//                                servicioFactura.actualizarAnulacion(anulacionFactura.getId_anulacion(), EstadoAnulacion.ERROR_HACIENDA.getEstadoAnulacion());
//
//                            } else if (respuestaHacienda.getInd_estado().equals("procesando")) {
//                                historico.setEstado_factura(EstadoAnulacion.PROCESANDO_HACIENDA.getEstadoAnulacion());
//                                servicioFactura.actualizarAnulacion(anulacionFactura.getId_anulacion(), EstadoAnulacion.PROCESANDO_HACIENDA.getEstadoAnulacion());
//                            }
//                        }
//                        if (respuestaHacienda.getDetalle().contains("400")) {
//                            servicioFactura.actualizarAnulacion(anulacionFactura.getId_anulacion(), EstadoAnulacion.PENDIENTE_DE_ENVIO_HACIENDA.getEstadoAnulacion());
//                            historico.setEstado_factura(EstadoAnulacion.PENDIENTE_DE_ENVIO_HACIENDA.getEstadoAnulacion());
//                        }
//
//                        servicioFactura.guardarHistoricoAnulacion(historico);
//
//                    } else {
//                        System.out.println("El proceso está respondiendo un nulo para la anulación " + anulacionFactura.getId_anulacion());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//}
