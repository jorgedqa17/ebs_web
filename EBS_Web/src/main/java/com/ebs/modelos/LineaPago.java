/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Jorge GBSYS
 */
@Data
public class LineaPago {

    private BigDecimal subTotal;
    private BigDecimal montoImpuesto;
    private BigDecimal montoTotalLinea;
    private Integer tarifa;
    private Long idTipoTarifa;
    private String numeroLinea;
    private String detalleProducto;
    private boolean sinImpuesto;
}
