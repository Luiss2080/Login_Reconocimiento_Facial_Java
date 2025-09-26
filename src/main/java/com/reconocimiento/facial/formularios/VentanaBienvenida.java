package com.reconocimiento.facial.formularios;

import com.reconocimiento.facial.modelos.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Ventana de bienvenida del sistema después del login exitoso
 * Muestra información del usuario y opciones básicas
 */
public class VentanaBienvenida extends javax.swing.JFrame {

    // ========== COLORES MODERNOS ========== 
    // Colores disponibles para futuras mejoras de diseño
    @SuppressWarnings("unused") private static final Color COLOR_PRIMARIO = new Color(52, 73, 94);
    @SuppressWarnings("unused") private static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    @SuppressWarnings("unused") private static final Color COLOR_FONDO = new Color(236, 240, 241);
    @SuppressWarnings("unused") private static final Color COLOR_BLANCO = new Color(255, 255, 255);
    @SuppressWarnings("unused") private static final Color COLOR_TEXTO = new Color(44, 62, 80);
    @SuppressWarnings("unused") private static final Color COLOR_EXITO = new Color(46, 204, 113);
    @SuppressWarnings("unused") private static final Color COLOR_ERROR = new Color(231, 76, 60);

    // Variables de formulario
    private javax.swing.JLabel lblTituloBienvenida;
    private javax.swing.JLabel lblNombreUsuario;
    private javax.swing.JLabel lblFechaHora;
    private javax.swing.JLabel lblEstadoSesion;
    private javax.swing.JLabel lblMetodoAcceso;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnConfiguracion;
    private javax.swing.JButton btnAyuda;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelInformacion;
    private javax.swing.JPanel panelBotones;

    // Usuario actual
    private Usuario usuarioActual;
    private Timer timerReloj;

    /**
     * Constructor de la ventana de bienvenida
     */
    public VentanaBienvenida(Usuario usuario) {
        this.usuarioActual = usuario;

        try {
            // Configurar look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            initComponents();
            configurarVentana();
            iniciarReloj();

        } catch (Exception e) {
            mostrarError("Error al inicializar ventana de bienvenida: " + e.getMessage());
        }
    }

