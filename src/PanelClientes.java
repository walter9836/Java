import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * PANEL CLIENTES
 * --------------
 * Lee historial.txt y muestra una tabla AGRUPADA POR CLIENTE (DNI/RUC).
 *
 * No persiste datos propios: deriva todo del historial. Cada vez que se entra
 * al panel se recalcula desde cero, así refleja siempre las ventas más recientes.
 *
 * Datos por cliente:
 *   - DNI/RUC          → clave única
 *   - Nombre           → último nombre registrado para ese DNI
 *   - # Compras        → cantidad de ventas asociadas
 *   - Monto total      → suma de todas sus compras
 *   - Última compra    → fecha de la venta más reciente
 *
 * Permite búsqueda en tiempo real por DNI o nombre (igual que PanelHistorial).
 */
public class PanelClientes extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    private DefaultTableModel modelo;
    private JTable tabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JLabel lblTotalClientes, lblMontoTotal;

    // Estructura interna que acumula datos por cliente mientras se lee el historial.
    // Se usa LinkedHashMap para preservar el orden de aparición (primer cliente arriba).
    private static class Cliente {
        String dni;
        String nombre;
        int numCompras;
        double montoTotal;
        String ultimaCompra; // fecha como string (formato dd/MM/yyyy HH:mm)
    }

    public PanelClientes() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        // --- Header con título y barra de búsqueda ---
        JPanel header = TemaOscuro.crearHeaderPagina("Clientes", "Información derivada del historial de ventas");
        JPanel headerDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerDer.setOpaque(false);
        headerDer.add(TemaOscuro.crearLabelPequeno("Buscar:"));
        txtBuscar = TemaOscuro.crearCampoTexto(true);
        txtBuscar.setPreferredSize(new Dimension(220, 30));

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        headerDer.add(txtBuscar);
        header.add(headerDer, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Tabla ---
        String[] cols = {"DNI / RUC", "Nombre", "# Compras", "Monto total (S/.)", "Última compra"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        TemaOscuro.aplicarTemaTabla(tabla);
        // Anchos relativos: DNI compacto, Nombre amplio, contadores compactos
        tabla.getColumnModel().getColumn(0).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(150);

        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        add(TemaOscuro.crearScrollPane(tabla), BorderLayout.CENTER);

        // --- Footer ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 6));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));
        lblTotalClientes = TemaOscuro.crearLabelPequeno("Clientes: 0");
        lblMontoTotal = TemaOscuro.crearValorLabel("Total: S/. 0.00");
        lblMontoTotal.setForeground(TemaOscuro.GREEN);
        footer.add(lblTotalClientes);
        footer.add(lblMontoTotal);
        add(footer, BorderLayout.SOUTH);

        cargar();
    }

    /**
     * Filtra la tabla por DNI o nombre. Igual patrón que PanelHistorial.
     * Columnas 0 (DNI/RUC) y 1 (Nombre).
     */
    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), 0, 1));
        }
        actualizarTotales();
    }

    /**
     * Lee historial.txt y agrupa por DNI/RUC.
     *
     * Estrategia:
     *   - LinkedHashMap<DNI, Cliente> para acumular datos manteniendo orden de aparición.
     *   - Por cada línea del historial se actualiza el cliente correspondiente:
     *       contador de compras++, suma del monto, último nombre y última fecha.
     *
     * Formato de cada línea de historial.txt (separado por "|"):
     *   nro | fecha | cliente | dni | producto | cantidad | total | metodoPago
     *      0      1         2     3          4          5       6             7
     */
    private void cargar() {
        modelo.setRowCount(0);
        LinkedHashMap<String, Cliente> mapa = new LinkedHashMap<>();

        try (Scanner sc = new Scanner(new File(Constantes.ARCHIVO_HISTORIAL))) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(Constantes.SEPARADOR_HISTORIAL);
                if (p.length < Constantes.COLUMNAS_HISTORIAL) continue; // línea malformada

                String fecha   = p[1].trim();
                String nombre  = p[2].trim();
                String dni     = p[3].trim();
                double monto;
                try {
                    monto = Double.parseDouble(p[6].trim());
                } catch (NumberFormatException e) {
                    continue; // monto inválido → saltar
                }

                // Acumular en el mapa (computeIfAbsent crea el cliente si no existe)
                Cliente c = mapa.computeIfAbsent(dni, k -> {
                    Cliente nuevo = new Cliente();
                    nuevo.dni = k;
                    return nuevo;
                });
                c.nombre = nombre; // último nombre registrado gana
                c.numCompras++;
                c.montoTotal += monto;
                // Comparar fechas como Date para encontrar la más reciente
                if (c.ultimaCompra == null || esMasReciente(fecha, c.ultimaCompra, fmt)) {
                    c.ultimaCompra = fecha;
                }
            }
        } catch (FileNotFoundException ignored) {
            // Sin historial → tabla vacía
        }

        // Volcar el mapa a filas de la tabla
        for (Cliente c : mapa.values()) {
            modelo.addRow(new Object[]{
                c.dni,
                c.nombre,
                c.numCompras,
                String.format("%.2f", c.montoTotal),
                c.ultimaCompra != null ? c.ultimaCompra : "-"
            });
        }

        actualizarTotales();
    }

    /**
     * Compara dos fechas en formato "dd/MM/yyyy HH:mm".
     * @return true si 'nueva' es posterior a 'actual'.
     */
    private boolean esMasReciente(String nueva, String actual, SimpleDateFormat fmt) {
        try {
            Date dn = fmt.parse(nueva);
            Date da = fmt.parse(actual);
            return dn.after(da);
        } catch (Exception e) {
            return false; // formato inválido → no actualizar
        }
    }

    /** Calcula totales del footer respetando el filtro de búsqueda. */
    private void actualizarTotales() {
        double total = 0;
        int count = tabla.getRowCount(); // respeta el filtro
        for (int i = 0; i < count; i++) {
            try {
                String val = tabla.getValueAt(i, 3).toString();
                total += Double.parseDouble(val);
            } catch (Exception ignored) {}
        }
        lblTotalClientes.setText("Clientes: " + count);
        lblMontoTotal.setText("Total: S/. " + String.format("%.2f", total));
    }

    @Override
    public void refrescar() { cargar(); }
}
