/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.pago.bean;

import com.ebs.constantes.enums.CondicionImpuestoEnum;
import com.ebs.constantes.enums.EstadoPago;
import com.ebs.constantes.enums.Indicadores;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.RespuestaMensaje;
import com.ebs.constantes.enums.TipoDocumento;
import com.ebs.constantes.enums.TiposCedulaMascaras;
import com.ebs.constantes.enums.TiposMimeTypes;
import com.ebs.entidades.CondicionImpuesto;
import com.ebs.entidades.Pago;
import com.ebs.entidades.PagoDetalle;
import com.ebs.entidades.Parametro;
import com.ebs.entidades.TipoActividad;
import com.ebs.entidades.TipoIdentificacion;
import com.ebs.entidades.TipoTarifaImpuesto;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.ArchivoNombre;
import com.ebs.modelos.Emisor;
import com.ebs.modelos.LineaPago;
import com.ebs.modelos.MensajesHaciendaReceptor;
import com.ebs.modelos.PagosModelo;
import com.ebs.modelos.PersonaHacienda;
import com.ebs.modelos.RespuestaHacienda;
import com.ebs.modelos.Token;
import com.esb.pago.servicios.ServicioPago;
import com.ebs.parametros.servicios.ServicioParametro;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.servicio.reporte.ServicioReporte;
import com.powersystem.utilitario.CorreoElectronico;
import com.powersystem.utilitario.EtiquetasUtil;
import com.powersystem.utilitario.MensajeUtil;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Data;
import org.primefaces.event.FileUploadEvent;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author Jorge GBSYS
 */
@ManagedBean
@ViewScoped
@Data
public class BeanPagos {

    @Getter
    @Setter
    private List<PagosModelo> listaPagos;
    private List<byte[]> listaBytesArchivos;
    @Getter
    @Setter
    private PagosModelo pagoManual;
    private boolean procesoCorriendo;
    private List<TipoIdentificacion> listaTiposCedulas;

    private PagosModelo pagoManualSeleccionado;

    private List<MensajesHaciendaReceptor> listaMensajeRecepcion;
    @Inject
    private ServicioPersona servicioPersona;
    @Inject
    private ServicioReporte servicioReporte;
    @Inject
    private ServicioPago servicioPago;
    @Inject
    private ServicioParametro servicioParametro;
    private Integer tamannoCedula;

    private String mascaraSeleccionada;

    private Long tipoIdentificacionSeleccionadaReceptor;
    private String resultadoValidacion = "";
    private List<CondicionImpuesto> listaCondicionImpuesto;
    private List<TipoActividad> listaActividades;
    private Long idCondicionImpuestoGlobal;
    private String detalleMensajeGlobal;
    private Long idTipoMensajeGlobal;
    private Emisor personaEmisor;
    private Utilitario utilitarioBusqueda;
    private Parametro parametroEndPointBusqueda;
    private Token token;
    private boolean ambienteHacienda;
    private String usuario;
    private String contrasenna;

    private String endPointCerreSesion;

    private String endPointToken;

    private String endPointConsultaDocumentos;

    private RespuestaHacienda respuestaHacienda;

    private List<TipoTarifaImpuesto> listaTiposTarifa;

    @Inject
    private ServicioProducto servicioProducto;

    private List<ArchivoNombre> listaArchivoPorCargar;

