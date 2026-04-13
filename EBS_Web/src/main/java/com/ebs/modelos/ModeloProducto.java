/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.constantes.enums.TipoCliente;
import com.ebs.constantes.enums.TiposPrecios;
import com.ebs.entidades.Cliente;
import com.ebs.entidades.Impuesto;
import com.ebs.entidades.InventarioPrecioProducto;
import com.ebs.entidades.InventarioSalidaDetalle;
import com.ebs.entidades.Producto;
import com.ebs.entidades.ProductoPrecio;
import com.ebs.entidades.TipoExoneracion;
import com.ebs.entidades.TipoMoneda;
import com.ebs.entidades.TipoPrecio;
import com.ebs.entidades.TipoProducto;
import com.ebs.entidades.UnidadMedida;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import javax.inject.Inject;
import com.ebs.parametros.servicios.ServicioParametro;
import com.ebs.constantes.enums.Generico;
import com.ebs.constantes.enums.Indicadores;
import com.ebs.constantes.enums.TipoImpuesto;
import com.ebs.constantes.enums.TipoUnidadMedida;
import com.ebs.entidades.Parametro;
import com.ebs.entidades.Persona;
import com.ebs.entidades.TipoTarifaImpuesto;
import com.powersystem.productos.servicios.ServicioProducto;

/**
 *
 * @author Jorge GBSYS
 */
public class ModeloProducto {

    @Inject
    private ServicioParametro servicioParametro;

    @Inject
    private ServicioProducto servicioProducto;
    @Getter
    @Setter
    private Long id_producto;
    @Getter
    @Setter
    private String codigo_barras;
    @Getter
    @Setter
    private Long id_unidad_medida;
    @Getter
    @Setter
    private String descripcion_unidad_medida;
    @Getter
    @Setter
    private String descripcion;
    @Getter
    @Setter
    private String numeroLote;
    @Getter
    @Setter
    private String fechaVencimientoLote;
    @Getter
    @Setter
    private Date fechaVencimientoLoteDate;
    @Getter
    @Setter
    private String descripcionLineaProducto;
    @Getter
    @Setter
    private Integer ind_caduce;
    @Getter
    @Setter
    private TipoProducto tipoProducto;
    @Getter
    @Setter
    private Integer ind_exonerado;
    @Getter
    @Setter
    private TipoMoneda tipoMoneda;
    @Getter
    @Setter
    private List<ModeloTipoPrecio> listaTiposPrecio;
    @Getter
    @Setter
    private List<ProductoExistencia> listaExistencias;
    @Getter
    @Setter
    private List<ProductoExistencia> listaExistenciasFiltro;
    @Getter
    @Setter
    private Double cantidad;

    @Getter
    @Setter
    private Double cantidadAAgregar;
    @Getter
    @Setter
    private Long idTipPrecioSeleccionado;
    @Getter
    @Setter
    private BigDecimal precio;
    @Getter
    @Setter
    private BigDecimal tipoCambio;
    @Getter
    @Setter
    private String moneda;
    @Getter
    @Setter
    private Integer descuento;
    @Getter
    @Setter
    private String naturalezaDescuento;
    @Getter
    @Setter
    private String simbolo;
    @Getter
    @Setter
    private Impuesto impuesto;
    @Getter
    @Setter
    private TipoExoneracion exoneracion;
    @Getter
    @Setter
    private BigDecimal montoTotal;
    @Getter
    @Setter
    private BigDecimal montoDescuento;
    @Getter
    @Setter
    private BigDecimal subTotal;
    @Getter
    @Setter
    private BigDecimal totalImpuesto;
    @Getter
    @Setter
    private BigDecimal totalImpuestoNeto;
    @Getter
    @Setter
    private BigDecimal totalExoneracion;
    @Getter
    @Setter
    private BigDecimal montoTotalLinea;

    @Getter
    @Setter
    private List<InventarioSalidaDetalle> listaSalidasDetalles;

    @Getter
    @Setter
    private Integer numeroLineaProducto;
    @Getter
    @Setter
    private Integer presentarProblemaInventario;
    @Getter
    @Setter
    private Producto producto;
    @Getter
    @Setter
    private boolean permiteEliminar;
    @Getter
    @Setter
    private boolean esPersonaExenta;

