/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebs.inventario.servicio;

import com.ebs.bodegas.servicio.ServicioBodega;
import com.ebs.constantes.enums.EstadoInvSalidaDetalle;
import com.ebs.constantes.enums.EstadoSolicitud;
import com.ebs.constantes.enums.EstadoSolicitudDetalle;
import com.ebs.constantes.enums.IngresoTipo;
import com.ebs.constantes.enums.ParametrosEnum;
import com.ebs.constantes.enums.TipoFacturaEnum;
import com.ebs.constantes.enums.TiposPrecios;
import com.ebs.entidades.Bodega;
import com.ebs.entidades.Factura;
import com.ebs.entidades.Inventario;
import com.ebs.entidades.InventarioEliminacion;
import com.ebs.entidades.InventarioIngreso;
import com.ebs.entidades.InventarioIngresoDetalle;
import com.ebs.entidades.InventarioPrecioProducto;
import com.ebs.entidades.InventarioSalida;
import com.ebs.entidades.InventarioSalidaDetalle;
import com.ebs.entidades.InventarioSolicDetElementos;
import com.ebs.entidades.InventarioSolicitud;
import com.ebs.entidades.InventarioSolicitudDetalle;
import com.ebs.entidades.Parametro;
import com.ebs.entidades.ProductoPrecio;
import com.ebs.entidades.TipoPrecio;
import com.ebs.exception.ExcepcionManager;
import com.ebs.facturacion.servicios.ServicioFactura;
import com.ebs.modelos.DetalleSolicitud;
import com.ebs.modelos.ElementosSolicitudDetalle;
import com.ebs.modelos.EnviosSucursalesModelo;
import com.ebs.modelos.FiltroInventario;
import com.ebs.modelos.IngresoMateriales;
import com.ebs.modelos.InventarioCantidadMinimasModelo;
import com.ebs.modelos.InventarioGeneral;
import com.ebs.modelos.TrazabilidadModelo;
import com.ebs.parametros.servicios.ServicioParametro;
import com.powersystem.util.ServicioBase;
import com.powersystem.utilitario.JSFUtil;
import com.powersystem.utilitario.Utilitario;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioInventario extends ServicioBase {

    private static final long serialVersionUID = 1L;

    @Inject
    private ServicioFactura servicioFactura;

    @Inject
    private ServicioParametro servicioParametro;

    @Inject
    private ServicioBodega servicioBodega;

    /**
     * Método que indica si una bodega tiene o no
     *
     * @param idBodega
     * @return (True = Existen productos con minimo de existencia al limite o
     * menores) (False = No Existen productos con minimo de existencia al limite
     * o menores)
     */
    public Integer verificarExistenciaInventario(Long idBodega) {
        Integer resultado = 0;
        try {
            StringBuilder sql = new StringBuilder()
                    .append(" SELECT COUNT(1) FROM ( ")
                    .append(" SELECT T1.ID_PRODUCTO,T1.ID_BODEGA, SUM(T1.CANTEXISTENCIA) AS TOTAL_EXISTENCIA ")
                    .append(" FROM SEARMEDICA.INVENTARIO T1 INNER JOIN SEARMEDICA.PRODUCTO T2  ")
                    .append(" ON T1.ID_PRODUCTO = T2.ID_PRODUCTO ")
                    .append(" WHERE T1.ACTIVO = 1 ")
                    .append(" AND T1.ID_BODEGA = :idBodega ")
                    .append(" GROUP  BY T1.ID_PRODUCTO,T1.ID_BODEGA ) TB INNER JOIN SEARMEDICA.PRODUCTO T3 ")
                    .append(" ON TB.ID_PRODUCTO = T3.ID_PRODUCTO  ")
                    .append(" AND TB.TOTAL_EXISTENCIA < T3.CANTIDAD_MINIMA ");

            Object objeto = (Object) em.createNativeQuery(sql.toString()).setParameter("idBodega", idBodega).getSingleResult();
            if (objeto != null) {
                if (objeto instanceof BigInteger) {
                    resultado = Integer.parseInt(objeto.toString());
                }
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.inventario.usuario",
                    "mensaje.error.inventario.desarrollador");
        }
        return resultado;
    }

    public List<InventarioCantidadMinimasModelo> obtenerListaProductosExistenciaActual(Long idBodega) {
        List<InventarioCantidadMinimasModelo> resultado = null;
        try {
            String sql
                    = " SELECT T3.ID_PRODUCTO, T3.DESCRIPCION, T3.CANTIDAD_MINIMA, TB.TOTAL_EXISTENCIA FROM (  "
                    + " SELECT T1.ID_PRODUCTO,T1.ID_BODEGA, SUM(T1.CANTEXISTENCIA) AS TOTAL_EXISTENCIA  "
                    + " FROM SEARMEDICA.INVENTARIO T1 INNER JOIN SEARMEDICA.PRODUCTO T2   "
                    + " ON T1.ID_PRODUCTO = T2.ID_PRODUCTO  "
                    + " WHERE T1.ACTIVO = 1  "
                    + " AND T1.ID_BODEGA = :idBodega "
                    + " GROUP  BY T1.ID_PRODUCTO,T1.ID_BODEGA ) TB INNER JOIN SEARMEDICA.PRODUCTO T3  "
                    + " ON TB.ID_PRODUCTO = T3.ID_PRODUCTO   "
                    + " AND TB.TOTAL_EXISTENCIA < T3.CANTIDAD_MINIMA ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).setParameter("idBodega", idBodega).getResultList();

            InventarioCantidadMinimasModelo modelo = null;
            resultado = new ArrayList<>();

            for (Object[] fila : lista) {
                modelo = new InventarioCantidadMinimasModelo();

                modelo.setIdProducto(Long.parseLong(fila[0].toString()));
                modelo.setDescripcion(fila[1].toString());
                modelo.setCantidadMinima(Long.parseLong(fila[2].toString()));
                modelo.setCantidadExistenciaActual(Long.parseLong(fila[3].toString()));

                resultado.add(modelo);
            }
        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.inventario.usuario",
                    "mensaje.error.inventario.desarrollador");
        }
        return resultado;
    }

    /*
    
SELECT T3.ID_PRODUCTO, T3.DESCRIPCIONT3.CANTIDAD_MINIMA, TB.TOTAL_EXISTENCIA FROM ( 
SELECT T1.ID_PRODUCTO,T1.ID_BODEGA, SUM(T1.CANTEXISTENCIA) AS TOTAL_EXISTENCIA 
FROM SEARMEDICA.INVENTARIO T1 INNER JOIN SEARMEDICA.PRODUCTO T2  
ON T1.ID_PRODUCTO = T2.ID_PRODUCTO 
WHERE T1.ACTIVO = 1 
AND T1.ID_BODEGA = 1
GROUP  BY T1.ID_PRODUCTO,T1.ID_BODEGA ) TB INNER JOIN SEARMEDICA.PRODUCTO T3 
ON TB.ID_PRODUCTO = T3.ID_PRODUCTO  
AND TB.TOTAL_EXISTENCIA < T3.CANTIDAD_MINIMA
     */
    public List<EnviosSucursalesModelo> obtenerListaProductosAEnviar(String fecha, String fecha2, String idBodegas) {
        List<EnviosSucursalesModelo> listaResultado = null;
        EnviosSucursalesModelo envio = null;
        try {
//            String sql
//                    = " SELECT t1.login, t3.descripcion, sum(t2.cantidad) FROM searmedica.factura t1 inner join searmedica.detalle_factura t2 "
//                    + " on t1.id_factura = t2.id_factura inner join searmedica.producto t3 "
//                    + " on t2.id_producto = t3.id_producto "
//                    + " where to_char(t1.fecha_factura,'dd/mm/yyyy') = :fecha and t1.id_tipo_factura <> " + TipoFacturaEnum.COTIZACION.getIdTipoFactura()
//                    + " group by t1.login, t3.descripcion ";
            String sql = " SELECT t1.login, t3.descripcion, sum(t2.cantidad) FROM searmedica.factura t1 inner join searmedica.detalle_factura t2 "
                    + " on t1.id_factura = t2.id_factura inner join searmedica.producto t3 "
                    + " on t2.id_producto = t3.id_producto inner join searmedica.usuario t4 "
                    + " on t1.login = t4.login  "
                    + " where to_date(to_char(t1.fecha_factura,'dd/mm/yyyy'),'dd/mm/yyyy') between to_date('" + fecha + "','DD/MM/YYYY')  and to_date( '" + fecha2 + "','DD/MM/YYYY') "
                    + " and t1.id_bodega in ( " + idBodegas + " ) "
                    + " and t1.numero_consecutivo is not null "
                    + " and t4.es_usuario_bodega  = 1 "
                    + " group by t1.login, t3.descripcion ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql).getResultList();

            envio = new EnviosSucursalesModelo();
            listaResultado = new ArrayList<>();

            for (Object[] fila : lista) {
                envio.setLogin(fila[0].toString());
                envio.setProducto(fila[1].toString());
                envio.setCantidad(Integer.parseInt(fila[2].toString()));
                listaResultado.add(envio);
                envio = new EnviosSucursalesModelo();
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.envios.diarios",
                    "mensaje.obtener.envios.diarios.desarrollador");
        }
        return listaResultado;
    }

    /**
     * Método que obtiene la lista de elementos de trazabilidad de una lista de
     * productos y de una bodega dada
     *
     * @param idBodega Long
     * @param ids String (1,2,3,4)
     * @return List<TrazabilidadModelo>
     */
    public List<TrazabilidadModelo> obtenerTrazabilidad(Long idBodega, String ids, String fechaInicio, String fechaFinal) {
        List<TrazabilidadModelo> resultado = null;
        try {

            StringBuilder sql = new StringBuilder()
                    .append(" SELECT TB.*, T5.DESCRIPCION FROM (SELECT ")
                    .append(" 01 AS ORDENAMIENTO, 'INGRESO' AS TIPO_GESTION,  T1.ID_PRODUCTO, T1.CANTIDAD, TO_DATE(TO_CHAR(T1.FECHAREGISTRO,'DD/MM/YYYY'),'DD/MM/YYYY') AS FECHAREGISTRO,  ")
                    .append(" T2.LOGIN, T3.DESCRIPCION AS BODEGA_ORIGEN, '' AS BODEGA_DESTINO, UPPER(T4.DESCRIPCION) AS TIPO, ''  AS ID_FACTURA  ")
                    .append(" FROM SEARMEDICA.INVENTARIO_INGRESO_DETALLE T1         ")
                    .append(" INNER JOIN SEARMEDICA.INVENTARIO_INGRESO T2   ")
                    .append(" ON T1.ID_INVENTARIO_INGRESO = T2.ID_INVENTARIO_INGRESO    ")
                    .append(" LEFT JOIN SEARMEDICA.BODEGA T3  ")
                    .append(" ON T2.ID_BODEGA_ORIGEN = T3.ID_BODEGA  ")
                    .append(" LEFT JOIN SEARMEDICA.INVENTARIO_INGRESO_TIPO T4  ")
                    .append(" ON T2.ID_INVENTARIO_INGRESO_TIPO = T4.ID_INVENTARIO_INGRESO_TIPO  ")
                    .append(" WHERE T1.ID_PRODUCTO IN ").append("(").append(ids).append(") ")
                    .append(" UNION ALL  ")
                    .append(" SELECT   ")
                    .append(" 02 AS ORDENAMIENTO, 'SOLICITUD' AS TIPO_GESTION,  T1.ID_PRODUCTO, T1.CANTIDAD, TO_DATE(TO_CHAR(T2.FECHAREGISTRO,'DD/MM/YYYY'),'DD/MM/YYYY') AS FECHAREGISTRO,  ")
                    .append(" T2.LOGIN, T3.DESCRIPCION AS BODEGA_ORIGEN, T4.DESCRIPCION AS BODEGA_DESTINO, '' AS TIPO, ''  AS ID_FACTURA   ")
                    .append(" FROM SEARMEDICA.INVENTARIO_SOLICITUD_DETALLE T1         ")
                    .append(" INNER JOIN SEARMEDICA.INVENTARIO_SOLICITUD T2   ")
                    .append(" ON T1.ID_INVENTARIO_SOLICITUD = T2.ID_INVENTARIO_SOLICITUD    ")
                    .append(" LEFT JOIN SEARMEDICA.BODEGA T3  ")
                    .append(" ON T2.ID_BODEGA_ORIGEN = T3.ID_BODEGA  ")
                    .append(" LEFT JOIN SEARMEDICA.BODEGA T4  ")
                    .append(" ON T1.ID_BODEGA_DESTINO = T4.ID_BODEGA  ")
                    .append(" WHERE T1.ID_PRODUCTO IN ").append("(").append(ids).append(") ")
                    .append(" UNION ALL   ")
                    .append(" SELECT  ")
                    .append(" 03 AS ORDENAMIENTO, 'SALIDA' AS TIPO_GESTION, T1.ID_PRODUCTO, T1.CANTIDAD, TO_DATE(TO_CHAR(T1.FECHAREGISTRO,'DD/MM/YYYY'),'DD/MM/YYYY') AS FECHAREGISTRO, ")
                    .append(" T2.LOGIN, T3.DESCRIPCION AS BODEGA_ORIGEN, '' AS BODEGA_DESTINO, ")
                    .append(" (CASE ")
                    .append("  WHEN T2.ID_INVENTARIO_SOLICITUD IS NOT NULL THEN 'POR SOLICITUD' ")
                    .append("  WHEN T1.ID_FACTURA IS NOT NULL THEN 'POR FACTURA' ")
                    .append("  END )  AS TIPO, (T4.ID_FACTURA||' / '||T4.NUMERO_CONSECUTIVO) AS ID_FACTURA ")
                    .append(" FROM SEARMEDICA.INVENTARIO_SALIDA_DETALLE T1 INNER JOIN SEARMEDICA.INVENTARIO_SALIDA T2 ")
                    .append(" ON T1.ID_INVENTARIO_SALIDA = T2.ID_INVENTARIO_SALIDA LEFT JOIN SEARMEDICA.BODEGA T3 ")
                    .append(" ON T1.ID_BODEGA_DESTINO = T3.ID_BODEGA LEFT JOIN SEARMEDICA.FACTURA T4")
                    .append(" ON T1.ID_FACTURA = T4.ID_FACTURA")
                    .append(" WHERE T1.ID_PRODUCTO IN ").append("(").append(ids).append(")")
                    .append(" UNION ALL ")
                    .append(" SELECT 04 AS ORDENAMIENTO ,T1.gestion AS TIPO_GESTION,T1.id_producto, T1.cantidad, T1.fecha AS FECHAREGISTRO,  ")
                    .append(" T1.login as LOGIN, T2.DESCRIPCION AS BODEGA_ORIGEN, T3.DESCRIPCION AS BODEGA_DESTINO,T1.tipo AS TIPO, ")
                    .append(" T1.numero_consecutivo AS ID_FACTURA ")
                    .append(" FROM searmedica.trazabilidad_producto T1 LEFT JOIN SEARMEDICA.BODEGA T2 ")
                    .append(" ON T1.ID_BODEGA_DESTINO = T2.ID_BODEGA LEFT JOIN  SEARMEDICA.BODEGA T3 ")
                    .append(" ON T1.ID_BODEGA_DESTINO = T3.ID_BODEGA")
                    .append(" WHERE T1.ID_PRODUCTO IN ").append("(").append(ids).append(")")
                    .append(") ")
                    .append(" TB LEFT JOIN SEARMEDICA.PRODUCTO T5 ")
                    .append(" ON TB.ID_PRODUCTO = T5.ID_PRODUCTO ")
                    .append("  WHERE TO_DATE(TO_CHAR( TB.FECHAREGISTRO,'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN TO_DATE(:fechaInicio,'DD/MM/YYYY') AND TO_DATE( :fechaFinal,'DD/MM/YYYY') ");

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql.toString())
                    .setParameter("fechaInicio", fechaInicio)
                    .setParameter("fechaFinal", fechaFinal)
                    .getResultList();

            TrazabilidadModelo trazabilidad = null;
            resultado = new ArrayList<>();

            for (Object[] fila : lista) {
                trazabilidad = new TrazabilidadModelo();
                trazabilidad.setTipoGestion(fila[1] == null ? "" : fila[1].toString());
                trazabilidad.setCantidad(fila[3] == null ? 0 : Integer.parseInt(fila[3].toString()));
                trazabilidad.setFechaRegistro(fila[4] == null ? null : fila[4].toString());
                trazabilidad.setLogin(fila[5] == null ? "" : fila[5].toString());
                trazabilidad.setBodegaOrigen(fila[6] == null ? "" : fila[6].toString());
                trazabilidad.setBodegaDestino(fila[7] == null ? "" : fila[7].toString());
                trazabilidad.setTipo(fila[8] == null ? "" : fila[8].toString());
                trazabilidad.setNumeroConsecutivoFactura(fila[9] == null ? "" : fila[9].toString());
                trazabilidad.setDescripcionProducto(fila[10] == null ? "" : fila[10].toString());
                resultado.add(trazabilidad);
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mesaje.error.obtener.trazabilidad",
                    "mesaje.error.obtener.trazabilidad.desarrollador");
        }
        return resultado;
    }

    public void devolverSalidasInventario(List<InventarioSalidaDetalle> listaSalidas, List<Inventario> listaInventario) {
        try {
            for (InventarioSalidaDetalle salidaDetalle : listaSalidas) {
                eliminar(salidaDetalle);
            }
            for (Inventario inventario : listaInventario) {
                actualizar(inventario);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.devolver.inventario",
                    "mensaje.error.devolver.inventario");
        }
    }

    public List<InventarioSalidaDetalle> guardarInventarioSalidaDetalle(List<InventarioSalidaDetalle> listaSalidas, List<Inventario> listaInventario) {
        try {
            for (InventarioSalidaDetalle salidaDetalle : listaSalidas) {
                salidaDetalle.setFechaRegistro(fechaHoraBD());
                guardar(salidaDetalle);
                //this.servicioFactura.guardarHijoControlInventario(salidaDetalle.getIdInventarioSalida(), salidaDetalle.getIdInventarioSalidaDetalle());
            }
            for (Inventario inventario : listaInventario) {
                actualizar(inventario);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensajae.error.guarda.invenatio.salida.detalle",
                    "mensajae.error.guarda.invenatio.salida.detalle.desarrollador");
        }
        return listaSalidas;
    }

    public Inventario consultarProductoInventPorBodega(Inventario inventario) {
        Inventario resultado;
        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try {
            StringBuilder hilera = new StringBuilder();
            hilera.append("select inv.* from searmedica.inventario inv where inv.id_bodega =");
            hilera.append(inventario.getIdBodega());
            hilera.append(" and inv.id_producto =");
            hilera.append(inventario.getIdProducto());
            hilera.append(" and inv.fechavencimiento ");
            if (inventario.getFechaVencimiento() != null) {
                hilera.append(" = to_date('");
                hilera.append(formato.format(inventario.getFechaVencimiento()));
                hilera.append("','DD/MM/YYYY') ");
            } else {
                hilera.append(" is null");
            }
            if (inventario.getNumeroLote() != null) {
                hilera.append(" and inv.numerolote = upper('");
                hilera.append(inventario.getNumeroLote().trim());
                hilera.append("')");
            } else {
                hilera.append(" and inv.numerolote is null");
            }

            Query consulta = em.createNativeQuery(hilera.toString(), Inventario.class);
            resultado = (Inventario) consulta.getSingleResult();
        } catch (NoResultException e) {
            resultado = inventario;
        }

        return resultado;
    }

    public void insertarInventarioIngreso(InventarioIngreso inventarioIngreso, List<InventarioIngresoDetalle> listaIngresoDetalle) throws PersistenceException {
        IngresoMateriales ingreso = new IngresoMateriales(inventarioIngreso, listaIngresoDetalle);
        insertarInventarioIngreso(ingreso);
    }

    public void insertarInventarioIngreso(IngresoMateriales ingreso) throws PersistenceException {

        if (ingreso.getInventarioIngreso().getIdInventarioIngreso() == null) {
            //Se inserta el inventario ingreso
            guardar(ingreso.getInventarioIngreso());
        } else {
            //Se actualiza el inventario ingreso
            actualizar(ingreso.getInventarioIngreso());
        }

        for (InventarioIngresoDetalle p : ingreso.getDetalles()) {

            /**
             * ************ Se verifica si existe el producto en inventario
             * ************************
             */
            /**
             * ************ de no estar se incluye nuevo sino se incrementa su
             * existencia ********
             */
            Inventario temporal = new Inventario(ingreso.getInventarioIngreso().getIdBodegaOrigen(), p.getIdProducto(),
                    p.getCantidad(), p.getFechaVencimiento(), p.getNumeroLote(), null, null, null, null, 1l, null);

            temporal = consultarProductoInventPorBodega(temporal);

            //Si la PK es nula se crea el producto
            if (temporal.getIdInventario() == null) {
                guardar(temporal);
            } else {//si la PK es distinta a nulo se incrementa la existencia
                long cantidadActual = temporal.getCantExistencia() + p.getCantidad();
                temporal.setCantExistencia(cantidadActual);
                temporal.setActivo(1l);
                actualizar(temporal);
            }

            /**
             * ******************** Se crea la linea de ingreso detalle
             * ***********
             */
            p.setIdInventario(temporal.getIdInventario());
            p.setIdInventarioIngreso(ingreso.getInventarioIngreso().getIdInventarioIngreso());
            guardar(p);

            InventarioPrecioProducto precio = new InventarioPrecioProducto();
            precio.setFechaRegistro(JSFUtil.obtenerFechaActual());
            precio.setId_inventario(p.getIdInventario());
            precio.setId_producto(p.getIdProducto());
            precio.setLogin(ingreso.getInventarioIngreso().getLogin());
            precio.setPrecio(p.getPrecio());
            precio.setId_bodega(ingreso.getInventarioIngreso().getIdBodegaOrigen());

            guardar(precio);

            this.modificarPrecioProducto(p.getIdProducto(), p.getPrecio(), TiposPrecios.PRECIO_BASE);

        }

    }

    public BigDecimal obtenerUltimoPrecioIngresadoProductoInventario(Long idProducto) {
        BigDecimal result = new BigDecimal("0.0");

        try {
            String sql = " select  max(fecha) fecha, id_inventario, id_producto, precio,id_bodega, id_precio_inventario, login "
                    + " FROM searmedica.inventario_producto_precio  "
                    + " where precio is not null and id_producto =  " + idProducto
                    + " group by id_inventario, id_producto, precio, fecha, id_bodega, id_precio_inventario, login "
                    + " order by fecha desc "
                    + " limit 1 ";

            Object[] object = (Object[]) em.createNativeQuery(sql.toString()).getSingleResult();

            if (object != null) {
                if (object[3] != null) {
                    result = new BigDecimal(object[3].toString());
                }
            }

        } catch (NoResultException nex) {
            //nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.inventario",
                    "mensaje.error.obtener.inventario");
        }
        return result;
    }

    public String obtenerUltimoProovedorIngresadoProductoInventario(Long idProducto) {
        String result = "";

        try {
            String sql = " SELECT  proveedor "
                    + " FROM searmedica.inventario_ingreso_detalle "
                    + " where proveedor is not null and id_producto = " + idProducto
                    + " order by fecharegistro desc "
                    + " limit 1 ";

            Object object = (Object) em.createNativeQuery(sql).getSingleResult();

            if (object != null) {
                result = object.toString();
            }

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.inventario",
                    "mensaje.error.obtener.inventario");
        }
        return result;
    }

    public void modificarPrecioProducto(Long idProducto, BigDecimal precio, TiposPrecios tipoPrecio) {
        ProductoPrecio productoPrecio = null;
        try {
            String hql = "SELECT prod FROM ProductoPrecio prod WHERE prod.productoPrecioPK.id_producto = :idProducto AND prod.productoPrecioPK.id_tipo = :idTipo";

            productoPrecio = em.createQuery(hql, ProductoPrecio.class).setParameter("idProducto", idProducto).setParameter("idTipo", tipoPrecio.getTipoPrecio()).getSingleResult();
            productoPrecio.setPrecio(precio);
            actualizar(productoPrecio);
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.inventario",
                    "mensaje.error.obtener.inventario");
        }
    }

    public List<InventarioIngreso> obtenerIngresoInventario() {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select i.* from searmedica.inventario_ingreso i order by i.fecharegistro DESC");
        Query consulta = em.createNativeQuery(hilera.toString(), InventarioIngreso.class);
        return consulta.getResultList();
    }

    public List<InventarioIngresoDetalle> obtenerDetalleIngresoInventario(Long idInventarioIngreso) {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select d.* from searmedica.inventario_ingreso_detalle d where d.id_inventario_ingreso =");
        hilera.append(idInventarioIngreso);
        Query consulta = em.createNativeQuery(hilera.toString(), InventarioIngresoDetalle.class);
        return consulta.getResultList();
    }

    public void insertarSolicitudes(InventarioSolicitud inventarioSolicitud, List<InventarioSolicitudDetalle> inventarioSolicitudDetalle) {

        //Se guarda la nueva solicitud
        guardar(inventarioSolicitud);

        //Se guardan las solicitudes detalles
        inventarioSolicitudDetalle.forEach((p) -> {
            p.setIdInventarioSolicitud(inventarioSolicitud.getIdInventarioSolicitud());
            guardar(p);
        });
    }

    public List<InventarioSolicitud> obtenerEncabezadoSolicitud(Long idBodegaDestino, Long idEstado, int tipoConsulta, Long... estadosSolicitudExcluidos) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select distinct en.* from SearMedica.inventario_solicitud en, SearMedica.inventario_solicitud_detalle de ");
        hilera.append(" where en.id_inventario_solicitud = de.id_inventario_solicitud");
        hilera.append((tipoConsulta == 1 ? " and de.id_bodega_destino = " : " and en.id_bodega_origen = "));
        hilera.append(idBodegaDestino);
        hilera.append(" and de.estado =");
        hilera.append(idEstado);
        if (estadosSolicitudExcluidos.length > 0) {
            hilera.append(" and en.estado not in (");
            for (Long temporal : estadosSolicitudExcluidos) {
                hilera.append(temporal.longValue()).append(",");
            }
            //Valida si tiene una coma. De ser si se la quita
            hilera.setLength(String.valueOf(hilera.charAt(hilera.length() - 1)).equals(",") ? hilera.length() - 1 : hilera.length());
            hilera.append(") ");
        }
        hilera.append(" order by en.fecharegistro asc");

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSolicitud.class);
        return consulta.getResultList();
    }

    public List<InventarioSolicitud> obtenerEncabezadoSolicitud(Long idBodegaOrigen, Long idEstado) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select distinct en.* from SearMedica.inventario_solicitud en, SearMedica.inventario_solicitud_detalle de ");
        hilera.append(" where en.id_inventario_solicitud = de.id_inventario_solicitud");
        hilera.append(" and en.id_bodega_origen = ");
        hilera.append(idBodegaOrigen);
        hilera.append(" and de.estado =");
        hilera.append(idEstado);
        hilera.append(" order by en.fecharegistro asc");

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSolicitud.class);
        return consulta.getResultList();
    }

    public List<InventarioSolicitudDetalle> obtenerDetalleEncabSolicitudEstado(Long idInventarioSolicitud, Long idEstado) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select de.* from SearMedica.inventario_solicitud_detalle de ");
        hilera.append(" where de.id_inventario_solicitud =");
        hilera.append(idInventarioSolicitud);
        hilera.append(" and de.estado = ");
        hilera.append(idEstado);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSolicitudDetalle.class);
        return consulta.getResultList();
    }

    /**
     * Método que obtiene un inventario pr el id
     *
     * @param idInventario
     * @return Inventario
     */
    public Inventario obtenerInventarioPorId(Long idInventario) {
        Inventario inventario = null;
        try {
            String hql = "SELECT inv FROM Inventario inv WHERE inv.idInventario = :idInventario";

            inventario = em.createQuery(hql, Inventario.class).setParameter("idInventario", idInventario).getSingleResult();

        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.inventario",
                    "mensaje.error.obtener.inventario");
        }

        return inventario;
    }

    public List<InventarioSolicitudDetalle> obtenerInvSolicDetalle(Long idInventarioSolicitud) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select de.* from SearMedica.inventario_solicitud_detalle de ");
        hilera.append(" where de.id_inventario_solicitud =");
        hilera.append(idInventarioSolicitud);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSolicitudDetalle.class);
        return consulta.getResultList();
    }

    public InventarioSolicitud obtenerInventarioSolicitud(Long idInventarioSolicitud) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select so.* from SearMedica.inventario_solicitud so ");
        hilera.append(" where so.id_inventario_solicitud =");
        hilera.append(idInventarioSolicitud);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSolicitud.class);
        try {
            return (InventarioSolicitud) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Inventario> consultarInventProducto(Long idBodega, Long idProducto, boolean activo) {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select inv.* from searmedica.inventario inv where inv.id_bodega =");
        hilera.append(idBodega);
        hilera.append("  and inv.id_producto =");
        hilera.append(idProducto);
        hilera.append(" and inv.activo =");
        hilera.append((activo ? "1" : "0"));
        hilera.append(" order by inv.fechavencimiento asc");

        Query consulta = em.createNativeQuery(hilera.toString(), Inventario.class);
        return consulta.getResultList();
    }

    public List<Inventario> consultarInventProductoPorCantidad(Long idBodega, Long idProducto, Long cantidad, boolean activo) {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select inv.* from searmedica.inventario inv where inv.id_bodega =");
        hilera.append(idBodega);
        hilera.append("  and inv.id_producto =");
        hilera.append(idProducto);
        hilera.append(" and inv.activo =");
        hilera.append((activo ? "1" : "0"));
        hilera.append(" order by inv.fechavencimiento asc");

        Query consulta = em.createNativeQuery(hilera.toString(), Inventario.class);
        return consulta.getResultList();
    }

    public Object[] obtenerDetalleSolicitud(Long idInvSolicDetalle) {

        Object[] resultado = null;
        try {
            StringBuilder hilera = new StringBuilder();
            hilera.append("select deta.* from SearMedica.inventario_solicitud_detalle deta where deta.id_inventario_solicitud_detalle =");
            hilera.append(idInvSolicDetalle);

            Query consulta = em.createNativeQuery(hilera.toString(), InventarioSolicitudDetalle.class);
            InventarioSolicitudDetalle solicitudDetalle = (InventarioSolicitudDetalle) consulta.getSingleResult();

            StringBuilder hilera2 = new StringBuilder();
            hilera2.append("select ele.* from SearMedica.inventario_solic_det_elementos ele where ele.id_inventario_solicitud_detalle =");
            hilera2.append(idInvSolicDetalle);

            Query consulta2 = em.createNativeQuery(hilera2.toString(), InventarioSolicDetElementos.class);
            List<InventarioSolicDetElementos> elementos = consulta2.getResultList();

            resultado = new Object[]{solicitudDetalle, elementos};

        } catch (NoResultException no) {
            resultado = null;
        }

        return resultado;
    }

    public List<InventarioSolicDetElementos> obtenerElementosDetalle(Long idInvSolicDetalle) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select ele.* from SearMedica.inventario_solic_det_elementos ele where ele.id_inventario_solicitud_detalle =");
        hilera.append(idInvSolicDetalle);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSolicDetElementos.class);
        return consulta.getResultList();
    }

    public void actualizarLineas(List<?> lista) {

        //Se actualiza el estado del detalle de la solicitud
        lista.forEach((p) -> {
            actualizar(p);
        });

    }

    public void actualizarLinea(Object linea) {
        //Se actualiza el estado del detalle de la solicitud
        actualizar(linea);
    }

    public void crearReservaTraslado(Long idBodegaOrigen, InventarioSolicitudDetalle invSolicDetalle, List<InventarioSolicDetElementos> elementos) {

        //Se guardan las nuevas caracteristicas en la solicitud
        elementos.forEach((q -> {
            guardar(q);
        }));

        //Se pregunta si no existe el inventario salida
        InventarioSalida inventarioSalida = obtenerInvSalidaFromInvSolicitud(invSolicDetalle.getIdInventarioSolicitud());
        if (inventarioSalida == null) {
            InventarioSalida nuevaSalida = new InventarioSalida(idBodegaOrigen, JSFUtil.obtenerFechaActual(), Utilitario.obtenerUsuario().getLogin(), invSolicDetalle.getIdInventarioSolicitud());
            guardar(nuevaSalida);
            inventarioSalida = nuevaSalida;
        }

        //Se construyen las lineas del inventario salida detalle y reduccion del inventario
        for (InventarioSolicDetElementos r : elementos) {

            //Se crea las lineas de inventario salida detalle
            InventarioSalidaDetalle inventarioSalidaDetalle = new InventarioSalidaDetalle(invSolicDetalle.getIdBodegaDestino(), invSolicDetalle.getIdProducto(), r.getCantidad(),
                    inventarioSalida.getIdInventarioSalida(), null, invSolicDetalle.getIdInventarioSolicitudDetalle(), EstadoInvSalidaDetalle.PENDIENTE.getId(), JSFUtil.obtenerFechaActual(),
                    r.getNumeroLote(), r.getFechaVencimiento());
            //Guarda la linea de detalle de salida
            guardar(inventarioSalidaDetalle);

            //Se reduce la linea de inventario salida detalle del inventario
            //Se construye la linea del inventario
            Inventario temporal = new Inventario(inventarioSalida.getIdBodegaOrigen(), inventarioSalidaDetalle.getIdProducto(), inventarioSalidaDetalle.getFechaVencimiento(),
                    inventarioSalidaDetalle.getNumeroLote());
            //Se busca la linea del inventario
            Inventario inventario = consultarProductoInventPorBodega(temporal);

            //Se realiza la reduccion en el inventario
            Long cantidad = inventario.getCantExistencia();
            cantidad -= inventarioSalidaDetalle.getCantidad();
            inventario.setCantExistencia(cantidad);
            if (cantidad == 0) {
                inventario.setActivo(0l);
            }
            //Analiza la forma de registrar el inventario            
            actualizar(inventario);

        }

    }

    public InventarioSalida obtenerInvSalidaFromInvSolicitud(Long idInventarioSolicitud) {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select sa.* from searmedica.inventario_salida sa where sa.id_inventario_solicitud = ");
        hilera.append(idInventarioSolicitud);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSalida.class);
        try {
            return (InventarioSalida) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public InventarioSalida obtenerInvSalidaFromID(Long idInventarioSalida) {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select sa.* from searmedica.inventario_salida sa where sa.id_inventario_salida = ");
        hilera.append(idInventarioSalida);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSalida.class);
        try {
            return (InventarioSalida) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void crearRetornoTraslado(Long idBodegaOrigen, InventarioSolicitudDetalle invSolicDetalle) {

        //Se obtiene la lista de inventario salida detalle a partir del inventario solicitud detalle
        List<InventarioSalidaDetalle> invSalidaDetalles = obtenerInvSaliDetalleFromInvSolicDetalle(invSolicDetalle.getIdInventarioSolicitudDetalle());
        for (InventarioSalidaDetalle p : invSalidaDetalles) {

            //Se obtiene el inventario asociado a cada linea del inventario salida detalle
            //Se construye la entidad inventario
            Inventario temporal = new Inventario(idBodegaOrigen, p.getIdProducto(), p.getFechaVencimiento(), p.getNumeroLote());
            //Se busca la entidad inventario
            Inventario inventario = consultarProductoInventPorBodega(temporal);

            //Se actualiza la cantidad del inventario
            Long cantidad = inventario.getCantExistencia();
            cantidad += p.getCantidad();
            inventario.setCantExistencia(cantidad);
            inventario.setActivo(1l);
            actualizar(inventario);

            //Se elimina la linea inventario salida detalle
            eliminar(p);
        }

        //Se obtiene el inventario de salida
        InventarioSalida inventarioSalida = obtenerInvSalidaFromInvSolicitud(invSolicDetalle.getIdInventarioSolicitud());

        //Se valida que exista el inventario salida
        if (inventarioSalida != null) {
            //Se valida si tiene hijos para poder eliminarla
            if (obtenerCantidadInvSalidaDetalleFromInvSolic(invSolicDetalle.getIdInventarioSolicitud()) == 0) {
                eliminar(inventarioSalida);
            }
        }

        //Se eliminan las caracteristicas actuales
        List<InventarioSolicDetElementos> actuales = obtenerElementosDetalle(invSolicDetalle.getIdInventarioSolicitudDetalle());
        actuales.forEach((p -> {
            eliminar(p);
        }));
    }

    public InventarioSolicitudDetalle obtenerInventSolicDetalle(Long idInventarioSolicitudDetalle) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select de.* from SearMedica.inventario_solicitud_detalle de ");
        hilera.append(" where de.id_inventario_solicitud_detalle =");
        hilera.append(idInventarioSolicitudDetalle);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSolicitudDetalle.class);
        try {
            return (InventarioSolicitudDetalle) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public StringBuilder obtenerExistenciaInventario(DetalleSolicitud detalle) {

        Query consulta = null;
        StringBuilder inactivo = new StringBuilder();
        StringBuilder cantInsuficiente = new StringBuilder();
        StringBuilder noExiste = new StringBuilder();

        for (ElementosSolicitudDetalle elem : detalle.getListaElementos()) {

            try {
                consulta = em.createNativeQuery(construirSentencia(detalle.getBodegaDestino().getBodegaPK().getId_bodega(), detalle.getProducto().getId_producto(),
                        elem.getFechaVencimiento(), elem.getNumeroLote()));

                Object[] datos = (Object[]) consulta.getSingleResult();
                Long cantidad = ((BigInteger) datos[0]).longValue();
                Long activo = ((BigInteger) datos[1]).longValue();

                if (activo == 0l) {
                    inactivo.append("Producto Inactivo: ");
                    inactivo.append(detalle.getProducto().getDescripcion());
                    inactivo.append(" \n");
                } else if ((cantidad - elem.getCantidad()) < 0l) {
                    cantInsuficiente.append("Producto Insuficiente: ");
                    cantInsuficiente.append("(").append(cantidad - elem.getCantidad()).append(") ");
                    cantInsuficiente.append(detalle.getProducto().getDescripcion());
                    cantInsuficiente.append(" \n");
                }

            } catch (NoResultException nr) {
                noExiste.append("Producto no Existe: ");
                noExiste.append(detalle.getProducto().getDescripcion());
                noExiste.append(" \n");
            }

        }

        StringBuilder resultado = new StringBuilder();
        if (inactivo.length() > 0) {
            resultado.append(inactivo.toString());
        }
        if (cantInsuficiente.length() > 0) {
            resultado.append(cantInsuficiente.toString());
        }
        if (noExiste.length() > 0) {
            resultado.append(noExiste.toString());
        }

        return resultado;

    }

    private String construirSentencia(Long idBodega, Long idProducto, Date fechaVencimiento, String numeroLote) {

        DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");

        StringBuilder hilera = new StringBuilder();
        hilera.append("select inv.cantexistencia, inv.activo from searmedica.inventario inv ");
        hilera.append("where inv.id_bodega = ");
        hilera.append(idBodega);
        hilera.append(" and inv.id_producto = ");
        hilera.append(idProducto);
        hilera.append(" and inv.fechavencimiento ");
        if (fechaVencimiento != null) {
            hilera.append("= to_date('");
            hilera.append(formato.format(fechaVencimiento));
            hilera.append("','DD-MM-YYYY') ");
        } else {
            hilera.append(" is null");
        }
        if (numeroLote != null) {
            hilera.append(" and inv.numerolote = upper('");
            hilera.append(numeroLote.trim());
            hilera.append("')");
        } else {
            hilera.append(" and inv.numerolote is null");
        }

        return hilera.toString();
    }

    public Long obtenerCantidadInvSalidaDetalleFromInvSolic(Long idInventarioSolicitud) {
        Long cantidad = 0l;

        StringBuilder hilera = new StringBuilder();
        hilera.append("select count(deta.*) from searmedica.inventario_salida_detalle deta, searmedica.inventario_salida sa ");
        hilera.append(" where sa.id_inventario_salida = deta.id_inventario_salida ");
        hilera.append(" and sa.id_inventario_solicitud = ");
        hilera.append(idInventarioSolicitud);

        Query consulta = em.createNativeQuery(hilera.toString());
        try {
            Object datos = (Object) consulta.getSingleResult();
            cantidad = ((BigInteger) datos).longValue();
        } catch (NoResultException e) {
            cantidad = 0l;
        }
        return cantidad;
    }

    public List<InventarioSalidaDetalle> obtenerInvSaliDetalleFromInvSolicDetalle(Long idInventarioSolicitudDetalle) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select invsadeta.* from searmedica.inventario_salida_detalle invsadeta ");
        hilera.append(" where invsadeta.id_inventario_solicitud_detalle = ");
        hilera.append(idInventarioSolicitudDetalle);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSalidaDetalle.class);
        return consulta.getResultList();
    }

    public List<InventarioSalidaDetalle> obtenerInvSaliDetalleFromInvSalida(Long idInvSalida) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select deta.* from searmedica.inventario_salida_detalle deta,searmedica.inventario_salida sali ");
        hilera.append(" where sali.id_inventario_salida = deta.id_inventario_salida and sali.id_inventario_salida = ");
        hilera.append(idInvSalida);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSalidaDetalle.class);
        return consulta.getResultList();
    }

    public List<InventarioSalidaDetalle> obtenerInvSaliDetalleFromInvSalidaPorLlaveDetalleFactura(Long idInvSalida, Long idProducto, Long idFactura, Long numeroLinea) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select deta.* from searmedica.inventario_salida_detalle deta where deta.id_inventario_salida = " + idInvSalida + " and deta.id_producto = " + idProducto + " and deta.id_factura = " + idFactura + " and deta.numero_linea = " + numeroLinea + " ");

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSalidaDetalle.class);
        return consulta.getResultList();
    }

    public List<InventarioSalidaDetalle> obtenerInvSaliDetallePorIdFactura(Long idFactura) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select deta.* from searmedica.inventario_salida_detalle deta ");
        hilera.append(" where  deta.id_factura = ");
        hilera.append(idFactura);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSalidaDetalle.class);
        return consulta.getResultList();
    }

    public InventarioIngreso obtenerInvIngresoFromInvSolicitud(Long idInventarioSolicitud, Long idBodega) {

        StringBuilder hilera = new StringBuilder();
        hilera.append("select invIng.* from searmedica.inventario_ingreso invIng ");
        hilera.append(" where invIng.id_inventario_solicitud = ");
        hilera.append(idInventarioSolicitud);
        hilera.append(" and invIng.id_bodega_origen = ").append(idBodega);

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioIngreso.class);
        try {
            return (InventarioIngreso) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void registrarIngresoSolicitudABodega(InventarioSolicitud inventarioSolicitud, List<InventarioSolicitudDetalle> detalles, boolean esReintegro) throws Exception {
        try {
            //Se actualizan las lineas de solicitud detalle
            actualizarLineas(detalles);

            //Se valida el cambio de estado de la solicitud        
            if (esReintegro) {
                if (validarEstadoTodasLineas(inventarioSolicitud, EstadoSolicitudDetalle.SOLICITUD_RECHAZADA)) {
                    inventarioSolicitud.setEstado(EstadoSolicitud.RECHAZADO.getId());
                }
            } else {
                if (validarEstadoTodasLineas(inventarioSolicitud, EstadoSolicitudDetalle.SOLICITUD_RECIBIDA)) {
                    inventarioSolicitud.setEstado(EstadoSolicitud.RECIBIDO.getId());
                } else {
                    inventarioSolicitud.setEstado(EstadoSolicitud.RECIBIDO_PARCIAL.getId());
                }
            }

            //Se actualiza el inventario solicitud
            actualizar(inventarioSolicitud);

            //Se elije la bodega correcta
            Long valorBodega = (esReintegro ? detalles.get(0).getIdBodegaDestino() : inventarioSolicitud.getIdBodegaOrigen());

            //Se consulta la existencia del inventario ingreso
            InventarioIngreso inventarioIngreso = obtenerInvIngresoFromInvSolicitud(inventarioSolicitud.getIdInventarioSolicitud(), valorBodega);

            //Valida la existencia del inventario ingreso
            if (inventarioIngreso == null) {

                //Se identifica el tipo de ingreso
                IngresoTipo ingresoTipo = (esReintegro ? IngresoTipo.REINTEGRO_BODEGA : IngresoTipo.TRASLADO_BODEGA);

                //Se registra el ingreso a la bodega
                inventarioIngreso = new InventarioIngreso(valorBodega,
                        Utilitario.obtenerUsuario().getLogin(), JSFUtil.obtenerFechaActual(), inventarioSolicitud.getIdInventarioSolicitud(),
                        ingresoTipo.getIdTipo());

                //Se crea la entidad
                guardar(inventarioIngreso);
            }

            //Se construye las lineas de ingreso detalle
            List<InventarioIngresoDetalle> listaIngresoDetalle = new ArrayList<>();
            for (InventarioSolicitudDetalle solicDetalle : detalles) {

                //Se obtiene la lista de caracteristicas de cada solicitud detalle
                List<InventarioSolicDetElementos> caracteristicas = obtenerElementosDetalle(solicDetalle.getIdInventarioSolicitudDetalle());
                for (InventarioSolicDetElementos element : caracteristicas) {

                    listaIngresoDetalle.add(new InventarioIngresoDetalle(inventarioIngreso.getIdInventarioIngreso(),
                            solicDetalle.getIdProducto(), solicDetalle.getCantidad(), element.getFechaVencimiento(), element.getNumeroLote(), null, JSFUtil.obtenerFechaActual()));
                }
            }

            //Se inserta tanto el inventario ingreso y el inventario ingreso detalle
            insertarInventarioIngreso(inventarioIngreso, listaIngresoDetalle);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean validarEstadoTodasLineas(InventarioSolicitud inventarioSolicitud, EstadoSolicitudDetalle estadoSolicitudDetalle) {
        boolean resultado = true;

        List<InventarioSolicitudDetalle> detalles = obtenerInvSolicDetalle(inventarioSolicitud.getIdInventarioSolicitud());
        for (InventarioSolicitudDetalle det : detalles) {
            if (det.getEstado().longValue() != estadoSolicitudDetalle.getId().longValue()) {
                resultado = false;
                break;
            }
        }

        return resultado;
    }

    public List<String> obtenerNumeroLoteFromProducto(Long idBodega, Long idProducto) {
        List<String> resultado = new ArrayList<>();

        StringBuilder hilera = new StringBuilder();
        hilera.append("select distinct inv.numerolote from searmedica.inventario inv ");
        hilera.append(" where inv.id_bodega = ");
        hilera.append(idBodega);
        hilera.append(" and inv.id_producto = ");
        hilera.append(idProducto);

        Query consulta = em.createNativeQuery(hilera.toString());
        try {
            List<String> datos = consulta.getResultList();
            resultado = datos;
        } catch (NoResultException e) {
            resultado = new ArrayList<>();
        }
        return resultado;

    }

    public List<Date> obtenerFechaVencimientoFromLote(Long idBodega, Long idProducto, String numeroLote) {
        List<Date> resultado = new ArrayList<>();

        StringBuilder hilera = new StringBuilder();
        hilera.append("select distinct inv.fechavencimiento from searmedica.inventario inv ");
        hilera.append(" where inv.id_bodega = ");
        hilera.append(idBodega);
        hilera.append(" and inv.id_producto = ");
        hilera.append(idProducto);
        hilera.append(" and inv.numerolote ");
        hilera.append((numeroLote == null ? " is null" : "= '" + numeroLote + "'"));

        Query consulta = em.createNativeQuery(hilera.toString());
        try {
            List<Date> datos = consulta.getResultList();
            resultado = datos;
        } catch (NoResultException e) {
            resultado = new ArrayList<>();
        }
        return resultado;

    }

    public void actualizarInventario(Inventario inventario) {
        try {
            actualizar(inventario);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.actualiza.inventario",
                    "mensaje.actualiza.inventario");
        }
    }

    /**
     * Metodo que gaudar un inventario de salida y devuelve la msima entidad
     *
     * @param salida
     * @return
     */
    public InventarioSalidaDetalle actualizarInventarioSalidaDetalle(InventarioSalidaDetalle salidaDetalle) {
        try {
            salidaDetalle.setFechaRegistro(fechaHoraBD());
            actualizar(salidaDetalle);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensajae.error.guarda.invenatio.salida.detalle",
                    "mensajae.error.guarda.invenatio.salida.detalle.desarrollador");
        }
        return salidaDetalle;
    }

    public void eliminarRegistroInventarioSalidaDetalle(InventarioSalidaDetalle salidaDetalle) {
        try {
            eliminar(salidaDetalle);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.eliminar.detalle.linea.inventario.salida",
                    "mensaje.error.eliminar.detalle.linea.inventario.salida");
        }
    }

    public void eliminarRegistroInventarioSalida(InventarioSalida salidaDetalle) {
        try {
            eliminar(salidaDetalle);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.eliminar.detalle.linea.inventario.salida",
                    "mensaje.error.eliminar.detalle.linea.inventario.salida");
        }
    }

    public void eliminarRegistroInventarioSalidaPorId(Long idSolicitudInventario) {
        try {
            InventarioSalida salidaDetalle = obtenerInvSalidaFromID(idSolicitudInventario);
            if (salidaDetalle != null) {
                eliminar(salidaDetalle);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.eliminar.detalle.linea.inventario.salida",
                    "mensaje.error.eliminar.detalle.linea.inventario.salida");
        }
    }

    /**
     * Metodo que gaudar un inventario de salida y devuelve la msima entidad
     *
     * @param salida
     * @return
     */
    public InventarioSalidaDetalle guardarInventarioSalidaDetalle(InventarioSalidaDetalle salidaDetalle) {
        try {
            salidaDetalle.setFechaRegistro(fechaHoraBD());
            guardar(salidaDetalle);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensajae.error.guarda.invenatio.salida.detalle",
                    "mensajae.error.guarda.invenatio.salida.detalle.desarrollador");
        }
        return salidaDetalle;
    }

    /**
     * Metodo que gaudar un inventario de salida y devuelve la msima entidad
     *
     * @param salida
     * @return
     */
    public InventarioSalida guardarInventarioSalida(InventarioSalida salida) {
        try {
            salida.setFechaRegistro(fechaHoraBD());
            guardar(salida);
            //this.servicioFactura.guardarPadreControlInventario(salida.getLogin(), salida.getIdBodegaOrigen(), salida.getIdInventarioSalida());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensajae.error.guarda.invenatio.salida",
                    "mensajae.error.guarda.invenatio.salida.desarrollador");
        }
        return salida;
    }

    public List<Inventario> obtenerInventarioGeneral(FiltroInventario filtroInventario) {

        StringBuilder hilera = new StringBuilder();
        boolean tieneWhere = false;
        String idsProductos = "";
        hilera.append("select inv.* from searmedica.inventario inv ");
        if (!filtroInventario.getIdBodegaSeleccionada().equals(0L)) {
            hilera.append(" where inv.id_bodega = ");
            hilera.append(filtroInventario.getIdBodegaSeleccionada());
            tieneWhere = true;
        }

        if (filtroInventario.getIdActivoSeleccionado() != null && filtroInventario.getIdActivoSeleccionado().longValue() != -1l) {
            hilera.append(tieneWhere ? " and inv.activo = " : " where inv.activo = ");
            hilera.append(filtroInventario.getIdActivoSeleccionado());
            tieneWhere = true;
        }
        if (filtroInventario.getIdProductoSeleccionado() != null && filtroInventario.getIdProductoSeleccionado().longValue() != -1l) {
            hilera.append(tieneWhere ? " and inv.id_producto = " : " where inv.id_producto = ");
            hilera.append(filtroInventario.getIdProductoSeleccionado());
            tieneWhere = true;
        }
        if (filtroInventario.getListaIdsProductos().size() > 0) {
            hilera.append(tieneWhere ? " and inv.id_producto in ( " : " where inv.id_producto in ( ");
            for (Long idProducto : filtroInventario.getListaIdsProductos()) {
                idsProductos = idsProductos + (idsProductos.equals("") ? idProducto : "," + idProducto);
            }
            hilera.append(idsProductos);
            hilera.append(tieneWhere ? " )" : " )");
        }

        Query consulta = em.createNativeQuery(hilera.toString(), Inventario.class);
        return consulta.getResultList();
    }

    public void anularLineasSolicitud(List<InventarioSolicitudDetalle> lineas) {

        //Se obtiene la solicitud
        InventarioSolicitud solicitud = (lineas.isEmpty() ? null : obtenerInventarioSolicitud(lineas.get(0).getIdInventarioSolicitud()));

        for (InventarioSolicitudDetalle detalle : lineas) {

            //Se libera los elementos de la linea de la solicitud
            crearRetornoTraslado(detalle.getIdBodegaDestino(), detalle);

            //Se actualiza el estado de la linea
            detalle.setEstado(EstadoSolicitudDetalle.SOLICITUD_ANULADA.getId());
            //Se actualiza la linea
            actualizar(detalle);
        }

        //Valida la solicitud
        if (solicitud != null) {

            //Analiza si todas sus lineas estan anuladas
            if (estadoLineasSolicitud(solicitud.getIdInventarioSolicitud(), EstadoSolicitudDetalle.SOLICITUD_ANULADA)) {
                solicitud.setEstado(EstadoSolicitud.ANULADA.getId());
                actualizar(solicitud);
            }
        }
    }

    public boolean estadoLineasSolicitud(Long idInvSolicitud, EstadoSolicitudDetalle estado) {
        boolean resultado = true;
        List<InventarioSolicitudDetalle> detalles = obtenerInvSolicDetalle(idInvSolicitud);
        for (InventarioSolicitudDetalle deta : detalles) {
            resultado = (deta.getEstado() == estado.getId());
            if (!resultado) {
                break;
            }
        }

        return resultado;
    }

    public Inventario eliminarExistencia(InventarioGeneral inventarioGeneral, String responsable, Long cantidad, String motivo) {

        //Se obtiene el inventario
        Inventario inventario = consultarProductoInventPorBodega(inventarioGeneral.getInventario());

        if (cantidad < inventario.getCantExistencia()) {
            Long cant = inventario.getCantExistencia() - cantidad;
            inventario.setCantExistencia(cant);
            actualizar(inventario);
        } else {
            inventario.setCantExistencia(0L);
            inventario.setActivo(0l);
            actualizar(inventario);
        }

        InventarioEliminacion inventarioEliminacion
                = new InventarioEliminacion(inventario.getIdInventario(), responsable, JSFUtil.obtenerFechaActual(), motivo, cantidad);
        guardar(inventarioEliminacion);

        return inventario;

    }

    public InventarioSalida obtenerInventarioSalidaFromFactura(Factura factura) {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select distinct salida.* from searmedica.inventario_salida_detalle detalle, searmedica.inventario_salida salida ");
        hilera.append(" where detalle.id_factura = ");
        hilera.append(factura.getId_factura());
        hilera.append(" and detalle.id_inventario_salida = salida.id_inventario_salida ");

        Query consulta = em.createNativeQuery(hilera.toString(), InventarioSalida.class);
        try {
            return (InventarioSalida) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Devuelve los productos a la bodega asignada como principal en la tabla de
     * parametros
     *
     * @param factura Entidad factura
     * @param login Usuario responsable del retorno
     * @param bodegaOrigen Bodega actual de donde se genera el retorno de los
     * productos
     * @throws Exception Dispara una excepcion en caso que las validaciones no
     * se cumplan
     */
    public void retornarABodegaPrincipal(Factura factura, String login, Bodega bodegaOrigen) throws Exception {

        //Se obtiene el valor de la bodega
        Parametro valorBodega = servicioParametro.obtenerValorParametro(ParametrosEnum.BODEGA_PRINCIPAL.getIdParametro());

        if (valorBodega == null
                | (valorBodega != null
                && (valorBodega.getValor() == null
                | (valorBodega.getValor() != null
                && valorBodega.getValor().isEmpty())))) {

            //se obtiene la bodega actual
            Long idBodega = Long.parseLong(valorBodega.getValor());
            Bodega bodegaPrincipal = servicioBodega.obtenerBodega(idBodega);

            //Se obtiene la salida de inventario 
            InventarioSalida inventarioSalida = obtenerInventarioSalidaFromFactura(factura);

            //Se dispara la excepcion en caso de nulo
            if (inventarioSalida == null) {
                throw new Exception("No se logro obtener el inventario salida");
            }

            //Se obtiene la lineas de detalle de salida de inventario
            List<InventarioSalidaDetalle> listaSalidaDetalle = obtenerInvSaliDetalleFromInvSalida(inventarioSalida.getIdInventarioSalida());

            //Se crea la solicitud a partir de la salida de inventario
            InventarioSolicitud inventarioSolicitud = crearSolicitudFromInvSalida(bodegaPrincipal, login, inventarioSalida, EstadoSolicitud.RECIBIDO);
            //Se crean las lines de detalle y sublineas
            List<InventarioSolicitudDetalle> listaDetalles = crearSolicDetalleFromInvSalidaDetalle(bodegaOrigen, inventarioSolicitud, listaSalidaDetalle, EstadoSolicitudDetalle.SOLICITUD_RECIBIDA);

            //Se valida que las variables no sea nulas
            if (inventarioSolicitud == null | (listaDetalles == null | (listaDetalles != null && listaDetalles.isEmpty()))) {
                throw new Exception("No se logro obtener la linea y subLineas de solicitud de inventarios");
            }

            //Se actualizan en base de datos las lineas de solicitud detalle ingreso, ingreso inventario e inventario general
            registrarIngresoSolicitudABodega(inventarioSolicitud, listaDetalles, false);

        } else {
            throw new Exception("No se logró identificar la bodega principal");
        }
    }

    private InventarioSolicitud crearSolicitudFromInvSalida(Bodega bodegaOrigen, String login, InventarioSalida salida, EstadoSolicitud estadoSolicitud) {
        InventarioSolicitud resultado = null;

        resultado = new InventarioSolicitud(bodegaOrigen.getBodegaPK().getId_bodega(), estadoSolicitud.getId(), JSFUtil.obtenerFechaActual(), login, JSFUtil.obtenerConsecutivoSolicitud());
        guardar(resultado);

        return resultado;
    }

    private List<InventarioSolicitudDetalle> crearSolicDetalleFromInvSalidaDetalle(Bodega bodegaDestino, InventarioSolicitud inventarioSolicitud,
            List<InventarioSalidaDetalle> listaSalidaDetalle,
            EstadoSolicitudDetalle estadoSolicitudDetalle) {

        List<InventarioSolicitudDetalle> lineas = new ArrayList<>();

        listaSalidaDetalle.forEach((p -> {

            //se crea la linea de inventarioSolicitudDetalle
            InventarioSolicitudDetalle linea = new InventarioSolicitudDetalle(inventarioSolicitud.getIdInventarioSolicitud(),
                    bodegaDestino.getBodegaPK().getId_bodega(),
                    p.getIdProducto(), p.getCantidad(),
                    estadoSolicitudDetalle.SOLICITUD_RECIBIDA.getId());

            //Se guarda la linea de solicitudDetalle
            guardar(linea);

            //Se crea la sublinea de inventarioSolicitudDetalle
            InventarioSolicDetElementos subLinea = new InventarioSolicDetElementos(linea.getIdInventarioSolicitudDetalle(),
                    p.getFechaVencimiento(), p.getNumeroLote(), p.getCantidad());

            //Se guarda la sublinea de inventarioSolicitudDetalle
            guardar(subLinea);

            //Se agrega la nueva linea
            lineas.add(linea);
        }));

        return lineas;
    }

}
