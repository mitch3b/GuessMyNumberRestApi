package com.mitch3a.game;

import java.util.List;
import java.util.UUID;

public class UpdateListener {
    final GameDAO gameDAO;

    public UpdateListener(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void setupGame(String gameId, Player player, String turn) {
        Game game = gameDAO.getGame(gameId);

        game.takeSetupTurn(player, turn);
        gameDAO.updateGame(game);
    }

    public void makeGuess(String gameId, Player player, String turn) {
        Game game = gameDAO.getGame(gameId);

        game.makeGuess(player, turn);
        gameDAO.updateGame(game);
    }

    public GameRequest requestGame(Player requester, Player opponent) {
        return requestGame(requester, opponent, null);
    }

    public GameRequest requestGame(Player requester, Player opponent, Player first) {
        String id = UUID.randomUUID().toString();

        GameRequest request = new GameRequest(id, requester, opponent, first);
        gameDAO.addRequest(request);

        return request;
    }

    public Game acceptGame(Player accepter, GameRequest request) {
        if(!request.accepter.equals(accepter)) {
            throw new IllegalArgumentException(String.format("Player %s can't accept game %s bc they are not accepter %s",
                    accepter.getId(), request.id, request.accepter.getId()));
        }

        Game game = new Game(request);

        gameDAO.acceptRequest(game);

        return game;
    }

    //TODO might have to fetch the gameRequest by id. Look for other cases of this as well
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
