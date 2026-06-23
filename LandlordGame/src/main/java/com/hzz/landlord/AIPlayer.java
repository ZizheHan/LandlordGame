package main.java.com.hzz.landlord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AIPlayer extends Player {

    private Random random;

    public AIPlayer(String name, int id) {
        super(name, id);
        this.random = new Random();
    }

    //AI出牌逻辑
    @Override
    public List<Card> playCard(Scanner scanner,List<Card> lastCards) {
        List<Card> hand = getHandCards();
        if (hand == null || hand.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 如果上家没出牌（比如自己是地主先出，或者别人都要不起）
        if (lastCards == null || lastCards.isEmpty()) {
            // 简单策略：出最小的牌
            List<Card> play = new ArrayList<>();
            // 因为手牌现在是从小到大排列的，索引 0 就是最小的牌
            play.add(hand.get(0));
            System.out.println("[AI] " + getPlayerName() + " 率先出牌: " + play);
            return play;
        }

        // 2. 上家出了牌，AI 需要管牌
        // 因为手牌是从小到大排列的，正序遍历，找一张最小的、能管得上的单牌
        for (int i = 0; i < hand.size(); i++) {
            List<Card> attempt = new ArrayList<>();
            attempt.add(hand.get(i));

            if (GameRule.canBeat(attempt, lastCards)) {
                System.out.println("[AI] " + getPlayerName() + " 跟牌: " + attempt);
                return attempt;
            }
        }

        // 3. 管不上，选择不要
        System.out.println("[AI] " + getPlayerName() + " 选择不要");
        return new ArrayList<>();
    }
}