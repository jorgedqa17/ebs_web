/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.InventarioSalida;
import com.ebs.entidades.InventarioSalidaDetalle;
import java.util.List;
import lombok.Data;

/**
 *
 * @author sergio
 */
@Data
public class SalidaMateriales {
    
    private InventarioSalida inventarioSalida;
    
    private List<InventarioSalidaDetalle> detalles;

    public SalidaMateriales(InventarioSalida inventarioSalida, List<InventarioSalidaDetalle> detalles) {
        this.inventarioSalida = inventarioSalida;
        this.detalles = detalles;
    }
}
