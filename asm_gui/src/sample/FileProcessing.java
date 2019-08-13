package sample;

import com.company.Asm;
import com.company.AsmGuiResult;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.io.*;

public class FileProcessing {
    public static void loadAsmFile(CodeArea asmCodeEditor, ListView<StatusItem> statusView){  //  load file with assembler code
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузка кода");
        File file = fileChooser.showOpenDialog(new Stage());
        if(file == null){
            StatusItem.status(new StatusItem("Вы не выбрали файл c кодом.", 0), statusView);
            return;
        }
        String line;
        BufferedReader inp = null;
        StringBuilder code = new StringBuilder();

        try{
            inp = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            while ((line = inp.readLine()) != null){
                line = line.trim();
                if(line.equals("")) continue;
                code.append(line + "\n");
            }
        } catch (Exception e){
            StatusItem.status(new StatusItem("Ошибка загрузки файла.", 0), statusView);
        }
        String result = code.toString();
        result = result.substring(0, result.length() - 1);
        asmCodeEditor.replaceText(0,asmCodeEditor.getLength(), result);
        StatusItem.status(new StatusItem("Файл с кодом успешно загружен.", 1), statusView);

    }

    public static void saveCurrentAsmCodeToFile(CodeArea asmCodeEditor){  //  save current asm code
        PrintWriter out = null;
        try{
            out = new PrintWriter(new FileWriter("currentSrc.txt"));
            out.println(asmCodeEditor.getText());
        }
        catch (IOException e){
            System.out.println(e);
        }
        finally {
            if(out != null) out.close();
        }

    }

    public static boolean genListingAndBinary(CodeArea listingView, CodeArea binaryView, ListView<StatusItem> statusView, CodeArea asmCodeEditor){  //  generate listing file and binary file
        try {
            boolean flag;
            saveCurrentAsmCodeToFile(asmCodeEditor);
            AsmGuiResult asmResult = Asm.main("currentSrc.txt");
            listingView.replaceText(0,listingView.getLength(), asmResult.getListing().substring(0, asmResult.getListing().length() - 1));
            binaryView.replaceText(0,binaryView.getLength(), asmResult.getBinary());
            StatusItem.status(new StatusItem("Листинг файл успешно сгенерирован.", 1),statusView);
            StatusItem.status(new StatusItem("Бинарный файл успешно сгенерирован.", 1),statusView);
            flag = true;

            if(!asmResult.getDiag().trim().equals("")) {
                StatusItem.status(new StatusItem(asmResult.getDiag(), 0),statusView);
                flag = false;
            }
            return flag;
        } catch (IOException e) {
            System.out.println("CONTROLLER::ERROR::TRANSLATING" + e.toString());
            return false;
        }

    }
}
