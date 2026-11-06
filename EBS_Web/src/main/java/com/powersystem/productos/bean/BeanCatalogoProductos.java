package com.powersystem.productos.bean;

import com.ebs.modelos.ModeloListaProductos;
import com.ebs.modelos.ModeloTipoPrecio;
import com.ebs.modelos.ProductoExistencia;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.ImagenesProducto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 * Catálogo de productos: consulta rápida de solo lectura. La lista es una tabla con lazy loading
 * (igual patrón que {@link ProductoLazyModel} ya usa en mantenimiento, listaProducto.xhtml) y no carga
 * fotos/precios/existencia por fila — eso solo se consulta una vez, a demanda, al pedir el detalle de
 * un producto puntual ({@link #verDetalle(ModeloListaProductos)}).
 */
@ManagedBean
@ViewScoped
public class BeanCatalogoProductos {

    @Inject
    private ServicioProducto servicioProducto;

    @Getter
    private ProductoLazyModel modeloProductos;

    @Getter
    @Setter
    private String codigoBarras;

    @Getter
    private ModeloListaProductos productoDetalle;
    @Getter
    private List<ModeloTipoPrecio> listaPreciosDetalle = new ArrayList<>();
    @Getter
    private List<ProductoExistencia> listaExistenciaDetalle = new ArrayList<>();
    @Getter
    private List<String> listaImagenesDetalle = new ArrayList<>();

    @PostConstruct
    public void inicializar() {
        modeloProductos = new ProductoLazyModel(servicioProducto, null);
    }

    /** Llamado desde el detalle de un producto (f:viewParam codigo): filtra la tabla a ese producto y abre su detalle solo. */
    public void buscarPorCodigoBarras() {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            return;
        }
        Map<String, String> filtroFijo = new HashMap<>();
        filtroFijo.put("codigo_barras", codigoBarras.trim());
        modeloProductos = new ProductoLazyModel(servicioProducto, filtroFijo);

        List<ModeloListaProductos> resultado = servicioProducto.listarProductosLista(0, 1, filtroFijo);
        if (!resultado.isEmpty()) {
            verDetalle(resultado.get(0));
        }
    }

    /** Carga, a demanda, el detalle completo de un producto: precios, existencia por bodega e imágenes. */
    public void verDetalle(ModeloListaProductos producto) {
        this.productoDetalle = producto;
        Long idProducto = producto.getProducto().getId_producto();
        this.listaPreciosDetalle = servicioProducto.obtenerPreciosParaLista(idProducto);
        this.listaExistenciaDetalle = servicioProducto.obtenerExistenciaProducto(idProducto);
        this.listaImagenesDetalle = ImagenesProducto.listarNombres(producto.getProducto().getRuta_imagen());
    }
}
