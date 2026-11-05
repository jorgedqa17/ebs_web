-- ============================================================
-- Script para agregar la pantalla "Catálogo de Productos"
-- al menú, dentro de la sección "Consultas"
-- (consulta visual con fotos, precio y existencia)
-- ============================================================

-- IMPORTANTE: Antes de ejecutar, verificar:
-- 1. El id_pantalla que se va a usar no exista ya
-- 2. El id de la sección "Consultas" en esta BD (aquí asumido 13, confirmado en BD local EBS_LOCAL_V4)
--
-- Para confirmar el id de "Consultas":
-- SELECT * FROM searmedica.pantalla WHERE descripcion = 'Consultas' AND id_padre_pantalla = 0;

INSERT INTO searmedica.pantalla (id_pantalla, descripcion, id_padre_pantalla, url, icono, activo)
SELECT (SELECT MAX(id_pantalla) + 1 FROM searmedica.pantalla),
       'Catálogo de Productos',
       (SELECT id_pantalla FROM searmedica.pantalla WHERE descripcion = 'Consultas' AND id_padre_pantalla = 0 LIMIT 1),
       '/xhtml/paginas/producto/catalogoProductos.xhtml',
       'fa fa-picture-o',
       1
WHERE NOT EXISTS (SELECT 1 FROM searmedica.pantalla WHERE descripcion = 'Catálogo de Productos');

-- Vincular la pantalla con los mismos roles que ya ven "Consulta de Precios" (otra consulta de productos
-- ya existente en esta sección) para no tener que adivinar roles uno por uno.
INSERT INTO searmedica.pantallas_roles (id_pantalla, id_rol)
SELECT (SELECT MAX(id_pantalla) FROM searmedica.pantalla WHERE descripcion = 'Catálogo de Productos'),
       pr.id_rol
FROM searmedica.pantallas_roles pr
WHERE pr.id_pantalla = (
    SELECT id_pantalla FROM searmedica.pantalla WHERE descripcion = 'Consulta de Precios' LIMIT 1
)
AND NOT EXISTS (
    SELECT 1 FROM searmedica.pantallas_roles pr2
    WHERE pr2.id_pantalla = (SELECT MAX(id_pantalla) FROM searmedica.pantalla WHERE descripcion = 'Catálogo de Productos')
    AND pr2.id_rol = pr.id_rol
);
