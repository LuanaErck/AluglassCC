package main;

import base.BaseCompra;
import base.BasePagoProveedor;
import clases.Compra;
import clases.CuentaProveedor;
import clases.PagoProveedor;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class DetalleCuentaProveedor extends VBox 
{
    private final BasePagoProveedor basePago = new BasePagoProveedor();

    public DetalleCuentaProveedor(BorderPane root, CuentaProveedor cuenta) 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(15);
        setPadding(new Insets(20));

        NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

        Label titulo = new Label("CUENTA CORRIENTE: " + cuenta.getNombreProveedor());
        titulo.getStyleClass().add("titulo-pantalla");

        Label info = new Label(
            "Saldo actual: " + moneda.format(cuenta.getSaldo()) + 
            "\nPróximo vencimiento: " + (cuenta.getProximoVencimiento() == null ? "Sin vencimiento" : cuenta.getProximoVencimiento())
        );
        info.setStyle("-fx-font-size:16px;");

        // Sección Compras
        Label comprasTitulo = new Label("Compras registradas");
        comprasTitulo.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        TableView<Compra> compras = new TableView<>();
        compras.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Compra, String> fc = new TableColumn<>("Fecha");
        fc.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<Compra, String> det = new TableColumn<>("Detalle");
        det.setCellValueFactory(new PropertyValueFactory<>("detalle"));

        TableColumn<Compra, Double> ic = new TableColumn<>("Importe");
        ic.setCellValueFactory(new PropertyValueFactory<>("importe"));

        compras.getColumns().addAll(fc, det, ic);
        compras.setItems(FXCollections.observableArrayList(new BaseCompra().listarComprasPorProveedor(cuenta.getIdProveedor())));
        compras.setPrefHeight(180);

        // Sección Pagos
        Label pagosTitulo = new Label("Pagos realizados");
        pagosTitulo.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");

        TableView<PagoProveedor> pagos = new TableView<>();
        pagos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PagoProveedor, String> fp = new TableColumn<>("Fecha");
        fp.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));

        TableColumn<PagoProveedor, Double> ip = new TableColumn<>("Importe");
        ip.setCellValueFactory(new PropertyValueFactory<>("importe"));

        TableColumn<PagoProveedor, String> forma = new TableColumn<>("Forma");
        forma.setCellValueFactory(new PropertyValueFactory<>("formaPago"));

        TableColumn<PagoProveedor, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Columna de Acciones: Anular Pago
        TableColumn<PagoProveedor, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>()
        {
            private final Button btnAnular = new Button("Anular Pago");

            {
                btnAnular.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                btnAnular.setOnAction(e ->
                {
                    PagoProveedor pago = getTableView().getItems().get(getIndex());
                    confirmarYAnular(pago, root, cuenta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) 
            {
                super.updateItem(item, empty);
                if (empty)
                {
                    setGraphic(null);
                }
                else 
                {
                    PagoProveedor pago = getTableView().getItems().get(getIndex());
                    if ("Anulado".equalsIgnoreCase(pago.getEstado())) 
                    {
                        btnAnular.setDisable(true);
                        btnAnular.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white;");
                    } 
                    else
                    {
                        btnAnular.setDisable(false);
                        btnAnular.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                    }
                    setGraphic(btnAnular);
                }
            }
        });

        pagos.getColumns().addAll(fp, ip, forma, estadoCol, colAcciones);
        pagos.setItems(FXCollections.observableArrayList(basePago.listarPorProveedor(cuenta.getIdProveedor())));
        pagos.setPrefHeight(180);

        // Botón Volver
        Button volver = new Button("← VOLVER A CUENTAS");
        volver.getStyleClass().add("boton-editar-tabla");
        volver.setOnAction(e -> root.setCenter(new ListadoCuentasProveedores(root)));

        getChildren().addAll(titulo, new Separator(), info, comprasTitulo, compras, pagosTitulo, pagos, volver);
    }

    private void confirmarYAnular(PagoProveedor pago, BorderPane root, CuentaProveedor cuenta) 
    {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Anulación");
        confirm.setHeaderText("¿Está seguro de anular el pago #" + pago.getIdPagoProveedor() + "?");
        confirm.setContentText("Esta acción revertirá el importe de $" + pago.getImporte() + " a la cuenta corriente del proveedor.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) 
        {
            boolean exito = basePago.anularPago(pago.getIdPagoProveedor());
            if (exito) 
            {
                // Recargar la pantalla para refrescar el saldo y el estado de la tabla
                root.setCenter(new DetalleCuentaProveedor(root, cuenta));
            } 
            else 
            {
                Alert err = new Alert(Alert.AlertType.ERROR, "No se pudo anular el pago.", ButtonType.OK);
                err.showAndWait();
            }
        }
    }
}