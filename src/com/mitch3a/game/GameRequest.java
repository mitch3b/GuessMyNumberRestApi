package com.mitch3a.game;

public class GameRequest {
    final String id;
    final Player requester;
    final Player accepter;
    final Player starterPlayer; //if null, will choose randomly
    GameState gameState;

    public GameRequest(String id, Player requester, Player accepter, Player starterPlayer) {
        if(id == null || id == "") {
            throw new IllegalArgumentException("Cannot create game request with null or empty id");
        }

        if(requester == null) {
            throw new IllegalArgumentException("Cannot create game with null requester.");
        }

        if(accepter == null) {
            throw new IllegalArgumentException("Cannot create game with null accepter.");
        }

        if(requester.equals(accepter)) {
            throw new IllegalArgumentException(String.format("Cannot create game with same requester and accepter (%s)",
                    requester.getId()));
        }

        if(starterPlayer != null && !starterPlayer.equals(requester) && !starterPlayer.equals(accepter)) {
            throw new IllegalArgumentException(String.format("Cannot create game with starter player %s who is neither requester (%s) nor acceptor (%s)",
                    starterPlayer.getId(), requester.getId(), accepter.getId()));
        }

        this.id = id;
        this.requester = requester;
        this.accepter = accepter;
        this.starterPlayer = starterPlayer;
        this.gameState = GameState.REQUESTED;
    }

    public void reject(Player player) {
        if(requester.equals(player)) {
            throw new IllegalArgumentException(String.format("Cannot reject game $s with player %s because they are not the accepter %s",
                    id, requester.getId(), accepter.getId()));
        }

        this.gameState = GameState.REJECTED;
    }
}
