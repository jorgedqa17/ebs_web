/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.InventarioIngreso;
import com.ebs.entidades.InventarioIngresoDetalle;
import java.util.List;
import lombok.Data;

/**
 *
 * @author 
 */
@Data
public class IngresoMateriales {
    
    private InventarioIngreso inventarioIngreso;
    
    private List<InventarioIngresoDetalle> detalles;

    public IngresoMateriales(InventarioIngreso inventarioIngreso, List<InventarioIngresoDetalle> detalles) {
        this.inventarioIngreso = inventarioIngreso;
        this.detalles = detalles;
    }    
    
    
}
