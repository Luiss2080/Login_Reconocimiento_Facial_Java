package com.reconocimiento.facial.formularios;

import com.reconocimiento.facial.neural.RedNeuronalReconocimiento;
import com.reconocimiento.facial.procesamiento.ManejadorCamara;
import com.reconocimiento.facial.servicios.ServicioUsuarioMejorado;
import com.reconocimiento.facial.modelos.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * 🎯 FORMULARIO PRINCIPAL DE AUTENTICACIÓN
 * Sistema dual: Credenciales tradicionales + Reconocimiento Facial
 * Diseño moderno e intuitivo para experiencia de usuario óptima
 */
public class FormularioLoginNuevo extends JFrame {

    // ========== COMPONENTES DE INTERFAZ ==========
    private JPanel panelPrincipal;
    private JPanel panelTitulo;
    private JPanel panelOpciones;
    private JPanel panelCredenciales;
    private JPanel panelFacial;
    private JPanel panelBotones;
    
    // Componentes de credenciales
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLoginCredenciales;
    
    // Componentes de reconocimiento facial
    private JLabel lblCamara;
    private JLabel lblVistaPrevia; // Nueva: Para mostrar video en tiempo real
    private JButton btnActivarCamara;
    private JButton btnLoginFacial;
    private JPanel panelVideoContainer; // Contenedor del video
    private Timer videoTimer; // Timer para actualizar frames
    
    // Componentes generales
    private JLabel lblTitulo;
    private JLabel lblSubtitulo;
    private JLabel lblEstado;
    private JButton btnRegistrarse;
    private JButton btnSalir;
    private JButton btnAyuda;

    // ========== SERVICIOS Y LÓGICA ==========
    private ServicioUsuarioMejorado servicioUsuario;
    private RedNeuronalReconocimiento redNeuronal;
    private ManejadorCamara manejadorCamara;
    private BufferedImage imagenCapturada;
    private boolean camaraActiva = false;

    // ========== CONSTANTES DE DISEÑO PROFESIONAL ==========
    private static final Color COLOR_PRINCIPAL = new Color(52, 73, 94);      // Azul oscuro profesional
    @SuppressWarnings("unused") private static final Color COLOR_ACENTO = new Color(41, 128, 185);       // Azul claro
    private static final Color COLOR_EXITO = new Color(39, 174, 96);         // Verde
    private static final Color COLOR_FONDO = new Color(248, 249, 250);       // Gris muy claro
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_TEXTO = new Color(44, 62, 80);          // Texto oscuro
    private static final Color COLOR_BORDE = new Color(189, 195, 199);       // Borde gris
    
