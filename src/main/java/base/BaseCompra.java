package base;

import clases.Compra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import utilidades.ConexionSQlite;

public class BaseCompra 
{
    //Registra la compra. Solo las compras pendientes aumentan la deuda con el proveedor.

    public List<Compra> listarCompras() 
    {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT co.*, p.nombre AS nombre_proveedor "
                + "FROM compras co JOIN proveedores p ON p.id_proveedor = co.id_proveedor "
                + "ORDER BY co.fecha_emision DESC, co.id_compra DESC";

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) 
        {
            while (rs.next()) 
            {
                Compra compra = crearCompra(rs);
                compra.setNombreProveedor(rs.getString("nombre_proveedor"));
                compras.add(compra);
            }
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
        return compras;
    }

    public Compra obtenerCompraPorId(int idCompra) 
    {
        String sql = "SELECT co.*, p.nombre AS nombre_proveedor "
                + "FROM compras co JOIN proveedores p ON p.id_proveedor = co.id_proveedor "
                + "WHERE co.id_compra = ?";

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, idCompra);
            try (ResultSet rs = stmt.executeQuery()) 
            {
                if (rs.next()) 
                {
                    Compra compra = crearCompra(rs);
                    compra.setNombreProveedor(rs.getString("nombre_proveedor"));
                    return compra;
                }
            }
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<Compra> listarComprasPorProveedor(int idProveedor) 
    {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM compras WHERE id_proveedor=? ORDER BY fecha_emision DESC,id_compra DESC";
        try(Connection conn=ConexionSQlite.conectar(); 
                PreparedStatement stmt=conn.prepareStatement(sql)) 
        {
            stmt.setInt(1,idProveedor);try(ResultSet rs=stmt.executeQuery()){while(rs.next())compras.add(crearCompra(rs));}
        } 
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        return compras;
    }

    private Compra crearCompra(ResultSet rs) throws Exception 
    {
        return new Compra(rs.getInt("id_compra"), rs.getInt("id_proveedor"),
                rs.getString("nro_factura"), rs.getString("fecha_emision"),
                rs.getString("detalle"), rs.getDouble("importe"),
                rs.getString("fecha_vencimiento"), rs.getString("estado"));
    }

    public boolean registrarCompra(Compra compra) 
    {
        String sqlCompra = "INSERT INTO compras(id_proveedor, nro_factura, fecha_emision, detalle, importe, fecha_vencimiento, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCuenta = "UPDATE cc_proveedores SET saldo = saldo + ? WHERE id_proveedor = ?";

        try (Connection conn = ConexionSQlite.conectar()) 
        {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtCompra = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
                stmtCompra.setInt(1, compra.getIdProveedor());
                stmtCompra.setString(2, compra.getNroFactura());
                stmtCompra.setString(3, compra.getFecha());
                stmtCompra.setString(4, compra.getDetalle());
                stmtCompra.setDouble(5, compra.getImporte());
                stmtCompra.setString(6, compra.getFechaVencimiento());
                stmtCompra.setString(7, compra.getEstado());

                if (stmtCompra.executeUpdate() != 1)
                {
                    conn.rollback();
                    return false;
                }

                try (ResultSet keys = stmtCompra.getGeneratedKeys()) 
                {
                    if (keys.next()) 
                    {
                        compra.setIdCompra(keys.getInt(1));
                    }
                }

                // Toda compra aumenta el saldo; los pagos determinan automáticamente su estado.
                try (PreparedStatement stmtCuenta = conn.prepareStatement(sqlCuenta)) 
                {
                    stmtCuenta.setDouble(1, compra.getImporte());
                    stmtCuenta.setInt(2, compra.getIdProveedor());
                    if (stmtCuenta.executeUpdate() != 1) 
                    {
                        conn.rollback();
                        return false;
                    }
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
