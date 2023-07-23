package com.example.pokergame;

import java.util.*;

class Player {
    private String name;
    private List<Card> hand;
    private int score;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    public void removeFromHand(Card card) {
        hand.remove(card);
    }

    public void clearHand() { hand.clear(); }



    public Card playCard(String input) {
        for(Card card: hand) {
            if (input.equals(card.toString())) {
                return card;
            }
        }
        return null;
    }


    public void incrementScore() {
        score++;
    }

    public void setScore(int score) {
        this.score = score;
    }


}