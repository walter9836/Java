import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * PANEL LISTADO GENERAL
 * ---------------------
 * Tabla con TODOS los productos del catálogo (10 atributos).
 * Vista de solo lectura (no se permite edición de celdas).
 *
 * Incluye búsqueda en tiempo real por nombre/resolución/panel/conectividad
 * — mismo patrón que PanelHistorial para mantener consistencia visual.
 */
public class PanelListar extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    private DefaultTableModel modelo;
    private JTable tabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JLabel lblTotal;

    public PanelListar() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        // --- Header con contador y barra de búsqueda ---
        JPanel header = TemaOscuro.crearHeaderPagina("Listado General", null);
        JPanel headerDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerDer.setOpaque(false);

        lblTotal = new JLabel(Tienda.productos.size() + " productos registrados");
        lblTotal.setFont(TemaOscuro.FONT_SM);
        lblTotal.setForeground(TemaOscuro.OVERLAY0);
        headerDer.add(lblTotal);

        headerDer.add(TemaOscuro.crearLabelPequeno("Buscar:"));
        txtBuscar = TemaOscuro.crearCampoTexto(true);
        txtBuscar.setPreferredSize(new Dimension(200, 30));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        headerDer.add(txtBuscar);

        header.add(headerDer, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Modelo", "Precio (S/.)", "Pulgadas", "Hz", "Resolución",
                "Stock", "Panel", "T.Resp (ms)", "Conectividad", "Garantía"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        TemaOscuro.aplicarTemaTabla(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(8).setPreferredWidth(120);

        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        add(TemaOscuro.crearScrollPane(tabla), BorderLayout.CENTER);
        cargar();
    }

    /**
     * Filtra por modelo (col 0), resolución (4), panel (6) o conectividad (8).
     * (?i) hace la búsqueda insensible a mayúsculas.
     */
    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), 0, 4, 6, 8));
        }
        lblTotal.setText(tabla.getRowCount() + " productos");
    }

    /** Recarga la tabla desde Tienda.productos. */
    private void cargar() {
        modelo.setRowCount(0);
        for (Producto p : Tienda.productos) {
            modelo.addRow(new Object[]{
                p.getNombre(),
                String.format("%.2f", p.getPrecio()),
                p.getPulgadas() + "\"",
                p.getHercios(),
                p.getResolucion(),
                p.getStock(),
                p.getTipoPanel(),
                p.getTiempoRespuesta(),
                p.getConectividad(),
                p.getGarantiaMeses() + " m"
            });
        }
        lblTotal.setText(Tienda.productos.size() + " productos registrados");
    }

    @Override
    public void refrescar() { cargar(); }
}
