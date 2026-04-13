/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.personas.bean;

import com.ebs.constantes.enums.ClienteEstado;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.constantes.enums.TiposCedulaMascaras;
import com.ebs.entidades.ActividadEconomica;
import com.ebs.entidades.Barrio;
import com.ebs.entidades.Canton;
import com.ebs.entidades.Cliente;
import com.ebs.entidades.Distrito;
import com.ebs.entidades.Parametro;
import com.ebs.entidades.Persona;
import com.ebs.entidades.PersonaCorreo;
import com.ebs.entidades.PersonaPK;
import com.ebs.entidades.Provincia;
import com.ebs.entidades.TipoCliente;
import com.ebs.entidades.TipoExoneracion;
import com.ebs.entidades.TipoIdentificacion;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.Actividades;
import com.ebs.modelos.CorreoElectronicoPersonaHacienda;
import com.ebs.modelos.ExoneracionUsuario;
import com.ebs.modelos.InfoExoneracion;
import com.ebs.modelos.ModeloPersonaTSE;
import com.ebs.modelos.PersonaHacienda;
import com.ebs.modelos.PersonaModelo;
import com.ebs.parametros.servicios.ServicioParametro;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.ubicacion.servicios.ServicioUbicaciones;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class BeanPersona {

    @Getter
    @Setter
    private List<TipoIdentificacion> listaTiposCedulas;
    @Inject
    ServicioPersona servicioPersona;
    @Getter
    @Setter
    private ExoneracionUsuario exoneracionUsuario;

    @Getter
    @Setter
    private Long codigoExoneracion;

    @Getter
    @Setter
    private List<ExoneracionUsuario> listaExoneraciones;

    @Getter
    @Setter
    private List<TipoExoneracion> listaTipoExoneracion;
    @Getter
    @Setter
    private List<ActividadEconomica> listaTiposActividadesEconomicas;
    @Getter
    @Setter
    private Long tipoIdentificacionSeleccionadaReceptor;
    @Getter
    @Setter
    private Long idProvinciaSeleccionadaReceptor;

    @Getter
    @Setter
    private Long idTipoClienteSeleccionado;
    @Getter
    @Setter
    private Long idCantonSeleccionadaReceptor;
    @Getter
    @Setter
    private Long idDistritoSeleccionadaReceptor;
    @Getter
    @Setter
    private List<Provincia> listaProvincias;
    @Getter
    @Setter
    private List<PersonaModelo> listaPersonas;
    @Getter
    @Setter
    private List<PersonaModelo> listaPersonasSeleccionadas;
    @Getter
    @Setter
    private List<TipoCliente> listaTiposClientes;
    @Getter
    @Setter
    private List<Canton> listaCantones;
    @Getter
    @Setter
    private List<Distrito> listaDistritos;
    @Getter
    @Setter
    private List<Barrio> listaBarrios;
    @Inject
    private ServicioUbicaciones servicioUbicaciones;
    @Inject
    private ServicioProducto servicioProducto;
    @Getter
    @Setter
    private Long idBarioSeleccionadaReceptor;
    @Getter
    @Setter
    private Long idActvidadSeleccionada;
    @Getter
    @Setter
    private String mascaraSeleccionada;
    @Getter
    @Setter
    private Integer tamannoCedula;
    @Getter
    @Setter
    private Persona persona;
    @Getter
    @Setter
    private String identificacion;
    @Getter
    @Setter
    private boolean esPersonaJuridica = false;

    @Getter
    @Setter
    private boolean estaModificando = false;
    @Getter
    @Setter
    private boolean inhabilitaBotones;

    @Getter
    @Setter
    private boolean requiereTransporte = false;

    @Getter
    @Setter
    private boolean terminoProcesoCorrectamente = false;

    @Getter
    @Setter
    private Cliente clienteSeleccionado;
    @Getter
    @Setter
    private List<PersonaCorreo> listaCorreosPersona;

    @Getter
    @Setter
    private String correoElectronico;

    @Getter
    @Setter
    private String alias;

    @Inject
    private ServicioParametro servicioParametro;

    public BeanPersona() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            this.inhabilitaBotones = true;
            idProvinciaSeleccionadaReceptor = 0L;
            idCantonSeleccionadaReceptor = 0L;
            idDistritoSeleccionadaReceptor = 0L;
            tipoIdentificacionSeleccionadaReceptor = 0l;
            idTipoClienteSeleccionado = 0l;
            identificacion = "";
            idTipoClienteSeleccionado = 0L;
            tipoIdentificacionSeleccionadaReceptor = 0l;
            this.listaExoneraciones = new ArrayList<>();
            this.persona = new Persona();
            listaPersonas = servicioPersona.obtenerTodasPersonasMantenimiento();
            exoneracionUsuario = ExoneracionUsuario.builder()
                    .tipoDocumento(ExoneracionUsuario.TipoDocumento.builder().codigo(0L).build())
                    .build();

            this.listaTiposActividadesEconomicas = this.servicioPersona.obtenerListaCodigosActividad();
            //Obtengo la lista tipos de cédulas
            this.listaTiposCedulas = servicioPersona.obtenerListaTiposIdentificacion();
            //Cargo las provincias
            this.listaProvincias = servicioUbicaciones.obtenerProvincias();
            //Cargo los cantones
            this.listaCantones = servicioUbicaciones.obtenerCantones(idProvinciaSeleccionadaReceptor);
            //Cargo los distritos
            this.listaDistritos = servicioUbicaciones.obtenerDistritos(idCantonSeleccionadaReceptor);
            //Cargo los barrios 
            this.listaBarrios = servicioUbicaciones.obtenerBarrios(idDistritoSeleccionadaReceptor);

            this.listaTipoExoneracion = servicioProducto.obtenerTodosTiposExoneraciones();

            this.listaTiposClientes = servicioPersona.obtenerListaTiposCliente();
            mascaraSeleccionada = TiposCedulaMascaras.CEDULA_FISICA.getMascara();
            tamannoCedula = TiposCedulaMascaras.CEDULA_FISICA.getTamannoTipoCedula();
            requiereTransporte = false;
            terminoProcesoCorrectamente = false;
            this.estaModificando = false;

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void inactivarCliente(PersonaModelo persona) {
        try {
            persona.getPersona().setEs_activo(ClienteEstado.INACTIVO.getEstado());
            servicioPersona.modificarEstadoCliente(persona.getPersona());
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.persona.correcto.estado.cambio"));
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void activarCliente(PersonaModelo persona) {
        try {
            persona.getPersona().setEs_activo(ClienteEstado.ACTIVO.getEstado());
            servicioPersona.modificarEstadoCliente(persona.getPersona());
            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.persona.correcto.estado.cambio"));
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void inicializarCancelar() {
        try {
            this.inhabilitaBotones = true;
            idProvinciaSeleccionadaReceptor = 0L;
            idCantonSeleccionadaReceptor = 0L;
            idDistritoSeleccionadaReceptor = 0L;
            tipoIdentificacionSeleccionadaReceptor = 0l;
            idTipoClienteSeleccionado = 0l;
            identificacion = "";
            idTipoClienteSeleccionado = 0L;
            tipoIdentificacionSeleccionadaReceptor = 0l;
            this.persona = new Persona();
            exoneracionUsuario = ExoneracionUsuario.builder()
                    .tipoDocumento(ExoneracionUsuario.TipoDocumento.builder().codigo(0L).build())
                    .build();

            //Obtengo la lista tipos de cédulas
            this.listaTiposCedulas = servicioPersona.obtenerListaTiposIdentificacion();
            //Cargo las provincias
            this.listaProvincias = servicioUbicaciones.obtenerProvincias();
            //Cargo los cantones
            this.listaCantones = servicioUbicaciones.obtenerCantones(idProvinciaSeleccionadaReceptor);
            //Cargo los distritos
            this.listaDistritos = servicioUbicaciones.obtenerDistritos(idCantonSeleccionadaReceptor);
            //Cargo los barrios 
            this.listaBarrios = servicioUbicaciones.obtenerBarrios(idDistritoSeleccionadaReceptor);

            this.listaTiposClientes = servicioPersona.obtenerListaTiposCliente();
            mascaraSeleccionada = TiposCedulaMascaras.CEDULA_FISICA.getMascara();
            tamannoCedula = TiposCedulaMascaras.CEDULA_FISICA.getTamannoTipoCedula();
            requiereTransporte = false;
            terminoProcesoCorrectamente = false;

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void editarSeleccionarPersona(PersonaModelo personaSeleccionada) {
        try {
            inhabilitaBotones = true;
            this.exoneracionUsuario = ExoneracionUsuario.builder()
                    .tipoDocumento(ExoneracionUsuario.TipoDocumento.builder().codigo(0L).build())
                    .build();
            this.codigoExoneracion = 0L;
            this.listaExoneraciones = Utilitario.convertirALista(this.persona.getExoneraciones(), ExoneracionUsuario.class);

            this.persona = personaSeleccionada.getPersona();
            this.persona.setCorreo_electronico(personaSeleccionada.getPersona().getCorreo_electronico() == null ? null : personaSeleccionada.getPersona().getCorreo_electronico().trim());
            this.clienteSeleccionado = personaSeleccionada.getCliente();

            idProvinciaSeleccionadaReceptor = this.persona.getId_provincia() == null ? 0l : this.persona.getId_provincia();
            idCantonSeleccionadaReceptor = this.persona.getId_canton() == null ? 0l : this.persona.getId_canton();
            idDistritoSeleccionadaReceptor = this.persona.getId_distrito() == null ? 0l : this.persona.getId_distrito();
            idBarioSeleccionadaReceptor = this.persona.getId_barrio() == null ? 0l : this.persona.getId_barrio();
            tipoIdentificacionSeleccionadaReceptor = this.persona.getPersonaPK().getId_tipo_cedula();
            idTipoClienteSeleccionado = personaSeleccionada.getCliente().getId_tipo_cliente();
            identificacion = this.persona.getPersonaPK().getNumero_cedula();
            requiereTransporte = !personaSeleccionada.getCliente().getInd_requiere_transporte().equals(0);
            idActvidadSeleccionada = personaSeleccionada.getPersona().getIdCodigoActividad();
            //Cargo los cantones
            this.listaCantones = servicioUbicaciones.obtenerCantones(idProvinciaSeleccionadaReceptor);
            //Cargo los distritos
            this.listaDistritos = servicioUbicaciones.obtenerDistritos(idCantonSeleccionadaReceptor);
            //Cargo los barrios 
            this.listaBarrios = servicioUbicaciones.obtenerBarrios(idDistritoSeleccionadaReceptor);
            this.persona.setListaCorreosPersona(servicioPersona.obtenerListaCorreosPersona(this.persona.getPersonaPK().getNumero_cedula(), this.persona.getPersonaPK().getId_tipo_cedula()));
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void eliminarCorreoElectronico(PersonaCorreo correo) {
        this.persona.getListaCorreosPersona().remove(correo);
    }

    public void seleccionarTipoCedula(ValueChangeEvent evt) {

        esPersonaJuridica = false;
        tipoIdentificacionSeleccionadaReceptor = Long.parseLong(evt.getNewValue() == null ? "0" : evt.getNewValue().toString());

        if (tipoIdentificacionSeleccionadaReceptor.equals(TiposCedulaMascaras.CEDULA_FISICA.getIdTipoCedula())) {
            mascaraSeleccionada = TiposCedulaMascaras.CEDULA_FISICA.getMascara();
            tamannoCedula = TiposCedulaMascaras.CEDULA_FISICA.getTamannoTipoCedula();
            esPersonaJuridica = true;
        }
        if (tipoIdentificacionSeleccionadaReceptor.equals(TiposCedulaMascaras.CEDULA_JURIDICA.getIdTipoCedula())) {
            mascaraSeleccionada = TiposCedulaMascaras.CEDULA_JURIDICA.getMascara();
            tamannoCedula = TiposCedulaMascaras.CEDULA_JURIDICA.getTamannoTipoCedula();
        }
        if (tipoIdentificacionSeleccionadaReceptor.equals(TiposCedulaMascaras.DIMEX.getIdTipoCedula())) {
            mascaraSeleccionada = TiposCedulaMascaras.DIMEX.getMascara();
            tamannoCedula = TiposCedulaMascaras.DIMEX.getTamannoTipoCedula();
        }
        if (tipoIdentificacionSeleccionadaReceptor.equals(TiposCedulaMascaras.NITE.getIdTipoCedula())) {
            mascaraSeleccionada = TiposCedulaMascaras.NITE.getMascara();
            tamannoCedula = TiposCedulaMascaras.NITE.getTamannoTipoCedula();
        }
    }

    public void seleccionarProvincias(ValueChangeEvent evt) {
        idProvinciaSeleccionadaReceptor = Long.parseLong(evt.getNewValue() == null ? "0" : evt.getNewValue().toString());
        listaCantones = servicioUbicaciones.obtenerCantones(idProvinciaSeleccionadaReceptor);
    }

    public void seleccionarTiposClientes(ValueChangeEvent evt) {
        idTipoClienteSeleccionado = Long.parseLong(evt.getNewValue() == null ? "0" : evt.getNewValue().toString());
    }

    public void seleccionarCantones(ValueChangeEvent evt) {
        idCantonSeleccionadaReceptor = Long.parseLong(evt.getNewValue() == null ? "0" : evt.getNewValue().toString());
        listaDistritos = servicioUbicaciones.obtenerDistritos(idCantonSeleccionadaReceptor);
    }

    public void seleccionarDistritos(ValueChangeEvent evt) {
        idDistritoSeleccionadaReceptor = Long.parseLong(evt.getNewValue() == null ? "0" : evt.getNewValue().toString());
        listaBarrios = servicioUbicaciones.obtenerBarrios(idDistritoSeleccionadaReceptor);
    }

    public void seleccionarBarrios(ValueChangeEvent evt) {
        idBarioSeleccionadaReceptor = Long.parseLong(evt.getNewValue() == null ? "0" : evt.getNewValue().toString());

    }

    public void seleccionarActividad(ValueChangeEvent evt) {
        idActvidadSeleccionada = Long.parseLong(evt.getNewValue() == null ? "0" : evt.getNewValue().toString());

    }

    public void crearNuevaPersona() {
        inicializarCancelar();
        estaModificando = false;
        inhabilitaBotones = false;

    }

    public void editarPersona() {
        estaModificando = true;
        inhabilitaBotones = false;

    }

    public boolean validarCliente() {
        boolean resultado = true;

        if (idProvinciaSeleccionadaReceptor != null) {
            if (!idProvinciaSeleccionadaReceptor.equals(Long.parseLong("0"))) {
//                resultado = false;
//                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.vdaliacion.ubicaciones"));
//                return resultado;
                if (idCantonSeleccionadaReceptor == null || idCantonSeleccionadaReceptor.equals(Long.parseLong("0"))) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.vdaliacion.ubicaciones"));
                    return resultado;
                }
                if (idDistritoSeleccionadaReceptor == null || idDistritoSeleccionadaReceptor.equals(Long.parseLong("0"))) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.vdaliacion.ubicaciones"));
                    return resultado;
                }
                if (idBarioSeleccionadaReceptor == null || idBarioSeleccionadaReceptor.equals(Long.parseLong("0"))) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.vdaliacion.ubicaciones"));
                    return resultado;
                }
                if (idActvidadSeleccionada == null || idActvidadSeleccionada.equals(0L)) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.vdaliacion.CODIGO.ACTIVIDAD"));
                    return resultado;
                }
                if (this.persona.getDireccion() == null) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia("Debe ingresar la dirección de domicilio");
                    return resultado;
                } else if (this.persona.getDireccion().equals("")) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia("Debe ingresar la dirección de domicilio");
                    return resultado;
                } else if (this.persona.getDireccion().length() < 5) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia("Debe ingresar más de 5 caracteres en la dirección de domicilio");
                    return resultado;
                }
            }

        }

        if (this.persona.getListaCorreosPersona() == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia("Debe ingresar al menos un alias y un correo electrónico");
            return resultado;
        } else if (this.persona.getListaCorreosPersona().isEmpty()) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia("Debe ingresar al menos un alias y un correo electrónico");
            return resultado;
        }

        if (tipoIdentificacionSeleccionadaReceptor == null || tipoIdentificacionSeleccionadaReceptor.equals(Long.parseLong("0"))) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.vdaliacion.tipos.clietne.cedula"));
            return resultado;
        }
        if (idTipoClienteSeleccionado == null || idTipoClienteSeleccionado.equals(Long.parseLong("0"))) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.vdaliacion.tipos.clietne.cedula"));
            return resultado;
        }
        if (identificacion == null || identificacion.equals("")) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.ingreos.cedula"));
            return resultado;
        } else {
            if (!this.estaModificando) {
                if (servicioPersona.existePersona(identificacion)) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.persona.cedula"));
                    return resultado;
                }
            }

        }

        if (this.persona.getNombre() == null || this.persona.getNombre().equals("")) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.nombre"));
            return resultado;
        }
