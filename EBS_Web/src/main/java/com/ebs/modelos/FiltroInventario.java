/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jorge Quesada
 */
public class FiltroInventario {

    @Getter
    @Setter
    private Long idBodegaSeleccionada;

    @Getter
    @Setter
    private Long idActivoSeleccionado;

    @Getter
    @Setter
    private Long idProductoSeleccionado;

    @Getter
    @Setter
    private List<Long> listaIdsProductos;

    private String productoTitulo;

    public FiltroInventario(Long idBodegaSeleccionada, Long idActivoSeleccionado, Long idProductoSeleccionado) {
        this.idBodegaSeleccionada = idBodegaSeleccionada;
        this.idActivoSeleccionado = idActivoSeleccionado;
        this.idProductoSeleccionado = idProductoSeleccionado;
        this.listaIdsProductos = new ArrayList<>();
    }

    public FiltroInventario(Long idBodegaSeleccionada, Long idActivoSeleccionado, String productoTexto) {
        this.idBodegaSeleccionada = idBodegaSeleccionada;
        this.idActivoSeleccionado = idActivoSeleccionado;
        String[] valores = productoTexto.split("#");
        this.idProductoSeleccionado = Long.parseLong(valores[0]);
        this.listaIdsProductos = new ArrayList<>();
    }

    public FiltroInventario() {
        this.idBodegaSeleccionada = null;
        this.idActivoSeleccionado = null;
        this.idProductoSeleccionado = null;
        this.listaIdsProductos = new ArrayList<>();
    }

    public void setProductoTexto(String texto) {
        if (texto != null) {
            String[] valores = texto.split("#");
            this.idProductoSeleccionado = Long.parseLong(valores[0]);
            this.productoTitulo = valores[1];
        } else {
            this.idProductoSeleccionado = null;
            this.productoTitulo = null;
        }
    }

    public String getProductoTexto() {
        return this.productoTitulo;
    }

}
