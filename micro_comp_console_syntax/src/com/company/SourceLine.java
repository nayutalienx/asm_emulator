package com.company;


import java.util.regex.Pattern;

public class SourceLine {
    String sourceLine;  //  исходная строка, заполняется конструктором
    String myLine;  //  исходная строка без пробелов слева и справа
    String[] words;  //  части строки - слова
    int lineNumber;  //  номер строки
    char lineType;  //  С - комментарий, A,O,D,E - команды ассемблера,
                    //  P - команда процессора, X - неизвестный тип

    int nAsm;  //  номер команды в таблице команд ассемблера
    int nProc;  //  номер команды в таблице команд микроЭВМ

    public SourceLine(String l, int n){
        sourceLine = new String(l);
        lineNumber = n;
        myLine = sourceLine.trim();

        if(myLine.charAt(0) == ';'){
            words = new String[1];
            words[0] = ";";
        } else {
            words = myLine.split("(\\s+)|[,;]");
        }
    }

    public InfoBag parseLine(String[] wo){  //  wo - массив слов строки
        InfoBag info = null;
        DiagLine dil = null;
        ListingLine lil = null;
        lineType = 'X';  //  а вдруг определить не удастатся
        String[] ww = null;  //  для частей из таблицы команд
        if(wo.length == 0)
            lineType = 'C';  //  пустая строка
        if(wo[0].equals(""))
            lineType = 'C';
        if(wo[0].charAt(0) == ';') {
            lineType = 'C';
        }
        else if((nProc = findCom(wo[0], Asm.prCom)) >= 0)  //  поиск команды в таблице
        {

            ww = Asm.PRCOMTAB[nProc].split("\\s+" );
            lineType = ww[3].charAt(0);  //  поле с номером 3 в PRCOMTAB - тип команды

        }
        switch(lineType){  //  обработка команды согласно ее типу
            case 'C':
                info = parseComment();
                info.lineType = 'C';
                break;
            case 'O':

                info = setOrg(wo); // ne rabotaet
                info.lineType = 'O';

                break;
            case 'D':
                info = setData(wo);
                info.lineType = 'D';
                break;
            case 'E':
                info = setEnd();
                info.lineType = 'E';
                break;
            case 'P':  //  команды микроЭВМ
                String kod = ww[1];  //  поле 1 в PRCOMTAB - код команды
                int binaryKod = Integer.parseInt(kod, 2);  //  получен код операции
                String format = ww[4];  //  выбор поля 4 - формата команды
                if(format.equals("RR"))
                    info = parseRR(wo, binaryKod);
                if(format.equals("RA"))
                    info = parseRA(wo, binaryKod);
                if(format.equals("J"))
                    info = parseJ(ww[5], wo, binaryKod);
                info.lineType = 'P';  //  строка - команда микроЭВМ
                break;
            default:
                dil = new DiagLine(lineNumber, sourceLine + " " + DiagLine.ErrorMess[1]);
                lil = new ListingLine(lineNumber, -1, -1, -1,"-", sourceLine);
                info = new InfoBag(lil, dil, null);
                info.lineType = 'X';
        }
        return info;
    }

    private int findCom(String w, String[] com){  //  прямой поиск w в com
        for(int i = 0; i < com.length; i++){
            if(w.equals(com[i]))
                return i;
        }
        return -1;
    }

    private InfoBag parseComment(){

        return new InfoBag(new ListingLine(lineNumber, Asm.acom, -1, -1,"" ,sourceLine), null, null);
    }

    private InfoBag setOrg (String[] ops){  //  параметр - массив слов строки
        DiagLine dil = null;
        ListingLine lil = null;
        BinCom bic = null;
        int av = -3;

        if(ops.length > 1)
            av = setAddrValue(ops[1], Asm.pAddr);

        if(av >= 0){
            lil = new ListingLine(lineNumber, av, -1, -1, "", sourceLine);

            bic = new BinCom(1, av, (byte)0, (byte)0);

        } else {
            lil = new ListingLine(lineNumber, -1, -1, -1, "-", sourceLine);
            dil = new DiagLine(lineNumber, sourceLine + " " + DiagLine.ErrorMess[-av]);
        }
        return new InfoBag(lil, dil, bic);
    }

    private InfoBag setData(String[] ops){
        DiagLine dil = null;
        ListingLine lil = null;
        BinCom bic = null;
        int av = -3;
        if(ops.length > 1)
            av = setAddrValue(ops[1], Asm.pAddr);
        if(av >= 0){
            lil = new ListingLine(lineNumber, Asm.acom, av, -1, "", sourceLine);
            bic = new BinCom(1, Asm.acom, (byte)av, (byte)0);
        } else {
            lil = new ListingLine(lineNumber, -1, -1, -1, "-", sourceLine);
            dil = new DiagLine(lineNumber, sourceLine + " " + DiagLine.ErrorMess[-av]);
        }
        return new InfoBag(lil, dil, bic);
    }

    private InfoBag setEnd(){
        return new InfoBag(new ListingLine(lineNumber, -1, -1, -1,"" ,"END"), null, null);
    }

