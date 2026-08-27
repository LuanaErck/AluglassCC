package clases;

public class Presupuesto extends DocumentoComercial
{
    private int idPresupuesto;
    private int idCliente;
    private double saldoPresupuesto;
    private String nombreCliente;

    public Presupuesto(int idPresupuesto, int idCliente, String fecha,
                       String descripcion, double total,
                       double saldoPresupuesto, String estado)
    {
        super(fecha, descripcion, total, estado);
        this.idPresupuesto = idPresupuesto;
        this.idCliente = idCliente;
        this.saldoPresupuesto = saldoPresupuesto;
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
        return super.getFecha();
    }

    public String getDescripcion()
    {
        return getDetalle();
    }

    public double getTotal()
    {
        return getImporte();
    }

    public double getSaldoPresupuesto()
    {
        return saldoPresupuesto;
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
    public String toString() 
    {
        //Esto es lo que aparecerá en la lista desplegable
        return "ID: " + idPresupuesto + " - " + getDetalle() + " (Saldo: $" + saldoPresupuesto + ")";
    }
}
