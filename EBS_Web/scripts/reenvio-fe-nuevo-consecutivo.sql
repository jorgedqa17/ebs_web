-- =====================================================================
-- Reenviar la factura 268647 a Hacienda DESARROLLO con consecutivo nuevo
--
-- Contexto: el primer intento se envió sin líneas de detalle. La consulta
-- del servicio (ebs_servicios, Consultas.obtenerDetalleFactura, rama de
-- factura) exige:
--
--     WHERE id_factura = ?
--       AND es_para_nota_credito = 0   -- PARA_NOTA_CREDITO_QUITAR
--       AND es_para_nota_debito  = 0   -- PARA_NOTA_DEBITO_QUITAR
--       AND id_estado            = 30  -- EstadosLineasFactura.NORMAL
--
-- Las líneas estaban en es_para_nota_credito = 1 (tomadas por la NC 8321),
-- así que no calzaban y el XML salió sin detalle.
--
-- Este script:
--   1. Devuelve las 3 líneas a sus valores originales, copiándolos de
--      detalle_factura_trazabilidad (id_factura_trazabilidad = 243948).
--   2. Reafirma los totales de la cabecera desde factura_trazabilidad.
--   3. Genera consecutivo y clave NUEVOS, porque el anterior ya se envió.
--   4. Deja la factura en estado 2 (Pendiente de Envío Hacienda).
--
-- OJO: este script NO es idempotente. El paso 3 consume un nextval de
-- searmedica.seq_factura_hacienda; cada corrida gasta un consecutivo.
-- Por eso el bloque de verificación en seco va ANTES del UPDATE.
--
-- TERMINA EN ROLLBACK A PROPÓSITO. Correrlo así primero, revisar, y solo
-- entonces cambiar ROLLBACK por COMMIT.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES
-- ---------------------------------------------------------------------

SELECT 'ANTES factura' AS bloque, id_factura, estado_factura,
       numero_consecutivo, clave, fecha_emision,
       total_venta, total_comprobante, total_exento, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;

SELECT 'ANTES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Hoy: es_para_nota_credito = 1 -> por eso el servicio no las trajo

SELECT 'ANTES secuencia' AS bloque, last_value AS seq_factura_hacienda
FROM searmedica.seq_factura_hacienda;
-- El consecutivo nuevo será 00100001010000 + (last_value + 1)

-- ---------------------------------------------------------------------
-- 1. Devolver las líneas a sus valores originales
--
--    Se copian de la trazabilidad de creación en vez de escribirlos a
--    mano. Ahí están en 0 / 0 / 30, que es justo lo que la consulta del
--    servicio necesita.
-- ---------------------------------------------------------------------

UPDATE searmedica.detalle_factura d
SET es_para_nota_credito = t.es_para_nota_credito,
    es_para_nota_debito  = t.es_para_nota_debito,
    id_estado            = t.id_estado
FROM searmedica.detalle_factura_trazabilidad t
WHERE d.id_factura = 268647
  AND t.id_factura_trazabilidad = 243948
  AND t.numero_linea = d.numero_linea
  AND t.id_producto  = d.id_producto;
-- Esperado: UPDATE 3

-- ---------------------------------------------------------------------
-- 2. Reafirmar los totales de la cabecera (idempotente)
-- ---------------------------------------------------------------------

UPDATE searmedica.factura f
SET total_descuentos            = t.total_descuentos,
    total_impuestos             = t.total_impuestos,
    total_venta                 = t.total_venta,
    total_venta_neta            = t.total_venta_neta,
    total_servicios_grabados    = t.total_servicios_grabados,
    total_servicios_exentos     = t.total_servicios_exentos,
    total_mercancias_gravadas   = t.total_mercancias_gravadas,
    total_mercancias_exentas    = t.total_mercancias_exentas,
    total_gravado               = t.total_gravado,
    total_exento                = t.total_exento,
    total_comprobante           = t.total_comprobante,
    total_servicios_exonerados  = t.total_servicios_exonerados,
    total_mercancias_exonerados = t.total_mercancias_exonerados,
    total_exonerado             = t.total_exonerado,
    monto_restante              = t.monto_restante
