package com.mitch3a.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitchken on 3/26/17.
 */
public class Game {
    final String id;
    final Player p1;
    final Player p2;
    final Player currentPlayer;
    int p1Number = -1;
    int p2Number = -1;
    final List<Integer> p1Guesses;
    final List<Integer> p2Guesses;
    GameState gameState;

    //Used for going from a request to a started game
    public Game(GameRequest request) {
        this.id = request.id;
        this.p1 = request.requester;
        this.p2 = request.accepter;
        this.currentPlayer = request.starterPlayer;
        this.gameState = GameState.SETUP;
        this.p1Guesses = new ArrayList<>();
        this.p2Guesses = new ArrayList<>();
    }

    //If only in setup
    public Game(String id, Player p1, Player p2, Player currentPlayer) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.currentPlayer = (currentPlayer != null) ? currentPlayer : getRandomPlayer(p1, p2);
        this.p1Guesses = new ArrayList<>();
        this.p2Guesses = new ArrayList<>();
        this.gameState = GameState.SETUP;
    }

    public void setup(Player player, String setupData) {
        int number = Integer.parseInt(setupData);

        if(!GameState.SETUP.equals(gameState)) {
            throw new IllegalArgumentException(String.format("Cannot set number to %d for player %s. Game is not in setup state, it is in the %s state.",
                    number, player.getId(), gameState));
        }

        if(p1.equals(player)) {
            if(p1Number != -1) {
                throw new IllegalArgumentException(
                        String.format("Can't set number %d for player %s. Number already set to %d", number, player.getId(), p1Number));
            }

            p1Number = number;
        }
        else if(p2.equals(player)) {
            if(p2Number != -1) {
                throw new IllegalArgumentException(
                        String.format("Can't set number %d for player %s. Number already set to %d", number, player.getId(), p2Number));
            }

            p2Number = number;
        }
        else {
            throw new IllegalArgumentException("Player " + player.getId() + " not in game " + id);
        }

        if(p1Number != -1 && p2Number != -1) {
            gameState = GameState.IN_PROGRESS;
        }
    }

    public String getId() {
        return id;
    }

    public void takeTurn(Player player, String turnString) {
        int number = parseTurn(turnString);
        boolean isP1 = isP1(player);

        switch(gameState) {
        case SETUP: takeSetupTurn(isP1, number);
        break;
        case IN_PROGRESS: makeGuess(isP1, player, number);
        break;
        }

        throw new IllegalArgumentException(String.format("Player %s cannot make turn %s because game is in state %s",
                player.getId(), turnString, gameState));
    }

    private int parseTurn(String turnString) {
        try {
            int result = Integer.parseInt(turnString);

            if(result < 0 || result > 9) {
                throw new IllegalArgumentException(String.format("Invalid turn number %d for game %s. Must be between 0 and 9.",
                        result, id));
            }

            return result;
        } catch(Exception e) {
            throw new IllegalArgumentException(String.format("Cannot parse turn in game %s. Turn string %s is not an integer",
                    id, turnString));
        }
    }

    private boolean isP1(Player player) {
        if(player == null){
            throw new IllegalArgumentException(String.format("Cannot take turn for game %s with null player", id));
        }

        if(p1.equals(player)){
            return true;
        }
        else if(p2.equals(player)){
            return false;
        }

        throw new IllegalStateException(String.format("Given player %s is not player 1 %s or player2 %s in game %s",
                currentPlayer.getId(), p1.getId(), p2.getId(), id));

    }

    private void takeSetupTurn(boolean isP1, int number) {
        if(isP1) {
            if(p1Number != -1) {
                throw new IllegalArgumentException(String.format("Can't setup p1 %s with number %d because already setup to number %d",
                        p1.getId(), number, p1Number));
            }

            p1Number = number;
        }
        else {
            if(p2Number != -1) {
                throw new IllegalArgumentException(String.format("Can't setup p2 %s with number %d because already setup to number %d",
                        p2.getId(), number, p2Number));
            }

            p2Number = number;
        }
    }

    private void makeGuess(boolean isP1, Player player, int guess) {
        if(!player.equals(currentPlayer)) {
            throw new IllegalArgumentException(String.format("Cannot make guess <%d> in game <%s> because it's not <%s>'s turn.",
                    guess, id, player.getId()));
        }

        if(isP1){
            makeGuess(p1Guesses, guess, p2Number, p2);
        }
        else {
            makeGuess(p2Guesses, guess, p1Number, p1);
        }
    }


    private void makeGuess(List<Integer> prevGuesses, int guess, int correctNumber, Player nextPlayer) {
        if(prevGuesses.contains(guess)) {
            throw new IllegalArgumentException(String.format("Cannot guess %d. Player %s already guessed it in game %s",
                    guess, currentPlayer.getId(), id));
        }

        prevGuesses.add(guess);

        //TODO track game completion? or calc on the fly
    }

    public boolean isGameComplete() {
        return p1Guesses.contains(p2Number) || p2Guesses.contains(p1Number);
    }

    private static final Player getRandomPlayer(Player p1, Player p2) {
        return (Math.random() < 0.5) ? p1 : p2;
    }
}
