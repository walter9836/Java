import javax.swing.*;
import java.awt.*;

public class ConfDesc extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField t1, t2, t3, t4;

	public ConfDesc() {
		setTitle("Configurar porcentajes de descuento");
		setBounds(100, 100, 450, 230);
		getContentPane().setLayout(null);

		t1 = crearFila(11, "1 a 5 unidades", Tienda.por1);
		t2 = crearFila(41, "6 a 10 unidades", Tienda.por2);
		t3 = crearFila(71, "11 a 15 unidades", Tienda.por3);
		t4 = crearFila(101, "Más de 15 unidades", Tienda.por4);

		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.setBounds(320, 11, 100, 23);
		btnAceptar.addActionListener(e -> {
			Tienda.por1 = Double.parseDouble(t1.getText());
			Tienda.por2 = Double.parseDouble(t2.getText());
			Tienda.por3 = Double.parseDouble(t3.getText());
			Tienda.por4 = Double.parseDouble(t4.getText());
			Tienda.guardarDatos();
			dispose();
		});
		getContentPane().add(btnAceptar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(320, 41, 100, 23);
		btnCancelar.addActionListener(e -> dispose());
		getContentPane().add(btnCancelar);
	}

	private JTextField crearFila(int y, String label, double val) {
		JLabel lbl = new JLabel(label);
		lbl.setBounds(10, y + 4, 150, 14);
		getContentPane().add(lbl);
		JTextField txt = new JTextField("" + val);
		txt.setBounds(170, y, 100, 20);
		getContentPane().add(txt);
		JLabel lblP = new JLabel("%");
		lblP.setBounds(275, y + 4, 20, 14);
		getContentPane().add(lblP);
		return txt;
	}
}
