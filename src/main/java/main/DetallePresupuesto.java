package main;

import base.BasePago;
import base.BasePresupuesto; // Importante para buscar el presupuesto
import clases.Pago;
import clases.Presupuesto;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.text.NumberFormat;
import java.util.Locale;

public class DetallePresupuesto extends VBox 
{
    public DetallePresupuesto(BorderPane rootPrincipal, int idPresupuesto, int idCliente, String origen) 
    {
        // 1. CARGAMOS EL OBJETO PRESUPUESTO DESDE LA BASE DE DATOS
        // Usamos el ID que recibimos para obtener todos los datos (nombre, total, etc.)
        Presupuesto presupuesto = new BasePresupuesto().obtenerPorId(idPresupuesto);

        // Configuración del contenedor
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(20);
        this.setPadding(new Insets(20));

        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es","AR"));

        // 2. TÍTULO
        Label lblTitulo = new Label("DETALLE DE PRESUPUESTO #" + idPresupuesto);
        lblTitulo.getStyleClass().add("titulo-pantalla");

        // 3. BLOQUE DE INFORMACIÓN
        String estiloInfo = "-fx-font-size: 18px; -fx-text-fill: #333333;";
        
        Label lblInfo = new Label(
            "• Cliente: " + presupuesto.getNombreCliente() + "\n" +
            "• Fecha: " + presupuesto.getFecha() + "\n" +
            "• Descripción: " + presupuesto.getDescripcion() + "\n" +
            "• Total: " + formato.format(presupuesto.getTotal()) + "\n" +
            "• Saldo Pendiente: " + formato.format(presupuesto.getSaldoPresupuesto()) + "\n" +
            "• Estado: " + presupuesto.getEstado()
        );
        lblInfo.setStyle(estiloInfo);
        lblInfo.setLineSpacing(5);

        // 4. TABLA DE PAGOS
        Label lblSubtitulo = new Label("Pagos realizados asociados:");
        lblSubtitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        TableView<Pago> tablaPagos = new TableView<>();
        tablaPagos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pago, String> colFecha = new TableColumn<>("Fecha Pago");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        
        TableColumn<Pago, String> colMoneda = new TableColumn<>("Moneda");
        colMoneda.setCellValueFactory(new PropertyValueFactory<>("moneda"));
        
        TableColumn<Pago, Double> colMonto = new TableColumn<>("Importe (ARS)");
        colMonto.setCellValueFactory(new PropertyValueFactory<>("importePesos"));
        colMonto.setCellFactory(col -> new TableCell<>() 
        {
            @Override protected void updateItem(Double v, boolean e) 
            {
                super.updateItem(v, e);
                setText(e || v == null ? null : formato.format(v));
            }
        });

        TableColumn<Pago, String> colForma = new TableColumn<>("Forma");
        colForma.setCellValueFactory(new PropertyValueFactory<>("formaPago"));
        
        TableColumn<Pago, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        // 4.5 COLUMNA ACCIONES (Para ver el recibo)
        TableColumn<Pago, Void> colAccionPago = new TableColumn<>("Recibo");
        colAccionPago.setCellFactory(param -> new TableCell<>() 
        {
            private final Button btnVerRecibo = new Button("Ver PDF");
            {
                btnVerRecibo.getStyleClass().add("boton-editar-tabla");
                btnVerRecibo.setOnAction(e -> 
                {
                    // 1. Obtenemos el pago de la fila actual
                    Pago pago = getTableView().getItems().get(getIndex());
                    // 2. Buscamos la ruta usando tu lógica de BaseRecibo
                    String ruta = new base.BaseRecibo().obtenerRutaPdfPorPago(pago.getIdPago());
                    if (ruta != null) 
                    {
                        try 
                        {
                            // 3. Intentamos abrir el archivo
                            java.awt.Desktop.getDesktop().open(new java.io.File(ruta));
                        } 
                        catch (Exception ex) 
                        {
                            ex.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Error al intentar abrir el archivo PDF").show();
                        }
                    } 
                    else 
                    {
                        // 4. Si la ruta es nula en la base de datos
                        new Alert(Alert.AlertType.WARNING, "No se encontró el archivo PDF para este pago").show();
                    }
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
                    setGraphic(btnVerRecibo);
                }
            }
        });

        tablaPagos.getColumns().addAll(colFecha, colMoneda, colMonto, colForma, colEstado, colAccionPago);
        
        // Carga de pagos asociados a este presupuesto específico
        tablaPagos.setItems(FXCollections.observableArrayList(new BasePago().listarPagosPorPresupuesto(idPresupuesto)));
        tablaPagos.setPrefHeight(250);

        // 5. BOTÓN VOLVER INTELIGENTE
        Button btnVolver = new Button();
        btnVolver.getStyleClass().add("boton-editar-tabla");
        btnVolver.setPadding(new Insets(10, 20, 10, 20));

        // Condicionamos la acción según el origen
        if ("presupuestos".equals(origen)) 
        {
            btnVolver.setText("← VOLVER AL LISTADO GENERAL");
            btnVolver.setOnAction(e -> 
            {
                rootPrincipal.setCenter(new ListadoPresupuestos(rootPrincipal));
            });
        } 
        else 
        {
            btnVolver.setText("← VOLVER AL DETALLE DE CUENTA");
            btnVolver.setOnAction(e -> 
            {
                // Usamos los IDs que recibimos para volver a la cuenta correcta
                rootPrincipal.setCenter(new DetalleCuenta(rootPrincipal, idCliente, origen));
            });
        }

        this.getChildren().addAll(lblTitulo, new Separator(), lblInfo, lblSubtitulo, tablaPagos, btnVolver);
    }
}