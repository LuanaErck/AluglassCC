package main;

import base.BaseProveedor;
import clases.Proveedor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ListadoProveedores extends VBox 
{

    private static final int FILAS_POR_PAGINA = 10;

    private final TableView<Proveedor> tabla = new TableView<>();
    private final TextField txtBusqueda = new TextField();
    private final ComboBox<String> comboFiltroEstado = new ComboBox<>();
    private final Pagination paginador = new Pagination();
    private final FilteredList<Proveedor> proveedoresFiltrados;

    public ListadoProveedores() 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(15);
        setPadding(new Insets(20));

        Label lblTitulo = new Label("LISTADO DE PROVEEDORES");
        lblTitulo.getStyleClass().add("titulo-pantalla");

        ObservableList<Proveedor> proveedores = FXCollections.observableArrayList(
                new BaseProveedor().listarProveedores());
        proveedoresFiltrados = new FilteredList<>(proveedores, proveedor -> true);

        configurarFiltros();
        configurarTabla();

        paginador.setPageFactory(this::crearPagina);
        actualizarCantidadPaginas();

        getChildren().addAll(lblTitulo, new Separator(), crearBarraFiltros(), paginador);
    }

    private void configurarFiltros() 
    {
        txtBusqueda.setPromptText("Buscar por nombre...");
        txtBusqueda.setPrefWidth(250);
        txtBusqueda.getStyleClass().add("input-login");
        txtBusqueda.textProperty().addListener((obs, anterior, actual) -> actualizarFiltro());

        comboFiltroEstado.getItems().addAll("Todos", "Activo", "Inactivo");
        comboFiltroEstado.setValue("Todos");
        comboFiltroEstado.setPrefWidth(120);
        comboFiltroEstado.valueProperty().addListener((obs, anterior, actual) -> actualizarFiltro());
    }

    private HBox crearBarraFiltros() 
    {
        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.getStyleClass().add("boton-editar-tabla");
        btnLimpiar.setOnAction(e -> 
        {
            txtBusqueda.clear();
            comboFiltroEstado.setValue("Todos");
        });

        HBox filtros = new HBox(15, new Label("Buscar:"), txtBusqueda,
                new Label("Estado:"), comboFiltroEstado, btnLimpiar);
        filtros.setAlignment(Pos.CENTER_LEFT);
        filtros.setPadding(new Insets(5, 0, 10, 0));
        return filtros;
    }

    private void configurarTabla() 
    {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<Proveedor, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colId.setMaxWidth(60);

        TableColumn<Proveedor, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Proveedor, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        TableColumn<Proveedor, String> colCuit = new TableColumn<>("CUIT");
        colCuit.setCellValueFactory(new PropertyValueFactory<>("cuit"));

        TableColumn<Proveedor, String> colCbuAlias = new TableColumn<>("CBU / Alias");
        colCbuAlias.setCellValueFactory(new PropertyValueFactory<>("cbuAlias"));

        TableColumn<Proveedor, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(columna -> new TableCell<>() 
        {
            @Override
            protected void updateItem(String estado, boolean empty)
            {
                super.updateItem(estado, empty);
                if (empty || estado == null) 
                {
                    setText(null);
                    setStyle("");
                }
                else 
                {
                    setText(estado);
                    setStyle(estado.equalsIgnoreCase("Inactivo")
                            ? "-fx-text-fill: #d32f2f; -fx-font-weight: bold;"
                            : "-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                }
            }
        });

        tabla.getColumns().addAll(colId, colNombre, colTelefono, colCuit, colCbuAlias, colEstado);
    }

    private TableView<Proveedor> crearPagina(int indicePagina) 
    {
        int desde = indicePagina * FILAS_POR_PAGINA;
        int hasta = Math.min(desde + FILAS_POR_PAGINA, proveedoresFiltrados.size());
        tabla.setItems(desde < proveedoresFiltrados.size()
                ? FXCollections.observableArrayList(proveedoresFiltrados.subList(desde, hasta))
                : FXCollections.emptyObservableList());
        return tabla;
    }

    private void actualizarFiltro() 
    {
        String nombre = txtBusqueda.getText().trim().toLowerCase();
        String estado = comboFiltroEstado.getValue();
        proveedoresFiltrados.setPredicate(proveedor ->
                (nombre.isEmpty() || proveedor.getNombre().toLowerCase().contains(nombre))
                && ("Todos".equals(estado) || proveedor.getEstado().equalsIgnoreCase(estado)));
        actualizarCantidadPaginas();
    }

    private void actualizarCantidadPaginas()
    {
        int paginas = Math.max(1, (int) Math.ceil((double) proveedoresFiltrados.size() / FILAS_POR_PAGINA));
        paginador.setPageCount(paginas);
        paginador.setCurrentPageIndex(0);
        crearPagina(0);
        tabla.refresh();
    }
}
