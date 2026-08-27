package base;

import clases.Cliente;
import utilidades.ConexionSQlite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
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
                        rs.getString("estado"),
                        rs.getString("cuit")
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
        String sql = "SELECT * FROM clientes WHERE estado = 'Activo' ORDER BY nombre ASC";
        
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
                        rs.getString("estado"),
                        rs.getString("cuit")
                ));
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error al listar clientes activos: " + e.getMessage());
        }

        return lista;
    }
    
    // Verifica si un cuit ya existe registrado en la base de datos
    public boolean existeCuit(String cuit) 
    {
        if (cuit == null || cuit.trim().isEmpty()) 
        {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM clientes WHERE cuit = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, cuit.trim());
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) 
                {
                    return rs.getInt(1) > 0;
                }
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return false;
    }
    
    // Agrega un cliente y crea su cuenta corriente automáticamente en una única transacción
    public boolean agregarCliente(Cliente cliente) 
    {
        String sqlCliente = "INSERT INTO clientes(nombre, telefono, estado, cuit) VALUES(?, ?, ?, ?)";
        String sqlCuenta = "INSERT INTO cuentas_corrientes(id_cliente, saldo_actual) VALUES(?, 0)";

        try (Connection conn = ConexionSQlite.conectar()) 
        {
            conn.setAutoCommit(false); // Iniciamos transacción

            try (PreparedStatement stmt = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS)) 
            {
                stmt.setString(1, cliente.getNombre());
                stmt.setString(2, cliente.getTelefono());
                stmt.setString(3, "Activo");

                // Si el CUIT viene nulo o vacío, seteamos NULL explícito para SQLite
                if (cliente.getCuit() == null || cliente.getCuit().trim().isEmpty()) 
                {
                    stmt.setNull(4, Types.VARCHAR);
                } 
                else 
                {
                    stmt.setString(4, cliente.getCuit().trim());
                }

                int filas = stmt.executeUpdate();

                if (filas > 0) 
                {
                    try (ResultSet rs = stmt.getGeneratedKeys()) 
                    {
                        if (rs.next()) 
                        {
                            int idCliente = rs.getInt(1);
                            cliente.setIdCliente(idCliente);

                            // CREAR CUENTA CORRIENTE
                            try (PreparedStatement stmtCuenta = conn.prepareStatement(sqlCuenta)) 
                            {
                                stmtCuenta.setInt(1, idCliente);
                                stmtCuenta.executeUpdate();
                            }
                        } 
                        else 
                        {
                            conn.rollback();
                            return false;
                        }
                    }
                    conn.commit(); // Confirmamos los cambios en la BD
                    return true;
                } 
                else 
                {
                    conn.rollback();
                }
            } 
            catch (Exception e) 
            {
                conn.rollback();
                e.printStackTrace();
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
        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, estado = ?, cuit = ? WHERE id_cliente = ?";

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getTelefono());
            stmt.setString(3, cliente.getEstado());

            // Mismo manejo de NULL para modificación
            if (cliente.getCuit() == null || cliente.getCuit().trim().isEmpty()) 
            {
                stmt.setNull(4, Types.VARCHAR);
            } 
            else 
            {
                stmt.setString(4, cliente.getCuit().trim());
            }

            stmt.setInt(5, cliente.getIdCliente());

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