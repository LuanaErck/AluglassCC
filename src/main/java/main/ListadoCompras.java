package main;

import base.BaseCompra;
import clases.Compra;
import java.text.NumberFormat;
import java.util.Locale;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ListadoCompras extends VBox 
{

    private static final int FILAS_POR_PAGINA = 15;
    private final TableView<Compra> tabla = new TableView<>();
    private final TextField txtProveedor = new TextField();
    private final ComboBox<String> comboEstado = new ComboBox<>();
    private final Pagination paginador = new Pagination();
    private final FilteredList<Compra> comprasFiltradas;

    public ListadoCompras(BorderPane rootPrincipal) 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(15);
        setPadding(new Insets(20));

        Label titulo = new Label("HISTORIAL DE COMPRAS");
        titulo.getStyleClass().add("titulo-pantalla");

        ObservableList<Compra> compras = FXCollections.observableArrayList(new BaseCompra().listarCompras());
        comprasFiltradas = new FilteredList<>(compras, compra -> true);
        configurarFiltros();
        configurarTabla(rootPrincipal);
        paginador.setPageFactory(this::crearPagina);
        actualizarCantidadPaginas();

        getChildren().addAll(titulo, new Separator(), crearBarraFiltros(), paginador);
    }

    private void configurarFiltros() 
    {
        txtProveedor.setPromptText("Buscar por proveedor...");
        txtProveedor.setPrefWidth(250);
        txtProveedor.getStyleClass().add("input-login");
        txtProveedor.textProperty().addListener((obs, anterior, actual) -> aplicarFiltros());

        comboEstado.getItems().addAll("Todos", "Pendiente", "Pagada, Cancelada");
        comboEstado.setValue("Todos");
        comboEstado.setPrefWidth(130);
        comboEstado.valueProperty().addListener((obs, anterior, actual) -> aplicarFiltros());
    }

    private HBox crearBarraFiltros() 
    {
        Button limpiar = new Button("Limpiar");
        limpiar.getStyleClass().add("boton-editar-tabla");
        limpiar.setOnAction(e ->
        {
            txtProveedor.clear();
            comboEstado.setValue("Todos");
        });
        HBox filtros = new HBox(15, new Label("Proveedor:"), txtProveedor,
                new Label("Estado:"), comboEstado, limpiar);
        filtros.setAlignment(Pos.CENTER_LEFT);
        filtros.setPadding(new Insets(5, 0, 10, 0));
        return filtros;
    }

    private void configurarTabla(BorderPane rootPrincipal)
    {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<Compra, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        id.setMaxWidth(60);
        
        TableColumn<Compra, String> proveedor = new TableColumn<>("Proveedor");
        proveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
      
        TableColumn<Compra, String> factura = new TableColumn<>("Factura");
        factura.setCellValueFactory(new PropertyValueFactory<>("nroFactura"));
       
        TableColumn<Compra, String> emision = new TableColumn<>("Emisión");
        emision.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        
        TableColumn<Compra, String> vencimiento = new TableColumn<>("Vencimiento");
        vencimiento.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
       
        TableColumn<Compra, Double> importe = new TableColumn<>("Importe");
        importe.setCellValueFactory(new PropertyValueFactory<>("importe"));
        importe.setCellFactory(columna -> columnaMoneda());
        
        TableColumn<Compra, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        estado.setCellFactory(columna -> columnaEstado());

        TableColumn<Compra, Void> acciones = new TableColumn<>("Acciones");
        acciones.setCellFactory(columna -> new TableCell<>() 
        {
            private final Button detalle = new Button("Detalle");
            {
                detalle.getStyleClass().add("boton-editar-tabla");
                detalle.setOnAction(e -> 
                {
                    Compra compra = getTableView().getItems().get(getIndex());
                    rootPrincipal.setCenter(new DetalleCompra(rootPrincipal, compra.getIdCompra()));
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) 
            {
                super.updateItem(item, empty);
                setGraphic(empty || getIndex() >= getTableView().getItems().size() ? null : detalle);
            }
        });

        tabla.getColumns().addAll(id, proveedor, factura, emision, vencimiento, importe, estado, acciones);
    }

    private TableCell<Compra, Double> columnaMoneda()
    {
        return new TableCell<>()
        {
            private final NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
            @Override
            protected void updateItem(Double valor, boolean empty)
            {
                super.updateItem(valor, empty);
                setText(empty || valor == null ? null : formato.format(valor));
            }
        };
    }

    private TableCell<Compra, String> columnaEstado() 
    {
        return new TableCell<>() 
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
                    setStyle("Pendiente".equalsIgnoreCase(estado)
                            ? "-fx-text-fill: #d32f2f; -fx-font-weight: bold;"
                            : "-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                }
            }
        };
    }

    private TableView<Compra> crearPagina(int indice)
    {
        int desde = indice * FILAS_POR_PAGINA;
        int hasta = Math.min(desde + FILAS_POR_PAGINA, comprasFiltradas.size());
        tabla.setItems(desde < comprasFiltradas.size()
                ? FXCollections.observableArrayList(comprasFiltradas.subList(desde, hasta))
                : FXCollections.emptyObservableList());
        return tabla;
    }

    private void aplicarFiltros() 
    {
        String proveedor = txtProveedor.getText().trim().toLowerCase();
        String estado = comboEstado.getValue();
        comprasFiltradas.setPredicate(compra ->
                (proveedor.isEmpty() || compra.getNombreProveedor().toLowerCase().contains(proveedor))
                && ("Todos".equals(estado) || compra.getEstado().equalsIgnoreCase(estado)));
        actualizarCantidadPaginas();
    }

    private void actualizarCantidadPaginas()
    {
        int paginas = Math.max(1, (int) Math.ceil((double) comprasFiltradas.size() / FILAS_POR_PAGINA));
        paginador.setPageCount(paginas);
        paginador.setCurrentPageIndex(0);
        crearPagina(0);
        tabla.refresh();
    }
}
