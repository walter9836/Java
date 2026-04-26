import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * PANEL GESTIONAR PRODUCTOS (AGREGAR / ELIMINAR)
 * ----------------------------------------------
 * Panel dividido en dos zonas lado a lado con GridLayout(1, 2):
 *   IZQUIERDA: formulario para AGREGAR un producto nuevo al catálogo
 *   DERECHA:   combo para ELIMINAR un producto existente
 *
 * Reglas de negocio:
 *   - Al agregar, se validan todos los campos
 *   - No se permite eliminar si queda 1 solo producto (el catálogo no puede estar vacío)
 *   - La eliminación pide confirmación porque es destructiva
 */
public class PanelGestionar extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    // Campos del formulario de AGREGAR (nombres completos: txt + atributo)
    private JTextField txtNombre, txtPrecio, txtPulgadas, txtHercios, txtResolucion,
                       txtStock, txtTipoPanel, txtTiempoRespuesta, txtConectividad, txtGarantia;
    private JComboBox<String> cboEliminar;

    public PanelGestionar() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        add(TemaOscuro.crearHeaderPagina("Gestionar Productos", "Agregar o eliminar productos del catálogo"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 14, 0));
        cuerpo.setOpaque(false);

        // ========== ZONA AGREGAR ==========
        JPanel tarjAdd = TemaOscuro.crearTarjeta("AGREGAR PRODUCTO");
        JPanel formAdd = new JPanel(new GridBagLayout());
        formAdd.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        txtNombre            = TemaOscuro.crearFilaFormulario(formAdd, g, 0, "Nombre / Modelo", true);
        txtPrecio            = TemaOscuro.crearFilaFormulario(formAdd, g, 1, "Precio (S/.)", true);
        txtPulgadas          = TemaOscuro.crearFilaFormulario(formAdd, g, 2, "Pulgadas", true);
        txtHercios           = TemaOscuro.crearFilaFormulario(formAdd, g, 3, "Frecuencia (Hz)", true);
        txtResolucion        = TemaOscuro.crearFilaFormulario(formAdd, g, 4, "Resolución", true);
        txtStock             = TemaOscuro.crearFilaFormulario(formAdd, g, 5, "Stock", true);
        txtTipoPanel         = TemaOscuro.crearFilaFormulario(formAdd, g, 6, "Tipo Panel", true);
        txtTiempoRespuesta   = TemaOscuro.crearFilaFormulario(formAdd, g, 7, "T. Respuesta (ms)", true);
        txtConectividad      = TemaOscuro.crearFilaFormulario(formAdd, g, 8, "Conectividad", true);
        txtGarantia          = TemaOscuro.crearFilaFormulario(formAdd, g, 9, "Garantía (meses)", true);

        // Filtros: bloquean letras al escribir en campos numéricos
        FiltroNumerico.aplicarDecimal(txtPrecio);
        FiltroNumerico.aplicarDecimal(txtPulgadas);
        FiltroNumerico.aplicarEntero(txtHercios);
        FiltroNumerico.aplicarEntero(txtStock);
        FiltroNumerico.aplicarEntero(txtTiempoRespuesta);
        FiltroNumerico.aplicarEntero(txtGarantia);

        tarjAdd.add(formAdd, BorderLayout.CENTER);

        JPanel btnsAdd = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btnsAdd.setOpaque(false);
        JButton btnAgregar = TemaOscuro.crearBoton("Agregar", TemaOscuro.GREEN);
        btnAgregar.addActionListener(e -> agregar());
        JButton btnLimpiar = TemaOscuro.crearBoton("Limpiar", TemaOscuro.YELLOW);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnsAdd.add(btnAgregar);
        btnsAdd.add(btnLimpiar);
        tarjAdd.add(btnsAdd, BorderLayout.SOUTH);

        cuerpo.add(tarjAdd);

        // ========== ZONA ELIMINAR ==========
        JPanel tarjDel = TemaOscuro.crearTarjeta("ELIMINAR PRODUCTO");
        JPanel formDel = new JPanel(new GridBagLayout());
        formDel.setOpaque(false);
        GridBagConstraints gd = new GridBagConstraints();
        gd.insets = new Insets(4, 8, 4, 8);
        gd.anchor = GridBagConstraints.WEST;
        gd.fill = GridBagConstraints.HORIZONTAL;

        gd.gridx = 0; gd.gridy = 0; gd.weightx = 0.4;
        formDel.add(TemaOscuro.crearLabelPequeno("Producto a eliminar"), gd);
        cboEliminar = new JComboBox<>(Tienda.getNombresProductos());
        cboEliminar.setFont(TemaOscuro.FONT_BASE);
        cboEliminar.setPreferredSize(new Dimension(200, 32));
        gd.gridx = 1; gd.weightx = 0.6;
        formDel.add(cboEliminar, gd);

        gd.gridx = 0; gd.gridy = 1; gd.gridwidth = 2;
        JLabel lblNota = new JLabel("<html><i>Atención: esta acción no se puede deshacer.<br>Verifique que no haya ventas pendientes.</i></html>");
        lblNota.setFont(TemaOscuro.FONT_SM);
        lblNota.setForeground(TemaOscuro.YELLOW);
        formDel.add(lblNota, gd);

        tarjDel.add(formDel, BorderLayout.CENTER);

        JPanel btnsDel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btnsDel.setOpaque(false);
        JButton btnEliminar = TemaOscuro.crearBoton("Eliminar", TemaOscuro.RED);
        btnEliminar.addActionListener(e -> eliminar());
        btnsDel.add(btnEliminar);
        tarjDel.add(btnsDel, BorderLayout.SOUTH);

        cuerpo.add(tarjDel);
        add(cuerpo, BorderLayout.CENTER);
    }

    private void agregar() {
        String nombre = txtNombre.getText().trim();
        if (Validador.esTextoVacio(nombre)) {
            Notificador.campoVacio(this, "Nombre / Modelo");
            txtNombre.requestFocus();
            return;
        }
        String err = Validador.validarProducto(txtPrecio.getText(), txtPulgadas.getText(),
                txtHercios.getText(), txtStock.getText(), txtTiempoRespuesta.getText(), txtGarantia.getText());
        if (err != null) { Notificador.advertencia(this, err); return; }
        if (Validador.esTextoVacio(txtResolucion.getText()))   { Notificador.campoVacio(this, "Resolución"); return; }
        if (Validador.esTextoVacio(txtTipoPanel.getText()))    { Notificador.campoVacio(this, "Tipo Panel"); return; }
        if (Validador.esTextoVacio(txtConectividad.getText())) { Notificador.campoVacio(this, "Conectividad"); return; }

        try {
            Producto nuevo = new Producto(
                nombre,
                Double.parseDouble(txtPrecio.getText().trim()),
                Double.parseDouble(txtPulgadas.getText().trim()),
                Integer.parseInt(txtHercios.getText().trim()),
                txtResolucion.getText().trim(),
                Integer.parseInt(txtStock.getText().trim()),
                txtTipoPanel.getText().trim(),
                Integer.parseInt(txtTiempoRespuesta.getText().trim()),
                txtConectividad.getText().trim(),
                Integer.parseInt(txtGarantia.getText().trim())
            );
            Tienda.productos.add(nuevo);
            Tienda.guardarDatos();
            cboEliminar.setModel(new DefaultComboBoxModel<>(Tienda.getNombresProductos()));
            limpiarFormulario();
            Notificador.exito(this, Constantes.MSG_PRODUCTO_AGREGADO);
        } catch (NumberFormatException e) {
            Notificador.error(this, Constantes.ERR_NUMERICO_INVALIDO);
        }
    }

    private void eliminar() {
        if (Tienda.productos.size() <= 1) {
            Notificador.advertencia(this, "Debe haber al menos un producto en el catálogo");
            return;
        }
        int idx = cboEliminar.getSelectedIndex();
        if (idx < 0) return;
        Producto p = Tienda.productos.get(idx);

        if (!Notificador.confirmar(this, "¿Eliminar el producto '" + p.getNombre() + "'?")) return;

        Tienda.productos.remove(idx);
        Tienda.guardarDatos();
        cboEliminar.setModel(new DefaultComboBoxModel<>(Tienda.getNombresProductos()));
        Notificador.exito(this, Constantes.MSG_PRODUCTO_ELIMINADO);
    }

    private void limpiarFormulario() {
        txtNombre.setText(""); txtPrecio.setText(""); txtPulgadas.setText("");
        txtHercios.setText(""); txtResolucion.setText(""); txtStock.setText("");
        txtTipoPanel.setText(""); txtTiempoRespuesta.setText("");
        txtConectividad.setText(""); txtGarantia.setText("");
    }

    @Override
    public void refrescar() {
        cboEliminar.setModel(new DefaultComboBoxModel<>(Tienda.getNombresProductos()));
    }
}
