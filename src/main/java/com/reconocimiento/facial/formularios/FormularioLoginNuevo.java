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
    private JButton btnActivarCamara;
    private JButton btnLoginFacial;
    
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
    private static final Color COLOR_ACENTO = new Color(41, 128, 185);       // Azul claro
    private static final Color COLOR_EXITO = new Color(39, 174, 96);         // Verde
    private static final Color COLOR_FONDO = new Color(248, 249, 250);       // Gris muy claro
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_TEXTO = new Color(44, 62, 80);          // Texto oscuro
    private static final Color COLOR_BORDE = new Color(189, 195, 199);       // Borde gris
    
    private static final Font FONT_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 13);

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
        
        btnLoginCredenciales = new JButton("INGRESAR");
        btnLoginCredenciales.setFont(FONT_BOTON);
        btnLoginCredenciales.setBackground(COLOR_PRINCIPAL);
        btnLoginCredenciales.setForeground(Color.BLACK);
        btnLoginCredenciales.setPreferredSize(new Dimension(280, 42));
        btnLoginCredenciales.setFocusPainted(false);
        btnLoginCredenciales.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLoginCredenciales.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
     * Crear panel de reconocimiento facial con diseño profesional
     */
    private void crearPanelFacial() {
        panelFacial = new JPanel();
        panelFacial.setBackground(COLOR_BLANCO);
        panelFacial.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel lblTituloFacial = new JLabel("Reconocimiento Facial");
        lblTituloFacial.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloFacial.setForeground(COLOR_ACENTO);
        lblTituloFacial.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblCamara = new JLabel("CAMARA DESACTIVADA");
        lblCamara.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCamara.setHorizontalAlignment(SwingConstants.CENTER);
        lblCamara.setVerticalAlignment(SwingConstants.CENTER);
        lblCamara.setPreferredSize(new Dimension(280, 180));
        lblCamara.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        lblCamara.setOpaque(true);
        lblCamara.setBackground(new Color(240, 240, 240));
        lblCamara.setForeground(new Color(120, 120, 120));
        
        btnActivarCamara = new JButton("ACTIVAR CAMARA");
        btnActivarCamara.setFont(FONT_BOTON);
        btnActivarCamara.setBackground(COLOR_ACENTO);
        btnActivarCamara.setForeground(Color.BLACK);
        btnActivarCamara.setPreferredSize(new Dimension(280, 40));
        btnActivarCamara.setFocusPainted(false);
        btnActivarCamara.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnActivarCamara.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLoginFacial = new JButton("RECONOCER ROSTRO");
        btnLoginFacial.setFont(FONT_BOTON);
        btnLoginFacial.setBackground(COLOR_EXITO);
        btnLoginFacial.setForeground(Color.BLACK);
        btnLoginFacial.setPreferredSize(new Dimension(280, 42));
        btnLoginFacial.setFocusPainted(false);
        btnLoginFacial.setEnabled(false);
        btnLoginFacial.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLoginFacial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Layout del panel facial con mejor espaciado
        panelFacial.setLayout(new BoxLayout(panelFacial, BoxLayout.Y_AXIS));
        panelFacial.add(lblTituloFacial);
        panelFacial.add(Box.createVerticalStrut(20));
        panelFacial.add(lblCamara);
        panelFacial.add(Box.createVerticalStrut(15));
        panelFacial.add(btnActivarCamara);
        panelFacial.add(Box.createVerticalStrut(10));
        panelFacial.add(btnLoginFacial);
        
        // Centrar todos los componentes
        lblTituloFacial.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCamara.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnActivarCamara.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLoginFacial.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Crear panel de botones adicionales con diseño profesional
     */
    private void crearPanelBotones() {
        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        btnRegistrarse = new JButton("Registrarse");
        btnRegistrarse.setFont(FONT_BOTON);
        btnRegistrarse.setBackground(new Color(230, 126, 34)); // Naranja profesional
        btnRegistrarse.setForeground(Color.BLACK);
        btnRegistrarse.setFocusPainted(false);
        btnRegistrarse.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnRegistrarse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAyuda = new JButton("Ayuda");
        btnAyuda.setFont(FONT_BOTON);
        btnAyuda.setBackground(new Color(149, 165, 166)); // Gris profesional
        btnAyuda.setForeground(Color.BLACK);
        btnAyuda.setFocusPainted(false);
        btnAyuda.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnAyuda.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSalir = new JButton("Salir");
        btnSalir.setFont(FONT_BOTON);
        btnSalir.setBackground(new Color(231, 76, 60)); // Rojo profesional
        btnSalir.setForeground(Color.BLACK);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panelBotones.add(btnRegistrarse);
        panelBotones.add(btnAyuda);
        panelBotones.add(btnSalir);
    }

    /**
     * Configurar el layout de la ventana con diseño profesional
     */
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Panel título con mejor spacing
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.add(Box.createVerticalStrut(20));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(8));
        panelTitulo.add(lblSubtitulo);
        panelTitulo.add(Box.createVerticalStrut(20));
        
        // Panel opciones con espacio adecuado
        panelOpciones.setLayout(new GridLayout(1, 2, 30, 0));
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panelOpciones.add(panelCredenciales);
        panelOpciones.add(panelFacial);
        
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
        setSize(950, 700);
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
     * 📹 Activar sistema de cámara
     */
    private void activarCamara() {
        if (!camaraActiva) {
            actualizarEstado("Activando camara...");
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return manejadorCamara.inicializarCamara();
                }
                
                @Override
                protected void done() {
                    try {
                        boolean exito = get();
                        if (exito) {
                            camaraActiva = true;
                            btnActivarCamara.setText("CAMARA ACTIVA");
                            btnActivarCamara.setBackground(new Color(40, 167, 69)); // Verde
                            btnActivarCamara.setEnabled(false);
                            btnLoginFacial.setEnabled(true);
                            lblCamara.setText("CAMARA CONECTADA");
                            lblCamara.setBackground(new Color(40, 167, 69));
                            lblCamara.setForeground(Color.WHITE);
                            actualizarEstado("Camara activa - Listo para reconocimiento");
                        } else {
                            mostrarError("No se pudo activar la camara");
                        }
                    } catch (Exception e) {
                        mostrarError("Error activando cámara: " + e.getMessage());
                    }
                }
            };
            
            worker.execute();
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
                
                // Procesar con red neuronal
                return redNeuronal.reconocerUsuario(imagenCapturada);
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