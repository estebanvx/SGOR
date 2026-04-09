package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {
    // Tus credenciales de la base de datos que acabas de crear
    private final String base = "sgor_db";
    private final String user = "root";
    private final String password = "1234"; // ¡Cambia esto!
    private final String url = "jdbc:mysql://localhost:3306/" + base + "?useSSL=false&serverTimezone=UTC";
    
    private Connection con = null;
    
    public Connection getConexion() {
        try {
            // Se llama al driver que agregaste a la carpeta lib
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos: " + e.getMessage());
        }
        return con;
    }
}