    private InfoBag parseRR(String[] ops, int bk){  //  формат RR
        DiagLine dil = null;
        ListingLine lil = null;
        BinCom bic = null;
        int k = bk;
        int rv1, rv2;
        if(ops.length > 1){
            rv1 = setRegValue(ops[1], Asm.pReg);  //  первый операнд есть
        } else {
            rv1 = -3;
        }
        if(ops.length > 2){
            rv2 = setRegValue(ops[2], Asm.pReg);  //  второй операнд есть
        } else {
            rv2 = -3;
        }
        k = (bk << 4) | ((rv1 & 3) << 2) | (rv2 & 3);
        if((rv1 >= 0) & (rv2 >= 0)){  //  нет ошибок в записи операндов
            lil = new ListingLine(lineNumber, Asm.acom, k, -1, "", sourceLine);
            bic = new BinCom(1, Asm.acom, (byte)k, (byte)0);
        } else {  //  ошибка в 1-м или 2-м операнде
            String xx = new String();
            String newString = new String();
            if(rv1 < 0){
                xx += new String("Первый операнд: " + DiagLine.ErrorMess[-rv1]);
                newString = "\n";  //  есть первая строка
            }
            if(rv2 < 0){
                xx += newString;  //  нужна вторая строка
                xx += new String("Второй операнд: " + DiagLine.ErrorMess[-rv2]);
            }
            lil = new ListingLine(lineNumber, Asm.acom, -1, -1, "-", sourceLine);
            dil = new DiagLine(lineNumber, sourceLine + " " + xx);
        }
        return new InfoBag(lil, dil, bic);

    }

    private InfoBag parseRA(String[] ops, int bk){  //  формат А - регистр, адрес
        int k = bk; int rv, av;
        DiagLine dil = null;
        ListingLine lil = null;
        BinCom bic = null;
        if(ops.length > 1){
            rv = setRegValue(ops[1], Asm.pReg);
        } else {
            rv = -3;  //  операнда нет
        }
        k = (bk << 4) | ((rv & 3) << 2);
        if(ops.length > 2){
            av = setAddrValue(ops[2], Asm.pAddr);
        } else {
            av = -3;  //  операнда нет
        }
        if((rv >= 0) & (av >= 0)){
            lil = new ListingLine(lineNumber, Asm.acom, k, av, "", sourceLine);
            bic = new BinCom(2, Asm.acom, (byte)k, (byte)av);
        } else {  //  ошибка в 1-м или 2-м операнде
            String xx = new String();
            String newString = new String();
            if(rv < 0){
                xx += new String("Первый операнд: " + DiagLine.ErrorMess[-rv]);
                newString = "\n";  //  есть первая строка
            }
            if(av < 0) {
                xx += new String("Второй операнд: " + DiagLine.ErrorMess[-av]);
            }
            lil = new ListingLine(lineNumber, -1, -1, -1, "-", sourceLine);
            dil = new DiagLine(lineNumber, sourceLine + " " + xx);
        }
        return new InfoBag(lil, dil, bic);
    }

    private InfoBag parseJ(String mask, String[] ops, int bk){  //  формат J - переходы
        int m = 0; int av;
        DiagLine dil = null;
        ListingLine lil = null;
        BinCom bic = null;
        m = Integer.parseInt(mask, 2);  //  преобразование строки в число
        int k = (bk << 4) | (m & 15);  //  4 бита - код операции, 4 бита - маска
        if(ops.length > 1){
            av = setAddrValue(ops[1], Asm.pAddr);
        } else {
            av = -3;
        }
        if(av >= 0){  //  ошибок в операнде нет
            lil = new ListingLine(lineNumber, Asm.acom, k, av, "", sourceLine);
            bic = new BinCom(2, Asm.acom, (byte)k, (byte)av);
        } else {  //  в операнде есть ошибки
            String xx = new String("Первый операнд: " + DiagLine.ErrorMess[-av]);
            lil = new ListingLine(lineNumber, -1, -1, -1, "-", sourceLine);
            dil = new DiagLine(lineNumber, sourceLine + " " + xx);
        }
        return new InfoBag(lil, dil, bic);
    }

    private int setRegValue(String w, Pattern pr){  //  w - слово, содержащее операнд-регистр
        int val = 0;
        if((w == null) || (w.length() == 0)){
            val = -3;
        } else {
            if(pr.matcher(w).matches()){
                if(Character.toUpperCase(w.charAt(0)) == 'R'){
                    val = Integer.parseInt(w.substring(1));
                } else {
                    int x = "abcdABCD".indexOf(w);
                    val = x%4;
                }
            } else {
                val = -2;
            }
        }
        return val;
    }

    private int setAddrValue(String w, Pattern pr){
        int val = 0;
        if((w == null) || (w.length() == 0)){
            val = -3;
        } else {
            if(pr.matcher(w).matches()){
                val = Integer.parseInt(w.substring(0, w.length() - 1), 16);
            } else {
                val = -2;
            }
        }
        return val;
    }


}
