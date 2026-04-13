/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
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
 * @author
 */
@Data
@EqualsAndHashCode(of = {"idElemento"}, callSuper = false)
@Entity
@Table(name = "inventario_solic_det_elementos", schema = "searmedica")
@SequenceGenerator(name = "seq_invent_solic_det_elementos", sequenceName = "searmedica.seq_invent_solic_det_elementos", allocationSize = 1)
public class InventarioSolicDetElementos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invent_solic_det_elementos")
    @Column(name = "id_elemento")
    private Long idElemento;

    @Column(name = "id_inventario_solicitud_detalle")
    private Long idInventarioSolicitudDetalle;

    @Column(name = "fechavencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "numerolote")
    private String numeroLote;

    @Column(name = "cantidad")
    private Long cantidad;

    public InventarioSolicDetElementos() {
    }

    public InventarioSolicDetElementos(Long idInventarioSolicitudDetalle, Date fechaVencimiento, String numeroLote, Long cantidad) {
        this.idInventarioSolicitudDetalle = idInventarioSolicitudDetalle;
        this.fechaVencimiento = fechaVencimiento;
        this.numeroLote = numeroLote;
        this.cantidad = cantidad;
    }
    
    
}
