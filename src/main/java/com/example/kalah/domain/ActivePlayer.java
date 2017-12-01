package com.example.kalah.domain;

public enum ActivePlayer {
    FIRST_PLAYER,
    SECOND_PLAYER;

    public static ActivePlayer swap(final ActivePlayer player) {
        return (player == ActivePlayer.FIRST_PLAYER)
               ? ActivePlayer.SECOND_PLAYER
               : ActivePlayer.FIRST_PLAYER;
    }
}