    private static final Font FONT_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    @SuppressWarnings("unused") private static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Constructor principal - Inicializa toda la interfaz y servicios
     */
    public FormularioLoginNuevo() {
        try {
            // Configurar Look & Feel moderno
            configurarApariencia();
            
            // Inicializar servicios
            inicializarServicios();
            
            // Crear interfaz
            inicializarComponentes();
            configurarLayout();
            configurarEventos();
            
            // Configuración final de la ventana
            configurarVentana();
            
            mostrarMensajeBienvenida();
            
        } catch (Exception e) {
            mostrarError("Error inicializando aplicación: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Configurar apariencia moderna y profesional
     */
    private void configurarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo configurar apariencia: " + e.getMessage());
        }
    }

    /**
     * Inicializar todos los servicios necesarios
     */
    private void inicializarServicios() {
        try {
            this.servicioUsuario = new ServicioUsuarioMejorado();
            this.redNeuronal = new RedNeuronalReconocimiento();
            this.manejadorCamara = new ManejadorCamara();
            
            System.out.println("Servicios inicializados correctamente");
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando servicios: " + e.getMessage(), e);
        }
    }

    /**
     * Inicializar todos los componentes de la interfaz con diseño profesional
     */
    private void inicializarComponentes() {
        // Panel principal con diseño moderno
        panelPrincipal = new JPanel();
        panelPrincipal.setBackground(COLOR_FONDO);
        
        // Panel de título con estilo corporativo
        panelTitulo = new JPanel();
        panelTitulo.setBackground(COLOR_PRINCIPAL);
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        
        lblTitulo = new JLabel("SISTEMA DE AUTENTICACION");
        lblTitulo.setFont(FONT_TITULO);
        lblTitulo.setForeground(COLOR_BLANCO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblSubtitulo = new JLabel("Acceso Seguro | Reconocimiento Facial y Credenciales");
        lblSubtitulo.setFont(FONT_SUBTITULO);
        lblSubtitulo.setForeground(new Color(236, 240, 241));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel de opciones principales con espaciado profesional
        panelOpciones = new JPanel();
        panelOpciones.setBackground(COLOR_FONDO);
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Panel de credenciales renovado
        crearPanelCredenciales();
        
        // Panel facial renovado
        crearPanelFacial();
        
        // Panel de botones renovado
        crearPanelBotones();
        
        // Estado con diseño limpio
        lblEstado = new JLabel("Seleccione su metodo de autenticacion preferido");
        lblEstado.setFont(FONT_SUBTITULO);
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setForeground(COLOR_TEXTO);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    }

    /**
     * Crear panel de autenticación por credenciales con diseño profesional
     */
    private void crearPanelCredenciales() {
        panelCredenciales = new JPanel();
        panelCredenciales.setBackground(COLOR_BLANCO);
        panelCredenciales.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel lblTituloCredenciales = new JLabel("Acceso con Credenciales");
        lblTituloCredenciales.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloCredenciales.setForeground(COLOR_PRINCIPAL);
        lblTituloCredenciales.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(FONT_LABEL);
        lblUsuario.setForeground(COLOR_TEXTO);
        
        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(280, 35));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(FONT_LABEL);
        lblContrasena.setForeground(COLOR_TEXTO);
        
        txtContrasena = new JPasswordField();
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContrasena.setPreferredSize(new Dimension(280, 35));
        txtContrasena.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        btnLoginCredenciales = crearBotonLogin("🔐 INGRESAR", 
            COLOR_PRINCIPAL, new Color(25, 42, 67));
        
        // Layout del panel credenciales con mejor espaciado
        panelCredenciales.setLayout(new BoxLayout(panelCredenciales, BoxLayout.Y_AXIS));
        panelCredenciales.add(lblTituloCredenciales);
        panelCredenciales.add(Box.createVerticalStrut(25));
        panelCredenciales.add(lblUsuario);
        panelCredenciales.add(Box.createVerticalStrut(8));
        panelCredenciales.add(txtUsuario);
        panelCredenciales.add(Box.createVerticalStrut(15));
        panelCredenciales.add(lblContrasena);
        panelCredenciales.add(Box.createVerticalStrut(8));
        panelCredenciales.add(txtContrasena);
        panelCredenciales.add(Box.createVerticalStrut(25));
        panelCredenciales.add(btnLoginCredenciales);
        
        // Centrar todos los componentes
        lblTituloCredenciales.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtContrasena.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLoginCredenciales.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Crear panel de reconocimiento facial moderno con vista previa
     */
    private void crearPanelFacial() {
        panelFacial = new JPanel();
        panelFacial.setBackground(COLOR_BLANCO);
        panelFacial.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2), // Borde azul moderno
            BorderFactory.createEmptyBorder(30, 25, 25, 25)
        ));
        panelFacial.setLayout(new BoxLayout(panelFacial, BoxLayout.Y_AXIS));
        
        // Título con icono y estilo moderno
        JLabel lblTituloFacial = new JLabel("* Reconocimiento Facial");
        lblTituloFacial.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloFacial.setForeground(new Color(52, 152, 219));
        lblTituloFacial.setHorizontalAlignment(SwingConstants.CENTER);
        lblTituloFacial.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Contenedor de video con diseño moderno
        crearContenedorVideo();
        
