/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
@EqualsAndHashCode(of = {"id_factura_trazabilidad"}, callSuper = false)
@Entity
@Table(name = "factura_trazabilidad", schema = "searmedica")
@SequenceGenerator(name = "seq_trazabilidad", sequenceName = "searmedica.seq_trazabilidad", allocationSize = 1)
public class FacturaTrazabilidad implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_trazabilidad")
    @Column(name = "id_factura_trazabilidad")
    private Long id_factura_trazabilidad;
    
    @Column(name = "id_factura")
    private Long id_factura;
    
    @Column(name = "id_cliente")
    private Long id_cliente;
    
    @Column(name = "id_cond_venta")
    private Long id_cond_venta;
    
    @Column(name = "id_medio_pago")
    private Long id_medio_pago;
    
    @Column(name = "id_anulacion")
    private Long id_anulacion;
    
    @Column(name = "fecha_factura")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_factura;
    
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_creacion;
    
    @Column(name = "fecha_factura_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_factura_actualizacion;
    
    @Column(name = "total_descuentos")
    private BigDecimal total_descuentos;
    
    @Column(name = "total_impuestos")
    private BigDecimal total_impuestos;
    
    @Column(name = "total_venta_neta")
    private BigDecimal total_venta_neta;
    
    @Column(name = "total_venta")
    private BigDecimal total_venta;
    
    @Column(name = "total_servicios_grabados")
    private BigDecimal total_servicios_grabados;
    
    @Column(name = "total_servicios_exentos")
    private BigDecimal total_servicios_exentos;
    
    @Column(name = "total_mercancias_gravadas")
    private BigDecimal total_mercancias_gravadas;
    
    @Column(name = "total_mercancias_exentas")
    private BigDecimal total_mercancias_exentas;
    
    @Column(name = "total_gravado")
    private BigDecimal total_gravado;
    
    @Column(name = "total_exento")
    private BigDecimal total_exento;
    
    @Column(name = "total_comprobante")
    private BigDecimal total_comprobante;
    
    @Column(name = "clave")
    private String clave;
    
    @Column(name = "numero_consecutivo")
    private String numero_consecutivo;
    
    @Column(name = "agente")
    private String agente;
    
    @Column(name = "descuento_aplicado")
    private Integer descuento_aplicado;
    
    @Column(name = "estado_factura")
    private Integer estado_factura;
    
    @Column(name = "fecha_emision")
    private String fecha_emision;
    
    @Column(name = "login")
    private String login;
    
    @Column(name = "plazo_credito")
    private String plazo_credito;
    
    @Column(name = "correo_electronico")
    private String correo_electronico;
    
    @Column(name = "envio_correo_electronico")
    private Integer envio_correo_electronico;
    
    @Column(name = "envio_respuesta_hacienda")
    private Integer envio_respuesta_hacienda;
    
    @Column(name = "nombre_cliente_fantasia")
    private String nombre_cliente_fantasia;
    
    @Column(name = "id_tipo_factura")
    private Long id_tipo_factura;
    
    @Column(name = "ip")
    private String ip;
    
    @Column(name = "nombre_estacion")
    private String nombre_estacion;
    
    @Column(name = "ip_actualizacion")
    private String ip_actualizacion;
    
    @Column(name = "nombre_estacion_actualizacion")
    private String nombre_estacion_actualizacion;
    
    @Column(name = "id_bodega")
    private Long id_bodega;
    
    @Column(name = "id_bodega_actualiza")
    private Long id_bodega_actualiza;
    
    @Column(name = "codigo_situacion_comprobante")
    private String codigo_situacion_comprobante;
    
    @Column(name = "codigo_referencia")
    private String codigo_referencia;
    
    @Column(name = "codigo_documento_referencia")
    private String codigo_documento_referencia;
    
    @Column(name = "fecha_documento_referencia")
    private String fecha_documento_referencia;
    
    @Column(name = "razon_documento_referencia")
    private String razon_documento_referencia;
    
    @Column(name = "numero_factura_documento_referencia")
    private String numero_factura_documento_referencia;
    
    @Column(name = "factura_cancelada")
    private Integer factura_cancelada;
    
    @Column(name = "fecha_cancelacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_cancelacion;
    
    @Column(name = "usuario_cancela")
    private String usuario_cancela;
    
    @Column(name = "cant_dias_vigencia")
    private Integer cant_dias_vigencia;
    
    @Column(name = "monto_restante")
    private BigDecimal monto_restante;
    
    @Column(name = "total_servicios_exonerados")
    private BigDecimal total_servicios_exonerados;
    
    @Column(name = "total_mercancias_exonerados")
    private BigDecimal total_mercancias_exonerados;
    
    @Column(name = "total_exonerado")
    private BigDecimal total_exonerado;
    
    @Column(name = "es_factura_sin_receptor")
    private Integer es_factura_sin_receptor;
    
    @Column(name = "id_factura_referencia")
    private Long id_factura_referencia;
    
    @Column(name = "tipo_operacion")
    private String tipo_operacion;
    
    @Column(name = "correo_electronico_cliente")
    private String correo_electronico_cliente;
    
    @Transient
    private String fechaVencimiento;
    
    @Transient
    private String descripcionEstado;
    @Transient
    private Estados estadoFactura;

    /**
     *
     * @param factura
     * @return FacturaTrazabilidad
     */
    public static FacturaTrazabilidad convertirFacturaATrazabilidad(Factura factura) {
        FacturaTrazabilidad resultado = new FacturaTrazabilidad();
        
        resultado.setId_factura(factura.getId_factura());
        resultado.setId_cliente(factura.getId_cliente());
        resultado.setId_cond_venta(factura.getId_cond_venta());
        resultado.setId_medio_pago(factura.getId_medio_pago());
        resultado.setId_anulacion(factura.getId_anulacion());
        resultado.setFecha_factura(factura.getFecha_factura());
        resultado.setFecha_factura_actualizacion(factura.getFecha_factura_actualizacion());
        resultado.setTotal_descuentos(factura.getTotal_descuentos());
        resultado.setTotal_impuestos(factura.getTotal_impuestos());
        resultado.setTotal_venta_neta(factura.getTotal_venta_neta());
        resultado.setTotal_venta(factura.getTotal_venta());
        resultado.setTotal_servicios_grabados(factura.getTotal_servicios_grabados());
        resultado.setTotal_servicios_exentos(factura.getTotal_servicios_exentos());
        resultado.setTotal_mercancias_gravadas(factura.getTotal_mercancias_gravadas());
        resultado.setTotal_mercancias_exentas(factura.getTotal_mercancias_exentas());
        resultado.setTotal_gravado(factura.getTotal_gravado());
        resultado.setTotal_exento(factura.getTotal_exento());
        resultado.setTotal_comprobante(factura.getTotal_comprobante());
        resultado.setClave(factura.getClave());
        resultado.setNumero_consecutivo(factura.getNumero_consecutivo());
        resultado.setAgente(factura.getAgente());
        resultado.setDescuento_aplicado(factura.getDescuento_aplicado());
        resultado.setEstado_factura(factura.getEstado_factura());
        resultado.setFecha_emision(factura.getFecha_emision());
        resultado.setLogin(factura.getLogin());
        resultado.setPlazo_credito(factura.getPlazo_credito());
        resultado.setCorreo_electronico(factura.getCorreo_electronico());
        resultado.setEnvio_correo_electronico(factura.getEnvio_correo_electronico());
        resultado.setEnvio_respuesta_hacienda(factura.getEnvio_respuesta_hacienda());
        resultado.setNombre_cliente_fantasia(factura.getNombre_cliente_fantasia());
        resultado.setId_tipo_factura(factura.getId_tipo_factura());
        resultado.setIp(factura.getIp());
        resultado.setNombre_estacion(factura.getNombre_estacion());
        resultado.setIp_actualizacion(factura.getIp_actualizacion());
        resultado.setNombre_estacion_actualizacion(factura.getNombre_estacion_actualizacion());
        resultado.setId_bodega(factura.getId_bodega());
        resultado.setId_bodega_actualiza(factura.getId_bodega_actualiza());
        resultado.setCodigo_situacion_comprobante(factura.getCodigo_situacion_comprobante());
        resultado.setCodigo_referencia(factura.getCodigo_referencia());
        resultado.setCodigo_documento_referencia(factura.getCodigo_documento_referencia());
        resultado.setFecha_documento_referencia(factura.getFecha_documento_referencia());
        resultado.setRazon_documento_referencia(factura.getRazon_documento_referencia());
        resultado.setNumero_factura_documento_referencia(factura.getNumero_factura_documento_referencia());
        resultado.setFactura_cancelada(factura.getFactura_cancelada());
        resultado.setFecha_cancelacion(factura.getFecha_cancelacion());
        resultado.setUsuario_cancela(factura.getUsuario_cancela());
        resultado.setCant_dias_vigencia(factura.getCant_dias_vigencia());
        resultado.setMonto_restante(factura.getMonto_restante());
        resultado.setTotal_servicios_exonerados(factura.getTotal_servicios_exonerados());
        resultado.setTotal_mercancias_exonerados(factura.getTotal_mercancias_exonerados());
        resultado.setTotal_exonerado(factura.getTotal_exonerado());
        resultado.setEs_factura_sin_receptor(factura.getEs_factura_sin_receptor());
        resultado.setId_factura_referencia(factura.getId_factura_referencia());
        resultado.setCorreo_electronico_cliente(factura.getCorreo_electronico_cliente());
        
        return resultado;
    }
}
