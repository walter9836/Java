import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * CLASE NOTIFICADOR
 * -----------------
 * Centraliza TODOS los popups de la aplicación. Garantiza que los mensajes
 * de error, advertencia, éxito y confirmación tengan el mismo formato
 * (mismo título, mismo icono, misma redacción) en todos los paneles.
 *
 * Antes existían 5 títulos distintos ("Error", "Éxito", "Validación",
 * "Atención", "Confirmar") y los mensajes se escribían a mano en cada
 * lugar. Ahora todo pasa por aquí.
 *
 * Uso típico:
 *     Notificador.error(this, "Stock insuficiente");
 *     Notificador.exito(this, "Cambios guardados");
 *     if (Notificador.confirmar(this, "¿Eliminar?")) { ... }
 *     Notificador.campoVacio(this, "Cliente");
 */
public class Notificador {

    /** Constructor privado: clase utilitaria, no se instancia. */
    private Notificador() {}

    /** Popup ROJO. Para fallos graves: archivo no se pudo guardar, error numérico, etc. */
    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje,
                Constantes.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
    }

    /** Popup AMARILLO. Para validaciones: campo vacío, formato inválido, sin stock. */
    public static void advertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje,
                Constantes.TITULO_ATENCION, JOptionPane.WARNING_MESSAGE);
    }

    /** Popup AZUL. Para confirmar acciones exitosas: producto agregado, cambios guardados. */
    public static void exito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje,
                Constantes.TITULO_EXITO, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Popup SI/NO para acciones destructivas (eliminar, salir).
     * @return true si el usuario presiona SI, false en cualquier otro caso.
     */
    public static boolean confirmar(Component padre, String mensaje) {
        int r = JOptionPane.showConfirmDialog(padre, mensaje,
                Constantes.TITULO_CONFIRMAR,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }

    /**
     * Atajo para "el campo X no puede estar vacío".
     * Genera el mensaje desde el nombre del campo, así todos los paneles
     * dicen exactamente lo mismo.
     */
    public static void campoVacio(Component padre, String nombreCampo) {
        advertencia(padre, "El campo '" + nombreCampo + "' no puede estar vacío");
    }

    /** Atajo para "el campo X no es válido". */
    public static void campoInvalido(Component padre, String nombreCampo) {
        advertencia(padre, "El campo '" + nombreCampo + "' no es válido");
    }
}
