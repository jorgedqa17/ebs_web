/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.pago.bean;

import com.ebs.constantes.enums.EstadoPago;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.RespuestaMensaje;
import com.ebs.constantes.enums.TipoDocumento;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Pago;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.EstadosPagosModelo;
import com.ebs.modelos.MensajesHaciendaReceptor;
import com.esb.pago.servicios.ServicioPago;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.CorreoElectronico;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Data;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author hp i5 7300u
 */
@ManagedBean
@ViewScoped
@Data
public class BeanAdministracionPagos {

    @Getter
    @Setter
    private Pago pago;
    @Getter
    @Setter
    private Pago pagoSeleccionado;
    @Getter
    @Setter
    private List<MensajesHaciendaReceptor> listaMensajeRecepcion;
    @Getter
    @Setter
    private List<MensajesHaciendaReceptor> listaMensajeRecepcionSeleccionado;
    @Getter
    @Setter
    private List<Pago> listaPagoResultadoBusqueda;
    @Getter
    @Setter
    private List<Pago> listaPagoResultadoBusquedaFiltro;
    @Getter
    @Setter
    private List<EstadosPagosModelo> listaEstadosPagos;
    @Getter
    @Setter
    private List<EstadosPagosModelo> listaEstadosPagosSeleccionado;
    @Inject
    private ServicioReporte servicioReporte;
    @Inject
    private ServicioPago servicioPago;

    private StreamedContent reporteDesplegar = null;
    private StreamedContent reporteDesplegarCompElectronico = null;
    private StreamedContent reporteDesplegarRespHacienda = null;
    private byte[] reporteConfirmacion;

    public BeanAdministracionPagos() {
    }

    @PostConstruct
    public void inicializar() {
        pago = new Pago();
        pagoSeleccionado = new Pago();
        listaMensajeRecepcion = Utilitario.obtenerListaMensajesHaciendaReceptor();
        listaMensajeRecepcionSeleccionado = listaMensajeRecepcion;
        listaEstadosPagos = servicioPago.obtenerEstadosPagos();
        listaEstadosPagosSeleccionado = listaEstadosPagos;
        //servicioPago.llenarcamposnombrePagos();
    }

