package clases;

public class Presupuesto
{
    private int idPresupuesto;
    private int idCliente;
    private String fecha;
    private String descripcion;
    private double total;
    private double saldoPresupuesto;
    private String estado;
    private String nombreCliente;

    public Presupuesto(int idPresupuesto, int idCliente, String fecha,
                       String descripcion, double total,
                       double saldoPresupuesto, String estado)
    {
        this.idPresupuesto = idPresupuesto;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.total = total;
        this.saldoPresupuesto = saldoPresupuesto;
        this.estado = estado;
    }

    public int getIdPresupuesto()
    {
        return idPresupuesto;
    }

    public int getIdCliente()
    {
        return idCliente;
    }

    public String getFecha()
    {
        return fecha;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public double getTotal()
    {
        return total;
    }

    public double getSaldoPresupuesto()
    {
        return saldoPresupuesto;
    }

    public String getEstado()
    {
        return estado;
    }
    
    public String getNombreCliente()
    {
        return nombreCliente;
    }
    
    public void setNombreCliente (String nombreCliente)
    {
        this.nombreCliente = nombreCliente;
    }

    //Para mostrar en ComboBox
    @Override
    public String toString() {
        //Esto es lo que aparecerá en la lista desplegable
        return "ID: " + idPresupuesto + " - " + descripcion + " (Saldo: $" + saldoPresupuesto + ")";
    }
}
