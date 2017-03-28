package com.mitch3a.game;

import java.util.List;
import java.util.UUID;

public class UpdateListener {
    final GameDAO gameDAO;

    public UpdateListener(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    //TODO too ambiguious to call this for setup and for guessing in same way
    public void updateGame(String gameId, Player player, String turn) {
        Game game = gameDAO.getGame(gameId);

        game.takeTurn(player, turn);
        gameDAO.updateGame(game);
    }

    public GameRequest requestGame(Player requester, Player opponent) {
        String id = UUID.randomUUID().toString();

        GameRequest request = new GameRequest(id, requester, opponent, null);
        gameDAO.addRequest(request);

        return request;
    }

    public Game acceptGame(Player accepter, GameRequest request) {
        if(!request.accepter.equals(accepter)) {
            throw new IllegalArgumentException(String.format("Player %s can't accept game %s bc they are not accepter %s",
                    accepter.getId(), request.id, request.accepter.getId()));
        }

        Game game = new Game(request);

        gameDAO.updateGame(game);

        return game;
    }

    public void rejectGame(Player accepter, GameRequest request) {
        if(!request.accepter.equals(accepter)) {
            throw new IllegalArgumentException(String.format("Player %s can't accept game %s bc they are not accepter %s",
                    accepter.getId(), request.id, request.accepter.getId()));
        }

        request.reject(accepter);
        gameDAO.rejectRequest(request);
    }

    public List<Game> getGames(Player player) {
        return gameDAO.getGames(player);
    }

    public List<GameRequest> getPendingRequests(Player player) {
        return gameDAO.getPendingRequests(player);
    }

    public Game getGame(String id) {
        return gameDAO.getGame(id);
    }
}
