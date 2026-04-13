/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.productos.bean;

import com.ebs.constantes.enums.TiposPrecios;
import com.ebs.entidades.Impuesto;
import com.ebs.entidades.Producto;
import com.ebs.entidades.ProductoPrecio;
import com.ebs.entidades.ProductoPrecioPK;
import com.ebs.entidades.TipoExoneracion;
import com.ebs.entidades.TipoMoneda;
import com.ebs.entidades.TipoPrecio;
import com.ebs.entidades.TipoProducto;
import com.ebs.entidades.TipoTarifaImpuesto;
import com.ebs.entidades.UnidadMedida;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.ModeloListaProductos;
import com.ebs.modelos.ModeloTipoPrecio;
import com.ebs.modelos.ProductoExistencia;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class BeanProductos {

    @Getter
    @Setter
    private List<ModeloListaProductos> listaProductos;
    @Getter
    @Setter
    private List<TipoPrecio> listaTiposPrecios;

    @Getter
    @Setter
    private Long idTipoPrecioSeleccionado;
    @Getter
    @Setter
    private List<ModeloTipoPrecio> listaPreciosProducto;
    @Getter
    @Setter
    private ProductoPrecio productoPrcioNuevo;
    @Getter
    @Setter
    private List<TipoMoneda> listaTiposMonedas;
    @Getter
    @Setter
    private Long idTipoMonedaSeleccionada;
    @Getter
    @Setter
    private List<UnidadMedida> listaUnidadesMedidas;
    @Getter
    @Setter
    private Long idUnidadMedidaSeleccionado;
    @Getter
    @Setter
    private List<Impuesto> listaImpuestos;
    @Getter
    @Setter
    private Long idImpuestoSeleccionado;
    @Getter
    @Setter
    private List<TipoExoneracion> listaExoneraciones;
    @Getter
    @Setter
    private Long idTipoTarifa;
    @Getter
    @Setter
    private List<TipoProducto> listaTiposProducto;
    @Getter
    @Setter
    private Long idTipoProductoSeleccionado;
    @Getter
    @Setter
    private List<ModeloListaProductos> listaProductosSeleccionados;
    @Getter
    @Setter
    private Producto producto;
    @Getter
    @Setter
    private Integer cantidadMinima;

    @Getter
    @Setter
    private String codigoCabys;
    @Inject
    private ServicioProducto servicioProducto;
    @Getter
    @Setter
    private boolean indCaduce;
    @Getter
    @Setter
    private boolean exonerado;
    @Getter
    @Setter
    private boolean exento;
    @Getter
    @Setter
    private boolean estaModificando;
    @Getter
    @Setter
    private TipoMoneda monedaSeleccionada;
    @Getter
    @Setter
    private BigDecimal precio;
    @Getter
    @Setter
    private boolean inhabilitaBotones;

    @Getter
    @Setter
    private String numeroDocumento;

    @Getter
    @Setter
    private String nombreInstitucion;

    @Getter
    @Setter
    private Date fechaEmision;

    @Getter
    @Setter
    private BigDecimal montoImpuesto;

    @Getter
    @Setter
    private Integer porcentajeCompra;

    @Getter
    @Setter
    private List<ProductoExistencia> listaExistencias;
    @Getter
    @Setter
    private List<ProductoExistencia> listaExistenciasFiltro;
    @Getter
    @Setter
    private List<TipoTarifaImpuesto> listaTiposTarifa;

    public BeanProductos() {
    }

    @PostConstruct
    public void inicializar() {
        try {

            numeroDocumento = null;
            nombreInstitucion = null;
            fechaEmision = null;
            montoImpuesto = null;
            porcentajeCompra = null;
            this.cantidadMinima = null;
            inhabilitaBotones = true;
            producto = new Producto();
            indCaduce = false;
            exonerado = false;
            listaPreciosProducto = new ArrayList<>();
            listaExistencias = new ArrayList<>();
            productoPrcioNuevo = new ProductoPrecio();
            precio = new BigDecimal("0.0");
            listaProductos = servicioProducto.obtenerTodosProductos();
            this.listaUnidadesMedidas = servicioProducto.obtenerTodasUnidadesMedida();
            this.listaImpuestos = servicioProducto.obtenerTodosImpuestos();
            this.listaExoneraciones = servicioProducto.obtenerTodosTiposExoneraciones();
            this.listaTiposProducto = servicioProducto.obtenerTodosTiposProductos();
            this.listaTiposPrecios = servicioProducto.obtenerListaTiposPrecio();
            this.listaTiposMonedas = servicioProducto.obtenerListaTiposMonedas();
            this.listaTiposTarifa = servicioProducto.obtenerListaTiposTarifas();
            this.idTipoTarifa = 0L;
            this.idImpuestoSeleccionado = 0L;
            this.idTipoMonedaSeleccionada = 0L;
            this.idTipoPrecioSeleccionado = 0L;
            this.idUnidadMedidaSeleccionado = 0L;
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void inicializarSinCarga() {
        try {

            numeroDocumento = null;
            nombreInstitucion = null;
            fechaEmision = null;
            montoImpuesto = null;
            porcentajeCompra = null;
            listaProductos = servicioProducto.obtenerTodosProductos();
            listaExistencias = new ArrayList<>();
            inhabilitaBotones = true;
            this.cantidadMinima = null;
            cantidadMinima = null;
            producto = new Producto();
            indCaduce = false;
            exonerado = false;
            listaPreciosProducto = new ArrayList<>();
            productoPrcioNuevo = new ProductoPrecio();
            precio = new BigDecimal("0.0");
            this.idTipoTarifa = 0L;
            this.idImpuestoSeleccionado = 0L;
            this.idTipoMonedaSeleccionada = 0L;
            this.idTipoPrecioSeleccionado = 0L;
            this.idUnidadMedidaSeleccionado = 0L;
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void crearNuevoProductoo(ActionEvent evt) {
        producto = new Producto();
        indCaduce = false;
        exonerado = false;
        listaPreciosProducto = new ArrayList<>();
        productoPrcioNuevo = new ProductoPrecio();
        precio = new BigDecimal("0.0");

        inhabilitaBotones = false;
    }

    public void editarProductoSeleccionado(ActionEvent evt) {
        this.estaModificando = true;
        this.inhabilitaBotones = false;
    }

    public void seleccionarUnidadMedida(ValueChangeEvent evt) {
        idUnidadMedidaSeleccionado = (Long) evt.getNewValue();
        producto.setId_unidad_medida(idUnidadMedidaSeleccionado);

    }

    public void eliminarRegistroTablaProducto(ModeloTipoPrecio producto) {
        this.listaPreciosProducto.remove(producto);
    }

    public void agregarTipoPrecio(ActionEvent evt) {
        if (!existePrecioProducto()) {
            if (!precio.equals(new BigDecimal("0.0"))) {
                ProductoPrecioPK productoPrecioPK = new ProductoPrecioPK(null, idTipoPrecioSeleccionado);
                ProductoPrecio productoPrecio = new ProductoPrecio();
                productoPrecio.setProductoPrecioPK(productoPrecioPK);
                productoPrecio.setId_moneda(idTipoMonedaSeleccionada);
                productoPrecio.setPrecio(precio);
                precio = new BigDecimal("0.0");

                ModeloTipoPrecio modelo = new ModeloTipoPrecio();
                modelo.setProductoPrecio(productoPrecio);
                modelo.setDescripcion(obtenerNombreTipoPrecio());
                modelo.setDescripcionTipoMoneda(obtenerNombreTipoMoneda());

                this.listaPreciosProducto.add(modelo);
            } else {

                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.ingreso.precio.producto"));
            }

        } else {

            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.existe.precio.producto"));
        }

    }

    public boolean existePrecioProducto() {
        boolean resultado = false;
        for (ModeloTipoPrecio modeloTipoPrecio : this.listaPreciosProducto) {
            if (modeloTipoPrecio.getProductoPrecio().getId_moneda().equals(idTipoMonedaSeleccionada)
                    && modeloTipoPrecio.getProductoPrecio().getProductoPrecioPK().getId_tipo().equals(idTipoPrecioSeleccionado)) {
                resultado = true;
            }
        }
        return resultado;
    }

    public void seleccionarTipoProducto(ValueChangeEvent evt) {
        idTipoProductoSeleccionado = (Long) evt.getNewValue();
        producto.setId_tipo_producto(idTipoProductoSeleccionado);
    }

    public void seleccionarImpuesto(ValueChangeEvent evt) {
        idImpuestoSeleccionado = (Long) evt.getNewValue();
    }

    public void seleccionarTipoTarifa(ValueChangeEvent evt) {
        idTipoTarifa = (Long) evt.getNewValue();
    }

    public void seleccionarTipoPrecio(ValueChangeEvent evt) {
        idTipoPrecioSeleccionado = (Long) evt.getNewValue();
    }

    public void seleccionarTipoMoneda(ValueChangeEvent evt) {
        idTipoMonedaSeleccionada = (Long) evt.getNewValue();
    }

    public String obtenerNombreTipoPrecio() {
        String resultado = "";
        for (TipoPrecio tipoPrecio : this.listaTiposPrecios) {
            if (tipoPrecio.getId_tipo().equals(idTipoPrecioSeleccionado)) {
                resultado = tipoPrecio.getDescripcion();
                break;
            }
        }
        return resultado;
    }

    public String obtenerNombreTipoMoneda() {
        String resultado = "";
        for (TipoMoneda tipoMoneda : this.listaTiposMonedas) {
            if (tipoMoneda.getId_moneda().equals(idTipoMonedaSeleccionada)) {
                resultado = tipoMoneda.getDescripcion();
                break;
            }
        }
        return resultado;
    }

    public void validarExistenciaCodigoBarrasProducto() {
        List<Producto> listaProducto = servicioProducto.obtenerListaProductosPorCodigoBarras(this.producto.getCodigo_barras());
        if (listaProducto.size() > 0) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.codigo.barras.existe"));
        }

    }

    public void guardarProducto() {
        try {
            String descripcionHistorico = "";
            if (validarProducto()) {
                inhabilitaBotones = true;
                producto.setCodigo_cabys(codigoCabys);
                producto.setCantidad_minima(cantidadMinima);
                producto.setInd_caduce(indCaduce ? 1 : 0);
                producto.setInd_exonerado(exonerado ? 1 : 0);
                producto.setInd_exento(exento ? 1 : 0);
                producto.setId_tipo_producto(idTipoProductoSeleccionado);

                if (exento) {
                    producto.setId_impuesto(null);
                    producto.setId_tipo_tarifa_impuesto(null);

                } else {
                    producto.setId_tipo_tarifa_impuesto(idTipoTarifa);
                    producto.setId_impuesto(idImpuestoSeleccionado);
                }
                if (!estaModificando) {
                    descripcionHistorico = "Se agrego nuevo";
                    List<ProductoPrecio> listaPrecios = new ArrayList<>();
                    for (ModeloTipoPrecio modeloTipoPrecio : this.listaPreciosProducto) {
                        listaPrecios.add(modeloTipoPrecio.getProductoPrecio());
                    }

                    List<Producto> listaProducto = servicioProducto.obtenerListaProductosPorCodigoBarras(this.producto.getCodigo_barras());
                    if (listaProducto.size() <= 0) {
                        servicioProducto.guadarProducto(producto, listaPrecios);
                    } else {
                        MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.codigo.barras.existe"));
                        return;
                    }

                } else {
                    descripcionHistorico = "Se modificó";
                    List<ProductoPrecio> listaPrecios = new ArrayList<>();
                    for (ModeloTipoPrecio modeloTipoPrecio : this.listaPreciosProducto) {
                        listaPrecios.add(modeloTipoPrecio.getProductoPrecio());
                    }
                    servicioProducto.modificarProducto(producto, listaPrecios);
                }
                servicioProducto.agregarHistoricoProducto(Utilitario.obtenerUsuario().getLogin(), descripcionHistorico, producto.getId_producto());
                estaModificando = false;
                inicializarSinCarga();
                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.confirmacion.gudar.producto"));
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void activarProducto(ModeloListaProductos productoSeleccionado) {
        try {
            this.producto = productoSeleccionado.getProducto();
            this.listaPreciosProducto = productoSeleccionado.getListaPrecios();
            this.producto.setActivo(1);
            servicioProducto.modificarProducto(producto, null);
            inicializarSinCarga();
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.confirmacion.gudar.producto"));

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void verExistencias(ModeloListaProductos productoSeleccionado) {
        listaExistencias = new ArrayList<>();
        try {
            this.listaExistencias = servicioProducto.obtenerExistenciaProducto(productoSeleccionado.getProducto().getId_producto());

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void inactivarProducto(ModeloListaProductos productoSeleccionado) {
        try {

            listaExistencias = new ArrayList<>();
            this.producto = productoSeleccionado.getProducto();
            this.listaExistencias = servicioProducto.obtenerExistenciaProducto(this.producto.getId_producto());
            if (this.listaExistencias.size() <= 0) {
                this.listaPreciosProducto = productoSeleccionado.getListaPrecios();
                this.producto.setActivo(0);
                servicioProducto.modificarProducto(producto, null);
                inicializarSinCarga();
                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.confirmacion.gudar.producto"));
            } else {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.confirmacion.gudar.producto.lista.existencias"));
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void editarProducto(ModeloListaProductos productoSeleccionado) {
        try {
            this.producto = productoSeleccionado.getProducto();
            this.codigoCabys = this.producto.getCodigo_cabys();
            this.idTipoTarifa = this.producto.getId_tipo_tarifa_impuesto();
            this.idImpuestoSeleccionado = this.producto.getId_impuesto();
            this.idTipoProductoSeleccionado = this.producto.getId_tipo_producto();
            this.idUnidadMedidaSeleccionado = this.producto.getId_unidad_medida();
            this.indCaduce = this.producto.getInd_caduce().equals(1);
            this.exonerado = this.producto.getInd_exonerado().equals(1);
            this.exento = this.producto.getInd_exento().equals(1);
            this.cantidadMinima = this.producto.getCantidad_minima();
            inhabilitaBotones = true;
            this.listaPreciosProducto = productoSeleccionado.getListaPrecios();

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void cancelar(ActionEvent evt) {

        estaModificando = false;
        inicializarSinCarga();
        inhabilitaBotones = true;
    }

    public boolean validarProducto() {
        boolean resultado = true;

        if (this.idUnidadMedidaSeleccionado == null || this.idUnidadMedidaSeleccionado.equals(0l)) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.unidad.medida"));
            return false;
        }
        if (this.codigoCabys == null || this.codigoCabys.equals("")) {
            MensajeUtil.agregarMensajeAdvertencia("Debe ingresar el código cabys");
            return false;
        } else if (this.codigoCabys.length() < 13) {
            MensajeUtil.agregarMensajeAdvertencia("El código cabys debe tener al menos 13 caracteres");
            return false;
        }
        if (this.idTipoProductoSeleccionado == null || this.idTipoProductoSeleccionado.equals(0l)) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.tipo.producto"));
            return false;
        }
        if (!this.exento) {
            if (this.idImpuestoSeleccionado == null || this.idImpuestoSeleccionado.equals(0l)) {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.impuesto"));
                return false;
            }
            if (this.idTipoTarifa == null || this.idTipoTarifa.equals(0l)) {
                MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar el tipo de tarifa");
                return false;
            }
        }
        if (this.cantidadMinima == null || this.cantidadMinima.equals(0)) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cantidad.minima"));
            return false;
        }

        if (this.producto.getCodigo_barras() != null) {
            if (this.producto.getCodigo_barras().equals("")) {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.codigo.barras"));
                return false;
            }
        } else {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.codigo.barras"));
            return false;
        }
        //
        if (this.producto.getDescripcion() != null) {
            if (this.producto.getDescripcion().equals("")) {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.nombre"));
                return false;
            }
        } else {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.nombre"));
            return false;
        }
        if (this.listaPreciosProducto.size() <= 0) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.lista"));
            return false;
        } else {
            boolean tienePrecioGeneral = false, tienePrecioPorMayor = false, tienePrecioEspecial = false;
            for (ModeloTipoPrecio modeloTipoPrecio : listaPreciosProducto) {
                if (modeloTipoPrecio.getProductoPrecio().getProductoPrecioPK().getId_tipo().equals(TiposPrecios.GENERAL.getTipoPrecio())) {
                    tienePrecioGeneral = true;
                } else if (modeloTipoPrecio.getProductoPrecio().getProductoPrecioPK().getId_tipo().equals(TiposPrecios.POR_MAYOR.getTipoPrecio())) {
                    tienePrecioPorMayor = true;
                } else if (modeloTipoPrecio.getProductoPrecio().getProductoPrecioPK().getId_tipo().equals(TiposPrecios.PRECIO_ESPECIAL.getTipoPrecio())) {
                    tienePrecioEspecial = true;
                }
            }
            if (!tienePrecioGeneral || !tienePrecioPorMayor || !tienePrecioEspecial) {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.tipos.repcios"));
                return false;
            }
        }
        return resultado;
    }

    public void redireccionarNuevoProducto(ActionEvent event) throws IOException {
        try {

            JSFUtil.redireccionarPagina("/xhtml/paginas/producto/producto.xhtml");
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

}
