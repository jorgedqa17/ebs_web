/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.parametros.servicios;

import com.ebs.entidades.Parametro;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.util.ServicioBase;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jdquesad
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioParametro extends ServicioBase {

    /**
     * Método que obtiene un valor de un parámetro dado un id de parmáetro
     *
     * @param idParametro - Long
     * @return String
     */
    public Parametro obtenerValorParametro(Long idParametro) {
        Parametro resultado = null;
        try {
            String sql = "SELECT param FROM Parametro param WHERE param.id_parametro = :idParametro";
            resultado = (Parametro) em.createQuery(sql, Parametro.class).setParameter("idParametro", idParametro).getSingleResult();

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.parmetro",
                    "mensaje.error.obtener.parmetro.desarrollador");
        }
        return resultado;
    }
}
