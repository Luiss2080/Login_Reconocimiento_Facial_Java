package com.reconocimiento.facial.formularios;

import com.reconocimiento.facial.procesamiento.ManejadorCamara;
import com.reconocimiento.facial.servicios.ServicioUsuarioMejorado;
import com.reconocimiento.facial.dto.UsuarioDTO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 👥 FORMULARIO DE REGISTRO DE USUARIOS
 * Sistema completo con captura biométrica y validaciones robustas
 * Incluye entrenamiento automático de la red neuronal
 */
public class FormularioRegistroCompleto extends JFrame {

    // ========== COMPONENTES DE INTERFAZ ==========
    private JPanel panelPrincipal;
    private JPanel panelTitulo;
    private JPanel panelFormulario;
    private JPanel panelCaptura;
    private JPanel panelBotones;
    
    // Campos del formulario
    private JTextField txtNombreUsuario;
    private JTextField txtNombreCompleto;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JPasswordField txtContrasena;
    private JPasswordField txtConfirmarContrasena;
    
    // Componentes de captura facial
    private JLabel lblCamara;
    private JButton btnActivarCamara;
    private JButton btnCapturarMuestra;
    private JProgressBar progressCaptura;
    private JLabel lblMuestrasCapturadas;
    private JLabel lblEstado;
    
    // Botones principales
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JButton btnVolverLogin;

    // ========== SERVICIOS Y LÓGICA ==========
    private ServicioUsuarioMejorado servicioUsuario;
    private ManejadorCamara manejadorCamara;
    
    // Variables de captura facial
    private List<BufferedImage> muestrasFaciales;
    private static final int MUESTRAS_REQUERIDAS = 5;
    private boolean camaraActiva = false;
    private boolean capturaEnProceso = false;

    // ========== CONSTANTES DE DISEÑO ==========
    private static final Color COLOR_PRIMARIO = new Color(33, 150, 243);
    private static final Color COLOR_SECUNDARIO = new Color(76, 175, 80);
    private static final Color COLOR_PELIGRO = new Color(244, 67, 54);
    private static final Color COLOR_FONDO = new Color(245, 245, 245);
    private static final Color COLOR_BLANCO = Color.WHITE;

