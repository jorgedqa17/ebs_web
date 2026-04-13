
package com.ebs.modelos;

import com.ebs.constantes.enums.EstadoSolicitud;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.InventarioSolicitud;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import lombok.Data;

/**
 *
 * @author
 */
@Data
public class EncabezadoSolicitud {
    
    private InventarioSolicitud inventarioSolicitud;
    private Bodega idBodegaOrigen;    
    private EstadoSolicitud estado;
    private String fechaTexto;

    public EncabezadoSolicitud(InventarioSolicitud inventarioSolicitud, Bodega idBodegaOrigen, EstadoSolicitud estado) {
        DateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        this.inventarioSolicitud = inventarioSolicitud;
        this.idBodegaOrigen = idBodegaOrigen;
        this.estado = estado;
        this.fechaTexto = formato.format(inventarioSolicitud.getFechaRegistro());
    }   
    
    
}
