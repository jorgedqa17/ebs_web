-- =====================================================================
-- Limpieza del maestro del producto 6991 (SELLOS HEPARINA TODOSCAN)
--
-- CORRER SOLO DESPUÉS DE VERIFICAR LA REEMISIÓN DE LA NC.
--
-- El producto tiene id_impuesto = 1 (IVA 13%) contradiciendo su propio
-- CABYS 4815099009900 "Instrumentos y aparatos utilizados en medicina,
-- cirugía o veterinaria, n.c.p.", que es de tarifa 0 según la API oficial
-- (https://api.hacienda.go.cr/fe/cabys?codigo=4815099009900).
--
-- Ese id_impuesto huérfano es lo que disparaba el doble conteo: la línea
-- caía a la vez en gravados (por tener impuesto) y en exentos (por
-- es_linea_exenta = 1), inflando total_venta de 21150 a 22950.
--
-- ORDEN IMPORTANTE: el fix de código (UtilitarioNotasDebitoCredito) ya
-- clasifica bien aunque el dato siga sucio. Si se limpia el dato ANTES de
-- reemitir, la NC saldría correcta aunque el fix estuviera mal y no se
-- sabría cuál de los dos funcionó. Primero se prueba el código contra el
-- dato sucio; esta limpieza va después.
--
-- TERMINA EN ROLLBACK A PROPÓSITO.
-- =====================================================================

BEGIN;

SELECT 'ANTES' AS bloque, id_producto, descripcion, codigo_cabys,
       ind_exento, ind_exonerado, id_impuesto, id_tipo_tarifa_impuesto
FROM searmedica.producto
WHERE id_producto = 6991;
-- Esperado: id_impuesto = 1, ind_exento = 1, codigo_cabys = 4815099009900

UPDATE searmedica.producto
SET id_impuesto = NULL
WHERE id_producto = 6991;
-- Esperado: UPDATE 1

SELECT 'DESPUES' AS bloque, id_producto, descripcion, codigo_cabys,
       ind_exento, id_impuesto, id_tipo_tarifa_impuesto
FROM searmedica.producto
WHERE id_producto = 6991;
-- Esperado: id_impuesto = NULL

-- ---------------------------------------------------------------------
-- Los otros 47 productos con la misma inconsistencia. NO se tocan aquí:
-- cada uno hay que contrastarlo contra su propio CABYS antes de decidir,
-- porque algunos podrían ser productos legítimamente gravados a los que
-- les sobra el ind_exento en vez de faltarles el impuesto.
-- ---------------------------------------------------------------------

SELECT 'PENDIENTES de revisar' AS bloque, id_producto, descripcion,
       codigo_cabys, ind_exento, id_impuesto, id_tipo_tarifa_impuesto
FROM searmedica.producto
WHERE ind_exento = 1
  AND id_impuesto IS NOT NULL
  AND id_producto <> 6991
ORDER BY id_producto;
-- Esperado: 47 filas

-- =====================================================================
ROLLBACK;
-- COMMIT;
