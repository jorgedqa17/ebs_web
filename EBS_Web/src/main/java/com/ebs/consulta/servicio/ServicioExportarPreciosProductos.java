package com.ebs.consulta.servicio;

import com.powersystem.productos.servicios.ServicioProducto;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioExportarPreciosProductos extends com.powersystem.util.ServicioBase {

    private static final long serialVersionUID = 1L;

    @Inject
    private ServicioProducto servicioProducto;

    /**
     * @return List<Object[]> con columnas [descripcion, precio]
     */
    public List<Object[]> obtenerPreciosProductosActivos(Long idTipoPrecio) {
        return servicioProducto.listarProductosActivosConPrecio(idTipoPrecio);
    }
}
