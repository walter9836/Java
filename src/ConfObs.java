import javax.swing.*;
import java.awt.*;

public class ConfObs extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField t1, t2, t3;

	public ConfObs() {
		setTitle("Configurar obsequios");
		setBounds(100, 100, 450, 200);
		getContentPane().setLayout(null);

		t1 = crearFila(11, "1 unidad", Tienda.obs1);
		t2 = crearFila(41, "2 a 5 unidades", Tienda.obs2);
		t3 = crearFila(71, "Más de 5 unidades", Tienda.obs3);

		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.setBounds(320, 11, 100, 23);
		btnAceptar.addActionListener(e -> {
			Tienda.obs1 = t1.getText();
			Tienda.obs2 = t2.getText();
			Tienda.obs3 = t3.getText();
			Tienda.guardarDatos();
			dispose();
		});
		getContentPane().add(btnAceptar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(320, 41, 100, 23);
		btnCancelar.addActionListener(e -> dispose());
		getContentPane().add(btnCancelar);
	}

	private JTextField crearFila(int y, String label, String val) {
		JLabel lbl = new JLabel(label);
		lbl.setBounds(10, y + 4, 150, 14);
		getContentPane().add(lbl);
		JTextField txt = new JTextField(val);
		txt.setBounds(150, y, 150, 20);
		getContentPane().add(txt);
		return txt;
	}
}
