import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * CLASE TIENDA (VENTANA PRINCIPAL)
 * --------------------------------
 * Extiende JFrame: es la ventana principal de la aplicación.
 *
 * Responsabilidades:
 *  1. Arrancar el programa (main)
 *  2. Guardar el estado global (productos, ventas, cuota) en variables static
 *  3. Construir la UI: sidebar + área central con CardLayout
 *  4. Cargar y guardar datos desde/hacia archivos
 *  5. Calcular el dashboard con estadísticas
 *
 * ¿Por qué variables static?
 * Para tener estado global accesible desde cualquier panel sin pasar referencias.
 * En una app más grande se usaría Dependency Injection, pero aquí simplifica el diseño.
 */
public class Tienda extends JFrame {
    private static final long serialVersionUID = 1L; // requerido al extender JFrame

    // ========== ESTADO GLOBAL DE LA APLICACIÓN ==========

    // Lista dinámica de productos. ArrayList permite agregar/eliminar en runtime.
    public static ArrayList<Producto> productos = new ArrayList<>();

    // Porcentajes configurables desde PanelConfDesc
    // Array de 4 posiciones: [1-5], [6-10], [11-15], [>15]
    public static double[] porcentajesDescuento = {5.0, 7.5, 10.0, 15.0};

    // Obsequios configurables desde PanelConfObs
    // Array de 3 posiciones: [1u], [2-5u], [>5u]
    public static String[] obsequios = {"Mouse Gamer", "Teclado Mecánico", "Silla Gamer"};

    public static int numVentas = 0;               // Contador histórico (usado para nro de boleta, nunca se reinicia)
    public static int ventasHoy = 0;               // Cantidad de ventas del día actual (se reinicia al cambiar de día)
    public static double importeAcumulado = 0;     // Total vendido hoy (se reinicia al cambiar de día)
    public static double cuotaDiaria = 30000.0;    // Meta de ventas (configurable)
    public static String fechaUltimoReset = "";    // Última fecha (yyyy-MM-dd) en que se reinició el día

    // Preferencia visual: true = tema oscuro (navy), false = tema claro
    // Se persiste en datos.txt para que el usuario no tenga que reconfigurar.
    public static boolean temaOscuro = true;

    // ========== ATRIBUTOS DE INSTANCIA (de la ventana) ==========

    private JPanel panelContenido;       // Contenedor con CardLayout
    private CardLayout cardLayout;       // Gestor que intercambia los paneles
    private JButton[] botonesSidebar;    // Botones del menú lateral
    private String[] claves;             // Claves asociadas a cada botón

    /**
     * MÉTODO MAIN: punto de entrada del programa.
     *
     * Pasos:
     *  1. Cargar productos iniciales (por si es la primera vez)
     *  2. Leer archivo datos.txt (sobreescribe si existe)
     *  3. Aplicar Look & Feel del sistema operativo
     *  4. Crear la ventana en el Event Dispatch Thread (EDT)
     *
     * ¿Por qué EventQueue.invokeLater?
     * Swing NO es thread-safe. Toda UI debe crearse en el EDT para evitar errores.
     */
    public static void main(String[] args) {
        inicializarProductosPorDefecto();
        cargarDatos();
        resetDiarioSiCorresponde(); // si cambió el día desde la última ejecución, reinicia
        // Aplicar el tema persistido antes de construir la UI
        if (temaOscuro) TemaOscuro.aplicarOscuro();
        else TemaOscuro.aplicarClaro();
        EventQueue.invokeLater(() -> {
            try {
                // Hace que la app se vea como una app nativa de Windows
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new Tienda().setVisible(true);
        });
    }

    /**
     * Carga 5 monitores por defecto al iniciar.
     * Sirve para que la app funcione la primera vez (sin archivo datos.txt).
     * Si el archivo existe, cargarDatos() los reemplazará con los guardados.
     */
    private static void inicializarProductosPorDefecto() {
        productos.add(new Producto("ASUS ROG Swift", 2500.0, 27.0, 240, "4K", 10, "IPS", 1, "HDMI, DP, USB-C", 36));
        productos.add(new Producto("Samsung Odyssey G7", 1800.0, 32.0, 240, "2K", 10, "VA", 1, "HDMI, DP", 24));
        productos.add(new Producto("MSI Optix G24", 950.0, 24.0, 144, "FHD", 15, "IPS", 4, "HDMI, DP", 12));
        productos.add(new Producto("LG UltraGear", 1200.0, 27.0, 165, "FHD", 12, "IPS", 1, "HDMI, DP", 24));
        productos.add(new Producto("Gigabyte M27Q", 1550.0, 27.0, 170, "2K", 8, "IPS", 1, "HDMI, DP, USB-C", 24));
    }

    /**
     * Devuelve un array con los nombres de todos los productos.
     * Se usa para poblar los JComboBox en varios paneles (Vender, Consultar, etc.)
     */
    public static String[] getNombresProductos() {
        String[] nombres = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++) nombres[i] = productos.get(i).getNombre();
        return nombres;
    }

