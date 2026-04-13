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
@EqualsAndHashCode(of = {"id_motivo_nota_debito"}, callSuper = false)
@Entity
@Table(name = "motivo_nota_debito", schema = "searmedica")
public class MotivoDebito implements Serializable {

    @Id
    @Column(name = "id_motivo_nota_debito")
    private Long id_motivo_nota_debito;

    @Column(name = "descripcion")
    private String descripcion;

}
