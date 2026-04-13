/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.ebs.constantes.enums.EstadoNotaDebito;
import com.ebs.constantes.enums.FacturaEnvioCorreos;
import com.ebs.constantes.enums.LineaDetalleEstado;
import com.ebs.constantes.enums.SituacionComprobante;
import com.ebs.constantes.enums.TipoDocumento;
import com.ebs.constantes.enums.TipoProducto;
import com.ebs.entidades.AnulacionFactura;
import com.ebs.entidades.FacturaDebito;
import com.ebs.entidades.MotivoDebito;
import com.ebs.entidades.TipoDocumentoReferencia;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.facturacion.servicios.ServicioNotaDebito;
import com.ebs.modelos.FacturaModeloAnulacion;
import com.ebs.modelos.LineaDetalleFactura;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Data;
import javax.inject.Inject;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author jdquesad
 */
@Data
@ManagedBean
@ViewScoped
public class BeanNotaDebito {

    @Inject
    private ServicioNotaDebito servicioNotaDebito;
    @Inject
    private ServicioFactura servicioFactura;
    private List<AnulacionFactura> listaNotasCredito;
    private List<AnulacionFactura> listaNotasCreditoFiltro;
    private AnulacionFactura notaCreditoSeleccionada;
    private List<TipoDocumentoReferencia> listaTipoDocumentoReferencia;
    private List<MotivoDebito> listaMotivoDebito;
    private Long motivoDebitoSeleccionado;
    private Long tipoDocumentoReferenciaSeleccionado;
    private String motivoDebito;
    private FacturaModeloAnulacion facturaNotaCreditoActual;
    private FacturaModeloAnulacion facturaNotaCreditoDespues;
    private boolean inhabilitarComponentes;
    private BigDecimal totalGravados;
    private BigDecimal totalGravadosServicios;
    private BigDecimal totalGravadosMercancias;
    private BigDecimal totalExentos;
    private BigDecimal totalExentosServicios;
    private BigDecimal totalExentosMercancias;
    private BigDecimal totalDescuentos;
    private BigDecimal totalDescuentosLineas;
    private Integer descuentoFactura;
    private BigDecimal totalVenta;
    private BigDecimal totalVentaNeta;
    private BigDecimal totalImpuestos;
    private BigDecimal totalFactura;
    private List<LineaDetalleFactura> listaLineasAsociadasNotaCredito;
    private boolean esNotaDebitoInterna;

