package org.example.ball;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.geometry.*;

import java.util.Random;

/**
 * Программа "Симулятор шарика с ручным перезапуском"
 *
 * Особенности:
 * - Реалистичная физика отскоков
 * - Шарик вылетает за границы и ждет команды
 * - Полный контроль пользователя
 * - Подробная документация
 */
public class ball extends Application {

    // Размеры основного окна
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;

    // Параметры стакана (области движения)
    private static final int GLASS_X = 150;       // Позиция стакана по X
    private static final int GLASS_Y = 100;       // Позиция стакана по Y
    private static final int GLASS_WIDTH = 800;   // Ширина области
    private static final int GLASS_HEIGHT = 400;  // Высота области

    // Параметры шарика
    private static final int BALL_RADIUS = 15;    // Радиус шарика
    private static final Color BALL_COLOR = Color.RED; // Цвет шарика

    // Физические параметры
    private static final double MIN_SPEED = 3;     // Минимальная скорость
    private static final double MAX_SPEED = 8;     // Максимальная скорость

    // Графические элементы
    private Circle ball;          // Объект шарика
    private Timeline timeline;    // Таймер анимации

    // Физические переменные
    private double dx, dy;        // Скорости по осям X и Y
    private int bounceCount = 0;  // Счетчик отскоков
    private Random random = new Random(); // Генератор случайных чисел

    // Элементы управления
    private TextField angleField; // Поле ввода угла
    private Button startBtn;      // Кнопка запуска/перезапуска
    private Label statusLabel;    // Метка статуса

