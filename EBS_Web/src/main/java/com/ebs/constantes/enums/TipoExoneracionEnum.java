/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.constantes.enums;

/**
 *
 * @author Jorge Quesada Arias
 */
public enum TipoExoneracionEnum {
    COMPRAS_AUTORIZADAS(1L),
    VENTAS_EXENTAS_A_DIPLOMÁTICOS(2L),
    EXENCIONES_DIRECCIÓN_GENERAL_DE_HACIENDA(4L),
    AUTORIZADO_POR_LEY_ESPECIAL(3L),
    TRANSITORIO_V(5L),
    TRANSITORIO_IX(6L),
    TRANSITORIO_XVII(7L),
    OTROS(8L);

    private Long idTipoExoneracion;

    private TipoExoneracionEnum(Long idTipoExoneracion) {
        this.idTipoExoneracion = idTipoExoneracion;
    }

    public Long getIdTipoExoneracion() {
        return idTipoExoneracion;
    }

    public void setIdTipoExoneracion(Long idTipoExoneracion) {
        this.idTipoExoneracion = idTipoExoneracion;
    }

}