    @Getter
    @Setter
    private boolean esPersonaExonerada;
    @Getter
    @Setter
    private List<InventarioPrecioProducto> listaPreciosIngreso;
    @Getter
    private BigDecimal precioBaseProducto;
    @Getter
    @Setter
    private Integer indicadorEsParaNotaDebito;
    @Setter
    @Getter
    private TipoTarifaImpuesto tipoTarifaImpuesto;
    @Getter
    @Setter
    private Persona personaAsociada;

    @Setter
    @Getter
    private Long idFactura;
    @Setter
    @Getter
    private Long idTipoTarifaSeleccionada;
    @Setter
    @Getter
    private List<TipoTarifaImpuesto> listadoDeTarifas;
    @Getter
    @Setter
    private ExoneracionLinea exoneracionLinea;
    @Getter
    @Setter
    private Parametro salarioBase;
    @Getter
    @Setter
    private Parametro valorMultiplicacion;

    public ModeloProducto() {

    }

    public static ModeloProducto inicializar() {

        ModeloProducto retorno = new ModeloProducto();

        retorno.id_producto = 0L;
        retorno.codigo_barras = "";
        retorno.id_unidad_medida = 0L;
        retorno.descripcion_unidad_medida = "";
        retorno.descripcion = "";
        retorno.ind_caduce = 0;
        retorno.ind_exonerado = 0;
        retorno.listaExistencias = new ArrayList<>();
        retorno.listaTiposPrecio = new ArrayList<>();
        retorno.cantidad = 0D;
        retorno.descuento = 0;
        retorno.tipoProducto = new TipoProducto();
        retorno.montoTotal = new BigDecimal("0");
        retorno.montoDescuento = new BigDecimal("0");
        retorno.subTotal = new BigDecimal("0");
        retorno.montoTotalLinea = new BigDecimal("0");
        retorno.totalImpuesto = new BigDecimal("0");

        return retorno;
    }

