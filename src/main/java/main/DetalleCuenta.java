package main;

import base.BaseCuenta;
import base.BasePago;
import base.BasePresupuesto;
import base.BaseRecibo;
import clases.Pago;
import clases.Presupuesto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.text.NumberFormat;
import java.util.Locale;

public class DetalleCuenta extends VBox 
{
    
    private NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

    public DetalleCuenta(BorderPane rootPrincipal, int idCliente, String origen) 
    {
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(15);
        this.setPadding(new Insets(20));

        // 1. CABECERA Y SALDO
        Button btnVolver = new Button("← Volver");
        btnVolver.getStyleClass().add("boton-editar-tabla");
        btnVolver.setOnAction(e ->
        {
            if ("clientes".equals(origen))
            {
                rootPrincipal.setCenter(new ListadoClientes(rootPrincipal));
            } 
            else if ("atrasadas".equals(origen)) 
            {
                rootPrincipal.setCenter(new ListadoCuentasAtrasadas(rootPrincipal));
            } 
            else {
                // Por defecto vuelve a ListadoCuentas (el origen "cuentas")
                rootPrincipal.setCenter(new ListadoCuentas(rootPrincipal));
            }
        });

        BaseCuenta baseCuenta = new BaseCuenta();
        double saldo = baseCuenta.obtenerSaldoCliente(idCliente);
        Label lblSaldo = new Label("SALDO TOTAL: " + formato.format(saldo));
        lblSaldo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #800000;");

        HBox cabecera = new HBox(20, btnVolver, lblSaldo);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        // SECCIÓN HISTORIAL DE PAGOS
        Label titPagos = new Label("HISTORIAL DE PAGOS");
        titPagos.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        
        TextField txtFiltroPagos = new TextField();
        txtFiltroPagos.setPromptText("Buscar por Estado o Forma de Pago (Efectivo, Transferencia, etc)...");
        txtFiltroPagos.setMaxWidth(400);
        txtFiltroPagos.getStyleClass().add("input-login");

        TableView<Pago> tablaPagos = new TableView<>();
        tablaPagos.setPrefHeight(250);
        tablaPagos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pago, Integer> colIdPago = new TableColumn<>("ID");
        colIdPago.setCellValueFactory(new PropertyValueFactory<>("idPago"));
        colIdPago.setMaxWidth(60);

        TableColumn<Pago, Double> colMonto = new TableColumn<>("Importe");
        colMonto.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colMonto.setCellFactory(col -> new TableCell<>() 
        {
            @Override protected void updateItem(Double v, boolean e) 
            {
                super.updateItem(v, e);
                if (e || v == null) setText(null);
                else 
                {
                    setText(formato.format(v));
                    setTextFill(javafx.scene.paint.Color.DARKGREEN);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });
        
        TableColumn<Pago, String> colFormaPago = new TableColumn<>("Forma de pago");
        colFormaPago.setCellValueFactory(new PropertyValueFactory<>("formaPago"));
        
        TableColumn<Pago, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<Pago, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() 
        {
            private final Button btnRecibo = new Button("Ver");
            private final Button btnAnular = new Button("Anular");
            private final HBox caja = new HBox(5, btnRecibo, btnAnular);
            {
                btnRecibo.getStyleClass().add("boton-editar-tabla");
                btnAnular.getStyleClass().add("boton-editar-tabla");
                
                btnRecibo.setOnAction(e -> 
                {
                    Pago pago = getTableView().getItems().get(getIndex());
                    String ruta = new BaseRecibo().obtenerRutaPdfPorPago(pago.getIdPago());
                    if (ruta != null)
                    {
                        try { java.awt.Desktop.getDesktop().open(new java.io.File(ruta)); }
                        catch (Exception ex) { ex.printStackTrace(); }
                    } 
                    else 
                    {
                        new Alert(Alert.AlertType.WARNING, "No se encontró el PDF").show();
                    }
                });

                // LÓGICA DE ANULACIÓN MEJORADA
                btnAnular.setOnAction(e -> 
                {
                    Pago pago = getTableView().getItems().get(getIndex());
                    
                    // Si ya está anulado, avisamos al usuario
                    if ("Anulado".equalsIgnoreCase(pago.getEstado())) 
                    {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setTitle("Atención");
                        info.setHeaderText(null);
                        info.setContentText("Este pago ya se encuentra anulado.");
                        info.showAndWait();
                        return;
                    }

                    // Confirmación de anulación
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea anular este pago?", ButtonType.YES, ButtonType.NO);
                    if (confirm.showAndWait().get() == ButtonType.YES) 
                    {
                        new BasePago().anularPago(pago.getIdPago());
                        // Refrescamos la vista
                        rootPrincipal.setCenter(new DetalleCuenta(rootPrincipal, idCliente, origen));
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) 
            {
                super.updateItem(item, empty);
                setGraphic(empty ? null : caja);
            }
        });

        tablaPagos.getColumns().addAll(colIdPago, colMonto, colFormaPago, colEstado, colAcciones);
        
        ObservableList<Pago> masterPagos = FXCollections.observableArrayList(new BasePago().listarPagosPorCliente(idCliente));
        FilteredList<Pago> filteredPagos = new FilteredList<>(masterPagos, p -> true);
        txtFiltroPagos.textProperty().addListener((obs, oldVal, newVal) -> 
        {
            filteredPagos.setPredicate(pago -> 
            {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return pago.getEstado().toLowerCase().contains(lower) || 
                       pago.getFormaPago().toLowerCase().contains(lower);
            });
        });
        tablaPagos.setItems(filteredPagos);

        // SECCIÓN PRESUPUESTOS
        Label titPres = new Label("PRESUPUESTOS ASOCIADOS");
        titPres.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        
        TextField txtFiltroPre = new TextField();
        txtFiltroPre.setPromptText("Buscar por Estado o Fecha...");
        txtFiltroPre.setMaxWidth(400);
        txtFiltroPre.getStyleClass().add("input-login");

        TableView<Presupuesto> tablaPre = new TableView<>();
        tablaPre.setPrefHeight(200);
        tablaPre.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Presupuesto, Integer> colIdPre = new TableColumn<>("ID");
        colIdPre.setCellValueFactory(new PropertyValueFactory<>("idPresupuesto"));
        colIdPre.setMaxWidth(60);
        
        TableColumn<Presupuesto, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<Presupuesto, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(col -> new TableCell<>()
        {
            @Override protected void updateItem(Double v, boolean e) 
            {
                super.updateItem(v, e);
                setText(e || v == null ? null : formato.format(v));
            }
        });
        
        TableColumn<Presupuesto, Double> colSaldoP = new TableColumn<>("Saldo");
        colSaldoP.setCellValueFactory(new PropertyValueFactory<>("saldoPresupuesto"));
        colSaldoP.setCellFactory(col -> new TableCell<>() 
        {
            @Override protected void updateItem(Double v, boolean e)
            {
                super.updateItem(v, e);
                if (e || v == null) setText(null);
                else 
                {
                    setText(formato.format(v));
                    if (v > 0) setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #388e3c;");
                }
            }
        });
        
        TableColumn<Presupuesto, String> colEstadoPres = new TableColumn<>("Estado");
        colEstadoPres.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        TableColumn<Presupuesto, Void> colAccionesPre = new TableColumn<>("Acción");
        colAccionesPre.setCellFactory(param -> new TableCell<>() 
        {
            private final Button btnVer = new Button("Ver Detalle");
            {
                btnVer.getStyleClass().add("boton-editar-tabla");
                btnVer.setOnAction(e -> 
                {
                    Presupuesto pres = getTableView().getItems().get(getIndex());
                    rootPrincipal.setCenter(new DetallePresupuesto(rootPrincipal, pres.getIdPresupuesto(), idCliente, origen));
                });
            }
            @Override protected void updateItem(Void item, boolean empty) 
            {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnVer);
            }
        });

        tablaPre.getColumns().addAll(colIdPre, colFecha, colTotal, colSaldoP, colEstadoPres, colAccionesPre);
        
        ObservableList<Presupuesto> masterPre = FXCollections.observableArrayList(new BasePresupuesto().listarPorCliente(idCliente));
        FilteredList<Presupuesto> filteredPre = new FilteredList<>(masterPre, p -> true);
        txtFiltroPre.textProperty().addListener((obs, oldVal, newVal) -> 
        {
            filteredPre.setPredicate(pres -> 
            {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return pres.getEstado().toLowerCase().contains(lower) || 
                       pres.getFecha().toLowerCase().contains(lower);
            });
        });
        tablaPre.setItems(filteredPre);

        this.getChildren().addAll(
            cabecera, new Separator(), 
            titPagos, txtFiltroPagos, tablaPagos, 
            new Separator(),
            titPres, txtFiltroPre, tablaPre
        );
    }
}