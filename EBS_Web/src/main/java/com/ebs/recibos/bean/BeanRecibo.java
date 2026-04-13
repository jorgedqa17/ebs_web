/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.recibos.bean;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.CancelacionFactura;
import com.ebs.constantes.enums.Clase;
import com.ebs.recibos.servicios.ServicioRecibo;
import com.ebs.constantes.enums.EstadoFactura;
import com.ebs.constantes.enums.EstadoRecibo;
import com.ebs.constantes.enums.FacturaEnvioCorreos;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.Roles;
import com.ebs.constantes.enums.TipoCondicionVenta;
import com.ebs.constantes.enums.TipoRecibo;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.constantes.enums.TomarEnCuentaCierre;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.CondicionVenta;
import com.ebs.entidades.Estados;
import com.ebs.entidades.MedioPago;
import com.ebs.entidades.Recibo;
import com.ebs.entidades.TipoFactura;
import com.ebs.entidades.Usuario;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.modelos.ConsultaFacturasModelo;
import com.ebs.modelos.FacturaCredito;
import com.ebs.modelos.ModeloPersona;
import com.ebs.modelos.PersonaModelo;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.seguridad.servicios.ServicioLogin;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Jorge Quesada Arias
 */
@Data
@ManagedBean
@ViewScoped
public class BeanRecibo {

    @Inject
    private ServicioRecibo servicioRecibo;
    @Inject
    private ServicioPersona servicioPersona;
    @Inject
    private ServicioFactura servicioFactura;
    @Inject
    private ServicioReporte servicioReporte;

    private ConsultaFacturasModelo facturaSeleccionada;
    private List<ConsultaFacturasModelo> listaFacturas;
    private List<ConsultaFacturasModelo> listaFacturasFiltro;
    private ModeloPersona clienteSeleccionado;
    private List<PersonaModelo> listaPersonas;
    private List<PersonaModelo> listaPersonasFiltro;
    private String clave;
    private String consecutivo;
    private String nombre;
    private String numeroFactua;
    private Recibo recibo;
    private List<Recibo> listaRecibos;
    private List<Recibo> listaRecibosFiltro;
    private boolean verListaPersonas;
    private boolean verListaFacturas;
    private StreamedContent reporteDesplegar = null;
    private StreamedContent reporteDesplegarEstadoCuenta = null;
    private boolean habilitarControlesNuevoRecibo;
    private boolean bloquearNuevoRecibo;
    private List<MedioPago> listaMedioPago;
    private String numeroConsecutivo;
    private List<FacturaCredito> listaFacturasCredito;
    private List<FacturaCredito> listaFacturasCreditoFiltro;
    private List<ConsultaFacturasModelo> listaFacturasEncontradasAgente;
    private List<ConsultaFacturasModelo> listaFacturasEncontradasAgenteFiltro;
    private boolean verImprimirAgente;
    private boolean inhabilitarBotonImprimirAgente;
    private boolean verInputNumeroReciboManual;
    private BigDecimal montoDeuda;
    private boolean puedeGenerarReciboAutomatico;
    //Busqueda de facturas
    @Getter
    @Setter
    private Date fechaInicio;
    @Getter
    @Setter
    private Date fechaFin;
    @Getter
    @Setter
    private Long idCondicionVentaSeleccionado;
    @Getter
    @Setter
    private List<CondicionVenta> listaCondicionVenta;
    @Getter
    @Setter
    private Long idMedioPagoSeleccionado;
    @Getter
    @Setter
    private Long idTipoFacturaSeleccionada;
    @Getter
    @Setter
    private List<TipoFactura> listaTiposFacturas;
    @Getter
    @Setter
    private String usuarioSeleccionado;
    @Getter
    @Setter
    private List<Usuario> listaUsuarios;
    @Getter
    @Setter
    private Long idBodegaSeleccionada;
    @Getter
    @Setter
    private List<Bodega> listaBodegas;
    @Getter
    @Setter
    private Long idEstadoSeleccionado;
    @Getter
    @Setter
    private List<Estados> listaEstadosFactura;
    @Inject
    private ServicioBodega servicioBodegas;
    @Inject
    private ServicioLogin servicioLogin;

    public BeanRecibo() {
    }

