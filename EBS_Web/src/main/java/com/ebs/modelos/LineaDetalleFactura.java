/*
 * To change this license header; choose License Headers in Project Properties.
 * To change this template file; choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.constantes.enums.Indicadores;
import com.ebs.entidades.DetalleFactura;
import com.ebs.entidades.Producto;
import com.ebs.entidades.TipoTarifaImpuesto;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

public class LineaDetalleFactura {

    @Getter
    @Setter
    private Integer NumeroLinea;
    @Getter
    @Setter
    private Double Cantidad;
    @Getter
    @Setter
    private String UnidadMedida;
    @Getter
    @Setter
    private String Detalle;
    @Getter
    @Setter
    private BigDecimal PrecioUnitario;
    @Getter
    @Setter
    private BigDecimal MontoTotal;
    @Getter
    @Setter
    private BigDecimal MontoDescuento;
    @Getter
    @Setter
    private String NaturalezaDescuento;
    @Getter
    @Setter
    private BigDecimal SubTotal;
    @Getter
    @Setter
    private BigDecimal totalImpuesto;
    @Getter
    @Setter
    private Impuesto Impuesto;
    @Getter
    @Setter
    private Exoneracion exoneracion;
    @Getter
    @Setter
    // private String tipo_exoneracion;
    private BigDecimal MontoTotalLinea;
    @Getter
    @Setter
    private DetalleFactura detalleFactura;

    private boolean esParaNotaCredito;

    private boolean esParaNotaDebito;
    @Getter
    @Setter
    private Producto producto;
    @Getter
    @Setter
    private boolean puedeModificar;

    @Getter
    @Setter
    private boolean tieneProblemasInventario;
    @Getter
    @Setter
    private TipoTarifaImpuesto tipoTarifaImpuesto;

    @Getter
    @Setter
    private BigDecimal totalImpuestoNeto;
    @Getter
    @Setter
    private BigDecimal totalExoneracion;
    @Getter
    @Setter
    private ExoneracionLinea exoneracionLinea;

    public void calcularImpuesto() {
        totalImpuesto = new BigDecimal("0.0");
        if (Impuesto != null) {
            Float imp = Float.parseFloat(Impuesto.getPorcentajeImpuesto().toString()) / 100f;
            totalImpuesto = SubTotal.multiply(new BigDecimal(imp)).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        }
    }

    public void calcularExoneracion() {
        if (producto.getPersonaAsociada() != null && (producto.getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador())) && (exoneracionLinea != null)) {

            Float exon = Float.parseFloat(exoneracionLinea.getPorcentaje_exonerado().toString()) / 100f;
            totalExoneracion = totalImpuesto.multiply(new BigDecimal(exon)).setScale(3, BigDecimal.ROUND_HALF_EVEN);

            totalImpuestoNeto = totalImpuesto.subtract(totalExoneracion);
        }
    }

    public boolean isEsParaNotaCredito() {
        return !detalleFactura.getEs_para_nota_credito().equals(0);
    }

    public void setEsParaNotaCredito(boolean esParaNotaCredito) {
        this.esParaNotaCredito = esParaNotaCredito;
    }

    public boolean isEsParaNotaDebito() {
        return !detalleFactura.getEs_para_nota_debito().equals(0);
    }

    public void setEsParaNotaDebito(boolean esParaNotaDebito) {
        this.esParaNotaDebito = esParaNotaDebito;
    }
}
