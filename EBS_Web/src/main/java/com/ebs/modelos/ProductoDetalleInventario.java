/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class ProductoDetalleInventario {

    @Getter
    @Setter
    private String numero_lote;
    @Getter
    @Setter
    private Date fecha_caducidad;

}
