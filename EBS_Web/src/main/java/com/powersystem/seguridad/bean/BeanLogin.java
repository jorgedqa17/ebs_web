/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.seguridad.bean;

import com.google.gson.Gson;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.Factura;
import com.ebs.entidades.FacturaHistoricoHacienda;

import com.ebs.exception.ExcepcionManager;
import com.ebs.entidades.Usuario;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.seguridad.servicios.ServicioLogin;
import com.powersystem.seguridad.servicios.ServicioPermisos;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Jorge GBSYS
 */
@ManagedBean
@ViewScoped
@Data
public class BeanLogin {

    @Getter
    @Setter
    private String usuario;
    @Getter
    @Setter
    private String password;
    @Inject
    private ServicioLogin servicio;
    @Inject
    private ServicioPermisos servicioPermisos;
    @Inject
    private ServicioPersona servicioPersona;
    @Getter
    @Setter
    List<Bodega> listaBodegasUsuario;
    @Getter
    @Setter
    private Long idBodegaSeleccionada;
    @Inject
    private ServicioFactura servFact;

    public BeanLogin() {

    }

    @PostConstruct
    public void inicializar() {
        servicioPersona.obtenerPersonaEmisor();
        // buscarFacturaMalasSinDOcs();
    }

    public void buscarFacturaMalasSinDOcs() {
        List<Factura> listaFActuras = servFact.obtenerTodasFacturas();
        List<FacturaHistoricoHacienda> listaHistorico = null;
        int contador = 0;
        boolean tieneDocXML = false;

        for (Factura factura : listaFActuras) {
            tieneDocXML = false;
            listaHistorico = servFact.obtenerHIstorioFactura(factura.getId_factura());
            for (FacturaHistoricoHacienda historico : listaHistorico) {
                if (historico.getDocumento_xml_firmado() != null) {
                    tieneDocXML = true;
                    break;
                }
            }
            if (!tieneDocXML) {
                contador++;
            }
        }
    }

    public void seleccionarBodega() {
        try {
            if (this.idBodegaSeleccionada != null && !this.idBodegaSeleccionada.equals(0L)) {

                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('bodegaDialog').hide()");

                JSFUtil.guardarEnSesion("idBodega", idBodegaSeleccionada);
                JSFUtil.guardarEnSesion("bodega", this.listaBodegasUsuario.stream()
                        .filter(predicate -> predicate.getBodegaPK().getId_bodega().equals(idBodegaSeleccionada))
                        .findAny().get());

                JSFUtil.redireccionarPagina("/xhtml/paginas/index.xhtml");
            } else {
                MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar la bodega");
            }

        } catch (IOException ex) {
            ExcepcionManager.manejarExcepcion(ex);
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerEtiqueta("mensaje.error.login"));
        }
    }

    public void redirigirCambioContrasena() throws IOException {
        JSFUtil.redireccionarPagina("/xhtml/paginas/usuario/cambiarcontrasenna.xhtml");
    }

    /**
     * Ejecuto el mètodo de inicio de sesiòn
     *
     * @param event
     */
    public void ingresar(ActionEvent event) throws Exception {
        try {
            //CT18001f78*

            if (usuario != null && !usuario.equals("")) {
                if (password != null && !password.equals("")) {
                    //Si logra encontrar el usaurio y las contraseñas coinciden permite continuar con el proceso
                    Usuario user = servicio.iniciarSesion(usuario, password);
                    if (user != null) {
                        user.setPassword("");
                        JSFUtil.guardarEnSesion("Usuario", user);

                        //Obtengo los permisos del usuario sobre las fucionalidades de las pantallas
                        servicioPermisos.obtenerRolesUsuario();
                        servicioPermisos.obtenerBodegasUsuario();
                        // servicio.obtenerUsuarioBodega(Utilitario.obtenerUsuario().getId_usuario());                        
                        servicio.obtenerUsuarioAdministrador(Utilitario.obtenerUsuario());
                        listaBodegasUsuario = (List<Bodega>) JSFUtil.obtenerDeSesion("bodegasUsuario");
                        if (((List<Bodega>) JSFUtil.obtenerDeSesion("bodegasUsuario")).size() > 1) {
                            RequestContext context = RequestContext.getCurrentInstance();
                            context.execute("PF('bodegaDialog').show()");
                        } else {
                            JSFUtil.guardarEnSesion("bodega", this.listaBodegasUsuario.stream()
                                    .filter(predicate -> predicate.getBodegaPK().getId_bodega().equals(servicio.obtenerUsuarioBodega(Utilitario.obtenerUsuario().getId_usuario()))).findAny().get());
                            JSFUtil.redireccionarPagina("/xhtml/paginas/index.xhtml");
                        }

                    } else {
                        MensajeUtil.agregarMensajeAdvertencia(
                                EtiquetasUtil.obtenerMensaje(
                                        "mensaje.error.login",
                                        ""));
                    }
                } else {
                    MensajeUtil.agregarMensajeAdvertencia(
                            EtiquetasUtil.obtenerMensaje(
                                    "mensaje.advertencia.datos.incompletos",
                                    ""));
                }
            } else {
                MensajeUtil.agregarMensajeAdvertencia(
                        EtiquetasUtil.obtenerMensaje(
                                "mensaje.advertencia.datos.incompletos",
                                ""));
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerEtiqueta("mensaje.error.login"));
        }

    }

    public Usuario obtenerUsuario() {
        return Utilitario.obtenerUsuario();
    }

    public Bodega obtenerBodega() {
        return Utilitario.obtenerBodega();
    }
}
