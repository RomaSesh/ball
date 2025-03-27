package org.example.ball;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ball extends Application {

    // Увеличенные размеры сцены
    private static final double SCENE_WIDTH = 1000;
    private static final double SCENE_HEIGHT = 700;

    // Параметры шарика
    private static final double BALL_RADIUS = 25;
    private static final double BALL_START_X = 150;
    private static final double BALL_START_Y = 350;

    // Увеличенные параметры стакана (3 стенки)
    private static final double GLASS_X = 100;
    private static final double GLASS_Y = 100;
    private static final double GLASS_WIDTH = 800;  // Значительно шире
    private static final double GLASS_HEIGHT = 500;  // Выше

    // Цвета
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BALL_COLOR = Color.RED;
    private static final Color GLASS_COLOR = Color.BLACK;

    // Усиленные физические параметры
    private static final double BOUNCE_FACTOR = 0.98;  // Почти идеальный отскок
    private static final double GRAVITY = 0.2;         // Меньше гравитация
    private static final double INITIAL_DX = 12;       // Сильнее начальный толчок
    private static final double INITIAL_DY = -10;      // Сильнее вверх

    private Circle ball;
    private Timeline timeline;
    private double dx = INITIAL_DX;
    private double dy = INITIAL_DY;
    private Label speedLabel;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(BACKGROUND_COLOR);

        // Создаем 3 стенки (верхнюю, правую и нижнюю)
        Line topWall = new Line(GLASS_X, GLASS_Y, GLASS_X + GLASS_WIDTH, GLASS_Y);
        Line rightWall = new Line(GLASS_X + GLASS_WIDTH, GLASS_Y,
                GLASS_X + GLASS_WIDTH, GLASS_Y + GLASS_HEIGHT);
        Line bottomWall = new Line(GLASS_X, GLASS_Y + GLASS_HEIGHT,
                GLASS_X + GLASS_WIDTH, GLASS_Y + GLASS_HEIGHT);

        // Более толстые стенки
        topWall.setStroke(GLASS_COLOR);
        rightWall.setStroke(GLASS_COLOR);
        bottomWall.setStroke(GLASS_COLOR);
        topWall.setStrokeWidth(4);
        rightWall.setStrokeWidth(4);
        bottomWall.setStrokeWidth(4);

        // Создаем шарик
        ball = new Circle(BALL_START_X, BALL_START_Y, BALL_RADIUS, BALL_COLOR);

        // Кнопка запуска
        Button startButton = new Button("ЗАПУСК СУПЕР-ШАРИКА");
        startButton.setLayoutX(SCENE_WIDTH - 220);
        startButton.setLayoutY(SCENE_HEIGHT - 60);
        startButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 25px; -fx-background-color: #ff5555; -fx-text-fill: white;");

        // Индикатор скорости
        speedLabel = new Label("Скорость: " + String.format("%.1f", Math.sqrt(dx*dx + dy*dy)));
        speedLabel.setLayoutX(50);
        speedLabel.setLayoutY(SCENE_HEIGHT - 40);
        speedLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        startButton.setOnAction(e -> {
            resetBall();
            timeline.play();
        });

        root.getChildren().addAll(topWall, rightWall, bottomWall, ball, startButton, speedLabel);

        // Анимация с усиленными отскоками
        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double nextX = ball.getCenterX() + dx;
            double nextY = ball.getCenterY() + dy;

            // Гравитация (меньше значение для более плавного падения)
            dy += GRAVITY;

            // Удар о правую стенку (сильный отскок)
            if (nextX + BALL_RADIUS > GLASS_X + GLASS_WIDTH) {
                dx = -Math.abs(dx) * BOUNCE_FACTOR;
                nextX = GLASS_X + GLASS_WIDTH - BALL_RADIUS;
            }

            // Удар о верхнюю стенку (сильный отскок)
            if (nextY - BALL_RADIUS < GLASS_Y) {
                dy = Math.abs(dy) * BOUNCE_FACTOR;
                nextY = GLASS_Y + BALL_RADIUS;
            }

            // Удар о нижнюю стенку (сильный отскок)
            if (nextY + BALL_RADIUS > GLASS_Y + GLASS_HEIGHT) {
                dy = -Math.abs(dy) * BOUNCE_FACTOR;
                nextY = GLASS_Y + GLASS_HEIGHT - BALL_RADIUS;
            }

            ball.setCenterX(nextX);
            ball.setCenterY(nextY);

            // Обновляем индикатор скорости
            speedLabel.setText("Скорость: " + String.format("%.1f", Math.sqrt(dx*dx + dy*dy)));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);

        primaryStage.setTitle("Шарик в большом стакане");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void resetBall() {
        ball.setCenterX(BALL_START_X);
        ball.setCenterY(BALL_START_Y);
        dx = INITIAL_DX;
        dy = INITIAL_DY;
        speedLabel.setText("Скорость: " + String.format("%.1f", Math.sqrt(dx*dx + dy*dy)));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
