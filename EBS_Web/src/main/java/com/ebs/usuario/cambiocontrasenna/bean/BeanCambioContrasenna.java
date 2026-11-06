/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.usuario.cambiocontrasenna.bean;

import com.ebs.exception.ExcepcionManager;
import com.powersystem.seguridad.servicios.ServicioLogin;
import com.powersystem.seguridad.util.LimiteIntentos;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import lombok.Data;

/**
 *
 * @author jdquesad
 */
@Data
@ManagedBean
@ViewScoped
public class BeanCambioContrasenna {

    @Inject
    private ServicioLogin servicioLogin;

    private String contrasenna;
    private String usuario;
    private String contrasennaAnterior;
    private boolean verBotonGuardar;

    public BeanCambioContrasenna() {
    }

    @PostConstruct
    public void inicializar() {
        contrasenna = "";
        usuario = "";
        contrasennaAnterior = "";
        verBotonGuardar = true;
    }

    public boolean validarIngresoInformacion() {
        boolean resultado = true;
        if (contrasenna == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.advertencia.datos.incompletos"));
        } else {
            if (contrasenna.equals("")) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.advertencia.datos.incompletos", ""));
            }
        }
        if (usuario == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.advertencia.datos.incompletos"));
        } else {
            if (usuario.equals("")) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.advertencia.datos.incompletos", ""));
            }
        }
        if (contrasennaAnterior == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.advertencia.datos.incompletos"));
        } else {
            if (contrasennaAnterior.equals("")) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.advertencia.datos.incompletos", ""));
            }
        }
        return resultado;
    }

    public void regresar() throws IOException {
        JSFUtil.redireccionarPagina("/login.xhtml");
    }

    public void cambiarContrasena() {
        try {
            if (this.validarIngresoInformacion()) {
                // Esta pantalla es pública y compara la contraseña actual: se limita igual que el login
                String ip = LimiteIntentos.ipCliente();
                long segundosBloqueado = LimiteIntentos.segundosBloqueado(ip, usuario);
                if (segundosBloqueado > 0) {
                    MensajeUtil.agregarMensajeAdvertencia(LimiteIntentos.mensajeBloqueo(segundosBloqueado));
                    return;
                }
                boolean existeUsuario = servicioLogin.validarExistenciaUsuario(usuario);
                if (!existeUsuario) {
                    LimiteIntentos.registrarFallo(ip, usuario);
                }
                if (existeUsuario) {
                    boolean contrasennaActualCorrecta = servicioLogin.validarContrasennasCoinciden(usuario, contrasennaAnterior);
                    if (!contrasennaActualCorrecta) {
                        LimiteIntentos.registrarFallo(ip, usuario);
                    }
                    if (contrasennaActualCorrecta) {
                        LimiteIntentos.registrarExito(usuario);
                        servicioLogin.cambiarContrasennaUsuario(usuario, contrasenna);
                        MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.validacon.cambiar.contrasenna.exito"));
                        verBotonGuardar = false;
                        contrasenna = "";
                        usuario = "";
                        contrasennaAnterior = "";
                    } else {
                        MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validcion.contrasenna.erronea"));
                    }
                } else {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.usaurio.existe"));
                }
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }
}
