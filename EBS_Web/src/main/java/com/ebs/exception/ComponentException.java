package com.ebs.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * Excepcion global enviada desde la capa de servicios.
 *
 * @author Consorcio Siansa-Indra. Diseñado y Desarrollado por: Lsanchez.
 * 01/04/2014.
 */
public class ComponentException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 724958865994216139L;
    /**
     * if a matching method is not found or if the name is "<init>"or
     * "<clinit>". NoSuchMethodException.
     */
    public static final int NO_SUCH_METHOD_EXCEPTION = -8001;
    /**
     * if name is null. NullPointerException.
     */
    public static final int NULL_POINTER_EXCEPTION = -8002;
    /**
     * If a security manager, s, is present and any of the following conditions
     * is met: invocation of s.checkMemberAccess(this, Member.PUBLIC) denies
     * access to the method the caller's class loader is not the same as or an
     * ancestor of the class loader for the current class and invocation of
     * s.checkPackageAccess() denies access to the package of this class
     */
    public static final int SECURITY_EXCEPTION = -8003;
    public static final int ILLEGAL_ACCESS_EXCEPTION = -8004;
    public static final int ILLEGAL_ARGUMENT_EXCEPTION = -8005;
    public static final int INVOCATION_TARGET_EXCEPTION = -8005;
    /**
     * Cuando no se conoce la exception.
     */
    public static final int UNKNOW_EXCEPTION = -8000;
    /**
     * code.
     */
    private int code;
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
    public ComponentException(final int pCode) {
        super();
        this.code = pCode;
    }

    /**
     * Constructor.
     *
     * @param pCode Codigo Error.
     * @param pNombreClase Nombre clase
     */
    public ComponentException(final int pCode, final String pNombreClase) {
        super();
        this.code = pCode;
        this.nombreClase = pNombreClase;
    }

    /**
     * Constructor.
     *
     * @param pException Exception.
     */
    public ComponentException(final Exception pException) {
        super();
        if (pException instanceof NoSuchMethodException) {
            code = NO_SUCH_METHOD_EXCEPTION;
        } else if (pException instanceof NullPointerException) {
            code = NULL_POINTER_EXCEPTION;
        } else if (pException instanceof SecurityException) {
            code = SECURITY_EXCEPTION;
        } else if (pException instanceof IllegalAccessException) {
            code = ILLEGAL_ACCESS_EXCEPTION;
        } else if (pException instanceof IllegalArgumentException) {
            code = ILLEGAL_ARGUMENT_EXCEPTION;
        } else if (pException instanceof InvocationTargetException) {
            code = INVOCATION_TARGET_EXCEPTION;
        } else {
            code = UNKNOW_EXCEPTION;
        }
        exception = pException;
    }

    /**
     * Constructor.
     *
     * @param pCode Codigo Error.
     * @param pException Exception
     */
    public ComponentException(
            final int pCode, final Exception pException) {
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
    public ComponentException(
            final int pCode, final Exception pException,
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
    public final int getCode() {
        return code;
    }

    /**
     * Establece el valor del atributo code.
     *
     * @param pCode El valor del atributo code a establecer
     */
    public final void setCode(final int pCode) {
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
}
