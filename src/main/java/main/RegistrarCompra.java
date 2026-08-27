package main;

import base.BaseCompra;
import base.BaseProveedor;
import clases.Compra;
import clases.Proveedor;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utilidades.Validadores;

public class RegistrarCompra extends VBox
{

    private Label lblMensaje;
    private Stage ventanaActual;
    private Proveedor proveedorPreseleccionado;
    private Compra compraCreada;

    public RegistrarCompra(BorderPane rootPrincipal)
    {
        ventanaActual = null;
        inicializarVista(rootPrincipal);
    }

    public RegistrarCompra(Stage ventanaActual, Proveedor proveedorPreseleccionado) 
    {
        this.ventanaActual = ventanaActual;
        this.proveedorPreseleccionado = proveedorPreseleccionado;
        inicializarVista(null);
    }

    public Compra getCompraCreada() //Para el alta rapida de la compra
    { 
        return compraCreada;
    }

    private void inicializarVista(BorderPane rootPrincipal) 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(20);
        setPadding(new Insets(30));

        Label titulo = new Label("REGISTRAR COMPRA");
        titulo.getStyleClass().add("titulo-pantalla");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_LEFT);

        ComboBox<Proveedor> comboProveedores = new ComboBox<>();
        comboProveedores.getItems().addAll(new BaseProveedor().listarProveedoresActivos());
        comboProveedores.setPromptText("Seleccione un proveedor...");
        comboProveedores.setPrefWidth(350);
        comboProveedores.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
        
        if (proveedorPreseleccionado != null) 
        {
            comboProveedores.setValue(proveedorPreseleccionado);
            comboProveedores.setDisable(true);
        }
        Button nuevoProveedor = new Button("+");
        nuevoProveedor.setStyle("-fx-font-weight:bold;-fx-background-color:#2e7d32;-fx-text-fill:white;-fx-font-size:16px;");
        nuevoProveedor.setOnAction(e -> abrirProveedor(comboProveedores));
        if (proveedorPreseleccionado != null) nuevoProveedor.setVisible(false);
        HBox filaProveedor = new HBox(10, comboProveedores, nuevoProveedor);
        filaProveedor.setAlignment(Pos.CENTER_LEFT);

        TextField txtFactura = crearCampo("Opcional");
        DatePicker fechaEmision = new DatePicker(LocalDate.now());
       
        fechaEmision.setPrefWidth(200);
        fechaEmision.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
        
        DatePicker fechaVencimiento = new DatePicker();
        fechaVencimiento.setPromptText("Opcional");
        fechaVencimiento.setPrefWidth(200);
        fechaVencimiento.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
       
        TextField txtImporte = crearCampo("0.00");
        
        TextArea txtDetalle = new TextArea();
        txtDetalle.setPromptText("Describa los ítems de la compra...");
        txtDetalle.setPrefRowCount(4);
        txtDetalle.setWrapText(true);
        txtDetalle.setPrefWidth(350);
        txtDetalle.getStyleClass().add("input-login");

        grid.add(etiqueta("Proveedor:"), 0, 0); grid.add(filaProveedor, 1, 0);
        grid.add(etiqueta("N° de factura:"), 0, 1); grid.add(txtFactura, 1, 1);
        grid.add(etiqueta("Fecha de emisión:"), 0, 2); grid.add(fechaEmision, 1, 2);
        grid.add(etiqueta("Fecha de vencimiento:"), 0, 3); grid.add(fechaVencimiento, 1, 3);
        grid.add(etiqueta("Importe:"), 0, 4); grid.add(txtImporte, 1, 4);
        grid.add(etiqueta("Detalle:"), 0, 5); grid.add(txtDetalle, 1, 5);

        Button guardar = new Button("GUARDAR COMPRA");
        guardar.getStyleClass().add("boton-editar-tabla");
        guardar.setPrefHeight(40);
        guardar.setCursor(javafx.scene.Cursor.HAND);
        guardar.setOnAction(e -> guardarCompra(rootPrincipal, comboProveedores, txtFactura,
                fechaEmision, fechaVencimiento, txtImporte, txtDetalle));

        lblMensaje = new Label();
        getChildren().addAll(titulo, new Separator(), grid, guardar, lblMensaje);
    }

    private void guardarCompra(BorderPane rootPrincipal, ComboBox<Proveedor> comboProveedores,
            TextField txtFactura, DatePicker fechaEmision, DatePicker fechaVencimiento,
            TextField txtImporte, TextArea txtDetalle)
    {
        Proveedor proveedor = comboProveedores.getValue();
        String importeTexto = txtImporte.getText().trim();
        String detalle = txtDetalle.getText().trim();

        boolean importeValido = Validadores.esNumero(importeTexto)
                && Validadores.esMayorACero(Double.parseDouble(importeTexto));
        boolean datosValidos = proveedor != null && fechaEmision.getValue() != null
                && !Validadores.estaVacio(detalle) && importeValido;
        boolean vencimientoValido = fechaEmision.getValue() == null || fechaVencimiento.getValue() == null
                || !fechaVencimiento.getValue().isBefore(fechaEmision.getValue());

        if (!datosValidos)
        {
            mostrarMensaje("❌ Complete proveedor, fecha, importe válido y detalle.", true);
        }
        else if (!vencimientoValido)
        {
            mostrarMensaje("❌ El vencimiento no puede ser anterior a la emisión.", true);
        }
        else
        {
            Compra compra = new Compra(0, proveedor.getIdProveedor(), txtFactura.getText().trim(),
                    fechaEmision.getValue().toString(), detalle, Double.parseDouble(importeTexto),
                    fechaVencimiento.getValue() == null ? null : fechaVencimiento.getValue().toString(),
                    "Pendiente");

            if (new BaseCompra().registrarCompra(compra))
            {
                compraCreada = compra;
                mostrarMensaje("✅ Compra registrada correctamente.", false);
                if (ventanaActual != null) ventanaActual.close();
                else {
                    txtFactura.clear(); fechaEmision.setValue(LocalDate.now()); fechaVencimiento.setValue(null);
                    txtImporte.clear(); txtDetalle.clear();
                }
            }
            else
            {
                mostrarMensaje("❌ No se pudo registrar la compra.", true);
            }
        }
    }

    private TextField crearCampo(String ayuda) 
    {
        TextField campo = new TextField();
        campo.setPromptText(ayuda);
        campo.setPrefWidth(350);
        campo.getStyleClass().add("input-login");
        return campo;
    }

    private Label etiqueta(String texto) 
    {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("label-formulario");
        return etiqueta;
    }

    private void mostrarMensaje(String mensaje, boolean esError)
    {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError
                ? "-fx-text-fill: #d32f2f; -fx-font-weight: bold;"
                : "-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
    }

    private void abrirProveedor(ComboBox<Proveedor> comboProveedores) 
    {
        Stage stage = new Stage();
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        AgregarProveedor vista = new AgregarProveedor(stage);
        javafx.scene.Scene escena = new javafx.scene.Scene(vista);
        
        try 
        { 
            escena.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm()); 
        } 
        catch (Exception e) {}
        
        stage.setScene(escena);
        stage.showAndWait();
        
        comboProveedores.getItems().setAll(new BaseProveedor().listarProveedoresActivos());
        
        if (vista.getProveedorCreado() != null) comboProveedores.setValue(vista.getProveedorCreado());
    }
}
