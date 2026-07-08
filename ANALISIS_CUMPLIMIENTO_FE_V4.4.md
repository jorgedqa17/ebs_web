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

---

## Revisión de los cálculos (reglas de cálculo del Anexo 1, v4.4)

Alcance: la generación/firma del XML vive en otro repositorio; aquí se revisó la lógica de cálculo de montos que este sistema produce y persiste: `ModeloProducto` (cálculo por línea), `BeanFactura` (totales de factura), `UtilitarioNotasDebitoCredito` (totales de notas de crédito/débito) y los servicios de notas.

### Cálculos que SÍ coinciden con la v4.4

| Regla v4.4 | Código | Estado |
|---|---|---|
| MontoTotal = PrecioUnitario × Cantidad | `ModeloProducto.calcularMontoTotal()` | Correcto |
| SubTotal = MontoTotal − MontoDescuento | `calcularSubTotal()` | Correcto |
| MontoImpuesto = SubTotal × Tarifa | `calcularImpuesto()` | Correcto (sin BaseImponible) |
| MontoExoneración = % exoneración × SubTotal (bitácora 21/05/2020, reafirmado en cambio 92) | `calcularExoneracion()` | Correcto |
| ImpuestoNeto = Impuesto − MontoExonerado (cambio 94) | `calcularExoneracion()` | Parcial: falta restar "Impuestos Asumidos por el Emisor o cobrado a Nivel de Fábrica" (campo no existe) |
| MontoTotalLinea = SubTotal + ImpuestoNeto (cambio 95) | `calcularMontoTotalLinea()` | Correcto |
| TotalVentaNeta = TotalVenta − TotalDescuentos | `BeanFactura.getTotalVentaNeta()` | Correcto |
| TotalComprobante = TotalVentaNeta + TotalImpuesto (+OtrosCargos −IVADevuelto) | `getTotalFactura()` = Σ MontoTotalLinea | Equivalente mientras no existan OtrosCargos/IVA devuelto |
| Estructura de la clave numérica (50 dígitos) | `ServicioFactura.construirClaveNumerica()` | Correcta (país+dd+mm+aa+cédula 12+consecutivo 20+situación+seguridad 8) |

### Errores de cálculo encontrados

**E1 — GRAVE. Servicios exonerados suman al acumulador de mercancías y con base equivocada.**
`BeanFactura.java:2315-2317` (`getTotalGravadosServicios`): para un servicio exonerado parcialmente, la porción gravada (1 − %exon/tarifa) se acumula en **`totalGravadosMercancias`** dentro del método de **servicios**. Como `getTotalGravados()` (línea 2382) evalúa mercancías **antes** que servicios, ese monto se pierde del total: `TotalGravado` y `TotalVenta` quedan cortos y el comprobante no cuadra. Además usa `MontoTotalLinea` (que incluye impuesto) como base, cuando los totales de clasificación se calculan sobre `MontoTotal`.

**E2 — GRAVE. Totales de la factura corrompidos al aplicar notas de crédito/débito.**
`ServicioNotaDebito.java:214-215` y `ServicioFactura.java:3156-3157`: `total_venta` y `total_venta_neta` están cruzados y encadenados sobre el valor ya mutado:
```java
factura.setTotal_venta_neta(factura.getTotal_venta().add(nd.getTotal_venta()));      // neta recibe venta
factura.setTotal_venta(factura.getTotal_venta_neta().add(nd.getTotal_venta_neta())); // venta = venta + ndVenta + ndVentaNeta
```
Tras una nota de débito, la venta queda sumada dos veces; en la nota de crédito ocurre la resta cruzada equivalente. Corrompe los totales persistidos de la factura afectada.

**E3 — GRAVE. Montos "fantasma" en totales exonerados.**
`BeanFactura.getTotalExoneradosServicios/Mercancias` (p. ej. `BeanFactura.java:2284-2303`) y sus copias en `UtilitarioNotasDebitoCredito.java:139-187`: `monto` y `exon` se declaran **fuera** del ciclo. Si una línea de la lista no cumple la condición interna (sin `exoneracionLinea`, tarifa 01, etc.), la iteración vuelve a sumar el monto y porcentaje de la **línea anterior**. El filtro de servicios (`obtenerProductoExonerados(false)`) no exige `exoneracionLinea != null`, así que el caso se da en la práctica.

