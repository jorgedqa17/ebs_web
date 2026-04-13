/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ebs.modelos;

import lombok.Data;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
public class Token {

    private String access_token;
    private String expires_in;
    private String id_token;
    private String refresh_token;
    private String refresh_expire_in;
}