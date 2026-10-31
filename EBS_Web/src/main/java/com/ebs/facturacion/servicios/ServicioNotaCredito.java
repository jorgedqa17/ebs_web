/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.servicios;

import com.ebs.modelos.NotaCreditoModelo;
import com.powersystem.util.ServicioBase;
import com.ebs.entidades.AnulacionFactura;
import com.ebs.constantes.enums.EstadoAnulacion;
import com.ebs.entidades.Estados;
import com.ebs.entidades.Factura;
import com.ebs.entidades.FacturaDebito;
import com.ebs.modelos.ConsultaNotasCreditoModelo;
import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
import com.ebs.exception.ExcepcionManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jdquesad
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioNotaCredito extends ServicioBase {

    @Inject
    private ServicioFactura servicioFactura;

    /**
     * Consulta las notas de crédito en un rango de fechas con una sola query.
     * Los filtros opcionales se ignoran cuando vienen vacíos o en cero.
     */
    public List<ConsultaNotasCreditoModelo> consultarNotasCreditoPorFiltros(Date fechaInicio, Date fechaFin,
            Long idEstado, String usuario, String consecutivo) {
        List<ConsultaNotasCreditoModelo> lista = new ArrayList<>();
        try {
            boolean filtraEstado = idEstado != null && !idEstado.equals(0L);
            boolean filtraUsuario = usuario != null && !usuario.trim().isEmpty();
            boolean filtraConsecutivo = consecutivo != null && !consecutivo.trim().isEmpty();

            StringBuilder consulta = new StringBuilder()
                    .append(" select t1.id_anulacion, t1.numero_consecutivo, t1.fecha_anulacion, t1.fecha_emision, ")
                    .append(" t1.id_estado, t2.descripcion, t1.login, t1.total_nota_credito, ")
                    .append(" t3.numero_consecutivo as consecutivo_factura, t4.numero_consecutivo as consecutivo_nd ")
                    .append(" from searmedica.factura_anulacion t1 ")
                    .append(" left join searmedica.estados t2 on t1.id_estado = t2.id_estado ")
                    .append(" left join searmedica.factura t3 on t1.id_factura = t3.id_factura ")
                    .append(" left join searmedica.factura_nota_debito t4 on t1.id_nota_debito = t4.id_nota_debito ")
                    .append(" where t1.fecha_anulacion >= :fechaInicio and t1.fecha_anulacion < :fechaFin ")
                    .append(filtraEstado ? " and t1.id_estado = :idEstado " : "")
                    .append(filtraUsuario ? " and t1.login = :usuario " : "")
                    .append(filtraConsecutivo ? " and t1.numero_consecutivo like :consecutivo " : "")
                    .append(" order by t1.id_anulacion desc ");

            Query query = em.createNativeQuery(consulta.toString())
                    .setParameter("fechaInicio", inicioDelDia(fechaInicio, 0), TemporalType.TIMESTAMP)
                    .setParameter("fechaFin", inicioDelDia(fechaFin, 1), TemporalType.TIMESTAMP);
            if (filtraEstado) {
                query.setParameter("idEstado", idEstado);
            }
            if (filtraUsuario) {
                query.setParameter("usuario", usuario.trim());
            }
            if (filtraConsecutivo) {
                query.setParameter("consecutivo", "%" + consecutivo.trim() + "%");
            }

            List<Object[]> listaObjetos = (List<Object[]>) query.getResultList();
            for (Object[] objeto : listaObjetos) {
                ConsultaNotasCreditoModelo fila = new ConsultaNotasCreditoModelo();
                fila.setIdAnulacion(Long.parseLong(objeto[0].toString()));
                fila.setNumeroConsecutivo(objeto[1] == null ? "" : objeto[1].toString());
                fila.setFechaAnulacion(objeto[2] == null ? null : (Date) objeto[2]);
                fila.setFechaEmision(objeto[3] == null ? "" : objeto[3].toString());
                fila.setIdEstado(objeto[4] == null ? null : Long.parseLong(objeto[4].toString()));
                fila.setEstado(objeto[5] == null ? "Sin Estado" : objeto[5].toString());
                fila.setUsuario(objeto[6] == null ? "" : objeto[6].toString());
                fila.setTotalNotaCredito(objeto[7] == null ? null : new BigDecimal(objeto[7].toString()).setScale(3, BigDecimal.ROUND_HALF_EVEN));
                fila.setConsecutivoFactura(objeto[8] == null ? "" : objeto[8].toString());
                fila.setConsecutivoNotaDebito(objeto[9] == null ? "" : objeto[9].toString());
                lista.add(fila);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtenre.notas.creditos",
                    "mensaje.error.obtenre.notas.creditos");
        }
        return lista;
    }

    private Date inicioDelDia(Date fecha, int diasAdicionales) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, diasAdicionales);
        return calendar.getTime();
    }

    public List<Estados> obtenerEstadosNotaCredito() {
        List<Long> ids = new ArrayList<>();
        for (EstadoAnulacion estado : EstadoAnulacion.values()) {
            ids.add(estado.getEstadoAnulacion());
        }
        try {
            return em.createQuery("SELECT e FROM Estados e WHERE e.idEstado IN :ids ORDER BY e.descripcion", Estados.class)
                    .setParameter("ids", ids).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.estado",
                    "mensaje.error.estado");
        }
    }

    /**
     * Carga el detalle de una sola nota de crédito con su factura y nota de
     * débito asociadas. Se usa al seleccionar una fila del listado.
     */
    public NotaCreditoModelo obtenerDetalleNotaCredito(Long idAnulacion) {
        NotaCreditoModelo modelo = new NotaCreditoModelo();
        try {
            AnulacionFactura notaCredito = servicioFactura.obtenerNotaCreditoBusqueda(idAnulacion);
            if (notaCredito == null) {
                return modelo;
            }
            notaCredito.setEstadoFactura(this.obtenerEstado(notaCredito.getId_estado()));
            modelo.setNotaCredito(notaCredito);

            if (notaCredito.getId_factura() != null) {
                Factura factura = servicioFactura.obtenerFacturaBusqueda(notaCredito.getId_factura());
                if (factura != null) {
                    factura.setEstadoFactura(this.obtenerEstado(Long.parseLong(factura.getEstado_factura().toString())));
                    modelo.setFactura(factura);
                }
            }
            if (notaCredito.getId_nota_debito() != null) {
                FacturaDebito notaDebito = servicioFactura.obtenerNotaDebitoBusqueda(notaCredito.getId_nota_debito());
                if (notaDebito != null) {
                    notaDebito.setEstadoFactura(this.obtenerEstado(notaDebito.getId_estado()));
                    modelo.setNotaDebito(notaDebito);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtenre.notas.creditos",
                    "mensaje.error.obtenre.notas.creditos");
        }
        return modelo;
    }

    public List<NotaCreditoModelo> obtenerNotasCredito() {
        List<NotaCreditoModelo> resultado = null;
        try {
            String sql = "SELECT nd FROM AnulacionFactura nd   ";
            List<AnulacionFactura> resultadoND = em.createQuery(sql, AnulacionFactura.class).getResultList();

            NotaCreditoModelo modelo = null;
            for (AnulacionFactura notaCredito : resultadoND) {
                if (resultado == null) {
                    resultado = new ArrayList<>();
                }
                modelo = new NotaCreditoModelo();                
                notaCredito.setEstadoFactura(this.obtenerEstado(notaCredito.getId_estado()));
                modelo.setNotaCredito(notaCredito);

                resultado.add(modelo);
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtenre.notas.creditos",
                    "mensaje.error.obtenre.notas.creditos");
        }

        return resultado;
    }

    public Estados obtenerEstado(Long idEstado) {
        Estados resultado = null;
        try {
            String sql = "SELECT nd FROM Estados nd where nd.idEstado = :idEstado";
            resultado = em.createQuery(sql, Estados.class).setParameter("idEstado", idEstado).getSingleResult();
        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.estado",
                    "mensaje.error.estado");
        }
        return resultado;
    }

    public List<FacturaAnulacionHistoricoHacienda> obtenerListaHistoricoNotaCredito(Long idNotaCredito) {
        List<FacturaAnulacionHistoricoHacienda> resultado = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" SELECT T1.id_anulacion, T1.estado_factura, T1.fecha, T1.login, T1.respuesta,   ")
                    .append(" T1.detallerespuesta, T1.documento_xml_firmado, T1.codigo_respuesta, T2.DESCRIPCION  ")
                    .append(" FROM SEARMEDICA.factura_anulacion_historico_hacienda T1   ")
                    .append(" INNER JOIN SEARMEDICA.ESTADOS T2  ")
                    .append(" ON T1.estado_factura = T2.ID_ESTADO  ")
                    .append(" WHERE T1.id_anulacion = :idNotaCredito  ")
                    .append(" ORDER BY T1.FECHA DESC  ");

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(consulta.toString())
                    .setParameter("idNotaCredito", idNotaCredito)
                    .getResultList();

            FacturaAnulacionHistoricoHacienda historico = null;
            for (Object[] objeto : listaObjetos) {
                historico = new FacturaAnulacionHistoricoHacienda();
                if (resultado == null) {
                    resultado = new ArrayList<>();
                }
                historico.setCodigo_respuesta(objeto[7] == null ? null : Integer.parseInt(objeto[7].toString()));
                historico.setDetallerespuesta(objeto[5] == null ? null : objeto[5].toString());
                historico.setDocumento_xml_firmado(objeto[6] == null ? null : objeto[6].toString());
                historico.setEstado(objeto[8] == null ? null : objeto[8].toString());
                historico.setEstado_factura(objeto[1] == null ? null : Long.parseLong(objeto[1].toString()));
                historico.setFecha(objeto[2] == null ? null : df.parse(objeto[2].toString()));
                historico.setId_anulacion(objeto[0] == null ? null : Long.parseLong(objeto[0].toString()));
                historico.setLogin(objeto[3] == null ? null : objeto[3].toString());
                historico.setRespuesta(objeto[4] == null ? null : objeto[4].toString());
                resultado.add(historico);
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.historico.nota.credito",
                    "mensaje.error.obtener.historico.nota.credito");
        }
        return resultado;
    }
}
