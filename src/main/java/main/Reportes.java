package main;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;
import clases.DeudorReporte;
import base.BasePago;
import java.util.List;

public class Reportes 
{

    public VBox getView()
    {
        BasePago basePago = new BasePago();
        
        VBox root = new VBox(25); // Un poco más de espacio entre secciones
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f4f7fa;");

        // SECCIÓN 1: TARJETAS DE TOTALES 
        HBox filaTotales = new HBox(25);
        filaTotales.setAlignment(Pos.CENTER);
        
        double ingresoMes = basePago.obtenerTotalIngresosMesActual();
        double ingresoTotal = basePago.obtenerTotalIngresosHistorico();
        double deudaCobrar = basePago.obtenerDeudaTotalGlobal();

        // Formateamos para que siempre muestre 2 decimales
        VBox cardIngresoMes = crearTarjeta("INGRESOS DEL MES", "$ " + String.format("%,.2f", ingresoMes), "#4CAF50");
        VBox cardIngresoTotal = crearTarjeta("TOTAL INGRESADO", "$ " + String.format("%,.2f", ingresoTotal), "#2196F3");
        VBox cardDeudaTotal = crearTarjeta("DEUDA PENDIENTE", "$ " + String.format("%,.2f", deudaCobrar), "#FF9800");

        filaTotales.getChildren().addAll(cardIngresoMes, cardIngresoTotal, cardDeudaTotal);

        // SECCIÓN 2: TABLA DE MOROSIDAD 
        VBox seccionTabla = new VBox(15);
        VBox.setVgrow(seccionTabla, Priority.ALWAYS); // Hace que la tabla use el espacio sobrante

        Label lblTabla = new Label("Alertas de Cobranza (Atraso > 30 días)");
        lblTabla.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #333;");

        TableView<DeudorReporte> tablaMora = new TableView<>();
        
        TableColumn<DeudorReporte, String> colCli = new TableColumn<>("Cliente");
        colCli.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colCli.setPrefWidth(250);
        
        TableColumn<DeudorReporte, Double> colSal = new TableColumn<>("Saldo Deudor");
        colSal.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        colSal.setPrefWidth(150);
        
        TableColumn<DeudorReporte, Integer> colDia = new TableColumn<>("Días de Atraso");
        colDia.setCellValueFactory(new PropertyValueFactory<>("diasAtraso"));
        colDia.setPrefWidth(120);

        TableColumn<DeudorReporte, String> colAcc = new TableColumn<>("Acción Sugerida");
        colAcc.setCellValueFactory(new PropertyValueFactory<>("accion"));
        colAcc.setPrefWidth(200);

        tablaMora.getColumns().addAll(colCli, colSal, colDia, colAcc);
        
        // Carga de datos
        List<DeudorReporte> morosos = basePago.obtenerClientesMorosos();
        tablaMora.getItems().addAll(morosos);
        
        // Mensaje si no hay morosos
        tablaMora.setPlaceholder(new Label("No hay deudas con más de 30 días de atraso. ¡Todo al día!"));

        seccionTabla.getChildren().addAll(lblTabla, tablaMora);

        // Agregamos solo las tarjetas y la tabla
        root.getChildren().addAll(filaTotales, seccionTabla);
        
        return root;
    }

    private VBox crearTarjeta(String titulo, String valor, String colorHex) 
    {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefWidth(320);
        card.setStyle("-fx-background-color: white; " +
                     "-fx-border-radius: 15; " +
                     "-fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 8);");
        
        Label lblT = new Label(titulo.toUpperCase());
        lblT.setStyle("-fx-font-size: 11px; -fx-text-fill: #999; -fx-font-weight: bold; -fx-letter-spacing: 1px;");
        
        Label lblV = new Label(valor);
        lblV.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        
        card.getChildren().addAll(lblT, lblV);
        return card;
    }
}