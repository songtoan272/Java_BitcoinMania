package fxml.util;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class MyTooltip extends Tooltip {
    public MyTooltip(String s){
        super(s);
        setShowDelay(Duration.seconds(0.01));
    }
}
