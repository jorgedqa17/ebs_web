/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.inventario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.EstadoSolicitud;
import com.ebs.constantes.enums.EstadoSolicitudDetalle;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.InventarioSolicDetElementos;
import com.ebs.entidades.InventarioSolicitud;
import com.ebs.entidades.InventarioSolicitudDetalle;
import com.ebs.entidades.Producto;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.DetalleSolicitud;
import com.ebs.modelos.ElementosSolicitudDetalle;
import com.ebs.modelos.EncabezadoSolicitud;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge Quesada
 */
@ManagedBean
@ViewScoped
public class BeanConsultaSolicitudes {

    @Getter
    @Setter
    private Long valorBodega;

    @Getter
    @Setter
    private List<SelectItem> listaBodegas;

    @Inject
    private ServicioBodega servicioBodega;

    @Inject
    private ServicioInventario servicioInventario;

    @Inject
    private ServicioProducto servicioProducto;

    @Getter
    @Setter
    private Long estadoActual;

    @Getter
    @Setter
    private List<SelectItem> listaEstados;

    @Getter
    @Setter
    private List<EncabezadoSolicitud> listaSolicitudes;

    @Getter
    @Setter
    private List<DetalleSolicitud> listaDetalles;

    @Getter
    @Setter
    private EncabezadoSolicitud solicitudSeleccionada;

    @Getter
    @Setter
    private DetalleSolicitud detalleModal;

    @Getter
    @Setter
    private List<DetalleSolicitud> detalleSeleccionadas;

    @Getter
    @Setter
    private List<EncabezadoSolicitud> filtroSolicitudes;

    @Getter
    @Setter
    private List<DetalleSolicitud> filtroDetalles;

    @PostConstruct
    public void inicializar() {
        this.listaBodegas = obtenerSeleccionBodegas();
        this.listaEstados = obtenerEstadosDetalle();
        this.listaSolicitudes = new ArrayList<>();
        this.listaDetalles = new ArrayList<>();

    }

    public List<SelectItem> obtenerSeleccionBodegas() {
        List<Bodega> bodegas = servicioBodega.obtenerListaBodegas();
        List<SelectItem> lista = new ArrayList<>();
        bodegas.forEach((p) -> {
            lista.add(new SelectItem(p.getBodegaPK().getId_bodega(), p.getDescripcion()));
        });

        return lista;
    }

