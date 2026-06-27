//========================================肖松========================================
package main.java.com.hzz.landlord;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class Settings {
    private static final Pane setPane = new Pane(new Button("我是奶龙"));

    public static Pane getSetPane() {
        return setPane;
    }
}