# Análisis de cumplimiento — Factura Electrónica v4.4 (Costa Rica)

**Documento de referencia:** "Anexos y Estructuras para la Emisión de Comprobantes Electrónicos, Versión 4.4" — Ministerio de Hacienda, noviembre 2024 (bitácora de ajustes al 22/04/2026).

**Fecha del análisis:** 08/07/2026

## Veredicto

**El código NO cumple con la versión 4.4.** La implementación corresponde a la estructura de la **versión 4.2 (2017)**, dos versiones atrás de la vigente. La v4.4 rige desde el **01 de setiembre del 2025** y desde esa fecha la v4.3 (y anteriores) quedó derogada, salvo para notas de crédito/débito que ajusten comprobantes emitidos durante su vigencia. Un comprobante generado con la estructura actual sería rechazado por el validador de Hacienda.

Nota de arquitectura: este repositorio no genera ni firma el XML directamente; serializa el modelo `FacturaElectronica` a JSON y lo envía a un servicio intermedio (`ServicioHacienda.java` → `http://localhost:8080/ServiciosRestFul/services/hacienda`). Aunque ese servicio externo estuviera actualizado, **el contrato de datos definido en este repo es v4.2**, por lo que los campos obligatorios de v4.4 nunca llegan a construirse.

---

## Hallazgos críticos (bloquean la aceptación del comprobante)

### 1. Namespace/esquema XML de la versión 4.2
- `EBS_Web/src/main/java/com/ebs/constantes/enums/Cadenas.java:13`
- `EBS_Web/src/main/java/com/ebs/modelos/package-info.java:6-8`

Usan `https://tribunet.hacienda.go.cr/docs/esquemas/2017/v4.2/facturaElectronica`. La v4.4 usa los esquemas publicados en `https://cdn.comprobanteselectronicos.go.cr/xml-schemas/v4.4/...` (uno por tipo de comprobante, incluyendo Factura Electrónica de Compra, de Exportación y Recibo Electrónico de Pago — bitácora 4.4.0, punto 1).

### 2. Estructura del nodo Impuesto obsoleta (`modelos/Impuesto.java`)
El modelo solo tiene `codigoImpuesto`, `porcentajeImpuesto` (Integer) y `total_impuestos`. La v4.4 exige:
- `CodigoTarifaIVA` (nota 8.1, cambios 62-64) — obligatorio para tarifas de IVA; incluye los códigos nuevos 09 "Tarifa reducida 0.5%", 10 "Tarifa Exenta" y 11 "Tarifa 0% sin derecho a crédito" (cambios 68-70).
- `Tarifa` como decimal 4,2 — no un entero.
- Campo `Impuesto` obligatorio con hasta 1000 repeticiones por línea (cambio 59); hoy el modelo permite un único impuesto por línea.
- `CodigoImpuestoOTRO` cuando se use el código 99 (cambio 61) y `FactorCalculoIVA` (cambio 72).
- Nodo `DatosImpuestosEspecificos` para códigos de impuesto 03-06 (cambio 73).

El enum `TipoTarifaTimpuestoEnum.java` tiene la tabla 8.1 de v4.4 en un comentario, pero solo define 3 valores y ningún código llega al modelo enviado.

### 3. `MedioPago` en el encabezado en lugar del resumen
`FacturaElectronica.java:30` define `medioPago` (String, único) en el encabezado. La v4.4 trasladó `MedioPago` al apartado "Resumen del Comprobante" (cambio 36), como nodo repetible (hasta 4) con monto por medio de pago, cuya sumatoria debe coincidir con `TotalComprobante` (cambios 123-124). Faltan además los códigos 06 "SINPE MÓVIL" y 07 "Plataforma Digital" (cambio 122) — `TiposMediosPago.java` no los contempla.

