package GUI;

import Objects.PPS;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PPSGUI extends Application {
    private int size = 500;
    private int timeStep = 60;
    private PPS pps;

    @Override
    public void init() {
        //5, 180, 17, 0.67
        pps = new PPS(1000, 100, 1000/timeStep);
        pps.initialize(5, 180, 17, 0.67);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Canvas canvas = new Canvas(size, size);
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        AnimationTimer mainLoop = new AnimationTimer() {
            @Override
            public void handle(long l) {
                ctx.setFill(Color.grayRgb(25));
                ctx.fillRect(0, 0, size, size);

                pps.show(ctx);
            }
        };

        stage.setScene(new Scene(root));
        stage.setTitle("PPS [" + pps.getSize() + " x " + pps.getSize() + "]");
        mainLoop.start();
        pps.start();
        stage.show();
    }

    @Override
    public void stop() {
        pps.end();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
