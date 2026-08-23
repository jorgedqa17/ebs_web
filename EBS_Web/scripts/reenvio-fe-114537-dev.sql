-- =====================================================================
-- Devolver la FE 114537 a su estado original para reenviarla a
-- Hacienda DESARROLLO
--
--   Factura : 268647 / 00100001010000114537
--   Clave   : 50616072600310222921600100001010000114537194330614
--
-- El bug de la nota de crédito dejó la factura con totales negativos y
-- en estado 3 (ya aprobada en producción). Este script la devuelve al
-- estado de recién creada para que ServiciosRestFul la levante y la
-- envíe al ambiente de pruebas.
--
-- Los valores originales NO se escriben a mano: se copian desde
-- searmedica.factura_trazabilidad, fila 243948 (tipo_operacion =
-- 'CREACION'), que quedó intacta porque
-- calcularMontosParaFacturaDespuesDeNotaCredito solo toca la tabla
-- factura. Coinciden exactamente con el XML que Hacienda aceptó.
--
-- NO se tocan: clave, numero_consecutivo, fecha_emision,
-- envio_respuesta_hacienda, el detalle de la factura ni la NC 8321.
--
-- TERMINA EN ROLLBACK A PROPÓSITO. Correrlo así primero, revisar los
-- bloques DESPUES, y solo entonces cambiar ROLLBACK por COMMIT.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 0. ANTES
-- ---------------------------------------------------------------------

SELECT 'ANTES factura' AS bloque, id_factura, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_exento, total_mercancias_exentas,
       monto_restante, envio_respuesta_hacienda
FROM searmedica.factura
WHERE id_factura = 268647;
-- Hoy: estado 3, venta -24750.000, neta -1800.000, gravado -1800.000,
--      comprobante 0.000, exento 0.000, monto_restante 0.000

SELECT 'ANTES trazabilidad (origen de los valores)' AS bloque,
       id_factura_trazabilidad, tipo_operacion, estado_factura,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_exento, total_mercancias_exentas, monto_restante
FROM searmedica.factura_trazabilidad
WHERE id_factura_trazabilidad = 243948;
-- Esperado: CREACION, estado 2, todos los montos en 21150

-- Identidad de la factura: debe quedar igual al final
SELECT 'ANTES identidad' AS bloque, clave, numero_consecutivo, fecha_emision
FROM searmedica.factura
WHERE id_factura = 268647;

-- ---------------------------------------------------------------------
-- 1. Restaurar los totales y el estado
--
--    estado_factura = 2 (Pagada Pendiente Envío Hacienda) es lo que hace
--    que el proceso externo la levante. El histórico de esta misma
--    factura confirma la secuencia 2 -> 6 (Enviada) -> 3 (Aprobada).
-- ---------------------------------------------------------------------

UPDATE searmedica.factura f
SET estado_factura              = 2,
    total_descuentos            = t.total_descuentos,
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
-- DESPUÉS
-- ---------------------------------------------------------------------

SELECT 'DESPUES factura' AS bloque, id_factura, estado_factura,
       total_descuentos, total_impuestos,
       total_venta, total_venta_neta, total_comprobante,
       total_gravado, total_exento,
       total_mercancias_gravadas, total_mercancias_exentas,
       total_exonerado, monto_restante, envio_respuesta_hacienda
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: estado 2
--           venta / neta / comprobante / exento / merc_exentas = 21150.000
--           gravado / merc_gravadas / impuestos / descuentos / exonerado = 0.000
--           monto_restante = 21150.00
--           envio_respuesta_hacienda = 1 (sin cambios, a propósito)

-- La identidad del comprobante NO debe haber cambiado
SELECT 'DESPUES identidad' AS bloque, clave, numero_consecutivo, fecha_emision
FROM searmedica.factura
WHERE id_factura = 268647;
-- Esperado: clave 50616072600310222921600100001010000114537194330614
--           consecutivo 00100001010000114537
--           fecha_emision 2026-07-16T08:54:06-06:00

-- El detalle y la nota de crédito quedan intactos: es la nota de débito
-- la que debe liberar las líneas, y ese es justamente el paso a probar
SELECT 'DESPUES detalle (sin cambios)' AS bloque, numero_linea, id_producto,
       es_para_nota_credito, es_para_nota_debito, id_estado
FROM searmedica.detalle_factura
WHERE id_factura = 268647
ORDER BY numero_linea;
-- Esperado: las 3 con es_para_nota_credito = 1, es_para_nota_debito = 0, id_estado = 30

SELECT 'DESPUES nota credito (sin cambios)' AS bloque,
       id_anulacion, numero_consecutivo, id_estado
FROM searmedica.factura_anulacion
WHERE id_anulacion = 8321;
-- Esperado: id_estado = 12 (Rechazada Hacienda)

-- ---------------------------------------------------------------------
-- AVISO: correo a un cliente real
--
-- La factura tiene correo_electronico_cliente = facturas@guanacasteca.com
-- y las credenciales SMTP están cargadas (parámetros 6 y 7). La bandera
-- envio_respuesta_hacienda se deja en 1 ("correo ya enviado") justamente
-- para que el reenvío de prueba no le llegue al cliente.
--
-- Antes de levantar ServiciosRestFul, revisar este dato:
-- ---------------------------------------------------------------------

SELECT 'AVISO correo cliente' AS bloque, id_factura,
       correo_electronico_cliente, envio_respuesta_hacienda, envio_correo_electronico
FROM searmedica.factura
WHERE id_factura = 268647;

-- =====================================================================
-- Cambiar por COMMIT cuando los bloques DESPUES se vean correctos.
-- =====================================================================
ROLLBACK;
-- COMMIT;
