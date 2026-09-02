package base;

import clases.PagoProveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import utilidades.ConexionSQlite;

public class BasePagoProveedor 
{
    public boolean registrarPago(PagoProveedor pago) 
    {
        String insertar = "INSERT INTO pagos_proveedores(id_proveedor, id_compra, fecha_pago, importe, forma_pago, observaciones, estado) VALUES (?, ?, ?, ?, ?, ?, 'Activo')";
        String actualizar = "UPDATE cc_proveedores SET saldo = saldo - ? WHERE id_proveedor = ?";
        String actualizarEstadoCompra = "UPDATE compras SET estado = CASE WHEN COALESCE((SELECT SUM(importe) FROM pagos_proveedores WHERE id_compra = ? AND estado = 'Activo'), 0) >= importe THEN 'Pagada' ELSE 'Pendiente' END WHERE id_compra = ? AND estado != 'Cancelada'";
        try (Connection conn = ConexionSQlite.conectar()) 
        {
            conn.setAutoCommit(false);
            try (PreparedStatement pagoStmt = conn.prepareStatement(insertar); PreparedStatement cuentaStmt = conn.prepareStatement(actualizar)) 
            {
                pagoStmt.setInt(1, pago.getIdProveedor()); pagoStmt.setInt(2, pago.getIdCompra()); pagoStmt.setString(3, pago.getFechaPago());
                pagoStmt.setDouble(4, pago.getImporte()); pagoStmt.setString(5, pago.getFormaPago());
                pagoStmt.setString(6, pago.getObservaciones());
                if (pagoStmt.executeUpdate() != 1) 
                { 
                    conn.rollback();
                    return false; 
                }
                cuentaStmt.setDouble(1, pago.getImporte()); cuentaStmt.setInt(2, pago.getIdProveedor());
                if (cuentaStmt.executeUpdate() != 1) 
                { 
                    conn.rollback(); 
                    return false; 
                }
                try (PreparedStatement estadoStmt = conn.prepareStatement(actualizarEstadoCompra)) {
                    estadoStmt.setInt(1, pago.getIdCompra());
                    estadoStmt.setInt(2, pago.getIdCompra());
                    estadoStmt.executeUpdate();
                }
            }
            conn.commit(); return true;
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean anularPago(int idPagoProv) 
    {
        String sqlPago = "SELECT id_proveedor, id_compra, importe, estado FROM pagos_proveedores WHERE id_pago_prov = ?";
        String sqlAnular = "UPDATE pagos_proveedores SET estado = 'Anulado' WHERE id_pago_prov = ?";
        String sqlRevertirCC = "UPDATE cc_proveedores SET saldo = saldo + ? WHERE id_proveedor = ?";
        // CORRECCIÓN ACA: Se añade "AND estado != 'Cancelada'" para NO reactivar compras anuladas
        String sqlActualizarCompra = "UPDATE compras SET estado = CASE WHEN COALESCE((SELECT SUM(importe) FROM pagos_proveedores WHERE id_compra = ? AND estado = 'Activo'), 0) >= importe THEN 'Pagada' ELSE 'Pendiente' END WHERE id_compra = ? AND estado != 'Cancelada'";

        try (Connection conn = ConexionSQlite.conectar()) 
        {
            conn.setAutoCommit(false);

            int idProveedor = 0;
            int idCompra = 0;
            double importe = 0.0;
            String estado = "";

            try (PreparedStatement stmtSel = conn.prepareStatement(sqlPago)) 
            {
                stmtSel.setInt(1, idPagoProv);
                try (ResultSet rs = stmtSel.executeQuery()) 
                {
                    if (rs.next())
                    {
                        idProveedor = rs.getInt("id_proveedor");
                        idCompra = rs.getInt("id_compra");
                        importe = rs.getDouble("importe");
                        estado = rs.getString("estado");
                    } 
                    else
                    {
                        conn.rollback();
                        return false;
                    }
                }
            }

            if ("Anulado".equalsIgnoreCase(estado))
            {
                conn.rollback();
                return false;
            }

            try (PreparedStatement stmtAnular = conn.prepareStatement(sqlAnular)) 
            {
                stmtAnular.setInt(1, idPagoProv);
                stmtAnular.executeUpdate();
            }

            try (PreparedStatement stmtCC = conn.prepareStatement(sqlRevertirCC))
            {
                stmtCC.setDouble(1, importe);
                stmtCC.setInt(2, idProveedor);
                stmtCC.executeUpdate();
            }

            if (idCompra > 0) 
            {
                try (PreparedStatement stmtCompra = conn.prepareStatement(sqlActualizarCompra)) {
                    stmtCompra.setInt(1, idCompra);
                    stmtCompra.setInt(2, idCompra);
                    stmtCompra.executeUpdate();
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

    public List<PagoProveedor> listarTodos() 
    {
        List<PagoProveedor> lista = new ArrayList<>();
        String sql = "SELECT pp.*, p.nombre AS nombre_proveedor FROM pagos_proveedores pp JOIN proveedores p ON p.id_proveedor=pp.id_proveedor ORDER BY pp.fecha_pago DESC, pp.id_pago_prov DESC";
        try (Connection conn=ConexionSQlite.conectar(); PreparedStatement stmt=conn.prepareStatement(sql); ResultSet rs=stmt.executeQuery()) 
        {
            while (rs.next()) 
            { 
                PagoProveedor pago = crearPago(rs); pago.setNombreProveedor(rs.getString("nombre_proveedor")); lista.add(pago); 
            }
        } 
        catch(Exception e) 
        { 
            e.printStackTrace(); 
        }
        return lista;
    }

    public List<PagoProveedor> listarPorProveedor(int idProveedor) 
    {
        List<PagoProveedor> lista = new ArrayList<>();
        String sql = "SELECT pp.*, p.nombre AS nombre_proveedor FROM pagos_proveedores pp JOIN proveedores p ON p.id_proveedor=pp.id_proveedor WHERE pp.id_proveedor=? ORDER BY pp.fecha_pago DESC, pp.id_pago_prov DESC";
        try (Connection conn=ConexionSQlite.conectar(); 
            PreparedStatement stmt=conn.prepareStatement(sql)) 
        {
            stmt.setInt(1,idProveedor); try(ResultSet rs=stmt.executeQuery()) 
            { 
                while(rs.next()) 
                { 
                    PagoProveedor pago=crearPago(rs); 
                    pago.setNombreProveedor(rs.getString("nombre_proveedor")); lista.add(pago); 
                } 
            }
        } 
        catch(Exception e) 
        { 
            e.printStackTrace(); 
        }
        return lista;
    }

    public List<PagoProveedor> listarPorCompra(int idCompra) 
    {
        List<PagoProveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagos_proveedores WHERE id_compra=? ORDER BY fecha_pago DESC,id_pago_prov DESC";
        try(Connection conn=ConexionSQlite.conectar(); 
                PreparedStatement stmt=conn.prepareStatement(sql)) 
        {
            stmt.setInt(1,idCompra); 
            try(ResultSet rs=stmt.executeQuery())
            {
                while(rs.next())lista.add(crearPago(rs));
            }
        } 
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        return lista;
    }

    private PagoProveedor crearPago(ResultSet rs) throws Exception 
    { 
        return new PagoProveedor(rs.getInt("id_pago_prov"),
                rs.getInt("id_proveedor"),
                rs.getInt("id_compra"),
                rs.getString("fecha_pago"),
                rs.getDouble("importe"),
                rs.getString("forma_pago"),
                rs.getString("observaciones"),
                rs.getString("estado")); 
    }
}