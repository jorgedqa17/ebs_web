/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author jdquesad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UsuarioComisionPK implements Serializable {

    @Column(name = "id_usuario")
    private Long id_usuario;

    @Column(name = "id_tipo_comision")
    private Long id_tipo_comision;
}