    /**
     * REGLA DE NEGOCIO: a más cantidad, más descuento.
     * Se usan if encadenados (en cascada) para encontrar el rango correcto.
     * Los límites se toman de Constantes para facilitar cambios.
     */
    public static double obtenerDescuento(int cantidad) {
        if (cantidad <= Constantes.RANGO_DESC_1_MAX) return porcentajesDescuento[0];  // 1-5  → 5%
        if (cantidad <= Constantes.RANGO_DESC_2_MAX) return porcentajesDescuento[1];  // 6-10 → 7.5%
        if (cantidad <= Constantes.RANGO_DESC_3_MAX) return porcentajesDescuento[2];  // 11-15 → 10%
        return porcentajesDescuento[3];                                                // >15  → 15%
    }

    /**
     * REGLA DE NEGOCIO: obsequio según cantidad comprada.
     */
    public static String obtenerObsequio(int cantidad) {
        if (cantidad == Constantes.RANGO_OBS_1) return obsequios[0];       // 1  → Mouse
        if (cantidad <= Constantes.RANGO_OBS_2_MAX) return obsequios[1];   // 2-5 → Teclado
        return obsequios[2];                                                // >5 → Silla
    }

    /**
     * CONSTRUCTOR DE LA VENTANA
     * Arma toda la UI: sidebar + contenido central + barra de estado.
     */
    public Tienda() {
        // Configuración básica del JFrame (solo se hace UNA vez en la vida de la ventana)
        setTitle(Constantes.APP_NOMBRE + " - " + Constantes.APP_SUBTITULO);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 620));
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout(0, 0));
        construirUI();
    }

    /**
     * Construye TODO el contenido de la ventana (sidebar + paneles + barra de estado).
     * Se separa del constructor para poder reconstruir la UI en caliente al cambiar
     * de tema, sin cerrar ni reabrir la ventana.
     */
    private void construirUI() {
        getContentPane().setBackground(TemaOscuro.BASE);

        // ========== SIDEBAR (menú lateral izquierdo) ==========
        JPanel sidebar = new JPanel();
        sidebar.setBackground(TemaOscuro.MANTLE);
        sidebar.setPreferredSize(new Dimension(210, 0));
        // BoxLayout vertical: apila los elementos uno debajo del otro
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo en la parte superior
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        logoPanel.setBackground(TemaOscuro.MANTLE);
        logoPanel.setBorder(new EmptyBorder(18, 4, 14, 0));
        logoPanel.setMaximumSize(new Dimension(210, 55));
        JLabel lblLogo = new JLabel(Constantes.APP_NOMBRE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(TemaOscuro.ACCENT);
        logoPanel.add(lblLogo);
        sidebar.add(logoPanel);
        sidebar.add(TemaOscuro.crearSeparador());
        sidebar.add(Box.createVerticalStrut(12));

        // Menú definido como matriz String[][]: [etiqueta, clave]
        // Si la clave es null y el texto no está vacío → es un TÍTULO DE SECCIÓN
        // Si la clave es null y el texto está vacío → es un ESPACIO
        // Si la clave no es null → es un BOTÓN navegable
        String[][] menu = {
            {"GENERAL", null},
            {"Inicio", "dashboard"},
            {"Punto de Venta", "vender"},
            {"Historial de Ventas", "historial"},
            {"Clientes", "clientes"},
            {"", null},
            {"PRODUCTOS", null},
            {"Consultar", "consultar"},
            {"Modificar", "modificar"},
            {"Agregar / Eliminar", "gestionar"},
            {"Listado General", "listar"},
            {"", null},
            {"CONFIGURACION", null},
            {"Descuentos", "descuentos"},
            {"Obsequios", "obsequios"},
            {"Cuota Diaria", "cuota"},
        };

        // Primero contamos cuántos botones reales hay (para dimensionar arrays)
        int numBotones = 0;
        for (String[] item : menu) if (item[1] != null) numBotones++;
        botonesSidebar = new JButton[numBotones];
        claves = new String[numBotones];
        int idx = 0;

        // Recorremos el menú y creamos cada elemento según su tipo
        for (String[] item : menu) {
            if (item[1] == null) {
                if (item[0].isEmpty()) {
                    // Texto vacío → espacio en blanco
                    sidebar.add(Box.createVerticalStrut(8));
                } else {
                    // Texto con clave null → título de sección
                    JLabel sec = new JLabel("  " + item[0]);
                    sec.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    sec.setForeground(TemaOscuro.SURFACE2);
                    sec.setBorder(new EmptyBorder(6, 14, 4, 0));
                    sec.setMaximumSize(new Dimension(210, 22));
                    sidebar.add(sec);
                }
            } else {
                // Es un botón navegable. El primer botón (idx=0) queda activo.
                JButton btn = TemaOscuro.crearBotonSidebar(item[0], idx == 0);
                String clave = item[1];
                final int i = idx; // variable final para usar en lambda
                // Al hacer clic, navega al panel correspondiente
                btn.addActionListener(e -> navegarA(clave, i));
                sidebar.add(btn);
                sidebar.add(Box.createVerticalStrut(1));
                botonesSidebar[idx] = btn;
                claves[idx] = clave;
                idx++;
            }
        }

        // Empuja los botones "Acerca de" y "Salir" hacia abajo
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(TemaOscuro.crearSeparador());

        // Botón "Cambiar Tema": alterna entre oscuro (navy) y claro empresarial.
        // Se persiste la preferencia y se recrea la ventana para refrescar todos los componentes.
        JButton btnTema = TemaOscuro.crearBotonSidebar(
                temaOscuro ? "Tema: Oscuro" : "Tema: Claro", false);
        btnTema.addActionListener(e -> cambiarTema());
        sidebar.add(btnTema);

        // Botón "Acerca de" → abre diálogo modal
        JButton btnAcerca = TemaOscuro.crearBotonSidebar("Acerca de", false);
        btnAcerca.addActionListener(e -> {
            AcercaDe d = new AcercaDe();
            d.setModal(true);              // bloquea la ventana padre hasta cerrar
            d.setLocationRelativeTo(this); // centra sobre la ventana principal
            d.setVisible(true);
        });
        sidebar.add(btnAcerca);

        // Botón "Salir" con confirmación
        JButton btnSalir = TemaOscuro.crearBotonSidebar("Salir", false);
        btnSalir.setForeground(TemaOscuro.RED); // color rojo para acción destructiva
        btnSalir.addActionListener(e -> {
            if (Notificador.confirmar(this, "¿Desea salir?")) System.exit(0);
        });
        sidebar.add(btnSalir);
        sidebar.add(Box.createVerticalStrut(8));
        getContentPane().add(sidebar, BorderLayout.WEST);

        // ========== CONTENIDO CENTRAL (CardLayout) ==========
        // CardLayout apila paneles y muestra solo uno a la vez (como tarjetas)
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(TemaOscuro.BASE);

        // Agregamos cada panel con su clave identificadora
        panelContenido.add(crearDashboard(), "dashboard");
        panelContenido.add(new PanelVender(this), "vender");
        panelContenido.add(new PanelHistorial(), "historial");
        panelContenido.add(new PanelClientes(), "clientes");
        panelContenido.add(new PanelConsultar(), "consultar");
        panelContenido.add(new PanelModificar(), "modificar");
        panelContenido.add(new PanelGestionar(), "gestionar");
        panelContenido.add(new PanelListar(), "listar");
        panelContenido.add(new PanelConfDesc(), "descuentos");
        panelContenido.add(new PanelConfObs(), "obsequios");
        panelContenido.add(new PanelConfCuota(), "cuota");

        getContentPane().add(panelContenido, BorderLayout.CENTER);

        // ========== BARRA DE ESTADO (inferior) ==========
        JPanel barraEstado = new JPanel(new BorderLayout());
        barraEstado.setBackground(TemaOscuro.CRUST);
        barraEstado.setPreferredSize(new Dimension(0, 26));
        barraEstado.setBorder(new EmptyBorder(0, 14, 0, 14));

        // Info de la app a la izquierda
        JLabel lblInfo = new JLabel(Constantes.APP_NOMBRE + " v" + Constantes.APP_VERSION +
                "  |  Cibertec - Grupo 12  |  Introducción a la Algoritmia");
        lblInfo.setFont(TemaOscuro.FONT_XS);
        lblInfo.setForeground(TemaOscuro.OVERLAY0);
        barraEstado.add(lblInfo, BorderLayout.WEST);

        // Reloj a la derecha, actualizado cada segundo
        JLabel lblReloj = new JLabel();
        lblReloj.setFont(TemaOscuro.FONT_XS);
        lblReloj.setForeground(TemaOscuro.OVERLAY0);
        barraEstado.add(lblReloj, BorderLayout.EAST);
        // Timer Swing: ejecuta un ActionListener cada N milisegundos (1000 = 1s)
        Timer timer = new Timer(1000, e ->
            lblReloj.setText(new java.text.SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(new java.util.Date())));
        timer.setInitialDelay(0); // primera ejecución inmediata (no esperar 1s)
        timer.start();

        getContentPane().add(barraEstado, BorderLayout.SOUTH);
    }

    /**
     * Navega a un panel específico.
     *  1. Resalta el botón activo, los demás quedan inactivos.
     *  2. Si es el dashboard, se reconstruye (para mostrar datos actualizados).
     *  3. Refresca todos los paneles Refrescables.
     *  4. Muestra el panel solicitado con cardLayout.show().
     *
     * ¿Por qué polimorfismo con Refrescable?
     * Para que cada panel sepa cómo actualizarse sin que Tienda conozca los detalles.
     */
    public void navegarA(String clave, int botonIndex) {
        // Verificar reset diario en cualquier navegación (por si la app está abierta días)
        resetDiarioSiCorresponde();
        for (int i = 0; i < botonesSidebar.length; i++) {
            if (i == botonIndex) TemaOscuro.marcarBotonActivo(botonesSidebar[i]);
            else TemaOscuro.marcarBotonInactivo(botonesSidebar[i]);
        }
        // El dashboard se reconstruye porque sus valores cambian con cada venta
        if (clave.equals("dashboard")) {
            panelContenido.remove(0);
            panelContenido.add(crearDashboard(), "dashboard", 0);
        }
        // Refresca cualquier panel que implemente la interfaz Refrescable
        for (Component c : panelContenido.getComponents()) {
            if (c instanceof Refrescable) ((Refrescable) c).refrescar();
        }
        cardLayout.show(panelContenido, clave);
    }

    /** Atajo para volver al dashboard */
    public void irADashboard() { navegarA("dashboard", 0); }

    /**
     * Reinicia los contadores diarios (ventasHoy, importeAcumulado) si la fecha
     * actual es distinta de la última fecha registrada.
     *
     * Se llama: al iniciar la app, al navegar al dashboard y antes de cada venta.
     * Devuelve true si efectivamente se hizo el reset (útil para refrescar UI).
     */
    public static boolean resetDiarioSiCorresponde() {
        String hoy = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        if (!hoy.equals(fechaUltimoReset)) {
            ventasHoy = 0;
            importeAcumulado = 0;
            fechaUltimoReset = hoy;
            guardarDatos();
            return true;
        }
        return false;
    }

    /**
     * Alterna entre tema oscuro y claro EN CALIENTE (sin cerrar la ventana).
     * Como los componentes Swing capturan colores al crearse, no se pueden
     * "recolorear" en sitio: se vacía el contentPane y se reconstruye toda
     * la UI con la paleta nueva. El estado (productos, ventas, etc.) sobrevive
     * porque está en variables static de Tienda.
     */
    private void cambiarTema() {
        TemaOscuro.cambiar();
        temaOscuro = TemaOscuro.modoOscuro;
        guardarDatos();
        getContentPane().removeAll(); // descarta sidebar + paneles + barra de estado
        construirUI();                 // los reconstruye con los nuevos colores
        revalidate();                  // recalcula layouts
        repaint();                     // redibuja
    }

    /**
     * CREAR DASHBOARD
     * Construye el panel principal con estadísticas en tiempo real.
     *
     * Estructura:
     *   Header (título + fecha)
     *   Fila 1: 4 StatCards (ventas, monto, stock, cuota)
     *   Fila 2: 3 tarjetas (avance cuota, inventario, reporte)
     */
    private JPanel crearDashboard() {
        JPanel dash = new JPanel(new BorderLayout(0, 0));
        dash.setBackground(TemaOscuro.BASE);
        dash.setBorder(new EmptyBorder(24, 28, 20, 28));

        // Fecha larga en español (ej: "jueves 16 de abril, 2026")
        String fecha = new java.text.SimpleDateFormat("EEEE dd 'de' MMMM, yyyy",
                new java.util.Locale("es")).format(new java.util.Date());
        dash.add(TemaOscuro.crearHeaderPagina("Dashboard", fecha), BorderLayout.NORTH);

        // Centro: dos filas apiladas con BoxLayout vertical
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        // ----- FILA 1: 4 tarjetas de estadísticas -----
        JPanel filaStats = new JPanel(new GridLayout(1, 4, 14, 0)); // 4 columnas iguales
        filaStats.setOpaque(false);
        filaStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        // Calculamos stock total recorriendo productos
        int totalStock = 0, stockBajo = 0;
        for (Producto p : productos) {
            totalStock += p.getStock();
            if (p.getStock() < Constantes.STOCK_MINIMO_ALERTA) stockBajo++;
        }
        // %cuota = (acumulado / meta) × 100. Con if para evitar división por cero.
        double porcCuota = cuotaDiaria > 0 ? (importeAcumulado / cuotaDiaria) * 100 : 0;

        filaStats.add(TemaOscuro.crearStatCard("VENTAS HOY", "" + ventasHoy, TemaOscuro.ACCENT));
        filaStats.add(TemaOscuro.crearStatCard("MONTO ACUMULADO", "S/. " + String.format("%.0f", importeAcumulado), TemaOscuro.GREEN));
        filaStats.add(TemaOscuro.crearStatCard("STOCK TOTAL", totalStock + " uds", TemaOscuro.ORANGE));
        // Color dinámico según avance: ≥100%→verde, ≥60%→amarillo, sino→rojo
        filaStats.add(TemaOscuro.crearStatCard("CUOTA DIARIA", String.format("%.1f%%", porcCuota),
                porcCuota >= 100 ? TemaOscuro.GREEN : porcCuota >= 60 ? TemaOscuro.YELLOW : TemaOscuro.RED));

        centro.add(filaStats);
        centro.add(Box.createVerticalStrut(18));

        // ----- FILA 2: 3 tarjetas (cuota + inventario + reporte) -----
        JPanel filaInferior = new JPanel(new GridLayout(1, 3, 14, 0));
        filaInferior.setOpaque(false);

        // --- Tarjeta 1: Avance de cuota con barra de progreso ---
        JPanel panelCuota = TemaOscuro.crearTarjeta("AVANCE DE CUOTA");
        JPanel cuotaC = new JPanel();
        cuotaC.setOpaque(false);
        cuotaC.setLayout(new BoxLayout(cuotaC, BoxLayout.Y_AXIS));
        cuotaC.add(Box.createVerticalStrut(8));
        JProgressBar barra = TemaOscuro.crearProgressBar();
        barra.setValue(Math.min((int) porcCuota, 100)); // máx 100 para no desbordar
        barra.setString(String.format("%.1f%%", porcCuota));
        barra.setForeground(porcCuota >= 100 ? TemaOscuro.GREEN : porcCuota >= 60 ? TemaOscuro.YELLOW : TemaOscuro.RED);
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        cuotaC.add(barra);
        cuotaC.add(Box.createVerticalStrut(12));

        // Grid con 3 filas: Meta / Acumulado / Faltante
        JPanel det = new JPanel(new GridLayout(3, 2, 8, 5));
        det.setOpaque(false);
        det.add(TemaOscuro.crearLabelPequeno("Meta"));
        det.add(TemaOscuro.crearValorLabel("S/. " + String.format("%.0f", cuotaDiaria)));
        det.add(TemaOscuro.crearLabelPequeno("Acumulado"));
        JLabel la = TemaOscuro.crearValorLabel("S/. " + String.format("%.0f", importeAcumulado));
        la.setForeground(TemaOscuro.GREEN);
        det.add(la);
        det.add(TemaOscuro.crearLabelPequeno("Faltante"));
        // Math.max(0, ...) para que no muestre negativos si se superó la meta
        double falt = Math.max(0, cuotaDiaria - importeAcumulado);
        JLabel lf = TemaOscuro.crearValorLabel("S/. " + String.format("%.0f", falt));
        lf.setForeground(falt > 0 ? TemaOscuro.YELLOW : TemaOscuro.GREEN);
        det.add(lf);
        cuotaC.add(det);
        panelCuota.add(cuotaC, BorderLayout.CENTER);
        filaInferior.add(panelCuota);

        // --- Tarjeta 2: Inventario (lista de productos con stock) ---
        JPanel panelInv = TemaOscuro.crearTarjeta("INVENTARIO (" + productos.size() + ")");
        JPanel invC = new JPanel();
        invC.setOpaque(false);
        invC.setLayout(new BoxLayout(invC, BoxLayout.Y_AXIS));
        invC.add(Box.createVerticalStrut(4));
        // Recorre productos y crea una fila por cada uno
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            JPanel fila = new JPanel(new BorderLayout());
            fila.setOpaque(false);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
            JLabel ln = new JLabel(p.getNombre());
            ln.setFont(TemaOscuro.FONT_SM);
            ln.setForeground(TemaOscuro.TEXT);
            fila.add(ln, BorderLayout.WEST);
            JLabel ls = new JLabel(p.getStock() + " uds  ");
            ls.setFont(TemaOscuro.FONT_BOLD_SM);
            // Color según nivel de stock: <5 rojo, <10 amarillo, resto verde
            ls.setForeground(p.getStock() < Constantes.STOCK_MINIMO_ALERTA ? TemaOscuro.RED
                    : p.getStock() < 10 ? TemaOscuro.YELLOW : TemaOscuro.GREEN);
            fila.add(ls, BorderLayout.EAST);
            invC.add(fila);
        }
        // Si hay productos con stock bajo, se muestra alerta al final
        if (stockBajo > 0) {
            invC.add(Box.createVerticalStrut(6));
            JLabel al = new JLabel("  " + stockBajo + " con stock bajo");
            al.setFont(TemaOscuro.FONT_XS);
            al.setForeground(TemaOscuro.RED);
            invC.add(al);
        }
        panelInv.add(invC, BorderLayout.CENTER);
        filaInferior.add(panelInv);

        // --- Tarjeta 3: Reporte del día (leyendo historial.txt) ---
        JPanel panelReporte = TemaOscuro.crearTarjeta("REPORTE DEL DIA");
        JPanel repC = new JPanel();
        repC.setOpaque(false);
        repC.setLayout(new BoxLayout(repC, BoxLayout.Y_AXIS));
        repC.add(Box.createVerticalStrut(4));

        String[] datosReporte = calcularReporte();
        agregarFilaReporte(repC, "Producto más vendido", datosReporte[0]);
        agregarFilaReporte(repC, "Venta más alta", datosReporte[1]);
        agregarFilaReporte(repC, "Promedio por venta", datosReporte[2]);
        agregarFilaReporte(repC, "Total clientes", datosReporte[3]);

        panelReporte.add(repC, BorderLayout.CENTER);
        filaInferior.add(panelReporte);

        centro.add(filaInferior);
        dash.add(centro, BorderLayout.CENTER);
        return dash;
    }

    /**
     * Helper para agregar una fila "label: valor" al reporte.
     * Se extrae en método para no repetir 4 veces el mismo bloque.
     */
    private void agregarFilaReporte(JPanel panel, String label, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        fila.setBorder(new EmptyBorder(2, 0, 2, 0));
        JLabel ll = TemaOscuro.crearLabelPequeno(label);
        fila.add(ll, BorderLayout.WEST);
        JLabel lv = TemaOscuro.crearValorLabel(valor);
        lv.setForeground(TemaOscuro.CYAN);
        fila.add(lv, BorderLayout.EAST);
        panel.add(fila);
    }

    /**
     * Lee historial.txt y calcula estadísticas.
     *
     * Estructuras de datos usadas:
     *  - HashMap<producto, cantidad>: para contar unidades vendidas por modelo.
     *    Se elige HashMap porque permite buscar por clave en O(1).
     *  - HashSet<dni>: para contar clientes únicos.
     *    Se elige HashSet porque no admite duplicados (un DNI se cuenta una vez).
     *
     * @return array con: [producto top, venta máxima, promedio, total clientes]
     */
    private String[] calcularReporte() {
        String productoTop = "Sin datos";
        String ventaMax = "S/. 0.00";
        String promedio = "S/. 0.00";
        String totalClientes = "0";

        try (Scanner sc = new Scanner(new File(Constantes.ARCHIVO_HISTORIAL))) {
            java.util.HashMap<String, Integer> conteo = new java.util.HashMap<>();
            double maxVenta = 0;
            double sumaVentas = 0;
            int totalVentas = 0;
            java.util.HashSet<String> clientes = new java.util.HashSet<>();

            while (sc.hasNextLine()) {
                String[] partes = sc.nextLine().split(Constantes.SEPARADOR_HISTORIAL);
                // Validamos que la línea tenga el número correcto de columnas (robustez)
                if (partes.length >= Constantes.COLUMNAS_HISTORIAL) {
                    String producto = partes[4];
                    // getOrDefault(k, 0) + 1 = patrón típico para contar con HashMap
                    conteo.put(producto, conteo.getOrDefault(producto, 0) + Integer.parseInt(partes[5].trim()));
                    double monto = Double.parseDouble(partes[6].trim());
                    if (monto > maxVenta) maxVenta = monto;
                    sumaVentas += monto;
                    totalVentas++;
                    clientes.add(partes[3].trim()); // HashSet ignora duplicados
                }
            }

            // Si hay ventas, calculamos los valores reales
            if (totalVentas > 0) {
                // Encontrar el producto con mayor cantidad vendida
                String top = "";
                int maxCant = 0;
                for (java.util.Map.Entry<String, Integer> e : conteo.entrySet()) {
                    if (e.getValue() > maxCant) {
                        maxCant = e.getValue();
                        top = e.getKey();
                    }
                }
                productoTop = top + " (" + maxCant + ")";
                ventaMax = "S/. " + String.format("%.2f", maxVenta);
                promedio = "S/. " + String.format("%.2f", sumaVentas / totalVentas);
                totalClientes = "" + clientes.size();
            }
        } catch (Exception ignored) {
            // Si no existe el archivo, se devuelven valores por defecto
        }

        return new String[]{productoTop, ventaMax, promedio, totalClientes};
    }

    // ========== PERSISTENCIA EN ARCHIVO ==========

    /**
     * Carga el estado de la aplicación desde datos.txt.
     *
     * Formato del archivo:
     *   línea 1: número de productos (N)
     *   líneas siguientes: por cada producto, 10 líneas con sus atributos
     *   4 líneas: porcentajes de descuento
     *   3 líneas: obsequios
     *   1 línea:  cuota diaria
     *   1 línea:  número de ventas
     *   1 línea:  importe acumulado
     *
     * Se usa try-with-resources para cerrar el Scanner automáticamente.
     * Si el archivo no existe, se mantienen los valores por defecto.
     */
    public static void cargarDatos() {
        try (Scanner sc = new Scanner(new File(Constantes.ARCHIVO_DATOS))) {
            int numProductos = Integer.parseInt(sc.nextLine().trim());
            productos.clear(); // limpia los productos por defecto
            // Lee N productos, 10 líneas por cada uno
            for (int i = 0; i < numProductos; i++) {
                String nombre = sc.nextLine();
                double precio = Double.parseDouble(sc.nextLine().trim());
                double pulgadas = Double.parseDouble(sc.nextLine().trim());
                int hercios = Integer.parseInt(sc.nextLine().trim());
                String resolucion = sc.nextLine();
                int stock = Integer.parseInt(sc.nextLine().trim());
                String panel = sc.nextLine();
                int tr = Integer.parseInt(sc.nextLine().trim());
                String conectividad = sc.nextLine();
                int garantia = Integer.parseInt(sc.nextLine().trim());
                productos.add(new Producto(nombre, precio, pulgadas, hercios,
                        resolucion, stock, panel, tr, conectividad, garantia));
            }
            // Configuración
            for (int i = 0; i < 4; i++) porcentajesDescuento[i] = Double.parseDouble(sc.nextLine().trim());
            for (int i = 0; i < 3; i++) obsequios[i] = sc.nextLine();
            cuotaDiaria = Double.parseDouble(sc.nextLine().trim());
            numVentas = Integer.parseInt(sc.nextLine().trim());
            importeAcumulado = Double.parseDouble(sc.nextLine().trim());
            // Líneas opcionales al final (retrocompatibles con archivos antiguos)
            if (sc.hasNextLine()) temaOscuro = Boolean.parseBoolean(sc.nextLine().trim());
            if (sc.hasNextLine()) ventasHoy = Integer.parseInt(sc.nextLine().trim());
            if (sc.hasNextLine()) fechaUltimoReset = sc.nextLine().trim();
        } catch (Exception e) {
            // Si el archivo no existe o está corrupto, se usan los valores por defecto
            System.out.println("Cargando valores por defecto...");
        }
    }

    /**
     * Guarda el estado completo en datos.txt.
     * Se llama después de cada venta, modificación o configuración.
     *
     * Sobreescribe el archivo cada vez (FileWriter sin 'true' al final).
     */
    public static void guardarDatos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constantes.ARCHIVO_DATOS))) {
            pw.println(productos.size());
            // Escribe cada producto como 10 líneas
            for (Producto p : productos) {
                pw.println(p.getNombre()); pw.println(p.getPrecio()); pw.println(p.getPulgadas());
                pw.println(p.getHercios()); pw.println(p.getResolucion()); pw.println(p.getStock());
                pw.println(p.getTipoPanel()); pw.println(p.getTiempoRespuesta());
                pw.println(p.getConectividad()); pw.println(p.getGarantiaMeses());
            }
            // Configuración
            for (double d : porcentajesDescuento) pw.println(d);
            for (String o : obsequios) pw.println(o);
            pw.println(cuotaDiaria);
            pw.println(numVentas);
            pw.println(importeAcumulado);
            pw.println(temaOscuro);
            pw.println(ventasHoy);
            pw.println(fechaUltimoReset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
