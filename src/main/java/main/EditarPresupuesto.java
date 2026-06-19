package main;

import base.BasePresupuesto;
import clases.Presupuesto;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utilidades.Validadores;

public class EditarPresupuesto extends Stage 
{
    public EditarPresupuesto(Presupuesto presupuesto, Runnable onUpdate) 
    {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        Label titulo = new Label("Editar Presupuesto #" + presupuesto.getIdPresupuesto());
        titulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #800000;");

        TextArea txtDescripcion = new TextArea(presupuesto.getDescripcion());
        txtDescripcion.setWrapText(true);
        
        TextField txtTotal = new TextField(String.valueOf(presupuesto.getTotal()));

        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.getStyleClass().add("boton-editar-tabla");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        btnGuardar.setOnAction(e -> 
        {
            String desc = txtDescripcion.getText().trim();
            String totalTxt = txtTotal.getText().trim();

            if(Validadores.estaVacio(desc) || Validadores.estaVacio(totalTxt) || !Validadores.esNumero(totalTxt)) 
            {
                new Alert(Alert.AlertType.WARNING, "Revise los campos.").show();
                return;
            }

            double total = Double.parseDouble(totalTxt);
            new BasePresupuesto().modificarPresupuesto(presupuesto.getIdPresupuesto(), desc, total);

            if(onUpdate != null) onUpdate.run();
            this.close();
            new Alert(Alert.AlertType.INFORMATION, "Actualizado correctamente.").show();
        });

        root.getChildren().addAll(titulo, new Label("Descripción:"), txtDescripcion, new Label("Monto Total:"), txtTotal, btnGuardar);
        
        this.setScene(new Scene(root, 350, 350));
        this.setTitle("Modificar Presupuesto");
    }
}