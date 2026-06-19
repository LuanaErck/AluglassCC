package clases;

public class Cliente 
{
    private int id_cliente;
    private String nombre;
    private String telefono;
    private String estado;
    
    //Constructor
    public Cliente(int id_cliente, String nombre, String telefono, String estado) 
    {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.estado = estado;
    }

    //Getters
    public int getIdCliente() 
    { 
        return id_cliente; 
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
    
    @Override
    public String toString()
    {
        return nombre;
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
}

