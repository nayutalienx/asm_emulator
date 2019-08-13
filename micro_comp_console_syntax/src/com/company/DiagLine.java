package com.company;

public class DiagLine {
    public static String[] ErrorMess = {
            "все хорошо",  //  0
            "неверная мнемоника",  //  -1
            "неправильные цифры константы (адреса)",  //  -2
            "операнд не существует"  //  -3
    };
    String LineNo;
    public DiagLine(){}
    public DiagLine(int ln, String msg){
        LineNo = new String(String.format("%03d %s", ln, msg));
    }
    public String toString() {
        return new String(LineNo);
    }
}
