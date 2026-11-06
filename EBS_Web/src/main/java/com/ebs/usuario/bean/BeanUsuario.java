package com.ebs.usuario.bean;

import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.ModeloUsuarioLista;
import com.ebs.usuario.servicio.ServicioUsuario;
import com.powersystem.utilitario.MensajeUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 * Mantenimiento de usuarios: lista, alta y edición (datos, contraseña, perfiles y bodegas). Solo el
 * perfil Administrador; la autorización se revisa en el servicio en cada operación, no solo aquí.
 * No hay ventana modal: la lista y el formulario se alternan en la misma página.
 */
@ManagedBean
@ViewScoped
public class BeanUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ServicioUsuario servicioUsuario;

    @Getter
    private boolean puedeAdministrar;
    @Getter
    private List<ModeloUsuarioLista> listaUsuarios = new ArrayList<>();
    @Getter
    @Setter
    private List<ModeloUsuarioLista> listaUsuariosFiltro;

    /** Opciones de los checks: descripción → id (así las lee f:selectItems). */
    @Getter
    private Map<String, Long> perfilesDisponibles = new LinkedHashMap<>();
    @Getter
    private Map<String, Long> bodegasDisponibles = new LinkedHashMap<>();

    // ---- formulario ----
    @Getter
    private boolean editando;
    private Long idUsuario;
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private boolean activo = true;
    @Getter
    @Setter
    private boolean esUsuarioBodega;
    @Getter
    @Setter
    private String numeroCaja;
    @Getter
    @Setter
    private String clave;
    @Getter
    @Setter
    private String confirmarClave;
    @Getter
    @Setter
    private List<Long> perfilesSeleccionados = new ArrayList<>();
    @Getter
    @Setter
    private List<Long> bodegasSeleccionadas = new ArrayList<>();

    @PostConstruct
    public void inicializar() {
        try {
            puedeAdministrar = servicioUsuario.sesionEsAdministrador();
            if (puedeAdministrar) {
                perfilesDisponibles = invertir(servicioUsuario.obtenerPerfilesDisponibles());
                bodegasDisponibles = invertir(servicioUsuario.obtenerBodegasDisponibles());
                cargarLista();
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    private static Map<String, Long> invertir(Map<Long, String> origen) {
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Map.Entry<Long, String> e : origen.entrySet()) {
            // si dos descripciones fueran iguales, se distingue con el id para no perder una opción
            String etiqueta = resultado.containsKey(e.getValue()) ? e.getValue() + " (" + e.getKey() + ")" : e.getValue();
            resultado.put(etiqueta, e.getKey());
        }
        return resultado;
    }

    private void cargarLista() {
        this.listaUsuarios = servicioUsuario.listarUsuariosMantenimiento();
        this.listaUsuariosFiltro = null;
    }

    public boolean isNuevo() {
        return idUsuario == null;
    }

    public void nuevoUsuario() {
        limpiarFormulario();
        this.editando = true;
    }

    public void editarUsuario(ModeloUsuarioLista fila) {
        try {
            limpiarFormulario();
            this.idUsuario = fila.getIdUsuario();
            this.login = fila.getLogin();
            this.nombre = fila.getNombre();
            this.activo = fila.isActivo();
            this.esUsuarioBodega = fila.isEsUsuarioBodega();
            this.numeroCaja = fila.getNumeroCaja();
            this.perfilesSeleccionados = servicioUsuario.obtenerIdsPerfilesUsuario(fila.getIdUsuario());
            this.bodegasSeleccionadas = servicioUsuario.obtenerIdsBodegasUsuario(fila.getIdUsuario());
            this.editando = true;
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void cancelar() {
        limpiarFormulario();
        this.editando = false;
    }

    public void guardar() {
        try {
            if (clave != null && !clave.isEmpty() && !clave.equals(confirmarClave)) {
                MensajeUtil.agregarMensajeAdvertencia("La contraseña y su confirmación no coinciden.");
                return;
            }
            String error = servicioUsuario.guardarUsuarioMantenimiento(idUsuario, login, nombre, activo,
                    esUsuarioBodega, numeroCaja, clave, perfilesSeleccionados, bodegasSeleccionadas);
            if (error != null) {
                MensajeUtil.agregarMensajeAdvertencia(error);
                return;
            }
            MensajeUtil.agregarMensajeInfo("Usuario guardado.");
            limpiarFormulario();
            this.editando = false;
            cargarLista();
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    private void limpiarFormulario() {
        this.idUsuario = null;
        this.login = null;
        this.nombre = null;
        this.activo = true;
        this.esUsuarioBodega = false;
        this.numeroCaja = null;
        this.clave = null;
        this.confirmarClave = null;
        this.perfilesSeleccionados = new ArrayList<>();
        this.bodegasSeleccionadas = new ArrayList<>();
    }
}
