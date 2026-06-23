package main.java.com.hzz.landlord;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, int id) {
        super(name, id);
    }

    @Override
    public List<Card> playCard(Scanner scanner, List<Card> lastCards) {
        List<Card> myCards = this.getHandCards();
        if (myCards.isEmpty()) return new ArrayList<>();

        while (true) {
            System.out.println("\n你的手牌:");
            for (int i = 0; i < myCards.size(); i++) {
                System.out.print("[" + i + ":" + myCards.get(i) + "] ");
            }
            System.out.println();

            String mustPlayMsg = (lastCards != null && !lastCards.isEmpty()) ? " (上家出了牌，你可以选择不要)" : "";
            System.out.print("请输入出牌索引(空格分隔，如 0 1 2)，或输入 -1 不要" + mustPlayMsg + ": ");

            String line = scanner.nextLine();
            if ("-1".equals(line.trim())) {
                return new ArrayList<>();
            }

            try {
                String[] parts = line.split("\\s+");
                List<Card> selected = new ArrayList<>();
                boolean validIndex = true;
                for (String p : parts) {
                    int idx = Integer.parseInt(p);
                    if (idx < 0 || idx >= myCards.size()) {
                        validIndex = false;
                        break;
                    }
                    selected.add(myCards.get(idx));
                }
                if (!validIndex) {
                    System.out.println("索引越界，请重新输入。");
                    continue;
                }
                // 校验规则
                if (GameRule.canBeat(selected, lastCards)) {
                    return selected;
                } else {
                    System.out.println("牌型不合法或打不过上家，请重新输入。");
                }
            } catch (Exception e) {
                System.out.println("输入格式错误，请输入数字索引。");
            }
        }
    }
}