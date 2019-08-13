package com.company;

public class ListingLine {
    String lineNo;  //  номер строки
    String comAddress;  //  адрес размещения команды
    String byteOne;  //  первый байт команды
    String byteTwo;  //  второй байт команды
    String errMark;  //  - отметка строки с ошибкой
    String srcLine;  //  копия исходной строки

    public ListingLine(){
        lineNo = new String();
        byteOne = new String();
        byteTwo = new String();
        comAddress = new String();
        srcLine = new String();
    }

    public ListingLine(int lno, int ad, int k1, int k2, String err, String line){
        lineNo = new String(String.format("%4d", lno));

        if(ad < 0)
            comAddress = new String(String.format("    "));
        else
            comAddress = new String(String.format("%03x", ad));

        if(k1 < 0)
            byteOne = new String(String.format(" "));
        else
            byteOne = new String(String.format("%02x", k1));

        if(k2 < 0)
            byteTwo = new String(String.format(" "));
        else
            byteTwo = new String(String.format("%02x", k2));

        errMark = err;
        srcLine = line;
    }

    public String toString(){
        return String.format("%-3s %-2s %-2s %s", comAddress, byteOne, byteTwo, srcLine.trim());
    }

    public String getHexCode(){
        return String.format("%-3s %-2s %-2s\n", comAddress, byteOne, byteTwo);
    }


}
