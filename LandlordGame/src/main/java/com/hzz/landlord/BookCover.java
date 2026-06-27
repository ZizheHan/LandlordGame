//========================================肖松========================================
package main.java.com.hzz.landlord;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BookCover extends Application {
    // 图片资源（路径请根据实际调整）
    private final Image[] images = {
            new Image(getClass().getResource("/IMG/back.png").toExternalForm()),
            new Image(getClass().getResource("/IMG/title.png").toExternalForm()),
            new Image(getClass().getResource("/IMG/lbw1.png").toExternalForm()),
            new Image(getClass().getResource("/IMG/lbw2.png").toExternalForm()),
            new Image(getClass().getResource("/IMG/lbw3.png").toExternalForm()),
            new Image(getClass().getResource("/IMG/lbw4.png").toExternalForm()),
            new Image(getClass().getResource("/IMG/setting.png").toExternalForm()),
            new Image(getClass().getResource("/IMG/countinue.png").toExternalForm()),
            new Image(getClass().getResource("/IMG/new.png").toExternalForm()),
};

    private final StackPane root = new StackPane();
    private final Pane bookPane = new Pane();
    private final Pane cardsPane = CardsGame.cardsPane;   // 游戏面板
    private final Pane setPane = Settings.getSetPane();   // 设置面板

    @Override
    public void start(Stage primaryStage) {
        root.getChildren().addAll(bookPane, cardsPane, setPane);
        bookPane.setVisible(true);
        cardsPane.setVisible(false);
        setPane.setVisible(false);

        // 背景图
        ImageView bg = new ImageView(images[0]);
        double screenW = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
        double screenH = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();
        bg.setFitWidth(screenW - 10);
        bg.setFitHeight(screenH - 30);
        bookPane.getChildren().add(bg);

        // 转换为 ImageView 并添加动画
        ImageView[] imgViews = new ImageView[images.length];
        for (int i = 0; i < images.length; i++) {
            imgViews[i] = new ImageView(images[i]);
        }

        showLBW(imgViews[1], 800, 0, 800, 170);
        showLBW(imgViews[3], 700, 400, 900, 500, 150);
        showLBW(imgViews[2], 650, 700, 800, 700, 150);
        showLBW(imgViews[5], 1500, 600, 1300, 600, 450);
        showLBW(imgViews[4], 1150, 1000, 1150, 800, 300);

        // 功能按钮
        Button newGame = new Button("", new ImageView(images[8]));
        Button contGame = new Button("", new ImageView(images[7]));
        Button setGame = new Button("", new ImageView(images[6]));
        newGame.setBackground(null);
        contGame.setBackground(null);
        setGame.setBackground(null);
        newGame.setOpacity(0);
        contGame.setOpacity(0);
        setGame.setOpacity(0);

        bookPane.setOnMouseClicked(e -> {
            showButton(newGame, 100, 580, 300, 580, 100);
            showButton(contGame, 100, 700, 300, 700, 500);
            showButton(setGame, 100, 820, 300, 820, 1000);
            bookPane.setOnMouseClicked(null);
        });

        newGame.setOnAction(e -> {
            CardsGame.startGame();
            cardsPane.setVisible(true);
            bookPane.setVisible(false);
            setPane.setVisible(false);
        });

        contGame.setOnAction(e -> {
            // 继续游戏（暂未实现）
        });

        setGame.setOnAction(e -> {
            setPane.setVisible(true);
            bookPane.setVisible(false);
            cardsPane.setVisible(false);
        });

        Scene scene = new Scene(root);
        primaryStage.setTitle("卢食传说");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 按钮动画
    private void showButton(Button btn, int sx, int sy, int ex, int ey, int delay) {
        btn.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(2000), btn);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delay));
        fade.play();

        PathTransition pt = new PathTransition();
        pt.setDelay(Duration.millis(delay));
        pt.setDuration(Duration.millis(1000));
        pt.setNode(btn);
        pt.setPath(new Line(sx, sy, ex, ey));
        bookPane.getChildren().add(btn);
        pt.play();
    }

    // 图片动画（带延迟）
    private void showLBW(ImageView img, int sx, int sy, int ex, int ey, int delay) {
        bookPane.getChildren().add(img);
        img.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), img);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(delay));
        ft.play();

        PathTransition pt = new PathTransition();
        pt.setDuration(Duration.millis(1000));
        pt.setNode(img);
        pt.setPath(new Line(sx, sy, ex, ey));
        pt.setDelay(Duration.millis(delay));
        pt.play();
    }

    private void showLBW(ImageView img, int sx, int sy, int ex, int ey) {
        showLBW(img, sx, sy, ex, ey, 0);
    }
}