    @PostConstruct
    public void inicializar() {
        //verListaPersonas = true;
        verListaFacturas = false;
        puedeGenerarReciboAutomatico = Utilitario.validarRolUsuario(Roles.PUEDE_CREAR_RECIBOS_AUTOMATICOS);
        this.habilitarControlesNuevoRecibo = true;
        this.bloquearNuevoRecibo = false;
        this.listaMedioPago = servicioFactura.obtenerMediosPago();
        verImprimirAgente = false;
        inhabilitarBotonImprimirAgente = true;
        verInputNumeroReciboManual = false;

        //Busqueda de lista de facturas
        this.listaEstadosFactura = this.servicioFactura.obtenerEstadoPorIdClase(Clase.FACTURA.getIdClase());
        this.listaMedioPago = servicioFactura.obtenerMediosPago();
        this.listaCondicionVenta = servicioFactura.obtenerCondicionesVenta();
        this.listaTiposFacturas = servicioFactura.obtenerTiposFacturas();
        this.listaBodegas = servicioBodegas.obtenerListaBodegas();
        this.listaUsuarios = servicioLogin.obtenerUsuariosSistema();
        this.idCondicionVentaSeleccionado = TipoCondicionVenta.CREDITO.getTipoCondicionVenta();

        fechaInicio = Calendar.getInstance().getTime();
        fechaFin = Calendar.getInstance().getTime();

        inicializarRecibo();
    }

