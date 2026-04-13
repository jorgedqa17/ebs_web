/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.inventario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Bodega;
import com.ebs.exception.ExcepcionManager;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.EnviosSucursalesModelo;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Jorge GBSYS
 */
@ManagedBean
@ViewScoped
public class BeanVentasDiariasSucursales {

    @Getter
    @Setter
    private List<EnviosSucursalesModelo> listaEnvios;
    @Getter
    @Setter
    private Date fechaEnvio;
    @Getter
    @Setter
    private Date fechaEnvio2;
    @Getter
    @Setter
    private List<Bodega> listaBodegas;
    @Setter
    StreamedContent reporteDesplegar = null;
    @Getter
    @Setter
    private Long idBodegaSeleccionada;

    @Inject
    private ServicioInventario servicioInventario;
    @Inject
    private ServicioReporte servicioReporte;
    @Inject
    private ServicioBodega servicioBodegas;

    public BeanVentasDiariasSucursales() {

    }

    @PostConstruct
    public void inicializar() {
        this.listaBodegas = new ArrayList<>();
        this.listaBodegas.add(Bodega.retornarBodega("TODAS"));
        this.listaBodegas.addAll(servicioBodegas.obtenerListaBodegas());

    }

    public void buscarEnvios(ActionEvent evt) {
        try {
            if (this.validar()) {
                StringBuilder idsBodegas = new StringBuilder();
                boolean primeraVez = false;
                if (this.idBodegaSeleccionada.equals(0L)) {
                    for (Bodega bodega : this.listaBodegas) {
                        if (!primeraVez) {
                            idsBodegas.append(bodega.getBodegaPK().getId_bodega());
                            primeraVez = true;
                        } else {
                            idsBodegas.append(",").append(bodega.getBodegaPK().getId_bodega());
                        }

                    }
                } else {
                    idsBodegas.append(this.idBodegaSeleccionada);
                }
                if (fechaEnvio != null) {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    listaEnvios = servicioInventario.obtenerListaProductosAEnviar(df.format(fechaEnvio), df.format(fechaEnvio2), idsBodegas.toString());
                }
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public boolean validar() {

        if (fechaEnvio == null || fechaEnvio2 == null) {

            MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar el rango de fechas");
            return false;

        }
        return true;
    }

    public void generarReporte() {
        byte[] reporte = null;
        try {
            if (this.validar()) {
                StringBuilder idsBodegas = new StringBuilder();
                boolean primeraVez = false;

                if (this.idBodegaSeleccionada.equals(0L)) {
                    for (Bodega bodega : this.listaBodegas) {
                        if (!primeraVez) {
                            idsBodegas.append(bodega.getBodegaPK().getId_bodega());
                            primeraVez = true;
                        } else {
                            idsBodegas.append(",").append(bodega.getBodegaPK().getId_bodega());
                        }

                    }
                } else {
                    idsBodegas.append(this.idBodegaSeleccionada);
                }

                Map parametros = new HashMap<>();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                parametros.put("fecha", df.format(fechaEnvio));
                parametros.put("fecha2", df.format(fechaEnvio2));
                parametros.put("bodegas", idsBodegas.toString());

                reporte = servicioReporte.generarReporte(Reportes.ENVIOS_DIARIOS, TiposMimeTypes.PDF, parametros, false);
                InputStream input = new ByteArrayInputStream(reporte);
                reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.ENVIOS_DIARIOS.getNombreReporte() + "." + TiposMimeTypes.PDF.getExtension()));

            }
        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public StreamedContent getReporteDesplegar() {
        return reporteDesplegar;
    }

}
