/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.google.zxing.WriterException;
import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.CancelacionFactura;
import com.ebs.constantes.enums.Clase;
import com.ebs.constantes.enums.EstadoFactura;
import com.ebs.constantes.enums.FacturaEnvioCorreos;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TipoFacturaEnum;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.CondicionVenta;
import com.ebs.entidades.DetalleFactura;
import com.ebs.entidades.Estados;
import com.ebs.entidades.Factura;
import com.ebs.entidades.FacturaHistoricoHacienda;
import com.ebs.entidades.FacturaTrazabilidad;
import com.ebs.entidades.MedioPago;
import com.ebs.entidades.TipoFactura;
import com.ebs.entidades.Usuario;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.facturacion.servicios.ServiciosRestFul;
import com.ebs.modelos.ConsultaFacturasModelo;
import com.ebs.modelos.FacturaImpresion;
import com.ebs.modelos.FacturaModeloAnulacion;
import com.ebs.modelos.FacturaModeloNotaDebito;
import com.ebs.modelos.LineaDetalleFactura;
import com.ebs.modelos.ObjetoFactura;
import com.ebs.parametros.servicios.ServicioParametro;
import com.google.gson.Gson;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.seguridad.servicios.ServicioLogin;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Jorge GBSYS
 */
@ManagedBean
@ViewScoped
public class BeanAdministracionFacturas {

    @Getter
    @Setter
    private List<Estados> listaEstadosFactura;
    @Getter
    @Setter
    private Long idEstadoSeleccionado;
    @Inject
    private ServicioFactura servicioFactura;
    @Inject
    private ServicioLogin servicioLogin;
    @Inject
    private ServicioPersona servicioPersona;

    @Inject
    private ServicioParametro servicioParametro;
    @Getter
    @Setter
    private List<ConsultaFacturasModelo> listaFacturas;
    @Setter
    StreamedContent reporteDesplegar = null;
    @Setter
    StreamedContent notaCredito = null;
    @Setter
    StreamedContent notaDebito = null;
    @Setter
    StreamedContent reporteDesplegarListadoFactura = null;
    @Inject
    private ServicioReporte servicioReporte;
    @Inject
    private ServicioBodega servicioBodegas;
    @Getter
    @Setter
    ConsultaFacturasModelo facturaSeleccionada;
    @Getter
    @Setter
    private List<ConsultaFacturasModelo> listaFacturasSeleccionadas;
    @Getter
    @Setter
    private List<CondicionVenta> listaCondicionVenta;
    @Getter
    @Setter
    private List<MedioPago> listaMedioPago;
    @Getter
    @Setter
    private String jsonToPrint;
    @Getter
    @Setter
    private Long idCondicionVentaSeleccionado;
    @Getter
    @Setter
    private Long idMedioPagoSeleccionado;
    @Getter
    @Setter
    private Long idTipoFacturaSeleccionada;
    @Getter
    @Setter
    private Long idBodegaSeleccionada;
    @Getter
    @Setter
    private String usuarioSeleccionado;
    @Getter
    @Setter
    private List<TipoFactura> listaTiposFacturas;
    @Getter
    @Setter
    private Date fechaInicio;
    @Getter
    @Setter
    private Date fechaFin;
    @Getter
    @Setter
    private String numeroConsecutivo;

    @Getter
    @Setter
    private String correoElectronicoPorActualizar;
    @Getter
    @Setter
    private Long idFactura;
    @Getter
    @Setter
    private Factura facturaSeleccionadaMostrar;

