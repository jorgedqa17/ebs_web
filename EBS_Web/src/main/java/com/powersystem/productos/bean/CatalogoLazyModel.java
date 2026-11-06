package com.powersystem.productos.bean;

import com.ebs.constantes.enums.TiposPrecios;
import com.ebs.constantes.enums.VistaCatalogo;
import com.ebs.modelos.ModeloListaProductos;
import com.powersystem.productos.servicios.ServicioProducto;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 * Lista lazy del catálogo: igual que {@link ProductoLazyModel} pero completa cada página con
 * precio(s), existencia total y primera imagen (2 consultas por página, no una por fila). Solo trae
 * los precios que la {@link VistaCatalogo} del usuario puede ver.
 */
public class CatalogoLazyModel extends ProductoLazyModel {

    private static final long serialVersionUID = 1L;

    private final ServicioProducto servicioProducto;
    private final VistaCatalogo vista;

    public CatalogoLazyModel(ServicioProducto servicioProducto, Map<String, String> filtrosFijos, VistaCatalogo vista) {
        super(servicioProducto, filtrosFijos);
        this.servicioProducto = servicioProducto;
        this.vista = vista;
    }

    @Override
    public List<ModeloListaProductos> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<ModeloListaProductos> pagina = super.load(first, pageSize, sortField, sortOrder, filters);
        Long tipoUnico = vista.getIdTipoPrecio();
        boolean general = tipoUnico == null || tipoUnico.equals(TiposPrecios.GENERAL.getTipoPrecio());
        boolean porMayor = tipoUnico == null || tipoUnico.equals(TiposPrecios.POR_MAYOR.getTipoPrecio());
        servicioProducto.completarDatosCatalogo(pagina, general, porMayor);
        return pagina;
    }
}
