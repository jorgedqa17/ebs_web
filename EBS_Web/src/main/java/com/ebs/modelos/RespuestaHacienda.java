/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
@Data
public class RespuestaHacienda {

    private String clave;

    private String nombreEmisor;

    private String tipoIdentificacionEmisor;

    private String numeroCedulaEmisor;

    private String nombreReceptor;

    private String tipoIdentificacionReceptor;

    private String mensaje;
    
    private String ind_estado;

    private String detalleMensaje;

    private String MontoTotalImpuesto;

    private String TotalFactura;
}