    /**
     * Точка входа в JavaFX приложение
     * @param stage Главное окно приложения
     */
    @Override
    public void start(Stage stage) {
        // Создаем корневой контейнер
        Pane root = new Pane();

        // Настройка сцены
        setupScene(root);

        // Создаем основную сцену
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Управляемый симулятор шарика");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Настройка всех графических элементов
     * @param root Корневой контейнер для добавления элементов
     */
    private void setupScene(Pane root) {
        // 1. Создаем стакан (3 стенки)
        createGlass(root);

        // 2. Инициализируем шарик
        initBall(root);

        // 3. Настраиваем элементы управления
        setupControls(root);

        // 4. Настраиваем анимацию
        setupAnimation();
    }

    /**
     * Создание графического представления "стакана" (3 стенки)
     * @param root Контейнер для добавления элементов
     */
    private void createGlass(Pane root) {
        // Верхняя стенка
        Line top = new Line(GLASS_X, GLASS_Y, GLASS_X + GLASS_WIDTH, GLASS_Y);// Создаем линию

        // Правая стенка
        Line right = new Line(GLASS_X + GLASS_WIDTH, GLASS_Y,
                GLASS_X + GLASS_WIDTH, GLASS_Y + GLASS_HEIGHT);

        // Нижняя стенка
        Line bottom = new Line(GLASS_X, GLASS_Y + GLASS_HEIGHT,
                GLASS_X + GLASS_WIDTH, GLASS_Y + GLASS_HEIGHT);

        // Настройка стилей стенок
        top.setStroke(Color.DARKBLUE);
        right.setStroke(Color.DARKBLUE);
        bottom.setStroke(Color.DARKBLUE);
        top.setStrokeWidth(3);
        right.setStrokeWidth(3);
        bottom.setStrokeWidth(3);

        // Добавляем стенки на сцену
        root.getChildren().addAll(top, right, bottom);
    }

    /**
     * Инициализация шарика
     * @param root Корневой контейнер
     */
    private void initBall(Pane root) {
        // Создаем круг (шарик)
        ball = new Circle(BALL_RADIUS, BALL_COLOR);

        // Устанавливаем начальную позицию
        resetBall();

        // Добавляем шарик на сцену
        root.getChildren().add(ball);
    }

    /**
     * Настройка панели управления
     * @param root Корневой контейнер
     */
    private void setupControls(Pane root) {
        // Создаем горизонтальную панель
        HBox controlPanel = new HBox(10);
        controlPanel.setLayoutX(20);
        controlPanel.setLayoutY(20);
        controlPanel.setPadding(new Insets(10));

        // Поле для ввода угла
        angleField = new TextField("45");
        angleField.setPrefWidth(60);
        angleField.setTooltip(new Tooltip("Введите угол от 0 до 90 градусов"));

        // Кнопка запуска/перезапуска
        startBtn = new Button("Запуск");
        startBtn.setOnAction(e -> startSimulation());

        // Метка статуса
        statusLabel = new Label("Нажмите 'Запуск' для начала");

        // Добавляем элементы на панель
        controlPanel.getChildren().addAll(
                new Label("Угол:"), angleField,
                startBtn, statusLabel
        );

        // Добавляем панель на сцену
        root.getChildren().add(controlPanel);
    }

    /**
     * Настройка анимации движения шарика
     */
    private void setupAnimation() {
        // Создаем анимацию с частотой ~60 кадров/сек
        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            updateBallPosition(); // Основной метод обновления позиции
        }));
        timeline.setCycleCount(Animation.INDEFINITE); // Бесконечная анимация
    }

    /**
     * Сброс шарика в начальное положение
     */
    private void resetBall() {
        // Устанавливаем начальную позицию (внутри стакана)
        ball.setCenterX(GLASS_X + BALL_RADIUS + 10);
        ball.setCenterY(GLASS_Y + GLASS_HEIGHT / 2);

        // Сбрасываем счетчик отскоков
        bounceCount = 0;

        // Делаем шарик видимым
        ball.setVisible(true);
    }

    /**
     * Запуск/перезапуск симуляции
     */
    private void startSimulation() {
        try {
            // Получаем угол от пользователя
            double angle = Double.parseDouble(angleField.getText());

            // Проверяем корректность угла
            if (angle < 0 || angle > 90) {
                statusLabel.setText("Угол должен быть от 0 до 90 градусов!");
                return;
            }

            // Генерируем случайную скорость в заданном диапазоне
            double speed = MIN_SPEED + random.nextDouble() * (MAX_SPEED - MIN_SPEED);

            // Пересчитываем угол в радианы
            double angleRad = Math.toRadians(angle);

            // Рассчитываем компоненты скорости
            dx = speed * Math.cos(angleRad);  // Горизонтальная составляющая
            dy = -speed * Math.sin(angleRad); // Вертикальная составляющая (ось Y направлена вниз)

            // Сбрасываем шарик
            resetBall();

            // Обновляем статус
            statusLabel.setText("Шарик в движении...");

            // Запускаем анимацию
            timeline.play();

        } catch (NumberFormatException e) {
            statusLabel.setText("Ошибка! Введите число для угла.");
        }
    }

    /**
     * Обновление позиции шарика и проверка столкновений
     */
    private void updateBallPosition() {
        // Рассчитываем новую позицию
        double nextX = ball.getCenterX() + dx;
        double nextY = ball.getCenterY() + dy;

        // Проверяем столкновения со стенками
        checkWallCollisions(nextX, nextY);

        // Проверяем вылет за границы
        if (isBallOutOfBounds(nextX, nextY)) {
            handleBallExit();
            return;
        }

        // Обновляем позицию шарика
        ball.setCenterX(nextX);
        ball.setCenterY(nextY);
    }

    /**
     * Проверка столкновений со стенками стакана
     * @param nextX Предполагаемая позиция X
     * @param nextY Предполагаемая позиция Y
     */
    private void checkWallCollisions(double nextX, double nextY) {
        // Столкновение с правой стенкой
        if (nextX + BALL_RADIUS > GLASS_X + GLASS_WIDTH) {
            dx = -dx; // Меняем направление по X
            nextX = GLASS_X + GLASS_WIDTH - BALL_RADIUS;
            bounceCount++;
        }

        // Столкновение с верхней стенкой
        if (nextY - BALL_RADIUS < GLASS_Y) {
            dy = -dy; // Меняем направление по Y
            nextY = GLASS_Y + BALL_RADIUS;
            bounceCount++;
        }

        // Столкновение с нижней стенкой
        if (nextY + BALL_RADIUS > GLASS_Y + GLASS_HEIGHT) {
            dy = -dy; // Меняем направление по Y
            nextY = GLASS_Y + GLASS_HEIGHT - BALL_RADIUS;
            bounceCount++;
        }
    }

    /**
     * Проверка вылета шарика за границы
     * @param nextX Предполагаемая позиция X
     * @param nextY Предполагаемая позиция Y
     * @return true если шарик вылетел за границы
     */
    private boolean isBallOutOfBounds(double nextX, double nextY) {
        // Проверяем левую границу, верх и низ экрана
        return nextX - BALL_RADIUS < 0 ||    // Левая граница экрана
                nextY - BALL_RADIUS < 0 ||    // Верхняя граница экрана
                nextY + BALL_RADIUS > WINDOW_HEIGHT; // Нижняя граница экрана
    }

    /**
     * Обработка вылета шарика за границы
     */
    private void handleBallExit() {
        // Останавливаем анимацию
        timeline.stop();

        // Прячем шарик
        ball.setVisible(false);

        // Обновляем статус
        statusLabel.setText("Шарик вылетел! Нажмите 'Запуск' для нового шарика");
    }

    /**
     * Точка входа в программу
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args); // Стандартный запуск JavaFX
    }
}



