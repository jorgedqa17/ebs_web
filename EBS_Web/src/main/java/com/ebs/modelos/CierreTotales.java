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
 * @author jdquesad
 */
@Data
public class CierreTotales {

    private String descripcion;
    private BigDecimal monto;
    private Long idMedioPago;
}