**E4 — MEDIO. Doble conteo de líneas con tarifa 0%.**
En la rama "normal" de `getTotalGravadosServicios/Mercancias` (`BeanFactura.java:2321-2327, 2349-2352`) solo se excluye la tarifa 10 (Exenta), no la 01 (0%). Pero `getTotalExentos*` (`BeanFactura.java:2370-2374`) incluye tarifas 01 **y** 10. Una línea con tarifa 01 e impuesto asignado se cuenta como gravada y como exenta a la vez → `TotalVenta` inflado y comprobante descuadrado.

**E5 — MEDIO. La factura y su nota de crédito calculan distinto.**
`BeanFactura` y `UtilitarioNotasDebitoCredito` son copias divergentes de la misma lógica: el utilitario (usado por notas de crédito/débito) acumula en la variable correcta y usa `MontoTotal` como base; el bean de factura tiene los errores E1/E4. Una nota de crédito total de una factura con exoneración **no reproduce los montos de la factura original**, inconsistencia que Hacienda valida contra el documento de referencia.

**E6 — MEDIO. Tarifas y porcentajes como Float/Integer.**
- `Impuesto.porcentajeImpuesto` es `Integer` y se llena con `Integer.parseInt(impProducto.getValor())` (`ServicioFactura.java:1537`): la **tarifa reducida 0.5% (código 09, v4.4)** es irrepresentable (truncaría a 0 o lanzaría excepción). La v4.4 define la tarifa como decimal 4,2.
- Uso sistemático de `Float.parseFloat` + `new BigDecimal(float)` (`ModeloProducto.java:427,440`, `BeanFactura.java:2297,2316`): `new BigDecimal(0.13f)` = 0.1299999952…; en montos altos el error alcanza el segundo decimal. Debe usarse `new BigDecimal(String)` y división con `MathContext`.

**E7 — MEDIO. "Cliente exento" sin respaldo documental.**
`calcularImpuesto()` pone el impuesto en 0 cuando `esPersonaExenta`, sin nodo de exoneración ni tarifa exenta. En la v4.4 no existe la figura de cliente exento "a secas": o la línea lleva tarifa 01/10/11 con su código, o lleva nodo `Exoneracion` con documento. Tal como está, la línea saldría gravada con impuesto 0 sin justificación → rechazo.

**E8 — MENOR. Sin validación de exoneración vs impuesto.**
Si el porcentaje de exoneración supera la tarifa, `ImpuestoNeto` queda negativo (`calcularExoneracion()`); la v4.4 exige que el monto exonerado no exceda el impuesto de la línea.

**E9 — MENOR. Redondeo.**
`setScale(3, HALF_EVEN)` está dentro de la tolerancia del documento (permite trabajar con redondeo a 2 decimales), pero se aplica en cada suma parcial; es preferible acumular sin redondear y redondear el total al final para minimizar deriva.

### Cálculos de v4.4 que no existen (consecuencia de la brecha estructural)

Sin estos campos, las reglas de cálculo nuevas de la v4.4 no pueden cumplirse: `BaseImponible` como base del impuesto cuando aplica (cambio 58), resta de `ImpuestosAsumidosEmisorFabrica` en el ImpuestoNeto (94), `TotalDesgloseImpuestoCobrado` (120), `TotalIVADevuelto`, `TotalOtrosCargos`, los totales "No Sujeto" (112, 116, 117) y la conciliación `TotalComprobante` = Σ montos por `MedioPago` (124).

## Recomendación

Dado que la brecha es estructural (v4.2 → v4.4), lo recomendable no es parchear campo por campo sino regenerar los modelos a partir de los XSD oficiales v4.4 (uno por tipo de comprobante), actualizar los catálogos (tarifas, medios de pago, condiciones de venta, referencias, exoneraciones) y revisar en conjunto el servicio externo `ServiciosRestFul`, que es quien firma y transmite. Mientras tanto, todo comprobante nuevo emitido con esta estructura está sujeto a rechazo desde el 01/09/2025.
