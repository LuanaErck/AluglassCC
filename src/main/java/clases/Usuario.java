package clases;

public class Usuario 
{
    private int idUsuario;
    private String usuario;
    private String contrasena;
    private String preguntaSeguridad;
    private String respuestaSeguridad;
    
    //Constructor
    public Usuario(int idUsuario, String usuario, String contrasena, String pregunta, String respuesta) 
    {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.preguntaSeguridad = pregunta;
        this.respuestaSeguridad = respuesta;
    }

    //Getters
    public int getIdUsuario() 
    {
        return idUsuario;
    }

    public String getUsuario() 
    {
        return usuario;
    }

    public String getContrasena() 
    {
        return contrasena;
    }
    
    public String getPregunta() 
    {
        return preguntaSeguridad;
    }
    
    public String getRespuesta() 
    {
        return respuestaSeguridad;
    }
    
    //Setters
    public void setPregunta (String preguntaSeguridad)
    {
        this.preguntaSeguridad = preguntaSeguridad;
    }
    
    public void setRespuesta (String respuestaSeguridad)
    {
        this.respuestaSeguridad = respuestaSeguridad;
    }
    
    public void setContrasena(String contrasena) 
    { 
        this.contrasena = contrasena; 
    }
}
