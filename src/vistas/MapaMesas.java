package vistas;

import conexion.Conexion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class MapaMesas extends JFrame {

    private JPanel panelCuadricula;
    private PanelMesero ventanaPadre; // ¡NUEVO! Referencia a la ventana que lo abrió
    
    // Colores
    private final Color COLOR_LIBRE = new Color(46, 204, 113); 
    private final Color COLOR_OCUPADA = new Color(231, 76, 60); 
    private final Color COLOR_FONDO = new Color(245, 245, 245);

    // NUEVO CONSTRUCTOR: Ahora pide quién es su padre
    public MapaMesas(PanelMesero padre) {
        this.ventanaPadre = padre; // Guardamos quién lo abrió
        
        setTitle("SGO Restaurant - Seleccionar Mesa");
        setSize(850, 650);
        // Cambiamos a DISPOSE para que al cerrar el mapa con la 'X', no se cierre todo el programa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(padre); // Que aparezca centrado sobre el PanelMesero
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("SELECCIONE UNA MESA PARA LA ORDEN", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        panelCuadricula = new JPanel(new GridLayout(0, 4, 20, 20)); 
        panelCuadricula.setBackground(COLOR_FONDO);
        panelCuadricula.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JScrollPane scrollMesas = new JScrollPane(panelCuadricula);
        scrollMesas.setBorder(null);
        add(scrollMesas, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar Mesas");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(41, 128, 185)); 
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarMesasDesdeBD());
        
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(COLOR_FONDO);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panelInferior.add(btnActualizar);
        add(panelInferior, BorderLayout.SOUTH);

        cargarMesasDesdeBD();
    }

    private void cargarMesasDesdeBD() {
        panelCuadricula.removeAll(); 
        String sql = "SELECT id_mesa, numero, estado FROM mesa ORDER BY numero ASC";

        try {
            Conexion cn = new Conexion();
            Connection con = cn.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idMesa = rs.getInt("id_mesa");
                int numero = rs.getInt("numero");
                String estado = rs.getString("estado");

                JButton btnMesa = new JButton("<html><center><font size='6'>Mesa " + numero + "</font><br>" + estado + "</center></html>");
                btnMesa.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnMesa.setFocusPainted(false);
                btnMesa.setForeground(Color.WHITE);

                if (estado.equalsIgnoreCase("Libre")) {
                    btnMesa.setBackground(COLOR_LIBRE);
                } else {
                    btnMesa.setBackground(COLOR_OCUPADA);
                }

                btnMesa.addActionListener(e -> {
                    if (estado.equalsIgnoreCase("Libre")) {
                        // ¡MAGIA AQUÍ! Le pasamos el número a la ventana que ya estaba abierta
                        if (ventanaPadre != null) {
                            ventanaPadre.setMesaSeleccionada(numero);
                            this.dispose(); // Cerramos solo el mapa
                        }
                    } else {
                        String[] opciones = {"Agregar más platillos", "Liberar Mesa", "Cancelar"};
                        int seleccion = JOptionPane.showOptionDialog(null,
                                "La Mesa " + numero + " está Ocupada. ¿Qué deseas hacer?",
                                "Opciones de Mesa",
                                JOptionPane.DEFAULT_OPTION, 
                                JOptionPane.QUESTION_MESSAGE, 
                                null, 
                                opciones, 
                                opciones[0]);

                        if (seleccion == 0) {
                            if (ventanaPadre != null) {
                                ventanaPadre.setMesaSeleccionada(numero);
                                this.dispose();
                            }
                        } else if (seleccion == 1) {
                            liberarMesaEnBD(idMesa, numero);
                        }
                    }
                });

                panelCuadricula.add(btnMesa);
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        panelCuadricula.revalidate();
        panelCuadricula.repaint();
    }
    
    private void liberarMesaEnBD(int idMesa, int numeroMesa) {
        int confirmacion = JOptionPane.showConfirmDialog(null, 
                "¿Estás seguro de liberar la Mesa " + numeroMesa + "?", 
                "Confirmar", JOptionPane.YES_NO_OPTION);
                
        if(confirmacion == JOptionPane.YES_OPTION) {
            try {
                Conexion cn = new Conexion();
                Connection con = cn.getConexion();
                String sql = "UPDATE mesa SET estado = 'Libre' WHERE id_mesa = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, idMesa);
                ps.executeUpdate();
                con.close();
                cargarMesasDesdeBD(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al liberar mesa: " + ex.getMessage());
            }
        }
    }
}