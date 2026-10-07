package fr.shuvly.zm.game;

public class RoundManager
{

    private int currentRound = 1;


    public void nextRound() { currentRound++; }

    public int getCurrentRound() { return currentRound; }

}
