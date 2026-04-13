/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.utilitario;

/**
 *
 * @author hp i5 7300u
 */
public interface SessionWrapper {

    public abstract String getIp();

    public abstract void setIp(String ip);

    public abstract String getUsuario();

    public abstract void setUsuario(String usuario);

    public abstract String getNombre();

    public abstract void setNombre(String nombre);

    public abstract Long getTiempoUltimaSolicitud();

    public abstract void setTiempoUltimaSolicitud(Long tiempoUltimaSolicitud);

    public abstract Boolean isAutentificado();

    public abstract void setAutentificado(Boolean autentificado);

    public abstract String getBrowser();

    public abstract void setBrowser(String browser);
}
