package main;

import base.BaseCliente;
import clases.Cliente;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import utilidades.Validadores;

public class ModificarCliente extends VBox 
{
    public ModificarCliente(BorderPane rootPrincipal, Cliente cliente) 
    {
        // Estilos CSS
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(20);

        // TÍTULO
        Label lblTitulo = new Label("MODIFICAR DATOS DEL CLIENTE");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        // FORMULARIO
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_LEFT);

        TextField txtNombre = new TextField(cliente.getNombre());
        txtNombre.getStyleClass().add("input-login");
        txtNombre.setPrefWidth(350);

        TextField txtTelefono = new TextField(cliente.getTelefono());
        txtTelefono.getStyleClass().add("input-login");
        txtTelefono.setPromptText("Solo números");

        Label lblEstado = new Label("Estado:");
        lblEstado.getStyleClass().add("label-formulario");

        ComboBox<String> comboEstado = new ComboBox<>();
        comboEstado.getItems().addAll("Activo", "Inactivo");
        comboEstado.getStyleClass().add("input-login");
        comboEstado.setPrefWidth(200);
        
        // Seteamos el valor que ya trae el cliente de la base
        comboEstado.setValue(cliente.getEstado()); 

        Label lblNombre = new Label("Nombre:");
        lblNombre.getStyleClass().add("label-formulario");

        Label lblTelefono = new Label("Teléfono:");
        lblTelefono.getStyleClass().add("label-formulario");

        // Agregamos al Grid
        grid.add(lblNombre, 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(lblTelefono, 0, 1);
        grid.add(txtTelefono, 1, 1);
        grid.add(lblEstado, 0, 2);   // Fila 2, Columna 0
        grid.add(comboEstado, 1, 2); // Fila 2, Columna 1

        // BOTONES
        Button btnGuardar = new Button("GUARDAR CAMBIOS");
        btnGuardar.getStyleClass().add("boton-editar-tabla");
        
        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.getStyleClass().add("boton-editar-tabla");

        // Acción Guardar
        btnGuardar.setOnAction(e -> 
        {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String estado = comboEstado.getValue(); // Obtenemos el nuevo estado

            if (Validadores.estaVacio(nombre)) 
            {
                new Alert(Alert.AlertType.WARNING, "El nombre es obligatorio").showAndWait();
                return;
            }

            // Actualizamos el objeto cliente
            cliente.setNombre(nombre);
            cliente.setTelefono(telefono);
            cliente.setEstado(estado); 

            BaseCliente base = new BaseCliente();
            if (base.modificarCliente(cliente)) 
            {
                new Alert(Alert.AlertType.INFORMATION, "Cliente actualizado con éxito").showAndWait();
                rootPrincipal.setCenter(new ListadoClientes(rootPrincipal));
            } 
            else 
            {
                new Alert(Alert.AlertType.ERROR, "Error al actualizar en la base de datos").showAndWait();
            }
        });

        // Acción Cancelar
        btnCancelar.setOnAction(e -> rootPrincipal.setCenter(new ListadoClientes(rootPrincipal)));

        // Estructura de botones abajo del formulario
        VBox cajaBotones = new VBox(10, btnGuardar, btnCancelar);
        
        this.getChildren().addAll(lblTitulo, new Separator(), grid, cajaBotones);
    }
}