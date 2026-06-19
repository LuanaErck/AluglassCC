package base;

import clases.Presupuesto;
import utilidades.ConexionSQlite;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BasePresupuesto
{
    public List<Presupuesto> listarPorCliente(int idCliente)
    {

        List<Presupuesto> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM presupuestos
            WHERE id_cliente = ?
            ORDER BY id_presupuesto DESC
        """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {

            stmt.setInt(1, idCliente);

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {

                Presupuesto p = new Presupuesto(
                        rs.getInt("id_presupuesto"),
                        rs.getInt("id_cliente"),
                        rs.getString("fecha_presupuesto"),
                        rs.getString("descripcion_trabajo"),
                        rs.getDouble("importe_total"),
                        rs.getDouble("saldo_presupuesto"),
                        rs.getString("estado")
                );

                lista.add(p);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return lista;
    }


    public List<Presupuesto> listarPendientesPorCliente(int idCliente)
    {

        List<Presupuesto> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM presupuestos
            WHERE id_cliente = ?
            AND saldo_presupuesto > 0
            ORDER BY id_presupuesto DESC
        """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {

            stmt.setInt(1, idCliente);

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {

                Presupuesto p = new Presupuesto(
                        rs.getInt("id_presupuesto"),
                        rs.getInt("id_cliente"),
                        rs.getString("fecha_presupuesto"),
                        rs.getString("descripcion_trabajo"),
                        rs.getDouble("importe_total"),
                        rs.getDouble("saldo_presupuesto"),
                        rs.getString("estado")
                );

                lista.add(p);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return lista;
    }


    public void registrarPresupuesto(int idCliente, double total, String descripcion)
    {

        String sql = """
            INSERT INTO presupuestos 
            (id_cliente, fecha_presupuesto, descripcion_trabajo, importe_total, saldo_presupuesto, estado)
            VALUES (?, date('now'), ?, ?, ?, ?)
        """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {

            stmt.setInt(1, idCliente);
            stmt.setString(2, descripcion);
            stmt.setDouble(3, total);
            stmt.setDouble(4, total);
            stmt.setString(5, "Pendiente");

            stmt.executeUpdate();

            // ACTUALIZAR CUENTA CORRIENTE
            String sqlCuenta = """
                UPDATE cuentas_corrientes
                SET saldo_actual = saldo_actual + ?
                WHERE id_cliente = ?
            """;

            PreparedStatement stmtCuenta = conn.prepareStatement(sqlCuenta);

            stmtCuenta.setDouble(1, total);
            stmtCuenta.setInt(2, idCliente);

            stmtCuenta.executeUpdate();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public List<Presupuesto> listarTodos()
    {

        List<Presupuesto> lista = new ArrayList<>();

        String sql = """
            SELECT p.*, c.nombre
            FROM presupuestos p
            JOIN clientes c ON p.id_cliente = c.id_cliente
            ORDER BY p.fecha_presupuesto DESC
        """;

        try(Connection conn = ConexionSQlite.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {

                Presupuesto p = new Presupuesto(
                        rs.getInt("id_presupuesto"),
                        rs.getInt("id_cliente"),
                        rs.getString("fecha_presupuesto"),
                        rs.getString("descripcion_trabajo"),
                        rs.getDouble("importe_total"),
                        rs.getDouble("saldo_presupuesto"),
                        rs.getString("estado")
                );

                p.setNombreCliente(rs.getString("nombre"));

                lista.add(p);

            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return lista;
    }


    public void modificarPresupuesto(int idPresupuesto, String descripcion, double total)
    {

        String sqlBuscar = """
            SELECT id_cliente, importe_total
            FROM presupuestos
            WHERE id_presupuesto = ?
        """;

        String sqlActualizar = """
            UPDATE presupuestos
            SET descripcion_trabajo = ?, 
                importe_total = ?, 
                saldo_presupuesto = ?
            WHERE id_presupuesto = ?
        """;

        String sqlCuenta = """
            UPDATE cuentas_corrientes
            SET saldo_actual = saldo_actual + ?
            WHERE id_cliente = ?
        """;

        try(Connection conn = ConexionSQlite.conectar())
        {

            int idCliente = 0;
            double totalAnterior = 0;

            // BUSCAR DATOS ACTUALES
            PreparedStatement stmtBuscar = conn.prepareStatement(sqlBuscar);
            stmtBuscar.setInt(1, idPresupuesto);

            ResultSet rs = stmtBuscar.executeQuery();

            if(rs.next())
            {
                idCliente = rs.getInt("id_cliente");
                totalAnterior = rs.getDouble("importe_total");
            }

            // CALCULAR DIFERENCIA
            double diferencia = total - totalAnterior;

            // ACTUALIZAR PRESUPUESTO
            PreparedStatement stmtActualizar = conn.prepareStatement(sqlActualizar);

            stmtActualizar.setString(1, descripcion);
            stmtActualizar.setDouble(2, total);
            stmtActualizar.setDouble(3, total);
            stmtActualizar.setInt(4, idPresupuesto);

            stmtActualizar.executeUpdate();

            // ACTUALIZAR CUENTA CORRIENTE
            PreparedStatement stmtCuenta = conn.prepareStatement(sqlCuenta);

            stmtCuenta.setDouble(1, diferencia);
            stmtCuenta.setInt(2, idCliente);

            stmtCuenta.executeUpdate();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Presupuesto obtenerPorId(int idPresupuesto) 
    {
        Presupuesto p = null;
        // Hacemos un JOIN con clientes para tener el nombre listo para el detalle
        String sql = """
            SELECT p.*, c.nombre 
            FROM presupuestos p
            JOIN clientes c ON p.id_cliente = c.id_cliente
            WHERE p.id_presupuesto = ?
        """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, idPresupuesto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) 
            {
                p = new Presupuesto(
                    rs.getInt("id_presupuesto"),
                    rs.getInt("id_cliente"),
                    rs.getString("fecha_presupuesto"),
                    rs.getString("descripcion_trabajo"),
                    rs.getDouble("importe_total"),
                    rs.getDouble("saldo_presupuesto"),
                    rs.getString("estado")
                );
                // Seteamos el nombre del cliente que viene del JOIN
                p.setNombreCliente(rs.getString("nombre"));
            }

        } catch (Exception e) 
        {
            System.err.println("Error al obtener presupuesto por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return p;
    }
}