/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
public class EnviosSucursalesModelo {

    @Getter
    @Setter
    private String producto;
    @Getter
    @Setter
    private Integer cantidad;
    @Getter
    @Setter
    private String login;
}
