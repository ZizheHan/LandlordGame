package main.java.com.hzz.landlord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 牌堆：负责初始化整副牌、洗牌、发牌、存储底牌
public class CardDeck {
    // 全部54张卡牌
    private List<Card> allCard;
    // 底牌固定3张
    private List<Card> bottomCard;

    public CardDeck() {
        allCard = new ArrayList<>();
        bottomCard = new ArrayList<>();
    }

    public List<Card> getAllCard() {
        return allCard;
    }

    public void setAllCard(List<Card> allCard) {
        this.allCard = allCard;
    }

    public List<Card> getBottomCard() {
        return bottomCard;
    }

    public void setBottomCard(List<Card> bottomCard) {
        this.bottomCard = bottomCard;
    }

    // 初始化牌堆
    public void initCard() {
        // 清空旧牌
        allCard.clear();
        bottomCard.clear();

        // 四种花色
        String[] suitArray = {"♠", "♥", "♣", "♦"};
        // 普通牌权重：3到2
        int[] rankArray = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        // 卡牌自增id
        int cardId = 0;

        // 循环生成52张普通花色牌
        for (String suit : suitArray) {
            for (int rank : rankArray) {
                Card newCard = new Card(suit, cardId, rank);
                allCard.add(newCard);
                cardId++;
            }
        }

        // 添加小王、大王
        Card smallKing = new Card(null, cardId++, 16);
        Card bigKing = new Card(null, cardId++, 17);
        allCard.add(smallKing);
        allCard.add(bigKing);
        System.out.println("牌堆初始化完成，总牌数：" + allCard.size());
    }

    // 洗牌
    public void shuffleCard() {
        if (allCard.size() != 54) {
            System.out.println("牌未初始化完成！");
            return;
        }
        // 工具类随机打乱集合
        Collections.shuffle(allCard);
        System.out.println("洗牌完成");
    }

    //发牌
    public void dealCard(Player p1, Player p2, Player p3) {
        if (allCard.size() != 54) {
            System.out.println("牌未初始化完成！");
            return;
        }
        // 清空玩家旧手牌
        p1.getHandCards().clear();
        p2.getHandCards().clear();
        p3.getHandCards().clear();
        bottomCard.clear();

        // 前51张用于分发，后3张作为底牌
        List<Card> dealPool = new ArrayList<>(allCard.subList(0, 51));
        bottomCard.addAll(new ArrayList<>(allCard.subList(51, 54)));

        // 循环轮流发牌
        for (int i = 0; i < dealPool.size(); i++) {
            Card current = dealPool.get(i);
            if (i % 3 == 0) {
                p1.getHandCards().add(current);
            } else if (i % 3 == 1) {
                p2.getHandCards().add(current);
            } else {
                p3.getHandCards().add(current);
            }
        }
        System.out.println("发牌结束");
    }
}