import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PANEL VENDER (PUNTO DE VENTA)
 * -----------------------------
 * Panel MÁS IMPORTANTE del sistema. Aquí se realiza toda venta.
 *
 * Implementa Refrescable para actualizar el combo de productos y la cuota
 * cuando se vuelve a esta vista (los productos pueden haber cambiado).
 *
 * Layout:
 *   IZQUIERDA: datos cliente + producto/cantidad + cuota diaria
 *   DERECHA:   boleta de venta + resumen con importe, desc, subtotal, IGV, total
 */
public class PanelVender extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    // Referencia a la ventana principal (por si se necesita navegar)
    private Tienda tienda;

    // Componentes del formulario
    private JComboBox<String> cboModelo, cboPago;
    private JTextField txtPrecio, txtCantidad, txtCliente, txtDni;
    private JTextArea txtBoleta;
    private JLabel lblDetalle, lblImporte, lblDescuento,
                   lblSubtotal, lblIgv, lblTotal, lblObsequio;
    private JLabel lblVentaNum, lblAcumulado;
    private JProgressBar barraAvance;

    public PanelVender(Tienda tienda) {
        this.tienda = tienda;
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        add(TemaOscuro.crearHeaderPagina("Punto de Venta", "Registrar nueva venta"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(14, 0));
        cuerpo.setOpaque(false);

        // ============ IZQUIERDA ============
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setPreferredSize(new Dimension(380, 0));

        // --- Tarjeta: Datos del Cliente ---
        JPanel tarjCliente = TemaOscuro.crearTarjeta("DATOS DEL CLIENTE");
        JPanel camposCliente = new JPanel(new GridBagLayout());
        camposCliente.setOpaque(false);
        GridBagConstraints gc = gbc();

        gc.gridy = 0;
        txtCliente = addCampo(camposCliente, gc, "Nombre");
        gc.gridy = 1;
        txtDni = addCampo(camposCliente, gc, "DNI / RUC");
        FiltroNumerico.aplicarEntero(txtDni); // DNI/RUC solo numéricos
        gc.gridy = 2;
        gc.gridx = 0;
        camposCliente.add(TemaOscuro.crearLabelPequeno("Método de pago"), gc);
        cboPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Yape / Plin", "Transferencia"});
        cboPago.setFont(TemaOscuro.FONT_BASE);
        cboPago.setPreferredSize(new Dimension(190, 30));
        gc.gridx = 1;
        camposCliente.add(cboPago, gc);

        tarjCliente.add(camposCliente, BorderLayout.CENTER);
        izq.add(tarjCliente);
        izq.add(Box.createVerticalStrut(10));

        // --- Tarjeta: Producto ---
        JPanel tarjProd = TemaOscuro.crearTarjeta("PRODUCTO");
        JPanel camposProd = new JPanel(new GridBagLayout());
        camposProd.setOpaque(false);
        GridBagConstraints gp = gbc();

        gp.gridy = 0;
        gp.gridx = 0;
        camposProd.add(TemaOscuro.crearLabelPequeno("Modelo"), gp);
        cboModelo = new JComboBox<>(Tienda.getNombresProductos());
        cboModelo.setFont(TemaOscuro.FONT_BASE);
        cboModelo.setPreferredSize(new Dimension(190, 30));
        cboModelo.addActionListener(e -> mostrarPrecio());
        gp.gridx = 1;
        camposProd.add(cboModelo, gp);

        gp.gridy = 1;
        gp.gridx = 0;
        camposProd.add(TemaOscuro.crearLabelPequeno("Precio"), gp);
        txtPrecio = TemaOscuro.crearCampoTexto(false);
        gp.gridx = 1;
        camposProd.add(txtPrecio, gp);

        gp.gridy = 2;
        gp.gridx = 0;
        camposProd.add(TemaOscuro.crearLabelPequeno("Cantidad"), gp);
        txtCantidad = TemaOscuro.crearCampoTexto(true);
        FiltroNumerico.aplicarEntero(txtCantidad); // solo dígitos
        gp.gridx = 1;
        camposProd.add(txtCantidad, gp);

        gp.gridy = 3;
        gp.gridx = 0;
        gp.gridwidth = 2;
        lblDetalle = TemaOscuro.crearLabelPequeno("---");
        camposProd.add(lblDetalle, gp);

        tarjProd.add(camposProd, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        btns.setOpaque(false);
        JButton btnVender = TemaOscuro.crearBoton("Vender", TemaOscuro.ACCENT);
        btnVender.addActionListener(e -> vender());
        JButton btnLimpiar = TemaOscuro.crearBoton("Limpiar", TemaOscuro.YELLOW);
        btnLimpiar.addActionListener(e -> limpiar());
        btns.add(btnVender);
        btns.add(btnLimpiar);
        tarjProd.add(btns, BorderLayout.SOUTH);

        izq.add(tarjProd);
        izq.add(Box.createVerticalStrut(10));

        // --- Tarjeta: Cuota diaria ---
        JPanel tarjCuota = TemaOscuro.crearTarjeta("CUOTA DIARIA");
        JPanel cuotaC = new JPanel(new GridLayout(1, 2, 8, 0));
        cuotaC.setOpaque(false);
        lblVentaNum = TemaOscuro.crearValorLabel("Ventas hoy: " + Tienda.ventasHoy);
        lblAcumulado = TemaOscuro.crearValorLabel("S/. " + String.format("%.2f", Tienda.importeAcumulado));
        lblAcumulado.setForeground(TemaOscuro.GREEN);
        cuotaC.add(lblVentaNum);
        cuotaC.add(lblAcumulado);

        JPanel cuotaW = new JPanel();
        cuotaW.setOpaque(false);
        cuotaW.setLayout(new BoxLayout(cuotaW, BoxLayout.Y_AXIS));
        cuotaW.add(cuotaC);
        cuotaW.add(Box.createVerticalStrut(6));
        barraAvance = TemaOscuro.crearProgressBar();
        barraAvance.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        actualizarAvance();
        cuotaW.add(barraAvance);
        tarjCuota.add(cuotaW, BorderLayout.CENTER);
        izq.add(tarjCuota);

        cuerpo.add(izq, BorderLayout.WEST);

        // ============ DERECHA ============
        JPanel der = new JPanel(new BorderLayout(0, 10));
        der.setOpaque(false);

        // --- Tarjeta: Boleta ---
        JPanel tarjBoleta = TemaOscuro.crearTarjeta("BOLETA DE VENTA");
        txtBoleta = new JTextArea();
        txtBoleta.setEditable(false);
        txtBoleta.setFont(TemaOscuro.FONT_MONO);
        txtBoleta.setBackground(TemaOscuro.MANTLE);
        txtBoleta.setForeground(TemaOscuro.SUBTEXT0);
        txtBoleta.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtBoleta.setText("  Realice una venta para ver la boleta aquí...");
        tarjBoleta.add(TemaOscuro.crearScrollPane(txtBoleta), BorderLayout.CENTER);
        der.add(tarjBoleta, BorderLayout.CENTER);

        // --- Tarjeta: Resumen ---
        JPanel tarjRes = TemaOscuro.crearTarjeta("RESUMEN");
        JPanel resGrid = new JPanel(new GridLayout(6, 2, 10, 3));
        resGrid.setOpaque(false);
        resGrid.add(TemaOscuro.crearLabelPequeno("Importe"));
        lblImporte = TemaOscuro.crearValorLabel("---");
        resGrid.add(lblImporte);
        resGrid.add(TemaOscuro.crearLabelPequeno("Descuento"));
        lblDescuento = TemaOscuro.crearValorLabel("---");
        resGrid.add(lblDescuento);
        resGrid.add(TemaOscuro.crearLabelPequeno("Subtotal"));
        lblSubtotal = TemaOscuro.crearValorLabel("---");
        resGrid.add(lblSubtotal);
        resGrid.add(TemaOscuro.crearLabelPequeno("IGV (18%)"));
        lblIgv = TemaOscuro.crearValorLabel("---");
        resGrid.add(lblIgv);
        resGrid.add(TemaOscuro.crearLabelPequeno("TOTAL"));
        lblTotal = TemaOscuro.crearValorLabel("---");
        lblTotal.setForeground(TemaOscuro.GREEN);
        lblTotal.setFont(TemaOscuro.FONT_TITLE);
        resGrid.add(lblTotal);
        resGrid.add(TemaOscuro.crearLabelPequeno("Obsequio"));
        lblObsequio = TemaOscuro.crearValorLabel("---");
        lblObsequio.setForeground(TemaOscuro.YELLOW);
        resGrid.add(lblObsequio);

        tarjRes.add(resGrid, BorderLayout.CENTER);
        tarjRes.setPreferredSize(new Dimension(0, 175));
        der.add(tarjRes, BorderLayout.SOUTH);

        cuerpo.add(der, BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);

        mostrarPrecio();
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 6, 3, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private JTextField addCampo(JPanel panel, GridBagConstraints g, String label) {
        g.gridx = 0;
        panel.add(TemaOscuro.crearLabelPequeno(label), g);
        JTextField txt = TemaOscuro.crearCampoTexto(true);
        g.gridx = 1;
        panel.add(txt, g);
        return txt;
    }

    private void mostrarPrecio() {
        if (Tienda.productos.isEmpty()) return;
        Producto producto = Tienda.productos.get(cboModelo.getSelectedIndex());
        txtPrecio.setText("S/. " + String.format("%.2f", producto.getPrecio()));
        lblDetalle.setText(producto.getPulgadas() + "\" " + producto.getTipoPanel() + " | " +
                producto.getHercios() + "Hz | " + producto.getResolucion() + " | Stock: " + producto.getStock());
        lblDetalle.setForeground(producto.getStock() > 0 ? TemaOscuro.GREEN : TemaOscuro.RED);
    }

    private void limpiar() {
        txtCantidad.setText("");
        txtCliente.setText("");
        txtDni.setText("");
        cboPago.setSelectedIndex(0);
        txtBoleta.setText("  Realice una venta para ver la boleta aquí...");
        lblImporte.setText("---");
        lblDescuento.setText("---");
        lblSubtotal.setText("---");
        lblIgv.setText("---");
        lblTotal.setText("---");
        lblObsequio.setText("---");
    }

    private void actualizarAvance() {
        double porc = (Tienda.importeAcumulado / Tienda.cuotaDiaria) * 100;
        barraAvance.setValue(Math.min((int) porc, 100));
        barraAvance.setString(String.format("%.1f%%  (S/. %.0f / %.0f)", porc, Tienda.importeAcumulado, Tienda.cuotaDiaria));
        barraAvance.setForeground(porc >= 100 ? TemaOscuro.GREEN : porc >= 60 ? TemaOscuro.YELLOW : TemaOscuro.RED);
        lblVentaNum.setText("Ventas hoy: " + Tienda.ventasHoy);
        lblAcumulado.setText("S/. " + String.format("%.2f", Tienda.importeAcumulado));
    }

    /**
     * MÉTODO PRINCIPAL: procesa una venta completa.
     *   1. Validar todos los campos con la clase Validador
     *   2. Calcular importe, descuento, subtotal, IGV, total
     *   3. Aplicar efectos: descontar stock, aumentar contador, guardar datos
     *   4. Actualizar la UI: labels del resumen, barra de avance, boleta
     */
    private void vender() {
        // ========== 1. VALIDACIONES ==========
        String cliente = txtCliente.getText().trim();
        String dni = txtDni.getText().trim();

        if (Validador.esTextoVacio(cliente)) {
            Notificador.campoVacio(this, "Nombre");
            txtCliente.requestFocus();
            return;
        }
        if (Validador.esTextoVacio(dni)) {
            Notificador.campoVacio(this, "DNI / RUC");
            txtDni.requestFocus();
            return;
        }
        if (!Validador.esDniOrocValido(dni)) {
            Notificador.advertencia(this, Constantes.ERR_DNI_RUC_INVALIDO);
            txtDni.requestFocus();
            return;
        }
        if (Validador.esTextoVacio(txtCantidad.getText())) {
            Notificador.campoVacio(this, "Cantidad");
            txtCantidad.requestFocus();
            return;
        }
        if (!Validador.esEnteroPositivo(txtCantidad.getText())) {
            Notificador.advertencia(this, Constantes.ERR_CANTIDAD_POSITIVA);
            txtCantidad.requestFocus();
            return;
        }

        Producto producto = Tienda.productos.get(cboModelo.getSelectedIndex());
        int cantidad = Integer.parseInt(txtCantidad.getText().trim());
        String metodo = (String) cboPago.getSelectedItem();

        if (!producto.tieneStock(cantidad)) {
            Notificador.advertencia(this, Constantes.ERR_STOCK_INSUFICIENTE + producto.getStock());
            return;
        }

        // ========== 2. CÁLCULOS ==========
        double precio = producto.getPrecio();
        double importe = precio * cantidad;
        double porcentajeDescuento = Tienda.obtenerDescuento(cantidad);
        double montoDescuento = importe * (porcentajeDescuento / 100);
        double subtotal = importe - montoDescuento;
        double igv = subtotal * Constantes.IGV;
        double total = subtotal + igv;
        String obsequio = Tienda.obtenerObsequio(cantidad);

        // ========== 3. EFECTOS DE NEGOCIO ==========
        Tienda.resetDiarioSiCorresponde(); // si cambió el día, reinicia contadores diarios
        producto.descontarStock(cantidad);
        Tienda.numVentas++;     // contador histórico (boleta)
        Tienda.ventasHoy++;     // contador del día
        Tienda.importeAcumulado += total;
        Tienda.guardarDatos();
        HistorialVentas.registrarVenta(Tienda.numVentas, cliente, dni, producto.getNombre(), cantidad, total, metodo);

        // ========== 4. ACTUALIZAR UI ==========
        lblImporte.setText("S/. " + String.format("%.2f", importe));
        lblDescuento.setText("S/. " + String.format("%.2f", montoDescuento) + " (" + porcentajeDescuento + "%)");
        lblSubtotal.setText("S/. " + String.format("%.2f", subtotal));
        lblIgv.setText("S/. " + String.format("%.2f", igv));
        lblTotal.setText("S/. " + String.format("%.2f", total));
        lblObsequio.setText(obsequio);
        actualizarAvance();
        generarBoleta(cliente, dni, metodo, producto, cantidad, precio, importe,
                porcentajeDescuento, montoDescuento, subtotal, igv, total, obsequio);
        mostrarPrecio();
    }

    private void generarBoleta(String cliente, String dni, String metodo, Producto producto,
                                int cantidad, double precio, double importe,
                                double porcentajeDescuento, double montoDescuento,
                                double subtotal, double igv, double total, String obsequio) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        txtBoleta.setText("");
        txtBoleta.append("  ============================================\n");
        txtBoleta.append("            BOLETA DE VENTA\n");
        txtBoleta.append("            " + Constantes.APP_NOMBRE + " - Grupo 12\n");
        txtBoleta.append("  ============================================\n");
        txtBoleta.append("  Nro. Venta    : " + String.format("%06d", Tienda.numVentas) + "\n");
        txtBoleta.append("  Fecha         : " + fecha + "\n");
        txtBoleta.append("  --------------------------------------------\n");
        txtBoleta.append("  CLIENTE\n");
        txtBoleta.append("  Nombre        : " + cliente + "\n");
        txtBoleta.append("  DNI/RUC       : " + dni + "\n");
        txtBoleta.append("  Método pago   : " + metodo + "\n");
        txtBoleta.append("  --------------------------------------------\n");
        txtBoleta.append("  DETALLE\n");
        txtBoleta.append("  Modelo        : " + producto.getNombre() + "\n");
        txtBoleta.append("  Precio unit.  : S/. " + String.format("%.2f", precio) + "\n");
        txtBoleta.append("  Cantidad      : " + cantidad + "\n");
        txtBoleta.append("  --------------------------------------------\n");
        txtBoleta.append("  Importe       : S/. " + String.format("%.2f", importe) + "\n");
        txtBoleta.append("  Descuento " + String.format("%.0f", porcentajeDescuento) + "%  : S/. " + String.format("%.2f", montoDescuento) + "\n");
        txtBoleta.append("  Subtotal      : S/. " + String.format("%.2f", subtotal) + "\n");
        txtBoleta.append("  IGV (18%)     : S/. " + String.format("%.2f", igv) + "\n");
        txtBoleta.append("  ============================================\n");
        txtBoleta.append("  TOTAL A PAGAR : S/. " + String.format("%.2f", total) + "\n");
        txtBoleta.append("  ============================================\n");
        txtBoleta.append("  Obsequio      : " + obsequio + "\n");
        txtBoleta.append("  Garantía      : " + producto.getGarantiaMeses() + " meses\n");
        txtBoleta.append("  Stock restante: " + producto.getStock() + " unidades\n");
        txtBoleta.append("  ============================================\n");
    }

    @Override
    public void refrescar() {
        cboModelo.setModel(new DefaultComboBoxModel<>(Tienda.getNombresProductos()));
        if (!Tienda.productos.isEmpty()) mostrarPrecio();
        actualizarAvance();
    }
}
