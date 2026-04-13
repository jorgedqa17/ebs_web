/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.usuario.servicio;

import com.ebs.entidades.Usuario;
import com.ebs.exception.ExcepcionManager;
import com.ebs.entidades.TipoComision;
import com.ebs.entidades.UsuarioBodega;
import com.powersystem.util.ServicioBase;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import lombok.extern.slf4j.Slf4j;
//package com.powersystem.usuario.servicio;

/**
 *
 * @author Temp_User
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)

public class ServicioUsuario extends ServicioBase {

    private static final long serialVersionUID = 1L;

//    public Usuario obtenerUsuarioPorId(long idUsuario) {
//        Usuario usuario = new Usuario();
//        try {
//            String sql = "SELECT * from Usuario";
//            usuario = em.createQuery(sql, Usuario.class)
//                    .getSingleResult();
//        } catch (NoResultException nex){
//            nex.printStackTrace();
//        } catch (Exception ex){
//            ex.printStackTrace();
//            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex, 
//                    "mensaje.error.obtener.usuario.final",
//                    "mensaje.error.obtener.usuario.desarrollador");
//        }
//        return usuario;
//    }
    public List<Usuario> obtenerListaUsuarios() {
        List<Usuario> listaResultado = null;
        try {
            String sql = "SELECT usu from Usuario usu ";

            listaResultado = em.createQuery(sql, Usuario.class)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.usuario.listado",
                    "mensaje.usuario.listado");
        }
        return listaResultado;
    }

    public List<TipoComision> obtenerListaTipoComision() {
        List<TipoComision> listaResultado = null;
        try {
            String sql = "SELECT usu from TipoComision usu ";

            listaResultado = em.createQuery(sql, TipoComision.class)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.tipo.comision.listado",
                    "mensaje.tipo.comision.listado");
        }
        return listaResultado;
    }

    public List<UsuarioBodega> obtenerListaBodegasUsuario(Usuario usuario) {
        List<UsuarioBodega> listaResultado = null;
        try {
            String sql = "SELECT usu from UsuarioBodega usu WHERE usu.id_usuario = :id_usuario";

            listaResultado = em.createQuery(sql, UsuarioBodega.class).setParameter("id_usuario", usuario.getId_usuario())
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.tipo.comision.listado",
                    "mensaje.tipo.comision.listado");
        }
        return listaResultado;
    }
}
