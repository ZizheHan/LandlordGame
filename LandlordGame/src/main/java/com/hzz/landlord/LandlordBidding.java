//========================================杨满豪========================================
package main.java.com.hzz.landlord;

import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 叫分（抢地主）阶段逻辑 —— 从 CardsGame 中提取出来的独立类。
 * 通过 BiddingHost 回调接口与 CardsGame 通信，不直接操作 CardsGame 的私有状态。
 */
public class LandlordBidding {

    // ========== 叫分状态 ==========
    private int currentBid = 0;
    private final int[] bids = new int[3];
    private final boolean[] hasCalled = new boolean[3];
    private int biddingPlayerIndex = 0;

    // ========== UI 引用（由 CardsGame 传入） ==========
    private final Pane cardsPane;
    private final Text statusText;
    private final Button btnCall1, btnCall2, btnCall3, btnNoCall;

    // ========== 宿主回调 ==========
    private final BiddingHost host;

    /**
     * CardsGame 需要实现的回调接口，供叫分阶段获取数据和控制游戏流程。
     */
    public interface BiddingHost {
        List<CardsGame.PokerCard> getHandByPlayer(int player);
        String getPlayerName(int player);
        void refreshPlayerHand();
        void refreshRobots();
        /** 确定地主：设置 landLord、分配底牌、排序、刷新手牌显示 */
        void setLandlord(int player);
        /** 进入出牌阶段 */
        void startPlayingPhase();
        /** 无人叫分时重新发牌 */
        void restartGame();
    }

    /**
     * @param cardsPane  游戏主面板（用于添加/移除叫分按钮）
     * @param statusText 状态文本
     * @param btnCall1   叫1分按钮
     * @param btnCall2   叫2分按钮
     * @param btnCall3   叫3分按钮
     * @param btnNoCall  不叫按钮
     * @param host       宿主回调实现
     */
    public LandlordBidding(Pane cardsPane, Text statusText,
                           Button btnCall1, Button btnCall2, Button btnCall3, Button btnNoCall,
                           BiddingHost host) {
        this.cardsPane = cardsPane;
        this.statusText = statusText;
        this.btnCall1 = btnCall1;
        this.btnCall2 = btnCall2;
        this.btnCall3 = btnCall3;
        this.btnNoCall = btnNoCall;
        this.host = host;

        // 绑定按钮事件（替代原 CardsGame 静态块中的绑定）
        btnCall1.setOnAction(e -> handleBid(1));
        btnCall2.setOnAction(e -> handleBid(2));
        btnCall3.setOnAction(e -> handleBid(3));
        btnNoCall.setOnAction(e -> handleBid(0));
    }

    // ==================== 入口 ====================

    /** 开始叫分阶段 */
    public void start() {
        reset();
        Random random = new Random();
        biddingPlayerIndex = random.nextInt(3);
        statusText.setText(host.getPlayerName(biddingPlayerIndex) + " 先开始叫分");
        showCallButton(biddingPlayerIndex);
    }

    /** 重置叫分状态（每次新游戏开始时调用） */
    private void reset() {
        currentBid = 0;
        Arrays.fill(bids, 0);
        Arrays.fill(hasCalled, false);
        biddingPlayerIndex = 0;
    }

    // ==================== 显示叫分按钮 ====================

    private void showCallButton(int playerIndex) {
        if (hasCalled[playerIndex]) return;
        if (playerIndex == 0) {
            // 人类玩家：显示叫分按钮
            btnCall1.setLayoutX(600); btnCall1.setLayoutY(500); btnCall1.setPrefSize(80, 50);
            btnCall2.setLayoutX(700); btnCall2.setLayoutY(500); btnCall2.setPrefSize(80, 50);
            btnCall3.setLayoutX(800); btnCall3.setLayoutY(500); btnCall3.setPrefSize(80, 50);
            btnNoCall.setLayoutX(900); btnNoCall.setLayoutY(500); btnNoCall.setPrefSize(80, 50);
            cardsPane.getChildren().addAll(btnCall1, btnCall2, btnCall3, btnNoCall);
            statusText.setText("轮到 " + host.getPlayerName(playerIndex) + " 叫分 (当前最高: " + currentBid + ")");
        } else {
            // AI 玩家
            handleAICall(playerIndex);
        }
    }

