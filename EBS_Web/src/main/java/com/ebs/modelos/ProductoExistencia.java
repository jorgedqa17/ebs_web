/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class ProductoExistencia {

    @Getter
    @Setter
    private Long id_producto;
    @Getter
    @Setter
    private Long id_bodega;
    @Getter
    @Setter
    private String descripcion_bodega;
    @Getter
    @Setter
    private Integer cantidad;
    @Getter
    @Setter
    private ProductoDetalleInventario productoDetalleInventario;

}