    @Getter
    @Setter
    private List<DetalleFactura> listaProductoFacturaSeleccionadMostrar;
    @Getter
    @Setter
    private FacturaHistoricoHacienda historicoSeleccionado;
    @Getter
    @Setter
    private List<FacturaHistoricoHacienda> listadoHistoricoFacturaMostrar;
    @Getter
    @Setter
    private List<FacturaModeloAnulacion> listaNotasCredito;
    @Getter
    @Setter
    private List<FacturaModeloNotaDebito> listaNotasDebito;
    @Getter
    @Setter
    private String formato;
    @Getter
    @Setter
    private List<Bodega> listaBodegas;
    @Getter
    @Setter
    private List<Usuario> listaUsuarios;
    @Getter
    private StreamedContent reporteDesplegarCompElectronico = null;
    @Getter
    private StreamedContent respuestaHaciendaXMLDescarga = null;
    @Getter
    @Setter
    private boolean facturaCancelada;
    @Getter
    @Setter
    private boolean inhabilidadComponentes;

    @Getter
    @Setter
    private List<LineaDetalleFactura> listaDetalle;

    @Getter
    @Setter
    private List<FacturaTrazabilidad> listaFacturasTrazabilidads;
    @Getter
    @Setter
    private List<FacturaTrazabilidad> listaFacturasTrazabilidadsFiltro;
    @Getter
    @Setter
    private List<LineaDetalleFactura> listaDetalleFiltro;

    public BeanAdministracionFacturas() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            this.listaEstadosFactura = this.servicioFactura.obtenerEstadoPorIdClase(Clase.FACTURA.getIdClase());
            this.listaMedioPago = servicioFactura.obtenerMediosPago();
            this.listaCondicionVenta = servicioFactura.obtenerCondicionesVenta();
            this.listaTiposFacturas = servicioFactura.obtenerTiposFacturas();
            this.listaBodegas = servicioBodegas.obtenerListaBodegas();
            this.listaUsuarios = servicioLogin.obtenerUsuariosSistema();

