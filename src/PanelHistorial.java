import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * PANEL HISTORIAL DE VENTAS
 * -------------------------
 * Muestra una tabla con todas las ventas registradas en historial.txt.
 *
 * Funciones:
 *   - Búsqueda en tiempo real por cliente, DNI o producto.
 *   - Filtro por rango de fecha (Todas / Hoy / Esta semana / Este mes).
 *   - Anular venta del día (restaura stock + descuenta del importe acumulado).
 *
 * Implementa Refrescable: cuando se entra al panel, se recarga el archivo.
 */
public class PanelHistorial extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    // Opciones del filtro de fecha
    private static final String FILTRO_TODAS = "Todas";
    private static final String FILTRO_HOY = "Hoy";
    private static final String FILTRO_SEMANA = "Esta semana";
    private static final String FILTRO_MES = "Este mes";

    private DefaultTableModel modelo;
    private JTable tabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JComboBox<String> cboFiltroFecha;
    private JLabel lblTotalReg, lblTotalMonto;

    public PanelHistorial() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        // --- Header con título, filtro de fecha y barra de búsqueda ---
        JPanel header = TemaOscuro.crearHeaderPagina("Historial de Ventas", null);
        JPanel headerDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerDer.setOpaque(false);

        // Combo de filtro de fecha
        headerDer.add(TemaOscuro.crearLabelPequeno("Período:"));
        cboFiltroFecha = new JComboBox<>(new String[]{FILTRO_TODAS, FILTRO_HOY, FILTRO_SEMANA, FILTRO_MES});
        cboFiltroFecha.setFont(TemaOscuro.FONT_BASE);
        cboFiltroFecha.setPreferredSize(new Dimension(140, 30));
        cboFiltroFecha.addActionListener(e -> filtrar());
        headerDer.add(cboFiltroFecha);

        // Búsqueda por texto
        headerDer.add(TemaOscuro.crearLabelPequeno("Buscar:"));
        txtBuscar = TemaOscuro.crearCampoTexto(true);
        txtBuscar.setPreferredSize(new Dimension(180, 30));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        headerDer.add(txtBuscar);

        header.add(headerDer, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Tabla de ventas ---
        String[] cols = {"Nro", "Fecha", "Cliente", "DNI/RUC", "Producto", "Cant.", "Total (S/.)", "Pago"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        TemaOscuro.aplicarTemaTabla(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(40);

        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        // Doble clic en una fila → muestra la boleta de esa venta antigua
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) verBoleta();
            }
        });

        add(TemaOscuro.crearScrollPane(tabla), BorderLayout.CENTER);

        // --- Footer: botón anular + totales ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel footerIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        footerIzq.setOpaque(false);
        JButton btnAnular = TemaOscuro.crearBoton("Anular venta", TemaOscuro.RED);
        btnAnular.addActionListener(e -> anular());
        footerIzq.add(btnAnular);
        footer.add(footerIzq, BorderLayout.WEST);

        JPanel footerDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 6));
        footerDer.setOpaque(false);
        lblTotalReg = TemaOscuro.crearLabelPequeno("Registros: 0");
        lblTotalMonto = TemaOscuro.crearValorLabel("Total: S/. 0.00");
        lblTotalMonto.setForeground(TemaOscuro.GREEN);
        footerDer.add(lblTotalReg);
        footerDer.add(lblTotalMonto);
        footer.add(footerDer, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);

        cargar();
    }

    /**
     * Aplica filtro combinado: texto de búsqueda + rango de fecha.
     * Se construye un RowFilter compuesto que pasa solo las filas que cumplen ambos.
     */
    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        String periodo = (String) cboFiltroFecha.getSelectedItem();

        java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<>();
        if (!texto.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), 2, 3, 4));
        }
        RowFilter<Object, Object> filtroFecha = construirFiltroFecha(periodo);
        if (filtroFecha != null) filtros.add(filtroFecha);

        if (filtros.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.andFilter(filtros));

        actualizarTotales();
    }

    /**
     * Construye un RowFilter que acepta solo filas cuya columna Fecha (índice 1)
     * cae dentro del período seleccionado. Devuelve null para "Todas" (sin filtro).
     */
    private RowFilter<Object, Object> construirFiltroFecha(String periodo) {
        if (periodo == null || periodo.equals(FILTRO_TODAS)) return null;

        // Calcular el límite inferior según el período
        Calendar limite = Calendar.getInstance();
        limite.set(Calendar.HOUR_OF_DAY, 0);
        limite.set(Calendar.MINUTE, 0);
        limite.set(Calendar.SECOND, 0);
        limite.set(Calendar.MILLISECOND, 0);
        if (periodo.equals(FILTRO_SEMANA)) {
            // Retroceder hasta el lunes (Calendar.MONDAY = 2)
            int diaActual = limite.get(Calendar.DAY_OF_WEEK);
            int retroceso = (diaActual - Calendar.MONDAY + 7) % 7;
            limite.add(Calendar.DAY_OF_MONTH, -retroceso);
        } else if (periodo.equals(FILTRO_MES)) {
            limite.set(Calendar.DAY_OF_MONTH, 1);
        }
        // Para "Hoy" basta con la fecha de hoy a las 00:00 (ya seteada arriba)
        Date desde = limite.getTime();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        return new RowFilter<Object, Object>() {
            @Override
            public boolean include(Entry<? extends Object, ? extends Object> entry) {
                String fechaStr = entry.getStringValue(1);
                try {
                    Date fila = fmt.parse(fechaStr);
                    return !fila.before(desde);
                } catch (Exception e) {
                    return false; // formato inválido → no se muestra
                }
            }
        };
    }

    /** Lee historial.txt y llena la tabla. */
    private void cargar() {
        modelo.setRowCount(0);
        try (Scanner sc = new Scanner(new File(Constantes.ARCHIVO_HISTORIAL))) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(Constantes.SEPARADOR_HISTORIAL);
                if (p.length >= Constantes.COLUMNAS_HISTORIAL) modelo.addRow(p);
            }
        } catch (FileNotFoundException ignored) {
            // Sin archivo → tabla vacía
        }
        actualizarTotales();
    }

    /**
     * Cuenta filas visibles y suma el total. Las ventas anuladas (prefijo en
     * la columna "Pago") se cuentan pero NO se suman al monto total.
     */
    private void actualizarTotales() {
        double total = 0;
        int count = tabla.getRowCount();
        for (int i = 0; i < count; i++) {
            try {
                String pago = tabla.getValueAt(i, 7).toString();
                if (pago.startsWith(Constantes.MARCA_ANULADA)) continue; // anuladas no suman
                String val = tabla.getValueAt(i, 6).toString();
                total += Double.parseDouble(val);
            } catch (Exception ignored) {}
        }
        lblTotalReg.setText("Registros: " + count);
        lblTotalMonto.setText("Total: S/. " + String.format("%.2f", total));
    }

    /**
     * Anula la venta seleccionada. Reglas:
     *   1. Debe haber una fila seleccionada.
     *   2. Solo se anulan ventas del DÍA ACTUAL (política de POS reales).
     *   3. La venta no debe estar ya anulada.
     *   4. El producto debe existir en el catálogo (para devolverle el stock).
     *
     * Efectos de anular:
     *   - Restaura el stock al producto (suma la cantidad vendida).
     *   - Descuenta del importeAcumulado y ventasHoy.
     *   - Marca la línea en historial.txt con prefijo [ANULADA].
     *   - Persiste el cambio.
     */
    private void anular() {
        int filaVisible = tabla.getSelectedRow();
        if (filaVisible < 0) {
            Notificador.advertencia(this, "Seleccione una venta para anular");
            return;
        }
        // Convertir índice de vista a índice del modelo (necesario por sorter/filter)
        int filaModelo = tabla.convertRowIndexToModel(filaVisible);

        String nroStr   = modelo.getValueAt(filaModelo, 0).toString().trim();
        String fechaStr = modelo.getValueAt(filaModelo, 1).toString().trim();
        String prodStr  = modelo.getValueAt(filaModelo, 4).toString().trim();
        String cantStr  = modelo.getValueAt(filaModelo, 5).toString().trim();
        String totalStr = modelo.getValueAt(filaModelo, 6).toString().trim();
        String pagoStr  = modelo.getValueAt(filaModelo, 7).toString().trim();

        // Ya anulada → no se puede anular dos veces
        if (pagoStr.startsWith(Constantes.MARCA_ANULADA)) {
            Notificador.advertencia(this, "Esta venta ya está anulada");
            return;
        }

        // Solo ventas del día actual
        String hoy = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        if (!fechaStr.startsWith(hoy)) {
            Notificador.advertencia(this, "Solo se pueden anular ventas del día actual");
            return;
        }

        // Buscar el producto en el catálogo (para devolverle stock)
        Producto producto = null;
        for (Producto p : Tienda.productos) {
            if (p.getNombre().equals(prodStr)) { producto = p; break; }
        }
        if (producto == null) {
            Notificador.error(this, "El producto '" + prodStr + "' ya no existe en el catálogo. " +
                    "No se puede anular automáticamente.");
            return;
        }

        // Confirmación
        if (!Notificador.confirmar(this,
                "¿Anular la venta " + nroStr + " de " + cantStr + " x " + prodStr + "?\n" +
                "Se devolverá el stock y se descontará S/. " + totalStr + " del acumulado del día.")) {
            return;
        }

        // Parsear cantidad y total
        int cantidad;
        double total;
        try {
            cantidad = Integer.parseInt(cantStr);
            total = Double.parseDouble(totalStr);
        } catch (NumberFormatException e) {
            Notificador.error(this, "Datos inválidos en la fila seleccionada");
            return;
        }
        int nroVenta;
        try {
            nroVenta = Integer.parseInt(nroStr);
        } catch (NumberFormatException e) {
            Notificador.error(this, "Número de venta inválido");
            return;
        }

        // Aplicar efectos
        producto.setStock(producto.getStock() + cantidad);
        Tienda.importeAcumulado -= total;
        if (Tienda.importeAcumulado < 0) Tienda.importeAcumulado = 0; // protección
        Tienda.ventasHoy = Math.max(0, Tienda.ventasHoy - 1);
        Tienda.guardarDatos();

        // Marcar en el archivo
        if (!HistorialVentas.anularVenta(nroVenta)) {
            Notificador.error(this, "No se pudo actualizar el archivo de historial");
            return;
        }

        cargar(); // recargar tabla
        Notificador.exito(this, "Venta " + nroStr + " anulada correctamente");
    }

    /**
     * Reconstruye y muestra la boleta de la venta seleccionada en un diálogo modal.
     *
     * Se reconstruye con los datos disponibles en el historial (nro, fecha, cliente,
     * dni, producto, cantidad, total, método de pago). Detalles del producto como
     * pulgadas/Hz/garantía se toman del catálogo actual SI el producto todavía existe.
     */
    private void verBoleta() {
        int filaVisible = tabla.getSelectedRow();
        if (filaVisible < 0) return;
        int filaModelo = tabla.convertRowIndexToModel(filaVisible);

        String nro      = modelo.getValueAt(filaModelo, 0).toString().trim();
        String fecha    = modelo.getValueAt(filaModelo, 1).toString().trim();
        String cliente  = modelo.getValueAt(filaModelo, 2).toString().trim();
        String dni      = modelo.getValueAt(filaModelo, 3).toString().trim();
        String prodStr  = modelo.getValueAt(filaModelo, 4).toString().trim();
        String cantStr  = modelo.getValueAt(filaModelo, 5).toString().trim();
        String totalStr = modelo.getValueAt(filaModelo, 6).toString().trim();
        String pago     = modelo.getValueAt(filaModelo, 7).toString().trim();
        boolean anulada = pago.startsWith(Constantes.MARCA_ANULADA);

        // Buscar el producto en el catálogo (puede no existir si fue eliminado)
        Producto producto = null;
        for (Producto p : Tienda.productos) {
            if (p.getNombre().equals(prodStr)) { producto = p; break; }
        }

        StringBuilder b = new StringBuilder();
        b.append("  ============================================\n");
        b.append("            BOLETA DE VENTA\n");
        b.append("            ").append(Constantes.APP_NOMBRE).append(" - Grupo 12\n");
        b.append("  ============================================\n");
        if (anulada) {
            b.append("  *** VENTA ANULADA ***\n");
            b.append("  --------------------------------------------\n");
        }
        b.append("  Nro. Venta    : ").append(nro).append("\n");
        b.append("  Fecha         : ").append(fecha).append("\n");
        b.append("  --------------------------------------------\n");
        b.append("  CLIENTE\n");
        b.append("  Nombre        : ").append(cliente).append("\n");
        b.append("  DNI/RUC       : ").append(dni).append("\n");
        b.append("  Método pago   : ").append(anulada ? pago.substring(Constantes.MARCA_ANULADA.length()) : pago).append("\n");
        b.append("  --------------------------------------------\n");
        b.append("  DETALLE\n");
        b.append("  Modelo        : ").append(prodStr).append("\n");
        b.append("  Cantidad      : ").append(cantStr).append("\n");
        if (producto != null) {
            b.append("  Especif.      : ").append(producto.getPulgadas()).append("\" ")
                    .append(producto.getTipoPanel()).append(" | ")
                    .append(producto.getHercios()).append("Hz | ")
                    .append(producto.getResolucion()).append("\n");
            b.append("  Garantía      : ").append(producto.getGarantiaMeses()).append(" meses\n");
        } else {
            b.append("  (producto ya no está en el catálogo)\n");
        }
        b.append("  --------------------------------------------\n");
        b.append("  TOTAL PAGADO  : S/. ").append(totalStr).append("\n");
        b.append("  ============================================\n");

        // Construir el diálogo modal
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Boleta " + nro, true);
        TemaOscuro.aplicarTemaDialogo(dlg, "Boleta " + nro, 460, 460);

        JTextArea area = new JTextArea(b.toString());
        area.setEditable(false);
        area.setFont(TemaOscuro.FONT_MONO);
        area.setBackground(TemaOscuro.MANTLE);
        area.setForeground(anulada ? TemaOscuro.RED : TemaOscuro.SUBTEXT0);
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        dlg.add(TemaOscuro.crearScrollPane(area), BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pie.setBackground(TemaOscuro.BASE);
        JButton btnCerrar = TemaOscuro.crearBoton("Cerrar", TemaOscuro.ACCENT);
        btnCerrar.addActionListener(e -> dlg.dispose());
        pie.add(btnCerrar);
        dlg.add(pie, BorderLayout.SOUTH);

        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    @Override
    public void refrescar() { cargar(); }
}
