import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * PANEL CONFIGURAR DESCUENTOS
 * ---------------------------
 * Permite editar los 4 porcentajes de descuento del array Tienda.porcentajesDescuento.
 *
 * Rangos:
 *   txtRango1 → 1 a 5 unidades
 *   txtRango2 → 6 a 10 unidades
 *   txtRango3 → 11 a 15 unidades
 *   txtRango4 → más de 15 unidades
 */
public class PanelConfDesc extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    private JTextField txtRango1, txtRango2, txtRango3, txtRango4;

    public PanelConfDesc() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        add(TemaOscuro.crearHeaderPagina("Configurar Descuentos", "Porcentajes por rango de cantidad"), BorderLayout.NORTH);

        JPanel tarjeta = TemaOscuro.crearTarjeta("PORCENTAJES DE DESCUENTO");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtRango1 = crearFila(form, gbc, 0, "1 a 5 unidades", Tienda.porcentajesDescuento[0]);
        txtRango2 = crearFila(form, gbc, 1, "6 a 10 unidades", Tienda.porcentajesDescuento[1]);
        txtRango3 = crearFila(form, gbc, 2, "11 a 15 unidades", Tienda.porcentajesDescuento[2]);
        txtRango4 = crearFila(form, gbc, 3, "Más de 15 unidades", Tienda.porcentajesDescuento[3]);

        // Solo aceptan dígitos y un punto (porcentajes como 7.5)
        FiltroNumerico.aplicarDecimal(txtRango1);
        FiltroNumerico.aplicarDecimal(txtRango2);
        FiltroNumerico.aplicarDecimal(txtRango3);
        FiltroNumerico.aplicarDecimal(txtRango4);

        tarjeta.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btns.setOpaque(false);
        JButton btnGuardar = TemaOscuro.crearBoton("Guardar", TemaOscuro.GREEN);
        btnGuardar.addActionListener(e -> guardar());
        btns.add(btnGuardar);
        tarjeta.add(btns, BorderLayout.SOUTH);

        add(tarjeta, BorderLayout.CENTER);
    }

    /** Crea una fila: [Label] [TextField] [%] (símbolo "%" extra al final). */
    private JTextField crearFila(JPanel panel, GridBagConstraints gbc, int fila, String label, double valor) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.5;
        panel.add(TemaOscuro.crearLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        JTextField txt = TemaOscuro.crearCampoTexto(true);
        txt.setText(formatear(valor));
        txt.setPreferredSize(new Dimension(100, 32));
        panel.add(txt, gbc);
        gbc.gridx = 2; gbc.weightx = 0.1;
        panel.add(TemaOscuro.crearLabel("%"), gbc);
        return txt;
    }

    private void guardar() {
        try {
            Tienda.porcentajesDescuento[0] = Double.parseDouble(txtRango1.getText().trim());
            Tienda.porcentajesDescuento[1] = Double.parseDouble(txtRango2.getText().trim());
            Tienda.porcentajesDescuento[2] = Double.parseDouble(txtRango3.getText().trim());
            Tienda.porcentajesDescuento[3] = Double.parseDouble(txtRango4.getText().trim());
            Tienda.guardarDatos();
            Notificador.exito(this, Constantes.MSG_DESCUENTOS_OK);
        } catch (NumberFormatException e) {
            Notificador.error(this, Constantes.ERR_NUMERICO_INVALIDO);
        }
    }

    @Override
    public void refrescar() {
        txtRango1.setText(formatear(Tienda.porcentajesDescuento[0]));
        txtRango2.setText(formatear(Tienda.porcentajesDescuento[1]));
        txtRango3.setText(formatear(Tienda.porcentajesDescuento[2]));
        txtRango4.setText(formatear(Tienda.porcentajesDescuento[3]));
    }

    /** Formatea sin .0 al final si es entero (5.0 → "5"), con decimales si los tiene (7.5 → "7.5"). */
    private static String formatear(double valor) {
        return valor == (long) valor ? String.valueOf((long) valor) : String.valueOf(valor);
    }
}
