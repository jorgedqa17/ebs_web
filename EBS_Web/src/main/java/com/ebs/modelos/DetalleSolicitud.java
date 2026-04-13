/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.constantes.enums.EstadoSolicitudDetalle;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.InventarioSolicitud;
import com.ebs.entidades.InventarioSolicitudDetalle;
import com.ebs.entidades.Producto;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Data;

/**
 *
 * @author
 */
@Data
public class DetalleSolicitud {

    private InventarioSolicitud inventarioSolicitud;

    private Bodega bodegaDestino;

    private Producto producto;

    private EstadoSolicitudDetalle estadoSolicitudDetalle;

    private InventarioSolicitudDetalle inventarioSolicitudDetalle;

    private List<ElementosSolicitudDetalle> listaElementos;
    
    private int semaforo;

    public DetalleSolicitud(InventarioSolicitud inventarioSolicitud, Bodega bodegaDestino, Producto producto, EstadoSolicitudDetalle estadoSolicitudDetalle, InventarioSolicitudDetalle inventarioSolicitudDetalle) {
        this.inventarioSolicitud = inventarioSolicitud;
        this.bodegaDestino = bodegaDestino;
        this.producto = producto;
        this.estadoSolicitudDetalle = estadoSolicitudDetalle;
        this.inventarioSolicitudDetalle = inventarioSolicitudDetalle;
        this.listaElementos = new ArrayList<>();
        this.semaforo = 0;
    }
    
    public DetalleSolicitud(InventarioSolicitud inventarioSolicitud, Bodega bodegaDestino, Producto producto, EstadoSolicitudDetalle estadoSolicitudDetalle, InventarioSolicitudDetalle inventarioSolicitudDetalle,List<ElementosSolicitudDetalle> listaElementos) {
        this.inventarioSolicitud = inventarioSolicitud;
        this.bodegaDestino = bodegaDestino;
        this.producto = producto;
        this.estadoSolicitudDetalle = estadoSolicitudDetalle;
        this.inventarioSolicitudDetalle = inventarioSolicitudDetalle;
        this.listaElementos = listaElementos;
        this.semaforo = (listaElementos.size() > 0 ? 1 : 0);
    }

    public void actualizarElementos(List<ElementosSolicitudDetalle> lista) {
        this.listaElementos.clear();
        this.listaElementos.addAll(lista);
        this.semaforo = (lista.size() > 0 ? 1 : 0);
    }

    public boolean igual(DetalleSolicitud detalleSolicitud) {
        boolean resultado = false;
        if (this.inventarioSolicitudDetalle.getIdInventarioSolicitudDetalle().longValue()
                == detalleSolicitud.getInventarioSolicitudDetalle().getIdInventarioSolicitudDetalle().longValue()) {
            resultado = true;
        }
        return resultado;
    }

    public void borrarElemento(String identificador) {
        Iterator<ElementosSolicitudDetalle> iterator = this.listaElementos.iterator();
        while (iterator.hasNext()) {
            ElementosSolicitudDetalle dato = iterator.next();
            if (dato.getIdentificador().equals(identificador)) {
                iterator.remove();
                break;
            }
        }
        this.semaforo = (listaElementos.size() > 0 ? 1 : 0);
    }
    
    public boolean verificarCantidadExacta(){
        boolean resultado;
        Long valor = 0l;
        for(ElementosSolicitudDetalle elem : listaElementos){
            valor += elem.getCantidad();
        }
        resultado = (valor > 0 & valor <= inventarioSolicitudDetalle.getCantidad().longValue());
        return resultado;
    }

    public boolean isTieneElementos() {
        return this.listaElementos.isEmpty();
    }

}
