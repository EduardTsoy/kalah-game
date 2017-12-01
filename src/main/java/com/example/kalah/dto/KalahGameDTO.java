package com.example.kalah.dto;

import java.util.Arrays;

public class KalahGameDTO {

    private long gameId;
    private int version;
    private int numberOfSmallPitsForEachPlayer;
    private int initialNumberOfStonesInEachPit;
    private byte[] firstPits;
    private byte firstStore;
    private byte[] secondPits;
    private byte secondStore;
    private String activePlayer;
    private String firstMessage;
    private String secondMessage;
    private String[] eventLog;
    private String gameOverMessage;

    private KalahGameDTO() {
    }

    public KalahGameDTO(final long gameId,
                        final int version,
                        final int numberOfSmallPitsForEachPlayer,
                        final int initialNumberOfStonesInEachPit,
                        final byte[] firstPits,
                        final byte firstStore,
                        final byte[] secondPits,
                        final byte secondStore,
                        final String activePlayer,
                        final String firstMessage,
                        final String secondMessage,
                        final String[] eventLog,
                        final String gameOverMessage) {
        this.gameId = gameId;
        this.version = version;
        this.numberOfSmallPitsForEachPlayer = numberOfSmallPitsForEachPlayer;
        this.initialNumberOfStonesInEachPit = initialNumberOfStonesInEachPit;
        this.firstPits = firstPits;
        this.firstStore = firstStore;
        this.secondPits = secondPits;
        this.secondStore = secondStore;
        this.activePlayer = activePlayer;
        this.firstMessage = firstMessage;
        this.secondMessage = secondMessage;
        this.eventLog = eventLog;
        this.gameOverMessage = gameOverMessage;
    }

    public static KalahGameDTO error(final String errorMessage) {
        final KalahGameDTO result = new KalahGameDTO();
        result.setEventLog(new String[]{errorMessage});
        return result;
    }

    @Override
    public String toString() {
        return "KalahGameDTO{" +
                "gameId=" + gameId +
                ", version=" + version +
                ", numberOfSmallPitsForEachPlayer=" + numberOfSmallPitsForEachPlayer +
                ", initialNumberOfStonesInEachPit=" + initialNumberOfStonesInEachPit +
                ", firstPits=" + Arrays.toString(firstPits) +
                ", firstStore=" + firstStore +
                ", secondPits=" + Arrays.toString(secondPits) +
                ", secondStore=" + secondStore +
                ", activePlayer='" + activePlayer + '\'' +
                ", firstMessage='" + firstMessage + '\'' +
                ", secondMessage='" + secondMessage + '\'' +
                ", eventLog=" + Arrays.toString(eventLog) +
                ", gameOverMessage='" + gameOverMessage + '\'' +
                '}';
    }
/* --------------------
     * Getters and setters
     */

    public long getGameId() {
        return gameId;
    }

    public void setGameId(final long gameId) {
        this.gameId = gameId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public int getNumberOfSmallPitsForEachPlayer() {
        return numberOfSmallPitsForEachPlayer;
    }

    public void setNumberOfSmallPitsForEachPlayer(final int numberOfSmallPitsForEachPlayer) {
        this.numberOfSmallPitsForEachPlayer = numberOfSmallPitsForEachPlayer;
    }

    public int getInitialNumberOfStonesInEachPit() {
        return initialNumberOfStonesInEachPit;
    }

    public void setInitialNumberOfStonesInEachPit(final int initialNumberOfStonesInEachPit) {
        this.initialNumberOfStonesInEachPit = initialNumberOfStonesInEachPit;
    }

    public byte getFirstStore() {
        return firstStore;
    }

    public void setFirstStore(final byte firstStore) {
        this.firstStore = firstStore;
    }

    public byte[] getFirstPits() {
        return firstPits;
    }

    public void setFirstPits(final byte[] firstPits) {
        this.firstPits = firstPits;
    }

    public byte getSecondStore() {
        return secondStore;
    }

    public void setSecondStore(final byte secondStore) {
        this.secondStore = secondStore;
    }

    public byte[] getSecondPits() {
        return secondPits;
    }

    public void setSecondPits(final byte[] secondPits) {
        this.secondPits = secondPits;
    }

    public String getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(final String activePlayer) {
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

    public String[] getEventLog() {
        return eventLog;
    }

    public void setEventLog(final String[] eventLog) {
        this.eventLog = eventLog;
    }

    public String getGameOverMessage() {
        return gameOverMessage;
    }

    public void setGameOverMessage(final String gameOverMessage) {
        this.gameOverMessage = gameOverMessage;
    }

}
