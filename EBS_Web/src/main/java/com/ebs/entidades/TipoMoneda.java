/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
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
@EqualsAndHashCode(of = {"id_moneda"}, callSuper = false)
@Entity
@Table(name = "tipo_moneda", schema = "searmedica")
public class TipoMoneda implements Serializable {

    @Id
    @Column(name = "id_moneda")
    private Long id_moneda;

    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "simbolo")
    private String simbolo;

    @Column(name = "tipo_cambio")
    private BigDecimal tipo_cambio;

}
