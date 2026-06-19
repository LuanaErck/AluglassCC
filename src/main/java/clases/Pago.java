package clases;

public class Pago
{
    private int idPago;
    private int idCliente;
    private int idPresupuesto;
    private String fechaPago;

    private double importe;
    private String moneda;
    private Double cotizacion;
    private double importePesos;

    private String formaPago;
    private String observaciones;
    
    private String nombreCliente;
    
    private String estado;

    public Pago(int idPago, int idCliente, int idPresupuesto, String fechaPago,
                double importe, String moneda, Double cotizacion, double importePesos,
                String formaPago, String observaciones, String nombreCliente, String estado)
    {
        this.idPago = idPago;
        this.idCliente = idCliente;
        this.idPresupuesto = idPresupuesto;
        this.fechaPago = fechaPago;
        this.importe = importe;
        this.moneda = moneda;
        this.cotizacion = cotizacion;
        this.importePesos = importePesos;
        this.formaPago = formaPago;
        this.observaciones = observaciones;
        this.nombreCliente = nombreCliente;
        this.estado = estado;
    }

    public int getIdPago() 
    { 
        return idPago; 
    }
    
    public int getIdCliente() 
    { 
        return idCliente; 
    }
    
    public int getIdPresupuesto() 
    { 
        return idPresupuesto; 
    }
    
    public String getFechaPago() 
    { 
        return fechaPago; 
    }

    public double getImporte() 
    { 
        return importe; 
    }
    
    public String getMoneda() 
    { 
        return moneda; 
    }
    
    public Double getCotizacion() 
    { 
        return cotizacion; 
    }
    
    public double getImportePesos() 
    { 
        return importePesos; 
    }

    public String getFormaPago() 
    { 
        return formaPago; 
    }
    
    public String getObservaciones() 
    { 
        return observaciones; 
    }
    
    public String getNombreCliente() 
    { 
        return nombreCliente; 
    }
    
    public String getEstado() 
    { 
        return estado; 
    }
    
    public void setEstado(String estado)
    {
        this.estado = estado;
    }
}
