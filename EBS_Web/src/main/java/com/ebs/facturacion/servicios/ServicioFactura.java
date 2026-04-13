/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.servicios;

import com.ebs.constantes.enums.Cadenas;
import com.ebs.constantes.enums.EstadoAnulacion;
import com.ebs.constantes.enums.EstadoFactura;
import com.ebs.constantes.enums.EstadoInvSalidaDetalle;
import com.ebs.constantes.enums.EstadoNotaDebito;
import com.ebs.constantes.enums.EstadoRecibo;
import com.ebs.constantes.enums.FacturaEnvioCorreos;
import com.ebs.constantes.enums.Indicadores;
import com.ebs.constantes.enums.LineaDetalleEstado;
import com.ebs.constantes.enums.SituacionComprobante;
import com.ebs.constantes.enums.TipoCondicionVenta;
import com.ebs.constantes.enums.TipoDocumento;
import com.ebs.constantes.enums.TipoFacturaEnum;
import com.ebs.constantes.enums.TomarEnCuentaCierre;
import com.ebs.entidades.AnulacionFactura;
import com.ebs.entidades.Cierre;
import com.ebs.entidades.CierreFactura;
import com.ebs.entidades.CierreNotasCredito;
import com.ebs.entidades.CierreRecibo;
import com.ebs.entidades.CondicionVenta;
import com.ebs.entidades.Consecutivos;
//import com.ebs.entidades.ControlInventario;
//import com.ebs.entidades.ControlInventarioDetalle;
import com.ebs.entidades.DetalleCierre;
import com.ebs.entidades.DetalleFactura;
import com.ebs.entidades.DetalleFacturaNotaCredito;
import com.ebs.entidades.DetalleFacturaNotaDebito;
import com.ebs.entidades.DetalleFacturaTrazabilidad;
import com.ebs.entidades.Estados;
import com.ebs.entidades.Factura;
import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
import com.ebs.entidades.FacturaCorreo;
import com.ebs.entidades.FacturaDebito;
import com.ebs.entidades.FacturaHistoricoHacienda;
import com.ebs.entidades.FacturaTrazabilidad;
import com.ebs.entidades.Impuesto;
import com.ebs.entidades.Inventario;
import com.ebs.entidades.InventarioSalida;
import com.ebs.entidades.InventarioSalidaDetalle;
import com.ebs.entidades.InventarioSolicitud;
import com.ebs.entidades.InventarioSolicitudDetalle;
import com.ebs.entidades.MedioPago;
import com.ebs.entidades.MotivoAnulacion;
import com.ebs.entidades.MotivoDebito;
import com.ebs.entidades.Parametro;
import com.ebs.entidades.Producto;
import com.ebs.entidades.TipoCierre;
import com.ebs.entidades.TipoDocumentoReferencia;
import com.ebs.entidades.TipoFactura;
import com.ebs.entidades.TrazabilidadProducto;
import com.ebs.modelos.CierreTotales;
import com.ebs.exception.ExcepcionManager;
import com.ebs.inventario.servicio.ServicioInventario;
import com.ebs.modelos.ConsultaFacturasModelo;
import com.ebs.modelos.FacturaBusquedaModelo;
import com.ebs.modelos.FacturaCierreModelo;
import com.ebs.modelos.FacturaCredito;
import com.ebs.modelos.LineaDetalleFactura;
import com.ebs.modelos.FacturaElectronica;
import com.ebs.modelos.FacturaImpresion;
import com.ebs.modelos.FacturaModeloAnulacion;
import com.ebs.modelos.FacturaModeloNotaDebito;
import com.ebs.modelos.InformacionReferenciaFactura;
import com.ebs.modelos.ModeloProducto;
import com.ebs.modelos.ReciboCierreModelo;
import com.ebs.modelos.ResumenFactura;
import com.ebs.modelos.correos.FacturaCorreoModelo;
import com.powersystem.personas.servicios.ServicioPersona;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.util.ServicioBase;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.Utilitario;
import com.powersystem.utilitario.UtilitarioNotasDebitoCredito;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.omega.view.misc.CollectorView;

/**
 *
 * @author Jorge GBSYS
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioFactura extends ServicioBase {

    private static final long serialVersionUID = 1L;
    @Inject
    private ServicioProducto servicioProducto;
    @Inject
    private ServicioInventario servicioInventario;
    @Inject
    private ServicioPersona servicioPersona;

    public List<Estados> obtenerEstadoPorIdClase(Long idClase) {
        List<Estados> resultado = null;
        try {
            String sql = "SELECT e FROM Estados e WHERE e.idClase = :estado";
            resultado = em.createQuery(sql, Estados.class).setParameter("estado", idClase).getResultList();
        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtner.estados",
                    "mensaje.error.obtner.estados");
        }
        return resultado;
    }

    public List<Factura> obtenerFacturasPorCliente(Long idCliente) {
        List<Factura> resultado = null;
        try {
            String sql = "SELECT e FROM Factura e WHERE e.id_cliente = :id_cliente";
            resultado = em.createQuery(sql, Factura.class).setParameter("id_cliente", idCliente).getResultList();

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtner.estados",
                    "mensaje.error.obtner.estados");
        }

        return resultado;
    }

    public List<FacturaImpresion> obtenerObjetoFacturaTrazabilidad(Long idFactura, String montoLetras) {
        String sql = "select t1.id_factura, " + "t1.numero_consecutivo, t1.clave,t1.fecha_factura, "
                + "t8.descripcion as medioPago, t7.descripcion as condicionVenta, "
                + "t2.cantidad, t2.precio_neto, t2.descuento, t2.total_descuento as descuentoLinea, t2.total_impuestos as impuestosLinea,  "
                + "(t2.total_impuestos+t2.sub_total) as totalLinea, t2.sub_total as subtotalLinea, "
                + "t3.descripcion as nombreProducto, t3.codigo_cabys as codigo_cabys, t3.codigo_barras as codigo_barras, "
                + "t4.id_cliente, t4.numero_cedula, t6.descripcion as tipoCedula, '' as correo_electronico, t1.correo_electronico_cliente, "
                + "trim(COALESCE(T5.NOMBRE,'') ||' '||COALESCE(T5.PRIMER_APELLIDO,'')||' '||COALESCE(T5.SEGUNDO_APELLIDO,'')) AS NOMBRE, "
                + "t1.total_comprobante, t1.plazo_credito, t1.total_descuentos, t1.total_impuestos, t2.descripcion as descripcionLinea, t1.nombre_cliente_fantasia, t5.direccion, "
                + "t9.descripcion, t1.id_anulacion, t10.motivo_anulacion, t10.numero_consecutivo as consecutivo_anulacion, t10.clave as clave_anulacion, t11.descripcion as estado_anulacion, t12.descripcion as tipoMotivoAnulacion, "
                + "T1.LOGIN, T1.AGENTE, T5.TELEFONO_1, T5.TELEFONO_2, t13.ubicacion as direccionBodega, t13.telefono_1 as telefonoUnoBodega, t13.telefono_2 as telefonoDosBodega,  "
                + "T5.nombre_fantasia as NMBfANT, T1.total_venta_neta, t1.total_descuentos as total_descuentos_factura, t2.monto_total, t1.total_venta "
                + "from searmedica.factura_trazabilidad t1 inner join searmedica.detalle_factura_trazabilidad t2 "
                + "on t1.id_factura_trazabilidad = t2.id_factura_trazabilidad inner join searmedica.producto t3 "
                + "on t2.id_producto = t3.id_producto left join searmedica.cliente t4 "
                + "on t1.id_cliente = t4.id_cliente left join searmedica.persona t5 "
                + "on t4.numero_cedula = t5.numero_cedula left join searmedica.tipo_cedula t6 "
                + "on t5.id_tipo_cedula = t6.id_tipo_cedula inner join searmedica.condicion_venta t7 "
                + "on t1.id_cond_venta = t7.id_cond_venta inner join  searmedica.medio_pago t8 "
                + "on t1.id_medio_pago = t8.id_medio_pago inner join searmedica.tipo_factura t9 "
                + "on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10 "
                + "on t1.id_anulacion = t10.id_anulacion left join searmedica.estados t11 "
                + "on t10.id_estado = t11.id_estado left join searmedica.motivo_anulacion t12 "
                + "on t10.id_motivo_anulacion = t12.id_motivo_anulacion left join searmedica.bodega t13 "
                + "on t1.id_bodega = t13.id_bodega "
                + "where t1.id_factura =  " + idFactura
                + "and t2.es_para_nota_credito = 0";

        List<FacturaImpresion> listaFacturaImpresion = new ArrayList<>();
        FacturaImpresion facturaLinea = null;

        List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();

        for (Object[] factura : lista) {
            facturaLinea = new FacturaImpresion();
            facturaLinea.setMontoLetras(montoLetras);
            facturaLinea.setId_factura(Long.parseLong(factura[0].toString()));
            facturaLinea.setNumero_consecutivo(factura[1].toString());
            facturaLinea.setClave(factura[2].toString());
            facturaLinea.setFecha_factura(factura[3].toString());
            facturaLinea.setMediopago(factura[4].toString());
            facturaLinea.setCondicionventa(factura[5].toString());
            facturaLinea.setCantidad(Integer.parseInt(factura[6].toString()));
            facturaLinea.setPrecio_neto(new BigDecimal(Double.parseDouble(factura[7].toString())));
            facturaLinea.setDescuento(new BigDecimal(Double.parseDouble(factura[8].toString())));
            facturaLinea.setDescuentolinea(new BigDecimal(Double.parseDouble(factura[9].toString())));
            facturaLinea.setImpuestoslinea(new BigDecimal(Double.parseDouble(factura[10].toString())));
            facturaLinea.setTotallinea(new BigDecimal(Double.parseDouble(factura[11].toString())));
            facturaLinea.setSubtotallinea(new BigDecimal(Double.parseDouble(factura[12].toString())));
            facturaLinea.setNombreproducto(factura[13].toString());
            facturaLinea.setCodigo_cabys(factura[14].toString());
            facturaLinea.setCodigo_barras(factura[15].toString());
            facturaLinea.setId_cliente(factura[16] == null ? null : Long.parseLong(factura[16].toString()));
            facturaLinea.setNumero_cedula(factura[17] == null ? null : factura[17].toString());
            facturaLinea.setTipocedula(factura[18] == null ? null : factura[18].toString());
            facturaLinea.setCorreo_electronico(factura[19] == null ? null : factura[19].toString());
            facturaLinea.setCorreo_electronico_cliente(factura[20] == null ? null : factura[20].toString());
            facturaLinea.setNombre(factura[21] == null ? null : factura[21].toString());
            facturaLinea.setTotal_comprobante(new BigDecimal(Double.parseDouble(factura[22].toString())));
            facturaLinea.setPlazo_credito(factura[23] == null ? null : Integer.parseInt(factura[23].toString()));
            facturaLinea.setTotal_descuentos(new BigDecimal(Double.parseDouble(factura[24].toString())));
            facturaLinea.setTotal_impuestos(new BigDecimal(Double.parseDouble(factura[25].toString())));
            facturaLinea.setDescripcionlinea(factura[26] == null ? "" : factura[26].toString());
            facturaLinea.setNombre_cliente_fantasia(factura[27] == null ? null : factura[27].toString());
            facturaLinea.setDireccion(factura[28] == null ? null : factura[28].toString());
            facturaLinea.setDescripcion(factura[29].toString());
            facturaLinea.setId_anulacion(factura[30] == null ? null : Long.parseLong(factura[30].toString()));
            facturaLinea.setMotivo_anulacion(factura[31] == null ? null : factura[31].toString());
            facturaLinea.setConsecutivo_anulacion(factura[32] == null ? null : factura[32].toString());
            facturaLinea.setClave_anulacion(factura[33] == null ? null : factura[33].toString());
            facturaLinea.setEstado_anulacion(factura[34] == null ? null : factura[34].toString());
            facturaLinea.setTipomotivoanulacion(factura[35] == null ? null : factura[35].toString());
            facturaLinea.setLogin(factura[36].toString());
            facturaLinea.setAgente(factura[37].toString());
            facturaLinea.setTelefono_1(factura[38] == null ? null : factura[38].toString());
            facturaLinea.setTelefono_2(factura[39] == null ? null : factura[39].toString());
            facturaLinea.setDireccionbodega(factura[40].toString());
            facturaLinea.setTelefonounobodega(factura[41].toString());
            facturaLinea.setTelefonodosbodega(factura[42].toString());
            facturaLinea.setNmbfant(factura[43] == null ? null : factura[43].toString());
            facturaLinea.setTotal_venta_neta(new BigDecimal(Double.parseDouble(factura[44].toString())));
            facturaLinea.setTotal_descuentos_factura(new BigDecimal(Double.parseDouble(factura[45].toString())));
            facturaLinea.setMonto_total(new BigDecimal(Double.parseDouble(factura[46].toString())));
            facturaLinea.setTotal_venta(new BigDecimal(Double.parseDouble(factura[47].toString())));
            listaFacturaImpresion.add(facturaLinea);
        }

        return listaFacturaImpresion;

    }

    public List<FacturaImpresion> obtenerObjetoFacturaConsolidado(Long idFactura, String montoLetras) {
        String sql = "select t1.id_factura,"
                + " t1.numero_consecutivo, t1.clave,t1.fecha_factura,"
                + " t8.descripcion as medioPago, t7.descripcion as condicionVenta,"
                + " t2.cantidad, t2.precio_neto, t2.descuento, t2.total_descuento as descuentoLinea, t2.total_impuestos as impuestosLinea, "
                + " (t2.total_impuestos+t2.sub_total) as totalLinea, t2.sub_total as subtotalLinea,"
                + " t3.descripcion as nombreProducto, t3.codigo_cabys as codigo_cabys, t3.codigo_barras as codigo_barras,"
                + " t4.id_cliente, t4.numero_cedula, t6.descripcion as tipoCedula, '' as correo_electronico, t1.correo_electronico_cliente,"
                + " trim(COALESCE(T5.NOMBRE,'') ||' '||COALESCE(T5.PRIMER_APELLIDO,'')||' '||COALESCE(T5.SEGUNDO_APELLIDO,'')) AS NOMBRE,"
                + " t1.total_comprobante, t1.plazo_credito, t1.total_descuentos, t1.total_impuestos, t2.descripcion as descripcionLinea, t1.nombre_cliente_fantasia, t5.direccion,"
                + " t9.descripcion, t1.id_anulacion, t10.motivo_anulacion, t10.numero_consecutivo as consecutivo_anulacion, t10.clave as clave_anulacion, t11.descripcion as estado_anulacion, t12.descripcion as tipoMotivoAnulacion,"
                + " T1.LOGIN, T1.AGENTE, T5.TELEFONO_1, T5.TELEFONO_2, t13.ubicacion as direccionBodega, t13.telefono_1 as telefonoUnoBodega, t13.telefono_2 as telefonoDosBodega, "
                + " T5.nombre_fantasia as NMBfANT, T1.total_venta_neta, t1.total_descuentos as total_descuentos_factura, t2.monto_total, t1.total_venta"
                + " from searmedica.factura t1 inner join searmedica.detalle_factura t2"
                + " on t1.id_factura = t2.id_factura inner join searmedica.producto t3"
                + " on t2.id_producto = t3.id_producto left join searmedica.cliente t4"
                + " on t1.id_cliente = t4.id_cliente left join searmedica.persona t5"
                + " on t4.numero_cedula = t5.numero_cedula left join searmedica.tipo_cedula t6"
                + " on t5.id_tipo_cedula = t6.id_tipo_cedula inner join searmedica.condicion_venta t7"
                + " on t1.id_cond_venta = t7.id_cond_venta inner join  searmedica.medio_pago t8"
                + " on t1.id_medio_pago = t8.id_medio_pago inner join searmedica.tipo_factura t9"
                + " on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10"
                + " on t1.id_anulacion = t10.id_anulacion left join searmedica.estados t11"
                + " on t10.id_estado = t11.id_estado left join searmedica.motivo_anulacion t12"
                + " on t10.id_motivo_anulacion = t12.id_motivo_anulacion left join searmedica.bodega t13"
                + " on t1.id_bodega = t13.id_bodega"
                + " where t1.id_factura =  " + idFactura
                + " and t2.es_para_nota_credito = 0";

        List<FacturaImpresion> listaFacturaImpresion = new ArrayList<>();
        FacturaImpresion facturaLinea = null;

        List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();

        for (Object[] factura : lista) {
            facturaLinea = new FacturaImpresion();
            facturaLinea.setMontoLetras(montoLetras);
            facturaLinea.setId_factura(Long.parseLong(factura[0].toString()));
            facturaLinea.setNumero_consecutivo(factura[1] == null ? factura[0].toString() : factura[1].toString());
            facturaLinea.setClave(factura[2] == null ? null : factura[2].toString());
            facturaLinea.setFecha_factura(factura[3].toString());
            facturaLinea.setMediopago(factura[4].toString());
            facturaLinea.setCondicionventa(factura[5].toString());
            facturaLinea.setCantidad(Integer.parseInt(factura[6].toString()));
            facturaLinea.setPrecio_neto(new BigDecimal(Double.parseDouble(factura[7].toString())));
            facturaLinea.setDescuento(new BigDecimal(Double.parseDouble(factura[8].toString())));
            facturaLinea.setDescuentolinea(new BigDecimal(Double.parseDouble(factura[9].toString())));
            facturaLinea.setImpuestoslinea(new BigDecimal(Double.parseDouble(factura[10].toString())));
            facturaLinea.setTotallinea(new BigDecimal(Double.parseDouble(factura[11].toString())));
            facturaLinea.setSubtotallinea(new BigDecimal(Double.parseDouble(factura[12].toString())));
            facturaLinea.setNombreproducto(factura[13].toString());
            facturaLinea.setCodigo_cabys(factura[14].toString());
            facturaLinea.setCodigo_barras(factura[15].toString());
            facturaLinea.setId_cliente(factura[16] == null ? null : Long.parseLong(factura[16].toString()));
            facturaLinea.setNumero_cedula(factura[17] == null ? null : factura[17].toString());
            facturaLinea.setTipocedula(factura[18] == null ? null : factura[18].toString());
            facturaLinea.setCorreo_electronico(factura[19] == null ? null : factura[19].toString());
            facturaLinea.setCorreo_electronico_cliente(factura[20] == null ? null : factura[20].toString());
            facturaLinea.setNombre(factura[21] == null ? null : factura[21].toString());
            facturaLinea.setTotal_comprobante(new BigDecimal(Double.parseDouble(factura[22].toString())));
            facturaLinea.setPlazo_credito(factura[23] == null ? null : Integer.parseInt(factura[23].toString()));
            facturaLinea.setTotal_descuentos(new BigDecimal(Double.parseDouble(factura[24].toString())));
            facturaLinea.setTotal_impuestos(new BigDecimal(Double.parseDouble(factura[25].toString())));
            facturaLinea.setDescripcionlinea(factura[26] == null ? "" : factura[26].toString());
            facturaLinea.setNombre_cliente_fantasia(factura[27] == null ? null : factura[27].toString());
            facturaLinea.setDireccion(factura[28] == null ? null : factura[28].toString());
            facturaLinea.setDescripcion(factura[29].toString());
            facturaLinea.setId_anulacion(factura[30] == null ? null : Long.parseLong(factura[30].toString()));
            facturaLinea.setMotivo_anulacion(factura[31] == null ? null : factura[31].toString());
            facturaLinea.setConsecutivo_anulacion(factura[32] == null ? null : factura[32].toString());
            facturaLinea.setClave_anulacion(factura[33] == null ? null : factura[33].toString());
            facturaLinea.setEstado_anulacion(factura[34] == null ? null : factura[34].toString());
            facturaLinea.setTipomotivoanulacion(factura[35] == null ? null : factura[35].toString());
            facturaLinea.setLogin(factura[36].toString());
            facturaLinea.setAgente(factura[37].toString());
            facturaLinea.setTelefono_1(factura[38] == null ? null : factura[38].toString());
            facturaLinea.setTelefono_2(factura[39] == null ? null : factura[39].toString());
            facturaLinea.setDireccionbodega(factura[40].toString());
            facturaLinea.setTelefonounobodega(factura[41].toString());
            facturaLinea.setTelefonodosbodega(factura[42].toString());
            facturaLinea.setNmbfant(factura[43] == null ? null : factura[43].toString());
            facturaLinea.setTotal_venta_neta(new BigDecimal(Double.parseDouble(factura[44].toString())));
            facturaLinea.setTotal_descuentos_factura(new BigDecimal(Double.parseDouble(factura[45].toString())));
            facturaLinea.setMonto_total(new BigDecimal(Double.parseDouble(factura[46].toString())));
            facturaLinea.setTotal_venta(new BigDecimal(Double.parseDouble(factura[47].toString())));
            listaFacturaImpresion.add(facturaLinea);
        }

        return listaFacturaImpresion;

    }

    public List<Factura> obtenerFacturaRechazadasReferencias() {
        List<Factura> resultado = null;
        Factura facturaRest = null;
        try {

            String sql = " SELECT  T1.id_factura,  T1.clave, T1.numero_consecutivo, T3.DESCRIPCION "
                    + " FROM searmedica.factura T1 INNER JOIN SEARMEDICA.ESTADOS T3 "
                    + " ON T1.ESTADO_FACTURA = T3.ID_ESTADO "
                    + " WHERE  T1.ESTADO_FACTURA IN (4) order by t1.id_factura desc, t1.fecha_factura desc ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();

            for (Object[] factura : lista) {
                if (resultado == null) {
                    resultado = new ArrayList<>();
                }
                facturaRest = new Factura();

                facturaRest.setId_factura(Long.parseLong(factura[0].toString()));
                facturaRest.setNumero_consecutivo(factura[2].toString());
                facturaRest.setClave(factura[1].toString());
                facturaRest.setDescripcionEstado(factura[3].toString());

                resultado.add(facturaRest);
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtner.estados",
                    "mensaje.error.obtner.estados");
        }

        return resultado;
    }

    public Factura obtenerFacturaPorNumeroConsecutivo(String numeroConsecutivo) {
        Factura resultado = null;
        try {
            String sql = "SELECT e FROM Factura e WHERE e.numero_consecutivo LIKE '" + numeroConsecutivo + "%' ";
            resultado = em.createQuery(sql, Factura.class).getSingleResult();

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.recibo.obtener.factura.numero.consec",
                    "mensaje.recibo.obtener.factura.numero.consec");
        }

        return resultado;
    }

    public Integer obtenerCantidadPedidosProformasSinVencer() {
        Integer resultado = 0;
        try {
            StringBuilder select = new StringBuilder()
                    .append(" SELECT COUNT(1) FROM SEARMEDICA.FACTURA T1 ")
                    .append(" WHERE T1.ID_TIPO_FACTURA in(2,3) AND T1.CLAVE IS NULL and T1.ESTADO_FACTURA <> :estado ");

            Object objeto = (Object) em.createNativeQuery(select.toString())
                    .setParameter("estado", EstadoFactura.VENCIDA.getEstadoFactura())
                    .getSingleResult();
            if (objeto != null) {
                if (objeto instanceof BigInteger) {
                    resultado = Integer.parseInt(objeto.toString());
                }
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.inventario.usuario",
                    "mensaje.error.inventario.desarrollador");
        }
        return resultado;
    }

    /**
     * Método que obtiene una lista de condiciones de venta
     *
     * @return List<CondicionVenta>
     */
    public List<CondicionVenta> obtenerCondicionesVenta() {
        List<CondicionVenta> listaCondicionesVenta = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM CondicionVenta t1 ";
            listaCondicionesVenta = em.createQuery(hql, CondicionVenta.class)
                    .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.condiciones.venta.final",
                    "mensaje.error.condiciones.venta.desarrollador");
        }
        return listaCondicionesVenta;
    }

    public List<Factura> obtenerTodasFacturas() {
        return em.createQuery("Select f FROM Factura f", Factura.class)
                .getResultList();
    }

    public List<Factura> obtenerTodasFacturasPorClavePorCOnsecutivo(String clave, String consecutivo) {
        List<Factura> resultado = new ArrayList<>();
        boolean where = false;
        try {
            StringBuilder sql = new StringBuilder()
                    .append("Select f FROM Factura f");

            if (!clave.trim().equals("") && !consecutivo.trim().equals("")) {
                sql.append("WHERE f.clave  like '%").append(clave.trim()).append("%'").
                        append("AND f.numero_consecutivo  like '%").append(consecutivo.trim()).append("%'");

            } else if (!clave.trim().equals("")) {
                sql.append("WHERE f.clave  like '%").append(clave.trim()).append("%'");
            } else if (!consecutivo.trim().equals("")) {
                sql.append("WHERE f.numero_consecutivo  like '%").append(consecutivo.trim()).append("%'");
            }
            resultado = em.createQuery(sql.toString(), Factura.class)
                    .getResultList();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.buscar.factura.error",
                    "mensaje.buscar.factura.error");
        }

        return resultado;
    }

    public List<FacturaHistoricoHacienda> obtenerHIstorioFactura(Long idfactura) {
        return em.createQuery("Select f FROM FacturaHistoricoHacienda f WHERE f.id_factura = :idFactura", FacturaHistoricoHacienda.class)
                .setParameter("idFactura", idfactura)
                .getResultList();

    }

    /**
     * Método que obtiene los medios de pago
     *
     * @return List<MedioPago>
     */
    public List<MedioPago> obtenerMediosPago() {
        List<MedioPago> listaCondicionesVenta = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM MedioPago t1 ";
            listaCondicionesVenta = em.createQuery(hql, MedioPago.class)
                    .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.medio.pago.final",
                    "mensaje.error.medio.pago.desarrollador");
        }
        return listaCondicionesVenta;
    }

    public List<FacturaCorreoModelo> obtenerFacturasEnvioCorreoElectronicoPDF() {

        try {
            List<FacturaCorreoModelo> listaFacturas = null;
            StringBuilder select = new StringBuilder()
                    .append(" SELECT id_factura, numero_consecutivo, id_cliente,  correo_electronico,  ")
                    .append(" envio_respuesta_hacienda, envio_correo_electronico, total_comprobante, clave, correo_electronico_cliente")
                    .append(" FROM searmedica.factura where envio_correo_electronico = 0 and clave is not null");

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(select.toString()).getResultList();

            lista.stream()
                    .map(map -> FacturaCorreoModelo.builder()
                    .id_factura(Utilitario.returnZeroLongIfNullOrEmpty(map[0]))
                    .numero_consecutivo(Utilitario.returnEmptyIfObjectNullOrEmpty(map[1]))
                    .id_cliente(Utilitario.returnZeroLongIfNullOrEmpty(map[2]))
                    .correo_electronico(Utilitario.returnEmptyIfObjectNullOrEmpty(map[3]))
                    .envio_respuesta_hacienda(Utilitario.returnZeroIntegerIfNullOrEmpty(map[4]))
                    .envio_correo_electronico(Utilitario.returnZeroIntegerIfNullOrEmpty(map[5]))
                    .total_comprobante(Utilitario.returnZeroBigDecimalIfNullOrEmpty(map[6]))
                    .clave(Utilitario.returnEmptyIfObjectNullOrEmpty(map[7]))
                    .correo_electronico_cliente(Utilitario.returnEmptyIfObjectNullOrEmpty(map[8]))
                    .build())
                    .collect(Collectors.toList());

            return listaFacturas;
        } catch (NoResultException nex) {
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.facturas.para.enviar.correo",
                    "mensaje.error.obtener.facturas.para.enviar.correo.desarrollador");
        }

    }

