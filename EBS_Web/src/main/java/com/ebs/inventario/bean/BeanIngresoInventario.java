/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.inventario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.IngresoTipo;
import com.ebs.constantes.enums.TiposPrecios;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.InventarioIngreso;
import com.ebs.entidades.InventarioIngresoDetalle;
import com.ebs.entidades.InventarioPrecioProducto;
import com.ebs.entidades.Producto;
import com.ebs.entidades.Usuario;
import com.ebs.exception.ExcepcionManager;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.IngresoMateriales;
import com.ebs.modelos.ModeloProducto;
import com.ebs.modelos.ModeloTipoPrecio;
import com.ebs.modelos.NuevoProducto;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import lombok.Data;

/**
 *
 * @author
 */
@ManagedBean
@ViewScoped
@Data
public class BeanIngresoInventario {

    //Estos valores estan asociados a los items de la tabla
    private List<NuevoProducto> listaNuevosProductos;

    private List<NuevoProducto> seleccionados;

    private List<ModeloProducto> busquedadSeleccionados;

    //Estos son los elementos de escogencia
    private String valorproducto;

    private List<SelectItem> listaBodegas;
    private Long valorBodega;

    private Usuario usuarioActual;

    //Estos son clases de servicio
    @Inject
    private ServicioBodega servicioBodega;

    @Inject
    private ServicioProducto servicioProducto;
    private String nombreProducto;

    private String busquedaCodigoBarras;
    @Inject
    private ServicioInventario servicioInventario;

    private long contador;

    private List<ModeloProducto> listaProductosBusqueda;

    private boolean verPanelBuscarProducto = true;

    private boolean esAdministrador;

    /**
     * Creates a new instance of BeanIngresoInventario
     */
    public BeanIngresoInventario() {
    }

    @PostConstruct
    public void inicializar() {
        listaBodegas = obtenerSeleccionBodegas(servicioBodega.obtenerListaBodegas());
        usuarioActual = Utilitario.obtenerUsuario();
        listaNuevosProductos = new ArrayList<>();
        seleccionados = new ArrayList<>();
        valorBodega = Utilitario.obtenerIdBodegaUsuario();
        esAdministrador = !Utilitario.esAdministrador();
    }

    public void buscarProductoPorDescripcion() {
        try {
            if (nombreProducto != null && !nombreProducto.trim().equals("")) {
                this.listaProductosBusqueda = servicioProducto.listarProductosPorDescripcion(nombreProducto);
            } else {
                if (listaProductosBusqueda != null) {
                    listaProductosBusqueda.clear();
                }
            }
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.erorr.buscar.producto.descripcion"));
        }
    }

