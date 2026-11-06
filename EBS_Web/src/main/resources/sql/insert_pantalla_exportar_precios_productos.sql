-- ============================================================
-- Script para agregar la pantalla "Exportar Precios de Productos"
-- al menú bajo la sección "Consultas"
-- ============================================================

-- IMPORTANTE: Antes de ejecutar, verificar que la sección "Consultas"
-- exista verbatim en la BD destino:
-- SELECT * FROM searmedica.pantalla WHERE descripcion = 'Consultas' AND id_padre_pantalla = 0;

INSERT INTO searmedica.pantalla (id_pantalla, descripcion, id_padre_pantalla, url, icono, activo)
SELECT (SELECT MAX(id_pantalla) + 1 FROM searmedica.pantalla),
       'Exportar Precios de Productos',
       (SELECT id_pantalla FROM searmedica.pantalla WHERE descripcion = 'Consultas' AND id_padre_pantalla = 0 LIMIT 1),
       '/xhtml/paginas/consulta/exportarPreciosProductos',
       'fa fa-file-excel-o',
       1
WHERE NOT EXISTS (SELECT 1 FROM searmedica.pantalla WHERE descripcion = 'Exportar Precios de Productos');

-- Vincular la pantalla con los mismos roles que ya tiene "Consulta de Precios"
INSERT INTO searmedica.pantallas_roles (id_pantalla, id_rol)
SELECT (SELECT MAX(id_pantalla) FROM searmedica.pantalla WHERE descripcion = 'Exportar Precios de Productos'),
       pr.id_rol
FROM searmedica.pantallas_roles pr
WHERE pr.id_pantalla = (SELECT id_pantalla FROM searmedica.pantalla WHERE descripcion = 'Consulta de Precios' LIMIT 1)
AND NOT EXISTS (
    SELECT 1 FROM searmedica.pantallas_roles pr2
    WHERE pr2.id_pantalla = (SELECT MAX(id_pantalla) FROM searmedica.pantalla WHERE descripcion = 'Exportar Precios de Productos')
    AND pr2.id_rol = pr.id_rol
);
