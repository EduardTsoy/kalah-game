package com.example.kalah.service;

import com.example.kalah.KalahException;
import com.example.kalah.domain.ActivePlayer;
import com.example.kalah.domain.KalahGameEntity;
import com.example.kalah.repository.KalahRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.function.Consumer;

@Service
public class KalahServiceImpl implements KalahService {
    private final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final KalahRepository kalahRepository;

    @Autowired
    public KalahServiceImpl(final KalahRepository kalahRepository) {
        this.kalahRepository = kalahRepository;
    }

    @Override
    public KalahGameEntity createGame(final byte pits,
                                      final byte stones) {
        if (pits * stones > MAX_STONES_PER_PLAYER) {
            throw new KalahException("Sorry, we don't have so many stones");
        }

        final byte[] firstPits = new byte[pits];
        Arrays.fill(firstPits, stones);
        final byte[] secondPits = new byte[pits];
        Arrays.fill(secondPits, stones);
        final KalahGameEntity entity = new KalahGameEntity(
                pits,
                stones,
                Arrays.toString(firstPits),
                (byte) 0,
                Arrays.toString(secondPits),
                (byte) 0,
                ActivePlayer.FIRST_PLAYER,
                "Game started!"
        );
        // We need to saveAndFlush the entity explicitly in order to obtain its new 'version'
        kalahRepository.saveAndFlush(entity);
        return entity;
    }

    @Override
    public KalahGameEntity findGame(final long gameId) {
        return kalahRepository.findOne(gameId);
    }

    @Override
    // TODO: Refactor this method to several smaller (private methods)
    public void makeMove(@Nonnull final KalahGameEntity game,
                         @Nonnull final ActivePlayer activePlayer,
                         final int sourcePitIndex) {
        final String message;
        // Check whether the move is valid
        if (activePlayer != game.getActivePlayer()) {
            message = "Sorry, " + activePlayer + ", it's not your turn now.";
        } else if (sourcePitIndex < 0 || sourcePitIndex >= game.getNumberOfSmallPitsForEachPlayer()) {
            message = "Sorry, there is no pit #" + sourcePitIndex;
        } else {
            // The move is valid (well, there is one more validity check a little later in this code block)

            // Abstract the active player's pits-and-store and his/her opponent's
            final byte[] selfPits;
            final byte[] opponentPits;
            final Consumer<Byte> increaseActorStore;
            final Consumer<Byte> increaseOpponentStore;
            final Consumer<String> setSelfPits;
            final Consumer<String> setOpponentPits;
            if (activePlayer == ActivePlayer.FIRST_PLAYER) {
                selfPits = KalahService.convertStringToByteArray(game.getFirstPits());
                setSelfPits = game::setFirstPits;
                increaseActorStore =
                        (amount) ->
                                game.setFirstStore((byte) (game.getFirstStore() + amount));
                opponentPits = KalahService.convertStringToByteArray(game.getSecondPits());
                setOpponentPits = game::setSecondPits;
                increaseOpponentStore =
                        (amount) ->
                                game.setSecondStore((byte) (game.getSecondStore() + amount));
            } else {
                selfPits = KalahService.convertStringToByteArray(game.getSecondPits());
                setSelfPits = game::setSecondPits;
                increaseActorStore =
                        (amount) ->
                                game.setSecondStore((byte) (game.getSecondStore() + amount));
                opponentPits = KalahService.convertStringToByteArray(game.getFirstPits());
                setOpponentPits = game::setFirstPits;
                increaseOpponentStore =
                        (amount) ->
                                game.setFirstStore((byte) (game.getFirstStore() + amount));
            }

            // Let's see how many stones are there in the pit selected for the move
            final int stones = selfPits[sourcePitIndex];
            if (stones == 0) {
                message = "Sorry, " + activePlayer + ", there are no stones in your pit #" + (sourcePitIndex + 1) + ".";
            } else {
                // Ok, now we are sure the move is totally valid

                // Initialize variables that will point to the place where each next moving stone will be placed
                ActivePlayer destinationPitOwner = activePlayer;
                int destinationPitIndex = sourcePitIndex;
                byte[] destinationPits = selfPits;
                boolean lastInStore = false;
                for (int i = 0; i < stones; i++) {
                    // deal with the next adjacent pit (or active player's store, occasionally)
                    destinationPitIndex++;
                    lastInStore = false;
                    if (destinationPitIndex >= game.getNumberOfSmallPitsForEachPlayer()) {
                        if (destinationPitOwner == activePlayer) {
                            destinationPitIndex = -1;

                            increaseActorStore.accept((byte) 1);
                            selfPits[sourcePitIndex]--;

                            destinationPits = opponentPits;
                            lastInStore = true;
                        } else {
                            destinationPitIndex = 0;
                            destinationPits = selfPits;
                            moveOneStoneBetweenPits(selfPits, sourcePitIndex, destinationPits, destinationPitIndex);
                        }
                        destinationPitOwner = ActivePlayer.swap(destinationPitOwner);
                    } else {
                        moveOneStoneBetweenPits(selfPits, sourcePitIndex, destinationPits, destinationPitIndex);
                    }
                }

                String additionalMessage = "";
                if (lastInStore) {
                    additionalMessage = activePlayer + ", your last stone dropped to your Store (Kalah)" +
                            ", therefore it's your turn again!\n";
                } else {
                    if (destinationPits == selfPits && selfPits[destinationPitIndex] == 1) {
                        final int oppositePitIndex = (game
                                .getNumberOfSmallPitsForEachPlayer() - 1) - destinationPitIndex;
                        final int oppositeStones = opponentPits[oppositePitIndex];
                        if (oppositeStones > 0) {
                            additionalMessage = activePlayer + ", your last stone dropped to your empty pit" +
                                    ", therefore you grab it together with your opponent's stones" +
                                    " (" + oppositeStones + ") in the opposite pit and take it to your Store! \n";

                            increaseActorStore.accept(opponentPits[oppositePitIndex]);
                            opponentPits[oppositePitIndex] = 0;

                            increaseActorStore.accept(selfPits[destinationPitIndex]);
                            selfPits[destinationPitIndex] = 0;
                        }
                    }
                    game.setActivePlayer(ActivePlayer.swap(game.getActivePlayer()));
                    switch (game.getActivePlayer()) {
                        case FIRST_PLAYER:
                            game.setFirstMessage(ActivePlayer.FIRST_PLAYER + ", it's your turn");
                            game.setSecondMessage("-");
                            break;
                        case SECOND_PLAYER:
                            game.setFirstMessage("-");
                            game.setSecondMessage(ActivePlayer.SECOND_PLAYER + ", it's your turn");
                            break;
                        default:
                            throw new RuntimeException(
                                    "Internal error: Unsupported player tag: " + game.getActivePlayer());
                    }
                }

                // Check whether the game has ended
                byte selfPitsSum = 0;
                for (final byte pit : selfPits) {
                    selfPitsSum += pit;
                }
                if (selfPitsSum == 0) {
                    // Game over

                    // Move opponent's stones from pits to his/her store
                    for (int i = 0; i < opponentPits.length; i++) {
                        increaseOpponentStore.accept(opponentPits[i]);
                        opponentPits[i] = 0;
                    }
                    // Select the winner
                    if (game.getFirstStore() > game.getSecondStore()) {
                        game.setGameOverMessage(ActivePlayer.FIRST_PLAYER + " has won!");
                    } else if (game.getFirstStore() < game.getSecondStore()) {
                        game.setGameOverMessage(ActivePlayer.SECOND_PLAYER + " has won!");
                    } else {
                        game.setGameOverMessage("Tie!");
                    }
                    game.setFirstMessage("-");
                    game.setSecondMessage("-");
                }

                // Store the new state from our temporary variables to the persisted entity
                setSelfPits.accept(Arrays.toString(selfPits));
                setOpponentPits.accept(Arrays.toString(opponentPits));
                sanityCheck(game);

                message = additionalMessage +
                        activePlayer + " moved " + stones + " stones from pit #" + (sourcePitIndex + 1) + ".";
            }
        }

        addGameLog(game, message);
        if (game.getGameOverMessage() != null && !game.getGameOverMessage().isEmpty()) {
            addGameLog(game, game.getGameOverMessage());
        }
    }

