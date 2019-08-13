package com.company;

import java.io.*;
import java.util.regex.Pattern;

public class Asm {
    static int acom = 0;  //  адрес команды
    static BufferedReader src = null;  //  поток для исходной программы
    static PrintWriter list = null;  // поток для листинга
    static PrintWriter diag = null;  // поток для диагностики
    static RandomAccessFile bin = null;  //  поток для двоичных команд
    static Pattern pAddr;  //  скомпилированное рег.выр. для адреса
    static Pattern pReg;  //  скомпилированное рег.выр. для регистра
    static String[] PRCOMTAB = {  //  команды микроЭВМ
            "ADD 0000 1 P RR",
            "AND 0001 1 P RR",
            "OR 0010 1 P RR",
            "JMP 1100 2 P J 0000",
            "JMPI 1100 2 P J 1000",
            "JZ 1100 2 P J 0010",
            "JNZ 1100 2 P J 1010",
            "JM 1100 2 P J 0100",
            "JNM 1100 2 P J 1100",
            "JOV 1100 2 P J 0001",
            "JNOV 1100 2 P J 1001",
            "JC 1100 2 P J 0111",
            "JNC 1100 2 P J 1111",
            "LDRC 1101 2 P RA",
            "LDRB 1110 2 P RA",
            "SVRB 1111 2 P RA",
            "ORG 0 0 O",
            "DATA 1 0 D",
            "END 0 0 E"
    };
    static String[] prCom;  //  мнемоника команд микроЭВМ
    public static StringBuilder asmDataForGui;  //  передача данных о константах ассемблеру

    static {
        pAddr = Pattern.compile("^[0-9a-fA-F]{1,2}[hH]$" );
        pReg = Pattern.compile("(^[Rr][0-3]$)|(^[abcdABCD]$)" );
        prCom = new String[PRCOMTAB.length];
        for(int i = 0; i < PRCOMTAB.length; i++){
            prCom[i] = PRCOMTAB[i].substring(0,PRCOMTAB[i].indexOf(' '));
        }
    }


    public static AsmGuiResult main(String fs) throws IOException {
        String srcFile = fs;
        String listFile = "List.txt";  //  листинг
        String binFile = "binary.bin";  //  двоичный код
        String diagFile = "Diag.txt";  // диагностика
        StringBuilder listingForGui = new StringBuilder();  //  listing code for gui text area
        StringBuilder binaryForGui = new StringBuilder();  //  binary code for gui text area
        StringBuilder diagForGui = new StringBuilder();  //  diag notification for gui
        asmDataForGui = new StringBuilder();  //  инициализация информации для ассемблера
        String line;  //  строка исходного файла
        int num = 0;  //  номер строки файла
        SourceLine sl;
        ListingLine ll;
        DiagLine dl;
        BinCom bc;

        try {
            src = new BufferedReader(new FileReader(srcFile));
            list = new PrintWriter(listFile);
            list.println("List file");
            diag = new PrintWriter(diagFile);
            bin = new RandomAccessFile(binFile, "rw");
            ll = new ListingLine();
            dl = new DiagLine();
            bc = new BinCom();
            InfoBag infobag = null;
            while((line = src.readLine()) != null){
                num++;
                sl = new SourceLine(line, num);

                try {
                    infobag = sl.parseLine(sl.words);
                    list.println(infobag.listline);
                    listingForGui.append(infobag.listline + "\n");
                    if(infobag.diagline != null)
                        diag.println(infobag.diagline);
                        if (infobag.diagline != null)
                        diagForGui.append(infobag.diagline);
                    switch (infobag.lineType){

                        case 'O':
                            Asm.acom = infobag.bincom.addr;
                            break;
                        case 'D':
                            //bin.writeByte(infobag.bincom.addr);
                            //binaryForGui.append(String.format("%-2x ", infobag.bincom.addr));
                            asmDataForGui.append(String.format("%2x %2x ", infobag.bincom.addr, infobag.bincom.byte1));
                            Asm.acom += infobag.bincom.len;
                            break;
                        case 'P':
                            bin.writeByte(infobag.bincom.addr);
                            binaryForGui.append(String.format("%-2x ", infobag.bincom.addr));
                            bin.writeByte(infobag.bincom.byte1);
                            binaryForGui.append(String.format("%-2x ", infobag.bincom.byte1));
                            if(infobag.bincom.len == 2){
                                bin.writeByte(infobag.bincom.addr + 1);
                                binaryForGui.append(String.format("%-2x ", infobag.bincom.addr + 1));
                                bin.writeByte(infobag.bincom.byte2);
                                binaryForGui.append(String.format("%-2x ", infobag.bincom.byte2));
                            }
                            Asm.acom += infobag.bincom.len;
                            break;
                        case 'E':
                            bin.writeByte(acom);
                            binaryForGui.append(String.format("%-2x ", acom));
                            bin.writeByte((byte)0xff);
                            binaryForGui.append(String.format("%-2x ", (byte)0xff));
                            break;
                        default:
                    }
                } catch (Exception e) {
                    p("ASM::MAIN::ERROR\n"+e.toString());
                    p("Assembler line - "+ num);
                }
            }
        } finally {
            if(src != null)
                src.close();
            if(list != null)
                list.close();
            if(diag != null)
                diag.close();
            if (bin != null)
                bin.close();
        }

        p("Asm syntaxis finished!");
        return new AsmGuiResult(listingForGui.toString(), binaryForGui.toString(), diagForGui.toString());

    }

    public static void p(String s){
        System.out.print(s);
    }
}
