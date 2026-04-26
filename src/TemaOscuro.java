import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * CLASE TEMA (paleta + factory de componentes Swing)
 * --------------------------------------------------
 * Centraliza TODOS los estilos visuales (colores, fuentes) y funciona como FACTORY
 * de componentes Swing personalizados.
 *
 * Soporta DOS temas con estética bancaria/empresarial:
 *   - OSCURO (navy): fondos azul marino profundo + texto blanco azulado
 *   - CLARO (white):  fondos blancos/grises sutiles + texto navy
 *
 * Cómo cambiar de tema:
 *     TemaOscuro.aplicarClaro();   // cambia la paleta
 *     // ... luego recrear la ventana para que los componentes reciban los nuevos colores
 *
 * NOTA: el nombre histórico se mantiene como "TemaOscuro" para no romper imports
 * en los demás paneles, pero ahora maneja ambos modos.
 */
public class TemaOscuro {

    /** true = tema oscuro (navy), false = tema claro (white). Lo lee Tienda al iniciar. */
    public static boolean modoOscuro = true;

    // ========== PALETA DE COLORES (mutable: cambia al alternar tema) ==========
    // Tonos de fondo (de más oscuro/sobrio a más claro)
    public static Color BASE;      // fondo principal de la página
    public static Color MANTLE;    // sidebar / header
    public static Color CRUST;     // barra de estado
    public static Color SURFACE0;  // tarjetas
    public static Color SURFACE1;  // bordes sutiles, zebra de tablas
    public static Color SURFACE2;  // bordes de inputs, elementos destacados
    // Tonos de texto
    public static Color OVERLAY0;  // texto tenue (subtítulos, placeholders)
    public static Color SUBTEXT0;  // texto secundario (etiquetas)
    public static Color TEXT;      // texto principal
    // Color de marca
    public static Color ACCENT;    // azul corporativo
    public static Color ACCENT_H;  // versión hover (más clara)
    // Colores semánticos
    public static Color GREEN;     // éxito, en stock, total
    public static Color YELLOW;    // advertencia, stock bajo
    public static Color RED;       // error, sin stock, acción destructiva
    public static Color CYAN;      // información
    public static Color ORANGE;    // destacar
    // Auxiliares (antes hardcodeados)
    public static Color READONLY_BG; // fondo de campos no editables
    public static Color ZEBRA_ALT;   // fila alterna de tablas

    // Inicializar al cargar la clase (modo oscuro por defecto)
    static { aplicarOscuro(); }

    /**
     * Aplica el tema OSCURO (navy bancario).
     * Paleta inspirada en dashboards de banca corporativa: fondo azul marino
     * profundo con acentos en azul corporativo y verde jade.
     */
    public static void aplicarOscuro() {
        modoOscuro  = true;
        BASE        = new Color( 15,  26,  46); // navy profundo
        MANTLE      = new Color( 10,  20,  36); // sidebar más oscuro
        CRUST       = new Color(  6,  14,  28); // barra de estado, casi negro
        SURFACE0    = new Color( 24,  40,  65); // tarjetas (un escalón más claro que BASE)
        SURFACE1    = new Color( 33,  51,  85); // bordes/zebra
        SURFACE2    = new Color( 45,  64, 104); // bordes de inputs
        OVERLAY0    = new Color(108, 124, 153); // tenue
        SUBTEXT0    = new Color(168, 181, 204); // secundario
        TEXT        = new Color(232, 238, 247); // principal
        ACCENT      = new Color( 46, 123, 255); // azul corporativo
        ACCENT_H    = new Color( 84, 148, 255); // hover
        GREEN       = new Color(  0, 179, 107); // verde jade (banca)
        YELLOW      = new Color(232, 179,  57); // dorado tenue
        RED         = new Color(224,  64,  80); // rojo corporativo
        CYAN        = new Color( 74, 144, 226);
        ORANGE      = new Color(230, 126,  34);
        READONLY_BG = new Color( 20,  34,  58); // ligeramente más oscuro que SURFACE0
        ZEBRA_ALT   = new Color( 31,  51,  84); // ligeramente más claro que SURFACE0
    }

