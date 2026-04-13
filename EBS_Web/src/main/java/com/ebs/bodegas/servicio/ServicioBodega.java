/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.bodegas.servicio;


import com.ebs.entidades.Bodega;
import com.ebs.entidades.Producto;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.util.ServicioBase;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Jorge GBSYS
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioBodega extends ServicioBase {

    /**
     * Método que obtiene un listado de bodegas
     *
     * @return List<Bodega>
     */
    public List<Bodega> obtenerListaBodegas() {
        List<Bodega> listaCondicionesVenta = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM Bodega t1 ";
            listaCondicionesVenta = em.createQuery(hql, Bodega.class)
                    .getResultList();
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.listar.bodegas.usuario",
                    "mensaje.error.listar.bodegas.desarrollador");
        }
        return listaCondicionesVenta;
    }

    public Bodega obtenerBodega(long idBodega) {
        Query consulta;
        Bodega resultado;
        try {
            consulta = em.createNativeQuery("select b.* from searmedica.bodega b where b.id_bodega = ?1", Bodega.class);
            consulta.setParameter(1, idBodega);
            Object preliminar = consulta.getSingleResult();
            if (preliminar == null) {
                resultado = null;
            } else {
                resultado = (Bodega) consulta.getSingleResult();
            }
        } catch (Exception e) {
            resultado = null;
        }

        return resultado;
    }
    
     public List<Bodega> buscarBodega(String texto) {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select b.* from searmedica.bodega b where upper(b.descripcion) like upper('%");
        hilera.append(texto);
        hilera.append("%') order by b.descripcion");
        Query consulta = em.createNativeQuery(hilera.toString(),Bodega.class);
        List<Bodega> lista = consulta.getResultList();
        return lista;
    }
}
