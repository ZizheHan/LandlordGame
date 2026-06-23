package main.java.com.hzz.landlord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public abstract class Player {

    protected String playerName; // 玩家昵称
    protected int playerId;      // 关联 login 模块的账号ID
    protected List<Card> handCards; // 手牌列表
    protected boolean isLandlord;   // 是否为地主

    public Player(String playerName, int playerId) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.handCards = new ArrayList<>();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public List<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<Card> handCards) {
        this.handCards = handCards;
    }

    public boolean isLandlord() {
        return isLandlord;
    }

    public void setLandlord(boolean landlord) {
        isLandlord = landlord;
    }

    public void addCard(Card c) {
        if (this.handCards == null) {
            this.handCards = new ArrayList<>();
        }
        this.handCards.add(c);
    }

    //出牌
    public void removeCard(List<Card> list) {
        if (list != null && !list.isEmpty()) {
            this.handCards.removeAll(list);
        }
    }

    //理牌
    public void sortCard() {
        if (this.handCards != null) {
            Collections.sort(this.handCards);
        }
    }

    //展示
    public void showHandCard() {
        System.out.print("[" + this.playerName + "] 的手牌: ");
        if (this.handCards == null || this.handCards.isEmpty()) {
            System.out.println("(无牌)");
        } else {
            for (Card card : this.handCards) {
                System.out.print(card.toString() + " ");
            }
            System.out.println();
        }
    }

    public abstract List<Card> playCard(Scanner scanner, List<Card> lastCards);
}
