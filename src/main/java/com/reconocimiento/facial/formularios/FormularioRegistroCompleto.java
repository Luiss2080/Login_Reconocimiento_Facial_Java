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
 * FORMULARIO DE REGISTRO DE USUARIOS
 * Sistema completo con captura biometrica y validaciones robustas
 * Incluye entrenamiento automatico de la red neuronal
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
    
    // Componentes de captura facial - MEJORADOS
    private JLabel lblCamara;
    private JLabel lblVistaPrevia; // Nueva: Para mostrar video en tiempo real HD
    private JButton btnActivarCamara;
    private JButton btnCapturarMuestra;
    private JProgressBar progressCaptura;
    private JLabel lblMuestrasCapturadas;
    private JLabel lblEstado;
    private JPanel panelVideoContainer; // Contenedor del video mejorado
    
    // Botones principales
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JButton btnVolverLogin;

    // ========== SERVICIOS Y LOGICA ==========
    private ServicioUsuarioMejorado servicioUsuario;
    private ManejadorCamara manejadorCamara;
    
    // Variables de captura facial
    private List<BufferedImage> muestrasFaciales;
    private static final int MUESTRAS_REQUERIDAS = 5;
    private boolean camaraActiva = false;
    private boolean capturaEnProceso = false;
    private javax.swing.Timer videoTimer; // Timer para actualizar video en tiempo real

    // ========== CONSTANTES DE DISEÑO PROFESIONAL ==========
    private static final Color COLOR_PRINCIPAL = new Color(52, 73, 94);      // Azul oscuro profesional
    private static final Color COLOR_ACENTO = new Color(41, 128, 185);       // Azul claro
    @SuppressWarnings("unused") private static final Color COLOR_EXITO = new Color(39, 174, 96);         // Verde
    private static final Color COLOR_FONDO = new Color(248, 249, 250);       // Gris muy claro
    private static final Color COLOR_BLANCO = Color.WHITE;
    @SuppressWarnings("unused") private static final Color COLOR_TEXTO = new Color(44, 62, 80);          // Texto oscuro
    private static final Color COLOR_BORDE = new Color(189, 195, 199);       // Borde gris
    
    // Constantes de compatibilidad
    private static final Color COLOR_PRIMARIO = COLOR_PRINCIPAL;
    private static final Color COLOR_SECUNDARIO = COLOR_ACENTO;
    private static final Color COLOR_PELIGRO = new Color(231, 76, 60);      // Rojo error
    
    @SuppressWarnings("unused") private static final Font FONT_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    @SuppressWarnings("unused") private static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 13);

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
            
            // Asegurar estado inicial limpio
            inicializarEstadoLimpio();
            
            mostrarMensajeBienvenida();
            
        } catch (Exception e) {
            mostrarError("Error inicializando formulario: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Configurar apariencia profesional
     */
    private void configurarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo configurar apariencia: " + e.getMessage());
        }
    }

    /**
     * Inicializar servicios
     */
    private void inicializarServicios() {
        try {
            this.servicioUsuario = new ServicioUsuarioMejorado();
            this.manejadorCamara = new ManejadorCamara();
            this.muestrasFaciales = new ArrayList<>();
            
            System.out.println("Servicios de registro inicializados");
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando servicios: " + e.getMessage(), e);
        }
    }

    /**
     * Inicializar componentes
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
        lblEstado = new JLabel("Complete el formulario con sus datos personales");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setForeground(COLOR_PRIMARIO);
    }

    /**
     * Crear panel de título moderno centrado (igual que el login)
     */
    private void crearPanelTitulo() {
        panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(COLOR_PRIMARIO);
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        
        // Contenedor para centrar el título
        JPanel contenedorTitulo = new JPanel();
        contenedorTitulo.setLayout(new BoxLayout(contenedorTitulo, BoxLayout.Y_AXIS));
        contenedorTitulo.setBackground(COLOR_PRIMARIO);
        
        // Título principal
        JLabel lblTitulo = new JLabel("REGISTRO DE NUEVO USUARIO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_BLANCO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Complete sus datos personales para crear su cuenta de acceso");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(189, 195, 199));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Agregar componentes al contenedor
        contenedorTitulo.add(Box.createVerticalStrut(10));
        contenedorTitulo.add(lblTitulo);
        contenedorTitulo.add(Box.createVerticalStrut(8));
        contenedorTitulo.add(lblSubtitulo);
        contenedorTitulo.add(Box.createVerticalStrut(10));
        
        // Centrar el contenedor en el panel
        panelTitulo.add(contenedorTitulo, BorderLayout.CENTER);
    }

    /**
     * Crear panel de formulario
     */
    private void crearPanelFormulario() {
        panelFormulario = new JPanel();
        panelFormulario.setBackground(COLOR_BLANCO);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTituloForm = new JLabel("Información Personal");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloForm.setForeground(COLOR_PRIMARIO);
        
        

        // Campos del formulario
        JLabel lblNombreUsuario = new JLabel("Nombre de Usuario:");
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtNombreUsuario = crearCampoTextoModerno();
        txtNombreUsuario.setToolTipText("Nombre unico para el acceso al sistema");
        
        JLabel lblNombreCompleto = new JLabel("Nombre Completo:");
        lblNombreCompleto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtNombreCompleto = crearCampoTextoModerno();
        txtNombreCompleto.setToolTipText("Su nombre completo real");
        
        JLabel lblCorreo = new JLabel("Correo Electronico:");
        lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCorreo = crearCampoTextoModerno();
        txtCorreo.setToolTipText("Correo electronico valido para notificaciones");
        
        JLabel lblTelefono = new JLabel("Telefono:");
        lblTelefono.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtTelefono = crearCampoTextoModerno();
        txtTelefono.setToolTipText("Numero de telefono de contacto");
        
        JLabel lblContrasena = new JLabel("Contrasena:");
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtContrasena = crearCampoPasswordModerno();
        txtContrasena.setToolTipText("<html><b>Contraseña Segura Requerida:</b><br>" +
                                   "• Mínimo 8 caracteres<br>" +
                                   "• Al menos 1 minúscula (a-z)<br>" +
                                   "• Al menos 1 mayúscula (A-Z)<br>" +
                                   "• Al menos 1 número (0-9)<br>" +
                                   "• Al menos 1 carácter especial (!@#$%^&*)<br>" +
                                   "• No puede ser una contraseña común<br>" +
                                   "<b>Ejemplo:</b> MiContra123!</html>");
        
        JLabel lblConfirmar = new JLabel("Confirmar Contrasena:");
        lblConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtConfirmarContrasena = crearCampoPasswordModerno();
        txtConfirmarContrasena.setToolTipText("Repita exactamente la misma contraseña para confirmar");
        
        // Layout del formulario
        panelFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 15, 0);
        panelFormulario.add(lblTituloForm, gbc);
        
        // Agregar mensaje informativo
      
        
        // Configuración para labels: más a la izquierda con menos ancho
        gbc.gridwidth = 1; 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0; // No expandir los labels
        gbc.insets = new Insets(8, 10, 8, 5); // Menos margen izquierdo para labels
        
        // Configuración para campos: más anchos
        GridBagConstraints gbcCampos = new GridBagConstraints();
        gbcCampos.gridwidth = 1;
        gbcCampos.anchor = GridBagConstraints.WEST;
        gbcCampos.weightx = 1.0; // Expandir los campos
        gbcCampos.fill = GridBagConstraints.HORIZONTAL;
        gbcCampos.insets = new Insets(8, 5, 8, 20); // Más margen derecho para campos
        
        gbc.gridx = 0; gbc.gridy = 2; panelFormulario.add(lblNombreUsuario, gbc);
        gbcCampos.gridx = 1; gbcCampos.gridy = 2; panelFormulario.add(txtNombreUsuario, gbcCampos);
        
        gbc.gridx = 0; gbc.gridy = 3; panelFormulario.add(lblNombreCompleto, gbc);
        gbcCampos.gridx = 1; gbcCampos.gridy = 3; panelFormulario.add(txtNombreCompleto, gbcCampos);
        
        gbc.gridx = 0; gbc.gridy = 4; panelFormulario.add(lblCorreo, gbc);
        gbcCampos.gridx = 1; gbcCampos.gridy = 4; panelFormulario.add(txtCorreo, gbcCampos);
        
        gbc.gridx = 0; gbc.gridy = 5; panelFormulario.add(lblTelefono, gbc);
        gbcCampos.gridx = 1; gbcCampos.gridy = 5; panelFormulario.add(txtTelefono, gbcCampos);
        
        gbc.gridx = 0; gbc.gridy = 6; panelFormulario.add(lblContrasena, gbc);
        gbcCampos.gridx = 1; gbcCampos.gridy = 6; panelFormulario.add(txtContrasena, gbcCampos);
        
        gbc.gridx = 0; gbc.gridy = 7; panelFormulario.add(lblConfirmar, gbc);
        gbcCampos.gridx = 1; gbcCampos.gridy = 7; panelFormulario.add(txtConfirmarContrasena, gbcCampos);
        
        // Agregar nota informativa sobre contraseñas
        JLabel lblAyudaContrasena = new JLabel("<html><center><small><b>💡 Contraseña Segura:</b> 8+ caracteres, mayúsculas, minúsculas, números y símbolos</small></center></html>");
        lblAyudaContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblAyudaContrasena.setForeground(new Color(100, 100, 100));
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.insets = new Insets(10, 0, 0, 0);
        panelFormulario.add(lblAyudaContrasena, gbc);
    }

    /**
     * Crear panel de captura facial con scroll
     */
    private void crearPanelCaptura() {
        // Panel interno que contendrá todos los componentes con más espacio
        JPanel panelInternoCaptura = new JPanel();
        panelInternoCaptura.setLayout(new BoxLayout(panelInternoCaptura, BoxLayout.Y_AXIS));
        panelInternoCaptura.setBackground(COLOR_FONDO);
        panelInternoCaptura.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));
        
        // Permitir que el panel se redimensione según su contenido
        panelInternoCaptura.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Crear el JScrollPane que contendrá el panel interno con estilo mejorado
        JScrollPane scrollCaptura = new JScrollPane(panelInternoCaptura);
        scrollCaptura.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCaptura.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollCaptura.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollCaptura.getVerticalScrollBar().setUnitIncrement(16);
        scrollCaptura.getVerticalScrollBar().setBlockIncrement(50);
        scrollCaptura.setBackground(COLOR_FONDO);
        scrollCaptura.getViewport().setBackground(COLOR_FONDO);
        
        // Personalizar apariencia del scroll
        scrollCaptura.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scrollCaptura.getVerticalScrollBar().setBackground(new Color(240, 240, 240));
        scrollCaptura.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
        
        // Mejorar la suavidad del scroll
        scrollCaptura.setWheelScrollingEnabled(true);
        
        // Panel principal que contendrá el scroll
        panelCaptura = new JPanel(new BorderLayout());
        panelCaptura.setBackground(COLOR_FONDO);
        panelCaptura.add(scrollCaptura, BorderLayout.CENTER);
        
        // Título de la sección
        JLabel lblTituloCaptura = new JLabel("Captura Biométrica");
        lblTituloCaptura.setFont(FONT_SUBTITULO);
        lblTituloCaptura.setForeground(COLOR_PRINCIPAL);
        lblTituloCaptura.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel para la cámara MEJORADO - Estilo Premium HD
        JPanel panelCamara = new JPanel(new BorderLayout());
        panelCamara.setBackground(COLOR_FONDO);
        panelCamara.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Crear contenedor de video moderno
        crearContenedorVideoModerno(panelCamara);
        
        // Panel de botones de cámara - IGUAL AL LOGIN (BoxLayout vertical)
        JPanel panelBotonesCamara = new JPanel();
        panelBotonesCamara.setLayout(new BoxLayout(panelBotonesCamara, BoxLayout.Y_AXIS));
        panelBotonesCamara.setBackground(COLOR_FONDO);
        panelBotonesCamara.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Botón Activar Cámara PREMIUM - Estilo Login
        btnActivarCamara = new JButton("▶ ACTIVAR CÁMARA");
        btnActivarCamara.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActivarCamara.setBackground(new Color(187, 222, 251)); // Azul muy claro
        btnActivarCamara.setForeground(Color.BLACK);
        btnActivarCamara.setPreferredSize(new Dimension(300, 45));
        btnActivarCamara.setMaximumSize(new Dimension(300, 45));
        btnActivarCamara.setFocusPainted(false);
        btnActivarCamara.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnActivarCamara.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActivarCamara.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Efectos hover premium para cámara
        btnActivarCamara.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btnActivarCamara.isEnabled()) {
                    btnActivarCamara.setBackground(new Color(144, 202, 249));
                    btnActivarCamara.setForeground(Color.BLACK);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btnActivarCamara.isEnabled() && !btnActivarCamara.getText().contains("ACTIVA")) {
                    btnActivarCamara.setBackground(new Color(187, 222, 251));
                    btnActivarCamara.setForeground(Color.BLACK);
                }
            }
        });
        
        // Botón Capturar Muestra PREMIUM - Más visible
        btnCapturarMuestra = new JButton("📸 CAPTURAR ROSTRO");
        btnCapturarMuestra.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCapturarMuestra.setBackground(new Color(200, 230, 201)); // Verde claro
        btnCapturarMuestra.setForeground(Color.BLACK);
        btnCapturarMuestra.setPreferredSize(new Dimension(300, 45));
        btnCapturarMuestra.setMaximumSize(new Dimension(300, 45));
        btnCapturarMuestra.setFocusPainted(false);
        btnCapturarMuestra.setBorder(BorderFactory.createRaisedBevelBorder());
        btnCapturarMuestra.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCapturarMuestra.setEnabled(false);
        btnCapturarMuestra.setVisible(true);
        btnCapturarMuestra.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Efectos hover premium para captura
        btnCapturarMuestra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btnCapturarMuestra.isEnabled()) {
                    btnCapturarMuestra.setBackground(new Color(165, 214, 167));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btnCapturarMuestra.isEnabled()) {
                    btnCapturarMuestra.setBackground(new Color(200, 230, 201));
                }
            }
        });
        
        panelBotonesCamara.add(btnActivarCamara);
        panelBotonesCamara.add(Box.createVerticalStrut(10)); // Espacio entre botones como en login
        panelBotonesCamara.add(btnCapturarMuestra);
        
        // Debug: Verificar que los botones se agreguen correctamente
        System.out.println("DEBUG: Botones agregados al panel - Activar: " + btnActivarCamara.getText() + 
                          ", Capturar: " + btnCapturarMuestra.getText() + 
                          ", Habilitado: " + btnCapturarMuestra.isEnabled());
        
        // Panel de progreso
        JPanel panelProgreso = new JPanel(new BorderLayout());
        panelProgreso.setBackground(COLOR_FONDO);
        panelProgreso.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        progressCaptura = new JProgressBar(0, MUESTRAS_REQUERIDAS);
        progressCaptura.setStringPainted(true);
        progressCaptura.setString("0 / " + MUESTRAS_REQUERIDAS + " muestras");
        progressCaptura.setPreferredSize(new Dimension(200, 25));
        progressCaptura.setBackground(COLOR_FONDO);
        progressCaptura.setForeground(COLOR_ACENTO);
        
        lblMuestrasCapturadas = new JLabel("<html><center>📷 OPCIONAL: Active la cámara y capture " + MUESTRAS_REQUERIDAS + " imágenes (para simulación futura)<br><b>Puede registrarse sin esto</b></center></html>");
        lblMuestrasCapturadas.setFont(FONT_LABEL);
        lblMuestrasCapturadas.setForeground(new Color(100, 100, 100));
        lblMuestrasCapturadas.setHorizontalAlignment(SwingConstants.CENTER);
        
        // El contenedor de video ya se agregó en crearContenedorVideoModerno()
        // No agregamos lblCamara porque usamos el nuevo panelVideoContainer
        
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(COLOR_FONDO);
        panelInfo.add(progressCaptura, BorderLayout.CENTER);
        panelInfo.add(lblMuestrasCapturadas, BorderLayout.SOUTH);
        
        // Agregar componentes en orden vertical (BoxLayout) al panel interno
        lblTituloCaptura.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInternoCaptura.add(lblTituloCaptura);
        panelInternoCaptura.add(Box.createVerticalStrut(10));
        
        // Agregar contenedor de video centrado
        panelCamara.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInternoCaptura.add(panelCamara);
        panelInternoCaptura.add(Box.createVerticalStrut(10));
        
        // Agregar panel de botones centrado
        panelBotonesCamara.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInternoCaptura.add(panelBotonesCamara);
        panelInternoCaptura.add(Box.createVerticalStrut(10));
        
        // Agregar panel de información centrado
        panelInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInternoCaptura.add(panelInfo);
        
        // Agregar espacio adicional al final para mejor apariencia del scroll
        panelInternoCaptura.add(Box.createVerticalStrut(20));
    }

    /**
     * Crear panel de botones modernos
     */
    private void crearPanelBotones() {
        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(25, 0, 35, 0));
        
        // Botón Registrar Usuario - Verde claro moderno con mejor contraste
        btnRegistrar = crearBotonModerno("Registrar", 
            new Color(220, 245, 220), new Color(200, 230, 200));
        btnRegistrar.setEnabled(false);
        // Forzar texto negro siempre
        btnRegistrar.setForeground(Color.BLACK);
        
        // Listener para mantener el color negro al habilitar
        btnRegistrar.addPropertyChangeListener("enabled", evt -> {
            btnRegistrar.setForeground(Color.BLACK);
        });
        
        // Botón Cancelar - Rosa claro moderno
        btnCancelar = crearBotonModerno("Cancelar", 
            new Color(255, 205, 210), new Color(255, 171, 145));
        
        // Botón Volver al Login - Azul claro moderno
        btnVolverLogin = crearBotonModerno("Volver al Login", 
            new Color(187, 222, 251), new Color(144, 202, 249));
        
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
        
        // Panel de estado y botones en el sur
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(COLOR_FONDO);
        panelSur.add(lblEstado, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * Configurar eventos
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
        // DocumentListener para validación mientras se escribe
        javax.swing.event.DocumentListener validarFormulario = new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> validarYHabilitarRegistro());
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> validarYHabilitarRegistro());
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> validarYHabilitarRegistro());
            }
        };
        
        // Agregar listeners a todos los campos de texto
        txtNombreUsuario.getDocument().addDocumentListener(validarFormulario);
        txtNombreCompleto.getDocument().addDocumentListener(validarFormulario);
        txtCorreo.getDocument().addDocumentListener(validarFormulario);
        txtTelefono.getDocument().addDocumentListener(validarFormulario);
        txtContrasena.getDocument().addDocumentListener(validarFormulario);
        txtConfirmarContrasena.getDocument().addDocumentListener(validarFormulario);
        
        // Validación inicial
        SwingUtilities.invokeLater(() -> validarYHabilitarRegistro());
    }

    /**
     * 🪟 Configurar ventana
     */
    private void configurarVentana() {
        setTitle("Registro de Nuevo Usuario - Sistema de Autenticación");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Agregar listener para limpiar recursos al cerrar
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                limpiarRecursos();
                System.exit(0);
            }
        });
    }
    
    /**
     * Crear contenedor de video moderno estilo premium
     */
    private void crearContenedorVideoModerno(JPanel panelPadre) {
        // Crear contenedor principal de video - MAS GRANDE Y VISIBLE
        panelVideoContainer = new JPanel(new BorderLayout());
        panelVideoContainer.setBackground(new Color(44, 62, 80));
        panelVideoContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelVideoContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(149, 165, 166), 2),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        // Tamaño IGUAL al login para consistencia visual - 280x180
        panelVideoContainer.setPreferredSize(new Dimension(350, 250));
        panelVideoContainer.setMinimumSize(new Dimension(350, 250));
        panelVideoContainer.setMaximumSize(new Dimension(350, 250));
        
        // Vista previa de video mejorada
        lblVistaPrevia = new JLabel();
        lblVistaPrevia.setHorizontalAlignment(SwingConstants.CENTER);
        lblVistaPrevia.setVerticalAlignment(SwingConstants.CENTER);
        lblVistaPrevia.setOpaque(true);
        lblVistaPrevia.setBackground(new Color(44, 62, 80));
        lblVistaPrevia.setForeground(Color.WHITE);
        lblVistaPrevia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Estado inicial MAS VISIBLE con texto más claro
        mostrarEstadoCamaraModerno("<html><center><b>Camara Desconectada</b><br><br>Presione 'ACTIVAR CAMARA' para conectar</center></html>", new Color(231, 76, 60));
        
        panelVideoContainer.add(lblVistaPrevia, BorderLayout.CENTER);
        
        // Agregar al panel padre (el contenedor se agregará manualmente en crearPanelCaptura)
        panelPadre.add(panelVideoContainer, BorderLayout.CENTER);
        
        // Forzar repaint
        panelVideoContainer.revalidate();
        panelVideoContainer.repaint();
        
        // Inicializar timer para actualización de video HD
        inicializarTimerVideoHD();
        
        // Crear label de estado (compatible con código existente) - INVISIBLE
        lblCamara = new JLabel();
        lblCamara.setVisible(false);
        
        System.out.println("DEBUG: Contenedor de video creado con tamaño " + panelVideoContainer.getPreferredSize());
    }
    
    /**
     * Mostrar estado de la cámara con estilo visual premium
     */
    private void mostrarEstadoCamaraModerno(String mensaje, Color color) {
        System.out.println("DEBUG REGISTRO: Actualizando area camara - Mensaje: '" + mensaje + "' - Color: " + color);
        
        // Si el mensaje ya contiene HTML, usarlo tal como está
        if (mensaje.startsWith("<html>")) {
            lblVistaPrevia.setText(mensaje);
        } else {
            lblVistaPrevia.setText("<html><center>" + mensaje + "</center></html>");
        }
        lblVistaPrevia.setBackground(color);
        lblVistaPrevia.setIcon(null);
        
        // Forzar actualización visual
        lblVistaPrevia.revalidate();
        lblVistaPrevia.repaint();
        
        System.out.println("DEBUG REGISTRO: Area camara actualizada correctamente");
    }
    
    /**
     * ⚡ Inicializar timer para actualización de video HD
     */
    private void inicializarTimerVideoHD() {
        videoTimer = new Timer(33, e -> { // ~30 FPS para fluidez premium
            if (camaraActiva && manejadorCamara != null) {
                try {
                    BufferedImage frame = manejadorCamara.capturarImagenBuffered();
                    if (frame != null) {
                        // Redimensionar imagen para contenedor HD (mejorado)
                        Image imagenEscalada = frame.getScaledInstance(390, 290, Image.SCALE_SMOOTH);
                        ImageIcon icono = new ImageIcon(imagenEscalada);
                        
                        SwingUtilities.invokeLater(() -> {
                            lblVistaPrevia.setIcon(icono);
                            lblVistaPrevia.setText("");
                        });
                    }
                } catch (Exception ex) {
                    // Si hay error, mantener el estado pero no actualizar frame
                }
            }
        });
    }
    
    /**
     * Iniciar stream de video premium
     */
    private void iniciarStreamVideoHD() {
        if (videoTimer != null && !videoTimer.isRunning()) {
            videoTimer.start();
            mostrarEstadoCamaraModerno("Transmision Activa", new Color(46, 204, 113));
        }
    }
    
    /**
     * Detener stream de video  
     */
    private void detenerStreamVideoHD() {
        if (videoTimer != null && videoTimer.isRunning()) {
            videoTimer.stop();
        }
        mostrarEstadoCamaraModerno("Camara Desconectada", new Color(231, 76, 60));
    }

    /**
     * 🧹 Limpiar recursos antes de cerrar
     */
    private void limpiarRecursos() {
        detenerStreamVideo();
        if (manejadorCamara != null) {
            manejadorCamara.liberarCamara();
        }
    }

    /**
     * � Inicializar estado limpio del formulario
     */
    private void inicializarEstadoLimpio() {
        // Limpiar datos existentes
        if (muestrasFaciales != null) {
            muestrasFaciales.clear();
        }
        
        // Resetear componentes de captura
        if (progressCaptura != null) {
            progressCaptura.setValue(0);
            progressCaptura.setString("0 / " + MUESTRAS_REQUERIDAS + " muestras");
        }
        
        // Resetear botones
        if (btnCapturarMuestra != null) {
            btnCapturarMuestra.setEnabled(false);
            btnCapturarMuestra.setText("📸 CAPTURAR ROSTRO");
            btnCapturarMuestra.setBackground(new Color(200, 230, 201));
            btnCapturarMuestra.setForeground(Color.BLACK);
        }
        
        if (btnRegistrar != null) {
            btnRegistrar.setEnabled(false);
        }
        
        // Resetear estado de cámara
        camaraActiva = false;
        
        System.out.println("DEBUG: Estado inicial limpio establecido - Muestras: " + 
                          (muestrasFaciales != null ? muestrasFaciales.size() : "null"));
    }

    /**
     * �💬 Mostrar mensaje de bienvenida
     */
    private void mostrarMensajeBienvenida() {
        actualizarEstado("Bienvenido al registro del sistema");
        
        SwingUtilities.invokeLater(() -> {
            String mensaje = "CREAR NUEVA CUENTA DE USUARIO \ud83d\udc64\n\n" +
                    "PASOS PARA REGISTRARSE:\n" +
                    "1. ✅ Complete todos los campos del formulario (OBLIGATORIO)\n" +
                    "2. 📷 Active la cámara (OPCIONAL - para simulación)\n" +
                    "3. 📷 Capture " + MUESTRAS_REQUERIDAS + " imágenes (OPCIONAL)\n" +
                    "4. ✅ Haga clic en \"Registrar\" para crear su cuenta\n\n" +
                    "MÉTODOS DE ACCESO DISPONIBLES:\n" +
                    "✅ Usuario y contraseña (autenticación principal)\n" +
                    "📷 Reconocimiento facial (solo para demostración)\n\n" +
                    "NOTA: Puede registrarse sin capturar imágenes faciales.";
                    
            JOptionPane.showMessageDialog(this, mensaje, "Guía de Registro", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Activar cámara con estilo premium y diagnóstico completo
     */
    private void activarCamara() {
        if (!camaraActiva) {
            System.out.println("REGISTRO: Iniciando activación de cámara...");
            
            // Cambiar estilo del botón durante la carga
            btnActivarCamara.setText("CONECTANDO...");
            btnActivarCamara.setBackground(new Color(243, 156, 18)); // Naranja de carga
            btnActivarCamara.setEnabled(false);
            
            actualizarEstado("Activando cámara - Puede tomar unos segundos...");
            
            SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    // Mostrar mensajes de progreso dinámicos EXACTAMENTE como el login
                    String[] mensajes = {
                        "Buscando dispositivos de cámara...",
                        "Inicializando controladores...", 
                        "Configurando resolución...",
                        "Optimizando rendimiento...",
                        "Calibrando detección facial..."
                    };
                    
                    Timer progressTimer = new Timer(800, e -> {
                        int index = (int)(System.currentTimeMillis() / 800) % mensajes.length;
                        publish(mensajes[index]);
                    });
                    progressTimer.start();
                    
                    try {
                        System.out.println("REGISTRO: Ejecutando inicializarCamara()...");
                        boolean resultado = manejadorCamara.inicializarCamara();
                        System.out.println("REGISTRO: Resultado = " + resultado);
                        
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
                        
                        // Mostrar progreso en vista previa EXACTAMENTE como el login
                        mostrarEstadoCamaraModerno(ultimoMensaje, new Color(243, 156, 18));
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        boolean exito = get();
                        System.out.println("REGISTRO: Resultado final = " + exito);
                        
                        if (exito) {
                            camaraActiva = true;
                            
                            // Actualizar botón con estilo premium exitoso
                            btnActivarCamara.setText("CAMARA ACTIVA");
                            btnActivarCamara.setBackground(new Color(46, 204, 113)); // Verde exitoso
                            btnActivarCamara.setEnabled(false);
                            
                            // Habilitar y hacer más visible el botón de captura
                            btnCapturarMuestra.setEnabled(true);
                            btnCapturarMuestra.setVisible(true);
                            btnCapturarMuestra.setText("📸 CAPTURAR ROSTRO");
                            btnCapturarMuestra.setBackground(new Color(76, 175, 80)); // Verde más llamativo
                            btnCapturarMuestra.setForeground(Color.WHITE);
                            
                            // Debug: Verificar estado del botón
                            System.out.println("DEBUG: Botón captura habilitado - Enabled: " + 
                                             btnCapturarMuestra.isEnabled() + ", Visible: " + 
                                             btnCapturarMuestra.isVisible() + ", Texto: " + 
                                             btnCapturarMuestra.getText());
                            
                            // Forzar repaint del panel con scroll
                            panelCaptura.revalidate();
                            panelCaptura.repaint();
                            SwingUtilities.invokeLater(() -> {
                                panelCaptura.getComponent(0).revalidate(); // Revalidar el scroll
                            });
                            
                            // Actualizar mensaje instructivo
                            lblMuestrasCapturadas.setText("<html><center>📸 <b>¡Cámara lista!</b><br>Use el botón 'CAPTURAR ROSTRO' para tomar " + MUESTRAS_REQUERIDAS + " fotos<br>Mire directamente a la cámara para mejores resultados</center></html>");
                            lblMuestrasCapturadas.setForeground(new Color(46, 125, 50)); // Verde oscuro
                            
                            // Iniciar stream de video HD
                            iniciarStreamVideoHD();
                            
                            actualizarEstado("✅ Cámara activa - Use el botón CAPTURAR ROSTRO");
                            
                            System.out.println("REGISTRO: Cámara activada exitosamente");
                        } else {
                            // Restaurar botón con estilo de error
                            btnActivarCamara.setText("ACTIVAR CAMARA");
                            btnActivarCamara.setBackground(new Color(52, 152, 219));
                            btnActivarCamara.setEnabled(true);
                            
                            // Mostrar error en vista previa
                            detenerStreamVideoHD();
                            
                            System.out.println("REGISTRO: Fallo en activación de cámara");
                            mostrarError("No se pudo activar la cámara.\n\n" +
                                       "Posibles soluciones:\n" +
                                       "• Verifique que no esté siendo usada por otra aplicación\n" +
                                       "• Revise permisos de cámara en Windows\n" +
                                       "• Asegúrese de que los drivers estén instalados");
                            actualizarEstado("Error activando cámara");
                        }
                    } catch (Exception e) {
                        // Restaurar estado del botón
                        btnActivarCamara.setText("ACTIVAR CAMARA");
                        btnActivarCamara.setBackground(new Color(52, 152, 219));
                        btnActivarCamara.setEnabled(true);
                        
                        // Mostrar error en vista previa
                        mostrarEstadoCamaraModerno("Error de Conexion", new Color(231, 76, 60));
                        
                        System.err.println("REGISTRO: Excepción = " + e.getMessage());
                        e.printStackTrace();
                        mostrarError("Error activando cámara: " + e.getMessage());
                        actualizarEstado("Error en activación de cámara");
                    }
                }
            };
            
            worker.execute();
        } else {
            System.out.println("REGISTRO: Cámara ya está activa");
            actualizarEstado("Cámara ya está activa");
        }
    }

    /**
     * Capturar muestra facial
     */
    private void capturarMuestra() {
        if (!camaraActiva || capturaEnProceso) {
            return;
        }
        
        capturaEnProceso = true;
        actualizarEstado("Capturando muestra...");
        
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                // Captura real de la cámara
                return manejadorCamara.capturarImagenBuffered();
            }
            
            @Override
            protected void done() {
                try {
                    BufferedImage imagen = get();
                    if (imagen != null) {
                        muestrasFaciales.add(imagen);
                        actualizarProgreso();
                        mostrarExito("Muestra " + muestrasFaciales.size() + " capturada");
                        
                        if (muestrasFaciales.size() >= MUESTRAS_REQUERIDAS) {
                            btnCapturarMuestra.setEnabled(false);
                            btnCapturarMuestra.setText("✅ CAPTURAS COMPLETAS");
                            btnCapturarMuestra.setBackground(new Color(46, 204, 113)); // Verde éxito
                            btnCapturarMuestra.setForeground(Color.WHITE);
                            actualizarEstado("✅ Todas las muestras capturadas correctamente");
                            validarYHabilitarRegistro();
                        } else {
                            // Actualizar el texto del botón con el progreso
                            btnCapturarMuestra.setText("📸 CAPTURAR (" + muestrasFaciales.size() + "/" + MUESTRAS_REQUERIDAS + ")");
                        }
                    } else {
                        mostrarError("Error capturando imagen");
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
            lblMuestrasCapturadas.setText(">> Capturas completadas correctamente");
            lblMuestrasCapturadas.setForeground(COLOR_SECUNDARIO);
        }
    }

    /**
     * Registrar usuario
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
                
                // 🔄 Registrar usuario con o sin características faciales
                List<BufferedImage> muestrasValidas = null;
                
                // Filtrar solo muestras válidas si existen
                if (muestrasFaciales != null && !muestrasFaciales.isEmpty()) {
                    muestrasValidas = muestrasFaciales.stream()
                        .filter(m -> m != null)
                        .collect(java.util.stream.Collectors.toList());
                    
                    System.out.println("INFO: Registrando con " + muestrasValidas.size() + " muestras faciales");
                } else {
                    System.out.println("INFO: Registrando SIN muestras faciales");
                }
                
                return servicioUsuario.registrarUsuarioCompleto(usuario, muestrasValidas);
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
                    String mensajeError = "Error registrando usuario: " + e.getMessage();
                    
                    // Mejorar mensajes de error específicos
                    if (e.getMessage().contains("Contraseña no cumple con la política")) {
                        mensajeError = "❌ CONTRASEÑA NO SEGURA\n\n" +
                                     "La contraseña debe cumplir con los siguientes requisitos:\n" +
                                     "• Al menos 8 caracteres\n" +
                                     "• Una letra minúscula (a-z)\n" +
                                     "• Una letra mayúscula (A-Z)\n" +
                                     "• Un número (0-9)\n" +
                                     "• Un carácter especial (!@#$%^&*)\n" +
                                     "• No puede ser una contraseña común\n\n" +
                                     "Ejemplo: MiContra123!";
                    } else if (e.getMessage().contains("cifrar contraseña")) {
                        mensajeError = "❌ Error de seguridad: La contraseña no es lo suficientemente segura";
                    } else if (e.getMessage().contains("base de datos") || e.getMessage().contains("SQL")) {
                        mensajeError = "❌ Error de conexión con la base de datos. Intente nuevamente";
                    } else if (e.getMessage().contains("usuario ya existe")) {
                        mensajeError = "❌ El nombre de usuario ya está en uso. Elija otro nombre";
                    }
                    
                    mostrarError(mensajeError);
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
        int muestrasCount = (muestrasFaciales != null) ? muestrasFaciales.size() : 0;
        String mensaje = "🎉 ¡CUENTA CREADA EXITOSAMENTE!\n\n" +
                "Usuario: " + txtNombreUsuario.getText() + "\n" +
                "Nombre: " + txtNombreCompleto.getText() + "\n" +
                "Email: " + txtCorreo.getText() + "\n" +
                "Imágenes guardadas: " + muestrasCount + " (para simulación)\n\n" +
                "MÉTODOS DE ACCESO DISPONIBLES:\n" +
                "✅ Usuario y contraseña (principal)\n" +
                "📷 Reconocimiento facial (simulación)\n\n" +
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
        
        // Validar política de contraseñas (antes de cifrarla)
        String mensajeValidacion = validarPoliticaContrasena(contrasena);
        if (mensajeValidacion != null) {
            mostrarError(mensajeValidacion);
            txtContrasena.requestFocus();
            return false;
        }
        
        if (!contrasena.equals(confirmacion)) {
            mostrarError("Las contraseñas no coinciden");
            txtConfirmarContrasena.requestFocus();
            return false;
        }
        
        // 🔧 RECONOCIMIENTO FACIAL OPCIONAL - No bloquea el registro
        // Mostrar advertencia si no hay muestras suficientes pero permitir continuar
        if (muestrasFaciales == null || muestrasFaciales.size() < MUESTRAS_REQUERIDAS) {
            System.out.println("INFO: Registro con credenciales únicamente - Muestras faciales: " + 
                             (muestrasFaciales != null ? muestrasFaciales.size() : 0) + 
                             "/" + MUESTRAS_REQUERIDAS + " (Opcional)");
            
            // Mostrar mensaje informativo que es normal registrarse sin reconocimiento facial
            JOptionPane.showMessageDialog(
                this,
                "ℹ️ REGISTRO POR CREDENCIALES\n\n" +
                "Su cuenta se creará con autenticación por credenciales:\n" +
                "• Usuario y contraseña (principal)\n" +
                "• Reconocimiento facial (opcional para uso futuro)\n\n" +
                "Las imágenes faciales se guardarán para simular\n" +
                "el funcionamiento del sistema biométrico.",
                "Información de Registro",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            // Validar que las muestras existentes no sean nulas
            long muestrasValidas = muestrasFaciales.stream().filter(m -> m != null).count();
            if (muestrasValidas < muestrasFaciales.size()) {
                System.out.println("AVISO: Algunas muestras son inválidas, se registrará con las válidas");
            }
        }
        
        System.out.println("DEBUG: Validación exitosa - Todas las muestras son válidas");
        return true;
    }

    /**
     * 🔄 Validar y habilitar botón de registro
     */
    private void validarYHabilitarRegistro() {
        try {
            // Obtener valores de los campos
            String nombreUsuario = txtNombreUsuario.getText().trim();
            String nombreCompleto = txtNombreCompleto.getText().trim();
            String correo = txtCorreo.getText().trim();
            char[] contrasena = txtContrasena.getPassword();
            char[] confirmarContrasena = txtConfirmarContrasena.getPassword();
            
            // ✅ Validación de campos obligatorios (teléfono es opcional)
            boolean camposCompletos = !nombreUsuario.isEmpty() &&
                                    !nombreCompleto.isEmpty() &&
                                    !correo.isEmpty() &&
                                    contrasena.length >= 6;
            
            // ✅ Validación de coincidencia de contraseñas
            boolean contrasenasCoinciden = java.util.Arrays.equals(contrasena, confirmarContrasena);
            
            // ✅ Validación básica de formato de email
            boolean emailValido = correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
            
            // ✅ Validación de política de contraseñas (sincronizada con backend)
            String contrasenaStr = new String(contrasena);
            boolean contrasenaValida = validarPoliticaContrasenaCompleta(contrasenaStr) == null;
            
            // Formulario válido si todos los campos están completos y las contraseñas coinciden
            boolean formValido = camposCompletos && contrasenasCoinciden && emailValido && contrasenaValida;
            
            // Activar/desactivar botón
            btnRegistrar.setEnabled(formValido);
            
            if (formValido) {
                btnRegistrar.setBackground(COLOR_SECUNDARIO);
                btnRegistrar.setForeground(Color.BLACK);
                
                // Mensaje dinámico según si tiene muestras faciales
                String estado;
                if (muestrasFaciales != null && muestrasFaciales.size() >= MUESTRAS_REQUERIDAS) {
                    estado = "✅ Listo para registrar CON reconocimiento facial";
                } else {
                    estado = "✅ Listo para registrar SIN reconocimiento facial (opcional)";
                }
                actualizarEstado(estado);
            } else {
                btnRegistrar.setBackground(Color.LIGHT_GRAY);
                btnRegistrar.setForeground(Color.DARK_GRAY);
                
                // Mensaje de qué falta
                if (!camposCompletos) {
                    actualizarEstado("⚠️ Complete todos los campos obligatorios");
                } else if (!contrasenasCoinciden) {
                    actualizarEstado("⚠️ Las contraseñas no coinciden");
                } else if (!emailValido) {
                    actualizarEstado("⚠️ Formato de email inválido");
                } else if (!contrasenaValida) {
                    String mensajeError = validarPoliticaContrasenaCompleta(contrasenaStr);
                    actualizarEstado("⚠️ " + mensajeError);
                }
            }
            
            // Limpiar arrays de contraseñas por seguridad
            java.util.Arrays.fill(contrasena, ' ');
            java.util.Arrays.fill(confirmarContrasena, ' ');
            
        } catch (Exception e) {
            System.err.println("Error en validación: " + e.getMessage());
            btnRegistrar.setEnabled(false);
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
        lblMuestrasCapturadas.setText("📷 OPCIONAL: Capture " + MUESTRAS_REQUERIDAS + " imágenes (para simulación)");
        lblMuestrasCapturadas.setForeground(Color.BLACK);
        
        btnCapturarMuestra.setEnabled(false);
        btnCapturarMuestra.setText("📸 CAPTURAR MUESTRA");
        btnRegistrar.setEnabled(false);
        
        if (camaraActiva) {
            btnActivarCamara.setText("ACTIVAR CÁMARA");
            btnActivarCamara.setEnabled(true);
            camaraActiva = false;
        }
        
        txtNombreUsuario.requestFocus();
        actualizarEstado("🔄 Formulario limpiado - Listo para nuevo registro");
    }

    /**
     * � Validar política de contraseñas
     */
    private String validarPoliticaContrasena(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            return "La contraseña es requerida";
        }
        
        if (contrasena.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        
        if (contrasena.length() > 128) {
            return "La contraseña no puede tener más de 128 caracteres";
        }
        
        // Verificar que tenga al menos una letra minúscula
        if (!contrasena.matches(".*[a-z].*")) {
            return "La contraseña debe contener al menos una letra minúscula";
        }
        
        // Verificar que tenga al menos una letra mayúscula
        if (!contrasena.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una letra mayúscula";
        }
        
        // Verificar que tenga al menos un número
        if (!contrasena.matches(".*[0-9].*")) {
            return "La contraseña debe contener al menos un número";
        }
        
        // Verificar que tenga al menos un carácter especial
        if (!contrasena.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return "La contraseña debe contener al menos un carácter especial (!@#$%^&*()_+-=[]{}|;':\"\\\\,.<>?)";
        }
        
        // Verificar contraseñas comunes (las más básicas)
        String[] contrasenasComunes = {
            "123456", "password", "123456789", "12345678", "12345", "1234567", "qwerty",
            "abc123", "password123", "admin", "administrator", "123123", "welcome",
            "login", "guest", "hello", "default", "changeme", "test", "temp"
        };
        
        String contrasenaLower = contrasena.toLowerCase();
        for (String comun : contrasenasComunes) {
            if (contrasenaLower.equals(comun)) {
                return "La contraseña es demasiado común. Use una contraseña más segura";
            }
        }
        
        // Verificar patrones simples
        if (contrasena.matches("^[0-9]+$")) {
            return "La contraseña no puede ser solo números";
        }
        
        if (contrasena.matches("^[a-zA-Z]+$")) {
            return "La contraseña no puede ser solo letras";
        }
        
        // Verificar secuencias repetitivas
        if (contrasena.matches(".*(.)\\1{2,}.*")) {
            return "La contraseña no puede tener más de 2 caracteres iguales consecutivos";
        }
        
        return null; // Contraseña válida
    }

    /**
     * �📊 Actualizar estado
     */
    private void actualizarEstado(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
            lblEstado.setForeground(COLOR_PRIMARIO);
        });
    }

    /**
     * Mostrar mensaje de exito
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
     * 🎥 Iniciar stream de video en tiempo real
     */
    private void iniciarStreamVideo() {
        if (videoTimer != null) {
            videoTimer.stop();
        }
        
        // Timer para actualizar el video cada 50ms (20 FPS)
        videoTimer = new javax.swing.Timer(50, e -> actualizarFrameVideo());
        videoTimer.start();
        
        System.out.println("🎥 Stream de video iniciado");
    }
    
    /**
     * 🔄 Actualizar frame de video
     */
    private void actualizarFrameVideo() {
        if (!camaraActiva) {
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                BufferedImage imagen = manejadorCamara.capturarImagenBuffered();
                if (imagen != null) {
                    // Escalar imagen para mostrar en el label
                    Image imagenEscalada = imagen.getScaledInstance(
                        lblCamara.getWidth() - 10, 
                        lblCamara.getHeight() - 10, 
                        Image.SCALE_FAST
                    );
                    
                    // Crear ImageIcon y asignarlo al label
                    ImageIcon icon = new ImageIcon(imagenEscalada);
                    lblCamara.setIcon(icon);
                    lblCamara.setText(""); // Quitar el texto para mostrar solo la imagen
                    
                } else {
                    // Si no hay imagen, mostrar mensaje de error
                    lblCamara.setIcon(null);
                    lblCamara.setText("❌ ERROR EN CÁMARA");
                    lblCamara.setBackground(COLOR_PELIGRO);
                }
                
            } catch (Exception ex) {
                System.err.println("❌ Error actualizando video: " + ex.getMessage());
                lblCamara.setIcon(null);
                lblCamara.setText("⚠️ ERROR VIDEO");
            }
        });
    }
    
    /**
     * Detener stream de video
     */
    private void detenerStreamVideo() {
        if (videoTimer != null) {
            videoTimer.stop();
            videoTimer = null;
        }
        
        // Limpiar el label
        lblCamara.setIcon(null);
        lblCamara.setText("CÁMARA DETENIDA");
        lblCamara.setBackground(new Color(245, 245, 245));
        
        System.out.println(">> Stream de video detenido");
    }

    /**
     * Crear campo de texto moderno con efectos
     */
    private JTextField crearCampoTextoModerno() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(450, 40)); // Más ancho: de 380 a 450
        campo.setMinimumSize(new Dimension(450, 40));   // Asegurar tamaño mínimo
        
        // Colores más visibles para los bordes
        Color bordeNormal = new Color(150, 150, 150);  // Gris más oscuro
        Color bordeFocus = new Color(100, 149, 237);   // Azul focus
        
        // Fondo blanco para mejor contraste
        campo.setBackground(Color.WHITE);
        campo.setOpaque(true);
        
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bordeNormal, 2),  // Borde más grueso
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Efectos de focus
        campo.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bordeFocus, 2),
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bordeNormal, 2),  // Mantener borde visible
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
        
        return campo;
    }

    /**
     * Crear campo de contraseña moderno con efectos
     */
    private JPasswordField crearCampoPasswordModerno() {
        JPasswordField campo = new JPasswordField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(450, 40)); // Más ancho: de 380 a 450
        campo.setMinimumSize(new Dimension(450, 40));   // Asegurar tamaño mínimo
        
        // Colores más visibles para los bordes
        Color bordeNormal = new Color(150, 150, 150);  // Gris más oscuro
        Color bordeFocus = new Color(100, 149, 237);   // Azul focus
        
        // Fondo blanco para mejor contraste
        campo.setBackground(Color.WHITE);
        campo.setOpaque(true);
        
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bordeNormal, 2),  // Borde más grueso
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Efectos de focus
        campo.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bordeFocus, 2),
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bordeNormal, 2),  // Mantener borde visible
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
        
        return campo;
    }

    /**
     * Crear botón moderno con efectos hover
     */
    private JButton crearBotonModerno(String texto, Color colorNormal, Color colorHover) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(colorNormal);
        boton.setForeground(Color.BLACK); // Usar Color.BLACK directamente
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 40));
        boton.setOpaque(true); // Asegurar que sea opaco
        
        // Bordes redondeados simulados con padding
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorNormal, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Efectos hover modernos
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (boton.isEnabled()) {
                    boton.setBackground(colorHover);
                    boton.setForeground(Color.BLACK); // Usar Color.BLACK directamente
                    boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorHover, 2),
                        BorderFactory.createEmptyBorder(9, 19, 9, 19)
                    ));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (boton.isEnabled()) {
                    boton.setBackground(colorNormal);
                    boton.setForeground(Color.BLACK); // Usar Color.BLACK directamente
                    boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorNormal, 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                    ));
                }
            }
        });
        
        return boton;
    }

    /**
     * � Validar política completa de contraseñas (sincronizada con backend)
     */
    private String validarPoliticaContrasenaCompleta(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            return "La contraseña es requerida";
        }
        
        // Longitud mínima y máxima
        if (contrasena.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        
        if (contrasena.length() > 128) {
            return "La contraseña no puede tener más de 128 caracteres";
        }
        
        // Verificar mayúsculas
        if (!contrasena.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una letra mayúscula";
        }
        
        // Verificar minúsculas
        if (!contrasena.matches(".*[a-z].*")) {
            return "La contraseña debe contener al menos una letra minúscula";
        }
        
        // Verificar números
        if (!contrasena.matches(".*[0-9].*")) {
            return "La contraseña debe contener al menos un número";
        }
        
        // Verificar contraseñas comunes (sincronizado con backend)
        String contrasenaLower = contrasena.toLowerCase();
        String[] contrasenasComunes = {
            "password", "123456", "12345678", "qwerty", "abc123",
            "password123", "admin", "letmein", "welcome", "monkey",
            "dragon", "master", "shadow", "superman", "michael",
            "football", "baseball", "soccer", "charlie", "jordan"
        };
        
        for (String comun : contrasenasComunes) {
            if (contrasenaLower.contains(comun)) {
                return "La contraseña es demasiado común. Use una más segura.";
            }
        }
        
        // Verificar secuencias comunes
        if (contrasenaLower.matches(".*123.*") || contrasenaLower.matches(".*abc.*")) {
            return "La contraseña no puede contener secuencias obvias (123, abc)";
        }
        
        // Verificar caracteres repetidos
        if (contrasena.matches(".*(.)\\1{2,}.*")) {
            return "La contraseña no puede tener más de 3 caracteres iguales consecutivos";
        }
        
        return null; // Contraseña válida
    }

    /**
     * �🚀 Método principal para pruebas
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