-- =====================================================================
-- Recrear la nota de crédito 8321 en Hacienda DESARROLLO
--
--   Factura : 268647 / 00100001010000115523  (ya ACEPTADA en desarrollo)
--   NC base : 8321   / 00100001030000008481  (rechazada en producción)
--
-- Se copia la 8321 tal cual, INCLUIDOS SUS TOTALES ROTOS
-- (total_gravado = 1800, total_venta = 22950), cambiando solo el
-- id_anulacion, el consecutivo, la clave y las fechas.
--
-- Los totales rotos son el objetivo: Consultas.obtenerFacturaParaNotaCredito
-- arma el ResumenFactura del XML leyendo FACTURA_ANULACION directamente,
-- sin recalcular. Así se reproducen los errores -111 y -55 en desarrollo.
--
-- La factura queda ANULADA (estado 8) con los totales que deja la versión
-- ya corregida de calcularMontosParaFacturaDespuesDeNotaCredito: cada
-- campo restado contra su homólogo, sin el cruce que había en producción.
--
-- NO SE TOCA: inventario, la NC 8321, ni factura_trazabilidad.
--
-- NO ES IDEMPOTENTE: cada corrida consume un nextval de seq_anulacion y
-- otro de seq_nota_credito. La corrida en seco ya quema un par.
--
-- TERMINA EN ROLLBACK A PROPÓSITO.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES
-- ---------------------------------------------------------------------

SELECT 'ANTES factura' AS bloque, id_factura, estado_factura,
       numero_consecutivo, clave,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado 3 (Aprobada), todos los montos en 21150 / 0

SELECT 'ANTES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: 0 / 0 / 30

SELECT 'ANTES NC 8321 (molde)' AS bloque, id_anulacion, numero_consecutivo,
       id_estado, total_gravado, total_exento, total_venta,
       total_venta_neta, total_impuesto, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8321;

SELECT 'ANTES secuencias' AS bloque,
       (SELECT last_value FROM searmedica.seq_anulacion)    AS seq_anulacion,
       (SELECT last_value FROM searmedica.seq_nota_credito) AS seq_nota_credito;

-- Diagnóstico de inventario: NO se toca, solo se deja constancia
SELECT 'DIAG inventario salida_detalle' AS bloque, count(*) AS filas
FROM searmedica.inventario_salida_detalle
WHERE id_factura = 268647;

SELECT 'DIAG inventario trazabilidad' AS bloque, count(*) AS filas
FROM searmedica.trazabilidad_producto
WHERE numero_consecutivo LIKE '268647-%';

-- ---------------------------------------------------------------------
-- 1. Crear la nota de crédito copiando la 8321
--
--    Réplica de construirNumeroConsecutivoNotaCredito:
--      "001" (casa matriz) + "00001" (terminal) + "03" (nota de crédito)
--      + nextval(seq_nota_credito) rellenado a 10
--
--    Réplica de construirClaveNumerica:
--      "506" + DD + MM + YY + cédula(12) + consecutivo(20)
--      + situación(1) + código de seguridad(8 aleatorio)
--
--    La cédula se extrae de la clave de la 8321 para no escribirla a mano.
-- ---------------------------------------------------------------------

