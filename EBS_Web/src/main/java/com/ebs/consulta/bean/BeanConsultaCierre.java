/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.consulta.bean;

import com.ebs.cierre.servicio.ServicioCierre;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Cierre;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.ConsultaCierre;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.MensajeUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author sergio
 */
@ManagedBean
@ViewScoped
public class BeanConsultaCierre {

    @Inject
    private ServicioCierre servicioCierre;
    @Getter
    @Setter
    private ConsultaCierre datoGeneral;
    @Getter
    @Setter
    private List<ConsultaCierre> listaGeneral;
    @Getter
    @Setter
    private List<ConsultaCierre> listaFiltrada;
    @Inject
    private ServicioReporte servicioReporte;

    private StreamedContent reporteDesplegar = null;
    @Setter
    @Getter
    private boolean semaforo;

    /**
     * Creates a new instance of BeanConsultaCierre
     */
    public BeanConsultaCierre() {
    }

    @PostConstruct
    public void inicializar() {
        datoGeneral = new ConsultaCierre();
        listaGeneral = servicioCierre.obtenerListaCierre(null, "");
    }

    public void generarReporteConsulta(ActionEvent evt) {
        byte[] reporte = null;
        try {
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("idCierre", datoGeneral.getId_cierre());

            reporte = servicioReporte.generarReporte(Reportes.CIERRE, TiposMimeTypes.PDF, parametros, true);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.CIERRE.getNombreReporte() + "-" + datoGeneral.getId_cierre() + "." + TiposMimeTypes.PDF.getExtension()));
            MensajeUtil.agregarMensajeInfo("El reporte se generó satisfactoriamente.");
            this.semaforo = true;
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError("Error al generar el informe del cierre sin embargo, el cierre se guardó correctamente");
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public StreamedContent getReporteDesplegar() {
        this.semaforo = false;
        return reporteDesplegar;
    }

    public void setReporteDesplegar(StreamedContent reporteDesplegar) {
        this.reporteDesplegar = reporteDesplegar;
    }

}
