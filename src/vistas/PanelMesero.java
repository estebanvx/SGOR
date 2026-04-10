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

    // --- VARIABLES DE MEMORIA PARA EL FLUJO DE SELECCIÓN ---
    private int platilloSeleccionadoId = 0; 
    private String platilloSeleccionadoNombre = "";
    private double platilloSeleccionadoPrecio = 0.0;
    private JButton botonSeleccionadoAnterior = null;

    public PanelMesero() {
        setTitle("Comanda - Panel de Mesero");
        setSize(1100, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); 
        getContentPane().setBackground(COLOR_FONDO);

        txtNotas = new JTextArea();
        txtNotas.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNotas.setLineWrap(true); 
        txtNotas.setWrapStyleWord(true); 

        // =========================================================
        // 1. PANEL IZQUIERDO: LOGO, TICKET Y BOTONES DE ACCIÓN
        // =========================================================
        
        JLabel lblLogo = new JLabel("LOGO", SwingConstants.CENTER);
        lblLogo.setBounds(20, 20, 250, 100);
        lblLogo.setOpaque(true);
        lblLogo.setBackground(new Color(240, 235, 225));
        lblLogo.setBorder(new LineBorder(COLOR_BORDE));
        add(lblLogo);

        // Columnas visibles e invisibles (ID y Precio)
        String[] columnas = {"Cant.", "Platillo", "Nota Especial", "ID_Platillo", "Precio_Unitario"};
        modeloTicket = new DefaultTableModel(null, columnas);
        tablaTicket = new JTable(modeloTicket);
        
        tablaTicket.getColumnModel().getColumn(0).setPreferredWidth(40);  
        tablaTicket.getColumnModel().getColumn(1).setPreferredWidth(100); 
        tablaTicket.getColumnModel().getColumn(2).setPreferredWidth(110); 
        
        // Ocultamos el ID y el Precio
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
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblCantidad);

        spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spCantidad.setBounds(100, 450, 170, 30);
        spCantidad.setFont(new Font("Arial", Font.BOLD, 16));
        add(spCantidad);

        JButton btnAgregar = crearBotonAccion("AGREGAR", 20, 490, 250, 40);
        JButton btnEliminar = crearBotonAccion("ELIMINAR", 20, 540, 250, 40);
        JButton btnBorrarOrden = crearBotonAccion("BORRAR ORDEN", 20, 590, 250, 40);
        JButton btnFinalizarOrden = crearBotonAccion("FINALIZAR ORDEN", 20, 640, 250, 40);

        // --- BOTÓN AGREGAR ---
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

        // --- BOTÓN ELIMINAR ---
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaTicket.getSelectedRow(); 
            if (filaSeleccionada == -1) { 
                JOptionPane.showMessageDialog(null, "Por favor, selecciona un platillo de la lista para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            } else {
                modeloTicket.removeRow(filaSeleccionada); 
            }
        });

        // --- BOTÓN BORRAR ORDEN ---
        btnBorrarOrden.addActionListener(e -> {
            if (modeloTicket.getRowCount() == 0) return; 
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de borrar toda la orden actual?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                modeloTicket.setRowCount(0); 
                lblMesa.setText("Mesa Seleccionada: 0"); 
            }
        });

        // =========================================================
        // --- BOTÓN FINALIZAR ORDEN (LA CONEXIÓN A MYSQL MAESTRA) ---
        // =========================================================
        btnFinalizarOrden.addActionListener(e -> {
            if (modeloTicket.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Error: No se ingresaron datos del pedido. Agrega platillos primero.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String textoMesa = lblMesa.getText();
            if (textoMesa.equals("Mesa Seleccionada: 0")) {
                JOptionPane.showMessageDialog(null, "Error: Debes seleccionar el número de mesa antes de enviar la orden.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea enviar la comanda a cocina para la " + textoMesa + "?", "Confirmar Envío", JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                
                try {
                    Conexion cn = new Conexion();
                    Connection con = cn.getConexion(); 
                    con.setAutoCommit(false); // Iniciamos transacción segura
                    
                    // 1. Calcular el total del pedido recorriendo la tabla
                    double totalPedido = 0.0;
                    for (int i = 0; i < modeloTicket.getRowCount(); i++) {
                        int cant = Integer.parseInt(modeloTicket.getValueAt(i, 0).toString());
                        double precioUnitario = Double.parseDouble(modeloTicket.getValueAt(i, 4).toString());
                        totalPedido += (cant * precioUnitario);
                    }
                    
                    // Extraemos solo el número de la mesa
                    int idMesa = Integer.parseInt(textoMesa.replace("Mesa Seleccionada: ", "").trim());
                    
                    // 2. INSERTAR EN LA TABLA 'pedido'
                    String sqlPedido = "INSERT INTO pedido (estado, total, forma_pago, id_mesa, id_usuario) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
                    psPedido.setString(1, "Pendiente");
                    psPedido.setDouble(2, totalPedido);
                    psPedido.setString(3, "Efectivo"); 
                    psPedido.setInt(4, idMesa);
                    psPedido.setInt(5, 1); // ID temporal del mesero
                    
                    psPedido.executeUpdate(); 
                    
                    // 3. RECUPERAR EL ID GENERADO
                    ResultSet rsLlaves = psPedido.getGeneratedKeys();
                    int idPedidoGenerado = 0;
                    if (rsLlaves.next()) {
                        idPedidoGenerado = rsLlaves.getInt(1);
                    }
                    
                    // 4. INSERTAR EN LA TABLA 'detalle_pedido'
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
                    
                    // 5. ACTUALIZAR EL ESTADO DE LA MESA A 'Ocupada' (¡Lo nuevo!)
                    String sqlActualizarMesa = "UPDATE mesa SET estado = 'Ocupada' WHERE id_mesa = ?";
                    PreparedStatement psMesa = con.prepareStatement(sqlActualizarMesa);
                    psMesa.setInt(1, idMesa);
                    psMesa.executeUpdate();

                    // Confirmamos que toda la transacción fue exitosa
                    con.commit(); 
                    con.close();
                    
                    JOptionPane.showMessageDialog(null, "¡Comanda #" + idPedidoGenerado + " enviada exitosamente a cocina!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Limpiamos pantalla
                    modeloTicket.setRowCount(0); 
                    lblMesa.setText("Mesa Seleccionada: 0");
                    txtNotas.setText("");
                    spCantidad.setValue(1);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al guardar en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // =========================================================
        // 2. PANEL SUPERIOR: FILTROS Y BÚSQUEDA
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
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 16));
        txtBuscar.setBorder(BorderFactory.createTitledBorder("Búsqueda rápida:"));
        add(txtBuscar);
        
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String textoBusqueda = txtBuscar.getText();
                cargarPlatillos("Todos", textoBusqueda);
            }
        });

        // =========================================================
        // 3. CENTRO DINÁMICO
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
        // 4. PANEL INFERIOR
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
        lblMesa.setFont(new Font("Arial", Font.BOLD, 16));
        lblMesa.setOpaque(true);
        lblMesa.setBackground(COLOR_BLANCO);
        lblMesa.setBorder(new LineBorder(COLOR_BORDE));
        lblMesa.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        lblMesa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String numMesa = JOptionPane.showInputDialog(null, "Ingresa el número de mesa:", "Seleccionar Mesa", JOptionPane.QUESTION_MESSAGE);
                if (numMesa != null && !numMesa.trim().isEmpty()) {
                    try {
                        int mesa = Integer.parseInt(numMesa.trim()); 
                        lblMesa.setText("Mesa Seleccionada: " + mesa);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingresa solo números.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        add(lblMesa);

        cargarPlatillos("Todos", ""); 
    }

    private JButton crearBotonAccion(String texto, int x, int y, int ancho, int alto) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, ancho, alto);
        btn.setBackground(COLOR_AZUL);
        btn.setForeground(COLOR_BLANCO);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
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
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(new LineBorder(COLOR_BORDE));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btn);
        return btn; 
    }

    private void cargarPlatillos(String filtroCategoria, String busqueda) {
        contenedorGrid.removeAll();
        platilloSeleccionadoId = 0; 
        platilloSeleccionadoNombre = "";
        platilloSeleccionadoPrecio = 0.0;
        botonSeleccionadoAnterior = null;
        lblImagenPlatillo.setText("IMAGEN");

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

    public static void main(String[] args) {
        PanelMesero ventana = new PanelMesero();
        ventana.setVisible(true);
    }
}