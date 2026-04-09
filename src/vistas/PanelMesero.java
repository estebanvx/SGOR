package vistas;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class PanelMesero extends JFrame {
    public PanelMesero() {
        setTitle("Panel de Mesero - Comanda");
        setSize(800, 600); // Una ventana más grande para el punto de venta
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel mensaje = new JLabel("¡BIENVENIDO AL PANEL DE MESERO!", SwingConstants.CENTER);
        add(mensaje);
    }
}