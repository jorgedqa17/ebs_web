/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
public class FacturaCredito {

    @Getter
    @Setter
    private Long idFactura;
    @Getter
    @Setter
    private Long idTipoFactura;
    @Getter
    @Setter
    private String numeroConsecutivo;
    @Getter
    @Setter
    private String clave;
    @Getter
    @Setter
    private Long idCliente;
    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private BigDecimal total_comprobante;
    @Getter
    @Setter
    private BigDecimal monto_restante;
    @Getter
    @Setter
    private String estadoFactura;
    @Getter
    @Setter
    private Integer estado_credito;
    @Getter
    @Setter
    private String estado_credito_descripcion;
    @Getter
    @Setter
    private String fechaFactura;
    @Getter
    @Setter
    private String plazoCredito;
    @Getter
    @Setter
    private String fechaVencimiento;
    @Getter
    @Setter
    private Integer diasAtraso;

    public FacturaCredito() {
    }

}
