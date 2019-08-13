package com.company;

public class BinCom {
    int len;
    int addr;
    byte byte1, byte2;

    public BinCom() {
        len = addr = byte1 = byte2 = (byte)0;
    }

    public BinCom(int l, int a, byte b1, byte b2){
        len = l;
        addr = a;
        byte1 = b1;
        byte2 = b2;
    }
}
