-- Anexos y Estructuras 4.4, bitacora de ajustes al 22/04/2026 (obligatorio 01/11/2026).
-- Nota 9: nuevos codigos de referencia para notas de credito y debito con efecto
-- contable en el mismo periodo del comprobante que se modifica.
--   13 Anula documento de referencia por error material (equivale a 01, anulacion total)
--   14 Corrige monto por error material                 (equivale a 02, ajuste parcial)
-- Los codigos 15 y 16 son para la factura nueva (facturacion.xhtml), no para NC/ND.

INSERT INTO searmedica.tipo_documento_referencia (id_tipo_doc_referencia, descripcion, codigo_hacienda)
SELECT 7, 'ANULA_DOCUMENTO_DE_REFERENCIA_POR_ERROR_MATERIAL', '13'
WHERE NOT EXISTS (SELECT 1 FROM searmedica.tipo_documento_referencia WHERE id_tipo_doc_referencia = 7);

INSERT INTO searmedica.tipo_documento_referencia (id_tipo_doc_referencia, descripcion, codigo_hacienda)
SELECT 8, 'CORRIGE_MONTO_POR_ERROR_MATERIAL', '14'
WHERE NOT EXISTS (SELECT 1 FROM searmedica.tipo_documento_referencia WHERE id_tipo_doc_referencia = 8);
