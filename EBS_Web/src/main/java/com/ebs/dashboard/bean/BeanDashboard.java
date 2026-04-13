/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.dashboard.bean;

import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.inventario.servicio.ServicioInventario;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import lombok.Data;

/**
 *
 * @author jdquesad
 */
@ManagedBean
@ViewScoped
@Data
public class BeanDashboard {

    @Inject
    private ServicioInventario servicioInventario;
    @Inject
    private ServicioFactura servicioFactura;

    private Integer cantidadProductosConExistenciasMinimas;
    private Integer cantidadPedidosProformas;

    public BeanDashboard() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            cantidadProductosConExistenciasMinimas = servicioInventario.verificarExistenciaInventario(Utilitario.obtenerIdBodegaUsuario());
            cantidadPedidosProformas = servicioFactura.obtenerCantidadPedidosProformasSinVencer();

        } catch (Exception e) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.dashboard"));
            ExcepcionManager.manejarExcepcion(e);
        }

    }

//    public void enviarAvisoBaseDatos() {
//        try {
//            servicioInventario.fechaHoraBD();
//        } catch (Exception e) {
//        }
//
//    }

}
