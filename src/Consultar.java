import javax.swing.*;
import java.awt.*;

public class Consultar extends JDialog {
	private static final long serialVersionUID = 1L;
	private JComboBox<String> cboM;
	private JTextField tP, tPul, tHz, tRes;

	public Consultar() {
		setTitle("Consultar");
		setBounds(100, 100, 450, 250);
		getContentPane().setLayout(null);

		JLabel lblM = new JLabel("Modelo");
		lblM.setBounds(10, 15, 80, 14);
		getContentPane().add(lblM);

		cboM = new JComboBox<>(new String[]{Tienda.m0, Tienda.m1, Tienda.m2, Tienda.m3, Tienda.m4});
		cboM.setBounds(100, 11, 200, 22);
		cboM.addActionListener(e -> mostrar());
		getContentPane().add(cboM);

		tP = crearCampo(45, "Precio (S/)");
		tPul = crearCampo(75, "Pulgadas");
		tHz = crearCampo(105, "Frecuencia");
		tRes = crearCampo(135, "Resolución");

		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.setBounds(320, 11, 100, 23);
		btnCerrar.addActionListener(e -> dispose());
		getContentPane().add(btnCerrar);

		mostrar();
	}

	private JTextField crearCampo(int y, String label) {
		JLabel lbl = new JLabel(label);
		lbl.setBounds(10, y + 4, 80, 14);
		getContentPane().add(lbl);
		JTextField txt = new JTextField();
		txt.setEditable(false);
		txt.setBounds(100, y, 150, 20);
		getContentPane().add(txt);
		return txt;
	}

	private void mostrar() {
		int i = cboM.getSelectedIndex();
		tP.setText("" + ((i==0)?Tienda.p0:(i==1)?Tienda.p1:(i==2)?Tienda.p2:(i==3)?Tienda.p3:Tienda.p4));
		tPul.setText("" + ((i==0)?Tienda.pul0:(i==1)?Tienda.pul1:(i==2)?Tienda.pul2:(i==3)?Tienda.pul3:Tienda.pul4));
		tHz.setText("" + ((i==0)?Tienda.hz0:(i==1)?Tienda.hz1:(i==2)?Tienda.hz2:(i==3)?Tienda.hz3:Tienda.hz4));
		tRes.setText((i==0)?Tienda.res0:(i==1)?Tienda.res1:(i==2)?Tienda.res2:(i==3)?Tienda.res3:Tienda.res4);
	}
}
