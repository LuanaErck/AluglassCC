package main;

import base.BaseProveedor;
import clases.Proveedor;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ModificarProveedor 
{

    private final Proveedor proveedor;
    private final Runnable onExito;

    public ModificarProveedor(Proveedor proveedor, Runnable onExito) 
    {
        this.proveedor = proveedor;
        this.onExito = onExito;
    }

    public void mostrar()
    {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar Proveedor");
        dialog.setHeaderText("Modificar datos de: " + proveedor.getNombre());

        ButtonType btnGuardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardarType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField txtNombre = new TextField(proveedor.getNombre());
        TextField txtTelefono = new TextField(proveedor.getTelefono() != null ? proveedor.getTelefono() : "");
        TextField txtCuit = new TextField(proveedor.getCuit() != null ? proveedor.getCuit() : "");
        TextField txtCbu = new TextField(proveedor.getCbuAlias() != null ? proveedor.getCbuAlias() : "");
        ComboBox<String> comboEstado = new ComboBox<>(FXCollections.observableArrayList("Activo", "Inactivo"));
        comboEstado.setValue(proveedor.getEstado());

        txtNombre.getStyleClass().add("input-login");
        txtTelefono.getStyleClass().add("input-login");
        txtCuit.getStyleClass().add("input-login");
        txtCbu.getStyleClass().add("input-login");

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Teléfono:"), 0, 1);
        grid.add(txtTelefono, 1, 1);
        grid.add(new Label("CUIT:"), 0, 2);
        grid.add(txtCuit, 1, 2);
        grid.add(new Label("CBU / Alias:"), 0, 3);
        grid.add(txtCbu, 1, 3);
        grid.add(new Label("Estado:"), 0, 4);
        grid.add(comboEstado, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Button btnGuardar = (Button) dialog.getDialogPane().lookupButton(btnGuardarType);
        btnGuardar.addEventFilter(javafx.event.ActionEvent.ACTION, event -> 
        {
            event.consume();

            String nuevoNombre = txtNombre.getText().trim();
            String nuevoCuit = txtCuit.getText().trim();
            boolean datosValidos = true;

            BaseProveedor dao = new BaseProveedor();

            if (nuevoNombre.isEmpty()) 
            {
                mostrarAlerta("El nombre del proveedor es obligatorio.");
                datosValidos = false;
            } 
            else if (!nuevoCuit.isEmpty() && !nuevoCuit.equals(proveedor.getCuit()) && dao.existeCuit(nuevoCuit))
            {
                mostrarAlerta("El CUIT ingresado ya pertenece a otro proveedor.");
                datosValidos = false;
            }

            if (datosValidos) 
            {
                proveedor.setNombre(nuevoNombre);
                proveedor.setTelefono(txtTelefono.getText().trim());
                proveedor.setCuit(nuevoCuit);
                proveedor.setCbuAlias(txtCbu.getText().trim());
                proveedor.setEstado(comboEstado.getValue());

                if (dao.actualizarProveedor(proveedor)) 
                {
                    if (onExito != null) 
                    {
                        onExito.run();
                    }
                    dialog.close();
                } 
                else 
                {
                    mostrarAlerta("No se pudieron guardar los cambios en la base de datos.");
                }
            }
        });

        dialog.showAndWait();
    }

    private void mostrarAlerta(String contenido) 
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
