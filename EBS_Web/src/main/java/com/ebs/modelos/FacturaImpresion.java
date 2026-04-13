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
 * @author Jorge Quesada Arias
 */
@Data
public class FacturaImpresion {

    private Long id_factura;
    private String numero_consecutivo;
    private String clave;
    private String fecha_factura;
    private String mediopago;
    private String condicionventa;
    private Integer cantidad;
    private BigDecimal precio_neto;
    private BigDecimal descuento;
    private BigDecimal descuentolinea;
    private BigDecimal impuestoslinea;
    private BigDecimal totallinea;
    private BigDecimal subtotallinea;
    private String nombreproducto;
    private String codigo_cabys;
    private String codigo_barras;
    private Long id_cliente;
    private String numero_cedula;
    private String tipocedula;
    private String correo_electronico;
    private String correo_electronico_cliente;
    private String nombre;
    private BigDecimal total_comprobante;
    private Integer plazo_credito;
    private BigDecimal total_descuentos;
    private BigDecimal total_impuestos;
    private String descripcionlinea;
    private String nombre_cliente_fantasia;
    private String direccion;
    private String descripcion;
    private Long id_anulacion;
    private String motivo_anulacion;
    private String consecutivo_anulacion;
    private String clave_anulacion;
    private String estado_anulacion;
    private String tipomotivoanulacion;
    private String login;
    private String agente;
    private String telefono_1;
    private String telefono_2;
    private String direccionbodega;
    private String telefonounobodega;
    private String telefonodosbodega;
    private String nmbfant;
    private BigDecimal total_venta_neta;
    private BigDecimal total_descuentos_factura;
    private BigDecimal monto_total;
    private BigDecimal total_venta;
    private String montoLetras;

    public FacturaImpresion() {
    }

}
