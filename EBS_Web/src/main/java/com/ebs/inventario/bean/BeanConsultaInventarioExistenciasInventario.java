/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.inventario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.entidades.Bodega;
import com.ebs.exception.ExcepcionManager;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.InventarioCantidadMinimasModelo;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jdquesad
 */
@ManagedBean
@ViewScoped
public class BeanConsultaInventarioExistenciasInventario {

    @Inject
    private ServicioBodega servicioBodega;

    @Getter
    @Setter
    private List<Bodega> listaBodegas;

    @Getter
    @Setter
    private Long idBodegaSeleccionada;

    @Inject
    private ServicioInventario servicioInventario;

    @Getter
    @Setter
    List<InventarioCantidadMinimasModelo> listaProductos;
    @Getter
    @Setter
    List<InventarioCantidadMinimasModelo> listaProductosFiltro;

    @PostConstruct
    public void inicializar() {
        this.listaBodegas = servicioBodega.obtenerListaBodegas();
    }

    public void buscarInformacion() {
        try {
            if (idBodegaSeleccionada != null || !idBodegaSeleccionada.equals(0L)) {
                listaProductos = this.servicioInventario.obtenerListaProductosExistenciaActual(idBodegaSeleccionada);
            } else {
                MensajeUtil.agregarMensajeAdvertencia("mensaje.validacion.productos.cantidad.mininas.consutla");
            }

        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.carga.productos.cantidad.mininas.consutla"));
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

}
