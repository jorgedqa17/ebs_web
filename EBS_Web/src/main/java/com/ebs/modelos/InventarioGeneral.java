package com.ebs.modelos;

import com.ebs.entidades.Bodega;
import com.ebs.entidades.Inventario;
import com.ebs.entidades.Producto;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import lombok.Data;

/**
 *
 * @author Jorge Quesada
 */
@Data
public class InventarioGeneral {

    private Inventario inventario;

    private Bodega bodega;

    private Producto producto;

    private String fechaTexto;

    private String activoTexto;

    private String cantidadTexto;

    private List<SelectItem> listaActivos;
    
    private String razonInactividad;
    
    private Integer cantidadMinima;
        

    public InventarioGeneral() {
        this.inventario = null;
        this.bodega = null;
        this.producto = null;
        this.fechaTexto = "";
        this.activoTexto = "";
        this.cantidadTexto = "";
        this.listaActivos = new ArrayList<>();
        this.razonInactividad = "";
        this.cantidadMinima = null;
    }

    public InventarioGeneral(Inventario inventario, Bodega bodega, Producto producto) {
        DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        DecimalFormat formatoNumero = new DecimalFormat("###,###.###");
        this.inventario = inventario;
        this.bodega = bodega;
        this.producto = producto;
        this.fechaTexto = (inventario.getFechaVencimiento() != null ? formatoFecha.format(inventario.getFechaVencimiento()) : "");
        this.activoTexto = (inventario.getActivo().longValue() == 1l ? "Si" : "No");
        this.cantidadTexto = formatoNumero.format(inventario.getCantExistencia());
        this.listaActivos = obtenerSeleccionEstado();
        this.razonInactividad = inventario.getRazonInactivo();
        this.cantidadMinima = producto.getCantidad_minima();
    }

    private List<SelectItem> obtenerSeleccionEstado() {

        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(1, "Si"));
        lista.add(new SelectItem(0, "No"));
        return lista;
    }

    public void setLineaActivo(Long activo) {
        if (inventario != null) {
            inventario.setActivo(activo);
            activoTexto = (inventario.getActivo().longValue() == 1l ? "Si" : "No");
        } else {
            activoTexto = "";
        }
    }

    public Long getLineaActivo() {
        if (inventario != null) {
            return inventario.getActivo();
        } else {
            return null;
        }
    }
    
    public void setRazonTexto(String texto){
        if (inventario != null) {
            inventario.setRazonInactivo(texto);
            razonInactividad = inventario.getRazonInactivo();
        } else {
            razonInactividad = "";
        }
    }
    
    public String getRazonTexto() {
        if (inventario != null) {
            return inventario.getRazonInactivo();
        } else {
            return null;
        }
    }
    
    public Integer getMinimaTexto(){
        if (producto != null) {
            return producto.getCantidad_minima();
        } else {
            return null;
        }
    }
    
    public void setMinimaTexto(Integer numero){
        if (producto != null) {
            producto.setCantidad_minima(numero);
            cantidadMinima = producto.getCantidad_minima();
        } else {
            cantidadMinima = null;
        }
    }

    public void actualizarLinea(InventarioGeneral inventarioGeneral) {
        DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        DecimalFormat formatoNumero = new DecimalFormat("###,###.###");
        this.inventario = inventarioGeneral.getInventario();
        this.bodega = inventarioGeneral.getBodega();
        this.producto = inventarioGeneral.getProducto();
        this.fechaTexto = (inventario.getFechaVencimiento() != null ? formatoFecha.format(inventario.getFechaVencimiento()) : "");
        this.activoTexto = (inventario.getActivo().longValue() == 1l ? "Si" : "No");
        this.cantidadTexto = formatoNumero.format(inventario.getCantExistencia());
        this.listaActivos = obtenerSeleccionEstado();
        this.razonInactividad = inventarioGeneral.getRazonInactividad();
        this.cantidadMinima = producto.getCantidad_minima();
    }

}
