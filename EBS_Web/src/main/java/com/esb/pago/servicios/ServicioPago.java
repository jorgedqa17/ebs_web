/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.pago.servicios;

import com.ebs.constantes.enums.Cadenas;
import com.ebs.constantes.enums.EstadoPago;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.entidades.CondicionImpuesto;
import com.ebs.entidades.Pago;
import com.ebs.entidades.PagoDetalle;
import com.ebs.entidades.PagoXmlAceptado;
import com.ebs.entidades.Parametro;
import com.ebs.entidades.TipoActividad;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.EstadosPagosModelo;
import com.ebs.modelos.PersonaHacienda;
import com.ebs.parametros.servicios.ServicioParametro;
import com.powersystem.util.ServicioBase;
import com.powersystem.utilitario.Utilitario;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Jorge GBSYS
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioPago extends ServicioBase {

    private static final long serialVersionUID = 1L;
    @Inject
    private ServicioParametro servicioParametro;

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

    public String obtenerConsecutivo() throws Exception {
        String resultado = "";
        try {
            Object objeto = (Object) em.createNativeQuery("select  nextval('searmedica.seq_mensaje_receptor') as consecutivo").getSingleResult();
            if (objeto == null) {
                System.out.println("El objeto del número de consecutivo llego nulo, revisar esto:");
            } else {
                System.out.println("Se obtiene el consecutivo:" + objeto.toString() + " para " + Utilitario.obtenerUsuario().getLogin());
            }
            if (objeto.toString().equals("0")) {
                objeto = (Object) em.createNativeQuery("select  nextval('searmedica.seq_mensaje_receptor') as consecutivo").getSingleResult();
            }
            resultado = objeto.toString();

            while (resultado.length() < 10) {
                resultado = "0" + resultado;
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
     * Método que carga la información de la lista de pagos
     *
     * @return List<Pago>
     */
    public List<Pago> obtenerListaPagosPorProcesar() {
        List<Pago> listaResultado = null;
        try {
            String sql = "SELECT pag from Pago pag WHERE pag.id_estado =:estado OR pag.id_estado =:estado2";

            listaResultado = em.createQuery(sql, Pago.class)
                    .setParameter("estado", EstadoPago.PENDIENTE_DE_ENVIO.getIdEstado().intValue())
                    .setParameter("estado2", EstadoPago.ERROR_DE_SERVICIO.getIdEstado().intValue())
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.pagos.obtener.pagos.por.procesar",
                    "mensaje.pagos.obtener.pagos.por.procesar");
        }
        return listaResultado;
    }

    public List<CondicionImpuesto> obtenerListaCondicionesImpuesto() {
        List<CondicionImpuesto> listaResultado = null;
        try {
            String sql = "SELECT cond from CondicionImpuesto cond ";

            listaResultado = em.createQuery(sql, CondicionImpuesto.class)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.pagos.obtener.pagos.por.procesar",
                    "mensaje.pagos.obtener.pagos.por.procesar");
        }
        return listaResultado;
    }

    public List<TipoActividad> obtenerListaActividades() {
        List<TipoActividad> listaResultado = null;
        try {
            String sql = "SELECT cond from TipoActividad cond ";

            listaResultado = em.createQuery(sql, TipoActividad.class)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.pagos.obtener.pagos.por.procesar",
                    "mensaje.pagos.obtener.pagos.por.procesar");
        }
        return listaResultado;
    }

    public List<Pago> obtenerListaPagosEnviados() {
        List<Pago> listaResultado = null;
        try {
            String sql = "SELECT pag from Pago pag WHERE pag.id_estado =:estado OR pag.id_estado =:estado2";

            listaResultado = em.createQuery(sql, Pago.class)
                    .setParameter("estado", EstadoPago.ENVIADO.getIdEstado().intValue())
                    .setParameter("estado2", EstadoPago.PROCESANDO_HACIENDA.getIdEstado().intValue())
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.pagos.obtener.pagos.por.procesar",
                    "mensaje.pagos.obtener.pagos.por.procesar");
        }
        return listaResultado;
    }

    /**
     * Metodo que obtieen un pago por el id
     *
     * @param idPago
     * @return Pago
     */
    public Pago obtenerPagoPorId(Long idPago) {
        Pago pago = null;
        try {
            String sql = "SELECT pag from Pago pag WHERE pag.id_pago =:idPago";

            pago = em.createQuery(sql, Pago.class)
                    .setParameter("idPago", idPago)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.pago.por.id",
                    "mensaje.error.obtener.pago.por.id.desarrollador");
        }

        return pago;
    }

    public Pago obtenerPagoPorClave(String clave) {
        Pago pago = null;
        try {
            String sql = "SELECT pag from Pago pag WHERE pag.clave_comprobante_pago =:clave";

            pago = em.createQuery(sql, Pago.class)
                    .setParameter("clave", clave)
                    .getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.pago.por.id",
                    "mensaje.error.obtener.pago.por.id.desarrollador");
        }

        return pago;
    }

    /**
     * Método que guarda un pago
     *
     * @param pago
     */
    public void guardarPagos(List<Pago> listaDePagosProcesar) {
        try {
            PagoXmlAceptado archivoPago = null;
            for (Pago pago : listaDePagosProcesar) {
                archivoPago = new PagoXmlAceptado();
                pago.setFecha_pago(fechaHoraBD());
                pago.setNumero_consecutivo(construirNumeroConsecutivo(pago.getTipo_mensaje_respuesta()));
                guardar(pago);
                System.out.println("Procesando factura numero: " + pago.getClave_comprobante_pago());
                pago.getListaPagoDetalle().forEach(elementoDetalle -> {
                    elementoDetalle.setId_pago(pago.getId_pago());
                    guardar(elementoDetalle);
                });

                archivoPago.setId_pago(pago.getId_pago());
                archivoPago.setXml_aceptado(pago.getXmlAceptado());
                guardar(archivoPago);
            }
        } catch (Exception ex) {
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.pago.usuario",
                    "mensaje.error.guardar.pago.desarrollador");
        }

    }

    public void guardarPagoNuevo(Pago pago) {
        try {
            PagoXmlAceptado archivoPago = null;

            archivoPago = new PagoXmlAceptado();
            pago.setFecha_pago(fechaHoraBD());
            pago.setNumero_consecutivo(construirNumeroConsecutivo(pago.getTipo_mensaje_respuesta()));
            guardar(pago);
            System.out.println("Procesando factura numero: " + pago.getClave_comprobante_pago());
            pago.getListaPagoDetalle().forEach(elementoDetalle -> {
                elementoDetalle.setId_pago(pago.getId_pago());
                guardar(elementoDetalle);
            });

            archivoPago.setId_pago(pago.getId_pago());
            archivoPago.setXml_aceptado(pago.getXmlAceptado());
            guardar(archivoPago);

        } catch (Exception ex) {
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.pago.usuario",
                    "mensaje.error.guardar.pago.desarrollador");
        }
    }

    public void guardarLineas(Long idPago, List<PagoDetalle> listaPagoDetalle) {
        listaPagoDetalle.forEach(elementoDetalle -> {
            elementoDetalle.setId_pago(idPago);
            guardar(elementoDetalle);
        });

    }

    public void guardarPago(Pago pago) {
        try {

            pago.setFecha_pago(fechaHoraBD());
            guardar(pago);

        } catch (Exception ex) {
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.pago.usuario",
                    "mensaje.error.guardar.pago.desarrollador");
        }

    }

    public void actualizarPago(Pago pagoActualizar) {
        try {
            actualizar(pagoActualizar);
        } catch (Exception ex) {
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.pago.usuario",
                    "mensaje.error.guardar.pago.desarrollador");
        }

    }

    /*
select t1.id_pago, t1.clave_comprobante_pago 
FROM searmedica.pago t1 left join searmedica.pago_xml_aceptado t2
on t1.id_pago  = t2.id_pago 
where t2.xml_aceptado is null
order by t1.id_pago asc

    
     */
    public void guardarXML(Long id, StringBuilder archivo) {
        try {
            guardar(new PagoXmlAceptado(id, archivo.toString()));
            System.out.println("XML Guardardo");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String obtenerXML(Long idPago) {
        String listaResultado = null;
        try {
            String sql = " select t1.id_pago, t2.xml_aceptado "
                    + " FROM searmedica.pago t1 inner join searmedica.pago_xml_aceptado t2 "
                    + " on t1.id_pago  = t2.id_pago "
                    + " where t1.id_pago = " + idPago
                    + " order by t1.id_pago asc ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();

            for (Object[] fila : lista) {

                listaResultado = fila[1].toString();;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.seleccionar.estado.pagos",
                    "mensaje.error.seleccionar.estado.pagos");
        }
        return listaResultado;

    }

    public List<String[]> obtenerPagosTodos() {
        List<String[]> listaResultado = null;
        try {
            String sql = " select t1.id_pago, t1.clave_comprobante_pago \n"
                    + " FROM searmedica.pago t1 left join searmedica.pago_xml_aceptado t2 \n"
                    + " on t1.id_pago  = t2.id_pago \n"
                    + " where t2.xml_aceptado is not null \n"
                    + " order by t1.id_pago asc ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();
            String[] array = null;
            listaResultado = new ArrayList<>();
            for (Object[] fila : lista) {
                array = new String[3];
                array[0] = fila[0].toString();
                array[1] = fila[1].toString();
                // array[2] = fila[2].toString();
                listaResultado.add(array);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.seleccionar.estado.pagos",
                    "mensaje.error.seleccionar.estado.pagos");
        }
        return listaResultado;

    }

    public List<String[]> obtenerPagosTodosSinDeta() {
        List<String[]> listaResultado = null;
        try {
            String sql = " select distinct t1.id_pago, t1.clave_comprobante_pago \n"
                    + "FROM searmedica.pago t1 left join searmedica.pago_detalle t2 \n"
                    + "on t1.id_pago  = t2.id_pago \n"
                    + "where t2.numero_linea is null\n"
                    + "order by t1.id_pago asc";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();
            String[] array = null;
            listaResultado = new ArrayList<>();
            for (Object[] fila : lista) {
                array = new String[3];
                array[0] = fila[0].toString();
                array[1] = fila[1].toString();
                // array[2] = fila[2].toString();
                listaResultado.add(array);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.seleccionar.estado.pagos",
                    "mensaje.error.seleccionar.estado.pagos");
        }
        return listaResultado;

    }

    public List<String[]> obtenerPagosSinARchivos() {
        List<String[]> listaResultado = null;
        try {
            String sql = " select t1.id_pago, t1.clave_comprobante_pago \n"
                    + " FROM searmedica.pago t1 left join searmedica.pago_xml_aceptado t2\n"
                    + " on t1.id_pago  = t2.id_pago \n"
                    + " where t2.xml_aceptado is null\n"
                    + " order by t1.id_pago asc";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();
            String[] array = null;
            listaResultado = new ArrayList<>();
            for (Object[] fila : lista) {
                array = new String[2];
                array[0] = fila[0].toString();
                array[1] = fila[1].toString();
                listaResultado.add(array);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.seleccionar.estado.pagos",
                    "mensaje.error.seleccionar.estado.pagos");
        }
        return listaResultado;

    }

    /**
     * Metodo que obtiene los estados de los pagos
     *
     * @return List<EstadosPagosModelo>
     */
    public List<EstadosPagosModelo> obtenerEstadosPagos() {
        List<EstadosPagosModelo> listaResultado = null;
        try {
            String sql = " SELECT T2.ID_ESTADO, T2.DESCRIPCION FROM SEARMEDICA.CLASES_ESTADOS T1 INNER JOIN SEARMEDICA.ESTADOS T2 "
                    + " ON T1.ID_CLASE = T2.ID_CLASE "
                    + " WHERE T1.ID_CLASE=3 ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();

            EstadosPagosModelo estado = null;
            listaResultado = new ArrayList<>();
            for (Object[] fila : lista) {
                estado = new EstadosPagosModelo();
                estado.setDescripcion(fila[1].toString());
                estado.setIdEstado(Long.parseLong(fila[0].toString()));
                listaResultado.add(estado);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.seleccionar.estado.pagos",
                    "mensaje.error.seleccionar.estado.pagos");
        }
        return listaResultado;

    }

    public void llenarcamposnombrePagos() {

        List<Pago> resultado = null;
        Utilitario utilitarioBusqueda = new Utilitario();
        Parametro parametroEndPointBusqueda = servicioParametro.obtenerValorParametro(ParametrosEnum.END_POINT_CONSULTA_PERSONA_HACIENDA.getIdParametro());
        try {
            String sql_where = "";
            String sql = " SELECT p FROM Pago p ";

            sql = sql + sql_where;
            List<Pago> listaPagos = em.createQuery(sql, Pago.class).getResultList();
            PersonaHacienda personaEncontrada = null;
            for (Pago pago : listaPagos) {
                personaEncontrada = utilitarioBusqueda.obtenerPersonaHacienda(pago.getIdentificacion_proveedor(), parametroEndPointBusqueda.getValor());
                if (personaEncontrada != null) {
                    pago.setCodigo_actividad(personaEncontrada.getActividades().stream().findFirst().get().getCodigo());
                    pago.setNombre_empresa(personaEncontrada.getNombre());
                } else {
                    System.out.println("NULO");
                }
                this.actualizar(pago);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensjae.error.obtener.pagos.cosnutla.administraicon",
                    "mensjae.error.obtener.pagos.cosnutla.administraicon.desarrollador");
        }

    }

    /**
     * Método que obtiene los pagos según los criterio de búsqueda
     *
     * @return List<Pago> resultado
     */
    public List<Pago> obtenerPagosConsulta(Pago pagoConsulta) {
        List<Pago> resultado = null;

        try {
            String sql_where = "";
            String sql
                    = " SELECT T1.ID_PAGO, T1.NUMERO_CONSECUTIVO,T1.CLAVE_COMPROBANTE_PAGO, CASE "
                    + " WHEN (T1.tipo_mensaje_respuesta = '05') THEN 'ACEPTADO' "
                    + " WHEN (T1.tipo_mensaje_respuesta = '06')  THEN 'PARCIALMENTE ACEPTADO' "
                    + " WHEN (T1.tipo_mensaje_respuesta = '07')  THEN 'RECHAZADO' "
                    + " END AS tipo_mensaje_respuesta,  T1.IDENTIFICACION_PROVEEDOR,  "
                    + " T1.MONTO_TOTAL_COMPROBANTE, T1.MONTO_IMPUESTOS , T2.DESCRIPCION "
                    + " FROM SEARMEDICA.PAGO T1 INNER JOIN SEARMEDICA.ESTADOS T2 "
                    + " ON T1.ID_ESTADO = T2.ID_ESTADO ";
            if (pagoConsulta != null) {
                if (pagoConsulta.getId_pago() != null) {
                    sql_where = sql_where + (sql_where.equals("") ? " WHERE T1.ID_PAGO = " + pagoConsulta.getId_pago() : " AND T1.ID_PAGO = " + pagoConsulta.getId_pago());
                }
                if (pagoConsulta.getNumero_consecutivo() != null) {
                    sql_where = sql_where + (sql_where.equals("") ? " WHERE T1.NUMERO_CONSECUTIVO = '" + pagoConsulta.getNumero_consecutivo() + "'" : " AND T1.NUMERO_CONSECUTIVO = '" + pagoConsulta.getNumero_consecutivo() + "'");
                }
                if (pagoConsulta.getClave_comprobante_pago() != null) {
                    sql_where = sql_where + (sql_where.equals("") ? " WHERE T1.CLAVE_COMPROBANTE_PAGO = '" + pagoConsulta.getClave_comprobante_pago() + "'" : " AND T1.CLAVE_COMPROBANTE_PAGO = '" + pagoConsulta.getClave_comprobante_pago() + "'");
                }
                if (pagoConsulta.getMensaje() != null && !pagoConsulta.getMensaje().equals("0")) {
                    sql_where = sql_where + (sql_where.equals("") ? " WHERE T1.mensaje = '" + pagoConsulta.getMensaje() + "'" : " AND T1.mensaje = '" + pagoConsulta.getMensaje() + "'");
                }
                if (pagoConsulta.getId_estado() != null && !pagoConsulta.getId_estado().equals(0)) {
                    sql_where = sql_where + (sql_where.equals("") ? " WHERE T1.id_estado = " + pagoConsulta.getId_estado() : " AND T1.id_estado = " + pagoConsulta.getId_estado());
                }
                if (pagoConsulta.getFecha_pago() != null) {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    sql_where = sql_where + (sql_where.equals("") ? " WHERE TO_DATE(TO_CHAR(T1.FECHA_PAGO,'DD/MM/YYYY'),'DD/MM/YYYY')= TO_DATE('" + df.format(pagoConsulta.getFecha_pago()) + "','DD/MM/YYYY') " : " AND TO_DATE(TO_CHAR(T1.FECHA_PAGO,'DD/MM/YYYY'),'DD/MM/YYYY')= TO_DATE('" + df.format(pagoConsulta.getFecha_pago()) + "','DD/MM/YYYY')  ");
                }
            }
            sql = sql + sql_where;
            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();
            Pago pago = null;
            resultado = new ArrayList<>();

            for (Object[] fila : lista) {
                pago = new Pago();
                pago.setId_pago(Long.parseLong(fila[0].toString()));
                pago.setNumero_consecutivo(fila[1].toString());
                pago.setClave_comprobante_pago(fila[2].toString());
                pago.setMensaje(fila[3].toString());
                pago.setIdentificacion_proveedor(fila[4].toString());
                pago.setMonto_total_comprobante(new BigDecimal(fila[5].toString()));
                pago.setMonto_impuestos(fila[6] != null ? new BigDecimal(fila[6].toString()) : null);
                pago.setDescripcionEstadoPago(fila[7].toString());
                resultado.add(pago);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensjae.error.obtener.pagos.cosnutla.administraicon",
                    "mensjae.error.obtener.pagos.cosnutla.administraicon.desarrollador");
        }
        return resultado;
    }

    /*


    
     */
}
