/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author jdquesad
 */
@Data
public class ExoneracionLinea {

    private Long id_exoneracion;

    private String numero_documento;

    private String nombre_institucion;

    private Date fecha_emision;
    private String fecha_emision_str;

    private Integer porcentaje_exonerado;

    private boolean aplicar_para_toda_factura;
    private Integer maximoPorcentajeExoneracion;

    public ExoneracionLinea() {
        this.porcentaje_exonerado = 0;
        this.id_exoneracion = 0L;
        this.numero_documento = "";
        this.nombre_institucion = "";
        this.aplicar_para_toda_factura
                = false;

        this.fecha_emision = new Date();
    }

}
