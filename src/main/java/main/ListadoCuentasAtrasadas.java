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

public class ListadoCuentasAtrasadas extends VBox 
{

    private TableView<CuentaCorriente> tabla;
    private ObservableList<CuentaCorriente> masterData;
    private FilteredList<CuentaCorriente> filteredData;
    private TextField txtBusquedaCliente;
    
    // VARIABLES DE PAGINACIÓN
    private Pagination paginador;
    private final int FILAS_POR_PAGINA = 15;

    public ListadoCuentasAtrasadas(BorderPane rootPrincipal) 
    {
        // Estilos estándar
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(15);
        this.setPadding(new Insets(20));

        // TÍTULO
        Label lblTitulo = new Label("CUENTAS CON SALDO PENDIENTE");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        // SECCIÓN DE FILTROS 
        HBox hbFiltros = new HBox(15);
        hbFiltros.setAlignment(Pos.CENTER_LEFT);
        hbFiltros.setPadding(new Insets(5, 0, 10, 0));

        txtBusquedaCliente = new TextField();
        txtBusquedaCliente.setPromptText("Filtrar deudor por nombre...");
        txtBusquedaCliente.setPrefWidth(300);
        txtBusquedaCliente.getStyleClass().add("input-login");

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.getStyleClass().add("boton-editar-tabla");
        btnLimpiar.setOnAction(e -> txtBusquedaCliente.clear());

        hbFiltros.getChildren().addAll(new Label("Buscar:"), txtBusquedaCliente, btnLimpiar);

        // TABLA E INICIALIZACIÓN 
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas(rootPrincipal);

        // CARGA DE DATOS 
        masterData = FXCollections.observableArrayList(new BaseCuenta().listarCuentasAtrasadas());
        filteredData = new FilteredList<>(masterData, p -> true);

        // PAGINADOR 
        paginador = new Pagination();
        VBox.setVgrow(paginador, Priority.ALWAYS);
        paginador.setPageFactory(this::crearPagina);

        // Listener para búsqueda en tiempo real
        txtBusquedaCliente.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

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

        filteredData.setPredicate(cuenta -> 
        {
            if (texto.isEmpty()) return true;
            return cuenta.getNombreCliente().toLowerCase().contains(texto);
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
        // Columna Cliente
        TableColumn<CuentaCorriente, String> colNombre = new TableColumn<>("Cliente");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colNombre.setPrefWidth(250);

        // Columna Saldo
        TableColumn<CuentaCorriente, Double> colSaldo = new TableColumn<>("Saldo Deuda");
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
                    setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;"); // Rojo Alerta
                }
            }
        });

        // Columna Estado
        TableColumn<CuentaCorriente, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
        colEstado.setPrefWidth(150);

        // Columna Acción: Detalle
        TableColumn<CuentaCorriente, Void> colAccion = new TableColumn<>("Acción");
        colAccion.setCellFactory(param -> new TableCell<>() 
        {
            private final Button btnDetalle = new Button("Ver");
            private final HBox caja = new HBox(btnDetalle);
            {
                caja.setAlignment(Pos.CENTER);
                btnDetalle.getStyleClass().add("boton-editar-tabla");
                btnDetalle.setOnAction(e -> 
                {
                    CuentaCorriente seleccionada = getTableView().getItems().get(getIndex());
                    // Redirigir al historial para gestionar el cobro
                    rootPrincipal.setCenter(new DetalleCuenta(rootPrincipal, seleccionada.getIdCliente(), "atrasadas"));
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

        tabla.getColumns().addAll(colNombre, colSaldo, colEstado, colAccion);
        tabla.setPlaceholder(new Label("No hay cuentas con saldos pendientes"));
    }
}