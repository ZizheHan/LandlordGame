//========================================韩梓哲========================================
package main.java.com.hzz.landlord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandType {

    // 牌型常量
    public static final int SINGLE = 1;         //单张
    public static final int PAIR = 2;           //对子
    public static final int TRIPLE = 3;         //三张
    public static final int TRIPLE_ONE = 4;     //三带一
    public static final int TRIPLE_TWO = 5;     //三带二
    public static final int STRAIGHT = 6;       //顺子
    public static final int BOMB = 7;           //炸弹
    public static final int ROCKET = 8;         //王炸
    public static final int SISTER_PAIR = 9;    //连对
    public static final int PLANE = 10;         //飞机

    private final int type;        //牌型类型
    private final int mainValue;   //主点数：比大小的权重
    private final int length;      //顺子长度、姊妹对对数、飞机节数
    private final int wingType;    //翅膀: 0=无, 1=单, 2=对

    public HandType(int type, int mainValue, int length, int wingType) {
        this.type = type;
        this.mainValue = mainValue;
        this.length = length;
        this.wingType = wingType;
    }

    public int getType() {
        return type;
    }

    public int getMainValue() {
        return mainValue;
    }

    public int getLength() {
        return length;
    }

    public int getWingType() {
        return wingType;
    }

    public boolean isBomb() {
        return type == BOMB || type == ROCKET;
    }

    public boolean isRocket() {
        return type == ROCKET;
    }

    //估算手牌权重
    public int getAvgPoint() {
        if (type == ROCKET) return 100;
        if (type == BOMB) return 60 + mainValue;
        return mainValue;
    }

    //牌型分析
    public static HandType analyze(List<CardsGame.PokerCard> cards) {
        if (cards == null || cards.isEmpty()) return null;

        //提取点数并排序，转化成数字列表
        List<Integer> values = cards.stream()
                .map(c -> c.num)
                .sorted()
                .collect(Collectors.toList());

        //统计每个点数出现的次数
        Map<Integer, Long> countMap = values.stream()
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()));

        int size = values.size();

        // 王炸
        if (size == 2 && countMap.containsKey(19) && countMap.containsKey(20) && countMap.size() == 2)
            return new HandType(ROCKET, 99, 2, 0);

        //单张
        if (size == 1)
            return new HandType(SINGLE, values.get(0), 1, 0);

        //对子
        if (size == 2 && values.get(0).equals(values.get(1)))
            return new HandType(PAIR, values.get(0), 1, 0);

        //三张
        if (size == 3 && countMap.size() == 1)
            return new HandType(TRIPLE, values.get(0), 1, 0);

        if (size == 4) {
            //炸弹
            if (countMap.size() == 1)
                return new HandType(BOMB, values.get(0), 1, 0);
            //三带一
            for (Map.Entry<Integer, Long> e : countMap.entrySet())
                if (e.getValue() == 3)
                    return new HandType(TRIPLE_ONE, e.getKey(), 1, 1);
        }

        //三带二
        if (size == 5) {
            boolean triple = false, pair = false;
            int tripleVal = 0;
            for (Map.Entry<Integer, Long> e : countMap.entrySet()) {
                if (e.getValue() == 3) {
                    triple = true;
                    tripleVal = e.getKey();
                }
                if (e.getValue() == 2) pair = true;
            }
            if (triple && pair && countMap.size() == 2)
                return new HandType(TRIPLE_TWO, tripleVal, 1, 2);
        }

        //顺子，至少5张，全部单张，点数连续，最大不超过14
        if (size >= 5 && countMap.values().stream().allMatch(v -> v == 1)) {
            int min = values.get(0);
            int max = values.get(size - 1);
            if (max <= 14 && max - min + 1 == size)
                return new HandType(STRAIGHT, max, size, 0);
        }

        //连对
        if (size == 6 && countMap.values().stream().allMatch(v -> v == 2) && countMap.size() == 3) {
            List<Integer> kv = new ArrayList<>(countMap.keySet());
            Collections.sort(kv);
            if (kv.get(2) - kv.get(0) == 2 && kv.get(1) - kv.get(0) == 1 && kv.get(2) - kv.get(1) == 1)
                return new HandType(SISTER_PAIR, kv.get(0), 3, 0);
        }

        //飞机，不带翅膀，至少6张，全部三张，点数连续，最大不超过14
        if (size >= 6 && size % 3 == 0 && countMap.values().stream().allMatch(v -> v == 3)) {
            List<Integer> kv = new ArrayList<>(countMap.keySet());
            Collections.sort(kv);
            boolean cont = true;
            for (int i = 1; i < kv.size(); i++)
                if (kv.get(i) - kv.get(i - 1) != 1)
                    cont = false;
            if (cont && kv.get(kv.size() - 1) <= 14)
                return new HandType(PLANE, kv.get(0), kv.size(), 0);
        }

        return null;
    }

    //比较大小
    public static boolean canBeat(HandType cur, HandType last) {
        if (cur == null || last == null) return false;
        //王炸
        if (cur.type == ROCKET) return true;
        if (last.type == ROCKET) return false;
        //炸弹
        if (cur.type == BOMB && last.type == BOMB) return cur.mainValue > last.mainValue;
        if (cur.type == BOMB && last.type != BOMB) return true;
        if (last.type == BOMB && cur.type != BOMB) return false;

        // 同类型、同长度比较主值
        if (cur.type != last.type || cur.length != last.length) return false;
        return cur.mainValue > last.mainValue;
    }
}