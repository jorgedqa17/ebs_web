/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class SalidasCierreModelo {

    @Getter
    @Setter
    private String motivoSalida;
    @Getter
    @Setter
    private Integer consecutivo;
    @Getter
    @Setter
    private BigDecimal monto;
}
