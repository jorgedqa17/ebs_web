/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.Cierre;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author sergio
 */
public class ConsultaCierre {

    @Getter
    @Setter
    private Long id_cierre;
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String fechaCierreTexto;
    @Getter
    @Setter
    private Date fechaCierre;
    @Getter
    @Setter
    private String fechaInicioTexto;
    @Getter
    @Setter
    private Date fechaInicio;
    @Getter
    @Setter
    private String fechaFinTexto;
    @Getter
    @Setter
    private Date fechaFin;
    @Getter
    @Setter
    private BigDecimal MontoEntrada;
    @Getter
    @Setter
    private BigDecimal MontoSalida;
    @Getter
    @Setter
    private BigDecimal MontoAnulacion;
    @Getter
    @Setter
    private BigDecimal monto_facturas_credito;
    @Getter
    @Setter
    private BigDecimal monto_facturas_tarjeta;
    @Getter
    @Setter
    private BigDecimal monto_efectivo;
    @Getter
    @Setter
    private BigDecimal monto_cheque;

    public ConsultaCierre() {
    }

    public ConsultaCierre(Cierre cierre) {
        DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        this.id_cierre = cierre.getId_cierre();
        this.login = cierre.getLogin();
        this.fechaCierre = cierre.getFecha_generacion_cierre();
        this.fechaInicio = cierre.getFecha_inicio();
        this.fechaFin = cierre.getFecha_fin();
        this.MontoEntrada = cierre.getMonto_entradas();
        this.MontoSalida = cierre.getMonto_saludas();
        this.MontoAnulacion = cierre.getMonto_anulaciones();
        this.fechaCierreTexto = (this.fechaCierre == null ? "" : formato.format(this.fechaCierre));
        this.fechaInicioTexto = (this.fechaInicio == null ? "" : formato.format(this.fechaInicio));
        this.fechaFinTexto = (this.fechaFin == null ? "" : formato.format(this.fechaFin));
        this.monto_cheque = cierre.getMonto_cheque();
        this.monto_efectivo = cierre.getMonto_efectivo();
        this.monto_facturas_credito = cierre.getMonto_facturas_credito();
        this.monto_facturas_tarjeta = cierre.getMonto_facturas_tarjeta();
    }

}
