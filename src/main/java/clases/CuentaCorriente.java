package clases;

public class CuentaCorriente 
{
    private int idCuenta;
    private int idCliente;
    private String nombreCliente;
    private double saldoActual;
    private String estadoPago;

    public CuentaCorriente(int idCuenta, int idCliente, String nombreCliente, double saldoActual, String estadoPago) 
    {
        this.idCuenta = idCuenta;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.saldoActual = saldoActual;
        this.estadoPago = estadoPago;
    }

    public int getIdCuenta() 
    { 
        return idCuenta; 
    }

    public int getIdCliente() 
    { 
        return idCliente; 
    }

    public String getNombreCliente() 
    { 
        return nombreCliente; 
    }

    public double getSaldoActual() 
    { 
        return saldoActual; 
    }
    
    public String getEstadoPago() 
    { 
        return estadoPago; 
    }
}
