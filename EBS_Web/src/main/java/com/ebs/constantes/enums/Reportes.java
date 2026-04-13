/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 *
 * @author Jorge GBSYS
 */
public enum Reportes {
    FACTURA("Factura", "reporte_factura.jrxml"),
    FACTURA_TRAZABILIDAD("Factura", "reporte_factura_trazabilidad.jrxml"),
    CIERRE("Cierre", "Reporte_cierre.jrxml"),
    ENVIOS_DIARIOS("Envios", "enviosDarios.jrxml"),
    VENTAS_MENSUALES("Ventas_Sucursales", "reporte_ventas_mensuales.jrxml"),
    VENTAS_MENSUALES_USUARIO("Ventas_Usuario", "reporte_ventas_mensualesPorUsuario.jrxml"),
    REPORTE_LISTADO_FACTURA("Listado_Facturas", "reporte_listado_facturas.jrxml"),
    REPORTE_NOTAS_CREDITO("Nota_Credito", "reporte_notas_credito.jrxml"),
    REPORTE_NOTAS_DEBITO("Nota_Debito", "reporte_notas_debito.jrxml"),
    CONFIRMACIO_COMPROBANTES_ELECTRONICOS("Confirmacion_Comprobante", "reporte_confirmacion_comprobantes_electronicos.jrxml"),
    REPORTE_EXISTENCIAS_INVENTARIO("Reporte_Existencias", "reporte_existencias_inventario.jrxml"),
    REPORTE_RECIBOS("Recibo", "recibos.jrxml"),
    REPORTE_ESTADO_CUENTA_CLIENTE("Estado_Cuenta", "estado_cuenta_cliente.jrxml"),
    REPORTE_FACTURA_PUNTO_VENTA("Factura_punto_venta","reporte_factura_tiquete.jrxml"),
    REPORTE_FACTURA_PUNTO_VENTA_TRAZABILIDAD("Factura_punto_venta","reporte_factura_tiquete_trazabilidad.jrxml");

    private final String nombreReporte;
    private final String jasper;

    private Reportes(String nombreReporte, String jasper) {
        this.nombreReporte = nombreReporte;

        this.jasper = jasper;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public String getJasper() {
        return jasper;
    }

}
