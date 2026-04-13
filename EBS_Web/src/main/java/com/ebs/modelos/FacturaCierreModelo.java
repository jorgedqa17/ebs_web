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
public class FacturaCierreModelo {

    @Getter
    @Setter
    private String numeroConsecutivo;
    @Getter
    @Setter
    private String fechaFactura;
    @Getter
    @Setter
    private String condicionDescripcion;
    @Getter
    @Setter
    private String nombreCliente;
    @Getter
    @Setter
    private String nombreClienteFantasia;
    @Getter
    @Setter
    private BigDecimal totalComprobante;
    @Getter
    @Setter
    private Long idFactura;
    @Getter
    @Setter
    private Long idAnulacion;
    @Getter
    @Setter
    private Long idMedioPago;
    @Getter
    @Setter
    private Long idCondicionVenta;
    @Getter
    @Setter
    private String descMedioPago;
    @Getter
    @Setter
    private String motivoAnulacion;
    @Getter
    @Setter
    private String tipoMotivoAnulacion;
    @Getter
    @Setter
    private String descripcion;
    @Getter
    @Setter
    private Long idBanco;
    @Getter
    @Setter
    private String numeroReferencia;

}
