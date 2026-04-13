/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jdquesad
 */
public class TrazabilidadModelo {

    @Getter
    @Setter
    private String tipoGestion;
    @Getter
    @Setter
    private Long idProducto;
    @Getter
    @Setter
    private Integer cantidad;
    @Getter
    @Setter
    private String fechaRegistro;
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String bodegaOrigen;
    @Getter
    @Setter
    private String bodegaDestino;
    @Getter
    @Setter
    private String tipo;
    @Getter
    @Setter
    private String numeroConsecutivoFactura;
    @Getter
    @Setter
    private String descripcionProducto;

}
