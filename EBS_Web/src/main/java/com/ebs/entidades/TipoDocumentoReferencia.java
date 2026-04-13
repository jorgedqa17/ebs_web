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
@EqualsAndHashCode(of = {"id_tipo_doc_referencia"}, callSuper = false)
@Entity
@Table(name = "tipo_documento_referencia", schema = "searmedica")
public class TipoDocumentoReferencia implements Serializable {

    @Id
    @Column(name = "id_tipo_doc_referencia")
    private Long id_tipo_doc_referencia;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "codigo_hacienda")
    private String codigo_hacienda;



}
