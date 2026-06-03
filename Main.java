import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
    }

    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {

    }
}


//进行账号的注册
class Register {
    private ArrayList<String> Account_name = new ArrayList<String>();
    private ArrayList<String> Account_passWord = new ArrayList<String>();
    private int index = 0;
    Scanner input = new Scanner(System.in);

    Register() {
    }

    //进行全部账号的打印
    public void printA() throws IOException, FileNotFoundException, ClassNotFoundException {
        System.out.println("-----打印账号-----");
        FileInputStream fileInputStream = new FileInputStream("StoreAccount.dat");
        ObjectInputStream dataInputStream = new ObjectInputStream(fileInputStream);
        try {
            System.out.println("name: " + dataInputStream.readObject());
            System.out.println("password:" + dataInputStream.readObject());
        } catch (EOFException ex) {
            System.out.println("打印完毕");//读取到了文件的末尾后就显示打印完毕
        }
    }

    //进行所有账号的清除
    public boolean Clear_All_Account() throws EOFException, IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream("StoreAccount.dat")
        ) {
//不写任何数据自动清空
        } catch (EOFException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    //进行账号密码的创建
    protected void setNameAndPassword() throws FileNotFoundException, IOException, ClassNotFoundException {
        //使用do-while循环会先进行一次判断密码是否合规，合规则打印注册成功，否则重来一次
        String name;
        String passWord;
        do {
            System.out.println("请输入用户名:");
            name = input.nextLine();
            System.out.println("请输入密码:");
            passWord = input.nextLine();
        }
        while (CheckPlayer(passWord, name));
        Account_name.add(name);
        Account_passWord.add(passWord);
        System.out.println("注册成功！");
        WriteAllaccount();
    }

    //

    /**
     * 将账号和密码写入文件
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    public void WriteAllaccount() throws IOException, FileNotFoundException, ClassNotFoundException {
        try (FileOutputStream Store_Account = new FileOutputStream("StoreAccount.dat");
             BufferedOutputStream Buffered_Store_Account = new BufferedOutputStream(Store_Account);
             ObjectOutputStream Store_Account_write = new ObjectOutputStream(Buffered_Store_Account);) {
            Store_Account_write.writeObject(Account_name);
            Store_Account_write.writeObject(Account_passWord);
        }
    }

    //读取账号和密码，让账号密码重新回到两个ArrayList里面保存，放止创建新的账号密码后被覆盖
    public void Return_Count() throws IOException, FileNotFoundException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream("Storeaccount.dat");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        ) {
            try {
                while ((Account_name = (ArrayList<String>) objectInputStream.readObject()) != null
                        && (Account_passWord = (ArrayList<String>) objectInputStream.readObject()) != null) ;
            } catch (EOFException ex) {
                System.out.println("");
            }
        } catch (IOException ex) {
            System.out.println("");
        }
    }

    //因为创建的过程需要满足Return_Count->setName_And_Password->WriteAllaccount,
    //如果一个个进行调用太过于繁琐，所以将其统合到一个方法里面，方便调用
    public void Create_Account() throws IOException, EOFException, ClassNotFoundException {
        this.Return_Count();
        this.setNameAndPassword();
        this.WriteAllaccount();
    }
    //进行账号创建时必要的检查

    /**
     *
     * @param AccountpassWord
     * @param Accountname
     * @return
     */
    public boolean CheckPlayer(String AccountpassWord, String Accountname) {
        //想要保证密码的长度大于8位，小于18位以及用户名长度大于0小于等于10，但ArrrayList会将一次性输入的密码当成一个元素
        //所以就必须将二者变为string进行长度判断
        if (AccountpassWord.length() < 8 || AccountpassWord.length() > 18) {
            System.out.println("密码输入长度不对！必须输入长度为8~18位的密码！");
            return true;
        }
        //对密码是否有空格进行检查，如果有，则返回true，在后续创建密码的方法里面进行重新输入
        for (int i = 0; i < AccountpassWord.length(); i++) {
            if (AccountpassWord.charAt(i) == ' ') {
                System.out.println("密码输入不能有空格！");
                return true;
            }
        }
//进行用户名长度是否合规的检测
        if (Accountname.length() <= 0 || Accountname.length() > 10) {
            System.out.println("用户名输入长度不对！必需输入长度为0~10位的名称！");
            return true;
        }
        //当用户名在长度合规的情况下，进行是否重复的检测
        if (Accountname.length() > 0 && Accountname.length() <= 10) {
            for (int i = 0; i < Account_name.size(); i++) {
                if (Accountname.equals(Account_name.get(i))) {
                    System.out.println("用户名已存在！请重新输入！");
                    return true;
                }
            }
        }
        return false;
    }

    //对私有成员进行获取方法的创建
    public ArrayList<String> getAccount_name() {
        return Account_name;
    }

    public ArrayList<String> getAccount_passWord() {
        return Account_passWord;
    }
}


class Account extends Register {
    Scanner input = new Scanner(System.in);
    private ArrayList<String> name = super.getAccount_name();
    private ArrayList<String> passWord = super.getAccount_passWord();
    String input_name;
    String input_password;

    Account() {

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

//单独创建一个生成和验证验证码的类，一面推在一起太过于冗杂不美观
class GetCode {
    private int code;
    private int input_code;

    GetCode() {

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
