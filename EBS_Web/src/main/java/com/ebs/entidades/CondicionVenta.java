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
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id_cond_venta"}, callSuper = false)
@Entity
@Table(name = "condicion_venta", schema = "searmedica")
public class CondicionVenta implements Serializable {

    @Id
    @Column(name = "id_cond_venta")
    private Long id_cond_venta;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "codigo_hacienda")
    private String codigo_hacienda;

}
