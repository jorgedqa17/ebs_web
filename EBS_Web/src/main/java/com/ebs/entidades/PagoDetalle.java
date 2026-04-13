/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import javax.persistence.Transient;
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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
@Entity
@Table(name = "pago_detalle", schema = "searmedica")
public class PagoDetalle implements Serializable {

    @Column(name = "id_pago")
    private Long id_pago;

    @Column(name = "numero_linea")
    @Id
    private String numero_linea;

    @Column(name = "detalle_producto")
    private String detalle_producto;

    @Column(name = "monto_sin_impuesto")
    private BigDecimal monto_sin_impuesto;

    @Column(name = "monto_impuesto")
    private BigDecimal monto_impuesto;

    @Column(name = "monto_con_impuesto")
    private BigDecimal monto_con_impuesto;

    @Column(name = "id_tipo_tarifa_impuesto")
    private Long id_tipo_tarifa_impuesto;

    @Column(name = "es_linea_sin_impuesto")
    private Integer es_linea_sin_impuesto;

    public PagoDetalle(String numero_linea, String detalle_producto, BigDecimal monto_sin_impuesto, BigDecimal monto_impuesto, BigDecimal monto_con_impuesto, Long id_tipo_tarifa_impuesto, Integer es_linea_sin_impuesto) {
        this.numero_linea = numero_linea;
        this.detalle_producto = detalle_producto;
        this.monto_sin_impuesto = monto_sin_impuesto;
        this.monto_impuesto = monto_impuesto;
        this.monto_con_impuesto = monto_con_impuesto;
        this.id_tipo_tarifa_impuesto = id_tipo_tarifa_impuesto;
        this.es_linea_sin_impuesto = es_linea_sin_impuesto;
    }
    
    
    
}
