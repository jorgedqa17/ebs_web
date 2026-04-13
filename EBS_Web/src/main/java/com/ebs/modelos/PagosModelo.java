/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Jorge GBSYS
 */
@Data
public class PagosModelo {

    private String numeroConsecutivoReceptor;

    private String identificacionReceptor;

    private String tipoIdentificacionReceptor;

    private String correoElectronicoReceptor;

    private BigDecimal montoImpuestos;

    private BigDecimal montoTotal;

    private String numeroConsecutivo;

    private String fechaEmision;

    private Long tipoRespuesta;

    private String detalleMensaje;

    private Long id_actividad;

    private String codigoActividad;

    private String nombreEmpresa;

    private BigDecimal monto_total_impuesto_acreditar;
    private BigDecimal monto_total_gasto_aplicable;
    private Long id_condicion_impuesto;
    private byte[] archivo;

    private List<LineaPago> listaLineasPago;

}
