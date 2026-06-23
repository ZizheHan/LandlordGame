package main.java.com.hzz.landlord;

import java.util.*;
import java.util.stream.Collectors;

public class GameRule {

    // 定义牌型常量
    public static final int TYPE_ERROR = 0;      // 错误/无法识别
    public static final int TYPE_SINGLE = 1;     // 单张
    public static final int TYPE_PAIR = 2;       // 对子
    public static final int TYPE_TRIPLE_ONE = 4; // 三带一
    public static final int TYPE_BOMB = 6;       // 炸弹
    public static final int TYPE_ROCKET = 7;     // 王炸


    //获取牌的点数权重（用于比较大小）

    private static int getCardValue(Card card) {
        int rank = card.getRank(); // 获取 int 类型的点数
        if (rank == 16) return 16; // 小王
        if (rank == 17) return 17; // 大王
        if (rank == 11) return 11; // J
        if (rank == 12) return 12; // Q
        if (rank == 13) return 13; // K
        if (rank == 14) return 14; // A
        if (rank == 15) return 15; // 2
        return rank; // 3-10 直接返回
    }
    // 分析手牌的牌型

    public static Map<String, Object> analyzeHand(List<Card> cards) {
        if (cards == null || cards.isEmpty()) return null;

        Map<String, Object> result = new HashMap<>();
        List<Integer> values = cards.stream().map(GameRule::getCardValue).sorted().collect(Collectors.toList());

        // 统计每个点数的出现次数
        Map<Integer, Long> countMap = values.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        List<Map.Entry<Integer, Long>> counts = new ArrayList<>(countMap.entrySet());
        counts.sort((a, b) -> a.getValue().compareTo(b.getValue()) != 0 ?
                Long.compare(b.getValue(), a.getValue()) : a.getKey().compareTo(b.getKey()));

        int size = cards.size();

        // --- 1. 王炸 ---
        if (size == 2 && values.contains(16) && values.contains(17)) {
            result.put("type", TYPE_ROCKET);
            result.put("mainValue", 999); // 王炸最大
            return result;
        }

        // --- 2. 炸弹 (4张相同) ---
        if (size == 4 && counts.get(0).getValue() == 4) {
            result.put("type", TYPE_BOMB);
            result.put("mainValue", counts.get(0).getKey());
            return result;
        }

        // --- 3. 单张 ---
        if (size == 1) {
            result.put("type", TYPE_SINGLE);
            result.put("mainValue", values.get(0));
            return result;
        }

        // --- 4. 对子 ---
        if (size == 2 && counts.get(0).getValue() == 2) {
            result.put("type", TYPE_PAIR);
            result.put("mainValue", counts.get(0).getKey());
            return result;
        }

        // --- 6. 三带一 ---
        if (size == 4 && counts.get(0).getValue() == 3) {
            result.put("type", TYPE_TRIPLE_ONE);
            result.put("mainValue", counts.get(0).getKey());
            return result;
        }

        // 暂时不支持顺子、连对、飞机等复杂牌型，可根据需要扩展
        return null; // 无法识别的牌型
    }


    //比较两组牌的大小

    public static boolean canBeat(List<Card> currentCards, List<Card> lastCards) {
        // 1. 如果是第一手牌，只要牌型合法即可出
        if (lastCards == null || lastCards.isEmpty()) {
            return analyzeHand(currentCards) != null;
        }

        Map<String, Object> currentInfo = analyzeHand(currentCards);
        Map<String, Object> lastInfo = analyzeHand(lastCards);

        // 2. 如果当前牌型都不合法，直接 false
        if (currentInfo == null) return false;

        int currentType = (int) currentInfo.get("type");
        int lastType = (int) lastInfo.get("type");

        // 3. 王炸无敌
        if (currentType == TYPE_ROCKET) return true;

        // 4. 炸弹可以炸非炸弹
        if (currentType == TYPE_BOMB && lastType != TYPE_BOMB && lastType != TYPE_ROCKET) return true;

        // 5. 同类型比较：牌型必须一致，且点数更大
        if (currentType == lastType) {
            int currentVal = (int) currentInfo.get("mainValue");
            int lastVal = (int) lastInfo.get("mainValue");
            return currentVal > lastVal;
        }

        // 6. 其他情况（如用对子管单张，或用小的炸弹管大的普通牌）均不可行
        return false;
    }
}
