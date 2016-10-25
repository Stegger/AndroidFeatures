/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.controllers;

import com.gluonhq.charm.down.common.PlatformFactory;
import com.gluonhq.charm.down.common.Position;
import com.gluonhq.charm.down.common.PositionService;
import com.gluonhq.charm.glisten.control.TextField;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.lodgon.openmapfx.core.DefaultBaseMapProvider;
import org.lodgon.openmapfx.core.LayeredMap;
import org.lodgon.openmapfx.core.LicenceLayer;
import org.lodgon.openmapfx.core.PositionLayer;
import org.lodgon.openmapfx.core.TileProvider;
import org.lodgon.openmapfx.desktop.SimpleProviderPicker;

/**
 * FXML Controller class
 *
 * @author Stegger
 */
public class MyLocationViewController implements Initializable
{

    @FXML
    BorderPane paneBorder;

    @FXML
    Button btnGetLoc;

    @FXML
    Label lblPos;

    @FXML
    Label lblFileLoc;
    
    @FXML
    Pane paneBottom;
    
    @FXML
    TextField txtGlisten;

    private Position pos;
    private Image image;
    private LayeredMap map;
    private TileProvider[] tileProviders;
    private SimpleProviderPicker spp;
    private LicenceLayer licenceLayer;
    private File mainFolder;
    private File locationFile;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        assert lblPos != null : "fx:id=\"lblPos\" was not injected: check your FXML file 'MyLocationView.fxml'.";
        assert btnGetLoc != null : "fx:id=\"btnGetLoc\" was not injected: check your FXML file 'MyLocationView.fxml'.";
        assert paneBorder != null : "fx:id=\"paneBorder\" was not injected: check your FXML file 'MyLocationView.fxml'.";

        URL im = this.getClass().getResource("/icons/pokeball.png");
        image = new Image(im.toString());

        setupMap();

        try
        {
            
            mainFolder = PlatformFactory.getPlatform().getPrivateStorage();
            locationFile = new File(mainFolder, "locations.txt");
            locationFile.createNewFile();
            lblFileLoc.setText("Location file: " + locationFile.getAbsolutePath());
            

        } catch (IOException ex)
        {
            Logger.getLogger(MyLocationViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        PositionService positionService = PlatformFactory.getPlatform().getPositionService();
        positionService.positionProperty().addListener(new ChangeListenerImpl());
    }

    @FXML
    private void getLocation(ActionEvent event)
    {
        PositionService positionService = PlatformFactory.getPlatform().getPositionService();
        Position position = positionService.getPosition();
        if (position != null)
        {
            pos = position;
            updateMapPosition(pos);
        }
        String loc = (pos != null) ? pos.getLatitude() + "," + pos.getLongitude() : "position unavailable";
        lblPos.setText(loc);
    }

    private void setupMap()
    {
        DefaultBaseMapProvider provider = new DefaultBaseMapProvider();

        spp = new SimpleProviderPicker(provider);
        paneBottom.getChildren().add(spp);
        paneBottom.setMinHeight(42.0);

        map = new LayeredMap(provider);
        paneBorder.setCenter(map);

        map.setZoom(8);

        licenceLayer = new LicenceLayer(provider);
        map.getLayers().add(0, licenceLayer);
        map.setSnapToPixel(true);
    }

    private void updateMapPosition(Position pos)
    {
        map.setCenter(pos.getLatitude(), pos.getLongitude());
        PositionLayer positionLayer = new PositionLayer(image);
        if (map.getLayers().size() > 1 && map.getLayers().get(1) != null)
        {
            map.getLayers().remove(1);
        }
        map.getLayers().add(1, positionLayer);
        positionLayer.updatePosition(pos.getLatitude(), pos.getLongitude());
    }

    private void writeLocationToFile(Position pos) throws IOException
    {
        if (locationFile == null || !locationFile.exists())
        {
            throw new IOException("Location file not initialised");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(locationFile, true)))
        {
            bw.write(pos.getLatitude()+" , "+pos.getLongitude());
            bw.newLine();
            bw.close();
        }
    }

    private class ChangeListenerImpl implements ChangeListener<Position>
    {

        @Override
        public void changed(ObservableValue<? extends Position> observable, Position oldValue, Position newValue)
        {
            pos = newValue;
            updateMapPosition(pos);
            try
            {
                writeLocationToFile(pos);
            } catch (IOException ex)
            {
                Logger.getLogger(MyLocationViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
