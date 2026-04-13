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
 * @author jdquesad
 */
public class FacturaBusquedaModelo {

    @Getter
    @Setter
    private Long idFactura;
    @Getter
    @Setter
    private Long idEstado;
    @Getter
    @Setter
    private String nombreFantasia;
    @Getter
    @Setter
    private String fechaFactura;
    @Setter
    private String nombreCliente;
    @Getter
    @Setter
    private String tipoFactura;
    @Getter
    @Setter
    private String numeroConsecutivo;
    @Getter
    @Setter
    private BigDecimal monto;

    public String getNombreCliente() {
        String nombreDevolver = "";
        if (!nombreCliente.equals("")) {
            nombreDevolver = nombreCliente;
        } else {
            nombreDevolver = nombreFantasia;
        }
        return nombreDevolver;
    }

}
