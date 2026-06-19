package main;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import base.BaseUsuario;
import clases.Usuario;
import javafx.scene.layout.Region;
import seguridad.Encriptador;

public class VentanaRecuperacion 
{
    private BaseUsuario baseUsuario = new BaseUsuario();

    public void mostrar()
    {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.setTitle("Recuperar Acceso - Aluglass");

        VBox root = new VBox(20); 
        root.setPadding(new Insets(30)); 
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label lblTitulo = new Label("RECUPERAR CONTRASEÑA");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2196F3;"); 
        
        Label lblIndicacion = new Label("Introduce tu nombre de usuario:");
        lblIndicacion.getStyleClass().add("label-perfil");

        TextField txtUser = new TextField();
        txtUser.getStyleClass().add("input-perfil");
        txtUser.setMaxWidth(350); 

        Button btnVerificarUser = new Button("Continuar");
        btnVerificarUser.getStyleClass().add("boton-guardar-perfil");
        btnVerificarUser.setPrefWidth(350);

        // CONFIGURACIÓN DE LA PREGUNTA
        Label lblPregunta = new Label();
        lblPregunta.setWrapText(true);
        lblPregunta.setAlignment(Pos.CENTER);
        // Forzamos a que el Label no use puntos suspensivos
        lblPregunta.setMinHeight(Region.USE_PREF_SIZE); 
        lblPregunta.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-alignment: center;");
        lblPregunta.setMaxWidth(350); 
        
        TextField txtRespuesta = new TextField();
        txtRespuesta.setPromptText("Tu respuesta");
        txtRespuesta.getStyleClass().add("input-perfil");
        txtRespuesta.setMaxWidth(350);
        txtRespuesta.setVisible(false);

        // BOTÓN INTERMEDIO PARA VALIDAR RESPUESTA (PASO 2)
        Button btnValidarRespuesta = new Button("Verificar Respuesta");
        btnValidarRespuesta.getStyleClass().add("boton-guardar-perfil");
        btnValidarRespuesta.setPrefWidth(350);
        btnValidarRespuesta.setVisible(false);

        // CAMPOS DE NUEVA CONTRASEÑA (PASO 3)
        PasswordField txtNuevaPass = new PasswordField();
        txtNuevaPass.setPromptText("Nueva Contraseña");
        txtNuevaPass.getStyleClass().add("input-perfil");
        txtNuevaPass.setMaxWidth(350);
        txtNuevaPass.setVisible(false);

        Button btnFinalizar = new Button("Cambiar Contraseña");
        btnFinalizar.setVisible(false);
        btnFinalizar.getStyleClass().add("boton-guardar-perfil");
        btnFinalizar.setPrefWidth(350);

        // PASO 1: Buscar el usuario
        btnVerificarUser.setOnAction(e -> 
        {
            Usuario u = baseUsuario.buscarUsuario(txtUser.getText().trim());
            if (u != null && u.getPregunta() != null && !u.getPregunta().isEmpty()) 
            {
                lblPregunta.setText("PREGUNTA:\n" + u.getPregunta());
                lblPregunta.setStyle("-fx-text-fill: #333; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-alignment: center;");
                txtUser.setDisable(true);
                btnVerificarUser.setVisible(false);
                lblIndicacion.setVisible(false);
                
                // Mostramos solo la respuesta y el botón de validar
                txtRespuesta.setVisible(true);
                btnValidarRespuesta.setVisible(true);
            } 
            else 
            {
                lblPregunta.setText("Usuario no encontrado o sin pregunta configurada.");
                lblPregunta.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");
            }
        });

        // PASO 2: Validar la respuesta
        btnValidarRespuesta.setOnAction(e -> {
            String respuestaIngresada = txtRespuesta.getText().trim().toLowerCase();
            
            if (baseUsuario.verificarRespuestaSeguridad(txtUser.getText().trim(), respuestaIngresada)) 
            {
                // Si es correcto, habilitamos el cambio de contraseña
                txtRespuesta.setDisable(true);
                btnValidarRespuesta.setVisible(false);
                
                txtNuevaPass.setVisible(true);
                btnFinalizar.setVisible(true);
                
                lblPregunta.setText("Identidad confirmada. Ingresa tu nueva contraseña:");
                lblPregunta.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
            } 
            else 
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "La respuesta de seguridad es incorrecta.");
                alert.showAndWait();
            }
        });

        // PASO 3: Ejecutar el cambio
        btnFinalizar.setOnAction(e -> 
        {
            String nuevaPass = txtNuevaPass.getText().trim();

            if (nuevaPass.isEmpty()) 
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Debes ingresar una nueva contraseña.");
                alert.showAndWait();
                return;
            }

            String nuevaPassHash = Encriptador.hashPassword(nuevaPass);
            if (baseUsuario.resetearPassword(txtUser.getText().trim(), nuevaPassHash)) 
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "¡Éxito! Ya puedes iniciar sesión.");
                alert.showAndWait();
                stage.close();
            }
        });

        root.getChildren().addAll(lblTitulo, lblIndicacion, txtUser, btnVerificarUser, 
                                   lblPregunta, txtRespuesta, btnValidarRespuesta, 
                                   txtNuevaPass, btnFinalizar);
        
        Scene scene = new Scene(root, 500, 500);
        try 
        {
            scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
        } 
        catch (Exception ex) 
        {
            System.out.println("No se pudo cargar el CSS.");
        }
        
        stage.setScene(scene);
        root.requestFocus(); 
        stage.show();
    }
}