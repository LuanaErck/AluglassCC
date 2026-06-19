package utilidades;

import clases.Pago;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

public class GeneradorReciboPDF 
{

    /**
     * Genera un archivo PDF para un pago específico.
     * @param pago Objeto Pago con todos sus datos (importe, moneda, cotizacion, importePesos).
     * @param nombreCliente Nombre del cliente para el encabezado.
     * @param numeroRecibo Identificador único para el nombre del archivo y el título.
     * @return La ruta del archivo generado o null si hubo un error.
     */
    public static String generar(Pago pago, String nombreCliente, String numeroRecibo) 
    {
        // 1. Configuración de Carpeta y Ruta
        String carpetaDestino = "recibos/";
        File carpeta = new File(carpetaDestino);
        if (!carpeta.exists()) 
        {
            carpeta.mkdirs();
        }

        String rutaArchivo = carpetaDestino + numeroRecibo + ".pdf";

        // 2. Configuración de Formateadores de Moneda
        // Formato para Pesos Argentinos (es_AR)
        NumberFormat fmtARS = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        // Formato para Dólares (en_US)
        NumberFormat fmtUSD = NumberFormat.getCurrencyInstance(Locale.US);

        try 
        {
            PdfWriter writer = new PdfWriter(rutaArchivo);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 3. ENCABEZADO PROFESIONAL
            document.add(new Paragraph("ALUGLASS")
                    .setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Vidrieria y Carpinteria de Aluminio")
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER).setItalic());
            
            document.add(new Paragraph("\nCOMPROBANTE DE PAGO")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("Recibo N°: " + numeroRecibo)
                    .setTextAlignment(TextAlignment.RIGHT));
            
            document.add(new LineSeparator(new SolidLine()));

            // 4. DATOS DE LA OPERACIÓN
            document.add(new Paragraph("\nDETALLE DEL CLIENTE").setBold().setUnderline());
            document.add(new Paragraph("Cliente: " + nombreCliente));
            document.add(new Paragraph("Fecha: " + pago.getFechaPago()));
            document.add(new Paragraph("Forma de Pago: " + pago.getFormaPago()));

            document.add(new Paragraph("\nDETALLE DE VALORES").setBold().setUnderline());

            // 5. LÓGICA DE MONEDA E IMPORTES
            // Limpiamos el string de moneda para evitar errores por espacios
            String monedaStr = (pago.getMoneda() != null) ? pago.getMoneda().trim() : "ARS";

            if ("Dolares".equals(monedaStr)) 
            {
                // Caso Dólares: Mostramos Importe Original -> Cotización -> Total en Pesos
                document.add(new Paragraph("Monto Recibido: " + fmtUSD.format(pago.getImporte()) + " USD"));
                document.add(new Paragraph("Cotización Aplicada: " + fmtARS.format(pago.getCotizacion())));
                document.add(new Paragraph("--------------------------------------------------"));
                document.add(new Paragraph("TOTAL ACREDITADO EN PESOS: " + fmtARS.format(pago.getImportePesos()))
                        .setBold().setFontSize(14));
            } 
            else 
            {
                // Caso Pesos: Mostramos el importe directo
                document.add(new Paragraph("TOTAL RECIBIDO (ARS): " + fmtARS.format(pago.getImporte()))
                        .setBold().setFontSize(14));
            }

            // 6. OBSERVACIONES
            if (pago.getObservaciones() != null && !pago.getObservaciones().trim().isEmpty()) 
            {
                document.add(new Paragraph("\nObservaciones:").setBold().setFontSize(10));
                document.add(new Paragraph(pago.getObservaciones()).setFontSize(10).setItalic());
            }

            // 7. PIE DE PÁGINA
            document.add(new Paragraph("\n\n\n\n"));
            document.add(new LineSeparator(new SolidLine()));
            document.add(new Paragraph("Este documento sirve como comprobante de pago de los conceptos anteriormente descritos.")
                    .setFontSize(8).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Gracias por elegir ALUGLASS")
                    .setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            document.close();
            return rutaArchivo;

        } 
        catch (Exception e) 
        {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}