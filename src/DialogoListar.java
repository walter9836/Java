import javax.swing.*;
import java.awt.*;

public class DialogoListar extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextArea txtS;

	public DialogoListar() {
		setTitle("Listado de monitores");
		setBounds(100, 100, 450, 450);
		getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 414, 340);
		getContentPane().add(scrollPane);

		txtS = new JTextArea();
		txtS.setEditable(false);
		txtS.setFont(new Font("Monospaced", Font.PLAIN, 12));
		scrollPane.setViewportView(txtS);

		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.setBounds(100, 365, 100, 23);
		btnCerrar.addActionListener(e -> dispose());
		getContentPane().add(btnCerrar);

		JButton btnListar = new JButton("Listar");
		btnListar.setBounds(230, 365, 100, 23);
		btnListar.addActionListener(e -> listar());
		getContentPane().add(btnListar);
		
		listar();
	}

	private void listar() {
		txtS.setText("LISTADO DE MONITORES\n\n");
		imprimir(Tienda.m0, Tienda.p0, Tienda.pul0, Tienda.hz0, Tienda.res0);
		imprimir(Tienda.m1, Tienda.p1, Tienda.pul1, Tienda.hz1, Tienda.res1);
		imprimir(Tienda.m2, Tienda.p2, Tienda.pul2, Tienda.hz2, Tienda.res2);
		imprimir(Tienda.m3, Tienda.p3, Tienda.pul3, Tienda.hz3, Tienda.res3);
		imprimir(Tienda.m4, Tienda.p4, Tienda.pul4, Tienda.hz4, Tienda.res4);
	}

	private void imprimir(String m, double p, double pul, int hz, String res) {
		txtS.append("Modelo     : " + m + "\n");
		txtS.append("Precio     : S/. " + p + "\n");
		txtS.append("Pulgadas   : " + pul + "\n");
		txtS.append("Hercios    : " + hz + " Hz\n");
		txtS.append("Resolución : " + res + "\n");
		txtS.append("--------------------------------------\n");
	}
}