//    /**
//     * Método que guarda un control de inventario para factura
//     *
//     * @param login
//     * @param idBodega
//     * @param idInventarioSalida
//     */
//    public ControlInventario guardarPadreControlInventario(String login, Long idBodega, Long idInventarioSalida) {
//        ControlInventario controlInv = new ControlInventario();
//        try {
//
//            controlInv.setFechaRegistro(fechaHoraBD());
//            controlInv.setLogin(login);
//            controlInv.setId_bodega(idBodega);
//            controlInv.setId_inventario_salida(idInventarioSalida);
//
//            guardar(controlInv);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
//                    "mensaje.error.guardar.control.inventario",
//                    "mensaje.error.guardar.control.inventario.desarrollador");
//        }
//        return controlInv;
//    }
//    public ControlInventario obtenerControlInventario(Long idInventarioSalida) {
//        ControlInventario control = null;
//        String sql = "SELECT ctrl FROM ControlInventario ctrl WHERE ctrl.id_inventario_salida = :idInventarioSalida";
//        try {
//            control = em.createQuery(sql, ControlInventario.class)
//                    .setParameter("idInventarioSalida", idInventarioSalida).getSingleResult();
//
//        } catch (Exception e) {
//        }
//        return control;
//    }
    /**
     * Método que guarda un control de inventario para factura
     *
     *
     * @param idInventarioSalida
     */
//    public void guardarHijoControlInventario(Long idInventarioSalida, Long idInventarioSalidaDetalle) {
//
//        try {
//            ControlInventario control = obtenerControlInventario(idInventarioSalida);
//            if (control == null) {
//                InventarioSalida salida = this.servicioInventario.obtenerInvSalidaFromID(idInventarioSalida);
//                control = this.guardarPadreControlInventario(salida.getLogin(), salida.getIdBodegaOrigen(), idInventarioSalida);
//            }
//            ControlInventarioDetalle controlInvDet = new ControlInventarioDetalle();
//            controlInvDet.setId_control(control.getId_control());
//            controlInvDet.setId_inventario_salida_detalle(idInventarioSalidaDetalle);
//
//            guardar(controlInvDet);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
//                    "mensaje.error.guardar.control.inventario.detalle",
//                    "mensaje.error.guardar.control.inventario.detalle.desarrollador");
//        }
//    }
//    public ControlInventarioModelo obtenerControlInventarioSinFactura(Long idBodega, String login) {
//        ControlInventarioModelo resultado = null;
//        try {
//
//            StringBuilder sql = new StringBuilder("SELECT ctrl FROM ControlInventario ctrl WHERE ctrl.id_bodega = :idBodega AND ctrl.login =:login ");
//            List<ControlInventario> listaControlInventario = em.createQuery(sql.toString(), ControlInventario.class)
//                    .setParameter("idBodega", idBodega)
//                    .setParameter("login", login)
//                    .getResultList();
//            for (ControlInventario controlInventario : listaControlInventario) {
//                this.eliminarControlInventarioSalidasDetalleInventario(controlInventario);
//            }
//
//
//            /*sql = new StringBuilder("SELECT ctrl FROM ControlInventarioDetalle ctrl WHERE ctrl.id_control = :idControl");
//
//            List<ControlInventarioDetalle> controlDetalle = em.createQuery(sql.toString(), ControlInventarioDetalle.class)
//                    .setParameter("idControl", control.getId_control()).getResultList();
//
//            resultado = new ControlInventarioModelo();
//            resultado.setControlInventario(control);
//            resultado.setListaControlInventarioDetalle(controlDetalle);*/
//        } catch (NoResultException nex) {
//            //nex.printStackTrace();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
//                    "mensaje.error.guardar.control.inventario.detalle.eliminar",
//                    "mensaje.error.guardar.control.inventario.detalle.desarrollador.eliminar");
//        }
//        return resultado;
//    }
//    public void eliminarControlInventarioSalidasDetalleInventario(ControlInventario control) {
//        try {
//
//            StringBuilder sql = new StringBuilder("SELECT ctrl FROM ControlInventarioDetalle ctrl WHERE ctrl.id_control = :idControl");
//
//            List<ControlInventarioDetalle> controlDetalle = em.createQuery(sql.toString(), ControlInventarioDetalle.class)
//                    .setParameter("idControl", control.getId_control()).getResultList();
//
//            for (ControlInventarioDetalle controlInventarioDetalle : controlDetalle) {
//                eliminar(controlInventarioDetalle);
//            }
//            eliminar(control);
//
//            InventarioSalida salida = this.servicioInventario.obtenerInvSalidaFromID(control.getId_inventario_salida());
//            List<InventarioSalidaDetalle> listaSalidasDetalle = this.servicioInventario.obtenerInvSaliDetalleFromInvSalida(control.getId_inventario_salida());
//
//            Inventario inventarioSeleccionado = null;
//            for (InventarioSalidaDetalle salidaDetalle : listaSalidasDetalle) {
//
//                inventarioSeleccionado = this.servicioInventario.obtenerInventarioPorId(salidaDetalle.getIdInventario());
//                inventarioSeleccionado.setCantExistencia(inventarioSeleccionado.getCantExistencia() + salidaDetalle.getCantidad());
//
//                if (inventarioSeleccionado.getCantExistencia() == 0l) {
//                    inventarioSeleccionado.setActivo(0l);
//                } else {
//                    inventarioSeleccionado.setActivo(1L);
//                }
//                this.servicioInventario.actualizarInventario(inventarioSeleccionado);
//            }
//
//            if (listaSalidasDetalle != null) {
//                for (InventarioSalidaDetalle salidaDetalle : listaSalidasDetalle) {
//                    eliminar(salidaDetalle);
//                }
//            }
//            if (salida != null) {
//                eliminar(salida);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
//                    "mensaje.error.guardar.control.inventario.detalle.eliminar",
//                    "mensaje.error.guardar.control.inventario.detalle.desarrollador.eliminar");
//        }
//    }
//    public void eliminarControlInventario(Long idInventarioSalida) {
//        try {
//
//            StringBuilder sql = new StringBuilder("SELECT ctrl FROM ControlInventario ctrl WHERE ctrl.id_inventario_salida = :idInventarioSalida");
//
//            ControlInventario control = em.createQuery(sql.toString(), ControlInventario.class)
//                    .setParameter("idInventarioSalida", idInventarioSalida).getSingleResult();
//
//            sql = new StringBuilder("SELECT ctrl FROM ControlInventarioDetalle ctrl WHERE ctrl.id_control = :idControl");
//
//            List<ControlInventarioDetalle> controlDetalle = em.createQuery(sql.toString(), ControlInventarioDetalle.class)
//                    .setParameter("idControl", control.getId_control()).getResultList();
//
//            for (ControlInventarioDetalle controlInventarioDetalle : controlDetalle) {
//                eliminar(controlInventarioDetalle);
//            }
//            eliminar(control);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
//                    "mensaje.error.guardar.control.inventario.detalle.eliminar",
//                    "mensaje.error.guardar.control.inventario.detalle.desarrollador.eliminar");
//        }
//    }
    /**
     * Método que se encarga de guardar una factura
     *
     * @param factura Factura
     * @param listaDetalleFactura List<DetalleFactura>
     */
    public Factura actualizarFactura(Long idFactura, String usuario, boolean facturaCancelada) {
        Factura facturaSeleccionada = null;
        try {
            facturaSeleccionada = this.obtenerFacturaBusqueda(idFactura);
            if (facturaCancelada) {
                facturaSeleccionada.setFecha_cancelacion(fechaHoraBD());
                facturaSeleccionada.setFactura_cancelada(1);
                facturaSeleccionada.setUsuario_cancela(usuario);
            } else {
                facturaSeleccionada.setFecha_cancelacion(null);
                facturaSeleccionada.setFactura_cancelada(0);
                facturaSeleccionada.setUsuario_cancela(null);
            }

            actualizar(facturaSeleccionada);

        } catch (NoResultException nex) {
            // nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.factura.guardar",
                    "mensaje.error.factura.gudar.desarrollador");
        }

        return facturaSeleccionada;
    }

    public Integer obtenerProximoNumeroLinea(Long idFactura) {
        Integer resultado = null;
        try {
            String sql = "SELECT MAX(NUMERO_LINEA) FROM SEARMEDICA.DETALLE_FACTURA WHERE ID_FACTURA = :idFactura";
            Object numero = em.createNativeQuery(sql).setParameter("idFactura", idFactura).getSingleResult();
            if (numero != null) {
                resultado = Integer.parseInt(numero.toString());
            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.numero.maximo.factura.linea",
                    "mensaje.error.obtener.numero.maximo.factura.linea");
        }
        return resultado;
    }

    /**
     *
     * @param factura
     * @param listaDetalleFactura
     * @param listaSalidaDetalle
     * @param salidaFactura
     * @param reCrearLineasFactura
     * @param crearNotaDebito
     * @param idMotivoDebitoTipo
     * @param motivoDebito
     * @param tipoDocumentoReferenciaSeleccionado
     * @return
     */
    public Factura actualizarFactura(Factura factura, List<DetalleFactura> listaDetalleFactura,
            boolean reCrearLineasFactura,
            boolean crearNotaDebito, Long idMotivoDebitoTipo, String motivoDebito, Long tipoDocumentoReferenciaSeleccionado,
            List<ModeloProducto> listaProductos, Parametro parametro) {
        try {
            List<DetalleFactura> listaDetalleProductosFacturas = null;
            List<InventarioSalidaDetalle> listaSalidas = null;
            factura.setFecha_factura_actualizacion(fechaHoraBD());
            factura.setFecha_factura(fechaHoraBD());

            if (factura.getId_cliente() == null) {
                factura.setEs_factura_sin_receptor(Indicadores.ES_FACTURA_SIN_RECEPTOR.getIndicador());
                //factura.setId_tipo_factura(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura());
            } else {
                factura.setEs_factura_sin_receptor(Indicadores.ES_FACTURA_CON_RECEPTOR.getIndicador());
            }
            if ((factura.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()) || factura.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) && factura.getClave() == null) {
                if (factura.getId_cliente() == null) {
                    factura.setNumero_consecutivo(this.construirNumeroConsecutivoTiqueteElectronico(TipoDocumento.TIQUETE_ELECTRONICO.getTipoDocumento()));
                    factura.setId_tipo_factura(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura());
                } else {
                    if (factura.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) {
                        factura.setNumero_consecutivo(this.construirNumeroConsecutivoTiqueteElectronico(TipoDocumento.TIQUETE_ELECTRONICO.getTipoDocumento()));
                    } else if (factura.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura())) {
                        factura.setNumero_consecutivo(this.construirNumeroConsecutivo(TipoDocumento.FACTURA_ELECTRONICA.getTipoDocumento()));
                    }

                }
                factura.setClave(this.construirClaveNumerica(factura.getNumero_consecutivo(),
                        Utilitario.obtenerEmisor().getIdentificacion().getNumeroCedula(),
                        (factura.getCodigo_situacion_comprobante().equals("4") ? SituacionComprobante.SITUACION_NORMAL.getSituacion() : factura.getCodigo_situacion_comprobante())));
            }
            factura.setMonto_restante(factura.getTotal_comprobante());
            actualizar(factura);

