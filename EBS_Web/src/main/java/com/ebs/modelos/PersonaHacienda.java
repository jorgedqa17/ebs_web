/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.util.List;
import lombok.Data;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
public class PersonaHacienda {

    private String nombre;
    private String tipoIdentificacion;
    private Regimen regimen;
    List<Actividades> actividades;
    private String code;
    private String status;
}
