package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import modelo.Usuario;

public class UsuarioDAO {
    // Instanciamos la conexión que probaste hace un rato
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Método principal para el Login
    public Usuario iniciarSesion(int id, String pin) {
        Usuario usu = null; // Empezamos asumiendo que no existe
        
        // Nuestra consulta SQL usando los nombres exactos de tus columnas
        String sql = "SELECT * FROM usuario WHERE id_usuario = ? AND pin_credencial = ?";
        
        try {
            con = cn.getConexion(); // Abrimos la puerta
            ps = con.prepareStatement(sql); // Preparamos la consulta
            
            // Reemplazamos los signos de interrogación (?) por lo que el usuario escribió
            ps.setInt(1, id);
            ps.setString(2, pin);
            
            rs = ps.executeQuery(); // Ejecutamos la búsqueda en MySQL
            
            // Si MySQL encuentra una fila que coincida (if rs.next)...
            if (rs.next()) {
                usu = new Usuario(); // Creamos nuestro "molde" de empleado
                
                // Le pasamos los datos de la base de datos a nuestro objeto en Java
                usu.setIdUsuario(rs.getInt("id_usuario"));
                usu.setNombre(rs.getString("nombre"));
                usu.setRol(rs.getString("rol"));
                usu.setPinCredencial(rs.getString("pin_credencial"));
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en la consulta de Login: " + e.toString());
        }
        
        // Si lo encontró, regresa el usuario lleno de datos. Si no, regresa 'null'.
        return usu; 
    }
}