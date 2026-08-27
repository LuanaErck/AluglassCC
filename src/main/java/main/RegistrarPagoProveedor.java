package main;

import base.BaseCompra;
import base.BasePagoProveedor;
import base.BaseProveedor;
import clases.Compra;
import clases.PagoProveedor;
import clases.Proveedor;
import java.time.LocalDate;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilidades.Validadores;

public class RegistrarPagoProveedor extends VBox 
{

    private final Label mensaje = new Label();
    private final ComboBox<Proveedor> proveedores = new ComboBox<>();
    private final ComboBox<Compra> compras = new ComboBox<>();

    public RegistrarPagoProveedor(BorderPane root) 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(20);
        setPadding(new Insets(20));

        Label titulo = new Label("REGISTRAR PAGO A PROVEEDOR");
        titulo.getStyleClass().add("titulo-pantalla");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_LEFT);

        // Selección de Proveedor
        proveedores.setPromptText("Seleccione un proveedor...");
        proveedores.setPrefWidth(350);
        proveedores.setStyle("-fx-font-size:14px;-fx-padding:5;");
        actualizarProveedores();

        Button plusProveedor = botonPlus();
        plusProveedor.setOnAction(e -> abrirProveedor());
        HBox filaProveedor = new HBox(10, proveedores, plusProveedor);
        filaProveedor.setAlignment(Pos.CENTER_LEFT);

        // Selección de Compra
        compras.setPromptText("Primero seleccione un proveedor...");
        compras.setPrefWidth(350);
        compras.setStyle("-fx-font-size:14px;-fx-padding:5;");
        proveedores.setOnAction(e -> actualizarCompras());

        Button plusCompra = botonPlus();
        plusCompra.setOnAction(e -> abrirCompra());
        HBox filaCompra = new HBox(10, compras, plusCompra);
        filaCompra.setAlignment(Pos.CENTER_LEFT);

        // Resto de Campos
        DatePicker fecha = new DatePicker(LocalDate.now());
        fecha.setPrefWidth(200);
        fecha.setStyle("-fx-font-size:14px;-fx-padding:5;");

        TextField importe = campo("0.00");

        ComboBox<String> forma = new ComboBox<>();
        forma.getItems().addAll("Efectivo", "Transferencia", "Cheque", "Tarjeta");
        forma.setValue("Transferencia");
        forma.setPrefWidth(250);
        forma.setStyle("-fx-font-size:14px;-fx-padding:5;");

        TextArea observaciones = new TextArea();
        observaciones.setPromptText("Opcional");
        observaciones.setPrefRowCount(3);
        observaciones.setPrefWidth(350);
        observaciones.setWrapText(true);
        observaciones.getStyleClass().add("input-login");

        // Disposición en Grid
        grid.add(etiqueta("Proveedor:"), 0, 0);
        grid.add(filaProveedor, 1, 0);

        grid.add(etiqueta("Compra:"), 0, 1);
        grid.add(filaCompra, 1, 1);

        grid.add(etiqueta("Fecha:"), 0, 2);
        grid.add(fecha, 1, 2);

        grid.add(etiqueta("Importe:"), 0, 3);
        grid.add(importe, 1, 3);

        grid.add(etiqueta("Forma de pago:"), 0, 4);
        grid.add(forma, 1, 4);

        grid.add(etiqueta("Observaciones:"), 0, 5);
        grid.add(observaciones, 1, 5);

        // Botón Registrar
        Button guardar = new Button("REGISTRAR PAGO");
        guardar.getStyleClass().add("boton-editar-tabla");
        guardar.setPadding(new Insets(12, 25, 12, 25));
        
        guardar.setOnAction(e -> 
        {
            String texto = importe.getText().trim();
            boolean valido = proveedores.getValue() != null 
                    && compras.getValue() != null 
                    && fecha.getValue() != null 
                    && Validadores.esNumero(texto) 
                    && Validadores.esMayorACero(Double.parseDouble(texto));

            if (!valido) 
            {
                mostrar("❌ Seleccione proveedor, compra, fecha e importe válido.", true);
            } 
            else 
            {
                PagoProveedor p = new PagoProveedor(
                    0, 
                    proveedores.getValue().getIdProveedor(), 
                    compras.getValue().getIdCompra(), 
                    fecha.getValue().toString(), 
                    Double.parseDouble(texto), 
                    forma.getValue(), 
                    observaciones.getText().trim(), 
                    "Activo"
                );

                if (new BasePagoProveedor().registrarPago(p)) 
                {
                    mostrar("✅ Pago registrado correctamente.", false);
                    importe.clear();
                    observaciones.clear();
                    fecha.setValue(LocalDate.now());
                } 
                else
                {
                    mostrar("❌ No se pudo registrar el pago.", true);
                }
            }
        });

        getChildren().addAll(titulo, new Separator(), grid, guardar, mensaje);
    }

    private void actualizarProveedores() 
    {
        proveedores.getItems().setAll(new BaseProveedor().listarProveedoresActivos());
    }

    private void actualizarCompras() 
    {
        compras.getItems().clear();
        Proveedor p = proveedores.getValue();
        if (p == null) 
        {
            compras.setPromptText("Primero seleccione un proveedor...");
        } 
        else
        {
            compras.getItems().addAll(new BaseCompra().listarComprasPorProveedor(p.getIdProveedor()));
            compras.setPromptText(compras.getItems().isEmpty() ? "Sin compras registradas" : "Seleccione una compra...");
        }
    }

    private void abrirProveedor() 
    {
        Stage s = new Stage();
        s.initModality(Modality.APPLICATION_MODAL);
        s.setTitle("Aluglass - Nuevo proveedor");
        AgregarProveedor vista = new AgregarProveedor(s);
        Scene escena = new Scene(vista);

        try 
        {
            escena.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
        } 
        catch (Exception e) {}

        s.setScene(escena);
        s.showAndWait();
        actualizarProveedores();

        if (vista.getProveedorCreado() != null) 
        {
            for (Proveedor p : proveedores.getItems()) 
            {
                if (p.getIdProveedor() == vista.getProveedorCreado().getIdProveedor()) 
                {
                    proveedores.setValue(p);
                    actualizarCompras();
                    break;
                }
            }
        }
    }

    private void abrirCompra()
    {
        Proveedor proveedor = proveedores.getValue();
        if (proveedor == null) 
        {
            mostrar("⚠️ Primero seleccione un proveedor.", true);
        } 
        else
        {
            Stage s = new Stage();
            s.initModality(Modality.APPLICATION_MODAL);
            s.setTitle("Aluglass - Nueva compra");
            RegistrarCompra vista = new RegistrarCompra(s, proveedor);
            Scene escena = new Scene(vista);

            try 
            {
                escena.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
            } catch (Exception e) {}

            s.setScene(escena);
            s.showAndWait();
            actualizarCompras();

            if (vista.getCompraCreada() != null) 
            {
                for (Compra compra : compras.getItems())
                {
                    if (compra.getIdCompra() == vista.getCompraCreada().getIdCompra()) 
                    {
                        compras.setValue(compra);
                        break;
                    }
                }
            }
        }
    }

    private Button botonPlus() 
    {
        Button b = new Button("+");
        b.setStyle("-fx-font-weight:bold;-fx-background-color:#2e7d32;-fx-text-fill:white;-fx-font-size:16px;");
        return b;
    }

    private TextField campo(String a) 
    {
        TextField t = new TextField();
        t.setPromptText(a);
        t.setPrefWidth(350);
        t.getStyleClass().add("input-login");
        return t;
    }

    private Label etiqueta(String a) 
    {
        Label l = new Label(a);
        l.setStyle("-fx-font-size:16px;-fx-font-weight:bold;");
        return l;
    }

    private void mostrar(String a, boolean error) 
    {
        mensaje.setText(a);
        mensaje.setStyle(error ? "-fx-text-fill:#d32f2f;-fx-font-weight:bold;" 
                               : "-fx-text-fill:#2e7d32;-fx-font-weight:bold;");
    }
}