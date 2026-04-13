/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author jdquesad
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "detalle_factura_nota_credito", schema = "searmedica")

public class DetalleFacturaNotaCredito implements Serializable {

    @Id
    @Column(name = "numero_linea")
    private Integer numero_linea;

    @Id
    @Column(name = "id_anulacion")
    private Long id_anulacion;

    @Id
    @Column(name = "id_producto")
    private Long id_producto;

    @Id
    @Column(name = "id_factura")
    private Long id_factura;

    public DetalleFacturaNotaCredito(Integer numero_linea, Long id_anulacion, Long id_producto, Long id_factura, DetalleFactura detalleFactura) {
        this.numero_linea = numero_linea;
        this.id_anulacion = id_anulacion;
        this.id_producto = id_producto;
        this.id_factura = id_factura;
        this.detalleFactura = detalleFactura;
    }

    @Transient
    private DetalleFactura detalleFactura;

}
