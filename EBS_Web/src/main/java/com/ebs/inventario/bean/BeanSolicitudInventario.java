/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.inventario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.EstadoInvSalidaDetalle;
import com.ebs.constantes.enums.EstadoSolicitud;
import com.ebs.constantes.enums.EstadoSolicitudDetalle;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.Inventario;
import com.ebs.entidades.InventarioSalida;
import com.ebs.entidades.InventarioSalidaDetalle;
import com.ebs.entidades.InventarioSolicDetElementos;
import com.ebs.entidades.InventarioSolicitud;
import com.ebs.entidades.InventarioSolicitudDetalle;
import com.ebs.entidades.Producto;
import com.ebs.entidades.Usuario;
import com.ebs.exception.ExcepcionManager;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.DetalleSolicitud;
import com.ebs.modelos.ElementosSolicitudDetalle;
import com.ebs.modelos.EncabezadoSolicitud;
import com.ebs.modelos.ModeloProducto;
import com.ebs.modelos.NuevaSolicitud;
import com.ebs.parametros.servicios.ServicioParametro;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author
 *
 */
@ManagedBean
@ViewScoped

public class BeanSolicitudInventario {

    /**
     * *************** Elementos para crear una solicitud ****************
     */
    @Getter
    @Setter
    private Long valorBodegaOrigenCreacion;

    @Getter
    @Setter
    private Usuario usuarioActual;

    @Getter
    @Setter
    private Long valorBodegaDestinoCreacion;

    @Getter
    @Setter
    private String valorProductoCreacion;

    @Getter
    @Setter
    private String codigoBarras;

    @Getter
    @Setter
    private String nombreProducto;

    @Getter
    @Setter
    private Long cantidadProductoCreacion;

    @Getter
    @Setter
    private List<NuevaSolicitud> listaSolicitudesCreacion;

    @Getter
    @Setter
    private List<NuevaSolicitud> listaSeleccionadosCreacion;

    private long contador;

    @Getter
    @Setter
    private boolean esAdministrador;

    /**
     * ********************** FIN *******************************************
     */
    /**
     * *************** Elementos para solicitudes pendientes ****************
     */
    @Getter
    @Setter
    private Long valorBodegaPendiente;

    @Getter
    @Setter
    private Long estadoActualPendiente;

    @Getter
    @Setter
    private List<SelectItem> listaEstadosPendiente;

    @Getter
    @Setter
    private List<SelectItem> listaNumeroLotes;

    @Getter
    @Setter
    private List<SelectItem> listaFechaVencimiento;

    @Getter
    @Setter
    private Long idInventarioSolicitudPendiente;

    @Getter
    @Setter
    private EncabezadoSolicitud seleccionadoPendiente;

    @Getter
    @Setter
    private List<EncabezadoSolicitud> filtroPendientes;

    @Getter
    @Setter
    private List<DetalleSolicitud> filtroDetalles;

    @Getter
    @Setter
    private Long esEstadoSolicitud;

    @Getter
    @Setter
    private List<DetalleSolicitud> listaSelecProductosPendientes;

    private Integer contadorElementosPendientes;

    @Getter
    @Setter
    private String informacionError;

    @Getter
    @Setter
    private String tipoSolicitud;

    private String tipoSolicitudAnterior;

    private InventarioSolicitudDetalle solicDetalleActual;

    //private List<Producto> listaProductosSustitutos;
    @Getter
    @Setter
    private String valorProductoSustituto;

    /**
     * ********************** FIN *******************************************
     */
    /**
     * *************** Elementos usados por todos ****************
     */
    @Getter
    @Setter
    private List<SelectItem> listaBodegas;//Usada por todos

    private List<Bodega> bodegas;

    @Getter
    @Setter
    private List<EncabezadoSolicitud> listaEncabezadoSolicitud;

    @Getter
    @Setter
    private List<DetalleSolicitud> listaDetalleSolicitud;

    @Getter
    @Setter
    private DetalleSolicitud modalDetalleSolicitud;

    @Getter
    @Setter
    private String fechaVencimientoModal;

    @Getter
    @Setter
    private Long cantidadModal;

    @Getter
    @Setter
    private String numeroLoteModal;

    /**
     * ********************** FIN *******************************************
     */
    /**
     * *************** Servicios de consulta e insercion ****************
     */
    @Inject
    private ServicioBodega servicioBodega;

    @Inject
    private ServicioInventario servicioInventario;

    @Inject
    private ServicioProducto servicioProducto;
    @Getter
    @Setter
    private List<ModeloProducto> listaProductosBusqueda;
    @Getter
    @Setter
    private List<ModeloProducto> busquedadSeleccionados;
    @Inject
    private ServicioParametro servicioParametro;

    /**
     * ********************** FIN *******************************************
     */
    /**
     * *************************** COMPONENTES A ACTUALIZAR ***************
     */
    /**
     * ********************************************************************
     */
    @PostConstruct
    public void inicializar() {
        this.contador = 0l;
        this.listaSolicitudesCreacion = new ArrayList<>();
        this.listaSeleccionadosCreacion = new ArrayList<>();
        this.bodegas = servicioBodega.obtenerListaBodegas();
        this.listaBodegas = obtenerSeleccionBodegas(bodegas);
        this.valorBodegaDestinoCreacion = Long.parseLong(servicioParametro.obtenerValorParametro(ParametrosEnum.BODEGA_PRINCIPAL.getIdParametro()).getValor());
        this.valorBodegaOrigenCreacion = Utilitario.obtenerIdBodegaUsuario();
        this.valorBodegaPendiente = Utilitario.obtenerIdBodegaUsuario();
        this.usuarioActual = Utilitario.obtenerUsuario();
        this.tipoSolicitud = "1";
        this.listaEstadosPendiente = obtenerEstadosDetalle(this.tipoSolicitud);
        this.listaEncabezadoSolicitud = new ArrayList<>();
        this.listaDetalleSolicitud = new ArrayList<>();
        this.contadorElementosPendientes = 0;
        this.informacionError = "";
        esAdministrador = !Utilitario.esAdministrador();
    }

    public List<Producto> busquedaProductos(String texto) {
        return servicioProducto.buscarProductosActivos(texto);
    }

    public List<SelectItem> obtenerSeleccionBodegas(List<Bodega> bodegas) {
        List<SelectItem> lista = new ArrayList<>();
        bodegas.forEach((p) -> {
            lista.add(new SelectItem(p.getBodegaPK().getId_bodega(), p.getDescripcion()));
        });

        return lista;
    }

