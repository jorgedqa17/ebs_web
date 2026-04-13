/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author jdquesad
 */
@Data
@EqualsAndHashCode(of = {"usuarioComisionPK"}, callSuper = false)
@Entity
@Table(name = "usuario_comision", schema = "searmedica")
public class UsuarioComision implements Serializable {

    @EmbeddedId
    protected UsuarioComisionPK usuarioComisionPK;

    @Column(name = "porcentaje")
    private String porcentaje;

    @Column(name = "sobre_ventas")
    private Integer sobre_ventas;

    @Column(name = "sobre_recibos")
    private Integer sobre_recibos;
}
