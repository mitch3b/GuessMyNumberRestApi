package com.mitch3a.game;

import java.util.List;

/**
 * Created by mitchken on 3/26/17.
 */
public interface GameDAO {
    Game getGame(String gameId);
    void addRequest(GameRequest request);
    void rejectRequest(GameRequest request);
    void acceptRequest(Game game);
    void updateGame(Game game);
    List<Game> getGames(Player player);
    List<GameRequest> getPendingRequests(Player player);
    List<GameRequest> getGamesToAccept(Player player);
}
