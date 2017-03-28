package com.mitch3a.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UpdateListenerTest {
    UpdateListener updateListener = new UpdateListener(new GameDAOLocal());

    @Test
    public void testBadRequest_emptyP1() {
        Player p1 = new Player("p1");
        Player p2 = null;

        Assertions.assertThrows(IllegalArgumentException.class,  () -> {
            updateListener.requestGame(p1, p2);
        });
    }

    @Test
    public void testBadRequest_emptyP2() {
        Player p1 = null;
        Player p2 = new Player("p2");

        Assertions.assertThrows(IllegalArgumentException.class,  () -> {
            updateListener.requestGame(p1, p2);
        });
    }

    @Test
    public void testRejectedGame() {
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        GameRequest gameRequest = updateListener.requestGame(p1, p2);
        gameRequest.reject(p2);

        Assertions.assertEquals(GameState.REJECTED, gameRequest.gameState);
    }

    @Test
    public void testRejectedGameByWrongPlayer() {
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        GameRequest gameRequest = updateListener.requestGame(p1, p2);
        Assertions.assertThrows(IllegalArgumentException.class,  () -> {
            gameRequest.reject(p1);
        });
    }

    @Test
    public void testTwoGameRequestsAtOnce() {
        Player p1 = new Player("TwoGameRequestsP1");
        Player p2 = new Player("TwoGameRequestsP2");

        GameRequest gameRequest1 = updateListener.requestGame(p1, p2);
        GameRequest gameRequest2 = updateListener.requestGame(p1, p2);

        Assertions.assertNotEquals(gameRequest1.id, gameRequest2.id);
    }

    @Test
    public void testQuickestGame() {
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        GameRequest gameRequest = updateListener.requestGame(p1, p2);
        Game game = updateListener.acceptGame(p2, gameRequest);

        Assertions.assertEquals(GameState.SETUP, game.gameState);

        updateListener.updateGame(game.id, p1, "1");
        Assertions.assertEquals(GameState.SETUP, game.gameState);

        updateListener.updateGame(game.id, p2, "1");
        Assertions.assertEquals(GameState.IN_PROGRESS, game.gameState);

        updateListener.updateGame(game.id, game.currentPlayer, "1");
        Assertions.assertEquals(GameState.COMPLETE, game.gameState);
    }
}
