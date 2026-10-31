/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.ebs.constantes.enums.EstadoAnulacion;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Estados;
import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
import com.ebs.entidades.Usuario;
import com.ebs.modelos.ConsultaNotasCreditoModelo;
import com.powersystem.seguridad.servicios.ServicioLogin;
import java.util.ArrayList;
import java.util.Date;
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
    @Inject
    private ServicioLogin servicioLogin;
    private List<ConsultaNotasCreditoModelo> listaNotasCredito;
    private List<ConsultaNotasCreditoModelo> listaNotasCreditoFiltro;
    private List<Estados> listaEstados;
    private List<Usuario> listaUsuarios;
    private Date fechaInicio;
    private Date fechaFin;
    private Long idEstadoSeleccionado;
    private String usuarioSeleccionado;
    private String consecutivo;
    private NotaCreditoModelo notaCreditoSeleccionada;

    private List<FacturaAnulacionHistoricoHacienda> listaHistoricoHacienda;
    private List<FacturaAnulacionHistoricoHacienda> listaHistoricoHaciendaFiltro;
    private FacturaAnulacionHistoricoHacienda historicoSeleccionado;

    @Getter
    private StreamedContent reporteDesplegarCompElectronico = null;

    @PostConstruct
    public void inicializar() {
        try {
            this.notaCreditoSeleccionada = new NotaCreditoModelo();
            this.listaNotasCredito = new ArrayList<>();
            this.listaEstados = servicioNotaCredito.obtenerEstadosNotaCredito();
            this.listaUsuarios = servicioLogin.obtenerUsuariosSistema();
            this.fechaInicio = new Date();
            this.fechaFin = new Date();
            this.idEstadoSeleccionado = 0L;
            this.usuarioSeleccionado = Utilitario.obtenerUsuario().getLogin();
            this.consecutivo = "";
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void buscarNotasCredito() {
        try {
            if (fechaInicio == null || fechaFin == null || fechaInicio.after(fechaFin)) {
                MensajeUtil.agregarMensajeAdvertencia("El rango de fechas no es válido");
                return;
            }
            this.listaNotasCreditoFiltro = null;
            this.notaCreditoSeleccionada = new NotaCreditoModelo();
            this.listaHistoricoHacienda = null;
            this.listaNotasCredito = servicioNotaCredito.consultarNotasCreditoPorFiltros(
                    fechaInicio, fechaFin, idEstadoSeleccionado, usuarioSeleccionado, consecutivo);
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarNotaCredito(ConsultaNotasCreditoModelo fila) {
        try {
            notaCreditoSeleccionada = servicioNotaCredito.obtenerDetalleNotaCredito(fila.getIdAnulacion());
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
