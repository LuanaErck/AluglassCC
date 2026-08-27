package clases;

public class CuentaProveedor 
{
    private final int idProveedor;
    private final String nombreProveedor;
    private final double saldo;
    private final String proximoVencimiento;

    public CuentaProveedor(int idProveedor, String nombreProveedor, double saldo, String proximoVencimiento) 
    {
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.saldo = saldo;
        this.proximoVencimiento = proximoVencimiento;
    }
    
    public int getIdProveedor() 
    { 
        return idProveedor; 
    }
    
    public String getNombreProveedor() 
    { 
        return nombreProveedor; 
    }
    
    public double getSaldo() 
    { 
        return saldo; 
    }
    public String getProximoVencimiento()
    { 
        return proximoVencimiento; 
    }
}
