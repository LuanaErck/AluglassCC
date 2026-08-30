package main;

import base.BasePago;
import base.BaseRecibo;
import clases.Pago;
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

public class ListaPagos extends VBox 
{

    private TableView<Pago> tabla;
    private ObservableList<Pago> masterData;
    private FilteredList<Pago> filteredData;
    private TextField txtBusquedaCliente;
    private ComboBox<String> comboFormaPago;
    private ComboBox<String> comboMoneda;
    
    // VARIABLES DE PAGINACIÓN
    private Pagination paginador;
    private final int FILAS_POR_PAGINA = 15; // Un poco más que clientes por ser historial

    public ListaPagos(BorderPane rootPrincipal) 
    {
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(15);
        this.setPadding(new Insets(20));

        Label lblTitulo = new Label("HISTORIAL GLOBAL DE COBROS");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        // SECCIÓN DE FILTROS
        HBox hbFiltros = new HBox(15);
        hbFiltros.setAlignment(Pos.CENTER_LEFT);
        hbFiltros.setPadding(new Insets(5, 0, 10, 0));

        txtBusquedaCliente = new TextField();
        txtBusquedaCliente.setPromptText("Buscar por cliente...");
        txtBusquedaCliente.setPrefWidth(200);
        txtBusquedaCliente.getStyleClass().add("input-login");

        comboFormaPago = new ComboBox<>();
        comboFormaPago.getItems().addAll("Todas las Formas", "Efectivo", "Transferencia", "Tarjeta", "Cheque");
        comboFormaPago.setValue("Todas las Formas");

        comboMoneda = new ComboBox<>();
        comboMoneda.getItems().addAll("Cualquier Moneda", "Pesos", "Dolares"); 
        comboMoneda.setValue("Cualquier Moneda");

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.getStyleClass().add("boton-editar-tabla");
        btnLimpiar.setOnAction(e -> 
        {
            txtBusquedaCliente.clear();
            comboFormaPago.setValue("Todas las Formas");
            comboMoneda.setValue("Cualquier Moneda");
        });

        hbFiltros.getChildren().addAll(
            new Label("Cliente:"), txtBusquedaCliente, 
            new Label("Forma:"), comboFormaPago, 
            new Label("Moneda:"), comboMoneda, 
            btnLimpiar
        );

        // TABLA E INICIALIZACIÓN
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas();

        // CARGA DE DATOS Y FILTROS 
        masterData = FXCollections.observableArrayList(new BasePago().listarTodos());
        filteredData = new FilteredList<>(masterData, p -> true);

        //  PAGINADOR
        paginador = new Pagination();
        VBox.setVgrow(paginador, Priority.ALWAYS);
        paginador.setPageFactory(this::crearPagina);

        // Listeners para filtros
        txtBusquedaCliente.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        comboFormaPago.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        comboMoneda.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        actualizarCantidadPaginas();

        this.getChildren().addAll(lblTitulo, new Separator(), hbFiltros, paginador);
    }

    private TableView<Pago> crearPagina(int pageIndex) 
    {
        int desde = pageIndex * FILAS_POR_PAGINA;
        int hasta = Math.min(desde + FILAS_POR_PAGINA, filteredData.size());

        if (desde < filteredData.size() && desde >= 0) 
        {
            tabla.setItems(FXCollections.observableArrayList(filteredData.subList(desde, hasta)));
        } 
        else 
        {
            tabla.setItems(FXCollections.emptyObservableList());
        }
        return tabla;
    }

    private void aplicarFiltros()
    {
        String texto = txtBusquedaCliente.getText().toLowerCase();
        String forma = comboFormaPago.getValue();
        String moneda = comboMoneda.getValue();

        filteredData.setPredicate(pago -> 
        {
            boolean coincideNombre = texto.isEmpty() || pago.getNombreCliente().toLowerCase().contains(texto);
            boolean coincideForma = forma.equals("Todas las Formas") || pago.getFormaPago().equals(forma);
            boolean coincideMoneda = moneda.equals("Cualquier Moneda") || pago.getMoneda().equals(moneda);

            return coincideNombre && coincideForma && coincideMoneda;
        });

        actualizarCantidadPaginas();
    }

    private void actualizarCantidadPaginas() 
    {
        int totalElementos = filteredData.size();
        int paginas = (int) Math.ceil((double) totalElementos / FILAS_POR_PAGINA);
        if (paginas <= 0) paginas = 1;
        
        paginador.setPageCount(paginas);
        paginador.setCurrentPageIndex(0);
        crearPagina(0);
        tabla.refresh();
    }

    private void configurarColumnas() 
    {
        TableColumn<Pago, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idPago"));
        colId.setMaxWidth(60);

        TableColumn<Pago, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colCliente.setPrefWidth(180);

        TableColumn<Pago, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));

        TableColumn<Pago, Double> colImporte = new TableColumn<>("Importe");
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importePesos"));
        colImporte.setCellFactory(column -> new TableCell<>() 
        {
            @Override
            protected void updateItem(Double monto, boolean empty) 
            {
                super.updateItem(monto, empty);
                if (empty || monto == null)
                {
                    setText(null);
                    setStyle("");
                } 
                else 
                {
                    NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
                    setText(formato.format(monto));
                    setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Pago, String> colForma = new TableColumn<>("Forma");
        colForma.setCellValueFactory(new PropertyValueFactory<>("formaPago"));

        TableColumn<Pago, String> colMoneda = new TableColumn<>("Moneda");
        colMoneda.setCellValueFactory(new PropertyValueFactory<>("moneda"));

        TableColumn<Pago, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(column -> new TableCell<>() 
        {
            @Override
            protected void updateItem(String item, boolean empty) 
            {
                super.updateItem(item, empty);
                if (empty || item == null) 
                {
                    setText(null);
                    setStyle("");
                } 
                else
                {
                    setText(item);
                    if (item.equalsIgnoreCase("Anulado")) 
                    {
                        setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                    } 
                    else 
                    {
                        setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // COLUMNA RECIBO CON CORRECCIÓN DE RENDERIZADO (para que no desaparezcan los botones)
        TableColumn<Pago, Void> colAccionPago = new TableColumn<>("Recibo");
        colAccionPago.setCellFactory(param -> new TableCell<>()
        {
            private final Button btnVerRecibo = new Button("Ver PDF");
            private final HBox caja = new HBox(btnVerRecibo);
            {
                caja.setAlignment(Pos.CENTER);
                btnVerRecibo.getStyleClass().add("boton-editar-tabla");
                btnVerRecibo.setOnAction(e -> 
                {
                    Pago pago = getTableView().getItems().get(getIndex());
                    String ruta = new BaseRecibo().obtenerRutaPdfPorPago(pago.getIdPago());
                    if (ruta != null) 
                    {
                        try 
                        {
                            java.awt.Desktop.getDesktop().open(new java.io.File(ruta));
                        } 
                        catch (Exception ex) 
                        {
                            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el PDF").show();
                        }
                    } 
                    else 
                    {
                        new Alert(Alert.AlertType.WARNING, "No existe archivo para este pago").show();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) 
            {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size())
                {
                    setGraphic(null);
                } 
                else 
                {
                    setGraphic(caja);
                }
            }
        });

        tabla.getColumns().addAll(colId, colCliente, colFecha, colImporte, colForma, colMoneda, colEstado, colAccionPago);
        tabla.setPlaceholder(new Label("No hay pagos que coincidan con el filtro"));
    }
}