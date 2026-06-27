//========================================极东魔术昼寝结社之夏========================================
package main.java.com.hzz.landlord;

import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class CardsGame {

    //========================================肖松========================================

    // ========== UI 组件 ==========
    public static final Pane cardsPane = new Pane();
    private static final Pane playerHandPane = new Pane();
    private static final Pane playAreaPane = new Pane();
    private static final Pane robotInfoPane = new Pane();
    private static final Text statusText = new Text();
    private static final Label passLabel = new Label();

    private static final Button btnCall1 = new Button("叫1分");
    private static final Button btnCall2 = new Button("叫2分");
    private static final Button btnCall3 = new Button("叫3分");
    private static final Button btnNoCall = new Button("不叫");
    private static final Button btnOut = new Button("出牌");
    private static final Button btnPass = new Button("不要");

    // ========== 游戏数据 ==========
    private static final List<PokerCard> playerHand = new ArrayList<>();
    private static final List<PokerCard> robot1Hand = new ArrayList<>();
    private static final List<PokerCard> robot2Hand = new ArrayList<>();
    private static final List<PokerCard> bottomCards = new ArrayList<>();

    private static int currentPlayer;      // 0=玩家, 1=机器人1, 2=机器人2
    private static int landLord;
    private static boolean isGameOver = false;
    private static boolean isPlayerTurn = false;
    private static List<PokerCard> lastPlayedCards = new ArrayList<>(); // 上家出的牌
    private static int consecutivePasses = 0;
    private static int lastPlayerWhoPlayed = -1;
    private static boolean roundReset = false;

    // 叫分相关
    private static LandlordBidding landlordBidding;

    static {
        cardsPane.getChildren().addAll(playerHandPane, playAreaPane, robotInfoPane, statusText, passLabel);
        hideAllButtons();

        statusText.setFont(Font.font(30));
        statusText.setStyle("-fx-text-fill: #29839c; -fx-font-weight: bold;");
        passLabel.setFont(Font.font(30));
        passLabel.setStyle("-fx-text-fill: #707065;");
        passLabel.setVisible(false);

        btnOut.setOnAction(e -> handlePlayerOut());
        btnPass.setOnAction(e -> handlePlayerPass());

        cardsPane.visibleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // 1. 定义一个包含所有背景图片路径的列表
                String[] bgImages = {
                        "/IMG/myback/cardsPlayBack1.png",
                        "/IMG/myback/cardsPlayBack2.png",
                        "/IMG/myback/cardsPlayBack3.png",
                        "/IMG/myback/cardsPlayBack4.png"
                };

                // 2. 从列表中随机选择一个图片路径
                Random random = new Random();
                String randomBgPath = bgImages[random.nextInt(bgImages.length)];

                // 3. 尝试加载并设置选中的背景图片
                var url = CardsGame.class.getResource(randomBgPath);
                if (url != null) {
                    Image bgImg = new Image(url.toExternalForm());
                    BackgroundImage bgi = new BackgroundImage(bgImg,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
                    cardsPane.setBackground(new Background(bgi));
                } else {
                    System.err.println("背景图片缺失：/IMG/myback/cardsPlayBack1.png");
                }
            }
        });
    }

    public static void startGame() {
        initGame();
        //LandlordBidding转交给叫分逻辑
        if (landlordBidding == null) {
            LandlordBidding.BiddingHost host = new LandlordBidding.BiddingHost() {
                @Override
                public List<PokerCard> getHandByPlayer(int player) {
                    return CardsGame.getHandByPlayer(player);
                }

                @Override
                public String getPlayerName(int player) {
                    return CardsGame.getPlayerName(player);
                }

                @Override
                public void refreshPlayerHand() {
                    CardsGame.refreshPlayerHand();
                }

                @Override
                public void refreshRobots() {
                    CardsGame.refreshRobots();
                }

                @Override
                public void setLandlord(int player) {
                    landLord = player;
                    List<PokerCard> landlordHand = getHandByPlayer(landLord);
                    landlordHand.addAll(bottomCards);
                    landlordHand.sort(Comparator.comparingInt(c -> c.num));
                    if (landLord == 0) refreshPlayerHand();
                    else refreshRobots();
                }

                @Override
                public void startPlayingPhase() {
                    CardsGame.startPlayingPhase();
                }

                @Override
                public void restartGame() {
                    CardsGame.startGame();
                }
            };
            landlordBidding = new LandlordBidding(
                    cardsPane, statusText,
                    btnCall1, btnCall2, btnCall3, btnNoCall,
                    host);
        }
        landlordBidding.start();
    }

    private static Image loadImage(String path) {
        try {
            return new Image(CardsGame.class.getResource(path).openStream());
        } catch (Exception e) {
            System.out.println("加载图片失败: " + path);
            return null;
        }
    }

    private static void initGame() {
        playerHand.clear();
        robot1Hand.clear();
        robot2Hand.clear();
        bottomCards.clear();
        playAreaPane.getChildren().clear();
        playerHandPane.getChildren().clear();
        robotInfoPane.getChildren().clear();
        isGameOver = false;
        isPlayerTurn = false;
        passLabel.setVisible(false);
        hideAllButtons();
        lastPlayedCards.clear();
        consecutivePasses = 0;
        lastPlayerWhoPlayed = -1;
        roundReset = false;

        String[] suits = {"Spade", "Heart", "Diamond", "Club"};
        List<String> names = new ArrayList<>();
        for (String suit : suits) {
            for (int num = 3; num <= 15; num++) {
                names.add(suit + num + ".png");
            }
        }
        names.add("JOKER-19.png");
        names.add("JOKER-20.png");
        Collections.shuffle(names);

        List<PokerCard> all = new ArrayList<>();
        for (String name : names) {
            Image img = loadImage("/IMG/PNG/" + name);
            if (img == null) {
                statusText.setText("错误：找不到图片 " + name);
                statusText.setLayoutX(400);
                statusText.setLayoutY(300);
                cardsPane.getChildren().add(statusText);
                return;
            }
            ImageView iv = new ImageView(img);
            iv.setFitHeight(200);
            iv.setFitWidth(140);
            int num = extractNumber(name);
            PokerCard card = new PokerCard(num, iv);
            card.setBackground(null);
            all.add(card);
        }

        int idx = 0;
        for (int i = 0; i < 17; i++) playerHand.add(all.get(idx++));
        for (int i = 0; i < 17; i++) robot1Hand.add(all.get(idx++));
        for (int i = 0; i < 17; i++) robot2Hand.add(all.get(idx++));
        for (int i = 0; i < 3; i++) bottomCards.add(all.get(idx++));

        showRobots();
        refreshPlayerHand();
        statusText.setText("准备抢地主");
        statusText.setLayoutX(650);
        statusText.setLayoutY(100);
    }

    private static int extractNumber(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) sb.append(c);
        }
        if (sb.length() > 0) return Integer.parseInt(sb.toString());
        return 1; // 默认
    }


    //进入出牌阶段
    static List<PokerCard> getHandByPlayer(int player) {
        switch (player) {
            case 0:
                return playerHand;
            case 1:
                return robot1Hand;
            case 2:
                return robot2Hand;
            default:
                return new ArrayList<>();
        }
    }

    // ==================== 出牌阶段 ====================
    //开始出牌
    private static void startPlayingPhase() {
        currentPlayer = landLord;
        isGameOver = false;
        lastPlayedCards.clear();
        consecutivePasses = 0;
        lastPlayerWhoPlayed = currentPlayer;
        roundReset = false;
        nextTurn();
    }

    //回合控制
    private static void nextTurn() {
        if (isGameOver) return;
        if (playerHand.isEmpty()) {
            statusText.setText("玩家胜利！");
            isGameOver = true;
            return;
        }
        if (robot1Hand.isEmpty()) {
            statusText.setText("机器人1胜利！");
            isGameOver = true;
            return;
        }
        if (robot2Hand.isEmpty()) {
            statusText.setText("机器人2胜利！");
            isGameOver = true;
            return;
        }

        //更新状态
        String name = getPlayerName(currentPlayer);
        statusText.setText("轮到 " + name + " 出牌");
        statusText.setLayoutX(650);
        statusText.setLayoutY(100);

        //区分玩家
        if (currentPlayer == 0) {
            isPlayerTurn = true;
            btnOut.setLayoutX(700);
            btnOut.setLayoutY(850);
            btnOut.setPrefSize(100, 50);
            btnPass.setLayoutX(850);
            btnPass.setLayoutY(850);
            btnPass.setPrefSize(100, 50);
            cardsPane.getChildren().addAll(btnOut, btnPass);
        } else {
            isPlayerTurn = false;
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(e -> robotTurn());
            delay.play();
        }
    }

    //========================================王思涵========================================
    private static void handlePlayerOut() {
        if (!isPlayerTurn || isGameOver) return;
        List<PokerCard> selected = new ArrayList<>();
        for (PokerCard card : playerHand) if (card.isSelected) selected.add(card);

        if (selected.isEmpty()) {
            statusText.setText("请先选牌！");
            return;
        }

        HandType currentType = HandType.analyze(selected);
        if (currentType == null) {
            statusText.setText("非法牌型！");
            return;
        }

        if (!lastPlayedCards.isEmpty() && !HandType.canBeat(currentType, HandType.analyze(lastPlayedCards))) {
            statusText.setText("管不上，请重新选择！");
            return;
        }

        // 出牌
        playerHand.removeAll(selected);
        lastPlayedCards.clear();
        lastPlayedCards.addAll(selected);
        lastPlayerWhoPlayed = 0;
        showPlayCards(selected, "玩家");
        refreshPlayerHand();
        cardsPane.getChildren().removeAll(btnOut, btnPass);
        isPlayerTurn = false;
        consecutivePasses = 0;
        roundReset = false;
        currentPlayer = (currentPlayer + 1) % 3;
        nextTurn();
    }

    private static void handlePlayerPass() {
        if (!isPlayerTurn || isGameOver) return;
        if (lastPlayedCards.isEmpty()) {
            statusText.setText("本轮必须出牌，不能不要！");
            return;
        }
        for (PokerCard card : playerHand) {
            if (card.isSelected) {
                card.isSelected = false;
                card.setTranslateY(0);
            }
        }
        showPass("玩家");
        cardsPane.getChildren().removeAll(btnOut, btnPass);
        isPlayerTurn = false;
        consecutivePasses++;
        if (consecutivePasses >= 2) {
            lastPlayedCards.clear();
            consecutivePasses = 0;
            currentPlayer = lastPlayerWhoPlayed;
            roundReset = true;
            statusText.setText("全部不要，轮到 " + getPlayerName(currentPlayer) + " 自由出牌");
        } else {
            currentPlayer = (currentPlayer + 1) % 3;
        }
        nextTurn();
    }

    //========================================王思涵========================================
    private static void robotTurn() {
        if (isGameOver) return;
        int robotId = currentPlayer;
        List<PokerCard> hand = (robotId == 1) ? robot1Hand : robot2Hand;
        String name = getPlayerName(robotId);

        HandType lastType = lastPlayedCards.isEmpty() ? null : HandType.analyze(lastPlayedCards);
        List<PokerCard> toPlay = AISearch.findValidCards(hand, lastType, roundReset);

        if (toPlay != null && !toPlay.isEmpty()) {
            // 能出牌
            hand.removeAll(toPlay);
            lastPlayedCards.clear();
            lastPlayedCards.addAll(toPlay);
            lastPlayerWhoPlayed = robotId;
            showPlayCards(toPlay, name);
            refreshRobots();
            consecutivePasses = 0;
            roundReset = false;
            currentPlayer = (currentPlayer + 1) % 3;
            if (hand.isEmpty()) {
                statusText.setText(name + " 胜利！");
                isGameOver = true;
            }
        } else {
            // 不要
            showPass(name);
            consecutivePasses++;
            if (consecutivePasses >= 2) {
                lastPlayedCards.clear();
                consecutivePasses = 0;
                currentPlayer = lastPlayerWhoPlayed;
                roundReset = true;
                statusText.setText("全部不要，轮到 " + getPlayerName(currentPlayer) + " 自由出牌");
            } else {
                currentPlayer = (currentPlayer + 1) % 3;
            }
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
        pause.setOnFinished(e -> nextTurn());
        pause.play();
    }

    //========================================肖松========================================

    // ==================== UI 辅助 ====================
    private static void refreshPlayerHand() {
        playerHandPane.getChildren().clear();
        if (playerHand.isEmpty()) return;
        playerHand.sort(Comparator.comparingInt(c -> c.num));
        double sw = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
        double cw = 140, overlap = 70;
        double total = (playerHand.size() - 1) * overlap + cw;
        double startX = (sw - total) / 2, y = 600;
        for (int i = 0; i < playerHand.size(); i++) {
            PokerCard card = playerHand.get(i);
            card.setLayoutX(startX + i * overlap);
            card.setLayoutY(y);
            card.setTranslateY(0);
            card.isSelected = false;
            card.setOnMouseClicked(e -> toggleSelect(card));
            playerHandPane.getChildren().add(card);
        }
    }

    private static void toggleSelect(PokerCard card) {
        if (!playerHand.contains(card)) return;
        if (card.isSelected) {
            card.setTranslateY(0);
            card.isSelected = false;
        } else {
            card.setTranslateY(-20);
            card.isSelected = true;
        }
    }

    private static void showRobots() {
        robotInfoPane.getChildren().clear();
        showRobotInfo(1, 50, 150);
        showRobotInfo(2, 1200, 150);
        refreshRobots();
    }

    private static void showRobotInfo(int id, int x, int y) {
        String[] heads = {"/IMG/Robot/head/head1.png", "/IMG/Robot/head/head2.png", "/IMG/Robot/head/head3.png",
                "/IMG/Robot/head/head4.png", "/IMG/Robot/head/head5.png", "/IMG/Robot/head/head7.png",
                "/IMG/Robot/head/head8.png"};
        String path = heads[new Random().nextInt(heads.length)];
        Image img = loadImage(path);
        if (img == null) return;
        ImageView hv = new ImageView(img);
        hv.setFitWidth(100);
        hv.setFitHeight(100);
        hv.setLayoutX(x);
        hv.setLayoutY(y);
        robotInfoPane.getChildren().add(hv);
        Text ct = new Text();
        ct.setFont(Font.font(20));
        ct.setStyle("-fx-text-fill: #727626;");
        ct.setLayoutX(x + 120);
        ct.setLayoutY(y + 50);
        ct.setId("robotCount_" + id);
        robotInfoPane.getChildren().add(ct);
    }

    private static void refreshRobots() {
        for (javafx.scene.Node node : robotInfoPane.getChildren()) {
            if (node instanceof Text && node.getId() != null && node.getId().startsWith("robotCount_")) {
                int id = Integer.parseInt(node.getId().split("_")[1]);
                int count = (id == 1) ? robot1Hand.size() : robot2Hand.size();
                ((Text) node).setText("牌数: " + count);
            }
        }
    }

    private static void showPlayCards(List<PokerCard> cards, String owner) {
        playAreaPane.getChildren().clear();
        if (cards.isEmpty()) return;
        Label label = new Label(owner + " 出牌");
        label.setFont(Font.font(24));
        label.setStyle("-fx-text-fill: #4168dc;");
        label.setLayoutX(700);
        label.setLayoutY(250);
        playAreaPane.getChildren().add(label);
        double sw = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
        double cw = 100, ov = -20;
        double total = (cards.size() - 1) * ov + cw;
        double sx = (sw - total) / 2, sy = 350;
        for (int i = 0; i < cards.size(); i++) {
            PokerCard card = cards.get(i);
            card.setFitSize(100, 140);
            card.setLayoutX(sx + i * ov);
            card.setLayoutY(sy);
            card.setOnMouseClicked(null);
            playAreaPane.getChildren().add(card);
        }
    }

    private static void showPass(String owner) {
        // 先移除避免重复添加
        playAreaPane.getChildren().remove(passLabel);
        passLabel.setText(owner + " 不要");
        passLabel.setLayoutX(700);
        passLabel.setLayoutY(350);
        passLabel.setVisible(true);
        playAreaPane.getChildren().add(passLabel);
        PauseTransition clear = new PauseTransition(Duration.seconds(2));
        clear.setOnFinished(e -> {
            passLabel.setVisible(false);
            playAreaPane.getChildren().remove(passLabel);
        });
        clear.play();
    }

    private static void hideAllButtons() {
        cardsPane.getChildren().removeAll(btnCall1, btnCall2, btnCall3, btnNoCall, btnOut, btnPass);
    }

    private static String getPlayerName(int id) {
        switch (id) {
            case 0:
                return "玩家";
            case 1:
                return "机器人1";
            case 2:
                return "机器人2";
            default:
                return "";
        }
    }


    // ==================== 内部类 PokerCard ====================
    public static class PokerCard extends Button {
        public int num;
        public boolean isSelected = false;

        public PokerCard(int num, ImageView img) {
            super("", img);
            this.num = num;
            setBackground(null);
        }

        public void setFitSize(double w, double h) {
            if (getGraphic() instanceof ImageView iv) {
                iv.setFitWidth(w);
                iv.setFitHeight(h);
            }
        }
    }
}