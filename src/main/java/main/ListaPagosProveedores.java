package main;

import base.BasePagoProveedor;
import clases.PagoProveedor;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class ListaPagosProveedores extends VBox 
{

    private static final int FILAS = 15;
    private final TableView<PagoProveedor> tabla = new TableView<>();
    private final TextField buscar = new TextField();
    private final ComboBox<String> forma = new ComboBox<>();
    private final Pagination paginas = new Pagination();
    private final FilteredList<PagoProveedor> filtrados;

    public ListaPagosProveedores() 
    {
        getStyleClass().add("contenedor-pantalla");
        setSpacing(15);
        setPadding(new Insets(20));

        Label titulo = new Label("HISTORIAL DE PAGOS A PROVEEDORES");
        titulo.getStyleClass().add("titulo-pantalla");

        filtrados = new FilteredList<>(
            FXCollections.observableArrayList(new BasePagoProveedor().listarTodos()), 
            p -> true
        );

        buscar.setPromptText("Buscar por proveedor...");
        buscar.setPrefWidth(230);
        buscar.getStyleClass().add("input-login");
        buscar.textProperty().addListener((o, a, n) -> filtrar());

        forma.getItems().addAll("Todas", "Efectivo", "Transferencia", "Cheque", "Tarjeta");
        forma.setValue("Todas");
        forma.valueProperty().addListener((o, a, n) -> filtrar());

        Button limpiar = new Button("Limpiar");
        limpiar.getStyleClass().add("boton-editar-tabla");
        limpiar.setOnAction(e -> 
        {
            buscar.clear();
            forma.setValue("Todas");
        });

        HBox filtros = new HBox(
            15, 
            new Label("Proveedor:"), 
            buscar, 
            new Label("Forma:"), 
            forma, 
            limpiar
        );
        filtros.setAlignment(Pos.CENTER_LEFT);

        configurarTabla();
        paginas.setPageFactory(this::pagina);
        actualizar();

        getChildren().addAll(titulo, new Separator(), filtros, paginas);
    }

    private void configurarTabla()
    {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<PagoProveedor, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("idPagoProveedor"));
        id.setMaxWidth(60);

        TableColumn<PagoProveedor, String> prov = new TableColumn<>("Proveedor");
        prov.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));

        TableColumn<PagoProveedor, String> fecha = new TableColumn<>("Fecha");
        fecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));

        TableColumn<PagoProveedor, Double> imp = new TableColumn<>("Importe");
        imp.setCellValueFactory(new PropertyValueFactory<>("importe"));
        imp.setCellFactory(c -> new TableCell<>() 
        {
            @Override
            protected void updateItem(Double v, boolean e)
            {
                super.updateItem(v, e);
                setText(e || v == null ? null : NumberFormat.getCurrencyInstance(new Locale("es", "AR")).format(v));
            }
        });

        TableColumn<PagoProveedor, String> f = new TableColumn<>("Forma");
        f.setCellValueFactory(new PropertyValueFactory<>("formaPago"));

        TableColumn<PagoProveedor, String> obs = new TableColumn<>("Observaciones");
        obs.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        tabla.getColumns().addAll(id, prov, fecha, imp, f, obs);
    }

    private TableView<PagoProveedor> pagina(int i) 
    {
        int d = i * FILAS;
        int h = Math.min(d + FILAS, filtrados.size());
        tabla.setItems(
            d < filtrados.size() 
                ? FXCollections.observableArrayList(filtrados.subList(d, h)) 
                : FXCollections.emptyObservableList()
        );
        return tabla;
    }

    private void filtrar() 
    {
        String texto = buscar.getText().trim().toLowerCase();
        String tipo = forma.getValue();
        
        filtrados.setPredicate(p -> 
            (texto.isEmpty() || p.getNombreProveedor().toLowerCase().contains(texto)) && 
            ("Todas".equals(tipo) || p.getFormaPago().equals(tipo))
        );
        
        actualizar();
    }

    private void actualizar() 
    {
        paginas.setPageCount(Math.max(1, (int) Math.ceil((double) filtrados.size() / FILAS)));
        paginas.setCurrentPageIndex(0);
        pagina(0);
        tabla.refresh();
    }
}
