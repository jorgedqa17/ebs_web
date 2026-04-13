/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.seguridad.bean;

import com.ebs.exception.ExcepcionManager;
import com.powersystem.util.BeanBase;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.SesionUtil;
import java.io.IOException;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge GBSYS
 */
@Named("beanLogOut")
@RequestScoped
public class BeanLogOut extends BeanBase {

    @Getter
    @Setter
    private String pantallaLogin = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/login.xhtml";

    @Override
    public void inicializar() {

    }

    @Override
    public void inicializarPost() {

    }

    /**
     * Método encargado de la accion de terminar la sesión del usuario. Llama al
     * método que se encarga de salir de la aplicación y borrar todo dato de la
     * sesión en uso. Además finalmente retorna al HOME de sire.
     *
     * @return Regla de navegación.
     */
    public void terminarSesion(ActionEvent event) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            InformacionUsuario infoUsuario = (InformacionUsuario) SesionUtil.obtenerDeSesion(SesionUtil.VariablesSesion.INFO_USUARIO);
            SesionUtil.salirAplicacionForce(facesContext, infoUsuario);         
            facesContext.getExternalContext().invalidateSession();        
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public String redireccionoarLogin() {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return contextPath + "/login.xhtml";
    }

    /**
     * Metodo encargado de terminar la sesión cuando es un usuario externo, con
     * los links.
     */
    public void terminarSesionExterna() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            InformacionUsuario infoUsuario = (InformacionUsuario) SesionUtil.obtenerDeSesion(SesionUtil.VariablesSesion.INFO_USUARIO);
            SesionUtil.salirAplicacionForce(facesContext, infoUsuario);
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }
}
