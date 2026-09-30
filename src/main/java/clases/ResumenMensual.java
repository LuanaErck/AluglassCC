package clases;

//Para comparar los ingresos y egresos de todos los meses
public class ResumenMensual 
{
    private final String periodo;
    private final double total;

    public ResumenMensual(String periodo, double total) 
    {
        this.periodo = periodo;
        this.total = total;
    }
    public String getPeriodo() 
    { 
        return periodo; 
    }
    
    public double getTotal() 
    { 
        return total; 
    }
}
