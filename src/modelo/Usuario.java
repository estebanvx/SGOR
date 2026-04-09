package modelo;

public class Usuario {
    
    // 1. Las variables (idénticas a tus columnas en MySQL)
    private int idUsuario;
    private String nombre;
    private String rol;
    private String pinCredencial;

    // 2. Constructor vacío (para crear usuarios en blanco cuando sea necesario)
    public Usuario() {
    }

    // 3. Getters y Setters (Para leer y guardar datos de forma segura)
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPinCredencial() {
        return pinCredencial;
    }

    public void setPinCredencial(String pinCredencial) {
        this.pinCredencial = pinCredencial;
    }
}