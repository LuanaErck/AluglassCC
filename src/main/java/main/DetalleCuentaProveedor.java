package main;

import base.BaseCompra;
import base.BasePagoProveedor;
import clases.Compra;
import clases.CuentaProveedor;
import clases.PagoProveedor;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class DetalleCuentaProveedor extends VBox 
{

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

        pagos.getColumns().addAll(fp, ip, forma);
        pagos.setItems(FXCollections.observableArrayList(new BasePagoProveedor().listarPorProveedor(cuenta.getIdProveedor())));
        pagos.setPrefHeight(180);

        // Botón Volver
        Button volver = new Button("← VOLVER A CUENTAS");
        volver.getStyleClass().add("boton-editar-tabla");
        volver.setOnAction(e -> root.setCenter(new ListadoCuentasProveedores(root)));

        getChildren().addAll(titulo, new Separator(), info, comprasTitulo, compras, pagosTitulo, pagos, volver);
    }
}