            fechaInicio = Calendar.getInstance().getTime();
            fechaFin = Calendar.getInstance().getTime();
            facturaSeleccionadaMostrar = new Factura();
            listaProductoFacturaSeleccionadMostrar = new ArrayList<>();
            listadoHistoricoFacturaMostrar = new ArrayList<>();

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    /**
     * Metodo que actualiza el campo envio correo elecornico para indicarle al
     * sistema que debe reenviar el pdf y el xml
     */
    public void reenviarCorreoElectronicoFactura() {
        try {
            Factura facturaActual = this.servicioFactura.obtenerFacturaBusqueda(facturaSeleccionadaMostrar.getId_factura());
            facturaActual.setEnvio_correo_electronico(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
            facturaActual.setEnvio_respuesta_hacienda(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
            this.servicioFactura.actualizar(facturaActual);
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.envio.correoelectronico.factura"));
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.actualizacion.factura"));
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void reconsultarFactura() {
        try {
            this.facturaSeleccionadaMostrar.setEstado_factura(EstadoFactura.ENVIADA_HACIENDA.getEstadoFactura());
            this.servicioFactura.actualizar(facturaSeleccionadaMostrar);
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.envio.correoelectronico.factura.repsuesta"));
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.actualizacion.factura.repsuesta"));
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    /**
     * Metodo que actualiza el campo envio correo elecornico para indicarle al
     * sistema que debe reenviar el pdf y el xml
     */
    public void reenviarCorreoElectronicoFacturaRespuestaHacienda() {
        try {
            this.facturaSeleccionadaMostrar.setEnvio_respuesta_hacienda(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
            this.servicioFactura.actualizar(facturaSeleccionadaMostrar);
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.envio.correoelectronico.factura.repsuesta"));
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.actualizacion.factura.repsuesta"));
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void reenviarFacturaHaciendaSinNotaCredito() {
        try {
            this.facturaSeleccionadaMostrar.setEstado_factura(EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura());
            this.servicioFactura.actualizar(facturaSeleccionadaMostrar);
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.reenvio.hacienda.factura"));
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.reenvio.hacienda.factura.error"));
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void buscarFacturas() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        //Obtengo todas las facturas
        //consultarFacturasPorFechaPorCondVentaPorUsuarioPorMedioPago(Date fechaInicio, Date fechaFinal, Long idCondicionVenta, Long idMedioPago, Long idTipoFactura, Long idBodega) {
        listaFacturas = servicioFactura.consultarFacturasPorFechaPorCondVentaPorUsuarioPorMedioPagoPorBodega(fechaInicio, fechaFin, idCondicionVentaSeleccionado, idMedioPagoSeleccionado, idTipoFacturaSeleccionada, idBodegaSeleccionada, usuarioSeleccionado, this.idEstadoSeleccionado);
    }

    public void obtenerInformacionFacturaSeleccionada(Long idFactura) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();

        inhabilidadComponentes = false;
        facturaSeleccionadaMostrar = servicioFactura.obtenerFacturaBusqueda(idFactura);
        calendar.setTime(facturaSeleccionadaMostrar.getFecha_factura());
        if (facturaSeleccionadaMostrar.getFactura_cancelada().equals(CancelacionFactura.CANCELADA.getFacturaCancelada())
                || facturaSeleccionadaMostrar.getEstado_factura().equals(EstadoFactura.EN_DESARROLLO.getEstadoFactura())) {
            inhabilidadComponentes = true;
        }

        if (facturaSeleccionadaMostrar.getEstado_factura().equals(EstadoFactura.ERROR_HACIENDA.getEstadoFactura())
                || facturaSeleccionadaMostrar.getEstado_factura().equals(EstadoFactura.RECHAZADA_HACIENDA.getEstadoFactura())
                || facturaSeleccionadaMostrar.getEstado_factura().equals(EstadoFactura.ERROR_SERVICIO.getEstadoFactura())
                || facturaSeleccionadaMostrar.getEstado_factura().equals(EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura())) {
            inhabilidadComponentes = true;

            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensa.aviso.admini.factura.estado"));
        }
        this.listadoHistoricoFacturaMostrar = servicioFactura.obtenerHistoricoFacturaHaciendaConsulta(idFactura);

        listaDetalle = servicioFactura.obtenerDetalleFactura(idFactura);
        listaFacturasTrazabilidads = servicioFactura.obtenerTrazabilidadFactura(idFactura);

        this.listaNotasCredito = servicioFactura.obtenerNotasCreditoPorIdFactura(idFactura);

        this.listaNotasDebito = servicioFactura.obtenerNotasDebitoPorIdFactura(idFactura);

        if (facturaSeleccionadaMostrar.getEstado_factura().equals(EstadoFactura.VENCIDA.getEstadoFactura())
                || facturaSeleccionadaMostrar.getEstado_factura().equals(EstadoFactura.EN_DESARROLLO.getEstadoFactura())) {

            calendar.add(Calendar.DATE, facturaSeleccionadaMostrar.getCant_dias_vigencia());
            facturaSeleccionadaMostrar.setFechaVencimiento(df.format(calendar.getTime()));
        }

    }

    public void seleccionarHistorico(ActionEvent evt) {
        this.historicoSeleccionado = (FacturaHistoricoHacienda) evt.getComponent().getAttributes().get("historioSeleccionado");
        if (this.historicoSeleccionado.getDocumento_xml_firmado() != null
                && !this.historicoSeleccionado.getDocumento_xml_firmado().equals("")) {
            generarXMLComprobanteElectronico();
        }

    }

    public void generarXMLComprobanteElectronico() {
        try {
            reporteDesplegarCompElectronico = null;
            byte[] arregloXML = Utilitario.obtenerArchivoXML(this.servicioParametro.obtenerValorParametro(ParametrosEnum.RUTA_PADRE.getIdParametro()).getValor(), facturaSeleccionadaMostrar.getClave(), facturaSeleccionadaMostrar.getNumero_consecutivo()); //Utilitario.convertirBase64ABytes(this.historicoSeleccionado.getDocumento_xml_firmado());
            if (arregloXML != null) {
                InputStream input = new ByteArrayInputStream(arregloXML);
                reporteDesplegarCompElectronico = new DefaultStreamedContent(input, TiposMimeTypes.XML.getMimeType(), ("Comprobante Electrónico - " + this.facturaSeleccionadaMostrar.getNumero_consecutivo() + "." + TiposMimeTypes.XML.getExtension()));
            } else {
                MensajeUtil.agregarMensajeAdvertencia("Archivo XML no encontrado, por favor comuniquese con el administrador del sistema");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            MensajeUtil.agregarMensajeError("Ha ocurrido un error cuando se intentó descargar el xml");
        }
    }

    public void descargarRespuestaComprobanteElectronicoXML() {
        try {
            respuestaHaciendaXMLDescarga = null;
            if (this.historicoSeleccionado != null
                    && this.historicoSeleccionado.getDetallerespuesta() != null
                    && !this.historicoSeleccionado.getDetallerespuesta().isEmpty()) {

                byte[] contenido;
                // El detallerespuesta puede venir como XML plano o Base64 — se intenta decodificar.
                try {
                    contenido = Utilitario.convertirBase64ABytes(
                            this.historicoSeleccionado.getDetallerespuesta());
                } catch (Exception e) {
                    contenido = this.historicoSeleccionado.getDetallerespuesta()
                            .getBytes(StandardCharsets.UTF_8);
                }

                String clave = facturaSeleccionadaMostrar.getClave() == null
                        ? facturaSeleccionadaMostrar.getId_factura().toString()
                        : facturaSeleccionadaMostrar.getClave();

                InputStream input = new ByteArrayInputStream(contenido);
                respuestaHaciendaXMLDescarga = new DefaultStreamedContent(
                        input,
                        TiposMimeTypes.XML.getMimeType(),
                        "RespuestaComprobanteElectronico_" + clave + "." + TiposMimeTypes.XML.getExtension()
                );
            } else {
                MensajeUtil.agregarMensajeAdvertencia(
                        "No hay detalle de respuesta disponible para descargar");
            }
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(
                    "Ha ocurrido un error cuando se intentó descargar la respuesta XML");
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarFactura(ConsultaFacturasModelo facturaSeleccionada) {
        this.facturaSeleccionada = facturaSeleccionada;
    }

    public StreamedContent imprirmirNotaCredito(FacturaModeloAnulacion notaCredito) {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idAnulacion", notaCredito.getIdAnulacion());
            parametros.put("simbolo", "CRC");

            reporte = servicioReporte.generarReporte(Reportes.REPORTE_NOTAS_CREDITO, TiposMimeTypes.PDF, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            this.notaCredito = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_NOTAS_CREDITO.getNombreReporte() + "-" + (notaCredito.getClave().equals("") ? notaCredito.getIdAnulacion() : notaCredito.getClave()) + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegar;
    }

    public StreamedContent imprimirNotaDebito(FacturaModeloNotaDebito notaDebito) {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idNotaDebito", notaDebito.getIdNotaDebito());
            parametros.put("simbolo", "CRC");
            reporte = servicioReporte.generarReporte(Reportes.REPORTE_NOTAS_DEBITO, TiposMimeTypes.PDF, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            this.notaDebito = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_NOTAS_DEBITO.getNombreReporte() + "-" + (notaDebito.getClave().equals("") ? notaDebito.getIdNotaDebito() : notaDebito.getClave()) + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegar;
    }

    public String retornarPrint() {
        Gson gson = new Gson();
        List<FacturaImpresion> lista = null;
        
        if (facturaSeleccionada != null) {
            if (!facturaSeleccionada.getDescripcionTipoFactura().equals("Proforma")) {
                lista = servicioFactura.obtenerObjetoFacturaTrazabilidad(facturaSeleccionada.getIdFactura(), Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));
            } else {
                lista = servicioFactura.obtenerObjetoFacturaConsolidado(facturaSeleccionada.getIdFactura(), Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));
            }
            ObjetoFactura objetoFactura = new ObjetoFactura();
            objetoFactura.setFactura(lista);
            return new String(gson.toJson(objetoFactura).getBytes(), StandardCharsets.UTF_8);
        }
        return "";
    }

    public StreamedContent getReportePuntoVentaDesplegar() {
        byte[] reporte = null;
        try {

            //ServiciosRestFul.enviarFactura(lista);
            //RequestContext.getCurrentInstance()
                    //.execute("callPrinter('" + jsonToPrint + "');");

            Map parametros = new HashMap<>();
            parametros.put("idFactura", facturaSeleccionada.getIdFactura());
            parametros.put("simbolo", "CRC");
            parametros.put("montoLetras", Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));
            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode(facturaSeleccionadaMostrar.getClave() == null ? facturaSeleccionadaMostrar.getId_factura().toString() : facturaSeleccionadaMostrar.getClave()))));
            if (!facturaSeleccionada.getDescripcionTipoFactura().equals("Proforma")) {
                reporte = servicioReporte.generarReporte(Reportes.REPORTE_FACTURA_PUNTO_VENTA_TRAZABILIDAD, TiposMimeTypes.PDF, parametros, false);
            } else {
                reporte = servicioReporte.generarReporte(Reportes.REPORTE_FACTURA_PUNTO_VENTA, TiposMimeTypes.PDF, parametros, false);
            }
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_FACTURA_PUNTO_VENTA_TRAZABILIDAD.getNombreReporte() + "-" + (facturaSeleccionada.getNumeroConsecutivo().equals("") ? facturaSeleccionada.getIdFactura() : facturaSeleccionada.getNumeroConsecutivo()) + "." + TiposMimeTypes.PDF.getExtension()));

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegar;
    }

    public void actualizarCorreoElectronico() {
        try {
            if (correoElectronicoPorActualizar != null && !correoElectronicoPorActualizar.isEmpty()) {
                this.servicioFactura.actualizarCorreoElectronicoFactura(facturaSeleccionada.getIdFactura(), this.correoElectronicoPorActualizar);
                MensajeUtil.agregarMensajeInfo("Correo actualizado correctamente");
                correoElectronicoPorActualizar = "";
            }
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError("Ha ocurrido un error cuando se intentó actualizar el correo electrónico");
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public StreamedContent getReportePuntoVentaDesplegarConsolidado() {
        byte[] reporte = null;

        try {

            List<FacturaImpresion> lista = null;
            if (!facturaSeleccionada.getDescripcionTipoFactura().equals("Proforma")) {
                lista = servicioFactura.obtenerObjetoFacturaTrazabilidad(facturaSeleccionada.getIdFactura(), Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));
            } else {
                lista = servicioFactura.obtenerObjetoFacturaConsolidado(facturaSeleccionada.getIdFactura(), Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));
            }
            //ServiciosRestFul.enviarFactura(lista);

            Gson gson = new Gson();
            ObjetoFactura objetoFactura = new ObjetoFactura();
            objetoFactura.setFactura(lista);
            String asciiEncodedString = new String(gson.toJson(objetoFactura).getBytes(), StandardCharsets.UTF_8);

            RequestContext.getCurrentInstance()
                    .execute("callPrinter('" + asciiEncodedString + "');");

            Map parametros = new HashMap<>();
            parametros.put("idFactura", facturaSeleccionada.getIdFactura());
            parametros.put("simbolo", "CRC");
            parametros.put("montoLetras", Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));
            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode(facturaSeleccionadaMostrar.getClave() == null ? facturaSeleccionadaMostrar.getId_factura().toString() : facturaSeleccionadaMostrar.getClave()))));
            if (!facturaSeleccionada.getDescripcionTipoFactura().equals("Proforma")) {
                reporte = servicioReporte.generarReporte(Reportes.REPORTE_FACTURA_PUNTO_VENTA_TRAZABILIDAD, TiposMimeTypes.PDF, parametros, false);
            } else {
                reporte = servicioReporte.generarReporte(Reportes.REPORTE_FACTURA_PUNTO_VENTA, TiposMimeTypes.PDF, parametros, false);
            }

            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_FACTURA_PUNTO_VENTA.getNombreReporte() + "-" + (facturaSeleccionada.getNumeroConsecutivo().equals("") ? facturaSeleccionada.getIdFactura() : facturaSeleccionada.getNumeroConsecutivo()) + "." + TiposMimeTypes.PDF.getExtension()));

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegar;
    }

    public StreamedContent getReporteDesplegarConsolidado() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idFactura", facturaSeleccionada.getIdFactura());
            parametros.put("simbolo", "CRC");
            parametros.put("montoLetras", Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));
            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode(facturaSeleccionadaMostrar.getClave() == null ? facturaSeleccionadaMostrar.getId_factura().toString() : facturaSeleccionadaMostrar.getClave()))));

            reporte = servicioReporte.generarReporte(Reportes.FACTURA, TiposMimeTypes.PDF, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.FACTURA.getNombreReporte() + "-" + (facturaSeleccionada.getNumeroConsecutivo().equals("") ? facturaSeleccionada.getIdFactura() : facturaSeleccionada.getNumeroConsecutivo()) + "." + TiposMimeTypes.PDF.getExtension()));

        } catch (JRException | IOException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        } catch (WriterException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegar;
    }

    public StreamedContent getReporteDesplegar() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idFactura", facturaSeleccionada.getIdFactura());
            parametros.put("simbolo", "CRC");
            parametros.put("montoLetras", Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));

            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode(facturaSeleccionadaMostrar.getClave() == null ? facturaSeleccionadaMostrar.getId_factura().toString() : facturaSeleccionadaMostrar.getClave()))));

            if (facturaSeleccionadaMostrar.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura())
                    || facturaSeleccionadaMostrar.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) {
                reporte = servicioReporte.generarReporte(Reportes.FACTURA_TRAZABILIDAD, TiposMimeTypes.PDF, parametros, false);
            } else {
                reporte = servicioReporte.generarReporte(Reportes.FACTURA, TiposMimeTypes.PDF, parametros, false);
            }

            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.FACTURA_TRAZABILIDAD.getNombreReporte() + "-" + (facturaSeleccionada.getNumeroConsecutivo().equals("") ? facturaSeleccionada.getIdFactura() : facturaSeleccionada.getNumeroConsecutivo()) + "." + TiposMimeTypes.PDF.getExtension()));

        } catch (JRException | IOException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        } catch (WriterException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegar;
    }

    public StreamedContent getReporteDesplegarListadoFacturas() {
        byte[] reporte = null;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Map parametros = new HashMap<>();
            parametros.put("fechaInicio", df.format(fechaInicio));
            parametros.put("fechaFinal", df.format(fechaFin));
            parametros.put("login", usuarioSeleccionado);
            reporte = servicioReporte.generarReporte(Reportes.REPORTE_LISTADO_FACTURA, TiposMimeTypes.XLSX, parametros, false);

            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegarListadoFactura = new DefaultStreamedContent(input, TiposMimeTypes.XLSX.getMimeType(), Reportes.REPORTE_LISTADO_FACTURA.getNombreReporte() + "." + TiposMimeTypes.XLSX.getExtension());

        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegarListadoFactura;
    }

    public void cancelarFactura() {
        try {
            this.servicioFactura.actualizarFactura(facturaSeleccionada.getIdFactura(), Utilitario.obtenerUsuario().getLogin(), facturaCancelada);
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.correcto.cancelacinl.factura"));

        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.cancelacion.factura"));
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public StreamedContent getNotaCredito() {
        return notaCredito;
    }

    public StreamedContent getNotaDebito() {
        return notaDebito;
    }

}
