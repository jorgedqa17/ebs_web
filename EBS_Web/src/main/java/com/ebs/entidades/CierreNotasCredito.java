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

/**
 *
 * @author Jorge GBSYS
 */
@Data
@EqualsAndHashCode(of = {"id_cierre"}, callSuper = false)
@Entity
@Table(name = "cierre_notas_credito", schema = "searmedica")
public class CierreNotasCredito implements Serializable {

    @Id
    @Column(name = "id_cierre")
    private Long id_cierre;

    @Id
    @Column(name = "id_anulacion")
    private Long id_anulacion;

}
