package base;

import clases.CuentaCorriente;
import utilidades.ConexionSQlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseCuenta
{

    // LISTAR CUENTAS CORRIENTES CON SALDO REAL
    public List<CuentaCorriente> listarCuentas()
    {
        List<CuentaCorriente> lista = new ArrayList<>();

        String sql = """
            SELECT cc.id_cuenta,
                   c.id_cliente,
                   c.nombre,
                   cc.saldo_actual
            FROM cuentas_corrientes cc
            JOIN clientes c ON cc.id_cliente = c.id_cliente
            ORDER BY c.nombre
        """;

        try(Connection conn = ConexionSQlite.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery())
        {
            while(rs.next())
            {
                CuentaCorriente cuenta = new CuentaCorriente(
                        rs.getInt("id_cuenta"),
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getDouble("saldo_actual"),
                        "Aldia"
                );

                lista.add(cuenta);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return lista;
    }

    // OBTENER SALDO DE UN CLIENTE
    public double obtenerSaldoCliente(int idCliente)
    {
        String sql = """
            SELECT IFNULL(SUM(saldo_presupuesto),0) AS saldo
            FROM presupuestos
            WHERE id_cliente = ?
        """;

        try(Connection conn = ConexionSQlite.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if(rs.next())
                return rs.getDouble("saldo");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }
    
    //OBTENER CUENTAS ATRASADAS
    public List<CuentaCorriente> listarCuentasAtrasadas()
    {

        List<CuentaCorriente> lista = new ArrayList<>();

        String sql = """
            SELECT 
                cc.id_cuenta,
                cc.id_cliente,
                c.nombre,
                cc.saldo_actual,
                MAX(p.fecha_pago) AS ultimo_pago,
                CASE
                    WHEN MAX(p.fecha_pago) IS NULL THEN 'Sin pagos'
                    WHEN julianday('now') - julianday(MAX(p.fecha_pago)) > 30 THEN 'Atrasado'
                    ELSE 'Al día'
                END AS estado_pago
            FROM cuentas_corrientes cc
            JOIN clientes c ON cc.id_cliente = c.id_cliente
            LEFT JOIN pagos p 
                ON p.id_cliente = cc.id_cliente 
                AND p.estado != 'Anulado'
            WHERE cc.saldo_actual > 0
            GROUP BY cc.id_cliente
            ORDER BY cc.saldo_actual DESC
        """;

        try(Connection conn = ConexionSQlite.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {

                CuentaCorriente cuenta = new CuentaCorriente(
                        rs.getInt("id_cuenta"),
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getDouble("saldo_actual"),
                        rs.getString("estado_pago")
                );

                lista.add(cuenta);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return lista;
    }
}