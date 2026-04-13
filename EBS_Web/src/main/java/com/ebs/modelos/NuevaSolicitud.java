/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Bodega;
import com.ebs.entidades.BodegaPK;
import com.ebs.entidades.Producto;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author
 */
public class NuevaSolicitud {
    @Getter
    @Setter
    private Long identificador;
    @Getter
    @Setter
    private Bodega bodegaDestino;
    @Getter
    @Setter
    private Bodega bodegaOrigen;
    @Getter
    @Setter    
    private Long cantidad;
    @Getter
    @Setter
    private Producto producto;
    
    private final List<Bodega> bodegas;
    @Getter
    @Setter
    private List<SelectItem> listaBodegas;
    
     
      

    public NuevaSolicitud(Long identificador,List<Bodega>listaBodegas,Bodega bodegaOrigen, Bodega bodegaDestino,Producto producto, Long cantidad) {
        this.bodegas = listaBodegas;
        this.listaBodegas = obtenerSeleccionBodegas(bodegas);
        this.identificador = identificador;
        this.bodegaOrigen = bodegaOrigen;
        this.bodegaDestino = bodegaDestino;
        this.producto = producto;
        this.cantidad = cantidad;
    }
    
    private List<SelectItem> obtenerSeleccionBodegas(List<Bodega> bodegas) {
        List<SelectItem> lista = new ArrayList<>();
        bodegas.forEach((p) -> {
            lista.add(new SelectItem(p.getBodegaPK().getId_bodega(), p.getDescripcion()));
        });

        return lista;
    }
    
    public Long getValorBodega(){
        return bodegaDestino.getBodegaPK().getId_bodega();
    }
    
    public void setValorBodega(Long idBodega){
        for(Bodega bodega : bodegas){
            if(bodega.getBodegaPK().getId_bodega().longValue() == idBodega){
                bodegaDestino = bodega;
                break;
            }
        }
    }
   
}