    /**
     * Método que construye un objeto ModeloProducto con los elmentos del
     * producto, la unidad de medida, la lista de precios, los tipos de precios
     * y las existencias
     *
     * @param producto
     * @param unidadMedida
     * @param listaPrecios
     * @param listaTiposPrecio
     * @param listaExistencias
     * @param cliente
     * @param listaMonedas
     * @param tipoProducto
     * @param impuesto
     * @param tipoExoneracion
     * @param tarifa
     * @param persona
     * @param listadoDeTarifas
     * @return ModeloProducto
     */
    public static ModeloProducto construirObjeto(Producto producto,
            UnidadMedida unidadMedida,
            List<ProductoPrecio> listaPrecios,
            List<TipoPrecio> listaTiposPrecio,
            List<ProductoExistencia> listaExistencias,
            Cliente cliente,
            List<TipoMoneda> listaMonedas,
            TipoProducto tipoProducto,
            Impuesto impuesto,
            TipoExoneracion tipoExoneracion,
            TipoTarifaImpuesto tarifa,
            Persona persona,
            List<TipoTarifaImpuesto> listadoDeTarifas) {

        ModeloProducto modeloProducto = ModeloProducto.inicializar();

        modeloProducto.setPersonaAsociada(persona);
        modeloProducto.setPresentarProblemaInventario(0);
        modeloProducto.setId_producto(producto.getId_producto());
        modeloProducto.setCodigo_barras(producto.getCodigo_barras());
        modeloProducto.setId_unidad_medida(unidadMedida.getId_unidad_medida());
        modeloProducto.setDescripcion_unidad_medida(unidadMedida.getDescripcion());
        modeloProducto.setDescripcion(producto.getDescripcion());
        modeloProducto.setInd_caduce(producto.getInd_caduce());
        modeloProducto.setInd_exonerado(producto.getInd_exonerado());
        if (tipoProducto == null) {
            TipoProducto tipoProductoNuevo = new TipoProducto();
            tipoProducto.setDescripcion("No definido");
            tipoProducto.setId_tipo_producto(com.ebs.constantes.enums.TipoProducto.MERCANCIA.getIdTipoProducto());
            modeloProducto.setTipoProducto(tipoProductoNuevo);

        } else {
            modeloProducto.setTipoProducto(tipoProducto);
        }

        modeloProducto.setCantidad(1D);
        modeloProducto.setDescuento(0);
        modeloProducto.setImpuesto(impuesto);
        modeloProducto.setExoneracion(tipoExoneracion);
        List<ModeloTipoPrecio> listaPreciosProducto = new ArrayList<>();
        for (ProductoPrecio precioProducto : listaPrecios) {
            for (TipoPrecio tipoPrecio : listaTiposPrecio) {
                if (precioProducto.getProductoPrecioPK().getId_tipo().equals(tipoPrecio.getId_tipo())) {
                    for (TipoMoneda moneda : listaMonedas) {
                        if (precioProducto.getId_moneda().equals(moneda.getId_moneda())) {
                            modeloProducto.setTipoMoneda(moneda);
                            listaPreciosProducto
                                    .add(new ModeloTipoPrecio(precioProducto.getProductoPrecioPK().getId_tipo(), moneda.getId_moneda(),
                                            tipoPrecio.getDescripcion(), precioProducto.getPrecio(), moneda.getDescripcion(),
                                            moneda.getSimbolo(), moneda.getTipo_cambio()));
                        }
                    }
                }
            }
        }

        //Si no selecciona cliente el tipo de cliente defautl es cliente al detalle o general
        if (cliente == null) {
            cliente = new Cliente();

            cliente.setId_tipo_cliente(TipoCliente.DETALLE.getIdTipoCliente());

        }

        for (ProductoPrecio precio : listaPrecios) {
            if (precio.getProductoPrecioPK().getId_tipo().equals(TiposPrecios.GENERAL.getTipoPrecio())) {
                if (cliente.getId_tipo_cliente().equals(TipoCliente.DETALLE.getIdTipoCliente())) {
                    modeloProducto.setPrecio(precio.getPrecio());
                    for (TipoMoneda moneda : listaMonedas) {
                        if (precio.getId_moneda().equals(moneda.getId_moneda())) {
                            modeloProducto.setMoneda(moneda.getDescripcion());
                            modeloProducto.setSimbolo(moneda.getSimbolo());
                            modeloProducto.setTipoCambio(moneda.getTipo_cambio());
                            modeloProducto.setIdTipPrecioSeleccionado(precio.getProductoPrecioPK().getId_tipo());
                        }
                    }
                    break;
                }
            } else if (precio.getProductoPrecioPK().getId_tipo().equals(TiposPrecios.POR_MAYOR.getTipoPrecio())) {
                if (cliente.getId_tipo_cliente().equals(TipoCliente.POR_MAYOR.getIdTipoCliente())) {
                    modeloProducto.setPrecio(precio.getPrecio());
                    for (TipoMoneda moneda : listaMonedas) {
                        if (precio.getId_moneda().equals(moneda.getId_moneda())) {
                            modeloProducto.setMoneda(moneda.getDescripcion());
                            modeloProducto.setSimbolo(moneda.getSimbolo());
                            modeloProducto.setTipoCambio(moneda.getTipo_cambio());
                            modeloProducto.setIdTipPrecioSeleccionado(precio.getProductoPrecioPK().getId_tipo());
                        }
                    }
                    break;
                }
            }
        }
        if (modeloProducto.getPersonaAsociada() != null) {
            modeloProducto.setEsPersonaExenta(modeloProducto.getPersonaAsociada().getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador()));
            modeloProducto.setEsPersonaExonerada(modeloProducto.getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()));
        } else {
            modeloProducto.setEsPersonaExenta(false);
            modeloProducto.setEsPersonaExonerada(false);
        }

        modeloProducto.setTipoTarifaImpuesto(tarifa);
        modeloProducto.setIdTipoTarifaSeleccionada(tarifa != null ? tarifa.getId_tipo_tarifa_impuesto() : null);
        modeloProducto.setListadoDeTarifas(listadoDeTarifas);
        modeloProducto.setProducto(producto);
        modeloProducto.setListaTiposPrecio(listaPreciosProducto);
        modeloProducto.setListaExistencias(listaExistencias);
        modeloProducto.calcularMontoTotal();
        modeloProducto.calcularDescuento();
        modeloProducto.calcularSubTotal();
        modeloProducto.calcularImpuesto();

        modeloProducto.calcularExoneracion();
        modeloProducto.calcularMontoTotalLinea();
        return modeloProducto;
    }

    public String getDescripcionCaduce() {
        return ind_caduce.equals(1) ? "Sí" : "No";
    }

    public String getDescripcionExonerado() {
        return ind_exonerado.equals(1) ? "Sí" : "No";
    }

    public String getDescripcionExento() {
        return producto.getInd_exento().equals(1) ? "Si" : "No";
    }

    public void calcularExoneracion() {
        totalImpuestoNeto = new BigDecimal("0.0");
        totalExoneracion = new BigDecimal("0.0");
        if (esPersonaExonerada && (exoneracionLinea != null)) {

            Float exon = Float.parseFloat(exoneracionLinea.getPorcentaje_exonerado().toString()) / 100f;
            totalExoneracion = subTotal.multiply(new BigDecimal(exon)).setScale(3, BigDecimal.ROUND_HALF_EVEN);

            totalImpuestoNeto = totalImpuesto.subtract(totalExoneracion);
        }
    }

    public void calcularMontoTotal() {
        montoTotal = new BigDecimal("0.0");
        montoTotal = precio.multiply(new BigDecimal(cantidad)).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
    }

    public boolean permiteEstePrecio(String indicadorValidarPrecio, String porcentajeGanacia) {
        boolean resultado = true;
        if (indicadorValidarPrecio.equals(Generico.SI.getValor())) {

            Float valorGanancia = Float.parseFloat(porcentajeGanacia) / 100f;

            BigDecimal precioBase = this.listaTiposPrecio.stream().filter(elemento -> elemento.getId_tipo().equals(TiposPrecios.PRECIO_BASE.getTipoPrecio())).findAny().get().getPrecio();

            BigDecimal precioComparacion = (precioBase.multiply(new BigDecimal(valorGanancia))).add(precioBase);
            if (this.precio.compareTo(precioComparacion) < 0) {
                resultado = false;
            }
            this.precioBaseProducto = precioBase;
        }
        return resultado;
    }

    public void calcularDescuento() {
        montoDescuento = new BigDecimal("0.0");
        if (descuento == null) {
            descuento = 0;
        }
        Float desc = Float.parseFloat(descuento.toString()) / 100f;
        montoDescuento = montoTotal.multiply(new BigDecimal(desc)).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
    }

    public void calcularSubTotal() {
        subTotal = new BigDecimal("0.0");
        subTotal = montoTotal.subtract(montoDescuento).setScale(3, BigDecimal.ROUND_HALF_EVEN);
    }

    public void calcularImpuesto() {
        totalImpuesto = new BigDecimal("0.0");
        if (impuesto != null && (producto.getInd_exento().equals(Indicadores.EXENTO_NO.getIndicador())) && !this.esPersonaExenta) {

            Float imp = Float.parseFloat(tipoTarifaImpuesto.getValor().toString()) / 100f;
            totalImpuesto = subTotal.multiply(new BigDecimal(imp)).setScale(3, BigDecimal.ROUND_HALF_EVEN);

        }
    }

    public void cambiarTipoTarifa() {
        for (TipoTarifaImpuesto tarifa : listadoDeTarifas) {
            if (tarifa.getId_tipo_tarifa_impuesto().equals(idTipoTarifaSeleccionada)) {
                tipoTarifaImpuesto = tarifa;
                break;
            }
        }
    }

    public void calcularMontos() {

        if (this.producto.getId_unidad_medida().equals(TipoUnidadMedida.ALQUILER_HABITACIONAL.getUnidadMedida())) {
            BigDecimal salarioComparar = new BigDecimal(salarioBase.getValor()).multiply(new BigDecimal(valorMultiplicacion.getValor()));

            if (this.esPersonaExenta) {
                this.producto.setInd_exento(Indicadores.EXENTO_SI.getIndicador());
            } else {
                if (precio.compareTo(salarioComparar) < 0) {
                    this.producto.setInd_exento(Indicadores.EXENTO_SI.getIndicador());
                    impuesto = null;
                } else {
                    this.producto.setInd_exento(Indicadores.EXENTO_NO.getIndicador());
                    impuesto = new Impuesto();
                    impuesto.setId_impuesto(TipoImpuesto.VALOR_AGREGADO.getIdImpuesto());
                    impuesto.setValor(TipoImpuesto.VALOR_AGREGADO.getValor());
                }
            }

        }
        this.calcularMontoTotal();
        this.calcularDescuento();
        this.calcularSubTotal();
        this.calcularImpuesto();

        this.calcularImpuesto();
        this.calcularExoneracion();
        this.calcularMontoTotalLinea();
    }

    public void calcularMontoTotalLinea() {
        montoTotalLinea = new BigDecimal("0.0");
        if ((esPersonaExonerada && exoneracionLinea != null) || producto.getInd_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador())) {
            montoTotalLinea = subTotal.add(totalImpuestoNeto).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        } else if (impuesto != null) {
            montoTotalLinea = subTotal.add(totalImpuesto).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        } else if (producto.getInd_exento().equals(1) || esPersonaExenta) {
            montoTotalLinea = subTotal;
        } else {
            montoTotalLinea = subTotal;
        }

    }

}
