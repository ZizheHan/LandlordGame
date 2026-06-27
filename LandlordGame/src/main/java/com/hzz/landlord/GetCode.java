//========================================张孝锐========================================
package main.java.com.hzz.landlord;

import java.util.Scanner;

public class GetCode {
    private int code;
    private int input_code;

    public GetCode() {

    }

    //创建一个生成四位数的随机验证码
    public void setCode() {
        code = 1000 + (int) (Math.random() * 100);
        System.out.println("验证码为：" + code);
    }

    //创建一个获取用户输入的验证码的方法
    public void SetInput_Code() throws IllegalArgumentException {
        try {
            Scanner input = new Scanner(System.in);
            input_code = input.nextInt();
        } catch (IllegalArgumentException ex) {
            System.out.println("请以整数形式输入！");
        }
    }

    //创建一个检查验证码是否正确的方法
    public boolean CheckCode() {
        if (code == input_code) {
            return true;
        } else {
            return false;
        }
    }
}
