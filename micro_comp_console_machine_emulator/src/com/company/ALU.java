package com.company;


import javafx.scene.control.TextArea;
import sample.EmulatorData;

public class ALU {
    private int rgP1, rgP2, rgPP;  //  регистры
    private int op, dbus;  //  операция
    private TextArea emulatorStatus;  //  emulator text area status
    public byte flagZ, flagM, flagOv, flagC;


    public ALU(TextArea ta){
        rgP1 = rgP2 = rgPP = 0;
        op = 0;
        flagC = 0;
        emulatorStatus = ta;
    };

    public int doOp(int oper){  //  выполнить операцию oper
        //  для выполнения операции должны быть заполнены регистры P1, P2
        op = oper&0xf0>>4;  //  выделение кода операции
        switch (op){
            case 0:
                rgPP = (rgP1&0xff) + (rgP2&0xff);  //  операция add
                flagC = setC();  //  формирование флагов
                flagM = setM();
                flagOv = setOv();
                status("АЛУ: операция сложения и формирование флагов.");
                break;
            case 1:
                rgPP = rgP1 & rgP2;  //  операция and
                flagM = setM();  //  формирование флагов
                flagZ = setZ();
                status("АЛУ: операция AND и формирование флагов.");
                break;
            case 2:
                rgPP = rgP1 | rgP2;  //  операция or
                flagM = setM();  //  формирование флагов
                flagZ = setZ();
                status("АЛУ: операция OR и формирование флагов.");
                break;
        }
        rgPP &= 0xff;
        return rgPP;
    }

    //  методы доступа к закрытым private полям
    public void setP1(byte r1){
        rgP1 = r1;
    }

    public void setP2(byte r2){
        rgP2 = r2;
    }

    public byte getPP(){
        return (byte) rgPP;
    }

    //  методы установки флагов
    private byte setM(){
        return (byte)(((rgPP&0x80) == 0) ? 0 : 1);
    }

    private byte setZ(){
        return (byte)(((rgPP&0xff) == 0) ? 1 : 0);
    }

    private byte setOv(){
        boolean bov = (((rgP1&0x80) == 0) && ((rgP2&0x80) == 0) && ((rgPP&0xff) != 0)) ||
                (((rgP1&0x80) != 0) && ((rgP2&0x80) != 0) && ((rgPP&0x08) == 0));
        return (byte)((bov) ? 1 : 0);  //  ov error probably
    }

    private byte setC(){
        return (byte)(((rgPP&0x100) == 0) ? 0 : 1);
    }

    //  методы доступа к флагам


    public byte getZ() {
        return flagZ;
    }

    public byte getM() {
        return flagM;
    }

    public byte getOv() {
        return flagOv;
    }

    public byte getC() {
        return flagC;
    }

    public void status(String text){  //  write current emulator status
        EmulatorData.writeEmulatorStatus(emulatorStatus, text);
    }
}