    public void buscarFacturas() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        //Obtengo todas las facturas
        //consultarFacturasPorFechaPorCondVentaPorUsuarioPorMedioPago(Date fechaInicio, Date fechaFinal, Long idCondicionVenta, Long idMedioPago, Long idTipoFactura, Long idBodega) {
        listaFacturas = servicioFactura.consultarFacturasPorFechaPorCondVentaPorUsuarioPorMedioPagoPorBodega(fechaInicio, fechaFin, idCondicionVentaSeleccionado, idMedioPagoSeleccionado, idTipoFacturaSeleccionada, idBodegaSeleccionada, usuarioSeleccionado, this.idEstadoSeleccionado);
    }

    public void seleccionarMedioPago(ValueChangeEvent evt) {
        recibo.setId_medio_pago((Long) evt.getNewValue());
    }

    public void verNumeroReciboManual() {
        if (recibo.isEsUnReciboManual()) {
            verInputNumeroReciboManual = false;
        } else {
            verInputNumeroReciboManual = true;
        }
    }

    public void imprimirRecibo(Recibo reciboSeleccionado) {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idRecibo", reciboSeleccionado.getId_recibo());
            parametros.put("montoLetras", Utilitario.Convertir(reciboSeleccionado.getMonto_pago().toString(), true));
            parametros.put("moneda", "CRC");
            reporte = servicioReporte.generarReporte(Reportes.REPORTE_RECIBOS, TiposMimeTypes.PDF, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_RECIBOS.getNombreReporte() + "-" + reciboSeleccionado.getId_recibo() + "." + TiposMimeTypes.PDF.getExtension()));

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void imprimirEstadoCuenta() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idCliente", clienteSeleccionado.getCliente().getId_cliente());
            parametros.put("idCondicionVenta", TipoCondicionVenta.CREDITO.getTipoCondicionVenta());
            reporte = servicioReporte.generarReporte(Reportes.REPORTE_ESTADO_CUENTA_CLIENTE, TiposMimeTypes.PDF, parametros, true);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegarEstadoCuenta = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_ESTADO_CUENTA_CLIENTE.getNombreReporte() + "-" + clienteSeleccionado.getNumero_cedula() + "." + TiposMimeTypes.PDF.getExtension()));

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void inicializarRecibo() {

        recibo = new Recibo();
        recibo.setMonto_pago(new BigDecimal("0.0"));
        recibo.setConcepto_recibo("");
        recibo.setId_medio_pago(0L);
        recibo.setNumero_referencia("");
        recibo.setEsUnReciboManual(true);
        recibo.setNumero_recibo_manual("");
    }

    public boolean validarBusquedaPersona() {
        boolean resultado = true;

        if (nombre == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.validar.nombre"));
        } else if (nombre.equals("")) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.validar.nombre"));
        }
        return resultado;
    }

    public void buscar() {
        this.clienteSeleccionado = new ModeloPersona();
        this.buscarPersona();

    }

    public boolean validarBusquedaFactura() {
        boolean resultado = true;

        if (this.numeroConsecutivo == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.validacion.numero.factura"));
        } else if (this.numeroConsecutivo.equals("")) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.validacion.numero.factura"));
        }
        return resultado;
    }

    public void buscarFactura() {

        try {
            this.facturaSeleccionada = null;
            inhabilitarBotonImprimirAgente = true;
            bloquearNuevoRecibo = true;
            if (validarBusquedaFactura()) {
                listaFacturasEncontradasAgente = servicioFactura.consultarFacturasPorNumeroConsecutivo(numeroConsecutivo);

                if (listaFacturasEncontradasAgente != null) {
                    if (listaFacturasEncontradasAgente.size() == 1) {
                        this.facturaSeleccionada = listaFacturasEncontradasAgente.get(0);
                        this.numeroConsecutivo = "";
                        if (this.facturaSeleccionada.getFactura().getId_cliente() != null) {
                            this.clienteSeleccionado = ModeloPersona.convertirPersonaAModeloPersona(servicioPersona.obtenerPersonaModeloPorIdCliente(this.facturaSeleccionada.getFactura().getId_cliente()));
                        }
                        verImprimirAgente = true;
                        this.listaFacturasEncontradasAgente = new ArrayList<>();

                        this.numeroConsecutivo = "";
                        if (this.facturaSeleccionada.getFactura().getId_cliente() != null) {
                            this.clienteSeleccionado = ModeloPersona.convertirPersonaAModeloPersona(servicioPersona.obtenerPersonaModeloPorIdCliente(this.facturaSeleccionada.getFactura().getId_cliente()));
                            listaFacturasCredito = this.servicioFactura.obtenerListaFacturaCredito(clienteSeleccionado.getCliente().getId_cliente());
                        }
                        verImprimirAgente = true;
                        this.bloquearNuevoRecibo = false;

                        this.listaFacturasEncontradasAgente = new ArrayList<>();

                        this.facturaSeleccionada = facturaSeleccionada;
                        verListaFacturas = false;
                        this.listaRecibos = servicioRecibo.obtenerRecibosPorFactura(facturaSeleccionada.getIdFactura());

                        this.bloquearNuevoRecibo = !permiteCrearRecibos();

                        MensajeUtil.agregarMensajeAdvertencia("Se ha encontrado una factura relacionada");
                    } else {
                        MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.factura.varias.factura"));
                    }
                } else {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.reciob.facutra.no.encontrada"));
                    this.numeroConsecutivo = "";
                }
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public boolean validarRecibo() {
        boolean resultado = true;

        if (recibo.getMonto_pago() == null) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.monto.nulo.cero"));
            resultado = false;
        } else if (recibo.getMonto_pago().setScale(1, BigDecimal.ROUND_HALF_EVEN).equals(new BigDecimal("0.0"))) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.monto.mayor.ceros"));
            resultado = false;
        } else if (recibo.getMonto_pago().compareTo(this.facturaSeleccionada.getFactura().getTotal_comprobante()) > 0) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.monto.no.mayor.monto.factura.total") + this.facturaSeleccionada.getFactura().getTotal_comprobante());
            resultado = false;
        }
        if (recibo.getConcepto_recibo() == null) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.concepto.recibo"));
            resultado = false;
        } else if (recibo.getConcepto_recibo().equals("")) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.concepto.recibo"));
            resultado = false;
        }
        if (recibo.getNumero_referencia() == null) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.numero.rrefencia.validacon"));
            resultado = false;
        } else if (recibo.getNumero_referencia().equals("")) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.numero.rrefencia.validacon"));
            resultado = false;
        }
        if (recibo.getId_medio_pago() == null) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.medio.pago.validacion"));
            resultado = false;
        } else if (recibo.getId_medio_pago().equals(0L)) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.medio.pago.validacion"));
            resultado = false;
        }
        if (!recibo.isEsUnReciboManual() && (recibo.getNumero_recibo_manual() == null || (recibo.getNumero_recibo_manual().equals("")))) {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.ingresar.numero.recibo.manual"));
            resultado = false;
        }
        return resultado;
    }

    public void habilitarGenerarRecibo() {
        if (facturaSeleccionada != null) {
            habilitarControlesNuevoRecibo = false;
            inhabilitarBotonImprimirAgente = true;
            inicializarRecibo();
        } else {
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.seleccionar.factura"));
        }

    }

    public void generarRecibo() {
        try {
            if (validarRecibo()) {
                recibo.setId_factura(facturaSeleccionada.getIdFactura());
                recibo.setMonto_total_factura(facturaSeleccionada.getMontoTotalComprobante());

                if (facturaSeleccionada.getFactura().getMonto_restante() == null) {
                    recibo.setMonto_restante(facturaSeleccionada.getMontoTotalComprobante().subtract(recibo.getMonto_pago()));
                } else {
                    recibo.setMonto_restante(facturaSeleccionada.getFactura().getMonto_restante().subtract(recibo.getMonto_pago()));
                }
                facturaSeleccionada.getFactura().setMonto_restante(recibo.getMonto_restante());

                if (clienteSeleccionado != null) {
                    recibo.setId_cliente(clienteSeleccionado.getCliente().getId_cliente());
                }
                recibo.setLogin(Utilitario.obtenerUsuario().getLogin());
                recibo.setFecha(new Date());
                recibo.setId_estado(EstadoRecibo.PAGADO.getEstadoRecibo());
                recibo.setEnvio_correo_electronico(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
                recibo.setEs_recibo_manual(!recibo.isEsUnReciboManual() ? TipoRecibo.MANUAL.getTipoRecibo() : TipoRecibo.AUTOMATICO.getTipoRecibo());

                recibo.setInd_tomar_cierre(TomarEnCuentaCierre.TOMAR_EN_CUENTA.getIndicador());

                recibo = servicioRecibo.guardarRecibo(recibo, facturaSeleccionada.getFactura());
                // si el monto restante es 0 o menor que 0
                if (recibo.getMonto_restante().compareTo(BigDecimal.ZERO) <= 0) {
                    if (clienteSeleccionado != null) {
                        //listaFacturas = servicioFactura.consultarFacturasPorIdClienteIdCondicionPago(clienteSeleccionado.getCliente().getId_cliente(), TipoCondicionVenta.CREDITO.getTipoCondicionVenta());
                        listaFacturasCredito = this.servicioFactura.obtenerListaFacturaCredito(clienteSeleccionado.getCliente().getId_cliente());
                        obtenerMontoDeudaCliente();
                    }
                    this.bloquearNuevoRecibo = true;
                    MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.recibo.factura.cancelada"));
                }

                this.listaRecibos = servicioRecibo.obtenerRecibosPorFactura(facturaSeleccionada.getIdFactura());
                this.habilitarControlesNuevoRecibo = true;

                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.recibo.generardo.exito") + " - " + this.recibo.getId_recibo());

                //verImprimirAgente = true;
                inhabilitarBotonImprimirAgente = false;
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void buscarPersona() {
        try {
            verListaPersonas = false;
            if (this.validarBusquedaPersona()) {
                this.listaPersonas = servicioPersona.obtenerTodasPersonasPorNombre(nombre);
                if (this.listaPersonas == null || this.listaPersonas.size() <= 0) {
                    MensajeUtil.agregarMensajeAdvertencia("Cliente no encontrado");
                } else if (this.listaPersonas.size() == 1) {
                    seleccionarPersona(this.listaPersonas.get(0));
                    //listaFacturasCredito = this.servicioFactura.obtenerListaFacturaCredito(clienteSeleccionado.getCliente().getId_cliente());
                } else if (this.listaPersonas.size() > 1) {
                    verListaPersonas = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void obtenerMontoDeudaCliente() {
        montoDeuda = new BigDecimal("0.0");
        for (FacturaCredito facturaCredito : listaFacturasCredito) {
            if (facturaCredito.getEstado_credito().equals(CancelacionFactura.PENDIENTE_PAGO.getFacturaCancelada()) && !facturaCredito.getEstadoFactura().equals("Anulada")) {
                //System.out.println(facturaCredito.getNumeroConsecutivo() + "  -  " + facturaCredito.getMonto_restante());
                montoDeuda = montoDeuda.add(facturaCredito.getMonto_restante() == null ? facturaCredito.getTotal_comprobante() : facturaCredito.getMonto_restante());

            }
        }
    }
//    public void obtenerMontoDeudaCliente() {
//        montoDeuda = new BigDecimal("0.0");
//        for (ConsultaFacturasModelo factura : this.listaFacturas) {
//            if (factura.getFactura().getFactura_cancelada().equals(CancelacionFactura.PENDIENTE_PAGO.getFacturaCancelada())) {
//
//                montoDeuda = montoDeuda.add(factura.getFactura().getMonto_restante() == null ? factura.getFactura().getTotal_comprobante() : factura.getFactura().getMonto_restante());
//
//            }
//        }
//    }

    public void seleccionarFacturaCredito(FacturaCredito factura) {
        try {

            clienteSeleccionado = ModeloPersona.convertirPersonaAModeloPersona(this.servicioPersona.obtenerPersonaModeloPorIdCliente(factura.getIdCliente()));
            nombre = clienteSeleccionado.getNombreCompleto();
            this.seleccionarFacturaControl(this.servicioFactura.consultarFacturaPorNumeroConsecutivo(factura.getNumeroConsecutivo()));
            listaFacturas = servicioFactura.consultarFacturasPorIdClienteIdCondicionPago(clienteSeleccionado.getCliente().getId_cliente(), TipoCondicionVenta.CREDITO.getTipoCondicionVenta());
            obtenerMontoDeudaCliente();

        } catch (Exception ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarPersona(PersonaModelo personaModelo) {
        try {
            clienteSeleccionado = ModeloPersona.convertirPersonaAModeloPersona(personaModelo);
            nombre = clienteSeleccionado.getNombreCompleto();
            //listaFacturas = servicioFactura.consultarFacturasPorIdClienteIdCondicionPago(clienteSeleccionado.getCliente().getId_cliente(), TipoCondicionVenta.CREDITO.getTipoCondicionVenta());
            listaFacturasCredito = this.servicioFactura.obtenerListaFacturaCredito(clienteSeleccionado.getCliente().getId_cliente());
            obtenerMontoDeudaCliente();
            this.verListaPersonas = false;

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public boolean permiteCrearRecibos() {
        boolean resultado = true;
        //si la factura tiene un estado distinto a Arpobada por hacienda, no se permite crear recibos
        if (!this.facturaSeleccionada.getFactura().getEstado_factura().equals(EstadoFactura.APROBADA_HACIENDA.getEstadoFactura())) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.factura.con.estado.errornea"));
        }
        //si la factura es cancelada no se pueden crear más recibos
        if (this.facturaSeleccionada.getFactura().getFactura_cancelada().equals(1)) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.recibo.factura.cancelada"));
        }
        return resultado;
    }

    public void anularRecibo(Recibo reciboSeleccionado) {
        try {
            this.facturaSeleccionada.setFactura(this.servicioRecibo.anularRecibo(reciboSeleccionado));

            MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.recibo.anulacion.recibo") + " - " + reciboSeleccionado.getId_recibo());
            this.listaRecibos = servicioRecibo.obtenerRecibosPorFactura(reciboSeleccionado.getId_factura());

            this.bloquearNuevoRecibo = !permiteCrearRecibos();

            if (clienteSeleccionado != null) {
                listaFacturas = servicioFactura.consultarFacturasPorIdClienteIdCondicionPago(clienteSeleccionado.getCliente().getId_cliente(), TipoCondicionVenta.CREDITO.getTipoCondicionVenta());
                obtenerMontoDeudaCliente();
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarFactura(ConsultaFacturasModelo facturaSeleccionada) {
        try {
            this.facturaSeleccionada = facturaSeleccionada;
            verListaFacturas = false;
            this.listaRecibos = servicioRecibo.obtenerRecibosPorFactura(facturaSeleccionada.getIdFactura());

            this.bloquearNuevoRecibo = !permiteCrearRecibos();
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarFacturaControl(ConsultaFacturasModelo facturaSeleccionada) {
        try {
            this.facturaSeleccionada = facturaSeleccionada;
            if (this.facturaSeleccionada.getFactura() == null) {
                this.facturaSeleccionada.setFactura(this.servicioFactura.obtenerFacturaBusqueda(facturaSeleccionada.getIdFactura()));
            }
            this.numeroConsecutivo = "";
            if (this.facturaSeleccionada.getFactura().getId_cliente() != null) {
                this.clienteSeleccionado = ModeloPersona.convertirPersonaAModeloPersona(servicioPersona.obtenerPersonaModeloPorIdCliente(this.facturaSeleccionada.getFactura().getId_cliente()));
            }
            verImprimirAgente = true;
            this.bloquearNuevoRecibo = false;

            this.listaFacturasEncontradasAgente = new ArrayList<>();

            this.facturaSeleccionada = facturaSeleccionada;
            verListaFacturas = false;
            this.listaRecibos = servicioRecibo.obtenerRecibosPorFactura(facturaSeleccionada.getIdFactura());

            this.bloquearNuevoRecibo = !permiteCrearRecibos();
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarFacturaAgente(ConsultaFacturasModelo facturaSeleccionada) {
        try {
            this.facturaSeleccionada = facturaSeleccionada;
            this.numeroConsecutivo = "";
            if (this.facturaSeleccionada.getFactura().getId_cliente() != null) {
                this.clienteSeleccionado = ModeloPersona.convertirPersonaAModeloPersona(servicioPersona.obtenerPersonaModeloPorIdCliente(this.facturaSeleccionada.getFactura().getId_cliente()));
            }
            verImprimirAgente = true;
            this.bloquearNuevoRecibo = false;

            this.listaFacturasEncontradasAgente = new ArrayList<>();
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }
}
