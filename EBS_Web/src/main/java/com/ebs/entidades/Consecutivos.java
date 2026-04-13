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

@Data
@Entity
@Table(name = "consecutivos", schema = "searmedica")
public class Consecutivos implements Serializable {

    @Id
    @Column(name = "id_bodega")
    private Long id_bodega;

    @Column(name = "numero_sucursal")
    private String numero_sucursal;

    @Column(name = "numero_caja")
    private String numero_caja;

    @Column(name = "consecutivo_factura_electronica")
    private Integer consecutivo_factura_electronica;

    @Column(name = "consecutivo_tiquete_electronico")
    private Integer consecutivo_tiquete_electronico;

    @Column(name = "consecutivo_nota_credito")
    private Integer consecutivo_nota_credito;

    @Column(name = "consecutivo_nota_debito")
    private Integer consecutivo_nota_debito;

    @Column(name = "consecutivo_documento_confirmacion_aceptacion")
    private Integer consecutivo_documento_confirmacion_aceptacion;

    @Column(name = "consecutivo_documento_confirmacion_rechazo")
    private Integer consecutivo_documento_confirmacion_rechazo;

    @Column(name = "consecutivo_documento_confirmacion_parcial")
    private Integer consecutivo_documento_confirmacion_parcial;
    
    
}
