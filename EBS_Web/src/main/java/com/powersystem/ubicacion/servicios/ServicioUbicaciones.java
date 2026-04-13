/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.ubicacion.servicios;

import com.ebs.entidades.Barrio;
import com.ebs.entidades.Canton;
import com.ebs.entidades.Distrito;
import com.ebs.exception.ExcepcionManager;
import com.ebs.entidades.Provincia;
import com.powersystem.util.ServicioBase;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Jorge GBSYS
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioUbicaciones extends ServicioBase {

    private static final long serialVersionUID = 1L;

    /**
     * Método que obtiene una lsta completo de las provincias
     *
     * @return List<Provincia>
     */
    public List<Provincia> obtenerProvincias() {
        List<Provincia> listaProvincias = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM Provincia t1  ORDER BY t1.id_provincia asc";
            listaProvincias = em.createQuery(hql, Provincia.class).getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.ubicaciones.usuario.final",
                    "mensaje.error.ubicaciones.desarrollador");
        }
        return listaProvincias;
    }

    /**
     * Método que obtiene el lista de cantones de una provincia específica
     *
     * @param idProvincia - Long idProvincia
     * @return List<Canton>
     */
    public List<Canton> obtenerCantones(Long idProvincia) {
        List<Canton> listaCantones = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM Canton t1 WHERE t1.id_provincia = :id_provincia ORDER BY t1.id_canton asc";
            listaCantones = em.createQuery(hql, Canton.class)
                    .setParameter("id_provincia", idProvincia)
                    .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.ubicaciones.usuario.final",
                    "mensaje.error.ubicaciones.desarrollador");
        }
        return listaCantones;
    }

    /**
     * Método que obtiene el lista de distritos de un canton específico
     *
     * @param idCanton - Long idCanton
     * @return List<Distrito>
     */
    public List<Distrito> obtenerDistritos(Long idCanton) {
        List<Distrito> listaDistritos = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM Distrito t1 WHERE t1.id_canton = :id_canton ";
            listaDistritos = em.createQuery(hql, Distrito.class)
                    .setParameter("id_canton", idCanton)
                    .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.ubicaciones.usuario.final",
                    "mensaje.error.ubicaciones.desarrollador");
        }
        return listaDistritos;
    }

    /**
     * Método que obtiene el lista de barrios de un distrito específico
     *
     * @param idBarrio - Long idBarrio
     * @return List<Barrio>
     */
    public List<Barrio> obtenerBarrios(Long idBarrio) {
        List<Barrio> listaBarrios = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM Barrio t1 WHERE t1.id_distrito = :id_distrito ";
            listaBarrios = em.createQuery(hql, Barrio.class)
                    .setParameter("id_distrito", idBarrio)
                    .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.ubicaciones.usuario.final",
                    "mensaje.error.ubicaciones.desarrollador");
        }
        return listaBarrios;
    }
}
