//package com.powersystem.jobs;
//
//import com.ebs.constantes.enums.EstadoFactura;
//import com.ebs.constantes.enums.TipoDocumento;
//import com.ebs.constantes.enums.TipoDocumentoReferenciaEnum;
//import com.ebs.entidades.FacturaHistoricoHacienda;
//import com.ebs.facturacion.servicios.ServicioFactura;
//import com.ebs.hacienda.servicios.ServicioHacienda;
//import com.ebs.modelos.DetalleServicio;
//import com.ebs.modelos.Emisor;
//import com.ebs.modelos.FacturaElectronica;
//import com.ebs.modelos.InformacionReferencia;
//import com.ebs.modelos.LineaDetalle;
//import com.ebs.modelos.Normativa;
//import com.ebs.modelos.Receptor;
//import com.ebs.modelos.RespuestaHacienda;
//import com.powersystem.personas.servicios.ServicioPersona;
//import com.powersystem.utilitario.Utilitario;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.logging.Logger;
//import javax.annotation.PostConstruct;
//
//import javax.ejb.Schedule;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//
////@Stateless
//public class TimerJobHacienda {
//
//    private final Logger log = Logger
//            .getLogger(TimerJobHacienda.class.getName());
//    @Inject
//    private ServicioFactura servicioFactura;
//    @Inject
//    private ServicioPersona servicioPersona;
//    @Inject
//    private ServicioHacienda servicioHacienda;
//    private Emisor personaEmisor;
//    private boolean estaEnviando = false;
//
//    @PostConstruct
//    public void inicializar() {
//        personaEmisor = servicioPersona.obtenerPersonaEmisorJob();
//        //personaEmisor = servicioPersona.obtenerPersonaEmisorJobPruebas();
//    }
//
//    //@Schedule(second = "*/20", minute = "*", hour = "*")
//    public void procesoEnvioHacienda() {
//
//        if (!estaEnviando) {
//
//            FacturaElectronica facturaEnviar = null;
//
//            try {
//                List<Object[]> listaFactura = new ArrayList<>();//servicioFactura.obtenerFacturaPorEnviarHacienda();//
//                for (Object[] factura : listaFactura) {
//                    this.estaEnviando = true;
//                    Long idFactura = Long.parseLong(factura[0].toString());
//                    //Construyo el objeto a enviar al restful de hacienda
//                    facturaEnviar = construirFacturaElectronica(idFactura, factura[1] == null ? null : Long.parseLong(factura[1].toString()), personaEmisor);
//                    //Envio a hacienda y espero la respuesta
//                    RespuestaHacienda respuestaHacienda = servicioHacienda.enviarHacienda(facturaEnviar);
//                    if (respuestaHacienda != null) {
//
//                        FacturaHistoricoHacienda historico = new FacturaHistoricoHacienda();
//                        historico.setId_factura(idFactura);
//                        historico.setFecha(new Date());
//                        historico.setLogin("Proceso Automático");
//                        historico.setRespuesta(respuestaHacienda.getDetalle());
//                        historico.setDetallerespuesta(respuestaHacienda.getDetalleRespuesta());
//                        historico.setEstado_factura(EstadoFactura.ENVIADA_HACIENDA.getEstadoFactura());
//                        historico.setDocumento_xml_firmado(respuestaHacienda.getDocumento_xml_firmado());
//                        servicioFactura.actualizarFactura(idFactura, EstadoFactura.ENVIADA_HACIENDA.getEstadoFactura());
//                        servicioFactura.guardarHistorico(historico);
//
//                    } else {
//                        guardarHistoricoErrorServicio(idFactura, respuestaHacienda);
//                    }
//
//                }
//                this.estaEnviando = false;
//            } catch (Exception e) {
//                this.estaEnviando = false;
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void guardarHistoricoErrorServicio(Long idFactura, RespuestaHacienda respuestaHacienda) {
//
//        FacturaHistoricoHacienda historico = new FacturaHistoricoHacienda();
//        historico.setId_factura(idFactura);
//        historico.setFecha(new Date());
//        historico.setLogin("Proceso Automático");
//        historico.setRespuesta(respuestaHacienda.getDetalle());
//        historico.setEstado_factura(EstadoFactura.ERROR_SERVICIO.getEstadoFactura());
//        servicioFactura.guardarHistorico(historico);
//    }
//
//    /**
//     * Método que construye un object FacturaElectronica para ser enviado a
//     * Hacienda y lo retorna
//     *
//     * @param idFactura - Long
//     * @param idCliente - Long
//     * @param personaEmisor -Emisor
//     * @return FacturaElectronica
//     * @throws Exception
//     */
//    public FacturaElectronica construirFacturaElectronica(Long idFactura, Long idCliente, Emisor personaEmisor) throws Exception {
//        FacturaElectronica facturaHacienda = null;
//        Receptor personaReceptor = null;
//        try {
//
//            facturaHacienda = servicioFactura.obtenerFactura(idFactura);
//
//            DetalleServicio detalle = new DetalleServicio();
//            List<LineaDetalle> listaDetalle = servicioFactura.obtenerDetalleFactura(idFactura);
//            detalle.setLineaDetalle(listaDetalle);
//            facturaHacienda.setDetalleServicio(detalle);
//
//            if (idCliente != null) {
//                personaReceptor = servicioPersona.obtenerPersonaReceptor(idCliente);
//            }
//            facturaHacienda.setPersonaEmisor(personaEmisor);
//            facturaHacienda.setPersonaReceptor(personaReceptor);
//
//            Normativa normativa = new Normativa();
//            normativa.setFechaResolucion("07-10-2016 08:00:00");
//            normativa.setNumeroResolucion("DGT-R-48-2016");
//            facturaHacienda.setNormativa(normativa);
//
//            facturaHacienda.setTipoDocumento(TipoDocumento.FACTURA_ELECTRONICA.getTipoDocumento());
//
//            String fecha = Utilitario.dateToRFC3339(new Date());
//            facturaHacienda.setFechaEmision(fecha);
//
//            servicioFactura.actualizarFactura(idFactura, EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura(), fecha);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ex;
//        }
//        return facturaHacienda;
//    }
//
//}
