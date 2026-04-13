///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.powersystem.jobs;
//
//import com.ebs.constantes.enums.EstadoAnulacion;
//import com.ebs.constantes.enums.EstadoFactura;
//import com.ebs.constantes.enums.FacturaEnvioCorreos;
//import com.ebs.constantes.enums.Reportes;
//import com.ebs.constantes.enums.TiposMimeTypes;
//import com.ebs.entidades.AnulacionFactura;
//import com.ebs.entidades.Factura;
//import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
//import com.ebs.entidades.Persona;
//import com.ebs.exception.ExcepcionManager;
//import com.ebs.facturacion.servicios.ServicioFactura;
//import com.powersystem.personas.servicios.ServicioPersona;
//import com.powersystem.servicio.reporte.ServicioReporte;
//import com.powersystem.utilitario.CorreoElectronico;
//import com.powersystem.utilitario.Utilitario;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Logger;
//import javax.annotation.PostConstruct;
//import javax.ejb.Schedule;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//import net.sf.jasperreports.engine.JRException;
//
///**
// *
// * @author Jorge GBSYS
// */
////@Stateless
//public class TimerJobEmailNotasCredito {
//
//    private final Logger log = Logger.getLogger(TimerJobHacienda.class.getName());
//    @Inject
//    private ServicioFactura servicioFactura;
//    @Inject
//    private ServicioReporte servicioReporte;
//    @Inject
//    private ServicioPersona servicioPersona;
//
//    @PostConstruct
//    public void inicializar() {
//
//    }
//
//    //@Schedule(minute = "*/1", hour = "*")
//    public void enviarCorreoElectronicos() {
//        try {
//            CorreoElectronico correo = new CorreoElectronico();
//            List<AnulacionFactura> listaNotasCredito = new ArrayList<>();// servicioFactura.obtenerNotasCreditoPorEnviar();// 
//            List<String> listaCorreosEnviar = new ArrayList<>();
//            Persona persona = null;
//            String documentoXML = null;
//            String respuestaHacienda = null;
//            Factura facturaRelacionada = null;
//            boolean permiteEnvioFactura = false;
//
//            for (AnulacionFactura anulacionFactura : listaNotasCredito) {
//                listaCorreosEnviar = new ArrayList<>();
//                permiteEnvioFactura = false;
//
//                facturaRelacionada = servicioFactura.obtenerFacturaBusqueda(anulacionFactura.getId_factura());
//
//                List<FacturaAnulacionHistoricoHacienda> listaHistorico = servicioFactura.obtenerHistoricoHaciendaNotasCredito(anulacionFactura.getId_anulacion(),
//                        EstadoAnulacion.APROBADA_HACIENDA.getEstadoAnulacion().toString(),
//                        EstadoAnulacion.ERROR_HACIENDA.getEstadoAnulacion().toString(),
//                        EstadoAnulacion.PROCESANDO_HACIENDA.getEstadoAnulacion().toString(),
//                        EstadoAnulacion.RECHAZADA_HACIENDA.getEstadoAnulacion().toString(),
//                        EstadoAnulacion.ENVIADA_HACIENDA.getEstadoAnulacion().toString());
//
//                boolean encontroDocElectronico = false;
//                boolean encontroRespuesta = false;
//                for (FacturaAnulacionHistoricoHacienda facturaHistoricoHacienda : listaHistorico) {
//                    if (facturaHistoricoHacienda.getEstado_factura().equals(EstadoAnulacion.ENVIADA_HACIENDA.getEstadoAnulacion())) {
//                        if (!facturaHistoricoHacienda.getDocumento_xml_firmado().equals("")
//                                && facturaHistoricoHacienda.getDocumento_xml_firmado() != null
//                                && !permiteEnvioFactura) {
//                            documentoXML = facturaHistoricoHacienda.getDocumento_xml_firmado();
//                            try {
//                                Utilitario.convertirBase64ABytes(documentoXML);
//                                encontroDocElectronico = true;
//                                permiteEnvioFactura = true;
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                    if (facturaHistoricoHacienda.getEstado_factura().equals(EstadoAnulacion.APROBADA_HACIENDA.getEstadoAnulacion())
//                            || facturaHistoricoHacienda.getEstado_factura().equals(EstadoAnulacion.RECHAZADA_HACIENDA.getEstadoAnulacion())
//                            || facturaHistoricoHacienda.getEstado_factura().equals(EstadoAnulacion.ERROR_HACIENDA.getEstadoAnulacion())) {
//                        respuestaHacienda = facturaHistoricoHacienda.getDetallerespuesta();
//                        encontroRespuesta = true;
//                    }
//                    if (encontroDocElectronico && encontroRespuesta) {
//                        break;
//                    }
//                }
//                try {
//                    if (facturaRelacionada.getCorreo_electronico() != null) {
//                        if (!facturaRelacionada.getCorreo_electronico().trim().equals("")) {
//                            listaCorreosEnviar.add(facturaRelacionada.getCorreo_electronico());
//                        }
//                    }
//                    if (facturaRelacionada.getId_cliente() != null) {
//                        persona = servicioPersona.obtenerPersonaPorIdCliente(facturaRelacionada.getId_cliente());
//                        if (persona.getCorreo_electronico() != null) {
//                            listaCorreosEnviar.add(persona.getCorreo_electronico());
//                        }
//                    }
//
//                    if (listaCorreosEnviar != null) {
//                        if (listaCorreosEnviar.size() > 0 && permiteEnvioFactura) {
//                            correo.sendEmailNotaCredito(anulacionFactura.getNumero_consecutivo(),
//                                    "Estimado cliente, adjuntamos la notificación de la Nota de Crédito de la Factura " + facturaRelacionada.getNumero_consecutivo(),
//                                    generarReporteNotaCredito(anulacionFactura),
//                                    Reportes.REPORTE_NOTAS_CREDITO.getNombreReporte() + "-" + anulacionFactura.getNumero_consecutivo() + "." + TiposMimeTypes.PDF.getExtension(),
//                                    listaCorreosEnviar,
//                                    (documentoXML == null ? null : Utilitario.convertirBase64ABytes(documentoXML)),
//                                    respuestaHacienda.getBytes());
//                            anulacionFactura.setEnvio_correo_electronico(FacturaEnvioCorreos.ENVIO_CORREO.getEnvioCorreo());
//                            servicioFactura.actualizarNotaCredito(anulacionFactura);
//                        } else {
//                            System.out.println("No permite aun enviarse la nota de credito id:"+anulacionFactura.getId_anulacion());
//                        }
//                    } else {
//                        anulacionFactura.setEnvio_correo_electronico(FacturaEnvioCorreos.ENVIO_CORREO.getEnvioCorreo());
//                        servicioFactura.actualizarNotaCredito(anulacionFactura);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    /**
//     * Método que obtiene los byte[] del reporte
//     *
//     * @param factura
//     * @return byte[]
//     */
//    public byte[] generarReporteNotaCredito(AnulacionFactura anulacion) {
//        byte[] reporte = null;
//        try {
//            Map parametros = new HashMap<>();
//            parametros.put("idAnulacion", anulacion.getId_anulacion());
//            reporte = servicioReporte.generarReporte(Reportes.REPORTE_NOTAS_CREDITO, TiposMimeTypes.PDF, parametros, false);
//
//        } catch (JRException ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        }
//        return reporte;
//    }
//}
