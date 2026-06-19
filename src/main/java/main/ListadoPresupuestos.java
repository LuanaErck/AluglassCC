package main;

import base.BasePresupuesto;
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

public class ListadoPresupuestos extends VBox 
{
    private TableView<Presupuesto> tabla;
    private ObservableList<Presupuesto> masterData;
    private FilteredList<Presupuesto> filteredData;
    private TextField txtBusquedaCliente;
    private ComboBox<String> comboFiltroEstado;
    
    // VARIABLES DE PAGINACIÓN
    private Pagination paginador;
    private final int FILAS_POR_PAGINA = 15;

    public ListadoPresupuestos(BorderPane rootPrincipal) 
    {
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(15);
        this.setPadding(new Insets(20));

        Label lblTitulo = new Label("LISTADO DE PRESUPUESTOS");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        // SECCIÓN DE FILTROS 
        HBox hbFiltros = new HBox(15);
        hbFiltros.setAlignment(Pos.CENTER_LEFT);
        hbFiltros.setPadding(new Insets(5, 0, 10, 0));

        txtBusquedaCliente = new TextField();
        txtBusquedaCliente.setPromptText("Buscar por cliente...");
        txtBusquedaCliente.setPrefWidth(250);
        txtBusquedaCliente.getStyleClass().add("input-login");

        comboFiltroEstado = new ComboBox<>();
        comboFiltroEstado.getItems().addAll("Todos", "Pendiente", "Pagado"); 
        comboFiltroEstado.setValue("Todos");
        comboFiltroEstado.setPrefWidth(150);

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.getStyleClass().add("boton-editar-tabla");
        btnLimpiar.setOnAction(e -> 
        {
            txtBusquedaCliente.clear();
            comboFiltroEstado.setValue("Todos");
        });

        hbFiltros.getChildren().addAll(
            new Label("Cliente:"), txtBusquedaCliente, 
            new Label("Estado:"), comboFiltroEstado, 
            btnLimpiar
        );

        // TABLA E INICIALIZACIÓN
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas(rootPrincipal);

        // CARGA DE DATOS 
        masterData = FXCollections.observableArrayList(new BasePresupuesto().listarTodos());
        filteredData = new FilteredList<>(masterData, p -> true);

        // PAGINADOR 
        paginador = new Pagination();
        VBox.setVgrow(paginador, Priority.ALWAYS);
        paginador.setPageFactory(this::crearPagina);

        // Listeners para filtros
        txtBusquedaCliente.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        comboFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        actualizarCantidadPaginas();

        this.getChildren().addAll(lblTitulo, new Separator(), hbFiltros, paginador);
    }

    private TableView<Presupuesto> crearPagina(int pageIndex) 
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
        String cliente = txtBusquedaCliente.getText().toLowerCase().trim();
        String estado = comboFiltroEstado.getValue();

        filteredData.setPredicate(p -> 
        {
            boolean coincideCliente = cliente.isEmpty() || 
                                     p.getNombreCliente().toLowerCase().contains(cliente);
            
            boolean coincideEstado = estado.equals("Todos") || 
                                     p.getEstado().equalsIgnoreCase(estado);
            
            return coincideCliente && coincideEstado;
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
        TableColumn<Presupuesto, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idPresupuesto"));
        colId.setMaxWidth(60);

        TableColumn<Presupuesto, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colCliente.setPrefWidth(200);

        TableColumn<Presupuesto, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<Presupuesto, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        configurarColumnaMoneda(colTotal);

        TableColumn<Presupuesto, Double> colSaldo = new TableColumn<>("Saldo Pendiente");
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldoPresupuesto"));
        
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
                    if (valor > 0) setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Presupuesto, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // COLUMNA DE ACCIONES UNIFICADA
        TableColumn<Presupuesto, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() 
        {
            private final Button btnVer = new Button("Detalle");
            private final Button btnEdit = new Button("Editar");
            private final HBox caja = new HBox(8, btnVer, btnEdit);
            {
                caja.setAlignment(Pos.CENTER);
                btnVer.getStyleClass().add("boton-editar-tabla");
                btnEdit.getStyleClass().add("boton-editar-tabla");

                btnVer.setOnAction(event ->
                {
                    Presupuesto p = getTableView().getItems().get(getIndex());
                    rootPrincipal.setCenter(new DetallePresupuesto(rootPrincipal, p.getIdPresupuesto(), p.getIdCliente(), "presupuestos"));
                });

                btnEdit.setOnAction(event -> 
                {
                    Presupuesto p = getTableView().getItems().get(getIndex());
                    if(p.getSaldoPresupuesto() != p.getTotal())
                    {
                        new Alert(Alert.AlertType.WARNING, "No se puede editar un presupuesto que ya tiene pagos.").show();
                    } 
                    else 
                    {
                        new EditarPresupuesto(p, () -> 
                        {
                            masterData.setAll(new BasePresupuesto().listarTodos());
                            aplicarFiltros();
                        }).show();
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

        tabla.getColumns().addAll(colId, colCliente, colFecha, colTotal, colSaldo, colEstado, colAcciones);
    }

    private void configurarColumnaMoneda(TableColumn<Presupuesto, Double> columna) 
    {
        columna.setCellFactory(col -> new TableCell<>() 
        {
            @Override
            protected void updateItem(Double valor, boolean empty) 
            {
                super.updateItem(valor, empty);
                if(empty || valor == null) setText(null);
                else 
                {
                    setText(NumberFormat.getCurrencyInstance(new Locale("es","AR")).format(valor));
                }
            }
        });
    }
}