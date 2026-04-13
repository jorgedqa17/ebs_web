/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.personas.servicios;

import com.ebs.constantes.enums.Indicadores;
import com.ebs.entidades.ActividadEconomica;
import com.ebs.entidades.Cliente;
import com.ebs.exception.ExcepcionManager;
import com.ebs.entidades.Persona;
import com.ebs.entidades.PersonaCorreo;
import com.ebs.entidades.PersonaPK;
import com.ebs.entidades.TipoCliente;
import com.ebs.entidades.TipoIdentificacion;
import com.ebs.modelos.Emisor;
import com.ebs.modelos.Identificacion;
import com.ebs.modelos.ModeloPersonaTSE;
import com.ebs.modelos.PersonaModelo;
import com.ebs.modelos.Receptor;
import com.ebs.modelos.Telefono;
import com.ebs.modelos.Ubicacion;
import com.powersystem.util.ServicioBase;
import com.powersystem.utilitario.JSFUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioPersona extends ServicioBase {

    private static final long serialVersionUID = 1L;

    /**
     * Método que trae una lista de personas por un nombre específico
     *
     * @param nombre
     * @return List<Persona>
     */
    public List<ActividadEconomica> obtenerListaCodigosActividad() {
        List<ActividadEconomica> listaPersonas = new ArrayList<>();
        try {

            String hql = " SELECT t1 FROM ActividadEconomica t1  ";
            //Obtengo la información de usuario
            listaPersonas = em.createQuery(hql, ActividadEconomica.class)
                    .getResultList();

        } catch (NoResultException nex) {
            //nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.usuario.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return listaPersonas;
    }

    /**
     * Método que trae una lista de personas por un nombre específico
     *
     * @param nombre
     * @return List<Persona>
     */
    public List<Persona> obtenerPersonas(String nombre) {
        List<Persona> listaPersonas = new ArrayList<>();
        try {

            String hql = " SELECT t1 FROM Persona t1 WHERE lower(t1.personaPK.nombre) LIKE :nombre ";
            //Obtengo la información de usuario
            listaPersonas = em.createQuery(hql, Persona.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();

        } catch (NoResultException nex) {
            //nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.usuario.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return listaPersonas;
    }

    public void modificarEstadoCliente(Persona persona) {
        try {
            actualizar(persona);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.persona.cambiar.estado.error",
                    "mensaje.persona.cambiar.estado.error");
        }

    }

    /**
     * Método que obtiene la lista completa de tipos de identificacion
     *
     * @return List<TipoIdentificacion>
     */
    public List<TipoIdentificacion> obtenerListaTiposIdentificacion() {
        List<TipoIdentificacion> listaTiposCedulas = new ArrayList<>();
        try {

            String hql = " SELECT t1 FROM TipoIdentificacion t1  ";
            //Obtengo la información de usuario
            listaTiposCedulas = em.createQuery(hql, TipoIdentificacion.class)
                    .getResultList();

        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.lista.tipos.identifcacion.usuario",
                    "mensaje.error.metodo.obtener.lista.tipos.identifcacion.desarrollador");
        }
        return listaTiposCedulas;
    }

    /**
     * Método que obtiene la lista completa de tipos de identificacion
     *
     * @return List<TipoIdentificacion>
     */
    public List<SelectItem> obtenerListaItemsTiposIdentificacion() {
        List<SelectItem> listaTiposCedulasResultado = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM TipoIdentificacion t1  ";
            //Obtengo la información de usuario
            List<TipoIdentificacion> listaTiposCedulas = em.createQuery(hql, TipoIdentificacion.class)
                    .getResultList();
            for (TipoIdentificacion listaTiposCedula : listaTiposCedulas) {
                listaTiposCedulasResultado.add(new SelectItem(listaTiposCedula.getId_tipo_cedula(), listaTiposCedula.getDescripcion()));
            }
        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.lista.tipos.identifcacion.usuario",
                    "mensaje.error.metodo.obtener.lista.tipos.identifcacion.desarrollador");
        }
        return listaTiposCedulasResultado;
    }

    /**
     * Método que obtiene una persona por el número de identificación y el tipo
     * de identificación
     *
     * @param identificacion - String identificación
     * @param tipoIdentificacion - Long tipo de identificación
     * @return Persona
     */
    public Persona obtenerPersonaPorNumeroCedula(String identificacion) {
        Persona personaResultado = null;
        try {
            String hql = "SELECT pers FROM Persona pers WHERE pers.personaPK.numero_cedula = :numero_cedula";
            //Obtengo la información de Persona
            personaResultado = em.createQuery(hql, Persona.class)
                    .setParameter("numero_cedula", identificacion.trim())
                    .getSingleResult();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return personaResultado;
    }

    public BigDecimal obtenerMontoDeudaCliente(Long idCliente, Long idCondVenta) {
        BigDecimal resultado = new BigDecimal("0.0");
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" SELECT    ")
                    .append(" SUM(T1.MONTO_RESTANTE)  ")
                    .append(" FROM  ")
                    .append(" searmedica.factura t1  ")
                    .append(" WHERE  ")
                    .append(" t1.id_cliente = :idCliente   ")
                    .append(" AND t1.id_cond_venta = :idCondVenta  ")
                    .append(" AND t1.clave IS NOT NULL  ")
                    .append(" AND t1.factura_cancelada = 0  ")
                    .append(" AND T1.ID_ANULACION IS NULL  ")
                    .append(" AND t1.fecha_factura + CAST(t1.plazo_credito || 'days' AS interval) <= now()  ");

            Object objeto = (Object) em.createNativeQuery(consulta.toString())
                    .setParameter("idCliente", idCliente)
                    .setParameter("idCondVenta", idCondVenta)
                    .getSingleResult();

            if (objeto != null) {
                resultado = new BigDecimal(objeto.toString());
            }

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.total.deuda.cliente",
                    "mensaje.error.total.deuda.cliente");
        }
        return resultado;
    }

    /**
     * Método que obtiene una persona por el número de identificación y el tipo
     * de identificación
     *
     * @param identificacion - String identificación
     * @param tipoIdentificacion - Long tipo de identificación
     * @return Persona
     */
    public Persona obtenerPersonaPorIdentificacion(String identificacion, Long tipoIdentificacion) {
        Persona personaResultado = null;
        try {
            String hql = "SELECT pers FROM Persona pers WHERE pers.personaPK.numero_cedula = :numero_cedula AND pers.personaPK.id_tipo_cedula = :id_tipo_cedula";
            //Obtengo la información de Persona
            personaResultado = em.createQuery(hql, Persona.class)
                    .setParameter("numero_cedula", identificacion)
                    .setParameter("id_tipo_cedula", tipoIdentificacion)
                    .getSingleResult();

        } catch (NoResultException nex) {
            // nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return personaResultado;
    }

    /**
     * Método que obtiene una persona por el número de identificación y el tipo
     * de identificación
     *
     * @param identificacion - String identificación
     * @param tipoIdentificacion - Long tipo de identificación
     * @return Persona
     */
    public Persona obtenerPersonaHacienda(String identificacion, Long tipoIdentificacion) {
        Persona personaResultado = null;
        try {
            String hql = "SELECT pers FROM Persona pers WHERE pers.personaPK.numero_cedula = :numero_cedula AND pers.personaPK.id_tipo_cedula = :id_tipo_cedula AND pers.es_persona_hacienda=1";
            //Obtengo la información de Persona
            personaResultado = em.createQuery(hql, Persona.class)
                    .setParameter("numero_cedula", identificacion)
                    .setParameter("id_tipo_cedula", tipoIdentificacion)
                    .getSingleResult();

        } catch (NoResultException nex) {
            // nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return personaResultado;
    }

    /**
     * Método que obtiene la información del cliente
     *
     * @param numeroCedula - String
     * @return Cliente
     */
    public Cliente obtenerCliente(String numeroCedula) {
        Cliente cliente = new Cliente();
        try {
            String hql = " SELECT cli FROM Cliente cli WHERE cli.numero_cedula = :numero_cedula ";
            cliente = em.createQuery(hql, Cliente.class)
                    .setParameter("numero_cedula", numeroCedula)
                    .getSingleResult();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.cliente.final",
                    "mensaje.error.obtener.cliente.desarrollador");
        }
        return cliente;
    }

    /**
     * Método que obtiene la información del cliente
     *
     * @param numeroCedula - String
     * @return Cliente
     */
    public Cliente obtenerClientePorIdCliente(Long idCliente) {
        Cliente cliente = new Cliente();
        try {
            String hql = " SELECT cli FROM Cliente cli WHERE cli.id_cliente = :id_cliente ";
            cliente = em.createQuery(hql, Cliente.class)
                    .setParameter("id_cliente", idCliente)
                    .getSingleResult();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.cliente.final",
                    "mensaje.error.obtener.cliente.desarrollador");
        }
        return cliente;
    }

    /**
     * Método que retorna un objeto de la persona receptor
     *
     * @param idCliente Long
     * @return PersonaHacienda
     */
    public Receptor obtenerPersonaReceptor(Long idCliente) {
        Receptor personaEmisor = new Receptor();
        Ubicacion ubicacion = new Ubicacion();
        Identificacion identificacion = new Identificacion();
        Telefono telefono = null;
        String sql = "select  t5.numero_cedula,  "
                + " coalesce(t5.nombre,'') ||' '||coalesce(t5.primer_apellido,'')||' '||coalesce(t5.segundo_apellido,'') as nombre, "
                + "  t5.correo_electronico,  "
                + " t6.codigo_hacienda as tipoCedula, "
                + " (select tp.codigo_hacienda from searmedica.provincia tp where tp.id_provincia = t5.id_provincia) as id_provincia, "
                + " (select tc.codigo_hacienda from searmedica.canton tc where tc.id_canton = t5.id_canton)as id_canton, "
                + " (select td.codigo_hacienda from searmedica.distrito td where td.id_distrito = t5.id_distrito) as id_distrito, "
                + " (select tb.codigo_hacienda from searmedica.barrio tb where tb.id_barrio = t5.id_barrio) as id_barrio, "
                + "  t5.direccion,  "
                + "  t5.codigo_pais,  "
                + "  t5.telefono_1, "
                + "  t5.telefono_2 "
                + "  from searmedica.persona t5 inner  join searmedica.tipo_cedula t6 "
                + "  on t5.id_tipo_cedula = t6.id_tipo_cedula inner  join searmedica.cliente t4 "
                + "  on t4.numero_cedula  = t5.numero_cedula "
                + "  and t4.id_cliente = :id_cliente ";

        try {
            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).setParameter("id_cliente", idCliente).getResultList();
            for (Object[] objeto : lista) {
                identificacion.setNumeroCedula(objeto[0].toString().trim());
                identificacion.setTipo(objeto[3].toString().trim());
                personaEmisor.setIdentificacion(identificacion);

                if (objeto[1].toString().length() > 80) {
                    personaEmisor.setNombre(objeto[1].toString().substring(0, 79).trim());
                } else {
                    personaEmisor.setNombre(objeto[1].toString());
                }

                personaEmisor.setCorreo_electronico(objeto[2] == null ? null : objeto[2].toString().trim());

                if (objeto[4] != null) {
                    ubicacion.setId_provincia(objeto[4] == null ? null : objeto[4].toString().trim());
                    ubicacion.setId_canton(objeto[5] == null ? null : objeto[5].toString().trim());
                    ubicacion.setId_distrito(objeto[6] == null ? null : objeto[6].toString().trim());
                    ubicacion.setId_barrio(objeto[7] == null ? null : objeto[7].toString().trim());
                    ubicacion.setOtrasSennas(objeto[8] == null ? null : objeto[8].toString().trim());
                    personaEmisor.setUbicacion(ubicacion);
                }

                if (objeto[10] != null) {
                    telefono = new Telefono();
                    telefono.setCodigo_pais(objeto[9] == null ? null : objeto[9].toString().trim());
                    telefono.setTelefono(objeto[10] == null ? null : objeto[10].toString().trim());
                    personaEmisor.setTelefono(telefono);
                }

                if (objeto[11] != null) {
                    telefono = new Telefono();
                    telefono.setCodigo_pais(objeto[9] == null ? null : objeto[9].toString().trim());
                    telefono.setTelefono(objeto[11] == null ? null : objeto[11].toString().trim());
                    personaEmisor.setFax(telefono);
                }

            }
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return personaEmisor;
    }

    /**
     * Método que retorna un objeto de la persona emisor
     *
     * @return PersonaHacienda
     */
    public Emisor obtenerPersonaEmisor() {
        Emisor personaEmisor = new Emisor();
        Ubicacion ubicacion = new Ubicacion();
        Identificacion identificacion = new Identificacion();
        Telefono telefono = new Telefono();

        String sql = "SELECT id_empresa, nombre, barrio, provincia, canton, "
                + " distrito, identificacion, tipo_identificacion, direccion,  "
                + " correo_electronico, telefono, fax, codigo_area, nombre_comercial "
                + " FROM searmedica.informacion_empresa ";
        try {
            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();
            for (Object[] objeto : lista) {
                personaEmisor.setNombre(objeto[1].toString().trim());
                personaEmisor.setCorreo_electronico(objeto[9].toString().trim());

                ubicacion.setId_barrio(objeto[2] == null ? null : objeto[2].toString().trim());
                ubicacion.setId_provincia(objeto[3].toString().trim());
                ubicacion.setId_canton(objeto[4].toString().trim());
                ubicacion.setId_distrito(objeto[5].toString().trim());
                ubicacion.setOtrasSennas(objeto[8].toString().trim());
                personaEmisor.setUbicacion(ubicacion);

                identificacion.setNumeroCedula(objeto[6].toString().trim());
                identificacion.setTipo(objeto[7].toString().trim());
                personaEmisor.setIdentificacion(identificacion);

                telefono.setCodigo_pais(objeto[12].toString().trim());
                telefono.setTelefono(objeto[10].toString().trim());
                personaEmisor.setTelefono(telefono);
                telefono.setTelefono(objeto[11].toString().trim());
                personaEmisor.setFax(telefono);
                personaEmisor.setNombreComercial(objeto[13].toString().trim());
            }
            JSFUtil.guardarEnSesion("personaEmisor", personaEmisor);
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return personaEmisor;
    }

    /**
     * Método que retorna un objeto de la persona emisor
     *
     * @return PersonaHacienda
     */
    public Emisor obtenerPersonaEmisorJob() {
        Emisor personaEmisor = new Emisor();
        Ubicacion ubicacion = new Ubicacion();
        Identificacion identificacion = new Identificacion();
        Telefono telefono = new Telefono();

        String sql = "SELECT id_empresa, nombre, barrio, provincia, canton, "
                + " distrito, identificacion, tipo_identificacion, direccion,  "
                + " correo_electronico, telefono, fax, codigo_area, nombre_comercial "
                + " FROM searmedica.informacion_empresa ";
        try {
            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();
            for (Object[] objeto : lista) {
                personaEmisor.setNombre(objeto[1].toString().trim());
                personaEmisor.setCorreo_electronico(objeto[9].toString().trim());

                ubicacion.setId_barrio(objeto[2] == null ? null : objeto[2].toString().trim());
                ubicacion.setId_provincia(objeto[3].toString().trim());
                ubicacion.setId_canton(objeto[4].toString().trim());
                ubicacion.setId_distrito(objeto[5].toString().trim());
                ubicacion.setOtrasSennas(objeto[8].toString().trim());
                personaEmisor.setUbicacion(ubicacion);

                identificacion.setNumeroCedula(objeto[6].toString().trim());
                identificacion.setTipo(objeto[7].toString().trim());
                personaEmisor.setIdentificacion(identificacion);

                telefono.setCodigo_pais(objeto[12].toString().trim());
                telefono.setTelefono(objeto[10].toString().trim());
                personaEmisor.setTelefono(telefono);
                telefono.setTelefono(objeto[11].toString().trim());
                personaEmisor.setFax(telefono);
                personaEmisor.setNombreComercial(objeto[13].toString().trim());
            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return personaEmisor;
    }

    public Emisor obtenerPersonaEmisorJobPruebas() {
        Emisor personaEmisor = new Emisor();
        Ubicacion ubicacion = new Ubicacion();
        Identificacion identificacion = new Identificacion();
        Telefono telefono = new Telefono();

        String sql = "SELECT id_empresa, nombre, barrio, provincia, canton, "
                + " distrito, identificacion, tipo_identificacion, direccion,  "
                + " correo_electronico, telefono, fax, codigo_area, nombre_comercial "
                + " FROM searmedica.informacion_empresa ";
        try {
            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();
            for (Object[] objeto : lista) {
                personaEmisor.setNombre("JORGE DAVID QUESADA ARIAS");
                personaEmisor.setCorreo_electronico("jorgedqa17@gmail.com");

                ubicacion.setId_barrio(null);
                ubicacion.setId_provincia("3");
                ubicacion.setId_canton("01");
                ubicacion.setId_distrito("09");
                ubicacion.setOtrasSennas("Casa mano izquierda portones cafes");
                personaEmisor.setUbicacion(ubicacion);

                identificacion.setNumeroCedula("113940124");
                identificacion.setTipo("01");
                personaEmisor.setIdentificacion(identificacion);

                telefono.setCodigo_pais("506");
                telefono.setTelefono("70134867");
                personaEmisor.setTelefono(telefono);
                telefono.setTelefono("70151046");
                personaEmisor.setFax(telefono);
                personaEmisor.setNombreComercial("Jorge David Quesada Arias");
            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.metodo.obtener.persona.final",
                    "mensaje.error.metodo.obtener.persona.desarrollador");
        }
        return personaEmisor;
    }
//

    /**
     * Método que obtiene el cliente por el id de la persona
     *
     * @param idCliente - Long
     * @return Persona
     */
    public Persona obtenerPersonaPorIdCliente(Long idCliente) {
        Persona persona = null;
        Cliente cliente = null;
        try {
            cliente = em.createQuery("SELECT ct FROM Cliente ct WHERE ct.id_cliente=:idCliente", Cliente.class).setParameter("idCliente", idCliente).getSingleResult();
            persona = em.createQuery("SELECT p FROM  Persona p WHERE p.personaPK.numero_cedula=:numeroCedula", Persona.class).setParameter("numeroCedula", cliente.getNumero_cedula().trim()).getSingleResult();

        } catch (NoResultException nex) {
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.persona.por.cliente",
                    "mensaje.error.obtener.persona.por.cliente.desarrollador");
        }
        return persona;
    }

    public Long obtenerIdCliente() {
        Long idCliente = null;
        try {
            String sql = " select nextval('searmedica.\"seq_cliente\"') ";
            Object resultado = (Object) em.createNativeQuery(sql)
                    .getSingleResult();

            idCliente = Long.parseLong(resultado.toString());
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.secuencia.cliente",
                    "mensaje.error.secuencia.cliente.desarrollador");
        }
        return idCliente;
    }

    /**
     * Método que guarda una persona
     *
     * @param persona
     * @param cliente
     */
    public void actualizazr(Persona persona, Cliente cliente) {
        try {
            actualizar(persona);
            List<PersonaCorreo> listaCorreos = this.obtenerListaCorreosPersona(persona.getPersonaPK().getNumero_cedula(), persona.getPersonaPK().getId_tipo_cedula());
            listaCorreos.forEach(elemento -> {
                eliminar(elemento);
            });
            persona.getListaCorreosPersona().forEach(elemento -> {
                guardar(elemento);
            });
            actualizar(cliente);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.persona",
                    "mensaje.error.guardar.persona.desarrollador");
        }
    }

    public void actualizarPersona(Persona persona) {
        try {
            Persona personaAct = this.obtenerPersonaPorIdentificacion(persona.getPersonaPK().getNumero_cedula(), persona.getPersonaPK().getId_tipo_cedula());
            personaAct.setIdCodigoActividad(persona.getIdCodigoActividad());
            actualizar(personaAct);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.persona",
                    "mensaje.error.guardar.persona.desarrollador");
        }
    }

    /**
     * Método que guarda una persona
     *
     * @param persona
     * @param cliente
     */
    public void guardarPersona(Persona persona, Cliente cliente) {
        try {
            guardar(persona);
            List<PersonaCorreo> listaCorreos = this.obtenerListaCorreosPersona(persona.getPersonaPK().getNumero_cedula(), persona.getPersonaPK().getId_tipo_cedula());
            listaCorreos.forEach(elemento -> {
                eliminar(elemento);
            });
            persona.getListaCorreosPersona().forEach(elemento -> {
                guardar(elemento);
            });
            guardar(cliente);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.persona",
                    "mensaje.error.guardar.persona.desarrollador");
        }
    }

    public boolean existePersona(String numeroCedula) {
        boolean resultado = false;
        try {
            String hql = "SELECT pers FROM Persona pers WHERE pers.personaPK.numero_cedula = :numero_cedula";

            Persona personaResultado = em.createQuery(hql, Persona.class)
                    .setParameter("numero_cedula", numeroCedula)
                    .getSingleResult();
            if (personaResultado != null) {
                resultado = true;
            }

        } catch (NoResultException nex) {
            //   nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.guardar.persona",
                    "mensaje.error.guardar.persona.desarrollador");
        }

        return resultado;

    }

    public PersonaModelo obtenerPersonaModeloPorIdCliente(Long idCliente) {
        PersonaModelo resultado = null;
        PersonaModelo personaModelo = null;
        Cliente cliente = null;
        try {
            String sqlPersonaCliente = "SELECT pers FROM Persona pers WHERE  pers.personaPK.numero_cedula = :numero_cedula";
            String sqlCliente = "SELECT clit FROM Cliente clit WHERE clit.id_cliente = :id_cliente ";

            cliente = em.createQuery(sqlCliente, Cliente.class)
                    .setParameter("id_cliente", idCliente)
                    .getSingleResult();

            if (cliente != null) {
                Persona personaEncontrada = em.createQuery(sqlPersonaCliente, Persona.class)
                        .setParameter("numero_cedula", cliente.getNumero_cedula())
                        .getSingleResult();

                resultado = new PersonaModelo();
                resultado.setCliente(cliente);
                resultado.setPersona(personaEncontrada);
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.todas.personas.usuario ",
                    "mensaje.error.obtener.todas.personas.desarrollador");
        }
        return resultado;

    }

    public List<PersonaModelo> obtenerTodasPersonasPorNombre(String nombre) {
        List<PersonaModelo> listaResultado = new ArrayList<>();
        PersonaModelo personaModelo = null;
        Cliente cliente = null;
        try {

            String sqlPersonaCliente = "SELECT pers FROM Persona pers WHERE  trim(COALESCE(pers.nombre,'') ||' '||COALESCE(pers.primer_apellido,'')||' '||COALESCE(pers.segundo_apellido,'')) LIKE '%" + nombre.toUpperCase().replace(" ", "%") + "%' ";
            String sqlCliente = "SELECT clit FROM Cliente clit WHERE clit.numero_cedula = :numero_cedula ";

            List<Persona> personasEncontradas = em.createQuery(sqlPersonaCliente, Persona.class)
                    .getResultList();
            for (Persona personaEncontrada : personasEncontradas) {
                if (personaEncontrada != null) {

                    cliente = em.createQuery(sqlCliente, Cliente.class)
                            .setParameter("numero_cedula", personaEncontrada.getPersonaPK().getNumero_cedula())
                            .getSingleResult();

                    if (cliente != null) {
                        personaModelo = new PersonaModelo();
                        personaModelo.setCliente(cliente);
                        personaModelo.setPersona(personaEncontrada);
                        listaResultado.add(personaModelo);
                    }

                }
            }
        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.todas.personas.usuario ",
                    "mensaje.error.obtener.todas.personas.desarrollador");
        }
        return listaResultado;

    }

    public List<PersonaModelo> obtenerTodasPersonasMantenimiento() {
        List<PersonaModelo> listaResultado = null;
        try {
            String sql = " SELECT "
                    + " t1.numero_cedula, t1.id_tipo_cedula, t1.nombre, t1.primer_apellido, t1.segundo_apellido,  "
                    + " t1.correo_electronico, t1.direccion, t1.telefono_1, t1.telefono_2, t1.id_provincia, t1.id_canton, "
                    + " t1.id_distrito, t1.id_barrio, t1.es_persona_hacienda, t1.nombre_fantasia, t1.codigo_pais,  "
                    + " t1.es_activo, t1.es_exonerado, t1.es_exento, t2.id_cliente , t2.id_tipo_cliente ,t2.ind_requiere_transporte, t1.id_codigo_actividad, t1.exoneraciones  "
                    + " FROM searmedica.persona t1 inner join searmedica.cliente t2 "
                    + " on t1.numero_cedula = t2.numero_cedula  ";
            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();
            PersonaModelo personaModelo = null;
            Cliente cliente = null;
            Persona personaEncontrada = null;
            PersonaPK personaEncontradaPK = null;
            listaResultado = new ArrayList<>();
            for (Object[] fila : lista) {
                personaModelo = new PersonaModelo();

                cliente = new Cliente();
                cliente.setId_cliente(Long.parseLong(fila[19].toString()));
                cliente.setId_tipo_cliente(Long.parseLong(fila[20].toString()));
                cliente.setInd_requiere_transporte(Integer.parseInt(fila[21].toString()));
                cliente.setNumero_cedula(fila[0].toString());
                personaModelo.setCliente(cliente);

                personaEncontrada = new Persona();
                personaEncontradaPK = new PersonaPK(fila[0].toString(), Long.parseLong(fila[1].toString()));
                personaEncontrada.setPersonaPK(personaEncontradaPK);
                personaEncontrada.setNombre(fila[2] == null ? "" : fila[2].toString());
                personaEncontrada.setPrimer_apellido(fila[3] == null ? "" : fila[3].toString());
                personaEncontrada.setSegundo_apellido(fila[4] == null ? "" : fila[4].toString());
                personaEncontrada.setCorreo_electronico(fila[5] == null ? "" : fila[5].toString());
                personaEncontrada.setDireccion(fila[6] == null ? "" : fila[6].toString());
                personaEncontrada.setTelefono_1(fila[7] == null ? "" : fila[7].toString());
                personaEncontrada.setTelefono_2(fila[8] == null ? "" : fila[8].toString());
                personaEncontrada.setId_provincia(fila[9] == null ? null : Long.parseLong(fila[9].toString()));
                personaEncontrada.setId_canton(fila[10] == null ? null : Long.parseLong(fila[10].toString()));
                personaEncontrada.setId_distrito(fila[11] == null ? null : Long.parseLong(fila[11].toString()));
                personaEncontrada.setId_barrio(fila[12] == null ? null : Long.parseLong(fila[12].toString()));
                personaEncontrada.setEs_persona_hacienda(fila[13] == null ? null : Integer.parseInt(fila[13].toString()));
                personaEncontrada.setNombre_fantasia(fila[14] == null ? "" : fila[14].toString());
                personaEncontrada.setCodigo_pais(fila[15] == null ? null : fila[15].toString());
                personaEncontrada.setEs_activo(fila[16] == null ? null : Integer.parseInt(fila[16].toString()));
                personaEncontrada.setEs_exonerado(fila[17] == null ? null : Integer.parseInt(fila[17].toString()));
                personaEncontrada.setEs_exento(fila[18] == null ? null : Integer.parseInt(fila[18].toString()));
                personaEncontrada.setIdCodigoActividad(fila[22] == null ? null : Long.parseLong(fila[22].toString()));
                personaEncontrada.setExoneraciones(fila[23] == null ? "" : fila[23].toString());
                personaModelo.setPersona(personaEncontrada);

                listaResultado.add(personaModelo);
            }

//            String sqlPersonaCliente = "SELECT pers FROM Persona pers WHERE pers.personaPK.numero_cedula=:cedula";
//            String sqlCliente = "SELECT clit FROM Cliente clit  ";
//
//            List<Cliente> listaClientes = em.createQuery(sqlCliente, Cliente.class)
//                    .getResultList();
//
//            for (Cliente cliente : listaClientes) {
//
//                try {
//                    personaModelo = new PersonaModelo();
//                    personaModelo.setCliente(cliente);
//
//                    Persona personaEncontrada = em.createQuery(sqlPersonaCliente, Persona.class)
//                            .setParameter("cedula", personaModelo.getCliente().getNumero_cedula().trim())
//                            .getSingleResult();
//                    personaEncontrada.setEsExento(personaEncontrada.getEs_exento() == null ? false : (personaEncontrada.getEs_exento().equals(Indicadores.EXENTO_SI.getIndicador())));
//                    personaEncontrada.setEsExonerado(personaEncontrada.getEs_exonerado() == null ? false : (personaEncontrada.getEs_exonerado().equals(Indicadores.EXONERADO_SI.getIndicador())));
//                    personaModelo.setPersona(personaEncontrada);
//
//                    listaResultado.add(personaModelo);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.todas.personas.usuario ",
                    "mensaje.error.obtener.todas.personas.desarrollador");
        }
        return listaResultado;

    }

    /**
     * Método que obtiene todas las personas de
     *
     * @return List<PersonaModelo>
     */
    public List<PersonaModelo> obtenerTodasPersonasAlias() {
        List<PersonaModelo> listaResultado = null;
        try {
            String sql = " select t2.numero_cedula, t2.id_tipo_cedula, (case when t1.alias_cliente is null then t2.nombre else t1.alias_cliente end) nombre , t2.primer_apellido, \n"
                    + "t2.segundo_apellido,  t1.correo_electronico, t2.direccion, t2.telefono_1, \n"
                    + "t2.telefono_2, t2.id_provincia, id_canton,   t2.id_distrito, t2.id_barrio, \n"
                    + "t2.es_persona_hacienda,   t2.nombre_fantasia, t2.codigo_pais, t2.es_activo, \n"
                    + "t3.id_cliente, t3.id_tipo_cliente, t3.ind_requiere_transporte,  t2.es_exonerado, \n"
                    + "t2.es_exento, t2.id_codigo_actividad  from SEARMEDICA.persona_correo T1 inner join SEARMEDICA.PERSONA T2 \n"
                    + "on T1.numero_cedula = T2.numero_cedula and T1.id_tipo_cedula = T2.id_tipo_cedula \n"
                    + "inner join SEARMEDICA.CLIENTE T3 on T2.numero_cedula = T3.numero_cedula\n"
                    + "where t2.es_activo = 1  ";

            List<Object[]> listaClientes = em.createNativeQuery(sql)
                    .getResultList();

            listaResultado = new ArrayList<>();
            PersonaModelo personaModelo = null;
            PersonaPK personaPK = null;
            Persona persona = null;
            for (Object[] cliente : listaClientes) {
                try {
                    personaModelo = new PersonaModelo();
                    personaPK = new PersonaPK(cliente[0].toString(), Long.parseLong(cliente[1].toString()));

                    persona = new Persona(personaPK,
                            cliente[2].toString(),
                            cliente[3] == null ? "" : cliente[3].toString(),
                            cliente[4] == null ? "" : cliente[4].toString(),
                            cliente[5] == null ? "" : cliente[5].toString(),
                            cliente[6] == null ? "" : cliente[6].toString(),
                            cliente[7] == null ? "" : cliente[7].toString(),
                            cliente[8] == null ? "" : cliente[8].toString(),
                            cliente[9] == null ? "" : cliente[9].toString(),
                            null,
                            null,
                            null,
                            null,
                            cliente[14] == null ? "" : cliente[14].toString(),
                            1,
                            cliente[20] == null ? 0 : Integer.parseInt(cliente[20].toString()),
                            cliente[21] == null ? 0 : Integer.parseInt(cliente[21].toString()));
                    persona.setIdCodigoActividad(cliente[22] == null ? null : Long.parseLong(cliente[22].toString()));

                    personaModelo.setCliente(new Cliente(Long.parseLong(cliente[17].toString()), Integer.parseInt(cliente[19].toString()),
                            Long.parseLong(cliente[18].toString()), cliente[0].toString()));

                    personaModelo.setPersona(persona);

                    listaResultado.add(personaModelo);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.todas.personas.usuario ",
                    "mensaje.error.obtener.todas.personas.desarrollador");
        }
        return listaResultado;

    }

    /**
     * Método que obtiene todas las personas de
     *
     * @return List<PersonaModelo>
     */
    public List<PersonaModelo> obtenerTodasPersonas() {
        List<PersonaModelo> listaResultado = null;
        try {
            String sql = " SELECT t1.numero_cedula, t1.id_tipo_cedula, t1.nombre, t1.primer_apellido, t1.segundo_apellido,  "
                    + " t1.correo_electronico, t1.direccion, t1.telefono_1, t1.telefono_2, t1.id_provincia, id_canton,   "
                    + " t1.id_distrito, t1.id_barrio, t1.es_persona_hacienda,   "
                    + " t1.nombre_fantasia, t1.codigo_pais, t1.es_activo, t2.id_cliente, t2.id_tipo_cliente, t2.ind_requiere_transporte,  "
                    + " t1.es_exonerado, t1.es_exento "
                    + " FROM searmedica.persona t1 inner join searmedica.cliente t2  "
                    + " on t1.numero_cedula = t2.numero_cedula  "
                    + " where t1.es_activo = 1  ";

            List<Object[]> listaClientes = em.createNativeQuery(sql)
                    .getResultList();

            listaResultado = new ArrayList<>();
            PersonaModelo personaModelo = null;
            PersonaPK personaPK = null;
            Persona persona = null;
            for (Object[] cliente : listaClientes) {
                try {
                    personaModelo = new PersonaModelo();
                    personaPK = new PersonaPK(cliente[0].toString(), Long.parseLong(cliente[1].toString()));

                    persona = new Persona(personaPK,
                            cliente[2].toString(),
                            cliente[3] == null ? "" : cliente[3].toString(),
                            cliente[4] == null ? "" : cliente[4].toString(),
                            cliente[5] == null ? "" : cliente[5].toString(),
                            cliente[6] == null ? "" : cliente[6].toString(),
                            cliente[7] == null ? "" : cliente[7].toString(),
                            cliente[8] == null ? "" : cliente[8].toString(),
                            cliente[9] == null ? "" : cliente[9].toString(),
                            null,
                            null,
                            null,
                            null,
                            cliente[14] == null ? "" : cliente[14].toString(),
                            1,
                            cliente[20] == null ? 0 : Integer.parseInt(cliente[20].toString()),
                            cliente[21] == null ? 0 : Integer.parseInt(cliente[21].toString()));

                    personaModelo.setCliente(new Cliente(Long.parseLong(cliente[17].toString()), Integer.parseInt(cliente[19].toString()),
                            Long.parseLong(cliente[18].toString()), cliente[0].toString()));

                    personaModelo.setPersona(persona);

                    listaResultado.add(personaModelo);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.todas.personas.usuario ",
                    "mensaje.error.obtener.todas.personas.desarrollador");
        }
        return listaResultado;

    }

    /*
    
     //Obtengo la información de Persona
            personaResultado = em.createQuery(hql, Persona.class)
                    .setParameter("numero_cedula", identificacion)
                    .setParameter("id_tipo_cedula", tipoIdentificacion)
                    .getSingleResult();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        }
    
       String hql = "SELECT pers FROM Persona pers WHERE pers.personaPK.numero_cedula = :numero_cedula AND pers.personaPK.id_tipo_cedula = :id_tipo_cedula AND pers.es_persona_hacienda=1";
            //Obtengo la información de Persona
            personaResultado = em.createQuery(hql, Persona.class)
                    .setParameter("numero_cedula", identificacion)
                    .setParameter("id_tipo_cedula", tipoIdentificacion)
                    .getSingleResult();
    
     */
    /**
     * Método que obtiene una persona del tse
     *
     * @param cedula
     * @return ModeloPersonaTSE
     */
    public ModeloPersonaTSE obtenerPersonaTSE(String cedula) {
        ModeloPersonaTSE modelo = null;
        try {

            String sql = " SELECT T1.CEDULA, T1.NOMBRE, T1.PRIMER_APELLIDO, T1.SEGUNDO_APELLIDO FROM SEARMEDICA.PERSONAS_TSE T1 WHERE T1.CEDULA = :cedula ";
            Object[] resultado = (Object[]) em.createNativeQuery(sql)
                    .setParameter("cedula", cedula)
                    .getSingleResult();

            if (resultado != null) {
                modelo = new ModeloPersonaTSE();
                modelo.setNombre(resultado[1].toString().trim());
                modelo.setPrimerApellido(resultado[2].toString().trim());
                modelo.setSegundoApellido(resultado[3].toString().trim());
            }

        } catch (NoResultException nex) {
            // nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "obtenerPersonaTSE",
                    "mensaje.error.obtener.persona.tse.desarrollador");
        }
        return modelo;
    }

    /**
     * Método que obtiene los tipos de clientes
     *
     * @return List<TipoCliente>
     */
    public List<TipoCliente> obtenerListaTiposCliente() {
        List<TipoCliente> listaResultado = null;
        try {
            String hql = " SELECT t1 FROM TipoCliente t1  ";
            listaResultado
                    = em.createQuery(hql, TipoCliente.class
                    )
                            .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.cliente",
                    "mensaje.error.obtener.cliente.desarrollador");
        }
        return listaResultado;
    }

    public List<PersonaCorreo> obtenerListaCorreosPersona(String numeroCedula, Long idTipoCedula) {
        List<PersonaCorreo> listaResultado = null;
        try {
            String hql = " SELECT t1 FROM PersonaCorreo t1  WHERE t1.numero_cedula = :numerocedula AND t1.id_tipo_cedula = :id_tipo_cedula";
            listaResultado = em.createQuery(hql, PersonaCorreo.class)
                    .setParameter("numerocedula", numeroCedula)
                    .setParameter("id_tipo_cedula", idTipoCedula)
                    .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "Ha ocurrido un error cuando se intentó obtener el listado de correos de la persona",
                    "Ha ocurrido un error cuando se intentó obtener el listado de correos de la persona");
        }
        return listaResultado;
    }
}
