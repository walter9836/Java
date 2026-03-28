import javax.swing.*;
import java.awt.*;

public class AcercaDe extends JDialog {
	private static final long serialVersionUID = 1L;

	public AcercaDe() {
		setTitle("Acerca de Tienda");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 1));
		getContentPane().add(panel, BorderLayout.CENTER);

		JLabel lblTitulo = new JLabel("Tienda 1.0 - Monitores Gamer", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
		panel.add(lblTitulo);

		JLabel lblAutores = new JLabel("Autores: walter eduardo alvarado guerrero", SwingConstants.CENTER);
		panel.add(lblAutores);

		JLabel lblCurso = new JLabel("Curso: Introducción a la Algoritmia", SwingConstants.CENTER);
		panel.add(lblCurso);

		JButton btnCerrar = new JButton("Cerrar");
		btnCerrar.addActionListener(e -> dispose());
		panel.add(btnCerrar);
	}
}
