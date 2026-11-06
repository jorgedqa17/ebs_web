package com.powersystem.productos.bean;

import com.ebs.constantes.enums.VistaCatalogo;
import com.ebs.entidades.Usuario;
import com.ebs.modelos.ModeloListaProductos;
import com.ebs.modelos.ModeloTipoPrecio;
import com.ebs.modelos.ProductoExistencia;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.seguridad.servicios.ServicioPermisos;
import com.powersystem.utilitario.ImagenesProducto;
import com.powersystem.utilitario.JSFUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 * Catálogo de productos: consulta rápida de solo lectura. La lista es una tabla con lazy loading
 * (igual patrón que {@link ProductoLazyModel} ya usa en mantenimiento, listaProducto.xhtml) y cada
 * página trae su miniatura, precio y existencia con pocas consultas ({@link CatalogoLazyModel}). El
 * detalle completo (todos los precios, existencia por bodega, fotos) se consulta a demanda.
 *
 * Qué ve cada usuario lo decide {@link VistaCatalogo}: Agente solo precio Por Mayor, Tienda solo precio
 * General, y ambos solo Disponible/Agotado. El filtro se aplica aquí, al cargar los datos, no solo
 * ocultando columnas.
 */
@ManagedBean
@ViewScoped
public class BeanCatalogoProductos {

    @Inject
    private ServicioProducto servicioProducto;
    @Inject
    private ServicioPermisos servicioPermisos;

    @Getter
    private ProductoLazyModel modeloProductos;

    @Getter
    @Setter
    private String codigoBarras;

    @Getter
    private VistaCatalogo vista = VistaCatalogo.COMPLETA;

    @Getter
    private ModeloListaProductos productoDetalle;
    @Getter
    private List<ModeloTipoPrecio> listaPreciosDetalle = new ArrayList<>();
    @Getter
    private List<ProductoExistencia> listaExistenciaDetalle = new ArrayList<>();
    @Getter
    private List<String> listaImagenesDetalle = new ArrayList<>();
    /** Solo se usa en vistas restringidas, que no ven cantidades. */
    @Getter
    private boolean disponibleDetalle;

    /** Texto del buscador (descripción o código; cada palabra debe aparecer). */
    @Getter
    @Setter
    private String busqueda;

    /** Filtros fijos del modelo lazy; se modifican en sitio al buscar (el modelo guarda esta misma referencia). */
    private Map<String, String> filtrosFijos;

    @PostConstruct
    public void inicializar() {
        Usuario usuario = (Usuario) JSFUtil.obtenerDeSesion("Usuario");
        if (usuario != null) {
            vista = VistaCatalogo.deUsuario(servicioPermisos.obtenerPerfilesUsuario(usuario.getLogin()));
        }
        filtrosFijos = filtrosBase();
        modeloProductos = new CatalogoLazyModel(servicioProducto, filtrosFijos, vista);
    }

    /** Aplica el buscador: la lista se vuelve a cargar desde el inicio con el nuevo filtro. */
    public void buscar() {
        String texto = busqueda == null ? "" : busqueda.trim();
        if (texto.isEmpty()) {
            filtrosFijos.remove("busqueda");
        } else {
            filtrosFijos.put("busqueda", texto);
        }
    }

    /**
     * Abre el detalle del producto cuyo id llega como parámetro "idProducto" (p:remoteCommand). Se
     * vuelve a consultar con los mismos filtros de la vista, así nadie puede abrir un producto
     * inactivo cambiando el id a mano.
     */
    public void verDetalleSolicitado() {
        String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idProducto");
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        Map<String, String> filtro = filtrosBase();
        filtro.put("id_producto", id.trim());
        List<ModeloListaProductos> resultado = servicioProducto.listarProductosLista(0, 1, filtro);
        if (resultado.isEmpty()) {
            this.productoDetalle = null;
            return;
        }
        verDetalle(resultado.get(0));
    }

    /** El catálogo lista únicamente productos activos, para todos los perfiles. */
    private Map<String, String> filtrosBase() {
        Map<String, String> filtros = new HashMap<>();
        filtros.put("estado", "Activo");
        return filtros;
    }

    public boolean isVistaCompleta() {
        return !vista.isRestringida();
    }

    /** Precio que ve una vista restringida (Por Mayor el agente, General la tienda). */
    public BigDecimal precioVista(ModeloListaProductos p) {
        return vista == VistaCatalogo.AGENTE ? p.getPrecioPorMayor() : p.getPrecioGeneral();
    }

    public String monedaVista(ModeloListaProductos p) {
        return vista == VistaCatalogo.AGENTE ? p.getMonedaPorMayor() : p.getMonedaGeneral();
    }

    /** Llamado desde el detalle de un producto (f:viewParam codigo): filtra la tabla a ese producto y abre su detalle solo. */
    public void buscarPorCodigoBarras() {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            return;
        }
        filtrosFijos.put("codigo_barras", codigoBarras.trim());

        List<ModeloListaProductos> resultado = servicioProducto.listarProductosLista(0, 1, filtrosFijos);
        if (!resultado.isEmpty()) {
            verDetalle(resultado.get(0));
        }
    }

    /**
     * Carga, a demanda, el detalle de un producto. Vista completa: todos los precios, existencia por
     * bodega e imágenes. Vista restringida: solo su precio y si hay existencia (sin cantidades ni
     * bodegas, que ni siquiera se consultan).
     */
    public void verDetalle(ModeloListaProductos producto) {
        this.productoDetalle = producto;
        Long idProducto = producto.getProducto().getId_producto();
        List<ModeloTipoPrecio> precios = servicioProducto.obtenerPreciosParaLista(idProducto);
        if (vista.isRestringida()) {
            Long tipoPermitido = vista.getIdTipoPrecio();
            List<ModeloTipoPrecio> visibles = new ArrayList<>();
            for (ModeloTipoPrecio p : precios) {
                if (tipoPermitido.equals(p.getProductoPrecio().getProductoPrecioPK().getId_tipo())) {
                    visibles.add(p);
                }
            }
            precios = visibles;
            this.listaExistenciaDetalle = new ArrayList<>();
            this.disponibleDetalle = servicioProducto.hayExistencia(idProducto);
        } else {
            this.listaExistenciaDetalle = servicioProducto.obtenerExistenciaProducto(idProducto);
        }
        this.listaPreciosDetalle = precios;
        this.listaImagenesDetalle = ImagenesProducto.listarNombres(producto.getProducto().getRuta_imagen());
    }
}
