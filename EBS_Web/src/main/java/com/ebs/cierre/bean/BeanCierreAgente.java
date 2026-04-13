/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.cierre.bean;

import lombok.Data;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TipoCondicionVenta;
import com.ebs.constantes.enums.TiposCierre;
import com.ebs.constantes.enums.TiposMediosPago;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Cierre;
import com.ebs.entidades.CierreFactura;
import com.ebs.entidades.CierreRecibo;
import com.ebs.entidades.DetalleCierre;
import com.ebs.modelos.CierreTotales;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.modelos.FacturaCierreModelo;
import com.ebs.modelos.ReciboCierreModelo;
import com.ebs.modelos.SalidasCierreModelo;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
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
import javax.inject.Inject;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author jdquesad
 */
@ManagedBean
@ViewScoped
@Data
public class BeanCierreAgente {

    @Inject
    private ServicioFactura servicioFactura;
    @Inject
    private ServicioReporte servicioReporte;
    private Date fechaInicio;
    private Date fechaFin;
    private List<FacturaCierreModelo> listaFacturas;
    private Long idTipoCierreSeleccionado;
    private SalidasCierreModelo salida;
    private List<SalidasCierreModelo> listaSalidas;
    private boolean inhabilitarFechaInicio;
    StreamedContent reporteDesplegar = null;
    private Cierre cierre;
    private BigDecimal montoEntradas;
    private BigDecimal montoSalidas;
    private BigDecimal montoAnulaciones;
    private BigDecimal montoTotal;
    private BigDecimal montoFacturasCredito;
    private BigDecimal montoFacturasTarjeta;
    private BigDecimal montoEfectivo;
    private BigDecimal montoCheque;
    private List<ReciboCierreModelo> listaReciboCierre;
    public List<CierreTotales> listaTotales;

    @PostConstruct
    public void inicializar() {
        try {

            cierre = new Cierre();
            listaFacturas = new ArrayList<>();

            salida = new SalidasCierreModelo();
            listaSalidas = new ArrayList<>();

            String fechaUltimoCierre = this.servicioFactura.obtenerFechaInicioCierre(Utilitario.obtenerUsuario().getLogin());
            if (fechaUltimoCierre == null) {
                fechaInicio = Calendar.getInstance().getTime();
                inhabilitarFechaInicio = false;
            } else {
                inhabilitarFechaInicio = true;
                fechaInicio = new SimpleDateFormat("dd/MM/yyyy").parse(fechaUltimoCierre);
            }

            fechaFin = Calendar.getInstance().getTime();
            idTipoCierreSeleccionado = TiposCierre.DIARIO.getIdTipoCierre();
            montoSalidas = new BigDecimal("0.0");
            montoEntradas = new BigDecimal("0.0");
            montoFacturasCredito = new BigDecimal("0.0");
            montoAnulaciones = new BigDecimal("0.0");
            montoEfectivo = new BigDecimal("0.0");

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void eliminarSalida(SalidasCierreModelo salidaEliminar) {
        this.listaSalidas.remove(salidaEliminar);
        this.calcularMontoEntregar();
    }

    /**
     * Métod que obtiene las factuaras según los filtros indicados
     *
     * @param evt
     */
    public void buscarFacturas(ActionEvent evt) {
        try {

            if (fechaInicio != null && fechaFin != null) {

                listaTotales = new ArrayList<>();
                cierre = new Cierre();
                listaFacturas = new ArrayList<>();
                salida = new SalidasCierreModelo();
                listaSalidas = new ArrayList<>();

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fechaInicio);

                listaTotales = servicioFactura.obtenerTotalesCierre(Utilitario.obtenerUsuario().getLogin(), df.format(calendar.getTime()), df.format(fechaFin));
                listaReciboCierre = servicioFactura.obtenerRecibosParaCierre(Utilitario.obtenerUsuario().getLogin(), df.format(calendar.getTime()), df.format(fechaFin));

                calcularMontoEntregar();
            } else {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacoin.generacion.cierra"));
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void agregarSalida(ActionEvent evt) {
        if (salida.getMotivoSalida() == null) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cierre"));
            return;
        }
        if (salida.getMotivoSalida().equals("")) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cierre"));
            return;
        }
        if (salida.getMonto() == null) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cierre.monto"));
            return;
        }
        if (salida.getMonto().equals(new BigDecimal("0.0"))) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cierre.monto"));
            return;
        }
        salida.setConsecutivo(this.listaSalidas.size() + 1);
        this.listaSalidas.add(salida);
        salida = new SalidasCierreModelo();
        calcularMontoEntregar();
        MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.aviso.agregacion.correcta.salida"));

    }

    public void calcularMontoEntregar() {
        montoSalidas = new BigDecimal("0.0");
        montoEntradas = new BigDecimal("0.0");
        montoFacturasCredito = new BigDecimal("0.0");
        montoAnulaciones = new BigDecimal("0.0");
        montoEfectivo = this.listaTotales.stream().filter(elemento -> elemento.getIdMedioPago().equals(TiposMediosPago.EFECTIVO.getIdMedioPago())).findAny().get().getMonto();

        //rebajo las salidas
        for (SalidasCierreModelo listaSalida : listaSalidas) {
            montoSalidas = montoSalidas.add(listaSalida.getMonto());
        }
        montoEfectivo = montoEfectivo.subtract(montoSalidas);

        //rebajo las facturas que son a credito pero que son en efectivo
        for (FacturaCierreModelo factura : listaFacturas) {
            if (factura.getIdCondicionVenta().equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta()) && factura.getIdMedioPago().equals(TiposMediosPago.EFECTIVO.getIdMedioPago())) {
                montoFacturasCredito = montoFacturasCredito.add(factura.getTotalComprobante());
            }
        }
        montoEfectivo = montoEfectivo.subtract(montoFacturasCredito);

