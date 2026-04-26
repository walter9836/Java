import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * PANEL CONFIGURAR OBSEQUIOS
 * --------------------------
 * Permite editar los 3 obsequios del array Tienda.obsequios.
 *
 * Rangos:
 *   txtRango1 → 1 unidad       (ej: "Mouse Gamer")
 *   txtRango2 → 2 a 5 unidades (ej: "Teclado Mecánico")
 *   txtRango3 → más de 5       (ej: "Silla Gamer")
 */
public class PanelConfObs extends JPanel implements Refrescable {
    private static final long serialVersionUID = 1L;

    private JTextField txtRango1, txtRango2, txtRango3;

    public PanelConfObs() {
        setBackground(TemaOscuro.BASE);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 20, 28));

        add(TemaOscuro.crearHeaderPagina("Configurar Obsequios", "Regalos por rango de cantidad"), BorderLayout.NORTH);

        JPanel tarjeta = TemaOscuro.crearTarjeta("OBSEQUIOS POR CANTIDAD");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtRango1 = TemaOscuro.crearFilaFormulario(form, gbc, 0, "1 unidad", true);
        txtRango1.setText(Tienda.obsequios[0]);
        txtRango2 = TemaOscuro.crearFilaFormulario(form, gbc, 1, "2 a 5 unidades", true);
        txtRango2.setText(Tienda.obsequios[1]);
        txtRango3 = TemaOscuro.crearFilaFormulario(form, gbc, 2, "Más de 5 unidades", true);
        txtRango3.setText(Tienda.obsequios[2]);

        tarjeta.add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btns.setOpaque(false);
        JButton btnGuardar = TemaOscuro.crearBoton("Guardar", TemaOscuro.GREEN);
        btnGuardar.addActionListener(e -> guardar());
        btns.add(btnGuardar);
        tarjeta.add(btns, BorderLayout.SOUTH);

        add(tarjeta, BorderLayout.CENTER);
    }

    private void guardar() {
        if (Validador.esTextoVacio(txtRango1.getText())) { Notificador.campoVacio(this, "Obsequio 1 unidad"); return; }
        if (Validador.esTextoVacio(txtRango2.getText())) { Notificador.campoVacio(this, "Obsequio 2 a 5 unidades"); return; }
        if (Validador.esTextoVacio(txtRango3.getText())) { Notificador.campoVacio(this, "Obsequio más de 5 unidades"); return; }
        Tienda.obsequios[0] = txtRango1.getText().trim();
        Tienda.obsequios[1] = txtRango2.getText().trim();
        Tienda.obsequios[2] = txtRango3.getText().trim();
        Tienda.guardarDatos();
        Notificador.exito(this, Constantes.MSG_OBSEQUIOS_OK);
    }

    @Override
    public void refrescar() {
        txtRango1.setText(Tienda.obsequios[0]);
        txtRango2.setText(Tienda.obsequios[1]);
        txtRango3.setText(Tienda.obsequios[2]);
    }
}