FROM searmedica.factura_trazabilidad t
WHERE f.id_factura = 268647
  AND t.id_factura_trazabilidad = 243948;
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- 3. Consecutivo, clave y fecha de emisión nuevos + estado pendiente
--
--    Réplica exacta de ServicioFactura.construirNumeroConsecutivo y
--    construirClaveNumerica:
--
--      consecutivo = "001" (casa matriz)
--                  + "00001" (terminal central)
--                  + "01" (tipo documento: factura electrónica)
--                  + nextval(seq_factura_hacienda) rellenado a 10
--
--      clave = "506" + DD + MM + YY + cédula emisor (12)
--            + consecutivo (20) + situación (1) + código seguridad (8)
--
--    La cédula se extrae de la clave anterior (posiciones 10-21) para no
--    escribirla a mano. El código de seguridad son 8 dígitos aleatorios,
--    igual que obtenerCodigoSeguridad().
-- ---------------------------------------------------------------------

WITH nuevo AS (
    SELECT '001' || '00001' || '01'
           || lpad(nextval('searmedica.seq_factura_hacienda')::text, 10, '0') AS consecutivo
)
UPDATE searmedica.factura f
SET numero_consecutivo = n.consecutivo,
    clave              = '506'
                         || to_char(now(), 'DDMM')
                         || lpad((extract(year from now())::int % 100)::text, 2, '0')
                         || substring(f.clave from 10 for 12)
                         || n.consecutivo
                         || '1'
                         || lpad(floor(random() * 100000000)::bigint::text, 8, '0'),
    fecha_emision      = to_char(now(), 'YYYY-MM-DD"T"HH24:MI:SS') || '-06:00',
    estado_factura     = 2
FROM nuevo n
WHERE f.id_factura = 268647;
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- DESPUÉS
-- ---------------------------------------------------------------------

SELECT 'DESPUES factura' AS bloque, id_factura, estado_factura,
       numero_consecutivo, clave, fecha_emision,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_exento, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado 2; consecutivo 00100001010000115522 (o el siguiente
--           libre); clave de 50 dígitos con la fecha de hoy;
--           venta/neta/comprobante/exento = 21150.000; gravado 0.000

SELECT 'DESPUES clave descompuesta' AS bloque,
       substring(clave from 1  for 3)  AS pais,
       substring(clave from 4  for 6)  AS ddmmyy,
       substring(clave from 10 for 12) AS cedula_emisor,
       substring(clave from 22 for 20) AS consecutivo,
       substring(clave from 42 for 1)  AS situacion,
       substring(clave from 43 for 8)  AS codigo_seguridad,
       length(clave)                   AS largo
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: pais 506, largo 50, situacion 1, y consecutivo igual al de
--           la columna numero_consecutivo

SELECT 'DESPUES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado,
       cantidad, precio_neto, sub_total, total_linea, es_linea_exenta
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: las 3 en 0 / 0 / 30, que es lo que exige la consulta del
--           servicio. Totales 15600 / 3750 / 1800.

-- Simulación de la consulta que hace ebs_servicios: debe devolver 3 filas
SELECT 'DESPUES prueba consulta del servicio' AS bloque,
       count(*) AS lineas_que_vera_el_servicio
FROM searmedica.detalle_factura
WHERE id_factura = 268647
  AND es_para_nota_credito = 0
  AND es_para_nota_debito  = 0
  AND id_estado            = 30;
-- Esperado: 3

-- =====================================================================
-- Cambiar por COMMIT cuando los bloques DESPUES se vean correctos.
-- Recordar: cada corrida consume un nextval de la secuencia.
-- =====================================================================
ROLLBACK;
-- COMMIT;
