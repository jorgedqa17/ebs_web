package com.powersystem.util;

import com.ebs.exception.BeanException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ServicioBase implements IServicioBase {

    private static final long serialVersionUID = 1L;

    @PersistenceContext()
    protected EntityManager em;

    protected JPAQueryFactory qf;

    @PostConstruct
    private void init() {
        qf = new JPAQueryFactory(em);
    }

    //<<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
    //<<>><<>><<>><<>><<>>   METODOS GENERALES    <<>><<>><<>><<>><<>><<>><<>>
    //<<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
    /**
     * Obtiene la fecha de la base de datos.
     *
     * @return fecha de la base de datos
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 12/09/2017 12:41
     */
    public Date fechaBD() {
        try {
            String consulta = "	SELECT current_date";
            return aDate(em.createNativeQuery(consulta).getSingleResult());
        } catch (Exception ex) {
            log.error("ServicioBase.fechaBD()", ex);
            throw new BeanException(ex);
        }
    }

    //<<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
    /**
     * Obtiene la fecha de la base de datos.
     *
     * @return fecha de la base de datos
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 12/09/2017 12:41
     */
    public Date fechaHoraBD() {
        try {
            String consulta = "	SELECT now()";
            Date date = (Date) em.createNativeQuery(consulta).getSingleResult();

            return date;
        } catch (Exception ex) {
            log.error("ServicioBase.fechaBD()", ex);
            throw new BeanException(ex);
        }
    }

    /**
     * Crea un nuevo registro en la base de datos utilizando la entidad
     * solicitada
     *
     * @param g Object
     * @throws RuntimeException
     */
    @Override
    public void guardar(Object g) {
        try {

            em.persist(g);
            em.flush();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ServicioBase.guardar({})", g.toString(), e);
            throw new BeanException(e);
        }
    }   //guardar

    /**
     * Permite almacenar una lista de objectos
     *
     * @param lista Lista de objetos
     */
    @Override
    public void guardar(List<Object> lista) {
        try {
            lista.forEach((p) -> {
                em.persist(p);
            });
            em.flush();
        } catch (Exception e) {
            log.error("ServicioBase.guardar(List<Object>)", lista.toString(), e);
            throw new BeanException(e);
        }
    }

    /**
     * Actualiza un registro ya existente en la base de datos utilizando la
     * entidad solicitada
     *
     * @param g Object
     * @throws RuntimeException
     */
    @Override
    public void actualizar(Object g) {
        try {
            em.merge(g);
            em.flush();
        } catch (Exception e) {
            log.error("ServicioBase.actualizar({})", g.toString(), e);
            throw new BeanException(e);
        }
    }   //guardar

    /**
     * Elimina un registro ya existente en la base de datos utilizando la
     * entidad solicitada
     *
     * @param g Object
     * @throws RuntimeException
     */
    @Override
    public void eliminar(Object g) throws BeanException {
        try {
            em.remove(em.merge(g));
            em.flush();
        } catch (Exception e) {
            log.error("ServicioBase.eliminar({})", g.toString(), e);
            throw new BeanException(e);
        }
    }   //eliminar

    /**
     * Metodo encargado de buscar en la capa de datos, por el registro de la
     * clase envia como parametro, que tenga su identificador igual a parametro
     * pk.
     *
     * @param <T> Parametro para hacer la funcion generica
     * @param clase Clase a encontrar
     * @param pk Llave primaria del objeto a encontrar
     * @return Objeto encontrado
     */
    protected <T> T encontrar(Class<T> clase, Serializable pk) {
        return em.find(clase, pk);
    }

    /**
     *
     * @param <T> Parametro para hacer la funcion generica
     * @param clase Clase a encontrar
     * @param pk Llave primaria del objeto a encontrar
     * @return Refe encontrado
     */
    protected <T> T obtenerReferencia(Class<T> clase, Serializable pk) {
        return em.getReference(clase, pk);
    }

    /**
     * Realiza un listado de TODAS las entidades solicitadas
     *
     * @param c Class<T>
     * @param <T> T
     * @return List[G]
     * @throws RuntimeException
     */
    protected <T> List<T> listar(Class<T> c) {

        try {
            Query query = em.createQuery("select object(o) from " + c.getSimpleName() + " as o");
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar el objeto " + c.getSimpleName(), e);
        }
    }

    /**
     * Este método se encarga de encontrar la secuencia correspondiente a la
     * clase indicada como parámetro. Para que una clase se le encuentre la
     * secuencia debe de poseer la anotacion SequenceGenerator, y el nombre de
     * dicha secuencia dentro del parametro nombre de dicha anotación.
     * <br/> Después de obtener el nombre de la secuencia, se encarga de
     * consultar a la capa de datos, por dicho valor.
     *
     * @param clase Clase del objeto a obtener consecutivo.
     * @return Valor del consecutivo.
     */
    protected Long obtenerSecuencia(Class clase) {
        try {
            //Encuentro el nombre de la secuencia.
            SequenceGenerator anotacion = (SequenceGenerator) clase.getAnnotation(SequenceGenerator.class);
            String nombre_secuencia = anotacion.sequenceName();
            //Encuentro el valor de la secuencia
            String consulta = "select " + nombre_secuencia + ".nextval from dual";
            return ((BigDecimal) (this.em.createNativeQuery(consulta)
                    .getResultList())
                    .get(0)).longValue();
        } catch (Exception ex) {
            return null;
//            throw new ExcepcionServicio("Ha ocurrido un error obteniendo el valor de una secuencia.",
//                    "Ha ocurrido un error obteniendo el valor de una secuencia. En "+
//                    getClass()+".obtenerSecuencia(Class clase)",
//                    ex, getClass());
        }
    }

    /**
     * Convierte un valor de una consulta nativa a Integer.
     *
     * @param obj Objecto a convertir
     * @return Valor del Integer o null
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 14/03/2017 15:10
     */
    protected Integer aInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return null;
    }

    /**
     * Convierte un valor de una consulta nativa a Long.
     *
     * @param obj Objecto a convertir
     * @return Valor del Long o null
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 14/03/2017 15:10
     */
    protected Long aLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).longValue();
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        return null;
    }

    /**
     * Convierte un valor de una consulta nativa a Doble.
     *
     * @param obj Objecto a convertir
     * @return Valor del Double o null
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 10/04/2017 14:22
     */
    protected Double aDouble(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).doubleValue();
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        return null;
    }

    /**
     * Convierte un valor de una consulta nativa a String.
     *
     * @param obj Objecto a convertir
     * @return Valor del String o null
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 14/03/2017 15:10
     */
    protected String aString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Character) {
            return String.valueOf((Character) obj);
        }
        return null;
    }

    /**
     * Convierte un valor de una consulta nativa a Date.
     *
     * @param obj Objecto a convertir
     * @return Valor del Date o null
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 14/03/2017 15:10
     */
    protected Date aDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        return null;
    }

    /**
     * Convierte un valor de una consulta nativa a Integer.
     *
     * @param obj Objecto a convertir
     * @return Valor del Integer o null
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 14/03/2017 15:10
     */
    protected Boolean aBoolean(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue() == 1;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        return null;
    }

    /**
     * Convierte un String de una consulta nativa a un Enumerado.
     *
     * @param <T> Tipo genérico de enumerado
     * @param enumerado Clase del enumerado
     * @param nombre Valor como String del enumerado
     * @return Valor del String a Enumerado o null si no lo encuentra
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 21/04/2017 07:50
     */
    protected <T extends Enum<T>> T aEnum(Class<T> enumerado, String nombre) {
        if (enumerado == null || nombre == null) {
            return null;
        }
        for (T valor : enumerado.getEnumConstants()) {
            if (valor.name().equalsIgnoreCase(nombre)) {
                return valor;
            }
        }
        return null;
    }

    /**
     * Convierte un String de una consulta nativa a un Enumerado.
     *
     * @param <T> Tipo genérico de enumerado
     * @param valor Valor a convertir
     * @param enumerado Clase del enumerado
     * @return Valor del String a Enumerado o null si no lo encuentra
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 21/04/2017 07:50
     */
    protected <T extends Enum<T>> T aEnum(Object valor, Class<T> enumerado) {
        String nombre = aString(valor);
        if (nombre == null) {
            return null;
        }
        return aEnum(enumerado, nombre);
    }

    /**
     * Convierte un String de una consulta nativa a un Enumerado.
     *
     * @param <T> Tipo genérico de enumerado
     * @param valor Valor a convertir
     * @param enumerado Clase del enumerado
     * @param defecto Valor por defecto del enumerado si no lo encuentra
     * @return Valor del String a Enumerado o null si no lo encuentra
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 21/04/2017 07:50
     */
    protected <T extends Enum<T>> T aEnum(Object valor, Class<T> enumerado, T defecto) {
        T resultado = aEnum(valor, enumerado);
        return resultado != null ? resultado : defecto;
    }

    /**
     * Nombre del método que se encarga de obtener el String del CLOB.
     */
    private static final String GET_WRAPPED_CLOB = "getWrappedClob";

    /**
     * Convierte un proxy de CLOB de una consulta nativa a un String.
     *
     * @param proxy Objecto proxy
     * @return El valor del CLOB como String
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 21/06/2017 07:50
     */
    protected String aStringClob(Object proxy) {
        if (proxy == null) {
            return null;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(proxy.getClass());
            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                Method readMethod = property.getReadMethod();
                if (readMethod.getName().equals(GET_WRAPPED_CLOB)) {
                    Object result = readMethod.invoke(proxy);
                    return clobToString((Clob) result);
                }
            }
        } catch (Exception ex) {
            log.error("ServicioBase.aStringClob({})", proxy, ex);
            throw new BeanException(ex);
        }
        return null;
    }

    /**
     * Convierte un objeto CLOB a String.
     *
     * @param data El CLOB a convertir
     * @return El valor del CLOB como String
     * @throws SQLException En caso de error en la consulta de SQL.
     * @throws IOException En caso de error de IO.
     * @author GBSYS. Diseñado y Desarrollado por: Ing. Herman Barrantes
     * @version 1.0.0
     * @date 21/06/2017 07:50
     */
    private String clobToString(Clob data) throws SQLException, IOException {
        if (data == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Reader reader = data.getCharacterStream();
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while (null != (line = br.readLine())) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
