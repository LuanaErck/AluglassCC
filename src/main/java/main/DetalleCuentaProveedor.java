package main;

import base.BaseCompra;
import base.BaseCuentaProveedor;
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
    private final BaseCompra baseCompra = new BaseCompra();
    private final BaseCuentaProveedor baseCuenta = new BaseCuentaProveedor();

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

        // --- SECCIÓN COMPRAS ---
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

        TableColumn<Compra, String> estadoCompraCol = new TableColumn<>("Estado");
        estadoCompraCol.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Columna de Acciones: Anular Compra
        TableColumn<Compra, Void> colAccionesCompras = new TableColumn<>("Acciones");
        colAccionesCompras.setCellFactory(param -> new TableCell<>()
        {
            private final Button btnAnularCompra = new Button("Anular Compra");

            {
                btnAnularCompra.setOnAction(e -> 
                {
                    Compra compra = getTableView().getItems().get(getIndex());
                    confirmarYAnularCompra(compra, root, cuenta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) 
                {
                    setGraphic(null);
                } 
                else
                {
                    Compra compra = getTableView().getItems().get(getIndex());
                    
                    // Evaluamos si el estado es Cancelada o Anulada
                    if ("Cancelada".equalsIgnoreCase(compra.getEstado()) || "Anulada".equalsIgnoreCase(compra.getEstado())) 
                    {
                        btnAnularCompra.setDisable(true);
                        btnAnularCompra.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white;");
                    }
                    else
                    {
                        btnAnularCompra.setDisable(false);
                        btnAnularCompra.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                    }
                    setGraphic(btnAnularCompra);
                }
            }
        });

        compras.getColumns().addAll(fc, det, ic, estadoCompraCol, colAccionesCompras);
        compras.setItems(FXCollections.observableArrayList(baseCompra.listarComprasPorProveedor(cuenta.getIdProveedor())));
        compras.setPrefHeight(180);

        // --- SECCIÓN PAGOS ---
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
        TableColumn<PagoProveedor, Void> colAccionesPagos = new TableColumn<>("Acciones");
        colAccionesPagos.setCellFactory(param -> new TableCell<>() 
        {
            private final Button btnAnularPago = new Button("Anular Pago");

            {
                btnAnularPago.setOnAction(e -> 
                {
                    PagoProveedor pago = getTableView().getItems().get(getIndex());
                    confirmarYAnularPago(pago, root, cuenta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) 
            {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size())
                {
                    setGraphic(null);
                }
                else
                {
                    PagoProveedor pago = getTableView().getItems().get(getIndex());
                    if ("Anulado".equalsIgnoreCase(pago.getEstado())) 
                    {
                        btnAnularPago.setDisable(true);
                        btnAnularPago.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white;");
                    } 
                    else
                    {
                        btnAnularPago.setDisable(false);
                        btnAnularPago.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                    }
                    setGraphic(btnAnularPago);
                }
            }
        });

        pagos.getColumns().addAll(fp, ip, forma, estadoCol, colAccionesPagos);
        pagos.setItems(FXCollections.observableArrayList(basePago.listarPorProveedor(cuenta.getIdProveedor())));
        pagos.setPrefHeight(180);

        // Botón Volver
        Button volver = new Button("← VOLVER A CUENTAS");
        volver.getStyleClass().add("boton-editar-tabla");
        volver.setOnAction(e -> root.setCenter(new ListadoCuentasProveedores(root)));

        getChildren().addAll(titulo, new Separator(), info, comprasTitulo, compras, pagosTitulo, pagos, volver);
    }

    private void confirmarYAnularCompra(Compra compra, BorderPane root, CuentaProveedor cuenta)
    {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Anulación");
        confirm.setHeaderText("¿Está seguro de anular la compra #" + compra.getIdCompra() + "?");
        confirm.setContentText("Esta acción descontará el importe de $" + compra.getImporte() + " del saldo de la cuenta corriente.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            boolean exito = baseCompra.anularCompra(compra.getIdCompra());
            if (exito) 
            {
                CuentaProveedor cuentaActualizada = baseCuenta.obtenerCuentaPorId(cuenta.getIdProveedor());
                root.setCenter(new DetalleCuentaProveedor(root, cuentaActualizada));
            } 
            else 
            {
                Alert err = new Alert(Alert.AlertType.ERROR, "No se pudo anular la compra (posiblemente ya fue anulada).", ButtonType.OK);
                err.showAndWait();
            }
        }
    }

    private void confirmarYAnularPago(PagoProveedor pago, BorderPane root, CuentaProveedor cuenta) 
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
                CuentaProveedor cuentaActualizada = baseCuenta.obtenerCuentaPorId(cuenta.getIdProveedor());
                root.setCenter(new DetalleCuentaProveedor(root, cuentaActualizada));
            } 
            else
            {
                Alert err = new Alert(Alert.AlertType.ERROR, "No se pudo anular el pago.", ButtonType.OK);
                err.showAndWait();
            }
        }
    }
}