### 4. `ResumenFactura.java` incompleto
Faltan campos que en v4.4 son obligatorios o condicionales:
- `CodigoTipoMoneda` (código de moneda + tipo de cambio): obligatorio en **todos** los comprobantes (cambio 105), incluidas notas de crédito/débito (cambios 108-109). El modelo no lo tiene.
- `TotalServNoSujetos`, `TotalMercNoSujetas`, `TotalNoSujeto` (cambios 112, 116, 117).
- `TotalIVADevuelto` y `TotalOtrosCargos` (v4.3, siguen vigentes).
- `TotalDesgloseImpuesto` (cambio 120) y `TotalImpAsumEmisorFabrica` (cambio 121).
- Nodo `MedioPago` con sus montos (ver punto 3).

### 5. `LineaDetalle.java` incompleto
- `Cantidad` es `Integer`; el esquema define decimal 16,3.
- Falta `BaseImponible`, ahora obligatoria según el código de impuesto (cambios 57-58).
- Falta `IVACobradoFabrica` (cambios 55-56) e `ImpuestosAsumidosEmisorFabrica` (cambio 93), que ahora participa en el cálculo del `ImpuestoNeto` (cambio 94).
- Falta `TipoTransaccion` (cambios 45-46), `NumeroVINSerie` (cambio 48), `RegistroMedicamento` y `FormaFarmaceutica` (bitácora 12/11/2024, aplicable desde 01/01/2025), `PartidaArancelaria` y nodo `CodigoComercial`.
- Falta el nodo `DetalleSurtido` obligatorio para códigos CAByS de combos/surtidos (cambio 49).
- Descuentos: `Descuento.java` solo tiene monto y naturaleza. La v4.4 hace obligatorio el `MontoDescuento`, agrega `CodigoDescuento` obligatorio (nota 20, cambios 50-54) y permite hasta 5 repeticiones; el modelo admite un solo descuento sin código.
- Código CAByS: la entidad `Producto` sí almacena `codigo_cabys`, pero debe verificarse que sea lo que viaja en el campo `Codigo` de la línea (obligatorio también en notas de crédito/débito, cambio 39).

### 6. Encabezado: campos nuevos obligatorios ausentes
- `ProveedorSistemas` (cambio 3): identifica el sistema emisor. No existe en el modelo.
- `CodigoActividadReceptor` (cambio 6): no existe (solo hay `CodigoActividad` del emisor).
- `Emisor.java` y `Receptor.java` aún tienen `Fax`, nodo **eliminado** en v4.4 (cambio 18).
- Correo del emisor: v4.4 permite hasta 4 repeticiones (cambio 19); el modelo tiene un único String.
- `PlazoCredito`: v4.4 lo define como entero de 5 posiciones expresado en días (cambio 35); `ServicioFactura.java:1431` lo envía como texto `"N días"`.
- Identificaciones: v4.4 amplía a 20 caracteres y permite alfanuméricos para jurídicas, y agrega tipos 05 "Extranjero No Domiciliado" y 06 "No Contribuyente" (cambios 7-11, 27-28).
- Teléfono: mínimo 8 / máximo 20 dígitos con excepción de números especiales (cambio 17 y bitácora 22/04/2026).

### 7. Nodo `Normativa` todavía presente
`FacturaElectronica.java:38` incluye `Normativa`, nodo que dejó de existir a partir de la v4.3. Enviarlo provoca rechazo por esquema.

### 8. Condiciones de venta desactualizadas
`TipoCondicionVenta.java` solo tiene 01-07. La v4.4 agrega 08-15 (cambios 20-25 y bitácora 12/06/2023): venta a crédito IVA 90 días (10/11), venta mercancía no nacionalizada (12), bienes usados no contribuyente (13), arrendamiento operativo (14) y financiero (15). Además falta `DetalleCondicionVentaOTRO` obligatorio con código 99 (cambio 34).

### 9. Información de referencia incompleta
La v4.4 agrega en nota 9 los códigos 06-12 (devolución, sustitución, endoso, notas financieras, proveedor no domiciliado, crédito por exoneración) más 13-17 (bitácora 22/04/2026), y en nota 10 los códigos 16-18 y 19-20; también `TipoDocOTRO` y `CodigoReferenciaOTRO` para código 99 (cambios 125-142). El modelo actual (`InformacionReferenciaFactura`) solo maneja tipo/número/fecha/código/razón con códigos fijos v4.2.

