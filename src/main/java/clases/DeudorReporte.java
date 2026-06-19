package clases;

public class DeudorReporte 
{
    private String nombreCompleto;
    private double saldo;
    private int diasAtraso;
    private String accion;

    public DeudorReporte(String nombre, double saldo, int dias, String accion)
    {
        this.nombreCompleto = nombre;
        this.saldo = saldo;
        this.diasAtraso = dias;
        this.accion = accion;
    }

    // Getters necesarios para la TableView
    public String getNombreCompleto() 
    { 
        return nombreCompleto; 
    }
    
    public double getSaldo() 
    {
        return saldo;
    }
    
    public int getDiasAtraso() 
    { 
        return diasAtraso; 
    }
    
    public String getAccion() 
    { 
        return accion; 
    }
}