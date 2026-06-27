//========================================张孝锐========================================
package main.java.com.hzz.landlord;

import java.util.ArrayList;
import java.util.Scanner;

public class Account extends Register {
    Scanner input = new Scanner(System.in);
    private ArrayList<String> name = super.getAccount_name();
    private ArrayList<String> passWord = super.getAccount_passWord();
    String input_name;
    String input_password;

    public Account() {

    }

    //为了方便后续图形交互的工作，需要设置输入账号和密码的方法
    public void Input_Name() {
        input_name = input.nextLine();
    }

    public void Input_Password() {
        input_password = input.nextLine();
    }

    //创建一个验证账号和密码是否对应的方法
    public boolean CheckNameAndPassword(String input_name, String input_password) {
        for (int i = 0; i < name.size(); i++) {
            if (input_name.equals(name.get(i))) {
                return input_password.equals(passWord.get(i));
            }
        }
        return false;
    }

    //创建一个完成登录的方法
    //涉及用户名、账号和验证码的输入然后验证是否正确
    public void LoginAccount() {
        System.out.println("请输入用户名:");
        Input_Name();
        System.out.println("请输入密码:");
        Input_Password();
        GetCode getCode = new GetCode();
        getCode.setCode();
        System.out.println("请输入验证码:");
        getCode.SetInput_Code();
        //以账号密码输入错误和验证码检验错误为循环条件，当账号密码输入正确以及验证码检验合格时，跳出循环
        while (!CheckNameAndPassword(input_name, input_password)
                && !getCode.CheckCode()) {
            System.out.println("请重新输入！");
            System.out.println("请输入用户名:");
            Input_Name();
            System.out.println("请输入密码:");
            Input_Password();
            getCode.setCode();
            System.out.println("请输入验证码:");
            getCode.SetInput_Code();

        }
        System.out.println("登录成功！");
    }
}