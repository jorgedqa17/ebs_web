package com.ebs.inventario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.Inventario;
import com.ebs.entidades.Producto;
import com.ebs.entidades.Usuario;
import com.ebs.exception.ExcepcionManager;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.FiltroInventario;
import com.ebs.modelos.InventarioGeneral;
import com.ebs.modelos.ModeloProducto;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Jorge Quesada
 */
@ManagedBean
@ViewScoped
public class BeanConsultaGeneralInventario {

    @Inject
    private ServicioInventario servicioInventario;

    @Inject
    private ServicioBodega servicioBodega;

    @Inject
    private ServicioProducto servicioProducto;

    /**
     * **************** Variables *******************
     */
    @Getter
    @Setter
    private List<InventarioGeneral> listaGeneralInventario;

    @Getter
    @Setter
    private List<InventarioGeneral> listaFiltrada;

    @Getter
    @Setter
    private FiltroInventario filtroInventario;

    @Getter
    @Setter
    private List<SelectItem> listaBodegas;

    @Getter
    @Setter
    private List<SelectItem> listaProductos;

    @Getter
    @Setter
    private List<SelectItem> listaEstados;

    @Getter
    @Setter
    private InventarioGeneral lineaSeleccionada;
    @Getter
    @Setter
    private StreamedContent reporteDesplegar = null;

    @Getter
    @Setter
    private InventarioGeneral lineaEliminacion;

    @Getter
    @Setter
    private Usuario usuarioActual;

    @Getter
    @Setter
    private String motivo;

    @Getter
    @Setter
    private Long cantidadExistencia;

    @Getter
    @Setter
    private boolean esAdministrador;

    @Inject
    private ServicioReporte servicioReporte;
    @Getter
    @Setter
    private List<ModeloProducto> listaProductosBusqueda;

    @Getter
    @Setter
    private List<ModeloProducto> busquedadSeleccionados;
    @Getter
    @Setter
    private String busquedaCodigoBarras;
    @Getter
    @Setter
    private String nombreProducto;

    private List<Bodega> listaBodegasEncontradas;

    /**
     * ***********************************************
     */
    @PostConstruct
    public void inicializar() {
        filtroInventario = new FiltroInventario();
        listaBodegas = obtenerSeleccionBodegas();
        listaProductos = obtenerSeleccionProductos();
        listaEstados = obtenerSeleccionEstado();
        filtroInventario.setIdBodegaSeleccionada(Utilitario.obtenerIdBodegaUsuario());
        listaGeneralInventario = new ArrayList<>();
        lineaSeleccionada = new InventarioGeneral();
        usuarioActual = Utilitario.obtenerUsuario();
        esAdministrador = Utilitario.esAdministrador();
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

                    busquedaCodigoBarras = "";
                } else {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.no.encontrado"));
                }
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public String getNombreBodega(Long idBodega) {
        return this.listaBodegasEncontradas
                .stream()
                .filter(bodega -> bodega.getBodegaPK().getId_bodega().equals(idBodega))
                .findFirst()
                .get().getDescripcion();
    }

    private void cargarInventario() {

        if (this.busquedadSeleccionados != null) {
            this.busquedadSeleccionados.forEach(elemento -> {
                filtroInventario.getListaIdsProductos().add(elemento.getId_producto());
            });
        }

        List<Inventario> lista = servicioInventario.obtenerInventarioGeneral(filtroInventario);
        listaGeneralInventario.clear();
        lista.forEach((p -> {
            Bodega bodega = servicioBodega.obtenerBodega(p.getIdBodega());
            Producto producto = servicioProducto.obtenerProducto(p.getIdProducto());
            listaGeneralInventario.add(new InventarioGeneral(p, bodega, producto));
        }));
    }

    public void obtenerNuevaLista(ActionEvent evento) {
        JSFUtil.limpiarFiltros("listaProductos");
        cargarInventario();
    }

