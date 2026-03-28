import javax.swing.*;
import java.awt.*;

public class ConfCuota extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField txtCuota;

	public ConfCuota() {
		setTitle("Configurar cuota diaria");
		setBounds(100, 100, 350, 150);
		getContentPane().setLayout(new GridLayout(2, 2, 10, 10));

		add(new JLabel("Cuota diaria esperada (S/.):"));
		txtCuota = new JTextField("" + Tienda.cuotaDiaria);
		add(txtCuota);

		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.addActionListener(e -> {
			try {
				Tienda.cuotaDiaria = Double.parseDouble(txtCuota.getText());
				Tienda.guardarDatos();
				dispose();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Ingrese un monto válido");
			}
		});
		add(btnAceptar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> dispose());
		add(btnCancelar);
	}
}
