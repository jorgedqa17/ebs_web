/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.bean;

import com.ebs.constantes.enums.Indicadores;
import com.google.zxing.WriterException;
import com.ebs.constantes.enums.CancelacionFactura;
import com.ebs.constantes.enums.EstadoFactura;
import com.ebs.constantes.enums.EstadosLineasFactura;
import com.ebs.constantes.enums.FacturaEnvioCorreos;
import com.ebs.constantes.enums.LineaDetalleEstado;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.SituacionComprobante;
import com.ebs.constantes.enums.TipoCliente;
import com.ebs.constantes.enums.TipoCondicionVenta;
import com.ebs.constantes.enums.TipoExoneracionEnum;
import com.ebs.constantes.enums.TipoFacturaEnum;
import com.ebs.constantes.enums.TipoProducto;
import com.ebs.constantes.enums.TipoTarifaTimpuestoEnum;
import com.ebs.constantes.enums.TipoUnidadMedida;
import com.ebs.constantes.enums.TiposCedulaMascaras;
import com.ebs.constantes.enums.TiposMediosPago;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.constantes.enums.TiposPrecios;
import com.ebs.entidades.ActividadEconomica;
import com.ebs.entidades.Barrio;
import com.ebs.entidades.Canton;
import com.ebs.entidades.Cliente;
import com.ebs.entidades.CondicionVenta;
import com.ebs.entidades.DetalleFactura;
import com.ebs.entidades.DetalleFacturaPK;
import com.ebs.entidades.Distrito;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.ModeloPersona;
import com.ebs.entidades.Factura;
import com.ebs.entidades.FacturaCorreo;
import com.ebs.entidades.FacturaHistoricoHacienda;
import com.ebs.entidades.Inventario;
import com.ebs.entidades.MedioPago;
import com.ebs.entidades.Parametro;
import com.ebs.entidades.Persona;
import com.ebs.entidades.MotivoDebito;
import com.ebs.entidades.PersonaCorreo;
import com.ebs.entidades.Producto;
import com.ebs.entidades.Provincia;
import com.ebs.entidades.TipoDocumentoReferencia;
import com.ebs.entidades.TipoExoneracion;
import com.ebs.entidades.TipoFactura;
import com.ebs.entidades.TipoIdentificacion;
import com.ebs.entidades.TipoMoneda;
import com.ebs.entidades.TipoPrecio;
import com.ebs.entidades.TipoTarifaImpuesto;
import com.ebs.entidades.Usuario;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.ControlInventarioModelo;
import com.ebs.modelos.ExoneracionLinea;
import com.ebs.modelos.FacturaBusquedaModelo;
import com.ebs.modelos.InfoExoneracion;
import com.ebs.modelos.InformacionReferenciaFactura;
import com.ebs.modelos.ModeloProducto;
import com.ebs.modelos.ModeloTipoPrecio;
import com.ebs.modelos.PersonaModelo;
import com.ebs.modelos.ProductoExistencia;
import com.ebs.parametros.servicios.ServicioParametro;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.seguridad.servicios.ServicioPermisos;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.ubicacion.servicios.ServicioUbicaciones;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * @author Jorge API Technologies, BRACH PARA MODIFICAR INVENTARIO
 */
@ManagedBean
@ViewScoped
public class BeanFactura {

    @Getter
    @Setter
    private boolean edicionFacturaPuedeGenerarConsec;
    @Getter
    @Setter
    private List<TipoDocumentoReferencia> listaTipoDocumentoReferencia;
    @Getter
    @Setter
    private Long tipoDocumentoReferenciaSeleccionado;
    @Getter
    @Setter
    private boolean puedeAgregarLineasNotaDebito;
    @Getter
    @Setter
    private String clave;
    @Getter
    @Setter
    private String numeroConsecutivo;
    @Getter
    @Setter
    private String busquedaCodigoBarras;
    @Getter
    @Setter
    private Factura nuevaFactura;
    @Getter
    @Setter
    private List<TipoIdentificacion> listaTiposCedulas;
    @Getter
    @Setter
    private Long tipoIdentificacionSeleccionadaReceptor;
    @Getter
    @Setter
    private ControlInventarioModelo controlInventario;
    @Getter
    @Setter
    private boolean encontroReservasSinFactura = false;

    @Getter
    @Setter
    private Long idProvinciaSeleccionadaReceptor;
    @Getter
    @Setter
    private String nombreProductoDetalle;
    @Getter
    @Setter
    private Long idCantonSeleccionadaReceptor;
    @Getter
    @Setter
    private Long idDistritoSeleccionadaReceptor;

    @Getter
    @Setter
    private Long idCondicionVentaSeleccionado;
    @Getter
    @Setter
    private Long idMedioPagoSeleccionado;
    @Getter
    @Setter
    private String agenteSeleccionado;

    @Getter
    @Setter
    private String correoSeleccionado;
    @Getter
    @Setter
    private Long idBarioSeleccionadaReceptor;
    @Getter
    @Setter
    private Long idTipoFacturaSeleccionada;
    @Getter
    @Setter
    private Long idTipoFacturaSeleccionadaBusqueda;
    @Getter
    @Setter
    private List<TipoFactura> listaTiposFacturas;
    @Getter
    @Setter
    private Long idFacturaBuscar;
    @Getter
    @Setter
    private Date fechaFacturaBuscar;
    @Getter
    @Setter
    private String identificacionReceptor;
    @Getter
    @Setter
    private String mascaraSeleccionada;
    @Getter
    @Setter
    private String nombreClienteFantasia;
    @Getter
    @Setter
    private String numeroGuia;
    @Getter
    @Setter
    private String correoElectronicoFactura;
    @Getter
    @Setter
    private String lapsoTiempoCredito;
    @Getter
    @Setter
    private boolean inhabilitarComponentes = true;
    @Getter
    @Setter
    private boolean envioFactura = false;
    @Getter
    @Setter
    private boolean facturaYaSeEnvio = false;
    @Getter
    @Setter
    private boolean verPlazoCredito = false;
    @Getter
    @Setter
    private Integer tamannoCedula;
    @Getter
    @Setter
    private ModeloPersona personaReceptor;
    @Getter
    @Setter
    private Long idActvidadSeleccionada;
    @Getter
    @Setter
    private List<ActividadEconomica> listaTiposActividadesEconomicas;
    @Getter
    @Setter
    private List<PersonaCorreo> listaPersonasCorreo;
    @Getter
    @Setter
    private List<Provincia> listaProvincias;
    @Getter
    @Setter
    private List<Canton> listaCantones;
    @Getter
    @Setter
    private List<Distrito> listaDistritos;
    @Getter
    @Setter
    private List<Barrio> listaBarrios;
    @Getter
    @Setter
    private List<ModeloProducto> listaProductoPorFacturar;
    @Getter
    @Setter
    private List<ModeloProducto> listaProductosBusqueda;
    @Getter
    @Setter
    private List<TipoMoneda> listaTiposMonedas;
    @Getter
    @Setter
    private List<FacturaBusquedaModelo> listaFacturasBuscarEdicion;
    @Getter
    @Setter
    private List<FacturaBusquedaModelo> listaFacturasBuscarEdicionSeleccionada;
    @Getter
    @Setter
    private List<TipoPrecio> listaPrecios;
    @Getter
    @Setter
    private List<TipoExoneracion> listaExoneraciones;
    @Getter
    @Setter
    private ModeloProducto productoSeleccionado;
    @Getter
    @Setter
    private ModeloProducto productoSeleccionadoExoneracion;
    @Getter
    @Setter
    private ExoneracionLinea exoneracionLinea;

    @Getter
    @Setter
    private List<ProductoExistencia> listaExistenciasProductoSeleccionado;
    @Getter
    @Setter
    private List<ModeloTipoPrecio> listaPreciosProductoSeleccionado;
    @Getter
    @Setter
    private List<ProductoExistencia> listaExistenciasProductoSeleccionadoFiltro;
    @Getter
    @Setter
    private List<ModeloTipoPrecio> listaPreciosProductoSeleccionadoFiltro;
    @Getter
    @Setter
    private List<Factura> listaFacturaRechazadasReferencias;
    @Getter
    @Setter
    private List<Factura> listaFacturaRechazadasReferenciasFiltro;
    @Getter
    @Setter
    private Cliente clienteSeleccionado;
    @Inject
    private ServicioPersona servicioPersona;
    @Inject
    private ServicioUbicaciones servicioUbicaciones;
    @Inject
    private ServicioProducto servicioProducto;
    @Inject
    private ServicioFactura servicioFactura;
    @Inject
    private ServicioReporte servicioReporte;
    @Getter
    @Setter
    private String nombreProducto;
    @Getter
    @Setter
    private ModeloTipoPrecio precioSeleccionado;
    @Getter
    @Setter
    private List<CondicionVenta> listaCondicionVenta;
    @Getter
    @Setter
    private List<MedioPago> listaMedioPago;
    @Setter
    private BigDecimal totalGravados;
    @Setter
    private BigDecimal totalGravadosServicios;
    @Setter
    private BigDecimal totalGravadosMercancias;
    @Setter
    private BigDecimal totalExentos;
    @Setter
    private BigDecimal totalExentosServicios;
    @Setter
    private BigDecimal totalExentosMercancias;

    private BigDecimal totalExonerados;
    @Setter
    private BigDecimal totalExoneradosServicios;
    @Setter
    private BigDecimal totalExoneradosMercancias;

    @Setter
    private BigDecimal totalDescuentos;
    @Setter
    private BigDecimal totalDescuentosLineas;
    @Setter
    private Integer descuentoFactura;
    @Setter
    private BigDecimal totalVenta;
    @Setter
    private BigDecimal totalVentaNeta;
    @Setter
    private BigDecimal totalImpuestos;
    @Setter
    private BigDecimal totalFactura;
    @Getter
    private Factura facturaNueva;

    private Usuario usuarioLogueado;
    @Setter
    StreamedContent reporteDesplegar = null;

    @Getter
    @Setter
    private boolean guardoCorrectamenta = false;
    @Getter
    @Setter
    private boolean esNuevaFactura = false;

    @Getter
    @Setter
    private boolean esFacturaElectronicaGuardo = true;

    @Getter
    @Setter
    private boolean verPanelNuevaPersona = true;

    @Getter
    @Setter
    private boolean verPanelBuscarPersona = true;

    @Getter
    @Setter
    private boolean verPanelBuscarProducto = true;

    @Getter
    @Setter
    private boolean inhabilitarModificacionPrecio = true;

    @Getter
    @Setter
    private String valorproducto;

    @Getter
    @Setter
    private ModeloProducto productoSeleccionadaLista;

    @Inject
    private ServicioPermisos servicioPermisos;
    @Getter
    @Setter
    private List<Usuario> listaUsuariosAgentes;
    @Getter
    @Setter
    private List<PersonaModelo> listaPersonas;

    @Getter
    @Setter
    private List<PersonaModelo> listaPersonasFiltro;
    @Getter
    @Setter
    private String codigoSituacionComprobanteSeleccionado;

    @Getter
    @Setter
    private InformacionReferenciaFactura informacionReferenciaDocumento;

    @Inject
    private ServicioInventario servicioInventario;
    @Getter
    @Setter
    public boolean deshabilitaBotonNuevo;
    @Getter
    @Setter
    private BigDecimal pagaCon;

    @Getter
    @Setter
    private Parametro parametro;

    @Getter
    @Setter
    private BigDecimal vuelto;

    @Getter
    @Setter
    private Integer cantidadDiasVigencia;
    @Getter
    @Setter
    private boolean reservarEnInventario;

    @Inject
    private ServicioParametro servicioParametro;
    @Getter
    @Setter
    private boolean esFacturaProvieneNOtaCredito;
    @Getter
    @Setter
    private List<MotivoDebito> listaMotivoDebito;
    @Getter
    @Setter
    private Long motivoDebitoSeleccionado;
    @Getter
    @Setter
    private String motivoDebito;

    private Persona personaEdicionFactura;

    @Getter
    @Setter
    private List<TipoTarifaImpuesto> listaTiposTarifa;

    @Getter
    @Setter
    private boolean esPersonaExenta;
    @Getter
    @Setter
    private boolean esPersonaExonerada;

    public BeanFactura() {
    }

    public void calcularVuelto() {
        if (totalFactura != null && !totalFactura.equals(new BigDecimal("0.0"))) {
            if (pagaCon != null) {
                vuelto = pagaCon.subtract(totalFactura);
            } else {
                vuelto = new BigDecimal(0.0);
            }
        } else {
            vuelto = new BigDecimal(0.0);
        }

    }

    public void editarFactura() {

        if (esFacturaProvieneNOtaCredito) {
            this.inhabilitarComponentes = false;
            esNuevaFactura = true;
            reporteDesplegar = null;
            this.envioFactura = false;

        } else {

            this.inhabilitarComponentes = false;
            esNuevaFactura = false;
            reporteDesplegar = null;
            this.envioFactura = false;
        }

    }