    /**
     * Inicializar componentes de la ventana
     */
    private void initComponents() {
        // Configuración de la ventana principal
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Reconocimiento Facial - Panel Principal");
        setResizable(false);

        // Panel principal
        panelPrincipal = new javax.swing.JPanel();
        panelPrincipal.setBackground(new Color(245, 248, 250));
        panelPrincipal.setLayout(new BorderLayout(30, 30));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Título de bienvenida
        lblTituloBienvenida = new javax.swing.JLabel();
        lblTituloBienvenida.setText("🎉 ¡BIENVENIDO AL SISTEMA!");
        lblTituloBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTituloBienvenida.setForeground(new Color(34, 139, 34));
        lblTituloBienvenida.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel de información del usuario
        panelInformacion = new javax.swing.JPanel();
        panelInformacion.setBackground(Color.WHITE);
        panelInformacion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 240), 2),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        panelInformacion.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.CENTER;

        // Nombre del usuario
        lblNombreUsuario = new javax.swing.JLabel();
        lblNombreUsuario.setText("👤 " + usuarioActual.getNombreCompleto());
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNombreUsuario.setForeground(new Color(25, 118, 210));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelInformacion.add(lblNombreUsuario, gbc);

        // Separador visual
        JSeparator separador = new JSeparator();
        separador.setPreferredSize(new Dimension(300, 1));
        separador.setForeground(new Color(224, 224, 224));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelInformacion.add(separador, gbc);

        // Información adicional
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        // Usuario
        JLabel lblUsuarioTitulo = new JLabel("● Usuario:");
        lblUsuarioTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        panelInformacion.add(lblUsuarioTitulo, gbc);

        JLabel lblUsuarioValor = new JLabel(usuarioActual.getNombreUsuario());
        lblUsuarioValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2;
        panelInformacion.add(lblUsuarioValor, gbc);

        // Correo
        JLabel lblCorreoTitulo = new JLabel("📧 Correo:");
        lblCorreoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        panelInformacion.add(lblCorreoTitulo, gbc);

        JLabel lblCorreoValor = new JLabel(usuarioActual.getCorreoElectronico());
        lblCorreoValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 3;
        panelInformacion.add(lblCorreoValor, gbc);

        // Estado de la sesión
        JLabel lblEstadoTitulo = new JLabel("🔐 Estado:");
        lblEstadoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4;
        panelInformacion.add(lblEstadoTitulo, gbc);

        lblEstadoSesion = new JLabel("✅ Sesión Activa");
        lblEstadoSesion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEstadoSesion.setForeground(new Color(76, 175, 80));
        gbc.gridx = 1; gbc.gridy = 4;
        panelInformacion.add(lblEstadoSesion, gbc);

        // Método de acceso
        JLabel lblMetodoTitulo = new JLabel("→ Acceso:");
        lblMetodoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 5;
        panelInformacion.add(lblMetodoTitulo, gbc);

        lblMetodoAcceso = new JLabel("* Reconocimiento Facial");
        lblMetodoAcceso.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMetodoAcceso.setForeground(new Color(156, 39, 176));
        gbc.gridx = 1; gbc.gridy = 5;
        panelInformacion.add(lblMetodoAcceso, gbc);

        // Fecha y hora actual
        lblFechaHora = new javax.swing.JLabel();
        lblFechaHora.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFechaHora.setForeground(new Color(96, 125, 139));
        lblFechaHora.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelInformacion.add(lblFechaHora, gbc);

        // Panel de botones
        panelBotones = new javax.swing.JPanel();
        panelBotones.setBackground(new Color(245, 248, 250));
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        // Botón cerrar sesión - Rosa claro moderno
        btnCerrarSesion = crearBoton("X Cerrar Sesión", new Color(255, 205, 210), Color.BLACK);
        btnCerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });

        // Botón configuración - Gris claro moderno
        btnConfiguracion = crearBoton("◦ Configuración", new Color(224, 224, 224), Color.BLACK);
        btnConfiguracion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirConfiguracion();
            }
        });

        // Botón ayuda - Azul claro moderno
        btnAyuda = crearBoton("? Ayuda", new Color(187, 222, 251), Color.BLACK);
        btnAyuda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAyuda();
            }
        });

        panelBotones.add(btnConfiguracion);
        panelBotones.add(btnAyuda);
        panelBotones.add(btnCerrarSesion);

        // Agregar componentes al panel principal
        panelPrincipal.add(lblTituloBienvenida, BorderLayout.NORTH);
        panelPrincipal.add(panelInformacion, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
        pack();
    }

    /**
     * Crear botón moderno con efectos hover
     */
    private JButton crearBoton(String texto, Color colorFondo, Color colorTexto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.BLACK); // Texto negro siempre
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 40));
        
        // Bordes redondeados simulados con padding
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorFondo, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Efectos hover modernos
        Color colorHover = colorFondo.brighter();
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover);
                boton.setForeground(Color.BLACK);
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorHover, 2),
                    BorderFactory.createEmptyBorder(9, 19, 9, 19)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
                boton.setForeground(Color.BLACK);
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorFondo, 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return boton;
    }

    /**
     * Configurar ventana adicional
     */
    private void configurarVentana() {
        setLocationRelativeTo(null); // Centrar ventana

        // Agregar listener para cerrar ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cerrarSesionYSalir();
            }
        });
    }

    /**
     * Iniciar reloj en tiempo real
     */
    private void iniciarReloj() {
        timerReloj = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarFechaHora();
            }
        });

        actualizarFechaHora(); // Actualizar inmediatamente
        timerReloj.start();
    }

    /**
     * Actualizar fecha y hora
     */
    private void actualizarFechaHora() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("📅 dd/MM/yyyy  🕐 HH:mm:ss");
        lblFechaHora.setText(formatter.format(ahora));
    }

    /**
     * Cerrar sesión
     */
    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea cerrar la sesión?",
            "Confirmar Cierre de Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            cerrarSesionYSalir();
        }
    }

    /**
     * Cerrar sesión y salir
     */
    private void cerrarSesionYSalir() {
        try {
            // Detener el timer
            if (timerReloj != null) {
                timerReloj.stop();
            }

            // Mostrar mensaje de despedida
            JOptionPane.showMessageDialog(
                this,
                "¡Gracias por usar el Sistema de Reconocimiento Facial!\n" +
                "Sesión cerrada exitosamente.",
                "Hasta Pronto",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Volver al login
            FormularioLoginNuevo formularioLogin = new FormularioLoginNuevo();
            formularioLogin.setVisible(true);
            this.dispose();

        } catch (Exception e) {
            mostrarError("Error al cerrar sesión: " + e.getMessage());
        }
    }

    /**
     * Abrir configuración
     */
    private void abrirConfiguracion() {
        JOptionPane.showMessageDialog(
            this,
            "🚧 Función en desarrollo\n\n" +
            "La configuración del sistema estará disponible en futuras versiones.\n" +
            "Por ahora, puede contactar al administrador para cambios en la configuración.",
            "Configuración",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Mostrar ayuda
     */
    private void mostrarAyuda() {
        String mensajeAyuda = "🔗 SISTEMA DE RECONOCIMIENTO FACIAL\n\n" +
                             "✅ FUNCIONES DISPONIBLES:\n\n" +
                             "* Reconocimiento Facial:\n" +
                             "   • Sistema de autenticación biométrica avanzado\n" +
                             "   • Utiliza redes neuronales para identificación\n" +
                             "   • Alta precisión y seguridad\n\n" +
                             "🔐 Login con Contraseña:\n" +
                             "   • Método tradicional de autenticación\n" +
                             "   • Respaldo al reconocimiento facial\n\n" +
                             "+ Registro de Usuarios:\n" +
                             "   • Captura automática de características faciales\n" +
                             "   • Entrenamiento personalizado de red neuronal\n" +
                             "   • Validación completa de datos\n\n" +
                             "🔒 SEGURIDAD:\n" +
                             "   • Cifrado BCrypt para contraseñas\n" +
                             "   • Protección contra intentos maliciosos\n" +
                             "   • Logs de auditoría completos\n\n" +
                             "📞 SOPORTE:\n" +
                             "   • Contacte al administrador del sistema\n" +
                             "   • Email: admin@reconocimiento-facial.com";

        JTextArea areaTexto = new JTextArea(mensajeAyuda);
        areaTexto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        areaTexto.setEditable(false);
        areaTexto.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(areaTexto);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Ayuda del Sistema",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Establecer método de acceso utilizado
     */
    public void setMetodoAcceso(String metodo) {
        if (metodo.equalsIgnoreCase("FACIAL")) {
            lblMetodoAcceso.setText("* Reconocimiento Facial");
            lblMetodoAcceso.setForeground(new Color(76, 175, 80));
        } else if (metodo.equalsIgnoreCase("CONTRASEÑA")) {
            lblMetodoAcceso.setText("🔐 Contraseña");
            lblMetodoAcceso.setForeground(new Color(33, 150, 243));
        } else {
            lblMetodoAcceso.setText("🔐 " + metodo);
            lblMetodoAcceso.setForeground(new Color(96, 125, 139));
        }
    }

    /**
     * Obtener información del usuario actual
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Mostrar mensaje de error
     */
    private void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }

    /**
     * Mostrar mensaje de información
     */
    public void mostrarMensaje(String titulo, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                mensaje,
                titulo,
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    /**
     * Actualizar estado de la sesión
     */
    public void actualizarEstadoSesion(String estado, Color color) {
        SwingUtilities.invokeLater(() -> {
            lblEstadoSesion.setText(estado);
            lblEstadoSesion.setForeground(color);
        });
    }
}