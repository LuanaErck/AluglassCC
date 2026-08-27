package clases;

//Datos comunes de documentos de venta (presupuesto) y compra

public abstract class DocumentoComercial 
{

    protected String fecha;
    protected String detalle;
    protected double importe;
    protected String estado;

    protected DocumentoComercial(String fecha, String detalle, double importe, String estado) 
    {
        this.fecha = fecha;
        this.detalle = detalle;
        this.importe = importe;
        this.estado = estado;
    }

    public String getFecha() 
    {
        return fecha;
    }

    public String getDetalle()
    {
        return detalle;
    }

    public double getImporte() 
    {
        return importe;
    }

    public String getEstado() 
    {
        return estado;
    }
}
