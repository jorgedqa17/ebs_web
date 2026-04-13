/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.ProductoPrecio;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class ModeloTipoPrecio {

    @Getter
    @Setter
    private ProductoPrecio productoPrecio;
    @Getter
    @Setter
    private Long id_tipo;
    @Getter
    @Setter
    private Long id_moneda;
    @Getter
    @Setter
    private String descripcion;
    @Getter
    @Setter
    private BigDecimal precio;
    @Getter
    @Setter
    private String descripcionTipoMoneda;
    @Getter
    @Setter
    private String simbolo;
    @Getter
    @Setter
    private BigDecimal tipo_cambio;
    @Setter
    @Getter
    private BigDecimal precioConImpuesto;
    @Getter
    @Setter
    private BigDecimal costoImpuesto;

    public ModeloTipoPrecio(Long id_tipo, String descripcion, BigDecimal precio) {
        this.id_tipo = id_tipo;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public ModeloTipoPrecio(Long id_tipo, Long id_moneda, String descripcion, BigDecimal precio, String descripcionTipoMoneda, String simbolo, BigDecimal tipo_cambio) {
        this.id_tipo = id_tipo;
        this.id_moneda = id_moneda;
        this.descripcion = descripcion;
        this.precio = precio;
        this.descripcionTipoMoneda = descripcionTipoMoneda;
        this.simbolo = simbolo;
        this.tipo_cambio = tipo_cambio;
    }

    public ModeloTipoPrecio(Long id_tipo, Long id_moneda, String descripcion, BigDecimal precio, String descripcionTipoMoneda, String simbolo, BigDecimal tipo_cambio, BigDecimal costoImpuesto) {
        this.id_tipo = id_tipo;
        this.id_moneda = id_moneda;
        this.descripcion = descripcion;
        this.precio = precio;
        this.descripcionTipoMoneda = descripcionTipoMoneda;
        this.simbolo = simbolo;
        this.tipo_cambio = tipo_cambio;
        this.costoImpuesto = costoImpuesto;
    }

    public ModeloTipoPrecio() {
    }

}
