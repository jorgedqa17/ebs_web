/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.ebs.constantes.enums.EstadoAnulacion;
import com.ebs.constantes.enums.EstadoFactura;
import com.ebs.constantes.enums.EstadosLineasFactura;
import com.ebs.constantes.enums.FacturaEnvioCorreos;
import com.ebs.constantes.enums.Indicadores;
import com.ebs.constantes.enums.LineaDetalleEstado;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TipoFacturaEnum;
import com.ebs.constantes.enums.TipoProducto;
import com.ebs.constantes.enums.TipoTarifaTimpuestoEnum;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.AnulacionFactura;
import com.ebs.entidades.Cliente;
import com.ebs.entidades.DetalleFactura;
import com.ebs.entidades.DetalleFacturaNotaCredito;
import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
import com.ebs.entidades.MotivoAnulacion;
import com.ebs.entidades.Persona;
import com.ebs.entidades.Producto;
import com.ebs.entidades.TipoDocumentoReferencia;
import com.ebs.entidades.TipoTarifaImpuesto;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.facturacion.servicios.ServicioNotaDebito;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.ExoneracionLinea;
import com.ebs.modelos.FacturaModeloAnulacion;
import com.ebs.modelos.LineaDetalleFactura;
import com.ebs.modelos.ModeloProducto;
import com.ebs.modelos.NotaDebitoModelo;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import com.powersystem.utilitario.UtilitarioNotasDebitoCredito;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import lombok.Data;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Jorge GBSYS
 */
@ManagedBean
@ViewScoped
@Data
public class BeanAnulacionFactura {

    @Inject
    private ServicioReporte servicioReporte;
    @Inject
    private ServicioFactura servicioFactura;
    @Inject
    private ServicioInventario servicioInventario;
    @Inject
    private ServicioProducto servicioProducto;
    @Inject
    private ServicioPersona servicioPersona;
    @Setter
    StreamedContent reporteDesplegar = null;
    @Setter
    StreamedContent notaCredito = null;
    private List<FacturaModeloAnulacion> listaFacturas;
    private List<FacturaModeloAnulacion> listaFacturasSeleccionadas;
    private List<MotivoAnulacion> listaMotivosAnulacion;
    private List<TipoDocumentoReferencia> listaTipoDocumentoReferencia;
    private FacturaModeloAnulacion facturaSeleccionada;
    private Long motivoAnulacionSeleccionado;
    private Long tipoDocumentoReferenciaSeleccionado;
    String motivoAnulacion;
    private AnulacionFactura anulacion;

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
    private BigDecimal totalExonerados;
    private BigDecimal totalExoneradosServicios;
    private BigDecimal totalExoneradosMercancias;

    private List<LineaDetalleFactura> listaDetalleFacturaFiltro;
    private boolean inhabilitarControles;
    @Inject
    private ServicioNotaDebito servicioNotaDebito;
    private List<NotaDebitoModelo> listaNotasDebito;
    private List<NotaDebitoModelo> listaNotasDebitoFiltro;
    private boolean esNotaCreditoParaFactura;
    private NotaDebitoModelo notaDebitoSeleccionada;
    private List<LineaDetalleFactura> listaLineasDetalleNotaDebito;
    private boolean notaDebitoAsociadaFactura;
    private boolean puedeCrearNotaCredito;
    private Persona personaEdicionFactura;
    private List<ModeloProducto> listaProductoPorFacturar;
    private List<TipoTarifaImpuesto> listaTiposTarifa;
    private boolean esNotaCreditoInterna;

