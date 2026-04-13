/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 *
 * @author jdquesad
 */
public enum ParametrosEnum {

    /**
     * Indica la activacion de la factura con el inventario
     */
    INVENTARIO_FACTURA_ACTIVO(1L),
    /**
     * Indica la bodega principal
     */
    BODEGA_PRINCIPAL(2L),
    /**
     * Indica la cantidad de días que tiene en reserva un pedido
     */
    CANTIDAD_DIAS_PEDIDO(3L),
    /**
     * Parámetro que indica la
     */
    END_POINT_SERVICIO(4L),
    PLANTILLA_CORREO(5L),
    USUARIO_CORREO(6L),
    CONTRASENNA_CORREO(7L),
    CUERPO_CORREO_RECIBO(8L),
    VALIDAR_PRECIOS(9L),
    PORCENTAJE_GANANCIA(10L),
    SALARIO_BASE(11L),
    VALOR_MULTIPLICACION_ALQUILERES(12L),
    END_POINT_CONSULTA_PERSONA_HACIENDA(13L),
    AMBIENTE_HACIENDA(14L),
    LINK_TOKEN_DESARROLLO(15L),
    LINK_TOKEN_PRODUCCION(16L),
    LINK_ESTADO_DOCUMENTO_ACEPTACION_DESARROLLO(19L),
    LINK_ESTADO_DOCUMENTO_ACEPTACION_PRODUCCION(20L),
    LINK_CIERRE_SESION_DESARROLLO(17L),
    LINK_CIERRE_SESION_PRODUCCION(18L),
    USUARIO_HACIENDA(21L),
    PASSWORD_HACIENDA(22L),
    API_URL_EXONERACION(25L),
    RUTA_PADRE(26L),
    LINK_OBTENCION_CORREO_ELECTRONICO(27L);

    private final Long idParametro;

    private ParametrosEnum(Long idParametro) {
        this.idParametro = idParametro;
    }

    public Long getIdParametro() {
        return idParametro;
    }
}
