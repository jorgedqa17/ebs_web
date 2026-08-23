-- =====================================================================
-- Devolver la factura 268647 a su estado vigente
--
--   Factura : 268647 / 00100001010000115523
--   NC      : 8398   / 00100001030000008553  (anulada)
--   ND      : 45     / 00100001020000000101  (APROBADA por Hacienda)
--
-- POR QUÉ
--
-- La nota de débito 45 fue aprobada y anuló la nota de crédito, y los
-- totales de la factura volvieron correctamente a 21150. Pero
-- estado_factura se quedó en 8 (Anulada): ningún punto del sistema lo
-- devuelve.
--
--   ServicioNotaDebito.guardarNotaDebito solo lo cambia si la ND es
--   interna; EnvioNotasDebitoHacienda y RevisionEstadoNotasDebito solo
--   tocan estados de la ND y de las líneas. En ebs_servicios,
--   actualizarEstadoFactura solo lo invocan procesos de facturas.
--
-- El histórico confirma que su estado previo a la nota de crédito era 3
-- (Aprobada Hacienda), registrado el 2026-08-22 19:49:33.
--
-- Sin esto la factura no aparece en anulacion.xhtml, porque
-- ServicioFactura.obtenerFacturasAnulacion excluye las anuladas, y no se
-- puede emitir la nota de crédito corregida.
--
-- El fix de código ya quedó aplicado en ServicioNotaDebito para que las
-- próximas notas de débito lo hagan solas. Este script es solo para el
-- caso que ya ocurrió.
--
-- TERMINA EN ROLLBACK A PROPÓSITO.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES
-- ---------------------------------------------------------------------

SELECT 'ANTES factura' AS bloque, id_factura, estado_factura,
       numero_consecutivo, total_venta, total_venta_neta,
       total_comprobante, total_gravado, total_exento, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado_factura = 8, totales en 21150.000

SELECT 'ANTES historico (estado previo a la NC)' AS bloque,
       fecha, estado_factura
FROM searmedica.factura_historico_hacienda
WHERE id_factura = 268647
ORDER BY fecha DESC
LIMIT 3;
-- Esperado: la fila más reciente con estado 3, del 2026-08-22 19:49:33

-- ---------------------------------------------------------------------
-- GUARDIA: solo corresponde restaurar si la nota de débito que anuló la
-- nota de crédito está efectivamente APROBADA por Hacienda (estado 40).
-- ---------------------------------------------------------------------

SELECT 'GUARDIA' AS bloque,
       nd.id_nota_debito, nd.numero_consecutivo AS nd_consecutivo,
       nd.id_estado AS estado_nd,
       a.id_anulacion, a.id_estado AS estado_nc,
       CASE WHEN nd.id_estado = 40 AND a.id_estado = 19
            THEN 'OK - la ND esta aprobada y la NC anulada'
            ELSE 'NO APLICAR - revisar estados' END AS diagnostico
FROM searmedica.factura_nota_debito nd
JOIN searmedica.factura_anulacion a ON a.id_anulacion = nd.id_anulacion
WHERE nd.id_nota_debito = 45;
-- Esperado: OK

-- ---------------------------------------------------------------------
-- 1. Restaurar el estado
--
--    3 = EstadoFactura.APROBADA_HACIENDA
--    La condición sobre estado_factura = 8 evita reaplicarlo.
-- ---------------------------------------------------------------------

UPDATE searmedica.factura f
SET estado_factura = 3
WHERE f.id_factura = 268647
  AND f.estado_factura = 8
  AND EXISTS (
        SELECT 1
        FROM searmedica.factura_nota_debito nd
        WHERE nd.id_nota_debito = 45
          AND nd.id_estado = 40   -- Aprobada Hacienda
      );
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- DESPUÉS
-- ---------------------------------------------------------------------

SELECT 'DESPUES factura' AS bloque, id_factura, estado_factura,
       numero_consecutivo, clave,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, monto_restante, login
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado_factura = 3 (Aprobada Hacienda)
--           venta / neta / comprobante / exento / merc_exentas = 21150.000
--           gravado y merc_gravadas = 0.000, monto_restante = 21150.000

-- ¿Aparecerá en la pantalla de anulación? Réplica del filtro de
-- ServicioFactura.obtenerFacturasAnulacion: excluye los estados 8 y 2,
-- exige numero_consecutivo y filtra por login.
SELECT 'DESPUES prueba pantalla anulacion' AS bloque,
       count(*) AS la_vera_el_usuario_allan
FROM searmedica.factura ft
WHERE ft.id_factura = 268647
  AND ft.estado_factura <> 8
  AND ft.estado_factura <> 2
  AND ft.login = 'allan'
  AND ft.numero_consecutivo IS NOT NULL;
-- Esperado: 1

SELECT 'DESPUES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Quedaron tomadas por la nota de débito (0 / 1 / 31). Para la nueva
-- nota de crédito no estorba: determinarLineasNoModificables y
-- obtenerDetalleFacturaBusqueda miran es_para_nota_credito, que está en 0.

-- =====================================================================
-- Cambiar por COMMIT cuando el bloque DESPUES se vea correcto.
-- =====================================================================
ROLLBACK;
-- COMMIT;
