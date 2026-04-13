/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author jdquesad
 */
@Data
public class ReciboCierreModelo {

    private Long id_recibo;

    private Long id_cliente;

    private Long id_factura;

    private Date fecha;

    private BigDecimal monto_pago;

    private BigDecimal monto_total_factura;

    private BigDecimal monto_restante;

    private String login;

    private String concepto_recibo;

    private Long id_estado;

    private Long id_medio_pago;

    private String numero_referencia;

    private Integer envio_correo_electronico;

    private Integer es_recibo_manual;

    private String numero_recibo_manual;

    private Integer ind_tomar_cierre;

    private String esUnReciboManual;
    private String estadoRecibo;
    private String numeroRecibo;
    private String medioPago;
    private String numeroConsecutivoFactura;
    private String nombreCliente;
}