//            if (factura.getListaCorreosFacturas() != null) {
//                for (FacturaCorreo correo : factura.getListaCorreosFacturas()) {
//                    correo.setId_factura(factura.getId_factura());
//                    guardar(correo);
//                }
//            }
            //Long idInventarioSalida = 0L;
            if (factura.getId_tipo_factura().equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura())
                    || factura.getId_tipo_factura().equals(TipoFacturaEnum.PEDIDO.getIdTipoFactura()) || reCrearLineasFactura) {

                listaDetalleProductosFacturas = obtenerDetalleFacturaBusqueda(factura.getId_factura());
                for (DetalleFactura detalleFactura : listaDetalleProductosFacturas) {
                    eliminar(detalleFactura);
                }
            }

            if (factura.getId_tipo_factura().equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura()) || factura.getId_tipo_factura().equals(TipoFacturaEnum.PEDIDO.getIdTipoFactura())) {
                listaSalidas = this.servicioInventario.obtenerInvSaliDetallePorIdFactura(factura.getId_factura());
                this.retonarLineasInventario(listaSalidas);
            }

            for (DetalleFactura detalleFactura : listaDetalleFactura) {
                detalleFactura.getDetallePK().setId_factura(factura.getId_factura());
                guardar(detalleFactura);
                if (!factura.getId_tipo_factura().equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura()) && parametro.getValor().equals("1")) {

                    InventarioSalida inventarioSalida = this.servicioInventario.obtenerInventarioSalidaFromFactura(factura);
                    if (inventarioSalida == null) {
                        inventarioSalida = new InventarioSalida();
                        inventarioSalida.setIdBodegaOrigen(Utilitario.obtenerIdBodegaUsuario());
                        inventarioSalida.setLogin(Utilitario.obtenerUsuario().getLogin());
                        inventarioSalida.setFechaRegistro(fechaHoraBD());
                        inventarioSalida = servicioInventario.guardarInventarioSalida(inventarioSalida);
                    }

                    listaSalidas = this.servicioInventario.obtenerInvSaliDetalleFromInvSalidaPorLlaveDetalleFactura(inventarioSalida.getIdInventarioSalida(), detalleFactura.getDetallePK().getId_producto(), detalleFactura.getDetallePK().getId_factura(), detalleFactura.getDetallePK().getNumero_linea().longValue());

                    this.retonarLineasInventario(listaSalidas);
                    this.crearLineasDetalleInventarioSalida(factura.getId_factura(), inventarioSalida, detalleFactura.getCantidad().longValue(), detalleFactura);
                }
            }
            if (crearNotaDebito) {
                crearNotaDebito(factura, idMotivoDebitoTipo, motivoDebito, listaDetalleFactura, tipoDocumentoReferenciaSeleccionado, listaProductos);
            }

            if (factura.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()) || factura.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) {
                FacturaTrazabilidad facturaTrazabilidad = FacturaTrazabilidad.convertirFacturaATrazabilidad(factura);
                facturaTrazabilidad.setFecha_creacion(fechaHoraBD());
                facturaTrazabilidad.setTipo_operacion("CREACION");
                guardar(facturaTrazabilidad);
                listaDetalleFactura.forEach(elemento -> {
                    guardar(DetalleFacturaTrazabilidad.convertirDetalleFacturaADetalleTrazabilidad(facturaTrazabilidad, elemento));
                });
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.factura.guardar",
                    "mensaje.error.factura.gudar.desarrollador");
        }

        return factura;
    }

    private void retonarLineasInventario(List<InventarioSalidaDetalle> listaSalidas) {
        if (listaSalidas != null) {
            for (InventarioSalidaDetalle salidaDetalle : listaSalidas) {
                Inventario inventarioSeleccionado = this.servicioInventario.obtenerInventarioPorId(salidaDetalle.getIdInventario());
                //al inventrio le agregar lo restado anteriormente y verifico si alcanzan las cantidades
                inventarioSeleccionado.setCantExistencia(inventarioSeleccionado.getCantExistencia() + salidaDetalle.getCantidad());

                if (inventarioSeleccionado.getCantExistencia() == 0l) {
                    inventarioSeleccionado.setActivo(0l);
                } else {
                    inventarioSeleccionado.setActivo(1L);
                }
                this.servicioInventario.eliminarRegistroInventarioSalidaDetalle(salidaDetalle);
                this.servicioInventario.actualizarInventario(inventarioSeleccionado);
            }
        }

    }

    private List<ModeloProducto> obtenerLineasParaCalcular(List<DetalleFactura> listaDetalleFacturaNotaCredito, List<ModeloProducto> listaProductos) {
        List<ModeloProducto> resultado = new ArrayList<>();

        for (ModeloProducto modeloProducto : listaProductos) {

            for (DetalleFactura detalleFacturaNotaCredito : listaDetalleFacturaNotaCredito) {
                if (modeloProducto.getIdFactura().equals(detalleFacturaNotaCredito.getDetallePK().getId_factura())
                        && modeloProducto.getId_producto().equals(detalleFacturaNotaCredito.getDetallePK().getId_producto())
                        && modeloProducto.getNumeroLineaProducto().equals(detalleFacturaNotaCredito.getDetallePK().getNumero_linea())) {
                    resultado.add(modeloProducto);

                }
            }
        }

        return resultado;
    }

    public void crearNotaDebito(Factura factura, Long idMotivoDebitoTipo, String motivoDebito, List<DetalleFactura> listaDetalleFactura,
            Long tipoDocumentoReferenciaSeleccionado, List<ModeloProducto> listaProductos) throws Exception {
        try {
            UtilitarioNotasDebitoCredito utilitario = new UtilitarioNotasDebitoCredito();
            utilitario.setListaProductoPorFacturar(this.obtenerLineasParaCalcular(listaDetalleFactura, listaProductos));
            if (factura.getId_cliente() != null) {
                utilitario.setPersonaEdicionFactura(servicioPersona.obtenerPersonaPorIdCliente(factura.getId_cliente()));
            }

            FacturaDebito debito = new FacturaDebito();
            debito.setLogin(Utilitario.obtenerUsuario().getLogin());
            debito.setId_bodega(Utilitario.obtenerIdBodegaUsuario());
            debito.setIp(JSFUtil.obtenerIpComputadora());
            debito.setNombre_maquina(JSFUtil.obtenerNombreMaquina());

            debito.setId_factura(factura.getId_factura());
            debito.setMotivo_nota_debito(motivoDebito);
            debito.setNumero_consecutivo(construirNumeroConsecutivoNotaDebito(TipoDocumento.NOTA_DE_DEBITO_ELECTRONICA.getTipoDocumento()));
            debito.setClave(
                    construirClaveNumerica(
                            debito.getNumero_consecutivo(),
                            Utilitario.obtenerEmisor().getIdentificacion().getNumeroCedula(),
                            SituacionComprobante.SITUACION_NORMAL.getSituacion()
                    )
            );
            debito.setId_tipo_doc_referencia(tipoDocumentoReferenciaSeleccionado);
            debito.setId_estado(EstadoNotaDebito.PENDIENTE_ENVIO.getEstadoNotaCredito());
            debito.setId_motivo_nota_debito(idMotivoDebitoTipo);
            debito.setEnvio_correo_electronico(FacturaEnvioCorreos.NO_ENVIO_CORREO.getEnvioCorreo());
            debito.setFecha_nota_debito(fechaHoraBD());
            debito.setTotal_descuento(utilitario.getTotalDescuentosLineas());
            debito.setTotal_impuesto(utilitario.getTotalImpuestos());
            debito.setTotal_venta_neta(utilitario.getTotalVentaNeta());
            debito.setTotal_venta(utilitario.getTotalVenta());
            debito.setTotal_servicios_grabados(utilitario.getTotalGravadosServicios());
            debito.setTotal_servicios_exentos(utilitario.getTotalExentosServicios());
            debito.setTotal_mercancias_gravadas(utilitario.getTotalGravadosMercancias());
            debito.setTotal_mercancias_exentas(utilitario.getTotalExentosMercancias());
            debito.setTotal_gravado(utilitario.getTotalGravados());
            debito.setTotal_exento(utilitario.getTotalExentos());
            debito.setTotal_nota_debito(utilitario.getTotalFactura());
            debito.setTotal_servicios_exonerados(utilitario.getTotalExoneradosServicios());
            debito.setTotal_mercancias_exonerados(utilitario.getTotalExoneradosMercancias());
            debito.setTotal_exonerado(utilitario.getTotalExonerados());

            guardar(debito);

            listaDetalleFactura.forEach(elemento -> {
                guardar(new DetalleFacturaNotaDebito(elemento.getDetallePK().getNumero_linea(),
                        debito.getId_nota_debito(), elemento.getDetallePK().getId_producto(), elemento.getDetallePK().getId_factura(), elemento));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.factura.guardar.nota.debito",
                    "mensaje.error.factura.guardar.nota.debito");
        }

    }

    private List<InventarioSalidaDetalle> crearLineasDetalleInventarioSalida(Long idFactura, InventarioSalida salidaFactura,
            Long cantidad, DetalleFactura detalleFactura) throws Exception {

        List<InventarioSalidaDetalle> resultado = new ArrayList<>();
        InventarioSalidaDetalle detalle = null;
        Long cantidadRestante = 0l;
        Long cantidadExistencia;
        Long cantidadRestanteInventario;

        boolean continuar = true;
        try {

            List< Inventario> listaInventarios = this.servicioInventario.consultarInventProductoPorCantidad(Utilitario.obtenerIdBodegaUsuario(),
                    detalleFactura.getDetallePK().getId_producto(), detalleFactura.getCantidad().longValue(), true);

            for (Inventario inventario : listaInventarios) {

                if (continuar) {
                    detalle = new InventarioSalidaDetalle();

                    while (inventario.getCantExistencia() > 0L && cantidad > 0L) {

                        detalle.setNumeroLote(inventario.getNumeroLote());
                        detalle.setFechaVencimiento(inventario.getFechaVencimiento());

                        detalle.setIdBodegaDestino(Utilitario.obtenerIdBodegaUsuario());
                        detalle.setIdProducto(detalleFactura.getDetallePK().getId_producto());
                        detalle.setIdInventario(inventario.getIdInventario());

                        detalle.setIdInventarioSalida(salidaFactura.getIdInventarioSalida());
                        detalle.setIdEstado(EstadoInvSalidaDetalle.PENDIENTE.getId());
                        detalle.setIdFactura(idFactura);
                        detalle.setCantidad(cantidad);
                        detalle.setNumeroLinea(detalleFactura.getDetallePK().getNumero_linea().longValue());

                        cantidadExistencia = inventario.getCantExistencia();
                        cantidadRestante = cantidad - cantidadExistencia;
                        cantidadRestanteInventario = inventario.getCantExistencia() - cantidad;

                        inventario.setCantExistencia(cantidadRestanteInventario <= 0L ? 0 : cantidadRestanteInventario);
                        if (inventario.getCantExistencia() <= 0l) {
                            inventario.setActivo(0l);
                        }
                        if (cantidadRestante <= 0L) {
                            detalle.setCantidad(cantidad);
                            continuar = false;
                        } else {
                            detalle.setCantidad(detalle.getCantidad() - cantidadRestante);
                        }
                        cantidad = cantidadRestante;
                        resultado.add(detalle);
                    }

                }
            }
            resultado = this.servicioInventario.guardarInventarioSalidaDetalle(resultado, listaInventarios);
        } catch (Exception e) {
            throw e;
        }

        return resultado;
    }

    public void retornarAInventario(Factura factura) throws Exception, Exception {
        List<InventarioSalidaDetalle> listaSalidasDetalle = servicioInventario.obtenerInvSaliDetallePorIdFactura(factura.getId_factura());
        Inventario inventarioSeleccionado = null;
        Long idSolicitudInventario = null;

        int contadorEliminacion = 0;
        for (InventarioSalidaDetalle detalle : listaSalidasDetalle) {

            inventarioSeleccionado = this.servicioInventario.obtenerInventarioPorId(detalle.getIdInventario());
            inventarioSeleccionado.setCantExistencia(inventarioSeleccionado.getCantExistencia() + detalle.getCantidad());
            idSolicitudInventario = detalle.getIdInventarioSalida();

            if (inventarioSeleccionado.getCantExistencia() == 0l) {
                inventarioSeleccionado.setActivo(0l);
            } else {
                inventarioSeleccionado.setActivo(1L);
            }
            this.servicioInventario.eliminarRegistroInventarioSalidaDetalle(detalle);
            crearNuevaTrazabilidad(detalle.getIdProducto(), detalle.getCantidad(), "INGRESO", "REEMPLAZO DE FACTURA, RECHAZADA POR HACIENDA",
                    (factura.getId_factura() + "-" + factura.getNumero_consecutivo()), factura.getLogin(), null, factura.getId_bodega());
            contadorEliminacion++;
            this.servicioInventario.actualizarInventario(inventarioSeleccionado);

        }
        if (idSolicitudInventario != null & (listaSalidasDetalle.size() == contadorEliminacion)) {
            this.servicioInventario.eliminarRegistroInventarioSalidaPorId(idSolicitudInventario);
        }

    }

    /**
     * Método que se encarga de guardar una factura
     *
     * @param factura Factura
     * @param listaDetalleFactura List<DetalleFactura>
     */
    public Factura guardarFactura(Factura factura, List<DetalleFactura> listaDetalleFactura, Parametro parametro) {
        try {
            //   Long idInventarioSalida = 0L;
            factura.setFecha_factura(fechaHoraBD());
            if (factura.getId_cliente() == null) {
                factura.setEs_factura_sin_receptor(Indicadores.ES_FACTURA_SIN_RECEPTOR.getIndicador());
                // factura.setId_tipo_factura(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura());
            } else {
                factura.setEs_factura_sin_receptor(Indicadores.ES_FACTURA_CON_RECEPTOR.getIndicador());
            }
            if (factura.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()) || factura.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) {
                if (factura.getId_cliente() == null) {
                    factura.setNumero_consecutivo(this.construirNumeroConsecutivoTiqueteElectronico(TipoDocumento.TIQUETE_ELECTRONICO.getTipoDocumento()));
                    factura.setId_tipo_factura(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura());
                } else {
                    if (factura.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) {
                        factura.setNumero_consecutivo(this.construirNumeroConsecutivoTiqueteElectronico(TipoDocumento.TIQUETE_ELECTRONICO.getTipoDocumento()));
                    } else if (factura.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura())) {
                        factura.setNumero_consecutivo(this.construirNumeroConsecutivo(TipoDocumento.FACTURA_ELECTRONICA.getTipoDocumento()));
                    }

                }
                factura.setClave(this.construirClaveNumerica(factura.getNumero_consecutivo(),
                        Utilitario.obtenerEmisor().getIdentificacion().getNumeroCedula(),
                        (factura.getCodigo_situacion_comprobante().equals("4") ? SituacionComprobante.SITUACION_NORMAL.getSituacion() : factura.getCodigo_situacion_comprobante())));
            }
            factura.setMonto_restante(factura.getTotal_comprobante());
            guardar(factura);

//            if (factura.getListaCorreosFacturas() != null) {
//                for (FacturaCorreo correo : factura.getListaCorreosFacturas()) {
//                    correo.setId_factura(factura.getId_factura());
//                    guardar(correo);
//                }
//            }
            InventarioSalida salidaFactura = new InventarioSalida();
            salidaFactura.setIdBodegaOrigen(Utilitario.obtenerIdBodegaUsuario());
            salidaFactura.setLogin(Utilitario.obtenerUsuario().getLogin());
            salidaFactura.setFechaRegistro(fechaHoraBD());
            salidaFactura = servicioInventario.guardarInventarioSalida(salidaFactura);

            for (DetalleFactura detalleFactura : listaDetalleFactura) {
                detalleFactura.getDetallePK().setId_factura(factura.getId_factura());
                guardar(detalleFactura);
                if (!factura.getId_tipo_factura().equals(TipoFacturaEnum.COTIZACION.getIdTipoFactura()) && parametro.getValor().equals("1")) {
                    crearLineasDetalleInventarioSalida(factura.getId_factura(), salidaFactura, detalleFactura.getCantidad().longValue(), detalleFactura);
                }
            }
            if (factura.getId_tipo_factura().equals(TipoFacturaEnum.FACTURA.getIdTipoFactura()) || factura.getId_tipo_factura().equals(TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura())) {
                FacturaTrazabilidad facturaTrazabilidad = FacturaTrazabilidad.convertirFacturaATrazabilidad(factura);
                facturaTrazabilidad.setFecha_creacion(fechaHoraBD());
                facturaTrazabilidad.setTipo_operacion("CREACION");
                guardar(facturaTrazabilidad);
                listaDetalleFactura.forEach(elemento -> {
                    guardar(DetalleFacturaTrazabilidad.convertirDetalleFacturaADetalleTrazabilidad(facturaTrazabilidad, elemento));
                });
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.factura.guardar",
                    "mensaje.error.factura.gudar.desarrollador");
        }

        return factura;
    }

    public AnulacionFactura obtenerFacturaAnulacionBusqueda(Long idFacturaAnulacion) {
        AnulacionFactura facturaResultado = null;
        try {
            String hql = " SELECT t1 FROM AnulacionFactura t1 WHERE id_anulacion=:id_anulacion ";
            facturaResultado = em.createQuery(hql, AnulacionFactura.class)
                    .setParameter("id_anulacion", idFacturaAnulacion)
                    .getSingleResult();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.factura",
                    "mensaje.obtener.factura.desarrollador");
        }
        return facturaResultado;
    }

    public Factura obtenerFacturaBusqueda(Long idFactura) {
        Factura facturaResultado = null;
        try {
            String hql = " SELECT t1 FROM Factura t1 WHERE id_factura=:idfactura ";
            facturaResultado = em.createQuery(hql, Factura.class)
                    .setParameter("idfactura", idFactura)
                    .getSingleResult();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.factura",
                    "mensaje.obtener.factura.desarrollador");
        }
        return facturaResultado;
    }

    public FacturaTrazabilidad obtenerFacturaTrazabilidadBusqueda(Long idFactura) {
        FacturaTrazabilidad facturaResultado = null;
        try {
            String hql = " SELECT t1 FROM FacturaTrazabilidad t1 WHERE id_factura=:idfactura ";
            facturaResultado = em.createQuery(hql, FacturaTrazabilidad.class)
                    .setParameter("idfactura", idFactura)
                    .getSingleResult();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.factura",
                    "mensaje.obtener.factura.desarrollador");
        }
        return facturaResultado;
    }

    public AnulacionFactura obtenerNotaCreditoBusqueda(Long idNotaCredito) {
        AnulacionFactura facturaResultado = null;
        try {
            String hql = " SELECT t1 FROM AnulacionFactura t1 WHERE id_anulacion=:id_anulacion ";
            facturaResultado = em.createQuery(hql, AnulacionFactura.class)
                    .setParameter("id_anulacion", idNotaCredito)
                    .getSingleResult();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtenre.nota.creidto",
                    "mensaje.error.obtenre.nota.creidto");
        }
        return facturaResultado;
    }

    public FacturaDebito obtenerNotaDebitoBusqueda(Long idNotaDebito) {
        FacturaDebito facturaResultado = null;
        try {
            String hql = " SELECT t1 FROM FacturaDebito t1 WHERE id_nota_debito=:idNotaDebito ";
            facturaResultado = em.createQuery(hql, FacturaDebito.class)
                    .setParameter("idNotaDebito", idNotaDebito)
                    .getSingleResult();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtenre.nota.debito",
                    "mensaje.error.obtenre.nota.debito");
        }
        return facturaResultado;
    }

    public List<FacturaBusquedaModelo> obtenerFacturasBusqueda(Long idTipoFactura, String login, String fecha, Long idFactura) {
        List<FacturaBusquedaModelo> resultado = null;
        String filtroIdTipoFactura = "";
        String filtroFecha = "";
        String filtroIdFactura = "";
        String filtroNotaCredito = "";
        try {
            String sql = " SELECT   t1.id_factura, t1.id_cliente, t1.fecha_factura, t1.total_comprobante,t4.descripcion, t1.id_tipo_factura, "
                    + " (t3.nombre||' '||t3.primer_apellido||' '||t3.segundo_apellido) as nombre , t1.nombre_cliente_fantasia, t1.numero_consecutivo"
                    + " FROM searmedica.factura t1 left join searmedica.cliente t2 "
                    + " on t1.id_cliente = t2.id_cliente left join searmedica.persona t3 "
                    + " on t2.numero_cedula = t3.numero_cedula left join searmedica.estados t4 "
                    + " on t1.estado_factura = t4.id_estado where t1.login = :login ";//and t1.estado_factura = " + EstadoFactura.EN_DESARROLLO.getEstadoFactura();
            if (idTipoFactura != null) {

                if (idTipoFactura.equals(4l)) {
                    filtroNotaCredito = " or  EXISTS (SELECT 1 FROM SEARMEDICA.FACTURA_ANULACION T5 WHERE T5.ID_FACTURA = T1.ID_FACTURA) ";
                } else if (!idTipoFactura.equals(0l)) {
                    filtroIdTipoFactura = " and t1.id_tipo_factura = " + idTipoFactura + " ";
                }
            }
            if (fecha != null) {
                if (!fecha.equals("")) {
                    filtroFecha = " and to_char(t1.fecha_factura,'dd/mm/yyyy') = '" + fecha + "' ";
                }
            }
            if (idFactura != null) {
                filtroIdFactura = " and t1.id_factura = " + idFactura + " ";
            }
            sql = sql + filtroIdTipoFactura + filtroNotaCredito + filtroFecha + filtroIdFactura;

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).setParameter("login", login).getResultList();

            resultado = new ArrayList<>();
            FacturaBusquedaModelo facturaModelo = null;
            for (Object[] factura : lista) {
                facturaModelo = new FacturaBusquedaModelo();

                facturaModelo.setFechaFactura(factura[2].toString());
                facturaModelo.setIdFactura(Long.parseLong(factura[0].toString()));
                facturaModelo.setMonto(new BigDecimal(Double.parseDouble(factura[3].toString())));
                facturaModelo.setNombreCliente(factura[6] == null ? "" : factura[6].toString());
                if (Long.parseLong(factura[5].toString()) == TipoFacturaEnum.FACTURA.getIdTipoFactura()) {
                    facturaModelo.setTipoFactura("Factura Electrónica");
                } else if (Long.parseLong(factura[5].toString()) == TipoFacturaEnum.COTIZACION.getIdTipoFactura()) {
                    facturaModelo.setTipoFactura("Proforma");
                } else if (Long.parseLong(factura[5].toString()) == TipoFacturaEnum.TIQUETE_ELECTRONICO.getIdTipoFactura()) {
                    facturaModelo.setTipoFactura("Tiquete Electrónico");
                } else if (Long.parseLong(factura[5].toString()) == TipoFacturaEnum.PEDIDO.getIdTipoFactura()) {
                    facturaModelo.setTipoFactura("Pedido");
                }
                facturaModelo.setNombreFantasia(factura[7] == null ? "" : factura[7].toString());
                facturaModelo.setNumeroConsecutivo(factura[8] == null ? "" : factura[8].toString());

                resultado.add(facturaModelo);

            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.busqueda",
                    "mensaje.obtener.facturas.busqueda.desarrollador");
        }
        return resultado;

    }

    /**
     * Método que obttiene los tipos de facturas
     *
     * @return
     */
    public List<TipoFactura> obtenerTiposFacturas() {
        List<TipoFactura> lisatResultado = null;
        try {
            String hql = " SELECT t1 FROM TipoFactura t1 ";
            lisatResultado = em.createQuery(hql, TipoFactura.class)
                    .getResultList();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.errro.obtener.tipos.factura",
                    "mensaje.errro.obtener.tipos.factura.desarrollador");
        }
        return lisatResultado;
    }

    public void actualizarCorreoElectronicoFactura(Long idFactura, String correoElectronico) {
        try {
            Factura factura = this.obtenerFacturaBusqueda(idFactura);
            FacturaTrazabilidad facturaTrazabilidad = this.obtenerFacturaTrazabilidadBusqueda(idFactura);

            factura.setCorreo_electronico_cliente(correoElectronico);
            facturaTrazabilidad.setCorreo_electronico_cliente(correoElectronico);

            actualizar(factura);
            actualizar(facturaTrazabilidad);
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.errro.obtener.tipos.factura",
                    "mensaje.errro.obtener.tipos.factura.desarrollador");
        }

    }

    public List<DetalleFactura> obtenerDetalleFacturaBusquedaParaReemplazo(Long idFactura) {
        List<DetalleFactura> lisatResultado = null;
        try {
            String hql = " SELECT t1 FROM DetalleFactura t1 WHERE t1.detallePK.id_factura =:idFactura AND t1.es_para_nota_credito = :esParaNotaCredito";
            lisatResultado = em.createQuery(hql, DetalleFactura.class)
                    .setParameter("idFactura", idFactura)
                    .setParameter("esParaNotaCredito", LineaDetalleEstado.PARA_NOTA_CREDITO.getEstadoLineaDetalle())
                    .getResultList();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.factura.detalle",
                    "mensaje.obtener.factura..detalledesarrollador");
        }
        return lisatResultado;
    }

    public List<DetalleFactura> obtenerDetalleFacturaBusqueda(Long idFactura) {
        List<DetalleFactura> lisatResultado = null;
        try {
            String hql = " SELECT t1 FROM DetalleFactura t1 WHERE t1.detallePK.id_factura =:idFactura AND t1.es_para_nota_credito = :esParaNotaCredito order by t1.detallePK.numero_linea ASC";
            lisatResultado = em.createQuery(hql, DetalleFactura.class)
                    .setParameter("idFactura", idFactura)
                    .setParameter("esParaNotaCredito", LineaDetalleEstado.PARA_NOTA_CREDITO_QUITAR.getEstadoLineaDetalle())
                    .getResultList();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.factura.detalle",
                    "mensaje.obtener.factura..detalledesarrollador");
        }
        return lisatResultado;
    }

    /**
     * Método que obtiene la información de la factura
     *
     * @param idFactura
     * @return
     */
    public FacturaElectronica obtenerFactura(Long idFactura) {
        FacturaElectronica factura = new FacturaElectronica();
        ResumenFactura resumen = new ResumenFactura();
        try {
            String sql = "select  "
                    + "t1.total_descuentos,   "
                    + "t1.total_impuestos,   "
                    + "t1.total_venta_neta,   "
                    + "t1.total_venta,   "
                    + "t1.total_servicios_grabados,   "
                    + "t1.total_servicios_exentos,   "
                    + "t1.total_mercancias_gravadas,   "
                    + "t1.total_mercancias_exentas,   "
                    + "t1.total_gravado,   "
                    + "t1.total_exento,   "
                    + "t1.total_comprobante,  "
                    + "t7.codigo_hacienda as condicionVenta,    "
                    + "t8.codigo_hacienda as medioPago,  "
                    + "t1.clave, t1.numero_consecutivo, t1.fecha_emision, t1.plazo_credito, "
                    + "t1.codigo_situacion_comprobante, t1.codigo_documento_referencia, t1.fecha_documento_referencia, t1.razon_documento_referencia, t1.numero_factura_documento_referencia "
                    + "from searmedica.factura t1 left outer join searmedica.condicion_venta t7  "
                    + "on t1.id_cond_venta = t7.id_cond_venta left outer join  searmedica.medio_pago t8  "
                    + "on t1.id_medio_pago = t8.id_medio_pago   "
                    + "where t1.id_factura = :idfactura";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).setParameter("idfactura", idFactura).getResultList();
            for (Object[] objeto : lista) {
                resumen.setTotal_descuentos(new BigDecimal(objeto[0].toString().trim()));
                resumen.setTotal_impuestos(new BigDecimal(objeto[1].toString().trim()));
                resumen.setTotal_venta_neta(new BigDecimal(objeto[2].toString().trim()));
                resumen.setTotal_venta(new BigDecimal(objeto[3].toString().trim()));
                resumen.setTotal_servicios_grabados(new BigDecimal(objeto[4].toString().trim()));
                resumen.setTotal_servicios_exentos(new BigDecimal(objeto[5].toString().trim()));
                resumen.setTotal_mercancias_gravadas(new BigDecimal(objeto[6].toString().trim()));
                resumen.setTotal_mercancias_exentas(new BigDecimal(objeto[7].toString().trim()));
                resumen.setTotal_gravado(new BigDecimal(objeto[8].toString().trim()));
                resumen.setTotal_exento(new BigDecimal(objeto[9].toString().trim()));
                resumen.setTotal_comprobante(new BigDecimal(objeto[10].toString().trim()));

                factura.setCondicionVenta(objeto[11].toString().trim());
                if (factura.getCondicionVenta().equals(TipoCondicionVenta.CREDITO.getCodigoHacienda())) {
                    factura.setPlazoCredito(objeto[16] == null ? "" : objeto[16].toString().trim() + " días");
                }
                factura.setMedioPago(objeto[12].toString().trim());
                factura.setClave(objeto[13].toString().trim());
                factura.setNumeroConsecutivo(objeto[14].toString().trim());
                factura.setFechaEmision(objeto[15] == null ? "" : objeto[15].toString().trim());
                factura.setResumen(resumen);

                if (!objeto[17].toString().equals(SituacionComprobante.SITUACION_NORMAL.getSituacion())) {
                    InformacionReferenciaFactura referencia = new InformacionReferenciaFactura();
                    referencia.setCodigo(objeto[18].toString());
                    referencia.setTipoDoc(TipoDocumento.FACTURA_ELECTRONICA.getTipoDocumento());
                    referencia.setNumero(objeto[21].toString());

                    //  DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                    // Date date = format.parse(string);
                    referencia.setFechaEmision(objeto[19].toString());
                    referencia.setRazon(objeto[20].toString());
                    factura.setReferencia(referencia);
                }
            }
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.factura.obttiene..factura.hacienda.usuario",
                    "mensaje.factura.obttiene..factura.hacienda.desarrollador");
        }
        return factura;
    }

    /**
     * Método que obtiene la información de la factura
     *
     * @param idFactura
     * @return
     */
    public Long obtenerIdClientePorFactura(Long idFactura) {
        Long idCliente = null;
        try {
            String sql = " SELECT ft FROM Factura ft WHERE ft.id_factura=:id_factura ";

            Factura factura = em.createQuery(sql, Factura.class).setParameter("id_factura", idFactura).getSingleResult();
            idCliente = factura.getId_cliente();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.cliente.por.factura.uusariuo",
                    "mensaje.obtener.cliente.por.factura");
        }
        return idCliente;
    }

    /**
     * Método que obtiene las facturas a neviarse a Hacienda
     *
     * @return List<Object[]>
     */
    public List<Object[]> obtenerFacturaPorEnviarHacienda() {
        List<Object[]> listaFactura = null;
        try {
            String sql = "select ft.id_factura, ft.id_cliente from searmedica.factura ft where ft.estado_factura = :estadofactura OR ft.estado_factura = :estadofactura2 limit 5";
            listaFactura = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("estadofactura", EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura())
                    .setParameter("estadofactura2", EstadoFactura.ERROR_SERVICIO.getEstadoFactura())
                    .getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtenr.facturas.por.enviar.hacienda",
                    "mensaje.error.obtenr.facturas.por.enviar.hacienda");

        }
        return listaFactura;
    }

    public List<LineaDetalleFactura> obtenerDetalleFacturaLineasNotaDebito(Long idNotaDebito) {
        List<LineaDetalleFactura> listaDetalle = new ArrayList<>();
        LineaDetalleFactura detalle = new LineaDetalleFactura();
        try {

            List<DetalleFactura> listaDetalleFactura = em.createQuery("SELECT det FROM DetalleFacturaNotaDebito nd INNER JOIN DetalleFactura det on nd.numero_linea = det.detallePK.numero_linea and nd.id_producto = det.detallePK.id_producto and nd.id_factura = det.detallePK.id_factura where nd.id_nota_debito = :idNotaDebito", DetalleFactura.class)
                    .setParameter("idNotaDebito", idNotaDebito)
                    .getResultList();

            for (DetalleFactura detalleFactura : listaDetalleFactura) {
                Producto producto = servicioProducto.obtenerProductoPorIdProducto(detalleFactura.getDetallePK().getId_producto());

                detalle.setDetalleFactura(detalleFactura);
                detalle.setNumeroLinea(detalleFactura.getDetallePK().getNumero_linea());
                detalle.setCantidad(detalleFactura.getCantidad());
                detalle.setUnidadMedida(servicioProducto.obtenerUnidadMedidaProducto(producto.getId_unidad_medida()).getCodigo_hacienda().trim());
                detalle.setDetalle(producto.getDescripcion().trim());
                detalle.setPrecioUnitario(detalleFactura.getPrecio_neto());
                detalle.setMontoTotal(detalleFactura.getPrecio_neto().multiply(new BigDecimal(detalleFactura.getCantidad().toString())));
                detalle.setMontoDescuento(detalleFactura.getTotal_descuento());
                detalle.setNaturalezaDescuento("Descuento por tipo de cliente");
                detalle.setSubTotal(detalleFactura.getSub_total());
                if (producto.getId_impuesto() != null) {
                    Impuesto impProducto = servicioProducto.obtenerImpuesto(producto.getId_impuesto());
                    com.ebs.modelos.Impuesto impuesto = new com.ebs.modelos.Impuesto();
                    impuesto.setCodigoImpuesto(impProducto.getCodigo_hacienda().trim());
                    impuesto.setPorcentajeImpuesto(Integer.parseInt(impProducto.getValor()));
                    impuesto.setTotal_impuestos(detalleFactura.getTotal_impuestos());
                    detalle.setImpuesto(impuesto);
                }
//                if (producto.getId_exoneracion() != null) {
//                    TipoExoneracion tipoExoneracion = this.servicioProducto.obtenerTipoExoneracion(producto.getId_exoneracion());
//                    
//                    Exoneracion exoneracion = new Exoneracion();
//                    
//                    exoneracion.setTipoDocumento(tipoExoneracion.getCodigo_hacienda());
//                    exoneracion.setNumeroDocumento(producto.getNumero_documento());
//                    exoneracion.setNombreInstitucion(producto.getNombre_institucion());
//                    exoneracion.setFechaEmision(producto.getFecha_emision());
//                    exoneracion.setMontoImpuesto(producto.getMonto_impuesto());
//                    exoneracion.setPorcentajeCompra(producto.getPorcentaje_compra());
//                    
//                    detalle.setExoneracion(exoneracion);
//                }

                detalle.setMontoTotalLinea(detalleFactura.getSub_total().add(detalleFactura.getTotal_impuestos()));
                detalle.setProducto(producto);
                listaDetalle.add(detalle);
                detalle = new LineaDetalleFactura();
            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.factura.obttiene.detalle.factura.hacienda.usuario",
                    "mensaje.factura.obttiene.detalle.factura.hacienda.desarrollador");
        }
        return listaDetalle;
    }

    public List<LineaDetalleFactura> obtenerDetalleFacturaLineasNotaCredito(Long idNotaCredito) {
        List<LineaDetalleFactura> listaDetalle = new ArrayList<>();
        LineaDetalleFactura detalle = new LineaDetalleFactura();
        try {

            List<DetalleFactura> listaDetalleFactura = em.createQuery("SELECT det FROM DetalleFacturaNotaCredito nd INNER JOIN DetalleFactura det on nd.numero_linea = det.detallePK.numero_linea and nd.id_producto = det.detallePK.id_producto and nd.id_factura = det.detallePK.id_factura where nd.id_anulacion = :idNotaCredito", DetalleFactura.class)
                    .setParameter("idNotaCredito", idNotaCredito)
                    .getResultList();

            for (DetalleFactura detalleFactura : listaDetalleFactura) {
                Producto producto = servicioProducto.obtenerProductoPorIdProducto(detalleFactura.getDetallePK().getId_producto());

                detalle.setDetalleFactura(detalleFactura);
                detalle.setNumeroLinea(detalleFactura.getDetallePK().getNumero_linea());
                detalle.setCantidad(detalleFactura.getCantidad());
                detalle.setUnidadMedida(servicioProducto.obtenerUnidadMedidaProducto(producto.getId_unidad_medida()).getCodigo_hacienda().trim());
                detalle.setDetalle(producto.getDescripcion().trim());
                detalle.setPrecioUnitario(detalleFactura.getPrecio_neto());
                detalle.setMontoTotal(detalleFactura.getPrecio_neto().multiply(new BigDecimal(detalleFactura.getCantidad().toString())));
                detalle.setMontoDescuento(detalleFactura.getTotal_descuento());
                detalle.setNaturalezaDescuento("Descuento por tipo de cliente");
                detalle.setSubTotal(detalleFactura.getSub_total());
                if (producto.getId_impuesto() != null) {
                    Impuesto impProducto = servicioProducto.obtenerImpuesto(producto.getId_impuesto());
                    com.ebs.modelos.Impuesto impuesto = new com.ebs.modelos.Impuesto();
                    impuesto.setCodigoImpuesto(impProducto.getCodigo_hacienda().trim());
                    impuesto.setPorcentajeImpuesto(Integer.parseInt(impProducto.getValor()));
                    impuesto.setTotal_impuestos(detalleFactura.getTotal_impuestos());
                    detalle.setImpuesto(impuesto);
                }
//                if (producto.getId_exoneracion() != null) {
//                    TipoExoneracion tipoExoneracion = this.servicioProducto.obtenerTipoExoneracion(producto.getId_exoneracion());
//                    
//                    Exoneracion exoneracion = new Exoneracion();
//                    
//                    exoneracion.setTipoDocumento(tipoExoneracion.getCodigo_hacienda());
//                    exoneracion.setNumeroDocumento(producto.getNumero_documento());
//                    exoneracion.setNombreInstitucion(producto.getNombre_institucion());
//                    exoneracion.setFechaEmision(producto.getFecha_emision());
//                    exoneracion.setMontoImpuesto(producto.getMonto_impuesto());
//                    exoneracion.setPorcentajeCompra(producto.getPorcentaje_compra());
//                    
//                    detalle.setExoneracion(exoneracion);
//                }

                detalle.setMontoTotalLinea(detalleFactura.getSub_total().add(detalleFactura.getTotal_impuestos()));
                detalle.setProducto(producto);
                listaDetalle.add(detalle);
                detalle = new LineaDetalleFactura();
            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.factura.obttiene.detalle.factura.hacienda.usuario",
                    "mensaje.factura.obttiene.detalle.factura.hacienda.desarrollador");
        }
        return listaDetalle;
    }

    public List<FacturaTrazabilidad> obtenerTrazabilidadFactura(Long idFactura) {
        List<FacturaTrazabilidad> resultado = null;
        try {
            resultado = em.createQuery("SELECT dt FROM FacturaTrazabilidad dt WHERE dt.id_factura=:idfactura", FacturaTrazabilidad.class)
                    .setParameter("idfactura", idFactura)
                    .getResultList();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.factura.obttiene.detalle.factura.hacienda.usuario",
                    "mensaje.factura.obttiene.detalle.factura.hacienda.desarrollador");
        }
        return resultado;
    }

    /**
     * Método que obtiene la información de la factura
     *
     * @param idFactura
     * @return
     */
    public List<LineaDetalleFactura> obtenerDetalleFactura(Long idFactura) {
        List<LineaDetalleFactura> listaDetalle = new ArrayList<>();
        LineaDetalleFactura detalle = new LineaDetalleFactura();
        try {

            List<DetalleFactura> listaDetalleFactura = em.createQuery("SELECT dt FROM DetalleFactura dt WHERE dt.detallePK.id_factura=:idfactura", DetalleFactura.class)
                    .setParameter("idfactura", idFactura)
                    .getResultList();

            for (DetalleFactura detalleFactura : listaDetalleFactura) {
                Producto producto = servicioProducto.obtenerProductoPorIdProducto(detalleFactura.getDetallePK().getId_producto());

                detalle.setDetalleFactura(detalleFactura);
                detalle.setNumeroLinea(detalleFactura.getDetallePK().getNumero_linea());
                detalle.setCantidad(detalleFactura.getCantidad());
                detalle.setUnidadMedida(servicioProducto.obtenerUnidadMedidaProducto(producto.getId_unidad_medida()).getCodigo_hacienda().trim());
                detalle.setDetalle(producto.getDescripcion().trim());
                detalle.setPrecioUnitario(detalleFactura.getPrecio_neto());
                detalle.setMontoTotal(detalleFactura.getPrecio_neto().multiply(new BigDecimal(detalleFactura.getCantidad().toString())));
                detalle.setMontoDescuento(detalleFactura.getTotal_descuento());
                detalle.setNaturalezaDescuento("Descuento por tipo de cliente");
                detalle.setSubTotal(detalleFactura.getSub_total());
                if (producto.getId_impuesto() != null) {
                    Impuesto impProducto = servicioProducto.obtenerImpuesto(producto.getId_impuesto());
                    com.ebs.modelos.Impuesto impuesto = new com.ebs.modelos.Impuesto();
                    impuesto.setCodigoImpuesto(impProducto.getCodigo_hacienda().trim());
                    impuesto.setPorcentajeImpuesto(Integer.parseInt(impProducto.getValor()));
                    impuesto.setTotal_impuestos(detalleFactura.getTotal_impuestos());
                    detalle.setImpuesto(impuesto);
                }
////                if (producto.getId_exoneracion() != null) {
////                    TipoExoneracion tipoExoneracion = this.servicioProducto.obtenerTipoExoneracion(producto.getId_exoneracion());
////                    
////                    Exoneracion exoneracion = new Exoneracion();
////                    
////                    exoneracion.setTipoDocumento(tipoExoneracion.getCodigo_hacienda());
////                    exoneracion.setNumeroDocumento(producto.getNumero_documento());
////                    exoneracion.setNombreInstitucion(producto.getNombre_institucion());
////                    exoneracion.setFechaEmision(producto.getFecha_emision());
////                    exoneracion.setMontoImpuesto(producto.getMonto_impuesto());
////                    exoneracion.setPorcentajeCompra(producto.getPorcentaje_compra());
////                    
////                    detalle.setExoneracion(exoneracion);
////                }

                detalle.setMontoTotalLinea(detalleFactura.getSub_total().add(detalleFactura.getTotal_impuestos()));
                detalle.setProducto(producto);
                listaDetalle.add(detalle);
                detalle = new LineaDetalleFactura();
            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.factura.obttiene.detalle.factura.hacienda.usuario",
                    "mensaje.factura.obttiene.detalle.factura.hacienda.desarrollador");
        }
        return listaDetalle;
    }

    /**
     * Método que construye el consecutivo del documento
     *
     * @param tipoDocumento Factura electrónica 01 Nota de débito electrónica 02
     * Nota de crédito electrónica 03 Tiquete Electrónico 04 Confirmación de
     * aceptación del comprobante electrónico 05 Confirmación de aceptación
     * parcial del comprobante electrónico 06 Confirmación de rechazo del
     * comprobante electrónico 07
     *
     *
     * @return String
     * @throws java.lang.Exception
     */
    public String construirNumeroConsecutivo(String tipoDocumento) throws Exception {
        StringBuilder resultado = new StringBuilder();
        try {
//            Consecutivos consec = this.obtenerInformacionConsecutivo(Utilitario.obtenerIdBodegaUsuario());
//            resultado.append(consec.getNumero_sucursal())
//                    .append(consec.getNumero_caja())
//                    .append(tipoDocumento)
//                    .append(obtenerInformacionBodegaConsecutivo(tipoDocumento));
            resultado.append(Cadenas.CASA_MATRIZ.getCadena())
                    .append(Cadenas.TERMINAL_CENTRAL.getCadena())
                    .append(tipoDocumento)
                    .append(obtenerConsecutivo());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado.toString();
    }

    public Consecutivos obtenerInformacionConsecutivo(Long idBodega) {
        Consecutivos result = null;

        StringBuilder sql = new StringBuilder()
                .append("SELECT consec FROM Consecutivos consec WHERE consec.id_bodega = ").append(Utilitario.obtenerIdBodegaUsuario());
        try {

            result = em.createQuery(sql.toString(), Consecutivos.class).getSingleResult();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obteniendo.info.bodega.consecutivo",
                    "mensaje.error.obteniendo.info.bodega.consecutivo");
        }
        return result;
    }

    public String obtenerInformacionBodegaConsecutivo(String tipoDocumento) {
        Consecutivos result = null;
        String resultado = "";

        StringBuilder sql = new StringBuilder()
                .append(" SELECT t1.id_bodega, t1.numero_sucursal, t1.numero_caja, t1.consecutivo_factura_electronica, t1.consecutivo_tiquete_electronico, t1.consecutivo_nota_credito, t1.consecutivo_nota_debito, t1.consecutivo_documento_confirmacion_aceptacion, t1.consecutivo_documento_confirmacion_rechazo, t1.consecutivo_documento_confirmacion_parcial ")
                .append(" FROM searmedica.consecutivos t1 where  t1.id_bodega = ").append(Utilitario.obtenerIdBodegaUsuario())
                .append(" for update ");

        String campo_buscar = "";

        try {

            switch (tipoDocumento) {
                case "01":
                    campo_buscar = "consecutivo_factura_electronica";
                    break;
                case "02":
                    campo_buscar = "consecutivo_nota_debito";
                    break;
                case "03":
                    campo_buscar = "consecutivo_nota_credito";
                    break;
                case "04":
                    campo_buscar = "consecutivo_tiquete_electronico";
                    break;
                case "05":
                    campo_buscar = "consecutivo_documento_confirmacion_aceptacion";
                    break;
                case "06":
                    campo_buscar = "consecutivo_documento_confirmacion_parcial";
                    break;
                case "07":
                    campo_buscar = "consecutivo_documento_confirmacion_rechazo";
                    break;
            }
            if (campo_buscar.equals("")) {
                throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, null,
                        "mensaje.error.obteniendo.info.bodega.consecutivo",
                        "No se encontro el tipo de documento correspondiente");
            }

            Object[] consecutivo = (Object[]) em.createNativeQuery(sql.toString()).getSingleResult();
            result = new Consecutivos();

            result.setId_bodega(Long.parseLong(consecutivo[0].toString()));
            result.setNumero_sucursal(consecutivo[1].toString());
            result.setNumero_caja(consecutivo[2].toString());
            result.setConsecutivo_factura_electronica(Integer.parseInt(consecutivo[3].toString()));
            result.setConsecutivo_tiquete_electronico(Integer.parseInt(consecutivo[4].toString()));
            result.setConsecutivo_nota_credito(Integer.parseInt(consecutivo[5].toString()));
            result.setConsecutivo_nota_debito(Integer.parseInt(consecutivo[6].toString()));
            result.setConsecutivo_documento_confirmacion_aceptacion(Integer.parseInt(consecutivo[7].toString()));
            result.setConsecutivo_documento_confirmacion_rechazo(Integer.parseInt(consecutivo[8].toString()));
            result.setConsecutivo_documento_confirmacion_parcial(Integer.parseInt(consecutivo[0].toString()));

            Integer numeroConsetivo = 0;

            switch (tipoDocumento) {
                case "01":
                    numeroConsetivo = result.getConsecutivo_factura_electronica();
                    numeroConsetivo++;
                    result.setConsecutivo_factura_electronica(numeroConsetivo);
                    actualizar(result);
                    break;
                case "02":
                    numeroConsetivo = result.getConsecutivo_nota_debito();
                    numeroConsetivo++;
                    result.setConsecutivo_nota_debito(numeroConsetivo);
                    actualizar(result);
                    break;
                case "03":
                    numeroConsetivo = result.getConsecutivo_nota_credito();
                    numeroConsetivo++;
                    result.setConsecutivo_nota_credito(numeroConsetivo);
                    actualizar(result);
                    break;
                case "04":
                    numeroConsetivo = result.getConsecutivo_tiquete_electronico();
                    numeroConsetivo++;
                    result.setConsecutivo_tiquete_electronico(numeroConsetivo);
                    actualizar(result);
                    break;
                case "05":
                    numeroConsetivo = result.getConsecutivo_documento_confirmacion_aceptacion();
                    numeroConsetivo++;
                    result.setConsecutivo_documento_confirmacion_aceptacion(numeroConsetivo);
                    actualizar(result);
                    break;
                case "06":
                    numeroConsetivo = result.getConsecutivo_documento_confirmacion_parcial();
                    numeroConsetivo++;
                    result.setConsecutivo_documento_confirmacion_parcial(numeroConsetivo);
                    actualizar(result);
                    break;
                case "07":
                    numeroConsetivo = result.getConsecutivo_documento_confirmacion_rechazo();
                    numeroConsetivo++;
                    result.setConsecutivo_documento_confirmacion_rechazo(numeroConsetivo);
                    actualizar(result);
                    break;
            }
            resultado = numeroConsetivo.toString();
            while (resultado.length() < 10) {
                resultado = "0" + resultado;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obteniendo.info.bodega.consecutivo",
                    "mensaje.error.obteniendo.info.bodega.consecutivo");
        }
        return resultado;
    }

    public String construirNumeroConsecutivoTiqueteElectronico(String tipoDocumento) throws Exception {
        StringBuilder resultado = new StringBuilder();
        try {
//            Consecutivos consec = this.obtenerInformacionConsecutivo(Utilitario.obtenerIdBodegaUsuario());
//            resultado.append(consec.getNumero_sucursal())
//                    .append(consec.getNumero_caja())
//                    .append(tipoDocumento)
//                    .append(obtenerInformacionBodegaConsecutivo(tipoDocumento));
            resultado.append(Cadenas.CASA_MATRIZ.getCadena())
                    .append(Cadenas.TERMINAL_CENTRAL.getCadena())
                    .append(tipoDocumento)
                    .append(obtenerConsecutivoTiqueteElectronico());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado.toString();
    }

    public String construirNumeroConsecutivoNotaCredito(String tipoDocumento) throws Exception {
        StringBuilder resultado = new StringBuilder();
        try {
//            Consecutivos consec = this.obtenerInformacionConsecutivo(Utilitario.obtenerIdBodegaUsuario());
//            resultado.append(consec.getNumero_sucursal())
//                    .append(consec.getNumero_caja())
//                    .append(tipoDocumento)
//                    .append(obtenerInformacionBodegaConsecutivo(tipoDocumento));
            resultado.append(Cadenas.CASA_MATRIZ.getCadena())
                    .append(Cadenas.TERMINAL_CENTRAL.getCadena())
                    .append(tipoDocumento)
                    .append(obtenerConsecutivoNotaCredito());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado.toString();
    }

    public String construirNumeroConsecutivoNotaDebito(String tipoDocumento) throws Exception {
        StringBuilder resultado = new StringBuilder();
        try {
//            Consecutivos consec = this.obtenerInformacionConsecutivo(Utilitario.obtenerIdBodegaUsuario());
//            resultado.append(consec.getNumero_sucursal())
//                    .append(consec.getNumero_caja())
//                    .append(tipoDocumento)
//                    .append(obtenerInformacionBodegaConsecutivo(tipoDocumento));
            resultado.append(Cadenas.CASA_MATRIZ.getCadena())
                    .append(Cadenas.TERMINAL_CENTRAL.getCadena())
                    .append(tipoDocumento)
                    .append(obtenerConsecutivoNotaDebito());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado.toString();
    }

    /**
     * Método que obtiene las fecha de la base de datos
     *
     * @return Object[](mes, dia, año)
     */
    public Object[] obtenerFechasParaHacienda() {
        Object[] resultado = null;
        try {
            String sql = " select to_char(now(),'mm') as mes, to_char(now(),'dd') as dia, to_char(now(),'yy') as anno ";
            resultado = (Object[]) em.createNativeQuery(sql).getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.fecha.base.datos",
                    "mensaje.error.obtener.fecha.base.datos");
        }
        return resultado;

    }

    /**
     * Método que construye la clave numérica del documento
     *
     * @param consecutivo
     * @param cedulaEmisor
     * @param situacionComprobante
     * @return String
     * @throws Exception
     */
    public String construirClaveNumerica(String consecutivo, String cedulaEmisor, String situacionComprobante) throws Exception {
        StringBuilder resultado = new StringBuilder();
        DecimalFormat mFormat = new DecimalFormat("00");

        Object[] fechas = obtenerFechasParaHacienda();
        try {
            resultado.append(Cadenas.CODIGO_PAIS.getCadena())
                    .append(mFormat.format(Double.valueOf(fechas[1].toString())))
                    .append(mFormat.format(Double.valueOf(fechas[0].toString())))
                    .append(Integer.parseInt(fechas[2].toString()) % 100)
                    .append(Utilitario.agregarCeros(cedulaEmisor, 12))
                    .append(consecutivo)
                    .append(situacionComprobante)
                    .append(obtenerCodigoSeguridad());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.clave.numerica.hacienda",
                    "mensaje.error.generar.clave.numerica.hacienda");
        }
        return resultado.toString();
    }

    public boolean yaExisteNumeroConsecutivoNotaDebito(String numeroConsecutivo) throws Exception {
        boolean resultado = false;

        try {
            Object objeto = (Object) em.createNativeQuery("SELECT COUNT(1) FROM SEARMEDICA.factura_nota_debito WHERE NUMERO_CONSECUTIVO like '%" + numeroConsecutivo + "'")
                    .getSingleResult();
            if (Integer.parseInt(objeto.toString()) >= 1) {
                resultado = true;
            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }

        return resultado;
    }

    public boolean yaExisteNumeroConsecutivoNotaCredito(String numeroConsecutivo) throws Exception {
        boolean resultado = false;

        try {
            Object objeto = (Object) em.createNativeQuery("SELECT COUNT(1) FROM SEARMEDICA.FACTURA_ANULACION WHERE NUMERO_CONSECUTIVO like '%" + numeroConsecutivo + "'")
                    .getSingleResult();
            if (Integer.parseInt(objeto.toString()) >= 1) {
                resultado = true;
            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }

        return resultado;
    }

    public boolean yaExisteNumeroConsecutivo(String numeroConsecutivo) throws Exception {
        boolean resultado = false;

        try {
            Object objeto = (Object) em.createNativeQuery("SELECT COUNT(1) FROM SEARMEDICA.FACTURA WHERE NUMERO_CONSECUTIVO like '%" + numeroConsecutivo + "'")
                    .getSingleResult();
            if (Integer.parseInt(objeto.toString()) >= 1) {
                resultado = true;
            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }

        return resultado;
    }

    public String obtenerConsecutivoNotaDebito() throws Exception {
        String resultado = "";
        boolean puedeContinuar = false;
        try {

            while (!puedeContinuar) {
                Object objeto = (Object) em.createNativeQuery("select  nextval('searmedica.seq_nota_debito') as consecutivo").getSingleResult();
                if (objeto == null) {
                    System.out.println("El objeto del número de consecutivo llego nulo, revisar esto:");
                } else {
                    System.out.println("Se obtiene el consecutivo:" + objeto.toString() + " para " + Utilitario.obtenerUsuario().getLogin());
                }
                resultado = objeto.toString();

                while (resultado.length() < 10) {
                    resultado = "0" + resultado;
                }
                if (!yaExisteNumeroConsecutivoNotaDebito(resultado)) {
                    puedeContinuar = true;
                }
            }

            System.out.println("El consecutivo final es:" + resultado);
        } catch (Exception ex) {
            System.out.println("Falló cuando se intentó proceso el método obtenerConsecutivo");
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado;
    }

    public String obtenerConsecutivoNotaCredito() throws Exception {
        String resultado = "";
        boolean puedeContinuar = false;
        try {

            while (!puedeContinuar) {
                Object objeto = (Object) em.createNativeQuery("select  nextval('searmedica.seq_nota_credito') as consecutivo").getSingleResult();
                if (objeto == null) {
                    System.out.println("El objeto del número de consecutivo llego nulo, revisar esto:");
                } else {
                    System.out.println("Se obtiene el consecutivo:" + objeto.toString() + " para " + Utilitario.obtenerUsuario().getLogin());
                }
                resultado = objeto.toString();

                while (resultado.length() < 10) {
                    resultado = "0" + resultado;
                }
                if (!yaExisteNumeroConsecutivoNotaCredito(resultado)) {
                    puedeContinuar = true;
                }
            }

            System.out.println("El consecutivo final es:" + resultado);
        } catch (Exception ex) {
            System.out.println("Falló cuando se intentó proceso el método obtenerConsecutivo");
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado;
    }

    /**
     * Método que obtiene el consecutivo del documento para ser enviado a
     * hacienda
     *
     * @return String
     * @throws Exception
     */
    public String obtenerConsecutivo() throws Exception {
        String resultado = "";
        boolean puedeContinuar = false;
        try {
            // while (!puedeContinuar) {
            Object objeto = (Object) em.createNativeQuery("select  nextval('searmedica.seq_factura_hacienda') as consecutivo").getSingleResult();
            if (objeto == null) {
                System.out.println("El objeto del número de consecutivo llego nulo, revisar esto:");
            } else {
                System.out.println("Se obtiene el consecutivo:" + objeto.toString() + " para " + Utilitario.obtenerUsuario().getLogin());
            }
            resultado = objeto.toString();

            while (resultado.length() < 10) {
                resultado = "0" + resultado;
            }

//                if (!yaExisteNumeroConsecutivo(resultado)) {
//                    puedeContinuar = true;
//                }
//         //   }
            System.out.println("El consecutivo final es:" + resultado);
        } catch (Exception ex) {
            System.out.println("Falló cuando se intentó proceso el método obtenerConsecutivo");
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado;
    }

    public Long obtenerConsecutivoTrazabilidad() throws Exception {
        Long resultado = 0L;

        try {
            // while (!puedeContinuar) {
            Object objeto = (Object) em.createNativeQuery("select  nextval('searmedica.seq_trazabilidad') as consecutivo").getSingleResult();
            if (objeto == null) {
                System.out.println("El objeto del número de consecutivo llego nulo, revisar esto:");
            } else {
                System.out.println("Se obtiene el consecutivo TRAZABILIDAD:" + objeto.toString());
            }
            resultado = Long.parseLong(objeto.toString());

            System.out.println("El consecutivo TRAZABILIDAD es:" + resultado);
        } catch (Exception ex) {
            System.out.println("Falló cuando se intentó proceso el método obtenerConsecutivo");
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado;
    }

    public String obtenerConsecutivoTiqueteElectronico() throws Exception {
        String resultado = "";
        boolean puedeContinuar = false;
        try {
            // while (!puedeContinuar) {
            Object objeto = (Object) em.createNativeQuery("select  nextval('searmedica.seq_tiquete_electronico') as consecutivo").getSingleResult();
            if (objeto == null) {
                System.out.println("El objeto del número de consecutivo llego nulo, revisar esto:");
            } else {
                System.out.println("Se obtiene el consecutivo TE:" + objeto.toString() + " para " + Utilitario.obtenerUsuario().getLogin());
            }
            resultado = objeto.toString();

            while (resultado.length() < 10) {
                resultado = "0" + resultado;
            }

            System.out.println("El consecutivo TE final es:" + resultado);
        } catch (Exception ex) {
            System.out.println("Falló cuando se intentó proceso el método obtenerConsecutivo");
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
        return resultado;
    }

    /**
     * Método que obtiene el código de seguridad
     *
     * @return Integer
     * @throws Exception
     */
    public Integer obtenerCodigoSeguridad() throws Exception {
        Random rnd = new Random();
        Integer number = 99999999;
//        boolean encontroResultado = false;
        boolean finalizo = false;

//        try {
        while (!finalizo) {
            number = rnd.nextInt(99999999);
            if (number.toString().length() == 8) {
                finalizo = true;
            }
//                
//                try {
//                    Object objeto = (Object) em.createNativeQuery(" SELECT codigo_seguridad FROM searmedica.codigos_seguridad where codigo_seguridad = '" + resultado.toString() + "'").getSingleResult();
//                    if (objeto != null) {
//                        encontroResultado = true;
//                    }
//                } catch (NoResultException nex) {
//                }
//                if (!encontroResultado) {
//                    
//                    if (resultado.toString().length() == 8) {
//                        CodigoSeguridad codigo = new CodigoSeguridad();
//                        codigo.setCodigo_seguridad(resultado.toString());
//                        guardar(codigo);
//                        finalizo = true;
//                    }
//                }
        }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
//                    "mensaje.error.obtener.codigo.seguridad.hacienda",
//                    "mensaje.error.obtener.codigo.seguridad.hacienda");
//        }
        return number;
    }

    public Integer obtenerEstadoFactura(Long idFactura) {
        Factura facturaResultado = null;
        try {
            String hql = " SELECT t1 FROM Factura t1 WHERE id_factura=:idfactura ";
            facturaResultado = em.createQuery(hql, Factura.class)
                    .setParameter("idfactura", idFactura)
                    .getSingleResult();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.factura",
                    "mensaje.obtener.factura.desarrollador");
        }
        return facturaResultado.getEstado_factura();
    }

    /**
     * Método que actualiza la información de la factura
     *
     * @param factura
     */
    public void actualizarFactura(Factura factura) {
        try {
            factura.setFecha_factura_actualizacion(fechaHoraBD());
            actualizar(factura);
        } catch (Exception ex) {
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.actualizar.factura",
                    "mensaje.error.actualizar.factura");
        }
    }

    /**
     * Método que actualiza la información de la factura
     *
     * @param factura
     */
    public void actualizarNotaCredito(AnulacionFactura anulacion) {
        try {

            actualizar(anulacion);
        } catch (Exception ex) {
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.actualizar.factura",
                    "mensaje.error.actualizar.factura");
        }
    }

    /**
     * Método que actualiza la información de la factura
     *
     * @param idFactura
     * @param consecutivo
     * @param clave
     * @param estadoFactura
     * @param fechaEmision
     */
    public void actualizarFactura(Long idFactura, Integer estadoFactura, String fechaEmision) {
        try {
            Factura facturaModificar = obtenerFacturaBusqueda(idFactura);
            facturaModificar.setEstado_factura(estadoFactura);
            facturaModificar.setFecha_emision(fechaEmision);

            actualizar(facturaModificar);

            /*QFactura factura = QFactura.factura;
             qf.update(factura)
             .set(factura.estado_factura, estadoFactura)
             .set(factura.fecha_emision, fechaEmision)
             .where(factura.id_factura.eq(idFactura)).execute();*/
        } catch (Exception ex) {
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.actualizar.factura",
                    "mensaje.error.actualizar.factura");
        }
    }

    /**
     * Método que actualiza la factura
     *
     * @param idFactura
     * @param estadoFactura
     */
    public void actualizarFactura(Long idFactura, Integer estadoFactura) {
        try {
            Factura facturaModificar = obtenerFacturaBusqueda(idFactura);
            facturaModificar.setEstado_factura(estadoFactura);
            actualizar(facturaModificar);

            /*QFactura factura = QFactura.factura;
             qf.update(factura)
             .set(factura.estado_factura, estadoFactura).where(factura.id_factura.eq(idFactura)).execute();*/
        } catch (Exception ex) {
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.actualizar.factura",
                    "mensaje.error.actualizar.factura");
        }
    }

    /**
     * Método que guarda una histórico para la factura
     *
     * @param historico
     */
    public void guardarHistorico(FacturaHistoricoHacienda historico) {
        try {
            historico.setFecha(fechaHoraBD());
            guardar(historico);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.historico",
                    "mensaje.error.guardar.historico");
        }
    }

    public String obtenerFechaEnddMMYYYY(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        System.out.println("=================");
        System.out.println(year);
        System.out.println(month);
        System.out.println(day);
        System.out.println("=================");
        return day + "/" + month + "/" + year;
    }

    /**
     * Método que obtiene las facturas por un rango de fechas
     *
     * @param fechaInicio
     * @param fechaFinal
     * @return
     */
    public List<ConsultaFacturasModelo> consultarFacturasPorFechaPorCondVentaPorUsuarioPorMedioPagoPorBodega(Date fechaInicio, Date fechaFinal, Long idCondicionVenta, Long idMedioPago, Long idTipoFactura, Long idBodega, String usuario, Long idEstado) {
        List<ConsultaFacturasModelo> lista = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setLenient(false);
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" select  ")
                    .append(" t1.id_factura,  ")
                    .append(" t1.login,  ")
                    .append(" t1.numero_consecutivo,  ")
                    .append(" t9.descripcion as tipoFactura,  ")
                    .append(" t2.descripcion as estado_factura,  ")
                    .append(" t1.total_comprobante , ")
                    .append(" t1.nombre_cliente_fantasia,  ")
                    .append(" coalesce(t4.nombre,'') ||' '||coalesce(t4.primer_apellido,'')||' '||coalesce(t4.segundo_apellido,'') as nombre ,  ")
                    .append(" t8.descripcion as medioPago,  ")
                    .append(" t7.descripcion as condicionVenta, ")
                    .append(" t1.plazo_credito,  ")
                    .append(" t10.motivo_anulacion,  ")
                    .append(" t11.descripcion as bodega, t4.numero_cedula ")
                    .append(" from searmedica.factura t1 left join searmedica.estados t2  ")
                    .append(" on t1.estado_factura = t2.id_estado left join searmedica.cliente t3  ")
                    .append(" on t1.id_cliente = t3.id_cliente left join searmedica.persona t4  ")
                    .append(" on t3.numero_cedula = t4.numero_cedula left join searmedica.condicion_venta t7 ")
                    .append(" on t1.id_cond_venta = t7.id_cond_venta left join searmedica.medio_pago t8 ")
                    .append(" on t1.id_medio_pago = t8.id_medio_pago left join searmedica.tipo_factura t9 ")
                    .append(" on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10 ")
                    .append(" on t1.id_anulacion = t10.id_anulacion left join searmedica.bodega t11 ")
                    .append(" on t1.id_bodega = t11.id_bodega")
                    .append(" where to_date(to_char(t1.fecha_factura, 'DD/MM/YYYY'),'DD/MM/YYYY') between to_date(:fechaInicio,'DD/MM/YYYY')  and to_date(:fechaFinal,'DD/MM/YYY') ")
                    //.append(" or to_date(to_char(t1.fecha_factura_actualizacion, 'DD/MM/YYYY'),'DD/MM/YYYY') between to_date(:fechaInicio,'DD/MM/YYYY')  and to_date(:fechaFinal,'DD/MM/YYY') ")
                    .append(idCondicionVenta.equals(0l) ? "" : " AND T1.ID_COND_VENTA = " + idCondicionVenta)
                    .append(idMedioPago.equals(0l) ? "" : " AND T1.ID_MEDIO_PAGO = " + idMedioPago)
                    .append(idTipoFactura.equals(0l) ? "" : " AND T1.id_tipo_factura = " + idTipoFactura)
                    .append(idBodega.equals(0l) ? "" : " AND T1.id_bodega = " + idBodega)
                    .append(usuario == null ? "" : " AND T1.login ='" + usuario.trim() + "'")
                    .append(idEstado == 0 ? "" : " AND T1.ESTADO_FACTURA = " + idEstado)
                    .append(" order by t1.numero_consecutivo desc ");

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(consulta.toString())
                    .setParameter("fechaInicio", df.format(fechaInicio)) //obtenerFechaEnddMMYYYY(fechaInicio))
                    .setParameter("fechaFinal", df.format(fechaFinal))//obtenerFechaEnddMMYYYY(fechaFinal))
                    .getResultList();
            ConsultaFacturasModelo factura = null;
            for (Object[] objeto : listaObjetos) {

                factura = new ConsultaFacturasModelo();

                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                factura.setUsuario(objeto[1].toString());
                factura.setIdentificacion(objeto[13] == null ? "" : objeto[13].toString());
                factura.setNumeroConsecutivo(objeto[2] == null ? "" : objeto[2].toString());
                factura.setDescripcionTipoFactura(objeto[3] == null ? "" : objeto[3].toString());
                factura.setEstadoFactura(objeto[4] == null ? "Sin Estado" : objeto[4].toString());
                factura.setMontoTotalComprobante(new BigDecimal(objeto[5].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                factura.setNombreClienteFantasia(objeto[6] == null ? "" : objeto[6].toString());
                factura.setNombreCliente(objeto[7] == null ? "" : objeto[7].toString());
                factura.setDescripcionMedioPago(objeto[8] == null ? "" : objeto[8].toString());
                factura.setDescripcionCondicionVenta(objeto[9] == null ? "" : objeto[9].toString());
                factura.setPlazoCredito(objeto[10] == null ? "" : objeto[10].toString());
                factura.setMotivoAnulacion(objeto[11] == null ? "" : objeto[11].toString());
                factura.setDescripcionBodega(objeto[12] == null ? "" : objeto[12].toString());

                lista.add(factura);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.consulta.factura.usuario.final",
                    "mensaje.error.consulta.factura.usuario.desarrollador");
        }
        return lista;
    }

    /**
     * Método que obtiene las facturas por un rango de fechas
     *
     * @param fechaInicio
     * @param fechaFinal
     * @return
     */
    public List<ConsultaFacturasModelo> consultarFacturasPorIdCliente(Long idCliente) {
        List<ConsultaFacturasModelo> lista = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" select  ")
                    .append(" t1.id_factura,  ")
                    .append(" t1.login,  ")
                    .append(" t1.numero_consecutivo,  ")
                    .append(" t9.descripcion as tipoFactura,  ")
                    .append(" t2.descripcion as estado_factura,  ")
                    .append(" t1.total_comprobante , ")
                    .append(" t1.nombre_cliente_fantasia,  ")
                    .append(" coalesce(t4.nombre,'') ||' '||coalesce(t4.primer_apellido,'')||' '||coalesce(t4.segundo_apellido,'') as nombre ,  ")
                    .append(" t8.descripcion as medioPago,  ")
                    .append(" t7.descripcion as condicionVenta, ")
                    .append(" t1.plazo_credito,  ")
                    .append(" t10.motivo_anulacion,  ")
                    .append(" t11.descripcion as bodega, t4.numero_cedula ")
                    .append(" from searmedica.factura t1 left join searmedica.estados t2  ")
                    .append(" on t1.estado_factura = t2.id_estado left join searmedica.cliente t3  ")
                    .append(" on t1.id_cliente = t3.id_cliente left join searmedica.persona t4  ")
                    .append(" on t3.numero_cedula = t4.numero_cedula left join searmedica.condicion_venta t7 ")
                    .append(" on t1.id_cond_venta = t7.id_cond_venta left join searmedica.medio_pago t8 ")
                    .append(" on t1.id_medio_pago = t8.id_medio_pago left join searmedica.tipo_factura t9 ")
                    .append(" on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10 ")
                    .append(" on t1.id_anulacion = t10.id_anulacion left join searmedica.bodega t11 ")
                    .append(" on t1.id_bodega = t11.id_bodega")
                    .append(" where t1.id_cliente = :idCliente order by t1.numero_consecutivo desc, t1.id_tipo_factura asc ");

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(consulta.toString())
                    .setParameter("idCliente", idCliente)
                    .getResultList();
            ConsultaFacturasModelo factura = null;
            for (Object[] objeto : listaObjetos) {

                factura = new ConsultaFacturasModelo();

                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                factura.setFactura(this.obtenerFacturaBusqueda(factura.getIdFactura()));

                factura.setUsuario(objeto[1].toString());
                factura.setIdentificacion(objeto[13] == null ? "" : objeto[13].toString());
                factura.setNumeroConsecutivo(objeto[2] == null ? "" : objeto[2].toString());
                factura.setDescripcionTipoFactura(objeto[3] == null ? "" : objeto[3].toString());
                factura.setEstadoFactura(objeto[4] == null ? "Sin Estado" : objeto[4].toString());
                factura.setMontoTotalComprobante(new BigDecimal(objeto[5].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                factura.setNombreClienteFantasia(objeto[6] == null ? "" : objeto[6].toString());
                factura.setNombreCliente(objeto[7] == null ? "" : objeto[7].toString());
                factura.setDescripcionMedioPago(objeto[8] == null ? "" : objeto[8].toString());
                factura.setDescripcionCondicionVenta(objeto[9] == null ? "" : objeto[9].toString());
                factura.setPlazoCredito(objeto[10] == null ? "" : objeto[10].toString());
                factura.setMotivoAnulacion(objeto[11] == null ? "" : objeto[11].toString());
                factura.setDescripcionBodega(objeto[12] == null ? "" : objeto[12].toString());

                lista.add(factura);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.consulta.factura.usuario.final",
                    "mensaje.error.consulta.factura.usuario.desarrollador");
        }
        return lista;
    }

    public List<ConsultaFacturasModelo> consultarFacturasPorNumeroConsecutivo(String numeroConsecutivo) {
        List<ConsultaFacturasModelo> resultado = null;
        ConsultaFacturasModelo factura = null;
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" select  ")
                    .append(" t1.id_factura,  ")
                    .append(" t1.login,  ")
                    .append(" t1.numero_consecutivo,  ")
                    .append(" t9.descripcion as tipoFactura,  ")
                    .append(" t2.descripcion as estado_factura,  ")
                    .append(" t1.total_comprobante , ")
                    .append(" t1.nombre_cliente_fantasia,  ")
                    .append(" coalesce(t4.nombre,'') ||' '||coalesce(t4.primer_apellido,'')||' '||coalesce(t4.segundo_apellido,'') as nombre ,  ")
                    .append(" t8.descripcion as medioPago,  ")
                    .append(" t7.descripcion as condicionVenta, ")
                    .append(" t1.plazo_credito,  ")
                    .append(" t10.motivo_anulacion,  ")
                    .append(" t11.descripcion as bodega, t4.numero_cedula ")
                    .append(" from searmedica.factura t1 left join searmedica.estados t2  ")
                    .append(" on t1.estado_factura = t2.id_estado left join searmedica.cliente t3  ")
                    .append(" on t1.id_cliente = t3.id_cliente left join searmedica.persona t4  ")
                    .append(" on t3.numero_cedula = t4.numero_cedula left join searmedica.condicion_venta t7 ")
                    .append(" on t1.id_cond_venta = t7.id_cond_venta left join searmedica.medio_pago t8 ")
                    .append(" on t1.id_medio_pago = t8.id_medio_pago left join searmedica.tipo_factura t9 ")
                    .append(" on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10 ")
                    .append(" on t1.id_anulacion = t10.id_anulacion left join searmedica.bodega t11 ")
                    .append(" on t1.id_bodega = t11.id_bodega")
                    .append(" where t1.numero_consecutivo like '%" + numeroConsecutivo + "' and t1.factura_cancelada = 0 order by t1.numero_consecutivo desc, t1.id_tipo_factura asc ");

            List<Object[]> objetoResultado = (List<Object[]>) em.createNativeQuery(consulta.toString())
                    .getResultList();

            for (Object[] objeto : objetoResultado) {
                if (resultado == null) {
                    resultado = new ArrayList<>();
                }
                factura = new ConsultaFacturasModelo();

                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                factura.setFactura(this.obtenerFacturaBusqueda(factura.getIdFactura()));

                factura.setUsuario(objeto[1].toString());
                factura.setIdentificacion(objeto[13] == null ? "" : objeto[13].toString());
                factura.setNumeroConsecutivo(objeto[2] == null ? "" : objeto[2].toString());
                factura.setDescripcionTipoFactura(objeto[3] == null ? "" : objeto[3].toString());
                factura.setEstadoFactura(objeto[4] == null ? "Sin Estado" : objeto[4].toString());
                factura.setMontoTotalComprobante(new BigDecimal(objeto[5].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                factura.setNombreClienteFantasia(objeto[6] == null ? "" : objeto[6].toString());
                factura.setNombreCliente(objeto[7] == null ? "" : objeto[7].toString());
                factura.setDescripcionMedioPago(objeto[8] == null ? "" : objeto[8].toString());
                factura.setDescripcionCondicionVenta(objeto[9] == null ? "" : objeto[9].toString());
                factura.setPlazoCredito(objeto[10] == null ? "" : objeto[10].toString());
                factura.setMotivoAnulacion(objeto[11] == null ? "" : objeto[11].toString());
                factura.setDescripcionBodega(objeto[12] == null ? "" : objeto[12].toString());

                resultado.add(factura);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.consulta.factura.usuario.final",
                    "mensaje.error.consulta.factura.usuario.desarrollador");
        }
        return resultado;
    }

    public ConsultaFacturasModelo consultarFacturaPorNumeroConsecutivo(String numeroConsecutivo) {
        ConsultaFacturasModelo resultado = null;
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" select  ")
                    .append(" t1.id_factura,  ")
                    .append(" t1.login,  ")
                    .append(" t1.numero_consecutivo,  ")
                    .append(" t9.descripcion as tipoFactura,  ")
                    .append(" t2.descripcion as estado_factura,  ")
                    .append(" t1.total_comprobante , ")
                    .append(" t1.nombre_cliente_fantasia,  ")
                    .append(" coalesce(t4.nombre,'') ||' '||coalesce(t4.primer_apellido,'')||' '||coalesce(t4.segundo_apellido,'') as nombre ,  ")
                    .append(" t8.descripcion as medioPago,  ")
                    .append(" t7.descripcion as condicionVenta, ")
                    .append(" t1.plazo_credito,  ")
                    .append(" t10.motivo_anulacion,  ")
                    .append(" t11.descripcion as bodega, t4.numero_cedula ")
                    .append(" from searmedica.factura t1 left join searmedica.estados t2  ")
                    .append(" on t1.estado_factura = t2.id_estado left join searmedica.cliente t3  ")
                    .append(" on t1.id_cliente = t3.id_cliente left join searmedica.persona t4  ")
                    .append(" on t3.numero_cedula = t4.numero_cedula left join searmedica.condicion_venta t7 ")
                    .append(" on t1.id_cond_venta = t7.id_cond_venta left join searmedica.medio_pago t8 ")
                    .append(" on t1.id_medio_pago = t8.id_medio_pago left join searmedica.tipo_factura t9 ")
                    .append(" on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10 ")
                    .append(" on t1.id_anulacion = t10.id_anulacion left join searmedica.bodega t11 ")
                    .append(" on t1.id_bodega = t11.id_bodega")
                    .append(" where t1.numero_consecutivo like '%" + numeroConsecutivo + "' order by t1.numero_consecutivo desc, t1.id_tipo_factura asc ");

            Object[] objeto = (Object[]) em.createNativeQuery(consulta.toString())
                    .getSingleResult();
            if (objeto != null) {
                resultado = new ConsultaFacturasModelo();

                resultado.setIdFactura(Long.parseLong(objeto[0].toString()));
                resultado.setFactura(this.obtenerFacturaBusqueda(resultado.getIdFactura()));

                resultado.setUsuario(objeto[1].toString());
                resultado.setIdentificacion(objeto[13] == null ? "" : objeto[13].toString());
                resultado.setNumeroConsecutivo(objeto[2] == null ? "" : objeto[2].toString());
                resultado.setDescripcionTipoFactura(objeto[3] == null ? "" : objeto[3].toString());
                resultado.setEstadoFactura(objeto[4] == null ? "Sin Estado" : objeto[4].toString());
                resultado.setMontoTotalComprobante(new BigDecimal(objeto[5].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                resultado.setNombreClienteFantasia(objeto[6] == null ? "" : objeto[6].toString());
                resultado.setNombreCliente(objeto[7] == null ? "" : objeto[7].toString());
                resultado.setDescripcionMedioPago(objeto[8] == null ? "" : objeto[8].toString());
                resultado.setDescripcionCondicionVenta(objeto[9] == null ? "" : objeto[9].toString());
                resultado.setPlazoCredito(objeto[10] == null ? "" : objeto[10].toString());
                resultado.setMotivoAnulacion(objeto[11] == null ? "" : objeto[11].toString());
                resultado.setDescripcionBodega(objeto[12] == null ? "" : objeto[12].toString());
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.consulta.factura.usuario.final",
                    "mensaje.error.consulta.factura.usuario.desarrollador");
        }
        return resultado;
    }

    /**
     * Método que obtiene las facturas por un rango de fechas
     *
     * @param fechaInicio
     * @param fechaFinal
     * @return
     */
    public List<ConsultaFacturasModelo> consultarFacturasPorIdClienteIdCondicionPago(Long idCliente, Long idCondicionVenta) {
        List<ConsultaFacturasModelo> lista = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" select  ")
                    .append(" t1.id_factura,  ")
                    .append(" t1.login,  ")
                    .append(" t1.numero_consecutivo,  ")
                    .append(" t9.descripcion as tipoFactura,  ")
                    .append(" t2.descripcion as estado_factura,  ")
                    .append(" t1.total_comprobante , ")
                    .append(" t1.nombre_cliente_fantasia,  ")
                    .append(" coalesce(t4.nombre,'') ||' '||coalesce(t4.primer_apellido,'')||' '||coalesce(t4.segundo_apellido,'') as nombre ,  ")
                    .append(" t8.descripcion as medioPago,  ")
                    .append(" t7.descripcion as condicionVenta, ")
                    .append(" t1.plazo_credito,  ")
                    .append(" t10.motivo_anulacion,  ")
                    .append(" t11.descripcion as bodega, t4.numero_cedula ")
                    .append(" from searmedica.factura t1 left join searmedica.estados t2  ")
                    .append(" on t1.estado_factura = t2.id_estado left join searmedica.cliente t3  ")
                    .append(" on t1.id_cliente = t3.id_cliente left join searmedica.persona t4  ")
                    .append(" on t3.numero_cedula = t4.numero_cedula left join searmedica.condicion_venta t7 ")
                    .append(" on t1.id_cond_venta = t7.id_cond_venta left join searmedica.medio_pago t8 ")
                    .append(" on t1.id_medio_pago = t8.id_medio_pago left join searmedica.tipo_factura t9 ")
                    .append(" on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10 ")
                    .append(" on t1.id_anulacion = t10.id_anulacion left join searmedica.bodega t11 ")
                    .append(" on t1.id_bodega = t11.id_bodega")
                    .append(" where t1.id_cliente = :idCliente AND t1.id_cond_venta = :idCondVenta and t1.id_anulacion is null order by t1.numero_consecutivo desc, t1.id_tipo_factura asc ");

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(consulta.toString())
                    .setParameter("idCliente", idCliente)
                    .setParameter("idCondVenta", idCondicionVenta)
                    .getResultList();
            ConsultaFacturasModelo factura = null;
            for (Object[] objeto : listaObjetos) {

                factura = new ConsultaFacturasModelo();

                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                factura.setFactura(this.obtenerFacturaBusqueda(factura.getIdFactura()));

                factura.setUsuario(objeto[1].toString());
                factura.setIdentificacion(objeto[13] == null ? "" : objeto[13].toString());
                factura.setNumeroConsecutivo(objeto[2] == null ? "" : objeto[2].toString());
                factura.setDescripcionTipoFactura(objeto[3] == null ? "" : objeto[3].toString());
                factura.setEstadoFactura(objeto[4] == null ? "Sin Estado" : objeto[4].toString());
                factura.setMontoTotalComprobante(new BigDecimal(objeto[5].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                factura.setNombreClienteFantasia(objeto[6] == null ? "" : objeto[6].toString());
                factura.setNombreCliente(objeto[7] == null ? "" : objeto[7].toString());
                factura.setDescripcionMedioPago(objeto[8] == null ? "" : objeto[8].toString());
                factura.setDescripcionCondicionVenta(objeto[9] == null ? "" : objeto[9].toString());
                factura.setPlazoCredito(objeto[10] == null ? "" : objeto[10].toString());
                factura.setMotivoAnulacion(objeto[11] == null ? "" : objeto[11].toString());
                factura.setDescripcionBodega(objeto[12] == null ? "" : objeto[12].toString());

                lista.add(factura);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.consulta.factura.usuario.final",
                    "mensaje.error.consulta.factura.usuario.desarrollador");
        }
        return lista;
    }

    /**
     * Método que obtiene las facturas por un rango de fechas
     *
     * @param fechaInicio
     * @param fechaFinal
     * @return
     */
    public List<ConsultaFacturasModelo> consultarFacturaPorFecha(Date fechaInicio, Date fechaFinal) {
        List<ConsultaFacturasModelo> lista = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" select  ")
                    .append(" t1.id_factura,  ")
                    .append(" t1.login,  ")
                    .append(" t1.numero_consecutivo,  ")
                    .append(" t9.descripcion as tipoFactura,  ")
                    .append(" t2.descripcion as estado_factura,  ")
                    .append(" t1.total_comprobante , ")
                    .append(" t1.nombre_cliente_fantasia,  ")
                    .append(" coalesce(t4.nombre,'') ||' '||coalesce(t4.primer_apellido,'')||' '||coalesce(t4.segundo_apellido,'') as nombre ,  ")
                    .append(" t8.descripcion as medioPago,  ")
                    .append(" t7.descripcion as condicionVenta, ")
                    .append(" t1.plazo_credito,  ")
                    .append(" t10.motivo_anulacion,  ")
                    .append(" t11.descripcion as bodega ")
                    .append(" from searmedica.factura t1 left join searmedica.estados t2  ")
                    .append(" on t1.estado_factura = t2.id_estado left join searmedica.cliente t3  ")
                    .append(" on t1.id_cliente = t3.id_cliente left join searmedica.persona t4  ")
                    .append(" on t3.numero_cedula = t4.numero_cedula left join searmedica.condicion_venta t7 ")
                    .append(" on t1.id_cond_venta = t7.id_cond_venta left join searmedica.medio_pago t8 ")
                    .append(" on t1.id_medio_pago = t8.id_medio_pago left join searmedica.tipo_factura t9 ")
                    .append(" on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10 ")
                    .append(" on t1.id_anulacion = t10.id_anulacion left join searmedica.bodega t11 ")
                    .append(" on t1.id_bodega = t11.id_bodega")
                    .append(" where to_date(to_char(t1.fecha_factura, 'DD/MM/YYYY'),'DD/MM/YYYY') between to_date(:fechaInicio,'DD/MM/YYYY')  and to_date(:fechaFinal,'DD/MM/YYY') ")
                    .append(" order by t1.numero_consecutivo desc, t1.id_tipo_factura asc ");

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(consulta.toString())
                    .setParameter("fechaInicio", df.format(fechaInicio))
                    .setParameter("fechaFinal", df.format(fechaFinal))
                    .getResultList();
            ConsultaFacturasModelo factura = null;
            for (Object[] objeto : listaObjetos) {

                factura = new ConsultaFacturasModelo();

                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                factura.setUsuario(objeto[1].toString());
                factura.setNumeroConsecutivo(objeto[2] == null ? "" : objeto[2].toString());
                factura.setDescripcionTipoFactura(objeto[3] == null ? "" : objeto[3].toString());
                factura.setEstadoFactura(objeto[4] == null ? "Sin Estado" : objeto[4].toString());
                factura.setMontoTotalComprobante(new BigDecimal(objeto[5].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                factura.setNombreClienteFantasia(objeto[6] == null ? "" : objeto[6].toString());
                factura.setNombreCliente(objeto[7] == null ? "" : objeto[7].toString());
                factura.setDescripcionMedioPago(objeto[8] == null ? "" : objeto[8].toString());
                factura.setDescripcionCondicionVenta(objeto[9] == null ? "" : objeto[9].toString());
                factura.setPlazoCredito(objeto[10] == null ? "" : objeto[10].toString());
                factura.setMotivoAnulacion(objeto[11] == null ? "" : objeto[11].toString());
                factura.setDescripcionBodega(objeto[12] == null ? "" : objeto[12].toString());

                lista.add(factura);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.consulta.factura.usuario.final",
                    "mensaje.error.consulta.factura.usuario.desarrollador");
        }
        return lista;
    }

    /**
     * Método que obtiene una lista de factura para el proceso de anulacion
     *
     * @return List<FacturaModeloAnulacion>
     */
    public List<FacturaModeloAnulacion> obtenerTodasFacturasCotizacionesEnvios() {
        List<FacturaModeloAnulacion> lista = new ArrayList<>();
        FacturaModeloAnulacion factura = null;
        try {

            String sql = "select ft.id_factura, ft.numero_consecutivo, est.descripcion as estado_factura, ft.total_comprobante from searmedica.factura ft left join searmedica.estados est on ft.estado_factura = est.id_estado order by ft.numero_consecutivo desc";
            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql).getResultList();

            for (Object[] objeto : listaObjetos) {
                factura = new FacturaModeloAnulacion();
                factura.setConsecutivo(objeto[1] == null ? "" : objeto[1].toString());
                factura.setEstadoHacienda(objeto[2] == null ? "Sin Estado" : objeto[2].toString());
                factura.setMontoTotal(new BigDecimal(objeto[3].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                lista.add(factura);
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.factura.anuacion",
                    "mensaje.error.obtener.factura.anuacion.desarrollador");
        }
        return lista;
    }

    public List<FacturaModeloAnulacion> obtenerNotasCreditoPorIdFactura(Long idFactura) {
        List<FacturaModeloAnulacion> lista = new ArrayList<>();
        FacturaModeloAnulacion factura = null;
        try {
            String sql = " SELECT T1.CLAVE, T1.NUMERO_CONSECUTIVO, T2.DESCRIPCION AS ESTADO, T1.MOTIVO_ANULACION, T1.LOGIN, T1.FECHA_ANULACION, T3.DESCRIPCION, T1.ID_ANULACION "
                    + " FROM SEARMEDICA.FACTURA_ANULACION T1 INNER JOIN SEARMEDICA.ESTADOS T2  "
                    + " ON T1.ID_ESTADO = T2.ID_ESTADO LEFT JOIN SEARMEDICA.MOTIVO_ANULACION T3  "
                    + " ON T1.ID_MOTIVO_ANULACION = T3.ID_MOTIVO_ANULACION "
                    + " WHERE T1.ID_FACTURA = :idFactura";

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("idFactura", idFactura)
                    .getResultList();

            for (Object[] objeto : listaObjetos) {
                factura = new FacturaModeloAnulacion();
                factura.setClave(objeto[0] == null ? "" : objeto[0].toString());
                factura.setConsecutivo(objeto[1] == null ? "" : objeto[1].toString());
                factura.setEstadoHacienda(objeto[2].toString());
                factura.setMotivoAnulacion(objeto[3].toString());
                factura.setLogin(objeto[4].toString());
                factura.setFechaAnulacion(objeto[5].toString());
                factura.setTipoMotivoAnulacion(objeto[6].toString());
                factura.setIdAnulacion(Long.parseLong(objeto[7].toString()));

                lista.add(factura);
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.factura.anuacion",
                    "mensaje.error.obtener.factura.anuacion.desarrollador");
        }
        return lista;
    }

    public List<FacturaModeloNotaDebito> obtenerNotasDebitoPorIdFactura(Long idFactura) {
        List<FacturaModeloNotaDebito> lista = new ArrayList<>();
        FacturaModeloNotaDebito factura = null;
        try {
            String sql = " SELECT T1.CLAVE, T1.NUMERO_CONSECUTIVO, T2.DESCRIPCION AS ESTADO, T1.motivo_nota_debito, T1.LOGIN, T1.fecha_nota_debito, T3.DESCRIPCION, T1.id_nota_debito "
                    + " FROM SEARMEDICA.factura_nota_debito T1 INNER JOIN SEARMEDICA.ESTADOS T2  "
                    + " ON T1.ID_ESTADO = T2.ID_ESTADO LEFT JOIN SEARMEDICA.motivo_nota_debito T3  "
                    + " ON T1.id_motivo_nota_debito = T3.id_motivo_nota_debito "
                    + " WHERE T1.ID_FACTURA = :idFactura";

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("idFactura", idFactura)
                    .getResultList();

            for (Object[] objeto : listaObjetos) {
                factura = new FacturaModeloNotaDebito();
                factura.setClave(objeto[0].toString());
                factura.setConsecutivo(objeto[1].toString());
                factura.setEstadoHacienda(objeto[2].toString());
                factura.setMotivoNotaDebito(objeto[3].toString());
                factura.setLogin(objeto[4].toString());
                factura.setFechaNotaDebito(objeto[5].toString());
                factura.setTipoMotivoNotaDebito(objeto[6].toString());
                factura.setIdNotaDebito(Long.parseLong(objeto[7].toString()));

                lista.add(factura);
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.factura.nota.debito",
                    "mensaje.error.obtener.factura.nota.debito");
        }
        return lista;
    }

    /**
     * Método que obtiene una lista de factura para el proceso de anulacion
     *
     * @return List<FacturaModeloAnulacion>
     */
    public List<FacturaModeloAnulacion> obtenerFacturasAnulacion(String login) {
        List<FacturaModeloAnulacion> lista = new ArrayList<>();
        FacturaModeloAnulacion factura = null;
        try {

            String sql = "select ft.id_factura, ft.numero_consecutivo, est.descripcion as estado_factura, ft.total_comprobante, to_date(to_char(now(), 'DD/MM/YYYY'), 'DD/MM/YYYY') - to_date(to_char(ft.fecha_factura, 'DD/MM/YYYY'), 'DD/MM/YYYY') as cantidad_dias, ft.fecha_factura "
                    + "from searmedica.factura ft left join searmedica.estados est on ft.estado_factura = est.id_estado where ft.estado_factura <> :estadofactura and ft.estado_factura <> :estadofactura2  and ft.login =:login and ft.numero_consecutivo is not null order by ft.fecha_factura desc";
            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("estadofactura", EstadoFactura.ANULADA.getEstadoFactura())
                    .setParameter("estadofactura2", EstadoFactura.PAGADA_PENDIENTE_ENVIO_HACIENDA.getEstadoFactura())
                    .setParameter("login", login)
                    .getResultList();

            for (Object[] objeto : listaObjetos) {
                factura = new FacturaModeloAnulacion();
                //factura.setFactura(this.obtenerFacturaBusqueda(Long.parseLong(objeto[0].toString())));
                //factura.setListaDetalleFactura(this.obtenerDetalleFactura(Long.parseLong(objeto[0].toString())));

                factura.setConsecutivo(objeto[1] == null ? objeto[0].toString() : objeto[1].toString());
                factura.setEstadoHacienda(objeto[2] == null ? "Sin Estado" : objeto[2].toString());
                factura.setMontoTotal(new BigDecimal(objeto[3].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                factura.setCantidadDias(Integer.parseInt(objeto[4].toString()));
                lista.add(factura);
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.factura.anuacion",
                    "mensaje.error.obtener.factura.anuacion.desarrollador");
        }
        return lista;
    }

    public FacturaModeloAnulacion obtenerFacturaModeloAnulacion(Long idFactura) {
        FacturaModeloAnulacion resultado = null;
        try {

            String sql = "select ft.id_factura, ft.numero_consecutivo, est.descripcion as estado_factura, ft.total_comprobante from searmedica.factura ft left join searmedica.estados est on ft.estado_factura = est.id_estado where ft.id_factura = :idFactura";
            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("idFactura", idFactura)
                    .getResultList();

            for (Object[] objeto : listaObjetos) {
                resultado = new FacturaModeloAnulacion();
                resultado.setFactura(this.obtenerFacturaBusqueda(Long.parseLong(objeto[0].toString())));
                resultado.setListaDetalleFactura(this.obtenerDetalleFactura(Long.parseLong(objeto[0].toString())));

                resultado.setConsecutivo(objeto[1] == null ? objeto[0].toString() : objeto[1].toString());
                resultado.setEstadoHacienda(objeto[2] == null ? "Sin Estado" : objeto[2].toString());
                resultado.setMontoTotal(new BigDecimal(objeto[3].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                resultado.setIdFactura(Long.parseLong(objeto[0].toString()));

            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.factura.anuacion",
                    "mensaje.error.obtener.factura.anuacion.desarrollador");
        }
        return resultado;
    }

    /**
     * Método que obtiene una lista de factura para el proceso de anulacion
     *
     * @return List<FacturaModeloAnulacion>
     */
    public List<AnulacionFactura> obtenerFacturasEnviosAnulacion() {
        List<AnulacionFactura> lista = new ArrayList<>();
        try {
            String sql = "SELECT anul FROM AnulacionFactura anul WHERE anul.id_estado=:estadofactura";
            lista = em.createQuery(sql, AnulacionFactura.class)
                    .setParameter("estadofactura", EstadoAnulacion.PENDIENTE_DE_ENVIO_HACIENDA.getEstadoAnulacion()).getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.factura.anuacion",
                    "mensaje.error.obtener.factura.anuacion.desarrollador");
        }
        return lista;
    }

    public boolean determinarDevolucionInventario(List<DetalleFacturaNotaCredito> listaDetalleFacturaNotaCredito, InventarioSalidaDetalle salida) {
        boolean resultado = false;

        for (DetalleFacturaNotaCredito detalleFacturaNotaCredito : listaDetalleFacturaNotaCredito) {
            if (detalleFacturaNotaCredito.getId_producto().equals(salida.getIdProducto())
                    && salida.getNumeroLinea().equals(Long.parseLong(detalleFacturaNotaCredito.getNumero_linea().toString()))) {
                resultado = true;
            }
        }

        return resultado;
    }

    /**
     * Método que guarda la anulación relacionada a una factura
     *
     * @param anulacion
     * @param listaDetalleFacturaNotaCredito
     * @param factura
     * @param facturaNotaDebito
     * @return
     */
    public AnulacionFactura guardarAnulacion(AnulacionFactura anulacion, List<DetalleFacturaNotaCredito> listaDetalleFacturaNotaCredito, Factura factura, FacturaDebito facturaNotaDebito, boolean esNotaCreditoParaFactura, boolean notaDebitoAsociadaFactura) {
        try {
            anulacion.setFecha_anulacion(fechaHoraBD());
            if (anulacion.getNota_credito_interna() != null && (anulacion.getNota_credito_interna().equals(Indicadores.ES_NOTA_CREDITO_INTERNA.getIndicador()))) {
                anulacion.setId_estado(EstadoAnulacion.NOTA_CREDITO_INTERNA.getEstadoAnulacion());
            } else {
                anulacion.setNumero_consecutivo(this.construirNumeroConsecutivoNotaCredito(TipoDocumento.NOTA_DE_CREDITO_ELECTRONICA.getTipoDocumento()));
                anulacion.setClave(
                        this.construirClaveNumerica(
                                anulacion.getNumero_consecutivo(),
                                Utilitario.obtenerEmisor().getIdentificacion().getNumeroCedula(),
                                SituacionComprobante.SITUACION_NORMAL.getSituacion()
                        )
                );

            }
            if (anulacion.getNota_credito_interna() == null) {
                anulacion.setNota_credito_interna(0);
            }
            guardar(anulacion);

            listaDetalleFacturaNotaCredito.forEach(elemento -> {
                elemento.setId_anulacion(anulacion.getId_anulacion());
                guardar(elemento);
                actualizar(elemento.getDetalleFactura());
            });
            if (notaDebitoAsociadaFactura) {
                factura = this.calcularMontosParaFacturaDespuesDeNotaCredito(factura, anulacion);

                actualizar(factura);
            }
            if (esNotaCreditoParaFactura) {

                factura = this.calcularMontosParaFacturaDespuesDeNotaCredito(factura, anulacion);
                if (anulacion.getId_tipo_doc_referencia().equals(1L)) {
                    factura.setEstado_factura(EstadoFactura.ANULADA.getEstadoFactura());
                }
                actualizar(factura);

                List<InventarioSalidaDetalle> listaSalidasDetalle = servicioInventario.obtenerInvSaliDetallePorIdFactura(anulacion.getId_factura());
                Inventario inventarioSeleccionado = null;
                Long idSolicitudInventario = null;

                int contadorEliminacion = 0;
                for (InventarioSalidaDetalle detalle : listaSalidasDetalle) {

                    if (determinarDevolucionInventario(listaDetalleFacturaNotaCredito, detalle)) {
                        inventarioSeleccionado = this.servicioInventario.obtenerInventarioPorId(detalle.getIdInventario());
                        inventarioSeleccionado.setCantExistencia(inventarioSeleccionado.getCantExistencia() + detalle.getCantidad());
                        idSolicitudInventario = detalle.getIdInventarioSalida();

                        if (inventarioSeleccionado.getCantExistencia().equals(0L)) {
                            inventarioSeleccionado.setActivo(0l);
                        } else {
                            inventarioSeleccionado.setActivo(1L);
                        }
                        this.servicioInventario.eliminarRegistroInventarioSalidaDetalle(detalle);
                        crearNuevaTrazabilidad(detalle.getIdProducto(), detalle.getCantidad(), "INGRESO", "POR NOTA CREDITO",
                                (factura.getId_factura() + "-" + factura.getNumero_consecutivo()), anulacion.getLogin(), null, anulacion.getId_bodega());
                        contadorEliminacion++;
                        this.servicioInventario.actualizarInventario(inventarioSeleccionado);
                    }

                }
                if (idSolicitudInventario != null & (listaSalidasDetalle.size() == contadorEliminacion)) {
                    this.servicioInventario.eliminarRegistroInventarioSalidaPorId(idSolicitudInventario);
                }
            } else {
                facturaNotaDebito.setId_estado(EstadoNotaDebito.ANULADA.getEstadoNotaCredito());
                actualizar(facturaNotaDebito);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.anulacion.usuario",
                    "mensaje.error.guardar.anulacion.desarrollador");
        }
        return anulacion;
    }

    public Factura calcularMontosParaFacturaDespuesDeNotaCredito(Factura factura, AnulacionFactura notaCredito) {

        factura.setTotal_descuentos(factura.getTotal_descuentos().subtract(notaCredito.getTotal_descuento()));
        factura.setTotal_impuestos(factura.getTotal_impuestos().subtract(notaCredito.getTotal_impuesto()));
        factura.setTotal_venta_neta(factura.getTotal_venta().subtract(notaCredito.getTotal_venta()));
        factura.setTotal_venta(factura.getTotal_venta_neta().subtract(notaCredito.getTotal_venta_neta()));
        factura.setTotal_servicios_grabados(factura.getTotal_servicios_grabados().subtract(notaCredito.getTotal_servicios_grabados()));
        factura.setTotal_servicios_exentos(factura.getTotal_servicios_exentos().subtract(notaCredito.getTotal_servicios_exentos()));
        factura.setTotal_mercancias_gravadas(factura.getTotal_mercancias_gravadas().subtract(notaCredito.getTotal_mercancias_gravadas()));
        factura.setTotal_mercancias_exentas(factura.getTotal_mercancias_exentas().subtract(notaCredito.getTotal_mercancias_exentas()));
        factura.setTotal_gravado(factura.getTotal_gravado().subtract(notaCredito.getTotal_gravado()));
        factura.setTotal_exento(factura.getTotal_exento().subtract(notaCredito.getTotal_exento()));
        factura.setTotal_comprobante(factura.getTotal_comprobante().subtract(notaCredito.getTotal_nota_credito()));
        factura.setTotal_servicios_exonerados(factura.getTotal_servicios_exonerados().subtract(notaCredito.getTotal_servicios_exenonerados()));
        factura.setTotal_mercancias_exonerados(factura.getTotal_mercancias_exonerados().subtract(notaCredito.getTotal_mercancias_exenonerados()));
        factura.setTotal_exonerado(factura.getTotal_exonerado().subtract(notaCredito.getTotal_exonerado()));
        factura.setMonto_restante(factura.getMonto_restante().subtract(notaCredito.getTotal_nota_credito()));

        return factura;
    }

    public void crearNuevaTrazabilidad(Long idProducto, Long cantidad, String gestion,
            String tipo, String numeroConsecutivo, String login, Long idBodegaDestino,
            Long idBodegaOrigen) throws Exception {
        TrazabilidadProducto trazabilidad = new TrazabilidadProducto();

        trazabilidad.setId_trazabilidad(this.obtenerConsecutivoTrazabilidad());
        trazabilidad.setId_producto(idProducto);
        trazabilidad.setCantidad(cantidad);
        trazabilidad.setGestion(gestion);
        trazabilidad.setTipo(tipo);
        trazabilidad.setNumero_consecutivo(numeroConsecutivo);
        trazabilidad.setLogin(login);
        trazabilidad.setId_bodega_destino(idBodegaDestino);
        trazabilidad.setId_bodega_destino(idBodegaDestino);
        trazabilidad.setFecha(fechaHoraBD());
        guardar(trazabilidad);

    }

    /**
     * Método que guarda una histórico para la factura
     *
     * @param historico
     */
    public void guardarHistoricoAnulacion(FacturaAnulacionHistoricoHacienda historico) {
        try {
            historico.setFecha(fechaHoraBD());
            guardar(historico);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.historico",
                    "mensaje.error.guardar.historico");
        }
    }

    /**
     * Mètodo que actualiza el estado de una anulaciòn dada
     *
     * @param idAnulacion Long
     * @param estado Long
     * @param fechaEmision
     */
    public void actualizarAnulacion(Long idAnulacion, Long estado, String fechaEmision) {
        try {
            AnulacionFactura anulacion = new AnulacionFactura();
            anulacion.setId_anulacion(idAnulacion);

            AnulacionFactura anulacionBusqueda = obtenerFacturaAnulacionBusqueda(anulacion.getId_anulacion());
            anulacionBusqueda.setId_estado(estado);
            anulacionBusqueda.setFecha_emision(fechaEmision);

            actualizar(anulacionBusqueda);
            /*QAnulacionFactura anulacion = QAnulacionFactura.anulacionFactura;
             qf.update(anulacion)
             .set(anulacion.id_estado, estado)
             .set(anulacion.fecha_emision, fechaEmision)
             .where(anulacion.id_anulacion.eq(idAnulacion)).execute();*/
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.actualizar.anulacion.estado.usuario",
                    "mensaje.actualizar.anulacion.estado.desarrollador");
        }
    }

    /**
     * Mètodo que actualiza el estado de una anulaciòn dada
     *
     * @param idAnulacion Long
     * @param estado Long
     */
    public void actualizarAnulacion(Long idAnulacion, Long estado) {
        try {

            AnulacionFactura anulacion = new AnulacionFactura();
            anulacion.setId_anulacion(idAnulacion);

            AnulacionFactura anulacionBusqueda = obtenerFacturaAnulacionBusqueda(anulacion.getId_anulacion());
            anulacionBusqueda.setId_estado(estado);

            actualizar(anulacionBusqueda);

            /*QAnulacionFactura anulacion = QAnulacionFactura.anulacionFactura;
             qf.update(anulacion)
             .set(anulacion.id_estado, estado)
             .where(anulacion.id_anulacion.eq(idAnulacion)).execute();*/
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.actualizar.anulacion.estado.usuario",
                    "mensaje.actualizar.anulacion.estado.desarrollador");
        }
    }

    public String obtenerFechaInicioCierre(String login) {
        String resultado = null;
        try {
            String sql = "SELECT to_char(T1.FECHA_FIN, 'DD/MM/YYYY')  FROM SEARMEDICA.CIERRE T1 WHERE T1.ID_CIERRE = (SELECT MAX(T2.ID_CIERRE) FROM SEARMEDICA.CIERRE T2 WHERE T2.LOGIN = :login )";

            Object resultadoConsulta = (Object) em.createNativeQuery(sql)
                    .setParameter("login", login).getSingleResult();

            resultado = resultadoConsulta.toString();

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.cierre",
                    "mensaje.obtener.facturas.cierre.desarrolladoro");
        }
        return resultado;
    }

    public List<FacturaCredito> obtenerListaFacturaCredito(Long idCliente) {

        List<FacturaCredito> listaResultado = null;

        try {

            String sql = "select "
                    + "t1.id_factura, "
                    + "t1.numero_consecutivo , "
                    + "t1.clave, t1.id_cliente ,  "
                    + "trim(COALESCE(t4.NOMBRE,'') ||' '||COALESCE(t4.PRIMER_APELLIDO,'')||' '||COALESCE(t4.SEGUNDO_APELLIDO,'')) AS NOMBRE, "
                    + "t1.total_comprobante ,  "
                    + "t1.monto_restante ,  "
                    + "t5.descripcion as estado_factura,  "
                    + "t1.factura_cancelada ,  "
                    + "CASE WHEN t1.factura_cancelada = 0  THEN 'Pendiente de Pago'  ELSE 'Factura Cancelada' end as estado_credito_factura, "
                    + "to_date(to_char(t1.fecha_factura, 'DD/MM/YYYY'),'DD/MM/YYYY') as fecha_factura , t1.plazo_credito, "
                    + "to_date(to_char(t1.fecha_factura + CAST(t1.plazo_credito||' days' AS INTERVAL), 'DD/MM/YYYY'),'DD/MM/YYYY') as fecha_vencimiento, "
                    + "(case	when (to_date(to_char(now(), 'DD/MM/YYYY'), 'DD/MM/YYYY')-to_date(to_char(t1.fecha_factura + CAST(t1.plazo_credito||' days' AS INTERVAL), 'DD/MM/YYYY'), 'DD/MM/YYYY'))>0 then	to_date(to_char(now(), 'DD/MM/YYYY'), 'DD/MM/YYYY')-to_date(to_char(t1.fecha_factura + CAST(t1.plazo_credito||' days' AS INTERVAL), 'DD/MM/YYYY'), 'DD/MM/YYYY') else 0 end ) as dias_atraso, t1.id_tipo_factura "
                    + "FROM searmedica.factura t1 inner join searmedica.condicion_venta t2  "
                    + "on t1.id_cond_venta = t2.id_cond_venta left join searmedica.cliente t3 "
                    + "on t1.id_cliente = t3.id_cliente left join searmedica.persona t4 "
                    + "on t3.numero_cedula = t4.numero_cedula inner join searmedica.estados t5 "
                    + "on t1.estado_factura = t5.id_estado  "
                    + "where t1.id_cond_venta = 2 and t1.id_cliente = :idcliente and t1.id_tipo_factura in (1,4) "
                    //+ " and t1.factura_cancelada=0 "
                    + "order by t1.factura_cancelada, t1.estado_factura asc, t1.fecha_factura asc  ";
            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql).setParameter("idcliente", idCliente)
                    .getResultList();

            listaResultado = new ArrayList<>();
            FacturaCredito factura = null;
            for (Object[] objeto : listaObjetos) {
                factura = new FacturaCredito();

                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                factura.setNumeroConsecutivo(objeto[1] == null ? null : objeto[1].toString());
                factura.setClave(objeto[2] == null ? null : objeto[2].toString());
                factura.setIdCliente(objeto[3] == null ? null : Long.parseLong(objeto[3].toString()));
                factura.setNombre(objeto[4] == null ? null : objeto[4].toString());
                factura.setTotal_comprobante(new BigDecimal(Double.parseDouble(objeto[5].toString())));
                factura.setMonto_restante(new BigDecimal(Double.parseDouble(objeto[6] == null ? "0.0" : objeto[6].toString())));
                factura.setEstadoFactura(objeto[7].toString());
                factura.setEstado_credito(Integer.valueOf(objeto[8].toString()));
                factura.setEstado_credito_descripcion(objeto[9].toString());
                factura.setFechaFactura(objeto[10].toString());
                factura.setPlazoCredito(objeto[11].toString());
                factura.setFechaVencimiento(objeto[12].toString());
                factura.setDiasAtraso(Integer.valueOf(objeto[13].toString()));
                factura.setIdTipoFactura(Long.parseLong(objeto[14].toString()));
                listaResultado.add(factura);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.credito",
                    "mensaje.obtener.facturas.credito");
        }
        return listaResultado;

    }

    /**
     * Método que obtiene las facturas para ejecutar el cierre según los
     * parámetros indicados
     *
     * @param login - Uusariso logueado
     * @param fechaInicio - Fecha Incio en formato DD/MM/YYYY
     * @param fechaFinal- Fecha Fin en formato DD/MM/YYYY
     * @return List<FacturaCierreModelo>
     */
    public List<FacturaCierreModelo> obtenerFacturasParaCierre(String login, String fechaInicio, String fechaFinal) {
        List<FacturaCierreModelo> listaResultado = null;

        try {

            String sql = " SELECT T1.ID_FACTURA, T1.FECHA_FACTURA, T1.NUMERO_CONSECUTIVO, T5.ID_COND_VENTA,  T5.DESCRIPCION AS CONDICION_VENTA, T6.ID_MEDIO_PAGO, T6.DESCRIPCION AS MEDIO_PAGO, T1.NOMBRE_CLIENTE_FANTASIA, COALESCE(T8.NOMBRE,'') ||' '||COALESCE(T8.PRIMER_APELLIDO,'')||' '||COALESCE(T8.SEGUNDO_APELLIDO,'') AS NOMBRE,  T1.TOTAL_COMPROBANTE "
                    + " FROM SEARMEDICA.FACTURA T1 LEFT JOIN SEARMEDICA.CONDICION_VENTA T5  "
                    + " ON T1.ID_COND_VENTA = T5.ID_COND_VENTA  LEFT JOIN SEARMEDICA.MEDIO_PAGO T6 "
                    + " ON T1.ID_MEDIO_PAGO = T6.ID_MEDIO_PAGO LEFT JOIN SEARMEDICA.CLIENTE T7 "
                    + " ON T1.ID_CLIENTE = T7.ID_CLIENTE LEFT JOIN SEARMEDICA.PERSONA T8 "
                    + " ON T7.NUMERO_CEDULA = T8.NUMERO_CEDULA "
                    + " where to_date(to_char(t1.fecha_factura, 'DD/MM/YYYY'),'DD/MM/YYYY') between to_date(:fechaInicio,'DD/MM/YYYY')  and to_date(:fechaFinal,'DD/MM/YYY') "
                    // + " or to_date(to_char(t1.fecha_factura_actualizacion, 'DD/MM/YYYY'),'DD/MM/YYYY') between to_date(:fechaInicio,'DD/MM/YYYY')  and to_date(:fechaFinal,'DD/MM/YYY') "
                    + " AND T1.NUMERO_CONSECUTIVO IS NOT NULL "
                    + " AND T1.LOGIN = :login AND T1.ID_ANULACION IS NULL AND NOT EXISTS (SELECT 1 FROM SEARMEDICA.CIERRE_FACTURA T6 WHERE T6.ID_FACTURA = T1.ID_FACTURA )  "
                    + " GROUP BY T1.ID_FACTURA, T1.NOMBRE_CLIENTE_FANTASIA,T8.NOMBRE,T8.PRIMER_APELLIDO, T8.SEGUNDO_APELLIDO,T5.ID_COND_VENTA, T5.DESCRIPCION, T6.ID_MEDIO_PAGO ,T6.DESCRIPCION  ORDER BY T5.ID_COND_VENTA ASC, T1.FECHA_FACTURA ASC";

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("login", login)
                    .setParameter("fechaInicio", fechaInicio)
                    .setParameter("fechaFinal", fechaFinal)
                    .getResultList();

            listaResultado = new ArrayList<>();
            FacturaCierreModelo factura = null;
            for (Object[] objeto : listaObjetos) {
                factura = new FacturaCierreModelo();

                factura.setIdFactura(Long.parseLong(objeto[0].toString()));
                factura.setFechaFactura(objeto[1].toString());
                factura.setNumeroConsecutivo(objeto[2].toString());
                factura.setIdCondicionVenta(Long.parseLong(objeto[3].toString()));
                factura.setCondicionDescripcion(objeto[4].toString());
                factura.setIdMedioPago(Long.parseLong(objeto[5].toString()));
                factura.setDescMedioPago(objeto[6].toString());
                factura.setNombreClienteFantasia(objeto[7] == null ? "" : objeto[7].toString());
                factura.setNombreCliente(objeto[8] == null ? "" : objeto[8].toString());
                factura.setTotalComprobante(new BigDecimal(Double.parseDouble(objeto[9].toString())));

                listaResultado.add(factura);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.cierre",
                    "mensaje.obtener.facturas.cierre.desarrolladoro");
        }
        return listaResultado;
    }

    public List<ReciboCierreModelo> obtenerRecibosParaCierre(String login, String fechaInicio, String fechaFinal) {
        List<ReciboCierreModelo> listaResultado = null;

        try {

            StringBuilder select = new StringBuilder()
                    .append(" SELECT t1.id_recibo, t1.id_factura, t1.monto_pago, t1.monto_total_factura, t1.monto_restante,    ")
                    .append(" t1.fecha, t1.login, t1.id_cliente, t1.concepto_recibo, t1.id_estado, t1.id_medio_pago, t1.numero_referencia, t1.es_recibo_manual, t1.numero_recibo_manual, t1.ind_tomar_cierre, ")
                    .append(" COALESCE(T4.NOMBRE,'') ||' '||COALESCE(T4.PRIMER_APELLIDO,'')||' '||COALESCE(T4.SEGUNDO_APELLIDO,'') AS NOMBRE,  ")
                    .append(" T2.DESCRIPCION AS MEDIO_PAGO, ")
                    .append(" T5.NUMERO_CONSECUTIVO, T6.DESCRIPCION AS ESTADO,  ")
                    .append(" (CASE WHEN t1.es_recibo_manual=1 THEN 'Recibo Manual' ")
                    .append("   ELSE 'Automático' ")
                    .append(" END) as CONDICION_RECIBO, ")
                    .append(" (CASE WHEN (t1.es_recibo_manual=1) THEN T1.numero_recibo_manual ")
                    .append("             ELSE cast (T1.ID_RECIBO as text) ")
                    .append(" END) as NUMERO_RECIBO ")
                    .append(" FROM SEARMEDICA.RECIBO T1 INNER JOIN SEARMEDICA.MEDIO_PAGO T2 ")
                    .append(" ON T1.ID_MEDIO_PAGO = T2.ID_MEDIO_PAGO LEFT JOIN SEARMEDICA.CLIENTE T3  ")
                    .append(" ON T1.ID_CLIENTE = T3.ID_CLIENTE LEFT JOIN SEARMEDICA.PERSONA T4 ")
                    .append(" ON T3.NUMERO_CEDULA = T4.NUMERO_CEDULA LEFT JOIN SEARMEDICA.FACTURA T5 ")
                    .append(" ON T1.ID_FACTURA = T5.ID_FACTURA LEFT JOIN SEARMEDICA.ESTADOS T6 ")
                    .append(" ON T1.ID_ESTADO = T6.ID_ESTADO ")
                    .append(" WHERE  TO_DATE(TO_CHAR(t1.FECHA,'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN TO_DATE(:fechaInicio,'DD/MM/YYYY') AND TO_DATE(:fechaFinal,'DD/MM/YYY')   ")
                    .append(" AND t1.login = :login ")
                    .append(" AND T1.IND_TOMAR_CIERRE = :tomarEnCuenta ")
                    .append(" AND NOT EXISTS (  SELECT  1  FROM  searmedica.cierre_recibo t6  WHERE t6.id_recibo = t1.id_recibo  )  ");

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(select.toString())
                    .setParameter("login", login)
                    .setParameter("fechaInicio", fechaInicio)
                    .setParameter("fechaFinal", fechaFinal)
                    .setParameter("tomarEnCuenta", 1)
                    .getResultList();
            listaResultado = new ArrayList<>();

            ReciboCierreModelo recibo = null;
            for (Object[] objeto : listaObjetos) {
                recibo = new ReciboCierreModelo();
                recibo.setId_recibo(Long.parseLong(objeto[0].toString()));
                recibo.setMonto_pago(new BigDecimal(objeto[2].toString()));
                recibo.setMonto_restante(new BigDecimal(objeto[4].toString()));
                recibo.setEsUnReciboManual(objeto[19].toString());
                recibo.setEstadoRecibo(objeto[18].toString());
                recibo.setNumeroRecibo(objeto[20].toString());
                recibo.setMedioPago(objeto[16].toString());
                recibo.setNumeroConsecutivoFactura(objeto[17].toString());
                recibo.setNombreCliente(objeto[15].toString());
                recibo.setNumero_referencia(objeto[11].toString());

                listaResultado.add(recibo);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.cierre",
                    "mensaje.obtener.facturas.cierre.desarrolladoro");
        }
        return listaResultado;
    }

    public List<CierreTotales> obtenerTotalesCierre(String login, String fechaInicio, String fechaFinal) {
        List<CierreTotales> listaResultado = null;

        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" SELECT   ")
                    .append(" tb.id_medio_pago,  ")
                    .append(" tb.medio_pago,   ")
                    .append(" sum(tb.monto)  ")
                    .append(" FROM  ")
                    .append(" (  ")
                    .append(" SELECT    ")
                    .append(" t6.id_medio_pago,   ")
                    .append(" t6.descripcion   AS medio_pago,  ")
                    .append(" t1.total_comprobante as monto  ")
                    .append(" FROM  searmedica.factura t1   ")
                    .append(" LEFT JOIN searmedica.medio_pago t6 ON t1.id_medio_pago = t6.id_medio_pago   ")
                    .append(" WHERE  TO_DATE(TO_CHAR(t1.fecha_factura,'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN TO_DATE(:fechaInicio,'DD/MM/YYYY') AND TO_DATE(:fechaFinal,'DD/MM/YYY')   ")
                    .append(" AND t1.numero_consecutivo IS NOT NULL   ")
                    .append("AND t1.login = :login   ")
                    .append("AND t1.id_anulacion IS NULL   ")
                    .append("AND NOT EXISTS (  SELECT  1  FROM  searmedica.cierre_factura t6  WHERE t6.id_factura = t1.id_factura  )   ")
                    .append("UNION ALL ")
                    .append("SELECT T1.ID_MEDIO_PAGO, T2.DESCRIPCION AS MEDIO_PAGO, T1.MONTO_PAGO  as monto FROM SEARMEDICA.RECIBO T1 INNER JOIN SEARMEDICA.MEDIO_PAGO T2 ")
                    .append("ON T1.ID_MEDIO_PAGO = T2.ID_MEDIO_PAGO ")
                    .append("WHERE  TO_DATE(TO_CHAR(t1.FECHA,'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN TO_DATE(:fechaInicio2,'DD/MM/YYYY') AND TO_DATE(:fechaFinal2,'DD/MM/YYY')   ")
                    .append("AND t1.login = :login2 ")
                    .append("AND T1.ID_ESTADO =  ").append(EstadoRecibo.PAGADO.getEstadoRecibo())
                    .append("AND T1.IND_TOMAR_CIERRE =  ").append(TomarEnCuentaCierre.TOMAR_EN_CUENTA.getIndicador())
                    .append("AND NOT EXISTS (  SELECT  1  FROM  searmedica.cierre_recibo t6  WHERE t6.id_recibo = t1.id_recibo  )  ")
                    .append(" ) tb  ")
                    .append(" GROUP BY  ")
                    .append(" tb.id_medio_pago,  ")
                    .append("  tb.medio_pago  ");

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(consulta.toString())
                    .setParameter("login", login)
                    .setParameter("fechaInicio", fechaInicio)
                    .setParameter("fechaFinal", fechaFinal)
                    .setParameter("login2", login)
                    .setParameter("fechaInicio2", fechaInicio)
                    .setParameter("fechaFinal2", fechaFinal)
                    .getResultList();

            listaResultado = new ArrayList<>();
            CierreTotales total = null;
            for (Object[] objeto : listaObjetos) {
                total = new CierreTotales();
                total.setDescripcion(objeto[1].toString());
                total.setMonto(new BigDecimal(objeto[2].toString()));
                total.setIdMedioPago(Long.parseLong(objeto[0].toString()));
                listaResultado.add(total);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtenre.totales.factura.cierre",
                    "mensaje.obtenre.totales.factura.cierre");
        }
        return listaResultado;
    }

    /**
     *
     * @param login
     * @param fechaInicio
     * @param fechaFinal
     * @return
     */
    public List<FacturaCierreModelo> obtenerFacturasParaCierreAnulacion(String login, String fechaInicio, String fechaFinal) {
        List<FacturaCierreModelo> listaResultado = null;

        try {

            /*String sql = " SELECT T1.ID_FACTURA, T1.FECHA_FACTURA, T1.NUMERO_CONSECUTIVO, T5.ID_COND_VENTA,  T5.DESCRIPCION AS CONDICION_VENTA,  "
                    + " T6.ID_MEDIO_PAGO, T6.DESCRIPCION AS MEDIO_PAGO, T1.NOMBRE_CLIENTE_FANTASIA,   "
                    + " COALESCE(T8.NOMBRE,'') ||' '||COALESCE(T8.PRIMER_APELLIDO,'')||' '||COALESCE(T8.SEGUNDO_APELLIDO,'') AS NOMBRE,    "
                    + " T12.total_nota_credito as TOTAL_COMPROBANTE, T12.MOTIVO_ANULACION   "
                    + " FROM SEARMEDICA.FACTURA_ANULACION T12 INNER JOIN SEARMEDICA.FACTURA T1  "
                    + " ON T12.ID_FACTURA = T1.ID_FACTURA LEFT JOIN SEARMEDICA.CONDICION_VENTA T5    "
                    + " ON T1.ID_COND_VENTA = T5.ID_COND_VENTA  LEFT JOIN SEARMEDICA.MEDIO_PAGO T6   "
                    + " ON T1.ID_MEDIO_PAGO = T6.ID_MEDIO_PAGO LEFT JOIN SEARMEDICA.CLIENTE T7   "
                    + " ON T1.ID_CLIENTE = T7.ID_CLIENTE LEFT JOIN SEARMEDICA.PERSONA T8   "
                    + " ON T7.NUMERO_CEDULA = T8.NUMERO_CEDULA   "
                    + " where to_date(to_char(T12.fecha_anulacion, 'DD/MM/YYYY'),'DD/MM/YYYY') between to_date(:fechaInicio,'DD/MM/YYYY')  and to_date(:fechaFinal,'DD/MM/YYY')   "
                    + " AND T1.NUMERO_CONSECUTIVO IS NOT NULL   "
                    + " AND T1.LOGIN = :login   ";*/
            //+ " AND T1.ID_ANULACION IS NOT NULL   "
            // + " AND NOT EXISTS (SELECT 1 FROM SEARMEDICA.CIERRE_FACTURA T6 WHERE T6.ID_FACTURA = T1.ID_FACTURA )    "
            // + " GROUP BY T1.ID_FACTURA, T1.NOMBRE_CLIENTE_FANTASIA,T8.NOMBRE,T8.PRIMER_APELLIDO, T8.SEGUNDO_APELLIDO,T5.ID_COND_VENTA, T5.DESCRIPCION,  "
            // + " T6.ID_MEDIO_PAGO ,T6.DESCRIPCION , T12.MOTIVO_ANULACION  ORDER BY T5.ID_COND_VENTA ASC, T1.FECHA_FACTURA ASC  ";
            String sql = " SELECT t1.id_anulacion, t1.id_factura, t1.motivo_anulacion, t2.descripcion as tipo_motivo_anulacion, "
                    + " t1.numero_consecutivo, t1.clave, t1.fecha_emision, t3.descripcion as estado, t1.login, t1.fecha_anulacion, t1.total_nota_credito "
                    + " FROM searmedica.factura_anulacion t1 inner join searmedica.motivo_anulacion t2 "
                    + " on t1.id_motivo_anulacion = t2.id_motivo_anulacion inner join searmedica.estados t3 "
                    + " on t1.id_estado = t3.id_estado "
                    + " where to_date(to_char(t1.fecha_anulacion, 'DD/MM/YYYY'),'DD/MM/YYYY') between to_date(:fechaInicio,'DD/MM/YYYY')  and to_date(:fechaFinal,'DD/MM/YYY')  "
                    + " and t1.login = :login "
                    + " and not exists (select 1 from searmedica.cierre_notas_credito t4 where t4.id_anulacion = t1.id_anulacion) ";

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("login", login)
                    .setParameter("fechaInicio", fechaInicio)
                    .setParameter("fechaFinal", fechaFinal)
                    .getResultList();

            listaResultado = new ArrayList<>();
            FacturaCierreModelo factura = null;
            for (Object[] objeto : listaObjetos) {
                factura = new FacturaCierreModelo();

                factura.setIdFactura(objeto[1] == null ? 0 : Long.parseLong(objeto[1].toString()));
                factura.setIdAnulacion(Long.parseLong(objeto[0].toString()));
                factura.setFechaFactura(objeto[9].toString());
                factura.setNumeroConsecutivo(objeto[4] == null ? "" : objeto[4].toString());
                factura.setTotalComprobante(new BigDecimal(Float.parseFloat(objeto[10].toString())));
                factura.setMotivoAnulacion(objeto[2] == null ? "" : objeto[2].toString());
                factura.setTipoMotivoAnulacion(objeto[4] == null ? "" : objeto[4].toString());

                listaResultado.add(factura);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.cierre",
                    "mensaje.obtener.facturas.cierre.desarrolladoro");
        }

        return listaResultado;
    }

    /**
     * Método que obtiene los tipso de anulacion
     *
     * @return List<MotivoAnulacion>
     */
    public List<TipoDocumentoReferencia> obtenerTiposDocumentosReferencia() {
        List<TipoDocumentoReferencia> listaResultado = null;

        try {
            String sql = "SELECT motv FROM TipoDocumentoReferencia motv ";
            listaResultado = em.createQuery(sql, TipoDocumentoReferencia.class)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.motivo.anulacion.factura.usuario",
                    "mensaje.error.motivo.anulacion.factura.desarrollador");
        }
        return listaResultado;

    }

    /**
     * Método que obtiene los tipso de anulacion
     *
     * @return List<MotivoAnulacion>
     */
    public List<MotivoAnulacion> obtenerTiposMotivosAnulacion() {
        List<MotivoAnulacion> listaResultado = null;

        try {
            String sql = "SELECT motv FROM MotivoAnulacion motv ";
            listaResultado = em.createQuery(sql, MotivoAnulacion.class)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.motivo.anulacion.factura.usuario",
                    "mensaje.error.motivo.anulacion.factura.desarrollador");
        }
        return listaResultado;

    }

    public List<FacturaCorreo> obtenerCorreoFactura(Long idFactura) {
        List<FacturaCorreo> listaResultado = null;

        try {
            String sql = "SELECT correo FROM FacturaCorreo correo WHERE correo.id_factura = :idfactura";
            listaResultado = em.createQuery(sql, FacturaCorreo.class)
                    .setParameter("idfactura", idFactura)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.lista.correo.factura",
                    "mensaje.lista.correo.factura");
        }
        return listaResultado;

    }

    public List<MotivoDebito> obtenerTiposMotivosDebitos() {
        List<MotivoDebito> listaResultado = null;

        try {
            String sql = "SELECT motv FROM MotivoDebito motv ";
            listaResultado = em.createQuery(sql, MotivoDebito.class)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.motivo.anulacion.factura.usuario",
                    "mensaje.error.motivo.anulacion.factura.desarrollador");
        }
        return listaResultado;

    }

    /**
     * Método que obtiene la lista de tipos de cierre existentes
     *
     * @return List<TipoCierre>
     */
    public List<TipoCierre> obtenerTiposCierre() {
        List<TipoCierre> listaResultado = null;

        try {
            String sql = "SELECT tipCierre FROM TipoCierre tipCierre ";
            listaResultado = em.createQuery(sql, TipoCierre.class)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.tipos.cierre",
                    "mensaje.obtener.tipos.cierre.desarrollador");
        }

        return listaResultado;

    }

    /**
     * Método que guarda el cierre diario o semanal
     *
     * @param cierre
     * @param listaCierres
     * @param listaDetalles
     */
    public void guardarCierre(Cierre cierre, List<CierreFactura> listaCierres, List<DetalleCierre> listaDetalles,
            InventarioSolicitud solicitud, List<InventarioSolicitudDetalle> listaSolicitud, List<CierreRecibo> listaRecibo,
            List<CierreNotasCredito> listaFacturasNotasCredito) {
        try {
            cierre.setFecha_generacion(fechaHoraBD());
            guardar(cierre);
            for (CierreFactura listaCierre : listaCierres) {
                listaCierre.setId_cierre(cierre.getId_cierre());
                guardar(listaCierre);
            }
            for (CierreRecibo recibo : listaRecibo) {
                recibo.setId_cierre(cierre.getId_cierre());
                guardar(recibo);
            }
            for (DetalleCierre listaDetalle : listaDetalles) {
                listaDetalle.setId_cierre(cierre.getId_cierre());
                guardar(listaDetalle);
            }
            for (CierreNotasCredito listaDetalle : listaFacturasNotasCredito) {
                listaDetalle.setId_cierre(cierre.getId_cierre());
                guardar(listaDetalle);
            }
            if (solicitud != null) {
                guardar(solicitud);
                if (listaSolicitud != null) {
                    for (InventarioSolicitudDetalle inventarioSolicitudDetalle : listaSolicitud) {
                        inventarioSolicitudDetalle.setIdInventarioSolicitud(solicitud.getIdInventarioSolicitud());
                        guardar(inventarioSolicitudDetalle);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guadar.cierre",
                    "mensaje.error.guadar.cierre.desarrollador");
        }
    }

    public List<FacturaHistoricoHacienda> obtenerHistoricoFacturaHacienda(Long idFactura, String... args) {
        List<FacturaHistoricoHacienda> listaResultados = null;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String estados = "";
        if (args != null) {
            for (String estado : args) {
                estados = estados + (estados.equals("") ? estado : "," + estado);
            }
        }

        String sql = " SELECT T1.ID_FACTURA, T1.ESTADO_FACTURA, T1.FECHA, T1.LOGIN, T1.RESPUESTA, T1.DETALLERESPUESTA, T1.DOCUMENTO_XML_FIRMADO, T2.DESCRIPCION AS ESTADO FROM SEARMEDICA.FACTURA_HISTORICO_HACIENDA T1 LEFT JOIN SEARMEDICA.ESTADOS T2 ON T1.ESTADO_FACTURA = T2.id_estado "
                + " WHERE T1.ID_FACTURA = :idFactura  "
                + (estados.equals("") ? "" : " AND T1.ESTADO_FACTURA in(" + estados + ")");
        try {

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("idFactura", idFactura)
                    .getResultList();
            listaResultados = new ArrayList<>();
            FacturaHistoricoHacienda historico = null;
            for (Object[] resultado : listaObjetos) {
                historico = new FacturaHistoricoHacienda();

                historico.setId_factura(Long.parseLong(resultado[0].toString()));
                historico.setEstado_factura(Integer.parseInt(resultado[1].toString()));
                historico.setRespuesta(resultado[6] == null ? "" : resultado[4].toString());
                historico.setDetallerespuesta(resultado[5] == null ? "" : resultado[5].toString());
                historico.setDocumento_xml_firmado(resultado[6] == null ? "" : resultado[6].toString());
                historico.setEstado(resultado[7] == null ? "" : resultado[7].toString());
                historico.setFecha(resultado[2] == null ? null : df.parse(resultado[2].toString()));
                listaResultados.add(historico);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.historico.factura.usuario",
                    "mensaje.error.obtener.historico.factura.desarrollador");
        }

        return listaResultados;
    }

    public List<FacturaHistoricoHacienda> obtenerHistoricoFacturaHaciendaConsulta(Long idFactura) {
        List<FacturaHistoricoHacienda> listaResultados = null;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String sql = " SELECT T1.ID_FACTURA, T1.ESTADO_FACTURA, T1.FECHA, T1.LOGIN, T1.RESPUESTA, T1.DETALLERESPUESTA, T1.DOCUMENTO_XML_FIRMADO, T2.DESCRIPCION AS ESTADO FROM SEARMEDICA.FACTURA_HISTORICO_HACIENDA T1 LEFT JOIN SEARMEDICA.ESTADOS T2 ON T1.ESTADO_FACTURA = T2.id_estado "
                + " WHERE T1.ID_FACTURA = :idFactura  ";
        try {

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("idFactura", idFactura)
                    .getResultList();
            listaResultados = new ArrayList<>();
            FacturaHistoricoHacienda historico = null;
            for (Object[] resultado : listaObjetos) {
                historico = new FacturaHistoricoHacienda();

                historico.setId_factura(Long.parseLong(resultado[0].toString()));
                historico.setEstado_factura(resultado[1] == null ? 0 : Integer.parseInt(resultado[1].toString()));
                historico.setRespuesta(resultado[4] == null ? "" : resultado[4].toString());
                historico.setDetallerespuesta(resultado[5] == null ? "" : resultado[5].toString());
                historico.setDocumento_xml_firmado(resultado[6] == null ? "" : resultado[6].toString());
                historico.setEstado(resultado[7] == null ? "" : resultado[7].toString());
                historico.setFecha(resultado[2] == null ? null : df.parse(resultado[2].toString()));
                historico.setLogin(resultado[3] == null ? null : resultado[3].toString());
                listaResultados.add(historico);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.historico.factura.usuario",
                    "mensaje.error.obtener.historico.factura.desarrollador");
        }

        return listaResultados;
    }

    public List<FacturaAnulacionHistoricoHacienda> obtenerHistoricoHaciendaNotasCredito(Long idAnulacion, String... args) {
        List<FacturaAnulacionHistoricoHacienda> listaResultados = null;

        String estados = "";
        for (String estado : args) {
            estados = estados + (estados.equals("") ? estado : "," + estado);
        }

        String sql = " SELECT ID_ANULACION, ESTADO_FACTURA, FECHA, LOGIN, RESPUESTA, DETALLERESPUESTA, DOCUMENTO_XML_FIRMADO "
                + " FROM SEARMEDICA.FACTURA_ANULACION_HISTORICO_HACIENDA  "
                + " WHERE ESTADO_FACTURA in(" + estados + ")  AND ID_ANULACION = :idAnulacion";
        try {
            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("idAnulacion", idAnulacion)
                    .getResultList();
            listaResultados = new ArrayList<>();

            FacturaAnulacionHistoricoHacienda historico = null;
            for (Object[] resultado : listaObjetos) {
                historico = new FacturaAnulacionHistoricoHacienda();

                historico.setId_anulacion(Long.parseLong(resultado[0].toString()));
                historico.setEstado_factura(Long.parseLong(resultado[1].toString()));
                historico.setRespuesta(resultado[4] == null ? "" : resultado[4].toString());
                historico.setDetallerespuesta(resultado[5] == null ? "" : resultado[5].toString());
                historico.setDocumento_xml_firmado(resultado[6] == null ? "" : resultado[6].toString());

                listaResultados.add(historico);
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.historico.factura.usuario",
                    "mensaje.error.obtener.historico.factura.desarrollador");
        }

        return listaResultados;
    }

    /**
     * Método que obtiene la lista de facturas para enviar los correos
     * electrónicos
     *
     * @return List<Factura>
     */
    public List<AnulacionFactura> obtenerNotasCreditoPorEnviar() {
        List<AnulacionFactura> listaFacturas = null;
        try {

            listaFacturas = em.createQuery("SELECT f FROM AnulacionFactura f WHERE (f.id_estado=:estado1 OR f.id_estado=:estado2 OR f.id_estado=:estado3) AND f.envio_correo_electronico=:envio", AnulacionFactura.class)
                    .setParameter("estado1", EstadoAnulacion.APROBADA_HACIENDA.getEstadoAnulacion())
                    .setParameter("estado2", EstadoAnulacion.ERROR_HACIENDA.getEstadoAnulacion())
                    .setParameter("estado3", EstadoAnulacion.RECHAZADA_HACIENDA.getEstadoAnulacion())
                    .setParameter("envio", 0)
                    .setMaxResults(10)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.facturas.para.enviar.correo",
                    "mensaje.error.obtener.facturas.para.enviar.correo.desarrollador");
        }
        return listaFacturas;
    }

    /**
     * Método que obtiene la lista de facturas para enviar los correos
     * electrónicos
     *
     * @return List<Factura>
     */
    public List<Factura> obtenerFacturasEnvioCorreo() {
        List<Factura> listaFacturas = null;
        try {
            listaFacturas = em.createQuery("SELECT f FROM Factura f WHERE  (f.estado_factura=:estado1 OR f.estado_factura=:estado2 OR f.estado_factura=:estado3)   AND f.envio_respuesta_hacienda=:envio", Factura.class)
                    .setParameter("estado1", EstadoFactura.APROBADA_HACIENDA.getEstadoFactura())
                    .setParameter("estado2", EstadoFactura.RECHAZADA_HACIENDA.getEstadoFactura())
                    .setParameter("estado3", EstadoFactura.ERROR_HACIENDA.getEstadoFactura())
                    .setParameter("envio", 0)
                    .setMaxResults(10)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.facturas.para.enviar.correo",
                    "mensaje.error.obtener.facturas.para.enviar.correo.desarrollador");
        }
        return listaFacturas;
    }

    /**
     * Método que obtiene el histórico de la factura
     *
     * @param idFactura
     * @param idEstado
     * @return FacturaHistoricoHacienda
     */
    public FacturaHistoricoHacienda obtenerHistoricoHaciendaFactura(Long idFactura, Long idEstado) {
        FacturaHistoricoHacienda historico = null;
        try {
            historico = em.createQuery("SELECT hist FROM FacturaHistoricoHacienda WHERE hist.id_factura=:idFactura AND hist.estado_factura=:estado_factura", FacturaHistoricoHacienda.class)
                    .setParameter("idFactura", idFactura)
                    .setParameter("estado_factura", idEstado)
                    .getSingleResult();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.enviadas",
                    "mensaje.error.obtener.estado.factura.desarrollador");
        }
        return historico;
    }

    public List<AnulacionFactura> obtenerFacturasAnuladasHacienda() {
        List<AnulacionFactura> listaResultado = null;
        try {
            listaResultado = em.createQuery("SELECT f FROM AnulacionFactura f where  f.id_estado=:estadoFactura2", AnulacionFactura.class)
                    .setParameter("estadoFactura2", EstadoAnulacion.ENVIADA_HACIENDA.getEstadoAnulacion())
                    //.setMaxResults(10)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.enviadas",
                    "mensaje.obtener.facturas.enviadas.desarrollador");
        }
        return listaResultado;
    }

    public List<Factura> obtenerFacturasEnviadasHacienda() {
        List<Factura> listaResultado = null;
        try {
            listaResultado = em.createQuery("SELECT f FROM Factura f where f.estado_factura=:estadoFactura OR f.estado_factura=:estadoFactura2", Factura.class)
                    .setParameter("estadoFactura", EstadoFactura.ENVIADA_HACIENDA.getEstadoFactura())
                    .setParameter("estadoFactura2", EstadoFactura.PROCESANDO_HACIENDA.getEstadoFactura())
                    .setMaxResults(10)
                    .getResultList();
        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.facturas.enviadas",
                    "mensaje.obtener.facturas.enviadas.desarrollador");
        }
        return listaResultado;
    }

    public List<FacturaImpresion> obtenerObjetoFacturaConsolidado(Long idFactura) throws SQLException, ClassNotFoundException {
        String sql = "select t1.id_factura,"
                + " t1.numero_consecutivo, t1.clave,t1.fecha_factura,"
                + " t8.descripcion as medioPago, t7.descripcion as condicionVenta,"
                + " t2.cantidad, t2.precio_neto, t2.descuento, t2.total_descuento as descuentoLinea, t2.total_impuestos as impuestosLinea, "
                + " (t2.total_impuestos+t2.sub_total) as totalLinea, t2.sub_total as subtotalLinea,"
                + " t3.descripcion as nombreProducto, t3.codigo_cabys as codigo_cabys, t3.codigo_barras as codigo_barras,"
                + " t4.id_cliente, t4.numero_cedula, t6.descripcion as tipoCedula, '' as correo_electronico, t1.correo_electronico_cliente,"
                + " trim(COALESCE(T5.NOMBRE,'') ||' '||COALESCE(T5.PRIMER_APELLIDO,'')||' '||COALESCE(T5.SEGUNDO_APELLIDO,'')) AS NOMBRE,"
                + " t1.total_comprobante, t1.plazo_credito, t1.total_descuentos, t1.total_impuestos, t2.descripcion as descripcionLinea, t1.nombre_cliente_fantasia, t5.direccion,"
                + " t9.descripcion, t1.id_anulacion, t10.motivo_anulacion, t10.numero_consecutivo as consecutivo_anulacion, t10.clave as clave_anulacion, t11.descripcion as estado_anulacion, t12.descripcion as tipoMotivoAnulacion,"
                + " T1.LOGIN, T1.AGENTE, T5.TELEFONO_1, T5.TELEFONO_2, t13.ubicacion as direccionBodega, t13.telefono_1 as telefonoUnoBodega, t13.telefono_2 as telefonoDosBodega, "
                + " T5.nombre_fantasia as NMBfANT, T1.total_venta_neta, t1.total_descuentos as total_descuentos_factura, t2.monto_total, t1.total_venta"
                + " from searmedica.factura t1 inner join searmedica.detalle_factura t2"
                + " on t1.id_factura = t2.id_factura inner join searmedica.producto t3"
                + " on t2.id_producto = t3.id_producto left join searmedica.cliente t4"
                + " on t1.id_cliente = t4.id_cliente left join searmedica.persona t5"
                + " on t4.numero_cedula = t5.numero_cedula left join searmedica.tipo_cedula t6"
                + " on t5.id_tipo_cedula = t6.id_tipo_cedula inner join searmedica.condicion_venta t7"
                + " on t1.id_cond_venta = t7.id_cond_venta inner join  searmedica.medio_pago t8"
                + " on t1.id_medio_pago = t8.id_medio_pago inner join searmedica.tipo_factura t9"
                + " on t1.id_tipo_factura = t9.id_tipo_factura left join searmedica.factura_anulacion t10"
                + " on t1.id_anulacion = t10.id_anulacion left join searmedica.estados t11"
                + " on t10.id_estado = t11.id_estado left join searmedica.motivo_anulacion t12"
                + " on t10.id_motivo_anulacion = t12.id_motivo_anulacion left join searmedica.bodega t13"
                + " on t1.id_bodega = t13.id_bodega"
                + " where t1.id_factura =  :id_factura"
                + " and t2.es_para_nota_credito = 0";

        List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(sql)
                .setParameter("id_factura", idFactura)
                .getResultList();

        //List<FacturaImpresion> resultado = null;
        // FacturaImpresion recibo = null;
//        ResultSet rsRecibos = null;
//        PreparedStatement pstm = null;
//        Connection conn = null;
//                rsRecibos = pstm.executeQuery();
//        conn = Conexion.obtenerConexion();
//        pstm = conn.prepareStatement(sql.toString());
//        rsRecibos = pstm.executeQuery();
        List<FacturaImpresion> listaFacturaImpresion = new ArrayList<>();
        FacturaImpresion facturaLinea = null;
        for (Object[] objeto : listaObjetos) {
            facturaLinea = new FacturaImpresion();
            facturaLinea.setMontoLetras(Utilitario.Convertir(objeto[23].toString(), true));
            facturaLinea.setId_factura(Long.parseLong(objeto[1].toString()));
            facturaLinea.setNumero_consecutivo(objeto[2] == null ? objeto[1].toString() : objeto[2].toString());
            facturaLinea.setClave(objeto[3] == null ? null : objeto[3].toString());
            facturaLinea.setFecha_factura(objeto[4].toString());
            facturaLinea.setMediopago(objeto[5].toString());
            facturaLinea.setCondicionventa(objeto[6].toString());
            facturaLinea.setCantidad(Integer.parseInt(objeto[7].toString()));
            facturaLinea.setPrecio_neto(new BigDecimal(Double.parseDouble(objeto[8].toString())));
            facturaLinea.setDescuento(new BigDecimal(Double.parseDouble(objeto[9].toString())));
            facturaLinea.setDescuentolinea(new BigDecimal(Double.parseDouble(objeto[10].toString())));
            facturaLinea.setImpuestoslinea(new BigDecimal(Double.parseDouble(objeto[11].toString())));
            facturaLinea.setTotallinea(new BigDecimal(Double.parseDouble(objeto[12].toString())));
            facturaLinea.setSubtotallinea(new BigDecimal(Double.parseDouble(objeto[13].toString())));
            facturaLinea.setNombreproducto(objeto[14].toString());
            facturaLinea.setCodigo_cabys(objeto[15].toString());
            facturaLinea.setCodigo_barras(objeto[16].toString());
            facturaLinea.setId_cliente(objeto[17] == null ? null : Long.parseLong(objeto[17].toString()));
            facturaLinea.setNumero_cedula(objeto[18] == null ? null : objeto[18].toString());
            facturaLinea.setTipocedula(objeto[19] == null ? null : objeto[19].toString());
            facturaLinea.setCorreo_electronico(objeto[20] == null ? null : objeto[20].toString());
            facturaLinea.setCorreo_electronico_cliente(objeto[21] == null ? null : objeto[21].toString());
            facturaLinea.setNombre(objeto[22] == null ? null : objeto[22].toString());
            facturaLinea.setTotal_comprobante(new BigDecimal(Double.parseDouble(objeto[23].toString())));
            facturaLinea.setPlazo_credito(objeto[24] == null ? null : Integer.parseInt(objeto[24].toString()));
            facturaLinea.setTotal_descuentos(new BigDecimal(Double.parseDouble(objeto[25].toString())));
            facturaLinea.setTotal_impuestos(new BigDecimal(Double.parseDouble(objeto[26].toString())));
            facturaLinea.setDescripcionlinea(objeto[27] == null ? "" : objeto[27].toString());
            facturaLinea.setNombre_cliente_fantasia(objeto[28] == null ? null : objeto[28].toString());
            facturaLinea.setDireccion(objeto[29] == null ? null : objeto[29].toString());
            facturaLinea.setDescripcion(objeto[30].toString());
            facturaLinea.setId_anulacion(objeto[31] == null ? null : Long.parseLong(objeto[31].toString()));
            facturaLinea.setMotivo_anulacion(objeto[32] == null ? null : objeto[32].toString());
            facturaLinea.setConsecutivo_anulacion(objeto[33] == null ? null : objeto[33].toString());
            facturaLinea.setClave_anulacion(objeto[34] == null ? null : objeto[34].toString());
            facturaLinea.setEstado_anulacion(objeto[35] == null ? null : objeto[35].toString());
            facturaLinea.setTipomotivoanulacion(objeto[36] == null ? null : objeto[36].toString());
            facturaLinea.setLogin(objeto[37].toString());
            facturaLinea.setAgente(objeto[38].toString());
            facturaLinea.setTelefono_1(objeto[39] == null ? null : objeto[39].toString());
            facturaLinea.setTelefono_2(objeto[40] == null ? null : objeto[40].toString());
            facturaLinea.setDireccionbodega(objeto[41].toString());
            facturaLinea.setTelefonounobodega(objeto[42].toString());
            facturaLinea.setTelefonodosbodega(objeto[43].toString());
            facturaLinea.setNmbfant(objeto[44] == null ? null : objeto[44].toString());
            facturaLinea.setTotal_venta_neta(new BigDecimal(Double.parseDouble(objeto[45].toString())));
            facturaLinea.setTotal_descuentos_factura(new BigDecimal(Double.parseDouble(objeto[46].toString())));
            facturaLinea.setMonto_total(new BigDecimal(Double.parseDouble(objeto[47].toString())));
            facturaLinea.setTotal_venta(new BigDecimal(Double.parseDouble(objeto[48].toString())));
            listaFacturaImpresion.add(facturaLinea);

        }

        return listaFacturaImpresion;

    }

}
