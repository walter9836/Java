import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * PANEL MODIFICAR PRODUCTO
 * ------------------------
 * Permite editar todos los atributos de un producto existente.
 * Similar a PanelConsultar pero con campos editables y botón Guardar.
 */
public class PanelModificar extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cboModelo;
    private JTextField txtPrecio, txtPulgadas, txtHercios, txtResolucion, txtStock,
                       txtTipoPanel, txtTiempoRespuesta, txtConectividad, txtGarantia;

    public PanelModificar() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        JPanel header = TemaOscuro.crearHeaderPagina("Modificar Producto", null);
        JPanel sel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sel.setOpaque(false);
        sel.add(TemaOscuro.crearLabel("Modelo:"));
        cboModelo = new JComboBox<>(Tienda.getNombresProductos());
        cboModelo.setFont(TemaOscuro.FONT_BASE);
        cboModelo.setPreferredSize(new Dimension(210, 30));
        cboModelo.addActionListener(e -> cargar());
        sel.add(cboModelo);
        header.add(sel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel tarjeta = TemaOscuro.crearTarjeta("EDITAR PRODUCTO");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 12, 5, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtPrecio          = TemaOscuro.crearFilaFormulario(form, gbc, 0, "Precio (S/.)", true);
        txtPulgadas        = TemaOscuro.crearFilaFormulario(form, gbc, 1, "Pulgadas", true);
        txtHercios         = TemaOscuro.crearFilaFormulario(form, gbc, 2, "Frecuencia (Hz)", true);
        txtResolucion      = TemaOscuro.crearFilaFormulario(form, gbc, 3, "Resolución", true);
        txtStock           = TemaOscuro.crearFilaFormulario(form, gbc, 4, "Stock", true);
        txtTipoPanel       = TemaOscuro.crearFilaFormulario(form, gbc, 5, "Tipo Panel", true);
        txtTiempoRespuesta = TemaOscuro.crearFilaFormulario(form, gbc, 6, "T. Respuesta (ms)", true);
        txtConectividad    = TemaOscuro.crearFilaFormulario(form, gbc, 7, "Conectividad", true);
        txtGarantia        = TemaOscuro.crearFilaFormulario(form, gbc, 8, "Garantía (meses)", true);

        // Filtros: bloquean letras al escribir en campos numéricos
        FiltroNumerico.aplicarDecimal(txtPrecio);
        FiltroNumerico.aplicarDecimal(txtPulgadas);
        FiltroNumerico.aplicarEntero(txtHercios);
        FiltroNumerico.aplicarEntero(txtStock);
        FiltroNumerico.aplicarEntero(txtTiempoRespuesta);
        FiltroNumerico.aplicarEntero(txtGarantia);

        tarjeta.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btns.setOpaque(false);
        JButton btnGuardar = TemaOscuro.crearBoton("Guardar", TemaOscuro.GREEN);
        btnGuardar.addActionListener(e -> guardar());
        btns.add(btnGuardar);
        tarjeta.add(btns, BorderLayout.SOUTH);

        add(tarjeta, BorderLayout.CENTER);
        cargar();
    }

    /** Copia los valores del producto seleccionado a los campos del formulario. */
    private void cargar() {
        if (Tienda.productos.isEmpty()) return;
        Producto p = Tienda.productos.get(cboModelo.getSelectedIndex());
        txtPrecio.setText("" + p.getPrecio());
        txtPulgadas.setText("" + p.getPulgadas());
        txtHercios.setText("" + p.getHercios());
        txtResolucion.setText(p.getResolucion());
        txtStock.setText("" + p.getStock());
        txtTipoPanel.setText(p.getTipoPanel());
        txtTiempoRespuesta.setText("" + p.getTiempoRespuesta());
        txtConectividad.setText(p.getConectividad());
        txtGarantia.setText("" + p.getGarantiaMeses());
    }

    /** Valida y aplica los cambios al producto seleccionado. */
    private void guardar() {
        String err = Validador.validarProducto(txtPrecio.getText(), txtPulgadas.getText(),
                txtHercios.getText(), txtStock.getText(), txtTiempoRespuesta.getText(), txtGarantia.getText());
        if (err != null) { Notificador.advertencia(this, err); return; }
        if (Validador.esTextoVacio(txtResolucion.getText()))   { Notificador.campoVacio(this, "Resolución"); return; }
        if (Validador.esTextoVacio(txtTipoPanel.getText()))    { Notificador.campoVacio(this, "Tipo Panel"); return; }
        if (Validador.esTextoVacio(txtConectividad.getText())) { Notificador.campoVacio(this, "Conectividad"); return; }

        try {
            Producto p = Tienda.productos.get(cboModelo.getSelectedIndex());
            p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            p.setPulgadas(Double.parseDouble(txtPulgadas.getText().trim()));
            p.setHercios(Integer.parseInt(txtHercios.getText().trim()));
            p.setResolucion(txtResolucion.getText().trim());
            p.setStock(Integer.parseInt(txtStock.getText().trim()));
            p.setTipoPanel(txtTipoPanel.getText().trim());
            p.setTiempoRespuesta(Integer.parseInt(txtTiempoRespuesta.getText().trim()));
            p.setConectividad(txtConectividad.getText().trim());
            p.setGarantiaMeses(Integer.parseInt(txtGarantia.getText().trim()));
            Tienda.guardarDatos();
            Notificador.exito(this, Constantes.MSG_GUARDADO_OK);
        } catch (NumberFormatException e) {
            Notificador.error(this, Constantes.ERR_NUMERICO_INVALIDO);
        }
    }

    @Override
    public void refrescar() {
        cboModelo.setModel(new DefaultComboBoxModel<>(Tienda.getNombresProductos()));
        if (!Tienda.productos.isEmpty()) cargar();
    }
}
