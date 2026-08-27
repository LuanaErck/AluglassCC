package base;

import clases.Proveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import utilidades.ConexionSQlite;

//Acceso a datos de proveedores y su cuenta corriente
public class BaseProveedor 
{

    public List<Proveedor> listarProveedores() 
    {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores ORDER BY nombre ASC";

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) 
        {
            while (rs.next()) 
            {
                proveedores.add(new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("estado"),
                        rs.getString("cuit"),
                        rs.getString("cbu_alias")
                ));
            }
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
        return proveedores;
    }

    public List<Proveedor> listarProveedoresActivos() 
    {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores WHERE estado = 'Activo' ORDER BY nombre ASC";

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) 
        {
            while (rs.next()) 
            {
                proveedores.add(new Proveedor(
                        rs.getInt("id_proveedor"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("estado"),
                        rs.getString("cuit"), rs.getString("cbu_alias")
                ));
            }
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
        return proveedores;
    }

    public boolean existeCuit(String cuit) 
    {
        if (cuit == null || cuit.trim().isEmpty()) 
        {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM proveedores WHERE cuit = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, cuit.trim());
            try (ResultSet rs = stmt.executeQuery()) 
            {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    //Inserta el proveedor y crea su cuenta corriente en una única transacción
    public boolean agregarProveedor(Proveedor proveedor) 
    {
        String sqlProveedor = "INSERT INTO proveedores(nombre, cuit, telefono, cbu_alias, estado) VALUES(?, ?, ?, ?, ?)";
        String sqlCuenta = "INSERT INTO cc_proveedores(id_proveedor, saldo) VALUES(?, 0)";

        try (Connection conn = ConexionSQlite.conectar())
        {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtProveedor = conn.prepareStatement(sqlProveedor, Statement.RETURN_GENERATED_KEYS)) {
                
                stmtProveedor.setString(1, proveedor.getNombre());
                
                // Manejo de CUIT nulo/vacío para SQLite UNIQUE para que no tome como un repetido
                if (proveedor.getCuit() == null || proveedor.getCuit().trim().isEmpty())
                {
                    stmtProveedor.setNull(2, Types.VARCHAR);
                } 
                else 
                {
                    stmtProveedor.setString(2, proveedor.getCuit().trim());
                }

                // Manejo opcional de teléfono nulo
                if (proveedor.getTelefono() == null || proveedor.getTelefono().trim().isEmpty()) 
                {
                    stmtProveedor.setNull(3, Types.VARCHAR);
                }
                else 
                {
                    stmtProveedor.setString(3, proveedor.getTelefono().trim());
                }

                // Manejo opcional de CBU/Alias nulo
                if (proveedor.getCbuAlias() == null || proveedor.getCbuAlias().trim().isEmpty())
                {
                    stmtProveedor.setNull(4, Types.VARCHAR);
                }
                else
                {
                    stmtProveedor.setString(4, proveedor.getCbuAlias().trim());
                }

                stmtProveedor.setString(5, proveedor.getEstado());

                if (stmtProveedor.executeUpdate() != 1) 
                {
                    conn.rollback();
                    return false;
                }

                try (ResultSet keys = stmtProveedor.getGeneratedKeys()) 
                {
                    if (!keys.next()) 
                    {
                        conn.rollback();
                        return false;
                    }

                    int idProveedor = keys.getInt(1);
                    try (PreparedStatement stmtCuenta = conn.prepareStatement(sqlCuenta))
                    {
                        stmtCuenta.setInt(1, idProveedor);
                        stmtCuenta.executeUpdate();
                    }
                    proveedor.setIdProveedor(idProveedor);
                }
            }
            conn.commit();
            return true;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }
}