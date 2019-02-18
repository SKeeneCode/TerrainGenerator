package ui;

import map.MapDrawer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

public class uiController implements Initializable {

    @FXML
    private Slider zoomSlider;
    @FXML
    private Slider ySlider;

    @FXML
    private Canvas mapCanvas;
    @FXML
    private Canvas sliceCanvas;

    private MapDrawer mapDrawer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMapDrawer(new MapDrawer(mapCanvas, sliceCanvas));

        zoomSlider.valueProperty().addListener((obs, oldV, newV) -> getMapDrawer().draw(newV.doubleValue(), (int) ySlider.getValue()));

        ySlider.setMax(mapCanvas.getHeight() - 1);
        ySlider.valueProperty().addListener((obs, oldV, newV) -> getMapDrawer().draw(zoomSlider.getValue(), newV.intValue()));

        zoomSlider.setValue(11);
    }

    private MapDrawer getMapDrawer() {
        return mapDrawer;
    }

    private void setMapDrawer(MapDrawer mapDrawer) {
        this.mapDrawer = mapDrawer;
    }
}
