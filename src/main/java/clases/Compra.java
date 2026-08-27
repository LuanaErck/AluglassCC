package clases;

public class Compra extends DocumentoComercial 
{

    private int idCompra;
    private int idProveedor;
    private String nroFactura;
    private String fechaVencimiento;
    private String nombreProveedor;

    public Compra(int idCompra, int idProveedor, String nroFactura, String fecha,
            String detalle, double importe, String fechaVencimiento, String estado) 
    {
        super(fecha, detalle, importe, estado);
        this.idCompra = idCompra;
        this.idProveedor = idProveedor;
        this.nroFactura = nroFactura;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getIdCompra() 
    {
        return idCompra;
    }

    public int getIdProveedor() 
    {
        return idProveedor;
    }

    public String getNroFactura() 
    {
        return nroFactura;
    }

    public String getFechaVencimiento()
    {
        return fechaVencimiento;
    }

    public String getNombreProveedor()
    {
        return nombreProveedor;
    }

    public void setIdCompra(int idCompra) 
    {
        this.idCompra = idCompra;
    }

    public void setNombreProveedor(String nombreProveedor) 
    {
        this.nombreProveedor = nombreProveedor;
    }

    @Override
    public String toString() { return "Compra #" + idCompra + " - " + getFecha() + " - $" + getImporte(); }
}
