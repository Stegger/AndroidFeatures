
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Stegger
 */
public class Main extends Application
{

    private Scene scene;
    private Pane root;

    @Override
    public void start(final Stage stage) throws Exception
    {
        stage.setFullScreen(true);
        root = FXMLLoader.load(getClass().getResource("/views/MyLocationView.fxml"));
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());

        stage.setTitle("Testing Android features");
        stage.setScene(scene);
        stage.show();
    }

}
