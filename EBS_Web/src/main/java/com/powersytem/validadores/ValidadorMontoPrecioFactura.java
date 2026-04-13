/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersytem.validadores;

import com.powersystem.utilitario.MensajeUtil;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Jorge GBSYS
 */
@FacesValidator("ValidadorMontoPrecioFactura")
public class ValidadorMontoPrecioFactura {

    public void validate(FacesContext fc, UIComponent uic, Object objeto) throws ValidatorException {
        /*  FacesMessage message = null;
        if (objeto != null) {
            if (!objeto.toString().equals("")) {
                Integer valor = (Integer) objeto;
                if (valor > 100d) {
                    message = MensajeUtil.crearFacesMessage(MensajeUtil.TipoMensaje.ADVERTENCIA, "El valor no debe superar el 100%");
                    throw new ValidatorException(message);
                }
            } else {
                message = MensajeUtil.crearFacesMessage(MensajeUtil.TipoMensaje.ADVERTENCIA, "Debe ingresar un valor");
                throw new ValidatorException(message);
            }
        }*/
    }

}
