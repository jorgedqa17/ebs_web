/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import com.ebs.entidades.InventarioSolicDetElementos;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author
 */
@Data
public class ElementosSolicitudDetalle {

    private Long idElemento;

    private Long idInventarioSolicitudDetalle;

    private Date fechaVencimiento;

    private String fechaTexto;

    private String numeroLote;

    private Long cantidad;
    
    private String identificador;

    public ElementosSolicitudDetalle(Long idElemento,Long idInventarioSolicitudDetalle, Date fechaVencimiento, String numeroLote, Long cantidad) {
        this.idElemento = idElemento;
        this.idInventarioSolicitudDetalle = idInventarioSolicitudDetalle;
        this.fechaVencimiento = fechaVencimiento;
        this.numeroLote = numeroLote;
        this.cantidad = cantidad;
        this.fechaTexto = convertirFecha(fechaVencimiento);
    }
    
    public ElementosSolicitudDetalle(InventarioSolicDetElementos elemento){
         this.idElemento = elemento.getIdElemento();
        this.idInventarioSolicitudDetalle = elemento.getIdInventarioSolicitudDetalle();
        this.fechaVencimiento = elemento.getFechaVencimiento();
        this.numeroLote = elemento.getNumeroLote();
        this.cantidad = elemento.getCantidad();
        this.fechaTexto = convertirFecha(fechaVencimiento);
    }
    
    public ElementosSolicitudDetalle(Long idElemento,Long idInventarioSolicitudDetalle, Date fechaVencimiento, String numeroLote, Long cantidad,String identificador) {
        this.idElemento = idElemento;
        this.idInventarioSolicitudDetalle = idInventarioSolicitudDetalle;
        this.fechaVencimiento = fechaVencimiento;
        this.numeroLote = numeroLote;
        this.cantidad = cantidad;
        this.fechaTexto = convertirFecha(fechaVencimiento);
        this.identificador = identificador;
    }

    private String convertirFecha(Date fechaVencimiento) {
        DateFormat fechaFormato = new SimpleDateFormat("dd-MM-yyyy");
        return (this.fechaVencimiento == null ? "" :fechaFormato.format(fechaVencimiento));
    }   

}
