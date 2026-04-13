/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.MensajeUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
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
public class BeanVentasMensuales {

    @Getter
    @Setter
    private Date fechaInicio;
    @Getter
    @Setter
    private String tipoSeleccionado;
    @Getter
    @Setter
    private Date fechaFinal;
    @Getter
    @Setter
    private String formato;
    @Inject
    private ServicioReporte servicioReporte;

    public BeanVentasMensuales() {
    }

    @PostConstruct
    public void inicializar() {
        this.tipoSeleccionado = "0";
        formato = "pdf";
    }

    @Setter
    StreamedContent reporteDesplegar = null;

    public StreamedContent getReporteDesplegar() {
        return reporteDesplegar;
    }

    public void generarReporteVentasMensuales(ActionEvent evt) {
        byte[] reporte = null;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Map parametros = new HashMap<>();
            parametros.put("fechaInicio", df.format(fechaInicio));
            parametros.put("fechaFinal", df.format(fechaFinal));
            reporte = servicioReporte.generarReporte((this.tipoSeleccionado.equals("0") ? Reportes.VENTAS_MENSUALES : Reportes.VENTAS_MENSUALES_USUARIO), (formato.equals("pdf") ? TiposMimeTypes.PDF : TiposMimeTypes.XLSX), parametros, true);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, (formato.equals("pdf") ? TiposMimeTypes.PDF.getMimeType() : TiposMimeTypes.XLSX.getMimeType()), ((this.tipoSeleccionado.equals("0") ? Reportes.VENTAS_MENSUALES.getNombreReporte() : Reportes.VENTAS_MENSUALES_USUARIO.getNombreReporte()) + "." + (formato.equals("pdf") ? TiposMimeTypes.PDF.getExtension() : TiposMimeTypes.XLSX.getExtension())));
            MensajeUtil.agregarMensajeInfo("El reporte se generó satisfactoriamente.");
        } catch (JRException ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

}
