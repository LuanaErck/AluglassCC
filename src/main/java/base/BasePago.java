package base;

import clases.Pago;
import clases.DeudorReporte;
import utilidades.ConexionSQlite;
import utilidades.GeneradorReciboPDF;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BasePago 
{
    // MÉTODOS DE OPERACIÓN
    public String registrarPago(int idCliente, String nombreCliente, int idPresupuesto, double monto, String moneda,
                               double cotizacion, double importePesos, String formaPago, String observaciones) 
    {
        String sql = """
            INSERT INTO pagos 
            (id_cliente, id_presupuesto, fecha_pago, importe, moneda, cotizacion, importe_pesos, forma_pago, observaciones, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Activo')
        """;

        String rutaGenerada = "";
        String fechaActual = LocalDate.now().toString();

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idPresupuesto);
            stmt.setString(3, fechaActual);
            stmt.setDouble(4, monto);
            stmt.setString(5, moneda);
            stmt.setDouble(6, cotizacion);
            stmt.setDouble(7, importePesos);
            stmt.setString(8, formaPago);
            stmt.setString(9, observaciones);

            stmt.executeUpdate();

            int idPago = 0;
            try (ResultSet rs = stmt.getGeneratedKeys()) 
            {
                if (rs.next()) 
                {
                    idPago = rs.getInt(1);
                }
            }

            actualizarSaldoPresupuesto(idPresupuesto, importePesos);
            actualizarCuentaCorriente(idCliente, importePesos);

            String monedaLimpia = (moneda != null) ? moneda.trim() : "ARS";

            Pago nuevoPago = new Pago(
                    idPago, idCliente, idPresupuesto, fechaActual, monto,
                    monedaLimpia, cotizacion, importePesos, formaPago,
                    observaciones, nombreCliente, "Activo"
            );

            String numeroRecibo = "R-" + idPago;
            rutaGenerada = GeneradorReciboPDF.generar(nuevoPago, nombreCliente, numeroRecibo);

            BaseRecibo baseRecibo = new BaseRecibo();
            baseRecibo.registrarRecibo(idPago, importePesos, "Pago de presupuesto #" + idPresupuesto, 
                                     numeroRecibo, rutaGenerada, monedaLimpia);

        }
        catch (SQLException e) 
        {
            e.printStackTrace();
            throw new RuntimeException("Error en la base de datos: " + e.getMessage());
        }
        return rutaGenerada;
    }

    private void actualizarSaldoPresupuesto(int idPresupuesto, double pagoPesos) 
    {
        String sqlDescontar = "UPDATE presupuestos SET saldo_presupuesto = saldo_presupuesto - ? WHERE id_presupuesto = ?";
        String sqlEstado = """
            UPDATE presupuestos SET estado = CASE WHEN saldo_presupuesto <= 0 THEN 'Cancelado' ELSE 'Pendiente' END 
            WHERE id_presupuesto = ?
        """;

        try (Connection conn = ConexionSQlite.conectar()) 
        {
            try (PreparedStatement st1 = conn.prepareStatement(sqlDescontar)) 
            {
                st1.setDouble(1, pagoPesos);
                st1.setInt(2, idPresupuesto);
                st1.executeUpdate();
            }
            try (PreparedStatement st2 = conn.prepareStatement(sqlEstado)) 
            {
                st2.setInt(1, idPresupuesto);
                st2.executeUpdate();
            }
        }
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        }
    }

    private void actualizarCuentaCorriente(int idCliente, double pagoPesos)
    {
        String sql = "UPDATE cuentas_corrientes SET saldo_actual = saldo_actual - ? WHERE id_cliente = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setDouble(1, pagoPesos);
            stmt.setInt(2, idCliente);
            stmt.executeUpdate();
        } 
        catch (Exception e) 
        { 
            e.printStackTrace();
        }
    }

    public void anularPago(int idPago) 
    {
        String sqlBuscar = "SELECT id_presupuesto, id_cliente, importe_pesos FROM pagos WHERE id_pago = ?";
        String sqlAnular = "UPDATE pagos SET estado = 'Anulado' WHERE id_pago = ?";
        // Lógica de reversión de saldos
        try (Connection conn = ConexionSQlite.conectar()) 
        {
            int idPre = 0, idCli = 0; double imp = 0;
            try (PreparedStatement stB = conn.prepareStatement(sqlBuscar)) 
            {
                stB.setInt(1, idPago);
                ResultSet rs = stB.executeQuery();
                if (rs.next()) 
                {
                    idPre = rs.getInt("id_presupuesto");
                    idCli = rs.getInt("id_cliente");
                    imp = rs.getDouble("importe_pesos");
                }
            }
            // Devolver saldos
            String sqlRevPre = "UPDATE presupuestos SET saldo_presupuesto = saldo_presupuesto + ?, estado = 'Pendiente' WHERE id_presupuesto = ?";
            String sqlRevCC = "UPDATE cuentas_corrientes SET saldo_actual = saldo_actual + ? WHERE id_cliente = ?";
            
            try (PreparedStatement stP = conn.prepareStatement(sqlRevPre)) 
            { 
                stP.setDouble(1, imp); stP.setInt(2, idPre); stP.executeUpdate(); 
            }
            try (PreparedStatement stC = conn.prepareStatement(sqlRevCC)) 
            { 
                stC.setDouble(1, imp); stC.setInt(2, idCli); stC.executeUpdate(); 
            }
            try (PreparedStatement stA = conn.prepareStatement(sqlAnular)) 
            { 
                stA.setInt(1, idPago); stA.executeUpdate(); 
            }
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        }
    }

    // MÉTODOS DE LISTADO
    public List<Pago> listarPagosPorPresupuesto(int idPresupuesto) 
    {
        List<Pago> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, c.nombre AS nombre_cliente
            FROM pagos p
            JOIN clientes c ON p.id_cliente = c.id_cliente
            WHERE p.id_presupuesto = ?
            ORDER BY p.fecha_pago DESC
        """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, idPresupuesto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) 
            {
                lista.add(new Pago(
                        rs.getInt("id_pago"), rs.getInt("id_cliente"), rs.getInt("id_presupuesto"),
                        rs.getString("fecha_pago"), rs.getDouble("importe"), rs.getString("moneda"),
                        rs.getDouble("cotizacion"), rs.getDouble("importe_pesos"), rs.getString("forma_pago"),
                        rs.getString("observaciones"), rs.getString("nombre_cliente"), rs.getString("estado")
                ));
            }
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        }
        return lista;
    }

    public List<Pago> listarPagosPorCliente(int idCliente) 
    {
        List<Pago> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, c.nombre AS nombre_cliente
            FROM pagos p
            JOIN clientes c ON p.id_cliente = c.id_cliente
            WHERE p.id_cliente = ?
            ORDER BY p.fecha_pago DESC
        """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                lista.add(new Pago(
                        rs.getInt("id_pago"), rs.getInt("id_cliente"), rs.getInt("id_presupuesto"),
                        rs.getString("fecha_pago"), rs.getDouble("importe"), rs.getString("moneda"),
                        rs.getDouble("cotizacion"), rs.getDouble("importe_pesos"), rs.getString("forma_pago"),
                        rs.getString("observaciones"), rs.getString("nombre_cliente"), rs.getString("estado")
                ));
            }
        } 
        catch (Exception e)
        { 
            e.printStackTrace(); 
        }
        return lista;
    }
    
    public List<Pago> listarTodos() 
    {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre FROM pagos p JOIN clientes c ON p.id_cliente = c.id_cliente ORDER BY p.fecha_pago DESC";
        try (Connection conn = ConexionSQlite.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) 
        {
            while (rs.next()) 
            {
                lista.add(new Pago(rs.getInt("id_pago"), rs.getInt("id_cliente"), rs.getInt("id_presupuesto"), rs.getString("fecha_pago"),
                        rs.getDouble("importe"), rs.getString("moneda"), rs.getDouble("cotizacion"), rs.getDouble("importe_pesos"),
                        rs.getString("forma_pago"), rs.getString("observaciones"), rs.getString("nombre"), rs.getString("estado")));
            }
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        }
        return lista;
    }

    // MÉTODOS PARA REPORTES (Dashboard) 
    public double obtenerTotalIngresosHistorico() 
    {
        String sql = "SELECT SUM(importe_pesos) FROM pagos WHERE estado = 'Activo'";
        try (Connection conn = ConexionSQlite.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql))
        {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } 
        catch (Exception e) 
        {
            return 0.0;
        }
    }

    public double obtenerTotalIngresosMesActual()
    {
        String sql = "SELECT SUM(importe_pesos) FROM pagos WHERE estado = 'Activo' AND strftime('%m', fecha_pago) = strftime('%m', 'now')";
        try (Connection conn = ConexionSQlite.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) 
        {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
        catch (Exception e)
        { 
            return 0.0;
        }
    }

    public double obtenerDeudaTotalGlobal()
    {
        String sql = "SELECT SUM(saldo_actual) FROM cuentas_corrientes";
        try (Connection conn = ConexionSQlite.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) 
        {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } 
        catch (Exception e) 
        {
            return 0.0; 
        }
    }

    public List<DeudorReporte> obtenerClientesMorosos() 
    {
        List<DeudorReporte> lista = new ArrayList<>();
        String sql = """
            SELECT c.nombre, cc.saldo_actual, 
            CAST(julianday('now') - julianday(MAX(p.fecha_pago)) AS INTEGER) as dias_atraso
            FROM clientes c
            JOIN cuentas_corrientes cc ON c.id_cliente = cc.id_cliente
            JOIN pagos p ON c.id_cliente = p.id_cliente
            WHERE cc.saldo_actual > 0 
            GROUP BY c.id_cliente
            HAVING dias_atraso > 30
            ORDER BY dias_atraso DESC
        """;
        try (Connection conn = ConexionSQlite.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) 
        {
            while (rs.next()) 
            {
                int dias = rs.getInt("dias_atraso");
                String recomendacion = (dias > 60) ? "SUSPENDER CRÉDITO" : "COBRAR ANTICIPO 70%";
                lista.add(new DeudorReporte(rs.getString("nombre"), rs.getDouble("saldo_actual"), dias, recomendacion));
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace(); 
        }
        return lista;
    }
}