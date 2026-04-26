# Documentación Completa — Proyecto GamerStore

**Sistema de Ventas de Monitores Gamer** — Java Swing
**Curso:** Introducción a la Algoritmia — Cibertec Grupo 12
**Integrantes:** Walter Alvarado, Erick Alcántara, Miguel Delgado, Luis Delgado

---

## Índice

1. [Arquitectura General](#arquitectura-general)
2. [Estructura de Archivos](#estructura-de-archivos)
3. [Clases del Modelo](#clases-del-modelo)
   - [Producto.java](#1-productojava)
   - [Constantes.java](#2-constantesjava)
   - [Validador.java](#3-validadorjava)
   - [Refrescable.java](#4-refrescablejava)
4. [Clase Principal](#clase-principal)
   - [Tienda.java](#5-tiendajava)
5. [Persistencia](#persistencia)
   - [HistorialVentas.java](#6-historialventasjava)
6. [Utilidades Visuales](#utilidades-visuales)
   - [TemaOscuro.java](#7-temaoscurojava)
7. [Paneles de la UI](#paneles-de-la-ui)
   - [PanelVender.java](#8-panelvenderjava)
   - [PanelHistorial.java](#9-panelhistorialjava)
   - [PanelConsultar.java](#10-panelconsultarjava)
   - [PanelModificar.java](#11-panelmodificarjava)
   - [PanelGestionar.java](#12-panelgestionarjava)
   - [PanelListar.java](#13-panellistarjava)
   - [PanelConfDesc / PanelConfObs / PanelConfCuota](#14-paneles-de-configuración)
   - [AcercaDe.java](#15-acercadejava)
8. [Flujo Completo de una Venta](#flujo-completo-de-una-venta)
9. [Conceptos Java Aplicados](#conceptos-java-aplicados)
10. [Preguntas de Examen](#preguntas-de-examen)

---

## Arquitectura General

El proyecto sigue un patrón **MVC simplificado**:

```
┌─────────────────────────────────────────────────┐
│                   Tienda.java                    │
│  (JFrame principal + CardLayout + estado global) │
└─────────────────────────────────────────────────┘
           │                 │               │
           ▼                 ▼               ▼
    ┌──────────┐      ┌──────────┐    ┌──────────┐
    │ Modelo   │      │ Paneles  │    │ Utilidad │
    │          │      │   (UI)   │    │          │
    │ Producto │      │ Vender   │    │TemaOscuro│
    │Constantes│      │Historial │    │Validador │
    │          │      │Consultar │    │Historial │
    │          │      │Modificar │    │  Ventas  │
    │          │      │Gestionar │    │          │
    │          │      │ Listar   │    │          │
    │          │      │ ConfDesc │    │          │
    │          │      │ ConfObs  │    │          │
    │          │      │ ConfCuota│    │          │
    └──────────┘      └──────────┘    └──────────┘
```

**Decisiones de diseño clave:**
- Estado global en `Tienda` (variables `static`) — simple para estudiantes, evita pasar referencias.
- `CardLayout` para navegación entre vistas sin crear ventanas nuevas.
- Interfaz `Refrescable` para actualizar datos al cambiar de vista.
- Persistencia en archivos de texto plano (`datos.txt`, `historial.txt`).

---

## Estructura de Archivos

```
Ventas/
├── src/
│   ├── Tienda.java              (main + JFrame + dashboard)
│   ├── Producto.java            (modelo POJO)
│   ├── Constantes.java          (valores fijos)
│   ├── Validador.java           (validaciones estáticas)
│   ├── Refrescable.java         (interfaz)
│   ├── HistorialVentas.java     (registro de ventas en archivo)
│   ├── TemaOscuro.java          (estilos y componentes visuales)
│   ├── PanelVender.java         (punto de venta)
│   ├── PanelHistorial.java      (tabla de ventas)
│   ├── PanelConsultar.java      (ver producto)
│   ├── PanelModificar.java      (editar producto)
│   ├── PanelGestionar.java      (agregar/eliminar)
│   ├── PanelListar.java         (lista completa)
│   ├── PanelConfDesc.java       (config descuentos)
│   ├── PanelConfObs.java        (config obsequios)
│   ├── PanelConfCuota.java      (config cuota)
│   └── AcercaDe.java            (diálogo de créditos)
├── datos.txt                    (estado persistente)
└── historial.txt                (registro de ventas)
```

---

## Clases del Modelo

### 1. `Producto.java`

Clase POJO que representa un monitor. Usa **encapsulación** (atributos privados + getters/setters).

#### Atributos (todos `private`)

| Atributo | Tipo | Descripción |
|---|---|---|
| `nombre` | `String` | Modelo del monitor (ej: "ASUS ROG Swift") |
| `precio` | `double` | Precio unitario en soles |
| `pulgadas` | `double` | Tamaño de pantalla |
| `hercios` | `int` | Frecuencia de refresco |
| `resolucion` | `String` | "FHD", "2K", "4K" |
| `stock` | `int` | Unidades disponibles |
| `tipoPanel` | `String` | "IPS", "VA", "TN" |
| `tiempoRespuesta` | `int` | Milisegundos |
| `conectividad` | `String` | "HDMI, DP, USB-C" |
| `garantiaMeses` | `int` | Garantía en meses |

#### Métodos

| Método | Retorno | Descripción |
|---|---|---|
| `Producto(10 params)` | — | Constructor que inicializa todos los atributos |
| `getNombre()`, `setNombre(String)` | `String` / `void` | Acceso al nombre |
| `getPrecio()`, `setPrecio(double)` | `double` / `void` | Acceso al precio |
| (resto de getters/setters) | — | Siguen el mismo patrón |
| `descontarStock(int cantidad)` | `void` | `this.stock -= cantidad` |
| `tieneStock(int cantidad)` | `boolean` | Devuelve `this.stock >= cantidad` |
| `toString()` | `String` | Devuelve el nombre (útil para combos) |

---

### 2. `Constantes.java`

Clase con solo **constantes** `public static final`. Centraliza valores "mágicos" para facilitar mantenimiento.

#### Constantes principales

```java
// Impuesto
public static final double IGV = 0.18;

// Rangos de descuento
public static final int RANGO_DESC_1_MAX = 5;   //  1-5   → 5%
public static final int RANGO_DESC_2_MAX = 10;  //  6-10  → 7.5%
public static final int RANGO_DESC_3_MAX = 15;  // 11-15  → 10%
                                                // >15    → 15%

// Rangos de obsequio
public static final int RANGO_OBS_1 = 1;        // 1     → Mouse
public static final int RANGO_OBS_2_MAX = 5;    // 2-5   → Teclado
                                                // >5    → Silla

// Validación
public static final int LONGITUD_DNI = 8;
public static final int LONGITUD_RUC = 11;

// Stock
public static final int STOCK_MINIMO_ALERTA = 5;

// Archivos
public static final String ARCHIVO_DATOS = "datos.txt";
public static final String ARCHIVO_HISTORIAL = "historial.txt";
public static final String SEPARADOR_HISTORIAL = "\\|";
public static final int COLUMNAS_HISTORIAL = 8;

// Mensajes (ERR_* y MSG_*)
// App
public static final String APP_NOMBRE = "GamerStore";
public static final String APP_VERSION = "2.0";
```

**Ventaja:** cambiar el IGV a 18% → 19% se hace en un solo lugar.

---

### 3. `Validador.java`

Clase con **métodos estáticos** para validar datos de entrada. No tiene atributos.

#### Métodos

| Método | Retorno | Lógica |
|---|---|---|
| `esTextoVacio(String)` | `boolean` | `texto == null \|\| texto.trim().isEmpty()` |
| `esDniValido(String)` | `boolean` | Longitud 8 + solo dígitos (`matches("\\d+")`) |
| `esRucValido(String)` | `boolean` | Longitud 11 + solo dígitos |
| `esDniOrocValido(String)` | `boolean` | `esDniValido() \|\| esRucValido()` |
| `esEnteroPositivo(String)` | `boolean` | `Integer.parseInt() > 0` (try-catch) |
| `esEnteroNoNegativo(String)` | `boolean` | `parseInt() >= 0` |
| `esDecimalPositivo(String)` | `boolean` | `Double.parseDouble() > 0` |
| `esDecimalNoNegativo(String)` | `boolean` | `parseDouble() >= 0` |
| `validarProducto(precio, pulgadas, hz, stock, tr, garantia)` | `String` | Valida todos los campos numéricos; devuelve mensaje de error o `null` si todo OK |

**Patrón clave:** uso de `try-catch (NumberFormatException)` para validar conversiones.

```java
public static boolean esEnteroPositivo(String texto) {
    try {
        return Integer.parseInt(texto.trim()) > 0;
    } catch (NumberFormatException e) {
        return false;
    }
}
```

---

### 4. `Refrescable.java`

Interfaz con un solo método:

```java
public interface Refrescable {
    void refrescar();
}
```

**Propósito:** los paneles que la implementen deben saber cómo actualizar sus datos cuando se cambia de vista. Permite uso polimórfico en `Tienda.navegarA()`:

```java
for (Component c : panelContenido.getComponents()) {
    if (c instanceof Refrescable) ((Refrescable) c).refrescar();
}
```

---

## Clase Principal

### 5. `Tienda.java`

Extiende `JFrame`. Contiene el **main**, el estado global y la construcción de la UI.

#### Variables estáticas (estado global de la app)

| Variable | Tipo | Valor inicial |
|---|---|---|
| `productos` | `ArrayList<Producto>` | lista vacía (se llena en main) |
| `porcentajesDescuento` | `double[]` | `{5.0, 7.5, 10.0, 15.0}` |
| `obsequios` | `String[]` | `{"Mouse Gamer", "Teclado Mecánico", "Silla Gamer"}` |
| `numVentas` | `int` | `0` |
| `importeAcumulado` | `double` | `0` |
| `cuotaDiaria` | `double` | `30000.0` |

#### Atributos de instancia

| Atributo | Tipo | Descripción |
|---|---|---|
| `panelContenido` | `JPanel` | Contenedor central con CardLayout |
| `cardLayout` | `CardLayout` | Gestor para intercambiar paneles |
| `botonesSidebar` | `JButton[]` | Botones del menú lateral |
| `claves` | `String[]` | Claves de navegación ("dashboard", "vender"...) |

#### Métodos

##### `main(String[] args)`
```java
public static void main(String[] args) {
    inicializarProductosPorDefecto();
    cargarDatos();
    EventQueue.invokeLater(() -> {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        new Tienda().setVisible(true);
    });
}
```
- Inicializa 5 productos por defecto.
- Carga los datos persistidos de `datos.txt` (sobreescribe los productos por defecto si el archivo existe).
- Usa `EventQueue.invokeLater` para cumplir con la regla de Swing: la UI solo se construye en el Event Dispatch Thread (EDT).

##### `inicializarProductosPorDefecto()`
Agrega 5 monitores al ArrayList: ASUS ROG Swift, Samsung Odyssey G7, MSI Optix G24, LG UltraGear, Gigabyte M27Q.

##### `getNombresProductos()`
Devuelve `String[]` con los nombres del catálogo. Usado para llenar los `JComboBox`.

##### `obtenerDescuento(int cantidad)` — LÓGICA DE NEGOCIO
```java
if (cantidad <= 5)  return porcentajesDescuento[0];   // 5%
if (cantidad <= 10) return porcentajesDescuento[1];   // 7.5%
if (cantidad <= 15) return porcentajesDescuento[2];   // 10%
return porcentajesDescuento[3];                       // 15%
```

##### `obtenerObsequio(int cantidad)`
```java
if (cantidad == 1) return obsequios[0];   // Mouse
if (cantidad <= 5) return obsequios[1];   // Teclado
return obsequios[2];                      // Silla
```

##### Constructor `Tienda()`
Arma la UI en 3 regiones (BorderLayout):
- **WEST (sidebar):** BoxLayout vertical con secciones (GENERAL, PRODUCTOS, CONFIGURACION) y botones que disparan `navegarA()`.
- **CENTER (panelContenido):** CardLayout con los 10 paneles.
- **SOUTH (barra de estado):** info + reloj (Timer Swing de 1 segundo).

##### `navegarA(String clave, int botonIndex)`
1. Marca activo el botón seleccionado, inactivos los demás.
2. Si es el dashboard, lo reconstruye (para que refleje datos actuales).
3. Llama `refrescar()` en cada panel que sea `Refrescable`.
4. `cardLayout.show(panelContenido, clave)`.

##### `crearDashboard()`
Construye el dashboard con:
- Header con título y fecha.
- Fila de 4 **stat cards**: ventas, monto, stock total, % cuota.
- Fila inferior de 3 tarjetas: avance de cuota (progress bar), inventario, reporte del día.

Cálculos:
```java
totalStock = Σ p.getStock() de todos los productos
stockBajo  = cantidad con stock < STOCK_MINIMO_ALERTA
porcCuota  = (importeAcumulado / cuotaDiaria) × 100
```

##### `calcularReporte()`
Lee `historial.txt` con `Scanner` y devuelve `String[]` con:
- Producto más vendido (usa `HashMap<String, Integer>` para contar).
- Venta más alta (máximo de columna 6).
- Promedio por venta (suma / total).
- Total de clientes únicos (`HashSet<String>` por DNI).

##### `cargarDatos()` — Persistencia
Lee `datos.txt` línea por línea con `Scanner`:
```
numProductos
(por cada producto: 10 líneas con cada atributo)
4 líneas → porcentajesDescuento[0..3]
3 líneas → obsequios[0..2]
1 línea  → cuotaDiaria
1 línea  → numVentas
1 línea  → importeAcumulado
```
Uso de `try-with-resources` — cierra el Scanner automáticamente.

##### `guardarDatos()`
Escribe con `PrintWriter(FileWriter(...))` en el mismo formato.

##### `irADashboard()`
Atajo que llama `navegarA("dashboard", 0)`.

##### `agregarFilaReporte(panel, label, valor)`
Helper privado para construir filas del reporte del dashboard.

---

## Persistencia

### 6. `HistorialVentas.java`

Una clase con un solo método estático:

```java
public static void registrarVenta(int nroVenta, String cliente, String dni,
                                   String producto, int cantidad, double total,
                                   String metodoPago) {
    try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_HISTORIAL, true))) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        pw.println(String.format("%06d", nroVenta) + "|" + fecha + "|" + cliente + "|" +
                dni + "|" + producto + "|" + cantidad + "|" +
                String.format("%.2f", total) + "|" + metodoPago);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

- **`FileWriter(ARCHIVO_HISTORIAL, true)`** — el `true` es **modo append** (no sobreescribe, añade al final).
- Cada línea tiene 8 columnas separadas por `|`:
  ```
  000001|16/04/2026 14:30|Juan Pérez|12345678|ASUS ROG|2|5950.00|Efectivo
  ```

---

## Utilidades Visuales

### 7. `TemaOscuro.java`

Clase con **solo miembros estáticos**: colores, fuentes y métodos factory para construir componentes UI con el tema oscuro.

#### Paleta de colores

```java
BASE       = (22, 22, 30)    // fondo principal
MANTLE     = (18, 18, 25)    // sidebar, header
CRUST      = (14, 14, 20)    // barra de estado
SURFACE0   = (32, 33, 45)    // tarjetas
SURFACE1/2 = gris más claro
TEXT       = (210, 215, 235) // texto principal
SUBTEXT0   = (155, 160, 185) // texto secundario
OVERLAY0   = (108, 112, 134) // texto tenue
ACCENT     = (100, 140, 255) // azul principal
GREEN, YELLOW, RED, CYAN, ORANGE
```

#### Fuentes

```java
FONT_XS (10), FONT_SM (11), FONT_BASE (12), FONT_MD (13)
FONT_BOLD, FONT_BOLD_SM, FONT_TITLE (14)
FONT_H1 (26), FONT_H2 (20)
FONT_MONO ("Consolas", 11)  // para boleta
```

#### Métodos principales

| Método | Técnica |
|---|---|
| `crearBoton(texto, bg)` | Sobreescribe `paintComponent` para dibujar `RoundRectangle2D`. `MouseAdapter` cambia color al pasar el mouse. |
| `crearBotonSidebar(texto, activo)` | Botón lateral rectangular; efecto hover. |
| `marcarBotonActivo/Inactivo(btn)` | Cambia color de fondo/texto del botón. |
| `crearLabel / crearLabelPequeno / crearValorLabel` | Etiquetas con distinta jerarquía tipográfica. |
| `crearCampoTexto(editable)` | JTextField con borde que cambia de color al recibir foco (`FocusAdapter`). |
| `crearTarjeta(titulo)` | JPanel con fondo redondeado (override `paintComponent`). |
| `crearStatCard(titulo, valor, acento)` | Tarjeta del dashboard con franja vertical de 4px. |
| `crearFilaFormulario(panel, gbc, fila, label, editable)` | Agrega una fila (label + campo) a un GridBagLayout. |
| `aplicarTemaTabla(JTable)` | Personaliza colores, fuentes y `CellRenderer` (filas zebra). |
| `crearScrollPane(view)` | ScrollPane con scrollbar minimalista (UI custom). |
| `crearProgressBar()` | Progress bar en forma de píldora (override `BasicProgressBarUI`). |
| `crearHeaderPagina(titulo, subtitulo)` | Encabezado de página con título grande. |
| `crearSeparador()` | Línea horizontal gris. |
| `esClaro(Color)` | Calcula luminancia para elegir color de texto contrastante. |

**Técnica común:** uso de clases anónimas que sobreescriben `paintComponent(Graphics g)` para dibujar gráficos personalizados con `Graphics2D` y antialiasing.

```java
JPanel tarjeta = new JPanel(new BorderLayout()) {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(SURFACE0);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        g2.dispose();
    }
};
```

---

## Paneles de la UI

### 8. `PanelVender.java`

Implementa `Refrescable`. Es el **punto de venta**.

#### Atributos
- `tienda` (referencia a la ventana principal)
- `cboM, cboPago` (combos de modelo y método de pago)
- `txtP, txtC, txtCliente, txtDni` (campos)
- `txtBoleta` (JTextArea donde se imprime el ticket)
- Labels del resumen: `lblImporte, lblDescuento, lblSubtotal, lblIgv, lblTotal, lblObsequio`
- `lblVentaNum, lblAcumulado, barraAvance` (cuota)

#### Métodos

##### Constructor
Construye tres zonas:
- **Izquierda:** datos del cliente + producto/cantidad + cuota diaria.
- **Derecha:** boleta + resumen.

##### `mostrarPrecio()`
Al cambiar el combo, actualiza precio y detalle del producto. Color verde/rojo según stock.

##### `vender()` — MÉTODO MÁS IMPORTANTE

```java
private void vender() {
    // 1. Validaciones
    if (Validador.esTextoVacio(cliente)) { ... return; }
    if (!Validador.esDniOrocValido(dni)) { ... return; }
    if (!Validador.esEnteroPositivo(txtC.getText())) { ... return; }
    if (!prod.tieneStock(cant)) { ... return; }

    // 2. Cálculos
    double imp    = precio * cantidad;
    double pDesc  = Tienda.obtenerDescuento(cant);
    double descMonto = imp * (pDesc / 100);
    double sub    = imp - descMonto;
    double igv    = sub * Constantes.IGV;
    double total  = sub + igv;
    String obs    = Tienda.obtenerObsequio(cant);

    // 3. Efectos de negocio
    prod.descontarStock(cant);
    Tienda.numVentas++;
    Tienda.importeAcumulado += total;
    Tienda.guardarDatos();
    HistorialVentas.registrarVenta(...);

    // 4. Actualizar UI
    lblImporte.setText(...);
    lblTotal.setText(...);
    actualizarAvance();
    generarBoleta(...);
}
```

##### `generarBoleta(...)`
Construye texto formateado:
```
============================================
         BOLETA DE VENTA
         GamerStore - Grupo 12
============================================
Nro. Venta    : 000001
Fecha         : 16/04/2026 14:30:45
--------------------------------------------
CLIENTE
Nombre        : Juan Pérez
DNI/RUC       : 12345678
Método pago   : Efectivo
--------------------------------------------
DETALLE
Modelo        : ASUS ROG Swift
Precio unit.  : S/. 2500.00
Cantidad      : 2
--------------------------------------------
Importe       : S/. 5000.00
Descuento 5%  : S/. 250.00
Subtotal      : S/. 4750.00
IGV (18%)     : S/. 855.00
============================================
TOTAL A PAGAR : S/. 5605.00
============================================
```

##### `limpiar()`, `actualizarAvance()`, `refrescar()`
- `limpiar`: resetea campos.
- `actualizarAvance`: recalcula `%cuota` y pinta la barra.
- `refrescar`: recarga combo y avance.

---

### 9. `PanelHistorial.java`

Tabla con historial de ventas.

#### Atributos
- `modelo` (`DefaultTableModel`) — datos de la tabla.
- `tabla` (`JTable`).
- `sorter` (`TableRowSorter`) — permite ordenar/filtrar.
- `txtBuscar` — campo de búsqueda.
- `lblTotalReg, lblTotalMonto` — estadísticas del pie.

#### Métodos clave

##### `cargar()`
Lee `historial.txt` y llena la tabla:
```java
while (sc.hasNextLine()) {
    String[] p = sc.nextLine().split(SEPARADOR_HISTORIAL);
    if (p.length >= COLUMNAS_HISTORIAL) modelo.addRow(p);
}
```

##### `filtrar()` — búsqueda en tiempo real
Se dispara con `DocumentListener`:
```java
sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 2, 3, 4));
```
- `(?i)` = insensible a mayúsculas.
- Filtra columnas 2 (cliente), 3 (DNI), 4 (producto).

##### `actualizarTotales()`
Suma la columna "Total" de las filas visibles.

---

### 10. `PanelConsultar.java`

Panel **de solo lectura** que muestra los detalles de un producto seleccionado.

Campos creados con `editable = false`. Al cambiar el combo se llama `mostrar()` que llena los campos y actualiza una etiqueta de estado:
- Stock > 5 → `"EN STOCK"` verde.
- Stock 1-5 → `"STOCK BAJO"` amarillo.
- Stock 0 → `"AGOTADO"` rojo.

---

### 11. `PanelModificar.java`

Campos editables. Permite modificar los atributos de un producto existente.

##### `grabar()`
1. Valida con `Validador.validarProducto()`.
2. Valida que los campos de texto no estén vacíos.
3. Usa los setters de `Producto` para actualizar los datos.
4. Llama `Tienda.guardarDatos()`.
5. Muestra mensaje de éxito con `JOptionPane`.

---

### 12. `PanelGestionar.java`

Dos tarjetas lado a lado con `GridLayout(1, 2)`:

##### `agregar()`
1. Valida nombre y campos numéricos.
2. Crea un nuevo `Producto` con los valores del formulario.
3. `Tienda.productos.add(nuevo)`.
4. `Tienda.guardarDatos()`.
5. Actualiza combo y limpia formulario.

##### `eliminar()`
1. Rechaza si solo queda 1 producto.
2. Pide confirmación con `JOptionPane.showConfirmDialog`.
3. `Tienda.productos.remove(idx)`.
4. Guarda y actualiza combo.

---

### 13. `PanelListar.java`

Tabla completa con todos los productos. Usa `DefaultTableModel` no editable y recorre `Tienda.productos` con un for-each para llenar las filas.

---

### 14. Paneles de Configuración

Los 3 son muy similares:

#### `PanelConfDesc.java`
4 campos numéricos para `porcentajesDescuento[0..3]`. En `guardar()`:
```java
Tienda.porcentajesDescuento[0] = Double.parseDouble(t1.getText().trim());
// ...
Tienda.guardarDatos();
```

#### `PanelConfObs.java`
3 campos de texto para `obsequios[0..2]`.

#### `PanelConfCuota.java`
1 campo numérico para `cuotaDiaria`.

---

### 15. `AcercaDe.java`

Extiende `JDialog`. Ventana modal con:
- Icono emoji (`🖥️`).
- Nombre "GamerStore" en grande.
- Lista de autores (4 integrantes).
- Curso e institución.
- Botón "Cerrar" que llama `dispose()`.

---

## Flujo Completo de una Venta

```
Usuario → botón "Vender" en PanelVender
    │
    ▼
PanelVender.vender()
    │
    ├─► Validador.esTextoVacio(cliente)      ──[si falla]──► JOptionPane.warn()
    ├─► Validador.esDniOrocValido(dni)       ──[si falla]──► return
    ├─► Validador.esEnteroPositivo(cantidad) ──[si falla]──► return
    ├─► producto.tieneStock(cant)            ──[si falla]──► return
    │
    ├─► CÁLCULOS:
    │   importe  = precio × cantidad
    │   pDesc    = Tienda.obtenerDescuento(cant)
    │   descMonto = importe × (pDesc / 100)
    │   sub      = importe − descMonto
    │   igv      = sub × 0.18
    │   total    = sub + igv
    │   obs      = Tienda.obtenerObsequio(cant)
    │
    ├─► producto.descontarStock(cant)
    ├─► Tienda.numVentas++
    ├─► Tienda.importeAcumulado += total
    ├─► Tienda.guardarDatos()            → reescribe datos.txt
    ├─► HistorialVentas.registrarVenta() → append historial.txt
    │
    └─► ACTUALIZAR UI:
        - lblImporte, lblDescuento, lblSubtotal, lblIgv, lblTotal, lblObsequio
        - barraAvance (% cuota con color según avance)
        - generarBoleta() → escribe en txtBoleta
        - mostrarPrecio() → refresca detalles (nuevo stock)
```

---

## Conceptos Java Aplicados

| Tema | Dónde se ve |
|---|---|
| **POO** — clases, encapsulación (private + getters/setters) | `Producto` |
| **Interfaces + polimorfismo** | `Refrescable`, loop en `navegarA` |
| **Colecciones** — ArrayList, HashMap, HashSet | `Tienda.productos`, `calcularReporte` |
| **Arrays** | `porcentajesDescuento[]`, `obsequios[]` |
| **Métodos estáticos** | `Validador`, `HistorialVentas.registrarVenta` |
| **Constantes** (`public static final`) | `Constantes.java` |
| **Archivos (I/O)** | `Scanner` para leer, `PrintWriter` para escribir |
| **try-with-resources** | `try (Scanner sc = ...)` cierra automáticamente |
| **Manejo de excepciones** | `NumberFormatException`, `FileNotFoundException` |
| **Lambdas** (Java 8+) | `btn.addActionListener(e -> vender())` |
| **For-each** | `for (Producto p : productos)` |
| **String.format** | Formateo de `%.2f`, `%06d` |
| **SimpleDateFormat** | Fechas en boleta y barra de estado |
| **Expresiones regulares** | `"\\d+"`, `RowFilter.regexFilter` |
| **Swing (UI)** | JFrame, JPanel, JButton, JTable, JOptionPane, JDialog |
| **Layouts** | BorderLayout, CardLayout, GridLayout, GridBagLayout, BoxLayout, FlowLayout |
| **Listeners** | ActionListener, DocumentListener, MouseAdapter, FocusAdapter |
| **Timer Swing** | Reloj en la barra de estado |

---

## Preguntas de Examen

### Nivel básico

**1. ¿Qué es la clase `Producto` y qué contiene?**
> Es una clase modelo (POJO) que representa un monitor. Tiene 10 atributos privados (nombre, precio, pulgadas, hercios, resolucion, stock, tipoPanel, tiempoRespuesta, conectividad, garantiaMeses), un constructor, getters/setters, y dos métodos de negocio: `descontarStock(int)` y `tieneStock(int)`.

**2. ¿Por qué los atributos de `Producto` son `private`?**
> Por **encapsulación**. Impide el acceso directo desde fuera de la clase, obligando a usar getters/setters. Esto permite validar o modificar la lógica sin afectar a quien use la clase.

**3. ¿Qué es `Constantes.java` y para qué sirve?**
> Es una clase que solo contiene constantes `public static final`. Sirve para centralizar todos los valores fijos del sistema (IGV, rangos de descuento, nombres de archivos, mensajes de error). Evita repetir valores "mágicos" y facilita el mantenimiento.

**4. ¿Qué hace el método `main` en `Tienda`?**
> 1) Inicializa productos por defecto, 2) carga datos del archivo `datos.txt`, 3) configura el Look and Feel del sistema operativo, 4) crea la ventana `Tienda` y la muestra. Usa `EventQueue.invokeLater` para garantizar que la UI se cree en el Event Dispatch Thread.

**5. ¿Qué es un POJO?**
> Plain Old Java Object. Una clase simple con atributos privados y getters/setters, sin lógica compleja. `Producto` es un POJO.

---

### Nivel intermedio

**6. ¿Cómo funciona el sistema de descuentos?**
> Depende de la cantidad vendida:
> - 1-5 unidades → 5% de descuento
> - 6-10 → 7.5%
> - 11-15 → 10%
> - Más de 15 → 15%
>
> Implementado en `Tienda.obtenerDescuento(int cantidad)` con `if` encadenados. Los porcentajes están en el array `porcentajesDescuento[]` y son configurables desde `PanelConfDesc`.

**7. ¿Cómo se calcula el total de una venta?**
```java
importe  = precio × cantidad
descuento = importe × (porcentaje / 100)
subtotal = importe − descuento
IGV      = subtotal × 0.18
TOTAL    = subtotal + IGV
```

**8. ¿Qué es la interfaz `Refrescable` y para qué sirve?**
> Interfaz con un solo método `refrescar()`. La implementan los paneles que necesitan actualizar sus datos cuando el usuario cambia de vista. En `Tienda.navegarA()` se recorren los componentes y se llama polimórficamente al método `refrescar()` en los que implementen la interfaz.

**9. ¿Por qué las variables de `Tienda` (productos, numVentas...) son `static`?**
> Para que sean **variables de clase**, compartidas por toda la aplicación. Así cualquier panel puede acceder al estado global sin necesidad de pasar referencias. Es una simplificación útil en un proyecto pequeño (en apps reales se preferiría inyección de dependencias).

**10. ¿Cómo funciona `CardLayout`?**
> Es un gestor de layout que apila varios paneles (como cartas de una baraja) y muestra solo uno a la vez. Cada panel se asocia con una clave (String). `cardLayout.show(contenedor, "clave")` cambia la vista visible sin cerrar/reabrir ventanas.

**11. ¿Cómo funcionan las validaciones?**
> Están en la clase `Validador` como métodos estáticos. Antes de realizar una operación, el panel llama métodos como `esTextoVacio()`, `esDniOrocValido()`, `esEnteroPositivo()`. Si alguno falla, se muestra un `JOptionPane.showMessageDialog` y se aborta la operación con `return`.

**12. ¿Cómo se valida un DNI?**
```java
public static boolean esDniValido(String dni) {
    if (dni == null) return false;
    dni = dni.trim();
    return dni.length() == 8 && dni.matches("\\d+");
}
```
> Debe tener exactamente 8 caracteres y ser todo dígitos. `\\d+` es una expresión regular: `\d` = dígito, `+` = uno o más.

---

### Nivel avanzado

**13. ¿Cómo funciona la persistencia de datos?**
> Hay dos archivos:
> - **`datos.txt`**: guardado secuencial línea por línea (productos, descuentos, obsequios, cuota, ventas, monto). Se reescribe completo cada vez con `PrintWriter(FileWriter(...))`.
> - **`historial.txt`**: append (modo `true` en `FileWriter`). Cada venta agrega una línea con 8 columnas separadas por `|`.
>
> Se lee con `Scanner` usando `try-with-resources` que cierra el recurso automáticamente.

**14. ¿Qué es `try-with-resources` y por qué se usa?**
```java
try (Scanner sc = new Scanner(new File(...))) {
    // usar sc
}
```
> Es una sintaxis de Java 7+ que cierra automáticamente recursos que implementan `AutoCloseable` (como `Scanner`, `FileWriter`, etc.) al salir del bloque. Evita fugas de archivos y simplifica el código.

**15. ¿Cómo funciona la búsqueda en tiempo real en el historial?**
> El `JTextField` tiene un `DocumentListener` que se dispara con cada cambio (insertar/borrar carácter). En el listener se llama `filtrar()` que aplica un `RowFilter.regexFilter` al `TableRowSorter` de la tabla. La tabla solo muestra filas que coincidan en las columnas 2 (cliente), 3 (DNI) o 4 (producto).

**16. ¿Para qué sirve `EventQueue.invokeLater()`?**
> Swing no es thread-safe. Toda la UI debe crearse y modificarse desde el **Event Dispatch Thread (EDT)**. `invokeLater()` encola una tarea (Runnable/lambda) para que se ejecute en el EDT. Usarlo en `main()` es la práctica correcta.

**17. ¿Cómo se calcula el reporte del dashboard?**
> `calcularReporte()` lee `historial.txt` y usa:
> - `HashMap<String, Integer>` para contar ventas por producto (clave=producto, valor=cantidad acumulada). El `HashMap` permite acceso O(1).
> - `HashSet<String>` para contar clientes únicos por DNI. El `HashSet` garantiza que no haya duplicados.
> - Variables locales para venta máxima, suma y total.
> - Al final recorre el HashMap buscando el máximo valor → producto más vendido.

**18. ¿Qué es la encapsulación y dónde se aplica?**
> Ocultar los datos internos de una clase tras getters/setters. Se aplica en `Producto`: los atributos son `private` y solo accesibles mediante métodos públicos. Permite cambiar la implementación interna sin romper código externo.

**19. ¿Qué es polimorfismo y dónde se usa?**
> Que un mismo método se comporte distinto según el tipo del objeto. Se ve en `Tienda.navegarA()`:
> ```java
> for (Component c : panelContenido.getComponents()) {
>     if (c instanceof Refrescable) ((Refrescable) c).refrescar();
> }
> ```
> Cada panel implementa `refrescar()` a su manera, pero se invocan todos con la misma línea.

**20. ¿Cómo se construye la boleta de venta?**
> En `PanelVender.generarBoleta(...)` se usa `txtBoleta.append(...)` repetidas veces para construir un texto formateado. Se usa `String.format("%.2f", monto)` para 2 decimales y `String.format("%06d", nro)` para 6 dígitos con ceros a la izquierda. La fecha se genera con `SimpleDateFormat`.

**21. ¿Qué ventaja tiene usar `TemaOscuro` como clase de utilidades?**
> Centraliza todos los estilos (colores, fuentes, componentes). Si se quiere cambiar el tema, se edita un solo archivo. Evita repetir código de estilos en cada panel. Facilita consistencia visual.

**22. ¿Cómo funciona el sistema de obsequios?**
> Depende de la cantidad comprada:
> - 1 unidad → Mouse Gamer
> - 2 a 5 → Teclado Mecánico
> - Más de 5 → Silla Gamer
>
> Implementado en `Tienda.obtenerObsequio(int cantidad)` con if-else. Los nombres están en el array `obsequios[]` y son configurables desde `PanelConfObs`.

**23. ¿Por qué se usa `ArrayList` en vez de un array normal para los productos?**
> Porque el tamaño es dinámico: se pueden agregar y eliminar productos en tiempo de ejecución desde `PanelGestionar`. `ArrayList` ofrece métodos `add()`, `remove(i)`, `get(i)`, `size()` que simplifican la gestión.

**24. Explica el uso de `instanceof` en el código.**
> Operador que verifica si un objeto es de un tipo dado. En `navegarA()`:
> ```java
> if (c instanceof Refrescable) ((Refrescable) c).refrescar();
> ```
> Primero verifica si el componente implementa `Refrescable`, y luego hace un **cast** para poder llamar `refrescar()`. Previene `ClassCastException`.

**25. ¿Cómo funciona la barra de progreso de la cuota?**
```java
double porcCuota = (importeAcumulado / cuotaDiaria) * 100;
barra.setValue(Math.min((int) porcCuota, 100));  // máx 100
barra.setForeground(
    porcCuota >= 100 ? GREEN  :
    porcCuota >= 60  ? YELLOW :
    RED
);
```
> Se calcula el porcentaje, se limita a 100 para no desbordar, y se cambia el color según el avance (rojo < 60%, amarillo < 100%, verde ≥ 100%).

---

### Preguntas conceptuales

**26. ¿Qué diferencia hay entre `==` y `.equals()` en Strings?**
> `==` compara referencias (misma posición de memoria). `.equals()` compara contenido. Para Strings siempre usar `.equals()` o `.equalsIgnoreCase()`.

**27. ¿Qué es el IGV y cómo se aplica aquí?**
> Impuesto General a las Ventas (Perú), 18%. Se aplica sobre el subtotal (ya con descuento):
> ```
> IGV = subtotal × 0.18
> total = subtotal + IGV
> ```

**28. ¿Por qué el IGV se calcula sobre el subtotal y no sobre el importe?**
> Porque el descuento reduce la base imponible. Fiscalmente, el IGV se aplica al precio real que paga el cliente (después del descuento).

**29. ¿Qué pasa si el archivo `datos.txt` no existe al iniciar?**
> El `Scanner` lanza `FileNotFoundException` que se captura en `try-catch (Exception e)`. Se imprime "Cargando valores por defecto..." y la app continúa con los 5 productos iniciales.

**30. ¿Qué son los `ActionListener` y cómo se usan aquí?**
> Interfaces funcionales que escuchan eventos (clic en botón, cambio en combo). Se implementan con lambdas:
> ```java
> btnVender.addActionListener(e -> vender());
> ```
> Cuando el usuario hace clic, se ejecuta `vender()`.

---

## Resumen rápido para memorizar

- **Entrada del programa:** `Tienda.main()` → carga datos → crea JFrame.
- **Estado global:** variables `static` en `Tienda` (productos, numVentas, importeAcumulado, cuotaDiaria).
- **Navegación:** `CardLayout` + sidebar con botones.
- **Lógica de negocio:** `Tienda.obtenerDescuento`, `obtenerObsequio`, `PanelVender.vender`.
- **Validaciones:** clase `Validador` (estática).
- **Persistencia:** `datos.txt` (estado) + `historial.txt` (append).
- **UI:** `TemaOscuro` centraliza colores, fuentes y componentes.
- **Patrón:** cada panel implementa `Refrescable` para actualizarse al cambiar de vista.
