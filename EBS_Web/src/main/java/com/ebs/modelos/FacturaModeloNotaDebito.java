/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import lombok.Data;

/**
 *
 * @author jdquesad
 */
@Data
public class FacturaModeloNotaDebito {

    private String consecutivo;

    private String estadoHacienda;

    private String clave;

    private String motivoNotaDebito;

    private String tipoMotivoNotaDebito;

    private String login;

    private String fechaNotaDebito;

    private Long idNotaDebito;
}
