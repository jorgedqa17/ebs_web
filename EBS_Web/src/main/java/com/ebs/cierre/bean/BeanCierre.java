/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.cierre.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.Roles;
import com.ebs.constantes.enums.TipoCondicionVenta;
import com.ebs.constantes.enums.TiposCierre;
import com.ebs.constantes.enums.TiposMediosPago;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.Cierre;
import com.ebs.entidades.CierreFactura;
import com.ebs.entidades.CierreNotasCredito;
import com.ebs.entidades.CierreRecibo;
import com.ebs.entidades.DetalleCierre;
import com.ebs.entidades.TipoCierre;
import com.ebs.modelos.CierreTotales;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.modelos.FacturaCierreModelo;
import com.ebs.modelos.ProductoSolicitudModelo;
import com.ebs.modelos.ReciboCierreModelo;
import com.ebs.modelos.SalidasCierreModelo;
import com.ebs.parametros.servicios.ServicioParametro;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.seguridad.modelos.RolesUsuario;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class BeanCierre {

    @Getter
    @Setter
    private Date fechaInicio;
    @Getter
    @Setter
    private Date fechaFin;
    @Getter
    @Setter
    private Long idTipoCierreSeleccionado;
    @Inject
    private ServicioFactura servicioFactura;
    @Inject
    private ServicioProducto servicioProducto;
    @Getter
    @Setter
    private List<FacturaCierreModelo> listaFacturas;
    @Getter
    @Setter
    private List<FacturaCierreModelo> listaFacturasAnulacion;
    @Getter
    @Setter
    private List<SalidasCierreModelo> listaSalidas;
    @Getter
    @Setter
    private SalidasCierreModelo salida;
    @Getter
    @Setter
    private List<TipoCierre> tiposCierre;
    @Getter
    @Setter
    private boolean tieneRolCierreSemanal = false;
    @Getter
    @Setter
    private BigDecimal montoEntradas;
    @Getter
    @Setter
    private BigDecimal montoSalidas;
    @Getter
    @Setter
    private BigDecimal montoAnulaciones;
    @Getter
    @Setter
    private BigDecimal montoTotal;
    @Getter
    @Setter
    private BigDecimal montoFacturasCredito;
    @Getter
    @Setter
    private BigDecimal montoFacturasTarjeta;
    @Getter
    @Setter
    private BigDecimal montoEfectivo;
    @Getter
    @Setter
    private BigDecimal montoCheque;
    @Inject
    private ServicioReporte servicioReporte;
    @Setter
    StreamedContent reporteDesplegar = null;
    @Getter
    @Setter
    private Cierre cierre;
    @Getter
    @Setter
    public List<ProductoSolicitudModelo> listaProductos;
    @Getter
    @Setter
    public List<CierreTotales> listaTotales;
    @Getter
    @Setter
    public List<ProductoSolicitudModelo> listaProductosFiltros;
    @Getter
    @Setter
    private List<Bodega> bodegas;
    @Inject
    private ServicioBodega servicioBodega;
    @Getter
    @Setter
    private Long bodegaSeleccionada;
    @Getter
    @Setter
    private boolean inhabilitarFechaInicio;
    @Inject
    private ServicioParametro servicioParametro;

    @Getter
    @Setter
    private List<ReciboCierreModelo> listaReciboCierre;
    @Getter
    @Setter
    private List<FacturaCierreModelo> listaFacturasCheque;
    @Getter
    @Setter
    private List<FacturaCierreModelo> listaFacturasTransferencia;

    public BeanCierre() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            this.bodegas = servicioBodega.obtenerListaBodegas();
            listaProductos = new ArrayList<>();
            listaTotales = new ArrayList<>();
            cierre = new Cierre();
            listaFacturas = new ArrayList<>();
            listaFacturasTransferencia = new ArrayList<>();
            listaFacturasCheque = new ArrayList<>();
            listaFacturasAnulacion = new ArrayList<>();
            salida = new SalidasCierreModelo();
            listaSalidas = new ArrayList<>();
            tiposCierre = servicioFactura.obtenerTiposCierre();
            //    calcularMontos();
            validarRoles();
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
            this.bodegaSeleccionada = Long.parseLong(servicioParametro.obtenerValorParametro(ParametrosEnum.BODEGA_PRINCIPAL.getIdParametro()).getValor());

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void validarRoles() {
        for (RolesUsuario rolesUsuario : Utilitario.obtenerRolesUsuario()) {
            if (rolesUsuario.getIdRol().equals(Roles.GENERACION_CIERRE.getIdRol())) {
                tieneRolCierreSemanal = true;
            }
        }
    }

    public void seleccionarTipoCierre(ValueChangeEvent evt) {
        idTipoCierreSeleccionado = (Long) evt.getNewValue();
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
                listaProductos = new ArrayList<>();
                listaTotales = new ArrayList<>();
                cierre = new Cierre();
                listaFacturas = new ArrayList<>();
                listaFacturasTransferencia = new ArrayList<>();
                listaFacturasCheque = new ArrayList<>();
                listaFacturasAnulacion = new ArrayList<>();
                salida = new SalidasCierreModelo();
                listaSalidas = new ArrayList<>();

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fechaInicio);

                //Obtengo todas las facturas
                listaFacturas = servicioFactura.obtenerFacturasParaCierre(Utilitario.obtenerUsuario().getLogin(), df.format(calendar.getTime()), df.format(fechaFin));
                listaTotales = servicioFactura.obtenerTotalesCierre(Utilitario.obtenerUsuario().getLogin(), df.format(calendar.getTime()), df.format(fechaFin));
                listaFacturasAnulacion = servicioFactura.obtenerFacturasParaCierreAnulacion(Utilitario.obtenerUsuario().getLogin(), df.format(calendar.getTime()), df.format(fechaFin));
                listaReciboCierre = servicioFactura.obtenerRecibosParaCierre(Utilitario.obtenerUsuario().getLogin(), df.format(calendar.getTime()), df.format(fechaFin));

                this.listaFacturas.forEach(elemento -> {
                    if (elemento.getIdMedioPago().equals(TiposMediosPago.CHEQUE.getIdMedioPago())) {
                        this.listaFacturasCheque.add(elemento);
                    } else if (elemento.getIdMedioPago().equals(TiposMediosPago.TRANSFERENCIA_DEPÓSITO_BANCARIO.getIdMedioPago())) {
                        this.listaFacturasTransferencia.add(elemento);
                    }
                });
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

    public void onCellEdit(CellEditEvent event) {
    }

    public void calcularMontoEntregar() {
        montoSalidas = new BigDecimal("0.0");
        montoEntradas = new BigDecimal("0.0");
        montoFacturasCredito = new BigDecimal("0.0");
        montoAnulaciones = new BigDecimal("0.0");
        //   boolean efectivoEsCero = false;
        Optional<CierreTotales> busqueda = this.listaTotales.stream().filter(elemento -> elemento.getIdMedioPago().equals(TiposMediosPago.EFECTIVO.getIdMedioPago())).findAny();
        if (busqueda.isPresent()) {
            montoEfectivo = busqueda.get().getMonto();

        } else {
            montoEfectivo = new BigDecimal("0.0");
            // efectivoEsCero = true;
        }

        //rebajo las salidas
        for (SalidasCierreModelo listaSalida : listaSalidas) {
            montoSalidas = montoSalidas.add(listaSalida.getMonto());
        }
        // if (!efectivoEsCero) {
        montoEfectivo = montoEfectivo.subtract(montoSalidas);
        // }

        //rebajo las facturas que son a credito pero que son en efectivo
        for (FacturaCierreModelo factura : listaFacturas) {
            if (factura.getIdCondicionVenta().equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta()) /*&& factura.getIdMedioPago().equals(TiposMediosPago.EFECTIVO.getIdMedioPago())*/) {
                montoFacturasCredito = montoFacturasCredito.add(factura.getTotalComprobante());
            }
        }
        for (FacturaCierreModelo factura : listaFacturas) {
            if (factura.getIdCondicionVenta().equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta()) && factura.getIdMedioPago().equals(TiposMediosPago.EFECTIVO.getIdMedioPago())) {

                montoEfectivo = montoEfectivo.subtract(factura.getTotalComprobante());
            }
        }
        //  if (!efectivoEsCero) {

        // }
        for (FacturaCierreModelo facturaCierreModelo : listaFacturasAnulacion) {
            montoAnulaciones = montoAnulaciones.add(facturaCierreModelo.getTotalComprobante());
        }

        for (CierreTotales cierreTotal : listaTotales) {
            montoEntradas = montoEntradas.add(cierreTotal.getMonto());
        }
    }

