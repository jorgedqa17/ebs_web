/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.seguridad.servicios;

import com.ebs.exception.ExcepcionManager;
import com.ebs.entidades.Pantalla;
import com.powersystem.util.ServicioBase;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioMenu extends ServicioBase {

    private static final long serialVersionUID = 1L;

    /**
     * Método que obtiene los nodos del menú según el usuario que se desea
     * loguead
     *
     * @param login - Strin login
     * @return List<Pantalla> lista de pantallas
     * @throws Exception
     */
    public List<Pantalla> obtenerNodosMenu(String login) throws Exception {
        List<Pantalla> lista = null;
        try {
            String sql = "SELECT distinct t6.id_padre_pantalla, t6.id_pantalla, t6.descripcion,  t6.url, t6.icono, t6.activo  FROM SEARMEDICA.USUARIO T1 INNER JOIN  SEARMEDICA.USUARIO_PERFILES T2 "
                    + " ON T1.ID_USUARIO = T2.ID_USUARIO INNER JOIN SEARMEDICA.PERFILES T3 "
                    + " ON T2.ID_PERFIL = T3.ID_PERFIL INNER JOIN SEARMEDICA.PERFILES_ROLES T4 "
                    + " ON T3.ID_PERFIL = T4.ID_PERFIL INNER JOIN SEARMEDICA.PANTALLAS_ROLES T5 "
                    + " ON T4.ID_ROL = T5.ID_ROL INNER JOIN SEARMEDICA.PANTALLA T6 "
                    + " ON T5.ID_PANTALLA = T6.ID_PANTALLA "
                    + " WHERE T1.LOGIN = :login   and t6.activo = 1 ";

            lista = em.createNativeQuery(sql, Pantalla.class)
                    .setParameter("login", login).getResultList();

        } catch (NoResultException nex) {
            nex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, nex,
                    "mensaje.error.metodo.obtener.nodo.usuario",
                    "mensaje.error.metodo.obtener.nodo.desarrollador");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.nodo.usuario",
                    "mensaje.error.metodo.obtener.nodo.desarrollador");
        }
        return lista;

    }

}
