package sample;

import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

public class FormatCell extends ListCell<StatusItem> {
    @Override
    protected void updateItem(StatusItem item, boolean empty){
        super.updateItem(item, empty);
        if(item == null)
            return;
        setText(item.getText());
        if(item.getType() == 1)
            setTextFill(Color.GREEN);
        else if(item.getType() == 2)
            setTextFill(Color.TEAL);
        else
            setTextFill(Color.RED);

    }
}
