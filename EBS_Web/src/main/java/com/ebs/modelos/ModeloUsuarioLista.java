package com.ebs.modelos;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fila de la lista del mantenimiento de usuarios (sin la contraseña). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloUsuarioLista implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUsuario;
    private String login;
    private String nombre;
    private boolean activo;
    private boolean esUsuarioBodega;
    private String numeroCaja;
    /** Descripciones de los perfiles, separadas por coma. */
    private String perfiles;
    /** Descripciones de las bodegas, separadas por coma. */
    private String bodegas;

    public String getEstado() {
        return activo ? "Activo" : "Inactivo";
    }
}