    public List<SelectItem> obtenerEstadosDetalle() {
        List<SelectItem> lista = new ArrayList<>();

        lista.add(new SelectItem(EstadoSolicitudDetalle.PENDIENTE_ENVIO.getId(), EstadoSolicitudDetalle.PENDIENTE_ENVIO.getDescripcion()));
        lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_ENVIADA.getId(), EstadoSolicitudDetalle.SOLICITUD_ENVIADA.getDescripcion()));
        lista.add(new SelectItem(EstadoSolicitudDetalle.PENDIENTE_DESPACHO.getId(), EstadoSolicitudDetalle.PENDIENTE_DESPACHO.getDescripcion()));
        lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_DESPACHADA.getId(), EstadoSolicitudDetalle.SOLICITUD_DESPACHADA.getDescripcion()));
        lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_RECHAZADA.getId(), EstadoSolicitudDetalle.SOLICITUD_RECHAZADA.getDescripcion()));
        lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_RECIBIDA.getId(), EstadoSolicitudDetalle.SOLICITUD_RECIBIDA.getDescripcion()));
        lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_ANULADA.getId(), EstadoSolicitudDetalle.SOLICITUD_ANULADA.getDescripcion()));

        return lista;
    }

    public void buscarEstado(ActionEvent evento) {
        cargarSolicitudes();
    }

    private void cargarSolicitudes() {

        JSFUtil.limpiarFiltros("DTSolicitudes");
        JSFUtil.limpiarFiltros("DTDetalles");

        List<InventarioSolicitud> lista = servicioInventario.obtenerEncabezadoSolicitud(valorBodega, estadoActual);

        limpiarVariables();

        //Se obtiene la bodega seleccionada
        Bodega bodega = servicioBodega.obtenerBodega(valorBodega);

        lista.forEach((p -> {
            listaSolicitudes.add(new EncabezadoSolicitud(p, bodega, EstadoSolicitud.obtenerEstado(estadoActual)));
        }));
    }

    private void limpiarVariables() {
        //Se limpia la lista de solicitudes
        if (listaSolicitudes != null) {
            listaSolicitudes.clear();
        }

        if (filtroSolicitudes != null) {
            filtroSolicitudes.clear();
        }

        if (filtroDetalles != null) {
            filtroDetalles.clear();
        }

        if (listaDetalles != null) {
            listaDetalles.clear();
        }

        if (solicitudSeleccionada != null) {
            solicitudSeleccionada = null;
        }

        if (detalleSeleccionadas != null) {
            detalleSeleccionadas.clear();
        }
    }

    public void buscarSolicitudDetalles(ActionEvent evento) {

        EncabezadoSolicitud encabezadoSolicitud = (EncabezadoSolicitud) evento.getComponent().getAttributes().get("idSolicitud");
        solicitudSeleccionada = encabezadoSolicitud;
        List<InventarioSolicitudDetalle> detalles = servicioInventario.obtenerInvSolicDetalle(encabezadoSolicitud.getInventarioSolicitud().getIdInventarioSolicitud());

        JSFUtil.limpiarFiltros("DTDetalles");

        if (filtroDetalles != null) {
            filtroDetalles.clear();
        }

        if (listaDetalles != null) {
            listaDetalles.clear();
        }

        detalles.forEach((p -> {
            Bodega bodega = servicioBodega.obtenerBodega(p.getIdBodegaDestino());
            Producto producto = servicioProducto.obtenerProducto(p.getIdProducto());
            List<InventarioSolicDetElementos> lotes = servicioInventario.obtenerElementosDetalle(p.getIdInventarioSolicitudDetalle());
            List<ElementosSolicitudDetalle> elementos = new ArrayList<>();
            lotes.forEach((q -> {
                elementos.add(new ElementosSolicitudDetalle(q));
            }));
            InventarioSolicitudDetalle inventarioSolicitudDetalle = servicioInventario.obtenerInventSolicDetalle(p.getIdInventarioSolicitudDetalle());
            InventarioSolicitud inventarioSolicitud = servicioInventario.obtenerInventarioSolicitud(p.getIdInventarioSolicitud());
            listaDetalles.add(new DetalleSolicitud(inventarioSolicitud, bodega, producto, EstadoSolicitudDetalle.obtenerEstado(p.getEstado()), inventarioSolicitudDetalle, elementos));
        }));
    }

    public void anularLineasSolicitud(ActionEvent evento) {

        if (detalleSeleccionadas != null && !detalleSeleccionadas.isEmpty()) {

            List<InventarioSolicitudDetalle> detalles = new ArrayList<>();
            List<DetalleSolicitud> copiaLista = new ArrayList<>();
            for (DetalleSolicitud deta : detalleSeleccionadas) {

                //Se realiza una copia
                copiaLista.add(deta);

                //Se valida las lineas que pueden ser liberadas
                if (deta.getInventarioSolicitudDetalle().getEstado() < EstadoSolicitudDetalle.SOLICITUD_DESPACHADA.getId()) {
                    detalles.add(deta.getInventarioSolicitudDetalle());
                }
            }

            //Se tratan de anular las lineas seleccionadas
            servicioInventario.anularLineasSolicitud(detalles);

            //Se eliminan las lineas visuales
            copiaLista.forEach((p -> {
                listaDetalles.remove(p);
                detalleSeleccionadas.remove(p);
                filtroDetalles.remove(p);
            }));

            JSFUtil.limpiarFiltros("DTDetalles");

            //Pregunta que ya no tiene hijos asociados
            if(listaDetalles.isEmpty()){
                listaSolicitudes.remove(solicitudSeleccionada);
                filtroSolicitudes.clear();
                filtroDetalles.clear();
                cargarSolicitudes();
            }

            MensajeUtil.agregarMensajeInfo("Los posibles productos fueron anulados.");

        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar al menos un producto.");
        }

    }

    public void abrirLote(ActionEvent evento) {
        detalleModal = (DetalleSolicitud) evento.getComponent().getAttributes().get("idDetalle");
        JSFUtil.abrirModal("panelModal");
    }

    public void cerrarLote(ActionEvent evento) {
        JSFUtil.cerrarModal("panelModal");
    }
}
