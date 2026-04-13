/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
@Data
@Entity
@Table(name = "usuario", schema = "searmedica")
public class Usuario implements Serializable {

    @Getter
    @Setter
    @Id
    private Long id_usuario;
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private Integer activo;
    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private Long es_usuario_bodega;

    @Getter
    @Setter
    private String numero_caja;

    public Usuario(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Usuario() {
    }

}
