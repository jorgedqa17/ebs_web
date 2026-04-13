/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.ebs.constantes.enums.EstadoAnulacion;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.facturacion.servicios.ServicioNotaCredito;
import com.ebs.modelos.NotaCreditoModelo;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import lombok.Data;
import lombok.Getter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author jdquesad
 */
@ManagedBean
@ViewScoped
@Data
public class BeanAdministrarNotaCredito {

    @Inject
    private ServicioNotaCredito servicioNotaCredito;
    @Inject
    private ServicioFactura servicioFactura;
    private List<NotaCreditoModelo> listaNotasCredito;
    private List<NotaCreditoModelo> listaNotasCreditoFiltro;
    private NotaCreditoModelo notaCreditoSeleccionada;

    private List<FacturaAnulacionHistoricoHacienda> listaHistoricoHacienda;
    private List<FacturaAnulacionHistoricoHacienda> listaHistoricoHaciendaFiltro;
    private FacturaAnulacionHistoricoHacienda historicoSeleccionado;

    @Getter
    private StreamedContent reporteDesplegarCompElectronico = null;

    @PostConstruct
    public void inicializar() {
        this.notaCreditoSeleccionada = new NotaCreditoModelo();
        this.listaNotasCredito = servicioNotaCredito.obtenerNotasCredito();
    }

    public void seleccionarNotaCredito(NotaCreditoModelo notaSeleccionada) {
        try {
            notaCreditoSeleccionada = notaSeleccionada;

            if (notaCreditoSeleccionada.getNotaCredito().getId_factura() != null) {
                notaCreditoSeleccionada.setFactura(servicioFactura.obtenerFacturaBusqueda(notaCreditoSeleccionada.getNotaCredito().getId_factura()));
                notaCreditoSeleccionada.getFactura().setEstadoFactura(servicioNotaCredito.obtenerEstado(Long.parseLong(notaCreditoSeleccionada.getFactura().getEstado_factura().toString())));
            }
            if (notaCreditoSeleccionada.getNotaCredito().getId_nota_debito() != null) {
                notaCreditoSeleccionada.setNotaDebito(servicioFactura.obtenerNotaDebitoBusqueda(notaCreditoSeleccionada.getNotaCredito().getId_nota_debito()));
                notaCreditoSeleccionada.getNotaDebito().setEstadoFactura(servicioNotaCredito.obtenerEstado(notaCreditoSeleccionada.getNotaDebito().getId_estado()));
            }
            this.listaHistoricoHacienda = this.servicioNotaCredito.obtenerListaHistoricoNotaCredito(notaCreditoSeleccionada.getNotaCredito().getId_anulacion());
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarHistorico(ActionEvent evt) {
        this.historicoSeleccionado = (FacturaAnulacionHistoricoHacienda) evt.getComponent().getAttributes().get("historioSeleccionado");
        if (this.historicoSeleccionado.getDocumento_xml_firmado() != null
                && !this.historicoSeleccionado.getDocumento_xml_firmado().equals("")) {
            generarXMLComprobanteElectronico();
        }
    }

    public void generarXMLComprobanteElectronico() {
        byte[] arregloXML = Utilitario.convertirBase64ABytes(this.historicoSeleccionado.getDocumento_xml_firmado());
        InputStream input = new ByteArrayInputStream(arregloXML);
        reporteDesplegarCompElectronico = new DefaultStreamedContent(input, TiposMimeTypes.XML.getMimeType(), ("Comprobante Electrónico - " + this.notaCreditoSeleccionada.getNotaCredito().getNumero_consecutivo() + "." + TiposMimeTypes.XML.getExtension()));
    }

    public void reenviarCorreoElectronicoFactura() {
        try {
            notaCreditoSeleccionada.getNotaCredito().setEnvio_correo_electronico(0);
            this.servicioFactura.actualizar(notaCreditoSeleccionada.getNotaCredito());
            MensajeUtil.agregarMensajeInfo("Se ha enviado el correo");
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.actualizacion.factura"));
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void reconsultarFactura() {
        try {
            notaCreditoSeleccionada.getNotaCredito().setId_estado(EstadoAnulacion.ENVIADA_HACIENDA.getEstadoAnulacion());
            this.servicioFactura.actualizar(notaCreditoSeleccionada.getNotaCredito());
            MensajeUtil.agregarMensajeInfo("Se ha reenviado la consulta a hacienda");
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.actualizacion.factura.repsuesta"));
            ExcepcionManager.manejarExcepcion(ex);
        }
    }
}
