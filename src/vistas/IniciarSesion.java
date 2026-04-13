package vistas;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class IniciarSesion extends JFrame {

    private JTextField txtId;
    private JPasswordField txtPin;
    private JButton btnIngresar;
    private JPanel panelPrincipal;

    private final Color COLOR_FONDO = new Color(240, 240, 240);
    private final Color COLOR_BOTON_NUM = Color.WHITE;
    private final Color COLOR_BOTON_NUM_HOVER = new Color(225, 225, 225);
    private final Color COLOR_BOTON_INGRESAR = new Color(41, 128, 185);
    private final Color COLOR_BOTON_INGRESAR_HOVER = new Color(52, 152, 219);
    private final Color COLOR_BORDE = new Color(200, 200, 200);
    
    // Nuestro contador de intentos de seguridad
    private int intentosFallidos = 0;

    public IniciarSesion() {
        setTitle("SGO Restaurant - Inicio de Sesión");
        setSize(400, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        panelPrincipal = new JPanel();
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setLayout(null);
        add(panelPrincipal);

        // --- SECCIÓN 1: EL LOGO ESCALADO ---
        JLabel lblLogo = new JLabel("", SwingConstants.CENTER);
        lblLogo.setBounds(100, 30, 200, 100);
        lblLogo.setBorder(new LineBorder(COLOR_BORDE, 1));
        
        try {
            ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/img/logo.png"));
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(imagenEscalada));
        } catch (Exception e) {
            lblLogo.setText("LOGO NO ENCONTRADO");
        }
        panelPrincipal.add(lblLogo);

        // --- SECCIÓN 2: CAJAS DE TEXTO ---
        Font fuenteEtiqueta = new Font("Segoe UI", Font.PLAIN, 14);
        Font fuenteCaja = new Font("Segoe UI", Font.BOLD, 18);

        JLabel lblId = new JLabel("ID de Empleado:");
        lblId.setBounds(50, 150, 300, 20);
        lblId.setFont(fuenteEtiqueta);
        panelPrincipal.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(50, 170, 300, 35);
        txtId.setFont(fuenteCaja);
        txtId.setBorder(new LineBorder(COLOR_BORDE, 1));
        panelPrincipal.add(txtId);

        JLabel lblPin = new JLabel("PIN de Acceso:");
        lblPin.setBounds(50, 220, 300, 20);
        lblPin.setFont(fuenteEtiqueta);
        panelPrincipal.add(lblPin);

        txtPin = new JPasswordField();
        txtPin.setBounds(50, 240, 300, 35);
        txtPin.setFont(fuenteCaja);
        txtPin.setBorder(new LineBorder(COLOR_BORDE, 1));
        panelPrincipal.add(txtPin);

        // --- LÓGICA DE TECLADO (El Cerebro) ---
        ActionListener accionTeclado = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numeroPulsado = ((JButton) e.getSource()).getText();
                if (txtPin.hasFocus()) {
                    txtPin.setText(new String(txtPin.getPassword()) + numeroPulsado);
                } else {
                    txtId.setText(txtId.getText() + numeroPulsado);
                    txtId.requestFocus(); 
                }
            }
        };

        // --- SECCIÓN 3: TECLADO NUMÉRICO ---
        int xIni = 85, yIni = 300, anchoBtn = 65, altoBtn = 65, hueco = 15;
        Font fuenteNumeros = new Font("Segoe UI", Font.BOLD, 22);
        String[][] numeros = { {"7", "8", "9"}, {"4", "5", "6"}, {"1", "2", "3"} };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton(numeros[i][j]);
                btn.setBounds(xIni + (j * (anchoBtn + hueco)), yIni + (i * (altoBtn + hueco)), anchoBtn, altoBtn);
                formatNumerButton(btn, fuenteNumeros);
                btn.addActionListener(accionTeclado); 
                panelPrincipal.add(btn);
            }
        }

        JButton btnLimpiar = new JButton("C");
        btnLimpiar.setBounds(xIni + (0 * (anchoBtn + hueco)), yIni + (3 * (altoBtn + hueco)), anchoBtn, altoBtn);
        formatNumerButton(btnLimpiar, fuenteNumeros);
        btnLimpiar.setForeground(new Color(192, 57, 43)); 
        btnLimpiar.addActionListener(e -> {
            if (txtPin.hasFocus()) txtPin.setText("");
            else { txtId.setText(""); txtId.requestFocus(); }
        });
        panelPrincipal.add(btnLimpiar);

        JButton btn0 = new JButton("0");
        btn0.setBounds(xIni + (1 * (anchoBtn + hueco)), yIni + (3 * (altoBtn + hueco)), anchoBtn, altoBtn);
        formatNumerButton(btn0, fuenteNumeros);
        btn0.addActionListener(accionTeclado);
        panelPrincipal.add(btn0);

        JButton btnBorrar = new JButton("<-");
        btnBorrar.setBounds(xIni + (2 * (anchoBtn + hueco)), yIni + (3 * (altoBtn + hueco)), anchoBtn, altoBtn);
        formatNumerButton(btnBorrar, fuenteNumeros);
        btnBorrar.addActionListener(e -> {
            JTextField caja = txtPin.hasFocus() ? txtPin : txtId;
            String texto = caja.getText();
            if (texto.length() > 0) {
                caja.setText(texto.substring(0, texto.length() - 1));
            }
        });
        panelPrincipal.add(btnBorrar);

        // --- SECCIÓN 4: BOTÓN INGRESAR ---
        btnIngresar = new JButton("INGRESAR");
        btnIngresar.setBounds(90, 630, 220, 45);
        btnIngresar.setBackground(COLOR_BOTON_INGRESAR);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnIngresar.setBorderPainted(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnIngresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnIngresar.setBackground(COLOR_BOTON_INGRESAR_HOVER); }
            @Override
            public void mouseExited(MouseEvent e) { btnIngresar.setBackground(COLOR_BOTON_INGRESAR); }
        });

        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idTexto = txtId.getText();
                String pinTexto = new String(txtPin.getPassword());

                if (idTexto.isEmpty() || pinTexto.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Por favor, ingresa tu ID y PIN.", "Campos vacíos", javax.swing.JOptionPane.WARNING_MESSAGE);
                    return; 
                }

                try {
                    int idEmpleado = Integer.parseInt(idTexto);

                    dao.UsuarioDAO baseDatos = new dao.UsuarioDAO();
                    modelo.Usuario empleado = baseDatos.iniciarSesion(idEmpleado, pinTexto);

                    if (empleado != null) {
                        intentosFallidos = 0; 
                        
                        // --- ¡EL ENRUTADOR MAESTRO! ---
                        String rolUsuario = empleado.getRol();
                        
                        switch (rolUsuario) {
                            case "Mesero":
                                PanelMesero ventanaMesero = new PanelMesero();
                                ventanaMesero.setIdMeseroActual(idEmpleado); // ¡Le pasamos el ID real!
                                ventanaMesero.setVisible(true); 
                                dispose(); // Cerramos el Login
                                break;
                                
                            case "Cocinero":
                                PanelCocinero ventanaCocinero = new PanelCocinero();
                                ventanaCocinero.setVisible(true);
                                dispose(); // Cerramos el Login
                                break;
                                
                            case "Administrador":
                                javax.swing.JOptionPane.showMessageDialog(null, "Bienvenido Administrador. Pronto construiremos tu Panel.");
                                break;
                                
                            case "Cajero":
                                javax.swing.JOptionPane.showMessageDialog(null, "Bienvenido Cajero. Pronto construiremos tu Panel.");
                                break;
                        }
                        
                    } else {
                        // FRACASO
                        intentosFallidos++;
                        
                        if (intentosFallidos >= 3) {
                            javax.swing.JOptionPane.showMessageDialog(null, 
                                "Acceso denegado, se han agotado los intentos", 
                                "Acceso Bloqueado", 
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(null, 
                                "Credenciales incorrectas", 
                                "Error de Acceso", 
                                javax.swing.JOptionPane.WARNING_MESSAGE);
                            
                            txtPin.setText("");
                            txtPin.requestFocus();
                        }
                    }

                } catch (NumberFormatException ex) {
                    javax.swing.JOptionPane.showMessageDialog(null, "El ID debe ser un número entero.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panelPrincipal.add(btnIngresar);
        txtId.requestFocus(); 
    }

    private void formatNumerButton(JButton btn, Font fuente) {
        btn.setFont(fuente);
        btn.setBackground(COLOR_BOTON_NUM);
        btn.setForeground(Color.BLACK);
        btn.setBorder(new LineBorder(COLOR_BORDE, 1));
        btn.setFocusPainted(false);
        btn.setFocusable(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_BOTON_NUM_HOVER); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(COLOR_BOTON_NUM); }
        });
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        IniciarSesion ventana = new IniciarSesion();
        ventana.setVisible(true);
    }
}