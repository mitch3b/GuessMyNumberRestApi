package com.mitch3a.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Class holds data about a given game. Note: creation is package private and
 * all fetching/updating of games should be done in UpdateListener
 */
public class Game {
    final String id;
    final Player p1;
    final Player p2;

    Player currentPlayer;
    int p1Number = -1;
    int p2Number = -1;
    final List<Integer> p1Guesses;
    final List<Integer> p2Guesses;
    GameState gameState;

    //Used for going from a request to a started game
    Game(GameRequest request) {
        this.id = request.id;
        this.p1 = request.requester;
        this.p2 = request.accepter;
        this.currentPlayer = (request.starterPlayer != null) ? request.starterPlayer : getRandomPlayer(p1, p2);
        this.gameState = GameState.SETUP;
        this.p1Guesses = new ArrayList<>();
        this.p2Guesses = new ArrayList<>();
    }

    //If only in setup
    Game(String id, Player p1, Player p2, Player currentPlayer) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.currentPlayer = (currentPlayer != null) ? currentPlayer : getRandomPlayer(p1, p2);
        this.p1Guesses = new ArrayList<>();
        this.p2Guesses = new ArrayList<>();
        this.gameState = GameState.SETUP;
    }

    public String getId() {
        return id;
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

    void takeSetupTurn(Player player, String turnString) {
        if(!GameState.SETUP.equals(gameState)) {
            throw new IllegalArgumentException(String.format("Player %s cannot make setup turn %s because game is in state %s",
                    player.getId(), turnString, gameState));
        }

        int number = parseTurn(turnString);
        boolean isP1 = isP1(player);

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

        if(p1Number != -1 && p2Number != -1) {
            gameState = GameState.IN_PROGRESS;
        }
    }

    void makeGuess(Player player, String turnString) {
        if(!GameState.IN_PROGRESS.equals(gameState)) {
            throw new IllegalArgumentException(String.format("Player %s cannot make setup turn %s because game is in state %s",
                    player.getId(), turnString, gameState));
        }

        int guess = parseTurn(turnString);
        boolean isP1 = isP1(player);

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

        if(guess == correctNumber) {
            gameState = GameState.COMPLETE;
        }
        else {
            currentPlayer = nextPlayer;
        }
    }

    public boolean isGameComplete() {
        return p1Guesses.contains(p2Number) || p2Guesses.contains(p1Number);
    }

    private static final Player getRandomPlayer(Player p1, Player p2) {
        return (Math.random() < 0.5) ? p1 : p2;
    }
}
