/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.seguridad.servicios;

import com.ebs.exception.ExcepcionManager;
import com.ebs.entidades.Usuario;
import com.powersystem.util.ServicioBase;
import com.powersystem.utilitario.Encriptador;
import com.powersystem.utilitario.JSFUtil;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioLogin extends ServicioBase {

    private static final long serialVersionUID = 1L;

    public boolean validarExistenciaUsuario(String usuario) {
        boolean resultado = false;
        try {
            String select = " SELECT 1 FROM SEARMEDICA.USUARIO WHERE upper(login) =  '" + usuario.toUpperCase().trim() + "'";
            Object objeto = (Object) em.createNativeQuery(select).getSingleResult();
            if (objeto != null) {
                resultado = true;
            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.cambiar.contrasenna.valuidacion.usaurio",
                    "mensaje.cambiar.contrasenna.valuidacion.usaurio");
        }
        return resultado;
    }

    public void cambiarContrasennaUsuario(String usuario, String nuevaContrsenna) {
        try {
            String hql = "SELECT usu FROM Usuario usu WHERE UPPER(usu.login) = :login AND usu.activo=1";
            //Obtengo la información de usuario
            Usuario usuarioEncontrado = em.createQuery(hql, Usuario.class)
                    .setParameter("login", usuario.toUpperCase())
                    .getSingleResult();

            String passwordEncriptado = Encriptador.encriptarCadena(nuevaContrsenna);
            usuarioEncontrado.setPassword(passwordEncriptado);
            actualizar(usuarioEncontrado);
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.cambiar.contrasenna.error",
                    "mensaje.cambiar.contrasenna.error");
        }
    }

    public boolean validarContrasennasCoinciden(String usuario, String password) {
        boolean resultado = false;
        try {
            String hql = "SELECT usu FROM Usuario usu WHERE UPPER(usu.login) = :login AND usu.activo=1";
            //Obtengo la información de usuario
            Usuario usuarioEncontrado = em.createQuery(hql, Usuario.class)
                    .setParameter("login", usuario.toUpperCase())
                    .getSingleResult();
            //Encripto el password
            String passwordEncriptado = Encriptador.encriptarCadena(password);
            //Verifico si trajo resultado
            if (usuarioEncontrado != null) {
                //Verifico el password
                if (usuarioEncontrado.getPassword().trim().equals(passwordEncriptado)) {
                    resultado = true;
                }
            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.cambiar.contrasenna.validacion.contrasenna",
                    "mensaje.cambiar.contrasenna.validacion.contrasenna");
        }
        return resultado;
    }

    /**
     * Método que retorna un objeto de la persona receptor
     *
     * @return PersonaHacienda
     */
    public Long obtenerUsuarioBodega(Long idUsuario) {
        Long idBodega = 0l;
        String sql = " SELECT ID_USUARIO, ID_BODEGA FROM SEARMEDICA.USUARIO_BODEGA WHERE ID_USUARIO =  " + idUsuario.toString();
        try {
            Object[] objeto = (Object[]) em.createNativeQuery(sql).getSingleResult();
            if (objeto != null) {
                idBodega = Long.parseLong(objeto[1].toString());
            }
            JSFUtil.guardarEnSesion("idBodega", idBodega);

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.usuario.usuario.usuario",
                    "mensaje.error.obtener.usuario.usuario.desarrollador");
        }
        return idBodega;
    }

    public void obtenerParametros() {
    }

    public void obtenerUsuarioAdministrador(Usuario usuario) {
        boolean resultado = false;

        try {
            StringBuilder hilera = new StringBuilder();
            hilera.append(" select count(usu.id_usuario) from searmedica.usuario usu, searmedica.perfiles per, searmedica.usuario_perfiles inter ");
            hilera.append(" where usu.id_usuario = ").append(usuario.getId_usuario());
            hilera.append(" and usu.id_usuario = inter.id_usuario and inter.id_perfil = per.id_perfil ");
            hilera.append(" and per.id_perfil = 2");

            Object objeto = (Object) em.createNativeQuery(hilera.toString()).getSingleResult();
            if (objeto != null) {
                int valor = Integer.parseInt(objeto.toString());
                resultado = valor > 0;
            }
        } catch (Exception ex) {
            resultado = false;
        }

        JSFUtil.guardarEnSesion("administrador", resultado);
    }

    public List<Usuario> obtenerUsuariosSistema() {
        List<Usuario> resultado = null;
        try {
            String hql = "SELECT usu FROM Usuario usu WHERE usu.activo=1";

            resultado = em.createQuery(hql, Usuario.class).getResultList();

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.usuario.usuario.usuario",
                    "mensaje.error.obtener.usuario.usuario.desarrollador");
        }
        return resultado;
    }

    /**
     * Método que verifica los datos de inicio de sesión
     *
     * @param usuario - Clase Usuario con la información a consultar
     * @return Boolean, true=Pasa la verificacion, False=No pasa la verificacion
     * @throws java.lang.Exception
     */
    public Usuario iniciarSesion(String usuario, String password) throws Exception {
        boolean pasaVerificacion = false;
        Usuario resultado = null;
        try {

            String hql = "SELECT usu FROM Usuario usu WHERE UPPER(usu.login) = :login AND usu.password =:password AND usu.activo=1";
            //Encripto el password
            String passwordEncriptado = Encriptador.encriptarCadena(password);
            //Verifico si trajo resultado
            //Obtengo la información de usuario
            resultado = em.createQuery(hql, Usuario.class)
                    .setParameter("login", usuario.toUpperCase())
                    .setParameter("password", passwordEncriptado)
                    .getSingleResult();
            //Verifico el1] password
            if (resultado != null && resultado.getPassword().trim().equals(passwordEncriptado)) {

                pasaVerificacion = true;
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.login.usuario.final",
                    "mensaje.error.metodo.login.desarrollador");
        }
        return pasaVerificacion ? resultado : null;
    }

}
