package main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Manual 
{

    private ScrollPane scrollContenido;
    private VBox contenedorSecciones;

    public HBox getView() 
    {
        HBox root = new HBox();
        root.setStyle("-fx-background-color: white;");

        // 1. PANEL LATERAL (ÍNDICE) 
        VBox indice = new VBox(8);
        indice.setPadding(new Insets(20));
        indice.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd; -fx-border-width: 0 1 0 0;");
        indice.setMinWidth(300);

        Label titleIndice = new Label("GUÍA DE OPERACIONES");
        titleIndice.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(5, 0, 10, 0));

        indice.getChildren().addAll(titleIndice, sep);

        // Índice alineado a la estructura real del sistema
        indice.getChildren().addAll(
            crearBtnIndice("1. Acceso al Sistema (Login)", 0),
            crearBtnIndice("2. Recuperación de Contraseña", 1),
            crearBtnIndice("3. Estructura del Menú Principal", 2),
            crearBtnIndice("4. Módulo Clientes", 3),
            crearBtnIndice("5. Módulo Proveedores", 4),
            crearBtnIndice("6. Módulo Estadísticas", 5),
            crearBtnIndice("7. Mi Perfil y Seguridad", 6),
            crearBtnIndice("8. Manual de Usuario", 7),
            crearBtnIndice("9. Cierre de Sesión", 8)
        );

        // 2. ÁREA DE CONTENIDO 
        scrollContenido = new ScrollPane();
        scrollContenido.setFitToWidth(true);
        scrollContenido.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        HBox.setHgrow(scrollContenido, Priority.ALWAYS);

        contenedorSecciones = new VBox(40);
        contenedorSecciones.setPadding(new Insets(40));
        contenedorSecciones.setStyle("-fx-background-color: white;");

        armarContenidoDetallado();

        scrollContenido.setContent(contenedorSecciones);
        root.getChildren().addAll(indice, scrollContenido);
        return root;
    }

    private void armarContenidoDetallado() 
    {
        // SECCIÓN 1: LOGIN
        contenedorSecciones.getChildren().add(crearSeccion(
            "1. Acceso al Sistema (Login)",
            "Para ingresar al sistema Aluglass, siga estos pasos:\n\n" +
            "1. Introduzca su **Nombre de Usuario** y **Contraseña** en los campos correspondientes.\n" +
            "2. Presione el botón **[INGRESAR]**.\n\n" +
            "Si los datos son incorrectos, el sistema emitirá una alerta y denegará el acceso por seguridad.",
            "CAPTURA_LOGIN.png" 
        ));

        // SECCIÓN 2: RECUPERACIÓN DE CONTRASEÑA
        contenedorSecciones.getChildren().add(crearSeccion(
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
        ));

        // SECCIÓN 3: MENÚ PRINCIPAL
        contenedorSecciones.getChildren().add(crearSeccion(
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
        ));

        // SECCIÓN 4: MÓDULO CLIENTES
        contenedorSecciones.getChildren().add(crearSeccion(
            "4. Módulo de Clientes",
            "Este módulo abarca la administración de clientes y su ciclo comercial completo:\n\n" +
            "[IMG:CAPTURA_CLIENTE.png]\n\n" +
            "• **Gestión de Clientes**:\n" +
            "  - **Añadir cliente**: Formulario para dar de alta nuevos contactos.\n" +
            "  - **Ver todos**: Padrón completo con opciones de búsqueda, consulta y edición.\n" +
            "  [IMG:CAPTURA_FORMULARIO_CLIENTE.png]\n\n" +
            "• **Presupuestos de Ventas**:\n" +
            "  - **Añadir presupuesto**: Carga de presupuestos para trabajos de carpintería y vidriería.\n" +
            "  - **Ver todos**: Historial general, filtrado por estados y emisión de comprobantes PDF.\n" +
            "  [IMG:CAPTURA_NUEVO_PRESUPUESTO.png]\n\n" +
            "• **Cobros**:\n" +
            "  - **Registrar cobro**: Carga de ingresos vinculados a presupuestos (admite cálculo automático en USD).\n" +
            "  - **Historial**: Registro histórico de cobros y reimpresión de recibos PDF.\n" +
            "  [IMG:CAPTURA_FORMULARIO_PAGO.png]\n\n" +
            "• **Cuentas Corrientes**:\n" +
            "  - **Listar cuentas**: Consulta global de saldos de todos los clientes.\n" +
            "  - **Cuentas pendientes**: Filtro rápido de clientes con saldos deudores (morosos).",
            ""
        ));

        // SECCIÓN 5: MÓDULO PROVEEDORES
        contenedorSecciones.getChildren().add(crearSeccion(
            "5. Módulo de Proveedores",
            "Permite administrar la relación con los proveedores de insumos y materia prima:\n\n" +
            "[IMG:CAPTURA_PROVEEDORES_MENU.png]\n\n" +
            "• **Gestión de Proveedores**:\n" +
            "  - **Añadir proveedor**: Alta con Razón Social, CUIT, teléfono y datos bancarios (CBU/Alias).\n" +
            "  - **Ver todos**: Padrón general para consultar o modificar datos de proveedores.\n" +
            "  [IMG:CAPTURA_FORMULARIO_PROVEEDOR.png]\n\n" +
            "• **Compras**:\n" +
            "  - **Registrar compra**: Carga de facturas de compra con detalle de ítems e importes.\n" +
            "  - **Historial de compras**: Consulta cronológica y ficha detallada de cada compra.\n" +
            "  [IMG:CAPTURA_REGISTRAR_COMPRA.png]\n\n" +
            "• **Pagos**:\n" +
            "  - **Registrar pago**: Asignación de pagos a facturas de proveedores pendientes.\n" +
            "  - **Historial de pagos**: Registro histórico de egresos a proveedores.\n" +
            "  [IMG:CAPTURA_REGISTRAR_PAGO_PROVEEDOR.png]\n\n" +
            "• **Cuentas Corrientes**:\n" +
            "  - **Deudas y vencimientos**: Monitoreo de saldos a pagar y fechas límite de vencimiento.",
            ""
        ));

        // SECCIÓN 6: ESTADÍSTICAS
        contenedorSecciones.getChildren().add(crearSeccion(
            "6. Módulo de Estadísticas",
            "Proporciona herramientas visuales para el control financiero y operativo del negocio.\n\n" +
            "• **Panel de Control Visual**:\n" +
            "Despliega gráficos e indicadores clave (KPIs) sobre ingresos del mes, rendimiento de ventas, facturación histórica, compras a proveedores y alertas de morosidad/vencimientos.\n\n" +
            "[IMG:CAPTURA_PANEL_ESTADISTICAS.png]",
            ""
        ));

        // SECCIÓN 7: MI PERFIL Y SEGURIDAD
        contenedorSecciones.getChildren().add(crearSeccion(
            "7. Mi Perfil y Seguridad",
            "Gestión de las credenciales de acceso del usuario activo:\n\n" +
            "• **Editar mis credenciales**:\n" +
            "Permite modificar la contraseña actual y actualizar la pregunta/respuesta de seguridad requerida para la recuperación de la cuenta.\n\n" +
            "[IMG:CAPTURA_CONFIGURACION_PERFIL.png]",
            ""
        ));

        // SECCIÓN 8: MANUAL DE USUARIO
        contenedorSecciones.getChildren().add(crearSeccion(
            "8. Manual de Usuario",
            "Herramienta integrada para asistencia en pantalla e instructivo descargable:\n\n" +
            "• **Ver manual de usuario**: Despliega esta pantalla interactiva con navegación por secciones.\n" +
            "• **Exportar manual**: Genera automáticamente un archivo **Manual_Aluglass.pdf** dentro de la carpeta `manuales` ubicada en el directorio del programa.",
            ""
        ));

        // SECCIÓN 9: CIERRE DE SESIÓN
        contenedorSecciones.getChildren().add(crearSeccion(
            "9. Salida Segura",
            "Para finalizar su jornada de trabajo, seleccione la opción **Cerrar sesión**.\n\n" +
            "Esta acción destruye la sesión actual para evitar accesos no autorizados y redirige a la pantalla de Login.",
            "CAPTURA_CERRAR_SESION.png"
        ));
    }

    private VBox crearSeccion(String titulo, String texto, String nombreImagenPrincipal) 
    {
        VBox seccion = new VBox(15);
        
        // 1. Título
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        seccion.getChildren().add(lblTitulo);

        // 2. Procesamiento de Texto e Imágenes Intermedias
        String[] partes = texto.split("\\[IMG:");

        for (int i = 0; i < partes.length; i++) 
        {
            String parte = partes[i];
            
            if (i == 0) 
            {
                if (!parte.trim().isEmpty()) 
                {
                    seccion.getChildren().add(crearFlowTexto(parte));
                }
            } 
            else 
            {
                if (parte.contains("]")) 
                {
                    String[] subPartes = parte.split("\\]", 2);
                    
                    String nombreImg = subPartes[0].trim();
                    seccion.getChildren().add(configurarImagen(nombreImg));
                    
                    if (subPartes.length > 1 && !subPartes[1].trim().isEmpty()) 
                    {
                        seccion.getChildren().add(crearFlowTexto(subPartes[1]));
                    }
                }
            }
        }

        // 3. Imagen Final (Pie de sección)
        if (nombreImagenPrincipal != null && !nombreImagenPrincipal.trim().isEmpty() && nombreImagenPrincipal.contains(".")) 
        {
            seccion.getChildren().add(configurarImagen(nombreImagenPrincipal));
        }

        return seccion;
    }

    private TextFlow crearFlowTexto(String contenido) 
    {
        TextFlow flow = new TextFlow();
        flow.setLineSpacing(5);

        String[] partes = contenido.split("(?<=\\*\\*)|(?=\\*\\*)");
        boolean esNegrita = false;

        for (String parte : partes) 
        {
            if (parte.equals("**")) 
            {
                esNegrita = !esNegrita;
                continue; 
            }

            Text textoNodo = new Text(parte);
            
            if (esNegrita) 
            {
                textoNodo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
            } 
            else 
            {
                textoNodo.setStyle("-fx-font-size: 15px; -fx-font-weight: normal; -fx-fill: #333333;");
            }
            
            flow.getChildren().add(textoNodo);
        }

        return flow;
    }

    private Node configurarImagen(String nombre) 
    {
        HBox centrador = new HBox();
        centrador.setAlignment(Pos.CENTER);
        centrador.setFillHeight(false);

        VBox caja = new VBox();
        caja.setAlignment(Pos.CENTER);
        caja.setPadding(new Insets(10));
        caja.setStyle("-fx-border-color: #EEE9E9; -fx-background-color: #ffffff; -fx-border-radius: 5;");
        caja.setMaxWidth(Region.USE_PREF_SIZE);

        if (nombre == null || nombre.trim().isEmpty() || !nombre.contains(".")) 
        {
            return new VBox(); 
        }

        try 
        {
            Image img = new Image(getClass().getResourceAsStream("/img/" + nombre));
            ImageView iv = new ImageView(img);
            
            iv.setPreserveRatio(true);
            iv.setSmooth(true);

            double anchoOriginal = img.getWidth();
            double limitePantalla = 550; 

            if (anchoOriginal > limitePantalla) 
            {
                iv.setFitWidth(limitePantalla);
            } 
            else
            {
                iv.setFitWidth(anchoOriginal);
            }
            
            caja.getChildren().add(iv);
            centrador.getChildren().add(caja);
        }
        catch (Exception e) 
        {
            Label pendiente = new Label("Captura pendiente: " + nombre);
            pendiente.setStyle("-fx-text-fill: #777; -fx-font-style: italic;");
            caja.getChildren().add(pendiente);
            centrador.getChildren().add(caja);
            return centrador;
        }
        
        return centrador;
    }

    private Button crearBtnIndice(String texto, int index) 
    {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555; -fx-font-size: 13px; -fx-cursor: hand;");

        btn.setOnAction(e -> 
        {
            if (contenedorSecciones.getChildren().size() > index) 
            {
                Node target = contenedorSecciones.getChildren().get(index);
                double totalHeight = contenedorSecciones.getBoundsInLocal().getHeight();
                double targetY = target.getBoundsInParent().getMinY();
                double scrollMax = totalHeight - scrollContenido.getViewportBounds().getHeight();
                scrollContenido.setVvalue(targetY / scrollMax);
            }
        });

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #007bff; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555;"));

        return btn;
    }
}