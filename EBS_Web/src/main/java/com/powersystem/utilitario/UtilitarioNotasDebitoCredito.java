/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.utilitario;

import com.ebs.constantes.enums.Indicadores;
import com.ebs.constantes.enums.TipoProducto;
import com.ebs.constantes.enums.TipoTarifaTimpuestoEnum;
import com.ebs.entidades.Persona;
import com.ebs.modelos.ModeloProducto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
public class UtilitarioNotasDebitoCredito {

    private List<ModeloProducto> listaProductoPorFacturar;

    private BigDecimal totalGravados;

    private BigDecimal totalGravadosServicios;

    private BigDecimal totalGravadosMercancias;

    private BigDecimal totalExentos;

    private BigDecimal totalExentosServicios;

    private BigDecimal totalExentosMercancias;

    private BigDecimal totalDescuentosLineas;

    private BigDecimal totalVenta;

    private BigDecimal totalVentaNeta;

    private BigDecimal totalImpuestos;

    private BigDecimal totalFactura;

    private BigDecimal totalExonerados;

    private BigDecimal totalExoneradosServicios;

    private BigDecimal totalExoneradosMercancias;

    private Persona personaEdicionFactura;

    public List<ModeloProducto> obtenerProductoGravados(boolean mercancias) {
        List<ModeloProducto> retorno = new ArrayList<>();
        if (personaEdicionFactura != null
                && (personaEdicionFactura.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador()))) {
            retorno = new ArrayList<>();
        } else if (mercancias) {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                    if (producto.getImpuesto() != null) {
                        retorno.add(producto);
                    }
                }
            }
        } else {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                    if (producto.getImpuesto() != null) {
                        retorno.add(producto);
                    }
                }
            }
        }

        return retorno;
    }

    public List<ModeloProducto> obtenerProductoExentos(boolean mercancias) {
        List<ModeloProducto> retorno = new ArrayList<>();
        if (personaEdicionFactura != null
                && (personaEdicionFactura.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador()))) {
            retorno.addAll(this.listaProductoPorFacturar);
        } else if (mercancias) {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                    if (producto.getInd_exonerado().equals(Indicadores.EXENTO_SI.getIndicador())) {
                        retorno.add(producto);
                    }
                }
            }
        } else {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                    if (producto.getInd_exonerado().equals(Indicadores.EXENTO_SI.getIndicador())) {
                        retorno.add(producto);
                    }
                }
            }
        }

        return retorno;
    }

    public List<ModeloProducto> obtenerProductoExonerados(boolean mercancias) {
        List<ModeloProducto> retorno = new ArrayList<>();

        if (mercancias) {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                    if ((producto.isEsPersonaExonerada() && producto.getExoneracionLinea() != null)
                            && (producto.getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()))
                            && producto.getTipoTarifaImpuesto() != null
                            && (!producto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
                        retorno.add(producto);
                    }
                }
            }
        } else {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                    if (producto.getPersonaAsociada() != null
                            && (producto.getPersonaAsociada().getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()))
                            && producto.getTipoTarifaImpuesto() != null
                            && (!producto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
                        retorno.add(producto);
                    }
                }
            }
        }

        return retorno;
    }

    public BigDecimal getTotalExoneradosServicios() {
        totalExoneradosServicios = new BigDecimal("0.0");
        BigDecimal monto = new BigDecimal("0.0");
        Float exon = 0F;
        List<ModeloProducto> retorno = obtenerProductoExonerados(false);
        for (ModeloProducto modeloProducto : retorno) {

            if (modeloProducto.getTipoTarifaImpuesto() != null
                    && (!modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {

                if (modeloProducto.isEsPersonaExonerada()
                        && (modeloProducto.getExoneracionLinea() != null)) {

                    monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
                    exon = Float.parseFloat(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / Float.parseFloat(modeloProducto.getTipoTarifaImpuesto().getValor().toString());

                }

            }

            totalExoneradosServicios = totalExoneradosServicios.add(monto.multiply(new BigDecimal(exon))).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        }
        return totalExoneradosServicios;
    }

    public BigDecimal getTotalExoneradosMercancias() {
        totalExoneradosMercancias = new BigDecimal("0.0");
        BigDecimal monto = new BigDecimal("0.0");
        Float exon = 0F;
        List<ModeloProducto> retorno = obtenerProductoExonerados(true);
        for (ModeloProducto modeloProducto : retorno) {

            if (modeloProducto.getTipoTarifaImpuesto() != null
                    && (!modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {

                if (modeloProducto.isEsPersonaExonerada()
                        && (modeloProducto.getExoneracionLinea() != null)) {

                    monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
                    exon = Float.parseFloat(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / Float.parseFloat(modeloProducto.getTipoTarifaImpuesto().getValor().toString());

                }

            }

            totalExoneradosMercancias = totalExoneradosMercancias.add(monto.multiply(new BigDecimal(exon))).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        }
        return totalExoneradosMercancias;
    }

    public BigDecimal getTotalGravadosServicios() {
        totalGravadosServicios = new BigDecimal("0.0");
        BigDecimal monto = new BigDecimal("0.0");
        List<ModeloProducto> retorno = obtenerProductoGravados(false);
        for (ModeloProducto modeloProducto : retorno) {

            if (modeloProducto.getPersonaAsociada() != null
                    && (modeloProducto.isEsPersonaExonerada()
                    && !modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))
                    && (modeloProducto.getExoneracionLinea() != null)) {

                monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
                Float exon = (1 - Float.parseFloat(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / Float.parseFloat(modeloProducto.getTipoTarifaImpuesto().getValor().toString()));
                totalGravadosServicios = totalGravadosServicios.add(monto.multiply(new BigDecimal(exon)));
                //totalGravadosServicios = totalGravadosServicios.add((modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).multiply(1 - modeloProducto.getProducto().getPorcentaje_compra() / 100));

            } else {

                // if (modeloProducto.isEsPersonaExenta()) {
                totalGravadosServicios = totalGravadosServicios.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                // }
            }
        }
        return totalGravadosServicios;
    }

    public BigDecimal getTotalGravadosMercancias() {
        totalGravadosMercancias = new BigDecimal("0.0");
        BigDecimal monto = new BigDecimal("0.0");
        List<ModeloProducto> retorno = obtenerProductoGravados(true);
        for (ModeloProducto modeloProducto : retorno) {
            if (modeloProducto.getPersonaAsociada() != null
                    && (modeloProducto.isEsPersonaExonerada()
                    && !modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))
                    && (modeloProducto.getExoneracionLinea() != null)) {

                monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
                Float exon = (1 - Float.parseFloat(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / Float.parseFloat(modeloProducto.getTipoTarifaImpuesto().getValor().toString()));
                totalGravadosMercancias = totalGravadosMercancias.add(monto.multiply(new BigDecimal(exon)));
            } else {
                // if (modeloProducto.isEsPersonaExenta()) {
                totalGravadosMercancias = totalGravadosMercancias.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                // }

            }
        }
        return totalGravadosMercancias;
    }

    public BigDecimal getTotalExonerados() {

        totalExonerados = new BigDecimal("0.0");
        totalExonerados = totalExonerados.add(getTotalExoneradosServicios()).add(getTotalExoneradosMercancias()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        return totalExonerados;
    }

    public BigDecimal getTotalExentos() {
        totalExentos = new BigDecimal("0.0");
        for (ModeloProducto producto : this.listaProductoPorFacturar) {
            if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador()) || producto.isEsPersonaExenta()) {
                totalExentos = totalExentos.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
            }
        }
        return totalExentos;
    }

    public BigDecimal getTotalGravados() {
        totalGravados = new BigDecimal("0.0");
        totalGravados = totalGravados.add(getTotalGravadosMercancias()).add(getTotalGravadosServicios()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        return totalGravados;
    }

    public BigDecimal getTotalExentosServicios() {
        totalExentosServicios = new BigDecimal("0.0");
        for (ModeloProducto producto : this.listaProductoPorFacturar) {
            if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador()) || producto.isEsPersonaExenta()) {
                    totalExentosServicios = totalExentosServicios.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                }
            }
        }
        return totalExentosServicios;
    }

    public BigDecimal getTotalExentosMercancias() {
        totalExentosMercancias = new BigDecimal("0.0");
        for (ModeloProducto producto : this.listaProductoPorFacturar) {
            if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador()) || producto.isEsPersonaExenta()) {
                    totalExentosMercancias = totalExentosMercancias.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                }
            }
        }
        return totalExentosMercancias;
    }

    public BigDecimal getTotalDescuentosLineas() {
        totalDescuentosLineas = new BigDecimal("0.0");
        for (ModeloProducto producto : this.listaProductoPorFacturar) {
            totalDescuentosLineas = totalDescuentosLineas.add(producto.getMontoDescuento() == null ? new BigDecimal("0.0") : producto.getMontoDescuento()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        }
        return totalDescuentosLineas;
    }

    public BigDecimal getTotalVenta() {
        totalVenta = new BigDecimal("0.0");
        totalVenta = getTotalGravados().add(getTotalExentos()).add(getTotalExonerados()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        return totalVenta;
    }

    public BigDecimal getTotalVentaNeta() {
        totalVentaNeta = new BigDecimal("0.0");
        totalVentaNeta = getTotalVenta().subtract(getTotalDescuentosLineas());
        return totalVentaNeta;
    }

    public BigDecimal getTotalImpuestos() {
        totalImpuestos = new BigDecimal("0.0");
        this.listaProductoPorFacturar.forEach((producto) -> {
            //producto.calcularMontos();
            if (producto.getImpuesto() != null) {
                if (producto.isEsPersonaExonerada() && producto.getExoneracionLinea() != null) {
                    totalImpuestos = totalImpuestos.add(producto.getTotalImpuestoNeto() == null ? new BigDecimal("0.0") : producto.getTotalImpuestoNeto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                } else {
                    totalImpuestos = totalImpuestos.add(producto.getTotalImpuesto() == null ? new BigDecimal("0.0") : producto.getTotalImpuesto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                }
            }
        });
        return totalImpuestos;
    }

    public BigDecimal getTotalFactura() {
        totalFactura = new BigDecimal("0.0");
        //totalFactura = totalFactura.add(getTotalVentaNeta()).add(getTotalImpuestos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        this.listaProductoPorFacturar.forEach((producto) -> {
            producto.calcularMontos();
            totalFactura = totalFactura.add(producto.getMontoTotalLinea()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        });
        return totalFactura;
    }
//    private List<ModeloProducto> obtenerProductoGravados(boolean mercancias) {
//        List<ModeloProducto> retorno = new ArrayList<>();
//
//        if (mercancias) {
//            for (ModeloProducto producto : this.listaProductoPorFacturar) {
//                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())
//                        && producto.getIndicadorEsParaNotaDebito().equals(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle())) {
//                    if (producto.getImpuesto() != null) {
//                        retorno.add(producto);
//                    }
//                }
//            }
//        } else {
//            for (ModeloProducto producto : this.listaProductoPorFacturar) {
//                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())
//                        && producto.getIndicadorEsParaNotaDebito().equals(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle())) {
//                    if (producto.getImpuesto() != null) {
//                        retorno.add(producto);
//                    }
//                }
//            }
//        }
//
//        return retorno;
//    }
//
//    public BigDecimal getTotalGravadosServicios() {
//        totalGravadosServicios = new BigDecimal("0");
//        List<ModeloProducto> retorno = obtenerProductoGravados(false);
//        for (ModeloProducto modeloProducto : retorno) {
//            totalGravadosServicios = totalGravadosServicios.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//        }
//        return totalGravadosServicios;
//    }
//
//    public BigDecimal getTotalGravadosMercancias() {
//        totalGravadosMercancias = new BigDecimal("0");
//        List<ModeloProducto> retorno = obtenerProductoGravados(true);
//        for (ModeloProducto modeloProducto : retorno) {
//            totalGravadosMercancias = totalGravadosMercancias.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//        }
//        return totalGravadosMercancias;
//    }
//
//    public BigDecimal getTotalExentos() {
//        totalExentos = new BigDecimal("0");
//        for (ModeloProducto producto : this.listaProductoPorFacturar) {
//            if (producto.getIndicadorEsParaNotaDebito().equals(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle())) {
//                if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
//                    totalExentos = totalExentos.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                }
//            }
//
//        }
//        return totalExentos;
//    }
//
//    public BigDecimal getTotalGravados() {
//        totalGravados = new BigDecimal("0");
//        if (listaProductoPorFacturar != null) {
//            for (ModeloProducto producto : this.listaProductoPorFacturar) {
//                if (producto.getIndicadorEsParaNotaDebito().equals(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle())) {
//                    if (producto.getImpuesto() != null && producto.getExoneracion() == null) {
//                        totalGravados = totalGravados.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                    }
//                }
//            }
//        }
//
//        return totalGravados;
//    }
//
//    public BigDecimal getTotalExentosServicios() {
//        totalExentosServicios = new BigDecimal("0");
//        for (ModeloProducto producto : this.listaProductoPorFacturar) {
//            if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
//                if (producto.getIndicadorEsParaNotaDebito().equals(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle())) {
//                    if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
//                        totalExentosServicios = totalExentosServicios.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                    }
//                }
//            }
//        }
//        return totalExentosServicios;
//    }
//
//    public BigDecimal getTotalExentosMercancias() {
//        totalExentosMercancias = new BigDecimal("0");
//        for (ModeloProducto producto : this.listaProductoPorFacturar) {
//            if (producto.getIndicadorEsParaNotaDebito().equals(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle())) {
//                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
//                    if (producto.getImpuesto() == null && producto.getExoneracion() != null) {
//                        totalExentosMercancias = totalExentosMercancias.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//                    }
//                }
//            }
//        }
//        return totalExentosMercancias;
//    }
//
//    public BigDecimal getTotalDescuentosLineas() {
//        totalDescuentosLineas = new BigDecimal("0");
//        for (ModeloProducto producto : this.listaProductoPorFacturar) {
//            if (producto.getIndicadorEsParaNotaDebito().equals(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle())) {
//                totalDescuentosLineas = totalDescuentosLineas.add(producto.getMontoDescuento() == null ? new BigDecimal("0") : producto.getMontoDescuento()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//            }
//        }
//        return totalDescuentosLineas;
//    }
//
//    public BigDecimal getTotalVenta() {
//        totalVenta = getTotalGravados().add(getTotalExentos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//        return totalVenta;
//    }
//
//    public BigDecimal getTotalVentaNeta() {
//        totalVentaNeta = getTotalVenta().subtract(getTotalDescuentosLineas());
//        return totalVentaNeta;
//    }
//
//    public BigDecimal getTotalImpuestos() {
//        totalImpuestos = new BigDecimal("0");
//        for (ModeloProducto producto : this.listaProductoPorFacturar) {
//            if (producto.getIndicadorEsParaNotaDebito().equals(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle())) {
//                totalImpuestos = totalImpuestos.add(producto.getTotalImpuesto() == null ? new BigDecimal("0") : producto.getTotalImpuesto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
//            }
//        }
//        return totalImpuestos;
//    }
//
//    public BigDecimal getTotalFactura() {
//        totalFactura = new BigDecimal("0");
//        totalFactura = totalFactura.add(getTotalVentaNeta()).add(getTotalImpuestos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
//        return totalFactura;
//    }

}
