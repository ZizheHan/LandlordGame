//========================================王思涵========================================
package main.java.com.hzz.landlord;

import java.util.*;

public class AISearch {

    //AI 出牌搜索
    public static List<CardsGame.PokerCard> findValidCards(List<CardsGame.PokerCard> hand, HandType lastType, boolean reset) {
        if (hand.isEmpty()) return null;

        // 自由出牌：出最小牌型
        if (lastType == null || reset) {
            List<List<CardsGame.PokerCard>> allCombos = getAllPossibleCombos(hand);
            if (!allCombos.isEmpty()) {
                allCombos.sort(Comparator.comparingInt(a -> {
                    HandType t = HandType.analyze(a);
                    return t == null ? 999 : t.getAvgPoint();
                }));
                return allCombos.get(0);
            }
            return null;
        }

        // 需要压牌
        List<List<CardsGame.PokerCard>> candidates = getAllPossibleCombos(hand);
        // 1. 同类型更大
        for (List<CardsGame.PokerCard> combo : candidates) {
            HandType cur = HandType.analyze(combo);
            if (cur != null && HandType.canBeat(cur, lastType)) {
                return combo;
            }
        }
        // 2. 炸弹/火箭压制（火箭最大，炸弹其次）
        if (lastType.isBomb() && !lastType.isRocket()) {
            for (List<CardsGame.PokerCard> combo : candidates) {
                HandType cur = HandType.analyze(combo);
                if (cur != null && cur.isBomb() && (cur.isRocket() || HandType.canBeat(cur, lastType))) {
                    return combo;
                }
            }
        } else if (!lastType.isRocket()) {
            for (List<CardsGame.PokerCard> combo : candidates) {
                HandType cur = HandType.analyze(combo);
                if (cur != null && cur.isBomb()) {
                    return combo;
                }
            }
        }
        return null;
    }


    //返回一手牌所有可能组成的合法牌型组合（List of List< PokerCard >）

    public static List<List<CardsGame.PokerCard>> getAllPossibleCombos(List<CardsGame.PokerCard> hand) {
        List<List<CardsGame.PokerCard>> result = new ArrayList<>();
        Map<Integer, List<CardsGame.PokerCard>> groups = new HashMap<>();
        for (CardsGame.PokerCard c : hand) {
            groups.computeIfAbsent(c.num, k -> new ArrayList<>()).add(c);
        }

        // 单张
        for (CardsGame.PokerCard c : hand) {
            result.add(Collections.singletonList(c));
        }

        // 对子
        for (List<CardsGame.PokerCard> list : groups.values()) {
            if (list.size() >= 2) result.add(list.subList(0, 2));
        }

        // 三不带
        for (List<CardsGame.PokerCard> list : groups.values()) {
            if (list.size() >= 3) result.add(new ArrayList<>(list.subList(0, 3)));
        }

        // 三带一
        for (Map.Entry<Integer, List<CardsGame.PokerCard>> e : groups.entrySet()) {
            if (e.getValue().size() >= 3) {
                List<CardsGame.PokerCard> triple = e.getValue().subList(0, 3);
                for (CardsGame.PokerCard c : hand) {
                    if (c.num != e.getKey()) {
                        List<CardsGame.PokerCard> combo = new ArrayList<>(triple);
                        combo.add(c);
                        result.add(combo);
                        break;
                    }
                }
            }
        }

        // 三带二
        for (Map.Entry<Integer, List<CardsGame.PokerCard>> e : groups.entrySet()) {
            if (e.getValue().size() >= 3) {
                List<CardsGame.PokerCard> triple = e.getValue().subList(0, 3);
                for (Map.Entry<Integer, List<CardsGame.PokerCard>> pair : groups.entrySet()) {
                    if (pair.getKey() != e.getKey() && pair.getValue().size() >= 2) {
                        List<CardsGame.PokerCard> combo = new ArrayList<>(triple);
                        combo.addAll(pair.getValue().subList(0, 2));
                        result.add(combo);
                        break;
                    }
                }
            }
        }

        // 顺子
        List<Integer> keys = new ArrayList<>(groups.keySet());
        Collections.sort(keys);
        for (int start : keys) {
            if (start > 10) continue;  // 顺子最大到A(14)，起始不能太高
            int len = 0;
            for (int i = start; i <= 14; i++) {
                if (groups.containsKey(i) && !groups.get(i).isEmpty()) len++;
                else break;
            }
            for (int end = start + 4; end < start + len; end++) {
                List<CardsGame.PokerCard> straight = new ArrayList<>();
                for (int i = start; i <= end; i++) {
                    straight.add(groups.get(i).get(0));
                }
                result.add(straight);
            }
        }

        // 炸弹
        for (List<CardsGame.PokerCard> list : groups.values()) {
            if (list.size() == 4) result.add(new ArrayList<>(list));
        }

        // 王炸
        if (groups.containsKey(19) && groups.containsKey(20)) {
            List<CardsGame.PokerCard> rocket = new ArrayList<>();
            rocket.add(groups.get(19).get(0));
            rocket.add(groups.get(20).get(0));
            result.add(rocket);
        }

        // 姊妹对（连续三对）
        for (int start : keys) {
            if (start > 12) continue;
            if (groups.containsKey(start) && groups.get(start).size() >= 2 &&
                    groups.containsKey(start + 1) && groups.get(start + 1).size() >= 2 &&
                    groups.containsKey(start + 2) && groups.get(start + 2).size() >= 2) {
                List<CardsGame.PokerCard> sister = new ArrayList<>();
                sister.addAll(groups.get(start).subList(0, 2));
                sister.addAll(groups.get(start + 1).subList(0, 2));
                sister.addAll(groups.get(start + 2).subList(0, 2));
                result.add(sister);
            }
        }

        // 飞机（不带翅膀，至少两个连续三张）
        for (int start : keys) {
            if (start > 13) continue;
            int len = 0;
            for (int i = start; i <= 14; i++) {
                if (groups.containsKey(i) && groups.get(i).size() >= 3) len++;
                else break;
            }
            for (int end = start + 1; end < start + len; end++) {
                List<CardsGame.PokerCard> plane = new ArrayList<>();
                for (int i = start; i <= end; i++) {
                    plane.addAll(groups.get(i).subList(0, 3));
                }
                result.add(plane);
            }
        }

        return result.stream().distinct().collect(java.util.stream.Collectors.toList());
    }
}