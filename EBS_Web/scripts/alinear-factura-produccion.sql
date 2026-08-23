-- =====================================================================
-- Alinear la factura 268647 con el estado post-NC de PRODUCCIÓN
--
--   Factura : 268647 / 00100001010000115523
--   NC      : 8398   / 00100001030000008553  (rechazada en desarrollo)
--
-- Al aplicar la NC se usó la resta ya corregida (cada campo contra su
-- homólogo), y quedó total_venta = -1800. Producción traía el bug de
-- campos cruzados, que dejaba total_venta = -24750:
--
--     neta  = 21150 - 22950 = -1800
--     venta = (-1800) - 22950 = -24750     <- lee la neta recién escrita
--
-- Este script reproduce ese único campo para que el ambiente de pruebas
-- sea fiel al incidente antes de ensayar la nota de débito.
--
-- OJO: reintroduce el bug SOLO EN LOS DATOS. El código sigue corregido
-- (ServicioFactura.calcularMontosParaFacturaDespuesDeNotaCredito), así
-- que ninguna nota de crédito nueva volverá a producir un -24750.
--
-- NO ES IDEMPOTENTE: una segunda corrida volvería a restar 22950 y
-- dejaría -47700. Revisar el bloque ANTES antes de aplicar.
--
-- TERMINA EN ROLLBACK A PROPÓSITO.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES — guardia contra doble aplicación
-- ---------------------------------------------------------------------

SELECT 'ANTES factura' AS bloque, estado_factura,
       total_descuentos, total_impuestos,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas,
       total_exonerado, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado ANTES de aplicar: total_venta = -1800.000
-- Si ya dice -24750.000, el script YA se aplicó: no volver a correrlo.

SELECT 'GUARDIA' AS bloque,
       CASE
         WHEN total_venta = -1800.000  THEN 'OK - listo para aplicar'
         WHEN total_venta = -24750.000 THEN 'YA APLICADO - no correr de nuevo'
         ELSE 'VALOR INESPERADO - revisar antes de continuar'
       END AS diagnostico,
       total_venta
FROM searmedica.factura
WHERE id_factura = 268647;

SELECT 'ANTES NC 8398' AS bloque, id_anulacion, numero_consecutivo, id_estado,
       total_venta, total_venta_neta, total_gravado, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8398;
-- Esperado: estado 12 (Rechazada), venta y neta 22950.000, gravado 1800.000

SELECT 'ANTES lineas (no se tocan)' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado,
       sub_total, total_linea
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: 1 / 0 / 46 con 15600 / 3750 / 1800 — ya iguales a producción

-- ---------------------------------------------------------------------
-- 1. Reproducir el campo cruzado
--
--    Se expresa con la fórmula, no con la constante, para que quede a la
--    vista de dónde sale el número:
--      total_venta = total_venta_neta (ya en -1800) - nc.total_venta_neta
-- ---------------------------------------------------------------------

UPDATE searmedica.factura f
SET total_venta = f.total_venta_neta - a.total_venta_neta
FROM searmedica.factura_anulacion a
WHERE f.id_factura = 268647
  AND a.id_anulacion = 8398
  AND f.total_venta = -1800.000;   -- guardia: solo si no se aplicó antes
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- DESPUÉS
-- ---------------------------------------------------------------------

SELECT 'DESPUES factura' AS bloque, estado_factura,
       total_descuentos, total_impuestos,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas,
       total_servicios_grabados, total_servicios_exentos,
       total_exonerado, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado, idéntico a producción post-NC:
--   estado_factura            = 8
--   total_venta               = -24750.000   <- el campo alineado
--   total_venta_neta          = -1800.000
--   total_comprobante         = 0.000
--   total_gravado             = -1800.000
--   total_mercancias_gravadas = -1800.000
--   total_exento              = 0.000
--   total_mercancias_exentas  = 0.000
--   monto_restante            = 0.000
--   descuentos, impuestos, servicios y exonerados = 0.000

SELECT 'DESPUES lineas (sin cambios)' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado,
       cantidad, precio_neto, sub_total, total_linea
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: las 3 en 1 / 0 / 46, montos 15600 / 3750 / 1800

-- =====================================================================
-- Cambiar por COMMIT cuando el bloque DESPUES se vea correcto.
-- =====================================================================
ROLLBACK;
-- COMMIT;
