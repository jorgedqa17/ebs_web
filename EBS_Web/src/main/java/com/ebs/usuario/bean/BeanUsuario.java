/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.usuario.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.TipoComision;
import com.ebs.entidades.Usuario;
import com.ebs.entidades.UsuarioBodega;
import com.ebs.entidades.UsuarioComision;
import com.ebs.entidades.UsuarioComisionPK;
import com.ebs.exception.ExcepcionManager;
import com.ebs.usuario.servicio.ServicioUsuario;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import lombok.Data;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author jdquesad
 */
@Data
@ManagedBean
@ViewScoped
public class BeanUsuario {

    private List<Usuario> listaUsuarios;
    private List<Usuario> listaUsuariosFiltro;
    private Usuario usuario;
    private UsuarioComision usuarioComision;
    private UsuarioComisionPK usuarioComisionPk;
    private List<TipoComision> listaTipoComision;
    private List<Bodega> listaBodegas;
    private Long idTipoComisionSeleccionada;
    @Inject
    private ServicioUsuario servicioUsuario;
    @Inject
    private ServicioBodega servicioBodega;
    private boolean inhablitarControles;
    private DualListModel<Bodega> listaBodegasDual;
    private List<Bodega> listaBodegasDisponibles;
    private List<Bodega> listaBodegasAsignadas;
    private List<UsuarioBodega> listaBodegasUsuario;

    public BeanUsuario() {
    }

    public void llenarListaStringBodegasAsignadas() {
        this.listaBodegas.forEach(elemento -> {
            for (UsuarioBodega usuarioBodega : listaBodegasUsuario) {
                if (elemento.getBodegaPK().getId_bodega().equals(usuarioBodega.getId_bodega())) {
                    listaBodegasAsignadas.add(elemento);
                }
            }
        });
    }

    @PostConstruct
    public void inicializar() {
        try {

            this.listaTipoComision = servicioUsuario.obtenerListaTipoComision();
            this.listaUsuarios = servicioUsuario.obtenerListaUsuarios();
            this.listaBodegas = servicioBodega.obtenerListaBodegas();
            this.listaBodegasDual = new DualListModel<Bodega>(new ArrayList<>(), new ArrayList<>());
            this.inhablitarControles = true;
            this.usuario = new Usuario();
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarUsuario(Usuario usuarioSeleccionado) {
        this.usuario = usuarioSeleccionado;
        this.inhablitarControles = true;
        this.listaBodegasUsuario = this.servicioUsuario.obtenerListaBodegasUsuario(usuario);
        listaBodegasAsignadas = new ArrayList<>();
        listaBodegasDisponibles = new ArrayList<>();
        llenarListaStringBodegasAsignadas();
        this.listaBodegasDual = new DualListModel<Bodega>(listaBodegas, listaBodegasAsignadas);
    }

    public void editarUsuario() {
        this.inhablitarControles = false;
    }

    public void nuevoUsuario() {
        usuario = new Usuario();

    }

    public void onTransfer(TransferEvent event) {

    }

    public void onSelect(SelectEvent event) {

    }

    public void onUnselect(UnselectEvent event) {

    }

    public void onReorder() {

    }
}
