package main;

import base.BaseCuentaProveedor;
import clases.CuentaProveedor;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ListadoCuentasProveedores extends VBox 
{

    public ListadoCuentasProveedores(BorderPane root) 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(15);
        setPadding(new Insets(20));

        Label titulo = new Label("CUENTAS CORRIENTES DE PROVEEDORES");
        titulo.getStyleClass().add("titulo-pantalla");

        TableView<CuentaProveedor> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Columnas
        TableColumn<CuentaProveedor, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        id.setMaxWidth(60);

        TableColumn<CuentaProveedor, String> proveedor = new TableColumn<>("Proveedor");
        proveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));

        TableColumn<CuentaProveedor, Double> saldo = new TableColumn<>("Saldo adeudado");
        saldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        saldo.setCellFactory(c -> new TableCell<>() 
        {
            @Override
            protected void updateItem(Double v, boolean empty) 
            {
                super.updateItem(v, empty);

                if (empty || v == null) 
                {
                    setText(null);
                    setStyle("");
                }
                else 
                {
                    setText(NumberFormat.getCurrencyInstance(new Locale("es", "AR")).format(v));
                    setStyle(v > 0 ? "-fx-text-fill:#d32f2f;-fx-font-weight:bold;" 
                                   : "-fx-text-fill:#388e3c;-fx-font-weight:bold;");
                }
            }
        });

        TableColumn<CuentaProveedor, String> venc = new TableColumn<>("Próximo vencimiento");
        venc.setCellValueFactory(new PropertyValueFactory<>("proximoVencimiento"));

        TableColumn<CuentaProveedor, Void> acciones = new TableColumn<>("Acciones");
        acciones.setCellFactory(c -> new TableCell<>() 
        {
            private final Button ver = new Button("Ver cuenta");

            {
                ver.getStyleClass().add("boton-editar-tabla");
                ver.setOnAction(e -> 
                {
                    CuentaProveedor cuenta = getTableView().getItems().get(getIndex());
                    root.setCenter(new DetalleCuentaProveedor(root, cuenta));
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) 
            {
                super.updateItem(v, empty);
                setGraphic(empty ? null : ver);
            }
        });

        // Configuración e inserción en la tabla
        tabla.getColumns().addAll(id, proveedor, saldo, venc, acciones);
        tabla.setItems(FXCollections.observableArrayList(new BaseCuentaProveedor().listarCuentas()));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        getChildren().addAll(titulo, new Separator(), tabla);
    }
}