    public BeanAnulacionFactura() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            esNotaCreditoInterna = false;
            listaTiposTarifa = servicioProducto.obtenerListaTiposTarifas();
            notaDebitoSeleccionada = new NotaDebitoModelo();
            esNotaCreditoParaFactura = true;
            puedeCrearNotaCredito = false;
            inhabilitarControles = true;
            notaDebitoAsociadaFactura = false;
            this.motivoAnulacionSeleccionado = 0L;
            this.tipoDocumentoReferenciaSeleccionado = 0L;
            listaFacturas = servicioFactura.obtenerFacturasAnulacion(Utilitario.obtenerUsuario().getLogin());
            listaMotivosAnulacion = servicioFactura.obtenerTiposMotivosAnulacion();
            listaTipoDocumentoReferencia = servicioFactura.obtenerTiposDocumentosReferencia();
            this.listaNotasDebito = servicioNotaDebito.obtenerNotasDebito();
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarFactura(FacturaModeloAnulacion facturaSeleccionada) {
        try {
            if (facturaSeleccionada.getCantidadDias() >= 364) {
                MensajeUtil.agregarMensajeAdvertencia("No puede crear una nota de crédito sobre una factura que supera los 364 días de haber sido emitida");
                return;
            }
            this.facturaSeleccionada = this.servicioFactura.obtenerFacturaModeloAnulacion(facturaSeleccionada.getIdFactura());
            determinarLineasNoModificables();
            esNotaCreditoParaFactura = true;
            Cliente clienteSeleccionado = null;

            List<DetalleFactura> listaDetalleFacturaEdicion = servicioFactura.obtenerDetalleFacturaBusqueda(this.facturaSeleccionada.getFactura().getId_factura());
            // List<InventarioSalidaDetalle> listaSalidasDetalle = servicioInventario.obtenerInvSaliDetallePorIdFactura(facturaEdicion.getIdFactura());

            if (this.facturaSeleccionada.getFactura().getId_cliente() != null) {
                clienteSeleccionado = servicioPersona.obtenerClientePorIdCliente(this.facturaSeleccionada.getFactura().getId_cliente());
                personaEdicionFactura = servicioPersona.obtenerPersonaPorNumeroCedula(clienteSeleccionado.getNumero_cedula());
            }

            listaProductoPorFacturar = new ArrayList<>();
            Producto detalleFacturaEdicion = null;
            ModeloProducto productoDelDetalle = null;

            for (DetalleFactura detalleFactura : listaDetalleFacturaEdicion) {
                detalleFacturaEdicion = new Producto();
                productoDelDetalle = new ModeloProducto();
                if (detalleFactura.getDetallePK().getNumero_linea().equals(10)) {
                    System.out.println("");
                }
                detalleFacturaEdicion = servicioProducto.obtenerProductoPorIdProducto(detalleFactura.getDetallePK().getId_producto());

                productoDelDetalle = ModeloProducto.construirObjeto(detalleFacturaEdicion,
                        servicioProducto.obtenerUnidadMedidaProducto(detalleFacturaEdicion.getId_unidad_medida()),
                        servicioProducto.obtenerListaPreciosProducto(detalleFacturaEdicion.getId_producto()),
                        new ArrayList<>(),
                        servicioProducto.obtenerExistenciaProducto(detalleFacturaEdicion.getId_producto()),
                        clienteSeleccionado,
                        new ArrayList<>(),
                        servicioProducto.obtenerTipoProducto(detalleFacturaEdicion.getId_tipo_producto()),
                        servicioProducto.obtenerImpuesto(detalleFactura.getId_impuesto()),
                        null,
                        servicioProducto.obtenerTipoTarifaImpuesto(detalleFactura.getId_tipo_tarifa()),
                        personaEdicionFactura,
                        listaTiposTarifa);

                detalleFacturaEdicion.setInd_exento(detalleFactura.getEs_linea_exenta());
                detalleFacturaEdicion.setInd_exonerado(detalleFactura.getEs_linea_exonerada());
                productoDelDetalle.setIdTipoTarifaSeleccionada(detalleFactura.getId_tipo_tarifa());
                productoDelDetalle.setProducto(detalleFacturaEdicion);
                productoDelDetalle.setCantidad(detalleFactura.getCantidad());
                productoDelDetalle.setDescuento(detalleFactura.getDescuento());
                productoDelDetalle.setMontoDescuento(detalleFactura.getTotal_descuento());
                productoDelDetalle.setTotalImpuesto(detalleFactura.getTotal_impuestos());
                productoDelDetalle.setSubTotal(detalleFactura.getSub_total());
                productoDelDetalle.setDescripcionLineaProducto(detalleFactura.getDescripcion());
                productoDelDetalle.setIdTipPrecioSeleccionado(detalleFactura.getId_tipo_precio());
                productoDelDetalle.setPrecio(detalleFactura.getPrecio_neto());
                productoDelDetalle.setNumeroLineaProducto(detalleFactura.getDetallePK().getNumero_linea());
                productoDelDetalle.setPermiteEliminar(!this.facturaSeleccionada.getFactura().getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()));
                productoDelDetalle.setIdFactura(detalleFactura.getDetallePK().getId_factura());
                if (detalleFactura.getId_exoneracion() != null) {
                    ExoneracionLinea exoLinea = new ExoneracionLinea();
                    exoLinea.setId_exoneracion(detalleFactura.getId_exoneracion());
                    exoLinea.setFecha_emision_str(detalleFactura.getFecha_emision());
                    exoLinea.setNombre_institucion(detalleFactura.getNombre_institucion());
                    exoLinea.setNumero_documento(detalleFactura.getNumero_documento());
                    exoLinea.setPorcentaje_exonerado(detalleFactura.getPorcentaje_exonerado());
                    productoDelDetalle.setExoneracionLinea(exoLinea);

                }

                productoDelDetalle.calcularMontos();

                listaProductoPorFacturar.add(productoDelDetalle);
            }

            for (ModeloProducto modeloProducto : listaProductoPorFacturar) {
                modeloProducto.calcularMontos();
            }

            if (this.facturaSeleccionada.getFactura().getEstado_factura().equals(EstadoFactura.RECHAZADA_HACIENDA.getEstadoFactura())) {
                this.facturaSeleccionada.getListaDetalleFactura().forEach(elemento -> {
                    elemento.setEsParaNotaCredito(true);
                    elemento.getDetalleFactura().setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO.getEstadoLineaDetalle());
                    //elemento.setPuedeModificar(false);
                    elemento.setEsParaNotaCredito(true);
                });
                calcularMontos();
                this.tipoDocumentoReferenciaSeleccionado = 1L;
                this.motivoAnulacionSeleccionado = 4L;
                this.motivoAnulacion = "Nota de crédito interna para reemplazo de factura rechaza por hacienda";
                esNotaCreditoInterna = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            ExcepcionManager.manejarExcepcion(e);
        }

    }

    public void determinarLineasNoModificables() {
        Integer contador = 0;
        if (this.facturaSeleccionada.getListaDetalleFactura() != null) {
            this.facturaSeleccionada.getListaDetalleFactura().forEach(elemento -> {
                if (elemento.getDetalleFactura().getEs_para_nota_credito().equals(LineaDetalleEstado.PARA_NOTA_CREDITO.getEstadoLineaDetalle())) {
                    elemento.setPuedeModificar(false);
                } else {
                    elemento.setPuedeModificar(true);
                }
            });
            inhabilitarControles = false;

        }
        contador = contador + this.facturaSeleccionada.getListaDetalleFactura().stream()
                .mapToInt(o -> (!o.isPuedeModificar() ? 1 : 0))
                .sum();
        if (contador.equals(this.facturaSeleccionada.getListaDetalleFactura().size())) {
            puedeCrearNotaCredito = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.aviso.lineas.facturas.en.nota.credito"));
        } else {
            puedeCrearNotaCredito = true;
        }
    }

    public void seleccionarLineaDetalle(LineaDetalleFactura lineaSeleccionada) {
        this.facturaSeleccionada.getListaDetalleFactura().forEach(elemento -> {
            if (elemento.equals(lineaSeleccionada)) {
                elemento.setEsParaNotaCredito(true);
                elemento.getDetalleFactura().setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO.getEstadoLineaDetalle());
            }
        });
        calcularMontos();
    }

    public void seleccionarLineaDetalleQuitar(LineaDetalleFactura lineaSeleccionada) {
        this.facturaSeleccionada.getListaDetalleFactura().forEach(elemento -> {
            if (elemento.equals(lineaSeleccionada)) {
                elemento.setEsParaNotaCredito(false);
                elemento.getDetalleFactura().setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO_QUITAR.getEstadoLineaDetalle());
            }
        });
        calcularMontos();
    }

    public void seleccionarNotaDebito(NotaDebitoModelo notaSeleccionada) {
        try {
            esNotaCreditoParaFactura = false;
            notaDebitoSeleccionada = notaSeleccionada;
            inhabilitarControles = false;
            this.listaLineasDetalleNotaDebito = this.servicioFactura.obtenerDetalleFacturaLineasNotaDebito(notaDebitoSeleccionada.getNotaDebito().getId_nota_debito());
            if (notaDebitoSeleccionada.getFactura() != null) {
                notaDebitoAsociadaFactura = true;
                this.facturaSeleccionada = this.servicioFactura.obtenerFacturaModeloAnulacion(notaDebitoSeleccionada.getFactura().getId_factura());
                eliminarLineasFacturaNotaDebito();
            } else if (notaDebitoSeleccionada.getNotaCredito() != null) {
                notaDebitoAsociadaFactura = true;
                this.facturaSeleccionada = this.servicioFactura.obtenerFacturaModeloAnulacion(notaDebitoSeleccionada.getNotaCredito().getId_factura());
                eliminarLineasFacturaNotaDebito();
            }
            this.puedeCrearNotaCredito = true;

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void eliminarLineasFacturaNotaDebito() {
        List<LineaDetalleFactura> listaEliminar = new ArrayList<>();

        for (LineaDetalleFactura lineaDetallePadre : this.facturaSeleccionada.getListaDetalleFactura()) {

            for (LineaDetalleFactura lineaDetalleHijo : this.listaLineasDetalleNotaDebito) {
                if (lineaDetallePadre.getDetalleFactura().getDetallePK().equals(lineaDetalleHijo.getDetalleFactura().getDetallePK())) {
                    listaEliminar.add(lineaDetallePadre);
                }
            }
        }

        this.facturaSeleccionada.getListaDetalleFactura().removeAll(listaEliminar);
    }

    public void seleccionarTipoDocReferencia(ValueChangeEvent evt) {
        tipoDocumentoReferenciaSeleccionado = (Long) evt.getNewValue();
        if (tipoDocumentoReferenciaSeleccionado.equals(1L)) {
            if (this.facturaSeleccionada.getListaDetalleFactura() != null) {
                this.facturaSeleccionada.getListaDetalleFactura().forEach(elemento -> {
                    if (elemento.isPuedeModificar()) {
                        elemento.setEsParaNotaCredito(true);
                        elemento.getDetalleFactura().setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO.getEstadoLineaDetalle());
                    }
                });
                inhabilitarControles = false;
                MensajeUtil.agregarMensajeAdvertencia("Ha seleccionado anular el documento de referencia. La factura se anulará y quedará con monto 0.0");
            }
        } else {
            if (this.facturaSeleccionada.getListaDetalleFactura() != null) {
                this.facturaSeleccionada.getListaDetalleFactura().forEach(elemento -> {
                    if (elemento.isPuedeModificar()) {
                        elemento.setEsParaNotaCredito(false);
                        elemento.getDetalleFactura().setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO_QUITAR.getEstadoLineaDetalle());
                    }
                });
                inhabilitarControles = false;
            }

        }
    }

    public void guardarAnulacion() {
        try {
            if (validarNotaCredito() && puedeCrearNotaCredito) {

                List<DetalleFacturaNotaCredito> listaDetalleFacturaNotaCredito = new ArrayList<>();

                AnulacionFactura anulacion = new AnulacionFactura();
                anulacion.setLogin(Utilitario.obtenerUsuario().getLogin());
                anulacion.setId_bodega(Utilitario.obtenerIdBodegaUsuario());
                anulacion.setIp(JSFUtil.obtenerIpComputadora());
                anulacion.setNombre_maquina(JSFUtil.obtenerNombreMaquina());

                anulacion.setMotivo_anulacion(motivoAnulacion);
                if (!this.facturaSeleccionada.getFactura().getEstado_factura().equals(EstadoFactura.RECHAZADA_HACIENDA.getEstadoFactura())) {
//                    anulacion.setNumero_consecutivo(servicioFactura.construirNumeroConsecutivoNotaCredito(TipoDocumento.NOTA_DE_CREDITO_ELECTRONICA.getTipoDocumento()));
//                    anulacion.setClave(
//                            servicioFactura.construirClaveNumerica(
//                                    anulacion.getNumero_consecutivo(),
//                                    Utilitario.obtenerEmisor().getIdentificacion().getNumeroCedula(),
//                                    SituacionComprobante.SITUACION_NORMAL.getSituacion()
//                            )
//                    );
                    anulacion.setId_estado(EstadoAnulacion.PENDIENTE_DE_ENVIO_HACIENDA.getEstadoAnulacion());
                } else {
                    anulacion.setId_estado(EstadoAnulacion.NOTA_CREDITO_INTERNA.getEstadoAnulacion());
                    anulacion.setNota_credito_interna(1);
                }

                anulacion.setId_tipo_doc_referencia(tipoDocumentoReferenciaSeleccionado);

                anulacion.setId_motivo_anulacion(motivoAnulacionSeleccionado);
                anulacion.setEnvio_correo_electronico(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());

                if (esNotaCreditoParaFactura) {
                    anulacion.setId_factura(facturaSeleccionada.getIdFactura());

                    this.facturaSeleccionada.getListaDetalleFactura().forEach(elemento -> {
                        if (elemento.isEsParaNotaCredito() && elemento.isPuedeModificar()) {
                            elemento.getDetalleFactura().setId_estado(EstadosLineasFactura.PENDIENTE_ENVIO.getEstadoLineaFactura());
                            listaDetalleFacturaNotaCredito.add(new DetalleFacturaNotaCredito(elemento.getNumeroLinea(), null, elemento.getDetalleFactura().getDetallePK().getId_producto(), elemento.getDetalleFactura().getDetallePK().getId_factura(), elemento.getDetalleFactura()));
                        }
                    });

                } else {
                    anulacion.setId_nota_debito(notaDebitoSeleccionada.getNotaDebito().getId_nota_debito());

                    this.listaLineasDetalleNotaDebito.forEach(elemento -> {

                        elemento.getDetalleFactura().setId_estado(EstadosLineasFactura.PENDIENTE_ENVIO.getEstadoLineaFactura());
                        elemento.getDetalleFactura().setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO.getEstadoLineaDetalle());
                        elemento.getDetalleFactura().setEs_para_nota_debito(LineaDetalleEstado.PARA_NOTA_DEBITO_QUITAR.getEstadoLineaDetalle());

                        listaDetalleFacturaNotaCredito.add(new DetalleFacturaNotaCredito(elemento.getDetalleFactura().getDetallePK().getNumero_linea(), null, elemento.getDetalleFactura().getDetallePK().getId_producto(), elemento.getDetalleFactura().getDetallePK().getId_factura(), elemento.getDetalleFactura()));

                    });
                }

                if (esNotaCreditoParaFactura) {

                    UtilitarioNotasDebitoCredito utilitario = new UtilitarioNotasDebitoCredito();
                    utilitario.setListaProductoPorFacturar(this.obtenerLineasParaCalcular(listaDetalleFacturaNotaCredito));
                    if (this.facturaSeleccionada.getFactura() != null) {
                        if (this.facturaSeleccionada.getFactura().getId_cliente() != null) {
                            utilitario.setPersonaEdicionFactura(servicioPersona.obtenerPersonaPorIdCliente(this.facturaSeleccionada.getFactura().getId_cliente()));
                        }
                    }

                    anulacion.setTotal_descuento(utilitario.getTotalDescuentosLineas());
                    anulacion.setTotal_impuesto(utilitario.getTotalImpuestos());
                    anulacion.setTotal_venta_neta(utilitario.getTotalVentaNeta());
                    anulacion.setTotal_venta(utilitario.getTotalVenta());
                    anulacion.setTotal_servicios_grabados(utilitario.getTotalGravadosServicios());
                    anulacion.setTotal_servicios_exentos(utilitario.getTotalExentosServicios());
                    anulacion.setTotal_mercancias_gravadas(utilitario.getTotalGravadosMercancias());
                    anulacion.setTotal_mercancias_exentas(utilitario.getTotalExentosMercancias());
                    anulacion.setTotal_gravado(utilitario.getTotalGravados());
                    anulacion.setTotal_exento(utilitario.getTotalExentos());
                    anulacion.setTotal_nota_credito(utilitario.getTotalFactura());
                    anulacion.setTotal_mercancias_exenonerados(utilitario.getTotalExoneradosMercancias());
                    anulacion.setTotal_servicios_exenonerados(utilitario.getTotalExoneradosServicios());
                    anulacion.setTotal_exonerado(utilitario.getTotalExonerados());

                } else if (notaDebitoAsociadaFactura) {
                    anulacion.setTotal_descuento(this.notaDebitoSeleccionada.getNotaDebito().getTotal_descuento());
                    anulacion.setTotal_impuesto(this.notaDebitoSeleccionada.getNotaDebito().getTotal_impuesto());
                    anulacion.setTotal_venta_neta(this.notaDebitoSeleccionada.getNotaDebito().getTotal_venta_neta());
                    anulacion.setTotal_venta(this.notaDebitoSeleccionada.getNotaDebito().getTotal_venta());
                    anulacion.setTotal_servicios_grabados(this.notaDebitoSeleccionada.getNotaDebito().getTotal_servicios_grabados());
                    anulacion.setTotal_servicios_exentos(this.notaDebitoSeleccionada.getNotaDebito().getTotal_servicios_exentos());
                    anulacion.setTotal_mercancias_gravadas(this.notaDebitoSeleccionada.getNotaDebito().getTotal_mercancias_gravadas());
                    anulacion.setTotal_mercancias_exentas(this.notaDebitoSeleccionada.getNotaDebito().getTotal_mercancias_exentas());
                    anulacion.setTotal_gravado(this.notaDebitoSeleccionada.getNotaDebito().getTotal_gravado());
                    anulacion.setTotal_exento(this.notaDebitoSeleccionada.getNotaDebito().getTotal_exento());
                    anulacion.setTotal_nota_credito(this.notaDebitoSeleccionada.getNotaDebito().getTotal_nota_debito());
                    anulacion.setTotal_mercancias_exenonerados(this.notaDebitoSeleccionada.getNotaDebito().getTotal_mercancias_exonerados());
                    anulacion.setTotal_servicios_exenonerados(this.notaDebitoSeleccionada.getNotaDebito().getTotal_servicios_exonerados());
                    anulacion.setTotal_exonerado(this.notaDebitoSeleccionada.getNotaDebito().getTotal_exonerado());
                }

                servicioFactura.guardarAnulacion(anulacion, listaDetalleFacturaNotaCredito, (this.facturaSeleccionada == null ? null : this.facturaSeleccionada.getFactura()), notaDebitoSeleccionada.getNotaDebito(), esNotaCreditoParaFactura, notaDebitoAsociadaFactura);
                this.anulacion = anulacion;
                FacturaAnulacionHistoricoHacienda historico = construirHistorico(anulacion);
                servicioFactura.guardarHistoricoAnulacion(historico);

                facturaSeleccionada = null;
                motivoAnulacion = "";
                this.motivoAnulacionSeleccionado = 0L;
                this.inicializar();
                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.confirmacion.anulacion"));
            }
        } catch (Exception e) {
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    private List<ModeloProducto> obtenerLineasParaCalcular(List<DetalleFacturaNotaCredito> listaDetalleFacturaNotaCredito) {
        List<ModeloProducto> resultado = new ArrayList<>();

        for (ModeloProducto modeloProducto : listaProductoPorFacturar) {

            for (DetalleFacturaNotaCredito detalleFacturaNotaCredito : listaDetalleFacturaNotaCredito) {
                if (modeloProducto.getIdFactura().equals(detalleFacturaNotaCredito.getId_factura())
                        && modeloProducto.getId_producto().equals(detalleFacturaNotaCredito.getId_producto())
                        && modeloProducto.getNumeroLineaProducto().equals(detalleFacturaNotaCredito.getNumero_linea())) {
                    resultado.add(modeloProducto);

                }
            }
        }

        return resultado;
    }

    public boolean validarNotaCredito() {
        boolean resultado = true;
        Integer contador = 0;

        if (motivoAnulacion == null || motivoAnulacion.equals("")) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.valudacion.debe.agregar.motivo.anulacion"));
            resultado = false;
        }
        if (this.motivoAnulacionSeleccionado.equals(0l)) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.valudacion.debe.agregar.motivo.anulacion.lista"));
            resultado = false;
        }

        if (this.esNotaCreditoParaFactura) {
            for (LineaDetalleFactura lineaDetalle : this.facturaSeleccionada.getListaDetalleFactura()) {
                if (lineaDetalle.isEsParaNotaCredito() && lineaDetalle.isPuedeModificar()) {
                    contador++;
                }
            }
            if (contador.equals(0)) {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.valudacion.debe.seleccionar.una.linea"));
                resultado = false;
            }
        }

        return resultado;
    }

    public FacturaAnulacionHistoricoHacienda construirHistorico(AnulacionFactura anulacion) {
        FacturaAnulacionHistoricoHacienda historico = new FacturaAnulacionHistoricoHacienda();
        historico.setId_anulacion(anulacion.getId_anulacion());
        historico.setFecha(new Date());
        historico.setLogin(Utilitario.obtenerUsuario().getLogin());
        historico.setEstado_factura(EstadoAnulacion.PENDIENTE_DE_ENVIO_HACIENDA.getEstadoAnulacion());
        return historico;
    }

    public void calcularMontos() {

//        totalImpuestos = new BigDecimal("0.0");
//        totalFactura = new BigDecimal("0.0");
//
//        this.calcularGravadosExentos();
//        this.calcularTotalesVentasDescuentos();
//        this.facturaSeleccionada.getListaDetalleFactura().forEach((producto) -> {
//            totalFactura = totalFactura.add(producto.getDetalleFactura().getTotal_linea()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//        });
    }

    public void calcularTotalesVentasDescuentos() {

        totalVenta = new BigDecimal("0.0");
        totalVentaNeta = new BigDecimal("0.0");
        totalDescuentosLineas = new BigDecimal("0.0");
        totalDescuentos = new BigDecimal("0.0");
        totalDescuentosLineas = new BigDecimal("0.0");
        totalVenta = getTotalGravados().add(totalExentos).add(totalExonerados).setScale(3, BigDecimal.ROUND_HALF_EVEN);

        for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
            totalDescuentosLineas = totalDescuentosLineas.add(producto.getMontoDescuento() == null ? new BigDecimal("0.0") : producto.getMontoDescuento()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        }
        totalDescuentos = totalDescuentosLineas;

        totalVentaNeta = totalVenta.subtract(totalDescuentosLineas);

    }

    public void calcularGravadosExentos() {
        totalExentos = new BigDecimal("0.0");
        totalExentosServicios = new BigDecimal("0.0");
        totalExentosMercancias = new BigDecimal("0.0");

        totalGravados = new BigDecimal("0.0");
        totalGravadosServicios = new BigDecimal("0.0");
        totalGravadosMercancias = new BigDecimal("0.0");

        totalExonerados = new BigDecimal("0.0");
        totalExoneradosServicios = new BigDecimal("0.0");
        totalExoneradosMercancias = new BigDecimal("0.0");

        BigDecimal montoTotal = new BigDecimal("0.0");
        Float exon = 0F;

        for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
            if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {

                montoTotal = producto.getDetalleFactura().getPrecio_neto().multiply(new BigDecimal(producto.getDetalleFactura().getCantidad().floatValue()));
                if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                    //ESTO ES PARA MERCANCIAS
                    if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())) {
                        //SI ES PARA EXENTO SE GUARDA EN EXENTOS
                        totalExentosServicios = totalExentosServicios.add(montoTotal).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                        totalExentos = totalExentos.add(producto.getDetalleFactura().getPrecio_neto().multiply(new BigDecimal(producto.getDetalleFactura().getCantidad().floatValue()))).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                    } else if (producto.getProducto().getId_impuesto() != null) {
                        //SI TIENE IMPUESTOS

                        //SI EL CLIENTE ESTA EXONERADO CALCULO LA EXONERACION       
                        if (producto.getProducto().getPersonaAsociada() != null
                                && (producto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador())
                                && !producto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))
                                && (producto.getExoneracionLinea() != null)) {

                            exon = (1 - Float.parseFloat(producto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / 100f);
                            totalExoneradosServicios = totalExoneradosServicios.add(montoTotal.multiply(new BigDecimal(exon))).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                            totalGravadosServicios = totalGravadosServicios.add(montoTotal.multiply(new BigDecimal(exon)));
                            totalImpuestos = totalImpuestos.add(producto.getTotalImpuestoNeto() == null ? new BigDecimal("0.0") : producto.getTotalImpuestoNeto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                        } else {
                            totalGravadosServicios = totalGravadosServicios.add(montoTotal).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                            totalImpuestos = totalImpuestos.add(producto.getTotalImpuesto() == null ? new BigDecimal("0.0") : producto.getTotalImpuesto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                        }
                    }
                } else if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                    //ESTO ES PARA MERCANCIAS
                    if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())) {
                        //SI ES PARA EXENTO SE GUARDA EN EXENTOS
                        totalExentosMercancias = totalExentosMercancias.add(montoTotal).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                        totalExentos = totalExentos.add(producto.getDetalleFactura().getPrecio_neto().multiply(new BigDecimal(producto.getDetalleFactura().getCantidad().floatValue()))).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                    } else if (producto.getProducto().getId_impuesto() != null) {
                        //SI TIENE IMPUESTOS

                        //SI EL CLIENTE ESTA EXONERADO CALCULO LA EXONERACION       
                        if (producto.getProducto().getPersonaAsociada() != null
                                && (producto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador())
                                && !producto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))
                                && (producto.getExoneracionLinea() != null)) {

                            exon = (1 - Float.parseFloat(producto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / 100f);
                            totalExoneradosMercancias = totalExoneradosMercancias.add(montoTotal.multiply(new BigDecimal(exon))).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                            totalGravadosMercancias = totalGravadosMercancias.add(montoTotal.multiply(new BigDecimal(exon)));
                            totalImpuestos = totalImpuestos.add(producto.getTotalImpuestoNeto() == null ? new BigDecimal("0.0") : producto.getTotalImpuestoNeto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                        } else {
                            totalGravadosMercancias = totalGravadosMercancias.add(montoTotal).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                            totalImpuestos = totalImpuestos.add(producto.getTotalImpuesto() == null ? new BigDecimal("0.0") : producto.getTotalImpuesto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                        }
                    }
                }
            }
        }

        totalGravados = totalGravados.add(totalGravadosMercancias).add(totalGravadosServicios);
        totalExonerados = totalExonerados.add(totalExoneradosMercancias).add(totalExoneradosServicios);
    }

    public StreamedContent imprirmirNotaCredito() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idAnulacion", anulacion.getId_anulacion());
            parametros.put("simbolo", "CRC");
            if (anulacion.getClave() == null) {
                anulacion.setClave("");
            }
            reporte = servicioReporte.generarReporte(Reportes.REPORTE_NOTAS_CREDITO, TiposMimeTypes.PDF, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            this.notaCredito = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_NOTAS_CREDITO.getNombreReporte() + "-" + (anulacion.getClave().equals("") ? anulacion.getId_anulacion() : anulacion.getClave()) + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporteDesplegar;
    }

