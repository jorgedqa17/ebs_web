/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.recibos.servicios;

import com.ebs.constantes.enums.CancelacionFactura;
import com.ebs.constantes.enums.Clase;
import com.ebs.constantes.enums.EstadoRecibo;
import com.ebs.entidades.Estados;
import com.ebs.entidades.Factura;
import com.ebs.entidades.HistoricoRecibo;
import com.ebs.entidades.Recibo;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.powersystem.util.ServicioBase;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Jorge Quesada Arias
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioRecibo extends ServicioBase {

    @Inject
    private ServicioFactura servicioFactura;

    public boolean cancelarFactura(String login, Factura factura) {
        try {
            //si el monto restante es 0 o menor que 0
            if (factura.getMonto_restante().compareTo(BigDecimal.ZERO) <= 0) {
                factura.setFecha_cancelacion(fechaHoraBD());
                factura.setUsuario_cancela(login);
                factura.setFactura_cancelada(1);
            }
            actualizar(factura);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.recibo.error.guardar",
                    "mensaje.recibo.error.guardar");
        }
        return true;
    }

    public Recibo guardarRecibo(Recibo recibo, Factura factura) {
        try {

            guardar(recibo);
//            // si el monto restante es 0 o menor que 0
            if (factura.getMonto_restante().compareTo(BigDecimal.ZERO) <= 0) {
                factura.setFecha_cancelacion(fechaHoraBD());
                factura.setUsuario_cancela(recibo.getLogin());
                factura.setFactura_cancelada(1);
            }
            actualizar(factura);

            HistoricoRecibo historicoRecibo = new HistoricoRecibo();
            historicoRecibo.setId_recibo(recibo.getId_recibo());
            historicoRecibo.setFecha(fechaHoraBD());
            historicoRecibo.setLogin(recibo.getLogin());
            historicoRecibo.setDescripcion("Se ha guardado un nuevo recibo");
            guardar(historicoRecibo);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.recibo.error.guardar",
                    "mensaje.recibo.error.guardar");
        }
        return recibo;
    }

    public Factura anularRecibo(Recibo recibo) {
        Factura factura = null;
        try {
            factura = servicioFactura.obtenerFacturaBusqueda(recibo.getId_factura());

            recibo.setId_estado(EstadoRecibo.ANULADO.getEstadoRecibo());
            factura.setMonto_restante(factura.getMonto_restante().add(recibo.getMonto_pago()));
            factura.setFactura_cancelada(CancelacionFactura.PENDIENTE_PAGO.getFacturaCancelada());
            factura.setFecha_cancelacion(null);
            factura.setUsuario_cancela(null);

            actualizar(recibo);
            actualizar(factura);

            HistoricoRecibo historicoRecibo = new HistoricoRecibo();
            historicoRecibo.setId_recibo(recibo.getId_recibo());
            historicoRecibo.setFecha(fechaHoraBD());
            historicoRecibo.setLogin(recibo.getLogin());
            historicoRecibo.setDescripcion("Se ha anulado el recibo");
            guardar(historicoRecibo);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.recibo.anulacion.recibo.error",
                    "mensaje.recibo.anulacion.recibo.error");
        }
        return factura;
    }

    public List<Recibo> obtenerRecibosPorFactura(Long idFactura) {
        List<Recibo> resultado = null;
        try {
            resultado = resultado = em.createQuery("SELECT re FROM Recibo re WHERE re.id_factura = :idFactura", Recibo.class)
                    .setParameter("idFactura", idFactura).getResultList();

            List<Estados> estados = em.createQuery("SELECT etc FROM Estados etc WHERE etc.idClase = :idClase", Estados.class)
                    .setParameter("idClase", Clase.RECIBO.getIdClase()).getResultList();

            resultado.forEach(elemento -> {
                estados.forEach(elementoEstado -> {
                    if (elementoEstado.getIdEstado().equals(elemento.getId_estado())) {
                        elemento.setEstado(elementoEstado.getDescripcion());
                    }
                });
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.recibo.obtener.reciobos.por.factura",
                    "mensaje.recibo.obtener.reciobos.por.factura");
        }
        return resultado;
    }

}