    public void buscarDetalleProducto(ModeloProducto idProducto) {
        try {
            this.listaExistenciasProductoSeleccionado = new ArrayList<>();
            this.listaPreciosProductoSeleccionado = new ArrayList<>();
            this.nombreProductoDetalle = idProducto.getId_producto() + " - " + idProducto.getDescripcion();
            listaExistenciasProductoSeleccionado = this.servicioProducto.obtenerExistenciaProducto(idProducto.getId_producto());
            listaPreciosProductoSeleccionado = this.servicioProducto.obtenerPreciosProductos(idProducto.getId_producto());
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public boolean getPermiteEnviarFactura() {
        boolean resultado = false;
        if (this.guardoCorrectamenta) {
            resultado = true;
        }
        return resultado;
    }

    public void seleccionarActividad(ValueChangeEvent evt) {
        idActvidadSeleccionada = Long.parseLong(evt.getNewValue() == null ? "0" : evt.getNewValue().toString());

    }

    public void nuevaFactura() {
        this.listaPersonasCorreo = new ArrayList<>();
        deshabilitaBotonNuevo = true;
        this.esFacturaProvieneNOtaCredito = false;
        this.esPersonaExenta = false;
        this.esPersonaExonerada = false;
    }

    public void cancelar() {
        this.listaPersonasCorreo = new ArrayList<>();
        deshabilitaBotonNuevo = false;
        this.inhabilitarComponentes = true;
        esFacturaProvieneNOtaCredito = false;
        this.esPersonaExenta = false;
        this.esPersonaExonerada = false;
    }

    public void generaerNuevaFactura(boolean esUnaFacturaPreexistente) {
        this.listaPersonasCorreo = new ArrayList<>();
        this.esPersonaExenta = false;
        this.esPersonaExonerada = false;

        encontroReservasSinFactura = false;
        this.cantidadDiasVigencia = 0;
        reservarEnInventario = true;
        esFacturaProvieneNOtaCredito = false;
        puedeAgregarLineasNotaDebito = false;
        edicionFacturaPuedeGenerarConsec = true;

        verPanelNuevaPersona = false;
        verPanelBuscarProducto = false;
        verPanelBuscarPersona = false;

        informacionReferenciaDocumento = InformacionReferenciaFactura.inicializar();

        clienteSeleccionado = null;
        facturaYaSeEnvio = false;
        idTipoFacturaSeleccionada = 0L;
        idCondicionVentaSeleccionado = 0L;
        idMedioPagoSeleccionado = 0L;
        tipoIdentificacionSeleccionadaReceptor = 0l;
        this.agenteSeleccionado = "0";
        identificacionReceptor = "";
        reporteDesplegar = null;
        esNuevaFactura = true;
        totalGravados = new BigDecimal("0");
        totalGravadosServicios = new BigDecimal("0");
        totalGravadosMercancias = new BigDecimal("0");
        totalExentos = new BigDecimal("0");
        totalExentosServicios = new BigDecimal("0");
        totalExentosMercancias = new BigDecimal("0");
        totalDescuentos = new BigDecimal("0");
        totalDescuentosLineas = new BigDecimal("0");
        totalVenta = new BigDecimal("0");
        totalVentaNeta = new BigDecimal("0");
        totalImpuestos = new BigDecimal("0");
        totalFactura = new BigDecimal("0");
        mascaraSeleccionada = TiposCedulaMascaras.CEDULA_FISICA.getMascara();
        tamannoCedula = TiposCedulaMascaras.CEDULA_FISICA.getTamannoTipoCedula();
        personaReceptor = new ModeloPersona();
        this.listaProductoPorFacturar = new ArrayList<>();
        this.listaProductosBusqueda = new ArrayList<>();
        this.productoSeleccionado = ModeloProducto.inicializar();
        this.identificacionReceptor = "";
        numeroConsecutivo = "";
        this.correoElectronicoFactura = "";
        this.correoSeleccionado = "0";
        this.nombreClienteFantasia = "";
        guardoCorrectamenta = false;
        this.inhabilitarComponentes = false;
        this.facturaNueva = new Factura();

    }

    @PostConstruct
    public void inicializar() {
        try {

            this.listaPersonasCorreo = new ArrayList<>();
            exoneracionLinea = new ExoneracionLinea();
            this.esPersonaExenta = false;
            this.esPersonaExonerada = false;

            esFacturaProvieneNOtaCredito = false;
            reservarEnInventario = true;
            this.cantidadDiasVigencia = 0;
            encontroReservasSinFactura = false;
            parametro = servicioParametro.obtenerValorParametro(ParametrosEnum.INVENTARIO_FACTURA_ACTIVO.getIdParametro());
            puedeAgregarLineasNotaDebito = false;
            edicionFacturaPuedeGenerarConsec = true;
            vuelto = new BigDecimal(0.0);
            this.listaTiposActividadesEconomicas = this.servicioPersona.obtenerListaCodigosActividad();

            verPanelNuevaPersona = false;
            verPanelBuscarProducto = false;
            verPanelBuscarPersona = false;
            informacionReferenciaDocumento = InformacionReferenciaFactura.inicializar();
            clienteSeleccionado = null;
            this.agenteSeleccionado = "0";
            listaTiposFacturas = servicioFactura.obtenerTiposFacturas();
            listaPersonas = servicioPersona.obtenerTodasPersonasAlias();//obtenerTodasPersonas();
            listaTiposTarifa = servicioProducto.obtenerListaTiposTarifas();

            numeroConsecutivo = "";
            this.correoElectronicoFactura = "";
            this.correoSeleccionado = "0";
            this.nombreClienteFantasia = "";
            guardoCorrectamenta = false;
            usuarioLogueado = (Usuario) JSFUtil.obtenerDeSesion("Usuario");
            this.listaUsuariosAgentes = servicioPermisos.obtenerUsuariosAgentes();
            this.listaCondicionVenta = servicioFactura.obtenerCondicionesVenta();
            //idCondicionVentaSeleccionado = this.listaCondicionVenta.get(0).getId_cond_venta();

            this.listaMedioPago = servicioFactura.obtenerMediosPago();
            //idMedioPagoSeleccionado = this.listaMedioPago.get(0).getId_medio_pago();

            this.listaPrecios = servicioProducto.obtenerListaTiposPrecio();
            this.listaTiposMonedas = servicioProducto.obtenerListaTiposMonedas();
            //Obtengo la lista de personas 
            this.listaTiposCedulas = servicioPersona.obtenerListaTiposIdentificacion();
            this.listaExoneraciones = servicioProducto.obtenerTodosTiposExoneraciones();

            totalGravados = new BigDecimal("0");
            totalGravadosServicios = new BigDecimal("0");
            totalGravadosMercancias = new BigDecimal("0");
            totalExentos = new BigDecimal("0");
            totalExentosServicios = new BigDecimal("0");
            totalExentosMercancias = new BigDecimal("0");
            totalDescuentos = new BigDecimal("0");
            totalDescuentosLineas = new BigDecimal("0");
            totalVenta = new BigDecimal("0");
            totalVentaNeta = new BigDecimal("0");
            totalImpuestos = new BigDecimal("0");
            totalFactura = new BigDecimal("0");
            mascaraSeleccionada = TiposCedulaMascaras.CEDULA_FISICA.getMascara();
            tamannoCedula = TiposCedulaMascaras.CEDULA_FISICA.getTamannoTipoCedula();
            personaReceptor = new ModeloPersona();
            this.listaProductoPorFacturar = new ArrayList<>();
            this.listaProductosBusqueda = new ArrayList<>();
            this.productoSeleccionado = ModeloProducto.inicializar();
            this.identificacionReceptor = "";
            idTipoFacturaSeleccionada = 0L;
            idCondicionVentaSeleccionado = 0L;
            idMedioPagoSeleccionado = 0L;
            tipoIdentificacionSeleccionadaReceptor = 0l;
            this.envioFactura = false;

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void seleccionarTipoCedula(ValueChangeEvent evt) {

        tipoIdentificacionSeleccionadaReceptor = Long.parseLong(evt.getNewValue().toString());

        if (tipoIdentificacionSeleccionadaReceptor.equals(TiposCedulaMascaras.CEDULA_FISICA.getIdTipoCedula())) {
            mascaraSeleccionada = TiposCedulaMascaras.CEDULA_FISICA.getMascara();
            tamannoCedula = TiposCedulaMascaras.CEDULA_FISICA.getTamannoTipoCedula();
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
        identificacionReceptor = "";
    }

    public List<String> busquedadProductos(String texto) {
        List<String> listaProductoString = new ArrayList<>();
        String valor = "";
        for (Producto producto : servicioProducto.buscarProductosActivos(texto)) {
            valor = producto.getDescripcion() + "#" + producto.getCodigo_barras();
            listaProductoString.add(valor);
        }
        return listaProductoString;
    }

    public void selecionarCondicionVenta(ValueChangeEvent evt) {
        idCondicionVentaSeleccionado = (Long) evt.getNewValue();
        if (idCondicionVentaSeleccionado.equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta())) {
            idMedioPagoSeleccionado = TiposMediosPago.TRANSFERENCIA_DEPÓSITO_BANCARIO.getIdMedioPago();
            lapsoTiempoCredito = "30";
        } else {
            idMedioPagoSeleccionado = 0L;
            lapsoTiempoCredito = "0";
        }
    }

    public void seleccionarCorreoElectronico(ValueChangeEvent evt) {
        this.correoSeleccionado = evt.getNewValue().toString();
    }

    public void seleccionarTipoFactura(ValueChangeEvent evt) {
        idTipoFacturaSeleccionada = (Long) evt.getNewValue();
        StringBuilder mensajeMostrar = new StringBuilder();

        try {
            if (this.facturaNueva.getId_tipo_factura() != null && ((this.facturaNueva.getId_tipo_factura().equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura()) && this.idTipoFacturaSeleccionada.equals(TipoFacturaEnum.PEDIDO.getIdTipoFactura())
                    || (this.facturaNueva.getId_tipo_factura().equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura()) && (this.idTipoFacturaSeleccionada.equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()) || this.idTipoFacturaSeleccionada.equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())))))) {

                for (ModeloProducto producto : this.listaProductoPorFacturar) {
                    //Obtengo la existencia de inventario
                    List<Inventario> listaResultado = this.servicioInventario.consultarInventProductoPorCantidad(Utilitario.obtenerIdBodegaUsuario(),
                            producto.getId_producto(), producto.getCantidad().longValue(), true);

                    //Valida si existe suficiente cantidad
                    if (!existeCantidadProductoSuficiente(producto.getId_producto(), producto.getCantidad().longValue(), listaResultado)) {

                        producto.setPresentarProblemaInventario(1);
                        mensajeMostrar.append("\n\n--" + producto.getDescripcion() + "  Existen " + listaResultado.stream()
                                .mapToLong(o -> o.getCantExistencia())
                                .sum());
                    }
                }
                if (!mensajeMostrar.toString().equals("")) {
                    MensajeUtil.agregarMensajeAdvertencia("Los productos carecen de la cantidad de producto solicitada o bien no existen en inventario: " + mensajeMostrar.toString());
                }
            }
        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.seleccion.tipo.factura"));
            ExcepcionManager.manejarExcepcion(ex);
        }

    }

    public void seleccionarMedioPago(ValueChangeEvent evt) {
        idMedioPagoSeleccionado = (Long) evt.getNewValue();
    }

    public void seleccionarProvincias(ValueChangeEvent evt) {
        idProvinciaSeleccionadaReceptor = Long.parseLong(evt.getNewValue().toString());
        listaCantones = servicioUbicaciones.obtenerCantones(idProvinciaSeleccionadaReceptor);
    }

    public void seleccionarCantones(ValueChangeEvent evt) {
        idCantonSeleccionadaReceptor = Long.parseLong(evt.getNewValue().toString());
        listaDistritos = servicioUbicaciones.obtenerDistritos(idCantonSeleccionadaReceptor);
    }

    public void seleccionarDistritos(ValueChangeEvent evt) {
        idDistritoSeleccionadaReceptor = Long.parseLong(evt.getNewValue().toString());
        listaBarrios = servicioUbicaciones.obtenerBarrios(idDistritoSeleccionadaReceptor);
    }

    public void seleccionarBarrios(ValueChangeEvent evt) {
        idBarioSeleccionadaReceptor = Long.parseLong(evt.getNewValue().toString());

    }