//        if (this.persona.getNombre_fantasia() == null || this.persona.getNombre_fantasia().equals("")) {
//            resultado = false;
//            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.nombre.fantasia"));
//            return resultado;
//        }

//        if (tipoIdentificacionSeleccionadaReceptor.equals(TiposCedulaMascaras.CEDULA_FISICA.getIdTipoCedula())) {
//            if (this.persona.getPrimer_apellido() == null || this.persona.getPrimer_apellido().equals("")) {
//                resultado = false;
//                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.nombre"));
//                return resultado;
//            }
//            if (this.persona.getSegundo_apellido() == null || this.persona.getSegundo_apellido().equals("")) {
//                resultado = false;
//                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.nombre"));
//                return resultado;
//            }
//        }

        /*if (this.persona.getCorreo_electronico() == null || this.persona.getCorreo_electronico().equals("")) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.correo.electrónico"));
            return resultado;
        }*/
        if (this.persona.getTelefono_1() != null) {
            if (!this.persona.getTelefono_1().equals("")) {
                if (this.persona.getCodigo_pais() == null || this.persona.getCodigo_pais().equals("")) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.codigo.pais"));
                    return resultado;
                }
            }

        }

        if (this.persona.getTelefono_2() != null) {
            if (!this.persona.getTelefono_2().equals("")) {
                if (this.persona.getCodigo_pais() == null || this.persona.getCodigo_pais().equals("")) {
                    resultado = false;
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.codigo.pais"));
                    return resultado;
                }
            }

        }

        return resultado;
    }

    public void buscarActividades() {
        List<String> listaPersonasSinActividad = new ArrayList<>();
        Utilitario utilitarioBusqueda = new Utilitario();
        PersonaHacienda personaEncontrada = null;
        Parametro endPointConsultaPersonaHacienda = servicioParametro.obtenerValorParametro(ParametrosEnum.END_POINT_CONSULTA_PERSONA_HACIENDA.getIdParametro());

        for (PersonaModelo personas : listaPersonas) {
            if (personas.getPersona().getIdCodigoActividad() == null) {
                personaEncontrada = utilitarioBusqueda.obtenerPersonaHacienda(personas.getPersona().getPersonaPK().getNumero_cedula(), endPointConsultaPersonaHacienda.getValor());
                if (personaEncontrada != null) {
                    persona.setNombre(personaEncontrada.getNombre());
                    alias = persona.getNombre();
                    this.esPersonaJuridica = false;

                    if (personaEncontrada.getActividades() == null || personaEncontrada.getActividades().isEmpty()) {
                        listaPersonasSinActividad.add(personas.getPersona().getPersonaPK().getNumero_cedula());
                    } else {
                        for (Actividades actividad : personaEncontrada.getActividades()) {
                            if (actividad.getEstado().equals("A")) {
                                Persona persona = this.servicioPersona.obtenerPersonaPorIdentificacion(personas.getPersona().getPersonaPK().getNumero_cedula(), personas.getPersona().getPersonaPK().getId_tipo_cedula());

                                ActividadEconomica tipo = this.listaTiposActividadesEconomicas.stream()
                                        .filter(e -> e.getCodigoActividadCIIU3().equals(actividad.getCodigo())
                                        || e.getCodigoActividadCIIU4().equals(actividad.getCodigo()))
                                        .findFirst()
                                        .orElse(null);
                                if (tipo != null) {
                                    persona.setIdCodigoActividad(tipo.getIdCodigoActividad());
                                    this.servicioPersona.actualizar(persona);
                                }

                                break;
                            }
                        }
                    }
                } else {
                    listaPersonasSinActividad.add(personas.getPersona().getPersonaPK().getNumero_cedula());
                }
            }
        }
    }

    public void buscarExoneracion() {
        if (this.exoneracionUsuario.getNumeroDocumento() == null || this.exoneracionUsuario.getNumeroDocumento().equals("")) {
            return;
        }
        Parametro endPointExoneracionHacienda = servicioParametro.obtenerValorParametro(ParametrosEnum.API_URL_EXONERACION.getIdParametro());
        Utilitario util = new Utilitario();
        this.exoneracionUsuario = util.obtenerExoneracionDetalle(endPointExoneracionHacienda, this.exoneracionUsuario.getNumeroDocumento());
        this.codigoExoneracion = this.exoneracionUsuario == null ? 0L : this.exoneracionUsuario.getTipoDocumento().getCodigo();
    }

    public boolean validarExoneracion() {
        if (this.exoneracionUsuario.getNumeroDocumento() == null || this.exoneracionUsuario.getNumeroDocumento().equals("")) {
            MensajeUtil.agregarMensajeAdvertencia("Debe ingresar el número de documento");
            return false;
        }
        if (this.codigoExoneracion == null || this.codigoExoneracion.equals(0L)) {
            MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar el tipo de exoneración");
            return false;
        }
        if (this.exoneracionUsuario.getCodigoInstitucion() == null || this.exoneracionUsuario.getCodigoInstitucion().equals("")) {
            MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar la institución");
            return false;
        }
        if (this.exoneracionUsuario.getFechaEmision() == null || this.exoneracionUsuario.getFechaEmision().equals("")) {
            MensajeUtil.agregarMensajeAdvertencia("Debe ingresar la fecha de emisión");
            return false;
        }
        if (this.exoneracionUsuario.getArticulo() == null || this.exoneracionUsuario.getArticulo().equals("")) {
            MensajeUtil.agregarMensajeAdvertencia("Debe ingresar el articulo");
            return false;
        }
        if (this.exoneracionUsuario.getInciso() == null || this.exoneracionUsuario.getInciso().equals("")) {
            MensajeUtil.agregarMensajeAdvertencia("Debe ingresar el inciso");
            return false;
        }
        return true;
    }

    public void agregarExoneracionLinea() {
        if (!validarExoneracion()) {
            return;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        this.listaExoneraciones.add(this.exoneracionUsuario);
        this.exoneracionUsuario = ExoneracionUsuario.builder()
                .tipoDocumento(ExoneracionUsuario.TipoDocumento.builder().codigo(0L).build())
                .build();
        this.codigoExoneracion = 0L;
        MensajeUtil.agregarMensajeInfo("Exoneración agregada correctamente");
        context.execute("PF('idDialogoExoneracion').hide()");
        context.execute("PF('idDialogoExoneracion2').hide()");
    }

    public void agregarExoneracionFactura() {
        RequestContext context = RequestContext.getCurrentInstance();
        this.exoneracionUsuario = ExoneracionUsuario.builder()
                .tipoDocumento(ExoneracionUsuario.TipoDocumento.builder().codigo(0L).build())
                .build();
        this.codigoExoneracion = 0L;
        context.execute("PF('idDialogoExoneracion2').show()");
    }

    public void agregarExoneracion() {
        RequestContext context = RequestContext.getCurrentInstance();
        this.exoneracionUsuario = ExoneracionUsuario.builder()
                .tipoDocumento(ExoneracionUsuario.TipoDocumento.builder().codigo(0L).build())
                .build();
        this.codigoExoneracion = 0L;
        context.execute("PF('idDialogoExoneracion').show()");
    }

    public void eliminarExoneracion(ExoneracionUsuario exo) {
        this.listaExoneraciones.remove(exo);
    }

    public void buscarPersonaTSE() {
        Utilitario utilitarioBusqueda = new Utilitario();
        PersonaHacienda personaEncontrada = null;
        CorreoElectronicoPersonaHacienda correoElectronicoPersona = null;
        Parametro endPointConsultaPersonaHacienda = servicioParametro.obtenerValorParametro(ParametrosEnum.END_POINT_CONSULTA_PERSONA_HACIENDA.getIdParametro());
        personaEncontrada = utilitarioBusqueda.obtenerPersonaHacienda(identificacion, endPointConsultaPersonaHacienda.getValor());
        correoElectronicoPersona = utilitarioBusqueda.obtenerCorreoElectronicoPersonaHacienda(identificacion, servicioParametro.obtenerValorParametro(ParametrosEnum.LINK_OBTENCION_CORREO_ELECTRONICO.getIdParametro()).getValor());

        if (personaEncontrada != null) {
            persona.setNombre(personaEncontrada.getNombre());
            alias = persona.getNombre();
//            String[] listaNombres = personaEncontrada.getNombre().split(" ");
//            if (listaNombres.length == 4) {
//                persona.setNombre(listaNombres[0] + " " + listaNombres[1]);
//                persona.setNombre(listaNombres[0] + " " + listaNombres[1]);
//                persona.setPrimer_apellido(listaNombres[0]);
//                persona.setSegundo_apellido("");
//            }
            this.esPersonaJuridica = false;
        } else {
            if (tipoIdentificacionSeleccionadaReceptor.equals(TiposCedulaMascaras.CEDULA_FISICA.getIdTipoCedula())) {

                ModeloPersonaTSE personaTSE = servicioPersona.obtenerPersonaTSE(identificacion);
                if (personaTSE != null) {
                    persona = personaTSE.obtenerPersona(this.persona);
                    alias = persona.getNombreCompleto();
                    this.esPersonaJuridica = true;
                } else {
                    this.esPersonaJuridica = false;
                }
            } else {

                persona.setNombre("");
                persona.setPrimer_apellido("");
                persona.setSegundo_apellido("");
            }
        }

        if (correoElectronicoPersona != null && correoElectronicoPersona.getEstado().equals("200")) {

            correoElectronicoPersona.getResultado().getCorreos().forEach(elemento -> {
                correoElectronico = elemento.getCorreo();
                alias = persona.getNombreCompleto();
                this.persona.getListaCorreosPersona().add(new PersonaCorreo(this.identificacion, this.tipoIdentificacionSeleccionadaReceptor, this.correoElectronico, this.alias));
                this.correoElectronico = "";

            });

            alias = persona.getNombreCompleto();
        }
    }

    public void guardarPersonaMantenimiento() {
        try {
            if (validarCliente()) {

                this.persona.setId_barrio(idBarioSeleccionadaReceptor);
                this.persona.setId_canton(idCantonSeleccionadaReceptor);
                this.persona.setId_distrito(idDistritoSeleccionadaReceptor);
                this.persona.setId_provincia(idProvinciaSeleccionadaReceptor);
                this.persona.setEs_activo(ClienteEstado.ACTIVO.getEstado());
                this.persona.setEs_exento(this.persona.isEsExento() ? 1 : 0);
                this.persona.setEs_exonerado(this.persona.isEsExonerado() ? 1 : 0);
                this.persona.setIdCodigoActividad(this.idActvidadSeleccionada);
                this.persona.setExoneraciones(Utilitario.convertirAJson(this.exoneracionUsuario));

                if (estaModificando) {
                    clienteSeleccionado.setInd_requiere_transporte(requiereTransporte ? 1 : 0);
                    clienteSeleccionado.setId_tipo_cliente(idTipoClienteSeleccionado);
                    this.servicioPersona.actualizazr(persona, clienteSeleccionado);
                } else {

                    PersonaPK personaPK = new PersonaPK(identificacion, tipoIdentificacionSeleccionadaReceptor);
                    this.persona.setPersonaPK(personaPK);
                    Cliente cliente = new Cliente();
                    cliente.setInd_requiere_transporte(requiereTransporte ? 1 : 0);
                    cliente.setId_tipo_cliente(idTipoClienteSeleccionado);
                    cliente.setNumero_cedula(personaPK.getNumero_cedula());

                    this.servicioPersona.guardarPersona(persona, cliente);

                }
                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.persona.guardad.correctamente"));
                this.inicializar();
                terminoProcesoCorrectamente = true;
                this.inhabilitaBotones = true;
                this.estaModificando = false;
                esPersonaJuridica = true;
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void guardarPersona() {
        try {
            if (validarCliente()) {
                this.persona.setId_barrio(idBarioSeleccionadaReceptor.equals(0L) ? null : this.idBarioSeleccionadaReceptor);
                this.persona.setId_canton(idCantonSeleccionadaReceptor.equals(0L) ? null : this.idCantonSeleccionadaReceptor);
                this.persona.setId_distrito(idDistritoSeleccionadaReceptor.equals(0L) ? null : this.idDistritoSeleccionadaReceptor);
                this.persona.setId_provincia(idProvinciaSeleccionadaReceptor.equals(0L) ? null : this.idProvinciaSeleccionadaReceptor);
                this.persona.setEs_activo(ClienteEstado.ACTIVO.getEstado());
                this.persona.setEs_exento(this.persona.isEsExento() ? 1 : 0);
                this.persona.setEs_exonerado(this.persona.isEsExonerado() ? 1 : 0);
                this.persona.setIdCodigoActividad(this.idActvidadSeleccionada);
                this.persona.setExoneraciones(Utilitario.convertirAJson(this.exoneracionUsuario));

                PersonaPK personaPK = new PersonaPK(identificacion, tipoIdentificacionSeleccionadaReceptor);
                this.persona.setPersonaPK(personaPK);
                Cliente cliente = new Cliente();
                cliente.setInd_requiere_transporte(requiereTransporte ? 1 : 0);
                cliente.setId_tipo_cliente(idTipoClienteSeleccionado);
                cliente.setNumero_cedula(personaPK.getNumero_cedula());

                this.servicioPersona.guardarPersona(persona, cliente);
                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.persona.guardad.correctamente"));
                terminoProcesoCorrectamente = true;
                esPersonaJuridica = true;

                //RequestContext context = RequestContext.getCurrentInstance();
                //context.execute("PF('dialogcliente').show()");
            } else {
                //RequestContext context = RequestContext.getCurrentInstance();
                //context.execute("PF('dialogcliente').show()");
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void agregarCorreoElectronico() {
        if ((this.alias != null && !this.alias.isEmpty()) && (this.correoElectronico != null && !this.correoElectronico.isEmpty())) {
            if (!this.persona.getListaCorreosPersona().stream().filter(predicate -> predicate.getCorreo_electronico().equals(this.correoElectronico) || predicate.getAlias_cliente().equals(this.alias)).findAny().isPresent()) {
                this.persona.getListaCorreosPersona().add(new PersonaCorreo(this.identificacion, this.tipoIdentificacionSeleccionadaReceptor, this.correoElectronico, this.alias));
                this.correoElectronico = "";
                alias = persona.getNombreCompleto();
            } else {
                MensajeUtil.agregarMensajeAdvertencia("El correo electrónico y/o el alias ingresado ya existe, ingrese uno distinto");
            }
        } else {
            MensajeUtil.agregarMensajeAdvertencia("Debe ingresar todos los valores");
        }

    }

    public void marcarPersonaExenta() {
        this.persona.setEs_exento(this.persona.isEsExento() ? 1 : 0);
        this.persona.setEs_exonerado(this.persona.isEsExonerado() ? 0 : 0);
    }

    public void marcarPersonaExonerada() {
        this.persona.setEs_exento(this.persona.isEsExonerado() ? 0 : 0);
        this.persona.setEs_exonerado(this.persona.isEsExonerado() ? 1 : 0);
    }
}
