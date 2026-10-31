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

    //Ids de la tabla (no el código de Hacienda) que anulan totalmente el documento:
    //01 "Anula documento de referencia" y 13 "Anula documento de referencia por error material"
    //(Nota 9, Anexos y Estructuras 4.4).
    public static final Long ID_ANULA_DOCUMENTO = 1L;
    public static final Long ID_ANULA_DOCUMENTO_ERROR_MATERIAL = 7L;

    public static boolean esAnulacionTotal(Long idTipoDocReferencia) {
        return ID_ANULA_DOCUMENTO.equals(idTipoDocReferencia)
                || ID_ANULA_DOCUMENTO_ERROR_MATERIAL.equals(idTipoDocReferencia);
    }

    @Id
    @Column(name = "id_tipo_doc_referencia")
    private Long id_tipo_doc_referencia;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "codigo_hacienda")
    private String codigo_hacienda;



}
