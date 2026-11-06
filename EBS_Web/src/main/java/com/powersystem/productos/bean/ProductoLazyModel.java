package com.powersystem.productos.bean;

import com.ebs.modelos.ModeloListaProductos;
import com.powersystem.productos.servicios.ServicioProducto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Lista de productos paginada y filtrada en la base de datos. Solo conserva la página actual en
 * memoria, en lugar de todos los productos con todos sus precios en cada sesión.
 */
public class ProductoLazyModel extends LazyDataModel<ModeloListaProductos> {

    private static final long serialVersionUID = 1L;

    private final ServicioProducto servicioProducto;
    private final Map<String, String> filtrosFijos;
    private List<ModeloListaProductos> paginaActual;
    private String ultimaClaveFiltros;
    private int ultimoConteo = -1;

    public ProductoLazyModel(ServicioProducto servicioProducto) {
        this(servicioProducto, null);
    }

    /** @param filtrosFijos filtros adicionales que no vienen de las columnas del dataTable (p. ej. llegar ya filtrado a un producto puntual desde otra pantalla). */
    public ProductoLazyModel(ServicioProducto servicioProducto, Map<String, String> filtrosFijos) {
        this.servicioProducto = servicioProducto;
        this.filtrosFijos = filtrosFijos;
    }

    @Override
    public List<ModeloListaProductos> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Map<String, String> filtros = new HashMap<>();
        if (filtrosFijos != null) {
            filtros.putAll(filtrosFijos);
        }
        if (filters != null) {
            for (Map.Entry<String, Object> f : filters.entrySet()) {
                if (f.getValue() != null) {
                    filtros.put(f.getKey(), f.getValue().toString());
                }
            }
        }
        // El total solo cambia si cambian los filtros: al pedir los bloques siguientes (first > 0) se
        // reutiliza en vez de volver a contar en la BD por cada bloque.
        String claveFiltros = new java.util.TreeMap<>(filtros).toString();
        if (first == 0 || ultimoConteo < 0 || !claveFiltros.equals(ultimaClaveFiltros)) {
            ultimoConteo = (int) servicioProducto.contarProductosLista(filtros);
            ultimaClaveFiltros = claveFiltros;
        }
        setRowCount(ultimoConteo);
        paginaActual = servicioProducto.listarProductosLista(first, pageSize, filtros);
        return paginaActual;
    }

    @Override
    public Object getRowKey(ModeloListaProductos producto) {
        return producto.getProducto().getId_producto();
    }

    @Override
    public ModeloListaProductos getRowData(String rowKey) {
        if (paginaActual != null) {
            for (ModeloListaProductos p : paginaActual) {
                if (String.valueOf(p.getProducto().getId_producto()).equals(rowKey)) {
                    return p;
                }
            }
        }
        return null;
    }
}
