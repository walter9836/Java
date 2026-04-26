import javax.swing.*;
import java.awt.*;

public class CalculadoraInversion extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTextField txtMonto;
	private JTextField txtMeses;
	private JLabel lblTasa;
	private JLabel lblInteres;
	private JLabel lblTotal;

	public CalculadoraInversion() {
		setTitle("Calculadora de Inversiones");
		setSize(500, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;

		// Título
		JLabel lblTitulo = new JLabel("CALCULADORA DE INVERSIONES");
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(lblTitulo, gbc);

		// Monto
		gbc.gridwidth = 1;
		gbc.gridy = 1;
		add(new JLabel("Monto a invertir (S/.):"), gbc);

		txtMonto = new JTextField(15);
		gbc.gridx = 1;
		add(txtMonto, gbc);

		// Meses
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Plazo en meses:"), gbc);

		txtMeses = new JTextField(15);
		gbc.gridx = 1;
		add(txtMeses, gbc);

		// Botón Calcular
		JButton btnCalcular = new JButton("Calcular");
		btnCalcular.addActionListener(e -> calcular());
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		add(btnCalcular, gbc);

		// Resultados
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.gridy = 4;
		add(new JLabel("Tasa anual:"), gbc);
		lblTasa = new JLabel("---");
		gbc.gridx = 1;
		add(lblTasa, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		add(new JLabel("Interés generado:"), gbc);
		lblInteres = new JLabel("---");
		gbc.gridx = 1;
		add(lblInteres, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		add(new JLabel("Monto final:"), gbc);
		lblTotal = new JLabel("---");
		lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
		gbc.gridx = 1;
		add(lblTotal, gbc);
	}

	private void calcular() {
		try {
			double monto = Double.parseDouble(txtMonto.getText());
			int meses = Integer.parseInt(txtMeses.getText());

			if (monto < 1000) {
				Notificador.advertencia(this, "El monto debe ser mayor o igual a S/. 1000.");
				return;
			}

			if (meses <= 0) {
				Notificador.advertencia(this, "El plazo debe ser mayor a 0 meses.");
				return;
			}

			double tasa = obtenerTasa(monto, meses);
			double interes = (monto * tasa * meses) / 12 / 100;
			double total = monto + interes;

			lblTasa.setText(tasa + "%");
			lblInteres.setText(String.format("S/. %.2f", interes));
			lblTotal.setText(String.format("S/. %.2f", total));

		} catch (NumberFormatException ex) {
			Notificador.error(this, "Ingrese valores numéricos válidos.");
		}
	}

	private double obtenerTasa(double monto, int meses) {
		if (monto >= 1000 && monto <= 5000) {
			return meses < 6 ? 2.0 : 3.0;
		} else if (monto > 5000 && monto <= 20000) {
			return meses < 12 ? 4.0 : 5.0;
		} else {
			return meses < 12 ? 5.5 : 6.5;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			CalculadoraInversion dialog = new CalculadoraInversion();
			dialog.setVisible(true);
		});
	}
}