    /**
     * Aplica el tema CLARO (white bancario).
     * Estética de banca empresarial moderna: fondos blancos/grises muy sutiles,
     * texto navy oscuro, mismo azul corporativo de marca.
     */
    public static void aplicarClaro() {
        modoOscuro  = false;
        BASE        = new Color(245, 247, 250); // gris-azulado muy claro (fondo)
        MANTLE      = new Color(236, 240, 247); // sidebar (un poco más gris que BASE)
        CRUST       = new Color(220, 227, 237); // barra de estado
        SURFACE0    = new Color(255, 255, 255); // tarjetas blancas (pop sobre el fondo)
        SURFACE1    = new Color(225, 231, 240); // bordes/zebra
        SURFACE2    = new Color(189, 201, 217); // bordes de inputs
        OVERLAY0    = new Color(124, 139, 163); // tenue
        SUBTEXT0    = new Color( 86,  98, 122); // secundario
        TEXT        = new Color( 26,  37,  64); // navy dark (principal)
        ACCENT      = new Color( 30,  95, 191); // mismo azul corporativo (más profundo)
        ACCENT_H    = new Color( 46, 115, 214); // hover
        GREEN       = new Color(  0, 135,  90); // verde jade más oscuro (legible en claro)
        YELLOW      = new Color(176, 122,  15); // dorado oscuro
        RED         = new Color(201,  43,  54); // rojo corporativo más oscuro
        CYAN        = new Color( 31, 119, 180);
        ORANGE      = new Color(194, 107,  26);
        READONLY_BG = new Color(240, 244, 250); // gris muy suave (deshabilitado visible)
        ZEBRA_ALT   = new Color(245, 247, 250); // alternancia tenue
    }

    /** Alterna entre oscuro y claro. */
    public static void cambiar() {
        if (modoOscuro) aplicarClaro();
        else aplicarOscuro();
    }

