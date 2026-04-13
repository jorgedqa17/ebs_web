/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.productos.servicios;

import com.ebs.entidades.Impuesto;
import com.ebs.entidades.InventarioPrecioProducto;
import com.ebs.entidades.Producto;
import com.ebs.entidades.ProductoHistorico;
import com.ebs.entidades.ProductoPrecio;
import com.ebs.entidades.ProductoPrecioPK;
import com.ebs.entidades.TipoExoneracion;
import com.ebs.entidades.TipoMoneda;
import com.ebs.entidades.TipoPrecio;
import com.ebs.entidades.TipoProducto;
import com.ebs.entidades.TipoTarifaImpuesto;
import com.ebs.entidades.UnidadMedida;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.ModeloListaProductos;
import com.ebs.modelos.ModeloProducto;
import com.ebs.modelos.ModeloTipoPrecio;
import com.ebs.modelos.ProductoExistencia;
import com.ebs.modelos.ProductoSolicitudModelo;
import com.powersystem.util.ServicioBase;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Jorge GBSYS
 */
@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioProducto extends ServicioBase {

    private static final long serialVersionUID = 1L;

    public Producto obtenerProductoPorCodigoBarrasPorBodegaInventario(String codigoBarras, Long idBodega) {
        Producto resultado = null;
        try {

            String sql = "SELECT T2.* "
                    + " FROM SEARMEDICA.INVENTARIO T1 INNER JOIN SEARMEDICA.PRODUCTO T2 "
                    + " ON T1.ID_PRODUCTO = T2.ID_PRODUCTO "
                    + " WHERE T1.CANTEXISTENCIA > 0 "
                    + " AND T1.ACTIVO = 1 "
                    + " AND TRIM(UPPER(T2.CODIGO_BARRAS)) = '" + codigoBarras.toUpperCase() + "'  "
                    + " AND T1.ID_BODEGA = :bodega "
                    + " AND T2.ACTIVO = 1 ";

            resultado = (Producto) em.createNativeQuery(sql, Producto.class)
                    .setParameter("bodega", idBodega).getSingleResult();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.lista.producto.por.descripcion.usuario",
                    "mensaje.obtener.lista.producto.por.descripcion.desarrollador");
        }
        return resultado;
    }

    /**
     * Método que obtiene un producto por el código de barras
     *
     * @param codigoBarras - String código de barras
     * @return Producto
     */
    public Producto obtenerProductoPorCodigoBarras(String codigoBarras) {
        Producto producto = null;
        try {
            String hql = " SELECT t1 FROM Producto t1 WHERE t1.codigo_barras = :codigo_barras  ";
            producto = em.createQuery(hql, Producto.class)
                    .setParameter("codigo_barras", codigoBarras)
                    .getSingleResult();
        } catch (NoResultException nex) {
            //  nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.producto.codigo.barra.usuario.final",
                    "mensaje.error.obtener.producto.codigo.barra.desarrollador", codigoBarras);
        }
        return producto;
    }

    public List<TipoTarifaImpuesto> obtenerListaTiposTarifas() {
        List<TipoTarifaImpuesto> tarifa = null;
        try {
            String hql = " SELECT t1 FROM TipoTarifaImpuesto t1  ";
            tarifa = em.createQuery(hql, TipoTarifaImpuesto.class)
                    .getResultList();
        } catch (NoResultException nex) {
            //  nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.producto.obtener.tipo.tarifa",
                    "mensaje.error.obtener.producto.obtener.tipo.tarifa");
        }
        return tarifa;
    }

    /**
     * Método que obtiene un producto por el código de barras
     *
     * @param codigoBarras - String código de barras
     * @return Producto
     */
    public TipoTarifaImpuesto obtenerTipoTarifaImpuesto(Long idTarifaImpuesto) {
        TipoTarifaImpuesto tarifa = null;
        try {
            if (idTarifaImpuesto != null) {
                String hql = " SELECT t1 FROM TipoTarifaImpuesto t1 WHERE t1.id_tipo_tarifa_impuesto = :id_tipo_tarifa_impuesto  ";
                tarifa = em.createQuery(hql, TipoTarifaImpuesto.class)
                        .setParameter("id_tipo_tarifa_impuesto", idTarifaImpuesto)
                        .getSingleResult();
            }
        } catch (NoResultException nex) {
            //  nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.producto.obtener.tipo.tarifa",
                    "mensaje.error.obtener.producto.obtener.tipo.tarifa");
        }
        return tarifa;
    }

    public BigDecimal obtenerImpuestoDelProducto(Long idProducto, BigDecimal precio) {
        BigDecimal resultado = new BigDecimal("0.0");
        Producto producto = this.obtenerProducto(idProducto);
        if (producto.getId_impuesto() != null && producto.getId_tipo_tarifa_impuesto() != null) {
            TipoTarifaImpuesto tarifaImpuesto = this.obtenerTipoTarifaImpuesto(producto.getId_tipo_tarifa_impuesto());

            Float imp = Float.parseFloat(tarifaImpuesto.getValor().toString()) / 100f;
            resultado = precio.multiply(new BigDecimal(imp)).setScale(3, BigDecimal.ROUND_HALF_EVEN);
        }
        return resultado;
    }

    public List<InventarioPrecioProducto> obtenerPreciosProductoIngreso(Long idInventario, Long idProducto) {
        List<InventarioPrecioProducto> resultado = null;
        try {
            String hql = " SELECT t1 FROM InventarioPrecioProducto t1 WHERE t1.id_inventario = :id_inventario AND t1.id_producto =:id_producto  ";

            resultado = em.createQuery(hql, InventarioPrecioProducto.class)
                    .setParameter("id_inventario", idInventario)
                    .setParameter("id_producto", idProducto)
                    .getResultList();

        } catch (NoResultException nex) {
            //  nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.precios.ingreso.producto",
                    "mensaje.error.obtener.precios.ingreso.producto");
        }

        return resultado;
    }

    /**
     * Método que obtiene un producto por el código de barras
     *
     * @param codigoBarras - String código de barras
     * @return Producto
     */
    public List<Producto> obtenerListaProductosPorCodigoBarras(String codigoBarras) {
        List<Producto> producto = null;
        try {
            String hql = " SELECT t1 FROM Producto t1 WHERE t1.codigo_barras = :codigo_barras  ";
            producto = em.createQuery(hql, Producto.class)
                    .setParameter("codigo_barras", codigoBarras)
                    .getResultList();
        } catch (NoResultException nex) {
            //  nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.producto.codigo.barra.usuario.final",
                    "mensaje.error.obtener.producto.codigo.barra.desarrollador", codigoBarras);
        }
        return producto;
    }

    /**
     * Método que obtiene la lista de productos que se vendieron en una fecha
     * dada en una bodega y con un usuario especfico
     *
     * @param fecha
     * @param usuario
     * @param idBodega
     * @return List<ProductoSolicitudModelo>
     */
    public List<ProductoSolicitudModelo> obtenerListaProductoVendidosPOrFechaPorBodegaPorUsuario(String fecha, String usuario, Long idBodega, String fechaFinal) {
        List<ProductoSolicitudModelo> listaResultado = null;
        try {

            String sql = " SELECT T1.ID_BODEGA, T3.ID_PRODUCTO,  T3.DESCRIPCION, SUM(T2.CANTIDAD) FROM SEARMEDICA.FACTURA T1 INNER JOIN SEARMEDICA.DETALLE_FACTURA T2 "
                    + " ON T1.ID_FACTURA = T2.ID_FACTURA INNER JOIN SEARMEDICA.PRODUCTO T3 "
                    + " ON T2.ID_PRODUCTO = T3.ID_PRODUCTO "
                    + " WHERE TO_DATE(TO_CHAR(T1.FECHA_FACTURA,'DD/MM/YYYY'),'DD/MM/YYYY') = TO_DATE(:fecha ,'DD/MM/YYYY') "//and TO_DATE(:fechaFinal ,'DD/MM/YYYY') "
                    + " AND T1.ID_BODEGA = :idBodega "
                    + " AND T1.LOGIN = :login "
                    + " AND NOT EXISTS (SELECT 1 FROM SEARMEDICA.CIERRE_FACTURA T4 WHERE T4.ID_FACTURA = T2.ID_FACTURA)"
                    + " AND T1.NUMERO_CONSECUTIVO IS NOT NULL "
                    + " GROUP BY T1.ID_BODEGA, T3.ID_PRODUCTO, T3.DESCRIPCION ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("fecha", fecha)
                    //                    .setParameter("fechaFinal", fechaFinal)
                    .setParameter("idBodega", idBodega)
                    .setParameter("login", usuario)
                    .getResultList();

            ProductoSolicitudModelo resultado = null;
            listaResultado = new ArrayList<>();
            for (Object[] objeto : lista) {
                resultado = new ProductoSolicitudModelo();

                resultado.setIdProducto(Long.parseLong(objeto[1].toString()));
                resultado.setIdBodega(Long.parseLong(objeto[0].toString()));
                resultado.setDescripcion(objeto[2].toString());
                resultado.setCantidad(Long.parseLong(objeto[3].toString()));

                listaResultado.add(resultado);
            }

        } catch (NoResultException nex) {
            //  nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.producto.codigo.barra.usuario.final",
                    "mensaje.error.obtener.producto.codigo.barra.desarrollador");
        }
        return listaResultado;
    }

    /**
     *
     * @return List<ModeloListaProductos>
     */
    public List<ModeloListaProductos> obtenerTodosProductos() {
        List<ModeloListaProductos> listaResultado = null;
        try {
            StringBuilder consulta = new StringBuilder();
            consulta
                    .append(" SELECT t1.id_producto,  ")
                    .append(" t1.id_unidad_medida, t2.descripcion as descripcion_unidad_medida,  ")
                    .append(" t1.descripcion,  ")
                    .append(" t1.ind_caduce, t1.ind_exonerado, t1.codigo_barras,  ")
                    .append(" t1.id_tipo_producto, t3.descripcion as descripcion_tipo_producto, ")
                    .append(" t1.id_impuesto, t4.descripcion as descripcion_impuesto, t1.activo, t1.cantidad_minima, t1.id_tipo_tarifa_impuesto, t1.ind_exento, t1.codigo_cabys ")
                    .append(" FROM searmedica.producto t1 left join searmedica.unidad_medida t2 ")
                    .append(" on t1.id_unidad_medida=t2.id_unidad_medida left join searmedica.tipo_producto t3 ")
                    .append(" on t1.id_tipo_producto = t3.id_tipo_producto left join searmedica.impuesto t4 ")
                    .append(" on t1.id_impuesto = t4.id_impuesto ")
                    .append(" group by t1.id_tipo_producto,t3.descripcion,t1.id_unidad_medida, t2.descripcion, t1.id_impuesto, t4.descripcion, t1.id_producto ")
                    .append(" order by t1.descripcion asc ");

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(consulta.toString()).getResultList();

            ModeloListaProductos modelo = null;
            Producto productoEntidad = null;

            listaResultado = new ArrayList<>();
            List<ModeloTipoPrecio> listaPrecios;
            ModeloTipoPrecio precio;
            ProductoPrecio precioEntidad;
            ProductoPrecioPK precioEntidadPk;

            String consultaPrecios = "  select t1.id_tipo, t1.id_moneda, t1.precio, t2.descripcion as descripcion_tipo, t3.descripcion as descripcion_moneda from  searmedica.producto_precios t1, searmedica.tipos_precios t2, searmedica.tipo_moneda t3  where t1.id_tipo = t2.id_tipo and t1.id_moneda = t3.id_moneda and t1.id_producto =:idProducto ";

            for (Object[] fila : lista) {
                try {

                    productoEntidad = new Producto();
                    modelo = new ModeloListaProductos();
                    modelo.setDescripcionUnidadMedida(fila[2].toString());

                    productoEntidad.setId_producto(Long.parseLong(fila[0].toString()));
                    productoEntidad.setId_unidad_medida(Long.parseLong(fila[1].toString()));
                    productoEntidad.setDescripcion(fila[3].toString());
                    productoEntidad.setInd_caduce(Integer.parseInt(fila[4].toString()));
                    productoEntidad.setInd_exonerado(Integer.parseInt(fila[5].toString()));
                    productoEntidad.setCodigo_barras(fila[6].toString());
                    productoEntidad.setId_tipo_producto(Long.parseLong(fila[7].toString()));
                    productoEntidad.setActivo(Integer.parseInt(fila[11].toString()));
                    productoEntidad.setCantidad_minima(fila[12] == null ? null : Integer.parseInt(fila[12].toString()));
                    productoEntidad.setId_tipo_tarifa_impuesto(fila[13] == null ? null : Long.parseLong(fila[13].toString()));
                    productoEntidad.setInd_exento(fila[14] == null ? null : Integer.parseInt(fila[14].toString()));
                    productoEntidad.setCodigo_cabys(fila[15] == null ? "" : String.valueOf(fila[15]));
//                    productoEntidad.setNumero_documento(fila[15] == null ? null : fila[15].toString());
//                    productoEntidad.setNombre_institucion(fila[16] == null ? null : fila[16].toString());
//                    productoEntidad.setFecha_emision(fila[17] == null ? null : fila[17].toString());
//                    productoEntidad.setMonto_impuesto(fila[18] == null ? null : new BigDecimal(fila[18].toString()));
//                    productoEntidad.setPorcentaje_compra(fila[19] == null ? null : new Integer(fila[19].toString()));
                    modelo.setDescripcionTipoProducto(fila[8].toString());
                    productoEntidad.setId_impuesto(fila[9] == null ? null : Long.parseLong(fila[9].toString()));
                    modelo.setDescripcionImpuesto(fila[10] == null ? "" : fila[10].toString());
                    modelo.setProducto(productoEntidad);

                    List<Object[]> listaPreciosProducto = (List<Object[]>) em.createNativeQuery(consultaPrecios)
                            .setParameter("idProducto", Long.parseLong(fila[0].toString())).getResultList();
                    if (listaPreciosProducto != null) {
                        listaPrecios = new ArrayList<>();

                        for (Object[] precioProd : listaPreciosProducto) {

                            precio = new ModeloTipoPrecio();
                            precioEntidad = new ProductoPrecio();
                            precioEntidadPk = new ProductoPrecioPK();

                            precioEntidadPk.setId_tipo(Long.parseLong(precioProd[0].toString()));
                            precioEntidadPk.setId_producto(Long.parseLong(fila[0].toString()));
                            precioEntidad.setProductoPrecioPK(precioEntidadPk);
                            precioEntidad.setId_moneda(Long.parseLong(precioProd[1].toString()));
                            precioEntidad.setPrecio(new BigDecimal(precioProd[2] == null ? "0.0" : precioProd[2].toString()));
                            precio.setDescripcion(precioProd[3].toString());
                            precio.setDescripcionTipoMoneda(precioProd[4].toString());
                            precio.setProductoPrecio(precioEntidad);
                            listaPrecios.add(precio);
                        }
                        modelo.setListaPrecios(listaPrecios);
                    }

                    listaResultado.add(modelo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.listar.producto",
                    "mensaje.error.listar.producto.desarrollador");
        }
        return listaResultado;
    }

    public List<ModeloTipoPrecio> obtenerPreciosProductos(Long idProducto) {
        List<ModeloTipoPrecio> listaPrecios = null;
        ModeloTipoPrecio precio;
        ProductoPrecio precioEntidad;
        ProductoPrecioPK precioEntidadPk;
        try {
            String consultaPrecios = "  select t1.id_tipo, t1.id_moneda, t1.precio, t2.descripcion as descripcion_tipo, t3.descripcion as descripcion_moneda from  searmedica.producto_precios t1, searmedica.tipos_precios t2, searmedica.tipo_moneda t3  where t1.id_tipo = t2.id_tipo and t1.id_moneda = t3.id_moneda and t1.id_producto =:idProducto order by t1.id_tipo asc";
            List<Object[]> listaPreciosProducto = (List<Object[]>) em.createNativeQuery(consultaPrecios)
                    .setParameter("idProducto", idProducto).getResultList();
            if (listaPreciosProducto != null) {
                listaPrecios = new ArrayList<>();

                for (Object[] precioProd : listaPreciosProducto) {

                    precio = new ModeloTipoPrecio();
                    precioEntidad = new ProductoPrecio();
                    precioEntidadPk = new ProductoPrecioPK();

                    precioEntidadPk.setId_tipo(Long.parseLong(precioProd[0].toString()));
                    precioEntidadPk.setId_producto(idProducto);
                    precioEntidad.setProductoPrecioPK(precioEntidadPk);
                    precioEntidad.setId_moneda(Long.parseLong(precioProd[1].toString()));
                    precioEntidad.setPrecio(new BigDecimal(precioProd[2] == null ? "0.0" : precioProd[2].toString()));
                    precio.setDescripcion(precioProd[3].toString());
                    precio.setDescripcionTipoMoneda(precioProd[4].toString());
                    precio.setProductoPrecio(precioEntidad);
                    precio.setCostoImpuesto(this.obtenerImpuestoDelProducto(idProducto, new BigDecimal(precioProd[2] == null ? "0.0" : precioProd[2].toString())));
                    precio.setPrecioConImpuesto(precioEntidad.getPrecio().add(precio.getCostoImpuesto()));
                    listaPrecios.add(precio);
                }

            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.erro.obtener.precios.producto",
                    "mensaje.erro.obtener.precios.producto");
        }

        return listaPrecios;
    }

    public List<ModeloTipoPrecio> obtenerPrecioProductoPorTipo(Long idProducto, Long idTipoPrecio) {
        List<ModeloTipoPrecio> listaPrecios = null;
        ModeloTipoPrecio precio;
        ProductoPrecio precioEntidad;
        ProductoPrecioPK precioEntidadPk;
        try {
            String consultaPrecios = "  select t1.id_tipo, t1.id_moneda, t1.precio, "
                    + "t2.descripcion as descripcion_tipo, t3.descripcion as descripcion_moneda"
                    + " from  searmedica.producto_precios t1, searmedica.tipos_precios t2, "
                    + "searmedica.tipo_moneda t3  "
                    + "where t1.id_tipo = t2.id_tipo and t1.id_moneda = t3.id_moneda and t1.id_producto =:idProducto and t2.id_tipo =  " + idTipoPrecio;
            List<Object[]> listaPreciosProducto = (List<Object[]>) em.createNativeQuery(consultaPrecios)
                    .setParameter("idProducto", idProducto).getResultList();
            if (listaPreciosProducto != null) {
                listaPrecios = new ArrayList<>();

                for (Object[] precioProd : listaPreciosProducto) {

                    precio = new ModeloTipoPrecio();
                    precioEntidad = new ProductoPrecio();
                    precioEntidadPk = new ProductoPrecioPK();

                    precioEntidadPk.setId_tipo(Long.parseLong(precioProd[0].toString()));
                    precioEntidadPk.setId_producto(idProducto);
                    precioEntidad.setProductoPrecioPK(precioEntidadPk);
                    precioEntidad.setId_moneda(Long.parseLong(precioProd[1].toString()));
                    precioEntidad.setPrecio(new BigDecimal(precioProd[2] == null ? "0.0" : precioProd[2].toString()));
                    precio.setDescripcion(precioProd[3].toString());
                    precio.setDescripcionTipoMoneda(precioProd[4].toString());
                    precio.setProductoPrecio(precioEntidad);
                    precio.setCostoImpuesto(this.obtenerImpuestoDelProducto(idProducto, new BigDecimal(precioProd[2] == null ? "0.0" : precioProd[2].toString())));
                    precio.setPrecioConImpuesto(precioEntidad.getPrecio().add(precio.getCostoImpuesto()));
                    listaPrecios.add(precio);
                }

            }
        } catch (NoResultException nex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.erro.obtener.precios.producto",
                    "mensaje.erro.obtener.precios.producto");
        }

        return listaPrecios;
    }

    /**
     * Método que obtiene un producto por el código de barras
     *
     * @param codigoBarras - String código de barras
     * @return Producto
     */
    public Producto obtenerProductoPorIdProducto(Long idProducto) {
        Producto producto = new Producto();
        try {
            String hql = " SELECT t1 FROM Producto t1 WHERE t1.id_producto = :id_producto  ";
            producto = em.createQuery(hql, Producto.class)
                    .setParameter("id_producto", idProducto)
                    .getSingleResult();
        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.producto.codigo.barra.usuario.final",
                    "mensaje.error.obtener.producto.codigo.barra.desarrollador", idProducto);
        }
        return producto;
    }

    /**
     * Método que obtiene una unidad de medida por su id
     *
     * @param idUnidadMedida - Long
     * @return UnidadMedida
     */
    public UnidadMedida obtenerUnidadMedidaProducto(Long idUnidadMedida) {
        UnidadMedida unidadMedida = new UnidadMedida();
        try {
            String hql = " SELECT t1 FROM UnidadMedida t1 WHERE t1.id_unidad_medida = :idUnidadMedida  ";
            unidadMedida = em.createQuery(hql, UnidadMedida.class)
                    .setParameter("idUnidadMedida", idUnidadMedida)
                    .getSingleResult();
        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.unidadMedida.usuario.final",
                    "mensaje.error.obtener.unidadMedida.desarrollador", idUnidadMedida);
        }
        return unidadMedida;
    }

    /**
     * Método que obtiene la lista comppleta de unidades de medida
     *
     * @return List<UnidadMedida>
     */
    public List<UnidadMedida> obtenerTodasUnidadesMedida() {
        List<UnidadMedida> listaResultado = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM UnidadMedida t1   ";
            listaResultado = em.createQuery(hql, UnidadMedida.class).getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.lista.unidadMedida.usuario.final",
                    "mensaje.error.obtener.lista.unidadMedida.desarrollador");
        }
        return listaResultado;
    }

    /**
     * Método que obtiene el tipo de moneda
     *
     * @return TipoMoneda
     */
    public List<TipoMoneda> obtenerListaTiposMonedas() {
        List<TipoMoneda> moneda = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM TipoMoneda t1 ";
            moneda = em.createQuery(hql, TipoMoneda.class)
                    .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.tipo.moneda.final",
                    "mensaje.error.obtener.tipo.moneda.desarrollador");
        }
        return moneda;
    }

    /**
     * Método que obtiene la lista de precios de un producto
     *
     * @param idProducto - Long
     * @return List<ProductoPrecio>
     */
    public List<ProductoPrecio> obtenerListaPreciosProducto(Long idProducto) {
        List<ProductoPrecio> listaPrecios = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM ProductoPrecio t1 WHERE t1.productoPrecioPK.id_producto = :id_producto  ";
            listaPrecios = em.createQuery(hql, ProductoPrecio.class)
                    .setParameter("id_producto", idProducto)
                    .getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.listaprecios.usuario.final",
                    "mensaje.error.obtener.listaprecios.desarrollador", idProducto);
        }
        return listaPrecios;
    }

    /**
     * Método que obtiene un lista de tipos de precio
     *
     * @return List<TipoPrecio>
     */
    public List<TipoPrecio> obtenerListaTiposPrecio() {
        List<TipoPrecio> listaPrecios = new ArrayList<>();
        try {
            String hql = " SELECT t1 FROM TipoPrecio t1   ";
            listaPrecios = em.createQuery(hql, TipoPrecio.class).getResultList();
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.listaTiposPrecio.usuario.final",
                    "mensaje.error.obtener.listaTiposPrecio.desarrollador");
        }
        return listaPrecios;
    }

    /**
     * Método que obtiene la existencia de un producto específico
     *
     * @param idProducto - Long
     * @return List<ProductoExistencia>
     */
    public List<ProductoExistencia> obtenerExistenciaProducto(Long idProducto) {
        List<ProductoExistencia> listaExistencias = new ArrayList<>();
        try {
            String sql = " SELECT T1.ID_PRODUCTO, SUM(T1.CANTEXISTENCIA) AS CANTIDAD,T1.ID_BODEGA, T3.DESCRIPCION AS BODEGA, T2.DESCRIPCION AS PRODUCTO "
                    + " FROM SEARMEDICA.INVENTARIO T1 INNER JOIN SEARMEDICA.PRODUCTO T2  "
                    + " ON T1.ID_PRODUCTO = T2.ID_PRODUCTO INNER JOIN SEARMEDICA.BODEGA T3 "
                    + " ON T1.ID_BODEGA = T3.ID_BODEGA "
                    + " WHERE T2.ID_PRODUCTO = :idProducto AND T1.ACTIVO = 1 "
                    + " GROUP BY T1.ID_BODEGA,T1.ID_PRODUCTO, T3.DESCRIPCION, T2.DESCRIPCION ";

            List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql)
                    .setParameter("idProducto", idProducto)
                    .getResultList();
            ProductoExistencia productoExistencia = null;

            for (Object[] array : lista) {
                productoExistencia = new ProductoExistencia();

                productoExistencia.setId_producto(Long.parseLong(array[0].toString()));
                productoExistencia.setId_bodega(Long.parseLong(array[2].toString()));
                productoExistencia.setDescripcion_bodega(array[3].toString());
                productoExistencia.setCantidad(Integer.parseInt(array[1].toString()));

                listaExistencias.add(productoExistencia);
            }
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.existencias.usuario.final",
                    "mensaje.error.obtener.existencias.programador");
        }

        return listaExistencias;
    }

    /**
     * Método que obtiene el tipo de producto basado en el id del tipo de
     * producto
     *
     * @param idTipoProducto
     * @return TipoProducto
     */
    public TipoProducto obtenerTipoProducto(Long idTipoProducto) {
        TipoProducto tipoProducto = null;
        try {
            if (idTipoProducto != null) {
                String hql = " SELECT t1 FROM TipoProducto t1 WHERE t1.id_tipo_producto =:id_tipo_producto  ";
                tipoProducto = em.createQuery(hql, TipoProducto.class)
                        .setParameter("id_tipo_producto", idTipoProducto)
                        .getSingleResult();
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.obtener.tipo.producto.usuario",
                    "mensaje.error.obtener.tipo.producto.desarrollador");
        }
        return tipoProducto;
    }

    /**
     * Método que obtiene un tipo de exoneracion
     *
     * @param idExoneracion Long
     * @return TipoExoneracion
     */
    public TipoExoneracion obtenerTipoExoneracion(Long idExoneracion) {
        TipoExoneracion exoneracion = null;
        try {
            if (idExoneracion != null) {
                String hql = " SELECT t1 FROM TipoExoneracion t1 WHERE t1.id_exoneracion =:id_exoneracion  ";
                exoneracion = em.createQuery(hql, TipoExoneracion.class)
                        .setParameter("id_exoneracion", idExoneracion)
                        .getSingleResult();
            }
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.tipo.exoneracion",
                    "mesaje.error.tipo.exoneracin.desarrollador");
        }
        return exoneracion;
    }

    /**
     * Método que obtiene todos los tipos de productos existentes
     *
     * @return List<TipoProducto>
     */
    public List<TipoProducto> obtenerTodosTiposProductos() {
        List<TipoProducto> listaResultado = null;
        try {
            String hql = " SELECT t1 FROM TipoProducto t1   ";
            listaResultado = em.createQuery(hql, TipoProducto.class).getResultList();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.lista.tipo.productos",
                    "mesaje.error.lista.tipo.productos.desarrollador");
        }
        return listaResultado;
    }

    /**
     * Método que obtiene la lista de tipso de exoneraciones
     *
     * @return List<TipoExoneracion>
     */
    public List<TipoExoneracion> obtenerTodosTiposExoneraciones() {
        List<TipoExoneracion> listaResultado = null;
        try {

            String hql = " SELECT t1 FROM TipoExoneracion t1   ";
            listaResultado = em.createQuery(hql, TipoExoneracion.class).getResultList();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.lista.tipo.exoneracion",
                    "mesaje.error.lista.tipo.exoneracin.desarrollador");
        }
        return listaResultado;
    }

    /**
     * Método que obtiene un objeto Impuesto
     *
     * @param idImpuesto Long
     * @return Impuesto
     */
    public Impuesto obtenerImpuesto(Long idImpuesto) {
        Impuesto impuesto = null;
        try {
            if (idImpuesto != null) {
                String hql = " SELECT t1 FROM Impuesto t1 WHERE t1.id_impuesto =:id_impuesto  ";
                impuesto = em.createQuery(hql, Impuesto.class)
                        .setParameter("id_impuesto", idImpuesto)
                        .getSingleResult();
            }

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.tipo.impuesto",
                    "mesaje.error.tipo.exoneracin.desarrollador");
        }
        return impuesto;
    }

    /**
     * Método que obtiene todos los impuestos
     *
     * @return List<Impuesto>
     */
    public List<Impuesto> obtenerTodosImpuestos() {
        List<Impuesto> listaResultado = null;
        try {

            String hql = " SELECT t1 FROM Impuesto t1  ";
            listaResultado = em.createQuery(hql, Impuesto.class).getResultList();

        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.lista.tipo.impuesto",
                    "mesaje.error.lista.tipo.exoneracin.desarrollador");
        }
        return listaResultado;
    }

    /*
    

    
     */
    public List<ModeloProducto> listarProductosPorDescripcionPorBodegaInventario(String nombreProducto, Long idBodega) {
        List<ModeloProducto> listaResultado = new ArrayList<>();
        ModeloProducto modeloProducto = null;
        try {
            if (nombreProducto != null) {
                String sql = " SELECT T2.CODIGO_BARRAS, T2.DESCRIPCION, T2.ID_PRODUCTO "
                        + " FROM SEARMEDICA.INVENTARIO T1 INNER JOIN SEARMEDICA.PRODUCTO T2 "
                        + " ON T1.ID_PRODUCTO = T2.ID_PRODUCTO "
                        + " WHERE T1.CANTEXISTENCIA > 0 "
                        + " AND T1.ACTIVO = 1 "
                        + " AND upper(T2.DESCRIPCION) LIKE '%" + nombreProducto.toUpperCase() + "%'  "
                        + " AND T1.ID_BODEGA = :bodega "
                        + " AND T2.ACTIVO = 1 "
                        + " ORDER BY T2.DESCRIPCION ASC ";

                List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql)
                        .setParameter("bodega", idBodega)
                        .getResultList();

                for (Object[] producto : lista) {
                    modeloProducto = new ModeloProducto();
                    modeloProducto.setCodigo_barras(producto[0].toString());
                    modeloProducto.setDescripcion(producto[1].toString());
                    modeloProducto.setId_producto(Long.parseLong(producto[2].toString()));
                    listaResultado.add(modeloProducto);
                }
            }
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.lista.producto.por.descripcion.usuario",
                    "mensaje.obtener.lista.producto.por.descripcion.desarrollador");
        }
        return listaResultado;
    }

    /**
     * Método que consulta los productos por descripcion
     *
     * @param nombreProducto
     * @return List<ModeloProducto>
     */
    public List<ModeloProducto> listarProductosPorDescripcion(String nombreProducto) {
        List<ModeloProducto> listaResultado = new ArrayList<>();
        ModeloProducto modeloProducto = null;
        try {
            if (nombreProducto != null) {
                String sql = "SELECT codigo_barras, descripcion, id_producto FROM searmedica.producto where upper(descripcion) like '%" + nombreProducto.toUpperCase() + "%' and activo = 1 order by descripcion asc";

                List<Object[]> lista = (List<Object[]>) em.createNativeQuery(sql)
                        .getResultList();

                for (Object[] producto : lista) {
                    modeloProducto = new ModeloProducto();
                    modeloProducto.setCodigo_barras(producto[0].toString());
                    modeloProducto.setDescripcion(producto[1].toString());
                    modeloProducto.setId_producto(Long.parseLong(producto[2].toString()));
                    listaResultado.add(modeloProducto);
                }
            }
        } catch (NoResultException nex) {
            nex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.obtener.lista.producto.por.descripcion.usuario",
                    "mensaje.obtener.lista.producto.por.descripcion.desarrollador");
        }
        return listaResultado;
    }

    /**
     * Método que guardar un nuevo producto
     *
     * @param producto
     * @param listaPrecios
     */
    public void guadarProducto(Producto producto, List<ProductoPrecio> listaPrecios) {
        try {
            guardar(producto);

            for (ProductoPrecio precio : listaPrecios) {
                precio.getProductoPrecioPK().setId_producto(producto.getId_producto());
                guardar(precio);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.gudar.producto",
                    "mensaje.error.guadar.producto.desarrollador");
        }

    }

    public void agregarHistoricoProducto(String login, String descripcion, Long idProducto) {
        try {
            ProductoHistorico historicoProducto = new ProductoHistorico();
            historicoProducto.setLogin(login);
            historicoProducto.setDescripcion(descripcion);
            historicoProducto.setId_producto(idProducto);
            historicoProducto.setFecha(fechaHoraBD());
            guardar(historicoProducto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que guardar un nuevo producto
     *
     * @param producto
     * @param listaPrecios
     */
    public void modificarProducto(Producto producto, List<ProductoPrecio> listaPrecios) {
        try {
            actualizar(producto);
            if (listaPrecios != null) {
                /*QProductoPrecio preciosProducto = QProductoPrecio.productoPrecio;
                qf.delete(preciosProducto).where(preciosProducto.productoPrecioPK.id_producto.eq(producto.getId_producto())).execute();*/

                List<ProductoPrecio> listaPreciosProductos = obtenerListaPreciosProducto(producto.getId_producto());

                for (ProductoPrecio precioProducto : listaPreciosProductos) {
                    eliminar(precioProducto);
                }

                for (ProductoPrecio precio : listaPrecios) {
                    precio.getProductoPrecioPK().setId_producto(producto.getId_producto());
                    guardar(precio);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.gudar.producto",
                    "mensaje.error.guadar.producto.desarrollador");
        }
    }

    public Producto obtenerProducto(Long idProducto) {
        Query consulta = null;
        Producto resultado;
        try {
            consulta = em.createNativeQuery("Select p.* from searMedica.producto p where p.id_producto = ?1", Producto.class);
            consulta.setParameter(1, idProducto);
            Object preliminar = consulta.getSingleResult();
            if (preliminar == null) {
                resultado = null;
            } else {
                resultado = (Producto) consulta.getSingleResult();
            }
        } catch (Exception e) {
            resultado = null;
        }

        return resultado;
    }

    public List<Producto> ObtenerProductosActivos(boolean esActivo) {
        Query consulta;
        List<Producto> lista;
        try {
            consulta = em.createNativeQuery("Select p.* from searMedica.producto p where p.activo = ?1 order by p.descripcion", Producto.class);
            consulta.setParameter(1, (esActivo ? 1 : 0));
            lista = consulta.getResultList();
        } catch (Exception e) {
            lista = new ArrayList<>();
        }

        return lista;
    }

    public List<Producto> buscarProductosActivos(String descripcion) {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select p.* from searmedica.producto p where p.activo = 1 and p.id_tipo_producto = 1 and upper(p.descripcion) like upper('%");
        hilera.append(descripcion);
        hilera.append("%') order by descripcion");
        Query consulta = em.createNativeQuery(hilera.toString(), Producto.class);
        List<Producto> lista = consulta.getResultList();
        return lista;
    }

    public List<Producto> buscarTodosProductos() {
        StringBuilder hilera = new StringBuilder();
        hilera.append("select p.* from searmedica.producto p");
        hilera.append(" order by p.descripcion asc");

        Query consulta = em.createNativeQuery(hilera.toString(), Producto.class);
        List<Producto> lista = consulta.getResultList();

        return lista;
    }

}
