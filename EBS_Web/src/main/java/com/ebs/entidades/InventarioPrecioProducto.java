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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
@EqualsAndHashCode(of = {"id_precio_inventario"}, callSuper = false)
@Entity
@Table(name = "inventario_producto_precio", schema = "searmedica")
@SequenceGenerator(name = "seq_inventario_precio_producto", sequenceName = "searmedica.seq_inventario_precio_producto", allocationSize = 1)

public class InventarioPrecioProducto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inventario_precio_producto")
    @Column(name = "id_precio_inventario")
    private Long id_precio_inventario;

    @Column(name = "id_inventario")
    private Long id_inventario;

    @Column(name = "id_producto")
    private Long id_producto;

    @Column(name = "id_bodega")
    private Long id_bodega;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "login")
    private String login;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    public InventarioPrecioProducto(Long id_producto, BigDecimal precio, String login, Date fechaRegistro) {
        this.id_producto = id_producto;
        this.precio = precio;
        this.login = login;
        this.fechaRegistro = fechaRegistro;
    }

    public InventarioPrecioProducto() {
    }

}
