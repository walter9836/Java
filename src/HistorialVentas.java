import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * CLASE HISTORIAL DE VENTAS
 * -------------------------
 * Responsable de registrar cada venta en el archivo "historial.txt".
 *
 * ¿Por qué una clase separada?
 * Para cumplir el principio de responsabilidad única: esta clase SOLO se encarga
 * de guardar ventas en archivo. No se mezcla con la lógica de UI ni de cálculos.
 *
 * ¿Por qué estática?
 * Porque es una operación simple sin estado: recibe datos y los escribe.
 * Se usa como HistorialVentas.registrarVenta(...) sin instanciar.
 */
public class HistorialVentas {

    /**
     * Agrega una línea al archivo historial.txt con los datos de la venta.
     *
     * Formato de la línea (8 columnas separadas por |):
     *   000001|16/04/2026 14:30|Juan Pérez|12345678|ASUS ROG|2|5950.00|Efectivo
     *
     * @param nroVenta   Número correlativo (se formatea con ceros: 000001)
     * @param cliente    Nombre del cliente
     * @param dni        DNI o RUC
     * @param producto   Nombre del monitor vendido
     * @param cantidad   Unidades vendidas
     * @param total      Monto total con IGV
     * @param metodoPago "Efectivo", "Tarjeta", etc.
     */
    public static void registrarVenta(int nroVenta, String cliente, String dni,
                                       String producto, int cantidad, double total, String metodoPago) {
        // try-with-resources: cierra el PrintWriter automáticamente al salir del bloque.
        // FileWriter(..., true) → el 'true' es MODO APPEND: agrega al final sin borrar
        // el contenido previo. Si fuera false, cada venta sobreescribiría todo.
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constantes.ARCHIVO_HISTORIAL, true))) {

            // Formato de fecha: día/mes/año hora:minuto
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

            // String.format("%06d", n) → número con 6 dígitos y ceros a la izquierda (000001)
            // String.format("%.2f", n) → decimal con 2 posiciones (5950.00)
            pw.println(String.format("%06d", nroVenta) + "|" + fecha + "|" + cliente + "|" +
                    dni + "|" + producto + "|" + cantidad + "|" +
                    String.format("%.2f", total) + "|" + metodoPago);
        } catch (Exception e) {
            // Si hay error de escritura, se imprime en consola para depuración.
            // En una app real se mostraría al usuario, pero aquí se falla silenciosamente.
            e.printStackTrace();
        }
    }

    /**
     * Marca una venta como ANULADA en el archivo historial.txt.
     *
     * Como el archivo es de solo append, para "modificar" una línea hay que:
     *   1. Leer todas las líneas a memoria.
     *   2. Modificar la que corresponde al número de venta dado.
     *   3. Reescribir el archivo completo.
     *
     * La marca consiste en anteponer "[ANULADA] " al método de pago. Así la
     * fila sigue ahí (trazabilidad) pero queda visualmente identificada.
     *
     * @param nroVenta número correlativo de la venta a anular (formato sin padding)
     * @return true si se encontró y se marcó, false si no se encontró
     */
    public static boolean anularVenta(int nroVenta) {
        File archivo = new File(Constantes.ARCHIVO_HISTORIAL);
        if (!archivo.exists()) return false;

        List<String> lineas = new ArrayList<>();
        boolean encontrada = false;
        String nroBuscado = String.format("%06d", nroVenta);

        try (Scanner sc = new Scanner(archivo)) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] partes = linea.split(Constantes.SEPARADOR_HISTORIAL);
                // Si la línea está bien formada y el primer campo (nro) coincide → anularla
                if (partes.length >= Constantes.COLUMNAS_HISTORIAL && partes[0].trim().equals(nroBuscado)) {
                    // Si ya estaba anulada, se devuelve false sin tocar nada
                    if (partes[7].startsWith(Constantes.MARCA_ANULADA)) return false;
                    partes[7] = Constantes.MARCA_ANULADA + partes[7];
                    linea = String.join("|", partes);
                    encontrada = true;
                }
                lineas.add(linea);
            }
        } catch (Exception e) {
            return false;
        }

        if (!encontrada) return false;

        // Reescribir el archivo completo (FileWriter sin 'true' → sobreescribe)
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constantes.ARCHIVO_HISTORIAL))) {
            for (String l : lineas) pw.println(l);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