    // ==================== 人类玩家叫分 ====================

    private void handleBid(int bid) {
        cardsPane.getChildren().removeAll(btnCall1, btnCall2, btnCall3, btnNoCall);
        bids[0] = bid;
        hasCalled[0] = true;
        if (bid > currentBid) {
            currentBid = bid;
            statusText.setText("玩家叫" + bid + "分");
        } else if (bid != 0) {
            statusText.setText("叫分低于当前最高 (" + currentBid + ")，无效");
            bids[0] = 0;
        } else {
            statusText.setText("玩家不叫");
        }
        nextBiddingPlayer();
    }

    // ==================== AI 叫分 ====================

    private void handleAICall(int playerIndex) {
        int bid = getAIBid(host.getHandByPlayer(playerIndex));
        bids[playerIndex] = bid;
        hasCalled[playerIndex] = true;
        if (bid > currentBid) {
            currentBid = bid;
            statusText.setText(host.getPlayerName(playerIndex) + " 叫" + bid + "分");
        } else if (bid != 0) {
            statusText.setText(host.getPlayerName(playerIndex) + " 叫" + bid + "分 (低于当前最高)");
        } else {
            statusText.setText(host.getPlayerName(playerIndex) + " 不叫");
        }
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(e -> nextBiddingPlayer());
        delay.play();
    }

    private int getAIBid(List<CardsGame.PokerCard> hand) {
        int strength = calculateHandStrength(hand);
        Random r = new Random();
        int bid;
        if (strength >= 12) bid = 3;
        else if (strength >= 8) bid = 2;
        else if (strength >= 5) bid = 1;
        else bid = r.nextInt(2);
        if (bid <= currentBid) bid = 0;
        return bid;
    }

    private int calculateHandStrength(List<CardsGame.PokerCard> hand) {
        int s = 0;
        for (CardsGame.PokerCard c : hand) {
            if (c.num == 20) s += 4;      // 大王
            else if (c.num == 19) s += 3; // 小王
            else if (c.num == 15) s += 2; // 2
            else if (c.num >= 13) s += 1;
        }
        return s;
    }

    // ==================== 轮转 ====================

    private void nextBiddingPlayer() {
        boolean allCalled = true;
        for (boolean c : hasCalled) if (!c) { allCalled = false; break; }
        if (allCalled) { determineLandlord(); return; }

        int next = (biddingPlayerIndex + 1) % 3;
        while (hasCalled[next]) {
            next = (next + 1) % 3;
            allCalled = true;
            for (boolean c : hasCalled) if (!c) { allCalled = false; break; }
            if (allCalled) { determineLandlord(); return; }
        }
        biddingPlayerIndex = next;
        showCallButton(biddingPlayerIndex);
    }

    // ==================== 确定地主 ====================

    private void determineLandlord() {
        int maxBid = 0, maxBidder = -1;
        for (int i = 0; i < 3; i++) {
            if (bids[i] > maxBid) { maxBid = bids[i]; maxBidder = i; }
        }
        if (maxBidder != -1) {
            statusText.setText(host.getPlayerName(maxBidder) + " 成为地主 (叫" + maxBid + "分)!");
            // 通知宿主：设置地主、分配底牌、刷新
            host.setLandlord(maxBidder);
        } else {
            statusText.setText("无人叫分，重新发牌!");
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> host.restartGame());
            delay.play();
            return;
        }
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> host.startPlayingPhase());
        delay.play();
    }

    // ==================== 供外部查询（如需） ====================

    public int getCurrentBid() { return currentBid; }
    public int[] getBids() { return bids; }
}