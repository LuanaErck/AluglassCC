package clases;

public class Cliente extends Persona
{
    private int id_cliente;
    
    //Constructor
    public Cliente(int id_cliente, String nombre, String telefono, String estado, String cuit) 
    {
        super(nombre, telefono, estado, cuit);
        this.id_cliente = id_cliente;
    }

    //Getters
    public int getIdCliente() 
    { 
        return id_cliente; 
    }
    
    
    @Override
    public String toString()
    {
        return nombre;
    }

    public void setIdCliente(int idCliente) 
    {
        this.id_cliente = id_cliente;
    }
}