    public String retornarConsulta() {
        StringBuilder resultado = new StringBuilder()
                .append("SELECT T3.DESCRIPCION, T1.CANTEXISTENCIA, T2.DESCRIPCION as producto ")
                .append("FROM SEARMEDICA.INVENTARIO T1 INNER JOIN SEARMEDICA.PRODUCTO T2 ")
                .append("ON T1.ID_PRODUCTO = T2.ID_PRODUCTO INNER JOIN SEARMEDICA.BODEGA T3 ")
                .append("ON T1.ID_BODEGA = T3.ID_BODEGA ")
                .append("WHERE T1.ACTIVO = 1 ");
        String idsProductos = "";

        if (filtroInventario.getListaIdsProductos().size() > 0) {
            resultado.append(" and T1.id_producto in ( ");
            for (Long idProducto : filtroInventario.getListaIdsProductos()) {
                idsProductos = idsProductos + (idsProductos.equals("") ? idProducto : "," + idProducto);
            }
            resultado.append(idsProductos);
            resultado.append(" )");
        }

        if (!this.filtroInventario.getIdBodegaSeleccionada().equals(0L)) {
            resultado.append(" AND T1.ID_BODEGA = ")
                    .append(this.filtroInventario.getIdBodegaSeleccionada());
        }
        resultado.append(" GROUP BY T3.DESCRIPCION,T1.CANTEXISTENCIA,T2.DESCRIPCION ")
                .append(" ORDER BY T3.DESCRIPCION,T2.DESCRIPCION ASC ");
        return resultado.toString();
    }

    public void generarReporte() {
        try {
            byte[] reporte = null;

            if (this.busquedadSeleccionados != null) {
                this.busquedadSeleccionados.forEach(elemento -> {
                    filtroInventario.getListaIdsProductos().add(elemento.getId_producto());
                });
            }

            Map parametros = new HashMap<>();
            parametros.put("sql", retornarConsulta());
            reporte = servicioReporte.generarReporte(Reportes.REPORTE_EXISTENCIAS_INVENTARIO, TiposMimeTypes.PDF, parametros, false);

            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_EXISTENCIAS_INVENTARIO.getNombreReporte() + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    private List<SelectItem> obtenerSeleccionBodegas() {

        listaBodegasEncontradas = servicioBodega.obtenerListaBodegas();
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(0L, "Todas"));
        listaBodegasEncontradas.forEach((p) -> {
            lista.add(new SelectItem(p.getBodegaPK().getId_bodega(), p.getDescripcion()));
        });

        return lista;
    }

    private List<SelectItem> obtenerSeleccionProductos() {

        List<Producto> productos = servicioProducto.buscarTodosProductos();
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(-1, "------"));
        productos.forEach((p) -> {
            lista.add(new SelectItem(p.getId_producto(), p.getDescripcion()));
        });

        return lista;
    }

