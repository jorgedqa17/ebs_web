package com.powersystem.utilitario;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Ícono (Font Awesome 4.7) de cada opción del menú. La columna pantalla.icono casi no tiene datos, así
 * que cuando está vacía se usa el ícono de esta tabla, buscado por la descripción de la pantalla (sin
 * tildes ni mayúsculas, para que no dependa de cómo esté escrita en la BD). Si en la BD hay un ícono,
 * ese manda. Pantalla que no está aquí: ícono genérico.
 */
public final class IconosMenu {

    private static final Map<String, String> POR_DESCRIPCION = new HashMap<>();

    static {
        // Secciones
        poner("Facturación", "fa fa-file-text-o");
        poner("Mantenimientos", "fa fa-cogs");
        poner("Inventarios", "fa fa-archive");
        poner("Consultas", "fa fa-search");
        // Facturación
        poner("Proceso de Facturación", "fa fa-shopping-cart");
        poner("Administración de Facturas", "fa fa-folder-open-o");
        poner("Reimpresión Facturas", "fa fa-print");
        poner("Notas de Crédito", "fa fa-minus-square-o");
        poner("Administración de Notas de Crédito", "fa fa-list-alt");
        poner("Notas de Débito", "fa fa-plus-square-o");
        poner("Administración de Notas de Débito", "fa fa-list-alt");
        poner("Generación de Cierre", "fa fa-lock");
        poner("Generación de Cierre Agente", "fa fa-lock");
        poner("Generar Recibos", "fa fa-file-text");
        poner("Control de Cuentas", "fa fa-balance-scale");
        poner("Procesamiento de Pagos", "fa fa-credit-card");
        poner("Administración de Pagos", "fa fa-money");
        // Mantenimientos
        poner("Nuevo Producto", "fa fa-plus-circle");
        poner("Lista Productos", "fa fa-list");
        poner("Proveedores", "fa fa-truck");
        poner("Clientes", "fa fa-address-book-o");
        poner("Listado de Clientes", "fa fa-address-book-o");
        poner("Mantenimiento de Usuarios", "fa fa-users");
        // Inventarios
        poner("Ingreso de Inventario", "fa fa-download");
        poner("Envios Diarios", "fa fa-paper-plane-o");
        poner("Solicitud de Inventario", "fa fa-inbox");
        poner("Consulta de Ingresos", "fa fa-history");
        poner("Consulta Trazabilidad Productos", "fa fa-map-signs");
        poner("Consulta de Exitencias Mínimas", "fa fa-exclamation-triangle");
        poner("Consulta de Existencias Mínimas", "fa fa-exclamation-triangle");
        poner("Consulta General de Inventario", "fa fa-cubes");
        // Consultas
        poner("Consulta Cierre", "fa fa-calendar-check-o");
        poner("Ventas Mensuales", "fa fa-bar-chart");
        poner("Consulta de Precios", "fa fa-tags");
        poner("Exportar Ventas y Gastos", "fa fa-file-excel-o");
        poner("Exportar Precios", "fa fa-file-excel-o");
        poner("Catálogo de Productos", "fa fa-picture-o");
    }

    private IconosMenu() {
    }

    private static void poner(String descripcion, String icono) {
        POR_DESCRIPCION.put(clave(descripcion), icono);
    }

    private static String clave(String texto) {
        String sinTildes = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * @param iconoBd lo que diga pantalla.icono (puede ser null o vacío)
     * @param descripcion descripción de la pantalla o sección
     * @param esSeccion true para las secciones del primer nivel (cambia el ícono genérico)
     */
    public static String para(String iconoBd, String descripcion, boolean esSeccion) {
        if (iconoBd != null && !iconoBd.trim().isEmpty()) {
            return iconoBd.trim();
        }
        String icono = POR_DESCRIPCION.get(clave(descripcion));
        if (icono != null) {
            return icono;
        }
        return esSeccion ? "fa fa-folder-o" : "fa fa-circle-o";
    }
}
