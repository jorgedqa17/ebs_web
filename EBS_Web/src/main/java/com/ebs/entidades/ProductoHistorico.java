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
 * @author Jorge Quesada Arias
 */
@Data
@EqualsAndHashCode(of = {"id_historico_producto"}, callSuper = false)
@Entity
@Table(name = "producto_historico", schema = "searmedica")
@SequenceGenerator(name = "seq_historico_producto", sequenceName = "searmedica.seq_historico_producto", allocationSize = 1)
public class ProductoHistorico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_historico_producto")
    @Column(name = "id_historico_producto")
    private Long id_historico_producto;

    @Column(name = "id_producto")
    private Long id_producto;

    @Column(name = "login")
    private String login;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
}
