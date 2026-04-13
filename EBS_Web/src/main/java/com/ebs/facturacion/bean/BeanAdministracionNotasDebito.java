/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.FacturaNotaDebitoHistoricoHacienda;
import com.ebs.exception.ExcepcionManager;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Data;
import com.ebs.facturacion.servicios.ServicioNotaDebito;
import com.ebs.modelos.NotaDebitoModelo;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Inject;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
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
public class BeanAdministracionNotasDebito {

    @Inject
    private ServicioNotaDebito servicioNotaDebito;
    private List<NotaDebitoModelo> listaNotasDebito;
    private List<NotaDebitoModelo> listaNotasDebitoFiltro;
    private NotaDebitoModelo notaDebitoSeleccionada;
    private List<FacturaNotaDebitoHistoricoHacienda> listaHistoricoHacienda;
    private List<FacturaNotaDebitoHistoricoHacienda> listaHistoricoHaciendaFiltro;
    private FacturaNotaDebitoHistoricoHacienda historicoSeleccionado;
    @Getter
    private StreamedContent reporteDesplegarCompElectronico = null;

    @PostConstruct
    public void inicializar() {
        this.notaDebitoSeleccionada = new NotaDebitoModelo();
        this.listaNotasDebito = servicioNotaDebito.obtenerNotasDebito();
    }

    public void seleccionarNotaDebito(NotaDebitoModelo notaSeleccionada) {
        try {
            notaDebitoSeleccionada = notaSeleccionada;
            this.listaHistoricoHacienda = this.servicioNotaDebito.obtenerListaHistoricoNotaDebito(notaDebitoSeleccionada.getNotaDebito().getId_nota_debito());
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarHistorico(ActionEvent evt) {
        this.historicoSeleccionado = (FacturaNotaDebitoHistoricoHacienda) evt.getComponent().getAttributes().get("historioSeleccionado");
        if (this.historicoSeleccionado.getDocumento_xml_firmado() != null
                && !this.historicoSeleccionado.getDocumento_xml_firmado().equals("")) {
            generarXMLComprobanteElectronico();
        }
    }

    public void generarXMLComprobanteElectronico() {
        byte[] arregloXML = Utilitario.convertirBase64ABytes(this.historicoSeleccionado.getDocumento_xml_firmado());
        InputStream input = new ByteArrayInputStream(arregloXML);
        reporteDesplegarCompElectronico = new DefaultStreamedContent(input, TiposMimeTypes.XML.getMimeType(), ("Comprobante Electrónico - " + this.notaDebitoSeleccionada.getNotaDebito().getNumero_consecutivo() + "." + TiposMimeTypes.XML.getExtension()));
    }
}