        // Panel de controles con mejor distribución
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBackground(COLOR_BLANCO);
        panelControles.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botón activar cámara con diseño moderno
        btnActivarCamara = new JButton("▶ ACTIVAR CÁMARA");
        btnActivarCamara.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActivarCamara.setBackground(new Color(187, 222, 251)); // Azul muy claro
        btnActivarCamara.setForeground(Color.BLACK); // Texto negro
        btnActivarCamara.setPreferredSize(new Dimension(300, 45));
        btnActivarCamara.setMaximumSize(new Dimension(300, 45));
        btnActivarCamara.setFocusPainted(false);
        btnActivarCamara.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnActivarCamara.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActivarCamara.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Efecto hover para el botón
        btnActivarCamara.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btnActivarCamara.isEnabled()) {
                    btnActivarCamara.setBackground(new Color(144, 202, 249)); // Azul claro
                    btnActivarCamara.setForeground(Color.BLACK); // Texto negro
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btnActivarCamara.isEnabled() && !btnActivarCamara.getText().contains("ACTIVA")) {
                    btnActivarCamara.setBackground(new Color(187, 222, 251)); // Azul muy claro
                    btnActivarCamara.setForeground(Color.BLACK); // Texto negro
                }
            }
        });
        
        // Botón reconocimiento con diseño premium
        btnLoginFacial = new JButton("● RECONOCER ROSTRO");
        btnLoginFacial.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoginFacial.setBackground(new Color(200, 230, 201)); // Verde muy claro
        btnLoginFacial.setForeground(Color.BLACK); // Texto negro
        btnLoginFacial.setPreferredSize(new Dimension(300, 45));
        btnLoginFacial.setMaximumSize(new Dimension(300, 45));
        btnLoginFacial.setFocusPainted(false);
        btnLoginFacial.setEnabled(false);
        btnLoginFacial.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnLoginFacial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLoginFacial.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Efecto hover para botón reconocimiento
        btnLoginFacial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btnLoginFacial.isEnabled()) {
                    btnLoginFacial.setBackground(new Color(165, 214, 167)); // Verde claro
                    btnLoginFacial.setForeground(Color.BLACK); // Texto negro
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btnLoginFacial.isEnabled()) {
                    btnLoginFacial.setBackground(new Color(200, 230, 201)); // Verde muy claro
                    btnLoginFacial.setForeground(Color.BLACK); // Texto negro
                }
            }
        });
        
        // Agregar componentes con espaciado compacto
        panelFacial.add(lblTituloFacial);
        panelFacial.add(Box.createVerticalStrut(15));
        panelFacial.add(panelVideoContainer);
        panelFacial.add(Box.createVerticalStrut(15));
        
        panelControles.add(btnActivarCamara);
        panelControles.add(Box.createVerticalStrut(10));
        panelControles.add(btnLoginFacial);
        
        panelFacial.add(panelControles);
        panelFacial.add(Box.createVerticalStrut(10));
    }
    
    /**
     * Crear contenedor de video compacto y responsivo
     */
    private void crearContenedorVideo() {
        panelVideoContainer = new JPanel(new BorderLayout());
        panelVideoContainer.setPreferredSize(new Dimension(280, 180));
        panelVideoContainer.setMaximumSize(new Dimension(280, 180));
        panelVideoContainer.setMinimumSize(new Dimension(280, 180));
        panelVideoContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 165, 166), 2),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        panelVideoContainer.setBackground(new Color(44, 62, 80));
        panelVideoContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Vista previa de video
        lblVistaPrevia = new JLabel();
        lblVistaPrevia.setHorizontalAlignment(SwingConstants.CENTER);
        lblVistaPrevia.setVerticalAlignment(SwingConstants.CENTER);
        lblVistaPrevia.setOpaque(true);
        lblVistaPrevia.setBackground(new Color(44, 62, 80));
        lblVistaPrevia.setForeground(Color.WHITE);
        lblVistaPrevia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Estado inicial
        mostrarEstadoCamara("* Cámara Desconectada", new Color(231, 76, 60));
        
        panelVideoContainer.add(lblVistaPrevia, BorderLayout.CENTER);
        
        // Inicializar timer para actualización de video
        inicializarTimerVideo();
    }
    
    /**
     * Mostrar estado de la cámara con estilo visual
     */
    private void mostrarEstadoCamara(String mensaje, Color color) {
        lblVistaPrevia.setText("<html><center>" + mensaje + "</center></html>");
        lblVistaPrevia.setBackground(color);
        lblVistaPrevia.setIcon(null);
    }
    
    /**
     * Inicializar timer para actualización de video en tiempo real
     */
    private void inicializarTimerVideo() {
        videoTimer = new Timer(33, e -> { // ~30 FPS
            if (camaraActiva && manejadorCamara != null) {
                try {
                    BufferedImage frame = manejadorCamara.capturarImagenBuffered();
                    if (frame != null) {
                        // Redimensionar imagen para el contenedor compacto
                        Image imagenEscalada = frame.getScaledInstance(274, 174, Image.SCALE_SMOOTH);
                        ImageIcon icono = new ImageIcon(imagenEscalada);
                        
                        SwingUtilities.invokeLater(() -> {
                            lblVistaPrevia.setIcon(icono);
                            lblVistaPrevia.setText("");
                        });
                    }
                } catch (Exception ex) {
                    // Si hay error, no actualizar frame pero mantener el timer
                }
            }
        });
    }
    
    /**
     * Iniciar stream de video
     */
    private void iniciarStreamVideo() {
        if (videoTimer != null && !videoTimer.isRunning()) {
            videoTimer.start();
            mostrarEstadoCamara("* Transmisión Activa", new Color(46, 204, 113));
        }
    }
    
    /**
     * Detener stream de video  
     */
    private void detenerStreamVideo() {
        if (videoTimer != null && videoTimer.isRunning()) {
            videoTimer.stop();
        }
        mostrarEstadoCamara("* Cámara Desconectada", new Color(231, 76, 60));
        lblCamara.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnActivarCamara.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLoginFacial.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Crear panel de botones modernos con iconos y efectos
     */
    private void crearPanelBotones() {
        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(25, 0, 35, 0));
        
        // Botón Registrarse moderno - Amarillo muy claro
        btnRegistrarse = crearBotonModerno("+ Registrarse", 
            new Color(255, 235, 59), new Color(255, 213, 79)); // Amarillo muy claro
        
        // Botón Ayuda moderno - Azul muy claro
        btnAyuda = crearBotonModerno("? Ayuda", 
            new Color(187, 222, 251), new Color(144, 202, 249)); // Azul super claro
        
        // Botón Salir moderno - Rosa claro  
        btnSalir = crearBotonModerno("X Salir", 
            new Color(255, 205, 210), new Color(255, 138, 128)); // Rosa muy claro
        
        panelBotones.add(btnRegistrarse);
        panelBotones.add(btnAyuda);
        panelBotones.add(btnSalir);
    }
    
    /**
     * Crear botón moderno con efectos hover
     */
    private JButton crearBotonModerno(String texto, Color colorNormal, Color colorHover) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(colorNormal);
        
        // FORZAR texto negro para todos los botones
        boton.setForeground(Color.BLACK);
        
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(140, 40));
        
        // Bordes redondeados simulados con padding
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorNormal, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Efectos hover modernos
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover);
                boton.setForeground(Color.BLACK); // FORZAR texto negro siempre
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorHover, 2),
                    BorderFactory.createEmptyBorder(9, 19, 9, 19)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorNormal);
                boton.setForeground(Color.BLACK); // FORZAR texto negro siempre
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorNormal, 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });
        
        return boton;
    }
    
    /**
     * Crear botón de login principal con diseño especial
     */
    private JButton crearBotonLogin(String texto, Color colorNormal, Color colorHover) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(colorNormal);
        boton.setForeground(Color.BLACK); // FORZAR texto negro siempre
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(280, 45));
        boton.setMaximumSize(new Dimension(280, 45));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Bordes modernos
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorNormal, 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        // Efectos hover elegantes
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover);
                boton.setForeground(Color.BLACK); // FORZAR texto negro siempre
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorHover, 2),
                    BorderFactory.createEmptyBorder(11, 19, 11, 19)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorNormal);
                boton.setForeground(Color.BLACK); // FORZAR texto negro siempre
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorNormal, 1),
                    BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }
        });
        
        return boton;
    }

    /**
     * Configurar el layout de la ventana con diseño profesional
     */
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Panel título centrado y moderno
        panelTitulo.setLayout(new BorderLayout());
        JPanel contenedorTitulo = new JPanel();
        contenedorTitulo.setLayout(new BoxLayout(contenedorTitulo, BoxLayout.Y_AXIS));
        contenedorTitulo.setBackground(COLOR_PRINCIPAL);
        
        // Centrar elementos del título
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contenedorTitulo.add(Box.createVerticalStrut(25));
        contenedorTitulo.add(lblTitulo);
        contenedorTitulo.add(Box.createVerticalStrut(10));
        contenedorTitulo.add(lblSubtitulo);
        contenedorTitulo.add(Box.createVerticalStrut(25));
        
        panelTitulo.add(contenedorTitulo, BorderLayout.CENTER);
        
        // Panel opciones responsivo con scroll
        panelOpciones.setLayout(new GridLayout(1, 2, 30, 0));
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Agregar scroll al panel facial para responsividad
        JScrollPane scrollFacial = new JScrollPane(panelFacial);
        scrollFacial.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollFacial.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFacial.setBorder(null);
        scrollFacial.getViewport().setBackground(COLOR_BLANCO);
        
        panelOpciones.add(panelCredenciales);
        panelOpciones.add(scrollFacial);
        
        // Panel central que contiene opciones y estado
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(COLOR_FONDO);
        panelCentro.add(panelOpciones, BorderLayout.CENTER);
        panelCentro.add(lblEstado, BorderLayout.SOUTH);
        
        // Panel principal con mejor organización
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        
        // Estructura final de la ventana
        add(panelPrincipal, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * 🎯 Configurar eventos de los componentes
     */
    private void configurarEventos() {
        // Evento login con credenciales
        btnLoginCredenciales.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarConCredenciales();
            }
        });
        
        // Evento activar cámara
        btnActivarCamara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activarCamara();
            }
        });
        
        // Evento login facial
        btnLoginFacial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarConReconocimientoFacial();
            }
        });
        
        // Evento registrarse
        btnRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormularioRegistro();
            }
        });
        
        // Evento ayuda
        btnAyuda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAyuda();
            }
        });
        
        // Evento salir
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salirAplicacion();
            }
        });
        
        // Enter en campos de texto
        txtUsuario.addActionListener(e -> txtContrasena.requestFocus());
        txtContrasena.addActionListener(e -> autenticarConCredenciales());
    }

    /**
     * Configurar propiedades de la ventana con diseño profesional
     */
    private void configurarVentana() {
        setTitle("Sistema de Autenticacion Empresarial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750); // Aumentar altura para mostrar todos los botones
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Configurar icono de la aplicación
        try {
            // setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono de la aplicacion");
        }
    }

    /**
     * 💬 Mostrar mensaje de bienvenida
     */
    private void mostrarMensajeBienvenida() {
        actualizarEstado("Sistema iniciado correctamente. Bienvenido!");
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "Bienvenido al Sistema de Autenticacion\n\n" +
                "Opciones disponibles:\n" +
                "• Login con usuario y contraseña\n" +
                "• Reconocimiento facial avanzado\n" +
                "• Registro de nuevos usuarios\n\n" +
                "Seleccione su metodo preferido para continuar.",
                "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * 🔑 Autenticación con credenciales tradicionales
     */
    private void autenticarConCredenciales() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor, complete todos los campos");
            return;
        }
        
        actualizarEstado("Verificando credenciales...");
        
        // Ejecutar autenticación en hilo separado
        SwingWorker<Optional<Usuario>, Void> worker = new SwingWorker<Optional<Usuario>, Void>() {
            @Override
            protected Optional<Usuario> doInBackground() throws Exception {
                return servicioUsuario.autenticarUsuario(usuario, contrasena);
            }
            
            @Override
            protected void done() {
                try {
                    Optional<Usuario> usuarioAutenticado = get();
                    if (usuarioAutenticado.isPresent()) {
                        mostrarExito("Autenticacion exitosa");
                        abrirVentanaPrincipal(usuarioAutenticado.get(), "CREDENCIALES");
                    } else {
                        mostrarError("Credenciales incorrectas");
                        limpiarCampos();
                    }
                } catch (Exception e) {
                    mostrarError("Error en autenticación: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }

    /**
     * 📹 Activar sistema de cámara con progreso visual
     */
    private void activarCamara() {
        if (!camaraActiva) {
            // Deshabilitar botón durante inicialización
            btnActivarCamara.setEnabled(false);
            btnActivarCamara.setText("INICIALIZANDO...");
            btnActivarCamara.setBackground(new Color(255, 193, 7)); // Amarillo
            
            System.out.println("🎥 DEBUG: Iniciando activación de cámara...");
            
            SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
                
                @Override
                protected Boolean doInBackground() throws Exception {
                    // Publicar progreso paso a paso
                    publish("Verificando drivers de cámara...");
                    Thread.sleep(1000);
                    
                    publish("Detectando cámaras disponibles...");
                    Thread.sleep(1500);
                    
                    publish("Inicializando conexión (puede tomar 30-60 segundos)...");
                    
                    // Crear un timer para mostrar progreso durante la inicialización
                    Timer progressTimer = new Timer(3000, e -> {
                        String[] mensajes = {
                            "Conectando con hardware de cámara...",
                            "Configurando resolución y formato...", 
                            "Estableciendo parámetros de captura...",
                            "Validando funcionamiento...",
                            "Finalizando inicialización..."
                        };
                        
                        int index = (int)(System.currentTimeMillis() / 3000) % mensajes.length;
                        publish(mensajes[index]);
                    });
                    progressTimer.start();
                    
                    try {
                        System.out.println("🎥 DEBUG: Ejecutando inicializarCamara() en background...");
                        boolean resultado = manejadorCamara.inicializarCamara();
                        System.out.println("🎥 DEBUG: Resultado inicializarCamara(): " + resultado);
                        return resultado;
                    } finally {
                        progressTimer.stop();
                    }
                }
                
                @Override
                protected void process(java.util.List<String> chunks) {
                    // Actualizar estado con el último mensaje
                    if (!chunks.isEmpty()) {
                        String ultimoMensaje = chunks.get(chunks.size() - 1);
                        actualizarEstado(ultimoMensaje);
                        
                        // Mostrar progreso en vista previa
                        mostrarEstadoCamara("🔄 " + ultimoMensaje, new Color(243, 156, 18));
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        System.out.println("🎥 DEBUG: SwingWorker.done() ejecutándose...");
                        boolean exito = get();
                        System.out.println("🎥 DEBUG: Éxito obtenido: " + exito);
                        
                        if (exito) {
                            camaraActiva = true;
                            
                            // Actualizar botón con estilo moderno
                            btnActivarCamara.setText("✅ CÁMARA ACTIVA");
                            btnActivarCamara.setBackground(new Color(46, 204, 113)); // Verde moderno
                            btnActivarCamara.setEnabled(false);
                            btnLoginFacial.setEnabled(true);
                            
                            // Iniciar stream de video en tiempo real
                            iniciarStreamVideo();
                            
                            actualizarEstado("✅ Cámara activa - Vista previa habilitada");
                            System.out.println("✅ DEBUG: Cámara activada exitosamente en UI");
                        } else {
                            // Restaurar estado del botón con diseño moderno
                            btnActivarCamara.setText("🔌 ACTIVAR CÁMARA");
                            btnActivarCamara.setBackground(new Color(52, 152, 219));
                            btnActivarCamara.setEnabled(true);
                            
                            // Mostrar error en vista previa
                            detenerStreamVideo();
                            
                            System.out.println("❌ DEBUG: Fallo en activación de cámara");
                            mostrarError("No se pudo activar la cámara.\n\n" +
                                      "Posibles soluciones:\n" +
                                      "• Verifique que no esté siendo usada por otra aplicación\n" +
                                      "• Revise permisos de cámara en Windows\n" +
                                      "• Asegúrese de que los drivers estén instalados");
                            actualizarEstado("❌ Error activando cámara");
                        }
                    } catch (Exception e) {
                        // Restaurar estado del botón con diseño moderno
                        btnActivarCamara.setText("🔌 ACTIVAR CÁMARA");
                        btnActivarCamara.setBackground(new Color(52, 152, 219));
                        btnActivarCamara.setEnabled(true);
                        
                        // Mostrar error en vista previa
                        mostrarEstadoCamara("❌ Error de Conexión", new Color(231, 76, 60));
                        
                        System.out.println("❌ DEBUG: Excepción en SwingWorker: " + e.getMessage());
                        e.printStackTrace();
                        mostrarError("Error activando cámara: " + e.getMessage());
                        actualizarEstado("❌ Error en activación de cámara");
                    }
                }
            };
            
            worker.execute();
        } else {
            System.out.println("ℹ️ DEBUG: Cámara ya está activa");
            actualizarEstado("ℹ️ Cámara ya está activa");
        }
    }

    /**
     * 🧠 Autenticación con reconocimiento facial
     */
    private void autenticarConReconocimientoFacial() {
        if (!camaraActiva) {
            mostrarError("Primero debe activar la cámara");
            return;
        }
        
        actualizarEstado("Analizando rostro...");
        
        SwingWorker<Optional<Usuario>, Void> worker = new SwingWorker<Optional<Usuario>, Void>() {
            @Override
            protected Optional<Usuario> doInBackground() throws Exception {
                // Capturar imagen
                imagenCapturada = manejadorCamara.capturarImagenBuffered();
                if (imagenCapturada == null) {
                    throw new Exception("No se pudo capturar la imagen");
                }
                
                // Procesar con servicio mejorado (incluye comparación de imágenes)
                return servicioUsuario.autenticarConReconocimientoFacial(imagenCapturada);
            }
            
            @Override
            protected void done() {
                try {
                    Optional<Usuario> usuarioReconocido = get();
                    if (usuarioReconocido.isPresent()) {
                        mostrarExito("Reconocimiento facial exitoso");
                        abrirVentanaPrincipal(usuarioReconocido.get(), "RECONOCIMIENTO_FACIAL");
                    } else {
                        mostrarError("Rostro no reconocido");
                        actualizarEstado("Intente nuevamente o use credenciales");
                    }
                } catch (Exception e) {
                    mostrarError("Error en reconocimiento: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }

    /**
     * 👥 Abrir formulario de registro
     */
    private void abrirFormularioRegistro() {
        this.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            new FormularioRegistroCompleto().setVisible(true);
        });
        this.dispose();
    }

    /**
     * 🏠 Abrir ventana principal después de login exitoso
     */
    private void abrirVentanaPrincipal(Usuario usuario, String metodoAcceso) {
        this.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            VentanaBienvenida ventana = new VentanaBienvenida(usuario);
            ventana.setVisible(true);
        });
        this.dispose();
    }

    /**
     * ❓ Mostrar ayuda al usuario
     */
    private void mostrarAyuda() {
        String ayuda = "🆘 AYUDA DEL SISTEMA\n\n" +
                "🔐 CREDENCIALES:\n" +
                "• Ingrese su usuario y contraseña\n" +
                "• Presione 'Ingresar' o Enter\n\n" +
                "📷 RECONOCIMIENTO FACIAL:\n" +
                "• Haga clic en 'Activar Cámara'\n" +
                "• Posicione su rostro frente a la cámara\n" +
                "• Haga clic en 'Reconocer Rostro'\n\n" +
                "👥 REGISTRO:\n" +
                "• Para nuevos usuarios\n" +
                "• Requiere captura de imágenes faciales\n\n" +
                "💡 CONSEJOS:\n" +
                "• Buena iluminación para reconocimiento facial\n" +
                "• Mantener el rostro centrado en la cámara\n" +
                "• Evitar obstrucciones (lentes, sombreros)";
        
        JOptionPane.showMessageDialog(this, ayuda, "Ayuda del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 🚪 Salir de la aplicación
     */
    private void salirAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea salir del sistema?",
            "Confirmar Salida", JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            // Limpiar recursos
            if (manejadorCamara != null) {
                manejadorCamara.liberarRecursos();
            }
            System.exit(0);
        }
    }

    /**
     * 🔄 Limpiar campos de entrada
     */
    private void limpiarCampos() {
        txtUsuario.setText("");
        txtContrasena.setText("");
        txtUsuario.requestFocus();
    }

    /**
     * 📊 Actualizar estado de la aplicación
     */
    private void actualizarEstado(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
            lblEstado.repaint();
        });
    }

    /**
     * Mostrar mensaje de éxito
     */
    private void mostrarExito(String mensaje) {
        actualizarEstado(mensaje);
        lblEstado.setForeground(COLOR_EXITO);
    }

    /**
     * Mostrar mensaje de error
     */
    private void mostrarError(String mensaje) {
        actualizarEstado(mensaje);
        lblEstado.setForeground(new Color(231, 76, 60)); // Rojo de error
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }



    /**
     * 🚀 Método principal para ejecutar la aplicación
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new FormularioLoginNuevo().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error fatal iniciando aplicación: " + e.getMessage(),
                    "Error Crítico", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}