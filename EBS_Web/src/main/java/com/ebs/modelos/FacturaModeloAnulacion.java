/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Factura;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Jorge GBSYS
 */
@Data
public class FacturaModeloAnulacion {

    private Long idAnulacion;

    private String consecutivo;

    private BigDecimal montoTotal;

    private String estadoHacienda;

    private Long idFactura;

    private String cliente;

    private String clave;

    private String motivoAnulacion;

    private String tipoMotivoAnulacion;

    private String login;

    private String fechaAnulacion;

    private Factura factura;

    private List<LineaDetalleFactura> listaDetalleFactura;

    private Integer cantidadDias;
}