    public void consultarPagos(ActionEvent evt) {
        try {
            listaPagoResultadoBusqueda = servicioPago.obtenerPagosConsulta(pago);
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void consultarPagoSeleccionado(Pago pagoSeleccionadoTabla) {
        try {
            pagoSeleccionado = servicioPago.obtenerPagoPorId(pagoSeleccionadoTabla.getId_pago());
            pagoSeleccionado.setDescripcionEstadoPago(pagoSeleccionadoTabla.getDescripcionEstadoPago());
            pagoSeleccionado.setDescripcionRespuesta(pagoSeleccionadoTabla.getMensaje());

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void reenviarCOnfirmacion() {
        try {
            String tipoDocumento = "";
            Pago nuevoPago = new Pago();

            nuevoPago.setId_estado(EstadoPago.PENDIENTE_DE_ENVIO.getIdEstado().intValue());

            if (pagoSeleccionado.getMensaje().equals(RespuestaMensaje.ACEPTADO.getCodigoRespuesta().toString())) {
                tipoDocumento = TipoDocumento.CONFIRMACION_DE_ACEPTACION_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                nuevoPago.setMensaje(RespuestaMensaje.ACEPTADO.getCodigoRespuesta().toString());
            } else if (pagoSeleccionado.getMensaje().equals(RespuestaMensaje.PARCIALMENTE_ACEPTADO.getCodigoRespuesta().toString())) {
                tipoDocumento = TipoDocumento.CONFIRMACION_DE_ACEPTACION_PARCIAL_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                nuevoPago.setMensaje(RespuestaMensaje.PARCIALMENTE_ACEPTADO.getCodigoRespuesta().toString());
            } else if (pagoSeleccionado.getMensaje().equals(RespuestaMensaje.RECHAZADO.getCodigoRespuesta().toString())) {
                tipoDocumento = TipoDocumento.CONFIRMACION_DE_RECHAZO_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                nuevoPago.setMensaje(RespuestaMensaje.RECHAZADO.getCodigoRespuesta().toString());
            }

            nuevoPago.setMensaje_detalle(pagoSeleccionado.getMensaje_detalle());
            nuevoPago.setNumero_consecutivo(servicioPago.construirNumeroConsecutivo(tipoDocumento));
            nuevoPago.setClave_comprobante_pago(pagoSeleccionado.getClave_comprobante_pago());
            nuevoPago.setIdentificacion_proveedor(pagoSeleccionado.getIdentificacion_proveedor());
            nuevoPago.setTipo_identificacion_proveedor(pagoSeleccionado.getTipo_identificacion_proveedor());
            nuevoPago.setTipo_mensaje_respuesta(tipoDocumento);
            nuevoPago.setMensaje(pagoSeleccionado.getMensaje());

            if (pagoSeleccionado.getMonto_impuestos() != null) {
                if (!pagoSeleccionado.getMonto_impuestos().equals(new BigDecimal("0.0"))) {
                    nuevoPago.setMonto_impuestos(pagoSeleccionado.getMonto_impuestos().setScale(3, BigDecimal.ROUND_HALF_EVEN));
                }
            }

            nuevoPago.setMonto_total_comprobante(pagoSeleccionado.getMonto_total_comprobante().setScale(3, BigDecimal.ROUND_HALF_EVEN));
            nuevoPago.setCorreo_electronico(pagoSeleccionado.getCorreo_electronico());
            nuevoPago.setId_pago_referencia(pagoSeleccionado.getId_pago());
            nuevoPago.setCodigo_actividad(pagoSeleccionado.getCodigo_actividad());
            nuevoPago.setMonto_total_gasto_aplicable(pagoSeleccionado.getMonto_total_gasto_aplicable());
            nuevoPago.setMonto_total_impuesto_acreditar(pagoSeleccionado.getMonto_total_impuesto_acreditar());
            nuevoPago.setId_condicion_impuesto(pagoSeleccionado.getId_condicion_impuesto());

            servicioPago.guardarPago(nuevoPago);

            //enviarCorreoElectronicoNuevoPago(nuevoPago);
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.pagos.guardado.correcto"));

        } catch (Exception ex) {
            Logger.getLogger(BeanAdministracionPagos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generarReporteInfoConfirmacionComprobanteNuevo(Pago nuevoPago) {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idPago", nuevoPago.getId_pago());
            reporte = servicioReporte.generarReporte(Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS, TiposMimeTypes.PDF, parametros, false);
            reporteConfirmacion = reporte;
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS.getNombreReporte() + "-" + nuevoPago.getNumero_consecutivo() + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void generarReporteInfoConfirmacionComprobante() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idPago", pagoSeleccionado.getId_pago());
            reporte = servicioReporte.generarReporte(Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS, TiposMimeTypes.PDF, parametros, false);
            reporteConfirmacion = reporte;
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS.getNombreReporte() + "-" + pagoSeleccionado.getNumero_consecutivo() + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void generarXMLComprobanteElectronico(ActionEvent evt) {
        if (pagoSeleccionado.getDocumento_xml_firmado() != null) {
            byte[] arregloXML = Utilitario.convertirBase64ABytes(pagoSeleccionado.getDocumento_xml_firmado());
            InputStream input = new ByteArrayInputStream(arregloXML);
            reporteDesplegarCompElectronico = new DefaultStreamedContent(input, TiposMimeTypes.XML.getMimeType(), ("Comprobante Electrónico - " + pagoSeleccionado.getNumero_consecutivo() + "." + TiposMimeTypes.XML.getExtension()));
        }

    }

    public void generarXMLRespuestaHacienda(ActionEvent evt) {
        if (pagoSeleccionado.getDetalle_respuesta_hacienda() != null) {
            byte[] arregloXML = pagoSeleccionado.getDetalle_respuesta_hacienda().getBytes();
            InputStream input = new ByteArrayInputStream(arregloXML);
            reporteDesplegarRespHacienda = new DefaultStreamedContent(input, TiposMimeTypes.XML.getMimeType(), ("Respuesta Hacienda - " + pagoSeleccionado.getNumero_consecutivo() + "." + TiposMimeTypes.XML.getExtension()));
        }
    }

    public void enviarCorreoElectronicoNuevoPago(Pago pagoNuevo) {
        try {
            CorreoElectronico correo = new CorreoElectronico();
            List<String> listaCorreos = new ArrayList<>();

            generarReporteInfoConfirmacionComprobanteNuevo(pagoNuevo);

            listaCorreos = new ArrayList<>();
            listaCorreos.add(pagoNuevo.getCorreo_electronico());
            correo.sendEmailConfirmacion("Confirmación de Comprobante Electrónico - ",
                    "Sear Médica le adjunta la respuesta de confirmación del Comprobante Electrónico " + pagoNuevo.getClave_comprobante_pago(),
                    reporteConfirmacion,
                    (Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS.getNombreReporte() + "-" + pagoNuevo.getNumero_consecutivo() + "." + TiposMimeTypes.PDF.getExtension()),
                    listaCorreos,
                    (pagoNuevo.getDocumento_xml_firmado() == null ? null : Utilitario.convertirBase64ABytes(pagoNuevo.getDocumento_xml_firmado())),
                    (pagoNuevo.getDetalle_respuesta_hacienda() == null ? null : pagoNuevo.getDetalle_respuesta_hacienda().getBytes()),
                    pagoNuevo.getNumero_consecutivo());

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void enviarCorreosElectronicos() {
        try {
            CorreoElectronico correo = new CorreoElectronico();
            List<String> listaCorreos = new ArrayList<>();
            this.generarReporteInfoConfirmacionComprobante();

            listaCorreos = new ArrayList<>();
            listaCorreos.add(pagoSeleccionado.getCorreo_electronico());
            correo.sendEmailConfirmacion("Confirmación de Comprobante Electrónico, Respuesta Hacienda - ",
                    "Sear Médica le adjunta la respuesta de Hacienda en relación a la confirmación del Comprobante Electrónico " + pagoSeleccionado.getClave_comprobante_pago(),
                    reporteConfirmacion,
                    (Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS.getNombreReporte() + "-" + pagoSeleccionado.getNumero_consecutivo() + "." + TiposMimeTypes.PDF.getExtension()),
                    listaCorreos,
                    (pagoSeleccionado.getDocumento_xml_firmado() == null ? null : Utilitario.convertirBase64ABytes(pagoSeleccionado.getDocumento_xml_firmado())),
                    (pagoSeleccionado.getDetalle_respuesta_hacienda() == null ? null : pagoSeleccionado.getDetalle_respuesta_hacienda().getBytes()),
                    pagoSeleccionado.getNumero_consecutivo());

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public StreamedContent getReporteDesplegar() {
        return reporteDesplegar;
    }

    public void setReporteDesplegar(StreamedContent reporteDesplegar) {
        this.reporteDesplegar = reporteDesplegar;
    }

    public StreamedContent getReporteDesplegarCompElectronico() {
        return reporteDesplegarCompElectronico;
    }

    public void setReporteDesplegarCompElectronico(StreamedContent reporteDesplegarCompElectronico) {
        this.reporteDesplegarCompElectronico = reporteDesplegarCompElectronico;
    }

    public StreamedContent getReporteDesplegarRespHacienda() {
        return reporteDesplegarRespHacienda;
    }

    public void setReporteDesplegarRespHacienda(StreamedContent reporteDesplegarRespHacienda) {
        this.reporteDesplegarRespHacienda = reporteDesplegarRespHacienda;
    }
}