    @PostConstruct
    public void inicializar() {
        try {
            this.inhabilitarComponentes = true;
            this.esNotaDebitoInterna = false;
            this.tipoDocumentoReferenciaSeleccionado = 0L;
            this.motivoDebitoSeleccionado = 0L;
            this.notaCreditoSeleccionada = new AnulacionFactura();
            this.listaNotasCredito = servicioNotaDebito.obtenerNotasCredito();
            listaMotivoDebito = servicioFactura.obtenerTiposMotivosDebitos();
            listaTipoDocumentoReferencia = servicioFactura.obtenerTiposDocumentosReferencia();
//            this.facturaNotaCreditoActual = new FacturaModeloAnulacion();
//            this.facturaNotaCreditoActual.setFactura(new Factura());
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarNotaCredito(AnulacionFactura notaCreditoSeleccionada) {
        this.notaCreditoSeleccionada = notaCreditoSeleccionada;
        this.esNotaDebitoInterna = this.notaCreditoSeleccionada.getNota_credito_interna().equals(1);
        if (this.esNotaDebitoInterna) {
            this.motivoDebito = "Se anula nota de crédito interna";
            this.motivoDebitoSeleccionado = 6L;
            this.tipoDocumentoReferenciaSeleccionado = 1L;
        }

        this.facturaNotaCreditoActual = this.servicioFactura.obtenerFacturaModeloAnulacion(this.notaCreditoSeleccionada.getId_factura());
        this.listaLineasAsociadasNotaCredito = this.servicioFactura.obtenerDetalleFacturaLineasNotaCredito(this.notaCreditoSeleccionada.getId_anulacion());
        this.facturaNotaCreditoDespues = this.facturaNotaCreditoActual;
        this.inhabilitarComponentes = false;
        indicarTodasLineasNotaCreditoParaNotaDebito();
    }

    public void indicarTodasLineasNotaCreditoParaNotaDebito() {
        this.listaLineasAsociadasNotaCredito.forEach(elemento -> {
            elemento.getDetalleFactura().setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO_QUITAR.getEstadoLineaDetalle());
            elemento.getDetalleFactura().setEs_para_nota_debito(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle());
        });

    }

    public boolean validarNotasDebito() {
        boolean resultado = true;
        if (motivoDebito == null || motivoDebito.equals("")) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.valudacion.debe.agregar.motivo.debito"));
            resultado = false;
        }
        if (this.motivoDebitoSeleccionado.equals(0l)) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.valudacion.debe.agregar.motivo.debito.lista"));
            resultado = false;
        }
        if (notaCreditoSeleccionada == null) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.selccione.nota.credito"));
            resultado = false;

        }
        return resultado;

    }

    public void crearNotaCredito() {
        try {
            if (this.validarNotasDebito()) {
                FacturaDebito notaDebito = new FacturaDebito();

                notaDebito.setId_anulacion(this.notaCreditoSeleccionada.getId_anulacion());
                notaDebito.setLogin(Utilitario.obtenerUsuario().getLogin());
                notaDebito.setId_bodega(Utilitario.obtenerIdBodegaUsuario());
                notaDebito.setIp(JSFUtil.obtenerIpComputadora());
                notaDebito.setNombre_maquina(JSFUtil.obtenerNombreMaquina());
                notaDebito.setMotivo_nota_debito(motivoDebito);
                notaDebito.setNota_debito_interna(this.esNotaDebitoInterna ? 1 : 0);

                notaDebito.setId_tipo_doc_referencia(tipoDocumentoReferenciaSeleccionado);

                notaDebito.setId_motivo_nota_debito(motivoDebitoSeleccionado);
                notaDebito.setEnvio_correo_electronico(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());

                notaDebito.setTotal_descuento(this.notaCreditoSeleccionada.getTotal_descuento());
                notaDebito.setTotal_impuesto(this.notaCreditoSeleccionada.getTotal_impuesto());
                notaDebito.setTotal_venta_neta(this.notaCreditoSeleccionada.getTotal_venta_neta());
                notaDebito.setTotal_venta(this.notaCreditoSeleccionada.getTotal_venta());
                notaDebito.setTotal_servicios_grabados(this.notaCreditoSeleccionada.getTotal_servicios_grabados());
                notaDebito.setTotal_servicios_exentos(this.notaCreditoSeleccionada.getTotal_servicios_exentos());
                notaDebito.setTotal_mercancias_gravadas(this.notaCreditoSeleccionada.getTotal_mercancias_gravadas());
                notaDebito.setTotal_mercancias_exentas(this.notaCreditoSeleccionada.getTotal_mercancias_exentas());
                notaDebito.setTotal_gravado(this.notaCreditoSeleccionada.getTotal_gravado());
                notaDebito.setTotal_exento(this.notaCreditoSeleccionada.getTotal_exento());
                notaDebito.setTotal_nota_debito(this.notaCreditoSeleccionada.getTotal_nota_credito());
                notaDebito.setTotal_servicios_exonerados(this.notaCreditoSeleccionada.getTotal_servicios_exenonerados());
                notaDebito.setTotal_mercancias_exonerados(this.notaCreditoSeleccionada.getTotal_mercancias_exenonerados());
                notaDebito.setTotal_exonerado(this.notaCreditoSeleccionada.getTotal_exonerado());

                notaDebito = this.servicioNotaDebito.guardarNotaDebito(notaDebito, notaCreditoSeleccionada, this.facturaNotaCreditoDespues.getFactura());

                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.confirmacoin.guardado.nota.debito") + "-" + notaDebito.getId_nota_debito());
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public List<LineaDetalleFactura> obtenerProductoGravados(boolean mercancias) {
        List<LineaDetalleFactura> retorno = new ArrayList<>();
        if (this.facturaNotaCreditoDespues != null) {
            if (mercancias) {
                for (LineaDetalleFactura producto : this.listaLineasAsociadasNotaCredito) {
                    if (producto.isEsParaNotaDebito()) {
                        if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                            if (producto.getImpuesto() != null) {
                                retorno.add(producto);
                            }
                        }
                    }
                }
            } else {
                for (LineaDetalleFactura producto : this.listaLineasAsociadasNotaCredito) {
                    if (producto.isEsParaNotaDebito()) {
                        if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {

                            if (producto.getImpuesto() != null) {
                                retorno.add(producto);
                            }
                        }
                    }
                }
            }
        }
        return retorno;
    }

    public BigDecimal getTotalGravadosServicios() {
        if (this.facturaNotaCreditoActual != null) {
            totalGravadosServicios = this.facturaNotaCreditoActual.getFactura().getTotal_servicios_grabados();
        } else {
            totalGravadosServicios = new BigDecimal("0.0");
        }

        List<LineaDetalleFactura> retorno = obtenerProductoGravados(false);
        for (LineaDetalleFactura modeloProducto : retorno) {
            if (modeloProducto.isEsParaNotaDebito()) {
                totalGravadosServicios = totalGravadosServicios.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
            }
        }
        return totalGravadosServicios;
    }

    public BigDecimal getTotalGravadosMercancias() {
        if (this.facturaNotaCreditoActual != null) {
            totalGravadosMercancias = this.facturaNotaCreditoActual.getFactura().getTotal_mercancias_gravadas();
        } else {
            totalGravadosMercancias = new BigDecimal("0.0");
        }

        if (this.facturaNotaCreditoDespues != null) {
            List<LineaDetalleFactura> retorno = obtenerProductoGravados(true);
            for (LineaDetalleFactura modeloProducto : retorno) {
                if (modeloProducto.isEsParaNotaDebito()) {
                    totalGravadosMercancias = totalGravadosMercancias.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                }
            }
        }
        return totalGravadosMercancias;
    }

    public BigDecimal getTotalExentos() {

        if (this.facturaNotaCreditoActual != null) {
            totalExentos = this.facturaNotaCreditoActual.getFactura().getTotal_exento();
        } else {
            totalExentos = new BigDecimal("0.0");
        }
        if (this.facturaNotaCreditoDespues != null) {
            for (LineaDetalleFactura producto : this.listaLineasAsociadasNotaCredito) {
                if (producto.isEsParaNotaDebito()) {
                    if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
                        totalExentos = totalExentos.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                    }
                }
            }
        }
        return totalExentos;
    }

    public BigDecimal getTotalGravados() {

        if (this.facturaNotaCreditoActual != null) {
            totalGravados = this.facturaNotaCreditoActual.getFactura().getTotal_gravado();
        } else {
            totalGravados = new BigDecimal("0.0");
        }

        if (this.facturaNotaCreditoDespues != null) {
            for (LineaDetalleFactura producto : this.listaLineasAsociadasNotaCredito) {
                if (producto.isEsParaNotaDebito()) {
                    if (producto.getImpuesto() != null && producto.getExoneracion() == null) {
                        totalGravados = totalGravados.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                    }
                }
            }
        }
        return totalGravados;
    }

    public BigDecimal getTotalExentosServicios() {
        if (this.facturaNotaCreditoActual != null) {
            totalExentosServicios = this.facturaNotaCreditoActual.getFactura().getTotal_servicios_exentos();
        } else {
            totalExentosServicios = new BigDecimal("0.0");
        }

        if (this.facturaNotaCreditoDespues != null) {
            for (LineaDetalleFactura producto : this.listaLineasAsociadasNotaCredito) {
                if (producto.isEsParaNotaDebito()) {
                    if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                        if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
                            totalExentosServicios = totalExentosServicios.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                        }
                    }
                }
            }
        }
        return totalExentosServicios;
    }

    public BigDecimal getTotalExentosMercancias() {

        if (this.facturaNotaCreditoActual != null) {
            totalExentosMercancias = this.facturaNotaCreditoActual.getFactura().getTotal_mercancias_exentas();
        } else {
            totalExentosMercancias = new BigDecimal("0.0");
        }

        if (this.facturaNotaCreditoDespues != null) {
            for (LineaDetalleFactura producto : this.listaLineasAsociadasNotaCredito) {
                if (producto.isEsParaNotaDebito()) {
                    if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                        if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
                            totalExentosMercancias = totalExentosMercancias.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                        }
                    }
                }
            }
        }
        return totalExentosMercancias;
    }

    public BigDecimal getTotalDescuentos() {
        return totalDescuentos;
    }

    public BigDecimal getTotalDescuentosLineas() {

        if (this.facturaNotaCreditoActual != null) {
            totalDescuentosLineas = this.facturaNotaCreditoActual.getFactura().getTotal_descuentos();
        } else {
            totalDescuentosLineas = new BigDecimal("0.0");
        }

        if (this.facturaNotaCreditoDespues != null) {
            for (LineaDetalleFactura producto : this.listaLineasAsociadasNotaCredito) {
                if (producto.isEsParaNotaDebito()) {
                    totalDescuentosLineas = totalDescuentosLineas.add(producto.getMontoDescuento() == null ? new BigDecimal("0") : producto.getMontoDescuento()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                }
            }
        }
        return totalDescuentosLineas;
    }

    public Integer getDescuentoFactura() {
        return descuentoFactura;
    }

    public BigDecimal getTotalVenta() {

        if (this.facturaNotaCreditoActual != null) {
            totalVenta = this.facturaNotaCreditoActual.getFactura().getTotal_venta();
        } else {
            totalVenta = new BigDecimal("0.0");
        }

        if (this.facturaNotaCreditoDespues != null) {
            totalVenta = getTotalGravados().add(getTotalExentos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        }
        return totalVenta;
    }

    public BigDecimal getTotalVentaNeta() {
        totalVentaNeta = getTotalVenta().subtract(getTotalDescuentosLineas());
        return totalVentaNeta;
    }

    public BigDecimal getTotalImpuestos() {
        if (this.facturaNotaCreditoActual != null) {
            totalImpuestos = this.facturaNotaCreditoActual.getFactura().getTotal_impuestos();
        } else {
            totalImpuestos = new BigDecimal("0.0");
        }

        if (this.facturaNotaCreditoDespues != null) {
            for (LineaDetalleFactura producto : this.listaLineasAsociadasNotaCredito) {
                if (producto.isEsParaNotaDebito()) {
                    producto.calcularImpuesto();
                    totalImpuestos = totalImpuestos.add(producto.getTotalImpuesto() == null ? new BigDecimal("0.0") : producto.getTotalImpuesto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                }
            }
        }
        return totalImpuestos;
    }

    public BigDecimal getTotalFactura() {
        totalFactura = new BigDecimal("0.0");
        totalFactura = totalFactura.add(getTotalVentaNeta()).add(getTotalImpuestos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        return totalFactura;
    }

}
