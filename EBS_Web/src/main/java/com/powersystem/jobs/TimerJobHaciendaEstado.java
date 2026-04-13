///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.powersystem.jobs;
//
//import com.ebs.constantes.enums.EstadoFactura;
//import com.ebs.entidades.Factura;
//import com.ebs.entidades.FacturaHistoricoHacienda;
//import com.ebs.facturacion.servicios.ServicioFactura;
//import com.ebs.hacienda.servicios.ServicioHacienda;
//import com.ebs.modelos.RespuestaHacienda;
//import com.powersystem.personas.servicios.ServicioPersona;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.logging.Logger;
//import javax.ejb.Schedule;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//
///**
// *
// * @author Jorge GBSYS
// */
////@Stateless
//public class TimerJobHaciendaEstado {
//
//    private final Logger log = Logger
//            .getLogger(TimerJobHacienda.class.getName());
//    @Inject
//    private ServicioFactura servicioFactura;
//    @Inject
//    private ServicioPersona servicioPersona;
//    @Inject
//    private ServicioHacienda servicioHacienda;
//
//    private boolean estaEnviando = false;
//
//    //@Schedule(second = "*/35", minute = "*", hour = "*")
//    public void obtenerEstado() {
//        try {
//            List<Factura> listaFacturas = new ArrayList<>();//servicioFactura.obtenerFacturasEnviadasHacienda();//
//
//            for (Factura factura : listaFacturas) {
//                RespuestaHacienda respuestaHacienda = null;
//                try {
//                    respuestaHacienda = servicioHacienda.obtenerEstadoDocumento(factura.getClave());
//                    if (respuestaHacienda != null) {
//
//                        FacturaHistoricoHacienda historico = new FacturaHistoricoHacienda();
//                        historico.setId_factura(factura.getId_factura());
//                        historico.setFecha(new Date());
//                        historico.setLogin("Proceso Automático");
//                        historico.setRespuesta(respuestaHacienda.getDetalle());
//                        historico.setDetallerespuesta(respuestaHacienda.getDetalleRespuesta());
//
//                        if (respuestaHacienda.getInd_estado() != null) {
//                            if (respuestaHacienda.getInd_estado().equals("rechazado")) {
//                                servicioFactura.actualizarFactura(factura.getId_factura(), EstadoFactura.RECHAZADA_HACIENDA.getEstadoFactura());
//                                historico.setEstado_factura(EstadoFactura.RECHAZADA_HACIENDA.getEstadoFactura());
//
//                            } else if (respuestaHacienda.getInd_estado().equals("aceptado")) {
//                                historico.setEstado_factura(EstadoFactura.APROBADA_HACIENDA.getEstadoFactura());
//                                servicioFactura.actualizarFactura(factura.getId_factura(), EstadoFactura.APROBADA_HACIENDA.getEstadoFactura());
//
//                            } else if (respuestaHacienda.getInd_estado().equals("error")) {
//                                historico.setEstado_factura(EstadoFactura.ERROR_HACIENDA.getEstadoFactura());
//                                servicioFactura.actualizarFactura(factura.getId_factura(), EstadoFactura.ERROR_HACIENDA.getEstadoFactura());
//
//                            } else if (respuestaHacienda.getInd_estado().equals("recibido")) {
//                                //en este caso, hacienda responde un recibido, significa que el comprobante fue enviado pero no se encontró lo que implica
//                                //volver a enviarlo. Por esa razón, pongo la factura en pendiente de envío a hacienda
//                                historico.setEstado_factura(EstadoFactura.ENVIADA_HACIENDA.getEstadoFactura());
//                                servicioFactura.actualizarFactura(factura.getId_factura(), EstadoFactura.ENVIADA_HACIENDA.getEstadoFactura());
//
//                            } else if (respuestaHacienda.getInd_estado().equals("procesando")) {
//                                historico.setEstado_factura(EstadoFactura.PROCESANDO_HACIENDA.getEstadoFactura());
//                                servicioFactura.actualizarFactura(factura.getId_factura(), EstadoFactura.PROCESANDO_HACIENDA.getEstadoFactura());
//                            } else if (respuestaHacienda.getInd_estado().equals("Error Servicio")) {
//
//                                historico = guardarHistoricoErrorServicio(factura.getId_factura(), respuestaHacienda);
//                                servicioFactura.actualizarFactura(factura.getId_factura(), EstadoFactura.ERROR_SERVICIO.getEstadoFactura());
//
//                            } else if (respuestaHacienda.getInd_estado().equals("error_servicio")) {
//                                historico = guardarHistoricoErrorServicio(factura.getId_factura(), respuestaHacienda);
//                                servicioFactura.actualizarFactura(factura.getId_factura(), EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura());
//                            }
//                        } else if (!historico.getRespuesta().contains("202") || !historico.getRespuesta().contains("200")) {
//
//                            historico.setEstado_factura(EstadoFactura.ERROR_SERVICIO.getEstadoFactura());
//                            historico.setRespuesta(historico.getRespuesta() + " " + "Se determina que no se encontró el comprobante electrónico, se debe enviar de nuevo o bien se generó un error en el api de hacienda o en el servicio del sistema");
//                            servicioFactura.actualizarFactura(factura.getId_factura(), EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura());
//                        }
//                        servicioFactura.guardarHistorico(historico);
//
//                    } else {
//                        guardarHistoricoErrorServicio(factura.getId_factura(), respuestaHacienda);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public FacturaHistoricoHacienda guardarHistoricoErrorServicio(Long idFactura, RespuestaHacienda respuestaHacienda) {
//
//        FacturaHistoricoHacienda historico = new FacturaHistoricoHacienda();
//        historico.setId_factura(idFactura);
//        historico.setFecha(new Date());
//        historico.setLogin("Proceso Automático");
//        historico.setRespuesta(respuestaHacienda.getDetalle());
//        historico.setEstado_factura(EstadoFactura.ERROR_SERVICIO.getEstadoFactura());
//        return historico;
//    }
//}
