package main;

import clases.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MenuPrincipal 
{

    private Stage stage;
    private BorderPane root;
    private Usuario usuarioLogueado;

    public MenuPrincipal(Stage stage, Usuario u) 
    {
        this.stage = stage;
        this.root = new BorderPane();
        this.usuarioLogueado = u;
    }

    public void mostrar() 
    {
        // PANEL LATERAL
        VBox menuLateral = new VBox();
        menuLateral.setId("menu-lateral");
        menuLateral.setPrefWidth(280);
        menuLateral.setMinWidth(280);
        menuLateral.setStyle("-fx-background-color: #FFFFFF;");

        // 1. Logo
        VBox logoContainer = new VBox();
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setPadding(new Insets(30, 10, 20, 10));
        
        try 
        {
            ImageView logoMenu = new ImageView(new Image(getClass().getResourceAsStream("/iconos/logo.jpeg")));
            logoMenu.setFitWidth(160);
            logoMenu.setPreserveRatio(true);
            logoContainer.getChildren().add(logoMenu);
        } catch (Exception e) {}

        // Espaciador Superior
        Region spacerTop = new Region();
        VBox.setVgrow(spacerTop, Priority.ALWAYS);

        // 2. Acordeón
        Accordion accordion = new Accordion();
        configurarAcordeon(accordion);

        // Espaciador Inferior
        Region spacerBottom = new Region();
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);

        // 3. Botón Salir
        Button btnSalir = new Button("CERRAR SESIÓN");
        btnSalir.setId("boton-salir");
        btnSalir.setMaxWidth(Double.MAX_VALUE);
        btnSalir.setMinHeight(50);

        btnSalir.setOnAction(e -> 
        {
            // 1. Iniciar el respaldo en segundo plano antes de volver al Login
            new Thread(() -> 
            {
                // Reemplazá "E" por la letra que tenga asignada el pendrive en la PC
                boolean exito = utilidades.Respaldo.respaldarTodoEnPendrive("D");

                if (!exito)
                {
                    System.out.println("[Respaldo] No se pudo realizar el respaldo automático al cerrar sesión (Pendrive no detectado).");
                }
            }).start();

            // 2. Volver a la pantalla de Login
            try 
            {
                new Login().start(stage);
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
        });

        VBox bottomContainer = new VBox(btnSalir);
        bottomContainer.setPadding(new Insets(20));

        // Armado del VBox lateral
        menuLateral.getChildren().addAll(logoContainer, spacerTop, accordion, spacerBottom, bottomContainer);
        
        root.setLeft(menuLateral);

        // PANEL CENTRAL
        StackPane centro = new StackPane(new Label("Bienvenido a Aluglass"));
        centro.setStyle("-fx-background-color: #FFFFFF;");
        root.setCenter(centro);

        // ESCENA 
        Scene scene = new Scene(root, 1280, 720); 
        scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(true); 
        stage.setTitle("Aluglass CC - Menú Principal");
        stage.show();
    }

    private void configurarAcordeon(Accordion accordion) 
    {
        // ==========================================
        // 1) CLIENTES
        // ==========================================
        TitledPane subGestionClientes = crearSubCategoria("Gestión de clientes",
            crearSubBoton("Añadir cliente", e -> root.setCenter(new AgregarCliente(root))),
            crearSubBoton("Ver todos", e -> root.setCenter(new ListadoClientes(root)))
        );

        TitledPane subPresupuestos = crearSubCategoria("Presupuestos de ventas",
            crearSubBoton("Añadir presupuesto", e -> root.setCenter(new RegistrarPresupuesto(root))),
            crearSubBoton("Ver todos", e -> root.setCenter(new ListadoPresupuestos(root)))
        );

        TitledPane subCobros = crearSubCategoria("Cobros",
            crearSubBoton("Registrar cobro", e -> root.setCenter(new RegistrarPago(root))),
            crearSubBoton("Historial", e -> root.setCenter(new ListaPagos(root)))
        );

        TitledPane subCuentasCorrientes = crearSubCategoria("Cuentas corrientes",
            crearSubBoton("Listar cuentas", e -> root.setCenter(new ListadoCuentas(root))),
            crearSubBoton("Cuentas pendientes", e -> root.setCenter(new ListadoCuentasAtrasadas(root)))
        );

        TitledPane paneClientes = crearSeccion("CLIENTES", "/iconos/clientes.png",
            subGestionClientes, subPresupuestos, subCobros, subCuentasCorrientes
        );

        // ==========================================
        // 2) PROVEEDORES
        // ==========================================
        TitledPane subGestionProveedores = crearSubCategoria("Gestión de proveedores",
            crearSubBoton("Añadir proveedor", e -> root.setCenter(new AgregarProveedor(root))),
            crearSubBoton("Ver todos", e -> root.setCenter(new ListadoProveedores()))
        );

        TitledPane subCompras = crearSubCategoria("Compras",
            crearSubBoton("Registrar compra", e -> root.setCenter(new RegistrarCompra(root))),
            crearSubBoton("Historial de compras", e -> root.setCenter(new ListadoCompras(root)))
        );

        TitledPane subPagosProveedores = crearSubCategoria("Pagos",
            crearSubBoton("Registrar pago", e -> root.setCenter(new RegistrarPagoProveedor(root))),
            crearSubBoton("Historial de pagos", e -> root.setCenter(new ListaPagosProveedores()))
        );

        TitledPane subCuentasProveedores = crearSubCategoria("Cuentas corrientes",
            crearSubBoton("Deudas y vencimientos", e -> root.setCenter(new ListadoCuentasProveedores(root)))
        );

        TitledPane paneProveedores = crearSeccion("PROVEEDORES", "/iconos/proveedores.png",
            subGestionProveedores, subCompras, subPagosProveedores, subCuentasProveedores
        );

        // ==========================================
        // 3) ESTADÍSTICAS
        // ==========================================
        TitledPane paneEstadisticas = crearSeccion("ESTADÍSTICAS", "/iconos/reportes.png",
            crearSubBoton("Panel de control visual", e -> 
            {
                Reportes vr = new Reportes();
                root.setCenter(vr.getView());
            })
        );

        // ==========================================
        // 4) MI PERFIL
        // ==========================================
        TitledPane panePerfil = crearSeccion("MI PERFIL", "/iconos/perfil.png",
            crearSubBoton("Editar mis credenciales", e -> 
            {
                VentanaPerfil ventana = new VentanaPerfil(this.usuarioLogueado);
                root.setCenter(ventana.getView());
            })
        );

        // ==========================================
        // 5) MANUAL DE USUARIO
        // ==========================================
        TitledPane paneManual = crearSeccion("MANUAL DE USUARIO", "/iconos/manual.png",
            crearSubBoton("Ver manual de usuario", e -> 
            {
                Manual vr = new Manual();
                root.setCenter(vr.getView());
            }),
            crearSubBoton("Exportar manual", e -> exportarManual())
        );

        // ==========================================
        // CARGA AL ACORDEÓN
        // ==========================================
        accordion.getPanes().addAll(
            paneClientes,
            paneProveedores,
            paneEstadisticas,
            panePerfil,
            paneManual
        );
    }

    private TitledPane crearSeccion(String titulo, String iconPath, javafx.scene.Node... elementos) 
    {
        VBox contenido = new VBox(6);
        contenido.setPadding(new Insets(10, 0, 10, 15));
        contenido.setStyle("-fx-background-color: #FFFFFF;");
        contenido.getChildren().addAll(elementos);

        // Permite scroll si la lista de opciones de la sección es más larga que la pantalla
        ScrollPane scrollContent = new ScrollPane(contenido);
        scrollContent.setFitToWidth(true);
        scrollContent.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        TitledPane pane = new TitledPane(titulo, scrollContent);
        pane.getStyleClass().add("seccion-menu");

        try 
        {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            iv.setFitHeight(30); 
            iv.setFitWidth(30);
            iv.setPreserveRatio(true);
            pane.setGraphic(iv);
        } catch (Exception e) {}

        return pane;
    }
    
    // Método para crear los sub-desplegables (ej: "Presupuestos de ventas")
    private TitledPane crearSubCategoria(String titulo, Button... subBotones) 
    {
        VBox contenido = new VBox(2);
        contenido.setPadding(new Insets(6, 0, 6, 15)); // Sangría para los botones internos
        contenido.setStyle("-fx-background-color: transparent;");
        contenido.getChildren().addAll(subBotones);

        TitledPane pane = new TitledPane(titulo, contenido);
        pane.setExpanded(false); // Arrancan cerrados por defecto
        
        pane.getStyleClass().add("sub-categoria-pane");

        return pane;
    }

    private Label crearSubtitulo(String texto) 
    {
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555; -fx-font-size: 13px;");
        lbl.setPadding(new Insets(8, 0, 2, 5));
        return lbl;
    }

    private Button crearSubBoton(String texto, javafx.event.EventHandler<javafx.event.ActionEvent> evento) 
    {
        Button btn = new Button(texto);
        btn.getStyleClass().add("sub-boton-menu");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(5, 0, 5, 15)); // Un poquito más de sangría para destacar que depende del subtítulo
        btn.setOnAction(evento);
        return btn;
    }

    private void exportarManual() 
    {
        String ruta = utilidades.GeneradorManualPDF.generar();
        if (ruta != null)
        {
            Alert aviso = new Alert(Alert.AlertType.INFORMATION, "Manual exportado correctamente en: " + new java.io.File(ruta).getAbsolutePath());
            aviso.setHeaderText(null);
            aviso.showAndWait();
        } 
        else 
        {
            new Alert(Alert.AlertType.ERROR, "No se pudo exportar el manual.").showAndWait();
        }
    }
}
