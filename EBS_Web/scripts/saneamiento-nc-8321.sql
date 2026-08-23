-- =====================================================================
-- Saneamiento para reemitir la nota de crédito rechazada 8321
--
--   Factura   : 268647 / 00100001010000114537
--   Anulación : 8321   / 00100001030000008481  (Rechazada Hacienda)
--
-- La NC fue rechazada por Hacienda con los errores -111 y -55. La emisión
-- fallida dejó la factura anulada y con totales negativos, y sus líneas
-- tomadas, así que la pantalla de anulación ya no la muestra.
--
-- Este script deshace eso para poder reemitir con consecutivo nuevo.
--
-- TERMINA EN ROLLBACK A PROPÓSITO. Correrlo así primero, revisar los
-- bloques de verificación, y solo entonces cambiar ROLLBACK por COMMIT.
--
-- Requiere el fix de código desplegado antes de reemitir:
--   - UtilitarioNotasDebitoCredito.esLineaExenta (clasificación por línea)
--   - ServicioFactura.calcularMontosParaFacturaDespuesDeNotaCredito
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES — estado actual
-- ---------------------------------------------------------------------

-- Factura: hoy estado_factura = 8 (Anulada) y totales negativos
SELECT 'ANTES factura' AS bloque, id_factura, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_exento, total_mercancias_gravadas,
       total_mercancias_exentas, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;

-- Líneas: hoy es_para_nota_credito = 1 e id_estado = 46 ("Anormal")
SELECT 'ANTES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, id_estado, es_linea_exenta, id_impuesto, sub_total
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;

-- Anulación rechazada
SELECT 'ANTES anulacion' AS bloque, id_anulacion, numero_consecutivo, id_estado,
       total_gravado, total_exento, total_venta, total_venta_neta,
       total_impuesto, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8321;

-- ---------------------------------------------------------------------
-- 0.b DIAGNÓSTICO DE INVENTARIO — solo lectura, no se corrige nada
--
-- El inventario YA fue devuelto una vez por la NC rechazada:
--   inventario_salida_detalle  -> 0 filas (fueron eliminadas)
--   trazabilidad_producto      -> 3 INGRESO / POR NOTA CREDITO (6, 3 y 9 uds)
--
-- Como guardarAnulacion recorre inventario_salida_detalle y ya está vacía,
-- reemitir NO vuelve a devolver existencias. Estas consultas son para
-- confirmarlo antes de reemitir: si salida_detalle trajera filas, habría
-- que detenerse y revisar, porque entonces sí se devolvería dos veces.
-- ---------------------------------------------------------------------

SELECT 'DIAG salida_detalle (debe ser 0)' AS bloque, count(*) AS filas
FROM searmedica.inventario_salida_detalle
WHERE id_factura = 268647;

SELECT 'DIAG trazabilidad (debe ser 3)' AS bloque, id_producto, cantidad, gestion, tipo, fecha
FROM searmedica.trazabilidad_producto
WHERE numero_consecutivo = '268647-00100001010000114537'
ORDER BY id_producto;

-- ---------------------------------------------------------------------
-- a. Restaurar la factura a los totales del XML que Hacienda aceptó
--
--    estado_factura = 3 (Aprobada Hacienda) es imprescindible:
--    ServicioFactura.obtenerFacturasAnulacion excluye las ANULADA (8),
--    así que hoy la factura no aparece en la pantalla de anulación.
-- ---------------------------------------------------------------------

UPDATE searmedica.factura
SET estado_factura              = 3,
    total_venta                 = 21150.000,
    total_venta_neta            = 21150.000,
    total_comprobante           = 21150.000,
    total_exento                = 21150.000,
    total_mercancias_exentas    = 21150.000,
    total_gravado               = 0.000,
    total_mercancias_gravadas   = 0.000,
    total_servicios_grabados    = 0.000,
    total_servicios_exentos     = 0.000,
    total_descuentos            = 0.000,
    total_impuestos             = 0.000,
    total_exonerado             = 0.000,
    total_mercancias_exonerados = 0.000,
    total_servicios_exonerados  = 0.000,
    monto_restante              = 21150.000
WHERE id_factura = 268647;
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- b. Liberar las 3 líneas
--
--    es_para_nota_credito = 0 -> LineaDetalleEstado.PARA_NOTA_CREDITO_QUITAR
--    id_estado            = 30 -> "Normal", el valor de las facturas sanas
-- ---------------------------------------------------------------------

UPDATE searmedica.detalle_factura
SET es_para_nota_credito = 0,
    id_estado            = 30
WHERE id_factura = 268647;
-- Esperado: UPDATE 3

-- ---------------------------------------------------------------------
-- c. Descartar la anulación rechazada para que el proceso de envío no la
--    levante. La clave ...8481... ya está registrada en Hacienda como
--    Rechazada y no se puede reenviar.
--    id_estado = 19 -> EstadoAnulacion.ANULADA
-- ---------------------------------------------------------------------

UPDATE searmedica.factura_anulacion
SET id_estado = 19
WHERE id_anulacion = 8321;
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- DESPUÉS — verificación
-- ---------------------------------------------------------------------

SELECT 'DESPUES factura' AS bloque, id_factura, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_exento, total_mercancias_gravadas,
       total_mercancias_exentas, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado 3, venta/neta/comprobante/exento/merc_exentas = 21150.000,
--           gravado y merc_gravadas = 0.000, monto_restante = 21150.000

SELECT 'DESPUES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, id_estado
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: las 3 con es_para_nota_credito = 0 e id_estado = 30

SELECT 'DESPUES anulacion' AS bloque, id_anulacion, numero_consecutivo, id_estado
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8321;
-- Esperado: id_estado = 19

-- =====================================================================
-- Cambiar por COMMIT cuando los bloques DESPUES se vean correctos.
-- =====================================================================
ROLLBACK;
-- COMMIT;
