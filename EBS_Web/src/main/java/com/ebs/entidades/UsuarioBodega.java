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
 * @author jdquesad
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "usuario_bodega", schema = "searmedica")
public class UsuarioBodega implements Serializable {

    @Id
    @Column(name = "id_usuario")
    private Long id_usuario;

    @Id
    @Column(name = "id_bodega")
    private Long id_bodega;
}
