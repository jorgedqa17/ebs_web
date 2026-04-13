/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.inventario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.entidades.Bodega;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.ModeloProducto;
import com.ebs.modelos.TrazabilidadModelo;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class BeanConsultaTrazabilidadProducto {

    @Getter
    @Setter
    private Date fechaInicio;
    @Getter
    @Setter
    private Date fechaFinal;
    @Getter
    @Setter
    private Long idBodegaSeleccionada;
    @Getter
    @Setter
    private List<ModeloProducto> listaProductosBusqueda;
    @Getter
    @Setter
    private List<ModeloProducto> listaProductosBusquedaSeleccionados;
    @Inject
    private ServicioProducto servicioProducto;
    @Inject
    private ServicioInventario servicioInventario;
    @Inject
    private ServicioBodega servicioBodegas;
    @Getter
    @Setter
    private List<Bodega> listaBodegas;
    @Getter
    @Setter
    private String nombreProducto;
    @Getter
    @Setter
    private List<TrazabilidadModelo> listaResultado;

    public BeanConsultaTrazabilidadProducto() {

    }

    @PostConstruct
    public void inicializar() {
        this.listaBodegas = servicioBodegas.obtenerListaBodegas();
        idBodegaSeleccionada = Utilitario.obtenerIdBodegaUsuario();
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

    public boolean validar() {
        boolean resultado = true;
        if (listaProductosBusquedaSeleccionados == null || listaProductosBusquedaSeleccionados.size() <= 0) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.seleccione.productos"));
        }
        if (fechaInicio == null || fechaFinal == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.fechas.trazabilidad"));
        }
        return resultado;
    }

    public void buscar() {
        if (validar()) {
            StringBuilder ids = new StringBuilder();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            listaProductosBusquedaSeleccionados.stream()
                    .forEach(o -> ids.append((ids.toString().equals("") ? "" : ",")).append(o.getId_producto()));

            listaResultado = this.servicioInventario.obtenerTrazabilidad(idBodegaSeleccionada, ids.toString(), df.format(fechaInicio), df.format(fechaFinal));
        }
    }

}
