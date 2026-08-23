-- =====================================================================
-- Corregir los datos para que la NOTA DE DÉBITO sea aceptada
--
--   Factura : 268647 / 00100001010000115523  (aceptada en desarrollo)
--   NC      : 8398   / 00100001030000008553  (rechazada, -111 y -55)
--
-- POR QUÉ HACE FALTA
--
-- BeanNotaDebito NO recalcula: copia los 14 totales de la nota de crédito
-- (BeanNotaDebito.java:155-168). Y el servicio arma el XML de la ND
-- leyendo FACTURA_NOTA_DEBITO directamente. Con la NC en 22950 / 1800,
-- la ND heredaría esos valores y Hacienda la rechazaría con los mismos
-- -111 y -55, dejando la NC sin anular.
--
-- QUÉ HACE ESTE SCRIPT
--
--   Bloque 1: corrige los 4 totales de la NC 8398 a los que produce el
--             clasificador ya arreglado sobre sus 3 líneas exentas.
--             La ND los hereda y Hacienda la acepta.
--
--   Bloque 2: deja la factura en ceros, que es donde la habría dejado una
--             NC correcta (21150 - 21150 = 0 en cada campo). Así, cuando
--             la ND sume sus 21150, la factura vuelve EXACTA al original.
--
-- Sin el bloque 2 la ND igual se acepta, pero la factura terminaría en
-- total_venta = -3600 y total_venta_neta = 19350, porque arrastra el daño
-- del bug de campos cruzados.
--
-- OJO: la NC 8398 dejará de reflejar los montos que efectivamente se
-- enviaron y fueron rechazados. El XML y la respuesta de Hacienda quedan
-- guardados en factura_anulacion_historico_hacienda, así que la evidencia
-- del incidente no se pierde.
--
-- NO ES IDEMPOTENTE en el sentido de que asume los valores actuales.
-- Los UPDATE traen guardias para no aplicarse dos veces.
--
-- TERMINA EN ROLLBACK A PROPÓSITO.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES
-- ---------------------------------------------------------------------

SELECT 'ANTES NC 8398' AS bloque, id_anulacion, numero_consecutivo, id_estado,
       total_venta, total_venta_neta, total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, total_impuesto, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8398;
-- Esperado ANTES: venta y neta 22950.000, gravado y merc_gravadas 1800.000

SELECT 'ANTES factura' AS bloque, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado ANTES: venta -24750.000, neta -1800.000, gravado -1800.000

SELECT 'GUARDIA' AS bloque,
       CASE WHEN total_gravado = 1800.000 THEN 'OK - listo para aplicar'
            WHEN total_gravado = 0.000    THEN 'YA APLICADO - no correr de nuevo'
            ELSE 'VALOR INESPERADO - revisar' END AS diagnostico
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8398;

-- ---------------------------------------------------------------------
-- 1. Corregir los totales de la nota de crédito
--
--    Sus 3 líneas son exentas (es_linea_exenta = 1) y sus CABYS son de
--    tarifa 0 según el catálogo de Hacienda, así que la clasificación
--    correcta es: todo a exento, nada a gravado.
--
--      gravado / mercancías gravadas -> 0
--      venta / venta neta            -> 21150 (= exento)
--
--    Los demás campos ya están bien y no se tocan.
-- ---------------------------------------------------------------------

UPDATE searmedica.factura_anulacion
SET total_gravado             = 0.000,
    total_mercancias_gravadas = 0.000,
    total_venta               = total_exento,      -- 21150.000
    total_venta_neta          = total_exento       -- 21150.000, no hay descuentos
WHERE id_anulacion = 8398
  AND total_gravado = 1800.000;   -- guardia contra doble aplicación
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- 2. Dejar la factura donde la habría dejado una NC correcta
--
--    Es una anulación total: factura (21150) menos NC correcta (21150)
--    da cero en cada campo. Partiendo de ahí, la ND devuelve la factura
--    exactamente a su valor original.
-- ---------------------------------------------------------------------

UPDATE searmedica.factura
SET total_descuentos            = 0.000,
    total_impuestos             = 0.000,
    total_venta                 = 0.000,
    total_venta_neta            = 0.000,
    total_servicios_grabados    = 0.000,
    total_servicios_exentos     = 0.000,
    total_mercancias_gravadas   = 0.000,
    total_mercancias_exentas    = 0.000,
    total_gravado               = 0.000,
    total_exento                = 0.000,
    total_comprobante           = 0.000,
    total_servicios_exonerados  = 0.000,
    total_mercancias_exonerados = 0.000,
    total_exonerado             = 0.000,
    monto_restante              = 0.000
WHERE id_factura = 268647;
-- Esperado: UPDATE 1
-- estado_factura se deja en 8 (Anulada): es el estado correcto mientras
-- la nota de débito no haya sido aprobada.

-- ---------------------------------------------------------------------
-- DESPUÉS
-- ---------------------------------------------------------------------

SELECT 'DESPUES NC 8398' AS bloque, id_anulacion, id_estado,
       total_descuento, total_impuesto,
       total_venta, total_venta_neta,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8398;
-- Esperado: venta y neta 21150.000; gravado y merc_gravadas 0.000;
--           exento y merc_exentas 21150.000; NC 21150.000
--           Estos son los valores que heredará la nota de débito.

SELECT 'DESPUES factura' AS bloque, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado 8 y TODOS los totales en 0.000

SELECT 'DESPUES lineas (sin cambios)' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado, sub_total
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: 1 / 0 / 46 con 15600 / 3750 / 1800.
-- No se tocan: guardarNotaDebito las pasa a 0 / 1 / 31 al crear la ND,
-- y obtenerLineasDetalleNotaCredito no filtra por estado, así que el 46
-- no impide encontrarlas.

-- Verificación de las identidades que valida Hacienda sobre la ND
SELECT 'DESPUES identidades que hereda la ND' AS bloque,
       total_gravado + total_exento + total_exonerado AS suma_clasificaciones,
       total_venta,
       total_venta_neta + total_impuesto AS neta_mas_impuesto,
       total_nota_credito,
       CASE WHEN total_gravado + total_exento + total_exonerado = total_venta
             AND total_venta_neta + total_impuesto = total_nota_credito
            THEN 'CUADRA - la ND deberia ser aceptada'
            ELSE 'NO CUADRA - revisar' END AS diagnostico
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8398;
-- Esperado: CUADRA

-- =====================================================================
-- Cambiar por COMMIT cuando los bloques DESPUES se vean correctos.
-- =====================================================================
ROLLBACK;
-- COMMIT;
