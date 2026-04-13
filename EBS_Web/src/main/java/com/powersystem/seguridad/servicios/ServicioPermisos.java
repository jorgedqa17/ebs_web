/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.seguridad.servicios;

import com.ebs.entidades.Bodega;
import com.ebs.entidades.BodegaPK;
import com.ebs.entidades.Usuario;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.seguridad.modelos.RolesUsuario;
import com.powersystem.util.ServicioBase;
import com.powersystem.utilitario.JSFUtil;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioPermisos extends ServicioBase {

    private static final long serialVersionUID = 1L;

    public void obtenerRolesUsuario() {
        Usuario usuarioLogueado = (Usuario) JSFUtil.obtenerDeSesion("Usuario");
        try {
            String sql = " select distinct t6.id_rol,  t6.id_pantalla "
                    + "from searmedica.usuario t1   "
                    + "inner join searmedica.usuario_perfiles t2  "
                    + "	on t1.id_usuario = t2.id_usuario  "
                    + "inner join searmedica.perfiles t3  "
                    + "	on t2.id_perfil = t3.id_perfil  "
                    + "inner join searmedica.perfiles_roles t4  "
                    + "	on t3.id_perfil = t4.id_perfil  "
                    + "inner join searmedica.roles t5  "
                    + "	on t4.id_rol = t5.id_rol  "
                    + "inner join searmedica.pantallas_roles t6  "
                    + "	on t6.id_pantalla = t6.id_pantalla  "
                    + "inner join searmedica.pantalla t7  "
                    + "	on t6.id_pantalla = t7.id_pantalla  "
                    + "where t1.login = :login  "
                    + "ORDER BY t6.id_pantalla, t6.id_rol asc  ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("login", usuarioLogueado.getLogin())
                    .getResultList();

            List<RolesUsuario> listaRoles = new ArrayList<>();
            RolesUsuario rolesUsuario = new RolesUsuario();
            for (Object[] objeto : lista) {
                rolesUsuario.setIdPantalla(Long.parseLong((objeto[1].toString())));
                rolesUsuario.setIdRol(Long.parseLong(objeto[0].toString()));
                listaRoles.add(rolesUsuario);
                rolesUsuario = new RolesUsuario();
            }

            JSFUtil.guardarEnSesion("rolesUsuario", listaRoles);
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /*

     */
    public List<Usuario> obtenerUsuariosAgentes() {
        List<Usuario> listaUsuarios = null;
        try {

            String hql = " SELECT T3.* FROM SEARMEDICA.USUARIO T3 INNER JOIN SEARMEDICA.USUARIO_PERFILES T4 "
                    + " ON T3.ID_USUARIO = T4.ID_USUARIO "
                    + " WHERE  T4.ID_PERFIL = 4  ";

            listaUsuarios = em.createNativeQuery(hql, Usuario.class)
                    .getResultList();

        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.usuarios.agentes",
                    "mensaje.obtener.usuarios.agentes");
        }
        return listaUsuarios;
    }

    /**
     * Métdo que obtiene los roles del
     */
    public void obtenerBodegasUsuario() {
        Usuario usuarioLogueado = (Usuario) JSFUtil.obtenerDeSesion("Usuario");
        try {
            String sql
                    = " SELECT t2.id_bodega, t2.id_organizacion,t2.descripcion,t2.ubicacion "
                    + " FROM searmedica.usuario_bodega t1, searmedica.bodega t2 "
                    + " WHERE t1.id_bodega = t2.id_bodega "
                    + " AND t1.id_usuario = :id_usuario";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("id_usuario", usuarioLogueado.getId_usuario())
                    .getResultList();

            List<Bodega> listaBodegas = new ArrayList<>();
            Bodega bodega = null;
            BodegaPK bodegaPK = null;

            for (Object[] objeto : lista) {
                bodega = new Bodega();
                bodegaPK = new BodegaPK(Long.parseLong(objeto[0].toString()), Long.parseLong(objeto[1].toString()));

                bodega.setBodegaPK(bodegaPK);
                bodega.setDescripcion(objeto[2].toString());
                bodega.setUbicacion(objeto[3].toString());
                listaBodegas.add(bodega);
            }

            JSFUtil.guardarEnSesion("bodegasUsuario", listaBodegas);
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            throw ex;
        }
    }
    

    /*
    ;*/
}
