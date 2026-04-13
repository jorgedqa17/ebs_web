///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.powersystem.jobs;
//
//import com.google.zxing.WriterException;
//import com.ebs.constantes.enums.EstadoFactura;
//import com.ebs.constantes.enums.FacturaEnvioCorreos;
//import com.ebs.constantes.enums.Reportes;
//import com.ebs.constantes.enums.TiposMimeTypes;
//import com.ebs.entidades.Factura;
//import com.ebs.entidades.FacturaHistoricoHacienda;
//import com.ebs.entidades.Persona;
//import com.ebs.exception.ExcepcionManager;
//import com.ebs.facturacion.servicios.ServicioFactura;
//import com.powersystem.personas.servicios.ServicioPersona;
//import com.powersystem.servicio.reporte.ServicioReporte;
//import com.powersystem.utilitario.CorreoElectronico;
//import com.powersystem.utilitario.Utilitario;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Logger;
//import javax.ejb.Schedule;
//import javax.ejb.Stateless;
//import javax.imageio.ImageIO;
//import javax.inject.Inject;
//import net.sf.jasperreports.engine.JRException;
//
///**
// *
// * @author hp i5 7300u
// */
////@Stateless
//public class JobEmailFactura {
//
//    private final Logger log = Logger.getLogger(TimerJobHacienda.class.getName());
//    @Inject
//    private ServicioFactura servicioFactura;
//    @Inject
//    private ServicioReporte servicioReporte;
//    @Inject
//    private ServicioPersona servicioPersona;
//
//    //@Schedule(minute = "*/1", hour = "*")
//    public void enviarCorreoElectronicos() {
//        try {
//            CorreoElectronico correo = new CorreoElectronico();
//            List<Factura> listaFacturas = new ArrayList<>();//servicioFactura.obtenerFacturasEnvioCorreoElectronicoPDF();//
//            List<String> listaCorreosEnviar = new ArrayList<>();
//            Persona persona = null;
//            boolean permiteEnvioFactura = false;
//            String documentoXML = "";
//
//            for (Factura facturaAEnviar : listaFacturas) {
//                listaCorreosEnviar = new ArrayList<>();
//                permiteEnvioFactura = false;
//
//                List<FacturaHistoricoHacienda> listaHistorico = servicioFactura.obtenerHistoricoFacturaHacienda(facturaAEnviar.getId_factura(),
//                        EstadoFactura.APROBADA_HACIENDA.getEstadoFactura().toString(),
//                        EstadoFactura.ERROR_HACIENDA.getEstadoFactura().toString(),
//                        EstadoFactura.PROCESANDO_HACIENDA.getEstadoFactura().toString(),
//                        EstadoFactura.RECHAZADA_HACIENDA.getEstadoFactura().toString(),
//                        EstadoFactura.ENVIADA_HACIENDA.getEstadoFactura().toString());
//
//                for (FacturaHistoricoHacienda facturaHistoricoHacienda : listaHistorico) {
//                    if (facturaHistoricoHacienda.getEstado_factura().equals(EstadoFactura.ENVIADA_HACIENDA.getEstadoFactura())) {
//                        if (!facturaHistoricoHacienda.getDocumento_xml_firmado().equals("")
//                                && facturaHistoricoHacienda.getDocumento_xml_firmado() != null
//                                && !permiteEnvioFactura) {
//                            documentoXML = facturaHistoricoHacienda.getDocumento_xml_firmado();
//                            try {
//                                Utilitario.convertirBase64ABytes(documentoXML);
//                                permiteEnvioFactura = true;
//                                break;
//                            } catch (Exception e) {
//                                //e.printStackTrace();
//                            }
//                        }
//
//                    }
//
//                }
//
//                try {
//                    if (facturaAEnviar.getCorreo_electronico() != null) {
//                        if (!facturaAEnviar.getCorreo_electronico().trim().equals("")) {
//                            listaCorreosEnviar.add(facturaAEnviar.getCorreo_electronico());
//                        }
//                    }
//                    if (facturaAEnviar.getId_cliente() != null) {
//                        persona = servicioPersona.obtenerPersonaPorIdCliente(facturaAEnviar.getId_cliente());
//                        if (persona.getCorreo_electronico() != null) {
//                            listaCorreosEnviar.add(persona.getCorreo_electronico());
//                        }
//
//                    }
//                    if (permiteEnvioFactura) {
//                        if (listaCorreosEnviar.size() > 0) {
//                            correo.sendEmailFacturaPDF(facturaAEnviar.getNumero_consecutivo(),
//                                    "Estimado cliente, adjuntamos su factura. SearMedica le agradece su compra.",
//                                    generarReporteFactura(facturaAEnviar),
//                                    Reportes.FACTURA.getNombreReporte() + "-" + facturaAEnviar.getNumero_consecutivo() + "." + TiposMimeTypes.PDF.getExtension(),
//                                    listaCorreosEnviar,
//                                    (documentoXML == null ? null : Utilitario.convertirBase64ABytes(documentoXML)),
//                                    null);
//                        }
//                        facturaAEnviar.setEnvio_correo_electronico(FacturaEnvioCorreos.ENVIO_CORREO.getEnvioCorreo());
//                        servicioFactura.actualizarFactura(facturaAEnviar);
//                    } else {
//                        System.out.println("NO permite envio aun para " + facturaAEnviar.getNumero_consecutivo());
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
//    public byte[] generarReporteFactura(Factura factura) {
//        byte[] reporte = null;
//        try {
//            Map parametros = new HashMap<>();
//            parametros.put("idFactura", factura.getId_factura());
//            parametros.put("simbolo", "CRC");
//            parametros.put("montoLetras", Utilitario.Convertir(factura.getTotal_comprobante().toString(), true));
//            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode("https://35.185.53.104:9999/ServiciosRestFul/services/comprobanteElectronico?consecutivo=" + factura.getNumero_consecutivo()))));
//            reporte = servicioReporte.generarReporte(Reportes.FACTURA, TiposMimeTypes.PDF, parametros, false);
//
//        } catch (JRException | IOException ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        } catch (WriterException ex) {
//            ExcepcionManager.manejarExcepcion(ex);
//        }
//        return reporte;
//    }
//
//}
