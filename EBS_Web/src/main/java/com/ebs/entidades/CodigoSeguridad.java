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

@Data
@EqualsAndHashCode(of = {"codigo_seguridad"}, callSuper = false)
@Entity
@Table(name = "codigos_seguridad", schema = "searmedica")
public class CodigoSeguridad implements Serializable {

    @Id
    @Column(name = "codigo_seguridad")
    private String codigo_seguridad;
}
