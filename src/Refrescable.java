/**
 * INTERFAZ REFRESCABLE
 * --------------------
 * Define un contrato: "quien implemente esta interfaz debe tener un método refrescar()".
 *
 * ¿Por qué una interfaz y no una clase?
 * Porque los paneles ya heredan de JPanel (Java no permite herencia múltiple).
 * Una interfaz permite que un panel sea JPanel + Refrescable al mismo tiempo.
 *
 * ¿Para qué sirve?
 * Permite POLIMORFISMO: en Tienda.navegarA() se recorren todos los paneles y
 * se llama refrescar() sobre los que implementen la interfaz. Cada panel
 * decide cómo refrescar sus datos (recargar combo, tabla, etc.).
 *
 * Ejemplo en Tienda.navegarA():
 *   for (Component c : panelContenido.getComponents()) {
 *       if (c instanceof Refrescable) ((Refrescable) c).refrescar();
 *   }
 */
public interface Refrescable {
    /**
     * Método que cada panel debe implementar.
     * Se llama al cambiar de vista para que los datos mostrados estén actualizados.
     */
    void refrescar();
}
