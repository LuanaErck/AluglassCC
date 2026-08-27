package base;

import clases.DeudorReporte;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import utilidades.ConexionSQlite;

public class BaseReporteProveedor 
{

    public double totalComprasMes() 
    {
        return consultarTotal("SELECT COALESCE(SUM(importe), 0) FROM compras WHERE strftime('%m', fecha_emision) = strftime('%m', 'now')");
    }

    public double totalComprasHistorico() 
    {
        return consultarTotal("SELECT COALESCE(SUM(importe), 0) FROM compras");
    }

    public double deudaTotal() 
    {
        return consultarTotal("SELECT COALESCE(SUM(CASE WHEN saldo > 0 THEN saldo ELSE 0 END), 0) FROM cc_proveedores");
    }

    private double consultarTotal(String sql) 
    {
        try (Connection c = ConexionSQlite.conectar();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) 
        {
            return r.next() ? r.getDouble(1) : 0;
        } 
        catch (Exception e) 
        {
            return 0;
        }
    }

    public List<DeudorReporte> obtenerProveedoresConDeuda() 
    {
        List<DeudorReporte> lista = new ArrayList<>();
        
        String sql = "SELECT p.nombre, cc.saldo, "
                   + "CAST(julianday('now') - julianday(MIN(co.fecha_vencimiento)) AS INTEGER) AS dias "
                   + "FROM proveedores p "
                   + "JOIN cc_proveedores cc ON cc.id_proveedor = p.id_proveedor "
                   + "LEFT JOIN compras co ON co.id_proveedor = p.id_proveedor AND co.estado = 'Pendiente' "
                   + "WHERE cc.saldo > 0 "
                   + "GROUP BY p.id_proveedor, p.nombre, cc.saldo "
                   + "ORDER BY dias DESC, cc.saldo DESC";

        try (Connection c = ConexionSQlite.conectar();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet r = s.executeQuery()) 
        {
            while (r.next()) 
            {
                int dias = r.getInt("dias");
                String accion = dias > 0 ? "PAGAR / REVISAR VENCIMIENTO" : "PROGRAMAR PAGO";
                
                lista.add(new DeudorReporte(
                        r.getString("nombre"),
                        r.getDouble("saldo"),
                        Math.max(dias, 0),
                        accion
                ));
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return lista;
    }
}