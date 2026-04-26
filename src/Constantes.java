/**
 * CLASE CONSTANTES
 * ----------------
 * Centraliza todos los valores fijos ("valores mágicos") del sistema.
 *
 * ¿Por qué una clase solo de constantes?
 * - Evita repetir números o strings por todo el código.
 * - Si cambia un valor (ej: IGV sube a 19%), se modifica en UN solo lugar.
 * - Los nombres descriptivos hacen el código más legible:
 *     if (stock < STOCK_MINIMO_ALERTA)     ← claro
 *     if (stock < 5)                        ← ¿qué es 5?
 *
 * ¿Por qué public static final?
 * - public  → accesible desde cualquier clase
 * - static  → no necesita instanciar Constantes (se usa como Constantes.IGV)
 * - final   → valor inmutable, no se puede reasignar
 */
public class Constantes {

    // ========== IMPUESTO ==========
    // IGV de Perú = 18%. Se usa en el cálculo del total de cada venta.
    public static final double IGV = 0.18;

    // ========== RANGOS DE CANTIDAD PARA DESCUENTOS ==========
    // Regla de negocio: a más cantidad, más descuento.
    // Estos valores se usan en Tienda.obtenerDescuento(int cantidad)
    public static final int RANGO_DESC_1_MIN = 1;
    public static final int RANGO_DESC_1_MAX = 5;    // 1-5  unidades → 5%
    public static final int RANGO_DESC_2_MIN = 6;
    public static final int RANGO_DESC_2_MAX = 10;   // 6-10 unidades → 7.5%
    public static final int RANGO_DESC_3_MIN = 11;
    public static final int RANGO_DESC_3_MAX = 15;   // 11-15 → 10%
                                                      // >15   → 15%

    // ========== RANGOS DE CANTIDAD PARA OBSEQUIOS ==========
    // Cada rango entrega un regalo distinto (Mouse / Teclado / Silla)
    public static final int RANGO_OBS_1 = 1;          // 1 unidad  → Mouse
    public static final int RANGO_OBS_2_MIN = 2;
    public static final int RANGO_OBS_2_MAX = 5;      // 2-5       → Teclado
                                                       // >5        → Silla

    // ========== VALIDACIÓN DE DOCUMENTOS (PERÚ) ==========
    // DNI = 8 dígitos (personas), RUC = 11 dígitos (empresas)
    public static final int LONGITUD_DNI = 8;
    public static final int LONGITUD_RUC = 11;

    // ========== STOCK ==========
    // Si el stock baja de este valor, se muestra alerta roja/amarilla
    public static final int STOCK_MINIMO_ALERTA = 5;
    public static final int STOCK_MINIMO = 0;

    // ========== ARCHIVOS DE PERSISTENCIA ==========
    // Usamos archivos de texto plano por simplicidad (no base de datos)
    public static final String ARCHIVO_DATOS = "datos.txt";         // estado general
    public static final String ARCHIVO_HISTORIAL = "historial.txt"; // ventas
    // "\\|" escapa el pipe porque split() usa regex y | es metacarácter
    public static final String SEPARADOR_HISTORIAL = "\\|";
    public static final int COLUMNAS_HISTORIAL = 8;  // nro|fecha|cliente|dni|prod|cant|total|pago

    // Prefijo que se antepone al método de pago cuando una venta es anulada.
    // Permite distinguir visualmente las ventas anuladas en el historial sin
    // borrar la fila (mantenemos trazabilidad).
    public static final String MARCA_ANULADA = "[ANULADA] ";

    // ========== TÍTULOS DE LOS POPUPS ==========
    // Centralizado para que todos los JOptionPane usen exactamente el mismo
    // título. Los usa Notificador.java internamente.
    public static final String TITULO_ERROR = "Error";
    public static final String TITULO_EXITO = "Éxito";
    public static final String TITULO_ATENCION = "Atención";
    public static final String TITULO_CONFIRMAR = "Confirmar";

    // ========== MENSAJES DE ERROR ==========
    // Centralizar mensajes permite traducir o modificar todos los textos en un solo lugar
    public static final String ERR_CAMPO_VACIO = "Este campo es obligatorio";
    public static final String ERR_DNI_INVALIDO = "El DNI debe tener 8 dígitos numéricos";
    public static final String ERR_RUC_INVALIDO = "El RUC debe tener 11 dígitos numéricos";
    public static final String ERR_DNI_RUC_INVALIDO = "Ingrese un DNI (8 dígitos) o RUC (11 dígitos) válido";
    public static final String ERR_CANTIDAD_POSITIVA = "La cantidad debe ser mayor a cero";
    public static final String ERR_CANTIDAD_ENTERA = "La cantidad debe ser un número entero";
    public static final String ERR_PRECIO_POSITIVO = "El precio debe ser mayor a cero";
    public static final String ERR_STOCK_NEGATIVO = "El stock no puede ser negativo";
    public static final String ERR_STOCK_INSUFICIENTE = "Stock insuficiente. Disponible: ";
    public static final String ERR_NUMERICO_INVALIDO = "Verifique que los campos numéricos tengan valores válidos";
    public static final String ERR_NOMBRE_CLIENTE = "Ingrese el nombre del cliente";
    public static final String ERR_DNI_CLIENTE = "Ingrese el DNI o RUC del cliente";
    public static final String ERR_CANTIDAD_REQUERIDA = "Ingrese la cantidad";

    // ========== MENSAJES DE ÉXITO ==========
    public static final String MSG_GUARDADO_OK = "Cambios guardados correctamente";
    public static final String MSG_DESCUENTOS_OK = "Descuentos actualizados";
    public static final String MSG_OBSEQUIOS_OK = "Obsequios actualizados";
    public static final String MSG_CUOTA_OK = "Cuota actualizada";
    public static final String MSG_PRODUCTO_AGREGADO = "Producto agregado correctamente";
    public static final String MSG_PRODUCTO_ELIMINADO = "Producto eliminado correctamente";

    // ========== INFORMACIÓN DE LA APLICACIÓN ==========
    public static final String APP_NOMBRE = "GamerStore";
    public static final String APP_VERSION = "2.0";
    public static final String APP_SUBTITULO = "Sistema de Ventas - Monitores Gamer";
}
