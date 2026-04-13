/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Factura;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class ConsultaFacturasModelo {

    @Getter
    @Setter
    private Long idFactura;
    @Getter
    @Setter
    private String usuario;
    @Getter
    @Setter
    private String identificacion;
    @Getter
    @Setter
    private String numeroConsecutivo;
    @Getter
    @Setter
    private String descripcionTipoFactura;
    @Getter
    @Setter
    private String estadoFactura;
    @Getter
    @Setter
    private BigDecimal montoTotalComprobante;
    @Getter
    @Setter
    private String nombreClienteFantasia;
    @Getter
    @Setter
    private String nombreCliente;
    @Getter
    @Setter
    private String descripcionMedioPago;
    @Getter
    @Setter
    private String descripcionCondicionVenta;
    @Getter
    @Setter
    private String plazoCredito;
    @Getter
    @Setter
    private String motivoAnulacion;
    @Getter
    @Setter
    private String descripcionBodega;
    @Getter
    @Setter
    private Factura factura;

    public String getEstadoFacturaCredito() {
        String resultado = "";

        if (factura.getFactura_cancelada().equals(1)) {
            resultado = "Cancelada";
        } else {
            resultado = "Pendiente";
        }
        return resultado;
    }

}
