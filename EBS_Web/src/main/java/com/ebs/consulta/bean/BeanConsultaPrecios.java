/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.consulta.bean;

import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.ModeloProducto;
import com.ebs.modelos.ModeloTipoPrecio;
import com.ebs.modelos.ProductoExistencia;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Jorge Quesada Arias
 */
@ManagedBean
@ViewScoped
public class BeanConsultaPrecios {

    private List<ModeloProducto> listaProductosBusqueda;

    private String nombreProducto;
    @Inject
    private ServicioProducto servicioProducto;

    private List<ProductoExistencia> listaExistenciasProductoSeleccionado;

    private List<ProductoExistencia> listaExistenciasProductoSeleccionadoFiltro;

    private List<ModeloTipoPrecio> listaPreciosProductoSeleccionado;

    private List<ModeloTipoPrecio> listaPreciosProductoSeleccionadoFiltro;

    private String nombreProductoDetalle;

    private ModeloTipoPrecio precioSeleccionado;

    private ModeloProducto productoSeleccionado;

    public BeanConsultaPrecios() {
        this.nombreProducto = "";
        this.nombreProductoDetalle = "";
    }

    /**
     * Método que carga la información de los productos basados en su
     * descripción
     */
    public void buscarProductoPorDescripcion() {
        try {

            this.listaProductosBusqueda = servicioProducto.listarProductosPorDescripcion(nombreProducto);

        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.erorr.buscar.producto.descripcion"));
        }
    }

    public void onRowSelect(SelectEvent event) {
        try {
            this.precioSeleccionado = (ModeloTipoPrecio) event.getObject();

            this.productoSeleccionado.setPrecio(this.precioSeleccionado.getPrecio());
            this.productoSeleccionado.setMoneda(this.precioSeleccionado.getDescripcionTipoMoneda());
            this.productoSeleccionado.setTipoCambio(this.precioSeleccionado.getTipo_cambio());
            this.productoSeleccionado.setSimbolo(this.precioSeleccionado.getSimbolo());
            this.productoSeleccionado.setIdTipPrecioSeleccionado(this.precioSeleccionado.getId_tipo());
            this.productoSeleccionado.calcularMontoTotal();
            this.productoSeleccionado.calcularDescuento();
            this.productoSeleccionado.calcularSubTotal();
            this.productoSeleccionado.calcularImpuesto();
            this.productoSeleccionado.calcularMontoTotalLinea();

            this.precioSeleccionado = null;
        } catch (Exception e) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.seleccion.precio.facturacin"));
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public void limpiar() {
        this.nombreProducto = "";
        this.nombreProductoDetalle = "";
    }

    public void buscarDetalleProducto(ModeloProducto idProducto) {
        try {
            this.listaExistenciasProductoSeleccionado = new ArrayList<>();
            this.listaPreciosProductoSeleccionado = new ArrayList<>();
            this.nombreProductoDetalle = idProducto.getId_producto() + " - " + idProducto.getDescripcion();
            listaExistenciasProductoSeleccionado = this.servicioProducto.obtenerExistenciaProducto(idProducto.getId_producto());
            listaPreciosProductoSeleccionado = this.servicioProducto.obtenerPreciosProductos(idProducto.getId_producto());
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public List<ModeloProducto> getListaProductosBusqueda() {
        return listaProductosBusqueda;
    }

    public void setListaProductosBusqueda(List<ModeloProducto> listaProductosBusqueda) {
        this.listaProductosBusqueda = listaProductosBusqueda;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public List<ProductoExistencia> getListaExistenciasProductoSeleccionado() {
        return listaExistenciasProductoSeleccionado;
    }

    public void setListaExistenciasProductoSeleccionado(List<ProductoExistencia> listaExistenciasProductoSeleccionado) {
        this.listaExistenciasProductoSeleccionado = listaExistenciasProductoSeleccionado;
    }

    public List<ModeloTipoPrecio> getListaPreciosProductoSeleccionado() {
        return listaPreciosProductoSeleccionado;
    }

    public void setListaPreciosProductoSeleccionado(List<ModeloTipoPrecio> listaPreciosProductoSeleccionado) {
        this.listaPreciosProductoSeleccionado = listaPreciosProductoSeleccionado;
    }

    public String getNombreProductoDetalle() {
        return nombreProductoDetalle;
    }

    public void setNombreProductoDetalle(String nombreProductoDetalle) {
        this.nombreProductoDetalle = nombreProductoDetalle;
    }

    public ModeloTipoPrecio getPrecioSeleccionado() {
        return precioSeleccionado;
    }

    public void setPrecioSeleccionado(ModeloTipoPrecio precioSeleccionado) {
        this.precioSeleccionado = precioSeleccionado;
    }

    public ModeloProducto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(ModeloProducto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public List<ModeloTipoPrecio> getListaPreciosProductoSeleccionadoFiltro() {
        return listaPreciosProductoSeleccionadoFiltro;
    }

    public void setListaPreciosProductoSeleccionadoFiltro(List<ModeloTipoPrecio> listaPreciosProductoSeleccionadoFiltro) {
        this.listaPreciosProductoSeleccionadoFiltro = listaPreciosProductoSeleccionadoFiltro;
    }

    public List<ProductoExistencia> getListaExistenciasProductoSeleccionadoFiltro() {
        return listaExistenciasProductoSeleccionadoFiltro;
    }

    public void setListaExistenciasProductoSeleccionadoFiltro(List<ProductoExistencia> listaExistenciasProductoSeleccionadoFiltro) {
        this.listaExistenciasProductoSeleccionadoFiltro = listaExistenciasProductoSeleccionadoFiltro;
    }

}
