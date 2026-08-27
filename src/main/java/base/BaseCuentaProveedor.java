package base;

import clases.CuentaProveedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import utilidades.ConexionSQlite;

public class BaseCuentaProveedor 
{
    public List<CuentaProveedor> listarCuentas() 
    {
        List<CuentaProveedor> lista = new ArrayList<>();
        String sql = "SELECT p.id_proveedor,p.nombre,cc.saldo,MIN(CASE WHEN co.estado='Pendiente' THEN co.fecha_vencimiento END) AS proximo_vencimiento FROM proveedores p JOIN cc_proveedores cc ON cc.id_proveedor=p.id_proveedor LEFT JOIN compras co ON co.id_proveedor=p.id_proveedor GROUP BY p.id_proveedor,p.nombre,cc.saldo ORDER BY cc.saldo DESC,p.nombre";
        try(Connection conn=ConexionSQlite.conectar(); 
                PreparedStatement stmt=conn.prepareStatement(sql); 
                ResultSet rs=stmt.executeQuery()) 
        {
            while(rs.next()) lista.add(new CuentaProveedor(rs.getInt("id_proveedor"),rs.getString("nombre"),rs.getDouble("saldo"),rs.getString("proximo_vencimiento")));
        } 
        catch(Exception e) 
        { 
            e.printStackTrace(); 
        }
        return lista;
    }
}
