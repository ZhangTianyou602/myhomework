package com.example.myhomework;

public class Record {
    public int id;
    public double amount;
    public int type;        // 0支出 1收入
    public String category;
    public String note;
    public String date;
    public int isFixed;

    public Record() {}

    public Record(double amount, int type, String category, String note, String date, int isFixed) {
        this.amount = amount; this.type = type; this.category = category;
        this.note = note; this.date = date; this.isFixed = isFixed;
    }
}