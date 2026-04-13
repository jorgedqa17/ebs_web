/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import lombok.Data;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
public class InfoExoneracion {

    private String numeroDocumento;
    private String identificacion;
    private String fechaEmision;
    private String fechaVencimiento;
    private String nombreInstitucion;
    private String porcentajeExoneracion;
    private TipoDocumento tipoDocumento;

    public String getInformacionExoneracion() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("Número de Documento: ").append(numeroDocumento).append("\n")
                .append("Identificación: ").append(numeroDocumento).append("\n")
                .append("Fecha Emisión: ").append(fechaEmision).append("\n")
                .append("Fecha Vencimiento: ").append(fechaVencimiento).append("\n")
                .append("Porcentaje Exoneración: ").append(porcentajeExoneracion).append("\n")
                .append("Nombre Institución: ").append(nombreInstitucion).append("\n")
                .append("Descripción: ").append(tipoDocumento.getDescripcion()).append("\n");
        return resultado.toString();
    }
}
