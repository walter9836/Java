/**
 * CLASE VALIDADOR
 * ---------------
 * Contiene métodos estáticos para validar entradas del usuario.
 *
 * ¿Por qué todos los métodos son static?
 * Porque no necesitan guardar estado: son funciones puras (dado un input
 * devuelven un output). Se usan directamente como Validador.esDniValido("123")
 * sin necesidad de crear un objeto.
 *
 * ¿Por qué separar las validaciones en una clase propia?
 * Para REUTILIZAR: las mismas validaciones se usan en PanelVender, PanelModificar,
 * PanelGestionar. Si se cambian las reglas, se modifica un solo archivo.
 */
public class Validador {

    /**
     * Verifica si un texto está vacío o es null.
     * trim() elimina espacios al inicio/final: "   " también se considera vacío.
     */
    public static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Valida un DNI peruano: exactamente 8 dígitos numéricos.
     * \\d+ es una expresión regular: \d = dígito, + = uno o más.
     * Se escribe \\d porque en Java, \ se escapa dentro de un String.
     */
    public static boolean esDniValido(String dni) {
        if (dni == null) return false;
        dni = dni.trim();
        return dni.length() == Constantes.LONGITUD_DNI && dni.matches("\\d+");
    }

    /**
     * Valida un RUC peruano: exactamente 11 dígitos numéricos.
     * Misma lógica que el DNI pero con distinta longitud.
     */
    public static boolean esRucValido(String ruc) {
        if (ruc == null) return false;
        ruc = ruc.trim();
        return ruc.length() == Constantes.LONGITUD_RUC && ruc.matches("\\d+");
    }

    /**
     * Acepta DNI (persona natural) o RUC (empresa).
     * Útil para ventas que pueden ser a cualquiera de los dos tipos de cliente.
     */
    public static boolean esDniOrocValido(String valor) {
        return esDniValido(valor) || esRucValido(valor);
    }

    /**
     * Valida que el texto sea un entero > 0.
     * ¿Por qué try-catch? Porque Integer.parseInt() lanza NumberFormatException
     * si el texto no es numérico (ej: "abc"). En vez de romper la app, devolvemos false.
     */
    public static boolean esEnteroPositivo(String texto) {
        try {
            return Integer.parseInt(texto.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida entero >= 0 (acepta 0). Útil para stock, que puede ser cero.
     */
    public static boolean esEnteroNoNegativo(String texto) {
        try {
            return Integer.parseInt(texto.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida decimal > 0. Double.parseDouble() acepta "12.50" o "12,50" según locale.
     * Útil para precios y pulgadas (que deben ser mayor que cero).
     */
    public static boolean esDecimalPositivo(String texto) {
        try {
            return Double.parseDouble(texto.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida decimal >= 0 (acepta 0).
     */
    public static boolean esDecimalNoNegativo(String texto) {
        try {
            return Double.parseDouble(texto.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida TODOS los campos numéricos de un producto de una sola vez.
     *
     * Devuelve null si todo está bien, o un mensaje específico que distingue
     * entre "no es número" (ej: el usuario escribió letras) y "el número no
     * cumple la regla" (ej: es negativo o cero). Antes ambos casos daban el
     * mismo mensaje y confundía al usuario.
     */
    public static String validarProducto(String precio, String pulgadas, String hz,
                                          String stock, String tr, String garantia) {
        String err;
        if ((err = validarDecimalPositivo("Precio", precio, "(ej: 1500.00)")) != null) return err;
        if ((err = validarDecimalPositivo("Pulgadas", pulgadas, "(ej: 27 o 27.5)")) != null) return err;
        if ((err = validarEnteroPositivo("Frecuencia (Hz)", hz, "(ej: 144)")) != null) return err;
        if ((err = validarEnteroNoNegativo("Stock", stock, "(ej: 10)")) != null) return err;
        if ((err = validarEnteroNoNegativo("Tiempo de respuesta", tr, "(ej: 1)")) != null) return err;
        if ((err = validarEnteroPositivo("Garantía (meses)", garantia, "(ej: 12)")) != null) return err;
        return null;
    }

    /**
     * Valida un decimal > 0 con mensaje de error específico.
     * Distingue: vacío, no numérico, o no positivo.
     */
    private static String validarDecimalPositivo(String campo, String texto, String ejemplo) {
        if (esTextoVacio(texto)) return "El campo '" + campo + "' está vacío";
        try {
            double v = Double.parseDouble(texto.trim());
            if (v <= 0) return "El campo '" + campo + "' debe ser mayor a cero";
            return null;
        } catch (NumberFormatException e) {
            return "El campo '" + campo + "' debe ser un número " + ejemplo;
        }
    }

    /** Valida un entero > 0 con mensaje específico. */
    private static String validarEnteroPositivo(String campo, String texto, String ejemplo) {
        if (esTextoVacio(texto)) return "El campo '" + campo + "' está vacío";
        try {
            int v = Integer.parseInt(texto.trim());
            if (v <= 0) return "El campo '" + campo + "' debe ser mayor a cero";
            return null;
        } catch (NumberFormatException e) {
            return "El campo '" + campo + "' debe ser un número entero " + ejemplo;
        }
    }

    /** Valida un entero >= 0 con mensaje específico. */
    private static String validarEnteroNoNegativo(String campo, String texto, String ejemplo) {
        if (esTextoVacio(texto)) return "El campo '" + campo + "' está vacío";
        try {
            int v = Integer.parseInt(texto.trim());
            if (v < 0) return "El campo '" + campo + "' no puede ser negativo";
            return null;
        } catch (NumberFormatException e) {
            return "El campo '" + campo + "' debe ser un número entero " + ejemplo;
        }
    }
}
