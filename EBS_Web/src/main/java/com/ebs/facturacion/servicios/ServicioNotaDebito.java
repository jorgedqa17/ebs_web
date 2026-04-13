/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.facturacion.servicios;

import com.ebs.constantes.enums.EstadoAnulacion;
import com.ebs.constantes.enums.EstadoFactura;
import com.ebs.constantes.enums.EstadoNotaDebito;
import com.ebs.constantes.enums.EstadosLineasFactura;
import com.ebs.constantes.enums.LineaDetalleEstado;
import com.ebs.constantes.enums.SituacionComprobante;
import com.ebs.constantes.enums.TipoDocumento;
import com.ebs.entidades.AnulacionFactura;
import com.ebs.entidades.DetalleFactura;
import com.ebs.entidades.DetalleFacturaNotaDebito;
import com.ebs.entidades.Estados;
import com.ebs.entidades.Factura;
import com.ebs.entidades.FacturaDebito;
import com.ebs.entidades.FacturaNotaDebitoHistoricoHacienda;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.util.ServicioBase;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import lombok.extern.slf4j.Slf4j;
import com.ebs.modelos.NotaDebitoModelo;
import com.powersystem.utilitario.Utilitario;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.persistence.NoResultException;

/**
 *
 * @author jdquesad
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioNotaDebito extends ServicioBase {

    @Inject
    private ServicioFactura servicioFactura;

    public List<NotaDebitoModelo> obtenerNotasDebito() {
        List<NotaDebitoModelo> resultado = null;
        try {
            String sql = " SELECT nd FROM FacturaDebito nd WHERE nd.login = :login ";
            List<FacturaDebito> resultadoND = em.createQuery(sql, FacturaDebito.class)
                    .setParameter("login", Utilitario.obtenerUsuario().getLogin()).getResultList();

            NotaDebitoModelo modelo = null;
            for (FacturaDebito facturaDebito : resultadoND) {
                if (resultado == null) {
                    resultado = new ArrayList<>();
                }
                modelo = new NotaDebitoModelo();
                if (facturaDebito.getId_factura() != null) {
                    modelo.setFactura(servicioFactura.obtenerFacturaBusqueda(facturaDebito.getId_factura()));
                    modelo.getFactura().setEstadoFactura(this.obtenerEstado(Long.parseLong(modelo.getFactura().getEstado_factura().toString())));
                }
                if (facturaDebito.getId_anulacion() != null) {
                    modelo.setNotaCredito(servicioFactura.obtenerNotaCreditoBusqueda(facturaDebito.getId_anulacion()));
                    modelo.getNotaCredito().setEstadoFactura(this.obtenerEstado(modelo.getNotaCredito().getId_estado()));
                }
                facturaDebito.setEstadoFactura(this.obtenerEstado(facturaDebito.getId_estado()));
                modelo.setNotaDebito(facturaDebito);

                resultado.add(modelo);
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtenre.nota.debito",
                    "mensaje.error.obtenre.nota.debito");
        }

        return resultado;
    }

    public List<AnulacionFactura> obtenerNotasCredito() {
        List<AnulacionFactura> resultado = null;
        try {
            String sql = "SELECT nd FROM AnulacionFactura nd WHERE nd.id_estado NOT in( :id_estado1, :idEstado2) ORDER BY id_anulacion desc";
            List<AnulacionFactura> resultadoNC = em.createQuery(sql, AnulacionFactura.class)
                    .setParameter("id_estado1", EstadoAnulacion.ANULADA.getEstadoAnulacion())
                    .setParameter("idEstado2", EstadoAnulacion.PENDIENTE_DE_ENVIO_HACIENDA.getEstadoAnulacion()).getResultList();

            for (AnulacionFactura notaCredito : resultadoNC) {
                if (resultado == null) {
                    resultado = new ArrayList<>();
                }
                notaCredito.setEstadoFactura(this.obtenerEstado(notaCredito.getId_estado()));
                resultado.add(notaCredito);
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtenre.nota.credito.listar.notas.credito",
                    "mensaje.error.obtenre.nota.credito.listar.notas.credito");
        }

        return resultado;
    }

    public List<FacturaNotaDebitoHistoricoHacienda> obtenerListaHistoricoNotaDebito(Long idNotaDebito) {
        List<FacturaNotaDebitoHistoricoHacienda> resultado = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            StringBuilder consulta = new StringBuilder()
                    .append(" SELECT T1.id_nota_debito, T1.estado_factura, T1.fecha, T1.login, T1.respuesta,   ")
                    .append(" T1.detallerespuesta, T1.documento_xml_firmado, T1.codigo_respuesta, T2.DESCRIPCION  ")
                    .append(" FROM SEARMEDICA.FACTURA_NOTA_DEBITO_HISTORICO_HACIENDA T1   ")
                    .append(" INNER JOIN SEARMEDICA.ESTADOS T2  ")
                    .append(" ON T1.estado_factura = T2.ID_ESTADO  ")
                    .append(" WHERE T1.ID_NOTA_DEBITO = :idNotaDebito  ")
                    .append(" ORDER BY T1.FECHA DESC  ");

            List<Object[]> listaObjetos = (List<Object[]>) em.createNativeQuery(consulta.toString())
                    .setParameter("idNotaDebito", idNotaDebito)
                    .getResultList();

            FacturaNotaDebitoHistoricoHacienda historico = null;
            for (Object[] objeto : listaObjetos) {
                historico = new FacturaNotaDebitoHistoricoHacienda();
                if (resultado == null) {
                    resultado = new ArrayList<>();
                }
                historico.setCodigo_respuesta(objeto[7] == null ? null : Integer.parseInt(objeto[7].toString()));
                historico.setDetallerespuesta(objeto[5] == null ? null : objeto[5].toString());
                historico.setDocumento_xml_firmado(objeto[6] == null ? null : objeto[6].toString());
                historico.setEstado(objeto[8] == null ? null : objeto[8].toString());
                historico.setEstado_factura(objeto[1] == null ? null : Long.parseLong(objeto[1].toString()));
                historico.setFecha(objeto[2] == null ? null : df.parse(objeto[2].toString()));
                historico.setId_nota_debito(objeto[0] == null ? null : Long.parseLong(objeto[0].toString()));
                historico.setLogin(objeto[3] == null ? null : objeto[3].toString());
                historico.setRespuesta(objeto[4] == null ? null : objeto[4].toString());
                resultado.add(historico);
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.historico.nota.debito",
                    "mensaje.error.obtener.historico.nota.debito");
        }
        return resultado;
    }

    public Estados obtenerEstado(Long idEstado) {
        Estados resultado = null;
        try {
            String sql = "SELECT nd FROM Estados nd where nd.idEstado = :idEstado";
            resultado = em.createQuery(sql, Estados.class).setParameter("idEstado", idEstado).getSingleResult();
        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.estado",
                    "mensaje.error.estado");
        }
        return resultado;
    }

    public List<DetalleFactura> obtenerLineasDetalleNotaCredito(Long idNotaCredito) {
        List<DetalleFactura> resultado = null;
        try {
            String sql = "SELECT det FROM DetalleFacturaNotaCredito nd INNER JOIN DetalleFactura det on nd.numero_linea = det.detallePK.numero_linea and nd.id_producto = det.detallePK.id_producto and nd.id_factura = det.detallePK.id_factura where nd.id_anulacion = :idNotaCredito  ";
            resultado = em.createQuery(sql, DetalleFactura.class).setParameter("idNotaCredito", idNotaCredito).getResultList();
        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtner.detaelle.anulacion",
                    "mensaje.error.obtner.detaelle.anulacion");
        }
        return resultado;
    }

    public List<DetalleFactura> obtenerLineasDetalleNotaDebito(Long idNotaDebito) {
        List<DetalleFactura> resultado = null;
        try {
            String sql = "SELECT det FROM DetalleFacturaNotaDebito nd INNER JOIN DetalleFactura det on nd.numero_linea = det.detallePK.numero_linea and nd.id_producto = det.detallePK.id_producto and nd.id_factura = det.detallePK.id_factura where nd.id_nota_debito = :id_nota_debito  ";
            resultado = em.createQuery(sql, DetalleFactura.class).setParameter("id_nota_debito", idNotaDebito).getResultList();
        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtner.detaelle.nota.debito",
                    "mensaje.error.obtner.detaelle.nota.debito");
        }
        return resultado;
    }

    public Factura calcularMontosParaFacturaDespuesDeNotaDebito(Factura factura, FacturaDebito notaDebito) {

        factura.setTotal_descuentos(factura.getTotal_descuentos().add(notaDebito.getTotal_descuento()));
        factura.setTotal_impuestos(factura.getTotal_impuestos().add(notaDebito.getTotal_impuesto()));
        factura.setTotal_venta_neta(factura.getTotal_venta().add(notaDebito.getTotal_venta()));
        factura.setTotal_venta(factura.getTotal_venta_neta().add(notaDebito.getTotal_venta_neta()));
        factura.setTotal_servicios_grabados(factura.getTotal_servicios_grabados().add(notaDebito.getTotal_servicios_grabados()));
        factura.setTotal_servicios_exentos(factura.getTotal_servicios_exentos().add(notaDebito.getTotal_servicios_exentos()));
        factura.setTotal_mercancias_gravadas(factura.getTotal_mercancias_gravadas().add(notaDebito.getTotal_mercancias_gravadas()));
        factura.setTotal_mercancias_exentas(factura.getTotal_mercancias_exentas().add(notaDebito.getTotal_mercancias_exentas()));
        factura.setTotal_gravado(factura.getTotal_gravado().add(notaDebito.getTotal_gravado()));
        factura.setTotal_exento(factura.getTotal_exento().add(notaDebito.getTotal_exento()));
        factura.setTotal_comprobante(factura.getTotal_comprobante().add(notaDebito.getTotal_nota_debito()));
        return factura;
    }

    public FacturaDebito guardarNotaDebito(FacturaDebito notaDebito, AnulacionFactura notaCredito, Factura factura) {
        List<DetalleFactura> lineasNotaCredito = null;
        try {
            notaDebito.setFecha_nota_debito(fechaHoraBD());
            if (!notaDebito.getNota_debito_interna().equals(1)) {
                notaDebito.setNumero_consecutivo(this.servicioFactura.construirNumeroConsecutivoNotaDebito(TipoDocumento.NOTA_DE_DEBITO_ELECTRONICA.getTipoDocumento()));
                notaDebito.setClave(
                        this.servicioFactura.construirClaveNumerica(
                                notaDebito.getNumero_consecutivo(),
                                Utilitario.obtenerEmisor().getIdentificacion().getNumeroCedula(),
                                SituacionComprobante.SITUACION_NORMAL.getSituacion()
                        )
                );
                notaDebito.setId_estado(EstadoNotaDebito.PENDIENTE_ENVIO.getEstadoNotaCredito());
            } else {
                notaDebito.setId_estado(EstadoNotaDebito.NOTA_DEBITO_INTERNA.getEstadoNotaCredito());

            }

            //guardo la nota de debit
            guardar(notaDebito);
            //obtengo la lineas de la nota de credito
            lineasNotaCredito = this.obtenerLineasDetalleNotaCredito(notaCredito.getId_anulacion());
            //guardr y relaciono las lineas de la factura y de la nota de credito
            lineasNotaCredito.forEach(elemento -> {
                //guardo las lineas de la nota de debito
                guardar(new DetalleFacturaNotaDebito(elemento.getDetallePK().getNumero_linea(),
                        notaDebito.getId_nota_debito(), elemento.getDetallePK().getId_producto(), elemento.getDetallePK().getId_factura(), elemento));

                //actualizo los indicadores
                elemento.setEs_para_nota_credito(LineaDetalleEstado.PARA_NOTA_CREDITO_QUITAR.getEstadoLineaDetalle());
                elemento.setEs_para_nota_debito(LineaDetalleEstado.PARA_NOTA_DEBITO.getEstadoLineaDetalle());
                //actualizo el estado de las lineas
                elemento.setId_estado(EstadosLineasFactura.PENDIENTE_ENVIO.getEstadoLineaFactura());
                actualizar(elemento);
            });
            //anulo la nota de credito
            notaCredito.setId_estado(EstadoAnulacion.ANULADA.getEstadoAnulacion());
            actualizar(notaCredito);

            factura = this.calcularMontosParaFacturaDespuesDeNotaDebito(factura, notaDebito);
            //actualizo los montos de la factura 
            if (notaDebito.getNota_debito_interna().equals(1)) {
                factura.setEstado_factura(EstadoFactura.RECHAZADA_HACIENDA.getEstadoFactura());
            }
            actualizar(factura);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.factura.guardar.nota.debito",
                    "mensaje.error.factura.guardar.nota.debito");
        }
        return notaDebito;
    }

}
