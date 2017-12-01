package com.example.kalah.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "kalah_game")
public class KalahGameEntity extends BaseEntity {

    private byte numberOfSmallPitsForEachPlayer;
    private byte initialNumberOfStonesInEachPit;

    private String firstPits;
    // Having too many stones in a game doesn't make much sense, even for the purposes of academic research.
    // Therefore we limit the number of stones*pits to a reasonable amount (no more than 126 stones in total).
    // It allows us to use 'byte' type for extra type safety and bug prevention.
    private byte firstStore;

    private String secondPits;
    private byte secondStore;

    @Enumerated(EnumType.ORDINAL)
    private ActivePlayer activePlayer;

    private String firstMessage;
    private String secondMessage;
    private String eventLog;

    private String gameOverMessage;

    public KalahGameEntity() {
    }

    public KalahGameEntity(final byte numberOfSmallPitsForEachPlayer,
                           final byte initialNumberOfStonesInEachPit,
                           final String firstPits,
                           final byte firstStore,
                           final String secondPits,
                           final byte secondStore,
                           final ActivePlayer activePlayer,
                           final String eventLog) {
        this.numberOfSmallPitsForEachPlayer = numberOfSmallPitsForEachPlayer;
        this.initialNumberOfStonesInEachPit = initialNumberOfStonesInEachPit;
        this.firstPits = firstPits;
        this.firstStore = firstStore;
        this.secondPits = secondPits;
        this.secondStore = secondStore;
        this.activePlayer = activePlayer;
        final String goFirst = activePlayer.name() + ", you go first!";
        switch (activePlayer) {
            case FIRST_PLAYER:
                this.firstMessage = goFirst;
                this.secondMessage = "-";
                break;
            case SECOND_PLAYER:
                this.firstMessage = "-";
                this.secondMessage = goFirst;
                break;
            default:
                throw new RuntimeException("Internal error: Unsupported player tag: " + activePlayer);
        }
        this.eventLog = eventLog;
    }
/* --------------------
     * Getters and setters
     */

    public int getNumberOfSmallPitsForEachPlayer() {
        return numberOfSmallPitsForEachPlayer;
    }

    public void setNumberOfSmallPitsForEachPlayer(final byte numberOfSmallPitsForEachPlayer) {
        this.numberOfSmallPitsForEachPlayer = numberOfSmallPitsForEachPlayer;
    }

    public byte getInitialNumberOfStonesInEachPit() {
        return initialNumberOfStonesInEachPit;
    }

    public void setInitialNumberOfStonesInEachPit(final byte initialNumberOfStonesInEachPit) {
        this.initialNumberOfStonesInEachPit = initialNumberOfStonesInEachPit;
    }

    public String getFirstPits() {
        return firstPits;
    }

    public void setFirstPits(final String firstPits) {
        this.firstPits = firstPits;
    }

    public byte getFirstStore() {
        return firstStore;
    }

    public void setFirstStore(final byte firstStore) {
        this.firstStore = firstStore;
    }

    public String getSecondPits() {
        return secondPits;
    }

    public void setSecondPits(final String secondPits) {
        this.secondPits = secondPits;
    }

    public byte getSecondStore() {
        return secondStore;
    }

    public void setSecondStore(final byte secondStore) {
        this.secondStore = secondStore;
    }

    public ActivePlayer getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(final ActivePlayer activePlayer) {
        this.activePlayer = activePlayer;
    }

    public String getFirstMessage() {
        return firstMessage;
    }

    public void setFirstMessage(final String firstMessage) {
        this.firstMessage = firstMessage;
    }

    public String getSecondMessage() {
        return secondMessage;
    }

    public void setSecondMessage(final String secondMessage) {
        this.secondMessage = secondMessage;
    }

    public String getEventLog() {
        return eventLog;
    }

    public void setEventLog(final String eventLog) {
        this.eventLog = eventLog;
    }

    public String getGameOverMessage() {
        return gameOverMessage;
    }

    public void setGameOverMessage(final String gameOverMessage) {
        this.gameOverMessage = gameOverMessage;
    }

}
