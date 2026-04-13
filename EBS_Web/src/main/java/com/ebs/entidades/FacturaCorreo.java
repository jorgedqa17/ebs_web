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

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
@Entity
@Table(name = "factura_correo", schema = "searmedica")
public class FacturaCorreo implements Serializable {

    @Id
    @Column(name = "id_factura")
    private Long id_factura;

    @Id
    @Column(name = "correo")
    private String correo;

    public FacturaCorreo() {
    }

    public FacturaCorreo(String correo) {

        this.correo = correo;
    }

}
