/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.constantes.enums.IngresoTipo;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.InventarioIngreso;
import com.ebs.entidades.Producto;
import com.powersystem.utilitario.Utilitario;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.faces.bean.ManagedBean;
import lombok.Data;

/**
 *
 * @author
 */
@Data
@ManagedBean
public class ModeloIngreso {

    private InventarioIngreso inventarioIngreso;

    private String bodegaOrigen;

    private String responsable;

    private String tipoIngreso;

    private String fecha;

    private Bodega bodega;

    public ModeloIngreso() {
    }

    public ModeloIngreso(InventarioIngreso inventarioIngreso, Bodega bodega) {
        this.inventarioIngreso = inventarioIngreso;
        this.bodega = bodega;
        this.cargarDatos();
    }

    public ModeloIngreso(InventarioIngreso inventarioIngreso, Bodega bodega, String responsable) {
        this.inventarioIngreso = inventarioIngreso;
        this.bodega = bodega;
        this.responsable = responsable;
        this.cargarDatos();
    }

    private void cargarDatos() {
        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        bodegaOrigen = bodega.getDescripcion();
        //responsable = Utilitario.obtenerUsuario().getLogin();
        fecha = formato.format(inventarioIngreso.getFechaRegistro());
        tipoIngreso = "";
        for (IngresoTipo tipo : IngresoTipo.values()) {
            if (tipo.getIdTipo().longValue() == inventarioIngreso.getIdInventarioIngresoTipo().longValue()) {
                tipoIngreso = tipo.getNombre();
                break;
            }
        }
    }

}
