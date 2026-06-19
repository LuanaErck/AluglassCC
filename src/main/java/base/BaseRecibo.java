package base;

import clases.Pago;
import utilidades.ConexionSQlite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BaseRecibo 
{

    public void registrarRecibo(int idPago, double monto, String descripcion, 
                               String numeroRecibo, String rutaPdf, String moneda) 
    {
        
        // Usamos INSERT OR REPLACE o verificamos existencia para evitar errores si se regenera
        String sql = """
            INSERT INTO recibos
            (id_pago, fecha_emision, monto, descripcion, numero_recibo, ruta_pdf, moneda)
            VALUES (?, date('now'), ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, idPago);
            stmt.setDouble(2, monto);
            stmt.setString(3, descripcion);
            stmt.setString(4, numeroRecibo);
            stmt.setString(5, rutaPdf);
            stmt.setString(6, moneda);

            stmt.executeUpdate();
        } 
        catch (Exception e) 
        {
            System.err.println("Error al registrar recibo en BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String obtenerRutaPdfPorPago(int idPago) 
    {
        String sql = "SELECT ruta_pdf FROM recibos WHERE id_pago = ? ORDER BY id_recibo DESC LIMIT 1";
        String ruta = null;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, idPago);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) 
            {
                ruta = rs.getString("ruta_pdf");
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return ruta;
    }

    public Pago obtenerDatosParaRecibo(int idPago) 
    {
        Pago pago = null;
        String sql = """
            SELECT p.*, c.nombre AS nombre_cliente
            FROM pagos p
            INNER JOIN clientes c ON p.id_cliente = c.id_cliente
            WHERE p.id_pago = ?
        """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, idPago);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) 
            {
                pago = new Pago(
                    rs.getInt("id_pago"),
                    rs.getInt("id_cliente"),
                    rs.getInt("id_presupuesto"),
                    rs.getString("fecha_pago"),
                    rs.getDouble("importe"),
                    rs.getString("moneda"),
                    rs.getDouble("cotizacion"),
                    rs.getDouble("importe_pesos"),
                    rs.getString("forma_pago"),
                    rs.getString("observaciones"),
                    rs.getString("nombre_cliente"),
                    rs.getString("estado")
                );
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error al obtener datos para recibo: " + e.getMessage());
            e.printStackTrace();
        }
        return pago;
    }
}