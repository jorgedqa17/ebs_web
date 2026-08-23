-- =====================================================================
-- Reintentar la NOTA DE DÉBITO que anula la NC 8398
--
--   NC  : 8398 / 00100001030000008553  (a corregir en Hacienda)
--   ND  : 44   / 00100001020000000033  (RECHAZADA, consecutivo duplicado)
--
-- POR QUÉ FALLÓ
--
--   -99, "La numeración consecutiva 00100001020000000033 del comprobante
--         que se está utilizando para este archivo XML ya existe en
--         nuestras bases de datos desde el día 29-08-2025 20:49:54 en la
--         clave 50629082500310222921600100001020000000033153818306"
--
-- La secuencia local viene de la copia de producción (última ND: 32),
-- pero el ambiente de pruebas de Hacienda ya consumió ese número en
-- agosto de 2025, con notas de débito emitidas desde otra base.
-- ServicioFactura.yaExisteNumeroConsecutivoNotaDebito solo valida contra
-- la base local, así que no lo detecta.
--
-- No falló con la factura ni con la nota de crédito porque esos
-- consecutivos van muy adelante en producción; las ND son escasas
-- (43 en años) y las pruebas de 2025 los rebasaron.
--
-- QUÉ HACE ESTE SCRIPT
--
--   1. Adelanta seq_nota_debito a 100, con margen sobre cualquier prueba
--      de 2025. La próxima ND tomará 00100001020000000101.
--   2. Devuelve la NC 8398 a estado 12 (Rechazada). Hoy está en 19
--      porque guardarNotaDebito la anula AL GUARDAR y no al aprobarse
--      (ServicioNotaDebito.java:263), pero Hacienda nunca aceptó esa
--      anulación.
--   3. Devuelve las líneas a 1 / 0 / 46, como estaban antes del intento,
--      para que la NC vuelva a ser seleccionable en la pantalla de ND.
--
-- NO SE TOCA: la factura (la ND va contra la NC, no contra la factura:
-- la ND 44 tiene id_factura NULL e id_anulacion 8398), ni la ND 44, que
-- queda en estado 45 como registro del intento fallido.
--
-- ATENCIÓN: setval NO respeta la transacción. El salto de la secuencia
-- se aplica aunque el script termine en ROLLBACK. Es idempotente, así
-- que correrlo dos veces no hace daño.
--
-- TERMINA EN ROLLBACK A PROPÓSITO.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES
-- ---------------------------------------------------------------------

SELECT 'ANTES secuencia' AS bloque, last_value AS seq_nota_debito
FROM searmedica.seq_nota_debito;
-- Esperado: 33

SELECT 'ANTES NC 8398' AS bloque, id_anulacion, numero_consecutivo, id_estado,
       total_venta, total_venta_neta, total_gravado, total_exento, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8398;
-- Esperado: id_estado 19 (Anulada); totales ya corregidos:
--           venta y neta 21150.000, gravado 0.000, exento 21150.000

SELECT 'ANTES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: 1 / 0 / 31

SELECT 'ANTES ND 44 (queda como registro)' AS bloque, id_nota_debito,
       numero_consecutivo, id_estado, id_anulacion, id_factura
FROM searmedica.factura_nota_debito
WHERE id_nota_debito = 44;
-- Esperado: estado 45, id_anulacion 8398, id_factura NULL

-- ---------------------------------------------------------------------
-- 1. Adelantar la secuencia de notas de débito
--
--    setval(100) deja el próximo nextval en 101, así que el consecutivo
--    será 001 + 00001 + 02 + 0000000101 = 00100001020000000101
-- ---------------------------------------------------------------------

SELECT 'SETVAL' AS bloque, setval('searmedica.seq_nota_debito', 100) AS nuevo_valor;
-- Esperado: 100. Recordar: esto NO se revierte con ROLLBACK.

-- ---------------------------------------------------------------------
-- 2. Devolver la nota de crédito a Rechazada
--
--    Su anulación no fue aceptada por Hacienda, así que no corresponde
--    dejarla en 19. En 12 vuelve a aparecer en la pantalla de notas de
--    débito, que excluye solo los estados 19 y 9.
-- ---------------------------------------------------------------------

UPDATE searmedica.factura_anulacion
SET id_estado = 12
WHERE id_anulacion = 8398
  AND id_estado = 19;   -- guardia
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- 3. Devolver las líneas al estado previo al intento
--
--    1 / 0 / 46: tomadas por la nota de crédito, en situación anormal,
--    que es como las dejó el servicio al rechazar la NC.
-- ---------------------------------------------------------------------

UPDATE searmedica.detalle_factura
SET es_para_nota_credito = 1,
    es_para_nota_debito  = 0,
    id_estado            = 46
WHERE id_factura = 268647;
-- Esperado: UPDATE 3

-- ---------------------------------------------------------------------
-- DESPUÉS
-- ---------------------------------------------------------------------

SELECT 'DESPUES secuencia' AS bloque, last_value AS seq_nota_debito
FROM searmedica.seq_nota_debito;
-- Esperado: 100 -> proximo consecutivo 00100001020000000101

SELECT 'DESPUES NC 8398' AS bloque, id_anulacion, numero_consecutivo, id_estado,
       total_venta, total_venta_neta, total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, total_impuesto, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8398;
-- Esperado: id_estado 12; venta y neta 21150.000; gravado y merc_gravadas
--           0.000; exento y merc_exentas 21150.000; NC 21150.000
--           Estos son los totales que heredará la nueva nota de débito.

SELECT 'DESPUES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: las 3 en 1 / 0 / 46

SELECT 'DESPUES factura (sin cambios)' AS bloque, estado_factura,
       total_venta, total_venta_neta, total_comprobante, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado 8 y totales en 0.000, sin tocar. La nota de débito
-- va contra la nota de crédito, no contra la factura.

-- Identidades que heredará la nueva ND y que valida Hacienda
SELECT 'DESPUES identidades de la futura ND' AS bloque,
       total_gravado + total_exento + total_exonerado AS suma_clasificaciones,
       total_venta,
       total_venta_neta + total_impuesto AS neta_mas_impuesto,
       total_nota_credito,
       CASE WHEN total_gravado + total_exento + total_exonerado = total_venta
             AND total_venta_neta + total_impuesto = total_nota_credito
            THEN 'CUADRA'
            ELSE 'NO CUADRA - revisar' END AS diagnostico
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8398;
-- Esperado: CUADRA

-- =====================================================================
-- Cambiar por COMMIT cuando los bloques DESPUES se vean correctos.
-- Recordar que el SETVAL ya quedó aplicado aunque esto haga ROLLBACK.
-- =====================================================================
ROLLBACK;
-- COMMIT;
