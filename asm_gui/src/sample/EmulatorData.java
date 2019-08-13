package sample;

import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmulatorData {
    private String address;
    private String hexValue;
    private String binValue;
    private String decimalValue;

    public EmulatorData(String address, String hexValue, String binValue, String decimalValue) {
        this.address = address;
        this.hexValue = hexValue;
        this.binValue = binValue;
        this.decimalValue = decimalValue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHexValue() {
        return hexValue;
    }

    public void setHexValue(String hexValue) {
        this.hexValue = hexValue;
    }

    public String getBinValue() {
        return binValue;
    }

    public void setBinValue(String binValue) {
        this.binValue = binValue;
    }

    public String getDecimalValue() {
        return decimalValue;
    }

    public void setDecimalValue(String decimalValue) {
        this.decimalValue = decimalValue;
    }

    public static TreeItem[] initEmulatorData(TreeTableView emulatorData){  //  init table data of emulator
        TreeTableColumn<EmulatorData, String> treeTableColumn1 = new TreeTableColumn<>("    Адрес    ");
        TreeTableColumn<EmulatorData, String> treeTableColumn2 = new TreeTableColumn<>("    HEX    ");
        TreeTableColumn<EmulatorData, String> treeTableColumn3 = new TreeTableColumn<>("    Bin          ");

        treeTableColumn1.setCellValueFactory(new TreeItemPropertyValueFactory<>("address"));
        treeTableColumn2.setCellValueFactory(new TreeItemPropertyValueFactory<>("hexValue"));
        treeTableColumn3.setCellValueFactory(new TreeItemPropertyValueFactory<>("binValue"));

        emulatorData.getColumns().clear();
        emulatorData.getColumns().add(treeTableColumn1);
        emulatorData.getColumns().add(treeTableColumn2);
        emulatorData.getColumns().add(treeTableColumn3);

        TreeItem procObject1 = new TreeItem(new EmulatorData("ОП", "...", "...","..."));
        TreeItem procObject2 = new TreeItem(new EmulatorData("РОНы", "...", "...","..."));
        TreeItem procObject3 = new TreeItem(new EmulatorData("Флаги", "...", "...","..."));
        TreeItem procObject4 = new TreeItem(new EmulatorData("СчАК", "0x00", "00000000","..."));

        TreeItem root = new TreeItem(new EmulatorData("Root", "", "",""));
        root.getChildren().add(procObject1);
        root.getChildren().add(procObject2);
        root.getChildren().add(procObject3);
        root.getChildren().add(procObject4);
        emulatorData.setRoot(root);
        emulatorData.setShowRoot(false);
        return new TreeItem[]{procObject1, procObject2, procObject3, procObject4};
    }

    public static void writeEmulatorStatus(TextArea status, String text){  //  write current emulator status
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        status.appendText("["+tf.format(now)+"] " + text + "\n");
    }

}
