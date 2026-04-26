import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * PANEL CONFIGURAR CUOTA DIARIA
 * -----------------------------
 * Permite editar la meta diaria de ventas (Tienda.cuotaDiaria) y muestra
 * el avance actual del día contra esa meta.
 *
 * Estructura:
 *   - Tarjeta IZQUIERDA: editar el monto de la cuota.
 *   - Tarjeta DERECHA: avance del día (meta, acumulado, faltante, %, barra).
 *
 * Implementa Refrescable para actualizar el avance al volver al panel
 * (los valores cambian con cada venta y al cambiar de día).
 */
public class PanelConfCuota extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    private JTextField txtCuota;
    // Componentes del panel de avance
    private JLabel lblMeta, lblAcumulado, lblFaltante, lblVentasHoy;
    private JProgressBar barra;

    public PanelConfCuota() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        add(TemaOscuro.crearHeaderPagina("Configurar Cuota Diaria", "Meta de ventas diaria"), BorderLayout.NORTH);

        // ===== Cuerpo: dos tarjetas lado a lado =====
        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 14, 0));
        cuerpo.setOpaque(false);

        cuerpo.add(crearTarjetaConfig());
        cuerpo.add(crearTarjetaAvance());

        add(cuerpo, BorderLayout.CENTER);
        actualizarAvance();
    }

    /** Tarjeta IZQUIERDA: campo para editar la cuota + botón guardar. */
    private JPanel crearTarjetaConfig() {
        JPanel tarjeta = TemaOscuro.crearTarjeta("CUOTA DIARIA DE VENTAS");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCuota = TemaOscuro.crearFilaFormulario(form, gbc, 0, "Monto esperado (S/.)", true);
        FiltroNumerico.aplicarDecimal(txtCuota);
        txtCuota.setText(formatear(Tienda.cuotaDiaria));

        // Nota explicativa debajo
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("<html><i>El acumulado y el contador de ventas se reinician<br>" +
                "automáticamente al cambiar de día.</i></html>");
        lblNota.setFont(TemaOscuro.FONT_SM);
        lblNota.setForeground(TemaOscuro.OVERLAY0);
        form.add(lblNota, gbc);

        tarjeta.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btns.setOpaque(false);
        JButton btnGuardar = TemaOscuro.crearBoton("Guardar", TemaOscuro.GREEN);
        btnGuardar.addActionListener(e -> guardar());
        btns.add(btnGuardar);
        tarjeta.add(btns, BorderLayout.SOUTH);
        return tarjeta;
    }

    /** Tarjeta DERECHA: avance del día contra la meta. */
    private JPanel crearTarjetaAvance() {
        JPanel tarjeta = TemaOscuro.crearTarjeta("AVANCE DEL DIA");
        JPanel cont = new JPanel();
        cont.setOpaque(false);
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.add(Box.createVerticalStrut(8));

        // Barra de progreso
        barra = TemaOscuro.crearProgressBar();
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        cont.add(barra);
        cont.add(Box.createVerticalStrut(14));

        // Grilla con los 4 datos
        JPanel det = new JPanel(new GridLayout(4, 2, 8, 6));
        det.setOpaque(false);

        det.add(TemaOscuro.crearLabelPequeno("Meta diaria"));
        lblMeta = TemaOscuro.crearValorLabel("---");
        det.add(lblMeta);

        det.add(TemaOscuro.crearLabelPequeno("Acumulado hoy"));
        lblAcumulado = TemaOscuro.crearValorLabel("---");
        lblAcumulado.setForeground(TemaOscuro.GREEN);
        det.add(lblAcumulado);

        det.add(TemaOscuro.crearLabelPequeno("Faltante"));
        lblFaltante = TemaOscuro.crearValorLabel("---");
        det.add(lblFaltante);

        det.add(TemaOscuro.crearLabelPequeno("Ventas hoy"));
        lblVentasHoy = TemaOscuro.crearValorLabel("---");
        det.add(lblVentasHoy);

        cont.add(det);
        tarjeta.add(cont, BorderLayout.CENTER);
        return tarjeta;
    }

    /** Recalcula y refresca todos los labels y la barra del panel de avance. */
    private void actualizarAvance() {
        double porc = Tienda.cuotaDiaria > 0
                ? (Tienda.importeAcumulado / Tienda.cuotaDiaria) * 100 : 0;
        // Math.max(0, ...) evita mostrar "faltante" negativo si se superó la meta
        double faltante = Math.max(0, Tienda.cuotaDiaria - Tienda.importeAcumulado);

        barra.setValue(Math.min((int) porc, 100));
        barra.setString(String.format("%.1f%%", porc));
        // Color según avance: verde ≥100%, amarillo ≥60%, rojo en otro caso
        barra.setForeground(porc >= 100 ? TemaOscuro.GREEN
                : porc >= 60 ? TemaOscuro.YELLOW
                : TemaOscuro.RED);

        lblMeta.setText("S/. " + String.format("%.2f", Tienda.cuotaDiaria));
        lblAcumulado.setText("S/. " + String.format("%.2f", Tienda.importeAcumulado));
        lblFaltante.setText("S/. " + String.format("%.2f", faltante));
        lblFaltante.setForeground(faltante > 0 ? TemaOscuro.YELLOW : TemaOscuro.GREEN);
        lblVentasHoy.setText("" + Tienda.ventasHoy);
    }

    private void guardar() {
        if (Validador.esTextoVacio(txtCuota.getText())) {
            Notificador.campoVacio(this, "Monto esperado");
            return;
        }
        try {
            double valor = Double.parseDouble(txtCuota.getText().trim());
            if (valor <= 0) {
                Notificador.advertencia(this, "El monto de la cuota debe ser mayor a cero");
                return;
            }
            Tienda.cuotaDiaria = valor;
            Tienda.guardarDatos();
            actualizarAvance(); // refresca el % con la meta nueva
            Notificador.exito(this, Constantes.MSG_CUOTA_OK);
        } catch (NumberFormatException e) {
            Notificador.error(this, Constantes.ERR_NUMERICO_INVALIDO);
        }
    }

    @Override
    public void refrescar() {
        txtCuota.setText(formatear(Tienda.cuotaDiaria));
        actualizarAvance();
    }

    /** Quita el .0 sobrante si el valor es entero (30000.0 → "30000"). */
    private static String formatear(double valor) {
        return valor == (long) valor ? String.valueOf((long) valor) : String.valueOf(valor);
    }
}
