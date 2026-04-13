/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.modelos;

import java.util.List;

public class DetalleServicio {

    private List<LineaDetalle> LineaDetalle;

    public List<LineaDetalle> getLineaDetalle() {
        return LineaDetalle;
    }

    public void setLineaDetalle(List<LineaDetalle> LineaDetalle) {
        this.LineaDetalle = LineaDetalle;
    }

}
