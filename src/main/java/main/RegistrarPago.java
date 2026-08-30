package main;

import base.BaseCliente;
import base.BasePago;
import base.BasePresupuesto;
import clases.Cliente;
import clases.Presupuesto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilidades.Validadores;
import java.awt.Desktop;
import java.io.File;

public class RegistrarPago extends VBox 
{
    private Label lblMensaje; 
    private ComboBox<Cliente> comboClientes;
    private ComboBox<Presupuesto> comboPresupuestos;
    private BorderPane rootPrincipal;

    public RegistrarPago(BorderPane rootPrincipal)
    {
        this.rootPrincipal = rootPrincipal;
        
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(20);
        this.setPadding(new Insets(20));

        Label lblTitulo = new Label("REGISTRAR NUEVO COBRO");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        lblMensaje = new Label("");
        lblMensaje.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.TOP_LEFT);

        String estiloLabel = "-fx-font-size: 16px; -fx-font-weight: bold;";
        String estiloCombo = "-fx-font-size: 14px; -fx-padding: 5;";

        // CLIENTE CON BOTÓN +
        Label lblC = new Label("Cliente:");
        lblC.setStyle(estiloLabel);
        
        comboClientes = new ComboBox<>();
        comboClientes.setPromptText("Seleccione un cliente...");
        comboClientes.setPrefWidth(350);
        comboClientes.setStyle(estiloCombo);
        actualizarComboClientes();

        Button btnPlusCliente = crearBotonPlus();
        btnPlusCliente.setOnAction(e -> abrirNuevoCliente());
        
        HBox hbCliente = new HBox(10, comboClientes, btnPlusCliente);
        hbCliente.setAlignment(Pos.CENTER_LEFT);

        // PRESUPUESTO CON BOTÓN +
        Label lblP = new Label("Presupuesto:");
        lblP.setStyle(estiloLabel);
        
        comboPresupuestos = new ComboBox<>();
        comboPresupuestos.setPromptText("Esperando cliente...");
        comboPresupuestos.setPrefWidth(350);
        comboPresupuestos.setStyle(estiloCombo);

        comboClientes.setOnAction(e -> actualizarComboPresupuestos());

        Button btnPlusPresupuesto = crearBotonPlus();
        btnPlusPresupuesto.setOnAction(e -> abrirNuevoPresupuesto());
        
        HBox hbPresupuesto = new HBox(10, comboPresupuestos, btnPlusPresupuesto);
        hbPresupuesto.setAlignment(Pos.CENTER_LEFT);

        // MONTO
        Label lblM = new Label("Monto:");
        lblM.setStyle(estiloLabel);
        TextField txtMonto = new TextField();
        txtMonto.setPromptText("0.00");
        txtMonto.getStyleClass().add("input-login");

        Label lblFecha = new Label("Fecha:");
        lblFecha.setStyle(estiloLabel);
        DatePicker fechaPago = new DatePicker(java.time.LocalDate.now());
        fechaPago.setPrefWidth(200);

        // MONEDA
        Label lblMon = new Label("Moneda:");
        lblMon.setStyle(estiloLabel);
        ComboBox<String> comboMoneda = new ComboBox<>();
        comboMoneda.getItems().addAll("Pesos", "Dolares");
        comboMoneda.setValue("Pesos");
        comboMoneda.setStyle(estiloCombo);
        comboMoneda.setPrefWidth(250);

        // COTIZACIÓN
        Label lblCot = new Label("Cotización USD:");
        lblCot.setStyle(estiloLabel);
        TextField txtCotizacion = new TextField();
        txtCotizacion.setPromptText("Ej: 1100");
        txtCotizacion.setDisable(true);
        txtCotizacion.getStyleClass().add("input-login");

        comboMoneda.setOnAction(e -> 
        {
            boolean esDolar = comboMoneda.getValue().equals("Dolares");
            txtCotizacion.setDisable(!esDolar);
            if (!esDolar) txtCotizacion.clear();
        });

