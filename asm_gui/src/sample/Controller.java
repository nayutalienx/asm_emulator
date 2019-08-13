package sample;

import com.company.Asm;
import com.company.AsmGuiResult;
import com.company.Proc;
import com.company.RAM;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.ArrayUtils.addAll;

public class Controller implements Initializable {



    @FXML
    public CodeArea asmCodeEditor;  //  object of code editor

    @FXML
    private CodeArea listingView;  //  object of listing type of code

    @FXML
    private CodeArea binaryView;  //  object of binary type of code

    @FXML
    private ListView<StatusItem> statusView;  //  indicate current status of program

    @FXML
    private TreeTableView emulatorData;  //  show current values of registers

    @FXML
    private CheckBox translateCodeAutomatically;  //  checkbox for translating code or not

    @FXML
    private TextArea emulatorStatus;  //  emulator text area status

    private RAM ram;  //  random access memory object
    private Proc proc;  //  processor of emulator object
    private TreeItem[] headers;  //  headers of emulator data table

    //  triggers to prevent unprocessed patterns
    private boolean fileLoadedSuccess;
    private boolean ramLoadedSuccess;

    @Override
    public void initialize(URL location, ResourceBundle resources) {  //  do when create window

        //  set id of editor for css
        asmCodeEditor.setId("codeArea");
        listingView.setId("codeArea2");
        binaryView.setId("codeArea3");

        // triggers
        fileLoadedSuccess = false;

        //  codearea settings
        binaryView.setWrapText(true);

        //  set cell factory for status list
        statusView.setCellFactory(new Callback<ListView<StatusItem>, ListCell<StatusItem>>() {
            @Override
            public ListCell<StatusItem> call(ListView<StatusItem> param) {
                return new FormatCell();
            }
        });

        //  add line numbers to the left of area
        asmCodeEditor.setParagraphGraphicFactory(LineNumberFactory.get(asmCodeEditor));
        listingView.setParagraphGraphicFactory(LineNumberFactory.get(listingView));

        //  make colors for code
        Subscription cleanupWhenNoLongerNeedIt = asmCodeEditor.multiPlainChanges().successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> asmCodeEditor.setStyleSpans(0, computeHighlighting(asmCodeEditor.getText())));

        //  setting data to emulator's values table
        headers = initEmulatorData();

        //  set current info
        status(new StatusItem("Программа запущена.", 1));
    }

    StyleSpans<Collection<String>> computeHighlighting(String s){
        return EditorColoring.computeHighlighting(s);
    }

    public void loadAsmFile(){  //  load file with assembler code
        FileProcessing.loadAsmFile(asmCodeEditor, statusView);
        if(translateCodeAutomatically.isSelected()){  //  check box then translate or not
            genListingAndBinary();
        }
    }

    public TreeItem[] initEmulatorData(){  //  init table data of emulator
        return EmulatorData.initEmulatorData(emulatorData);
    }

    public void genListingAndBinary(){  //  generate listing file and binary file
        if(asmCodeEditor.getText().trim().length() > 0)
            fileLoadedSuccess = FileProcessing.genListingAndBinary(listingView, binaryView, statusView, asmCodeEditor);
        else status(new StatusItem("Напишите программный код или загрузите файл.", 0));
    }

    public void loadRam(){  //  load data to ram

        if(fileLoadedSuccess) {
            ram = new RAM(256);

            //  load commands and constant values to ram
            String[] data1 = binaryView.getText().split("\\s+");
            String[] data2 = Asm.asmDataForGui.toString().split("\\s+");
            String[] data = addAll(data1, data2);

            //  init proc object with PC;
            proc = new Proc(ram, Integer.parseInt(data[0]), emulatorStatus);


            int counter = 0;
            do {
                ram.wM(Integer.parseInt(data[counter], 16), (byte) Integer.parseInt(data[counter + 1], 16));
                counter += 2;
            } while (counter <= data.length - 2);
            loadEmulatorData();
            status(new StatusItem("Данные успешно загружены в ОП.", 2));
            status(new StatusItem("Счетчик адреса команды проставлен.", 2));
            ramLoadedSuccess = true;
        } else {
            status(new StatusItem("Проверьте листинг.", 0));
            return;
        }
    }

    public void loadEmulatorData(){  //  load data to emulator table data

        //  load ram to table

        //  clear all previous information;
        for (TreeItem i:headers) {
            i.getChildren().clear();
        }

        //  add new info
        int address = 0;
        do {
            byte b = ram.rM(address);
            TreeItem item = new TreeItem(new EmulatorData(String.format("%x", address),String.format("%x", b),String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'),""));
            headers[0].getChildren().add(item);
            address++;
        } while(address < 256);

        //  load PC to table
        EmulatorData item = (EmulatorData) headers[3].getValue();
        item.setHexValue(String.format("%x", proc.getRgPC()));
        item.setBinValue(Integer.toBinaryString(proc.getRgPC()));

        //  load RONs
        address = 0;
        do {
            byte b = (byte) proc.getRON()[address];
            TreeItem i = new TreeItem(new EmulatorData(String.format("РОН[%d]", address),String.format("%x", b),String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'),""));
            headers[1].getChildren().add(i);
            address++;
        } while(address < 4);

        //  load flags
        byte flag = proc.getAlu().flagZ;
        TreeItem i = new TreeItem(new EmulatorData("Z",String.format("%x",flag), String.format("%8s", Integer.toBinaryString(flag & 0xFF)).replace(' ', '0'), ""));
        headers[2].getChildren().add(i);
        flag = proc.getAlu().getM();
        i = new TreeItem(new EmulatorData("M",String.format("%x",flag), String.format("%8s", Integer.toBinaryString(flag & 0xFF)).replace(' ', '0'), ""));
        headers[2].getChildren().add(i);
        flag = proc.getAlu().getOv();
        i = new TreeItem(new EmulatorData("Ov",String.format("%x",flag), String.format("%8s", Integer.toBinaryString(flag & 0xFF)).replace(' ', '0'), ""));
        headers[2].getChildren().add(i);
        flag = proc.getAlu().getC();
        i = new TreeItem(new EmulatorData("C",String.format("%x",flag), String.format("%8s", Integer.toBinaryString(flag & 0xFF)).replace(' ', '0'), ""));
        headers[2].getChildren().add(i);

        emulatorData.refresh();
    }

    public void runEmulator(){  //  run translated asm code

        if(ramLoadedSuccess) {

            while (proc.isRunning()) {
                statusEmulator("Процессор: счетчик адреса команды переключен.");
                proc.loadCom();
                proc.doCOM();
            }

            loadEmulatorData();
        } else {
            status(new StatusItem("Загрузите команды в ОП.",0));
        }
    }

    public void status(StatusItem item){  //  print status of program
        StatusItem.status(item, statusView);
    }  //  indicate current program status

    public void statusEmulator(String text){  //  write current emulator status
        EmulatorData.writeEmulatorStatus(emulatorStatus, text);
    }
}
