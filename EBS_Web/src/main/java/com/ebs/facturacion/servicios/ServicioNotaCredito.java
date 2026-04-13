/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.servicios;

import com.ebs.modelos.NotaCreditoModelo;
import com.powersystem.util.ServicioBase;
import com.ebs.entidades.AnulacionFactura;
import com.ebs.entidades.Estados;
import com.ebs.entidades.FacturaAnulacionHistoricoHacienda;
import com.ebs.exception.ExcepcionManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
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