        for (CierreTotales cierreTotal : listaTotales) {
            montoEntradas = montoEntradas.add(cierreTotal.getMonto());
        }
    }

    public boolean validarCierre() {
        boolean resultado = true;
        boolean faltaIngresarNumeroReferencia = false;

        if (faltaIngresarNumeroReferencia) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cierre.numero.refernecia"));
        }
        return resultado;
    }

    public BigDecimal obtenerTotales(TiposMediosPago medioPago) {
        BigDecimal monto = new BigDecimal("0.0");
        try {
            monto = this.listaTotales.stream().filter(elemento -> elemento.getIdMedioPago().equals(medioPago.getIdMedioPago())).findAny().get().getMonto();
        } catch (Exception e) {
        }
        return monto;
    }

    /**
     * Método que guarda un cierre
     *
     * @param evt
     */
    public void guadarCierre(ActionEvent evt) {

        try {
            if (validarCierre()) {
                cierre = new Cierre();

                List<CierreFactura> listaFacturasCierre = new ArrayList<>();
                List<CierreRecibo> listaRecibosCierre = new ArrayList<>();
                List<DetalleCierre> listaDetalles = new ArrayList<>();

                DetalleCierre detalle = new DetalleCierre();

                cierre.setFecha_fin(fechaFin);
                cierre.setFecha_inicio(fechaInicio);
                cierre.setId_tipo_cierre(idTipoCierreSeleccionado);
                cierre.setLogin(Utilitario.obtenerUsuario().getLogin());
                cierre.setFecha_generacion_cierre(Calendar.getInstance().getTime());

                cierre.setMonto_entradas(montoEntradas);
                cierre.setMonto_saludas(montoSalidas);
                cierre.setMonto_anulaciones(montoAnulaciones);
                cierre.setMonto_total(montoEfectivo);
                cierre.setMonto_facturas_credito(montoFacturasCredito);
                cierre.setMonto_facturas_tarjeta(obtenerTotales(TiposMediosPago.TARJETA));//this.listaTotales.stream().filter(elemento -> elemento.getIdMedioPago().equals(TiposMediosPago.TARJETA.getIdMedioPago())).findAny().get().getMonto());
                cierre.setMonto_efectivo(montoEfectivo);
                cierre.setMonto_cheque(obtenerTotales(TiposMediosPago.CHEQUE));//this.listaTotales.stream().filter(elemento -> elemento.getIdMedioPago().equals(TiposMediosPago.CHEQUE.getIdMedioPago())).findAny().get().getMonto());
                cierre.setMonto_transferencia(obtenerTotales(TiposMediosPago.TRANSFERENCIA_DEPÓSITO_BANCARIO));//this.listaTotales.stream().filter(elemento -> elemento.getIdMedioPago().equals(TiposMediosPago.TRANSFERENCIA_DEPÓSITO_BANCARIO.getIdMedioPago())).findAny().get().getMonto());
                cierre.setMonto_recaudado_terceros(obtenerTotales(TiposMediosPago.RECAUDADO_POR_TERCEROS));//this.listaTotales.stream().filter(elemento -> elemento.getIdMedioPago().equals(TiposMediosPago.RECAUDADO_POR_TERCEROS.getIdMedioPago())).findAny().get().getMonto());

                for (SalidasCierreModelo listaSalida : listaSalidas) {
                    detalle.setId_detalle(listaSalida.getConsecutivo());
                    detalle.setMonto(listaSalida.getMonto());
                    detalle.setMotivo_salida(listaSalida.getMotivoSalida());
                    listaDetalles.add(detalle);
                    detalle = new DetalleCierre();
                }

                this.listaReciboCierre.forEach(elemento -> {
                    listaRecibosCierre.add(new CierreRecibo(null, elemento.getId_recibo()));
                });

                servicioFactura.guardarCierre(cierre, listaFacturasCierre, listaDetalles, null, null, listaRecibosCierre, new ArrayList<>());
                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.aviso.guadr.cierre.correcto") + " - " + cierre.getId_cierre());
                this.generarReporteFactura();
                inicializar();
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public byte[] generarReporteFactura() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();

            parametros.put("idCierre", cierre.getId_cierre());

            reporte = servicioReporte.generarReporte(Reportes.CIERRE, TiposMimeTypes.PDF, parametros, true);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.CIERRE.getNombreReporte() + "-" + cierre.getId_cierre() + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException ex) {
            MensajeUtil.agregarMensajeError("Error al generar el informe del cierre sin embargo, el cierre se guardó correctamente");
            ExcepcionManager.manejarExcepcion(ex);
        }

        return reporte;
    }

    public StreamedContent getReporteDesplegar() {
        return reporteDesplegar;
    }
}
