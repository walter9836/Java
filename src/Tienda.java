import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class Tienda extends JFrame {
	private static final long serialVersionUID = 1L;

	// DATOS GLOBALES (MONITORES GAMER)
	public static String m0 = "ASUS ROG Swift", m1 = "Samsung Odyssey G7", m2 = "MSI Optix G24", m3 = "LG UltraGear", m4 = "Gigabyte M27Q";
	public static double p0 = 2500.0, p1 = 1800.0, p2 = 950.0, p3 = 1200.0, p4 = 1550.0;
	public static double pul0 = 27.0, pul1 = 32.0, pul2 = 24.0, pul3 = 27.0, pul4 = 27.0;
	public static int hz0 = 240, hz1 = 240, hz2 = 144, hz3 = 165, hz4 = 170;
	public static String res0 = "4K", res1 = "2K", res2 = "FHD", res3 = "FHD", res4 = "2K";

	// CONFIGURACIÓN (DESCUENTOS Y OBSEQUIOS)
	public static double por1 = 5.0, por2 = 7.5, por3 = 10.0, por4 = 15.0;
	public static String obs1 = "Mouse Gamer", obs2 = "Teclado Mecánico", obs3 = "Silla Gamer";
	
	// VARIABLES DE CONTROL DE VENTAS (CIBERTEC REQ)
	public static int numVentas = 0;
	public static double importeAcumulado = 0;
	public static double cuotaDiaria = 30000.0;

	public static void main(String[] args) {
		cargarDatos();
		EventQueue.invokeLater(() -> {
			try {
				Tienda frame = new Tienda();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Tienda() {
		setTitle("Tienda 1.0 - Monitores Gamer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// Menú Archivo
		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);
		addItem(mnArchivo, "Salir", e -> {
			int r = JOptionPane.showConfirmDialog(this, "¿Desea salir de la aplicación?", "Confirmar", JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.YES_OPTION) System.exit(0);
		});

		// Menú Mantenimiento
		JMenu mnMant = new JMenu("Mantenimiento");
		menuBar.add(mnMant);
		addItem(mnMant, "Consultar monitor", e -> abrir(new Consultar()));
		addItem(mnMant, "Modificar monitor", e -> abrir(new Modificar()));
		addItem(mnMant, "Listar monitores", e -> abrir(new DialogoListar()));

		// Menú Ventas
		JMenu mnVentas = new JMenu("Ventas");
		menuBar.add(mnVentas);
		addItem(mnVentas, "Vender", e -> abrir(new Vender()));

		// Menú Configuración
		JMenu mnConfig = new JMenu("Configuracion");
		menuBar.add(mnConfig);
		addItem(mnConfig, "Configurar descuentos", e -> abrir(new ConfDesc()));
		addItem(mnConfig, "Configurar obsequios", e -> abrir(new ConfObs()));
		addItem(mnConfig, "Configurar cuota diaria", e -> abrir(new ConfCuota()));

		// Menú Ayuda
		JMenu mnAyuda = new JMenu("Ayuda");
		menuBar.add(mnAyuda);
		addItem(mnAyuda, "Acerca de Tienda", e -> abrir(new AcercaDe()));
	}

	private void addItem(JMenu menu, String texto, ActionListener listener) {
		JMenuItem item = new JMenuItem(texto);
		item.addActionListener(listener);
		menu.add(item);
	}

	private void abrir(JDialog dialogo) {
		dialogo.setModal(true);
		dialogo.setLocationRelativeTo(this);
		dialogo.setVisible(true);
	}

	public static void cargarDatos() {
		try (Scanner sc = new Scanner(new File("datos.txt"))) {
			m0 = sc.nextLine(); m1 = sc.nextLine(); m2 = sc.nextLine(); m3 = sc.nextLine(); m4 = sc.nextLine();
			p0 = sc.nextDouble(); p1 = sc.nextDouble(); p2 = sc.nextDouble(); p3 = sc.nextDouble(); p4 = sc.nextDouble();
			pul0 = sc.nextDouble(); pul1 = sc.nextDouble(); pul2 = sc.nextDouble(); pul3 = sc.nextDouble(); pul4 = sc.nextDouble();
			hz0 = sc.nextInt(); hz1 = sc.nextInt(); hz2 = sc.nextInt(); hz3 = sc.nextInt(); hz4 = sc.nextInt();
			sc.nextLine(); 
			res0 = sc.nextLine(); res1 = sc.nextLine(); res2 = sc.nextLine(); res3 = sc.nextLine(); res4 = sc.nextLine();
			por1 = sc.nextDouble(); por2 = sc.nextDouble(); por3 = sc.nextDouble(); por4 = sc.nextDouble();
			sc.nextLine(); 
			obs1 = sc.nextLine(); obs2 = sc.nextLine(); obs3 = sc.nextLine();
			cuotaDiaria = sc.nextDouble();
			// El historial de ventas (numVentas e importeAcumulado) suele reiniciarse cada día, 
			// pero lo guardamos por si el docente lo pide.
			numVentas = sc.nextInt();
			importeAcumulado = sc.nextDouble();
		} catch (Exception e) {
			System.out.println("Cargando valores por defecto...");
		}
	}

	public static void guardarDatos() {
		try (PrintWriter pw = new PrintWriter(new FileWriter("datos.txt"))) {
			pw.println(m0); pw.println(m1); pw.println(m2); pw.println(m3); pw.println(m4);
			pw.println(p0); pw.println(p1); pw.println(p2); pw.println(p3); pw.println(p4);
			pw.println(pul0); pw.println(pul1); pw.println(pul2); pw.println(pul3); pw.println(pul4);
			pw.println(hz0); pw.println(hz1); pw.println(hz2); pw.println(hz3); pw.println(hz4);
			pw.println(res0); pw.println(res1); pw.println(res2); pw.println(res3); pw.println(res4);
			pw.println(por1); pw.println(por2); pw.println(por3); pw.println(por4);
			pw.println(obs1); pw.println(obs2); pw.println(obs3);
			pw.println(cuotaDiaria);
			pw.println(numVentas);
			pw.println(importeAcumulado);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


