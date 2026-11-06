package com.ebs.constantes.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Qué ve el usuario en el catálogo de productos según sus perfiles.
 * <ul>
 * <li>COMPLETA: personal interno (o perfiles sin restricción): todos los precios y existencias.</li>
 * <li>AGENTE: perfil Agente: solo precio Por Mayor, existencia como Disponible/Agotado.</li>
 * <li>TIENDA: perfil Tienda: solo precio General, existencia como Disponible/Agotado.</li>
 * </ul>
 */
public enum VistaCatalogo {

    COMPLETA(null),
    AGENTE(TiposPrecios.POR_MAYOR),
    TIENDA(TiposPrecios.GENERAL);

    private static final long PERFIL_AGENTE = 4L;
    private static final long PERFIL_TIENDA = 7L;
    /** Facturador, Administrador, Bodega, Gestor Pagos, Gestor Inv, Perfil Bodegas. */
    private static final Set<Long> PERFILES_PERSONAL = new HashSet<>(Arrays.asList(1L, 2L, 3L, 5L, 6L, 8L));

    private final TiposPrecios tipoPrecio;

    private VistaCatalogo(TiposPrecios tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }

    /** Tipo de precio único que ve esta vista, o null si ve todos (COMPLETA). */
    public Long getIdTipoPrecio() {
        return tipoPrecio == null ? null : tipoPrecio.getTipoPrecio();
    }

    public boolean isRestringida() {
        return this != COMPLETA;
    }

    /**
     * Perfil Agente gana siempre (aunque el usuario tenga también perfiles de personal): solo ve el precio
     * Por Mayor. Sin Agente, el personal interno ve todo; después Tienda; cualquier otro caso ve todo.
     */
    public static VistaCatalogo deUsuario(Collection<Long> idsPerfiles) {
        if (idsPerfiles == null || idsPerfiles.isEmpty()) {
            return COMPLETA;
        }
        if (idsPerfiles.contains(PERFIL_AGENTE)) {
            return AGENTE;
        }
        for (Long id : idsPerfiles) {
            if (PERFILES_PERSONAL.contains(id)) {
                return COMPLETA;
            }
        }
        if (idsPerfiles.contains(PERFIL_TIENDA)) {
            return TIENDA;
        }
        return COMPLETA;
    }
}