    // ========== FUENTES (no cambian con el tema) ==========
    public static final Font FONT_XS     = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font FONT_SM     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BASE   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MD     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BOLD_SM= new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_H2     = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_H1     = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 11);

    /** Color de texto adecuado para escribir SOBRE un fondo claro (legible en ambos temas). */
    private static Color textoSobreClaro() {
        return modoOscuro ? CRUST : TEXT;
    }

    // ========== BOTONES ==========
    public static JButton crearBoton(String texto, Color bg) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        // Texto contrastante: si el fondo es claro → texto oscuro; si es oscuro → blanco
        btn.setForeground(esClaro(bg) ? textoSobreClaro() : Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(120, 34));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color hover = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    public static JButton crearBotonSidebar(String texto, boolean activo) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BASE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setPreferredSize(new Dimension(200, 36));
        btn.setMaximumSize(new Dimension(220, 36));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 20, 0, 10));

        if (activo) {
            btn.setBackground(SURFACE0);
            btn.setForeground(ACCENT);
            btn.setOpaque(true);
        } else {
            btn.setBackground(MANTLE);
            btn.setForeground(SUBTEXT0);
            btn.setOpaque(true);
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.getForeground().equals(ACCENT)) {
                    btn.setBackground(SURFACE0);
                    btn.setForeground(TEXT);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!btn.getForeground().equals(ACCENT)) {
                    btn.setBackground(MANTLE);
                    btn.setForeground(SUBTEXT0);
                }
            }
        });
        return btn;
    }

    public static void marcarBotonActivo(JButton btn) {
        btn.setBackground(SURFACE0);
        btn.setForeground(ACCENT);
    }

    public static void marcarBotonInactivo(JButton btn) {
        btn.setBackground(MANTLE);
        btn.setForeground(SUBTEXT0);
    }

    // ========== LABELS ==========
    public static JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(TEXT);
        return lbl;
    }

    public static JLabel crearLabelPequeno(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SM);
        lbl.setForeground(SUBTEXT0);
        return lbl;
    }

    public static JLabel crearValorLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(TEXT);
        return lbl;
    }

    // ========== CAMPOS DE TEXTO ==========
    public static JTextField crearCampoTexto(boolean editable) {
        JTextField txt = new JTextField();
        txt.setEditable(editable);
        txt.setFont(FONT_BASE);
        txt.setPreferredSize(new Dimension(190, 32));
        txt.setBackground(editable ? SURFACE0 : READONLY_BG);
        txt.setForeground(TEXT);
        txt.setCaretColor(TEXT);
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(editable ? SURFACE2 : SURFACE1, 1, true),
            new EmptyBorder(4, 10, 4, 10)));

        if (editable) {
            txt.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent e) {
                    txt.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT, 1, true),
                        new EmptyBorder(4, 10, 4, 10)));
                }
                public void focusLost(java.awt.event.FocusEvent e) {
                    txt.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(SURFACE2, 1, true),
                        new EmptyBorder(4, 10, 4, 10)));
                }
            });
        }
        return txt;
    }

    // ========== TARJETAS ==========
    public static JPanel crearTarjeta(String titulo) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE0);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                // En modo claro: borde sutil para que la tarjeta blanca se separe del fondo
                if (!modoOscuro) {
                    g2.setColor(SURFACE1);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(14, 16, 14, 16));

        if (titulo != null && !titulo.isEmpty()) {
            JLabel lblTit = new JLabel(titulo);
            lblTit.setFont(FONT_BOLD_SM);
            lblTit.setForeground(OVERLAY0);
            tarjeta.add(lblTit, BorderLayout.NORTH);
        }
        return tarjeta;
    }

    public static JPanel crearStatCard(String titulo, String valor, Color acento) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE0);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                if (!modoOscuro) {
                    g2.setColor(SURFACE1);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                // Franja vertical de 4px del color de acento
                g2.setColor(acento);
                g2.fillRect(0, 0, 4, getHeight());
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 18, 14, 14));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_XS);
        lblTitulo.setForeground(OVERLAY0);
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(4));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(FONT_H2);
        lblValor.setForeground(acento);
        card.add(lblValor);

        return card;
    }

    // ========== FORMULARIOS ==========
    public static JTextField crearFilaFormulario(JPanel panel, GridBagConstraints gbc,
                                                  int fila, String label, boolean editable) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BASE);
        lbl.setForeground(SUBTEXT0);
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.4;
        panel.add(lbl, gbc);

        JTextField txt = crearCampoTexto(editable);
        txt.setPreferredSize(new Dimension(180, 32));
        gbc.gridx = 1; gbc.weightx = 0.6;
        panel.add(txt, gbc);
        return txt;
    }

    // ========== DIÁLOGO ==========
    public static void aplicarTemaDialogo(JDialog dialogo, String titulo, int ancho, int alto) {
        dialogo.setTitle(titulo);
        dialogo.setSize(ancho, alto);
        dialogo.setResizable(false);
        dialogo.getContentPane().setBackground(BASE);
        dialogo.getContentPane().setLayout(new BorderLayout(0, 0));
    }

    // ========== TABLA ==========
    public static void aplicarTemaTabla(JTable tabla) {
        tabla.setBackground(SURFACE0);
        tabla.setForeground(TEXT);
        tabla.setSelectionBackground(ACCENT);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(SURFACE1);
        tabla.setFont(FONT_BASE);
        tabla.setRowHeight(34);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? SURFACE0 : ZEBRA_ALT);
                    setForeground(TEXT);
                }
                return this;
            }
        };
        tabla.setDefaultRenderer(Object.class, renderer);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(MANTLE);
        header.setForeground(ACCENT);
        header.setFont(FONT_BOLD_SM);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBackground(MANTLE);
                setForeground(ACCENT);
                setFont(FONT_BOLD_SM);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, SURFACE1),
                    new EmptyBorder(0, 12, 0, 12)));
                return this;
            }
        });
    }

    public static JScrollPane crearScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BASE);
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = SURFACE2;
                trackColor = MANTLE;
            }
            @Override protected JButton createDecreaseButton(int o) { return btnVacio(); }
            @Override protected JButton createIncreaseButton(int o) { return btnVacio(); }
            private JButton btnVacio() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        return sp;
    }

    // ========== PROGRESS BAR ==========
    public static JProgressBar crearProgressBar() {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setFont(FONT_SM);
        bar.setBackground(SURFACE1);
        bar.setForeground(ACCENT);
        bar.setBorder(null);
        bar.setPreferredSize(new Dimension(0, 18));

        bar.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth(), h = c.getHeight();
                g2.setColor(SURFACE1);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
                int fw = (int) (w * bar.getPercentComplete());
                if (fw > 0) {
                    g2.setColor(bar.getForeground());
                    g2.fill(new RoundRectangle2D.Float(0, 0, fw, h, h, h));
                }
                String s = bar.getString();
                if (s != null) {
                    g2.setFont(bar.getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    // En la barra: si la parte rellena cubre el centro, texto blanco;
                    // si no, texto principal del tema (legible en ambos modos)
                    g2.setColor(fw > w / 2 ? Color.WHITE : TEXT);
                    g2.drawString(s, (w - fm.stringWidth(s)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
                }
                g2.dispose();
            }
        });
        return bar;
    }

    // ========== HEADER DE PÁGINA ==========
    public static JPanel crearHeaderPagina(String titulo, String subtitulo) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_H2);
        lblTitulo.setForeground(TEXT);
        header.add(lblTitulo, BorderLayout.WEST);

        if (subtitulo != null) {
            JLabel lblSub = new JLabel(subtitulo);
            lblSub.setFont(FONT_SM);
            lblSub.setForeground(OVERLAY0);
            header.add(lblSub, BorderLayout.EAST);
        }
        return header;
    }

    // ========== UTILIDADES ==========
    public static JSeparator crearSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(SURFACE1);
        sep.setBackground(SURFACE1);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    /**
     * Determina si un color es CLARO u OSCURO usando luminancia perceptual (ITU-R BT.601).
     *   Y = 0.299 R + 0.587 G + 0.114 B
     * Si Y > 150 → claro → conviene usar texto oscuro encima.
     */
    private static boolean esClaro(Color c) {
        return (c.getRed() * 299 + c.getGreen() * 587 + c.getBlue() * 114) / 1000 > 150;
    }
}
