package com.ebs.consulta.servicio;

import com.ebs.constantes.enums.Cadenas;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.util.ServicioBase;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioExportarVentasGastos extends ServicioBase {

    private static final long serialVersionUID = 1L;

    /**
     * Obtiene los datos de ventas agrupados por año, mes y tarifa
     *
     * @param anno - año a consultar
     * @return List<Object[]> con columnas [anno, mes, descripcion, monto]
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> obtenerVentas(int anno) {
        try {
            String sql = "SELECT DATOS.ANNO, "
                    + "       to_char(to_timestamp(CAST(DATOS.MES AS TEXT), 'MM'), 'Month'),  "
                    + "       (CASE "
                    + "           WHEN T2.DESCRIPCION IS NULL THEN 'Exento' "
                    + "           ELSE T2.DESCRIPCION "
                    + "        END) AS DESCRIPCION, "
                    + "       SUM(DATOS.TOTAL_LINEA) AS MONTO "
                    + "FROM   (SELECT T1.ID_FACTURA, "
                    + "               T1.FECHA_FACTURA, "
                    + "               EXTRACT(YEAR FROM T1.FECHA_FACTURA)  AS ANNO, "
                    + "               EXTRACT(MONTH FROM T1.FECHA_FACTURA) AS MES, "
                    + "               T2.TOTAL_LINEA, "
                    + "               0 AS ID_TIPO_TARIFA, "
                    + "               T2.ES_LINEA_EXENTA "
                    + "        FROM   SEARMEDICA.FACTURA T1 "
                    + "               INNER JOIN SEARMEDICA.DETALLE_FACTURA T2 "
                    + "                       ON T1.ID_FACTURA = T2.ID_FACTURA "
                    + "        WHERE  T1.ID_TIPO_FACTURA IN(1, 4) "
                    + "               AND T2.ES_LINEA_EXENTA = 1 "
                    + "               AND T1.ESTADO_FACTURA NOT IN (8) "
                    + "               AND T2.ES_PARA_NOTA_CREDITO = 0 "
                    + "        UNION ALL "
                    + "        SELECT T1.ID_FACTURA, "
                    + "               T1.FECHA_FACTURA, "
                    + "               EXTRACT(YEAR FROM T1.FECHA_FACTURA)  AS ANNO, "
                    + "               EXTRACT(MONTH FROM T1.FECHA_FACTURA) AS MES, "
                    + "               T2.TOTAL_LINEA, "
                    + "               T2.ID_TIPO_TARIFA, "
                    + "               T2.ES_LINEA_EXENTA "
                    + "        FROM   SEARMEDICA.FACTURA T1 "
                    + "               INNER JOIN SEARMEDICA.DETALLE_FACTURA T2 "
                    + "                       ON T1.ID_FACTURA = T2.ID_FACTURA "
                    + "        WHERE  T1.ID_TIPO_FACTURA IN(1, 4) "
                    + "               AND T2.ES_LINEA_EXENTA = 0 "
                    + "               AND T1.ESTADO_FACTURA NOT IN (8) "
                    + "               AND T2.ES_PARA_NOTA_CREDITO = 0) DATOS "
                    + "       LEFT JOIN SEARMEDICA.TIPO_TARIFA_IMPUESTO T2 "
                    + "              ON DATOS.ID_TIPO_TARIFA = T2.ID_TIPO_TARIFA_IMPUESTO "
                    + String.format(" WHERE  DATOS.ANNO = %s ", anno)
                    + "GROUP  BY DATOS.ANNO, "
                    + "          DATOS.MES, "
                    + "          T2.ID_TIPO_TARIFA_IMPUESTO, "
                    + "          T2.DESCRIPCION "
                    + "ORDER  BY DATOS.ANNO, "
                    + "          DATOS.MES, "
                    + "          T2.ID_TIPO_TARIFA_IMPUESTO ASC";

            return em.createNativeQuery(sql)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }

    }

    /**
     * Obtiene los datos de gastos agrupados por año, mes y tarifa
     *
     * @param anno - año a consultar
     * @return List<Object[]> con columnas [anno, mes, descripcion, monto]
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> obtenerGastos(int anno) {

        try {
            String sql = "SELECT DATOS.ANNO, "
                    + "           to_char(to_timestamp(CAST(DATOS.MES AS TEXT), 'MM'), 'Month'),  "
                    + "       (CASE "
                    + "           WHEN T2.DESCRIPCION IS NULL THEN 'EXENTO' "
                    + "           ELSE T2.DESCRIPCION "
                    + "        END) AS DESCRIPCION, "
                    + "       SUM(DATOS.MONTO_CON_IMPUESTO) "
                    + "FROM   (SELECT EXTRACT(YEAR FROM T1.FECHA_PAGO)  AS ANNO, "
                    + "               EXTRACT(MONTH FROM T1.FECHA_PAGO) AS MES, "
                    + "               T2.MONTO_CON_IMPUESTO, "
                    + "               T2.ID_TIPO_TARIFA_IMPUESTO "
                    + "        FROM   SEARMEDICA.PAGO T1 "
                    + "               INNER JOIN SEARMEDICA.PAGO_DETALLE T2 "
                    + "                       ON T1.ID_PAGO = T2.ID_PAGO) DATOS "
                    + "       LEFT JOIN SEARMEDICA.TIPO_TARIFA_IMPUESTO T2 "
                    + "              ON DATOS.ID_TIPO_TARIFA_IMPUESTO = T2.ID_TIPO_TARIFA_IMPUESTO "
                    + String.format(" WHERE  DATOS.ANNO = %s ", anno)
                    + "GROUP  BY DATOS.ANNO, "
                    + "          DATOS.MES, "
                    + "          T2.DESCRIPCION "
                    + "ORDER  BY DATOS.ANNO, "
                    + "          DATOS.MES, "
                    + "          T2.DESCRIPCION ASC";

            return em.createNativeQuery(sql)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.error.generar.consecutivo.hacienda",
                    "mensaje.error.generar.consecutivo.hacienda");
        }
    }
}