    @Override
    public void addGameLog(@Nonnull final KalahGameEntity game,
                           @Nonnull final String message) {
        game.setEventLog(message + "\n" + game.getEventLog());
    }

    @Override
    public void saveAndFlush(@Nonnull final KalahGameEntity game) {
        // Sometimes we need to saveAndFlush the entity explicitly in order to obtain its new 'version'
        kalahRepository.saveAndFlush(game);
    }

    /* ----------------
     * PRIVATE METHODS
     */

    // This method helps to eliminate code duplication
    // and also to make sure that we don't lose any stones (or suddenly bring in more ones out of nowhere :))
    private void moveOneStoneBetweenPits(final byte[] sourcePits,
                                         final int sourcePitIndex,
                                         final byte[] destinationPits,
                                         final int destinationPitIndex) {
        destinationPits[destinationPitIndex]++;
        sourcePits[sourcePitIndex]--;
    }

    private void sanityCheck(@Nonnull final KalahGameEntity game) {
        final int HowManyStonesShouldBeInTheGame =
                game.getNumberOfSmallPitsForEachPlayer()
                        * game.getInitialNumberOfStonesInEachPit()
                        * 2;
        final int howManyStonesFirstPlayerHasNow =
                sumPits(KalahService.convertStringToByteArray(game.getFirstPits()))
                        + game.getFirstStore();
        final int howManyStonesSecondPlayerHasNow =
                sumPits(KalahService.convertStringToByteArray(game.getSecondPits()))
                        + game.getSecondStore();
        if (howManyStonesFirstPlayerHasNow + howManyStonesSecondPlayerHasNow != HowManyStonesShouldBeInTheGame) {
            throw new RuntimeException(
                    "Internal error: Sorry, our programming logic appears to be broken." +
                            " A total of " + HowManyStonesShouldBeInTheGame +
                            " stones were expected to be in the game, but we actually see " +
                            (howManyStonesFirstPlayerHasNow + howManyStonesSecondPlayerHasNow) +
                            " (" + game.getFirstPits() + " " + game.getFirstStore() + "," +
                            " " + game.getSecondPits() + " " + game.getSecondStore() + ")" +
                            ".");
        }
    }

    private int sumPits(@Nonnull final byte[] pits) {
        int sum = 0;
        for (final byte pit : pits) {
            sum += pit;
        }
        return sum;
    }

}
