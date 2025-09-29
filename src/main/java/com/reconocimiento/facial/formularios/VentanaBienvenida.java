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
        lblTituloBienvenida.setText("BIENVENIDO AL SISTEMA DE RECONOCIMIENTO FACIAL");
        lblTituloBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloBienvenida.setForeground(new Color(37, 99, 235));
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
        lblNombreUsuario.setText(usuarioActual.getNombreCompleto());
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNombreUsuario.setForeground(new Color(30, 58, 138));
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
        JLabel lblUsuarioTitulo = new JLabel("USUARIO:");
        lblUsuarioTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuarioTitulo.setForeground(new Color(75, 85, 99));
        gbc.gridx = 0; gbc.gridy = 2;
        panelInformacion.add(lblUsuarioTitulo, gbc);

        JLabel lblUsuarioValor = new JLabel(usuarioActual.getNombreUsuario());
        lblUsuarioValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2;
        panelInformacion.add(lblUsuarioValor, gbc);

        // Correo
        JLabel lblCorreoTitulo = new JLabel("CORREO ELECTRÓNICO:");
        lblCorreoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCorreoTitulo.setForeground(new Color(75, 85, 99));
        gbc.gridx = 0; gbc.gridy = 3;
        panelInformacion.add(lblCorreoTitulo, gbc);

        JLabel lblCorreoValor = new JLabel(usuarioActual.getCorreoElectronico());
        lblCorreoValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 3;
        panelInformacion.add(lblCorreoValor, gbc);

        // Estado de la sesión
        JLabel lblEstadoTitulo = new JLabel("ESTADO DE SESIÓN:");
        lblEstadoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstadoTitulo.setForeground(new Color(75, 85, 99));
        gbc.gridx = 0; gbc.gridy = 4;
        panelInformacion.add(lblEstadoTitulo, gbc);

        lblEstadoSesion = new JLabel("ACTIVA");
        lblEstadoSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstadoSesion.setForeground(new Color(34, 197, 94));
        gbc.gridx = 1; gbc.gridy = 4;
        panelInformacion.add(lblEstadoSesion, gbc);

        // Método de acceso
        JLabel lblMetodoTitulo = new JLabel("MÉTODO DE ACCESO:");
        lblMetodoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMetodoTitulo.setForeground(new Color(75, 85, 99));
        gbc.gridx = 0; gbc.gridy = 5;
        panelInformacion.add(lblMetodoTitulo, gbc);

        lblMetodoAcceso = new JLabel("RECONOCIMIENTO FACIAL");
        lblMetodoAcceso.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMetodoAcceso.setForeground(new Color(147, 51, 234));
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

        // Botón cerrar sesión - Rojo moderno
        btnCerrarSesion = crearBoton("CERRAR SESIÓN", new Color(239, 68, 68), Color.BLACK);
        btnCerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });

        // Botón configuración - Gris moderno
        btnConfiguracion = crearBoton("CONFIGURACIÓN", new Color(156, 163, 175), Color.BLACK);
        btnConfiguracion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirConfiguracion();
            }
        });

        // Botón ayuda - Azul moderno
        btnAyuda = crearBoton("AYUDA", new Color(59, 130, 246), Color.BLACK);
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
        boton.setForeground(colorTexto);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(180, 45));
        
        // Bordes redondeados simulados con padding
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorFondo, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Efectos hover modernos
        Color colorHover = colorFondo.darker();
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover);
                boton.setForeground(colorTexto);
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorHover, 2),
                    BorderFactory.createEmptyBorder(9, 19, 9, 19)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
                boton.setForeground(colorTexto);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
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
                "Gracias por usar el Sistema de Reconocimiento Facial.\n\n" +
                "Su sesión ha sido cerrada exitosamente.\n" +
                "Todos los datos han sido guardados de forma segura.",
                "Sesión Finalizada",
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
            "CONFIGURACIÓN DEL SISTEMA\n\n" +
            "═══════════════════════════════════════════════════════════\n\n" +
            "Esta función se encuentra actualmente en desarrollo.\n\n" +
            "La configuración avanzada del sistema estará disponible\n" +
            "en futuras versiones del software.\n\n" +
            "Para realizar cambios en la configuración, contacte\n" +
            "al administrador del sistema.\n\n" +
            "Estado: EN DESARROLLO\n" +
            "Versión disponible: Próxima actualización",
            "Configuración del Sistema",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Mostrar ayuda
     */
    private void mostrarAyuda() {
        String mensajeAyuda = "SISTEMA DE RECONOCIMIENTO FACIAL v2.0\n\n" +
                             "═══════════════════════════════════════════════════════════\n\n" +
                             "FUNCIONES DISPONIBLES:\n\n" +
                             "• RECONOCIMIENTO FACIAL:\n" +
                             "   - Sistema de autenticación biométrica avanzado\n" +
                             "   - Utiliza algoritmos de comparación de imágenes\n" +
                             "   - Alta precisión y seguridad en la identificación\n" +
                             "   - Procesamiento en tiempo real\n\n" +
                             "• AUTENTICACIÓN POR CONTRASEÑA:\n" +
                             "   - Método tradicional de autenticación\n" +
                             "   - Sistema de respaldo al reconocimiento facial\n" +
                             "   - Validación de credenciales segura\n\n" +
                             "• REGISTRO DE USUARIOS:\n" +
                             "   - Captura automática de fotografías faciales\n" +
                             "   - Almacenamiento seguro de características biométricas\n" +
                             "   - Validación completa de datos de usuario\n" +
                             "   - Configuración de múltiples muestras faciales\n\n" +
                             "CARACTERÍSTICAS DE SEGURIDAD:\n\n" +
                             "• Cifrado BCrypt para contraseñas\n" +
                             "• Protección contra ataques de fuerza bruta\n" +
                             "• Sistema completo de auditoría y logs\n" +
                             "• Validación de integridad de datos\n" +
                             "• Control de acceso multinivel\n\n" +
                             "SOPORTE TÉCNICO:\n\n" +
                             "• Administrador del Sistema\n" +
                             "• Email: admin@reconocimiento-facial.com\n" +
                             "• Versión: 2.0.0 - Actualizado 2025";

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
            lblMetodoAcceso.setText("RECONOCIMIENTO FACIAL");
            lblMetodoAcceso.setForeground(new Color(147, 51, 234));
        } else if (metodo.equalsIgnoreCase("CONTRASEÑA")) {
            lblMetodoAcceso.setText("CONTRASEÑA");
            lblMetodoAcceso.setForeground(new Color(59, 130, 246));
        } else {
            lblMetodoAcceso.setText(metodo.toUpperCase());
            lblMetodoAcceso.setForeground(new Color(75, 85, 99));
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