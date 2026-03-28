import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class Vender extends JDialog {
	private static final long serialVersionUID = 1L;
	private JComboBox<String> cboM;
	private JTextField txtP, txtC;
	private JTextArea txtS;
	private JButton btnVender, btnCerrar;

	public Vender() {
		setTitle("Vender");
		setBounds(100, 100, 450, 400);
		getContentPane().setLayout(null);

		JLabel lblM = new JLabel("Modelo");
		lblM.setBounds(10, 15, 80, 14);
		getContentPane().add(lblM);

		cboM = new JComboBox<>(new String[]{Tienda.m0, Tienda.m1, Tienda.m2, Tienda.m3, Tienda.m4});
		cboM.setBounds(100, 11, 200, 22);
		cboM.addActionListener(e -> mostrarPrecio());
		getContentPane().add(cboM);

		JLabel lblP = new JLabel("Precio (S/)");
		lblP.setBounds(10, 45, 80, 14);
		getContentPane().add(lblP);

		txtP = new JTextField();
		txtP.setEditable(false);
		txtP.setBounds(100, 41, 100, 20);
		getContentPane().add(txtP);

		JLabel lblC = new JLabel("Cantidad");
		lblC.setBounds(10, 75, 80, 14);
		getContentPane().add(lblC);

		txtC = new JTextField();
		txtC.setBounds(100, 71, 100, 20);
		getContentPane().add(txtC);

		btnVender = new JButton("Vender");
		btnVender.setBounds(320, 11, 100, 23);
		btnVender.addActionListener(e -> vender());
		getContentPane().add(btnVender);

		btnCerrar = new JButton("Cerrar");
		btnCerrar.setBounds(320, 41, 100, 23);
		btnCerrar.addActionListener(e -> dispose());
		getContentPane().add(btnCerrar);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 110, 414, 240);
		getContentPane().add(scrollPane);

		txtS = new JTextArea();
		txtS.setEditable(false);
		txtS.setFont(new Font("Monospaced", Font.PLAIN, 12));
		scrollPane.setViewportView(txtS);

		mostrarPrecio();
	}

	private void mostrarPrecio() {
		int i = cboM.getSelectedIndex();
		double p = (i==0)?Tienda.p0:(i==1)?Tienda.p1:(i==2)?Tienda.p2:(i==3)?Tienda.p3:Tienda.p4;
		txtP.setText("" + p);
	}

	private void vender() {
		try {
			if (txtC.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Debe ingresar una cantidad", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			int idx = cboM.getSelectedIndex();
			int cant = Integer.parseInt(txtC.getText());
			
			if (cant <= 0) {
				JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			double pre = obtenerPre(idx);
			double impCompra = pre * cant;
			
			double pDesc;
			if (cant >= 1 && cant <= 5) pDesc = Tienda.por1;
			else if (cant >= 6 && cant <= 10) pDesc = Tienda.por2;
			else if (cant >= 11 && cant <= 15) pDesc = Tienda.por3;
			else pDesc = Tienda.por4;
			
			double impDescuento = impCompra * (pDesc / 100);
			
			double subtotalNeto = impCompra - impDescuento;
			double montoIgv = subtotalNeto * 0.18;
			double totalAPagar = subtotalNeto + montoIgv;
			
			String obs;
			if (cant == 1) obs = Tienda.obs1;
			else if (cant >= 2 && cant <= 5) obs = Tienda.obs2;
			else obs = Tienda.obs3;

			Tienda.numVentas++;
			Tienda.importeAcumulado += totalAPagar;
			Tienda.guardarDatos();

			txtS.setText("      BOLETA DE VENTA\n");
			txtS.append("====================================\n");
			txtS.append("Modelo           : " + cboM.getSelectedItem() + "\n");
			txtS.append("Precio unitario  : S/. " + String.format("%.2f", pre) + "\n");
			txtS.append("Cantidad         : " + cant + "\n");
			txtS.append("------------------------------------\n");
			txtS.append("Importe compra   : S/. " + String.format("%.2f", impCompra) + "\n");
			txtS.append("Importe descuento: S/. " + String.format("%.2f", impDescuento) + "\n");
			txtS.append("------------------------------------\n");
			txtS.append("Subtotal (Neto)  : S/. " + String.format("%.2f", subtotalNeto) + "\n");
			txtS.append("IGV (18%)        : S/. " + String.format("%.2f", montoIgv) + "\n");
			txtS.append("------------------------------------\n");
			txtS.append("TOTAL A PAGAR    : S/. " + String.format("%.2f", totalAPagar) + "\n");
			txtS.append("------------------------------------\n");
			txtS.append("Obsequio         : " + obs + "\n");
			txtS.append("====================================\n");

			if (Tienda.numVentas % 5 == 0) mostrarAvance();

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Error: La cantidad debe ser un número entero", "Error de entrada", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void mostrarAvance() {
		DecimalFormat df = new DecimalFormat("0.00");
		double porc = (Tienda.importeAcumulado / Tienda.cuotaDiaria) * 100;
		String msg = "Avance de ventas\n\n" +
					 "Venta Nro. " + Tienda.numVentas + "\n" +
					 "Importe total acumulado : S/. " + df.format(Tienda.importeAcumulado) + "\n" +
					 "Porcentaje cuota diaria : " + df.format(porc) + "%";
		JOptionPane.showMessageDialog(this, msg, "Mensaje de alerta", JOptionPane.INFORMATION_MESSAGE);
	}

	private double obtenerPre(int i) {
		switch(i) {
			case 0: return Tienda.p0; case 1: return Tienda.p1; case 2: return Tienda.p2; case 3: return Tienda.p3;
			default: return Tienda.p4;
		}
	}
}
