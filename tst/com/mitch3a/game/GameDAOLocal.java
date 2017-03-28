package com.mitch3a.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For testing, use a DAO that just keeps a map of the data locally
 */
public class GameDAOLocal implements GameDAO {
    Map<String, Game> gameMap = new HashMap<>();
    Map<String, GameRequest> gameRequestMap = new HashMap<>();

    @Override public Game getGame(String gameId) {
        return gameMap.get(gameId);
    }

    @Override public void addRequest(GameRequest request) {
        gameRequestMap.put(request.id, request);
    }

    @Override public void rejectRequest(GameRequest request) {
        gameRequestMap.put(request.id, request);
    }

    @Override public void acceptRequest(Game game) {
        gameRequestMap.remove(game.getId());
        gameMap.put(game.getId(), game);
    }

    @Override public void updateGame(Game game) {
        if(!gameMap.containsKey(game.getId())) throw new IllegalArgumentException("Can't update game " + game.getId()
        + " because there's no current record of it");

        gameMap.put(game.getId(), game);
    }

    @Override public List<Game> getGames(Player player) {
        List<Game> result = new ArrayList<>();

        for(Game game : gameMap.values()) {
            if(game.p1.equals(player) || game.p2.equals(player)) {
                result.add(game);
            }
        }

        return result;
    }

    @Override public List<GameRequest> getPendingRequests(Player player) {
        List<GameRequest> result = new ArrayList<>();

        for(GameRequest request : gameRequestMap.values()) {
            if(request.requester.equals(player)) {
                result.add(request);
            }
        }

        return result;
    }

    @Override public List<GameRequest> getGamesToAccept(Player player) {
        List<GameRequest> result = new ArrayList<>();

        for(GameRequest request : gameRequestMap.values()) {
            if(request.accepter.equals(player)) {
                result.add(request);
            }
        }

        return result;
    }
}