    @PostConstruct
    public void inicializar() {
        listaArchivoPorCargar = new ArrayList<>();
        listaTiposTarifa = servicioProducto.obtenerListaTiposTarifas();
        listaPagos = new ArrayList<>();
        pagoManual = new PagosModelo();
        this.pagoManualSeleccionado = new PagosModelo();
        listaMensajeRecepcion = Utilitario.obtenerListaMensajesHaciendaReceptor();
        this.listaTiposCedulas = servicioPersona.obtenerListaTiposIdentificacion();
        this.listaCondicionImpuesto = servicioPago.obtenerListaCondicionesImpuesto();
        // this.listaActividades = servicioPago.obtenerListaActividades();
        this.idCondicionImpuestoGlobal = 0l;
        this.detalleMensajeGlobal = "";
        this.idTipoMensajeGlobal = 0L;
        personaEmisor = servicioPersona.obtenerPersonaEmisorJob();
        utilitarioBusqueda = new Utilitario();
        parametroEndPointBusqueda = this.servicioParametro.obtenerValorParametro(ParametrosEnum.END_POINT_CONSULTA_PERSONA_HACIENDA.getIdParametro());
        ambienteHacienda = this.servicioParametro.obtenerValorParametro(ParametrosEnum.AMBIENTE_HACIENDA.getIdParametro()).getValor().equals(Indicadores.HACIENDA_PROD.getIndicador().toString());
        usuario = this.servicioParametro.obtenerValorParametro(ParametrosEnum.USUARIO_HACIENDA.getIdParametro()).getValor();
        contrasenna = this.servicioParametro.obtenerValorParametro(ParametrosEnum.PASSWORD_HACIENDA.getIdParametro()).getValor();
        if (ambienteHacienda) {
            endPointCerreSesion = this.servicioParametro.obtenerValorParametro(ParametrosEnum.LINK_CIERRE_SESION_PRODUCCION.getIdParametro()).getValor();
            endPointToken = this.servicioParametro.obtenerValorParametro(ParametrosEnum.LINK_TOKEN_PRODUCCION.getIdParametro()).getValor();
            endPointConsultaDocumentos = this.servicioParametro.obtenerValorParametro(ParametrosEnum.LINK_ESTADO_DOCUMENTO_ACEPTACION_PRODUCCION.getIdParametro()).getValor();

        } else {
            endPointCerreSesion = this.servicioParametro.obtenerValorParametro(ParametrosEnum.LINK_CIERRE_SESION_DESARROLLO.getIdParametro()).getValor();
            endPointToken = this.servicioParametro.obtenerValorParametro(ParametrosEnum.LINK_TOKEN_DESARROLLO.getIdParametro()).getValor();
            endPointConsultaDocumentos = this.servicioParametro.obtenerValorParametro(ParametrosEnum.LINK_ESTADO_DOCUMENTO_ACEPTACION_DESARROLLO.getIdParametro()).getValor();
        }
        token = new Token();

//        try {
//            List<String> lista = this.obtenerListaClaves("C:\\Users\\hp i5 7300u\\Desktop\\FACTURAS_FALTANTES.txt");
//            for (String pag : lista) {
//                String[] arreglo = pag.split(",");
//                
//                this.convertFileToXML(arreglo[1].toString(), arreglo[2].toString());
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(BeanPagos.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void procesarArchivoNuevos() throws IOException {

        List<String> lista = this.obtenerListaClaves("C:\\Users\\hp i5 7300u\\Desktop\\FACTURAS_FALTANTES.txt");

        for (String pag : lista) {
            String[] arreglo = pag.split(",");
            byte[] archivo = obtenerArchivo(arreglo[1]);

            InputStream input = new ByteArrayInputStream(archivo);
            leerArchivosXML2(input, Long.parseLong(arreglo[0]));
            System.out.println(arreglo[1]);
        }
    }

    public void convertFileToXML(String clave, String archi)
            throws IOException {
        System.out.println(clave);
        InputStream input = new ByteArrayInputStream(Utilitario.convertirBase64ABytes(archi));

        byte[] buffer = new byte[input.available()];
        input.read(buffer);

        File targetFile = new File("C:\\Users\\hp i5 7300u\\Desktop\\archivos_nuevos\\" + clave + ".xml");
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.close();
        input.close();
    }

    public List<String> obtenerListaClaves(String ruta) throws IOException {
        return Files.readAllLines(Paths.get(ruta), StandardCharsets.UTF_8);
    }

    public byte[] obtenerArchivo(String clave) {
        File folderPrincipal = new File("C:\\Users\\hp i5 7300u\\Desktop\\archivos_nuevos");
        File[] listaArchivo = folderPrincipal.listFiles();
        byte[] archivo = null;
        for (File archivoXML : listaArchivo) {
            try {
                if (this.executeRegex(
                        new String(Files.readAllBytes(Paths.get(archivoXML.getAbsolutePath())), StandardCharsets.UTF_8),
                        "<Clave>" + clave.trim() + "<\\/Clave>")) {

                    if (this.executeRegex(
                            new String(Files.readAllBytes(Paths.get(archivoXML.getAbsolutePath())), StandardCharsets.UTF_8),
                            "<FacturaElectronica")) {
                        archivo = Files.readAllBytes(archivoXML.toPath());
                        break;
                    }

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return archivo;
    }

    private static boolean executeRegex(String line, String regex) {
        boolean result = false;
        Pattern pattern = Pattern.compile(regex);
        Matcher m;
        m = pattern.matcher(line);
        if (m.find()) {
            result = true;
        }
        return result;
    }

    public void editarFila(CellEditEvent evt) {
    }

    public void cargarArchivo(FileUploadEvent evt) {
        try {
            leerArchivosXML(evt);
        } catch (IOException ex) {
            ex.printStackTrace();
            MensajeUtil.agregarMensajeError("Ha ocurrido un error cuando se intentó leer los documentos");
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
        this.pagoManual.setIdentificacionReceptor("");
    }

    private Long obtenerTipoTarifa(Long idTarifa) {
        TipoTarifaImpuesto tarifa = this.listaTiposTarifa.stream()
                .filter(predicate -> predicate.getValor().equals(idTarifa.intValue())).findFirst().get();
        return tarifa.getId_tipo_tarifa_impuesto();
    }

    public boolean validarPagoManual() {
        boolean resultado = true;

        if (tipoIdentificacionSeleccionadaReceptor.equals(0l)) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.tipo.cedula.pagos"));
            return resultado;
        }
        if (this.pagoManual.getIdentificacionReceptor() == null || this.pagoManual.getIdentificacionReceptor().contains("_")) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.cedula.receptor"));
            return resultado;
        }
        if (this.pagoManual.getNumeroConsecutivoReceptor() == null || this.pagoManual.getNumeroConsecutivoReceptor().equals("")) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.numero.consecutivo.receptor"));
            return resultado;
        }

        if (this.pagoManual.getMontoTotal() == null || this.pagoManual.getMontoTotal().equals(new BigDecimal("0.0"))) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.numero.montoComprobante.receptor"));
            return resultado;
        }

        return resultado;
    }

    public boolean validarProcesarPagos() {
        boolean resultado = true;
        if (this.listaPagos == null || this.listaPagos.size() <= 0) {
            resultado = false;
            MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensjae.validacion.lista.pagos"));
            return resultado;
        }

        for (PagosModelo pagoPorProcesar : listaPagos) {
            if (pagoPorProcesar.getTipoRespuesta().equals(0L)) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia(EtiquetasUtil.obtenerMensaje("mensaje.validacion.pagos.seleccion.respuesta"));
                break;
            }
            if (pagoPorProcesar.getId_condicion_impuesto().equals(0L)) {
                resultado = false;
                MensajeUtil.agregarMensajeAdvertencia("Debe ingresar la condición de la venta para todas las factura");
                break;
            }
        }
        return resultado;
    }

    public void procesarPagos() {
        try {
            if (token != null) {
                utilitarioBusqueda.cerrarSesion(token.getRefresh_token(), endPointCerreSesion, ambienteHacienda);
            }

            if (validarProcesarPagos()) {
                List<Pago> listaDePagosProcesar = new ArrayList<>();
                String tipoDocumento = "";
                Pago pago = null;
                for (PagosModelo nuevoPago : this.listaPagos) {
                    pago = new Pago();

                    pago.setId_estado(EstadoPago.PENDIENTE_DE_ENVIO.getIdEstado().intValue());
                    pago.setXmlAceptado(Base64.encodeBase64String(nuevoPago.getArchivo()));

                    if (nuevoPago.getTipoRespuesta().equals(RespuestaMensaje.ACEPTADO.getCodigoRespuesta())) {
                        tipoDocumento = TipoDocumento.CONFIRMACION_DE_ACEPTACION_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                        pago.setMensaje(RespuestaMensaje.ACEPTADO.getCodigoRespuesta().toString());
                    } else if (nuevoPago.getTipoRespuesta().equals(RespuestaMensaje.PARCIALMENTE_ACEPTADO.getCodigoRespuesta())) {
                        tipoDocumento = TipoDocumento.CONFIRMACION_DE_ACEPTACION_PARCIAL_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                        pago.setMensaje(RespuestaMensaje.PARCIALMENTE_ACEPTADO.getCodigoRespuesta().toString());
                    } else if (nuevoPago.getTipoRespuesta().equals(RespuestaMensaje.RECHAZADO.getCodigoRespuesta())) {
                        tipoDocumento = TipoDocumento.CONFIRMACION_DE_RECHAZO_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                        pago.setMensaje(RespuestaMensaje.RECHAZADO.getCodigoRespuesta().toString());
                    }

                    pago.setMensaje_detalle(nuevoPago.getDetalleMensaje());

                    pago.setClave_comprobante_pago(nuevoPago.getNumeroConsecutivoReceptor());
                    pago.setIdentificacion_proveedor(nuevoPago.getIdentificacionReceptor());
                    pago.setTipo_identificacion_proveedor(nuevoPago.getTipoIdentificacionReceptor());
                    pago.setTipo_mensaje_respuesta(tipoDocumento);
                    pago.setId_condicion_impuesto(nuevoPago.getId_condicion_impuesto());
                    if (!CondicionImpuestoEnum.PROPORCIONALIDAD.getIdCondicionImpuesto().equals(nuevoPago.getId_condicion_impuesto())) {
                        pago.setMonto_total_impuesto_acreditar(nuevoPago.getMonto_total_impuesto_acreditar());
                        pago.setMonto_total_gasto_aplicable(nuevoPago.getMonto_total_gasto_aplicable());
                        pago.setCodigo_actividad(nuevoPago.getCodigoActividad());
                    }

                    if (nuevoPago.getMontoImpuestos() != null | (nuevoPago.getMontoImpuestos() != null && !nuevoPago.getMontoImpuestos().equals(new BigDecimal("0.0")))) {
                        pago.setMonto_impuestos(nuevoPago.getMontoImpuestos().setScale(3, BigDecimal.ROUND_HALF_EVEN));
                    }

                    pago.setMonto_total_comprobante(nuevoPago.getMontoTotal().setScale(3, BigDecimal.ROUND_HALF_EVEN));
                    pago.setCorreo_electronico(nuevoPago.getCorreoElectronicoReceptor());
                    pago.setNombre_empresa(nuevoPago.getNombreEmpresa());

                    listaDePagosProcesar.add(pago);

                    List<PagoDetalle> listaPagosDetalles = new ArrayList<>();

                    nuevoPago.getListaLineasPago().forEach(elemento -> {

                        listaPagosDetalles.add(
                                new PagoDetalle(elemento.getNumeroLinea(),
                                        elemento.getDetalleProducto(),
                                        elemento.getSubTotal(),
                                        elemento.getMontoImpuesto(),
                                        elemento.getMontoTotalLinea(),
                                        elemento.getIdTipoTarifa(),
                                        (elemento.isSinImpuesto() ? 1 : 0)
                                ));
                    });
                    pago.setListaPagoDetalle(listaPagosDetalles);
                }

                listaDePagosProcesar.forEach(pagoNuevo -> {
                    servicioPago.guardarPagoNuevo(pagoNuevo);
                });

                //enviarCorreosElectronicos(listaDePagosProcesar);
                MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.pagos.guardado.correcto"));
                inicializar();
            }

        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void procesarPagos2(PagosModelo nuevoPago) {
        try {

            List<Pago> listaDePagosProcesar = new ArrayList<>();
            String tipoDocumento = "";
            Pago pago = null;

            pago = new Pago();

            pago.setId_estado(EstadoPago.PENDIENTE_DE_ENVIO.getIdEstado().intValue());
            pago.setXmlAceptado(Base64.encodeBase64String(nuevoPago.getArchivo()));

            if (nuevoPago.getTipoRespuesta().equals(RespuestaMensaje.ACEPTADO.getCodigoRespuesta())) {
                tipoDocumento = TipoDocumento.CONFIRMACION_DE_ACEPTACION_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                pago.setMensaje(RespuestaMensaje.ACEPTADO.getCodigoRespuesta().toString());
            } else if (nuevoPago.getTipoRespuesta().equals(RespuestaMensaje.PARCIALMENTE_ACEPTADO.getCodigoRespuesta())) {
                tipoDocumento = TipoDocumento.CONFIRMACION_DE_ACEPTACION_PARCIAL_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                pago.setMensaje(RespuestaMensaje.PARCIALMENTE_ACEPTADO.getCodigoRespuesta().toString());
            } else if (nuevoPago.getTipoRespuesta().equals(RespuestaMensaje.RECHAZADO.getCodigoRespuesta())) {
                tipoDocumento = TipoDocumento.CONFIRMACION_DE_RECHAZO_DEL_COMPROBANTE_ELECTRONICO.getTipoDocumento();
                pago.setMensaje(RespuestaMensaje.RECHAZADO.getCodigoRespuesta().toString());
            }

            pago.setMensaje_detalle(nuevoPago.getDetalleMensaje());

            pago.setClave_comprobante_pago(nuevoPago.getNumeroConsecutivoReceptor());
            pago.setIdentificacion_proveedor(nuevoPago.getIdentificacionReceptor());
            pago.setTipo_identificacion_proveedor(nuevoPago.getTipoIdentificacionReceptor());
            pago.setTipo_mensaje_respuesta(tipoDocumento);
            pago.setId_condicion_impuesto(nuevoPago.getId_condicion_impuesto());
            if (!CondicionImpuestoEnum.PROPORCIONALIDAD.getIdCondicionImpuesto().equals(nuevoPago.getId_condicion_impuesto())) {
                pago.setMonto_total_impuesto_acreditar(nuevoPago.getMonto_total_impuesto_acreditar());
                pago.setMonto_total_gasto_aplicable(nuevoPago.getMonto_total_gasto_aplicable());
                pago.setCodigo_actividad(nuevoPago.getCodigoActividad());
            }

            if (nuevoPago.getMontoImpuestos() != null | (nuevoPago.getMontoImpuestos() != null && !nuevoPago.getMontoImpuestos().equals(new BigDecimal("0.0")))) {
                pago.setMonto_impuestos(nuevoPago.getMontoImpuestos().setScale(3, BigDecimal.ROUND_HALF_EVEN));
            }

            pago.setMonto_total_comprobante(nuevoPago.getMontoTotal().setScale(3, BigDecimal.ROUND_HALF_EVEN));
            pago.setCorreo_electronico(nuevoPago.getCorreoElectronicoReceptor());
            pago.setNombre_empresa(nuevoPago.getNombreEmpresa());

            //  listaDePagosProcesar.add(pago);
            List<PagoDetalle> listaPagosDetalles = new ArrayList<>();

            nuevoPago.getListaLineasPago().forEach(elemento -> {

                listaPagosDetalles.add(
                        new PagoDetalle(elemento.getNumeroLinea(),
                                elemento.getDetalleProducto(),
                                elemento.getSubTotal(),
                                elemento.getMontoImpuesto(),
                                elemento.getMontoTotalLinea(),
                                elemento.getIdTipoTarifa(),
                                (elemento.isSinImpuesto() ? 1 : 0)
                        ));
            });
            pago.setListaPagoDetalle(listaPagosDetalles);

            //listaDePagosProcesar.forEach(pagoNuevo -> {
            servicioPago.guardarPagoNuevo(pago);
            //});

            //enviarCorreosElectronicos(listaDePagosProcesar);
            //MensajeUtil.agregarMensajeInfo(EtiquetasUtil.obtenerMensaje("mensaje.pagos.guardado.correcto"));
            //inicializar();
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void procesarPagosActualizar(PagosModelo nuevoPago, Long idPago) {
        try {

            //  listaDePagosProcesar.add(pago);
            List<PagoDetalle> listaPagosDetalles = new ArrayList<>();

            nuevoPago.getListaLineasPago().forEach(elemento -> {
                listaPagosDetalles.add(
                        new PagoDetalle(elemento.getNumeroLinea(),
                                elemento.getDetalleProducto(),
                                elemento.getSubTotal(),
                                elemento.getMontoImpuesto(),
                                elemento.getMontoTotalLinea(),
                                elemento.getIdTipoTarifa(),
                                (elemento.isSinImpuesto() ? 1 : 0)
                        ));
            });
            servicioPago.guardarLineas(servicioPago.obtenerPagoPorId(idPago).getId_pago(), listaPagosDetalles);
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public void enviarCorreosElectronicos(List<Pago> listaPagoPorEnviarCorreo) {
        try {
            CorreoElectronico correo = new CorreoElectronico();
            List<String> listaCorreos = new ArrayList<>();
            for (Pago pago : listaPagoPorEnviarCorreo) {
                listaCorreos = new ArrayList<>();
                listaCorreos.add(pago.getCorreo_electronico());
                correo.sendEmailConfirmacion("Confirmación de Comprobante Electrónico - ",
                        "Sear Médica le adjunta la respuesta de confirmación del Comprobante Electrónico " + pago.getClave_comprobante_pago(),
                        generarReporteInfoConfirmacionComprobante(pago),
                        (Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS.getNombreReporte() + "-" + pago.getNumero_consecutivo() + "." + TiposMimeTypes.PDF.getExtension()),
                        listaCorreos,
                        null,
                        null,
                        pago.getNumero_consecutivo());
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public byte[] generarReporteInfoConfirmacionComprobante(Pago pago) {
        byte[] reporte = null;
        try {
            Map parametros = new HashMap<>();
            parametros.put("idPago", pago.getId_pago());
            reporte = servicioReporte.generarReporte(Reportes.CONFIRMACIO_COMPROBANTES_ELECTRONICOS, TiposMimeTypes.PDF, parametros, false);

        } catch (JRException ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
        return reporte;
    }

    public void agregarDescripcionLinea() {
        for (PagosModelo pago : listaPagos) {
            if (pago.getNumeroConsecutivoReceptor().equals(pagoManualSeleccionado.getNumeroConsecutivoReceptor())) {
                pago = pagoManualSeleccionado;
            }
        }
        this.pagoManualSeleccionado = null;
        this.pagoManualSeleccionado = new PagosModelo();
    }

    public void eliminarPago(PagosModelo pagoSeleccionado) {
        this.listaPagos.remove(pagoSeleccionado);
    }

    public void agregarPAgoManual() {
        if (validarPagoManual()) {
            this.listaPagos.add(pagoManual);
            pagoManual = null;
            pagoManual = new PagosModelo();
        }
    }

    public void agregarInformacionGlobal() {
        boolean agregarTipoMensaje = !this.idTipoMensajeGlobal.equals(0L);
        boolean agregarCondicionImpuesto = !this.idCondicionImpuestoGlobal.equals(0L);
        boolean agregarMensajeDetalle = !(this.detalleMensajeGlobal == null || (this.detalleMensajeGlobal.equals("")));

        this.listaPagos.forEach(elemento -> {
            if (agregarTipoMensaje) {
                elemento.setTipoRespuesta(idTipoMensajeGlobal);
                if (idTipoMensajeGlobal.equals(RespuestaMensaje.RECHAZADO.getCodigoRespuesta())) {
                    elemento.setMonto_total_gasto_aplicable(new BigDecimal("0.0"));
                    elemento.setMonto_total_impuesto_acreditar(new BigDecimal("0.0"));
                }
            }
            if (agregarCondicionImpuesto) {
                elemento.setId_condicion_impuesto(idCondicionImpuestoGlobal);
                if (idCondicionImpuestoGlobal.equals(5L)) {
                    elemento.setMonto_total_gasto_aplicable(new BigDecimal("0.0"));
                    elemento.setMonto_total_impuesto_acreditar(new BigDecimal("0.0"));
                } else if (idCondicionImpuestoGlobal.equals(1L)) {
                    elemento.setMonto_total_gasto_aplicable(elemento.getMontoTotal());
                    elemento.setMonto_total_impuesto_acreditar(elemento.getMontoImpuestos());

                }
            }
            if (agregarMensajeDetalle) {
                elemento.setDetalleMensaje(detalleMensajeGlobal);
            }
        });

    }

    public byte[] documentToByte(Document document) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        org.apache.xml.security.utils.XMLUtils.outputDOM(document, baos, true);
        return baos.toByteArray();
    }

    public void leerProcesarLecturaArchivos() {
        if (!procesoCorriendo) {
            procesoCorriendo = true;
        }
    }

    public void leerArchivosXML(FileUploadEvent evt) throws IOException {

        PagosModelo pago = null;
        boolean permiteAgregarPago = true;
        ArchivoNombre archivo = new ArchivoNombre(evt.getFile().getInputstream(), evt.getFile().getFileName());
        try {
            if (token.getAccess_token() == null) {
                token = utilitarioBusqueda.obtenerTokenHacienda(this.endPointToken, this.usuario, this.contrasenna, this.ambienteHacienda);
            }
            if (token == null || token.getAccess_token() == null) {
                MensajeUtil.agregarMensajeAdvertencia("Problemas con la conexión a hacienda");
                return;
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(evt.getFile().getInputstream());
            doc.getDocumentElement().normalize();

            NodeList nodoPrincipal = doc.getElementsByTagName("FacturaElectronica");
            if (nodoPrincipal.getLength() <= 0) {
                //nodoPrincipal = doc.getElementsByTagName("NotaCreditoElectronica");
                //if (nodoPrincipal.getLength() <= 0) {
                //nodoPrincipal = doc.getElementsByTagName("NotaDebitoElectronica");
                //if (nodoPrincipal.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no es válido. Únicamente se permiten Facturas Electrónicas, Tiquetes Electrónicos, Notas de Crédito y Notas de Débito\n" : ", El documento " + archivo.getNombre() + " no es válido.  Únicamente se permiten Facturas Electrónicas, Tiquetes Electrónicos, Notas de Crédito y Notas de Débito\n");
                permiteAgregarPago = false;
                //}
                //}
            }
            pago = new PagosModelo();
            pago.setArchivo(this.documentToByte(doc));

            for (int temp = 0; temp < nodoPrincipal.getLength(); temp++) {
                Node nNode = nodoPrincipal.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getElementsByTagName("Clave").item(0) != null) {
                        pago.setNumeroConsecutivoReceptor(eElement.getElementsByTagName("Clave").item(0).getTextContent());
                        // System.out.println("Factura por Aceptar" + pago.getNumeroConsecutivoReceptor());
                    } else {
                        resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Clave'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Clave'.\n");
                        permiteAgregarPago = false;

                    }

                }
            }
            for (int temp = 0; temp < nodoPrincipal.getLength(); temp++) {
                Node nNode = nodoPrincipal.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    if (eElement.getElementsByTagName("CodigoActividad").item(0) != null) {
                        pago.setCodigoActividad(eElement.getElementsByTagName("CodigoActividad").item(0).getTextContent());
                    }
//                    else {
//                        resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'CodigoActividad'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'CodigoActividad'.\n");
//                        permiteAgregarPago = false;
//
//                    }
                }
            }
            NodeList nodoEmisor = doc.getElementsByTagName("Emisor");
            if (nodoEmisor.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Emisor'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Emisor'.\n");
                permiteAgregarPago = false;

            }

            for (int temp = 0; temp < nodoEmisor.getLength(); temp++) {
                Node nNode = nodoEmisor.item(temp);

                NodeList nodoEmisorIdentificador = doc.getElementsByTagName("Identificacion");
                if (nodoEmisorIdentificador.getLength() <= 0) {
                    resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Identificacion'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Identificacion'.\n");
                    permiteAgregarPago = false;
                }
                for (int temp2 = 0; temp2 < nodoEmisorIdentificador.getLength(); temp2++) {
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        if (eElement.getElementsByTagName("Tipo").item(0) != null) {
                            pago.setTipoIdentificacionReceptor(eElement.getElementsByTagName("Tipo").item(0).getTextContent());
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Tipo'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Tipo'.\n");
                            permiteAgregarPago = false;
                        }

                        if (eElement.getElementsByTagName("Numero").item(0) != null) {
                            pago.setIdentificacionReceptor(eElement.getElementsByTagName("Numero").item(0).getTextContent());
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Numero'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Numero'.\n");
                            permiteAgregarPago = false;
                        }
                        if (eElement.getElementsByTagName("CorreoElectronico").item(0) != null) {
                            pago.setCorreoElectronicoReceptor(eElement.getElementsByTagName("CorreoElectronico").item(0).getTextContent());
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'CorreoElectronico'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'CorreoElectronico'.\n");
                            permiteAgregarPago = false;
                        }
                        break;
                    }
                }

            }

            NodeList nodoReceptor = doc.getElementsByTagName("Receptor");
            if (nodoReceptor.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Receptor'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Receptor'.\n");
                permiteAgregarPago = false;
            }
            for (int temp = 0; temp < nodoReceptor.getLength(); temp++) {
                Node nNode = nodoReceptor.item(temp);

                NodeList nodoEmisorIdentificador = doc.getElementsByTagName("Identificacion");
                if (nodoEmisorIdentificador.getLength() <= 0) {
                    resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Identificacion'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Identificacion'.\n");
                    permiteAgregarPago = false;
                }
                for (int temp2 = 0; temp2 < nodoEmisorIdentificador.getLength(); temp2++) {
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        if (eElement.getElementsByTagName("Tipo").item(0) != null) {
                            if (!eElement.getElementsByTagName("Tipo").item(0).getTextContent().equals(personaEmisor.getIdentificacion().getTipo())) {
                                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " el tag 'Tipo' no es igual a la cedula de la empresa.\n" : ", El documento " + archivo.getNombre() + " el tag 'Tipo' no es igual a la cedula de la empresa.\n");
                                permiteAgregarPago = false;
                            }
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Tipo'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Tipo'.\n");
                            permiteAgregarPago = false;
                        }

                        if (eElement.getElementsByTagName("Numero").item(0) != null) {
                            if (!eElement.getElementsByTagName("Numero").item(0).getTextContent().equals(personaEmisor.getIdentificacion().getNumeroCedula())) {
                                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " el tag 'Numero' no es igual a la cedula de la empresa.\n" : ", El documento " + archivo.getNombre() + " el tag 'Numero' no es igual a la cedula de la empresa.\n");
                                permiteAgregarPago = false;
                            }
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " el tag 'Numero' no es igual a la cedula de la empresa.\n" : ", El documento " + archivo.getNombre() + " el tag 'Numero' no es igual a la cedula de la empresa.\n");
                            permiteAgregarPago = false;
                        }

                        break;
                    }
                }

            }
            NodeList nodoDetalleServicio = doc.getElementsByTagName("LineaDetalle");
            List<LineaPago> listaLineasPagos = new ArrayList<>();
            if (nodoDetalleServicio.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'DetalleServicio'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'DetalleServicio'.\n");
                permiteAgregarPago = false;
            } else {

                LineaPago lineaPago = new LineaPago();
                Node nodoDetSenc = nodoDetalleServicio.item(0);
                NodeList nodeList = nodoDetSenc.getChildNodes();
                Float tarifa = null;
                for (int temp = 0; temp < nodeList.getLength() - 1; temp++) {
                    Node nNode = nodoDetalleServicio.item(temp);
                    lineaPago = new LineaPago();
                    if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        lineaPago.setSubTotal(new BigDecimal(new Double(eElement.getElementsByTagName("SubTotal").item(0).getTextContent())));
                        lineaPago.setMontoTotalLinea(new BigDecimal(new Double(eElement.getElementsByTagName("MontoTotalLinea").item(0).getTextContent())));
                        lineaPago.setDetalleProducto(eElement.getElementsByTagName("Detalle").item(0).getTextContent());
                        lineaPago.setNumeroLinea(eElement.getElementsByTagName("NumeroLinea").item(0).getTextContent());

                        if (eElement.getElementsByTagName("Impuesto").item(0) != null) {

                            Node nNodeImpuesto = eElement.getElementsByTagName("Impuesto").item(0);

                            if (nNodeImpuesto.getNodeType() == Node.ELEMENT_NODE) {
                                Element nodElementImpuesto = (Element) nNodeImpuesto;
                                lineaPago.setSinImpuesto(false);
                                if (nodElementImpuesto.getElementsByTagName("Tarifa").item(0).getTextContent() != null) {
                                    tarifa = Float.parseFloat(nodElementImpuesto.getElementsByTagName("Tarifa").item(0).getTextContent().trim());
                                    lineaPago.setMontoImpuesto(new BigDecimal(new Double(eElement.getElementsByTagName("Monto").item(0).getTextContent())));
                                    lineaPago.setTarifa(tarifa.intValue());
                                    lineaPago.setIdTipoTarifa(this.obtenerTipoTarifa(Long.parseLong(lineaPago.getTarifa().toString())));
                                }
                            }
                            if (eElement.getElementsByTagName("Exoneracion").item(0) != null) {

                                lineaPago.setMontoImpuesto(new BigDecimal(new Double(eElement.getElementsByTagName("ImpuestoNeto").item(0).getTextContent())));
                            }

                        } else {
                            lineaPago.setSinImpuesto(true);
                        }
                        listaLineasPagos.add(lineaPago);
                    }
                }
            }
            pago.setListaLineasPago(listaLineasPagos);

            NodeList nodoResumenFactura = doc.getElementsByTagName("ResumenFactura");

            if (nodoResumenFactura.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'ResumenFactura'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'ResumenFactura'.\n");
                permiteAgregarPago = false;
            }

            for (int temp = 0; temp < nodoResumenFactura.getLength(); temp++) {
                Node nNode = nodoResumenFactura.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getElementsByTagName("TotalImpuesto").item(0) != null) {
                        pago.setMontoImpuestos(new BigDecimal(new Double(eElement.getElementsByTagName("TotalImpuesto").item(0).getTextContent())));
                    }
//                    else {
//                        resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'TotalImpuesto'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'TotalImpuesto'.\n");
//                        permiteAgregarPago = false;
//                    }

                    if (eElement.getElementsByTagName("TotalComprobante").item(0) != null) {
                        pago.setMontoTotal(new BigDecimal(new Double(eElement.getElementsByTagName("TotalComprobante").item(0).getTextContent())));
                    } else {
                        resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'TotalComprobante'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'TotalComprobante'.\n");
                        permiteAgregarPago = false;
                    }
                    break;
                }

            }
            if (permiteAgregarPago) {
                boolean continuar = true;
                while (continuar) {
                    try {
                        respuestaHacienda = utilitarioBusqueda.obtenerEstadoDocumentoConfirmacion(token, pago.getNumeroConsecutivoReceptor(), endPointConsultaDocumentos, ambienteHacienda, endPointCerreSesion);
                        if (respuestaHacienda != null && (respuestaHacienda.getInd_estado().equals("aceptado"))) {
                            //if (pago.getCodigoActividad() == null) {
                            PersonaHacienda personaEncontrada = this.utilitarioBusqueda.obtenerPersonaHacienda(pago.getIdentificacionReceptor(), parametroEndPointBusqueda.getValor());
                            if (personaEncontrada != null) {
                                pago.setCodigoActividad(personaEncontrada.getActividades().stream().findFirst().get().getCodigo());
                                pago.setNombreEmpresa(personaEncontrada.getNombre());
                            }
                            pago.setMonto_total_gasto_aplicable(new BigDecimal("0.0"));
                            pago.setMonto_total_impuesto_acreditar(new BigDecimal("0.0"));
                            pago.setId_actividad(0l);

                            listaPagos.add(pago);
                            continuar = false;
                        } else if (respuestaHacienda != null && (respuestaHacienda.getInd_estado().equals("rechazado"))) {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " se encuentra " + (respuestaHacienda.getInd_estado() == null ? respuestaHacienda.getMensaje() : respuestaHacienda.getInd_estado()) + ". Por favor comunicarse con el comercio para generar el comprobante correcto.\n" : ", El documento " + archivo.getNombre() + " se encuentra " + respuestaHacienda.getInd_estado() + ". Por favor comunicarse con el comercio para generar el comprobante correcto.\n");
                            continuar = false;
                        } else if (respuestaHacienda != null && (respuestaHacienda.getInd_estado().equals("No Recibido"))) {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " se encuentra " + (respuestaHacienda.getInd_estado() == null ? respuestaHacienda.getMensaje() : respuestaHacienda.getInd_estado()) + ". Por favor comunicarse con el comercio para generar el comprobante correcto.\n" : ", El documento " + archivo.getNombre() + " se encuentra " + respuestaHacienda.getInd_estado() + ". Por favor comunicarse con el comercio para generar el comprobante correcto.\n");
                            continuar = false;
                        } else if (respuestaHacienda.getMensaje().equals("Unauthorized")) {
                            utilitarioBusqueda.cerrarSesion(token.getRefresh_token(), endPointCerreSesion, ambienteHacienda);
                            token = utilitarioBusqueda.obtenerTokenHacienda(this.endPointToken, this.usuario, this.contrasenna, this.ambienteHacienda);
                        }
                    } catch (Exception e) {
                        System.out.println("Analizando de nuevo el documento : " + pago.getNumeroConsecutivoReceptor());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void leerArchivosXML2(InputStream evt, Long idPago) throws IOException {

        PagosModelo pago = null;
        boolean permiteAgregarPago = true;
        ArchivoNombre archivo = new ArchivoNombre(evt, idPago.toString());
        try {
            if (token.getAccess_token() == null) {
                token = utilitarioBusqueda.obtenerTokenHacienda(this.endPointToken, this.usuario, this.contrasenna, this.ambienteHacienda);
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(evt);
            doc.getDocumentElement().normalize();

            NodeList nodoPrincipal = doc.getElementsByTagName("FacturaElectronica");
            if (nodoPrincipal.getLength() <= 0) {
                //nodoPrincipal = doc.getElementsByTagName("NotaCreditoElectronica");
                //if (nodoPrincipal.getLength() <= 0) {
                //nodoPrincipal = doc.getElementsByTagName("NotaDebitoElectronica");
                //if (nodoPrincipal.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no es válido. Únicamente se permiten Facturas Electrónicas, Tiquetes Electrónicos, Notas de Crédito y Notas de Débito\n" : ", El documento " + archivo.getNombre() + " no es válido.  Únicamente se permiten Facturas Electrónicas, Tiquetes Electrónicos, Notas de Crédito y Notas de Débito\n");
                permiteAgregarPago = false;
                //}
                //}
            }
            pago = new PagosModelo();
            pago.setArchivo(this.documentToByte(doc));

            for (int temp = 0; temp < nodoPrincipal.getLength(); temp++) {
                Node nNode = nodoPrincipal.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getElementsByTagName("Clave").item(0) != null) {
                        pago.setNumeroConsecutivoReceptor(eElement.getElementsByTagName("Clave").item(0).getTextContent());
                        // System.out.println("Factura por Aceptar" + pago.getNumeroConsecutivoReceptor());
                    } else {
                        resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Clave'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Clave'.\n");
                        permiteAgregarPago = false;

                    }

                }
            }
            for (int temp = 0; temp < nodoPrincipal.getLength(); temp++) {
                Node nNode = nodoPrincipal.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    if (eElement.getElementsByTagName("CodigoActividad").item(0) != null) {
                        pago.setCodigoActividad(eElement.getElementsByTagName("CodigoActividad").item(0).getTextContent());
                    }
//                    else {
//                        resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'CodigoActividad'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'CodigoActividad'.\n");
//                        permiteAgregarPago = false;
//
//                    }
                }
            }
            NodeList nodoEmisor = doc.getElementsByTagName("Emisor");
            if (nodoEmisor.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Emisor'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Emisor'.\n");
                permiteAgregarPago = false;

            }

            for (int temp = 0; temp < nodoEmisor.getLength(); temp++) {
                Node nNode = nodoEmisor.item(temp);

                NodeList nodoEmisorIdentificador = doc.getElementsByTagName("Identificacion");
                if (nodoEmisorIdentificador.getLength() <= 0) {
                    resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Identificacion'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Identificacion'.\n");
                    permiteAgregarPago = false;
                }
                for (int temp2 = 0; temp2 < nodoEmisorIdentificador.getLength(); temp2++) {
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        if (eElement.getElementsByTagName("Tipo").item(0) != null) {
                            pago.setTipoIdentificacionReceptor(eElement.getElementsByTagName("Tipo").item(0).getTextContent());
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Tipo'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Tipo'.\n");
                            permiteAgregarPago = false;
                        }

                        if (eElement.getElementsByTagName("Numero").item(0) != null) {
                            pago.setIdentificacionReceptor(eElement.getElementsByTagName("Numero").item(0).getTextContent());
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Numero'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Numero'.\n");
                            permiteAgregarPago = false;
                        }
                        if (eElement.getElementsByTagName("CorreoElectronico").item(0) != null) {
                            pago.setCorreoElectronicoReceptor(eElement.getElementsByTagName("CorreoElectronico").item(0).getTextContent());
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'CorreoElectronico'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'CorreoElectronico'.\n");
                            permiteAgregarPago = false;
                        }
                        break;
                    }
                }

            }

            NodeList nodoReceptor = doc.getElementsByTagName("Receptor");
            if (nodoReceptor.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Receptor'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Receptor'.\n");
                permiteAgregarPago = false;
            }
            for (int temp = 0; temp < nodoReceptor.getLength(); temp++) {
                Node nNode = nodoReceptor.item(temp);

                NodeList nodoEmisorIdentificador = doc.getElementsByTagName("Identificacion");
                if (nodoEmisorIdentificador.getLength() <= 0) {
                    resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Identificacion'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Identificacion'.\n");
                    permiteAgregarPago = false;
                }
                for (int temp2 = 0; temp2 < nodoEmisorIdentificador.getLength(); temp2++) {
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        if (eElement.getElementsByTagName("Tipo").item(0) != null) {
                            if (!eElement.getElementsByTagName("Tipo").item(0).getTextContent().equals(personaEmisor.getIdentificacion().getTipo())) {
                                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " el tag 'Tipo' no es igual a la cedula de la empresa.\n" : ", El documento " + archivo.getNombre() + " el tag 'Tipo' no es igual a la cedula de la empresa.\n");
                                permiteAgregarPago = false;
                            }
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'Tipo'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'Tipo'.\n");
                            permiteAgregarPago = false;
                        }

                        if (eElement.getElementsByTagName("Numero").item(0) != null) {
                            if (!eElement.getElementsByTagName("Numero").item(0).getTextContent().equals(personaEmisor.getIdentificacion().getNumeroCedula())) {
                                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " el tag 'Numero' no es igual a la cedula de la empresa.\n" : ", El documento " + archivo.getNombre() + " el tag 'Numero' no es igual a la cedula de la empresa.\n");
                                permiteAgregarPago = false;
                            }
                        } else {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " el tag 'Numero' no es igual a la cedula de la empresa.\n" : ", El documento " + archivo.getNombre() + " el tag 'Numero' no es igual a la cedula de la empresa.\n");
                            permiteAgregarPago = false;
                        }

                        break;
                    }
                }

            }
            NodeList nodoDetalleServicio = doc.getElementsByTagName("LineaDetalle");
            List<LineaPago> listaLineasPagos = new ArrayList<>();
            if (nodoDetalleServicio.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'DetalleServicio'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'DetalleServicio'.\n");
                permiteAgregarPago = false;
            } else {

                LineaPago lineaPago = new LineaPago();
                Node nodoDetSenc = nodoDetalleServicio.item(0);
                NodeList nodeList = nodoDetSenc.getChildNodes();
                Float tarifa = null;
                for (int temp = 0; temp < nodeList.getLength() - 1; temp++) {
                    Node nNode = nodoDetalleServicio.item(temp);
                    lineaPago = new LineaPago();
                    if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        lineaPago.setSubTotal(new BigDecimal(new Double(eElement.getElementsByTagName("SubTotal").item(0).getTextContent())));
                        lineaPago.setMontoTotalLinea(new BigDecimal(new Double(eElement.getElementsByTagName("MontoTotalLinea").item(0).getTextContent())));
                        lineaPago.setDetalleProducto(eElement.getElementsByTagName("Detalle").item(0).getTextContent());
                        lineaPago.setNumeroLinea(eElement.getElementsByTagName("NumeroLinea").item(0).getTextContent());

                        if (eElement.getElementsByTagName("Impuesto").item(0) != null) {

                            Node nNodeImpuesto = eElement.getElementsByTagName("Impuesto").item(0);

                            if (nNodeImpuesto.getNodeType() == Node.ELEMENT_NODE) {
                                Element nodElementImpuesto = (Element) nNodeImpuesto;
                                lineaPago.setSinImpuesto(false);
                                if (nodElementImpuesto.getElementsByTagName("Tarifa").item(0).getTextContent() != null) {
                                    tarifa = Float.parseFloat(nodElementImpuesto.getElementsByTagName("Tarifa").item(0).getTextContent().trim());
                                    lineaPago.setMontoImpuesto(new BigDecimal(new Double(eElement.getElementsByTagName("Monto").item(0).getTextContent())));
                                    lineaPago.setTarifa(tarifa.intValue());
                                    lineaPago.setIdTipoTarifa(this.obtenerTipoTarifa(Long.parseLong(lineaPago.getTarifa().toString())));
                                }
                            }
                            if (eElement.getElementsByTagName("Exoneracion").item(0) != null) {

                                lineaPago.setMontoImpuesto(new BigDecimal(new Double(eElement.getElementsByTagName("ImpuestoNeto").item(0).getTextContent())));
                            }

                        } else {
                            lineaPago.setSinImpuesto(true);
                        }
                        listaLineasPagos.add(lineaPago);
                    }
                }
            }
            pago.setListaLineasPago(listaLineasPagos);

            NodeList nodoResumenFactura = doc.getElementsByTagName("ResumenFactura");

            if (nodoResumenFactura.getLength() <= 0) {
                resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'ResumenFactura'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'ResumenFactura'.\n");
                permiteAgregarPago = false;
            }

            for (int temp = 0; temp < nodoResumenFactura.getLength(); temp++) {
                Node nNode = nodoResumenFactura.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getElementsByTagName("TotalImpuesto").item(0) != null) {
                        pago.setMontoImpuestos(new BigDecimal(new Double(eElement.getElementsByTagName("TotalImpuesto").item(0).getTextContent())));
                    }
