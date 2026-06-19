package main;

import base.BaseCuenta;
import clases.CuentaCorriente;
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

public class ListadoCuentas extends VBox 
{

    private TableView<CuentaCorriente> tabla;
    private ObservableList<CuentaCorriente> masterData;
    private FilteredList<CuentaCorriente> filteredData;
    private TextField txtBusquedaCliente;
    private ComboBox<String> comboFiltroSaldo;
    
    // VARIABLES DE PAGINACIÓN
    private Pagination paginador;
    private final int FILAS_POR_PAGINA = 15;

    public ListadoCuentas(BorderPane rootPrincipal) 
    {
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(15);
        this.setPadding(new Insets(20));

        Label lblTitulo = new Label("CUENTAS CORRIENTES");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        // SECCIÓN DE FILTROS 
        HBox hbFiltros = new HBox(15);
        hbFiltros.setAlignment(Pos.CENTER_LEFT);
        hbFiltros.setPadding(new Insets(5, 0, 10, 0));

        txtBusquedaCliente = new TextField();
        txtBusquedaCliente.setPromptText("Buscar por cliente...");
        txtBusquedaCliente.setPrefWidth(250);
        txtBusquedaCliente.getStyleClass().add("input-login");

        comboFiltroSaldo = new ComboBox<>();
        comboFiltroSaldo.getItems().addAll("Todos", "Con Deuda", "Al día");
        comboFiltroSaldo.setValue("Todos");
        comboFiltroSaldo.setPrefWidth(150);

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.getStyleClass().add("boton-editar-tabla");
        btnLimpiar.setOnAction(e -> 
        {
            txtBusquedaCliente.clear();
            comboFiltroSaldo.setValue("Todos");
        });

        hbFiltros.getChildren().addAll(
            new Label("Cliente:"), txtBusquedaCliente, 
            new Label("Estado Saldo:"), comboFiltroSaldo, 
            btnLimpiar
        );

        // TABLA E INICIALIZACIÓN 
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas(rootPrincipal);

        // CARGA DE DATOS 
        masterData = FXCollections.observableArrayList(new BaseCuenta().listarCuentas());
        filteredData = new FilteredList<>(masterData, p -> true);

        // PAGINADOR 
        paginador = new Pagination();
        VBox.setVgrow(paginador, Priority.ALWAYS);
        paginador.setPageFactory(this::crearPagina);

        // Listeners
        txtBusquedaCliente.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        comboFiltroSaldo.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        actualizarCantidadPaginas();

        this.getChildren().addAll(lblTitulo, new Separator(), hbFiltros, paginador);
    }

    private TableView<CuentaCorriente> crearPagina(int pageIndex) 
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
        String texto = txtBusquedaCliente.getText().toLowerCase().trim();
        String estadoSaldo = comboFiltroSaldo.getValue();

        filteredData.setPredicate(cuenta -> 
        {
            boolean coincideNombre = texto.isEmpty() || 
                                     cuenta.getNombreCliente().toLowerCase().contains(texto);
            
            boolean coincideSaldo = true;
            if (estadoSaldo.equals("Con Deuda")) 
            {
                coincideSaldo = cuenta.getSaldoActual() > 0;
            } 
            else if (estadoSaldo.equals("Al día"))
            {
                coincideSaldo = cuenta.getSaldoActual() <= 0;
            }

            return coincideNombre && coincideSaldo;
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

    private void configurarColumnas(BorderPane rootPrincipal) 
    {
        TableColumn<CuentaCorriente, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idCuenta"));
        colId.setMaxWidth(60);

        TableColumn<CuentaCorriente, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colCliente.setPrefWidth(250);

        TableColumn<CuentaCorriente, Double> colSaldo = new TableColumn<>("Saldo Actual");
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldoActual"));
        colSaldo.setPrefWidth(150);
        
        colSaldo.setCellFactory(col -> new TableCell<>() 
        {
            @Override
            protected void updateItem(Double valor, boolean empty) 
            {
                super.updateItem(valor, empty);
                if (empty || valor == null) 
                {
                    setText(null);
                    setStyle("");
                } 
                else 
                {
                    NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
                    setText(formato.format(valor));
                    if (valor > 0) 
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

        TableColumn<CuentaCorriente, Void> colAccion = new TableColumn<>("Detalle");
        colAccion.setCellFactory(param -> new TableCell<>() 
        {
            private final Button btnDetalle = new Button("Ver Historial");
            private final HBox caja = new HBox(btnDetalle);
            {
                caja.setAlignment(Pos.CENTER);
                btnDetalle.getStyleClass().add("boton-editar-tabla");
                btnDetalle.setOnAction(e -> 
                {
                    CuentaCorriente seleccionada = getTableView().getItems().get(getIndex());
                    rootPrincipal.setCenter(new DetalleCuenta(rootPrincipal, seleccionada.getIdCliente(), "cuentas"));
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

        tabla.getColumns().addAll(colId, colCliente, colSaldo, colAccion);
    }
}