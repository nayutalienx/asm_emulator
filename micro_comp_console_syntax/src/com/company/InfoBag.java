package com.company;

public class InfoBag {
    // ------------------------
    // Кодирование типа строки:
    // С - комментарий
    // P - команда микроЭВМ
    // О - команда ORG
    // D - команда DATA
    // E - команда END
    // X - неизвестно
    // ------------------------
    char lineType;  // тип строки в исходном тексте
    ListingLine listline;  // строка листинга
    DiagLine diagline;  // строка диагностики
    BinCom bincom;  // двоичный код, соответствующий строке

    public InfoBag(ListingLine listline, DiagLine diagline, BinCom bincom) {
        this.listline = listline;
        this.diagline = diagline;
        this.bincom = bincom;
    }


}
