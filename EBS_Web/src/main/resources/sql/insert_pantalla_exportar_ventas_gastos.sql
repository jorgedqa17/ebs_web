-- ============================================================
-- Script para agregar la pantalla "Exportar Ventas y Gastos"
-- al menú bajo la sección "Consultas"
-- ============================================================

-- IMPORTANTE: Antes de ejecutar, verificar:
-- 1. El id_pantalla que se va a usar no exista ya
-- 2. El id_padre_pantalla corresponda a la sección "Consultas"
--
-- Para encontrar el id del padre "Consultas", ejecutar:
-- SELECT * FROM searmedica.pantalla WHERE id_padre_pantalla = 0;
--
-- Para encontrar el próximo id disponible:
-- SELECT MAX(id_pantalla) + 1 FROM searmedica.pantalla;

-- Insertar la nueva pantalla (ajustar id_pantalla e id_padre_pantalla según su BD)
INSERT INTO searmedica.pantalla (id_pantalla, descripcion, id_padre_pantalla, url, icono, activo)
VALUES (
    (SELECT MAX(id_pantalla) + 1 FROM searmedica.pantalla),
    'Exportar Ventas y Gastos',
    (SELECT id_pantalla FROM searmedica.pantalla WHERE descripcion = 'Consultas' AND id_padre_pantalla = 0 LIMIT 1),
    '/xhtml/paginas/consulta/exportarVentasGastos',
    'fa fa-file-excel-o',
    1
);

-- Vincular la pantalla con los roles existentes
-- (Ajustar el id_rol según los roles que deban tener acceso)
-- Ejemplo: vincular con todos los roles que tienen acceso a pantallas de Consultas
INSERT INTO searmedica.pantallas_roles (id_pantalla, id_rol)
SELECT (SELECT MAX(id_pantalla) FROM searmedica.pantalla WHERE descripcion = 'Exportar Ventas y Gastos'),
       pr.id_rol
FROM searmedica.pantallas_roles pr
WHERE pr.id_pantalla = (
    SELECT MIN(p2.id_pantalla)
    FROM searmedica.pantalla p2
    WHERE p2.id_padre_pantalla = (
        SELECT id_pantalla FROM searmedica.pantalla WHERE descripcion = 'Consultas' AND id_padre_pantalla = 0 LIMIT 1
    )
)
AND NOT EXISTS (
    SELECT 1 FROM searmedica.pantallas_roles pr2
    WHERE pr2.id_pantalla = (SELECT MAX(id_pantalla) FROM searmedica.pantalla WHERE descripcion = 'Exportar Ventas y Gastos')
    AND pr2.id_rol = pr.id_rol
);
