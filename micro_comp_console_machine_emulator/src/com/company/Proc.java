package com.company;


import javafx.scene.control.TextArea;
import sample.EmulatorData;

public class Proc {
    //  к процессору ProcB добавлен анализ флагов
    //  и выполнение команд перехода

    private boolean running = true;  //  flag to stop incrementing PC
    private TextArea emulatorStatus;  //  emulator text area status
    private ALU alu; int rgCOM, rgCOM2; int[] RON;
    RAM ram;
    int rgPC;

    public Proc(RAM m, int initPC, TextArea e){
        RON = new int[4];
        ram = m;
        rgPC = initPC;
        emulatorStatus = e;
        alu = new ALU(emulatorStatus);
    }

    public void status(String text){  //  write current emulator status
        EmulatorData.writeEmulatorStatus(emulatorStatus, text);
    }

    public void loadCom(){  //  чтение команды из памяти
        rgCOM = ram.rM(rgPC);  //  загрузка первого байта
        rgPC++;
        int check = rgCOM & 0xff;
        if(cL() == 2 && check != 0xff){
            rgCOM2 = ram.rM(rgPC); rgPC++;  //  и, если надо, второго байта
        }

    }

    public void doCOM(){
        int check = rgCOM & 0xff;
        if(check == 0xff){
            running = false;
            status("Процессор: конец программы.");
            return;
        }

        int cop = (rgCOM & 0xf0) >> 4;
        switch(cop){

            case 0xC:
                int mask = rgCOM & 0x0f;  //  выделение маски из первого байта команды
                status("Процессор: выделение маски из первого байта команды перехода.");
                switch(mask){
                    case 0:
                        rgPC = rgCOM2;  //  JMP
                        break;
                    case 1:
                        if(alu.flagOv != 0)
                            rgPC = rgCOM2;  //  JOV
                        break;
                    case 2:
                        if(alu.flagZ != 0)
                            rgPC = rgCOM2;  //  JZ
                        break;
                    case 4:
                        if(alu.flagM != 0)
                            rgPC = rgCOM2;  // JM
                        break;
                    case 7:
                        if(alu.flagC != 0)
                            rgPC = rgCOM2;  //  JC
                        break;
                    case 8:
                        rgPC = ram.M[rgCOM2];  //  JMPI
                        break;
                    case 9:
                        if(alu.flagOv == 0)
                            rgPC = rgCOM2;  //  JNOV
                        break;
                    case 0xA:
                        if(alu.flagZ == 0)
                            rgPC = rgCOM2;  //  JNZ
                        break;
                    case 0xC:
                        if(alu.flagM == 0)
                            rgPC = rgCOM2;  //  JNM
                        break;
                    case 0xF:
                        if(alu.flagC == 0)  //  jnc
                            rgPC = rgCOM2;
                        break;
                }
                break;
            case 0xD:
                RON[(rgCOM & 0xC) >> 2] = rgCOM2;  //  загрузка константы
                status(String.format("Процессор: загрузка константы %x в РОН.", rgCOM2));
                break;
            case 0xE:
                RON[(rgCOM & 0xC) >> 2] = ram.rM(rgCOM2);  //  загрузка байта
                status(String.format("Процессор: загрузка байта %x в РОН.", rgCOM2));
                break;
            case 0xF:
                ram.wM(rgCOM2, (byte) RON[(rgCOM & 0xC) >> 2]);  //  запись байта
                status(String.format("Процессор: запись байта %x в ОП.", rgCOM2));
                break;
            default:  //  выполняются старые команды
                loadAluRegs(rgCOM);  //  загрузка Р1 и Р2
                status("Процессор: загрузка операндов в АЛУ.");
                alu.doOp(cop);  //  выполнение команды в АЛУ
                status("Процессор: выполнение команды в АЛУ.");
                saveAluRes();  //  сохранение результата (РР) в РОН
                status("Процессор: сохранение регистра результата в РОН.");

        }
    }

    public void loadAluRegs(int rgC){  //  загрузка Р1 и Р2 из РОН, указанных в команде
        alu.setP1((byte)(RON[rgC & 0xC]));  //  загрузка Р1
        alu.setP2((byte)(RON[rgC & 0x3]));  //  загрузка Р2
    }

    public void saveAluRes(){  //  сохранение РР в РОН первого операнда
        RON[(rgCOM & 0xC) >> 2] = alu.getPP();
    }

    public int cL(){  //  определение длины команды по ее коду
        int len = 1;
        switch((rgCOM & 0xf0) >> 4){
            case 0xc:
            case 0xd:
            case 0xe:
            case 0xf:
                len = 2;
                break;
            default:
                len = 1;
        }
        return len;
    }

    public int getRgPC() {
        return rgPC;
    }

    public boolean isRunning() {
        return running;
    }

    public int[] getRON() {
        return RON;
    }

    public ALU getAlu() {
        return alu;
    }
}
