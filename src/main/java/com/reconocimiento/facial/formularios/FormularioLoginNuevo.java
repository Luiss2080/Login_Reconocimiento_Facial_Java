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

    // ========== CONSTANTES DE DISEÑO ==========
    private static final Color COLOR_PRIMARIO = new Color(33, 150, 243);
    private static final Color COLOR_SECUNDARIO = new Color(76, 175, 80);
    private static final Color COLOR_PELIGRO = new Color(244, 67, 54);
    private static final Color COLOR_FONDO = new Color(245, 245, 245);
    private static final Color COLOR_BLANCO = Color.WHITE;

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
     * 🎨 Configurar apariencia moderna
     */
    private void configurarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo configurar apariencia: " + e.getMessage());
        }
    }

    /**
     * 🔧 Inicializar todos los servicios necesarios
     */
    private void inicializarServicios() {
        try {
            this.servicioUsuario = new ServicioUsuarioMejorado();
            this.redNeuronal = new RedNeuronalReconocimiento();
            this.manejadorCamara = new ManejadorCamara();
            
            System.out.println("✅ Servicios inicializados correctamente");
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando servicios: " + e.getMessage(), e);
        }
    }

    /**
     * 🏗️ Inicializar todos los componentes de la interfaz
     */
    private void inicializarComponentes() {
        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setBackground(COLOR_FONDO);
        
        // Panel de título
        panelTitulo = new JPanel();
        panelTitulo.setBackground(COLOR_PRIMARIO);
        
        lblTitulo = new JLabel("🛡️ SISTEMA DE AUTENTICACIÓN");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_BLANCO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblSubtitulo = new JLabel("Reconocimiento Facial • Credenciales Tradicionales");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitulo.setForeground(COLOR_BLANCO);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel de opciones principales
        panelOpciones = new JPanel();
        panelOpciones.setBackground(COLOR_FONDO);
        
        // Panel de credenciales
        crearPanelCredenciales();
        
        // Panel facial
        crearPanelFacial();
        
        // Panel de botones
        crearPanelBotones();
        
        // Estado
        lblEstado = new JLabel("✨ Seleccione su método de autenticación preferido");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setForeground(COLOR_PRIMARIO);
    }

    /**
     * 🔐 Crear panel de autenticación por credenciales
     */
    private void crearPanelCredenciales() {
        panelCredenciales = new JPanel();
        panelCredenciales.setBackground(COLOR_BLANCO);
        panelCredenciales.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTituloCredenciales = new JLabel("🔐 Acceso con Credenciales");
        lblTituloCredenciales.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloCredenciales.setForeground(COLOR_PRIMARIO);
        
        JLabel lblUsuario = new JLabel("👤 Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(250, 35));
        
        JLabel lblContrasena = new JLabel("🔑 Contraseña:");
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtContrasena = new JPasswordField();
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContrasena.setPreferredSize(new Dimension(250, 35));
        
        btnLoginCredenciales = new JButton("🚀 INGRESAR");
        btnLoginCredenciales.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoginCredenciales.setBackground(COLOR_PRIMARIO);
        btnLoginCredenciales.setForeground(COLOR_BLANCO);
        btnLoginCredenciales.setPreferredSize(new Dimension(250, 40));
        btnLoginCredenciales.setFocusPainted(false);
        
        // Layout del panel credenciales
        panelCredenciales.setLayout(new BoxLayout(panelCredenciales, BoxLayout.Y_AXIS));
        panelCredenciales.add(lblTituloCredenciales);
        panelCredenciales.add(Box.createVerticalStrut(15));
        panelCredenciales.add(lblUsuario);
        panelCredenciales.add(Box.createVerticalStrut(5));
        panelCredenciales.add(txtUsuario);
        panelCredenciales.add(Box.createVerticalStrut(10));
        panelCredenciales.add(lblContrasena);
        panelCredenciales.add(Box.createVerticalStrut(5));
        panelCredenciales.add(txtContrasena);
        panelCredenciales.add(Box.createVerticalStrut(20));
        panelCredenciales.add(btnLoginCredenciales);
    }

    /**
     * 📷 Crear panel de reconocimiento facial
     */
    private void crearPanelFacial() {
        panelFacial = new JPanel();
        panelFacial.setBackground(COLOR_BLANCO);
        panelFacial.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_SECUNDARIO, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTituloFacial = new JLabel("📷 Reconocimiento Facial");
        lblTituloFacial.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloFacial.setForeground(COLOR_SECUNDARIO);
        
        lblCamara = new JLabel("📸");
        lblCamara.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblCamara.setHorizontalAlignment(SwingConstants.CENTER);
        lblCamara.setPreferredSize(new Dimension(200, 150));
        lblCamara.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        lblCamara.setOpaque(true);
        lblCamara.setBackground(new Color(240, 240, 240));
        
        btnActivarCamara = new JButton("📹 ACTIVAR CÁMARA");
        btnActivarCamara.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActivarCamara.setBackground(COLOR_SECUNDARIO);
        btnActivarCamara.setForeground(COLOR_BLANCO);
        btnActivarCamara.setPreferredSize(new Dimension(200, 35));
        btnActivarCamara.setFocusPainted(false);
        
        btnLoginFacial = new JButton("🧠 RECONOCER ROSTRO");
        btnLoginFacial.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoginFacial.setBackground(COLOR_SECUNDARIO);
        btnLoginFacial.setForeground(COLOR_BLANCO);
        btnLoginFacial.setPreferredSize(new Dimension(200, 40));
        btnLoginFacial.setFocusPainted(false);
        btnLoginFacial.setEnabled(false);
        
        // Layout del panel facial
        panelFacial.setLayout(new BoxLayout(panelFacial, BoxLayout.Y_AXIS));
        panelFacial.add(lblTituloFacial);
        panelFacial.add(Box.createVerticalStrut(15));
        panelFacial.add(lblCamara);
        panelFacial.add(Box.createVerticalStrut(10));
        panelFacial.add(btnActivarCamara);
        panelFacial.add(Box.createVerticalStrut(10));
        panelFacial.add(btnLoginFacial);
    }

    /**
     * 🎛️ Crear panel de botones adicionales
     */
    private void crearPanelBotones() {
        panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(COLOR_FONDO);
        
        btnRegistrarse = new JButton("👥 Registrarse");
        btnRegistrarse.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRegistrarse.setBackground(new Color(255, 193, 7));
        btnRegistrarse.setForeground(Color.BLACK);
        btnRegistrarse.setFocusPainted(false);
        
        btnAyuda = new JButton("❓ Ayuda");
        btnAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAyuda.setBackground(new Color(108, 117, 125));
        btnAyuda.setForeground(COLOR_BLANCO);
        btnAyuda.setFocusPainted(false);
        
        btnSalir = new JButton("🚪 Salir");
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSalir.setBackground(COLOR_PELIGRO);
        btnSalir.setForeground(COLOR_BLANCO);
        btnSalir.setFocusPainted(false);
        
        panelBotones.add(btnRegistrarse);
        panelBotones.add(btnAyuda);
        panelBotones.add(btnSalir);
    }

    /**
     * 📐 Configurar el layout de la ventana
     */
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Panel título
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.add(Box.createVerticalStrut(20));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblSubtitulo);
        panelTitulo.add(Box.createVerticalStrut(20));
        
        // Panel opciones
        panelOpciones.setLayout(new GridLayout(1, 2, 20, 0));
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        panelOpciones.add(panelCredenciales);
        panelOpciones.add(panelFacial);
        
        // Panel principal
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelOpciones, BorderLayout.CENTER);
        panelPrincipal.add(lblEstado, BorderLayout.SOUTH);
        
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
     * 🪟 Configurar propiedades de la ventana
     */
    private void configurarVentana() {
        setTitle("Sistema de Autenticación - Reconocimiento Facial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Icono de la aplicación
        try {
            // setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono de la aplicación");
        }
    }

    /**
     * 💬 Mostrar mensaje de bienvenida
     */
    private void mostrarMensajeBienvenida() {
        actualizarEstado("🎉 Sistema iniciado correctamente. ¡Bienvenido!");
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "🚀 Bienvenido al Sistema de Autenticación\n\n" +
                "Opciones disponibles:\n" +
                "• 🔐 Login con usuario y contraseña\n" +
                "• 📷 Reconocimiento facial avanzado\n" +
                "• 👥 Registro de nuevos usuarios\n\n" +
                "Seleccione su método preferido para continuar.",
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
        
        actualizarEstado("🔍 Verificando credenciales...");
        
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
                        mostrarExito("✅ Autenticación exitosa");
                        abrirVentanaPrincipal(usuarioAutenticado.get(), "CREDENCIALES");
                    } else {
                        mostrarError("❌ Credenciales incorrectas");
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
            actualizarEstado("📹 Activando cámara...");
            
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
                            btnActivarCamara.setText("📹 CÁMARA ACTIVA");
                            btnActivarCamara.setEnabled(false);
                            btnLoginFacial.setEnabled(true);
                            lblCamara.setText("📹");
                            lblCamara.setBackground(COLOR_SECUNDARIO);
                            actualizarEstado("✅ Cámara activa - Listo para reconocimiento");
                        } else {
                            mostrarError("❌ No se pudo activar la cámara");
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
        
        actualizarEstado("🧠 Analizando rostro...");
        
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
                        mostrarExito("✅ Reconocimiento facial exitoso");
                        abrirVentanaPrincipal(usuarioReconocido.get(), "RECONOCIMIENTO_FACIAL");
                    } else {
                        mostrarError("❌ Rostro no reconocido");
                        actualizarEstado("🔍 Intente nuevamente o use credenciales");
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
     * ✅ Mostrar mensaje de éxito
     */
    private void mostrarExito(String mensaje) {
        actualizarEstado(mensaje);
        lblEstado.setForeground(COLOR_SECUNDARIO);
    }

    /**
     * ❌ Mostrar mensaje de error
     */
    private void mostrarError(String mensaje) {
        actualizarEstado(mensaje);
        lblEstado.setForeground(COLOR_PELIGRO);
        
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