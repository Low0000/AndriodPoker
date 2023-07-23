package com.example.pokergame;

enum Rank {
    BLANK("b",0),
    ACE("a",1),
    TWO("2",2),
    THREE("3",3),
    FOUR("4",4),
    FIVE("5",5),
    SIX("6",6),
    SEVEN("7",7),
    EIGHT("8",8),
    NINE("9",9),
    TEN("10",10),
    JACK("j",10),
    QUEEN("q",10),
    KING("k",10);

    private String symbol;
    private int value;

    Rank(String symbol, int value) {
        this.symbol = symbol;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
