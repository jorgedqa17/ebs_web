/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.inventario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.InventarioIngreso;
import com.ebs.entidades.InventarioIngresoDetalle;
import com.ebs.entidades.Producto;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.EncabezadoSolicitud;
import com.ebs.modelos.ModeloIngreso;
import com.ebs.modelos.ModeloIngresoDetalle;
import com.powersystem.productos.servicios.ServicioProducto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import lombok.Data;

/**
 *
 * @author
 */
@ManagedBean
@ViewScoped
@Data
public class BeanConsultaIngresoInventario {

    private List<ModeloIngreso> listaIngreso;

    private List<ModeloIngreso> ingresoSelecionados;

    private List<ModeloIngresoDetalle> listaDetalle;

    private List<ModeloIngreso> ingresoDetalleSelecionados;

    @Inject
    private ServicioInventario servicioInventario;

    @Inject
    private ServicioBodega servicioBodega;

    @Inject
    private ServicioProducto servicioProducto;

    private ModeloIngreso ingresoSeleccionado;

    @PostConstruct
    public void inicializar() {
        listaIngreso = CargarIngresos(servicioInventario.obtenerIngresoInventario());
    }

    private List<ModeloIngreso> CargarIngresos(List<InventarioIngreso> obtenerIngresoInventario) {
        List<ModeloIngreso> lista = new ArrayList<>();
        obtenerIngresoInventario.forEach((p) -> {
            Bodega bodega = servicioBodega.obtenerBodega(p.getIdBodegaOrigen());
            lista.add(new ModeloIngreso(p, bodega, p.getLogin()));
        });

        return lista;
    }

    public void cargarLinea(ActionEvent evento) {
        ModeloIngreso temporal = (ModeloIngreso) evento.getComponent().getAttributes().get("idLinea");
        ingresoSeleccionado = temporal;
        listaDetalle = CargarIngresoDetalle(servicioInventario.obtenerDetalleIngresoInventario(ingresoSeleccionado.getInventarioIngreso().getIdInventarioIngreso()));
    }

    private List<ModeloIngresoDetalle> CargarIngresoDetalle(List<InventarioIngresoDetalle> obtenerDetalleIngresoInventario) {
        List<ModeloIngresoDetalle> lista = new ArrayList<>();
        obtenerDetalleIngresoInventario.forEach((p) -> {
            Producto producto = servicioProducto.obtenerProducto(p.getIdProducto());
            lista.add(new ModeloIngresoDetalle(producto, p));
        });

        return lista;
    }

}
