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
@EqualsAndHashCode(of = {"id_motivo_anulacion"}, callSuper = false)
@Entity
@Table(name = "motivo_anulacion", schema = "searmedica")
public class MotivoAnulacion implements Serializable {

    @Id
    @Column(name = "id_motivo_anulacion")
    private Long id_motivo_anulacion;

    @Column(name = "descripcion")
    private String descripcion;

}
