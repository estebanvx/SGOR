package vistas;

import conexion.Conexion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList; // ¡NUEVO IMPORT PARA LA MEMORIA!
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class PanelMesero extends JFrame {

    private final Color COLOR_FONDO = new Color(245, 245, 245);
    private final Color COLOR_AZUL = new Color(41, 128, 185);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_BORDE = new Color(200, 200, 200);

    private JPanel panelDinamicoPlatillos;
    private JPanel contenedorGrid; 
    private DefaultTableModel modeloTicket;
    private JTable tablaTicket;
    private JLabel lblImagenPlatillo;
    private JTextArea txtNotas; 
    private JSpinner spCantidad; 
    private JLabel lblMesa; 

    // --- VARIABLES DE MEMORIA ---
    private int platilloSeleccionadoId = 0; 
    private String platilloSeleccionadoNombre = "";
    private double platilloSeleccionadoPrecio = 0.0;
    private JButton botonSeleccionadoAnterior = null;
    
    // El ID real del mesero que inició sesión
    private int idMeseroActual = 1; 
    
    // ¡NUEVO!: Memoria para no spamear al mesero con el estado "En Preparación"
    private ArrayList<Integer> pedidosNotificadosEnPrep = new ArrayList<>();

    public PanelMesero() {
        setTitle("SGO Restaurant - Panel de Mesero");
        setSize(1100, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); 
        getContentPane().setBackground(COLOR_FONDO);

        txtNotas = new JTextArea();
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNotas.setLineWrap(true); 
        txtNotas.setWrapStyleWord(true); 

        // =========================================================
        // 1. PANEL IZQUIERDO: LOGO, TICKET Y BOTONES
        // =========================================================
        JLabel lblLogo = new JLabel("LOGO", SwingConstants.CENTER);
        lblLogo.setBounds(20, 20, 250, 100);
        lblLogo.setOpaque(true);
        lblLogo.setBackground(new Color(240, 235, 225));
        lblLogo.setBorder(new LineBorder(COLOR_BORDE));
        add(lblLogo);

        String[] columnas = {"Cant.", "Platillo", "Nota Especial", "ID_Platillo", "Precio_Unitario"};
        modeloTicket = new DefaultTableModel(null, columnas);
        tablaTicket = new JTable(modeloTicket);
        
        tablaTicket.getColumnModel().getColumn(0).setPreferredWidth(40);  
        tablaTicket.getColumnModel().getColumn(1).setPreferredWidth(100); 
        tablaTicket.getColumnModel().getColumn(2).setPreferredWidth(110); 
        
        tablaTicket.getColumnModel().getColumn(3).setMinWidth(0);
        tablaTicket.getColumnModel().getColumn(3).setMaxWidth(0);
        tablaTicket.getColumnModel().getColumn(3).setWidth(0);
        tablaTicket.getColumnModel().getColumn(4).setMinWidth(0);
        tablaTicket.getColumnModel().getColumn(4).setMaxWidth(0);
        tablaTicket.getColumnModel().getColumn(4).setWidth(0);
        
        JScrollPane scrollTicket = new JScrollPane(tablaTicket);
        scrollTicket.setBounds(20, 130, 250, 310); 
        scrollTicket.setBorder(new LineBorder(COLOR_BORDE));
        add(scrollTicket);

        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setBounds(20, 450, 80, 30);
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(lblCantidad);

        spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spCantidad.setBounds(100, 450, 170, 30);
        spCantidad.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(spCantidad);

        JButton btnAgregar = crearBotonAccion("AGREGAR", 20, 490, 250, 40);
        JButton btnEliminar = crearBotonAccion("ELIMINAR", 20, 540, 250, 40);
        JButton btnBorrarOrden = crearBotonAccion("BORRAR ORDEN", 20, 590, 250, 40);
        JButton btnFinalizarOrden = crearBotonAccion("FINALIZAR ORDEN", 20, 640, 250, 40);

        btnAgregar.addActionListener(e -> {
            if (platilloSeleccionadoNombre.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Primero selecciona un platillo del menú.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String notaActual = txtNotas.getText().trim().replaceAll("\\s+", " ");
            int cantidadAgregar = (int) spCantidad.getValue();
            boolean encontrado = false;

            for (int i = 0; i < modeloTicket.getRowCount(); i++) {
                String nombreEnTabla = modeloTicket.getValueAt(i, 1).toString();
                String notaEnTabla = modeloTicket.getValueAt(i, 2).toString();

                if (nombreEnTabla.equals(platilloSeleccionadoNombre) && notaEnTabla.equalsIgnoreCase(notaActual)) {
                    int cantidadActual = Integer.parseInt(modeloTicket.getValueAt(i, 0).toString());
                    modeloTicket.setValueAt(cantidadActual + cantidadAgregar, i, 0);
                    encontrado = true;
                    break; 
                }
            }

            if (!encontrado) {
                Object[] nuevaFila = { cantidadAgregar, platilloSeleccionadoNombre, notaActual, platilloSeleccionadoId, platilloSeleccionadoPrecio };
                modeloTicket.addRow(nuevaFila);
            }

            txtNotas.setText("");
            spCantidad.setValue(1); 
        });

        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaTicket.getSelectedRow(); 
            if (filaSeleccionada == -1) { 
                JOptionPane.showMessageDialog(null, "Por favor, selecciona un platillo de la lista para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            } else {
                modeloTicket.removeRow(filaSeleccionada); 
            }
        });

        btnBorrarOrden.addActionListener(e -> {
            if (modeloTicket.getRowCount() == 0) return; 
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de borrar toda la orden actual?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                modeloTicket.setRowCount(0); 
                lblMesa.setText("Mesa Seleccionada: 0"); 
                limpiarMemoriaPlatillo();
            }
        });

        btnFinalizarOrden.addActionListener(e -> {
            if (modeloTicket.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Error: No se ingresaron datos del pedido. Agrega platillos primero.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String textoMesa = lblMesa.getText();
            if (textoMesa.equals("Mesa Seleccionada: 0")) {
                JOptionPane.showMessageDialog(null, "Error: Debes seleccionar una mesa en el recuadro inferior derecho antes de enviar la orden.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea enviar la comanda a cocina para la " + textoMesa + "?", "Confirmar Envío", JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    Conexion cn = new Conexion();
                    Connection con = cn.getConexion(); 
                    con.setAutoCommit(false); 
                    
                    double totalPedido = 0.0;
                    for (int i = 0; i < modeloTicket.getRowCount(); i++) {
                        int cant = Integer.parseInt(modeloTicket.getValueAt(i, 0).toString());
                        double precioUnitario = Double.parseDouble(modeloTicket.getValueAt(i, 4).toString());
                        totalPedido += (cant * precioUnitario);
                    }
                    
                    int idMesa = Integer.parseInt(textoMesa.replace("Mesa Seleccionada: ", "").trim());
                    
                    String sqlPedido = "INSERT INTO pedido (estado, total, forma_pago, id_mesa, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
                    psPedido.setString(1, "Pendiente");
                    psPedido.setDouble(2, totalPedido);
                    psPedido.setString(3, "Pendiente"); 
                    psPedido.setInt(4, idMesa);
                    psPedido.setInt(5, idMeseroActual); 
                    
                    psPedido.executeUpdate(); 
                    
                    ResultSet rsLlaves = psPedido.getGeneratedKeys();
                    int idPedidoGenerado = 0;
                    if (rsLlaves.next()) {
                        idPedidoGenerado = rsLlaves.getInt(1);
                    }
                    
                    String sqlDetalle = "INSERT INTO detalle_pedido (id_pedido, id_platillo, cantidad, precio_unitario, nota_especial) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
                    
                    for (int i = 0; i < modeloTicket.getRowCount(); i++) {
                        int cantidad = Integer.parseInt(modeloTicket.getValueAt(i, 0).toString());
                        String nota = modeloTicket.getValueAt(i, 2).toString();
                        int idPlatillo = Integer.parseInt(modeloTicket.getValueAt(i, 3).toString());
                        double precioUnit = Double.parseDouble(modeloTicket.getValueAt(i, 4).toString());
                        
                        psDetalle.setInt(1, idPedidoGenerado);
                        psDetalle.setInt(2, idPlatillo);
                        psDetalle.setInt(3, cantidad);
                        psDetalle.setDouble(4, precioUnit);
                        psDetalle.setString(5, nota);
                        
                        psDetalle.addBatch(); 
                    }
                    psDetalle.executeBatch(); 
                    
                    String sqlActualizarMesa = "UPDATE mesa SET estado = 'Ocupada' WHERE id_mesa = ?";
                    PreparedStatement psMesa = con.prepareStatement(sqlActualizarMesa);
                    psMesa.setInt(1, idMesa);
                    psMesa.executeUpdate();

                    con.commit(); 
                    con.close();
                    
                    JOptionPane.showMessageDialog(null, "¡Comanda #" + idPedidoGenerado + " enviada exitosamente a cocina!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                    modeloTicket.setRowCount(0); 
                    lblMesa.setText("Mesa Seleccionada: 0");
                    txtNotas.setText("");
                    spCantidad.setValue(1);
                    limpiarMemoriaPlatillo();
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al guardar en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // =========================================================
        // 2. PANEL SUPERIOR Y FILTROS
        // =========================================================
        int xTop = 290;
        JButton btnTodos = crearBotonFiltro("Todos", xTop, 20, 90, 45);
        JButton btnPlatillos = crearBotonFiltro("Platillos", xTop + 100, 20, 100, 45);
        JButton btnBebidas = crearBotonFiltro("Bebidas", xTop + 210, 20, 100, 45);
        JButton btnPostres = crearBotonFiltro("Postres", xTop + 320, 20, 100, 45);
        
        btnTodos.addActionListener(e -> cargarPlatillos("Todos", ""));
        btnPlatillos.addActionListener(e -> cargarPlatillos("Platillos", ""));
        btnBebidas.addActionListener(e -> cargarPlatillos("Bebidas", ""));
        btnPostres.addActionListener(e -> cargarPlatillos("Postres", ""));
        
        JTextField txtBuscar = new JTextField();
        txtBuscar.setBounds(xTop + 430, 20, 270, 45); 
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtBuscar.setBorder(BorderFactory.createTitledBorder("Búsqueda rápida:"));
        add(txtBuscar);
        
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarPlatillos("Todos", txtBuscar.getText());
            }
        });

        // =========================================================
        // 3. CENTRO DINÁMICO (MENÚ)
        // =========================================================
        contenedorGrid = new JPanel(new GridLayout(0, 4, 15, 15));
        contenedorGrid.setBackground(COLOR_BLANCO);
        contenedorGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelDinamicoPlatillos = new JPanel(new BorderLayout());
        panelDinamicoPlatillos.setBackground(COLOR_BLANCO);
        panelDinamicoPlatillos.add(contenedorGrid, BorderLayout.NORTH);
        
        JScrollPane scrollDinamico = new JScrollPane(panelDinamicoPlatillos);
        scrollDinamico.setBounds(290, 80, 770, 480);
        scrollDinamico.setBorder(new LineBorder(COLOR_BORDE));
        scrollDinamico.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollDinamico.getVerticalScrollBar().setUnitIncrement(16); 
        add(scrollDinamico);

        // =========================================================
        // 4. PANEL INFERIOR Y SELECCIÓN DE MESA
        // =========================================================
        lblImagenPlatillo = new JLabel("IMAGEN", SwingConstants.CENTER);
        lblImagenPlatillo.setBounds(290, 580, 150, 100);
        lblImagenPlatillo.setOpaque(true);
        lblImagenPlatillo.setBackground(COLOR_BLANCO);
        lblImagenPlatillo.setBorder(new LineBorder(COLOR_BORDE));
        add(lblImagenPlatillo);

        JScrollPane scrollNotas = new JScrollPane(txtNotas);
        scrollNotas.setBounds(460, 580, 380, 100);
        scrollNotas.setBorder(BorderFactory.createTitledBorder("Notas Especiales / Comentarios:"));
        add(scrollNotas);

        lblMesa = new JLabel("Mesa Seleccionada: 0", SwingConstants.CENTER);
        lblMesa.setBounds(860, 580, 200, 100);
        lblMesa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMesa.setOpaque(true);
        lblMesa.setBackground(COLOR_BLANCO);
        lblMesa.setBorder(new LineBorder(COLOR_BORDE));
        lblMesa.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        lblMesa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MapaMesas mapa = new MapaMesas(PanelMesero.this);
                mapa.setVisible(true);
            }
        });
        
        add(lblMesa);

        cargarPlatillos("Todos", ""); 
        iniciarEscuchaNotificaciones();
    }

    private void limpiarMemoriaPlatillo() {
        platilloSeleccionadoId = 0; 
        platilloSeleccionadoNombre = "";
        platilloSeleccionadoPrecio = 0.0;
        lblImagenPlatillo.setText("IMAGEN");
        if (botonSeleccionadoAnterior != null) {
            botonSeleccionadoAnterior.setBorder(new LineBorder(COLOR_BORDE, 2)); 
            botonSeleccionadoAnterior = null;
        }
    }

    private JButton crearBotonAccion(String texto, int x, int y, int ancho, int alto) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, ancho, alto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(COLOR_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btn);
        return btn;
    }

    private JButton crearBotonFiltro(String texto, int x, int y, int ancho, int alto) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, ancho, alto);
        btn.setBackground(COLOR_BLANCO);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new LineBorder(COLOR_BORDE));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btn);
        return btn; 
    }

    private void cargarPlatillos(String filtroCategoria, String busqueda) {
        contenedorGrid.removeAll();
        limpiarMemoriaPlatillo(); 

        String sql = "SELECT id_platillo, nombre, precio, categoria FROM platillo WHERE 1=1";
        
        if (!filtroCategoria.equals("Todos")) {
            sql += " AND categoria = ?";
        }
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql += " AND nombre LIKE ?"; 
        }

        try {
            Conexion cn = new Conexion();
            Connection con = cn.getConexion(); 
            PreparedStatement ps = con.prepareStatement(sql);
            
            int paramIndex = 1;
            if (!filtroCategoria.equals("Todos")) {
                ps.setString(paramIndex++, filtroCategoria);
            }
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + busqueda.trim() + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_platillo"); 
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");

                JButton btnPlatillo = new JButton("<html><center><b>" + nombre + "</b><br><font color='green'>$" + precio + "</font></center></html>");
                btnPlatillo.setPreferredSize(new Dimension(140, 100)); 
                btnPlatillo.setBackground(COLOR_BLANCO);
                btnPlatillo.setBorder(new LineBorder(COLOR_BORDE, 2));
                btnPlatillo.setFocusPainted(false);
                btnPlatillo.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btnPlatillo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (botonSeleccionadoAnterior != null) {
                            botonSeleccionadoAnterior.setBorder(new LineBorder(COLOR_BORDE, 2));
                        }
                        
                        btnPlatillo.setBorder(new LineBorder(new Color(41, 128, 185), 4));
                        botonSeleccionadoAnterior = btnPlatillo;

                        platilloSeleccionadoId = id; 
                        platilloSeleccionadoNombre = nombre;
                        platilloSeleccionadoPrecio = precio;

                        lblImagenPlatillo.setText("<html><center>" + nombre + "</center></html>");
                    }
                });

                contenedorGrid.add(btnPlatillo);
            }
            con.close();

        } catch (Exception e) {
            System.out.println("Error al cargar platillos: " + e.getMessage());
        }

        contenedorGrid.revalidate();
        contenedorGrid.repaint();
    }

    public void setIdMeseroActual(int id) {
        this.idMeseroActual = id;
    }
    
    public void setMesaSeleccionada(int numMesa) {
        lblMesa.setText("Mesa Seleccionada: " + numMesa);
    }

    // =========================================================
    // RF07: HILO DE NOTIFICACIONES AUTOMÁTICAS MEJORADO
    // =========================================================
    private void iniciarEscuchaNotificaciones() {
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verificarPedidosListos();
            }
        });
        timer.start();
    }

    private void verificarPedidosListos() {
        // ¡CAMBIO! Ahora busca los que están "En preparación" O "Entregado"
        String sqlBusqueda = "SELECT p.id_pedido, m.numero, p.estado FROM pedido p " +
                             "JOIN mesa m ON p.id_mesa = m.id_mesa " +
                             "WHERE p.id_usuario = ? AND p.estado IN ('En preparación', 'Entregado')";

        try {
            Conexion cn = new Conexion();
            Connection con = cn.getConexion();
            PreparedStatement ps = con.prepareStatement(sqlBusqueda);
            ps.setInt(1, idMeseroActual);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");
                int numMesa = rs.getInt("numero");
                String estadoActual = rs.getString("estado");

                if (estadoActual.equals("En preparación")) {
                    // Si NO lo hemos notificado antes, lanzamos la alerta y lo guardamos en la memoria
                    if (!pedidosNotificadosEnPrep.contains(idPedido)) {
                        pedidosNotificadosEnPrep.add(idPedido);
                        
                        JOptionPane.showMessageDialog(PanelMesero.this, 
                            "👨‍🍳 Actualización de Orden:\nLa Comanda #" + idPedido + " de la Mesa " + numMesa + " ha comenzado a prepararse en cocina.", 
                            "Pedido en Curso", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } 
                else if (estadoActual.equals("Entregado")) {
                    // Si ya está entregado, lo pasamos a servido para no volver a leerlo nunca
                    String sqlActualizar = "UPDATE pedido SET estado = 'Servido' WHERE id_pedido = ?";
                    PreparedStatement psActualizar = con.prepareStatement(sqlActualizar);
                    psActualizar.setInt(1, idPedido);
                    psActualizar.executeUpdate();

                    java.awt.Toolkit.getDefaultToolkit().beep(); 
                    JOptionPane.showMessageDialog(PanelMesero.this, 
                            "🔔 ¡ATENCIÓN MESERO!\nLa Comanda #" + idPedido + " de la Mesa " + numMesa + " ya está lista en barra.", 
                            "Pedido Terminado", 
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            con.close();
        } catch (Exception ex) {
            System.out.println("Error en el hilo de notificaciones: " + ex.getMessage());
        }
    }
}