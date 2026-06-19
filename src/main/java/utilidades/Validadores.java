package utilidades;

public class Validadores
{

    // CAMPO VACÍO
    public static boolean estaVacio(String texto)
    {
        return texto == null || texto.trim().isEmpty();
    }

    // ES NÚMERO (double)
    public static boolean esNumero(String texto)
    {
        return texto.matches("\\d+(\\.\\d+)?");
    }

    // SOLO NÚMEROS ENTEROS
    public static boolean esEntero(String texto)
    {
        return texto.matches("\\d+");
    }

    // MAYOR A 0
    public static boolean esMayorACero(double valor)
    {
        return valor > 0;
    }

    // TEXTO SOLO LETRAS (opcional)
    public static boolean esTexto(String texto)
    {
        return texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }
}