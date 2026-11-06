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
import com.ebs.modelos.ModeloUsuarioLista;
import com.powersystem.util.ServicioBase;
import com.powersystem.utilitario.Encriptador;
import com.powersystem.utilitario.JSFUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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

    // ---------------------------------------------------------------------------------------------
    // Mantenimiento de usuarios (pantalla usuario/usuarios.xhtml). Solo el perfil Administrador (2).
    // ---------------------------------------------------------------------------------------------
    public static final long PERFIL_ADMINISTRADOR = 2L;
    public static final int LARGO_MINIMO_CLAVE = 8;
    private static final Pattern LOGIN_VALIDO = Pattern.compile("^[A-Za-z0-9._\\-]{3,50}$");

    /** El usuario de la sesión tiene el perfil Administrador. Se vuelve a revisar en cada operación. */
    public boolean sesionEsAdministrador() {
        Usuario actual = (Usuario) JSFUtil.obtenerDeSesion("Usuario");
        if (actual == null || actual.getLogin() == null) {
            return false;
        }
        List<?> r = em.createNativeQuery(" select 1 from searmedica.usuario u inner join searmedica.usuario_perfiles up "
                + " on u.id_usuario = up.id_usuario where u.login = :login and up.id_perfil = :perfil ")
                .setParameter("login", actual.getLogin()).setParameter("perfil", PERFIL_ADMINISTRADOR).getResultList();
        return !r.isEmpty();
    }

    public List<ModeloUsuarioLista> listarUsuariosMantenimiento() {
        if (!sesionEsAdministrador()) {
            return new ArrayList<>();
        }
        String sql = " select u.id_usuario, u.login, u.nombre, u.activo, u.es_usuario_bodega, u.numero_caja, "
                + " (select string_agg(distinct p.descripcion, ', ') from searmedica.usuario_perfiles up "
                + "    inner join searmedica.perfiles p on p.id_perfil = up.id_perfil where up.id_usuario = u.id_usuario) as perfiles, "
                + " (select string_agg(distinct b.descripcion, ', ') from searmedica.usuario_bodega ub "
                + "    inner join searmedica.bodega b on b.id_bodega = ub.id_bodega where ub.id_usuario = u.id_usuario) as bodegas "
                + " from searmedica.usuario u order by upper(u.login) ";
        List<ModeloUsuarioLista> lista = new ArrayList<>();
        for (Object[] f : (List<Object[]>) em.createNativeQuery(sql).getResultList()) {
            lista.add(new ModeloUsuarioLista(Long.valueOf(f[0].toString()),
                    f[1] == null ? "" : f[1].toString(),
                    f[2] == null ? "" : f[2].toString(),
                    f[3] != null && "1".equals(f[3].toString()),
                    f[4] != null && "1".equals(f[4].toString()),
                    f[5] == null ? "" : f[5].toString(),
                    f[6] == null ? "" : f[6].toString(),
                    f[7] == null ? "" : f[7].toString()));
        }
        return lista;
    }

    /** id_perfil → descripción, para elegir los perfiles del usuario. */
    public Map<Long, String> obtenerPerfilesDisponibles() {
        Map<Long, String> mapa = new LinkedHashMap<>();
        for (Object[] f : (List<Object[]>) em.createNativeQuery(" select id_perfil, descripcion from searmedica.perfiles order by id_perfil ").getResultList()) {
            mapa.put(Long.valueOf(f[0].toString()), String.valueOf(f[1]));
        }
        return mapa;
    }

    /** id_bodega → descripción, para elegir las bodegas del usuario. */
    public Map<Long, String> obtenerBodegasDisponibles() {
        Map<Long, String> mapa = new LinkedHashMap<>();
        for (Object[] f : (List<Object[]>) em.createNativeQuery(" select distinct on (id_bodega) id_bodega, descripcion from searmedica.bodega order by id_bodega ").getResultList()) {
            mapa.put(Long.valueOf(f[0].toString()), String.valueOf(f[1]));
        }
        return mapa;
    }

    public List<Long> obtenerIdsPerfilesUsuario(Long idUsuario) {
        List<Long> ids = new ArrayList<>();
        for (Object f : em.createNativeQuery(" select id_perfil from searmedica.usuario_perfiles where id_usuario = :id ")
                .setParameter("id", idUsuario).getResultList()) {
            ids.add(Long.valueOf(f.toString()));
        }
        return ids;
    }

    public List<Long> obtenerIdsBodegasUsuario(Long idUsuario) {
        List<Long> ids = new ArrayList<>();
        for (Object f : em.createNativeQuery(" select id_bodega from searmedica.usuario_bodega where id_usuario = :id ")
                .setParameter("id", idUsuario).getResultList()) {
            ids.add(Long.valueOf(f.toString()));
        }
        return ids;
    }

    /**
     * Crea o modifica un usuario con sus perfiles y bodegas, todo en una transacción. La contraseña se
     * guarda con {@link Encriptador#claveParaGuardar}; en una modificación, clave vacía = no cambiarla.
     *
     * @param idUsuario null para crear
     * @return mensaje de validación (nada se guardó), o null si todo salió bien
     */
    public String guardarUsuarioMantenimiento(Long idUsuario, String login, String nombre, boolean activo,
            boolean esUsuarioBodega, String numeroCaja, String clave, List<Long> perfiles, List<Long> bodegas) {
        try {
            if (!sesionEsAdministrador()) {
                return "Solo el perfil Administrador puede mantener usuarios.";
            }
            boolean nuevo = idUsuario == null;
            String loginLimpio = login == null ? "" : login.trim();
            if (!LOGIN_VALIDO.matcher(loginLimpio).matches()) {
                return "El usuario debe tener entre 3 y 50 caracteres: letras, números, punto, guion o guion bajo.";
            }
            if (perfiles == null || perfiles.isEmpty()) {
                return "Seleccione al menos un perfil.";
            }
            boolean hayClave = clave != null && !clave.isEmpty();
            if ((nuevo || hayClave) && (clave == null || clave.length() < LARGO_MINIMO_CLAVE)) {
                return "La contraseña debe tener al menos " + LARGO_MINIMO_CLAVE + " caracteres.";
            }
            Usuario sesion = (Usuario) JSFUtil.obtenerDeSesion("Usuario");

            // Serializa la creación/edición de usuarios (tabla pequeña) para que dos altas simultáneas no tomen el mismo id.
            em.createNativeQuery(" lock table searmedica.usuario in share row exclusive mode ").executeUpdate();

            Number repetidos = (Number) em.createNativeQuery(" select count(*) from searmedica.usuario where upper(login) = upper(:login) and id_usuario <> :id ")
                    .setParameter("login", loginLimpio).setParameter("id", nuevo ? -1L : idUsuario).getSingleResult();
            if (repetidos.longValue() > 0) {
                return "Ya existe un usuario con ese nombre de acceso.";
            }

            Usuario usuario;
            if (nuevo) {
                Number siguiente = (Number) em.createNativeQuery(" select coalesce(max(id_usuario), 0) + 1 from searmedica.usuario ").getSingleResult();
                usuario = new Usuario();
                usuario.setId_usuario(siguiente.longValue());
                usuario.setLogin(loginLimpio);
            } else {
                usuario = em.find(Usuario.class, idUsuario);
                if (usuario == null) {
                    return "El usuario ya no existe.";
                }
                // Evita que el administrador se deje a sí mismo sin acceso.
                if (sesion != null && usuario.getLogin() != null && usuario.getLogin().equalsIgnoreCase(sesion.getLogin())) {
                    if (!activo) {
                        return "No puede desactivar su propio usuario.";
                    }
                    if (!perfiles.contains(PERFIL_ADMINISTRADOR)) {
                        return "No puede quitarse a sí mismo el perfil Administrador.";
                    }
                }
            }
            usuario.setNombre(nombre == null || nombre.trim().isEmpty() ? null : nombre.trim());
            usuario.setActivo(activo ? 1 : 0);
            usuario.setEs_usuario_bodega(esUsuarioBodega ? 1L : 0L);
            usuario.setNumero_caja(numeroCaja == null || numeroCaja.trim().isEmpty() ? null : numeroCaja.trim());
            if (nuevo || hayClave) {
                usuario.setPassword(Encriptador.claveParaGuardar(clave));
            }
            if (nuevo) {
                em.persist(usuario);
            } else {
                em.merge(usuario);
            }
            em.flush();

            Long id = usuario.getId_usuario();
            em.createNativeQuery(" delete from searmedica.usuario_perfiles where id_usuario = :id ").setParameter("id", id).executeUpdate();
            for (Long perfil : new LinkedHashSet<>(perfiles)) {
                em.createNativeQuery(" insert into searmedica.usuario_perfiles (id_usuario, id_perfil) values (:u, :p) ")
                        .setParameter("u", id).setParameter("p", perfil).executeUpdate();
            }
            em.createNativeQuery(" delete from searmedica.usuario_bodega where id_usuario = :id ").setParameter("id", id).executeUpdate();
            if (bodegas != null) {
                for (Long bodega : new LinkedHashSet<>(bodegas)) {
                    em.createNativeQuery(" insert into searmedica.usuario_bodega (id_usuario, id_bodega) values (:u, :b) ")
                            .setParameter("u", id).setParameter("b", bodega).executeUpdate();
                }
            }
            log.info("Usuario '{}' {} por '{}'", loginLimpio, nuevo ? "creado" : ("modificado" + (hayClave ? " (clave cambiada)" : "")),
                    sesion == null ? "?" : sesion.getLogin());
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.usuario.listado",
                    "mensaje.usuario.listado");
        }
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
