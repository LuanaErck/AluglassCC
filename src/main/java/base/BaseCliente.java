package base;

import clases.Cliente;
import utilidades.ConexionSQlite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//Acceso a datos de la tabla clientes
public class BaseCliente 
{

    //Obtiene la lista de todos los clientes
    public List<Cliente> listarClientes() 
    {

        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        
        //Hace la concexion a la base
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) 
        {

            while (rs.next()) 
            {
                String estadoBD = rs.getString("estado");
                
                lista.add(new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        estadoBD
                ));
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return lista;
    }
    
    //Obtiene la lista de todos los clientes activos para nuevos presupuestos y pagos
    public List<Cliente> listarClientesActivos() 
    {

        List<Cliente> lista = new ArrayList<>();
        // Agregamos: WHERE estado = 'Activo'
        String sql = "SELECT * FROM clientes WHERE estado = 'Activo' ORDER BY nombre ASC";
        
        //Hace la concexion a la base
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) 
        {

            while (rs.next()) 
            {  
                lista.add(new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("estado")
                ));
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error al listar clientes activos: " + e.getMessage());
        }

        return lista;
    }
    
    // Agrega un cliente y crea su cuenta corriente automáticamente
    public boolean agregarCliente(Cliente cliente) 
    {
        String sql = "INSERT INTO clientes(nombre, telefono, estado) VALUES(?, ?, ?)";

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getTelefono());
            stmt.setString(3, "Activo");

            int filas = stmt.executeUpdate();

            if(filas > 0)
            {
                ResultSet rs = stmt.getGeneratedKeys();

                if(rs.next())
                {
                    int idCliente = rs.getInt(1);

                    // CREAR CUENTA CORRIENTE
                    String sqlCuenta = """
                        INSERT INTO cuentas_corrientes(id_cliente, saldo_actual)
                        VALUES(?,0)
                    """;

                    PreparedStatement stmtCuenta = conn.prepareStatement(sqlCuenta);
                    stmtCuenta.setInt(1, idCliente);
                    stmtCuenta.executeUpdate();
                }

                return true;
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return false;
    }

    //Actualiza los datos de un cliente existente
    public boolean modificarCliente(Cliente cliente) 
    {

       String sql = "UPDATE clientes SET nombre = ?, telefono = ?, estado = ? WHERE id_cliente = ?";

       try (Connection conn = ConexionSQlite.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) 
       {

           stmt.setString(1, cliente.getNombre());
           stmt.setString(2, cliente.getTelefono());
           stmt.setString(3, cliente.getEstado());
           stmt.setInt(4, cliente.getIdCliente());

           stmt.executeUpdate();
           return true;

       } 
       catch (Exception e) 
       {
           e.printStackTrace();
           return false;
       }
   }
}