INSERT INTO searmedica.factura_anulacion (
    id_anulacion, id_factura, motivo_anulacion, numero_consecutivo, clave,
    fecha_emision, id_estado, id_motivo_anulacion, login, id_bodega,
    fecha_anulacion, ip, nombre_maquina, envio_correo_electronico,
    id_tipo_doc_referencia, id_nota_debito,
    total_descuento, total_impuesto, total_venta_neta,
    total_servicios_grabados, total_servicios_exentos,
    total_mercancias_gravadas, total_mercancias_exentas,
    total_gravado, total_exento, total_nota_credito, total_venta,
    total_mercancias_exenonerados, total_servicios_exenonerados,
    total_exonerado, nota_credito_interna
)
SELECT
    nextval('searmedica.seq_anulacion'),
    a.id_factura,
    a.motivo_anulacion,
    '001' || '00001' || '03' || lpad(nextval('searmedica.seq_nota_credito')::text, 10, '0'),
    '506'
      || to_char(now(), 'DDMM')
      || lpad((extract(year from now())::int % 100)::text, 2, '0')
      || substring(a.clave from 10 for 12)
      || ('001' || '00001' || '03' || lpad(currval('searmedica.seq_nota_credito')::text, 10, '0'))
      || '1'
      || lpad(floor(random() * 100000000)::bigint::text, 8, '0'),
    to_char(now(), 'YYYY-MM-DD"T"HH24:MI:SS') || '-06:00',
    9,                       -- EstadoAnulacion.PENDIENTE_DE_ENVIO_HACIENDA
    a.id_motivo_anulacion,
    a.login,
    a.id_bodega,
    now(),
    a.ip,
    a.nombre_maquina,
    a.envio_correo_electronico,
    a.id_tipo_doc_referencia,
    a.id_nota_debito,
    -- Totales copiados TAL CUAL: son los rotos, y ese es el punto
    a.total_descuento, a.total_impuesto, a.total_venta_neta,
    a.total_servicios_grabados, a.total_servicios_exentos,
    a.total_mercancias_gravadas, a.total_mercancias_exentas,
    a.total_gravado, a.total_exento, a.total_nota_credito, a.total_venta,
    a.total_mercancias_exenonerados, a.total_servicios_exenonerados,
    a.total_exonerado, a.nota_credito_interna
FROM searmedica.factura_anulacion a
WHERE a.id_anulacion = 8321;
-- Esperado: INSERT 1

-- ---------------------------------------------------------------------
-- 2. Copiar las 3 líneas de la nota de crédito
-- ---------------------------------------------------------------------

INSERT INTO searmedica.detalle_factura_nota_credito (
    id_anulacion, numero_linea, id_producto, id_factura
)
SELECT currval('searmedica.seq_anulacion'), d.numero_linea, d.id_producto, d.id_factura
FROM searmedica.detalle_factura_nota_credito d
WHERE d.id_anulacion = 8321;
-- Esperado: INSERT 3

-- ---------------------------------------------------------------------
-- 3. Marcar las líneas de la factura como tomadas por la nota de crédito
--
--    Es lo que hace BeanAnulacionFactura al guardar, y lo que exige la
--    consulta de líneas del servicio para las notas de crédito:
--      es_para_nota_credito = 1  AND  id_estado = 31
-- ---------------------------------------------------------------------

UPDATE searmedica.detalle_factura
SET es_para_nota_credito = 1,
    id_estado            = 31
WHERE id_factura = 268647;
-- Esperado: UPDATE 3

-- ---------------------------------------------------------------------
-- 4. Restar la nota de crédito a la factura y anularla
--
--    Cada campo contra su homólogo, que es la versión ya corregida de
--    calcularMontosParaFacturaDespuesDeNotaCredito. Se resta desde la NC
--    recién creada en vez de escribir constantes.
-- ---------------------------------------------------------------------

UPDATE searmedica.factura f
SET estado_factura              = 8,   -- EstadoFactura.ANULADA
    total_descuentos            = f.total_descuentos            - a.total_descuento,
    total_impuestos             = f.total_impuestos             - a.total_impuesto,
    total_venta                 = f.total_venta                 - a.total_venta,
    total_venta_neta            = f.total_venta_neta            - a.total_venta_neta,
    total_servicios_grabados    = f.total_servicios_grabados    - a.total_servicios_grabados,
    total_servicios_exentos     = f.total_servicios_exentos     - a.total_servicios_exentos,
    total_mercancias_gravadas   = f.total_mercancias_gravadas   - a.total_mercancias_gravadas,
    total_mercancias_exentas    = f.total_mercancias_exentas    - a.total_mercancias_exentas,
    total_gravado               = f.total_gravado               - a.total_gravado,
    total_exento                = f.total_exento                - a.total_exento,
    total_comprobante           = f.total_comprobante           - a.total_nota_credito,
    total_servicios_exonerados  = f.total_servicios_exonerados  - a.total_servicios_exenonerados,
    total_mercancias_exonerados = f.total_mercancias_exonerados - a.total_mercancias_exenonerados,
    total_exonerado             = f.total_exonerado             - a.total_exonerado,
    monto_restante              = f.monto_restante              - a.total_nota_credito
