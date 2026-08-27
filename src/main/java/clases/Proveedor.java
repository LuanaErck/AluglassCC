package clases;

public class Proveedor extends Persona 
{

    private int idProveedor;
    private String cbuAlias;

    public Proveedor(int idProveedor, String nombre, String telefono, String estado,
            String cuit, String cbuAlias) 
    {
        super(nombre, telefono, estado, cuit);
        this.idProveedor = idProveedor;
        this.cbuAlias = cbuAlias;
    }

    public int getIdProveedor() 
    {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) 
    {
        this.idProveedor = idProveedor;
    }

    public String getCbuAlias() 
    {
        return cbuAlias;
    }

    public void setCbuAlias(String cbuAlias) 
    {
        this.cbuAlias = cbuAlias;
    }

    @Override
    public String toString() 
    {
        return nombre;
    }
}
