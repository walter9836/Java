import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DIÁLOGO ACERCA DE
 * -----------------
 * Ventana modal que muestra información de la aplicación:
 *   - Logo (emoji de monitor 🖥️)
 *   - Nombre y versión
 *   - Lista de autores
 *   - Curso e institución
 *   - Botón Cerrar
 *
 * ¿Por qué JDialog y no JFrame?
 * JDialog está pensado para ventanas "hijas" que se abren sobre una ventana
 * principal. Es modal por defecto → bloquea la ventana padre hasta cerrar.
 *
 * Se usa BoxLayout vertical para apilar los elementos centrados.
 */
public class AcercaDe extends JDialog {
    private static final long serialVersionUID = 1L;

    public AcercaDe() {
        // Configuración básica del diálogo (del helper de TemaOscuro)
        TemaOscuro.aplicarTemaDialogo(this, "Acerca de", 460, 480);

        // Panel principal con BoxLayout vertical (apila todo centrado)
        JPanel main = new JPanel();
        main.setBackground(TemaOscuro.BASE);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(25, 40, 20, 40));

        // --- ÍCONO: emoji de monitor ---
        // \uD83D\uDDA5 es el código unicode del emoji 🖥️ (par surrogate para emojis)
        JLabel lblIcono = new JLabel("\uD83D\uDDA5", SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT); // centrar en BoxLayout
        main.add(lblIcono);
        main.add(Box.createVerticalStrut(10));

        // --- Nombre de la app ---
        JLabel lblNombre = new JLabel("GamerStore");
        lblNombre.setFont(TemaOscuro.FONT_H1);
        lblNombre.setForeground(TemaOscuro.ACCENT);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(lblNombre);

        // --- Subtítulo ---
        JLabel lblSub = new JLabel("Sistema de Ventas - Monitores Gamer");
        lblSub.setFont(TemaOscuro.FONT_BASE);
        lblSub.setForeground(TemaOscuro.SUBTEXT0);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(lblSub);

        // --- Versión ---
        JLabel lblVer = new JLabel("Versión 2.0");
        lblVer.setFont(TemaOscuro.FONT_SM);
        lblVer.setForeground(TemaOscuro.OVERLAY0);
        lblVer.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(lblVer);

        main.add(Box.createVerticalStrut(20));
        main.add(TemaOscuro.crearSeparador());
        main.add(Box.createVerticalStrut(15));

        // --- Sección: EQUIPO DE DESARROLLO ---
        JLabel lblSeccion = new JLabel("EQUIPO DE DESARROLLO");
        lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSeccion.setForeground(TemaOscuro.ACCENT);
        lblSeccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(lblSeccion);
        main.add(Box.createVerticalStrut(10));

        // Lista de los 4 integrantes del grupo
        agregarAutor(main, "Walter Eduardo Alvarado Guerrero");
        agregarAutor(main, "Erick Jorge Alcántara Castañeda");
        agregarAutor(main, "Delgado Ramos Miguel Edu");
        agregarAutor(main, "Luis Axel Delgado Ramos");

        main.add(Box.createVerticalStrut(15));
        main.add(TemaOscuro.crearSeparador());
        main.add(Box.createVerticalStrut(12));

        // --- Sección: CURSO ---
        JLabel lblCursoTit = new JLabel("CURSO");
        lblCursoTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblCursoTit.setForeground(TemaOscuro.ACCENT);
        lblCursoTit.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(lblCursoTit);
        main.add(Box.createVerticalStrut(4));

        JLabel lblCurso = new JLabel("Introducción a la Algoritmia");
        lblCurso.setFont(TemaOscuro.FONT_MD);
        lblCurso.setForeground(TemaOscuro.TEXT);
        lblCurso.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(lblCurso);

        JLabel lblInst = new JLabel("Cibertec - Grupo 12");
        lblInst.setFont(TemaOscuro.FONT_SM);
        lblInst.setForeground(TemaOscuro.OVERLAY0);
        lblInst.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(lblInst);

        getContentPane().add(main, BorderLayout.CENTER);

        // --- Footer con botón Cerrar ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        footer.setBackground(TemaOscuro.BASE);
        footer.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton btnCerrar = TemaOscuro.crearBoton("Cerrar", TemaOscuro.ACCENT);
        // dispose() cierra y libera los recursos del diálogo
        btnCerrar.addActionListener(e -> dispose());
        footer.add(btnCerrar);

        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    /**
     * Helper para agregar un nombre de autor con viñeta (●).
     * Evita repetir 5 líneas por cada autor.
     * \u25CF es el código unicode del círculo negro ●
     */
    private void agregarAutor(JPanel panel, String nombre) {
        JLabel lbl = new JLabel("\u25CF  " + nombre);
        lbl.setFont(TemaOscuro.FONT_BASE);
        lbl.setForeground(TemaOscuro.SUBTEXT0);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(3));
    }
}
