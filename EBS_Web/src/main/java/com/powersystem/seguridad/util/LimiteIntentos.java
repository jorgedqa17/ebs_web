package com.powersystem.seguridad.util;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Limita los intentos fallidos de inicio de sesión (y de cambio de contraseña, que también permite adivinar claves).
 *
 * Cuenta fallos por dirección IP y por usuario, en memoria (se pierde al reiniciar Payara, aceptable para este fin).
 * Pasado el máximo dentro de la ventana, la clave queda bloqueada un tiempo, aunque la contraseña sea correcta.
 * El límite por usuario es más alto que el de la IP para que alguien desde afuera no pueda dejar fuera
 * a un usuario real con unos pocos intentos.
 */
public final class LimiteIntentos {

    public static final int MAX_POR_IP = 5;
    public static final int MAX_POR_USUARIO = 10;
    private static final long VENTANA_MS = 10 * 60 * 1000L;
    private static final long BLOQUEO_MS = 10 * 60 * 1000L;
    private static final int TOPE_ENTRADAS = 5000;

    private static final class Registro {

        int fallos;
        long inicioVentana;
        long bloqueadoHasta;
    }

    private static final Map<String, Registro> REGISTROS = new ConcurrentHashMap<>();

    private LimiteIntentos() {
    }

    /** IP del cliente tal como la ve el servidor (la aplicación se publica directo, sin proxy). */
    public static String ipCliente() {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null && ctx.getExternalContext().getRequest() instanceof HttpServletRequest) {
                String ip = ((HttpServletRequest) ctx.getExternalContext().getRequest()).getRemoteAddr();
                return ip == null ? "desconocida" : ip;
            }
        } catch (RuntimeException ex) {
            // sin contexto: se trata como una sola IP desconocida
        }
        return "desconocida";
    }

    private static String claveIp(String ip) {
        return "ip:" + ip;
    }

    private static String claveUsuario(String usuario) {
        return "usuario:" + (usuario == null ? "" : usuario.trim().toUpperCase());
    }

    /** Segundos que faltan para poder reintentar, o 0 si no está bloqueado. */
    public static long segundosBloqueado(String ip, String usuario) {
        long ahora = System.currentTimeMillis();
        long hasta = Math.max(bloqueadoHasta(claveIp(ip), ahora), bloqueadoHasta(claveUsuario(usuario), ahora));
        return hasta > ahora ? (hasta - ahora + 999) / 1000 : 0;
    }

    private static long bloqueadoHasta(String clave, long ahora) {
        Registro r = REGISTROS.get(clave);
        if (r == null) {
            return 0;
        }
        synchronized (r) {
            return r.bloqueadoHasta;
        }
    }

    /** Anota un intento fallido contra la IP y contra el usuario. */
    public static void registrarFallo(String ip, String usuario) {
        long ahora = System.currentTimeMillis();
        limpiarSiHaceFalta(ahora);
        sumar(claveIp(ip), MAX_POR_IP, ahora);
        sumar(claveUsuario(usuario), MAX_POR_USUARIO, ahora);
    }

    /** Un ingreso correcto borra los fallos del usuario (los de la IP siguen su ventana). */
    public static void registrarExito(String usuario) {
        REGISTROS.remove(claveUsuario(usuario));
    }

    private static void sumar(String clave, int maximo, long ahora) {
        Registro r = REGISTROS.computeIfAbsent(clave, k -> new Registro());
        synchronized (r) {
            if (ahora - r.inicioVentana > VENTANA_MS) {
                r.inicioVentana = ahora;
                r.fallos = 0;
            }
            r.fallos++;
            if (r.fallos >= maximo) {
                r.bloqueadoHasta = ahora + BLOQUEO_MS;
                r.fallos = 0;
                r.inicioVentana = ahora;
            }
        }
    }

    private static void limpiarSiHaceFalta(long ahora) {
        if (REGISTROS.size() < TOPE_ENTRADAS) {
            return;
        }
        for (Iterator<Map.Entry<String, Registro>> it = REGISTROS.entrySet().iterator(); it.hasNext();) {
            Registro r = it.next().getValue();
            synchronized (r) {
                if (r.bloqueadoHasta < ahora && ahora - r.inicioVentana > VENTANA_MS) {
                    it.remove();
                }
            }
        }
    }

    /** Texto para mostrar al usuario. */
    public static String mensajeBloqueo(long segundos) {
        long minutos = (segundos + 59) / 60;
        return "Demasiados intentos fallidos. Espere " + minutos + " minuto(s) e intente de nuevo.";
    }
}