    /**
     * Constructor principal
     */
    public FormularioRegistroCompleto() {
        try {
            // Configurar apariencia
            configurarApariencia();
            
            // Inicializar servicios
            inicializarServicios();
            
            // Crear interfaz
            inicializarComponentes();
            configurarLayout();
            configurarEventos();
            
            // Configurar ventana
            configurarVentana();
            
            mostrarMensajeBienvenida();
            
        } catch (Exception e) {
            mostrarError("Error inicializando formulario: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 🎨 Configurar apariencia
     */
    private void configurarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo configurar apariencia: " + e.getMessage());
        }
    }

    /**
     * 🔧 Inicializar servicios
     */
    private void inicializarServicios() {
        try {
            this.servicioUsuario = new ServicioUsuarioMejorado();
            this.manejadorCamara = new ManejadorCamara();
            this.muestrasFaciales = new ArrayList<>();
            
            System.out.println("✅ Servicios de registro inicializados");
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando servicios: " + e.getMessage(), e);
        }
    }

    /**
     * 🏗️ Inicializar componentes
     */
    private void inicializarComponentes() {
        // Panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setBackground(COLOR_FONDO);
        
        // Panel título
        crearPanelTitulo();
        
        // Panel formulario
        crearPanelFormulario();
        
        // Panel captura
        crearPanelCaptura();
        
        // Panel botones
        crearPanelBotones();
        
        // Estado
        lblEstado = new JLabel("✨ Complete el formulario y capture sus imágenes faciales");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setForeground(COLOR_PRIMARIO);
    }

    /**
     * 📋 Crear panel de título
     */
    private void crearPanelTitulo() {
        panelTitulo = new JPanel();
        panelTitulo.setBackground(COLOR_PRIMARIO);
        
        JLabel lblTitulo = new JLabel("👥 REGISTRO DE NUEVO USUARIO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_BLANCO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblSubtitulo = new JLabel("Complete sus datos y capture imágenes para el reconocimiento facial");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitulo.setForeground(COLOR_BLANCO);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.add(Box.createVerticalStrut(20));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblSubtitulo);
        panelTitulo.add(Box.createVerticalStrut(20));
    }

    /**
     * 📝 Crear panel de formulario
     */
    private void crearPanelFormulario() {
        panelFormulario = new JPanel();
        panelFormulario.setBackground(COLOR_BLANCO);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTituloForm = new JLabel("📝 Información Personal");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloForm.setForeground(COLOR_PRIMARIO);
        
        // Campos del formulario
        JLabel lblNombreUsuario = new JLabel("👤 Nombre de Usuario:");
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtNombreUsuario = new JTextField();
        txtNombreUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNombreUsuario.setPreferredSize(new Dimension(200, 30));
        
        JLabel lblNombreCompleto = new JLabel("📋 Nombre Completo:");
        lblNombreCompleto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtNombreCompleto = new JTextField();
        txtNombreCompleto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNombreCompleto.setPreferredSize(new Dimension(200, 30));
        
        JLabel lblCorreo = new JLabel("📧 Correo Electrónico:");
        lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCorreo = new JTextField();
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCorreo.setPreferredSize(new Dimension(200, 30));
        
        JLabel lblTelefono = new JLabel("📱 Teléfono:");
        lblTelefono.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtTelefono = new JTextField();
        txtTelefono.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTelefono.setPreferredSize(new Dimension(200, 30));
        
        JLabel lblContrasena = new JLabel("🔑 Contraseña:");
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtContrasena = new JPasswordField();
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtContrasena.setPreferredSize(new Dimension(200, 30));
        
        JLabel lblConfirmar = new JLabel("🔐 Confirmar Contraseña:");
        lblConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtConfirmarContrasena = new JPasswordField();
        txtConfirmarContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtConfirmarContrasena.setPreferredSize(new Dimension(200, 30));
        
        // Layout del formulario
        panelFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panelFormulario.add(lblTituloForm, gbc);
        
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1; panelFormulario.add(lblNombreUsuario, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panelFormulario.add(txtNombreUsuario, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; panelFormulario.add(lblNombreCompleto, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panelFormulario.add(txtNombreCompleto, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; panelFormulario.add(lblCorreo, gbc);
        gbc.gridx = 1; gbc.gridy = 3; panelFormulario.add(txtCorreo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; panelFormulario.add(lblTelefono, gbc);
        gbc.gridx = 1; gbc.gridy = 4; panelFormulario.add(txtTelefono, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; panelFormulario.add(lblContrasena, gbc);
        gbc.gridx = 1; gbc.gridy = 5; panelFormulario.add(txtContrasena, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; panelFormulario.add(lblConfirmar, gbc);
        gbc.gridx = 1; gbc.gridy = 6; panelFormulario.add(txtConfirmarContrasena, gbc);
    }

    /**
     * 📷 Crear panel de captura facial
     */
    private void crearPanelCaptura() {
        panelCaptura = new JPanel();
        panelCaptura.setBackground(COLOR_BLANCO);
        panelCaptura.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_SECUNDARIO, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTituloCaptura = new JLabel("📷 Captura Biométrica");
        lblTituloCaptura.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloCaptura.setForeground(COLOR_SECUNDARIO);
        
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
        btnActivarCamara.setPreferredSize(new Dimension(180, 35));
        btnActivarCamara.setFocusPainted(false);
        
        btnCapturarMuestra = new JButton("📸 CAPTURAR MUESTRA");
        btnCapturarMuestra.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCapturarMuestra.setBackground(COLOR_SECUNDARIO);
        btnCapturarMuestra.setForeground(COLOR_BLANCO);
        btnCapturarMuestra.setPreferredSize(new Dimension(180, 35));
        btnCapturarMuestra.setFocusPainted(false);
        btnCapturarMuestra.setEnabled(false);
        
        progressCaptura = new JProgressBar(0, MUESTRAS_REQUERIDAS);
        progressCaptura.setStringPainted(true);
        progressCaptura.setString("0 / " + MUESTRAS_REQUERIDAS + " muestras");
        progressCaptura.setPreferredSize(new Dimension(180, 25));
        
        lblMuestrasCapturadas = new JLabel("✨ Capture " + MUESTRAS_REQUERIDAS + " imágenes de su rostro");
        lblMuestrasCapturadas.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblMuestrasCapturadas.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Layout del panel captura
        panelCaptura.setLayout(new BoxLayout(panelCaptura, BoxLayout.Y_AXIS));
        panelCaptura.add(lblTituloCaptura);
        panelCaptura.add(Box.createVerticalStrut(15));
        panelCaptura.add(lblCamara);
        panelCaptura.add(Box.createVerticalStrut(10));
        panelCaptura.add(btnActivarCamara);
        panelCaptura.add(Box.createVerticalStrut(5));
        panelCaptura.add(btnCapturarMuestra);
        panelCaptura.add(Box.createVerticalStrut(10));
        panelCaptura.add(progressCaptura);
        panelCaptura.add(Box.createVerticalStrut(5));
        panelCaptura.add(lblMuestrasCapturadas);
    }

    /**
     * 🎛️ Crear panel de botones
     */
    private void crearPanelBotones() {
        panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(COLOR_FONDO);
        
        btnRegistrar = new JButton("✅ REGISTRAR USUARIO");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBackground(COLOR_SECUNDARIO);
        btnRegistrar.setForeground(COLOR_BLANCO);
        btnRegistrar.setPreferredSize(new Dimension(180, 40));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setEnabled(false);
        
        btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setBackground(COLOR_PELIGRO);
        btnCancelar.setForeground(COLOR_BLANCO);
        btnCancelar.setFocusPainted(false);
        
        btnVolverLogin = new JButton("🔙 Volver al Login");
        btnVolverLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVolverLogin.setBackground(new Color(108, 117, 125));
        btnVolverLogin.setForeground(COLOR_BLANCO);
        btnVolverLogin.setFocusPainted(false);
        
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnVolverLogin);
    }

    /**
     * 📐 Configurar layout
     */
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Panel principal con formulario y captura
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelCentral.add(panelFormulario);
        panelCentral.add(panelCaptura);
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(lblEstado, BorderLayout.SOUTH);
        
        // Panel de botones en el sur
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(COLOR_FONDO);
        panelSur.add(lblEstado, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * 🎯 Configurar eventos
     */
    private void configurarEventos() {
        // Activar cámara
        btnActivarCamara.addActionListener(e -> activarCamara());
        
        // Capturar muestra
        btnCapturarMuestra.addActionListener(e -> capturarMuestra());
        
        // Registrar usuario
        btnRegistrar.addActionListener(e -> registrarUsuario());
        
        // Cancelar
        btnCancelar.addActionListener(e -> cancelarRegistro());
        
        // Volver al login
        btnVolverLogin.addActionListener(e -> volverAlLogin());
        
        // Validación en tiempo real
        configurarValidacionTiempoReal();
    }

    /**
     * ⚡ Configurar validación en tiempo real
     */
    private void configurarValidacionTiempoReal() {
        // Listener para habilitar botón registrar
        ActionListener validarFormulario = e -> validarYHabilitarRegistro();
        
        txtNombreUsuario.addActionListener(validarFormulario);
        txtNombreCompleto.addActionListener(validarFormulario);
        txtCorreo.addActionListener(validarFormulario);
        txtContrasena.addActionListener(validarFormulario);
        txtConfirmarContrasena.addActionListener(validarFormulario);
    }

    /**
     * 🪟 Configurar ventana
     */
    private void configurarVentana() {
        setTitle("Registro de Usuario - Sistema de Reconocimiento Facial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * 💬 Mostrar mensaje de bienvenida
     */
    private void mostrarMensajeBienvenida() {
        actualizarEstado("🎉 Bienvenido al registro del sistema");
        
        SwingUtilities.invokeLater(() -> {
            String mensaje = "🚀 REGISTRO DE NUEVO USUARIO\n\n" +
                    "Pasos a seguir:\n" +
                    "1️⃣ Complete todos los campos del formulario\n" +
                    "2️⃣ Active la cámara para captura facial\n" +
                    "3️⃣ Capture " + MUESTRAS_REQUERIDAS + " imágenes de su rostro\n" +
                    "4️⃣ Confirme el registro\n\n" +
                    "💡 Consejos:\n" +
                    "• Use buena iluminación\n" +
                    "• Mantenga el rostro centrado\n" +
                    "• Evite obstrucciones (lentes, sombreros)\n" +
                    "• Varíe ligeramente la posición entre capturas";
                    
            JOptionPane.showMessageDialog(this, mensaje, "Guía de Registro", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * 📹 Activar cámara
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
                            btnCapturarMuestra.setEnabled(true);
                            lblCamara.setText("📹");
                            lblCamara.setBackground(COLOR_SECUNDARIO);
                            actualizarEstado("✅ Cámara activa - Listo para captura");
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
     * 📸 Capturar muestra facial
     */
    private void capturarMuestra() {
        if (!camaraActiva || capturaEnProceso) {
            return;
        }
        
        capturaEnProceso = true;
        actualizarEstado("📸 Capturando muestra...");
        
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                // Simular captura (reemplazar con implementación real)
                Thread.sleep(1000); // Simular tiempo de captura
                // return manejadorCamara.capturarImagen();
                
                // Por ahora crear una imagen simulada
                BufferedImage imagen = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = imagen.createGraphics();
                g2d.setColor(new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
                g2d.fillRect(0, 0, 200, 200);
                g2d.dispose();
                return imagen;
            }
            
            @Override
            protected void done() {
                try {
                    BufferedImage imagen = get();
                    if (imagen != null) {
                        muestrasFaciales.add(imagen);
                        actualizarProgreso();
                        mostrarExito("✅ Muestra " + muestrasFaciales.size() + " capturada");
                        
                        if (muestrasFaciales.size() >= MUESTRAS_REQUERIDAS) {
                            btnCapturarMuestra.setEnabled(false);
                            btnCapturarMuestra.setText("✅ CAPTURAS COMPLETAS");
                            actualizarEstado("🎉 Todas las muestras capturadas correctamente");
                            validarYHabilitarRegistro();
                        }
                    } else {
                        mostrarError("❌ Error capturando imagen");
                    }
                } catch (Exception e) {
                    mostrarError("Error en captura: " + e.getMessage());
                } finally {
                    capturaEnProceso = false;
                }
            }
        };
        
        worker.execute();
    }

    /**
     * 📊 Actualizar progreso de captura
     */
    private void actualizarProgreso() {
        int capturas = muestrasFaciales.size();
        progressCaptura.setValue(capturas);
        progressCaptura.setString(capturas + " / " + MUESTRAS_REQUERIDAS + " muestras");
        
        if (capturas >= MUESTRAS_REQUERIDAS) {
            progressCaptura.setForeground(COLOR_SECUNDARIO);
            lblMuestrasCapturadas.setText("✅ Capturas completadas correctamente");
            lblMuestrasCapturadas.setForeground(COLOR_SECUNDARIO);
        }
    }

    /**
     * ✅ Registrar usuario
     */
    private void registrarUsuario() {
        if (!validarDatos()) {
            return;
        }
        
        actualizarEstado("💾 Registrando usuario...");
        btnRegistrar.setEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Crear DTO del usuario
                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setNombreUsuario(txtNombreUsuario.getText().trim());
                usuario.setNombreCompleto(txtNombreCompleto.getText().trim());
                usuario.setCorreo(txtCorreo.getText().trim());
                usuario.setTelefono(txtTelefono.getText().trim());
                usuario.setContrasena(new String(txtContrasena.getPassword()));
                
                // Registrar usuario y características faciales
                return servicioUsuario.registrarUsuarioCompleto(usuario, muestrasFaciales);
            }
            
            @Override
            protected void done() {
                try {
                    Boolean exito = get();
                    if (exito) {
                        mostrarRegistroExitoso();
                    } else {
                        mostrarError("❌ Error en el registro del usuario");
                        btnRegistrar.setEnabled(true);
                    }
                } catch (Exception e) {
                    mostrarError("Error registrando usuario: " + e.getMessage());
                    btnRegistrar.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }

    /**
     * 🎉 Mostrar registro exitoso
     */
    private void mostrarRegistroExitoso() {
        String mensaje = "🎉 ¡REGISTRO EXITOSO!\n\n" +
                "Usuario: " + txtNombreUsuario.getText() + "\n" +
                "Nombre: " + txtNombreCompleto.getText() + "\n" +
                "Muestras faciales: " + muestrasFaciales.size() + "\n\n" +
                "Ya puede usar el sistema con:\n" +
                "• Sus credenciales de usuario\n" +
                "• Reconocimiento facial\n\n" +
                "¿Desea ir al login ahora?";
        
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, 
                "Registro Exitoso", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            volverAlLogin();
        } else {
            limpiarFormulario();
        }
    }

    /**
     * ✔️ Validar datos del formulario
     */
    private boolean validarDatos() {
        // Validar campos requeridos
        if (txtNombreUsuario.getText().trim().isEmpty()) {
            mostrarError("El nombre de usuario es requerido");
            txtNombreUsuario.requestFocus();
            return false;
        }
        
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            mostrarError("El nombre completo es requerido");
            txtNombreCompleto.requestFocus();
            return false;
        }
        
        if (txtCorreo.getText().trim().isEmpty()) {
            mostrarError("El correo electrónico es requerido");
            txtCorreo.requestFocus();
            return false;
        }
        
        // Validar formato de correo
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!Pattern.matches(emailRegex, txtCorreo.getText().trim())) {
            mostrarError("Formato de correo electrónico inválido");
            txtCorreo.requestFocus();
            return false;
        }
        
        // Validar contraseñas
        String contrasena = new String(txtContrasena.getPassword());
        String confirmacion = new String(txtConfirmarContrasena.getPassword());
        
        if (contrasena.isEmpty()) {
            mostrarError("La contraseña es requerida");
            txtContrasena.requestFocus();
            return false;
        }
        
        if (contrasena.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres");
            txtContrasena.requestFocus();
            return false;
        }
        
        if (!contrasena.equals(confirmacion)) {
            mostrarError("Las contraseñas no coinciden");
            txtConfirmarContrasena.requestFocus();
            return false;
        }
        
        // Validar muestras faciales
        if (muestrasFaciales.size() < MUESTRAS_REQUERIDAS) {
            mostrarError("Debe capturar " + MUESTRAS_REQUERIDAS + " muestras faciales");
            return false;
        }
        
        return true;
    }

    /**
     * 🔄 Validar y habilitar botón de registro
     */
    private void validarYHabilitarRegistro() {
        boolean formValido = !txtNombreUsuario.getText().trim().isEmpty() &&
                           !txtNombreCompleto.getText().trim().isEmpty() &&
                           !txtCorreo.getText().trim().isEmpty() &&
                           txtContrasena.getPassword().length >= 6 &&
                           new String(txtContrasena.getPassword()).equals(new String(txtConfirmarContrasena.getPassword())) &&
                           muestrasFaciales.size() >= MUESTRAS_REQUERIDAS;
        
        btnRegistrar.setEnabled(formValido);
        
        if (formValido) {
            btnRegistrar.setBackground(COLOR_SECUNDARIO);
            actualizarEstado("✅ Listo para registrar - Haga clic en REGISTRAR USUARIO");
        }
    }

    /**
     * ❌ Cancelar registro
     */
    private void cancelarRegistro() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cancelar el registro?\nSe perderán todos los datos ingresados.",
            "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            limpiarFormulario();
        }
    }

    /**
     * 🔙 Volver al login
     */
    private void volverAlLogin() {
        this.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            new FormularioLoginNuevo().setVisible(true);
        });
        this.dispose();
    }

    /**
     * 🧹 Limpiar formulario
     */
    private void limpiarFormulario() {
        txtNombreUsuario.setText("");
        txtNombreCompleto.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtContrasena.setText("");
        txtConfirmarContrasena.setText("");
        
        muestrasFaciales.clear();
        progressCaptura.setValue(0);
        progressCaptura.setString("0 / " + MUESTRAS_REQUERIDAS + " muestras");
        lblMuestrasCapturadas.setText("✨ Capture " + MUESTRAS_REQUERIDAS + " imágenes de su rostro");
        lblMuestrasCapturadas.setForeground(Color.BLACK);
        
        btnCapturarMuestra.setEnabled(false);
        btnCapturarMuestra.setText("📸 CAPTURAR MUESTRA");
        btnRegistrar.setEnabled(false);
        
        if (camaraActiva) {
            btnActivarCamara.setText("📹 ACTIVAR CÁMARA");
            btnActivarCamara.setEnabled(true);
            camaraActiva = false;
        }
        
        txtNombreUsuario.requestFocus();
        actualizarEstado("🔄 Formulario limpiado - Listo para nuevo registro");
    }

    /**
     * 📊 Actualizar estado
     */
    private void actualizarEstado(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
            lblEstado.setForeground(COLOR_PRIMARIO);
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
     * ❌ Mostrar error
     */
    private void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
            lblEstado.setForeground(COLOR_PELIGRO);
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    /**
     * 🚀 Método principal para pruebas
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new FormularioRegistroCompleto().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error iniciando formulario: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}