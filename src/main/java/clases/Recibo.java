package clases;

public class Recibo
{
    private int idRecibo;
    private int idPago;
    private String fechaEmision;
    private double monto;
    private String descripcion;
    private String numeroRecibo;
    private String rutaPdf;
    private String moneda;
    
    public Recibo(int idRecibo, int idPago, String fechaEmision,
                  double monto, String descripcion,
                  String numeroRecibo, String rutaPdf, String moneda)
    {
        this.idRecibo = idRecibo;
        this.idPago = idPago;
        this.fechaEmision = fechaEmision;
        this.monto = monto;
        this.descripcion = descripcion;
        this.numeroRecibo = numeroRecibo;
        this.rutaPdf = rutaPdf;
        this.moneda = moneda;
    }

    public int getIdRecibo() 
    { 
        return idRecibo; 
    }
    
    public int getIdPago() 
    { 
        return idPago; 
    }
    
    public String getFechaEmision() 
    { 
        return fechaEmision; 
    }
    
    public double getMonto() 
    { 
        return monto; 
    }
    
    public String getDescripcion() 
    { 
        return descripcion; 
    }
    
    public String getNumeroRecibo() 
    { 
        return numeroRecibo; 
    }
    
    public String getRutaPdf() 
    { 
        return rutaPdf; 
    }
    
    public String getMoneda() 
    { 
        return moneda; 
    }
}