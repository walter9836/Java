import javax.swing.*;
import java.awt.*;

public class Modificar extends JDialog {
	private static final long serialVersionUID = 1L;
	private JComboBox<String> cboM;
	private JTextField tP, tPul, tHz, tRes;

	public Modificar() {
		setTitle("Modificar");
		setBounds(100, 100, 450, 250);
		getContentPane().setLayout(null);

		JLabel lblM = new JLabel("Modelo");
		lblM.setBounds(10, 15, 80, 14);
		getContentPane().add(lblM);

		cboM = new JComboBox<>(new String[]{Tienda.m0, Tienda.m1, Tienda.m2, Tienda.m3, Tienda.m4});
		cboM.setBounds(100, 11, 200, 22);
		cboM.addActionListener(e -> cargar());
		getContentPane().add(cboM);

		tP = crearCampo(45, "Precio (S/)");
		tPul = crearCampo(75, "Pulgadas");
		tHz = crearCampo(105, "Frecuencia");
		tRes = crearCampo(135, "Resolución");

		JButton btnGrabar = new JButton("Grabar");
		btnGrabar.setBounds(320, 11, 100, 23);
		btnGrabar.addActionListener(e -> grabar());
		getContentPane().add(btnGrabar);

		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.setBounds(320, 41, 100, 23);
		btnCerrar.addActionListener(e -> dispose());
		getContentPane().add(btnCerrar);

		cargar();
	}

	private JTextField crearCampo(int y, String label) {
		JLabel lbl = new JLabel(label);
		lbl.setBounds(10, y + 4, 80, 14);
		getContentPane().add(lbl);
		JTextField txt = new JTextField();
		txt.setBounds(100, y, 150, 20);
		getContentPane().add(txt);
		return txt;
	}

	private void cargar() {
		int i = cboM.getSelectedIndex();
		tP.setText("" + ((i==0)?Tienda.p0:(i==1)?Tienda.p1:(i==2)?Tienda.p2:(i==3)?Tienda.p3:Tienda.p4));
		tPul.setText("" + ((i==0)?Tienda.pul0:(i==1)?Tienda.pul1:(i==2)?Tienda.pul2:(i==3)?Tienda.pul3:Tienda.pul4));
		tHz.setText("" + ((i==0)?Tienda.hz0:(i==1)?Tienda.hz1:(i==2)?Tienda.hz2:(i==3)?Tienda.hz3:Tienda.hz4));
		tRes.setText((i==0)?Tienda.res0:(i==1)?Tienda.res1:(i==2)?Tienda.res2:(i==3)?Tienda.res3:Tienda.res4);
	}

	private void grabar() {
		try {
			int i = cboM.getSelectedIndex();
			double p = Double.parseDouble(tP.getText()), pul = Double.parseDouble(tPul.getText());
			int hz = Integer.parseInt(tHz.getText());
			String res = tRes.getText();
			
			if(i==0){Tienda.p0=p;Tienda.pul0=pul;Tienda.hz0=hz;Tienda.res0=res;}
			else if(i==1){Tienda.p1=p;Tienda.pul1=pul;Tienda.hz1=hz;Tienda.res1=res;}
			else if(i==2){Tienda.p2=p;Tienda.pul2=pul;Tienda.hz2=hz;Tienda.res2=res;}
			else if(i==3){Tienda.p3=p;Tienda.pul3=pul;Tienda.hz3=hz;Tienda.res3=res;}
			else {Tienda.p4=p;Tienda.pul4=pul;Tienda.hz4=hz;Tienda.res4=res;}
			
			Tienda.guardarDatos();
			JOptionPane.showMessageDialog(this, "Cambios guardados");
			dispose();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Datos inválidos");
		}
	}
}
