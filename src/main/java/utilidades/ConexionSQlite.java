package utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.io.File;

public class ConexionSQlite 
{
    // Cambiamos la ruta fija por una que busque el archivo al lado del programa
    private static final String DB_NAME = "clientes_aluglass.db";
    private static final String URL = "jdbc:sqlite:" + DB_NAME;

    public static Connection conectar() 
    {
        try 
        {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            
            // Opcional: Imprime la ruta absoluta en consola para saber dónde se creó
            // File f = new File(DB_NAME);
            // System.out.println("Base de datos ubicada en: " + f.getAbsolutePath());
            
            return conn;
        } catch (Exception e) 
        {
            System.out.println("Error de conexión");
            e.printStackTrace();
            return null;
        }
    }
}

