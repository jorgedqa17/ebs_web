/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.cierre.servicio;

import com.ebs.entidades.Cierre;
import com.ebs.modelos.ConsultaCierre;
import com.powersystem.util.ServicioBase;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

/**
 *
 * @author sergio
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioCierre extends ServicioBase {

    private static final long serialVersionUID = 1L;

    public List<ConsultaCierre> obtenerListaCierre(Date fechaCierre, String usuario) {
        List<Cierre> resultado = new ArrayList<>();
        List<ConsultaCierre> lista = new ArrayList<>();
        StringBuilder hilera = new StringBuilder();
        DateFormat formato = new SimpleDateFormat("dd/mm/yyyy");
        
        hilera.append("SELECT ci.* FROM searmedica.CIERRE ci ");
        String filtro = fechaCierre == null ? usuario.isEmpty() ? "" : "WHERE LOWER(LOGIN) LIKE LOWER('%" + usuario + "%')"
                : " WHERE FECHA_GENERACION_CIERRE = to_date('" + formato.format(fechaCierre) + "', 'DD/MM/YYYY')";
        hilera.append(filtro);
        hilera.append(" ORDER BY FECHA_GENERACION_CIERRE");

        resultado = em.createNativeQuery(hilera.toString(), Cierre.class).getResultList();
        for(Cierre dato : resultado){
            lista.add(new ConsultaCierre(dato));
        }        
        return lista;
    }
    
    
}