//    public void calcularMontos() {
//        montoEntradas = new BigDecimal("0.0");
//        montoSalidas = new BigDecimal("0.0");
//        montoAnulaciones = new BigDecimal("0.0");
//        montoFacturasCredito = new BigDecimal("0.0");
//        montoFacturasTarjeta = new BigDecimal("0.0");
//        montoEfectivo = new BigDecimal("0.0");
//        montoCheque = new BigDecimal("0.0");
//        montoTotal = new BigDecimal("0.0");
//        for (FacturaCierreModelo listaFactura : listaFacturas) {
//            montoEntradas = montoEntradas.add(listaFactura.getTotalComprobante());
//        }
//        for (FacturaCierreModelo factura : listaFacturas) {
//            if (factura.getIdCondicionVenta().equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta())) {
//                montoFacturasCredito = montoFacturasCredito.add(factura.getTotalComprobante());
//            }
//        }
//        for (FacturaCierreModelo factura : listaFacturas) {
//            if (!factura.getIdCondicionVenta().equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta()) && factura.getIdMedioPago().equals(TiposMediosPago.TARJETA.getIdMedioPago())) {
//                montoFacturasTarjeta = montoFacturasTarjeta.add(factura.getTotalComprobante());
//            }
//        }
//        for (FacturaCierreModelo factura : listaFacturas) {
//            if (!factura.getIdCondicionVenta().equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta()) && factura.getIdMedioPago().equals(TiposMediosPago.EFECTIVO.getIdMedioPago())) {
//                montoEfectivo = montoEfectivo.add(factura.getTotalComprobante());
//            }
//        }
//        for (FacturaCierreModelo factura : listaFacturas) {
//            if (!factura.getIdCondicionVenta().equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta()) && factura.getIdMedioPago().equals(TiposMediosPago.CHEQUE.getIdMedioPago())) {
//                montoCheque = montoCheque.add(factura.getTotalComprobante());
//            }
//        }
//
//        for (SalidasCierreModelo listaSalida : listaSalidas) {
//            montoSalidas = montoSalidas.add(listaSalida.getMonto());
//        }
//        for (FacturaCierreModelo facturaCierreModelo : listaFacturasAnulacion) {
//            montoAnulaciones = montoAnulaciones.add(facturaCierreModelo.getTotalComprobante());
//        }
//
//        montoTotal = montoEntradas.subtract(montoSalidas).subtract(montoFacturasCredito).subtract(montoFacturasTarjeta).subtract(montoCheque);
//    }
    public boolean validarCierre() {
        boolean resultado = true;
        boolean faltaIngresarNumeroReferencia = false;

        for (FacturaCierreModelo facturaCierreModelo : this.listaFacturasCheque) {
            if (facturaCierreModelo.getNumeroReferencia() == null || (facturaCierreModelo.getNumeroReferencia().equals(""))) {
                faltaIngresarNumeroReferencia = true;
                break;
            }
        }
        for (FacturaCierreModelo facturaCierreModelo : this.listaFacturasTransferencia) {
            if (facturaCierreModelo.getNumeroReferencia() == null || (facturaCierreModelo.getNumeroReferencia().equals(""))) {
                //faltaIngresarNumeroReferencia = true;
                break;
            }
        }
        if (faltaIngresarNumeroReferencia) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cierre.numero.refernecia"));
        }
        if (bodegaSeleccionada.equals(0l)) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cierre.seleccion.bodea.destino"));
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
                List<CierreFactura> listaFacturasCierreRespaldo = new ArrayList<>();
                List<CierreRecibo> listaRecibosCierre = new ArrayList<>();
                List<CierreNotasCredito> listaFacturasNotasCredito = new ArrayList<>();
                List<DetalleCierre> listaDetalles = new ArrayList<>();
                CierreFactura cierreFactura = new CierreFactura();
                CierreNotasCredito cierreNotasCredito = new CierreNotasCredito();

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
                cierre.setMonto_efectivo(obtenerTotales(TiposMediosPago.EFECTIVO));
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
                for (FacturaCierreModelo filaCierre : this.listaFacturas) {
                    cierreFactura.setId_factura(filaCierre.getIdFactura());
                    listaFacturasCierre.add(cierreFactura);
                    cierreFactura = new CierreFactura();
                }
                for (FacturaCierreModelo facturaCierreModelo : listaFacturasAnulacion) {
                    //  if (permiteAgregar(listaFacturasCierre, facturaCierreModelo.getIdFactura())) {
                    cierreNotasCredito.setId_anulacion(facturaCierreModelo.getIdAnulacion());
                    listaFacturasNotasCredito.add(cierreNotasCredito);
                    cierreNotasCredito = new CierreNotasCredito();
                    //  }
                }
                this.listaReciboCierre.forEach(elemento -> {
                    listaRecibosCierre.add(new CierreRecibo(null, elemento.getId_recibo()));
                });

                servicioFactura.guardarCierre(cierre, listaFacturasCierre, listaDetalles, null, null, listaRecibosCierre, listaFacturasNotasCredito);
                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.aviso.guadr.cierre.correcto") + " - " + cierre.getId_cierre());
                this.generarReporteFactura();
                inicializar();
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    private boolean permiteAgregar(List<CierreFactura> listaFacturasCierre, Long idFactura) {
        boolean resultado = true;
        for (CierreFactura factura : listaFacturasCierre) {
            if (factura.getId_factura().equals(idFactura)) {
                resultado = false;
                break;
            }
        }
        return resultado;
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
