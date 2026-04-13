/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.ProductoPrecio;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class ModeloTiposPrecios {

    @Getter
    @Setter
    private ProductoPrecio productoPrecio;
    @Getter
    @Setter
    private String descripcionTipoMoneda;
    @Getter
    @Setter
    private String descripcionTipoPrecio;
}
