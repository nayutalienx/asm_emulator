package com.company;

public class RAM {
    public byte[] M;

    public RAM(int size){
        M = new byte[size];
    }

    public byte rM(int adr){  //  чтение данных, read from Memory
        return M[adr];
    }

    public void wM(int adr, byte value){  //  запись value, write to Memory
        M[adr] = value;
    }
}
