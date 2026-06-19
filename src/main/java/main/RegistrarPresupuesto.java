package main;

import base.BaseCliente;
import base.BasePresupuesto;
import clases.Cliente;
import clases.Presupuesto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilidades.Validadores;

public class RegistrarPresupuesto extends VBox 
{
    private Label lblMensaje;
    private ComboBox<Cliente> comboClientes;
    private BorderPane rootPrincipal;
    private Stage ventanaActual;
    private Cliente clientePreseleccionado; 
    private Presupuesto presupuestoCreado; // Para capturar el objeto recién guardado
    
    public Presupuesto getPresupuestoCreado() 
    {
        return presupuestoCreado;
    }

    // Constructor para Menú
    public RegistrarPresupuesto(BorderPane rootPrincipal) 
    {
        this.rootPrincipal = rootPrincipal;
        this.ventanaActual = null;
        this.clientePreseleccionado = null;
        inicializarVista();
    }

    // Constructor para Ventana Emergente Genérica
    public RegistrarPresupuesto(Stage ventanaActual) 
    {
        this.rootPrincipal = null;
        this.ventanaActual = ventanaActual;
        this.clientePreseleccionado = null;
        inicializarVista();
    }
    
    // Constructor para abrir desde Pagos con un cliente ya elegido
    public RegistrarPresupuesto(Stage ventanaActual, Cliente clienteExistente)
    {
        this.rootPrincipal = null;
        this.ventanaActual = ventanaActual;
        this.clientePreseleccionado = clienteExistente; 
        inicializarVista();
    }

    private void inicializarVista() 
    {
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(20);
        this.setPadding(new Insets(30));

        Label lblTitulo = new Label("AÑADIR NUEVO PRESUPUESTO");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        lblMensaje = new Label("");
        lblMensaje.setPadding(new Insets(10, 0, 0, 0));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_LEFT);

        String estiloLabel = "-fx-font-size: 16px; -fx-font-weight: bold;";
        String estiloInputGrande = "-fx-font-size: 14px; -fx-padding: 5;";

        Label lblC = new Label("Cliente:");
        lblC.setStyle(estiloLabel);
        
        comboClientes = new ComboBox<>();
        comboClientes.setPromptText("Seleccione un cliente...");
        comboClientes.setPrefWidth(300);
        comboClientes.setStyle(estiloInputGrande);
        actualizarComboClientes();

        // Lógica de Preselección
        if (clientePreseleccionado != null)
        {
            for (Cliente c : comboClientes.getItems()) 
            {
                if (c.getIdCliente() == clientePreseleccionado.getIdCliente()) 
                {
                    comboClientes.setValue(c);
                    comboClientes.setDisable(true); // Bloqueamos para que sea para ese cliente sí o sí
                    break;
                }
            }
        }

        Button btnNuevoCliente = new Button("+");
        btnNuevoCliente.setStyle("-fx-font-weight: bold; -fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-size: 16px;");
        btnNuevoCliente.setCursor(javafx.scene.Cursor.HAND);
        btnNuevoCliente.setOnAction(e -> abrirVentanaNuevoCliente());

        // Si ya hay cliente, ocultamos el botón "+" para no confundir
        if (clientePreseleccionado != null) btnNuevoCliente.setVisible(false);

        HBox hbCliente = new HBox(10, comboClientes, btnNuevoCliente);
        hbCliente.setAlignment(Pos.CENTER_LEFT);

        Label lblM = new Label("Monto Total:");
        lblM.setStyle(estiloLabel);
        TextField txtTotal = new TextField();
        txtTotal.setPromptText("0.00");
        txtTotal.setPrefWidth(350);
        txtTotal.setStyle(estiloInputGrande);

        Label lblD = new Label("Descripción:");
        lblD.setStyle(estiloLabel);
        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Detalle del trabajo...");
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setWrapText(true);
        txtDescripcion.setPrefWidth(350);
        txtDescripcion.setStyle(estiloInputGrande);

        grid.add(lblC, 0, 0);
        grid.add(hbCliente, 1, 0);
        grid.add(lblM, 0, 1);
        grid.add(txtTotal, 1, 1);
        grid.add(lblD, 0, 2);
        grid.add(txtDescripcion, 1, 2);

        Button btnGuardar = new Button("GUARDAR PRESUPUESTO");
        btnGuardar.getStyleClass().add("boton-editar-tabla");
        btnGuardar.setPadding(new Insets(12, 25, 12, 25));
        btnGuardar.setCursor(javafx.scene.Cursor.HAND);

        btnGuardar.setOnAction(e -> 
        {
            Cliente cliente = comboClientes.getValue();
            String totalTexto = txtTotal.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            if (cliente == null || Validadores.estaVacio(totalTexto) || Validadores.estaVacio(descripcion)) 
            {
                mostrarMensaje("❌ Complete todos los campos.", true);
                return;
            }

            BasePresupuesto base = new BasePresupuesto();
            base.registrarPresupuesto(cliente.getIdCliente(), Double.parseDouble(totalTexto), descripcion);
            
            this.presupuestoCreado = new Presupuesto(
                0, 
                cliente.getIdCliente(), 
                java.time.LocalDate.now().toString(),
                descripcion, 
                Double.parseDouble(totalTexto), 
                Double.parseDouble(totalTexto), // El saldo inicial es el total
                "Pendiente"
            );
            
            if (ventanaActual != null) 
            {
                ventanaActual.close(); 
            } 
            else 
            {
                rootPrincipal.setCenter(new ListadoPresupuestos(rootPrincipal)); 
            }
        });

        this.getChildren().addAll(lblTitulo, new Separator(), grid, btnGuardar, lblMensaje);
    }

    private void actualizarComboClientes() 
    {
        comboClientes.getItems().clear();
        comboClientes.getItems().addAll(new BaseCliente().listarClientesActivos());
    }

    private void abrirVentanaNuevoCliente() 
    {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Aluglass - Nuevo Cliente");
        AgregarCliente layout = new AgregarCliente(stage);
        Scene scene = new Scene(layout);
        try { scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm()); } catch (Exception e) {}
        stage.setScene(scene);
        stage.showAndWait();
        actualizarComboClientes();
    }

    private void mostrarMensaje(String texto, boolean esError) 
    {
        lblMensaje.setText(texto);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #d32f2f; -fx-font-weight: bold;" : "-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
    }
}