### 10. Exoneraciones deshabilitadas y obsoletas
El código de exoneración está comentado (`LineaDetalle.java:30`, `ServicioFactura.java:1541-1554`). La v4.4 rehace el apartado: `TipoDocumentoEX`, códigos nuevos de nota 10.1 (08 Zona Franca, 09 servicios complementarios exportación, 10 corporaciones municipales, 11 autorización local concreta), artículo e inciso obligatorios, institución codificada según nota 23 y cálculo del monto exonerado sobre el subtotal (cambios 75-92).

### 11. Falta el nodo `OtrosCargos`
Existente desde v4.3 y ampliado en v4.4 (identificación del tercero, `TipoDocumentoOC`, códigos 08-10, cambios 96-104). No hay modelo para él.

---

## Hallazgos secundarios

- **Clave numérica** (`ServicioFactura.java:1980-2001`): la estructura general de 50 dígitos es correcta (506 + dd + mm + aa + cédula 12 + consecutivo 20 + situación + código seguridad 8). Observaciones: el año se agrega con `% 100` sin formatear a 2 dígitos (funciona hasta 2099 pero es frágil) y el código de seguridad usa `java.util.Random` en vez de un generador criptográfico.
- **Recibo Electrónico de Pago (código 10)**: está en `TipoDocumento.java` pero no hay estructura ni flujo que lo genere; en v4.4 es un comprobante con esquema propio.
- **Firma XAdES**: no se encuentra en este repo (delegada al servicio externo). La v4.4 amplía el nodo de firma para firma de receptor en créditos y endosos (cambio 145); debe verificarse en el servicio `ServiciosRestFul`.
- **Endpoint hardcodeado** a `localhost:8080` en `ServicioHacienda.java:71,131`, con manejo de respuesta comentado: hoy el sistema ni siquiera interpreta la respuesta de Hacienda en ese servicio.
- **Consultas con concatenación de strings** (`yaExisteNumeroConsecutivo*`, `ServicioFactura.java:2007,2028,2049`): riesgo de inyección SQL; conviene parametrizarlas al hacer la migración.

---

## Resumen por área del Anexo 1

| Área del documento v4.4 | Estado del código |
|---|---|
| Encabezado (Clave, Consecutivo, ProveedorSistemas, actividades, identificaciones) | Parcial: clave y consecutivo bien; faltan ProveedorSistemas, actividad del receptor, tipos de identificación 05/06, eliminar Fax |
| Detalle de mercancía/servicio | No cumple: sin CodigoTarifaIVA, BaseImponible, surtidos, códigos de descuento, tipo de transacción, campos de medicamentos |
| Resumen / Total del comprobante | No cumple: sin CodigoTipoMoneda, totales "No Sujeto", desglose de impuesto, medios de pago con montos |
| Información de referencia | Parcial: estructura básica existe, códigos desactualizados |
| Otros (uso comercial) | Existe nodo `Otros`; verificar tamaños (500 caracteres, cambios 143-144) |
| Mecanismo de seguridad (Anexo 2, XAdES) | Fuera de este repo (servicio externo, sin verificar) |
| Conexión API (Anexo 3) | Fuera de este repo; el cliente local apunta a un intermediario en localhost |

## Recomendación

Dado que la brecha es estructural (v4.2 → v4.4), lo recomendable no es parchear campo por campo sino regenerar los modelos a partir de los XSD oficiales v4.4 (uno por tipo de comprobante), actualizar los catálogos (tarifas, medios de pago, condiciones de venta, referencias, exoneraciones) y revisar en conjunto el servicio externo `ServiciosRestFul`, que es quien firma y transmite. Mientras tanto, todo comprobante nuevo emitido con esta estructura está sujeto a rechazo desde el 01/09/2025.
