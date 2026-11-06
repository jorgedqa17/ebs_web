package com.ebs.personas.bean;

import com.ebs.modelos.PersonaModelo;
import com.powersystem.personas.servicios.ServicioPersona;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Clientes de la pantalla de mantenimiento, paginados y filtrados en la base de datos. Solo conserva
 * la página actual en memoria, en lugar de todos los clientes en cada sesión.
 */
public class ClienteMantenimientoLazyModel extends LazyDataModel<PersonaModelo> {

    private static final long serialVersionUID = 1L;

    private final ServicioPersona servicioPersona;
    private List<PersonaModelo> paginaActual;

    public ClienteMantenimientoLazyModel(ServicioPersona servicioPersona) {
        this.servicioPersona = servicioPersona;
    }

    @Override
    public List<PersonaModelo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Map<String, String> filtros = new HashMap<>();
        if (filters != null) {
            for (Map.Entry<String, Object> f : filters.entrySet()) {
                if (f.getValue() != null) {
                    filtros.put(f.getKey(), f.getValue().toString());
                }
            }
        }
        setRowCount((int) servicioPersona.contarClientesMantenimiento(filtros));
        paginaActual = servicioPersona.listarClientesMantenimiento(first, pageSize, filtros);
        return paginaActual;
    }

    @Override
    public Object getRowKey(PersonaModelo persona) {
        return persona.getPersona().getPersonaPK().getNumero_cedula() + "|" + persona.getPersona().getPersonaPK().getId_tipo_cedula();
    }

    @Override
    public PersonaModelo getRowData(String rowKey) {
        if (paginaActual != null) {
            for (PersonaModelo p : paginaActual) {
                if (getRowKey(p).equals(rowKey)) {
                    return p;
                }
            }
        }
        return null;
    }
}
