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
        VBox indice = new VBox(10);
        indice.setPadding(new Insets(25));
        indice.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd; -fx-border-width: 0 1 0 0;");
        indice.setMinWidth(280);

        Label titleIndice = new Label("GUÍA DE OPERACIONES");
        titleIndice.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 0, 10, 0));

        indice.getChildren().addAll(titleIndice, sep);

        indice.getChildren().addAll(
            crearBtnIndice("1. Acceso al Sistema", 0),
            crearBtnIndice("2. Recuperación de contraseña", 1),
            crearBtnIndice("3. Panel Principal (Menú)", 2),
            crearBtnIndice("4. Módulo de Clientes", 3),
            crearBtnIndice("5. Cuentas Corrientes", 4),
            crearBtnIndice("6. Gestión de Presupuestos", 5),
            crearBtnIndice("7. Registro de Cobranzas", 6),
            crearBtnIndice("8. Reportes y Estadísticas", 7),
            crearBtnIndice("9. Mi Perfil y Seguridad", 8),
            crearBtnIndice("10. Cierre de Sesión", 9)
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
        // SECCIÓN 1: LOGIN (Esta está perfecta)
        contenedorSecciones.getChildren().add(crearSeccion(
            "1. Acceso al Sistema (Login)",
            "Para ingresar a Aluglass, siga estos pasos:\n\n" +
            "1. Introduzca su **Nombre de Usuario** y **Contraseña** en los campos correspondientes.\n" +
            "2. Presione el botón **[INGRESAR]**.\n\n" +
            "Si los datos son incorrectos, el sistema emitirá una alerta y denegará el acceso por seguridad.",
            "CAPTURA_LOGIN.png" 
        ));

        // SECCIÓN 2: RECUPERACIÓN DE CONTRASEÑA (CORREGIDA: Se quitó el texto del parámetro de imagen)
        contenedorSecciones.getChildren().add(crearSeccion(
            "2. Recuperación de Contraseña",
            "En caso de olvido de credenciales, el sistema cuenta con un asistente de recuperación:\n\n" +
            "• **Paso 1: Identificación**\n" +
            "Haga clic en **[¿Olvidó su contraseña?]** e ingrese su nombre de usuario.\n" +
            "[IMG:CAPTURA_OLVIDO.png]\n\n" +
            "• **Paso 2: Validation de Identidad**\n" +
            "Responda a su pregunta de seguridad preconfigurada. La respuesta debe ser exacta.\n" +
            "[IMG:CAPTURA_PREGUNTA.png]\n\n" +
            "• **Paso 3: Restablecimiento**\n" +
            "Una vez validado, el sistema le permitirá ingresar y confirmar su nueva clave de acceso.\n" +
            "[IMG:CAPTURA_NUEVA_CONTRASEÑA.png]\n\n" +
            "Al finalizar, será redirigido automáticamente a la pantalla de inicio para ingresar con sus nuevos datos.",
            "" // Se deja vacío porque las imágenes ya se cargan de forma intermedia con [IMG:]
        ));

        // SECCIÓN 3: MENÚ PRINCIPAL
        contenedorSecciones.getChildren().add(crearSeccion(
            "3. Panel Principal de Navegación",
            "Desde el menú principal podrá acceder a todos los módulos operativos del sistema:\n\n" +
            "[IMG:CAPTURA_MENU.png]\n\n" +
            "• **Clientes**: Administración de la base de datos de contactos.\n" +
            "• **Cuentas**: Monitoreo de saldos y estados financieros.\n" +
            "• **Pagos**: Registro de ingresos y consulta de historial global.\n" +
            "• **Presupuestos**: Carga de trabajos de carpintería y seguimiento.\n" +
            "• **Estadísticas**: Análisis visual del rendimiento del negocio.\n" +
            "• **Mi Perfil**: Configuración de seguridad del usuario actual.\n" +
            "• **Cerrar Sesión**: Salida segura del sistema.",
            ""
        ));

        // SECCIÓN 4: GESTIÓN DE CLIENTES
        contenedorSecciones.getChildren().add(crearSeccion(
            "4. Módulo de Clientes",
            "Este módulo permite administrar la información de los clientes y vincularlos a sus estados financieros.\n\n" +
            "[IMG:CAPTURA_CLIENTE.png]\n\n" +
            "**A. Consulta y Listado:**\n" +
            "Utilice el botón **[VER TODOS]** para desplegar el padrón completo. Aquí podrá visualizar el estado rápido de cada contacto.\n" +
            "[IMG:CAPTURA_LISTA_TODOS_CLIENTES.png]\n\n" +
            "**B. Registro de Nuevo Cliente:**\n" +
            "1. Presione **[AÑADIR CLIENTE]**.\n" +
            "2. Complete el formulario (Nombre y Teléfono son campos obligatorios).\n" +
            "3. Al guardar, el sistema creará automáticamente su Cuenta Corriente.\n" +
            "[IMG:CAPTURA_FORMULARIO_CLIENTE.png]\n\n" +
            "**C. Edición y Mantenimiento:**\n" +
            "Seleccione un cliente de la lista y presione **[EDITAR]** para actualizar sus datos personales.\n" +
            "[IMG:CAPTURA_VENTANA_EDICION_CLIENTE.png]\n\n" +
            "**D. Acceso a Cuenta Corriente:**\n" +
            "Mediante el botón **[CUENTA]**, accederá de forma directa al historial de movimientos del cliente seleccionado.\n" +
            "[IMG:CAPTURA_HISTORIAL_CLIENTE.png]",
            ""
        ));

        // SECCIÓN 5: CUENTAS CORRIENTES (CORREGIDA: Se movió la imagen al texto intermedio)
        contenedorSecciones.getChildren().add(crearSeccion(
            "5. Gestión de Cuentas Corrientes",
            "Centraliza el control de deudas y saldos de los clientes de la vidriería.\n\n" +
            "[IMG:CAPTURA_CUENTAS_GENERAL.png]\n\n" +
            "**A. Listado General:**\n" +
            "Muestra a todos los clientes y su saldo total (positivo o negativo).\n" +
            "[IMG:CAPTURA_LISTADO_GENERAL.png]\n\n" +
            "**B. Cuentas Pendientes (Morosos):**\n" +
            "Filtra automáticamente la lista para mostrar únicamente a los clientes que presentan deudas activas.\n" +
            "[IMG:CAPTURA_LISTADO_MOROSOS.png]\n\n" +
            "**C. Historial Detallado:**\n" +
            "Al presionar **[VER HISTORIAL]**, se visualiza la ficha técnica con el detalle cronológico de todos los presupuestos y pagos del cliente.\n\n" +
            "[IMG:CAPTURA_FICHA_TECNICA_CUENTA.png]",
            ""
        ));

        // SECCIÓN 6: GESTIÓN DE PRESUPUESTOS (CORREGIDA: Se movió la imagen final al texto de arriba)
        contenedorSecciones.getChildren().add(crearSeccion(
            "6. Gestión de Presupuestos",
            "Módulo destinado a la creación y seguimiento de presupuestos por trabajos de carpintería.\n\n" +
            "[IMG:CAPTURA_PRESUPUESTO_MODULO.png]\n\n" +
            "**A. Creación de Nuevo Presupuesto:**\n" +
            "Ingrese el cliente, el monto total y el detalle del trabajo. Puede usar el botón **[+]** para dar de alta un cliente nuevo rápidamente.\n" +
            "[IMG:CAPTURA_NUEVO_PRESUPUESTO.png]\n\n" +
            "**B. Control y Seguimiento:**\n" +
            "Acceda a **[VER TODOS]** para consultar el historial. Puede filtrar por nombre de cliente o por estado del presupuesto.\n" +
            "[IMG:CAPTURA_HISTORIAL_PRESUPUESTOS.png]\n\n" +
            "**C. Edición:**\n" +
            "Podrá modificar presupuestos siempre y cuando no tengan pagos registrados vinculados.\n" +
            "[IMG:CAPTURA_EDICION_PRESUPUESTO.png]\n\n" +
            "**D. Detalles y Comprobantes:**\n" +
            "Desde el botón **[DETALLE]**, podrá ver la información técnica y descargar el **PDF** de los recibos asociados.\n\n" +
            "[IMG:CAPTURA_VISOR_PDF.png]",
            ""
        ));

        // SECCIÓN 7: PAGOS Y COMPROBANTES
        contenedorSecciones.getChildren().add(crearSeccion(
            "7. Módulo de Pagos y Cobranzas",
            "Este módulo gestiona los ingresos de dinero y la emisión de comprobantes.\n\n" +
            "[IMG:CAPTURA_MODULO_PAGOS.png]\n\n" +
            "**A. Registro de Cobranza:**\n" +
            "1. Presione **[REGISTRAR PAGO]**.\n" +
            "2. Seleccione el Cliente y luego el presupuesto que desea abonar.\n" +
            "3. En caso de pagos en **Dólares (USD)**, el sistema permite ingresar la cotización para el cálculo automático en pesos.\n" +
            "[IMG:CAPTURA_FORMULARIO_PAGO.png]\n\n" +
            "**B. Generación de Recibos:**\n" +
            "Al confirmar el pago, el sistema ofrecerá generar el comprobante en formato **PDF** para su entrega al cliente.\n" +
            "[IMG:CAPTURA_ALERTA_PDF.png]\n\n" +
            "**C. Historial de Pagos:**\n" +
            "Consulte el listado global para ver todos los ingresos registrados y reimprimir recibos si es necesario mediante el botón **[VER PDF]**.\n" +
            "[IMG:CAPTURA_HISTORIAL_PAGOS_GLOBAL.png]",
            ""
        ));

        // SECCIÓN 8: ESTADÍSTICAS (CORREGIDA: Se unificó el parámetro de la imagen)
        contenedorSecciones.getChildren().add(crearSeccion(
            "8. Análisis y Estadísticas",
            "Herramienta de análisis para la toma de decisiones financieras.\n\n" +
            "**A. Análisis de Ingresos:**\n" +
            "Visualice el total acumulado histórico y el desglose de ingresos del mes actual para controlar el flujo de caja.\n" +
            "[IMG:CAPTURA_PANEL_ESTADISTICAS.png]\n\n" +
            "**B. Reporte de Morosidad:**\n" +
            "Genera una lista de clientes con deudas críticas (aquellas con más de 30 días de antigüedad).\n\n" +
            "[IMG:CAPTURA_REPORTE_MOROSIDAD.png]",
            ""
        ));

        // SECCIÓN 9: SEGURIDAD DEL PERFIL (CORREGIDA: Se pasó la última imagen al cuerpo del texto)
        contenedorSecciones.getChildren().add(crearSeccion(
            "9. Perfil de Usuario y Seguridad",
            "Ajustes de la cuenta de usuario activa:\n\n" +
            "• **Cambio de Credenciales**: Actualice su contraseña periódicamente.\n" +
            "• **Pregunta de Seguridad**: Configure o edite su pregunta y respuesta secreta para asegurar la recuperación de su cuenta.\n\n" +
            "[IMG:CAPTURA_CONFIGURACION_PERFIL.png]\n\n" +
            "[IMG:CAPTURA_EDITAR_PREGUNTA_SEGURIDAD.png]",
            ""
        ));

        // SECCIÓN 10: CIERRE DE SESIÓN (CORREGIDA: Se unificó la imagen de pie de página)
        contenedorSecciones.getChildren().add(crearSeccion(
            "10. Salida Segura",
            "Para finalizar su jornada de trabajo, utilice siempre el botón **[CERRAR SESIÓN]**.\n\n" +
            "Esta acción destruye la sesión actual y protege los datos de Aluglass ante accesos no autorizados, regresando al usuario a la pantalla de Login.",
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
                // La primera parte SIEMPRE es texto (lo que está antes del primer [IMG:)
                if (!parte.trim().isEmpty()) {
                    seccion.getChildren().add(crearFlowTexto(parte));
                }
            } 
            else 
            {
                // Las partes siguientes contienen "NombreImagen.png] Texto..."
                if (parte.contains("]")) 
                {
                    String[] subPartes = parte.split("\\]", 2);
                    
                    // A. Añadir la imagen que causó el split
                    String nombreImg = subPartes[0].trim();
                    seccion.getChildren().add(configurarImagen(nombreImg));
                    
                    // B. Añadir el texto que sigue a esa imagen (si existe)
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

        // Dividimos el texto buscando los delimitadores **
        String[] partes = contenido.split("(?<=\\*\\*)|(?=\\*\\*)");
        
        boolean esNegrita = false;

        for (String parte : partes) 
        {
            if (parte.equals("**")) 
            {
                // Cambia el estado y salta la impresión de los asteriscos
                esNegrita = !esNegrita;
                continue; 
            }

            Text textoNodo = new Text(parte);
            
            if (esNegrita) 
            {
                // Mantiene tus 16px pero añade el grosor de negrita
                textoNodo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            } 
            else 
            {
                // Mantiene tus 16px en estado normal
                textoNodo.setStyle("-fx-font-size: 16px; -fx-font-weight: normal;");
            }
            
            flow.getChildren().add(textoNodo);
        }

        return flow;
    }

    private Node configurarImagen(String nombre) 
    {
        // Usamos un HBox como envoltorio para poder centrar la "caja" de la imagen
        HBox centrador = new HBox();
        centrador.setAlignment(Pos.CENTER);
        centrador.setFillHeight(false);

        VBox caja = new VBox();
        caja.setAlignment(Pos.CENTER);
        caja.setPadding(new Insets(10));
        caja.setStyle("-fx-border-color: #EEE9E9; -fx-background-color: #ffff; -fx-border-radius: 5;");
        
        // No permitimos que la caja gris crezca más que la imagen que contiene
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

            // LÓGICA DE TAMAÑO:
            double anchoOriginal = img.getWidth();
            double limitePantalla = 550; // Ajusta este valor si tu pantalla es pequeña

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
            System.out.println("Error: " + nombre);
            return new VBox(); 
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