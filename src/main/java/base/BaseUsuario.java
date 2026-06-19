package base;

import clases.Usuario;
import utilidades.ConexionSQlite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import seguridad.Encriptador;

public class BaseUsuario 
{
    // Ahora solo buscamos por nombre de usuario
    public Usuario buscarUsuario(String usuario) 
    {
        // La contraseña no se filtra en el SQL
        String sql = "SELECT * FROM usuario WHERE usuario = ?";

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) 
            {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("usuario"),
                        rs.getString("contrasena"), // Este es el Hash guardado
                        rs.getString("preguntaSeguridad"),
                        rs.getString("respuestaSeguridad")
                );
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    //Para que actualice los datos en caso de olvidarse la contraseña y la pregunta de recuperacion
    public boolean actualizarPerfil(int id, String nuevaPass, String pregunta, String respuestaHash) 
    {
        String sql = "UPDATE usuario SET contrasena = ?, preguntaSeguridad = ?, respuestaSeguridad = ? WHERE id_usuario = ?";

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, nuevaPass);
            stmt.setString(2, pregunta);
            stmt.setString(3, respuestaHash);
            stmt.setInt(4, id);

            return stmt.executeUpdate() > 0;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean verificarRespuestaSeguridad(String usuario, String respuestaIngresada) 
    {
        String sql = "SELECT respuestaSeguridad FROM usuario WHERE usuario = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) 
            {
                String hashDB = rs.getString("respuestaSeguridad");
                // Usamos BCrypt para comparar la respuesta escrita con el hash
                return Encriptador.verificarPassword(respuestaIngresada.toLowerCase().trim(), hashDB);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return false;
    }

    // Método para resetear la pass una vez validada la respuesta
    public boolean resetearPassword(String usuario, String nuevaPassHash) 
    {
        String sql = "UPDATE usuario SET contrasena = ? WHERE usuario = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, nuevaPassHash);
            stmt.setString(2, usuario);
            return stmt.executeUpdate() > 0;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }
}