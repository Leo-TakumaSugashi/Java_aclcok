///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.openjfx:javafx-controls:22
//JAVA 22
//JAVA_OPTIONS --module-path=${JBANG_JFX_CLASSPATH} --add-modules=javafx.controls,javafx.graphics

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.time.LocalTime;

public class TS_AnalogClock extends Application {

    private double dragOffsetX;
    private double dragOffsetY;

    // GEOMETORY 
    private final int WIDTH = 300;
    private final int HEIGHT = 300;

    private final double RADIUS = WIDTH / 2.0 - 10;

    private Line hourHand = new Line();
    private Line minuteHand = new Line();
    private Line secondHand = new Line();

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);

        Pane root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);

        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        enableDragging(stage, root);

        double centerX = WIDTH / 2.0;
        double centerY = HEIGHT / 2.0;

        // 背景円
        Circle bg = new Circle(centerX, centerY, RADIUS);
        bg.setFill(Color.rgb(20, 200, 220, 0.3));
        bg.setStroke(Color.rgb(0,0,0,0.01));
        bg.setStrokeWidth(8);

        // 文字盤の目盛りとローマ数字
        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            double outerX = centerX + Math.cos(angle) * (RADIUS - 5);
            double outerY = centerY + Math.sin(angle) * (RADIUS - 5);

            if (i == 3 || i == 6 || i == 9 || i == 12) {
                String roman = switch (i) {
                    case 3 -> "III ";
                    case 6 -> "VI";
                    case 9 -> " IX";
                    case 12 -> "XII";
                    default -> "";
                };
                Text text = new Text(outerX - 10, outerY + 5, roman);
                text.setFill(Color.WHITE);
                text.setFont(Font.font(32));
                root.getChildren().add(text);
            } else {
                Circle dot = new Circle(outerX, outerY, 2, Color.WHITE);
                root.getChildren().add(dot);
            }
        }

        // 針の初期スタイル設定
        hourHand.setStroke(Color.rgb(255,255,255,1));
        hourHand.setStrokeWidth(20);
        minuteHand.setStroke(Color.rgb(180,180,180,1));
        minuteHand.setStrokeWidth(6);
        secondHand.setStroke(Color.RED);
        secondHand.setStrokeWidth(2);

        root.getChildren().addAll(bg, hourHand, minuteHand, secondHand);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> drawHands(centerX, centerY)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        drawHands(centerX, centerY);
    }

    private void drawHands(double cx, double cy) {
        LocalTime now = LocalTime.now();

        setHand(hourHand, cx, cy, now.getHour() % 12 * 30 + now.getMinute() * 0.5, RADIUS * 0.5);
        setHand(minuteHand, cx, cy, now.getMinute() * 6 + now.getSecond() * 0.1, RADIUS * 0.75);
        setHand(secondHand, cx, cy, now.getSecond() * 6, RADIUS * 0.9);
    }

    private void setHand(Line hand, double cx, double cy, double angleDeg, double length) {
        double angle = Math.toRadians(angleDeg - 90);
        double x = cx + Math.cos(angle) * length;
        double y = cy + Math.sin(angle) * length;

        hand.setStartX(cx);
        hand.setStartY(cy);
        hand.setEndX(x);
        hand.setEndY(y);
    }

    private void enableDragging(Stage stage, Pane pane) {
        pane.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX();
            dragOffsetY = e.getSceneY();
        });
        pane.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
