package com.ebs.modelos;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * Fila liviana del listado de administración de notas de crédito.
 *
 * @author jdquesad
 */
@Data
public class ConsultaNotasCreditoModelo {

    private Long idAnulacion;
    private String numeroConsecutivo;
    private Date fechaAnulacion;
    private String fechaEmision;
    private Long idEstado;
    private String estado;
    private String usuario;
    private BigDecimal totalNotaCredito;
    private String consecutivoFactura;
    private String consecutivoNotaDebito;
}
