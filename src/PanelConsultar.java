import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * PANEL CONSULTAR PRODUCTO
 * ------------------------
 * Muestra los detalles de un producto en modo SOLO LECTURA.
 * El usuario elige el modelo del combo y se muestran sus datos sin poder editarlos.
 */
public class PanelConsultar extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cboModelo;
    private JTextField txtPrecio, txtPulgadas, txtHercios, txtResolucion, txtStock,
                       txtTipoPanel, txtTiempoRespuesta, txtConectividad, txtGarantia;
    private JLabel lblEstado;

    public PanelConsultar() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        JPanel header = TemaOscuro.crearHeaderPagina("Consultar Producto", null);
        JPanel sel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sel.setOpaque(false);
        sel.add(TemaOscuro.crearLabel("Modelo:"));
        cboModelo = new JComboBox<>(Tienda.getNombresProductos());
        cboModelo.setFont(TemaOscuro.FONT_BASE);
        cboModelo.setPreferredSize(new Dimension(210, 30));
        cboModelo.addActionListener(e -> mostrar());
        sel.add(cboModelo);
        lblEstado = new JLabel();
        lblEstado.setFont(TemaOscuro.FONT_BOLD);
        sel.add(lblEstado);
        header.add(sel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel tarjeta = TemaOscuro.crearTarjeta("DETALLES DEL PRODUCTO");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 12, 5, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtPrecio          = TemaOscuro.crearFilaFormulario(form, gbc, 0, "Precio (S/.)", false);
        txtPulgadas        = TemaOscuro.crearFilaFormulario(form, gbc, 1, "Pulgadas", false);
        txtHercios         = TemaOscuro.crearFilaFormulario(form, gbc, 2, "Frecuencia (Hz)", false);
        txtResolucion      = TemaOscuro.crearFilaFormulario(form, gbc, 3, "Resolución", false);
        txtStock           = TemaOscuro.crearFilaFormulario(form, gbc, 4, "Stock", false);
        txtTipoPanel       = TemaOscuro.crearFilaFormulario(form, gbc, 5, "Tipo Panel", false);
        txtTiempoRespuesta = TemaOscuro.crearFilaFormulario(form, gbc, 6, "T. Respuesta (ms)", false);
        txtConectividad    = TemaOscuro.crearFilaFormulario(form, gbc, 7, "Conectividad", false);
        txtGarantia        = TemaOscuro.crearFilaFormulario(form, gbc, 8, "Garantía (meses)", false);

        tarjeta.add(form, BorderLayout.CENTER);
        add(tarjeta, BorderLayout.CENTER);
        mostrar();
    }

    /** Llena los campos con los datos del producto seleccionado. */
    private void mostrar() {
        if (Tienda.productos.isEmpty()) return;
        Producto p = Tienda.productos.get(cboModelo.getSelectedIndex());
        txtPrecio.setText(String.format("S/. %.2f", p.getPrecio()));
        txtPulgadas.setText(p.getPulgadas() + "\"");
        txtHercios.setText(p.getHercios() + " Hz");
        txtResolucion.setText(p.getResolucion());
        txtStock.setText(p.getStock() + " unidades");
        txtStock.setForeground(p.getStock() > 0 ? TemaOscuro.GREEN : TemaOscuro.RED);
        txtTipoPanel.setText(p.getTipoPanel());
        txtTiempoRespuesta.setText(p.getTiempoRespuesta() + " ms");
        txtConectividad.setText(p.getConectividad());
        txtGarantia.setText(p.getGarantiaMeses() + " meses");

        if (p.getStock() > Constantes.STOCK_MINIMO_ALERTA) {
            lblEstado.setText("EN STOCK");
            lblEstado.setForeground(TemaOscuro.GREEN);
        } else if (p.getStock() > 0) {
            lblEstado.setText("STOCK BAJO");
            lblEstado.setForeground(TemaOscuro.YELLOW);
        } else {
            lblEstado.setText("AGOTADO");
            lblEstado.setForeground(TemaOscuro.RED);
        }
    }

    @Override
    public void refrescar() {
        cboModelo.setModel(new DefaultComboBoxModel<>(Tienda.getNombresProductos()));
        if (!Tienda.productos.isEmpty()) mostrar();
    }
}
