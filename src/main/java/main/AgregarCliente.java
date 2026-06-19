package main;

import base.BaseCliente;
import clases.Cliente;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utilidades.Validadores;

public class AgregarCliente extends VBox 
{
    private Cliente clienteCreado; 
    private Stage ventanaActual; // Variable para manejar el modo modal
    private BorderPane rootPrincipal; // Variable para manejar el modo pantalla completa

    public Cliente getClienteCreado() 
    {
        return clienteCreado;
    }

    // Constructor para Pantalla Completa (Menú lateral)
    public AgregarCliente(BorderPane rootPrincipal) 
    {
        this.rootPrincipal = rootPrincipal;
        this.ventanaActual = null;
        inicializarVista();
    }

    // Constructor para Ventana Emergente (Botón +)
    public AgregarCliente(Stage ventanaActual)
    {
        this.rootPrincipal = null;
        this.ventanaActual = ventanaActual;
        inicializarVista();
    }
    
    //Centraliza el diseño y la lógica de la pantalla
    private void inicializarVista() 
    {
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(20);
        this.setPadding(new Insets(30));

        Label lblTitulo = new Label("NUEVO CLIENTE");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_LEFT);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo");
        txtNombre.getStyleClass().add("input-login");
        txtNombre.setPrefWidth(350);

        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Solo números");
        txtTelefono.getStyleClass().add("input-login");

        Label lblNombre = new Label("Nombre:");
        lblNombre.getStyleClass().add("label-formulario");

        Label lblTelefono = new Label("Teléfono:");
        lblTelefono.getStyleClass().add("label-formulario");

        grid.add(lblNombre, 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(lblTelefono, 0, 1);
        grid.add(txtTelefono, 1, 1);

        Button btnGuardar = new Button("GUARDAR CLIENTE");
        btnGuardar.getStyleClass().add("boton-editar-tabla");
        btnGuardar.setPrefHeight(40);
        btnGuardar.setCursor(javafx.scene.Cursor.HAND);
        
        Label lblMensaje = new Label();

        btnGuardar.setOnAction(e -> 
        {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();

            if (Validadores.estaVacio(nombre)) 
            {
                lblMensaje.setText("⚠️ El nombre es obligatorio");
                lblMensaje.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                return;
            }

            if (!Validadores.esTexto(nombre)) 
            {
                lblMensaje.setText("⚠️ El nombre solo debe contener letras");
                lblMensaje.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                return;
            }

            if (!Validadores.estaVacio(telefono) && !Validadores.esEntero(telefono)) 
            {
                lblMensaje.setText("⚠️ Solo números en el teléfono");
                lblMensaje.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                return;
            }

            Cliente nuevo = new Cliente(0, nombre, telefono, "Activo");
            BaseCliente base = new BaseCliente();

            if (base.agregarCliente(nuevo)) 
            {
                this.clienteCreado = nuevo; 
                
                if (ventanaActual != null) 
                {
                    ventanaActual.close(); 
                } 
                else 
                {
                    lblMensaje.setText("✅ Cliente agregado correctamente");
                    lblMensaje.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    txtNombre.clear();
                    txtTelefono.clear();
                }
            } 
            else
            {
                lblMensaje.setText("❌ Error al guardar el cliente");
                lblMensaje.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            }
        });

        this.getChildren().addAll(lblTitulo, new Separator(), grid, btnGuardar, lblMensaje);
    }
}