        // FORMA DE PAGO 
        Label lblF = new Label("Forma de Pago:");
        lblF.setStyle(estiloLabel);
        ComboBox<String> comboForma = new ComboBox<>();
        comboForma.getItems().addAll("Efectivo", "Transferencia", "Cheque");
        comboForma.setValue("Efectivo");
        comboForma.setPrefWidth(250);
        comboForma.setStyle(estiloCombo);

        // OBSERVACIONES
        Label lblO = new Label("Observaciones:");
        lblO.setStyle(estiloLabel);
        TextField txtObs = new TextField();
        txtObs.setPromptText("Observaciones opcionales...");
        txtObs.getStyleClass().add("input-login");

        grid.add(lblC, 0, 0);       grid.add(hbCliente, 1, 0);
        grid.add(lblP, 0, 1);       grid.add(hbPresupuesto, 1, 1);
        grid.add(lblFecha, 0, 2);   grid.add(fechaPago, 1, 2);
        grid.add(lblM, 0, 3);       grid.add(txtMonto, 1, 3);
        grid.add(lblMon, 0, 4);     grid.add(comboMoneda, 1, 4);
        grid.add(lblCot, 0, 5);     grid.add(txtCotizacion, 1, 5);
        grid.add(lblF, 0, 6);       grid.add(comboForma, 1, 6);
        grid.add(lblO, 0, 7);       grid.add(txtObs, 1, 7);

        Button btnGuardar = new Button("REGISTRAR PAGO Y GENERAR RECIBO");
        btnGuardar.getStyleClass().add("boton-editar-tabla");
        btnGuardar.setPadding(new Insets(12, 25, 12, 25));

        btnGuardar.setOnAction(e -> 
        {
            Cliente cliente = comboClientes.getValue();
            Presupuesto presupuesto = comboPresupuestos.getValue();
            String montoTexto = txtMonto.getText().trim();
            String cotizacionTexto = txtCotizacion.getText().trim();
            String moneda = comboMoneda.getValue();
            String forma = comboForma.getValue();
            String obs = txtObs.getText().trim();

            boolean esValido = true;
            double monto = 0;
            double cotizacion = 1;
            double importePesos = 0;

            if (cliente == null || presupuesto == null || fechaPago.getValue() == null) 
            {
                mostrarMensaje("❌ Seleccione cliente y presupuesto", true);
                esValido = false;
            }
            else if (Validadores.estaVacio(montoTexto) || !Validadores.esNumero(montoTexto)) 
            {
                mostrarMensaje("❌ Ingrese un monto numérico válido", true);
                esValido = false;
            }
            else
            {
                monto = Double.parseDouble(montoTexto);
                if (!Validadores.esMayorACero(monto)) 
                {
                    mostrarMensaje("❌ El monto debe ser mayor a 0", true);
                    esValido = false;
                }
            }

            if (esValido && moneda.equals("Dolares")) 
            {
                if (Validadores.estaVacio(cotizacionTexto) || !Validadores.esNumero(cotizacionTexto)) 
                {
                    mostrarMensaje("❌ Ingrese una cotización válida", true);
                    esValido = false;
                }
                else
                {
                    cotizacion = Double.parseDouble(cotizacionTexto);
                }
            }

            if (esValido) 
            {
                importePesos = monto * cotizacion;
                if (importePesos > presupuesto.getSaldoPresupuesto()) 
                {
                    mostrarMensaje("❌ El pago supera el saldo del presupuesto", true);
                    esValido = false;
                }
            }

            if (esValido) 
            {
                BasePago base = new BasePago();
                String rutaPdf = base.registrarPago(presupuesto.getIdPresupuesto(), monto, moneda,
                        cotizacion, importePesos, forma, obs, fechaPago.getValue().toString());

                Alert alertPdf = new Alert(Alert.AlertType.CONFIRMATION);
                alertPdf.setTitle("Éxito");
                alertPdf.setHeaderText("Pago registrado correctamente");
                alertPdf.setContentText("¿Desea abrir el recibo en PDF?");
                ButtonType btnSi = new ButtonType("Sí, abrir");
                ButtonType btnNo = new ButtonType("No, volver");
                alertPdf.getButtonTypes().setAll(btnSi, btnNo);

                var res = alertPdf.showAndWait();
                if (res.isPresent() && res.get() == btnSi) 
                {
                    abrirArchivoPDF(rutaPdf);
                }
                rootPrincipal.setCenter(new ListaPagos(rootPrincipal));
            }
        });

