package main;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import seguridad.Encriptador;

import base.BaseUsuario;
import clases.Usuario;
import java.io.InputStream;

public class Login extends Application 
{

    @Override
    public void start(Stage stage) 
    {
        // LOGO PRINCIPAL
        ImageView logo = new ImageView();
        try 
        {
            InputStream is = getClass().getResourceAsStream("/iconos/logo.jpeg");
            if (is != null) 
            {
                logo.setImage(new Image(is));
                logo.setFitWidth(280); 
                logo.setPreserveRatio(true);
            }
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        }

        // PREPARACIÓN DE ICONOS PARA INPUTS 
        ImageView iconUsuario = cargarIcono("/iconos/user.png");
        ImageView iconPassword = cargarIcono("/iconos/lock6.png");

        // COMPONENTES (Usando ControlsFX) 
        
        // Campo de Usuario con icono
        CustomTextField txtUsuario = new CustomTextField();
        txtUsuario.setPromptText("Usuario");
        txtUsuario.getStyleClass().add("input-login");
        if (iconUsuario != null) 
        {
            txtUsuario.setLeft(iconUsuario); // Coloca el icono a la izquierda
        }

        // Campo de Contraseña con icono
        CustomPasswordField txtContrasena = new CustomPasswordField();
        txtContrasena.setPromptText("Contraseña");
        txtContrasena.getStyleClass().add("input-login");
        if (iconPassword != null) 
        {
            txtContrasena.setLeft(iconPassword); // Coloca el icono a la izquierda
        }

        Button btnLogin = new Button("INGRESAR");
        btnLogin.setId("boton-ingresar");

        Label lblMensaje = new Label();
        lblMensaje.setId("mensaje-error");
        
        Hyperlink linkOlvido = new Hyperlink("¿Olvidó su contraseña?");
        linkOlvido.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

        linkOlvido.setOnAction(e -> 
        {
            VentanaRecuperacion rec = new VentanaRecuperacion();
            rec.mostrar();
        });

        // CONTENEDOR 
        VBox root = new VBox(25, logo, txtUsuario, txtContrasena, btnLogin, lblMensaje, linkOlvido);
        root.setAlignment(Pos.CENTER);
        root.setId("fondo-login");

        // LÓGICA 
        btnLogin.setOnAction(e -> 
        {
            String usuario = txtUsuario.getText().trim();
            String contrasena = txtContrasena.getText().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) 
            {
                lblMensaje.setText("Complete todos los campos");
            }
            else
            {
                BaseUsuario baseUsuario = new BaseUsuario();
                Usuario u = baseUsuario.buscarUsuario(usuario);
                if (u != null && Encriptador.verificarPassword(contrasena, u.getContrasena())) 
                {
                    new MenuPrincipal(stage, u).mostrar();
                } 
                else 
                {
                    lblMensaje.setText("Usuario o contraseña incorrectos");
                    lblMensaje.setStyle("-fx-text-fill: red;");
                }
            }
        });

        // CARGA DEL CSS 
        Scene scene = new Scene(root, 500, 500); // Aumentado un poco el alto
        try 
        {
            scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
        } 
        catch (Exception e) 
        {
            System.out.println("No se pudo cargar el CSS: " + e.getMessage());
        }

        stage.setTitle("Aluglass - Control de Cuentas");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // Función auxiliar para cargar y dimensionar los iconos de los inputs
    private ImageView cargarIcono(String ruta) 
    {
        try 
        {
            InputStream is = getClass().getResourceAsStream(ruta);
            if (is != null) 
            {
                Image img = new Image(is);
                ImageView iv = new ImageView(img);
                iv.setFitHeight(20); // Altura adecuada para estar dentro del textfield
                iv.setPreserveRatio(true);
                // Añadir un pequeño margen a la derecha del icono
                VBox.setMargin(iv, new javafx.geometry.Insets(0, 5, 0, 0)); 
                return iv;
            }
        } 
        catch (Exception e) 
        {
            System.out.println("No se pudo cargar el icono: " + ruta);
        }
        return null;
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
