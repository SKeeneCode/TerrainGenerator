package map;

import generators.OpenSimplexNoise;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.nio.ByteBuffer;
import java.util.Objects;

import static map.MapConstants.*;

public class MapDrawer {

    private Canvas mapCanvas;
    private Canvas sliceCanvas;
    private OpenSimplexNoise noiseGenerator = new OpenSimplexNoise();
    private double[][] heightMap;
    private byte[] pixels;


    public MapDrawer(Canvas mapCanvas, Canvas sliceCanvas) {
        this.mapCanvas = Objects.requireNonNull(mapCanvas, "Canvas must not be null");
        this.mapCanvas.setWidth(MAP_WIDTH);
        this.mapCanvas.setHeight(MAP_HEIGHT);
        this.sliceCanvas = Objects.requireNonNull(sliceCanvas, "Canvas must not be null");
        this.sliceCanvas.setWidth(MAP_WIDTH);
        this.heightMap = new double[MAP_WIDTH][MAP_HEIGHT];
        this.pixels = new byte[MAP_WIDTH * MAP_HEIGHT * 3];
    }

    public void draw(double zoom, int yAxis) {
        // Get the graphics context of the mapCanvas
        GraphicsContext gc = getMapCanvas().getGraphicsContext2D();
        // Create the PixelWriter
        PixelWriter pixelWriter = gc.getPixelWriter();
        // Define the PixelFormat
        PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {

                int i = y * MAP_WIDTH * 3 + x * 3;

                double nx = (double) x / MAP_WIDTH - 0.5;
                double ny = (double) y / MAP_HEIGHT - 0.5;

                if (MAP_WIDTH >= MAP_HEIGHT) {
                    ny *= (double) MAP_HEIGHT / MAP_WIDTH;
                } else {
                    nx *= (double) MAP_WIDTH / MAP_HEIGHT;
                }

                byte val = (byte) (noise(zoom * nx, zoom * ny) * 255);
                pixels[i] = val;
                pixels[i + 1] = val;
                pixels[i + 2] = val;
            }
        }

        pixelWriter.setPixels(0, 0, MAP_WIDTH, MAP_HEIGHT, pixelFormat, pixels, 0, MAP_WIDTH * 3);
        gc.setFill(Color.rgb(255, 0, 0, 0.5));
        gc.fillRect(0, yAxis, getMapCanvas().getWidth(), 1);

        // Get the graphics context of the sliceCanvas
        GraphicsContext rightGC = getSliceCanvas().getGraphicsContext2D();

        rightGC.clearRect(0, 0, getSliceCanvas().getWidth(), getSliceCanvas().getHeight());

        for (int i = yAxis * MAP_WIDTH * 3; i < (yAxis + 1) * MAP_WIDTH * 3; i += 3) {
            int val = pixels[i];
            if (val < 0) {
                val += 256;
            }
            rightGC.setFill(Color.rgb(val, val, val));
            int x = (i - yAxis * MAP_WIDTH * 3) / 3;
            rightGC.fillRect(x, (double) val / 4, 1, 100 - (double) val / 4);
        }

    }

    private double noise(double nx, double ny) {
        return getNoiseGenerator().eval(nx, ny) / 2 + 0.5;
    }

    public OpenSimplexNoise getNoiseGenerator() {
        return noiseGenerator;
    }

    public void setNoiseGenerator(OpenSimplexNoise noiseGenerator) {
        this.noiseGenerator = noiseGenerator;
    }

    private Canvas getMapCanvas() {
        return mapCanvas;
    }

    public void setMapCanvas(Canvas mapCanvas) {
        this.mapCanvas = mapCanvas;
    }

    public Canvas getSliceCanvas() {
        return sliceCanvas;
    }

    public void setSliceCanvas(Canvas sliceCanvas) {
        this.sliceCanvas = sliceCanvas;
    }

    public double[][] getHeightMap() {
        return heightMap;
    }

    public void setHeightMap(double[][] heightMap) {
        this.heightMap = heightMap;
    }

    public byte[] getPixels() {
        return pixels;
    }

    public void setPixels(byte[] pixels) {
        this.pixels = pixels;
    }
}
