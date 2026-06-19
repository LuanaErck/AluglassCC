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
    private Usuario usuarioLogueado; //Se debe tener esta variable global en la clase

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

        // Espaciador Superior (Empuja el menú al centro)
        Region spacerTop = new Region();
        VBox.setVgrow(spacerTop, Priority.ALWAYS);

        // 2. Acordeón
        Accordion accordion = new Accordion();
        configurarAcordeon(accordion);

        // Espaciador Inferior (Empuja el botón al fondo)
        Region spacerBottom = new Region();
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);

        // 3. Botón Salir
        Button btnSalir = new Button("CERRAR SESIÓN");
        btnSalir.setId("boton-salir");
        btnSalir.setMaxWidth(Double.MAX_VALUE);
        btnSalir.setMinHeight(50); // Altura fija para que no se aplaste
        btnSalir.setOnAction(e -> new Login().start(stage));

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
        // Usamos el tamaño máximo disponible para que no se esconda nada
        Scene scene = new Scene(root, 1280, 720); 
        scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

        stage.setScene(scene);
        
        // CORRECCIÓN DE TAMAÑO: Forzamos maximizado o pantalla completa
        stage.setMaximized(true); 
        
        stage.setTitle("Aluglass CC - Menú Principal");
        stage.show();
    }

    private void configurarAcordeon(Accordion accordion) 
    {
        
        // 1. CLIENTES
        TitledPane paneClientes = crearSeccion("CLIENTES", "/iconos/clientes.png",
            crearSubBoton("Añadir cliente", e -> root.setCenter(new AgregarCliente(root))),
            crearSubBoton("Ver todos", e -> root.setCenter(new ListadoClientes(root)))
        );

        // 2. CUENTAS CORRIENTES
        TitledPane paneCuentas = crearSeccion("CUENTAS", "/iconos/cuentas.png",
            crearSubBoton("Listar cuentas", e -> root.setCenter(new ListadoCuentas(root))),
            crearSubBoton("Cuentas pendientes", e -> root.setCenter(new ListadoCuentasAtrasadas(root)))
        );

        // 3. PAGOS
        TitledPane panePagos = crearSeccion("PAGOS", "/iconos/pagos.png",
            crearSubBoton("Registrar pago", e -> root.setCenter(new RegistrarPago(root))),
            crearSubBoton("Historial de pagos", e -> root.setCenter(new ListaPagos(root)))
        );

        // 4. PRESUPUESTOS
        TitledPane panePresupuestos = crearSeccion("PRESUPUESTOS", "/iconos/presupuestos.png",
            crearSubBoton("Añadir presupuesto", e -> root.setCenter(new RegistrarPresupuesto(root))),
            crearSubBoton("Ver todos", e -> root.setCenter(new ListadoPresupuestos(root)))
        );
        
        // 5. MI PERFIL
        TitledPane panePerfil = crearSeccion("MI PERFIL", "/iconos/perfil.png",
            crearSubBoton("Editar mi perfil", e -> 
            {
            // Pasamos el usuario logueado, NO el root
            VentanaPerfil ventana = new VentanaPerfil(this.usuarioLogueado);
            root.setCenter(ventana.getView());
            })
        );
        
        // 6. REPORTES
        TitledPane paneReportes = crearSeccion("ESTADÍSTICAS", "/iconos/reportes.png",
            crearSubBoton("Centro de Reportes", e -> 
            {
                Reportes vr = new Reportes();
                root.setCenter(vr.getView());
            })
        );
        
        //7. MANUAL DE USUAURIO
        TitledPane paneManual = crearSeccion("MANUAL DE USUARIO", "/iconos/manual.png",
            crearSubBoton("Ver Manual", e -> 
            {
                Manual vr = new Manual();
                root.setCenter(vr.getView());
            })
        );
      
        accordion.getPanes().addAll(paneClientes, paneCuentas, panePagos, panePresupuestos, paneReportes, panePerfil, paneManual);
    }

    private TitledPane crearSeccion(String titulo, String iconPath, Button... subBotones) 
    {
        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(10, 0, 10, 20));
        contenido.setStyle("-fx-background-color: #FFFFFF;");
        contenido.getChildren().addAll(subBotones);

        TitledPane pane = new TitledPane(titulo, contenido);
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

    private Button crearSubBoton(String texto, javafx.event.EventHandler<javafx.event.ActionEvent> evento) 
    {
        Button btn = new Button(texto);
        btn.getStyleClass().add("sub-boton-menu");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(evento);
        return btn;
    }
}