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
 * @author Jorge Quesada Arias
 */
@Data
@EqualsAndHashCode(of = {"id_pago"}, callSuper = false)
@Entity
@Table(name = "pago_xml_aceptado", schema = "searmedica")
public class PagoXmlAceptado implements Serializable {

    @Id
    @Column(name = "id_pago")
    private Long id_pago;

    @Column(name = "xml_aceptado")
    private String xml_aceptado;

    public PagoXmlAceptado(Long id_pago, String xml_aceptado) {
        this.id_pago = id_pago;
        this.xml_aceptado = xml_aceptado;
    }

    public PagoXmlAceptado() {
    }

}
