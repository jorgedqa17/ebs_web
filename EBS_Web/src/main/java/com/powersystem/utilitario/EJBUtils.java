package com.powersystem.utilitario;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * Clase Utilitaria para controlar EJBs
 *
 * @author ewatt
 *
 *
 * @author Consorcio Siansa-Indra. Modificado por: Lsanchez 21/08/2014 Año:2014
 */
public class EJBUtils {

    /**
     * Busca un EJB en el context. Es necesario que el EJB, tenga el mismo
     * nombre de la clase que se envia por parámetro.
     *
     * @param clase Clase del EJB al que se desea obtener una instancia del EJB.
     * @param <T> T
     * @return Objeto con la instancia del EJB que se indicó obtener.
     */
    public static <T> T obtenerEJB(Class<T> clase) {

        InitialContext iniCtx;
        try {
            iniCtx = new InitialContext();
            String mname = (String) iniCtx.lookup("java:module/ModuleName");
            String name = clase.getSimpleName();
            String nombre = "java:global/" + mname + "/" + name;
            return (T) iniCtx.lookup(nombre);
        } catch (NamingException ex) {
            return null;
        }
    }
}
