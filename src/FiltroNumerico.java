import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * CLASE FILTRO NUMERICO
 * ---------------------
 * Bloquea la entrada de caracteres no permitidos EN TIEMPO DE ESCRITURA.
 * Si el usuario intenta escribir una letra en un campo de cantidad, el carácter
 * simplemente no aparece (en lugar de mostrar un error después de presionar
 * "Guardar").
 *
 * Cómo se usa:
 *     FiltroNumerico.aplicarEntero(txtCantidad);   // solo dígitos: 0-9
 *     FiltroNumerico.aplicarDecimal(txtPrecio);    // dígitos + UN punto: 12.50
 *
 * Nota técnica:
 *   Swing usa DocumentFilter para interceptar inserciones/reemplazos antes de
 *   que el carácter llegue al modelo del campo. Aceptamos o rechazamos según
 *   un regex aplicado al texto resultante (no al carácter aislado, así se
 *   evita "1." aceptado pero ".1" rechazado, o viceversa).
 */
public class FiltroNumerico {

    /** Constructor privado: clase utilitaria, no se instancia. */
    private FiltroNumerico() {}

    /** Permite SOLO dígitos enteros (sin signo, sin decimales). */
    public static void aplicarEntero(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr)
                    throws BadLocationException {
                if (esEnteroValido(textoResultante(fb, offset, 0, texto))) {
                    super.insertString(fb, offset, texto, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attr)
                    throws BadLocationException {
                if (esEnteroValido(textoResultante(fb, offset, length, texto))) {
                    super.replace(fb, offset, length, texto, attr);
                }
            }
        });
    }

    /** Permite dígitos y como máximo UN punto decimal (formato 12.50, 0.5, 100, etc.). */
    public static void aplicarDecimal(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr)
                    throws BadLocationException {
                if (esDecimalValido(textoResultante(fb, offset, 0, texto))) {
                    super.insertString(fb, offset, texto, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attr)
                    throws BadLocationException {
                if (esDecimalValido(textoResultante(fb, offset, length, texto))) {
                    super.replace(fb, offset, length, texto, attr);
                }
            }
        });
    }

    /** Calcula cómo quedaría el campo si se aplicara la inserción/reemplazo. */
    private static String textoResultante(DocumentFilter.FilterBypass fb, int offset, int length, String texto)
            throws BadLocationException {
        String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
        // Construye el texto resultante: prefijo + nuevo + sufijo
        return actual.substring(0, offset) + texto + actual.substring(offset + length);
    }

    /** Vacío también es válido (permite borrar todo el campo). */
    private static boolean esEnteroValido(String s) {
        return s.isEmpty() || s.matches("\\d+");
    }

    /**
     * Acepta vacío, "12", "12.", "12.5", ".5". Rechaza dos puntos, letras, etc.
     * Regex: dígitos opcionales + (punto opcional + dígitos opcionales) — pero al menos uno de los lados.
     */
    private static boolean esDecimalValido(String s) {
        return s.isEmpty() || s.matches("\\d*\\.?\\d*") && s.indexOf('.') == s.lastIndexOf('.');
    }
}
