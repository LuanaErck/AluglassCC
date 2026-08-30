package utilidades;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.File;
import java.io.InputStream;

public class GeneradorManualPDF 
{

    public static String generar() 
    {
        File carpeta = new File("manuales");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        String ruta = "manuales/Manual_Aluglass.pdf";

        try (PdfWriter writer = new PdfWriter(ruta);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf)) 
        {
            // ENCABEZADO PRINCIPAL (26pt / 15pt)
            documento.add(new Paragraph("ALUGLASS CC")
                    .setBold()
                    .setFontSize(26)
                    .setFontColor(ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4));

            documento.add(new Paragraph("MANUAL DE USUARIO Y OPERACIONES")
                    .setFontSize(15)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(24));

            // SECCIÓN 1
            agregarSeccion(documento, 
                "1. Acceso al Sistema (Login)",
                "Para ingresar al sistema Aluglass, siga estos pasos:\n\n" +
                "1. Introduzca su **Nombre de Usuario** y **Contraseña** en los campos correspondientes.\n" +
                "2. Presione el botón **[INGRESAR]**.\n\n" +
                "Si los datos son incorrectos, el sistema emitirá una alerta y denegará el acceso por seguridad.",
                "CAPTURA_LOGIN.png"
            );

            // SECCIÓN 2
            agregarSeccion(documento, 
                "2. Recuperación de Contraseña",
                "En caso de olvido de credenciales, el sistema cuenta con un asistente de recuperación:\n\n" +
                "• **Paso 1: Identificación**\n" +
                "Haga clic en **[¿Olvidó su contraseña?]** e ingrese su nombre de usuario.\n" +
                "[IMG:CAPTURA_OLVIDO.png]\n\n" +
                "• **Paso 2: Validación de Identidad**\n" +
                "Responda a su pregunta de seguridad preconfigurada.\n" +
                "[IMG:CAPTURA_PREGUNTA.png]\n\n" +
                "• **Paso 3: Restablecimiento**\n" +
                "Una vez validado, el sistema le permitirá ingresar y confirmar su nueva clave de acceso.\n" +
                "[IMG:CAPTURA_NUEVA_CONTRASEÑA.png]\n\n" +
                "Al finalizar, será redirigido automáticamente a la pantalla de inicio para ingresar con sus nuevos datos.",
                ""
            );

            // SECCIÓN 3
            agregarSeccion(documento, 
                "3. Estructura del Menú Principal",
                "El menú lateral se despliega organizando las operaciones según las áreas clave de la vidriería:\n\n" +
                "[IMG:CAPTURA_MENU.png]\n\n" +
                "1. **Clientes**: Gestión comercial, emisión de presupuestos, cobranzas y cuentas corrientes.\n" +
                "2. **Proveedores**: Gestión de compras, registro de pagos y seguimiento de deudas/vencimientos.\n" +
                "3. **Estadísticas**: Acceso directo al Panel de Control Visual.\n" +
                "4. **Mi Perfil**: Edición de credenciales de seguridad.\n" +
                "5. **Manual de Usuario**: Visualización interactiva y exportación en PDF.\n" +
                "6. **Cerrar Sesión**: Salida segura del sistema.",
                ""
            );

            // SECCIÓN 4
            agregarSeccion(documento, 
                "4. Módulo de Clientes",
                "Este módulo abarca la administración de clientes y su ciclo comercial completo:\n\n" +
                "[IMG:CAPTURA_CLIENTE.png]\n\n" +
                "• **Gestión de Clientes**:\n" +
                "  - **Añadir cliente**: Formulario para dar de alta nuevos contactos de forma directa.\n" +
                "  [IMG:CAPTURA_FORMULARIO_CLIENTE.png]\n" +
                "  - **Ver todos**: Padrón completo con opciones de búsqueda, consulta y edición.\n" +
                "  [IMG:VER_TODOS_CLIENTE.png]\n\n" +
                "• **Presupuestos de Ventas**:\n" +
                "  - **Añadir presupuesto**: Carga de presupuestos para trabajos de carpintería y vidriería.\n" +
                "  [IMG:CAPTURA_NUEVO_PRESUPUESTO.png]\n" +
                "  - **Alta Rápida de Cliente**: Al generar un nuevo presupuesto, si el cliente no está registrado, puede presionar el botón **[+] Nuevo Cliente** ubicado junto al selector para registrarlo de forma express sin salir ni perder los datos del comprobante.\n" +
                "  - **Ver todos**: Historial general, edición, filtrado por estados y emisión de comprobantes PDF.\n" +
                "  [IMG:VER_TODOS_PRESUPUESTO.png]\n" +
                "  - **Advertencia de Eliminación**: Al intentar eliminar un presupuesto existente, el sistema mostrará un cuadro de diálogo de **confirmación de seguridad**. Si el presupuesto ya cuenta con pagos asociados o afectó la cuenta corriente, la acción actualizará o revertirá el saldo correspondiente tras la confirmación explícita del usuario.\n\n" +
                "• **Cobros**:\n" +
                "  - **Registrar cobro**: Carga de ingresos vinculados a presupuestos (admite cálculo automático en USD).\n" +
                "  [IMG:CAPTURA_FORMULARIO_PAGO.png]\n" +
                "  - **Alta Rápida de Cliente**: Al momento de registrar un nuevo cobro, puede utilizar el botón **[+] Nuevo Cliente** situado al lado del selector para realizar el alta inmediata sin abandonar la carga del recibo.\n" +
                "  - **Historial**: Registro histórico de cobros y reimpresión de recibos PDF.\n" +
                "  [IMG:HISTORIAL_PAGOS.png]\n" +
                "  - **Advertencia al Anular/Eliminar Cobro**: La eliminación o anulación de un pago registrado solicita confirmación obligatoria. Al confirmarse, el monto cobrado volverá a impactar como saldo adeudado en la cuenta corriente del cliente.\n\n" +
                "• **Cuentas Corrientes**:\n" +
                "  - **Listar cuentas**: Consulta global de saldos de todos los clientes.\n" +
                "  [IMG:CAPTURA_LISTADO_GENERAL.png]\n" +
                "  - **Cuentas pendientes**: Filtro rápido de clientes con saldos deudores (morosos).\n" +
                "  [IMG:CAPTURA_LISTADO_MOROSOS.png]",
                ""
            );

            // SECCIÓN 5
            agregarSeccion(documento, 
                "5. Módulo de Proveedores",
                "Permite administrar la relación con los proveedores de insumos y materia prima:\n\n" +
                "[IMG:CAPTURA_PROVEEDORES_MENU.png]\n\n" +
                "• **Gestión de Proveedores**:\n" +
                "  - **Añadir proveedor**: Alta con Razón Social, CUIT, teléfono y datos bancarios (CBU/Alias).\n" +
                "  [IMG:CAPTURA_FORMULARIO_PROVEEDOR.png]\n" +
                "  - **Ver todos**: Padrón general para consultar o modificar datos de proveedores.\n" +
                "  [IMG:LISTADO_PROVEEDORES.png]\n\n" +
                "• **Compras**:\n" +
                "  - **Registrar compra**: Carga de facturas de compra con detalle de ítems e importes.\n" +
                "  [IMG:CAPTURA_REGISTRAR_COMPRA.png]\n" +
                "  - **Alta Rápida de Proveedor**: Desde la pantalla de registro de compra, puede presionar el botón **[+] Nuevo Proveedor** ubicado al lado del selector para dar de alta un nuevo proveedor en el momento sin interrumpir el comprobante.\n" +
                "  - **Historial de compras**: Consulta cronológica y ficha detallada de cada compra.\n" +
                "  [IMG:HISTORIAL_COMPRAS.png]\n" +
                "  - **Advertencia al Eliminar Compra**: Si requiere eliminar o cancelar una compra del historial, una ventana de advertencia le solicitará confirmar la acción. Al proceder, se ajustará automáticamente el saldo adeudado en la cuenta corriente del proveedor.\n\n" +
                "• **Pagos**:\n" +
                "  - **Registrar pago**: Asignación de pagos a facturas de proveedores pendientes.\n" +
                "  [IMG:CAPTURA_REGISTRAR_PAGO_PROVEEDOR.png]\n" +
                "  - **Historial de pagos**: Registro histórico de egresos a proveedores.\n" +
                "  [IMG:HISTORIAL_PAGO_PROVEEDOR.png]\n" +
                "  - **Advertencia al Eliminar Pago**: Cualquier anulación de pago a proveedor requiere confirmación mediante alerta. La eliminación restituirá el comprobante a estado pendiente de pago.\n\n" +
                "• **Cuentas Corrientes**:\n" +
                "  - **Deudas y vencimientos**: Monitoreo de saldos a pagar y fechas límite de vencimiento.\n" +
                "  [IMG:DEUDAS_VENCIMIENTOS.png]",
                ""
            );

            // SECCIÓN 6
            agregarSeccion(documento, 
                "6. Módulo de Estadísticas",
                "Proporciona herramientas visuales para el control financiero y operativo del negocio.\n\n" +
                "[IMG:CAPTURA_PANEL_ESTADISTICAS.png]\n\n" +
                "• **Panel de Control Visual**:\n" +
                "Despliega gráficos e indicadores clave sobre ingresos del mes, rendimiento de ventas, facturación histórica, compras a proveedores y alertas de morosidad/vencimientos.\n\n" +
                "[IMG:ESTADISTICAS_CLIENTES.png]\n" +
                "[IMG:ESTADISTICAS_PROVEEDORES.png]",
                ""
            );

            // SECCIÓN 7
            agregarSeccion(documento, 
                "7. Mi Perfil y Seguridad",
                "Gestión de las credenciales de acceso del usuario activo:\n\n" +
                "• **Editar mis credenciales**:\n" +
                "Permite modificar la contraseña actual y actualizar la pregunta/respuesta de seguridad requerida para la recuperación de la cuenta.\n\n" +
                "[IMG:CONFIGURACION_PERFIL.png]",
                ""
            );

            // SECCIÓN 8
            agregarSeccion(documento, 
                "8. Manual de Usuario",
                "Herramienta integrada para asistencia en pantalla e instructivo descargable:\n\n" +
                "• **Ver manual de usuario**: Despliega la pantalla interactiva con navegación por secciones dentro del sistema.\n" +
                "• **Exportar manual**: Genera automáticamente este archivo **Manual_Aluglass.pdf** dentro de la carpeta `manuales` ubicada en el directorio raíz del programa.",
                "MANUAL.png"
            );

            // SECCIÓN 9
            agregarSeccion(documento, 
                "9. Salida Segura",
                "Para finalizar su jornada de trabajo, seleccione la opción **Cerrar sesión**.\n\n" +
                "Esta acción destruye la sesión actual para evitar accesos no autorizados y redirige a la pantalla de Login.",
                "CAPTURA_CERRAR_SESION.png"
            );

            return ruta;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }

