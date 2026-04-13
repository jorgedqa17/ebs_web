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
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author jdquesad
 */
@Data
@EqualsAndHashCode(of = {"id_recibo"}, callSuper = false)
@Entity
@Table(name = "historico_anulacion_recibo", schema = "searmedica")
public class HistoricoRecibo implements Serializable {

    @Id
    @Column(name = "id_recibo")
    private Long id_recibo;

    @Column(name = "login")
    private String login;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
}
