package seguridad;

import org.mindrot.jbcrypt.BCrypt;

public class Encriptador 
{

    /**
     * Toma una contraseña en texto plano (ej: "1234") y devuelve el hash.
     * Este hash es lo que guardaremos en la base de datos.
     */
    public static String hashPassword(String passwordPlana) 
    {
        // El número 12 es el "costo" del procesamiento (fuerza del hash)
        return BCrypt.hashpw(passwordPlana, BCrypt.gensalt(12));
    }

    /**
     * Compara una contraseña ingresada por el usuario contra el hash guardado.
     */
    public static boolean verificarPassword(String passwordPlana, String hashedPassword) 
    {
        try 
        {
            // BCrypt extrae el salt del propio hash para comparar
            return BCrypt.checkpw(passwordPlana, hashedPassword);
        }
        catch (Exception e) 
        {
            return false;
        }
    }
}
