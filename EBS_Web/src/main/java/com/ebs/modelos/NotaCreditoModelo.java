/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.AnulacionFactura;
import com.ebs.entidades.Factura;
import com.ebs.entidades.FacturaDebito;
import lombok.Data;

/**
 *
 * @author jdquesad
 */
@Data
public class NotaCreditoModelo {

    private FacturaDebito notaDebito;
    private Factura factura;
    private AnulacionFactura notaCredito;
}
