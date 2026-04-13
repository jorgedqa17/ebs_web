/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id_nota_debito"}, callSuper = false)
@Entity
@Table(name = "factura_nota_debito_historico_hacienda", schema = "searmedica")
public class FacturaNotaDebitoHistoricoHacienda implements Serializable {

    @Id
    @Column(name = "id_nota_debito")
    private Long id_nota_debito;

    @Id
    @Column(name = "fecha")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;

    @Column(name = "estado_factura")
    private Long estado_factura;

    @Column(name = "respuesta")
    private String respuesta;

    @Column(name = "login")
    private String login;

    @Column(name = "detallerespuesta")
    private String detallerespuesta;

    @Column(name = "documento_xml_firmado")
    private String documento_xml_firmado;

    @Column(name = "codigo_respuesta")
    private Integer codigo_respuesta;

    @Transient
    private String estado;
}
