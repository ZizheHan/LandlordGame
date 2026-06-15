package main.java.com.hzz.landlord;

public class Card implements Comparable<Card> {
    private int id;
    private String suit;
    private int rank;


    public Card(String suit, int id, int rank) {
        this.suit = suit;
        this.id = id;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean compareTo1(Card other) {
        // 权重值越大牌越大
        return this.rank > other.getRank();
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.rank, o.rank);
    }

    // 打印卡牌信息
    @Override
    public String toString() {
        // 判断大小王
        if (this.rank == 16) {
            return "[小王]";
        }
        if (this.rank == 17) {
            return "[大王]";
        }
        if (suit == null) {
            return "[未知牌]";
        }

        // 转换J/Q/K/A/2的文字显示
        String pointStr;
        switch (this.rank) {
            case 11:
                pointStr = "J";
                break;
            case 12:
                pointStr = "Q";
                break;
            case 13:
                pointStr = "K";
                break;
            case 14:
                pointStr = "A";
                break;
            case 15:
                pointStr = "2";
                break;
            default:
                // 3-10直接数字输出
                pointStr = String.valueOf(this.rank);
                break;
        }
        // 拼接花色+点数
        return "[" + this.suit + pointStr + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return id == card.id && rank == card.rank && suit.equals(card.suit);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, suit, rank);
    }
}