    private List<SelectItem> obtenerSeleccionEstado() {

        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(-1, "------"));
        lista.add(new SelectItem(1, "Activo"));
        lista.add(new SelectItem(0, "Inactivo"));
        return lista;
    }

    public void abrirLineaInventario(ActionEvent evento) {
        //Se selecciona la linea
        lineaSeleccionada = (InventarioGeneral) evento.getComponent().getAttributes().get("lineaInventario");

        //Se abre el modal
        JSFUtil.abrirModal("panelModal");
    }

    public void guardarLineaInventario(ActionEvent evento) {

        //Se obtiene la linea
        Inventario inventario = lineaSeleccionada.getInventario();

        //se trae el inventario de base de datos para comparar
        Inventario inventarioBD = servicioInventario.obtenerInventarioPorId(inventario.getIdInventario());

        //Valida que un producto que no tenga existencias se active
        if (inventarioBD.getActivo() == 0 & inventario.getActivo() == 1 & inventarioBD.getCantExistencia() == 0) {
            MensajeUtil.agregarMensajeError("No puede activar un producto que no posee existencias.");
            return;
        }

        //Valida si el motivo esta vacio
        if (inventario.getRazonInactivo() == null | (inventario.getRazonInactivo() != null && inventario.getRazonInactivo().trim().isEmpty())) {
            MensajeUtil.agregarMensajeError("Debe agregar un motivo.");
            return;
        }

        //Se actualiza el inventario
        servicioInventario.actualizar(inventario);
        //Se actualiza la cantidad minima del producto
        //servicioProducto.actualizar(lineaSeleccionada.getProducto());

        //Se actualiza la linea visual
        listaGeneralInventario.forEach((p -> {
            if (p.getInventario().getIdInventario().longValue() == lineaSeleccionada.getInventario().getIdInventario().longValue()) {
                p.actualizarLinea(lineaSeleccionada);
            }
        }));

        //Limpia la linea
        lineaSeleccionada = new InventarioGeneral();

        //Se cierra el modal
        JSFUtil.cerrarModal("panelModal");
    }

    public void cerrarLineaInventario(ActionEvent evento) {

        //Limpia la linea
        lineaSeleccionada = new InventarioGeneral();

        //Se cierra el modal
        JSFUtil.cerrarModal("panelModal");
    }

    public void actualizarmotivo() {
        if (lineaSeleccionada != null) {
            lineaSeleccionada.setRazonTexto("");
        }
    }

    public List<Producto> busquedadProductos(String texto) {
        return servicioProducto.buscarProductosActivos(texto);
    }

    public void abrirModalEliminacion(ActionEvent evento) {

        //Se selecciona la linea
        lineaEliminacion = (InventarioGeneral) evento.getComponent().getAttributes().get("lineaInventarioEliminacion");

        if (lineaEliminacion == null) {
            MensajeUtil.agregarMensajeError("Error al seleccionar la linea de inventario.");
            return;
        } else if (lineaEliminacion.getInventario().getCantExistencia() <= 0l) {
            MensajeUtil.agregarMensajeError("No existe cantidad suficiente para eliminar de existencias.");
            return;
        }

        //Se abre el modal
        JSFUtil.abrirModal("panelEliminacion");
    }

    public void guardarEliminacion(ActionEvent evento) {

        // Se realiza una validacion previa
        if (lineaEliminacion == null) {
            MensajeUtil.agregarMensajeError("Error al seleccionar la linea de inventario.");
            return;
        } else if (cantidadExistencia == null | (cantidadExistencia != null && cantidadExistencia < 0)) {
            MensajeUtil.agregarMensajeError("El campo de cantidad no debe ser vac�o, ni menor a cero.");
            return;
        } else if (cantidadExistencia > lineaEliminacion.getInventario().getCantExistencia()) {
            MensajeUtil.agregarMensajeError("El producto no posee suficiente existencia para eliminar la cantidad solicitada.");
            return;
        } else if (motivo == null | (motivo != null && motivo.isEmpty())) {
            MensajeUtil.agregarMensajeError("El campo del Motivo no debe ser vac�o.");
            return;
        }

        //Se resta de la existencia de inventario
        Inventario inventario = servicioInventario.eliminarExistencia(lineaEliminacion, usuarioActual.getLogin(), cantidadExistencia, motivo);

        //Se actualiza la linea
        lineaEliminacion = new InventarioGeneral(inventario, lineaEliminacion.getBodega(), lineaEliminacion.getProducto());

        //Se actualiza la linea visual
        listaGeneralInventario.forEach((p -> {
            if (p.getInventario().getIdInventario().longValue() == lineaEliminacion.getInventario().getIdInventario().longValue()) {
                p.actualizarLinea(lineaEliminacion);
            }
        }));

        //Limpian las variables
        motivo = "";
        cantidadExistencia = null;
        lineaEliminacion = null;

        //Se cierra el modal
        JSFUtil.cerrarModal("panelEliminacion");
    }

    public void cerrarEliminacion(ActionEvent evento) {

        //Limpian las variables
        motivo = "";
        cantidadExistencia = 0l;
        lineaEliminacion = null;

        //Se cierra el modal
        JSFUtil.cerrarModal("panelEliminacion");
    }

}
