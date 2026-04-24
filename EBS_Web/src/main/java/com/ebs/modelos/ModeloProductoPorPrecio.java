package com.ebs.modelos;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

public class ModeloProductoPorPrecio {

    @Getter
    @Setter
    private String nombreProducto;

    @Getter
    @Setter
    private BigDecimal precio;

    public ModeloProductoPorPrecio(String nombreProducto, BigDecimal precio) {
        this.nombreProducto = nombreProducto;
        this.precio = precio;
    }

    public ModeloProductoPorPrecio() {
    }
}
