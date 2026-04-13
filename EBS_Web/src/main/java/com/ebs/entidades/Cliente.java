/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"id_cliente"}, callSuper = false)
@Entity
@Table(name = "cliente", schema = "searmedica")
@SequenceGenerator(name = "seq_cliente", sequenceName = "searmedica.seq_cliente", allocationSize = 1)
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cliente")
    @Column(name = "id_cliente")
    private Long id_cliente;

    @Column(name = "ind_requiere_transporte")
    private Integer ind_requiere_transporte;

    @Column(name = "id_tipo_cliente")
    private Long id_tipo_cliente;

    @Column(name = "numero_cedula")
    private String numero_cedula;

    public Cliente() {
    }

    public Cliente(Long id_cliente, Integer ind_requiere_transporte, Long id_tipo_cliente, String numero_cedula) {
        this.id_cliente = id_cliente;
        this.ind_requiere_transporte = ind_requiere_transporte;
        this.id_tipo_cliente = id_tipo_cliente;
        this.numero_cedula = numero_cedula;
    }

}
