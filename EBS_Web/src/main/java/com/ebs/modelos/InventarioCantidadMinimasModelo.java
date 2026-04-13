/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jdquesad
 */
public class InventarioCantidadMinimasModelo {

    @Getter
    @Setter
    private Long idProducto;

    @Getter
    @Setter
    private String descripcion;

    @Getter
    @Setter
    private Long cantidadExistenciaActual;
    @Getter
    @Setter
    private Long cantidadMinima;
}
