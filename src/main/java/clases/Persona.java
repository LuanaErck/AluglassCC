package clases;

//Datos de contacto compartidos por clientes y proveedores.
public abstract class Persona 
{

    protected String nombre;
    protected String telefono;
    protected String estado;
    protected String cuit;

    protected Persona(String nombre, String telefono, String estado, String cuit) 
    {
        this.nombre = nombre;
        this.telefono = telefono;
        this.estado = estado;
        this.cuit = cuit;
    }

    public String getNombre() 
    {
        return nombre;
    }

    public String getTelefono() 
    {
        return telefono;
    }

    public String getEstado()
    {
        return estado;
    }

    public String getCuit() 
    {
        return cuit;
    }

    public void setNombre(String nombre) 
    {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    public void setCuit(String cuit)
    {
        this.cuit = cuit;
    }
}
