package main;

import base.BaseCliente;
import clases.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.geometry.Insets;

public class ListadoClientes extends VBox 
{
    
    private TableView<Cliente> tabla;
    private ObservableList<Cliente> masterData;
    private FilteredList<Cliente> filteredData;
    private TextField txtBusqueda;
    private ComboBox<String> comboFiltroEstado;
    
    // VARIABLES DE PAGINACIÓN
    private Pagination paginador;
    private final int FILAS_POR_PAGINA = 10;

    public ListadoClientes(BorderPane rootPrincipal) 
    {
        // Estilos iniciales
        this.getStyleClass().add("contenedor-pantalla");
        this.setSpacing(15);
        this.setPadding(new Insets(20));

        // Título
        Label lblTitulo = new Label("LISTADO DE CLIENTES");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        // SECCIÓN DE FILTROS
        HBox hbFiltros = new HBox(15);
        hbFiltros.setAlignment(Pos.CENTER_LEFT);
        hbFiltros.setPadding(new Insets(5, 0, 10, 0));

        txtBusqueda = new TextField();
        txtBusqueda.setPromptText("Buscar por nombre...");
        txtBusqueda.setPrefWidth(250);
        txtBusqueda.getStyleClass().add("input-login");

        comboFiltroEstado = new ComboBox<>();
        comboFiltroEstado.getItems().addAll("Todos", "Activo", "Inactivo");
        comboFiltroEstado.setValue("Todos");
        comboFiltroEstado.setPrefWidth(120);

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.getStyleClass().add("boton-editar-tabla");
        btnLimpiar.setOnAction(e -> 
        {
            txtBusqueda.clear();
            comboFiltroEstado.setValue("Todos");
        });

        hbFiltros.getChildren().addAll(new Label("Buscar:"), txtBusqueda, new Label("Estado:"), comboFiltroEstado, btnLimpiar);

        // INICIALIZACIÓN DE TABLA Y COLUMNAS 
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        configurarColumnas(rootPrincipal);

        // CARGA DE DATOS 
        BaseCliente base = new BaseCliente();
        masterData = FXCollections.observableArrayList(base.listarClientes());
        filteredData = new FilteredList<>(masterData, p -> true);

        // PAGINADOR 
        paginador = new Pagination();
        VBox.setVgrow(paginador, Priority.ALWAYS);
        
        // El PageFactory es el que dice qué mostrar en cada página
        paginador.setPageFactory(this::crearPagina);

        // Listeners para que el paginador reaccione a los filtros
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> actualizarFiltro());
        comboFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> actualizarFiltro());

        // Calculamos las páginas iniciales
        actualizarCantidadPaginas();

        // Ensamble final
        this.getChildren().addAll(lblTitulo, new Separator(), hbFiltros, paginador);
    }

    private TableView<Cliente> crearPagina(int pageIndex) 
    {
        int desde = pageIndex * FILAS_POR_PAGINA;
        int hasta = Math.min(desde + FILAS_POR_PAGINA, filteredData.size());

        if (desde < filteredData.size() && desde >= 0) 
        {
            // Creamos una sublista con los elementos que corresponden a la página
            tabla.setItems(FXCollections.observableArrayList(filteredData.subList(desde, hasta)));
        } 
        else
        {
            tabla.setItems(FXCollections.emptyObservableList());
        }
        return tabla;
    }

    private void actualizarFiltro() 
    {
        String nombre = txtBusqueda.getText();
        String estado = comboFiltroEstado.getValue();

        filteredData.setPredicate(cliente -> 
        {
            boolean coincideNombre = (nombre == null || nombre.isEmpty()) || 
                                     cliente.getNombre().toLowerCase().contains(nombre.toLowerCase());
            boolean coincideEstado = (estado == null || estado.equals("Todos")) || 
                                     cliente.getEstado().equalsIgnoreCase(estado);
            return coincideNombre && coincideEstado;
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
        
        // Forzamos el refresco de la página 0 y de la tabla
        crearPagina(0);
        tabla.refresh();
    }

    private void configurarColumnas(BorderPane rootPrincipal) 
    {
        // Columna ID
        TableColumn<Cliente, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colId.setMaxWidth(60);

        // Columna Nombre
        TableColumn<Cliente, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Columna Teléfono
        TableColumn<Cliente, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        
        // Columna Cuit
        TableColumn<Cliente, String> colCuit = new TableColumn<>("CUIT");
        colCuit.setCellValueFactory(new PropertyValueFactory<>("cuit"));

        // Columna Estado con formato de color
        TableColumn<Cliente, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(column -> new TableCell<Cliente, String>()
        {
            @Override
            protected void updateItem(String item, boolean empty) 
            {
                super.updateItem(item, empty);
                if (item == null || empty) 
                {
                    setText(null);
                    setStyle("");
                } 
                else
                {
                    setText(item);
                    if (item.equalsIgnoreCase("Inactivo")) 
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

        // Columna Acciones CORREGIDA para evitar desaparición de botones
        TableColumn<Cliente, Void> colAccion = new TableColumn<>("Acciones");
        colAccion.setMinWidth(180);
        colAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnCuenta = new Button("Cuenta");
            private final HBox cajaBotones = new HBox(10, btnEditar, btnCuenta);

            {
                cajaBotones.setAlignment(Pos.CENTER);
                btnEditar.getStyleClass().add("boton-editar-tabla");
                btnCuenta.getStyleClass().add("boton-editar-tabla");

                btnEditar.setOnAction(event -> 
                {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    rootPrincipal.setCenter(new ModificarCliente(rootPrincipal, cliente));
                });

                btnCuenta.setOnAction(event -> 
                {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    rootPrincipal.setCenter(new DetalleCuenta(rootPrincipal, cliente.getIdCliente(), "clientes"));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) 
            {
                super.updateItem(item, empty);
                // Si la celda está vacía o el índice es mayor a los items actuales, quitamos el gráfico
                if (empty || getIndex() >= getTableView().getItems().size())
                {
                    setGraphic(null);
                } 
                else 
                {
                    // Si hay datos, aseguramos de que el HBox esté presente
                    setGraphic(cajaBotones);
                }
            }
        });

        tabla.getColumns().addAll(colId, colNombre, colTelefono, colCuit, colEstado, colAccion);
    }
}