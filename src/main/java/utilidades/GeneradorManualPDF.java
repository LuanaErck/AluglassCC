package utilidades;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.File;

public class GeneradorManualPDF 
{
    public static String generar() 
    {
        File carpeta = new File("manuales");
        if (!carpeta.exists()) carpeta.mkdirs();
        String ruta = "manuales/Manual_Aluglass.pdf";
        
        try (PdfWriter writer = new PdfWriter(ruta); PdfDocument pdf = new PdfDocument(writer); Document documento = new Document(pdf)) 
        {
            documento.add(new Paragraph("ALUGLASS CC").setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER));
            documento.add(new Paragraph("MANUAL DE USUARIO").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            agregar(documento, "1. Acceso y navegación", "Ingrese con sus credenciales. El menú lateral organiza Clientes, Proveedores, Estadísticas, Perfil y Manual de usuario.");
            agregar(documento, "2. Clientes", "Permite dar de alta, listar y editar clientes. Cada alta crea una cuenta corriente. Desde Presupuestos se registran trabajos y desde Cobros se selecciona cliente, presupuesto, fecha, importe, moneda y forma de pago.");
            agregar(documento, "3. Proveedores", "Permite dar de alta y listar proveedores. Cada proveedor tiene cuenta corriente propia. Las altas rápidas mediante el botón + están disponibles dentro de los formularios relacionados.");
            agregar(documento, "4. Compras", "Registre proveedor, factura, fechas, importe y detalle de ítems. Las compras nacen pendientes; su estado se actualiza automáticamente según los pagos asociados. El historial permite abrir el detalle y ver sus pagos.");
            agregar(documento, "5. Pagos a proveedores", "Seleccione proveedor y compra, luego fecha, importe y forma de pago. Se admiten pagos mayores al importe de la compra: el excedente queda como saldo a favor del proveedor.");
            agregar(documento, "6. Cuentas corrientes", "Las cuentas de clientes muestran deudas y cobros. Las cuentas de proveedores muestran saldo, vencimiento más próximo, compras y pagos asociados.");
            agregar(documento, "7. Panel de control visual", "La pestaña Clientes resume ingresos y morosidad. La pestaña Proveedores resume compras, deuda con proveedores y alertas de vencimientos.");
            agregar(documento, "8. Perfil y seguridad", "Permite actualizar contraseña, pregunta y respuesta de seguridad.");
            agregar(documento, "9. Exportación del manual", "Desde Manual de usuario > Exportar manual se genera este PDF en la carpeta manuales.");
            documento.add(new Paragraph("Capturas sugeridas: CAPTURA_MENU.png, CAPTURA_FORMULARIO_CLIENTE.png, CAPTURA_REGISTRAR_COMPRA.png, CAPTURA_REGISTRAR_PAGO_PROVEEDOR.png, CAPTURA_CUENTAS_PROVEEDORES.png y CAPTURA_ESTADISTICAS_PROVEEDORES.png.").setItalic().setFontSize(10));
            return ruta;
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
            return null; 
        }
    }
    private static void agregar(Document d, String titulo, String texto) 
    { 
        d.add(new Paragraph(titulo).setBold().setFontSize(15)); 
        d.add(new Paragraph(texto).setFontSize(11)); 
    }
}
