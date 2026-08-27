package main;

import base.BaseCompra;
import base.BasePagoProveedor;
import clases.Compra;
import clases.PagoProveedor;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DetalleCompra extends VBox 
{

    public DetalleCompra(BorderPane rootPrincipal, int idCompra) 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(20);
        setPadding(new Insets(20));

        Compra compra = new BaseCompra().obtenerCompraPorId(idCompra);
        Label titulo = new Label("DETALLE DE COMPRA #" + idCompra);
        titulo.getStyleClass().add("titulo-pantalla");

        if (compra == null) 
        {
            getChildren().addAll(titulo, new Separator(), new Label("No se encontró la compra solicitada."));
        } 
        else 
        {
            NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
           
            String factura = compra.getNroFactura() == null || compra.getNroFactura().isBlank()
                    ? "Sin número" : compra.getNroFactura();
           
            String vencimiento = compra.getFechaVencimiento() == null || compra.getFechaVencimiento().isBlank()
                    ? "Sin vencimiento" : compra.getFechaVencimiento();
            
            Label informacion = new Label(
                    "• Proveedor: " + compra.getNombreProveedor() + "\n"
                    + "• N° de factura: " + factura + "\n"
                    + "• Fecha de emisión: " + compra.getFecha() + "\n"
                    + "• Fecha de vencimiento: " + vencimiento + "\n"
                    + "• Importe: " + formato.format(compra.getImporte()) + "\n"
                    + "• Estado: " + compra.getEstado() + "\n\n"
                    + "Detalle de ítems:\n" + compra.getDetalle());
            
            informacion.setStyle("-fx-font-size: 18px; -fx-text-fill: #333333;");
            informacion.setWrapText(true);
            informacion.setLineSpacing(5);

            Label pagosTitulo = new Label("Pagos asociados a esta compra:");
            pagosTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            TableView<PagoProveedor> pagos = new TableView<>();
            pagos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<PagoProveedor, String> fecha = new TableColumn<>("Fecha");
            fecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
            
            TableColumn<PagoProveedor, Double> importe = new TableColumn<>("Importe");
            importe.setCellValueFactory(new PropertyValueFactory<>("importe"));
            
            TableColumn<PagoProveedor, String> forma = new TableColumn<>("Forma de pago");
            forma.setCellValueFactory(new PropertyValueFactory<>("formaPago"));
            
            TableColumn<PagoProveedor, String> observaciones = new TableColumn<>("Observaciones");
            observaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
           
            pagos.getColumns().addAll(fecha, importe, forma, observaciones);
            pagos.setItems(javafx.collections.FXCollections.observableArrayList(new BasePagoProveedor().listarPorCompra(idCompra)));
            pagos.setPrefHeight(180);

            Button volver = new Button("← VOLVER AL HISTORIAL DE COMPRAS");
            volver.getStyleClass().add("boton-editar-tabla");
            volver.setOnAction(e -> rootPrincipal.setCenter(new ListadoCompras(rootPrincipal)));
            getChildren().addAll(titulo, new Separator(), informacion, pagosTitulo, pagos, volver);
        }
    }
}
