package main.java.com.hzz.landlord;


import com.huanle.doudizhu.model.Card;
import com.huanle.doudizhu.player.AIPlayer;
import com.huanle.doudizhu.player.HumanPlayer;
import com.huanle.doudizhu.player.Player;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RobLandlordStage {
    private Scanner scanner;
    private Player landlord;
    private int currentBid;
    private boolean biddingFinished;

    public RobLandlordStage() {
        this.scanner = new Scanner(System.in);
        this.currentBid = 0;
        this.biddingFinished = false;
        this.landlord = null;
    }

    public Player startRobbing(List<Player> players, CardDeck cardDeck) {
        System.out.println("\n========== 抢地主阶段 ==========");
        reset();

        Random random = new Random();
        int startIndex = random.nextInt(players.size());
        Player currentPlayer = players.get(startIndex);

        System.out.println("随机决定起始玩家: " + currentPlayer.getPlayerName());

        for (int round = 0; round < players.size(); round++) {
            System.out.println("\n--- 第 " + (round + 1) + " 轮叫分 ---");
            System.out.println("当前玩家: " + currentPlayer.getPlayerName());
            System.out.println("当前最高叫分: " + currentBid);

            int bid = getBidFromPlayer(currentPlayer);

            if (bid > currentBid) {
                currentBid = bid;
                landlord = currentPlayer;
                System.out.println(currentPlayer.getPlayerName() + " 叫分: " + bid + " 分");
            } else if (bid == 0) {
                System.out.println(currentPlayer.getPlayerName() + " 放弃叫分");
            } else {
                System.out.println(currentPlayer.getPlayerName() + " 叫分不足，无效");
            }

            currentPlayer = getNextPlayer(players, currentPlayer);
        }

        return finalizeLandlord(players, cardDeck);
    }

    private int getBidFromPlayer(Player player) {
        if (player instanceof HumanPlayer) {
            return getHumanBid((HumanPlayer) player);
        } else if (player instanceof AIPlayer) {
            return getAIBid((AIPlayer) player);
        }
        return 0;
    }

    private int getHumanBid(HumanPlayer humanPlayer) {
        System.out.print(humanPlayer.getPlayerName() + "，请输入叫分(0-3，0表示不抢): ");

        try {
            String input = scanner.nextLine().trim();
            int bid = Integer.parseInt(input);

            if (bid < 0 || bid > 3) {
                System.out.println("输入无效，必须在0-3之间");
                return 0;
            }

            if (bid <= currentBid && bid != 0) {
                System.out.println("叫分必须高于当前最高分 " + currentBid);
                return 0;
            }

            return bid;
        } catch (NumberFormatException e) {
            System.out.println("输入错误，默认不抢");
            return 0;
        }
    }

    private int getAIBid(AIPlayer aiPlayer) {
        Random random = new Random();

        int handStrength = calculateHandStrength(aiPlayer);

        int aiBid;
        if (handStrength >= 12) {
            aiBid = 3;
        } else if (handStrength >= 8) {
            aiBid = 2;
        } else if (handStrength >= 5) {
            aiBid = 1;
        } else {
            aiBid = random.nextInt(2);
        }

        if (aiBid <= currentBid) {
            aiBid = 0;
        }

        System.out.println(aiPlayer.getPlayerName() + " AI思考中...");
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(aiPlayer.getPlayerName() + " 叫分: " + (aiBid > 0 ? aiBid : "放弃"));

        return aiBid;
    }

    private int calculateHandStrength(AIPlayer aiPlayer) {
        int strength = 0;

        for (Card card : aiPlayer.getHandCards()) {
            int rank = card.getRank();

            if (rank == 17) {
                strength += 4;
            } else if (rank == 16) {
                strength += 3;
            } else if (rank == 15) {
                strength += 2;
            } else if (rank >= 13) {
                strength += 1;
            }
        }

        return strength;
    }

    private Player getNextPlayer(List<Player> players, Player currentPlayer) {
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();
        return players.get(nextIndex);
    }

    private Player finalizeLandlord(List<Player> players, CardDeck cardDeck) {
        biddingFinished = true;

        if (landlord != null) {
            landlord.setLandlord(true);

            List<Card> bottomCards = cardDeck.getBottomCard();
            System.out.println("\n========== 抢地主结束 ==========");
            System.out.println("地主是: " + landlord.getPlayerName());
            System.out.println("叫分: " + currentBid + " 分");
            System.out.println("底牌: " + bottomCards);

            for (Card card : bottomCards) {
                landlord.addCard(card);
            }

            landlord.sortCard();

            System.out.println("\n" + landlord.getPlayerName() + " 获得底牌后的手牌:");
            landlord.showHandCard();

        } else {
            System.out.println("\n========== 抢地主结束 ==========");
            System.out.println("无人叫分，重新发牌！");

            cardDeck.shuffleCard();
            cardDeck.dealCard(players.get(0), players.get(1), players.get(2));

            System.out.println("重新发牌完成，再次进入抢地主阶段...\n");
            return startRobbing(players, cardDeck);
        }

        return landlord;
    }

    public void reset() {
        this.currentBid = 0;
        this.biddingFinished = false;
        this.landlord = null;
    }

    public Player getLandlord() {
        return landlord;
    }

    public int getCurrentBid() {
        return currentBid;
    }

    public boolean isBiddingFinished() {
        return biddingFinished;
    }
}