//                    else {
//                        resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'TotalImpuesto'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'TotalImpuesto'.\n");
//                        permiteAgregarPago = false;
//                    }

                    if (eElement.getElementsByTagName("TotalComprobante").item(0) != null) {
                        pago.setMontoTotal(new BigDecimal(new Double(eElement.getElementsByTagName("TotalComprobante").item(0).getTextContent())));
                    } else {
                        resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " no tiene el tag 'TotalComprobante'.\n" : ", El documento " + archivo.getNombre() + " no tiene el tag 'TotalComprobante'.\n");
                        permiteAgregarPago = false;
                    }
                    break;
                }

            }
            if (permiteAgregarPago) {
                boolean continuar = true;
                while (continuar) {
                    try {
                        respuestaHacienda = utilitarioBusqueda.obtenerEstadoDocumentoConfirmacion(token, pago.getNumeroConsecutivoReceptor(), endPointConsultaDocumentos, ambienteHacienda, endPointCerreSesion);
                        if (respuestaHacienda != null && (respuestaHacienda.getInd_estado().equals("aceptado"))) {
                            //if (pago.getCodigoActividad() == null) {
                            PersonaHacienda personaEncontrada = this.utilitarioBusqueda.obtenerPersonaHacienda(pago.getIdentificacionReceptor(), parametroEndPointBusqueda.getValor());
                            if (personaEncontrada != null) {
                                pago.setCodigoActividad(personaEncontrada.getActividades().stream().findFirst().get().getCodigo());
                                pago.setNombreEmpresa(personaEncontrada.getNombre());
                            }
                            pago.setMonto_total_gasto_aplicable(new BigDecimal("0.0"));
                            pago.setMonto_total_impuesto_acreditar(new BigDecimal("0.0"));
                            pago.setId_actividad(0l);
                            this.procesarPagosActualizar(pago, idPago);
                            listaPagos.add(pago);
                            continuar = false;
                        } else if (respuestaHacienda != null && (respuestaHacienda.getInd_estado().equals("rechazado"))) {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " se encuentra " + (respuestaHacienda.getInd_estado() == null ? respuestaHacienda.getMensaje() : respuestaHacienda.getInd_estado()) + ". Por favor comunicarse con el comercio para generar el comprobante correcto.\n" : ", El documento " + archivo.getNombre() + " se encuentra " + respuestaHacienda.getInd_estado() + ". Por favor comunicarse con el comercio para generar el comprobante correcto.\n");
                            continuar = false;
                        } else if (respuestaHacienda != null && (respuestaHacienda.getInd_estado().equals("No Recibido"))) {
                            resultadoValidacion = resultadoValidacion + (resultadoValidacion.equals("") ? "El documento " + archivo.getNombre() + " se encuentra " + (respuestaHacienda.getInd_estado() == null ? respuestaHacienda.getMensaje() : respuestaHacienda.getInd_estado()) + ". Por favor comunicarse con el comercio para generar el comprobante correcto.\n" : ", El documento " + archivo.getNombre() + " se encuentra " + respuestaHacienda.getInd_estado() + ". Por favor comunicarse con el comercio para generar el comprobante correcto.\n");
                            continuar = false;
                        } else if (respuestaHacienda.getMensaje().equals("Unauthorized")) {
                            utilitarioBusqueda.cerrarSesion(token.getRefresh_token(), endPointCerreSesion, ambienteHacienda);
                            token = utilitarioBusqueda.obtenerTokenHacienda(this.endPointToken, this.usuario, this.contrasenna, this.ambienteHacienda);
                        }
                    } catch (Exception e) {
                        System.out.println("Analizando de nuevo el documento : " + pago.getNumeroConsecutivoReceptor());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