    public void buscarProductoPorDescripcion() {
        try {
            this.listaProductosBusqueda = servicioProducto.listarProductosPorDescripcion(this.nombreProducto);
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.erorr.buscar.producto.descripcion"));
        }
    }

    public void seleccionarTipoSolicitud() {

        //Trata que el tipo de solicitud nunca sea nulo
        if (tipoSolicitud == null) {
            tipoSolicitud = tipoSolicitudAnterior;
        } else {
            tipoSolicitudAnterior = tipoSolicitud;
        }

        this.listaEstadosPendiente = obtenerEstadosDetalle(this.tipoSolicitud);
        this.tipoSolicitudAnterior = this.tipoSolicitud;
        limpiarVariablesPendientes();
        limpiarFiltrosPendientes();
    }

    public void agregarNuevoProducto(ActionEvent evento) {

        if (this.busquedadSeleccionados.size() > 0) {

            this.busquedadSeleccionados.forEach(e -> {
                this.contador++;
                Bodega bodOrigen = servicioBodega.obtenerBodega(valorBodegaOrigenCreacion);
                Bodega bodDestino = servicioBodega.obtenerBodega(valorBodegaDestinoCreacion);
                Producto producto = servicioProducto.obtenerProducto(e.getId_producto());

                if (!existeProducto(producto, bodDestino, listaSolicitudesCreacion)) {
                    listaSolicitudesCreacion.add(new NuevaSolicitud(contador, bodegas, bodOrigen, bodDestino, producto, 0L));
                } else {
                    MensajeUtil.agregarMensajeAdvertencia("El producto " + producto.getDescripcion() + " ya existe en la lista de productos.");
                }
            });
            limpiarNuevoProductoSolicitud();
        } else {
            limpiarNuevoProductoSolicitud();
            MensajeUtil.agregarMensajeAdvertencia("Digite un nuevo producto.");
        }
    }

    private boolean existeProducto(Producto idProducto, Bodega bodega, List<NuevaSolicitud> lista) {
        boolean resultado = false;
        for (NuevaSolicitud solic : lista) {
            if ((solic.getProducto().getId_producto().longValue() == idProducto.getId_producto().longValue())
                    & (solic.getBodegaDestino().getBodegaPK().getId_bodega().longValue() == bodega.getBodegaPK().getId_bodega().longValue())) {
                resultado = true;
                break;
            }
        }
        return resultado;
    }

    public void eliminarNuevaSolicitud(ActionEvent evento) {

        if (!listaSeleccionadosCreacion.isEmpty()) {
            listaSeleccionadosCreacion.forEach((nuevo) -> {
                listaSolicitudesCreacion.remove(nuevo);
            });
            listaSeleccionadosCreacion.clear();
        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar un producto para eliminar.");
        }
    }

    private Object[] prepararSolicitud(EstadoSolicitud estadoSolicitud, EstadoSolicitudDetalle estadoSolicitudDetalle) {
        InventarioSolicitud nuevaSolicitud
                = new InventarioSolicitud(valorBodegaOrigenCreacion, estadoSolicitud.getId(),
                        JSFUtil.obtenerFechaActual(), usuarioActual.getLogin(), JSFUtil.obtenerConsecutivoSolicitud());
        List<InventarioSolicitudDetalle> detalles = new ArrayList<>();
        listaSeleccionadosCreacion.forEach((p) -> {
            detalles.add(new InventarioSolicitudDetalle(null, p.getBodegaDestino().getBodegaPK().getId_bodega(), p.getProducto().getId_producto(),
                    p.getCantidad(), estadoSolicitudDetalle.getId()));
        });

        return new Object[]{nuevaSolicitud, detalles};
    }

    public boolean validarCantidadSolicitudes() {
        boolean resultado = false;
        for (NuevaSolicitud producto : listaSolicitudesCreacion) {
            if (producto.getCantidad() == null | (producto.getCantidad() != null && producto.getCantidad() <= 0)) {
                resultado = true;
            }
        }
        return resultado;
    }

    public void enviarNuevaSolicitud(ActionEvent evento) {
        if (!validarCantidadSolicitudes()) {
            if (!this.listaSeleccionadosCreacion.isEmpty()) {

                if (!verificarProductosEnLista(listaSolicitudesCreacion)) {
                    Object[] resultado = prepararSolicitud(EstadoSolicitud.PENDIENTE, EstadoSolicitudDetalle.SOLICITUD_ENVIADA);
                    servicioInventario.insertarSolicitudes((InventarioSolicitud) resultado[0],
                            (List<InventarioSolicitudDetalle>) resultado[1]);

                    listaSeleccionadosCreacion.forEach((nuevo) -> {
                        listaSolicitudesCreacion.remove(nuevo);
                    });
                    listaSeleccionadosCreacion.clear();
                    MensajeUtil.agregarMensajeInfo("Solicitud creada correctamente");
                } else {
                    MensajeUtil.agregarMensajeAdvertencia("Existe productos repetidos para una misma bodega.");
                }
            } else {
                MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar productos.");
            }
        } else {
            MensajeUtil.agregarMensajeAdvertencia("Todos los productos deben tener cantidades superiores a 0");
        }

    }

    public void guardarNuevaSolicitud(ActionEvent evento) {

        if (!this.listaSeleccionadosCreacion.isEmpty()) {

            if (!verificarProductosEnLista(listaSolicitudesCreacion)) {

                Object[] resultado = prepararSolicitud(EstadoSolicitud.PENDIENTE, EstadoSolicitudDetalle.PENDIENTE_ENVIO);
                servicioInventario.insertarSolicitudes((InventarioSolicitud) resultado[0],
                        (List<InventarioSolicitudDetalle>) resultado[1]);

                listaSeleccionadosCreacion.forEach((nuevo) -> {
                    listaSolicitudesCreacion.remove(nuevo);
                });
                listaSeleccionadosCreacion.clear();
            } else {
                MensajeUtil.agregarMensajeError("Existe productos repetidos para una misma bodega.");
            }
        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar productos.");
        }
    }

