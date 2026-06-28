//========================================韩梓哲========================================
package main.java.com.hzz.landlord;

import javafx.application.Application;

import java.util.Scanner;

public class GameStarter {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Register REGISTER = new Register();

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      欢迎来到「十七张牌你能秒我？」文字登录系统       ");
        System.out.println("=================================================");

        loadAccountsSilently();

        while (true) {
            printMenu();
            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    if (doRegister()) {
                        System.out.print("注册成功！是否立即登录并进入游戏？(y/n): ");
                        String answer = SCANNER.nextLine().trim();
                        if ("y".equalsIgnoreCase(answer)) {
                            if (doLogin()) {
                                launchGame();
                                return;
                            }
                        }
                    }
                    break;
                case "2":
                    if (doLogin()) {
                        launchGame();
                        return;
                    }
                    break;
                case "3":
                    printAllAccounts();
                    break;
                case "4":
                    clearAllAccounts();
                    break;
                case "5":
                    System.out.println("欢迎下次使用，再见！");
                    return;
                default:
                    System.out.println("输入有误，请重新选择。");
            }
        }
    }

    // 主菜单
    private static void printMenu() {
        System.out.println("\n----- 主菜单 ------");
        System.out.println("1.     注册新账号    ");
        System.out.println("2.    登录已有账号   ");
        System.out.println("3. 打印所有已注册账号 ");
        System.out.println("4.    清除所有账号   ");
        System.out.println("5.       退出       ");
        System.out.print(" 请输入选项 (1/2/3/4/5):");
    }

    //注册
    private static boolean doRegister() {
        try {
            System.out.println("\n>>> 开始注册新账号 <<<");
            REGISTER.Create_Account();
            return true;
        } catch (Exception e) {
            System.out.println("注册过程出现异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //登录
    private static boolean doLogin() {
        System.out.println("\n>>> 登录 <<<");
        loadAccountsSilently();
        System.out.print("请输入用户名: ");
        String username = SCANNER.nextLine().trim();
        System.out.print("请输入密码: ");
        String password = SCANNER.nextLine().trim();

        GetCode getCode = new GetCode();
        getCode.setCode();
        System.out.print("请输入验证码: ");
        getCode.SetInput_Code();

        Account account = new Account();
        while (!account.CheckNameAndPassword(username, password)
                && !getCode.CheckCode()) {
            System.out.println("账号密码错误或验证码错误，请重新输入！");
            System.out.print("请输入用户名: ");
            username = SCANNER.nextLine().trim();
            System.out.print("请输入密码: ");
            password = SCANNER.nextLine().trim();
            getCode.setCode();
            System.out.print("请输入验证码: ");
            getCode.SetInput_Code();
        }
        System.out.println("登录成功！即将进入游戏...");
        return true;
    }

    //打印所有注册账号
    private static void printAllAccounts() {
        try {
            //重新加载
            REGISTER.Return_Count();
            System.out.println("\n>>> 所有注册账号 <<<");
            REGISTER.printA();
        } catch (Exception e) {
            System.out.println("打印账号失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //清除所有账号
    private static void clearAllAccounts() {
        System.out.print("\n警告：此操作将删除所有注册账号，不可恢复！是否继续？(y/n): ");
        String confirm = SCANNER.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("操作已取消。");
            return;
        }

        try {
            REGISTER.Clear_All_Account();
            //清空内存中的列表
            REGISTER.getAccount_name().clear();
            REGISTER.getAccount_passWord().clear();
            System.out.println("所有账号已成功清除。");
        } catch (Exception e) {
            System.out.println("清除账号失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //辅助方法
    private static void loadAccountsSilently() {
        try {
            REGISTER.Return_Count();
        } catch (Exception e) {
            System.out.println("(提示) 暂无存档账号，请先注册。");
        }
    }

    private static void launchGame() {
        System.out.println("\n>>> 正在启动游戏界面 <<<");
        Application.launch(BookCover.class);
    }
}