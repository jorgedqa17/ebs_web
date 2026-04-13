//package com.powersystem.jobs;
//
//import com.ebs.constantes.enums.EstadoAnulacion;
//import com.ebs.constantes.enums.TipoDocumento;
//import com.ebs.constantes.enums.TipoDocumentoReferenciaEnum;
//import com.ebs.entidades.AnulacionFactura;
//import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
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
//public class TimerJobAnulacionHacienda {
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
//    //@Schedule(second = "*/20", minute = "*", hour = "*")
//    public void procesoEnvioHaciendaAnulaciones() {
//
//        if (!estaProcesoEnvio) {
//
//            FacturaElectronica facturaEnviar = null;
//            try {
//                List<AnulacionFactura> lista = new ArrayList<>();// servicioFactura.obtenerFacturasEnviosAnulacion();//
//
//                for (AnulacionFactura anulacionFactura : lista) {
//                    estaProcesoEnvio = true;
//                    facturaEnviar = construirNotaCredito(anulacionFactura, anulacionFactura.getId_factura(), personaEmisor);
//                    //Envio a hacienda y espero la respuesta
//                    RespuestaHacienda respuestaHacienda = servicioHacienda.enviarHacienda(facturaEnviar);
//
//                    if (respuestaHacienda != null) {
//                        FacturaAnulacionHistoricoHacienda historico = new FacturaAnulacionHistoricoHacienda();
//                        historico.setId_anulacion(anulacionFactura.getId_anulacion());
//                        historico.setFecha(new Date());
//                        historico.setLogin("Proceso Automático");
//                        historico.setRespuesta(respuestaHacienda.getDetalle());
//                        historico.setDetallerespuesta(respuestaHacienda.getDetalleRespuesta());
//                        historico.setDocumento_xml_firmado(respuestaHacienda.getDocumento_xml_firmado());
//                        historico.setEstado_factura(EstadoAnulacion.ENVIADA_HACIENDA.getEstadoAnulacion());
//                        servicioFactura.actualizarAnulacion(anulacionFactura.getId_anulacion(), EstadoAnulacion.ENVIADA_HACIENDA.getEstadoAnulacion());
//                        servicioFactura.guardarHistoricoAnulacion(historico);
//                    }
//                }
//                estaProcesoEnvio = false;
//            } catch (Exception e) {
//                estaProcesoEnvio = false;
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * Método que construye un object FacturaElectronica para ser enviado a
//     * Hacienda y lo retorna
//     *
//     * @param anulacion
//     * @param idFactura - Long
//     * @param personaEmisor -Emisor
//     * @return FacturaElectronica
//     * @throws Exception
//     */
//    public FacturaElectronica construirNotaCredito(AnulacionFactura anulacion, Long idFactura, Emisor personaEmisor) throws Exception {
//        FacturaElectronica facturaHacienda = null;
//        Receptor personaReceptor = null;
//        Long idCliente = null;
//        try {
//
//            idCliente = servicioFactura.obtenerIdClientePorFactura(idFactura);
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
//            facturaHacienda.setTipoDocumento(TipoDocumento.NOTA_DE_CREDITO_ELECTRONICA.getTipoDocumento());
//
//            Normativa normativa = new Normativa();
//            normativa.setFechaResolucion("07-10-2016 08:00:00");
//            normativa.setNumeroResolucion("DGT-R-48-2016");
//            facturaHacienda.setNormativa(normativa);
//
//            InformacionReferencia referencia = new InformacionReferencia();
//            referencia.setCodigo(TipoDocumentoReferenciaEnum.ANULA_DOCUMENTO_DE_REFERENCIA.getCodigo());
//            referencia.setTipoDoc(TipoDocumento.NOTA_DE_CREDITO_ELECTRONICA.getTipoDocumento());
//            referencia.setNumero(facturaHacienda.getNumeroConsecutivo().trim());
//            referencia.setFechaEmision(facturaHacienda.getFechaEmision());
//            referencia.setRazon(anulacion.getMotivo_anulacion().trim());
//            facturaHacienda.setReferencia(referencia);
//
//            facturaHacienda.setClave(anulacion.getClave().trim());
//            facturaHacienda.setNumeroConsecutivo(anulacion.getNumero_consecutivo().trim());
//            String fecha = Utilitario.dateToRFC3339(new Date());
//            facturaHacienda.setFechaEmision(fecha);
//
//            servicioFactura.actualizarAnulacion(anulacion.getId_anulacion(), EstadoAnulacion.PENDIENTE_DE_ENVIO_HACIENDA.getEstadoAnulacion(), fecha);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ex;
//        }
//        return facturaHacienda;
//    }
//}
