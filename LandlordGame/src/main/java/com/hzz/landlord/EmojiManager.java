package main.java.com.hzz.landlord;


import java.util.*;

public class EmojiManager {
    private Map<String, List<String>> emojiPacks;

    public EmojiManager() {
        emojiPacks = new HashMap<>();
        initializeEmojiPacks();
    }

    private void initializeEmojiPacks() {
        emojiPacks.put("挑衅", Arrays.asList(
                "来啊，互相伤害啊！",
                "就这？就这？",
                "我劝你赶快投降！",
                "你打不过我，你信吗",
                "哈哈，你输定了！"
        ));

        emojiPacks.put("嘲讽", Arrays.asList(
                "菜鸡，回家种地吧！",
                "这也太弱了吧~",
                "你是认真的吗？",
                "我都懒得理你",
                "这就是你的实力？"
        ));

        emojiPacks.put("得意", Arrays.asList(
                "嘿嘿，看我厉害吧！",
                "小意思啦~",
                "轻轻松松~",
                "无敌是多么寂寞",
                "还有谁？！"
        ));

        emojiPacks.put("互动", Arrays.asList(
                "加油加油！",
                "好牌！",
                "抱歉抱歉~",
                "谢谢配合",
                "玩得开心！"
        ));

        emojiPacks.put("表情", Arrays.asList(
                "😀", "😂", "🤣", "😎", "🤪",
                "😜", "🤔", "😏", "🙄", "😅",
                "🤷", "🤦", "👍", "👎", "🎉"
        ));
    }

    public List<String> getEmojiPack(String category) {
        return emojiPacks.getOrDefault(category, new ArrayList<>());
    }

    public Set<String> getCategories() {
        return emojiPacks.keySet();
    }

    public String getRandomEmoji(String category) {
        List<String> emojis = emojiPacks.get(category);
        if (emojis != null && !emojis.isEmpty()) {
            Random random = new Random();
            return emojis.get(random.nextInt(emojis.size()));
        }
        return "";
    }

    public void sendEmoji(String playerName, String category) {
        String emoji = getRandomEmoji(category);
        if (!emoji.isEmpty()) {
            System.out.println("[" + playerName + "] 发送了 [" + category + "] 表情: " + emoji);
        }
    }
}