    public void buscarProducto() {
        try {
            if (busquedaCodigoBarras != null && !busquedaCodigoBarras.trim().equals("")) {
                Producto productoEncontrado = servicioProducto.obtenerProductoPorCodigoBarras(busquedaCodigoBarras);
                if (productoEncontrado != null) {
                    this.valorproducto = productoEncontrado.getId_producto() + "#" + productoEncontrado.getDescripcion();
                    agregarNuevaFilaAuto();
                    this.valorproducto = null;
                    busquedaCodigoBarras = "";
                } else {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.no.encontrado"));
                }
            }
            /*else {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.buscar.producto.codigo.barras.facturacion"));
            }*/
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public List<SelectItem> obtenerSeleccionBodegas(List<Bodega> bodegas) {
        List<SelectItem> lista = new ArrayList<>();
        bodegas.forEach((p) -> {
            lista.add(new SelectItem(p.getBodegaPK().getId_bodega(), p.getDescripcion()));
        });

        return lista;
    }

    public void agregarNuevaFila(ActionEvent action) {

        if (busquedadSeleccionados != null && !busquedadSeleccionados.isEmpty()) {
            busquedadSeleccionados.forEach((p) -> {
                contador++;
                String nuevoNombre = p.getId_producto() + "#" + p.getDescripcion();
                List<ModeloTipoPrecio> listaTiposPrecios = this.servicioProducto.obtenerPreciosProductos(p.getId_producto());

                listaNuevosProductos.add(new NuevoProducto(contador, nuevoNombre,
                        this.servicioInventario.obtenerUltimoPrecioIngresadoProductoInventario(p.getId_producto()),
                        this.servicioInventario.obtenerUltimoProovedorIngresadoProductoInventario(p.getId_producto()),
                        
                        listaTiposPrecios.stream().filter(predicate -> predicate.getProductoPrecio().getProductoPrecioPK().getId_tipo().equals(
                                TiposPrecios.GENERAL.getTipoPrecio()))
                                .findAny().get(),
                        
                        listaTiposPrecios.stream().filter(predicate -> predicate.getProductoPrecio().getProductoPrecioPK().getId_tipo().equals(
                                TiposPrecios.POR_MAYOR.getTipoPrecio()))
                                .findAny().get()
                        
                ));
            });
            valorproducto = null;
            busquedadSeleccionados.clear();
        } else {
            MensajeUtil.agregarMensajeError("No existe producto ha agregar.");
        }
    }

    public void agregarNuevaFilaAuto() {

        if (valorproducto != null) {
            contador++;
            listaNuevosProductos.add(new NuevoProducto(contador, valorproducto));
            limpiarBusqueda();
        } else {
            MensajeUtil.agregarMensajeError("No existe producto ha agregar.");
        }
    }

    public void eliminarFila(ActionEvent action) {

        if (!seleccionados.isEmpty()) {
            for (NuevoProducto sele : seleccionados) {
                for (Iterator<NuevoProducto> fila = listaNuevosProductos.iterator(); fila.hasNext();) {
                    if (fila.next().getNumeroFila().longValue() == sele.getNumeroFila().longValue()) {
                        fila.remove();
                        break;
                    }
                }
            }

            seleccionados.clear();
        } else {
            MensajeUtil.agregarMensajeError("No existe producto ha eliminar.");
        }
    }

    private boolean validarCampos() {
        Boolean resultado = true;
        for (NuevoProducto p : seleccionados) {
            if (p.getCantidad() < 1
                    | (p.getNombreProducto() == null ? true : p.getNombreProducto().isEmpty()) //| (p.getPrecio() == null | (p.getPrecio().equals(new BigDecimal("0.0"))))
                    ) {
                resultado = false;
            }
        }
        return resultado;
    }

    public void guardarInventario(ActionEvent action) {

        if (!seleccionados.isEmpty()) {
            if (validarCampos()) {

                //Se crean las variables
                IngresoMateriales ingresoMateriales;
                List<InventarioIngresoDetalle> detalles = new ArrayList<>();
                List<InventarioPrecioProducto> precios = new ArrayList<>();

                //Se completan las entidades
                InventarioIngreso ingreso = new InventarioIngreso(valorBodega, usuarioActual.getLogin(),
                        JSFUtil.obtenerFechaActual(), null, IngresoTipo.INGRESO_BODEGA.getIdTipo());

                seleccionados.forEach((p) -> {
                    detalles.add(new InventarioIngresoDetalle(null, p.getIdProductoActual(), p.getCantidad(),
                            p.getFechaVencimiento(),
                            (p.getNumeroLote() == null ? p.getNumeroLote() : p.getNumeroLote().trim().toUpperCase()),
                            null, JSFUtil.obtenerFechaActual(), p.getNombreProveedor(), p.getPrecio()));

                });

                ingresoMateriales = new IngresoMateriales(ingreso, detalles);

                try {
                    //Se inserta al inventario
                    servicioInventario.insertarInventarioIngreso(ingresoMateriales);
                    MensajeUtil.agregarMensajeInfo("Se ha incluido los productos al inventario.");
                    seleccionados.forEach((nuevo) -> {
                        listaNuevosProductos.remove(nuevo);
                    });
                    seleccionados.clear();
                } catch (PersistenceException e) {
                    MensajeUtil.agregarMensajeError("No se logró ingresar los productos al inventario.");
                }

            } else {
                MensajeUtil.agregarMensajeAdvertencia("Existen productos con cantidades menores o igual a cero o bien productos a ingresar sin los precios correspondientes");
            }
        } else {
            MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar productos para incluir al inventario.");
        }
    }

    public List<Producto> busquedadProductos(String texto) {
        return servicioProducto.buscarProductosActivos(texto);
    }

    private void limpiarBusqueda() {
        valorproducto = null;
    }
}
