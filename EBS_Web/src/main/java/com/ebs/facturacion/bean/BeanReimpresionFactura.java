/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.google.zxing.WriterException;
import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.CondicionVenta;
import com.ebs.entidades.DetalleFactura;
import com.ebs.entidades.Factura;
import com.ebs.entidades.FacturaHistoricoHacienda;
import com.ebs.entidades.MedioPago;
import com.ebs.entidades.TipoFactura;
import com.ebs.entidades.Usuario;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.modelos.ConsultaFacturasModelo;
import com.ebs.modelos.FacturaModeloAnulacion;
import com.ebs.modelos.LineaDetalleFactura;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.seguridad.servicios.ServicioLogin;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Jorge GBSYS
 */
@ManagedBean
@ViewScoped
public class BeanReimpresionFactura {

    @Inject
    private ServicioFactura servicioFactura;
    @Inject
    private ServicioLogin servicioLogin;
    @Inject
    private ServicioPersona servicioPersona;
    @Getter
    @Setter
    private List<ConsultaFacturasModelo> listaFacturas;
    @Setter
    StreamedContent reporteDesplegar = null;
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
    @Setter
    private List<LineaDetalleFactura> listaDetalle;

    @Getter
    @Setter
    private List<LineaDetalleFactura> listaDetalleFiltro;

    public BeanReimpresionFactura() {
    }

    @PostConstruct
    public void inicializar() {
        try {
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

    public void buscarFacturas() {
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        //Obtengo todas las facturas
        //consultarFacturasPorFechaPorCondVentaPorUsuarioPorMedioPago(Date fechaInicio, Date fechaFinal, Long idCondicionVenta, Long idMedioPago, Long idTipoFactura, Long idBodega) {
        //listaFacturas = servicioFactura.consultarFacturasPorFechaPorCondVentaPorUsuarioPorMedioPagoPorBodega(fechaInicio, fechaFin, idCondicionVentaSeleccionado, idMedioPagoSeleccionado, idTipoFacturaSeleccionada, idBodegaSeleccionada, usuarioSeleccionado);
    }

    public void obtenerInformacionFacturaSeleccionada(Long idFactura) {
        facturaSeleccionadaMostrar = servicioFactura.obtenerFacturaBusqueda(idFactura);
        listaProductoFacturaSeleccionadMostrar = servicioFactura.obtenerDetalleFacturaBusqueda(idFactura);
        this.listadoHistoricoFacturaMostrar = servicioFactura.obtenerHistoricoFacturaHaciendaConsulta(idFactura);

        this.listaNotasCredito = servicioFactura.obtenerNotasCreditoPorIdFactura(idFactura);

        listaDetalle = servicioFactura.obtenerDetalleFactura(idFactura);

    }

    public void seleccionarHistorico(ActionEvent evt) {
        this.historicoSeleccionado = (FacturaHistoricoHacienda) evt.getComponent().getAttributes().get("historioSeleccionado");
        if (this.historicoSeleccionado.getDocumento_xml_firmado() != null
                && !this.historicoSeleccionado.getDocumento_xml_firmado().equals("")) {
            generarXMLComprobanteElectronico();
        }

    }

    public void generarXMLComprobanteElectronico() {
        byte[] arregloXML = Utilitario.convertirBase64ABytes(this.historicoSeleccionado.getDocumento_xml_firmado());
        InputStream input = new ByteArrayInputStream(arregloXML);
        reporteDesplegarCompElectronico = new DefaultStreamedContent(input, TiposMimeTypes.XML.getMimeType(), ("Comprobante Electrónico - " + this.facturaSeleccionadaMostrar.getNumero_consecutivo() + "." + TiposMimeTypes.XML.getExtension()));
    }

    public void seleccionarFactura(ConsultaFacturasModelo facturaSeleccionada) {
        this.facturaSeleccionada = facturaSeleccionada;
    }

    public StreamedContent getReporteDesplegar() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idFactura", facturaSeleccionada.getIdFactura());
            parametros.put("simbolo", "CRC");
            parametros.put("montoLetras", Utilitario.Convertir(facturaSeleccionada.getMontoTotalComprobante().toString(), true));
            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode("https://35.185.53.104:8181/ServiciosRestFul/services/comprobanteElectronico?consecutivo=" + this.facturaSeleccionada.getNumeroConsecutivo()))));
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

    public StreamedContent getReporteDesplegarListadoFacturas() {
        byte[] reporte = null;
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        try {
            Map parametros = new HashMap<>();
            parametros.put("fechaInicio", df.format(fechaInicio));
            parametros.put("fechaFinal", df.format(fechaFin));
            reporte = servicioReporte.generarReporte(Reportes.REPORTE_LISTADO_FACTURA, TiposMimeTypes.XLSX, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegarListadoFactura = new DefaultStreamedContent(input, TiposMimeTypes.XLSX.getMimeType(), Reportes.REPORTE_LISTADO_FACTURA.getNombreReporte() + "." + TiposMimeTypes.XLSX.getExtension());

        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegarListadoFactura;
    }

}
