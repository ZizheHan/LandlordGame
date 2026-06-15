package main.java.com.hzz.landlord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 牌堆类，组合多个Card对象
 * 负责初始化整副牌、洗牌、发牌、存储底牌
 */
public class CardDeck {
    // 存放完整54张卡牌集合
    private List<Card> allCard;
    // 斗地主底牌集合，固定3张
    private List<Card> bottomCard;

    /**
     * 无参构造，初始化两个卡牌集合
     */
    public CardDeck() {
        allCard = new ArrayList<>();
        bottomCard = new ArrayList<>();
    }

    // ---------------------- Getter Setter ----------------------
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

    /**
     * 初始化54张完整扑克牌，对应类图initCard方法
     */
    public void initCard() {
        // 清空旧牌
        allCard.clear();
        bottomCard.clear();

        // 定义四种花色符号
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

    /**
     * 打乱牌堆顺序洗牌，对应类图shuffleCard方法
     */
    public void shuffleCard() {
        if (allCard.size() != 54) {
            System.out.println("牌未初始化，请先执行initCard()");
            return;
        }
        // 工具类随机打乱集合
        Collections.shuffle(allCard);
        System.out.println("洗牌完成");
    }

    /**
     * 给三位玩家发牌，对应类图dealCard方法
     * 规则：51张轮流发，每人17张；最后3张留作底牌
     *
     * @param p1 玩家1
     * @param p2 玩家2
     * @param p3 玩家3
     */
    public void dealCard(Player p1, Player p2, Player p3) {
        if (allCard.size() != 54) {
            System.out.println("无法发牌，请先初始化并洗牌");
            return;
        }
        // 清空玩家旧手牌
        p1.getHandCard().clear();
        p2.getHandCard().clear();
        p3.getHandCard().clear();
        bottomCard.clear();

        // 前51张用于分发，后3张作为底牌
        List<Card> dealPool = allCard.subList(0, 51);
        bottomCard.addAll(allCard.subList(51, 54));

        // 循环轮流发牌
        for (int i = 0; i < dealPool.size(); i++) {
            Card current = dealPool.get(i);
            if (i % 3 == 0) {
                p1.getHandCard().add(current);
            } else if (i % 3 == 1) {
                p2.getHandCard().add(current);
            } else {
                p3.getHandCard().add(current);
            }
        }
        System.out.println("发牌结束，底牌3张，每位玩家17张手牌");
    }
}