    public void buscarFacturasEdicion() {
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String fechaParametro = null;
            if (fechaFacturaBuscar != null) {
                fechaParametro = df.format(fechaFacturaBuscar);
            }
            this.listaFacturasBuscarEdicion = servicioFactura.obtenerFacturasBusqueda(idTipoFacturaSeleccionadaBusqueda == null ? null : idTipoFacturaSeleccionadaBusqueda, Utilitario.obtenerUsuario().getLogin(), fechaParametro, idFacturaBuscar);
            this.listaFacturasBuscarEdicionSeleccionada = null;
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarFactura(FacturaBusquedaModelo facturaEdicion) {
        try {

            esFacturaProvieneNOtaCredito = false;
            StringBuilder mensajeMostrar = new StringBuilder();
            this.generaerNuevaFactura(true);

            this.inhabilitarComponentes = true;

            listaFacturasBuscarEdicion = new ArrayList<>();
            this.envioFactura = false;
            esNuevaFactura = false;
            Factura facturaSeleccionadaBusqueda = servicioFactura.obtenerFacturaBusqueda(facturaEdicion.getIdFactura());
            this.agenteSeleccionado = facturaSeleccionadaBusqueda.getAgente();

            //permito que se peuda generar el consecutivo
            edicionFacturaPuedeGenerarConsec = !facturaSeleccionadaBusqueda.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()) && !facturaSeleccionadaBusqueda.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura());
            //agrego un indicador para determinar si las lineas nuevas que agregue se van con el indicado de nota de debito
            puedeAgregarLineasNotaDebito = facturaSeleccionadaBusqueda.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()) || facturaSeleccionadaBusqueda.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura());

            if (puedeAgregarLineasNotaDebito) {
                listaMotivoDebito = servicioFactura.obtenerTiposMotivosDebitos();
                listaTipoDocumentoReferencia = servicioFactura.obtenerTiposDocumentosReferencia();
            }

            facturaNueva = facturaSeleccionadaBusqueda;

            this.cantidadDiasVigencia = facturaNueva.getCant_dias_vigencia();

            if (facturaSeleccionadaBusqueda.getId_cliente() != null) {
                clienteSeleccionado = servicioPersona.obtenerClientePorIdCliente(facturaSeleccionadaBusqueda.getId_cliente());
                personaEdicionFactura = servicioPersona.obtenerPersonaPorNumeroCedula(clienteSeleccionado.getNumero_cedula());
                this.listaPersonasCorreo = servicioPersona.obtenerListaCorreosPersona(personaEdicionFactura.getPersonaPK().getNumero_cedula(), personaEdicionFactura.getPersonaPK().getId_tipo_cedula());
                if (this.listaPersonasCorreo != null) {
                    if (this.listaPersonasCorreo.size() == 1) {
                        this.correoSeleccionado = this.listaPersonasCorreo.get(0).getCorreo_electronico();
                    }
                }
                personaReceptor = ModeloPersona.convertirPersonaAModeloPersona(personaEdicionFactura);
                tipoIdentificacionSeleccionadaReceptor = personaReceptor.getId_tipo_cedula();
                identificacionReceptor = personaReceptor.getNumero_cedula();
                this.esPersonaExenta = personaEdicionFactura.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador());
                this.esPersonaExonerada = personaEdicionFactura.getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador());

            }
            idCondicionVentaSeleccionado = facturaSeleccionadaBusqueda.getId_cond_venta();
            idMedioPagoSeleccionado = facturaSeleccionadaBusqueda.getId_medio_pago();
            nombreClienteFantasia = facturaSeleccionadaBusqueda.getNombre_cliente_fantasia() == null ? null : facturaSeleccionadaBusqueda.getNombre_cliente_fantasia().trim();
            descuentoFactura = facturaSeleccionadaBusqueda.getDescuento_aplicado();
            correoElectronicoFactura = facturaSeleccionadaBusqueda.getCorreo_electronico() == null ? null : facturaSeleccionadaBusqueda.getCorreo_electronico().trim();
            correoSeleccionado = facturaSeleccionadaBusqueda.getCorreo_electronico_cliente() == null ? null : facturaSeleccionadaBusqueda.getCorreo_electronico_cliente().trim();
            lapsoTiempoCredito = facturaSeleccionadaBusqueda.getPlazo_credito() == null ? null : facturaSeleccionadaBusqueda.getPlazo_credito().trim();
            idTipoFacturaSeleccionada = facturaSeleccionadaBusqueda.getId_tipo_factura();
            seleccionarTipoCondicionVenta();

            List<DetalleFactura> listaDetalleFacturaEdicion = servicioFactura.obtenerDetalleFacturaBusqueda(facturaEdicion.getIdFactura());
            // List<InventarioSalidaDetalle> listaSalidasDetalle = servicioInventario.obtenerInvSaliDetallePorIdFactura(facturaEdicion.getIdFactura());

            listaProductoPorFacturar = new ArrayList<>();
            Producto detalleFacturaEdicion = null;
            ModeloProducto productoDelDetalle = null;

            for (DetalleFactura detalleFactura : listaDetalleFacturaEdicion) {
                detalleFacturaEdicion = new Producto();
                productoDelDetalle = new ModeloProducto();

                detalleFacturaEdicion = servicioProducto.obtenerProductoPorIdProducto(detalleFactura.getDetallePK().getId_producto());

                productoDelDetalle = ModeloProducto.construirObjeto(detalleFacturaEdicion,
                        servicioProducto.obtenerUnidadMedidaProducto(detalleFacturaEdicion.getId_unidad_medida()),
                        servicioProducto.obtenerListaPreciosProducto(detalleFacturaEdicion.getId_producto()),
                        this.listaPrecios,
                        servicioProducto.obtenerExistenciaProducto(detalleFacturaEdicion.getId_producto()),
                        clienteSeleccionado,
                        this.listaTiposMonedas,
                        servicioProducto.obtenerTipoProducto(detalleFacturaEdicion.getId_tipo_producto()),
                        servicioProducto.obtenerImpuesto(detalleFacturaEdicion.getId_impuesto()),
                        null,
                        servicioProducto.obtenerTipoTarifaImpuesto(detalleFacturaEdicion.getId_tipo_tarifa_impuesto()),
                        personaEdicionFactura,
                        listaTiposTarifa);

                productoDelDetalle.setProducto(detalleFacturaEdicion);
                productoDelDetalle.setCantidad(detalleFactura.getCantidad());
                productoDelDetalle.setDescuento(detalleFactura.getDescuento());
                productoDelDetalle.setMontoDescuento(detalleFactura.getTotal_descuento());
                productoDelDetalle.setTotalImpuesto(detalleFactura.getTotal_impuestos());
                productoDelDetalle.setSubTotal(detalleFactura.getSub_total());
                productoDelDetalle.setDescripcionLineaProducto(detalleFactura.getDescripcion());
                productoDelDetalle.setIdTipPrecioSeleccionado(detalleFactura.getId_tipo_precio());
                productoDelDetalle.setPrecio(detalleFactura.getPrecio_neto());
                productoDelDetalle.setNumeroLineaProducto(detalleFactura.getDetallePK().getNumero_linea());
                productoDelDetalle.setPermiteEliminar(!facturaSeleccionadaBusqueda.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura())
                        && !facturaSeleccionadaBusqueda.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura()));

                productoDelDetalle.setIdTipoTarifaSeleccionada(detalleFactura.getId_tipo_tarifa());

                if (detalleFactura.getId_exoneracion() != null) {
                    ExoneracionLinea exoLinea = new ExoneracionLinea();
                    exoLinea.setId_exoneracion(detalleFactura.getId_exoneracion());
                    exoLinea.setFecha_emision(Utilitario.convertirStringToDate(detalleFactura.getFecha_emision()));
                    exoLinea.setNombre_institucion(detalleFactura.getNombre_institucion());
                    exoLinea.setNumero_documento(detalleFactura.getNumero_documento());
                    exoLinea.setPorcentaje_exonerado(detalleFactura.getPorcentaje_exonerado());
                    exoLinea.setMaximoPorcentajeExoneracion(productoDelDetalle.getTipoTarifaImpuesto().getValor());
                    productoDelDetalle.setExoneracionLinea(exoLinea);

                }

                productoDelDetalle.calcularMontos();

                listaProductoPorFacturar.add(productoDelDetalle);
            }

            for (ModeloProducto modeloProducto : listaProductoPorFacturar) {
                modeloProducto.calcularMontos();
            }
            if (!mensajeMostrar.toString().equals("")) {
                MensajeUtil.agregarMensajeAdvertencia("Los productos carecen de la cantidad de producto solicitada o bien no existen en inventario: " + mensajeMostrar.toString());
            }
            this.deshabilitaBotonNuevo = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarFacturaParaReemplazo(FacturaBusquedaModelo facturaEdicion) {
        try {

            this.inhabilitarComponentes = false;
            esNuevaFactura = true;
            reporteDesplegar = null;
            this.envioFactura = false;

            StringBuilder mensajeMostrar = new StringBuilder();
            this.generaerNuevaFactura(true);

            this.inhabilitarComponentes = false;

            listaFacturasBuscarEdicion = new ArrayList<>();
            this.envioFactura = false;
            Factura facturaSeleccionadaBusqueda = servicioFactura.obtenerFacturaBusqueda(facturaEdicion.getIdFactura());
            this.agenteSeleccionado = facturaSeleccionadaBusqueda.getAgente();

            //permito que se peuda generar el consecutivo
            edicionFacturaPuedeGenerarConsec = true;
            //agrego un indicador para determinar si las lineas nuevas que agregue se van con el indicado de nota de debito
            puedeAgregarLineasNotaDebito = false;

            if (puedeAgregarLineasNotaDebito) {
                listaMotivoDebito = servicioFactura.obtenerTiposMotivosDebitos();
                listaTipoDocumentoReferencia = servicioFactura.obtenerTiposDocumentosReferencia();
            }

            facturaNueva = facturaSeleccionadaBusqueda;
            this.cantidadDiasVigencia = facturaNueva.getCant_dias_vigencia();

            if (facturaSeleccionadaBusqueda.getId_cliente() != null) {
                clienteSeleccionado = servicioPersona.obtenerClientePorIdCliente(facturaSeleccionadaBusqueda.getId_cliente());
                personaEdicionFactura = servicioPersona.obtenerPersonaPorNumeroCedula(clienteSeleccionado.getNumero_cedula());
                this.listaPersonasCorreo = servicioPersona.obtenerListaCorreosPersona(personaEdicionFactura.getPersonaPK().getNumero_cedula(), personaEdicionFactura.getPersonaPK().getId_tipo_cedula());
                if (this.listaPersonasCorreo != null) {
                    if (this.listaPersonasCorreo.size() == 1) {
                        this.correoSeleccionado = this.listaPersonasCorreo.get(0).getCorreo_electronico();
                    }
                }
                personaReceptor = ModeloPersona.convertirPersonaAModeloPersona(personaEdicionFactura);
                tipoIdentificacionSeleccionadaReceptor = personaReceptor.getId_tipo_cedula();
                identificacionReceptor = personaReceptor.getNumero_cedula();

                this.esPersonaExenta = personaEdicionFactura.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador());
                this.esPersonaExonerada = personaEdicionFactura.getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador());

            }
            idCondicionVentaSeleccionado = facturaSeleccionadaBusqueda.getId_cond_venta();
            idMedioPagoSeleccionado = facturaSeleccionadaBusqueda.getId_medio_pago();
            nombreClienteFantasia = facturaSeleccionadaBusqueda.getNombre_cliente_fantasia() == null ? null : facturaSeleccionadaBusqueda.getNombre_cliente_fantasia().trim();
            descuentoFactura = facturaSeleccionadaBusqueda.getDescuento_aplicado();
            correoElectronicoFactura = facturaSeleccionadaBusqueda.getCorreo_electronico() == null ? null : facturaSeleccionadaBusqueda.getCorreo_electronico().trim();
            correoSeleccionado = facturaSeleccionadaBusqueda.getCorreo_electronico_cliente() == null ? null : facturaSeleccionadaBusqueda.getCorreo_electronico_cliente().trim();

            lapsoTiempoCredito = facturaSeleccionadaBusqueda.getPlazo_credito() == null ? null : facturaSeleccionadaBusqueda.getPlazo_credito().trim();
            idTipoFacturaSeleccionada = facturaSeleccionadaBusqueda.getId_tipo_factura();
            seleccionarTipoCondicionVenta();

            List<DetalleFactura> listaDetalleFacturaEdicion = servicioFactura.obtenerDetalleFacturaBusquedaParaReemplazo(facturaEdicion.getIdFactura());
            // List<InventarioSalidaDetalle> listaSalidasDetalle = servicioInventario.obtenerInvSaliDetallePorIdFactura(facturaEdicion.getIdFactura());

            listaProductoPorFacturar = new ArrayList<>();
            Producto detalleFacturaEdicion = null;
            ModeloProducto productoDelDetalle = null;
            TipoTarifaImpuesto tarifaBuscar = null;

            for (DetalleFactura detalleFactura : listaDetalleFacturaEdicion) {
                detalleFacturaEdicion = new Producto();
                productoDelDetalle = new ModeloProducto();

                detalleFacturaEdicion = servicioProducto.obtenerProductoPorIdProducto(detalleFactura.getDetallePK().getId_producto());

                productoDelDetalle = ModeloProducto.construirObjeto(detalleFacturaEdicion,
                        servicioProducto.obtenerUnidadMedidaProducto(detalleFacturaEdicion.getId_unidad_medida()),
                        servicioProducto.obtenerListaPreciosProducto(detalleFacturaEdicion.getId_producto()),
                        this.listaPrecios,
                        servicioProducto.obtenerExistenciaProducto(detalleFacturaEdicion.getId_producto()),
                        clienteSeleccionado,
                        this.listaTiposMonedas,
                        servicioProducto.obtenerTipoProducto(detalleFacturaEdicion.getId_tipo_producto()),
                        servicioProducto.obtenerImpuesto(detalleFacturaEdicion.getId_impuesto()),
                        null,
                        servicioProducto.obtenerTipoTarifaImpuesto(detalleFacturaEdicion.getId_tipo_tarifa_impuesto()),
                        personaEdicionFactura,
                        listaTiposTarifa);

                productoDelDetalle.setProducto(detalleFacturaEdicion);
                productoDelDetalle.setCantidad(detalleFactura.getCantidad());
                productoDelDetalle.setDescuento(detalleFactura.getDescuento());
                productoDelDetalle.setMontoDescuento(detalleFactura.getTotal_descuento());
                productoDelDetalle.setTotalImpuesto(detalleFactura.getTotal_impuestos());
                productoDelDetalle.setSubTotal(detalleFactura.getSub_total());
                productoDelDetalle.setDescripcionLineaProducto(detalleFactura.getDescripcion());
                productoDelDetalle.setIdTipPrecioSeleccionado(detalleFactura.getId_tipo_precio());
                productoDelDetalle.setPrecio(detalleFactura.getPrecio_neto());
                productoDelDetalle.setNumeroLineaProducto(detalleFactura.getDetallePK().getNumero_linea());
                productoDelDetalle.setPermiteEliminar(true);

                //tarifaBuscar = servicioProducto.obtenerTipoTarifaImpuesto(detalleFacturaEdicion.getId_tipo_tarifa_impuesto());
                productoDelDetalle.setIdTipoTarifaSeleccionada(detalleFactura.getId_tipo_tarifa()); //== null ? (tarifaBuscar != null ? tarifaBuscar.getId_tipo_tarifa_impuesto() : null) : detalleFactura.getId_tipo_tarifa());
                productoDelDetalle.cambiarTipoTarifa();

                if (detalleFactura.getId_exoneracion() != null) {
                    ExoneracionLinea exoLinea = new ExoneracionLinea();
                    exoLinea.setId_exoneracion(detalleFactura.getId_exoneracion());
                    exoLinea.setFecha_emision(Utilitario.convertirStringToDate(detalleFactura.getFecha_emision()));
                    exoLinea.setNombre_institucion(detalleFactura.getNombre_institucion());
                    exoLinea.setNumero_documento(detalleFactura.getNumero_documento());
                    exoLinea.setPorcentaje_exonerado(detalleFactura.getPorcentaje_exonerado());
                    exoLinea.setMaximoPorcentajeExoneracion(productoDelDetalle.getTipoTarifaImpuesto().getValor());
                    productoDelDetalle.setExoneracionLinea(exoLinea);

                }

                productoDelDetalle.calcularMontos();

                listaProductoPorFacturar.add(productoDelDetalle);
            }
            this.informacionReferenciaDocumento.setTipoDoc(Indicadores.OPCION_PARA_REEMPLAZAR_FACTURA_RECHAZADA.getIndicador().toString());
            this.informacionReferenciaDocumento.setIdFacturaReferencia(facturaSeleccionadaBusqueda.getId_factura());
            this.informacionReferenciaDocumento.setNumero(facturaSeleccionadaBusqueda.getClave());
            this.informacionReferenciaDocumento.setCodigo("01");
            this.informacionReferenciaDocumento.setTipoDocumentoReferencia("10");
            this.informacionReferenciaDocumento.setRazon("Se reemplaza la factura " + facturaSeleccionadaBusqueda.getClave());

            for (ModeloProducto modeloProducto : listaProductoPorFacturar) {
                modeloProducto.calcularMontos();
            }
            if (!mensajeMostrar.toString().equals("")) {
                MensajeUtil.agregarMensajeAdvertencia("Los productos carecen de la cantidad de producto solicitada o bien no existen en inventario: " + mensajeMostrar.toString());
            }
            this.deshabilitaBotonNuevo = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void seleccionarTipoCondicionVenta() {
        if (idCondicionVentaSeleccionado != null) {
            if (idCondicionVentaSeleccionado.equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta())) {
                this.verPlazoCredito = true;
                idMedioPagoSeleccionado = TiposMediosPago.TRANSFERENCIA_DEPÓSITO_BANCARIO.getIdMedioPago();
            } else {
                this.verPlazoCredito = false;
            }
        }

    }

    public void editarFila(CellEditEvent evt) {

    }

    public void buscarPersonaPorCedula(String cedula, Long tipoCedula) {
        if (tipoCedula != null && !cedula.equals("")) {
            identificacionReceptor = cedula;
            tipoIdentificacionSeleccionadaReceptor = tipoCedula;
            buscarPersona(null);
        }
        this.verPanelNuevaPersona = false;
    }

    public void mostrarPanelBuscarPersonal() {
        this.verPanelBuscarPersona = true;
    }

    public void mostrarPanelBuscarProducto() {
        this.verPanelBuscarProducto = true;
    }

    public void seleccionarProductoAgregarExoneracion() {
        exoneracionLinea = new ExoneracionLinea();
    }

    public void agregarExoneracionLinea() {
        RequestContext context = RequestContext.getCurrentInstance();
        try {
            if (exoneracionLinea.getId_exoneracion()
                    .equals(TipoExoneracionEnum.EXENCIONES_DIRECCIÓN_GENERAL_DE_HACIENDA.getIdTipoExoneracion())) {
                Utilitario util = new Utilitario();
                Parametro endPointExoneracionHacienda = servicioParametro.obtenerValorParametro(ParametrosEnum.API_URL_EXONERACION.getIdParametro());

                InfoExoneracion resultado = util.obtenerExoneracion(endPointExoneracionHacienda, exoneracionLinea.getNumero_documento());

                if (resultado != null) {
                    if (resultado.getFechaVencimiento() != null) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        Date fechaVencimiento = format.parse(util.obtenerValorRegex(resultado.getFechaVencimiento(), "(.*)T.*", 1));

                        Date hoy = new Date();

                        if (fechaVencimiento.before(hoy)) {
                            MensajeUtil.agregarMensajeError("La exoneración no es válida, es una exoneración vencida.\n\n" + resultado.getInformacionExoneracion());
                            return;
                        } else {
                            exoneracionLinea.setPorcentaje_exonerado(Integer.parseInt(resultado.getPorcentajeExoneracion()));
                            MensajeUtil.agregarMensajeInfo("La exoneración válida");
                        }
                    } else {
                        MensajeUtil.agregarMensajeAdvertencia("La exoneración no existe");
                        return;
                    }
                } else {
                    MensajeUtil.agregarMensajeAdvertencia("La exoneración no existe");
                    return;
                }
            }

            if (this.exoneracionLinea.isAplicar_para_toda_factura()) {
                this.listaProductoPorFacturar.forEach(elemento -> {

                    elemento.setExoneracionLinea(this.exoneracionLinea);
                    elemento.calcularMontos();

                });
            } else {
                this.listaProductoPorFacturar.forEach(elemento -> {
                    if (elemento.getId_producto().equals(this.productoSeleccionadoExoneracion.getId_producto())
                            && elemento.getNumeroLineaProducto().equals(this.productoSeleccionadoExoneracion.getNumeroLineaProducto())) {
                        elemento.setExoneracionLinea(this.exoneracionLinea);
                        elemento.calcularMontos();
                    }
                });
                exoneracionLinea = new ExoneracionLinea();
            }

            context.execute("PF('idDialogoExoneracion').hide()");
        } catch (ParseException ex) {
            ex.printStackTrace();
            MensajeUtil.agregarMensajeError("Ha ocurrido un error cuando se intentó validar la exoneración");
        }

    }

    public void buscarPersonaSeleccionTabla(SelectEvent event) {
        try {

            PersonaModelo personaModelo = (PersonaModelo) event.getObject();
            personaModelo.getPersona().setListaCorreosPersona(servicioPersona.obtenerListaCorreosPersona(personaModelo.getPersona().getPersonaPK().getNumero_cedula(), personaModelo.getPersona().getPersonaPK().getId_tipo_cedula()));
            this.listaPersonasCorreo = personaModelo.getPersona().getListaCorreosPersona();
            if (this.listaPersonasCorreo != null) {
                if (this.listaPersonasCorreo.size() == 1) {
                    this.correoSeleccionado = this.listaPersonasCorreo.get(0).getCorreo_electronico();
                } else if (this.listaPersonasCorreo.size() > 1) {
                    this.correoSeleccionado = personaModelo.getPersona().getCorreo_electronico();
                }
            }
            personaReceptor = ModeloPersona.convertirPersonaAModeloPersona(personaModelo.getPersona());
            this.idActvidadSeleccionada = personaReceptor.getCodigoActividad();

            personaEdicionFactura = personaModelo.getPersona();

            this.esPersonaExenta = personaEdicionFactura.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador());
            this.esPersonaExonerada = personaEdicionFactura.getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador());

            clienteSeleccionado = personaModelo.getCliente();

            tipoIdentificacionSeleccionadaReceptor = personaReceptor.getId_tipo_cedula();
            identificacionReceptor = personaReceptor.getNumero_cedula();
            this.verPanelBuscarPersona = false;

            BigDecimal montoAdeudado = this.servicioPersona.obtenerMontoDeudaCliente(clienteSeleccionado.getId_cliente(), TipoCondicionVenta.CREDITO.getTipoCondicionVenta());

            if (montoAdeudado.compareTo(new BigDecimal("0.0")) > 0) {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.factura.advertencia.persona.con.deuda") + " " + montoAdeudado);
            }

            if (this.listaProductoPorFacturar.size() > 0) {
                this.listaProductoPorFacturar.forEach(elemento -> {
                    elemento.setPersonaAsociada(personaEdicionFactura);
                    elemento.setEsPersonaExenta(personaEdicionFactura.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador()));
                    elemento.setEsPersonaExonerada(personaEdicionFactura.getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador()));
                    elemento.calcularMontos();
                    // if (personaEdicionFactura.getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador())) {
                    //     elemento.setExoneracionLinea(new ExoneracionLinea());
                    // }
                });
            }
            if (personaModelo.getCliente().getId_tipo_cliente().equals(TipoCliente.POR_MAYOR.getIdTipoCliente())) {
                idCondicionVentaSeleccionado = TipoCondicionVenta.CREDITO.getTipoCondicionVenta();
                idMedioPagoSeleccionado = TiposMediosPago.TRANSFERENCIA_DEPÓSITO_BANCARIO.getIdMedioPago();
                lapsoTiempoCredito = "30";
            } else {
                idCondicionVentaSeleccionado = 0l;
                idMedioPagoSeleccionado = 0l;
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void buscarPersona(ActionEvent evt) {
        try {
            personaReceptor = new ModeloPersona();

            if (tipoIdentificacionSeleccionadaReceptor != null) {

                if (!tipoIdentificacionSeleccionadaReceptor.equals(0l)) {
                    if (identificacionReceptor != null) {
                        if (!identificacionReceptor.contains("_")) {
                            //Obtengo la persona relacionada a la búsqueda
                            Persona persona = this.servicioPersona.obtenerPersonaPorIdentificacion(identificacionReceptor, tipoIdentificacionSeleccionadaReceptor);
                            if (persona != null) {
                                personaReceptor = ModeloPersona.convertirPersonaAModeloPersona(persona);
                                clienteSeleccionado = this.servicioPersona.obtenerCliente(persona.getPersonaPK().getNumero_cedula());

                                personaReceptor.setListaCorreosPersona(servicioPersona.obtenerListaCorreosPersona(personaReceptor.getNumero_cedula(), personaReceptor.getId_tipo_cedula()));
                                this.listaPersonasCorreo = personaReceptor.getListaCorreosPersona();
                                if (this.listaPersonasCorreo != null) {
                                    if (this.listaPersonasCorreo.size() == 1) {
                                        this.correoSeleccionado = this.listaPersonasCorreo.get(0).getCorreo_electronico();
                                    }
                                }

                            } else {
                                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.aviso.no.encontro.persona"));
                            }
                        } else {
                            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.ingreso.cedula"));
                        }

                    } else {
                        MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.ingreso.cedula"));
                    }
                } else {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.ingreso.tipo.cedula"));
                }
//
            } else {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.ingreso.tipo.cedula"));
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    /**
     * Método que carga la información de los productos basados en su
     * descripción
     */
    public void buscarProductoPorDescripcion() {
        try {

            this.listaProductosBusqueda = servicioProducto.listarProductosPorDescripcion(nombreProducto);

        } catch (Exception ex) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.erorr.buscar.producto.descripcion"));
        }
    }

    public void agregarNUevoCliente() {
        this.verPanelNuevaPersona = true;

    }

    public ModeloProducto buscarProductoPorId(ModeloProducto productoSeleccionado) {
        ModeloProducto productoResultado = null;
        try {
            Producto productoEncontrado = this.servicioProducto.obtenerProducto(productoSeleccionado.getId_producto());
            if (productoEncontrado != null) {

                productoResultado = ModeloProducto.construirObjeto(productoEncontrado,
                        servicioProducto.obtenerUnidadMedidaProducto(productoEncontrado.getId_unidad_medida()),
                        servicioProducto.obtenerListaPreciosProducto(productoEncontrado.getId_producto()),
                        this.listaPrecios,
                        servicioProducto.obtenerExistenciaProducto(productoEncontrado.getId_producto()),
                        clienteSeleccionado,
                        this.listaTiposMonedas,
                        servicioProducto.obtenerTipoProducto(productoEncontrado.getId_tipo_producto()),
                        servicioProducto.obtenerImpuesto(productoEncontrado.getId_impuesto()),
                        null,
                        servicioProducto.obtenerTipoTarifaImpuesto(productoEncontrado.getId_tipo_tarifa_impuesto()),
                        personaEdicionFactura,
                        listaTiposTarifa);
                // productoResultado.setSalarioBase(parametro);
                productoResultado.setPermiteEliminar(true);
                productoResultado.setCantidadAAgregar(productoResultado.getCantidad());
                productoResultado.setNumeroLineaProducto(this.listaProductoPorFacturar.size() + 1);

                if (productoEncontrado.getId_unidad_medida().equals(TipoUnidadMedida.ALQUILER_HABITACIONAL.getUnidadMedida())) {
                    productoResultado.setSalarioBase(servicioParametro.obtenerValorParametro(ParametrosEnum.SALARIO_BASE.getIdParametro()));
                    productoResultado.setValorMultiplicacion(servicioParametro.obtenerValorParametro(ParametrosEnum.VALOR_MULTIPLICACION_ALQUILERES.getIdParametro()));
                }

            } else {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.no.encontrado"));
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return productoResultado;
    }

    public void buscarProducto() {
        try {

            if (busquedaCodigoBarras != null && !busquedaCodigoBarras.equals("")) {
                Producto productoEncontrado = null;

                productoEncontrado = servicioProducto.obtenerProductoPorCodigoBarras(busquedaCodigoBarras);

                if (productoEncontrado != null) {

                    ModeloProducto productoSeleccionadoLista = ModeloProducto.construirObjeto(productoEncontrado,
                            servicioProducto.obtenerUnidadMedidaProducto(productoEncontrado.getId_unidad_medida()),
                            servicioProducto.obtenerListaPreciosProducto(productoEncontrado.getId_producto()),
                            this.listaPrecios,
                            servicioProducto.obtenerExistenciaProducto(productoEncontrado.getId_producto()),
                            clienteSeleccionado,
                            this.listaTiposMonedas,
                            servicioProducto.obtenerTipoProducto(productoEncontrado.getId_tipo_producto()),
                            servicioProducto.obtenerImpuesto(productoEncontrado.getId_impuesto()),
                            null,
                            servicioProducto.obtenerTipoTarifaImpuesto(productoEncontrado.getId_tipo_tarifa_impuesto()),
                            personaEdicionFactura,
                            listaTiposTarifa);
                    productoSeleccionadoLista.setPermiteEliminar(true);
                    productoSeleccionadoLista.setCantidadAAgregar(productoSeleccionadoLista.getCantidad());
                    productoSeleccionadoLista.setNumeroLineaProducto(this.listaProductoPorFacturar.size() + 1);

                    if (productoEncontrado.getId_unidad_medida().equals(TipoUnidadMedida.ALQUILER_HABITACIONAL.getUnidadMedida())) {
                        productoSeleccionadoLista.setSalarioBase(servicioParametro.obtenerValorParametro(ParametrosEnum.SALARIO_BASE.getIdParametro()));
                        productoSeleccionadoLista.setValorMultiplicacion(servicioParametro.obtenerValorParametro(ParametrosEnum.VALOR_MULTIPLICACION_ALQUILERES.getIdParametro()));
                    }

                    if (!this.idTipoFacturaSeleccionada.equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura()) && parametro.getValor().equals("1")) {

                        //Obtengo la existencia de inventario
                        List<Inventario> listaResultado = this.servicioInventario.consultarInventProductoPorCantidad(Utilitario.obtenerIdBodegaUsuario(),
                                productoSeleccionadoLista.getId_producto(), productoSeleccionadoLista.getCantidad().longValue(), true);
                        //Valida si existe suficiente cantidad
                        if (!existeCantidadProductoSuficiente(productoSeleccionadoLista.getId_producto(), productoSeleccionadoLista.getCantidad().longValue(), listaResultado)) {

                            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.no.existe.no.tiene.cantidad") + "  Existen " + listaResultado.stream()
                                    .mapToLong(o -> o.getCantExistencia())
                                    .sum());

                        } else {
                            productoSeleccionadoLista.setListaExistencias(servicioProducto.obtenerExistenciaProducto(productoSeleccionadoLista.getId_producto()));
                            listaProductoPorFacturar.add(productoSeleccionadoLista);
                        }

                    } else if (!parametro.getValor().equals("1")) {
                        productoSeleccionadoLista.setListaExistencias(servicioProducto.obtenerExistenciaProducto(productoSeleccionadoLista.getId_producto()));
                        listaProductoPorFacturar.add(productoSeleccionadoLista);
                    }
                } else {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.no.encontrado"));
                }
                busquedaCodigoBarras = "";
            } else {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.buscar.producto.codigo.barras.facturacion"));
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void guardarFactura() {
        try {
            if (validarFactura()) {

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                //  List<InventarioSalidaDetalle> listaSalidaDetalle = new ArrayList<>();
                if (esNuevaFactura) {

                    facturaNueva = new Factura();
                    facturaNueva.setCant_dias_vigencia(cantidadDiasVigencia);
                    facturaNueva.setCodigo_situacion_comprobante(this.informacionReferenciaDocumento.getTipoDoc());
                    if (!this.informacionReferenciaDocumento.getTipoDoc().equals(SituacionComprobante.SITUACION_NORMAL.getSituacion())) {

                        facturaNueva.setCodigo_referencia(this.informacionReferenciaDocumento.getCodigo());
                        facturaNueva.setCodigo_documento_referencia(this.informacionReferenciaDocumento.getTipoDocumentoReferencia());
                        facturaNueva.setFecha_documento_referencia(Utilitario.dateToRFC3339(this.informacionReferenciaDocumento.getFecha()));
                        facturaNueva.setRazon_documento_referencia(this.informacionReferenciaDocumento.getRazon());
                        facturaNueva.setNumero_factura_documento_referencia(this.informacionReferenciaDocumento.getNumero());
                        if (this.informacionReferenciaDocumento.getIdFacturaReferencia() != null) {
                            facturaNueva.setId_factura_referencia(this.informacionReferenciaDocumento.getIdFacturaReferencia());
                        }

                    }
                    facturaNueva.setFactura_cancelada(CancelacionFactura.PENDIENTE_PAGO.getFacturaCancelada());
                    DetalleFactura detalleFactura = new DetalleFactura();
                    DetalleFacturaPK detalleFacturaPK = new DetalleFacturaPK();
                    List<DetalleFactura> listaDetalleFactura = new ArrayList<>();

                    if (clienteSeleccionado != null) {
                        facturaNueva.setId_cliente(clienteSeleccionado.getId_cliente());
                        personaEdicionFactura.setIdCodigoActividad(this.idActvidadSeleccionada);
                        this.servicioPersona.actualizarPersona(personaEdicionFactura);

                    } else {
                        facturaNueva.setId_cliente(null);
                    }
                    facturaNueva.setPlazo_credito(idCondicionVentaSeleccionado.equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta()) ? this.lapsoTiempoCredito : null);
                    facturaNueva.setLogin(usuarioLogueado.getLogin());
                    facturaNueva.setTotal_descuentos(getTotalDescuentos());
                    facturaNueva.setTotal_impuestos(getTotalImpuestos());
                    facturaNueva.setTotal_venta_neta(getTotalVentaNeta());
                    facturaNueva.setTotal_venta(getTotalVenta());
                    facturaNueva.setTotal_servicios_grabados(getTotalGravadosServicios());
                    facturaNueva.setTotal_servicios_exentos(getTotalExentosServicios());
                    facturaNueva.setTotal_mercancias_gravadas(getTotalGravadosMercancias());
                    facturaNueva.setTotal_mercancias_exentas(getTotalExentosMercancias());
                    facturaNueva.setTotal_gravado(getTotalGravados());
                    facturaNueva.setTotal_exento(getTotalExentos());
                    facturaNueva.setTotal_servicios_exonerados(getTotalExoneradosServicios());
                    facturaNueva.setTotal_mercancias_exonerados(getTotalExoneradosMercancias());
                    facturaNueva.setTotal_exonerado(getTotalExonerados());
                    facturaNueva.setTotal_comprobante(getTotalFactura());
                    facturaNueva.setId_cond_venta(idCondicionVentaSeleccionado);
                    facturaNueva.setId_medio_pago(idMedioPagoSeleccionado);
                    facturaNueva.setEstado_factura(EstadoFactura.EN_DESARROLLO.getEstadoFactura());
                    facturaNueva.setEnvio_correo_electronico(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
                    facturaNueva.setEnvio_respuesta_hacienda(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
                    facturaNueva.setNombre_cliente_fantasia(nombreClienteFantasia);
                    facturaNueva.setId_tipo_factura(this.idTipoFacturaSeleccionada);
                    facturaNueva.setDescuento_aplicado(descuentoFactura);

                    if (this.correoElectronicoFactura != null) {
                        if (!this.correoElectronicoFactura.trim().equals("")) {
                            facturaNueva.setCorreo_electronico(this.correoElectronicoFactura.trim());
                        }
                    }
                    if (this.correoSeleccionado != null) {
                        if (!this.correoSeleccionado.trim().equals("0")) {
                            facturaNueva.setCorreo_electronico_cliente(this.correoSeleccionado.trim());
                        }
                    }

                    Integer numeroLinea = 1;

                    for (ModeloProducto modeloProducto : listaProductoPorFacturar) {

                        detalleFactura.setCantidad(modeloProducto.getCantidad());
                        detalleFactura.setPrecio_neto(modeloProducto.getPrecio());
                        detalleFactura.setId_moneda(modeloProducto.getTipoMoneda().getId_moneda());
                        detalleFactura.setTipo_cambio(modeloProducto.getTipoCambio());
                        detalleFactura.setDescuento(modeloProducto.getDescuento());
                        detalleFactura.setTotal_descuento(modeloProducto.getMontoDescuento());
                        detalleFactura.setTotal_impuestos(modeloProducto.getTotalImpuesto());
                        detalleFactura.setMonto_total(modeloProducto.getMontoTotal());

                        detalleFactura.setTotal_exonerado(modeloProducto.getTotalExoneracion());
                        detalleFactura.setTotal_impuesto_neto(modeloProducto.getTotalImpuestoNeto());

                        detalleFactura.setSub_total(modeloProducto.getSubTotal());
                        detalleFactura.setId_tipo_precio(modeloProducto.getIdTipPrecioSeleccionado());
                        detalleFactura.setTotal_linea(modeloProducto.getMontoTotalLinea());
                        detalleFactura.setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO_QUITAR.getEstadoLineaDetalle());
                        detalleFactura.setEs_para_nota_debito(LineaDetalleEstado.PARA_NOTA_DEBITO_QUITAR.getEstadoLineaDetalle());
                        detalleFactura.setId_estado(EstadosLineasFactura.NORMAL.getEstadoLineaFactura());
                        detalleFactura.setId_tipo_tarifa(modeloProducto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador()) ? null : modeloProducto.getIdTipoTarifaSeleccionada());

                        if (modeloProducto.isEsPersonaExonerada()) {
                            if (modeloProducto.getExoneracionLinea() != null) {
                                detalleFactura.setEs_linea_exonerada(Indicadores.EXONERADO_SI.getIndicador());

                                detalleFactura.setId_exoneracion(modeloProducto.getExoneracionLinea().getId_exoneracion());
                                detalleFactura.setNumero_documento(modeloProducto.getExoneracionLinea().getNumero_documento());
                                detalleFactura.setNombre_institucion(modeloProducto.getExoneracionLinea().getNombre_institucion());
                                detalleFactura.setFecha_emision(Utilitario.dateToRFC3339(modeloProducto.getExoneracionLinea().getFecha_emision()));
                                detalleFactura.setPorcentaje_exonerado(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado());
                            } else {
                                detalleFactura.setEs_linea_exonerada(Indicadores.EXONERADO_NO.getIndicador());
                            }
                        } else {
                            detalleFactura.setEs_linea_exonerada(Indicadores.EXONERADO_NO.getIndicador());
                        }
                        if (modeloProducto.isEsPersonaExenta()) {
                            detalleFactura.setEs_linea_exenta(Indicadores.EXENTO_SI.getIndicador());
                        } else {
                            detalleFactura.setEs_linea_exenta(modeloProducto.getProducto().getInd_exento());
                            detalleFactura.setId_impuesto(modeloProducto.getImpuesto() == null ? null : modeloProducto.getImpuesto().getId_impuesto());
                        }
                        detalleFactura.setDescripcion(modeloProducto.getDescripcionLineaProducto());
                        detalleFacturaPK.setId_producto(modeloProducto.getId_producto());
                        detalleFacturaPK.setNumero_linea(numeroLinea);
                        detalleFactura.setDetallePK(detalleFacturaPK);

                        listaDetalleFactura.add(detalleFactura);
                        detalleFactura = new DetalleFactura();
                        detalleFacturaPK = new DetalleFacturaPK();
                        modeloProducto.setNumeroLineaProducto(numeroLinea);

                        numeroLinea++;
                    }

                    if (facturaNueva.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()) || facturaNueva.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) {
                        facturaNueva.setEstado_factura(EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura());
                        esFacturaElectronicaGuardo = true;
                    }

                    facturaNueva.setIp(JSFUtil.obtenerIpComputadora());
                    facturaNueva.setNombre_estacion(JSFUtil.obtenerNombreMaquina());
                    facturaNueva.setId_bodega(Utilitario.obtenerIdBodegaUsuario());
                    facturaNueva.setAgente(agenteSeleccionado);
                    //facturaNueva.setListaCorreosFacturas(this.crearFacturaCorreo());
                    facturaNueva = servicioFactura.guardarFactura(facturaNueva, listaDetalleFactura, this.parametro);

                } else {

                    System.out.println("Entro a actualizar la factura " + facturaNueva.getId_factura());

                    facturaNueva.setCant_dias_vigencia(cantidadDiasVigencia);
                    facturaNueva.setFactura_cancelada(CancelacionFactura.PENDIENTE_PAGO.getFacturaCancelada());
                    facturaNueva.setCodigo_situacion_comprobante(this.informacionReferenciaDocumento.getTipoDoc());
                    if (!this.informacionReferenciaDocumento.getTipoDoc().equals(SituacionComprobante.SITUACION_NORMAL.getSituacion())) {
                        facturaNueva.setCodigo_referencia(this.informacionReferenciaDocumento.getCodigo());
                        facturaNueva.setCodigo_documento_referencia(this.informacionReferenciaDocumento.getTipoDocumentoReferencia());
                        facturaNueva.setFecha_documento_referencia(df.format(this.informacionReferenciaDocumento.getFecha()));
                        facturaNueva.setRazon_documento_referencia(this.informacionReferenciaDocumento.getRazon());
                        facturaNueva.setNumero_factura_documento_referencia(this.informacionReferenciaDocumento.getNumero());
                        if (this.informacionReferenciaDocumento.getIdFacturaReferencia() != null) {
                            facturaNueva.setId_factura_referencia(this.informacionReferenciaDocumento.getIdFacturaReferencia());
                        }
                    }

                    DetalleFactura detalleFactura = new DetalleFactura();
                    DetalleFacturaPK detalleFacturaPK = new DetalleFacturaPK();
                    List<DetalleFactura> listaDetalleFactura = new ArrayList<>();

                    if (clienteSeleccionado != null) {
                        facturaNueva.setId_cliente(clienteSeleccionado.getId_cliente());
                        personaEdicionFactura.setIdCodigoActividad(this.idActvidadSeleccionada);
                        this.servicioPersona.actualizarPersona(personaEdicionFactura);
                    } else {
                        facturaNueva.setId_cliente(null);
                    }
                    facturaNueva.setPlazo_credito(idCondicionVentaSeleccionado.equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta()) ? this.lapsoTiempoCredito : null);
                    facturaNueva.setLogin(usuarioLogueado.getLogin());
                    facturaNueva.setTotal_descuentos(getTotalDescuentos());
                    facturaNueva.setTotal_impuestos(getTotalImpuestos());
                    facturaNueva.setTotal_venta_neta(getTotalVentaNeta());
                    facturaNueva.setTotal_venta(getTotalVenta());
                    facturaNueva.setTotal_servicios_grabados(getTotalGravadosServicios());
                    facturaNueva.setTotal_servicios_exentos(getTotalExentosServicios());
                    facturaNueva.setTotal_mercancias_gravadas(getTotalGravadosMercancias());
                    facturaNueva.setTotal_mercancias_exentas(getTotalExentosMercancias());
                    facturaNueva.setTotal_gravado(getTotalGravados());
                    facturaNueva.setTotal_exento(getTotalExentos());
                    facturaNueva.setTotal_servicios_exonerados(getTotalExoneradosServicios());
                    facturaNueva.setTotal_mercancias_exonerados(getTotalExoneradosMercancias());
                    facturaNueva.setTotal_exonerado(getTotalExonerados());
                    facturaNueva.setTotal_comprobante(getTotalFactura());
                    facturaNueva.setId_cond_venta(idCondicionVentaSeleccionado);
                    facturaNueva.setId_medio_pago(idMedioPagoSeleccionado);
                    if (edicionFacturaPuedeGenerarConsec) {
                        facturaNueva.setEstado_factura(EstadoFactura.EN_DESARROLLO.getEstadoFactura());
                    }
                    facturaNueva.setEnvio_correo_electronico(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
                    facturaNueva.setEnvio_respuesta_hacienda(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
                    facturaNueva.setNombre_cliente_fantasia(nombreClienteFantasia);
                    facturaNueva.setId_tipo_factura(this.idTipoFacturaSeleccionada);

                    facturaNueva.setDescuento_aplicado(descuentoFactura);

                    if (this.correoElectronicoFactura != null) {
                        if (!this.correoElectronicoFactura.trim().equals("")) {
                            facturaNueva.setCorreo_electronico(this.correoElectronicoFactura.trim());
                        }
                    }
                    if (this.correoSeleccionado != null) {
                        if (!this.correoSeleccionado.trim().equals("0")) {
                            facturaNueva.setCorreo_electronico_cliente(this.correoSeleccionado.trim());
                        }
                    }

                    Integer numeroLinea = servicioFactura.obtenerProximoNumeroLinea(facturaNueva.getId_factura()) + 1;
                    for (ModeloProducto modeloProducto : listaProductoPorFacturar) {
                        modeloProducto.setIdFactura(facturaNueva.getId_factura());
                        if (modeloProducto.isPermiteEliminar()) {
                            detalleFactura.setCantidad(modeloProducto.getCantidad());
                            detalleFactura.setPrecio_neto(modeloProducto.getPrecio());
                            detalleFactura.setId_moneda(modeloProducto.getTipoMoneda().getId_moneda());
                            detalleFactura.setTipo_cambio(modeloProducto.getTipoCambio());
                            detalleFactura.setDescuento(modeloProducto.getDescuento());
                            detalleFactura.setTotal_descuento(modeloProducto.getMontoDescuento());
                            detalleFactura.setMonto_total(modeloProducto.getMontoTotal());

                            detalleFactura.setTotal_impuestos(modeloProducto.getTotalImpuesto());
                            detalleFactura.setTotal_exonerado(modeloProducto.getTotalExoneracion());
                            detalleFactura.setTotal_impuesto_neto(modeloProducto.getTotalImpuestoNeto());
                            detalleFactura.setSub_total(modeloProducto.getSubTotal());
                            detalleFactura.setId_tipo_precio(modeloProducto.getIdTipPrecioSeleccionado());
                            detalleFactura.setTotal_linea(modeloProducto.getMontoTotalLinea());
                            detalleFactura.setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO_QUITAR.getEstadoLineaDetalle());
                            detalleFactura.setId_tipo_tarifa(modeloProducto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador()) ? null : modeloProducto.getIdTipoTarifaSeleccionada());

                            if (puedeAgregarLineasNotaDebito) {
                                modeloProducto.setIndicadorEsParaNotaDebito(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle());
                                detalleFactura.setEs_para_nota_debito(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle());
                                detalleFactura.setId_estado(EstadosLineasFactura.PENDIENTE_ENVIO.getEstadoLineaFactura());
                            } else {
                                modeloProducto.setIndicadorEsParaNotaDebito(LineaDetalleEstado.PARA_NOTA_DEBITO_QUITAR.getEstadoLineaDetalle());
                                detalleFactura.setEs_para_nota_debito(LineaDetalleEstado.PARA_NOTA_DEBITO_QUITAR.getEstadoLineaDetalle());
                                detalleFactura.setId_estado(EstadosLineasFactura.NORMAL.getEstadoLineaFactura());
                            }
                            if (modeloProducto.isEsPersonaExonerada()) {
                                if (modeloProducto.getExoneracionLinea() != null) {
                                    detalleFactura.setEs_linea_exonerada(Indicadores.EXONERADO_SI.getIndicador());

                                    detalleFactura.setId_exoneracion(modeloProducto.getExoneracionLinea().getId_exoneracion());
                                    detalleFactura.setNumero_documento(modeloProducto.getExoneracionLinea().getNumero_documento());
                                    detalleFactura.setNombre_institucion(modeloProducto.getExoneracionLinea().getNombre_institucion());
                                    detalleFactura.setFecha_emision(Utilitario.dateToRFC3339(modeloProducto.getExoneracionLinea().getFecha_emision()));
                                    detalleFactura.setPorcentaje_exonerado(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado());
                                }
                            } else {
                                detalleFactura.setEs_linea_exonerada(Indicadores.EXONERADO_NO.getIndicador());
                            }
                            if (modeloProducto.isEsPersonaExenta()) {
                                detalleFactura.setEs_linea_exenta(Indicadores.EXENTO_SI.getIndicador());
                            } else {
                                detalleFactura.setEs_linea_exenta(modeloProducto.getProducto().getInd_exento());
                                detalleFactura.setId_impuesto(modeloProducto.getImpuesto() == null ? null : modeloProducto.getImpuesto().getId_impuesto());
                            }
                            detalleFactura.setDescripcion(modeloProducto.getDescripcionLineaProducto());
                            detalleFacturaPK.setId_producto(modeloProducto.getId_producto());
                            detalleFacturaPK.setNumero_linea(numeroLinea);
                            detalleFactura.setDetallePK(detalleFacturaPK);

                            listaDetalleFactura.add(detalleFactura);
                            detalleFactura = new DetalleFactura();
                            detalleFacturaPK = new DetalleFacturaPK();
                            modeloProducto.setNumeroLineaProducto(numeroLinea);

                            numeroLinea++;
                        } else {
                            modeloProducto.setIndicadorEsParaNotaDebito(LineaDetalleEstado.PARA_NOTA_DEBITO_QUITAR.getEstadoLineaDetalle());
                        }
                    }

                    if ((facturaNueva.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura())
                            || facturaNueva.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) && edicionFacturaPuedeGenerarConsec) {

                        facturaNueva.setEstado_factura(EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura());

                        esFacturaElectronicaGuardo = true;
                        edicionFacturaPuedeGenerarConsec = false;
                    }
                    facturaNueva.setIp_actualizacion(JSFUtil.obtenerIpComputadora());
                    facturaNueva.setNombre_estacion_actualizacion(JSFUtil.obtenerNombreMaquina());
                    facturaNueva.setId_bodega_actualiza(Utilitario.obtenerIdBodegaUsuario());
                    facturaNueva.setAgente(agenteSeleccionado);
                    //facturaNueva.setListaCorreosFacturas(this.crearFacturaCorreo());

                    facturaNueva = servicioFactura.actualizarFactura(facturaNueva, listaDetalleFactura, !puedeAgregarLineasNotaDebito,
                            puedeAgregarLineasNotaDebito, motivoDebitoSeleccionado, motivoDebito, tipoDocumentoReferenciaSeleccionado, listaProductoPorFacturar,
                            this.parametro
                    );

                }

                FacturaHistoricoHacienda historico = new FacturaHistoricoHacienda();
                historico.setId_factura(facturaNueva.getId_factura());
                historico.setFecha(new Date());
                historico.setEstado_factura(facturaNueva.getEstado_factura());
                historico.setRespuesta(esNuevaFactura ? "Nueva Factura/Pedido/Cotizacion" : "Factura/Pedido/Cotizacion Actualizado");
                historico.setLogin(usuarioLogueado.getLogin());
                servicioFactura.guardarHistorico(historico);

                this.inhabilitarComponentes = true;
                guardoCorrectamenta = true;
                this.envioFactura = false;

                if (this.esFacturaElectronicaGuardo) {
                    this.envioFactura = true;
                }

                String mensajeMOstrar = "Se ha guardo correctamente: ";
                if (idTipoFacturaSeleccionada.equals(TipoFacturaEnum.FACTURA.getIdTipoFactura())) {
                    mensajeMOstrar = mensajeMOstrar + "Factura Electrónica: " + facturaNueva.getNumero_consecutivo();
                } else if (idTipoFacturaSeleccionada.equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura())) {
                    mensajeMOstrar = mensajeMOstrar + "Proforma: " + facturaNueva.getId_factura();
                } else if (idTipoFacturaSeleccionada.equals(TipoFacturaEnum.PEDIDO.getIdTipoFactura())) {
                    mensajeMOstrar = mensajeMOstrar + "Pedido: " + facturaNueva.getId_factura();
                } else if (idTipoFacturaSeleccionada.equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) {
                    mensajeMOstrar = mensajeMOstrar + "Tiquete Electrónico: " + facturaNueva.getNumero_consecutivo();
                }
                MensajeUtil.agregarMensajeInfo(mensajeMOstrar);

                deshabilitaBotonNuevo = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public List<FacturaCorreo> crearFacturaCorreo() {
        List<FacturaCorreo> resultado = new ArrayList<>();

        resultado.add(new FacturaCorreo(personaReceptor.getCorreo_electronico()));
        resultado.add(new FacturaCorreo(correoSeleccionado));
        return resultado;
    }

    public boolean validarFactura() {

        boolean resultado = true;

        if (this.listaProductoPorFacturar != null) {
            if (this.listaProductoPorFacturar.size() <= 0) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.lista.productos.vacia"));
                return resultado;
            }
        } else {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.lista.productos.vacia"));
            return resultado;
        }

        if (clienteSeleccionado != null) {
            if (idActvidadSeleccionada == null || idActvidadSeleccionada.equals(0l)) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar la actividad");
                return resultado;
            }
        }

        if (idCondicionVentaSeleccionado == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validar.condicion.venta"));
            return resultado;
        } else if (idCondicionVentaSeleccionado.equals(0l)) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validar.condicion.venta"));
            return resultado;
        }
        if (idCondicionVentaSeleccionado.equals(TipoCondicionVenta.CREDITO.getTipoCondicionVenta())) {
            if (lapsoTiempoCredito.equals("0")) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validar.condicion.venta"));
                return resultado;
            }
        }

        if (idMedioPagoSeleccionado == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validar.medio.pago"));
            return resultado;
        } else if (idMedioPagoSeleccionado.equals(0l)) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validar.medio.pago"));
            return resultado;
        }
        if (idTipoFacturaSeleccionada == null) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensje.validacion.factura.tipo.factura"));
            return resultado;
        } else if (idTipoFacturaSeleccionada.equals(0l)) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensje.validacion.factura.tipo.factura"));
            return resultado;
        }

        if (!this.informacionReferenciaDocumento.getTipoDoc().equals(SituacionComprobante.SITUACION_NORMAL.getSituacion())) {

            if (this.informacionReferenciaDocumento.getNumero() == null || (this.informacionReferenciaDocumento.getNumero().equals(""))) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.factura.docuemto.referencia.numero.factura"));
                return resultado;
            }
            if (this.informacionReferenciaDocumento.getRazon() == null || (this.informacionReferenciaDocumento.getRazon().equals(""))) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.factura.docuemto.referencia.razon"));
                return resultado;
            }
            if (this.informacionReferenciaDocumento.getCodigo().equals("00")) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia("Debe seleccionar el código de referencia");
                return resultado;
            }

            if (this.informacionReferenciaDocumento.getTipoDocumentoReferencia().equals("00")) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia("Debe ingresar el tipo de documento de referencia");
                return resultado;
            }

        }
        boolean lineasConProblemas = false;
        for (ModeloProducto modeloProducto : this.listaProductoPorFacturar) {
            if (modeloProducto.getPresentarProblemaInventario().equals(1)) {
                lineasConProblemas = true;
                break;
            }
        }
        if (lineasConProblemas) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.lienas.con.problemas"));
            return resultado;
        }
        boolean existenNuevasLineas = false;
        if (puedeAgregarLineasNotaDebito) {

            for (ModeloProducto productoFacturar : this.listaProductoPorFacturar) {
                if (productoFacturar.isPermiteEliminar()) {
                    existenNuevasLineas = true;
                    break;
                }
            }
            if (existenNuevasLineas) {
                if (motivoDebito == null || motivoDebito.equals("")) {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.valudacion.debe.agregar.motivo.debito"));
                    resultado = false;
                }
                if (this.motivoDebitoSeleccionado.equals(0l)) {
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.valudacion.debe.agregar.motivo.debito.lista"));
                    resultado = false;
                }
            }
        }
        return resultado;
    }

    public void generarReporteFactura() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idFactura", facturaNueva.getId_factura());
            parametros.put("simbolo", "CRC");
            parametros.put("montoLetras", Utilitario.Convertir(facturaNueva.getTotal_comprobante().toString(), true));
            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode(facturaNueva.getClave() == null ? facturaNueva.getId_factura().toString() : facturaNueva.getClave()))));
            reporte = servicioReporte.generarReporte(Reportes.FACTURA, TiposMimeTypes.PDF, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.FACTURA.getNombreReporte() + "-" + (facturaNueva.getNumero_consecutivo() == null ? facturaNueva.getId_factura() : facturaNueva.getNumero_consecutivo()) + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException | IOException ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        } catch (WriterException ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void generarReporteFacturaFacturaElectronicaPuntoVenta() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idFactura", facturaNueva.getId_factura());
            parametros.put("simbolo", "CRC");
            parametros.put("montoLetras", Utilitario.Convertir(facturaNueva.getTotal_comprobante().toString(), true));
            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode(facturaNueva.getClave() == null ? "" : facturaNueva.getClave()))));
            reporte = servicioReporte.generarReporte(Reportes.REPORTE_FACTURA_PUNTO_VENTA_TRAZABILIDAD, TiposMimeTypes.PDF, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.REPORTE_FACTURA_PUNTO_VENTA_TRAZABILIDAD.getNombreReporte() + "-" + (facturaNueva.getNumero_consecutivo() == null ? facturaNueva.getId_factura() : facturaNueva.getNumero_consecutivo()) + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException | IOException ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        } catch (WriterException ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void generarReporteFacturaFacturaElectronica() {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idFactura", facturaNueva.getId_factura());
            parametros.put("simbolo", "CRC");
            parametros.put("montoLetras", Utilitario.Convertir(facturaNueva.getTotal_comprobante().toString(), true));
            parametros.put("QRCode", ImageIO.read(new ByteArrayInputStream(Utilitario.generarQRCode(facturaNueva.getClave() == null ? "" : facturaNueva.getClave()))));
            reporte = servicioReporte.generarReporte(Reportes.FACTURA, TiposMimeTypes.PDF, parametros, false);
            InputStream input = new ByteArrayInputStream(reporte);
            reporteDesplegar = new DefaultStreamedContent(input, TiposMimeTypes.PDF.getMimeType(), (Reportes.FACTURA.getNombreReporte() + "-" + (facturaNueva.getNumero_consecutivo() == null ? facturaNueva.getId_factura() : facturaNueva.getNumero_consecutivo()) + "." + TiposMimeTypes.PDF.getExtension()));
        } catch (JRException | IOException ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        } catch (WriterException ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public StreamedContent getReporteDesplegar() {
        return reporteDesplegar;
    }

    /**
     * Método que determina si un producto ya fue o no ingresado a la lista de
     * productos
     *
     * @param productoEncontrado
     * @return boolean
     */
    public boolean validarProductoYaIngresado(Producto productoEncontrado) {
        boolean resultado = false;
        for (ModeloProducto modeloProducto : listaProductoPorFacturar) {
            if (productoEncontrado.getId_producto().equals(modeloProducto.getId_producto())) {
                resultado = true;
            }
        }
        return resultado;
    }

    public void seleccionarProducto(SelectEvent event) {
        try {
            this.busquedaCodigoBarras = valorproducto.split("#")[1];
            this.buscarProducto();
            this.nombreProducto = "";
            this.valorproducto = null;

        } catch (Exception e) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.seleccion.precio.facturacin"));
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public ModeloProducto obtenerProductoListaDetalle(Long idProducto) {
        ModeloProducto producto = null;
        for (ModeloProducto modeloProducto : listaProductoPorFacturar) {
            if (modeloProducto.getId_producto().equals(idProducto)) {
                producto = modeloProducto;
                break;
            }
        }
        return producto;
    }

    public void reemplazarLineaListaProducto(ModeloProducto productoNuevo) {
        for (ModeloProducto modeloProducto : listaProductoPorFacturar) {
            if (modeloProducto.getId_producto().equals(productoNuevo.getId_producto())
                    && modeloProducto.getNumeroLineaProducto().equals(productoNuevo.getNumeroLineaProducto())) {
                modeloProducto = productoNuevo;
                break;
            }
        }
    }

    /**
     * Método que determina si un producto tiene suficiente cantidad en
     * inventario
     *
     * @param idProducto
     * @param cantidad
     * @param listaInventariosProducto
     * @return boolean
     */
    public boolean existeCantidadProductoSuficiente(Long idProducto, Long cantidad, List<Inventario> listaInventariosProducto) {
        boolean resultado = false;
        Long cantidadInventario = 0L;

        for (Inventario inventario : listaInventariosProducto) {
            cantidadInventario = cantidadInventario + inventario.getCantExistencia();

            if (cantidadInventario >= cantidad) {
                resultado = true;
                break;
            }
        }
        return resultado;
    }

    public void onRowSelectBusquedaProductosDescripcion(SelectEvent event) {

        try {
            //DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            ModeloProducto productoSeleccionadoLista = (ModeloProducto) event.getObject();
            productoSeleccionadoLista = buscarProductoPorId(productoSeleccionadoLista);

            //calculo montos
            this.calcularMontos(productoSeleccionadoLista);

            this.nombreProducto = "";
            this.verPanelBuscarProducto = false;

            if (!this.idTipoFacturaSeleccionada.equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura()) && parametro.getValor().equals("1")) {
                //Obtengo la existencia de inventario
                List<Inventario> listaResultado = this.servicioInventario.consultarInventProductoPorCantidad(Utilitario.obtenerIdBodegaUsuario(),
                        productoSeleccionadoLista.getId_producto(), productoSeleccionadoLista.getCantidad().longValue(), true);
                //Valida si existe suficiente cantidad
                if (!existeCantidadProductoSuficiente(productoSeleccionadoLista.getId_producto(), productoSeleccionadoLista.getCantidad().longValue(), listaResultado)) {

                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.no.existe.no.tiene.cantidad") + "  Existen " + listaResultado.stream()
                            .mapToLong(o -> o.getCantExistencia())
                            .sum());

                } else {
                    productoSeleccionadoLista.setListaExistencias(servicioProducto.obtenerExistenciaProducto(productoSeleccionadoLista.getId_producto()));
                    listaProductoPorFacturar.add(productoSeleccionadoLista);
                }
            } else {
                productoSeleccionadoLista.setListaExistencias(servicioProducto.obtenerExistenciaProducto(productoSeleccionadoLista.getId_producto()));
                listaProductoPorFacturar.add(productoSeleccionadoLista);
            }
        } catch (Exception e) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.seleccion.precio.facturacin"));
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public void seleccionarTipoTarifa(ModeloProducto productoSeleccionado) {

        this.listaProductoPorFacturar.forEach(elemento -> {
            if (elemento.getId_producto().equals(productoSeleccionado.getId_producto())
                    && elemento.getNumeroLineaProducto().equals(productoSeleccionado.getNumeroLineaProducto())) {
                elemento.cambiarTipoTarifa();
            }
            elemento.calcularMontos();
        });
    }

    public void seleccionarTipoPrecioProducto(ModeloProducto productoSeleccionado) {
        try {
            this.productoSeleccionado = productoSeleccionado;
            Long idTipoPrecioSeleccionada = this.productoSeleccionado.getIdTipPrecioSeleccionado();

            this.inhabilitarModificacionPrecio = true;
            for (ModeloTipoPrecio modeloTipoPrecio : this.productoSeleccionado.getListaTiposPrecio()) {
                if (modeloTipoPrecio.getId_tipo().equals(idTipoPrecioSeleccionada)) {

                    if (idTipoPrecioSeleccionada.equals(TiposPrecios.PRECIO_ESPECIAL.getTipoPrecio())) {
                        this.inhabilitarModificacionPrecio = false;
                    }
                    this.productoSeleccionado.setPrecio(modeloTipoPrecio.getPrecio().setScale(3, BigDecimal.ROUND_HALF_EVEN));
                    this.productoSeleccionado.setMoneda(modeloTipoPrecio.getDescripcionTipoMoneda());
                    this.productoSeleccionado.setTipoCambio(modeloTipoPrecio.getTipo_cambio());
                    this.productoSeleccionado.setSimbolo(modeloTipoPrecio.getSimbolo());
                    this.productoSeleccionado.setIdTipPrecioSeleccionado(modeloTipoPrecio.getId_tipo());
                    this.productoSeleccionado.calcularMontos();
                }
            }

            if (idTipoPrecioSeleccionada.equals(TiposPrecios.PRECIO_ESPECIAL.getTipoPrecio())) {
                this.inhabilitarModificacionPrecio = false;
            }

        } catch (Exception e) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.seleccion.precio.facturacin"));
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public void onRowSelect(SelectEvent event) {
        try {
            this.precioSeleccionado = (ModeloTipoPrecio) event.getObject();

            this.productoSeleccionado.setPrecio(this.precioSeleccionado.getPrecio());
            this.productoSeleccionado.setMoneda(this.precioSeleccionado.getDescripcionTipoMoneda());
            this.productoSeleccionado.setTipoCambio(this.precioSeleccionado.getTipo_cambio());
            this.productoSeleccionado.setSimbolo(this.precioSeleccionado.getSimbolo());
            this.productoSeleccionado.setIdTipPrecioSeleccionado(this.precioSeleccionado.getId_tipo());
            this.productoSeleccionado.calcularMontoTotal();
            this.productoSeleccionado.calcularDescuento();
            this.productoSeleccionado.calcularSubTotal();
            this.productoSeleccionado.calcularImpuesto();
            this.productoSeleccionado.calcularMontoTotalLinea();

            this.precioSeleccionado = null;
        } catch (Exception e) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.seleccion.precio.facturacin"));
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public void eliminarRegistroTablaProducto(ModeloProducto producto) {
        try {
            //En caso de que sea una factura de tipo cotizacion no gnera nada en inventario

            if (producto.isPermiteEliminar()) {
                this.listaProductoPorFacturar.remove(producto);
            } else {
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.advertencia.permite.elimilnar.producto"));
            }

        } catch (Exception e) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.seleccion.eliminar.producto"));
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public void agregarDescripcionLinea() {
        for (ModeloProducto modeloProducto : listaProductoPorFacturar) {
            if (modeloProducto.getId_producto().equals(productoSeleccionado.getId_producto()) && modeloProducto.getNumeroLineaProducto().equals(productoSeleccionado.getNumeroLineaProducto())) {
                modeloProducto.setDescripcionLineaProducto(productoSeleccionado.getDescripcionLineaProducto());
            }
        }
    }

    public void quitarClienteFactura() {
        clienteSeleccionado = null;
        personaReceptor = new ModeloPersona();
        mascaraSeleccionada = TiposCedulaMascaras.CEDULA_FISICA.getMascara();
        tamannoCedula = TiposCedulaMascaras.CEDULA_FISICA.getTamannoTipoCedula();
        personaReceptor = new ModeloPersona();
        this.identificacionReceptor = "";
        this.tipoIdentificacionSeleccionadaReceptor = 0l;

        this.listaProductoPorFacturar.forEach(elemento -> {
            elemento.setPersonaAsociada(null);
            elemento.setEsPersonaExenta(false);
            elemento.setEsPersonaExonerada(false);
            elemento.calcularMontos();
        });

    }

    public void generarLineaDetalleCantidadProducto(ModeloProducto producto) {
        try {

            Long cantidadExistente = 0L;

            //En caso de que sea una factura de tipo cotizacion no gnera nada en inventario
            if (!this.idTipoFacturaSeleccionada.equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura()) && parametro.getValor().equals("1")) {
                //Obtengo la existencia de inventario
                List<Inventario> listaResultado = this.servicioInventario.consultarInventProductoPorCantidad(Utilitario.obtenerIdBodegaUsuario(),
                        producto.getId_producto(), producto.getCantidad().longValue(), true);

                //Valida si existe suficiente cantidad
                if (!existeCantidadProductoSuficiente(producto.getId_producto(), producto.getCantidadAAgregar().longValue(), listaResultado)) {
                    cantidadExistente = listaResultado.stream().mapToLong(o -> o.getCantExistencia()).sum();
                    producto.setCantidadAAgregar(producto.getCantidad());
                    MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.no.existe.no.tiene.cantidad") + "  Existen " + cantidadExistente);
                } else {
                    producto.setCantidad(producto.getCantidadAAgregar());
                    producto.setPresentarProblemaInventario(0);
                }
            } else {
                producto.setCantidad(producto.getCantidadAAgregar());
                producto.setPresentarProblemaInventario(0);
            }
            reemplazarLineaListaProducto(producto);
            this.calcularMontos(producto);
        } catch (Exception e) {
            MensajeUtil.agregarMensajeError(EtiquetasUtil.obtenerMensaje("mensaje.error.cambiar.cantidad"));
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public void calcularMontos(ModeloProducto producto) {
//        if (producto.permiteEstePrecio(this.servicioParametro.obtenerValorParametro(ParametrosEnum.VALIDAR_PRECIOS.getIdParametro()).getValor(),
//                this.servicioParametro.obtenerValorParametro(ParametrosEnum.PORCENTAJE_GANANCIA.getIdParametro()).getValor())) {
        producto.calcularMontos();
//        } else {
//            producto.setPrecio(new BigDecimal("0.0"));
//            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.producto.precio.mayor.precio.base") + producto.getPrecioBaseProducto());
//        }
    }

    public List<ModeloProducto> obtenerProductoGravados(boolean mercancias) {
        List<ModeloProducto> retorno = new ArrayList<>();
        if (personaEdicionFactura != null
                && (personaEdicionFactura.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador()))) {
            retorno = new ArrayList<>();
        } else if (mercancias) {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                    if (producto.getImpuesto() != null) {
                        retorno.add(producto);
                    }
                }
            }
        } else {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                    if (producto.getImpuesto() != null) {
                        retorno.add(producto);
                    }
                }
            }
        }

        return retorno;
    }

    public List<ModeloProducto> obtenerProductoExentos(boolean mercancias) {
        List<ModeloProducto> retorno = new ArrayList<>();
        if (personaEdicionFactura != null
                && (personaEdicionFactura.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador()))) {
            retorno.addAll(this.listaProductoPorFacturar);
        } else if (mercancias) {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                    if (producto.getInd_exonerado().equals(Indicadores.EXENTO_SI.getIndicador())) {
                        retorno.add(producto);
                    }
                }
            }
        } else {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                    if (producto.getInd_exonerado().equals(Indicadores.EXENTO_SI.getIndicador())) {
                        retorno.add(producto);
                    }
                }
            }
        }

        return retorno;
    }

    public List<ModeloProducto> obtenerProductoExonerados(boolean mercancias) {
        List<ModeloProducto> retorno = new ArrayList<>();
        if (mercancias) {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                    if ((producto.isEsPersonaExonerada() && producto.getExoneracionLinea() != null)
                            && producto.getTipoTarifaImpuesto() != null
                            && (!producto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {
                        retorno.add(producto);
                    }
                }
            }
        } else {
            for (ModeloProducto producto : this.listaProductoPorFacturar) {
                if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                    if ((producto.isEsPersonaExonerada() && producto.getExoneracionLinea() != null)
                            && producto.getTipoTarifaImpuesto() != null
                            && (!producto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_10.getIdTipoTarifa()))) {
                        retorno.add(producto);
                    }
                }
            }
        }
        return retorno;
    }

    public BigDecimal getTotalExoneradosServicios() {
        totalExoneradosServicios = new BigDecimal("0.0");
        BigDecimal monto = new BigDecimal("0.0");
        Float exon = 0F;
        List<ModeloProducto> retorno = obtenerProductoExonerados(false);
        for (ModeloProducto modeloProducto : retorno) {

            if (modeloProducto.getTipoTarifaImpuesto() != null
                    && (!modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_10.getIdTipoTarifa()))) {

                if (modeloProducto.isEsPersonaExonerada()
                        && (modeloProducto.getExoneracionLinea() != null)) {

                    monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
                    exon = Float.parseFloat(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / Float.parseFloat(modeloProducto.getTipoTarifaImpuesto().getValor().toString());
                }
            }
            totalExoneradosServicios = totalExoneradosServicios.add(monto.multiply(new BigDecimal(exon))).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        }
        return totalExoneradosServicios;
    }

    public BigDecimal getTotalExoneradosMercancias() {
        totalExoneradosMercancias = new BigDecimal("0.0");
        BigDecimal monto = new BigDecimal("0.0");
        Float exon = 0F;
        List<ModeloProducto> retorno = obtenerProductoExonerados(true);
        for (ModeloProducto modeloProducto : retorno) {
            if (modeloProducto.getTipoTarifaImpuesto() != null
                    && (!modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))) {

                if (modeloProducto.isEsPersonaExonerada()
                        && (modeloProducto.getExoneracionLinea() != null)) {

                    monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
                    exon = Float.parseFloat(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / Float.parseFloat(modeloProducto.getTipoTarifaImpuesto().getValor().toString());
                }
            }
            totalExoneradosMercancias = totalExoneradosMercancias.add(monto.multiply(new BigDecimal(exon))).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        }
        return totalExoneradosMercancias;
    }

    public BigDecimal getTotalGravadosServicios() {
        totalGravadosServicios = new BigDecimal("0.0");
        BigDecimal monto = new BigDecimal("0.0");
        List<ModeloProducto> retorno = obtenerProductoGravados(false);
        for (ModeloProducto modeloProducto : retorno) {

            if (modeloProducto.isEsPersonaExonerada()
                    && (!modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa()))
                    && (modeloProducto.getExoneracionLinea() != null)) {

                monto = modeloProducto.getMontoTotalLinea() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotalLinea();
                Float exon = (1 - Float.parseFloat(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / Float.parseFloat(modeloProducto.getTipoTarifaImpuesto().getValor().toString()));
                totalGravadosMercancias = totalGravadosMercancias.add(new BigDecimal(exon).multiply(monto));
                //CAMBIO QUE SE HIZO EN LA EXONERACION SOBRE EL CALCULO 01/07/2020 totalGravadosServicios = totalGravadosServicios.add(monto.multiply(new BigDecimal(exon)));
                //totalGravadosServicios = totalGravadosServicios.add((modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).multiply(1 - modeloProducto.getProducto().getPorcentaje_compra() / 100));
            } else {
                if (!modeloProducto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())
                        && !modeloProducto.isEsPersonaExenta()
                        && !modeloProducto.getProducto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_10.getIdTipoTarifa())) {
                    // if (modeloProducto.isEsPersonaExenta()) {
                    totalGravadosServicios = totalGravadosServicios.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                    // }
                }

            }
        }
        return totalGravadosServicios;
    }

    public BigDecimal getTotalGravadosMercancias() {
        totalGravadosMercancias = new BigDecimal("0.0");
        BigDecimal monto = new BigDecimal("0.0");
        List<ModeloProducto> retorno = obtenerProductoGravados(true);
        for (ModeloProducto modeloProducto : retorno) {
            if (modeloProducto.isEsPersonaExonerada()
                    && (!modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa())
                    && !modeloProducto.getTipoTarifaImpuesto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_10.getIdTipoTarifa()))
                    && (modeloProducto.getExoneracionLinea() != null)) {

                monto = modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal();
                Float exon = (1 - Float.parseFloat(modeloProducto.getExoneracionLinea().getPorcentaje_exonerado().toString()) / Float.parseFloat(modeloProducto.getTipoTarifaImpuesto().getValor().toString()));
                totalGravadosMercancias = totalGravadosMercancias.add(new BigDecimal(exon).multiply(monto));
                //CAMBIO QUE SE HIZO EN LA EXONERACION SOBRE EL CALCULO 01/07/2020 totalGravadosMercancias = totalGravadosMercancias.add(monto.multiply(new BigDecimal(exon)));
            } else {
                if (!modeloProducto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())
                        && !modeloProducto.isEsPersonaExenta()
                        && !modeloProducto.getProducto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_10.getIdTipoTarifa())) {
                    totalGravadosMercancias = totalGravadosMercancias.add(modeloProducto.getMontoTotal() == null ? new BigDecimal("0.0") : modeloProducto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                }

            }
        }
        return totalGravadosMercancias;
    }

    public BigDecimal getTotalExonerados() {

        totalExonerados = new BigDecimal("0.0");
        totalExonerados = totalExonerados.add(getTotalExoneradosServicios()).add(getTotalExoneradosMercancias()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        return totalExonerados;
    }

    public BigDecimal getTotalExentos() {
        totalExentos = new BigDecimal("0.0");
        for (ModeloProducto producto : this.listaProductoPorFacturar) {
            if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())
                    || producto.isEsPersonaExenta()
                    || producto.getProducto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa())
                    || producto.getProducto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_10.getIdTipoTarifa())) {
                totalExentos = totalExentos.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
            }
        }
        return totalExentos;
    }

    public BigDecimal getTotalGravados() {
        totalGravados = new BigDecimal("0.0");
        totalGravados = totalGravados.add(getTotalGravadosMercancias()).add(getTotalGravadosServicios()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        return totalGravados;
    }

    public BigDecimal getTotalExentosServicios() {
        totalExentosServicios = new BigDecimal("0.0");
        for (ModeloProducto producto : this.listaProductoPorFacturar) {
            if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.SERVICIO.getIdTipoProducto())) {
                if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())
                        || producto.isEsPersonaExenta()
                        || producto.getProducto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_10.getIdTipoTarifa())
                        || producto.getProducto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa())) {
                    totalExentosServicios = totalExentosServicios.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                }
            }
        }
        return totalExentosServicios;
    }

    public BigDecimal getTotalExentosMercancias() {
        totalExentosMercancias = new BigDecimal("0.0");
        for (ModeloProducto producto : this.listaProductoPorFacturar) {
            if (producto.getTipoProducto().getId_tipo_producto().equals(TipoProducto.MERCANCIA.getIdTipoProducto())) {
                if (producto.getProducto().getInd_exento().equals(Indicadores.EXENTO_SI.getIndicador())
                        || producto.isEsPersonaExenta()
                        || producto.getProducto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_01.getIdTipoTarifa())
                        || producto.getProducto().getId_tipo_tarifa_impuesto().equals(TipoTarifaTimpuestoEnum.TARIFA_10.getIdTipoTarifa())) {
                    totalExentosMercancias = totalExentosMercancias.add(producto.getMontoTotal()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
                }
            }
        }
        return totalExentosMercancias;
    }

    public BigDecimal getTotalDescuentos() {
        totalDescuentos = new BigDecimal("0.0");
        totalDescuentos = getTotalDescuentosLineas();
        return totalDescuentos;
    }

    public BigDecimal getTotalDescuentosLineas() {
        totalDescuentosLineas = new BigDecimal("0.0");
        for (ModeloProducto producto : this.listaProductoPorFacturar) {
            totalDescuentosLineas = totalDescuentosLineas.add(producto.getMontoDescuento() == null ? new BigDecimal("0.0") : producto.getMontoDescuento()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        }
        return totalDescuentosLineas;
    }

    public Integer getDescuentoFactura() {
        return descuentoFactura;
    }

    public BigDecimal getTotalVenta() {
        totalVenta = new BigDecimal("0.0");
        totalVenta = getTotalGravados().add(getTotalExentos()).add(getTotalExonerados()).setScale(3, BigDecimal.ROUND_HALF_EVEN);;
        return totalVenta;
    }

    public BigDecimal getTotalVentaNeta() {
        totalVentaNeta = new BigDecimal("0.0");
        totalVentaNeta = getTotalVenta().subtract(getTotalDescuentosLineas());
        return totalVentaNeta;
    }

    public BigDecimal getTotalImpuestos() {
        totalImpuestos = new BigDecimal("0.0");
        this.listaProductoPorFacturar.forEach((producto) -> {
            if (producto.getImpuesto() != null) {
                if (producto.isEsPersonaExonerada() && producto.getExoneracionLinea() != null) {
                    totalImpuestos = totalImpuestos.add(producto.getTotalImpuestoNeto() == null ? new BigDecimal("0.0") : producto.getTotalImpuestoNeto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                } else {
                    totalImpuestos = totalImpuestos.add(producto.getTotalImpuesto() == null ? new BigDecimal("0.0") : producto.getTotalImpuesto()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
                }
            }
        });
        return totalImpuestos;
    }

    public BigDecimal getTotalFactura() {
        totalFactura = new BigDecimal("0.0");
        //totalFactura = totalFactura.add(getTotalVentaNeta()).add(getTotalImpuestos()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        this.listaProductoPorFacturar.forEach((producto) -> {
            totalFactura = totalFactura.add(producto.getMontoTotalLinea()).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        });
        return totalFactura;
    }

    public void obtenerFacturaRechazasReferencias() {
        try {
            listaFacturaRechazadasReferencias = this.servicioFactura.obtenerFacturaRechazadasReferencias();
        } catch (Exception e) {
            ExcepcionManager.manejarExcepcion(e);
        }
    }

    public void seleccionarFacturRechazadaReferencias(Long idFactura, String clave) {

        this.informacionReferenciaDocumento.setNumero(clave);
        this.informacionReferenciaDocumento.setIdFacturaReferencia(idFactura);

    }

}
