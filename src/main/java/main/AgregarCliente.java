package main;

import base.BaseCliente;
import clases.Cliente;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
    
    // Centraliza el diseño y la lógica de la pantalla
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

        TextField txtNombre = crearCampo("Nombre completo");
        TextField txtTelefono = crearCampo("Solo números");
        TextField txtCuit = crearCampo("Ej: 20123456789 (Sin guiones)");

        grid.add(crearEtiqueta("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);

        grid.add(crearEtiqueta("Teléfono:"), 0, 1);
        grid.add(txtTelefono, 1, 1);

        grid.add(crearEtiqueta("CUIT:"), 0, 2);
        grid.add(txtCuit, 1, 2);

        Button btnGuardar = new Button("GUARDAR CLIENTE");
        btnGuardar.getStyleClass().add("boton-editar-tabla");
        btnGuardar.setPrefHeight(40);
        btnGuardar.setCursor(javafx.scene.Cursor.HAND);
        
        Label lblMensaje = new Label();
        btnGuardar.setOnAction(e -> guardar(txtNombre, txtTelefono, txtCuit, lblMensaje));

        this.getChildren().addAll(lblTitulo, new Separator(), grid, btnGuardar, lblMensaje);
    }

    private TextField crearCampo(String textoAyuda)
    {
        TextField campo = new TextField();
        campo.setPromptText(textoAyuda);
        campo.setPrefWidth(350);
        campo.getStyleClass().add("input-login");
        return campo;
    }

    private Label crearEtiqueta(String texto)
    {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("label-formulario");
        return etiqueta;
    }

    private void guardar(TextField txtNombre, TextField txtTelefono, TextField txtCuit, Label lblMensaje) 
    {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String cuit = txtCuit.getText().trim();
        BaseCliente base = new BaseCliente();

        String error = validar(nombre, telefono, cuit, base);
        if (error != null) 
        {
            mostrarError(lblMensaje, error);
        } 
        else 
        {
            // Conversión clave: Si el CUIT viene vacío, enviamos null para evitar colisiones UNIQUE en SQLite
            String cuitFinal = cuit.isEmpty() ? null : cuit;

            Cliente nuevo = new Cliente(0, nombre, telefono, "Activo", cuitFinal);

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
                    txtCuit.clear();
                }
            }
            else 
            {
                mostrarError(lblMensaje, "❌ Error al guardar el cliente en la base de datos");
            }
        }
    }

    private String validar(String nombre, String telefono, String cuit, BaseCliente base) 
    {
        if (Validadores.estaVacio(nombre)) 
        {
            return "⚠️ El nombre es obligatorio";
        }
        if (!Validadores.esTexto(nombre)) 
        {
            return "⚠️ El nombre solo debe contener letras";
        }
        if (!Validadores.estaVacio(telefono) && !Validadores.esEntero(telefono))
        {
            return "⚠️ Solo números en el teléfono";
        }
        if (!Validadores.estaVacio(cuit)) 
        {
            if (!Validadores.esEntero(cuit)) 
            {
                return "⚠️ El CUIT debe contener solo números";
            }
            if (base.existeCuit(cuit)) 
            {
                return "⚠️ Ya existe un cliente registrado con ese CUIT";
            }
        }
        return null;
    }

    private void mostrarError(Label etiqueta, String mensaje) 
    {
        etiqueta.setText(mensaje);
        etiqueta.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
    }
}