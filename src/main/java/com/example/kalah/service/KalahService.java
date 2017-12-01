package com.example.kalah.service;

import com.example.kalah.domain.ActivePlayer;
import com.example.kalah.domain.KalahGameEntity;

import javax.annotation.Nonnull;

public interface KalahService {

    // Having too many stones in a game doesn't make much sense, even for the purposes of academic research.
    // Therefore we limit the number of stones*pits to a reasonable amount.
    int MAX_STONES_PER_PLAYER = 63;

    KalahGameEntity createGame(final byte pits,
                               final byte stones);

    KalahGameEntity findGame(final long gameId);

    void makeMove(@Nonnull final KalahGameEntity game,
                  @Nonnull final ActivePlayer activePlayer,
                  final int pitIndex);

    void addGameLog(@Nonnull final KalahGameEntity game,
                    @Nonnull final String message);

    void saveAndFlush(@Nonnull final KalahGameEntity game);

    /* ------------------------
     * Static utility function
     */

    /**
     * (Use Arrays.toString(byteArray) function for a reverse conversion.)
     */
    static byte[] convertStringToByteArray(final String pits) {
        final String withoutSquareBrackets = pits
                .replace("[", "")
                .replace("]", "")
                .trim();
        final String[] split = withoutSquareBrackets.split(",");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }
        final byte[] result = new byte[split.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Byte.valueOf(split[i]);
        }
        return result;
    }

}
