/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
@Entity
@Table(name = "pago_totales", schema = "searmedica")
public class PagoTotal implements Serializable {

    @Column(name = "id_pago")
    @Id
    private Long id_pago;

    @Column(name = "id_tipo_tarifa_impuesto")
    private Long id_tipo_tarifa_impuesto;

    @Column(name = "monto")
    private BigDecimal monto;
}