    private static void agregarSeccion(Document doc, String titulo, String texto, String imagenPie) 
    {
        // Línea separadora de secciones más visible
        SolidLine linea = new SolidLine(1.0f);
        linea.setColor(ColorConstants.LIGHT_GRAY);
        LineSeparator ls = new LineSeparator(linea);
        ls.setMarginTop(20);
        ls.setMarginBottom(12);
        doc.add(ls);

        // Título de Sección (20pt, Negrita)
        doc.add(new Paragraph(titulo)
                .setBold()
                .setFontSize(20)
                .setFontColor(ColorConstants.BLACK)
                .setMarginBottom(10));

        // Procesar texto
        String[] partes = texto.split("\\[IMG:");

        for (int i = 0; i < partes.length; i++) 
        {
            String parte = partes[i];

            if (i == 0) 
            {
                if (!parte.trim().isEmpty()) {
                    doc.add(crearParrafoConFormato(parte));
                }
            } 
            else 
            {
                if (parte.contains("]")) 
                {
                    String[] subPartes = parte.split("\\]", 2);
                    String nombreImg = subPartes[0].trim();
                    
                    insertarImagenPDF(doc, nombreImg);

                    if (subPartes.length > 1 && !subPartes[1].trim().isEmpty()) {
                        doc.add(crearParrafoConFormato(subPartes[1]));
                    }
                }
            }
        }

        // Imagen al pie de la sección
        if (imagenPie != null && !imagenPie.trim().isEmpty() && imagenPie.contains(".")) {
            insertarImagenPDF(doc, imagenPie);
        }
    }

