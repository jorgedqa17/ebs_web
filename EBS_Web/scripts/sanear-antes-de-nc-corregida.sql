-- =====================================================================
-- Sanear factura y nota de crédito antes de emitir la NC corregida
--
--   Factura : 268647 / 00100001010000115523  (estado 3, totales 21150)
--   NC 8398 : 00100001030000008553           (estado 19, anulada por ND)
--   ND 45   : 00100001020000000101           (estado 40, aprobada)
--
-- QUÉ ESTÁ FUERA DE LUGAR
--
-- Solo las líneas de detalle. Quedaron en:
--     es_para_nota_credito = 0, es_para_nota_debito = 1, id_estado = 31
--
-- Es decir "pendientes de envío dentro de una nota de débito", pero la
-- ND 45 ya fue aprobada por Hacienda. El servicio debía dejarlas en
-- FINALIZADA (33) al aprobarse, pero su consulta
-- (Transacciones.actualizarEstadoNotaDebitoLineas) exige
-- T3.id_estado = ENVIADA (32) y estaban en 31, así que no las alcanzó.
--
-- El estado limpio es 0 / 0 / 30 (Normal), que es como estaban al
-- crearse la factura según detalle_factura_trazabilidad.
--
-- LA FACTURA Y LA NC YA ESTÁN BIEN. Se verifican pero no se tocan:
--   factura : estado 3, venta/neta/comprobante/exento = 21150.000,
--             gravado 0.000, monto_restante 21150.000
--   NC 8398 : estado 19 (Anulada), con respaldo fiscal de la ND aprobada
--
-- NO SE TOCA la NC 8321 (00100001030000008481): es la de producción, con
-- sus totales rotos, y se conserva como registro del incidente.
--
-- TERMINA EN ROLLBACK A PROPÓSITO.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES
-- ---------------------------------------------------------------------

SELECT 'ANTES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado,
       cantidad, precio_neto, sub_total, total_linea, es_linea_exenta, id_impuesto
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: 0 / 1 / 31. El producto 6991 conserva id_impuesto = 1 a
-- propósito: es el dato sucio que debe soportar el fix de clasificación.

SELECT 'ANTES factura (no se toca)' AS bloque, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_exento, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado 3 y totales en 21150.000 / 0.000

SELECT 'ANTES notas de credito (no se tocan)' AS bloque,
       id_anulacion, numero_consecutivo, id_estado,
       total_venta, total_gravado, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_factura = 268647
ORDER BY id_anulacion;
-- Esperado: 8321 en 12 con los totales rotos (registro de produccion)
--           8398 en 19, anulada y con totales corregidos

SELECT 'ANTES notas de debito (no se tocan)' AS bloque,
       id_nota_debito, numero_consecutivo, id_estado
FROM searmedica.factura_nota_debito
WHERE id_anulacion IN (8321, 8398)
ORDER BY id_nota_debito;
-- Esperado: 44 en 45 (anulada, intento fallido), 45 en 40 (aprobada)

-- ---------------------------------------------------------------------
-- 1. Liberar las líneas
--
--    es_para_nota_credito = 0  -> disponibles para una nueva NC
--    es_para_nota_debito  = 0  -> ya no penden de ninguna ND
--    id_estado            = 30 -> EstadosLineasFactura.NORMAL
--
--    Los montos NO se tocan.
-- ---------------------------------------------------------------------

UPDATE searmedica.detalle_factura
SET es_para_nota_credito = 0,
    es_para_nota_debito  = 0,
    id_estado            = 30
WHERE id_factura = 268647
  AND id_estado <> 30;   -- guardia contra reaplicación
-- Esperado: UPDATE 3

-- ---------------------------------------------------------------------
-- DESPUÉS
-- ---------------------------------------------------------------------

SELECT 'DESPUES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado,
       cantidad, precio_neto, sub_total, total_linea
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: las 3 en 0 / 0 / 30, montos 15600 / 3750 / 1800 sin cambios

SELECT 'DESPUES factura' AS bloque, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: sin cambios. Estado 3, totales 21150.000 / 0.000

-- ¿La verá el usuario en la pantalla de anulación?
-- Réplica del filtro de ServicioFactura.obtenerFacturasAnulacion.
SELECT 'DESPUES prueba pantalla anulacion' AS bloque,
       count(*) AS la_vera_allan
FROM searmedica.factura ft
WHERE ft.id_factura = 268647
  AND ft.estado_factura <> 8
  AND ft.estado_factura <> 2
  AND ft.login = 'allan'
  AND ft.numero_consecutivo IS NOT NULL;
-- Esperado: 1

-- ¿Podrá seleccionar las líneas para la nueva nota de crédito?
-- Réplica de ServicioFactura.obtenerDetalleFacturaBusqueda, que alimenta
-- listaProductoPorFacturar y exige es_para_nota_credito = 0.
SELECT 'DESPUES prueba lineas seleccionables' AS bloque,
       count(*) AS lineas_disponibles
FROM searmedica.detalle_factura
WHERE id_factura = 268647
  AND es_para_nota_credito = 0;
-- Esperado: 3

-- Próximos consecutivos que se van a usar
SELECT 'DESPUES proximos consecutivos' AS bloque,
       '001' || '00001' || '03'
         || lpad(((SELECT last_value FROM searmedica.seq_nota_credito) + 1)::text, 10, '0')
         AS proxima_nota_credito;
-- Esperado: 00100001030000008554

-- =====================================================================
-- Cambiar por COMMIT cuando los bloques DESPUES se vean correctos.
-- =====================================================================
ROLLBACK;
-- COMMIT;
