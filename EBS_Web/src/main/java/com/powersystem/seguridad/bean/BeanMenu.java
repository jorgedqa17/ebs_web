/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.seguridad.bean;

import com.ebs.exception.ExcepcionManager;
import com.ebs.entidades.Pantalla;
import com.ebs.entidades.Usuario;
import com.powersystem.seguridad.servicios.ServicioMenu;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.SesionUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 * @author Jorge API Technologies
 */
@ManagedBean
@ViewScoped
public class BeanMenu {

    private MenuModel model;
    @Inject
    private ServicioMenu servicioMenu;

    public BeanMenu() {
    }

    public void construirMenu() {
        model = new DefaultMenuModel();
        Usuario usuarioLogueado = (Usuario) JSFUtil.obtenerDeSesion("Usuario");
        if (usuarioLogueado != null) {
            //Si el menú ya existe en sesión no es necesario volver a crear, se obtiene el actual y se setea el valor 
            if (JSFUtil.obtenerDeSesion("menu") == null) {
                try {
                    //Obtengo las pantallas
                    List<Pantalla> listaNodos = servicioMenu.obtenerNodosMenu(usuarioLogueado.getLogin());
                    //Ordeno los nodos y construyo el menú
                    for (Pantalla pantalla : listaNodos) {
                        if (pantalla.getId_padre_pantalla().equals(0)) {
                            DefaultSubMenu nodoPadre = new DefaultSubMenu(pantalla.getDescripcion());
                            for (Pantalla pantallaHija : obtenerListaNodosHijos(listaNodos, pantalla.getId_pantalla())) {
                                DefaultMenuItem nodoH = new DefaultMenuItem(pantallaHija.getDescripcion());
                                //nodoH.setUrl(pantallaHija.getUrl());
                                nodoH.setOutcome(pantallaHija.getUrl());
                                nodoH.setIcon(pantallaHija.getIcono());
                                nodoH.setId(pantalla.getId_pantalla().toString());
                                nodoH.setValue(pantallaHija.getDescripcion());
                                nodoPadre.addElement(nodoH);
                            }
                            model.addElement(nodoPadre);
                        }
                    }
                    JSFUtil.guardarEnSesion("menu", model);
                } catch (Exception ex) {
                    ExcepcionManager.manejarExcepcion(ex);
                }
            } else {
                model = (MenuModel) JSFUtil.obtenerDeSesion("menu");
            }
        } 

    }

    /**
     * Método que obtiene las pantallas pertenecientes a una pantalla padre
     *
     * @param listaPantallas
     * @param idPantallaPadre
     * @return lista con las pantallas
     */
    public List<Pantalla> obtenerListaNodosHijos(List<Pantalla> listaPantallas, Long idPantallaPadre) {
        List<Pantalla> listaResultado = new ArrayList<>();
        for (Pantalla pantalla : listaPantallas) {
            if (pantalla.getId_padre_pantalla().equals(idPantallaPadre.intValue())) {
                listaResultado.add(pantalla);
            }
        }
        return listaResultado;
    }

    public MenuModel getModel() throws Exception {
        construirMenu();
        return model;
    }
}
