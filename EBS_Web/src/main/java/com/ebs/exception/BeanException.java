package com.ebs.exception;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import javax.naming.NamingException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

/**
 * Excepcion global enviada desde la capa de servicios.
 *
 * @author Consorcio Siansa-Indra.<br>
 * Diseñado y Desarrollado por: bejimenez, lsanchez. 01/04/2014.<br>
 * Año:2014<br>
 */
public class BeanException extends PersistenceException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 724958865994216139L;
    /**
     * if the instance is not an entity.
     */
    public static final String ILLEGAL_ARGUMENT_EXCEPTION = "-7001";
    /**
     * if called for a Java Persistence query language UPDATE or DELETE
     * statement.
     */
    public static final String ILLEGAL_STATE_EXCEPTION = "-7002";
    /**
     * if a lock mode other than NONE has been set and there is no transaction
     * or the persistence context has not been joined to the transaction.
     */
    public static final String TRANSACTION_REQUIRED_EXCEPTION = "-7003";
    /**
     * if the entity already exists. (If the entity already exists, the
     * EntityExistsException may be thrown when the persist operation is
     * invoked, or the EntityExistsException or another PersistenceException may
     * be thrown at flush or commit time.).
     */
    public static final String ENTITY_EXISTS_EXCEPTION = "-7004";
    /**
     * if the query execution exceeds the query timeout value set and the
     * transaction is rolled back.
     */
    public static final String PERSISTENCE_EXCEPTION = "-7005";
    /**
     * if the entity state cannot be accessed.
     */
    public static final String ENTITY_NOT_FOUND_EXCEPTION = "-7006";
    /**
     * if pessimistic locking fails and only the statement is rolled back.
     */
    public static final String LOCK_TIMEOUT_EXCEPTION = "-7007";
    /**
     * if pessimistic locking fails and the transaction is rolled back.
     */
    public static final String PESSIMISTIC_LOCK_EXCEPTION = "-7008";
    /**
     * if the query execution exceeds the query timeout value set and only the
     * statement is rolled back.
     */
    public static final String QUERY_TIMEOUT_EXCEPTION = "-7009";
    /**
     * if more than one result.
     */
    public static final String NO_RESULT_EXCEPTION = "-7010";
    /**
     * if there is no result.
     */
    public static final String NON_UNIQUE_RESULT_EXCEPTION = "-7011";
    /**
     * if a database access error occurs or this method is called on a closed
     * connection.
     */
    public static final String SQL_EXCEPTION = "-7203";
    /**
     * if a database access error occurs or this method is called on a closed
     * connection.
     */
    public static final String NAMING_EXCEPTION = "-7204";
    /**
     * Cuando ocurre un error del sistema.
     */
    public static final String UNKNOW_EXCEPTION = "-7205";
    /**
     * io exception.
     */
    public static final String IO_EXCEPTION = "-7012";
    /**
     * No se puede realizar la actualización de rollo e imagen debido a que está
     * siendo utilizada por alguien más.
     */
    public static final String USUARIO_INVALIDO_EXCEPTION = "-7401";
    /**
     * No se puede realizar la actualización de rollo e imagen debido a que está
     * siendo utilizada por alguien más.
     */
    public static final String TERMINAL_INVALIDA_EXCEPTION = "-7402";
    /**
     * No se puede realizar la actualización de rollo e imagen debido a que está
     * siendo utilizada por alguien más.
     */
    public static final String FUNCIONARIO_INVALIDO_EXCEPTION = "-7403";
    /**
     * No se puede realizar la actualización de rollo e imagen debido a que está
     * siendo utilizada por alguien más.
     */
    public static final String USUARIO_NO_ASOCIADO_EXCEPTION = "-7404";
    /**
     * No se puede realizar la actualización de rollo e imagen debido a que está
     * siendo utilizada por alguien más.
     */
    public static final String ACTUALIZACION_ROLLO_EXCEPTION = "-7501";
    /**
     * ,"El documento con las citas: Tomo"-"+ documentoVO.getNTomo()+"
     * Asiento"-" +documentoVO.getNAsiento() +" no pudo ser Actualizado");.
     */
    public static final String DOCUMENTO_NO_ACTUALIZADO = "-7502";
    /**
     * ,"El documento con las citas: Tomo"-"+ documentoVO.getNTomo()+"
     * Asiento"-" +documentoVO.getNAsiento() +" no pudo ser Actualizado");.
     */
    public static final String DOCUMENTO_ACTUALIZADO_POR_OTRO = "-7503";
    /**
     * No se pudo realizar el cambio de registrador para los documentos en
     * proceso.
     */
    public static final String SP_EXCEPTION = "-7504";
    /**
     * No se pudo realizar el cambio de registrador para los documentos en
     * proceso.
     */
    public static final String NULL_POINTER = "-7505";
    /**
     * No se pudo realizar la actualización del funcionario
     */
    public static final String SP_EXCEPTION_ACTUALIZA_FUNCIONARIO = "-7506";
    /**
     * No se pudo realizar la guardando el funcionario
     */
    public static final String SP_EXCEPTION_GUARDAR_FUNCIONARIO = "-7507";
    /**
     * No se pudo realizar la actualización del movimiento
     */
    public static final String SP_EXCEPTION_ACTUALIZA_MOVIMIENTO = "-7508";
    /**
     * No se pudo realizar la guardando el movimiento
     */
    public static final String SP_EXCEPTION_GUARDAR_MOVIMIENTO = "-7509";
    /**
     * No se pudo realizar el cálculo de pesos
     */
    public static final String SP_EXCEPTION_CALCULA_PESOS = "-7510";
    /**
     * No se pudo realizar la activación/desactivación de registradores
     */
    public static final String SP_EXCEPTION_ACTIVA_DESACTIVA_REGIS = "-7511";
    /**
     * ERROR SUMANDO O RESTANDO LOS PESOS
     */
    public static final String SP_EXCEPTION_ERROR_SUMANDO_O_RESTANDO_LOS_PESOS = "-7512";
    /**
     * ERROR REGISTRADOR NO SE ENCUENTRA
     */
    public static final String SP_EXCEPTION_ERROR_REGISTRADOR_NO_SE_ENCUENTRA = "-7513";
    /**
     * ERROR REGISTRO INVALIDO
     */
    public static final String SP_EXCEPTION_ERROR_REGISTRO_INVALIDO = "-7514";
    /**
     * ERROR: NO SE LE PUEDE RESTAR MAS PESOS DE LOS QUE TIENE EL REGISTRADOR.
     */
    public static final String SP_EXCEPTION_ERROR_NO_SE_LE_PUEDE_RESTAR_MAS_PESOS_DE_LOS_QUE_TIENE_EL_REGISTRADOR = "-7515";
    /**
     * PROCEDENCIA INVALIDA
     */
    public static final String SP_EXCEPTION_PROCEDENCIA_INVALIDA = "-7516";
    /**
     * Error Consulta previa inválida
     */
    public static final String SP_EXCEPTION_CONSULTA_PREVIA_INVALIDA = "-7517";
    /**
     * code.
     */
    private String code;
    /**
     * nombreClase.
     */
    private String nombreClase;
    /**
     * exception.
     */
    private Exception exception;
    /**
     * exception.
     */
    private Object object;

    /**
     * Constructor.
     *
     * @param pCode Codigo Error.
     */
    public BeanException(final String pCode) {
        super();
        this.code = pCode;
    }

    /**
     * Constructor.
     *
     * @param pCode Codigo Error.
     * @param pNombreClase Nombre clase
     */
    public BeanException(final String pCode, final String pNombreClase) {
        super();
        this.code = pCode;
        this.nombreClase = pNombreClase;
    }

    /**
     * Constructor.
     *
     * @param pException Exception.
     */
    public BeanException(final Exception pException) {
        super();
        boolean cargaException = true;
        if (pException instanceof NamingException) {
            code = NAMING_EXCEPTION;
        } else if (pException instanceof SQLException) {
            code = SQL_EXCEPTION;
        } else if (pException instanceof SQLTimeoutException) {
            code = SQL_EXCEPTION;
        } else if (pException instanceof IllegalArgumentException) {
            code = ILLEGAL_ARGUMENT_EXCEPTION;
        } else if (pException instanceof EntityNotFoundException) {
            code = ENTITY_NOT_FOUND_EXCEPTION;
        } else if (pException instanceof NoResultException) {
            code = NO_RESULT_EXCEPTION;
        } else if (pException instanceof TransactionRequiredException) {
            code = TRANSACTION_REQUIRED_EXCEPTION;
        } else if (pException instanceof PersistenceException) {
            code = PERSISTENCE_EXCEPTION;
        } else if (pException instanceof EntityExistsException) {
            code = ENTITY_EXISTS_EXCEPTION;
        } else if (pException instanceof IOException) {
            code = IO_EXCEPTION;
        } else if (pException instanceof IOException) {
            code = SP_EXCEPTION;
        } else if (pException instanceof NullPointerException) {
            code = NULL_POINTER;
        } else if (pException instanceof BeanException) {
            code = ((BeanException) pException).getCode();
            exception = ((BeanException) pException).getException();
            cargaException = false;
        } else {
            code = UNKNOW_EXCEPTION;
        }
        if (cargaException) {
            exception = pException;
        }
    }

    /**
     * Constructor.
     *
     * @param pCode Codigo Error.
     * @param pException Exception
     */
    public BeanException(
            final String pCode, final Exception pException) {
        super();
        this.code = pCode;
        this.exception = pException;
    }

    /**
     * Constructor.
     *
     * @param pCode Codigo Error.
     * @param pException Exception
     * @param pObject Object
     */
    public BeanException(
            final String pCode, final Exception pException,
            final Object pObject) {
        super();
        this.code = pCode;
        this.exception = pException;
        this.object = pObject;
    }

    /**
     * Obtiene el valor del atributo code.
     *
     * @return El valor del atributo code
     */
    public final String getCode() {
        return code;
    }

    /**
     * Establece el valor del atributo code.
     *
     * @param pCode El valor del atributo code a establecer
     */
    public final void setCode(final String pCode) {
        this.code = pCode;
    }

    /**
     * Obtiene el valor del atributo nombreClase.
     *
     * @return El valor del atributo nombreClase
     */
    public final String getNombreClase() {
        return nombreClase;
    }

    /**
     * Establece el valor del atributo nombreClase.
     *
     * @param pNombreClase El valor del atributo nombreClase a establecer
     */
    public final void setNombreClase(final String pNombreClase) {
        this.nombreClase = pNombreClase;
    }

    /**
     * Obtiene el valor del atributo exception.
     *
     * @return El valor del atributo exception
     */
    public final Exception getException() {
        return exception;
    }

    /**
     * Establece el valor del atributo code.
     *
     * @param pException El valor del atributo code a establecer
     */
    public final void setException(final Exception pException) {
        this.exception = pException;
    }

    /**
     * Obtiene el valor del atributo object.
     *
     * @return El valor del atributo object
     */
    public final Object getObject() {
        return object;
    }

    /**
     * Establece el valor del atributo object.
     *
     * @param pObject El valor del atributo object a establecer
     */
    public final void setObject(final Object pObject) {
        this.object = pObject;
    }

    /**
     * Valida si la entidad fue removida por otra persona.
     *
     * @return true si la entidad fue removida de lo contrario false.
     */
    public final boolean isEntityRemove() {
        return this.code == ILLEGAL_ARGUMENT_EXCEPTION;
    }
}
