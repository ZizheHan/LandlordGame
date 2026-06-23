package main.java.com.hzz.landlord;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    // 全局Scanner，避免多处创建导致输入流冲突
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Register register = new Register();
        Account account = new Account();

        while (true) {
            System.out.println("\n===== 欢迎使用斗地主系统 =====");
            System.out.println("1. 注册账号");
            System.out.println("2. 登录账号并开始游戏");
            System.out.println("3. 打印所有账号");
            System.out.println("4. 清空所有账号");
            System.out.println("0. 退出系统");
            System.out.print("请输入选项: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("请输入有效的数字选项！");
                continue;
            }

            switch (choice) {
                case 1:
                    handleRegister(register);
                    break;
                case 2:
                    handleLoginAndStartGame(register, account);
                    break;
                case 3:
                    handlePrintAccounts(register);
                    break;
                case 4:
                    handleClearAccounts(register);
                    break;
                case 0:
                    System.out.println("感谢使用，再见！");
                    scanner.close();
                    return;
                default:
                    System.out.println("无效的选项，请重新输入！");
            }
        }
    }

    //功能模块封装

    private static void handleRegister(Register register) {
        try {
            register.Create_Account();
            System.out.println("注册流程结束。");
        } catch (Exception e) {
            System.out.println("注册过程中发生错误：" + e.getMessage());
        }
    }

    private static void handlePrintAccounts(Register register) {
        try {
            register.printA();
        } catch (Exception e) {
            System.out.println("读取账号失败：" + e.getMessage());
        }
    }

    private static void handleClearAccounts(Register register) {
        try {
            register.Clear_All_Account();
            System.out.println("所有账号已清空！");
        } catch (Exception e) {
            System.out.println("清空失败：" + e.getMessage());
        }
    }

    /**
     * 登录并启动游戏的核心逻辑
     */
    private static void handleLoginAndStartGame(Register register, Account account) {
        try {
            // 1. 执行登录
            account.Return_Count();
            account.LoginAccount();
            System.out.println("\n登录成功，正在进入游戏大厅！！");


            String currentUserName = "玩家1";

            try {
                java.lang.reflect.Field f = account.getClass().getDeclaredField("input_name");
                f.setAccessible(true);
                currentUserName = (String) f.get(account);
            } catch (Exception e) { /* 忽略反射错误，使用默认名 */ }

            HumanPlayer human = new HumanPlayer(currentUserName, 1001);
            AIPlayer ai1 = new AIPlayer("电脑玩家A", 2001);
            AIPlayer ai2 = new AIPlayer("电脑玩家B", 2002);

            List<Player> players = new ArrayList<>();
            players.add(human);
            players.add(ai1);
            players.add(ai2);

            // 3. 启动游戏流程
            startGameLoop(players);

        } catch (Exception e) {
            System.out.println("登录或游戏启动失败：" + e.getMessage());
            e.printStackTrace();
        }
    }


    //游戏主逻辑：发牌 抢地主 打牌

    private static void startGameLoop(List<Player> players) {
        System.out.println("\n========== 游戏开始 ==========");

        // 初始化牌堆
        CardDeck deck = new CardDeck();
        deck.initCard();
        deck.shuffleCard();
        deck.dealCard(players.get(0), players.get(1), players.get(2));

        // 显示玩家手牌
        System.out.println("\n--- 发牌结果 ---");
        for (Player p : players) {
            p.sortCard();
            if (p instanceof HumanPlayer) {
                p.showHandCard();
            }
        }

        // 抢地主阶段
        RobLandlordStage robStage = new RobLandlordStage();
        Player landlord = robStage.startRobbing(players, deck);

        if (landlord == null) {
            System.out.println("游戏结束（无人叫分且重发失败）。");
            return;
        }

        // 正式打牌阶段
        playGame(players, landlord);
    }

    //打牌逻辑
    private static void playGame(List<Player> players, Player landlord) {
        System.out.println("\n========== 正式打牌阶段 ==========");

        // 简单逻辑：地主先出牌
        int currentPlayerIndex = players.indexOf(landlord);
        List<Card> lastCards = null; // 桌面上最后打出的牌
        Player lastPlayer = null;    // 最后出牌的人

        int passCount = 0; // 连续不要的次数

        while (true) {
            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println("\n--------------------------------");
            //是否要修改
            System.out.println("当前轮到: " + currentPlayer.getPlayerName() +
                    (currentPlayer.isLandlord() ? " (地主)" : " (农民)"));

            // 如果是人类玩家，显示手牌
            if (currentPlayer instanceof HumanPlayer) {
                currentPlayer.showHandCard();
            }
            // 核心变化：直接调用多态方法，传入 scanner 和 上家的牌
            List<Card> cardsToPlay = currentPlayer.playCard(scanner, lastCards);


            // 处理出牌结果
            if (cardsToPlay != null && !cardsToPlay.isEmpty()) {
                // 出牌成功
                currentPlayer.removeCard(cardsToPlay);
                lastCards = new ArrayList<>(cardsToPlay); // 记录当前牌
                lastPlayer = currentPlayer;
                passCount = 0; // 重置过牌计数

                System.out.println(">> " + currentPlayer.getPlayerName() + " 打出了: " + cardsToPlay);

                // 判断胜利
                if (currentPlayer.getHandCards().isEmpty()) {
                    System.out.println("\n========== 游戏结束 ==========");
                    System.out.println("获胜者是: " + currentPlayer.getPlayerName());
                    break;
                }
            } else {
                // 不要/过
                System.out.println(">> " + currentPlayer.getPlayerName() + " 选择不要");
                passCount++;

                // 如果所有人都过了一圈，桌面清空，最后出牌的人获得新一轮出牌权
                if (passCount >= 2 && lastPlayer != null) {
                    System.out.println("\n[系统] 都要不起，轮到 " + lastPlayer.getPlayerName() + " 重新出牌。");
                    lastCards = null;
                    lastPlayer = null;
                }
            }

            // 下一个玩家
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            // 如果桌面牌空了（大家都不要），轮到谁谁就可以随意出
            if (lastCards == null) {

                lastPlayer = null;
            }
        }
    }
}
