/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.constantes.enums.TipoResultado;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author Jorge Quesada
 */
@Data
public class ProductoInventario {
    
    Long idProducto;
    Long idInventario;
    String numeroLote;
    Date fechaVencimiento;
    Long idBodega;
    TipoResultado respuesta;

    public ProductoInventario(Long idProducto, Long idInventario, String numeroLote, Date fechaVencimiento, Long idBodega, TipoResultado respuesta) {
        this.idProducto = idProducto;
        this.idInventario = idInventario;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.idBodega = idBodega;
        this.respuesta = respuesta;
    }
    
    
    
}
