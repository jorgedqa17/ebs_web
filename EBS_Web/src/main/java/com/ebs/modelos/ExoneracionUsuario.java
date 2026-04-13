/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExoneracionUsuario {

    private String numeroDocumento;
    private String identificacion;
    private int codigoProyectoCFIA;
    private int porcentajeExoneracion;
    private int autorizacion;
    private String fechaEmision;
    private String fechaVencimiento;
    private int ano;
    private List<String> cabys;
    private String tipoAutorizacion;
    private TipoDocumento tipoDocumento;
    private String CodigoInstitucion;
    private String nombreInstitucion;
    private boolean poseeCabys;
    private String articulo;
    private String inciso;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TipoDocumento {

        private Long codigo;
        private String descripcion;
    }
}