FROM searmedica.factura_anulacion a
WHERE f.id_factura = 268647
  AND a.id_anulacion = currval('searmedica.seq_anulacion');
-- Esperado: UPDATE 1

-- ---------------------------------------------------------------------
-- 5. Histórico, igual que construirHistorico + guardarHistoricoAnulacion
-- ---------------------------------------------------------------------

INSERT INTO searmedica.factura_anulacion_historico_hacienda (
    id_anulacion, estado_factura, fecha, login, respuesta,
    detallerespuesta, documento_xml_firmado
)
SELECT currval('searmedica.seq_anulacion'), 9, now(), a.login, '', '', ''
FROM searmedica.factura_anulacion a
WHERE a.id_anulacion = currval('searmedica.seq_anulacion');
-- Esperado: INSERT 1

-- ---------------------------------------------------------------------
-- DESPUÉS
-- ---------------------------------------------------------------------

SELECT 'DESPUES NC nueva' AS bloque, id_anulacion, numero_consecutivo,
       clave, id_estado, fecha_emision,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas,
       total_venta, total_venta_neta, total_impuesto, total_nota_credito
FROM searmedica.factura_anulacion
WHERE id_anulacion = currval('searmedica.seq_anulacion');
-- Esperado: id_estado 9; consecutivo 001000010300000085XX;
--           gravado 1800.000, merc_gravadas 1800.000  <- los rotos
--           exento 21150.000, venta y neta 22950.000, NC 21150.000

SELECT 'DESPUES clave descompuesta' AS bloque,
       substring(clave from 1  for 3)  AS pais,
       substring(clave from 4  for 6)  AS ddmmyy,
       substring(clave from 10 for 12) AS cedula_emisor,
       substring(clave from 22 for 20) AS consecutivo,
       substring(clave from 42 for 1)  AS situacion,
       substring(clave from 43 for 8)  AS codigo_seguridad,
       length(clave)                   AS largo
FROM searmedica.factura_anulacion
WHERE id_anulacion = currval('searmedica.seq_anulacion');
-- Esperado: pais 506, largo 50, situacion 1, consecutivo igual al de la columna

SELECT 'DESPUES factura' AS bloque, id_factura, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_mercancias_gravadas,
       total_exento, total_mercancias_exentas, monto_restante
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado 8 (Anulada); venta y neta -1800.000;
--           comprobante 0.000; gravado y merc_gravadas -1800.000;
--           exento y merc_exentas 0.000; monto_restante 0.000

SELECT 'DESPUES lineas' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: 1 / 0 / 31

-- Simulación de la consulta de líneas que hace el servicio para la NC
SELECT 'DESPUES prueba consulta del servicio' AS bloque,
       count(*) AS lineas_que_vera_el_servicio
FROM searmedica.detalle_factura t1
INNER JOIN searmedica.detalle_factura_nota_credito t2
   ON t1.numero_linea = t2.numero_linea
  AND t1.id_producto  = t2.id_producto
  AND t1.id_factura   = t2.id_factura
WHERE t1.id_factura = 268647
  AND t1.es_para_nota_credito = 1
  AND t1.id_estado = 31
  AND t2.id_anulacion = currval('searmedica.seq_anulacion');
-- Esperado: 3

-- Referencia que llevará el XML: la clave de la factura, que debe ser la
-- que Hacienda desarrollo ya aceptó
SELECT 'DESPUES referencia al XML' AS bloque,
       clave AS clave_factura_referenciada, numero_consecutivo, fecha_emision
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: 50622082600310222921600100001010000115523197322853

-- =====================================================================
-- Cambiar por COMMIT cuando los bloques DESPUES se vean correctos.
-- =====================================================================
ROLLBACK;
-- COMMIT;