//
//    public List<LineaDetalle> obtenerProductoGravados(boolean mercancias) {
//        List<LineaDetalle> retorno = new ArrayList<>();
//        if (this.facturaSeleccionada != null) {
//            if (mercancias) {
//                for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                    if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {
//                        if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
//                            if (producto.getImpuesto() != null) {
//                                retorno.add(producto);
//                            }
//                        }
//                    }
//
//                }
//            } else {
//                for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                    if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {
//                        if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
//                            if (producto.getImpuesto() != null) {
//                                retorno.add(producto);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return retorno;
//    }
//
//    public BigDecimal getTotalGravadosServicios() {
//        totalGravadosServicios = new BigDecimal("0");
//
//        List<LineaDetalle> retorno = obtenerProductoGravados(false);
//        for (LineaDetalleFactura modeloProducto : retorno) {
//            if (modeloProducto.isEsParaNotaCredito() && modeloProducto.isPuedeModificar()) {
//                totalGravadosServicios = totalGravadosServicios.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//
//            }
//        }
//        return totalGravadosServicios;
//    }
//
//    public BigDecimal getTotalGravadosMercancias() {
//        totalGravadosMercancias = new BigDecimal("0");
//        if (this.facturaSeleccionada != null) {
//            List<LineaDetalle> retorno = obtenerProductoGravados(true);
//            for (LineaDetalleFactura modeloProducto : retorno) {
//                if (modeloProducto.isEsParaNotaCredito() && modeloProducto.isPuedeModificar()) {
//                    totalGravadosMercancias = totalGravadosMercancias.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                }
//            }
//        }
//        return totalGravadosMercancias;
//    }
//
//    public BigDecimal getTotalExentos() {
//        totalExentos = new BigDecimal("0");
//        if (this.facturaSeleccionada != null) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {
//                    if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
//                        totalExentos = totalExentos.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                    }
//                }
//            }
//        }
//        return totalExentos;
//    }
//
//    public BigDecimal getTotalGravados() {
//        totalGravados = new BigDecimal("0");
//        if (this.facturaSeleccionada != null) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {
//                    if (producto.getImpuesto() != null && producto.getExoneracion() == null) {
//                        totalGravados = totalGravados.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                    }
//                }
//            }
//        }
//
//        return totalGravados;
//    }
//
//    public BigDecimal getTotalExentosServicios() {
//        totalExentosServicios = new BigDecimal("0");
//        if (this.facturaSeleccionada != null) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {
//                    if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
//                        if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
//                            totalExentosServicios = totalExentosServicios.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                        }
//                    }
//                }
//            }
//        }
//        return totalExentosServicios;
//    }
//
//    public BigDecimal getTotalExentosMercancias() {
//        totalExentosMercancias = new BigDecimal("0");
//        if (this.facturaSeleccionada != null) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {
//                    if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
//                        if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
//                            totalExentosMercancias = totalExentosMercancias.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                        }
//                    }
//                }
//            }
//        }
//        return totalExentosMercancias;
//    }
//
//    public BigDecimal getTotalDescuentos() {
//        return totalDescuentos;
//    }
//
//    public BigDecimal getTotalDescuentosLineas() {
//        totalDescuentosLineas = new BigDecimal("0");
//        if (this.facturaSeleccionada != null) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {
//                    totalDescuentosLineas = totalDescuentosLineas.add(producto.getMontoDescuento() == null ? new BigDecimal("0") : producto.getMontoDescuento()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                }
//            }
//        }
//        return totalDescuentosLineas;
//    }
//
//    public Integer getDescuentoFactura() {
//        return descuentoFactura;
//    }
//
//    public BigDecimal getTotalVenta() {
//        totalVenta = new BigDecimal("0");
//        if (this.facturaSeleccionada != null) {
//            totalVenta = getTotalGravados().add(getTotalExentos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//        }
//        return totalVenta;
//    }
//
//    public BigDecimal getTotalVentaNeta() {
//        totalVentaNeta = getTotalVenta().subtract(getTotalDescuentosLineas());
//        return totalVentaNeta;
//    }
//
//    public BigDecimal getTotalImpuestos() {
//        totalImpuestos = new BigDecimal("0");
//        if (this.facturaSeleccionada != null) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.isEsParaNotaCredito() && producto.isPuedeModificar()) {
//                    producto.calcularImpuesto();
//                    totalImpuestos = totalImpuestos.add(producto.getTotalImpuesto() == null ? new BigDecimal("0.0") : producto.getTotalImpuesto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                }
//            }
//        }
//        return totalImpuestos;
//    }
//
//    public BigDecimal getTotalFactura() {
//        totalFactura = new BigDecimal("0");
//        totalFactura = totalFactura.add(getTotalVentaNeta()).add(getTotalImpuestos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//        return totalFactura;
//    }
//
//    public List<LineaDetalle> obtenerProductoGravados(boolean mercancias) {
//        List<LineaDetalle> retorno = new ArrayList<>();
//
//        if (mercancias) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
//                    if (producto.getImpuesto() != null) {
//                        retorno.add(producto);
//                    }
//                }
//            }
//        } else {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
//                    if (producto.getImpuesto() != null) {
//                        retorno.add(producto);
//                    }
//                }
//            }
//        }
//
//        return retorno;
//    }
//
//    public List<LineaDetalle> obtenerProductoExentos(boolean mercancias) {
//        List<LineaDetalle> retorno = new ArrayList<>();
//
//        if (mercancias) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
//                    if (producto.getProducto().getInd_exonerado().equals(Indicadores.EXENTO_SI.getIndicador())) {
//                        retorno.add(producto);
//                    }
//                }
//            }
//        } else {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
//                    if (producto.getProducto().getInd_exonerado().equals(Indicadores.EXENTO_SI.getIndicador())) {
//                        retorno.add(producto);
//                    }
//                }
//            }
//        }
//
//        return retorno;
//    }
//
//    public List<LineaDetalle> obtenerProductoExonerados(boolean mercancias) {
//        List<LineaDetalle> retorno = new ArrayList<>();
//
//        if (mercancias) {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
//                    if (producto.getProducto().getPersonaAsociada() != null
//                            && (producto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()))
//                            && producto.getTipoTarifaImpuesto() != null
//                            && (!producto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
//                        retorno.add(producto);
//                    }
//                }
//            }
//        } else {
//            for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//                if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
//                    if (producto.getProducto().getPersonaAsociada() != null
//                            && (producto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()))
//                            && producto.getTipoTarifaImpuesto() != null
//                            && (!producto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
//                        retorno.add(producto);
//                    }
//                }
//            }
//        }
//
//        return retorno;
//    }
//
//    public BigDecimal getTotalExoneradosServicios() {
//        totalExoneradosServicios = new BigDecimal("0.0");
//        BigDecimal monto = new BigDecimal("0.0");
//        Float exon = 0F;
//        List<LineaDetalle> retorno = obtenerProductoExonerados(false);
//        for (LineaDetalleFactura modeloProducto : retorno) {
//
//            if (modeloProducto.getTipoTarifaImpuesto() != null
//                    && (!modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
//
//                if (modeloProducto.getProducto().getPersonaAsociada() != null
//                        && (modeloProducto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()))) {
//
//                    monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
//                    exon = Float.parseFloat(modeloProducto.getProducto().getPersonaAsociada().getPorcentaje_exonerado().toString()) / 100f;
//
//                }
//
//            }
//
//            totalExoneradosServicios = totalExoneradosServicios.add(monto.multiply(new BigDecimal(exon))).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//        }
//        return totalExoneradosServicios;
//    }
//
//    public BigDecimal getTotalExoneradosMercancias() {
//        totalExoneradosMercancias = new BigDecimal("0.0");
//        BigDecimal monto = new BigDecimal("0.0");
//        Float exon = 0F;
//        List<LineaDetalle> retorno = obtenerProductoExonerados(true);
//        for (LineaDetalleFactura modeloProducto : retorno) {
//
//            if (modeloProducto.getTipoTarifaImpuesto() != null
//                    && (!modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
//
//                if (modeloProducto.getProducto().getPersonaAsociada() != null
//                        && (modeloProducto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()))) {
//
//                    monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
//                    exon = Float.parseFloat(modeloProducto.getProducto().getPersonaAsociada().getPorcentaje_exonerado().toString()) / 100f;
//
//                }
//
//            }
//
//            totalExoneradosMercancias = totalExoneradosMercancias.add(monto.multiply(new BigDecimal(exon))).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//        }
//        return totalExoneradosMercancias;
//    }
//
//    public BigDecimal getTotalGravadosServicios() {
//        totalGravadosServicios = new BigDecimal("0.0");
//        BigDecimal monto = new BigDecimal("0.0");
//        List<LineaDetalle> retorno = obtenerProductoGravados(false);
//        for (LineaDetalleFactura modeloProducto : retorno) {
//
//            if (modeloProducto.getProducto().getPersonaAsociada() != null
//                    && (modeloProducto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador())
//                    && !modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
//
//                monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
//                Float exon = (1 - Float.parseFloat(modeloProducto.getProducto().getPersonaAsociada().getPorcentaje_exonerado().toString()) / 100f);
//                totalGravadosServicios = totalGravadosServicios.add(monto.multiply(new BigDecimal(exon)));
//                //totalGravadosServicios = totalGravadosServicios.add((modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).multiply(1 - modeloProducto.getProducto().getPorcentaje_compra() / 100));
//            } else {
//                totalGravadosServicios = totalGravadosServicios.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//            }
//        }
//        return totalGravadosServicios;
//    }
//
//    public BigDecimal getTotalGravadosMercancias() {
//        totalGravadosMercancias = new BigDecimal("0.0");
//        BigDecimal monto = new BigDecimal("0.0");
//        List<LineaDetalle> retorno = obtenerProductoGravados(true);
//        for (LineaDetalleFactura modeloProducto : retorno) {
//            if (modeloProducto.getProducto().getPersonaAsociada() != null
//                    && (modeloProducto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador())
//                    && !modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
//
//                monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
//                Float exon = (1 - Float.parseFloat(modeloProducto.getProducto().getPersonaAsociada().getPorcentaje_exonerado().toString()) / 100f);
//                totalGravadosMercancias = totalGravadosMercancias.add(monto.multiply(new BigDecimal(exon)));
//            } else {
//
//                totalGravadosMercancias = totalGravadosMercancias.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//            }
//        }
//        return totalGravadosMercancias;
//    }
//
//    public BigDecimal getTotalExonerados() {
//
//        totalExonerados = new BigDecimal("0.0");
//        totalExonerados = totalExonerados.add(getTotalExoneradosServicios()).add(getTotalExoneradosMercancias()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//        return totalExonerados;
//    }
//
//    public BigDecimal getTotalExentos() {
//        totalExentos = new BigDecimal("0.0");
//        for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//            if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())) {
//                totalExentos = totalExentos.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//            }
//        }
//        return totalExentos;
//    }
//
//    public BigDecimal getTotalGravados() {
//        totalGravados = new BigDecimal("0.0");
//        totalGravados = totalGravados.add(getTotalGravadosMercancias()).add(getTotalGravadosServicios()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//        return totalGravados;
//    }
//
//    public BigDecimal getTotalExentosServicios() {
//        totalExentosServicios = new BigDecimal("0.0");
//        for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//            if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
//                if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())) {
//                    totalExentosServicios = totalExentosServicios.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                }
//            }
//        }
//        return totalExentosServicios;
//    }
//
//    public BigDecimal getTotalExentosMercancias() {
//        totalExentosMercancias = new BigDecimal("0.0");
//        for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//            if (producto.getProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
//                if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())) {
//                    totalExentosMercancias = totalExentosMercancias.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                }
//            }
//        }
//        return totalExentosMercancias;
//    }
//
//    public BigDecimal getTotalDescuentos() {
//        totalDescuentos = new BigDecimal("0.0");
//        totalDescuentos = getTotalDescuentosLineas();
//        return totalDescuentos;
//    }
//
//    public BigDecimal getTotalDescuentosLineas() {
//        totalDescuentosLineas = new BigDecimal("0.0");
//        for (LineaDetalleFactura producto : this.facturaSeleccionada.getListaDetalleFactura()) {
//            totalDescuentosLineas = totalDescuentosLineas.add(producto.getMontoDescuento() == null ? new BigDecimal("0.0") : producto.getMontoDescuento()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//        }
//        return totalDescuentosLineas;
//    }
//
//    public Integer getDescuentoFactura() {
//        return descuentoFactura;
//    }
//
//    public BigDecimal getTotalVenta() {
//        totalVenta = new BigDecimal("0.0");
//        totalVenta = getTotalGravados().add(getTotalExentos()).add(getTotalExonerados()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//        return totalVenta;
//    }
//
//    public BigDecimal getTotalVentaNeta() {
//        totalVentaNeta = new BigDecimal("0.0");
//        totalVentaNeta = getTotalVenta().subtract(getTotalDescuentosLineas());
//        return totalVentaNeta;
//    }
//
//    public BigDecimal getTotalImpuestos() {
//        totalImpuestos = new BigDecimal("0.0");
//        facturaSeleccionada.getListaDetalleFactura().forEach((producto) -> {
//            if (producto.getImpuesto() != null) {
//                if (producto.getProducto().getPersonaAsociada() != null && (producto.getProducto().getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()))) {
//                    totalImpuestos = totalImpuestos.add(producto.getTotalImpuestoNeto() == null ? new BigDecimal("0.0") : producto.getTotalImpuestoNeto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//                } else {
//                    totalImpuestos = totalImpuestos.add(producto.getTotalImpuesto() == null ? new BigDecimal("0.0") : producto.getTotalImpuesto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//                }
//            }
//        });
//        return totalImpuestos;
//    }
//
//    public BigDecimal getTotalFactura() {
//        totalFactura = new BigDecimal("0.0");
//        //totalFactura = totalFactura.add(getTotalVentaNeta()).add(getTotalImpuestos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//        this.facturaSeleccionada.getListaDetalleFactura().forEach((producto) -> {
//            totalFactura = totalFactura.add(producto.getMontoTotalLinea()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//        });
//        return totalFactura;
//    }
}
