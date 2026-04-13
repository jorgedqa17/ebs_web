/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos.correos;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author jorge
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaCorreoModelo {

    private Long id_factura;

    private Long id_cliente;

    private Long id_cond_venta;

    private Long id_medio_pago;

    private Long id_anulacion;

    private Date fecha_factura;

    private Date fecha_factura_actualizacion;

    private BigDecimal total_descuentos;

    private BigDecimal total_impuestos;

    private BigDecimal total_venta_neta;

    private BigDecimal total_venta;

    private BigDecimal total_servicios_grabados;

    private BigDecimal total_servicios_exentos;

    private BigDecimal total_mercancias_gravadas;

    private BigDecimal total_mercancias_exentas;

    private BigDecimal total_gravado;

    private BigDecimal total_exento;

    private BigDecimal total_comprobante;

    private String clave;

    private String numero_consecutivo;

    private String agente;

    private Integer descuento_aplicado;

    private Integer estado_factura;

    private String fecha_emision;

    private String login;

    private String plazo_credito;

    private String correo_electronico;

    private String correo_electronico_cliente;

    private Integer envio_correo_electronico;

    private Integer envio_respuesta_hacienda;

    private String nombre_cliente_fantasia;

    private Long id_tipo_factura;

    private String ip;

    private String nombre_estacion;

    private String ip_actualizacion;

    private String nombre_estacion_actualizacion;

    private Long id_bodega;

    private Long id_bodega_actualiza;

    private String codigo_situacion_comprobante;

    private String codigo_documento_referencia;

    private String fecha_documento_referencia;

    private String razon_documento_referencia;

    private String numero_factura_documento_referencia;

    private Integer factura_cancelada;

    private Date fecha_cancelacion;

    private String usuario_cancela;
}