        this.getChildren().addAll(lblTitulo, new Separator(), grid, btnGuardar, lblMensaje);
    }

    private Button crearBotonPlus() 
    {
        Button b = new Button("+");
        b.setStyle("-fx-font-weight: bold; -fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-size: 16px;");
        b.setCursor(javafx.scene.Cursor.HAND);
        return b;
    }

    private void actualizarComboClientes() 
    {
        comboClientes.getItems().clear();
        comboClientes.getItems().addAll(new BaseCliente().listarClientesActivos());
    }

    private void actualizarComboPresupuestos() 
    {
        Cliente c = comboClientes.getValue();
        comboPresupuestos.getItems().clear();
        if (c != null) 
        {
            var lista = new BasePresupuesto().listarPendientesPorCliente(c.getIdCliente());
            if (lista.isEmpty())
            {
                comboPresupuestos.setPromptText("Sin presupuestos pendientes");
            } 
            else 
            {
                comboPresupuestos.getItems().addAll(lista);
                comboPresupuestos.setPromptText("Seleccione presupuesto...");
            }
        }
    }

    private void abrirNuevoCliente() 
    {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Aluglass - Nuevo Cliente");
        AgregarCliente layout = new AgregarCliente(stage);
        Scene scene = new Scene(layout);
        try 
        { 
            scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm()); 
        } 
        catch (Exception e) {}
        stage.setScene(scene);
        stage.showAndWait();

        actualizarComboClientes();
        if (layout.getClienteCreado() != null) 
        {
            for (Cliente c : comboClientes.getItems()) 
            {
                if (c.getNombre().equals(layout.getClienteCreado().getNombre())) 
                {
                    comboClientes.setValue(c);
                    actualizarComboPresupuestos();
                    break;
                }
            }
        }
    }

    private void abrirNuevoPresupuesto() 
    {
        Cliente clienteActual = comboClientes.getValue();
        if (clienteActual == null) 
        {
            mostrarMensaje("⚠️ Primero debe seleccionar un cliente", true);
        }
        else
        {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Aluglass - Nuevo Presupuesto para " + clienteActual.getNombre());
            RegistrarPresupuesto layout = new RegistrarPresupuesto(stage, clienteActual);
            Scene scene = new Scene(layout);
            try 
            { 
                scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm()); 
            } 
            catch (Exception e) {}

            stage.setScene(scene);
            stage.showAndWait();
            actualizarComboPresupuestos();

            if (layout.getPresupuestoCreado() != null) 
            {
                String descNueva = layout.getPresupuestoCreado().getDescripcion();
                for (Presupuesto p : comboPresupuestos.getItems()) 
                {
                    if (p.getDescripcion().equals(descNueva)) 
                    {
                        comboPresupuestos.setValue(p);
                        break;
                    }
                }
            }
        }
    }

    private void mostrarMensaje(String texto, boolean esError) 
    {
        lblMensaje.setText(texto);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #d32f2f; -fx-font-weight: bold;" : "-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
    }

    private void abrirArchivoPDF(String ruta) 
    {
        try 
        {
            File file = new File(ruta);
            if (file.exists() && Desktop.isDesktopSupported()) 
            {
                Desktop.getDesktop().open(file);
            }
        } 
        catch (Exception ex)
        {
            System.err.println("Error al abrir el PDF: " + ex.getMessage());
        }
    }
}
