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
@EqualsAndHashCode(of = {"idInventario"}, callSuper = false)
@Entity
@Table(name = "inventario", schema = "searmedica")
@SequenceGenerator(name = "seq_inventario", sequenceName = "searmedica.seq_inventario", allocationSize = 1)
public class Inventario implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inventario")
    @Column(name = "id_inventario")
    private Long idInventario;
    
    @Column(name = "id_bodega")
    private Long idBodega;
    
    @Column(name = "id_producto")
    private Long idProducto;
    
    @Column(name = "cantexistencia")
    private Long cantExistencia;
    
    @Column(name = "fechavencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    
    @Column(name = "numerolote")
    private String numeroLote;
    
    @Column(name = "ub_piso")
    private String ubPiso;
    
    @Column(name = "ub_cuadrante")
    private String ubCuadrante;
    
    @Column(name = "ub_estante")
    private String ubEstante;
    
    @Column(name = "ub_fila")
    private String ubFila;
    
    @Column(name = "activo")
    private Long activo;
    
    @Column(name = "razon_inactivo")
    private String razonInactivo;
       

    public Inventario() {
    }


    /**
     * Constructor con parametros obligatorios 
     * @param idBodega
     * @param idProducto
     * @param cantExistencia 
     */
    public Inventario(long idBodega, long idProducto, long cantExistencia) {
        this.idBodega = idBodega;
        this.idProducto = idProducto;
        this.cantExistencia = cantExistencia;
    }

    /*
     * Constructor con parametros obligatorios y adicionales
     * 
     */
    public Inventario(long idBodega, long idProducto, long cantExistencia, Date fechaVencimiento, String numeroLote, String ubPiso, String ubCuadrante, String ubEstante, String ubFila,Long activo,String razonInactivo) {
        this.idBodega = idBodega;
        this.idProducto = idProducto;
        this.cantExistencia = cantExistencia;
        this.fechaVencimiento = fechaVencimiento;
        this.numeroLote = numeroLote;
        this.ubPiso = ubPiso;
        this.ubCuadrante = ubCuadrante;
        this.ubEstante = ubEstante;
        this.ubFila = ubFila;
        this.activo = activo;
        this.razonInactivo = razonInactivo;
    }

    /**
     * Constructor solo para utilizar la entidad como consulta
     * @param idBodega
     * @param idProducto
     * @param fechaVencimiento
     * @param numeroLote 
     */
    public Inventario(long idBodega, long idProducto, Date fechaVencimiento, String numeroLote) {
        this.idBodega = idBodega;
        this.idProducto = idProducto;
        this.fechaVencimiento = fechaVencimiento;
        this.numeroLote = numeroLote;
    }

    
    

}
