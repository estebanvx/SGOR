package vistas;

import conexion.Conexion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class PanelCocinero extends JFrame {

    // --- PALETA DE COLORES FLAT (RESPECTANDO TUS MOCKUPS) ---
    private final Color COLOR_FONDO_FRAME = new Color(241, 241, 241); 
    private final Color COLOR_PANEL_BLANCO = Color.WHITE; 
    private final Color COLOR_FLAT_NARANJA = new Color(230, 126, 34); 
    private final Color COLOR_FLAT_VERDE = new Color(39, 174, 96);   
    private final Color COLOR_FLAT_AZUL = new Color(41, 128, 185);   // ¡NUEVO AZUL! Belize Hole Flat
    private final Color COLOR_FLAT_GRIS = new Color(189, 195, 199);  
    private final Color COLOR_FLAT_GRIS_BORDE = new Color(220, 220, 220); 

    // Componentes que cambian dinámicamente
    private JPanel panelCuadriculaPedidos;
    private JLabel lblTituloDetalles;
    private JTextArea txtDetallesPedido;
    private JButton btnEstadoCocinero;
    private JButton btnNotificarMesero; // Este será solo ícono, cuadrado y azul

    // Imagen del ícono redimensionada
    private ImageIcon iconoCampanaFinal;

    // Memoria del pedido seleccionado
    private int pedidoSeleccionadoId = 0;
    private int mesaSeleccionadaNum = 0;
    private String estadoPedidoSeleccionado = "";

    public PanelCocinero() {
        setTitle("SGO Restaurant - KDS Panel de Cocinero");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15)); 
        getContentPane().setBackground(COLOR_FONDO_FRAME);

        // =========================================================
        // CARGAR Y REDIMENSIONAR EL ÍCONO DE LA CAMPANA (/img/campana.png)
        // =========================================================
        URL iconURL = getClass().getResource("/img/campana.png"); 
        if (iconURL != null) {
            ImageIcon iconoOriginal = new ImageIcon(iconURL);
            // ¡MAGIA! Redimensionamos a 40x40 para que se adapte al botón cuadrado de 60x60
            java.awt.Image imgEscalada = iconoOriginal.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
            iconoCampanaFinal = new ImageIcon(imgEscalada);
        } else {
            System.err.println("Error: No se encontró el archivo src/img/campana.png");
        }

        // =========================================================
        // ENCABEZADO
        // =========================================================
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(COLOR_PANEL_BLANCO);
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_FLAT_GRIS_BORDE)); 
        
        JLabel lblTitulo = new JLabel("RESTAURANTE: COCINA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26)); 
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setContentAreaFilled(false); 
        btnActualizar.addActionListener(e -> cargarPedidosActivos());
        
        JPanel panelBotonHeader = new JPanel();
        panelBotonHeader.setBackground(COLOR_PANEL_BLANCO);
        panelBotonHeader.add(btnActualizar);
        panelHeader.add(panelBotonHeader, BorderLayout.EAST);
        
        add(panelHeader, BorderLayout.NORTH);

        // =========================================================
        // PANEL IZQUIERDO: DETALLES 
        // =========================================================
        JPanel panelIzquierdoContenedor = new JPanel(new BorderLayout(10, 10));
        panelIzquierdoContenedor.setPreferredSize(new Dimension(320, 0));
        panelIzquierdoContenedor.setBackground(COLOR_PANEL_BLANCO);
        panelIzquierdoContenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 20, 0), 
                new LineBorder(COLOR_FLAT_GRIS_BORDE, 1) 
        ));

        JPanel panelInteriorIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelInteriorIzquierdo.setBackground(COLOR_PANEL_BLANCO);
        panelInteriorIzquierdo.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel lblImagenMock = new JLabel("IMAGEN", SwingConstants.CENTER);
        lblImagenMock.setPreferredSize(new Dimension(280, 160));
        lblImagenMock.setOpaque(true);
        lblImagenMock.setBackground(new Color(230, 230, 230)); 
        lblImagenMock.setBorder(new LineBorder(COLOR_FLAT_GRIS_BORDE));
        lblImagenMock.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblImagenMock.setForeground(Color.GRAY);
        panelInteriorIzquierdo.add(lblImagenMock, BorderLayout.NORTH);

        lblTituloDetalles = new JLabel("Detalles mesa #", SwingConstants.CENTER);
        lblTituloDetalles.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        txtDetallesPedido = new JTextArea();
        txtDetallesPedido.setFont(new Font("Monospaced", Font.PLAIN, 16)); 
        txtDetallesPedido.setEditable(false);
        txtDetallesPedido.setLineWrap(true);
        txtDetallesPedido.setWrapStyleWord(true);
        
        JScrollPane scrollDetalles = new JScrollPane(txtDetallesPedido);
        scrollDetalles.setBorder(null); 
        
        JPanel panelCentroDetalles = new JPanel(new BorderLayout(5, 10));
        panelCentroDetalles.setBackground(COLOR_PANEL_BLANCO);
        panelCentroDetalles.add(lblTituloDetalles, BorderLayout.NORTH);
        panelCentroDetalles.add(scrollDetalles, BorderLayout.CENTER);
        
        panelInteriorIzquierdo.add(panelCentroDetalles, BorderLayout.CENTER);

        // --- PANEL DE BOTONES INFERIORES (¡NUEVO DISEÑO ASIMÉTRICO!) ---
        // Usamos BorderLayout para que un botón se estire y el otro sea fijo y cuadrado
        JPanel panelBotonesAccion = new JPanel(new BorderLayout(10, 0)); 
        panelBotonesAccion.setBackground(COLOR_PANEL_BLANCO);
        panelBotonesAccion.setPreferredSize(new Dimension(280, 60)); // Altura fija de 60

        // Botón 1: Estado (CENTER se estira ocupando el espacio restante)
        btnEstadoCocinero = crearBotonFlat("Seleccione pedido", COLOR_FLAT_GRIS, null);
        btnEstadoCocinero.setEnabled(false);
        btnEstadoCocinero.addActionListener(e -> cambiarEstadoPedido());
        panelBotonesAccion.add(btnEstadoCocinero, BorderLayout.CENTER);

        // Botón 2: Campanita (EAST es cuadrado, fijo 60x60)
        btnNotificarMesero = crearBotonFlat("", null, iconoCampanaFinal); 
        btnNotificarMesero.setPreferredSize(new Dimension(60, 60)); // Cuadrado perfecto ligeramente mayor a la imagen
        btnNotificarMesero.setEnabled(false);
        btnNotificarMesero.addActionListener(e -> notificarMeseroYLimpiar());
        panelBotonesAccion.add(btnNotificarMesero, BorderLayout.EAST);

        panelInteriorIzquierdo.add(panelBotonesAccion, BorderLayout.SOUTH);

        panelIzquierdoContenedor.add(panelInteriorIzquierdo, BorderLayout.CENTER);
        add(panelIzquierdoContenedor, BorderLayout.WEST);

        // =========================================================
        // PANEL DERECHO: CUADRÍCULA 
        // =========================================================
        JPanel panelDerechoContenedor = new JPanel(new BorderLayout());
        panelDerechoContenedor.setBackground(COLOR_PANEL_BLANCO);
        panelDerechoContenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 20, 20),
                new LineBorder(COLOR_FLAT_GRIS_BORDE, 1)
        ));

        panelCuadriculaPedidos = new JPanel(new GridLayout(0, 3, 20, 20)); 
        panelCuadriculaPedidos.setBackground(COLOR_PANEL_BLANCO);
        panelCuadriculaPedidos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPedidos = new JScrollPane(panelCuadriculaPedidos);
        scrollPedidos.setBorder(null); 
        panelDerechoContenedor.add(scrollPedidos, BorderLayout.CENTER);

        add(panelDerechoContenedor, BorderLayout.CENTER);

        cargarPedidosActivos();
    }

    // =========================================================
    // MÉTODOS AUXILIARES DE DISEÑO
    // =========================================================
    
    // Crea un botón con estilo plano (flat). Acepta texto O ícono.
    private JButton crearBotonFlat(String texto, Color colorFondo, ImageIcon icono) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        if (icono != null) {
            btn.setIcon(icono);
            btn.setHorizontalAlignment(SwingConstants.CENTER); // Centramos el icono
        }
        
        // Estilo básico
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Si el color es nulo, es el estado desactivado de la campana (gris suave, transparente)
        if (colorFondo == null && icono != null) {
             btn.setContentAreaFilled(false); // Fondo transparente
             btn.setOpaque(false);
             btn.setBorder(new LineBorder(COLOR_FLAT_GRIS_BORDE, 1)); // Borde sutil
             btn.setBorderPainted(true);
        } else if (colorFondo != null) {
             // Estado normal/activo con fondo sólido
             btn.setBackground(colorFondo);
             btn.setForeground(Color.WHITE);
             btn.setBorderPainted(false); // Sin bordes tridimensionales
             btn.setOpaque(true);
        }

        return btn;
    }

    // =========================================================
    // LÓGICA DE DATOS
    // =========================================================
    
    private void cargarPedidosActivos() {
        panelCuadriculaPedidos.removeAll();
        limpiarPanelIzquierdo();

        // Incluimos 'Terminado' para la vista de notificación
        String sql = "SELECT p.id_pedido, p.estado, m.numero " +
                     "FROM pedido p " +
                     "JOIN mesa m ON p.id_mesa = m.id_mesa " +
                     "WHERE p.estado IN ('Pendiente', 'En preparación', 'Terminado') " +
                     "ORDER BY p.fecha_hora ASC"; 

        try {
            Conexion cn = new Conexion();
            Connection con = cn.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");
                int numMesa = rs.getInt("numero");
                String estado = rs.getString("estado");

                JButton btnTicketMesa = new JButton("<html><center><font size='6' color='black'><b>Mesa " + numMesa + "</b></font><br><font color='gray'>Comanda #" + idPedido + "<br><i>" + estado + "</i></font></center></html>");
                btnTicketMesa.setPreferredSize(new Dimension(160, 110));
                btnTicketMesa.setBackground(COLOR_PANEL_BLANCO);
                btnTicketMesa.setFocusPainted(false);
                btnTicketMesa.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Color del borde según el estado
                if(estado.equals("En preparación")) {
                    btnTicketMesa.setBorder(new LineBorder(COLOR_FLAT_VERDE, 2));
                } else if(estado.equals("Terminado")) {
                    btnTicketMesa.setBorder(new LineBorder(COLOR_FLAT_NARANJA, 2)); 
                } else {
                    btnTicketMesa.setBorder(new LineBorder(COLOR_FLAT_GRIS_BORDE, 1));
                }

                btnTicketMesa.addActionListener(e -> cargarDetallesPedido(idPedido, numMesa, estado));

                panelCuadriculaPedidos.add(btnTicketMesa);
            }
            con.close();

        } catch (Exception e) {
            System.out.println("Error al cargar pedidos: " + e.getMessage());
        }

        panelCuadriculaPedidos.revalidate();
        panelCuadriculaPedidos.repaint();
    }

    private void cargarDetallesPedido(int idPedido, int numMesa, String estado) {
        pedidoSeleccionadoId = idPedido;
        mesaSeleccionadaNum = numMesa;
        estadoPedidoSeleccionado = estado;

        lblTituloDetalles.setText("Detalles mesa #" + numMesa);
        txtDetallesPedido.setText(""); 

        String sql = "SELECT dp.cantidad, p.nombre, dp.nota_especial " +
                     "FROM detalle_pedido dp " +
                     "JOIN platillo p ON dp.id_platillo = p.id_platillo " +
                     "WHERE dp.id_pedido = ?";

        try {
            Conexion cn = new Conexion();
            Connection con = cn.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();

            StringBuilder detalles = new StringBuilder();

            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                String nombre = rs.getString("nombre");
                String nota = rs.getString("nota_especial");

                detalles.append("-x").append(cantidad).append(" ").append(nombre).append("\n");
                if (nota != null && !nota.trim().isEmpty()) {
                    detalles.append("   * Nota: ").append(nota).append("\n");
                }
                detalles.append("\n"); 
            }
            
            txtDetallesPedido.setText(detalles.toString());
            con.close();

        } catch (Exception e) {
            System.out.println("Error al cargar detalles: " + e.getMessage());
        }

        // --- LÓGICA DE BOTONES ROBUSTA (¡CON TAMAÑOS Y AZUL!) ---
        btnEstadoCocinero.setEnabled(true);

        if (estado.equals("Pendiente")) {
            btnEstadoCocinero.setText("Empezar a preparar");
            btnEstadoCocinero.setBackground(COLOR_FLAT_NARANJA);
            
            // Campanita desactivada (Transparente con borde sutil)
            btnNotificarMesero.setEnabled(false);
            btnNotificarMesero.setContentAreaFilled(false); 
            btnNotificarMesero.setOpaque(false);
            btnNotificarMesero.setBorder(new LineBorder(COLOR_FLAT_GRIS_BORDE, 1));
            btnNotificarMesero.setBorderPainted(true);
            
        } else if (estado.equals("En preparación")) {
            btnEstadoCocinero.setText("Marcar como listo");
            btnEstadoCocinero.setBackground(COLOR_FLAT_VERDE);
            
            // Campanita desactivada
            btnNotificarMesero.setEnabled(false);
            btnNotificarMesero.setContentAreaFilled(false); 
            btnNotificarMesero.setOpaque(false);
            btnNotificarMesero.setBorder(new LineBorder(COLOR_FLAT_GRIS_BORDE, 1));
            btnNotificarMesero.setBorderPainted(true);
            
        } else if (estado.equals("Terminado")) {
            // ¡ESTADO DE NOTIFICACIÓN! (Mockup 3)
            btnEstadoCocinero.setText("Pedido Listo");
            btnEstadoCocinero.setBackground(COLOR_FLAT_GRIS);
            btnEstadoCocinero.setEnabled(false); // Ya se cocinó
            
            // ACTIVAMOS LA CAMPANITA (¡Se pone AZUL FLAT sólido!)
            btnNotificarMesero.setEnabled(true);
            btnNotificarMesero.setContentAreaFilled(true); // Fondo sólido
            btnNotificarMesero.setOpaque(true);
            btnNotificarMesero.setBackground(COLOR_FLAT_AZUL); // AZUL Flat
            btnNotificarMesero.setBorderPainted(false); // Sin bordes
        }
    }

    private void cambiarEstadoPedido() {
        String nuevoEstado = "";
        
        if (estadoPedidoSeleccionado.equals("Pendiente")) {
            nuevoEstado = "En preparación";
        } else if (estadoPedidoSeleccionado.equals("En preparación")) {
            nuevoEstado = "Terminado"; 
        }

        if (!nuevoEstado.isEmpty()) {
            int idGuardado = pedidoSeleccionadoId;
            int mesaGuardada = mesaSeleccionadaNum;
            
            actualizarEstadoEnBD(nuevoEstado);
            cargarPedidosActivos(); 
            cargarDetallesPedido(idGuardado, mesaGuardada, nuevoEstado); 
        }
    }
    
    private void actualizarEstadoEnBD(String estado) {
         try {
            Conexion cn = new Conexion();
            Connection con = cn.getConexion();
            String sql = "UPDATE pedido SET estado = ? WHERE id_pedido = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setInt(2, pedidoSeleccionadoId);
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error SQL: " + e.getMessage());
        }
    }

    private void notificarMeseroYLimpiar() {
        if (pedidoSeleccionadoId == 0 || mesaSeleccionadaNum == 0) {
            return; 
        }

        // Al notificar, pasamos a estado final ('Entregado')
        actualizarEstadoEnBD("Entregado"); 
        
        JOptionPane.showMessageDialog(null, "¡Mesero notificado para recoger Orden #" + pedidoSeleccionadoId + " de Mesa " + mesaSeleccionadaNum + "!", "Campanita", JOptionPane.INFORMATION_MESSAGE);
        
        cargarPedidosActivos(); 
    }

    private void limpiarPanelIzquierdo() {
        pedidoSeleccionadoId = 0;
        mesaSeleccionadaNum = 0;
        estadoPedidoSeleccionado = "";
        lblTituloDetalles.setText("Detalles mesa #");
        txtDetallesPedido.setText("");
        
        btnEstadoCocinero.setText("Seleccione pedido");
        btnEstadoCocinero.setBackground(COLOR_FLAT_GRIS);
        btnEstadoCocinero.setEnabled(false);
        
        // Campanita desactivada
        btnNotificarMesero.setOpaque(false); // Transparente
        btnNotificarMesero.setContentAreaFilled(false); 
        btnNotificarMesero.setBorder(new LineBorder(COLOR_FLAT_GRIS_BORDE, 1));
        btnNotificarMesero.setEnabled(false);
    }

    public static void main(String[] args) {
        // Mejoramos el renderizado de texto
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        new PanelCocinero().setVisible(true);
    }
}