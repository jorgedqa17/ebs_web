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
public enum TipoTarifaTimpuestoEnum {
    /*
1	Tarifa 0% (Exento) 	01	0
2	Tarifa reducida 1%	02	1
3	Tarifa reducida 2%	03	2
4	Tarifa reducida 4%	04	4
5	Transitorio 0%,	05	0
6	Transitorio 4% 	06	4
7	Transitorio 8%	07	8
8	Tarifa general 13%	08	13
9	Tarifa reducida 0.5%	09	0
10	Tarifa Exenta	10	0
11	Tarifa 0% sin derecho a crédito 	11	0
     */
    TARIFA_01(1L),
      TARIFA_10(10L),
    TARIFA_07(7L);

    private final Long idTipoTarifa;

    private TipoTarifaTimpuestoEnum(Long idTipoTarifa) {
        this.idTipoTarifa = idTipoTarifa;
    }

    public Long getIdTipoTarifa() {
        return idTipoTarifa;
    }

}
