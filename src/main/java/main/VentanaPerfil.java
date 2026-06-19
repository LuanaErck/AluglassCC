package main;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import base.BaseUsuario;
import clases.Usuario;
import seguridad.Encriptador;

public class VentanaPerfil 
{
    private Usuario usuarioLogueado;
    private BaseUsuario baseUsuario = new BaseUsuario();

    public VentanaPerfil(Usuario usuario) 
    {
        this.usuarioLogueado = usuario;
    }

    public VBox getView() 
    {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_LEFT);

        Label lblTitulo = new Label("CONFIGURACIÓN DE PERFIL");
        lblTitulo.getStyleClass().add("titulo-perfil");

        // Campo Usuario (No editable)
        VBox boxUser = crearCampo("Usuario:", new TextField(usuarioLogueado.getUsuario()), true);
        ((TextField)boxUser.getChildren().get(1)).setDisable(true);

        // Nueva Contraseña
        PasswordField txtPass = new PasswordField();
        VBox boxPass = crearCampo("Nueva Contraseña:", txtPass, false);
        txtPass.setPromptText("Dejar en blanco para no cambiar");

        // Pregunta de Seguridad
        TextField txtPregunta = new TextField(usuarioLogueado.getPregunta() != null ? usuarioLogueado.getPregunta() : "");
        VBox boxPregunta = crearCampo("Pregunta de Seguridad:", txtPregunta, false);
        txtPregunta.setPromptText("Ej: ¿Nombre de tu primer mascota?");

        // Respuesta de Seguridad
        PasswordField txtRespuesta = new PasswordField();
        VBox boxRespuesta = crearCampo("Respuesta de Seguridad:", txtRespuesta, false);
        txtRespuesta.setPromptText("Escribe la respuesta");

        Button btnGuardar = new Button("GUARDAR CAMBIOS");
        btnGuardar.getStyleClass().add("boton-guardar-perfil");

        Label lblMsj = new Label();

        btnGuardar.setOnAction(e -> 
        {
            String pass = txtPass.getText().trim();
            String pregunta = txtPregunta.getText().trim();
            
            // Se aplica LowerCase y Trim a la respuesta antes de procesarla
            String respuestaLimpia = txtRespuesta.getText().trim().toLowerCase();

            if (pregunta.isEmpty() || respuestaLimpia.isEmpty()) 
            {
                lblMsj.setText("La pregunta y respuesta son obligatorias.");
                lblMsj.setStyle("-fx-text-fill: red;");
                return;
            }

            // Si la pass está vacía, usamos la que ya tiene el usuario (sin volver a hashear)
            String passFinal = pass.isEmpty() ? usuarioLogueado.getContrasena() : Encriptador.hashPassword(pass);
            
            // La respuesta se hashea siempre en minúsculas
            String respuestaHash = Encriptador.hashPassword(respuestaLimpia);

            boolean exito = baseUsuario.actualizarPerfil(usuarioLogueado.getIdUsuario(), passFinal, pregunta, respuestaHash);

            if (exito)
            {
                lblMsj.setText("¡Datos actualizados correctamente!");
                lblMsj.setStyle("-fx-text-fill: green;");
                
                // Actualizamos el objeto en memoria para que el resto de la sesión tenga los datos nuevos
                usuarioLogueado.setPregunta(pregunta);
                usuarioLogueado.setRespuesta(respuestaHash);
                if(!pass.isEmpty()) usuarioLogueado.setContrasena(passFinal);
                
                // Limpiamos los campos de password por seguridad
                txtPass.clear();
                txtRespuesta.clear();
            } 
            else 
            {
                lblMsj.setText("Error al guardar en la base de datos.");
                lblMsj.setStyle("-fx-text-fill: red;");
            }
        });

        root.getChildren().addAll(lblTitulo, boxUser, boxPass, boxPregunta, boxRespuesta, btnGuardar, lblMsj);
        return root;
    }

    private VBox crearCampo(String titulo, Control input, boolean deshabilitado) 
    {
        VBox box = new VBox(5);
        Label lbl = new Label(titulo);
        lbl.getStyleClass().add("label-perfil");
        input.getStyleClass().add("input-perfil");
        box.getChildren().addAll(lbl, input);
        return box;
    }
}