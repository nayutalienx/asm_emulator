package sample;

import javafx.scene.control.ListView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusItem {
    private String text;
    private int type;

    public StatusItem(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        if(text == null)
            return "";
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static void status(StatusItem item, ListView<StatusItem> statusView){  //  print status of program
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        item.setText("["+tf.format(now)+"] " + item.getText());
        statusView.getItems().add(item);
    }
}