    private static Paragraph crearParrafoConFormato(String contenido) 
    {
        // Párrafo principal a 14pt con interlineado holgado 1.4f
        Paragraph p = new Paragraph().setFontSize(14).setMultipliedLeading(1.4f).setMarginBottom(8);
        String[] partes = contenido.split("(?<=\\*\\*)|(?=\\*\\*)");
        boolean esNegrita = false;

        for (String parte : partes) 
        {
            if (parte.equals("**")) 
            {
                esNegrita = !esNegrita;
                continue;
            }

            Text txt = new Text(parte);
            if (esNegrita) {
                // Negritas destacadas a 14pt en negro sólido
                txt.setBold().setFontColor(ColorConstants.BLACK);
            } else {
                txt.setFontColor(ColorConstants.DARK_GRAY);
            }
            p.add(txt);
        }
        return p;
    }

    private static void insertarImagenPDF(Document doc, String nombreImagen) 
    {
        try (InputStream is = GeneradorManualPDF.class.getResourceAsStream("/img/" + nombreImagen)) 
        {
            if (is != null) 
            {
                byte[] bytes = is.readAllBytes();
                Image img = new Image(ImageDataFactory.create(bytes));
                img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                img.setMarginTop(10);
                img.setMarginBottom(10);

                // Escalar si excede el ancho útil
                if (img.getImageScaledWidth() > 450) {
                    img.scaleToFit(450, 600);
                }

                doc.add(img);
            } 
            else 
            {
                Paragraph p = new Paragraph("[ Captura pendiente: " + nombreImagen + " ]")
                        .setItalic()
                        .setFontSize(11)
                        .setFontColor(ColorConstants.GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(6)
                        .setMarginBottom(6);
                doc.add(p);
            }
        } 
        catch (Exception e) 
        {
            // Manejo silencioso
        }
    }
}