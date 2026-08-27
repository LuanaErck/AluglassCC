package main;

import base.BaseProveedor;
import clases.Proveedor;
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

public class AgregarProveedor extends VBox 
{

    private Proveedor proveedorCreado;
    private Stage ventanaActual;

    public AgregarProveedor(BorderPane rootPrincipal) 
    {
        ventanaActual = null;
        inicializarVista();
    }

    public AgregarProveedor(Stage ventanaActual)
    { 
        this.ventanaActual = ventanaActual; 
        inicializarVista(); 
    }
    
    public Proveedor getProveedorCreado() 
    { 
        return proveedorCreado;
    }

    private void inicializarVista() 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(20);
        setPadding(new Insets(30));

        Label lblTitulo = new Label("NUEVO PROVEEDOR");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_LEFT);

        TextField txtNombre = crearCampo("Nombre o razón social");
        TextField txtTelefono = crearCampo("Solo números");
        TextField txtCuit = crearCampo("Ej: 20123456789 (sin guiones)");
        TextField txtCbuAlias = crearCampo("CBU o alias (opcional)");

        grid.add(crearEtiqueta("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);

        grid.add(crearEtiqueta("Teléfono:"), 0, 1);
        grid.add(txtTelefono, 1, 1);

        grid.add(crearEtiqueta("CUIT:"), 0, 2);
        grid.add(txtCuit, 1, 2);

        grid.add(crearEtiqueta("CBU / Alias:"), 0, 3);
        grid.add(txtCbuAlias, 1, 3);

        Button btnGuardar = new Button("GUARDAR PROVEEDOR");
        btnGuardar.getStyleClass().add("boton-editar-tabla");
        btnGuardar.setPrefHeight(40);
        btnGuardar.setCursor(javafx.scene.Cursor.HAND);

        Label lblMensaje = new Label();
        btnGuardar.setOnAction(e -> guardar(txtNombre, txtTelefono, txtCuit, txtCbuAlias, lblMensaje));

        getChildren().addAll(lblTitulo, new Separator(), grid, btnGuardar, lblMensaje);
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

    private void guardar(TextField txtNombre, TextField txtTelefono, TextField txtCuit,
                         TextField txtCbuAlias, Label lblMensaje)
    {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String cuit = txtCuit.getText().trim();
        String cbuAlias = txtCbuAlias.getText().trim();
        BaseProveedor base = new BaseProveedor();

        String error = validar(nombre, telefono, cuit, base);
        if (error != null) 
        {
            mostrarError(lblMensaje, error);
        } 
        else 
        {
            // Si el CUIT viene vacío, asignamos null para evitar violaciones de la restricción UNIQUE en SQLite
            String cuitFinal = cuit.isEmpty() ? null : cuit;

            Proveedor proveedor = new Proveedor(0, nombre, telefono, "Activo", cuitFinal, cbuAlias);
            if (base.agregarProveedor(proveedor)) 
            {
                proveedorCreado = proveedor;
                lblMensaje.setText("✅ Proveedor agregado correctamente");
                lblMensaje.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                if (ventanaActual != null) 
                {
                    ventanaActual.close();
                } 
                else
                { 
                    txtNombre.clear(); 
                    txtTelefono.clear(); 
                    txtCuit.clear(); 
                    txtCbuAlias.clear(); 
                }
            } 
            else 
            {
                mostrarError(lblMensaje, "❌ Error al guardar el proveedor en la base de datos");
            }
        }
    }

    private String validar(String nombre, String telefono, String cuit, BaseProveedor base) 
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
                return "⚠️ Ya existe un proveedor registrado con ese CUIT";
            }
        }
        return null;
    }

    private void mostrarError(Label etiqueta, String mensaje) {
        etiqueta.setText(mensaje);
        etiqueta.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
    }
}