    private boolean verificarProductosEnLista(List<NuevaSolicitud> lista) {

        boolean resultado = false;

        //Ordena la lista para que los productos esten juntos
        Collections.sort(lista, (o1, o2) -> o1.getProducto().getId_producto().compareTo(o2.getProducto().getId_producto()));

        //Se valida que no exista duplicados
        for (int i = 0; i < lista.size() - 1; i++) {

            NuevaSolicitud N1 = lista.get(i);
            NuevaSolicitud N2 = lista.get(i + 1);

            if ((N1.getProducto().getId_producto().longValue() == N2.getProducto().getId_producto().longValue())
                    & (N1.getBodegaDestino().getBodegaPK().getId_bodega().longValue() == N2.getBodegaDestino().getBodegaPK().getId_bodega().longValue())) {
                resultado = true;
                break;
            }
        }

        return resultado;
    }

    public void seleccionarBodegaDestino(ValueChangeEvent evt) {
        this.valorBodegaDestinoCreacion = Long.parseLong(evt.getNewValue().toString());
    }

    public void buscarProducto() {
        try {
            if (codigoBarras != null && !codigoBarras.equals("")) {
                Producto productoEncontrado = null;
                productoEncontrado = servicioProducto.obtenerProductoPorCodigoBarras(codigoBarras);
                if (productoEncontrado != null) {
                    this.contador++;
                    Bodega bodOrigen = servicioBodega.obtenerBodega(valorBodegaOrigenCreacion);
                    Bodega bodDestino = servicioBodega.obtenerBodega(valorBodegaDestinoCreacion);
                    Producto producto = servicioProducto.obtenerProducto(productoEncontrado.getId_producto());

                    if (!existeProducto(producto, bodDestino, listaSolicitudesCreacion)) {
                        listaSolicitudesCreacion.add(new NuevaSolicitud(contador, bodegas, bodOrigen, bodDestino, producto, 0L));
                    } else {
                        MensajeUtil.agregarMensajeAdvertencia("El producto " + producto.getDescripcion() + " ya existe en la lista de productos.");
                    }
                } else {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.no.encontrado"));
                }
                limpiarNuevoProductoSolicitud();
            } else {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.buscar.producto.codigo.barras.facturacion"));
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    private void limpiarNuevoProductoSolicitud() {
        this.valorProductoCreacion = null;
        this.cantidadProductoCreacion = null;
        this.busquedadSeleccionados = new ArrayList<>();
        this.listaProductosBusqueda = new ArrayList<>();
        this.nombreProducto = "";
        this.codigoBarras = "";
    }

    public List<SelectItem> obtenerEstadosDetalle(String tipo) {
        List<SelectItem> lista = new ArrayList<>();

        switch (tipo) {
            case "1":
                lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_ENVIADA.getId(), EstadoSolicitudDetalle.SOLICITUD_ENVIADA.getDescripcion()));
                lista.add(new SelectItem(EstadoSolicitudDetalle.PENDIENTE_DESPACHO.getId(), EstadoSolicitudDetalle.PENDIENTE_DESPACHO.getDescripcion()));
                lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_RECHAZADA.getId(), EstadoSolicitudDetalle.SOLICITUD_RECHAZADA.getDescripcion()));
                this.estadoActualPendiente = EstadoSolicitudDetalle.SOLICITUD_ENVIADA.getId();
                break;
            case "2":
                lista.add(new SelectItem(EstadoSolicitudDetalle.PENDIENTE_ENVIO.getId(), EstadoSolicitudDetalle.PENDIENTE_ENVIO.getDescripcion()));
                lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_DESPACHADA.getId(), EstadoSolicitudDetalle.SOLICITUD_DESPACHADA.getDescripcion()));
                lista.add(new SelectItem(EstadoSolicitudDetalle.SOLICITUD_RECHAZADA.getId(), EstadoSolicitudDetalle.SOLICITUD_RECHAZADA.getDescripcion()));
                this.estadoActualPendiente = EstadoSolicitudDetalle.SOLICITUD_DESPACHADA.getId();
                break;
        }

        return lista;
    }

    public void buscarSolicitudesEstados(ActionEvent evento) {
        buscarSolicitudesEstadosTemporal();
    }

    private void buscarSolicitudesEstadosTemporal() {
        JSFUtil.limpiarFiltros("SolicitudesPendientes");
        JSFUtil.limpiarFiltros("DetallesPendientes");
        //Aveces el tipo de variable viene nula
        tipoSolicitud = (tipoSolicitud == null ? tipoSolicitudAnterior : tipoSolicitud);
        List<InventarioSolicitud> solicitudes = servicioInventario.obtenerEncabezadoSolicitud(valorBodegaPendiente,
                estadoActualPendiente, Integer.parseInt(tipoSolicitud), EstadoSolicitud.RECIBIDO.getId(), EstadoSolicitud.RECHAZADO.getId());
        this.limpiarVariablesPendientes();
        solicitudes.forEach((encabe) -> {
            Bodega origen = servicioBodega.obtenerBodega(encabe.getIdBodegaOrigen());
            this.listaEncabezadoSolicitud.add(new EncabezadoSolicitud(encabe, origen, EstadoSolicitud.obtenerEstado(encabe.getEstado())));
        });
    }

    public void buscarDetallesSolicitud(ActionEvent evento) {
        EncabezadoSolicitud encabezadoSolicitud = (EncabezadoSolicitud) evento.getComponent().getAttributes().get("idSolicitud");
        //Actualizar seleccion
        seleccionadoPendiente = encabezadoSolicitud;

        contadorElementosPendientes = 0;
        JSFUtil.limpiarFiltros("DetallesPendientes");
        List<InventarioSolicitudDetalle> detalles = servicioInventario.obtenerDetalleEncabSolicitudEstado(encabezadoSolicitud
                .getInventarioSolicitud().getIdInventarioSolicitud(), estadoActualPendiente);
        this.listaDetalleSolicitud.clear();
        //List<ElementosSolicitudDetalle> elementosSolicitud = new ArrayList<>();
        detalles.forEach((deta) -> {
            Bodega destino = servicioBodega.obtenerBodega(deta.getIdBodegaDestino());
            InventarioSolicitud inventarioSolicitud = servicioInventario.obtenerInventarioSolicitud(deta.getIdInventarioSolicitud());
            Producto producto = servicioProducto.obtenerProducto(deta.getIdProducto());
            EstadoSolicitudDetalle estadoSolicitudDetalle = EstadoSolicitudDetalle.obtenerEstado(deta.getEstado());
            List<InventarioSolicDetElementos> elementos = servicioInventario.obtenerElementosDetalle(deta.getIdInventarioSolicitudDetalle());
            List<ElementosSolicitudDetalle> elementosSolicitud = new ArrayList<>();
            elementos.forEach((elem -> {
                elementosSolicitud.add(new ElementosSolicitudDetalle(elem.getIdElemento(),
                        elem.getIdInventarioSolicitudDetalle(), elem.getFechaVencimiento(),
                        elem.getNumeroLote(), elem.getCantidad(), String.valueOf(++contadorElementosPendientes)));
            }));
            this.listaDetalleSolicitud.add(new DetalleSolicitud(inventarioSolicitud, destino, producto, estadoSolicitudDetalle, deta, elementosSolicitud));
        });
    }

    private void limpiarVariablesPendientes() {
        if (listaEncabezadoSolicitud != null) {
            this.listaEncabezadoSolicitud.clear();
        }
        if (listaDetalleSolicitud != null) {
            this.listaDetalleSolicitud.clear();
        }
        if (listaSelecProductosPendientes != null) {
            this.listaSelecProductosPendientes.clear();
        }
        this.seleccionadoPendiente = null;
    }

    private void limpiarFiltrosPendientes() {
        JSFUtil.limpiarFiltros("SolicitudesPendientes");
        JSFUtil.limpiarFiltros("DetallesPendientes");
    }

    public void aplicarEnviarDespacho(ActionEvent evento) {

        //La lista no puede quedar vacia
        if (!listaSelecProductosPendientes.isEmpty()) {

            //Valida si se agrego caracteristicas a las lineas
            if (validarEnvioDespacho(listaSelecProductosPendientes)) {

                //Se crea una lista nueva
                List<InventarioSolicitudDetalle> detalles = new ArrayList<>();

                //Se actualizan los estados a las lineas de solicitud detalle
                this.listaSelecProductosPendientes.forEach((p) -> {
                    InventarioSolicitudDetalle temporal = p.getInventarioSolicitudDetalle();
                    temporal.setEstado(EstadoSolicitudDetalle.PENDIENTE_DESPACHO.getId());
                    detalles.add(temporal);
                });

                //Se actualizan en base de datos las lineas de solicitud detalle
                servicioInventario.actualizarLineas(detalles);

                //Se remueva de la solicitud general aquellas lineas que ya pasaron de estado
                this.listaSelecProductosPendientes.forEach((p) -> {
                    this.listaDetalleSolicitud.remove(p);
                });

                //Se limpia la lista de seleccion
                this.listaSelecProductosPendientes.clear();

                if (!listaDetalleSolicitud.isEmpty()) {
                    //Se limpia los filtros de pendientes
                    JSFUtil.limpiarFiltros("DetallesPendientes");
                } else {
                    buscarSolicitudesEstadosTemporal();
                }

            } else {
                MensajeUtil.agregarMensajeError("Existen productos pendientes de agregar Lotes.");
            }
        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar productos");
        }
    }

    public void aplicarEnviarSolicitud(ActionEvent evento) {

        //Se valida si existen productos que despachar
        if (!listaSelecProductosPendientes.isEmpty()) {

            List<InventarioSolicitudDetalle> detalles = new ArrayList<>();

            //Se cambia el estado de la solicitud
            this.listaSelecProductosPendientes.forEach((p) -> {
                InventarioSolicitudDetalle temporal = p.getInventarioSolicitudDetalle();
                temporal.setEstado(EstadoSolicitudDetalle.SOLICITUD_DESPACHADA.getId());
                detalles.add(temporal);
            });

            //Se envia a actualizar la solicitud asi como su inventario salida detalle
            servicioInventario.actualizarLineas(detalles);

            //Se actualiza el inventario salida detalle
            for (InventarioSolicitudDetalle deta : detalles) {

                //Se obtiene la lista de salida detalle
                List<InventarioSalidaDetalle> listaSalidaDetalle
                        = servicioInventario.obtenerInvSaliDetalleFromInvSolicDetalle(deta.getIdInventarioSolicitudDetalle());

                //Se recorre la lista de salida detalle
                listaSalidaDetalle.forEach((p -> {

                    //Se obtiene el inventario salida para recuperar la bodega de origen
                    InventarioSalida inventarioSalida = servicioInventario.obtenerInvSalidaFromID(p.getIdInventarioSalida());

                    //Se construye la linea del inventario
                    Inventario temporal = new Inventario(inventarioSalida.getIdBodegaOrigen(), p.getIdProducto(), p.getFechaVencimiento(),
                            p.getNumeroLote());

                    //Se busca la linea del inventario
                    Inventario inventario = servicioInventario.consultarProductoInventPorBodega(temporal);

                    p.setIdEstado(EstadoInvSalidaDetalle.APLICADO.getId());
                    p.setIdInventario(inventario.getIdInventario());
                }));

                //Se envia a actualizar las lineas de salida detalle
                servicioInventario.actualizarLineas(listaSalidaDetalle);
            }

            //Se remove de la lista las lineas despachas
            this.listaSelecProductosPendientes.forEach((p) -> {
                this.listaDetalleSolicitud.remove(p);
            });

            //Se limpiar la lista de pendientes
            this.listaSelecProductosPendientes.clear();

            if (!listaDetalleSolicitud.isEmpty()) {
                //Se limpia los filtros de pendientes
                JSFUtil.limpiarFiltros("DetallesPendientes");
            } else {
                buscarSolicitudesEstadosTemporal();
            }
        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar productos");
        }
    }

    public void devolverSolicitud(ActionEvent evento) {

        if (!listaSelecProductosPendientes.isEmpty()) {

            //Se crea una lista nueva
            List<InventarioSolicitudDetalle> detalles = new ArrayList<>();

            //Se actualizan los estados a las lineas de solicitud detalle
            this.listaSelecProductosPendientes.forEach((p) -> {
                InventarioSolicitudDetalle temporal = p.getInventarioSolicitudDetalle();
                temporal.setEstado(EstadoSolicitudDetalle.SOLICITUD_ENVIADA.getId());
                detalles.add(temporal);
            });

            //Se actualizan en base de datos las lineas de solicitud detalle
            servicioInventario.actualizarLineas(detalles);

            //Se remueva de la solicitud general aquellas lineas que ya pasaron de estado
            this.listaSelecProductosPendientes.forEach((p) -> {
                this.listaDetalleSolicitud.remove(p);
            });

            //Se limpia la lista de seleccion
            this.listaSelecProductosPendientes.clear();

            if (!listaDetalleSolicitud.isEmpty()) {
                //Se limpia los filtros de pendientes
                JSFUtil.limpiarFiltros("DetallesPendientes");
            } else {
                buscarSolicitudesEstadosTemporal();
            }
        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar productos");
        }
    }

    private boolean validarEnvioDespacho(List<DetalleSolicitud> lista) {
        boolean resultado = true;
        for (DetalleSolicitud deta : lista) {
            if (deta.getListaElementos().isEmpty()) {
                resultado = false;
                break;
            }
        }
        return resultado;
    }

    public void agregarCaracteristica(ActionEvent evento) {

        if (modalDetalleSolicitud != null) {
            try {
                DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
                Date fecha = (fechaVencimientoModal == null ? null : formato.parse(fechaVencimientoModal));
                if (cantidadModal != null) {
                    if (!existeCaracteristica(fecha, numeroLoteModal, modalDetalleSolicitud)) {
                        contadorElementosPendientes++;
                        ElementosSolicitudDetalle elemento = new ElementosSolicitudDetalle(null, modalDetalleSolicitud.getInventarioSolicitudDetalle().getIdInventarioSolicitudDetalle(),
                                fecha, numeroLoteModal, cantidadModal, String.valueOf(contadorElementosPendientes));
                        modalDetalleSolicitud.getListaElementos().add(elemento);
                        this.limpiarVariablesModal();
                    } else {
                        MensajeUtil.agregarMensajeError("El lote existe.");
                    }
                } else {
                    MensajeUtil.agregarMensajeError("Debe digitar la cantidad suministrada.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean existeCaracteristica(Date fecha, String numeroLote, DetalleSolicitud detalleSolicitud) {
        boolean resultado = false;

        List<ElementosSolicitudDetalle> lista = detalleSolicitud.getListaElementos();

        for (ElementosSolicitudDetalle elem : lista) {
            if (elem.getFechaVencimiento() == fecha & elem.getNumeroLote() == numeroLote) {
                resultado = true;
                break;
            }
        }

        return resultado;
    }

    private void limpiarVariablesModal() {
        this.cantidadModal = null;
    }

    public void borrarCaracteristica(ActionEvent evento) {
        String id = JSFUtil.obtenerParametrosPagina("idDetalleElemento");
        modalDetalleSolicitud.borrarElemento(id);
    }

    public void guardarCaracteristicas(ActionEvent evento) {
        boolean errores = false;

        //Se valida la cantidad exacta de las caracteristicas y la existencia de las mismas
        if (modalDetalleSolicitud.verificarCantidadExacta()) {

            //Se valida si el guardado tiene elementos anteriores
            List<InventarioSolicDetElementos> listaElementos = servicioInventario.obtenerElementosDetalle(modalDetalleSolicitud.getInventarioSolicitudDetalle().getIdInventarioSolicitudDetalle());
            if (!listaElementos.isEmpty()) {

                //* ************* Ahora se incrementa los antiguos productos
                registrarIncrementoProductos();

                //* ************* Ahora se reducen los nuevos productos *****
                //Se procede a reducir productos del inventario
                errores = registrarReduccionProductos();
            } else {//La solicitud no tiene elementos anteriores

                //Se procede a reducir productos del inventario
                errores = registrarReduccionProductos();
            }

            if (!errores) {
                //Se limpian las variables
                this.limpiarVariablesModal();
                modalDetalleSolicitud = null;
                //Se cierra el modal
                JSFUtil.cerrarModal("panelModal");
            }

        } else {
            MensajeUtil.agregarMensajeError("La cantidad a guardar no concuerda con la cantidad solicitada.");
        }
    }

    private boolean registrarReduccionProductos() {
        boolean resultado = false;

        informacionError = "";
        informacionError = servicioInventario.obtenerExistenciaInventario(modalDetalleSolicitud).toString();
        if (informacionError.length() == 0) {

            //Se crean las entidades para las caracteristicas
            List<InventarioSolicDetElementos> elementos = new ArrayList<>();
            modalDetalleSolicitud.getListaElementos().forEach(q -> {
                elementos.add(new InventarioSolicDetElementos(modalDetalleSolicitud.getInventarioSolicitudDetalle().getIdInventarioSolicitudDetalle(),
                        q.getFechaVencimiento(), q.getNumeroLote(), q.getCantidad()));
            });

            //Se inicia la inclusion de las entidades en la base de datos
            servicioInventario.crearReservaTraslado(modalDetalleSolicitud.getBodegaDestino().getBodegaPK().getId_bodega(), modalDetalleSolicitud.getInventarioSolicitudDetalle(),
                    elementos);

            //Se actualiza los elementos dentro de la lista            
            this.listaDetalleSolicitud.forEach((p) -> {
                if (p.igual(modalDetalleSolicitud)) {
                    p.actualizarElementos(modalDetalleSolicitud.getListaElementos());
                }
            });

        } else {
            resultado = true;
            JSFUtil.abrirModal("panelAviso");
        }

        return resultado;
    }

    private void registrarIncrementoProductos() {

        //Se inicia el incremento del inventario y eliminacion de las caracteristicas
        servicioInventario.crearRetornoTraslado(modalDetalleSolicitud.getBodegaDestino().getBodegaPK().getId_bodega(), modalDetalleSolicitud.getInventarioSolicitudDetalle());

        //Se actualiza los elementos dentro de la lista visual
        this.listaDetalleSolicitud.forEach((p) -> {
            if (p.igual(modalDetalleSolicitud)) {
                p.actualizarElementos(modalDetalleSolicitud.getListaElementos());
            }
        });

    }

    public void liberarLotes(ActionEvent evento) {

        //Se valida si se selecciono producto para liberar reserva
        if (!this.listaSelecProductosPendientes.isEmpty()) {

            for (DetalleSolicitud p : listaSelecProductosPendientes) {

                //Se obtiene las caracteristicas de cada solicitud detalle
                List<InventarioSolicDetElementos> listaElementos = servicioInventario.obtenerElementosDetalle(p.getInventarioSolicitudDetalle().getIdInventarioSolicitudDetalle());
                //Se pregunta si tiene elementos
                if (!listaElementos.isEmpty()) {

                    //* ************* Ahora se reintegra el producto al inventario
                    //Se inicia el incremento del inventario y eliminacion de las caracteristicas
                    servicioInventario.crearRetornoTraslado(p.getBodegaDestino().getBodegaPK().getId_bodega(), p.getInventarioSolicitudDetalle());

                    //Se actualiza los elementos dentro de la lista visual
                    this.listaDetalleSolicitud.forEach((q) -> {
                        if (q.igual(p)) {
                            q.actualizarElementos(new ArrayList<>());
                        }
                    });
                }
            }

            MensajeUtil.agregarMensajeInfo("Los productos fueron liberados.");

        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar productos");
        }

    }

    public void abrirCaracteristicas(ActionEvent evento) {
        Long idInventarioSolicitudDetalle = Long.parseLong(JSFUtil.obtenerParametrosPagina("idDetalleSolicitud"));
        /**
         * ************** Se reconstruye el pojo *****************************
         */
        Object[] datos = servicioInventario.obtenerDetalleSolicitud(idInventarioSolicitudDetalle);
        if (datos != null) {
            InventarioSolicitudDetalle inventarioSolicitudDetalle = (InventarioSolicitudDetalle) datos[0];
            List<InventarioSolicDetElementos> listaSolicDetElementos = (List<InventarioSolicDetElementos>) datos[1];
            InventarioSolicitud inventarioSolicitud = servicioInventario.obtenerInventarioSolicitud(inventarioSolicitudDetalle.getIdInventarioSolicitud());
            Bodega destino = servicioBodega.obtenerBodega(inventarioSolicitudDetalle.getIdBodegaDestino());
            Producto producto = servicioProducto.obtenerProducto(inventarioSolicitudDetalle.getIdProducto());
            DetalleSolicitud detalle = new DetalleSolicitud(inventarioSolicitud, destino, producto,
                    EstadoSolicitudDetalle.obtenerEstado(inventarioSolicitudDetalle.getEstado()), inventarioSolicitudDetalle);
            List<ElementosSolicitudDetalle> listaElementos = new ArrayList<>();
            listaSolicDetElementos.forEach((p -> {
                contadorElementosPendientes++;
                listaElementos.add(new ElementosSolicitudDetalle(p.getIdElemento(), inventarioSolicitudDetalle.getIdInventarioSolicitudDetalle(),
                        p.getFechaVencimiento(), p.getNumeroLote(), p.getCantidad(), String.valueOf(contadorElementosPendientes)));
            }));
            detalle.setListaElementos(listaElementos);
            /**
             * ************************************************************************
             */

            //Se actualiza la variable
            modalDetalleSolicitud = detalle;

            //Se carga los numeros de lotes iniciales
            listaNumeroLotes = obtenerLotes(destino.getBodegaPK().getId_bodega(), producto.getId_producto());
            String loteCruda = (listaNumeroLotes.isEmpty() ? null : listaNumeroLotes.get(0).getValue() == null ? null : listaNumeroLotes.get(0).getValue().toString());
            this.numeroLoteModal = loteCruda;
            listaFechaVencimiento = obtenerFechasVencimiento(destino.getBodegaPK().getId_bodega(), producto.getId_producto(), (loteCruda != null ? String.valueOf(loteCruda) : null));
            try {
                DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
                this.fechaVencimientoModal = (listaFechaVencimiento.isEmpty() ? null : listaFechaVencimiento.get(0).getValue() == null ? null : formato.format(formato.parse(listaFechaVencimiento.get(0).getValue().toString())));
            } catch (ParseException pe) {
                this.fechaVencimientoModal = null;
            }

            //Se abre el modal
            JSFUtil.abrirModal("panelModal");
        } else {
            modalDetalleSolicitud = null;
            MensajeUtil.agregarMensajeError("No se logró cargar el detalle del producto.");
        }

    }

    public void incluirRapido(ActionEvent evento) {
        boolean errores = false;
        Long idInventarioSolicitudDetalle = Long.parseLong(JSFUtil.obtenerParametrosPagina("idDetalleSolicitudRapida"));
        Object[] datos = servicioInventario.obtenerDetalleSolicitud(idInventarioSolicitudDetalle);

        if (datos != null) {
            InventarioSolicitudDetalle inventarioSolicitudDetalle = (InventarioSolicitudDetalle) datos[0];
            List<InventarioSolicDetElementos> listaSolicDetElementos = (List<InventarioSolicDetElementos>) datos[1];

            InventarioSolicitud inventarioSolicitud = servicioInventario.obtenerInventarioSolicitud(inventarioSolicitudDetalle.getIdInventarioSolicitud());
            Bodega destino = servicioBodega.obtenerBodega(inventarioSolicitudDetalle.getIdBodegaDestino());
            Producto producto = servicioProducto.obtenerProducto(inventarioSolicitudDetalle.getIdProducto());

            DetalleSolicitud detalle = new DetalleSolicitud(inventarioSolicitud, destino, producto,
                    EstadoSolicitudDetalle.obtenerEstado(inventarioSolicitudDetalle.getEstado()), inventarioSolicitudDetalle);

            List<ElementosSolicitudDetalle> listaElementos = new ArrayList<>();
            listaSolicDetElementos.forEach((p -> {
                contadorElementosPendientes++;
                listaElementos.add(new ElementosSolicitudDetalle(p.getIdElemento(), inventarioSolicitudDetalle.getIdInventarioSolicitudDetalle(),
                        p.getFechaVencimiento(), p.getNumeroLote(), p.getCantidad(), String.valueOf(contadorElementosPendientes)));
            }));

            detalle.setListaElementos(listaElementos);
            modalDetalleSolicitud = detalle;

            if (modalDetalleSolicitud.getListaElementos().size() == 0) {

                //Se crea el elemento de detalle para verificar si se puede reducir del inventario
                contadorElementosPendientes++;
                ElementosSolicitudDetalle elemento = new ElementosSolicitudDetalle(null, modalDetalleSolicitud.getInventarioSolicitudDetalle().getIdInventarioSolicitudDetalle(),
                        null, null, modalDetalleSolicitud.getInventarioSolicitudDetalle().getCantidad(), String.valueOf(contadorElementosPendientes));
                modalDetalleSolicitud.getListaElementos().add(elemento);

                //Se procede a reducir productos del inventario
                errores = registrarReduccionProductos();

                if (!errores) {
                    modalDetalleSolicitud = null;
                    MensajeUtil.agregarMensajeInfo("Lote ha sido asignado.");
                }
            } else {
                MensajeUtil.agregarMensajeAdvertencia("Ya existe un lote asignado.");
            }

        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar un producto.");
        }
    }

    public void cerrarCaracteristicas(ActionEvent evento) {
        this.limpiarVariablesModal();
        modalDetalleSolicitud = null;
        JSFUtil.cerrarModal("panelModal");
    }

    public boolean getVisualizarColumnaEnviada() {
        if (this.estadoActualPendiente == null) {
            return false;
        } else {
            return EstadoSolicitudDetalle.SOLICITUD_ENVIADA.getId().equals(estadoActualPendiente);
        }
    }

    public void setVisualizarColumnaEnviada(boolean estado) {
    }

    public void recibirSolicitud(ActionEvent evento) {
        try {
            if (!this.listaSelecProductosPendientes.isEmpty()) {

                //Se actualizan las lineas de solicitud detalle
                List<InventarioSolicitudDetalle> detalles = new ArrayList<>();
                this.listaSelecProductosPendientes.forEach((p -> {
                    p.getInventarioSolicitudDetalle().setEstado(EstadoSolicitudDetalle.SOLICITUD_RECIBIDA.getId());
                    detalles.add(p.getInventarioSolicitudDetalle());
                }));

                //Se obtiene el inventario solicitud base de la linea de solicitud detalle
                InventarioSolicitud inventarioSolicitud = listaSelecProductosPendientes.get(0).getInventarioSolicitud();

                //Se actualizan en base de datos las lineas de solicitud detalle, ingreso inventario e inventario general
                servicioInventario.registrarIngresoSolicitudABodega(inventarioSolicitud, detalles, false);

                //Se remueva de la solicitud general aquellas lineas que ya pasaron de estado
                this.listaSelecProductosPendientes.forEach((p) -> {
                    this.listaDetalleSolicitud.remove(p);
                });

                //Se limpia la lista de seleccion
                this.listaSelecProductosPendientes.clear();

                if (!listaDetalleSolicitud.isEmpty()) {
                    //Se limpia los filtros de pendientes
                    JSFUtil.limpiarFiltros("DetallesPendientes");
                } else {
                    buscarSolicitudesEstadosTemporal();
                }
            } else {
                MensajeUtil.agregarMensajeError("Debe seleccionar productos");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajeUtil.agregarMensajeError("Ha ocurrido un error cuando se recibieron los productos");
        }

    }

    public void rechazarSolicitud(ActionEvent evento) {

        if (!this.listaSelecProductosPendientes.isEmpty()) {

            //Se actualizan las lineas de solicitud detalle
            List<InventarioSolicitudDetalle> detalles = new ArrayList<>();
            this.listaSelecProductosPendientes.forEach((p -> {
                p.getInventarioSolicitudDetalle().setEstado(EstadoSolicitudDetalle.SOLICITUD_RECHAZADA.getId());
                detalles.add(p.getInventarioSolicitudDetalle());
            }));

            //Se actualizan en base de datos las lineas de solicitud detalle
            servicioInventario.actualizarLineas(detalles);

            //Se remueva de la solicitud general aquellas lineas que ya pasaron de estado
            this.listaSelecProductosPendientes.forEach((p) -> {
                this.listaDetalleSolicitud.remove(p);
            });

            //Se limpia la lista de seleccion
            this.listaSelecProductosPendientes.clear();

            if (!listaDetalleSolicitud.isEmpty()) {
                //Se limpia los filtros de pendientes
                JSFUtil.limpiarFiltros("DetallesPendientes");
            } else {
                buscarSolicitudesEstadosTemporal();
            }

        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar productos");
        }
    }

    public void devolverRechazoSolicitud(ActionEvent evento) {

        if (!this.listaSelecProductosPendientes.isEmpty()) {

            //Se actualizan las lineas de solicitud detalle
            List<InventarioSolicitudDetalle> detalles = new ArrayList<>();
            this.listaSelecProductosPendientes.forEach((p -> {
                p.getInventarioSolicitudDetalle().setEstado(EstadoSolicitudDetalle.SOLICITUD_DESPACHADA.getId());
                detalles.add(p.getInventarioSolicitudDetalle());
            }));

            //Se actualizan en base de datos las lineas de solicitud detalle
            servicioInventario.actualizarLineas(detalles);

            //Se remueva de la solicitud general aquellas lineas que ya pasaron de estado
            this.listaSelecProductosPendientes.forEach((p) -> {
                this.listaDetalleSolicitud.remove(p);
            });

            //Se limpia la lista de seleccion
            this.listaSelecProductosPendientes.clear();

            if (!listaDetalleSolicitud.isEmpty()) {
                //Se limpia los filtros de pendientes
                JSFUtil.limpiarFiltros("DetallesPendientes");
            } else {
                buscarSolicitudesEstadosTemporal();
            }

        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar productos");
        }
    }

    public void enviarSolicitud(ActionEvent evento) {

        if (!this.listaSelecProductosPendientes.isEmpty()) {

            List<InventarioSolicitudDetalle> detalles = new ArrayList<>();
            this.listaSelecProductosPendientes.forEach((p -> {
                p.getInventarioSolicitudDetalle().setEstado(EstadoSolicitudDetalle.SOLICITUD_ENVIADA.getId());
                detalles.add(p.getInventarioSolicitudDetalle());
            }));

            //Se actualizan en base de datos las lineas de solicitud detalle
            servicioInventario.actualizarLineas(detalles);

            listaSelecProductosPendientes.forEach((nuevo) -> {
                listaDetalleSolicitud.remove(nuevo);
            });

            listaSelecProductosPendientes.clear();

            if (!listaDetalleSolicitud.isEmpty()) {
                //Se limpia los filtros de pendientes
                JSFUtil.limpiarFiltros("DetallesPendientes");
            } else {
                buscarSolicitudesEstadosTemporal();
            }
        } else {
            MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar productos.");
        }
    }

    public void reintegrarSolicitud(ActionEvent evento) throws Exception {

        if (!this.listaSelecProductosPendientes.isEmpty()) {

            //Se actualizan las lineas de solicitud detalle
            List<InventarioSolicitudDetalle> detalles = new ArrayList<>();
            this.listaSelecProductosPendientes.forEach((p -> {
                p.getInventarioSolicitudDetalle().setEstado(EstadoSolicitudDetalle.SOLICITUD_DEVUELTA.getId());
                detalles.add(p.getInventarioSolicitudDetalle());
            }));

            //Se obtiene el inventario solicitud base de la linea de solicitud detalle
            InventarioSolicitud inventarioSolicitud = listaSelecProductosPendientes.get(0).getInventarioSolicitud();

            try {
                //Se actualizan en base de datos las lineas de solicitud detalle, ingreso inventario e inventario general
                servicioInventario.registrarIngresoSolicitudABodega(inventarioSolicitud, detalles, true);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }

            //Se remueva de la solicitud general aquellas lineas que ya pasaron de estado
            this.listaSelecProductosPendientes.forEach((p) -> {
                this.listaDetalleSolicitud.remove(p);
            });

            //Se limpia la lista de seleccion
            this.listaSelecProductosPendientes.clear();

            if (!listaDetalleSolicitud.isEmpty()) {
                //Se limpia los filtros de pendientes
                JSFUtil.limpiarFiltros("DetallesPendientes");
            } else {
                buscarSolicitudesEstadosTemporal();
            }
        } else {
            MensajeUtil.agregarMensajeError("Debe seleccionar productos");
        }
    }

    public List<SelectItem> obtenerLotes(Long idBodega, Long idProducto) {
        List<String> lotes = servicioInventario.obtenerNumeroLoteFromProducto(idBodega, idProducto);
        List<SelectItem> lista = new ArrayList<>();
        lotes.forEach((p -> {
            lista.add(new SelectItem(p, p));
        }));

        return lista;
    }

    public List<SelectItem> obtenerFechasVencimiento(Long idBodega, Long idProducto, String lote) {
        List<Date> lotes = servicioInventario.obtenerFechaVencimientoFromLote(idBodega, idProducto, lote);
        List<SelectItem> lista = new ArrayList<>();
        DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        lotes.forEach((p -> {
            String fechaTexto = (p == null ? null : formato.format(p));
            lista.add(new SelectItem(fechaTexto, fechaTexto));
        }));

        return lista;
    }

    public void cargarFechasVencimiento() {
        Bodega bodega = modalDetalleSolicitud.getBodegaDestino();
        Producto producto = modalDetalleSolicitud.getProducto();
        listaFechaVencimiento = obtenerFechasVencimiento(bodega.getBodegaPK().getId_bodega(), producto.getId_producto(), numeroLoteModal);
    }

    public void eliminarSolicitud(ActionEvent evento) {
        Long idInventarioSolicitudDetalle = Long.parseLong(JSFUtil.obtenerParametrosPagina("idDetalleEliminar"));
        InventarioSolicitudDetalle solicitudDetalle = servicioInventario.obtenerInventSolicDetalle(idInventarioSolicitudDetalle);
        List<InventarioSolicDetElementos> reservas = servicioInventario.obtenerElementosDetalle(idInventarioSolicitudDetalle);
        InventarioSolicitud solicitud = servicioInventario.obtenerInventarioSolicitud(solicitudDetalle.getIdInventarioSolicitud());

        //Verifica que no exista reservas de lotes
        if (!reservas.isEmpty()) {

            MensajeUtil.agregarMensajeError("Debe liberar los lotes antes de realizar la eliminación.");
            return;
        }

        //Se borra la linea
        servicioInventario.eliminar(solicitudDetalle);
        Iterator<DetalleSolicitud> iterable = listaDetalleSolicitud.iterator();
        while (iterable.hasNext()) {
            DetalleSolicitud deta = iterable.next();
            if (Objects.equals(deta.getInventarioSolicitudDetalle().getIdInventarioSolicitudDetalle(), solicitudDetalle.getIdInventarioSolicitudDetalle())) {
                iterable.remove();
                break;
            }
        }

        JSFUtil.limpiarFiltros("DetallesPendientes");

        //Verifica si la solicitud se quedo sin lineas
        List<InventarioSolicitudDetalle> hijos = servicioInventario.obtenerInvSolicDetalle(solicitud.getIdInventarioSolicitud());
        if (hijos.isEmpty()) {
            servicioInventario.eliminar(solicitud);
            Iterator<EncabezadoSolicitud> iterador = listaEncabezadoSolicitud.iterator();
            while (iterador.hasNext()) {
                EncabezadoSolicitud deta = iterador.next();
                if (Objects.equals(deta.getInventarioSolicitud().getIdInventarioSolicitud(), solicitud.getIdInventarioSolicitud())) {
                    iterador.remove();
                    break;
                }
            }
            JSFUtil.limpiarFiltros("SolicitudesPendientes");
        }

    }

    public void sustituirSolicitud(ActionEvent evento) {
        Long idInventarioSolicitudDetalle = Long.parseLong(JSFUtil.obtenerParametrosPagina("idDetalleSustituir"));
        solicDetalleActual = servicioInventario.obtenerInventSolicDetalle(idInventarioSolicitudDetalle);

        //Se abre el modal
        JSFUtil.abrirModal("panelSustitucion");
    }

    public void guardarSustitucion(ActionEvent evento) {

        //Se validan que los valores 
        if (valorProductoSustituto == null | solicDetalleActual == null) {
            MensajeUtil.agregarMensajeError("No se seleccionó el producto correctamente.");
            return;
        }

        //Se obtiene el producto
        String[] partes = valorProductoSustituto.split("#");
        Producto productoSustituto = servicioProducto.obtenerProducto(Long.parseLong(partes[0]));

        //Verifica que no exista reservas de lotes
        List<InventarioSolicDetElementos> reservas = servicioInventario.obtenerElementosDetalle(solicDetalleActual.getIdInventarioSolicitudDetalle());
        if (!reservas.isEmpty()) {

            MensajeUtil.agregarMensajeError("Debe liberar los lotes antes de realizar la sustitución.");
            return;
        }

        //Se actualiza la entidad
        solicDetalleActual.setIdProducto(productoSustituto.getId_producto());
        servicioInventario.actualizar(solicDetalleActual);

        //Se actualiza la lista visual
        listaDetalleSolicitud.forEach((p -> {
            if (Objects.equals(p.getInventarioSolicitudDetalle().getIdInventarioSolicitudDetalle(), solicDetalleActual.getIdInventarioSolicitudDetalle())) {
                p.setProducto(productoSustituto);
                p.getInventarioSolicitudDetalle().setIdProducto(productoSustituto.getId_producto());
            }
        }));

        valorProductoSustituto = null;

        //Se cierra el modal
        JSFUtil.cerrarModal("panelSustitucion");
    }

    public void cerrarSustitucion(ActionEvent evento) {

        JSFUtil.cerrarModal("panelSustitucion");
        //Se limpia la variable global
        solicDetalleActual = null;
        valorProductoSustituto = null;
    }

}
